/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.projecthusky.common.hl7cdar2.POCDMT000040ClinicalDocument;
import org.projecthusky.communication.xd.xdm.DocumentContentAndMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.ui.service.ServiceComponent;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class EhcDocument extends PersistentObject {
	private static Logger logger = LoggerFactory.getLogger(EhcDocument.class);

	public enum EhcDocType {
		CLINICALDOCUMENT, XDM
	}

	public static final String TABLENAME = "at_medevit_elexis_ehc_document"; //$NON-NLS-1$
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$

	public static final String VERSIONID = "VERSION"; //$NON-NLS-1$

	public static final String FLD_NAME = "name"; //$NON-NLS-1$
	public static final String FLD_TIMESTAMP = "timestamp"; //$NON-NLS-1$
	public static final String FLD_PATIENT = "patient"; //$NON-NLS-1$
	public static final String FLD_LOCATION = "location"; //$NON-NLS-1$

	// @formatter:off
	static final String create =
			"CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
			"ID VARCHAR(25) primary key, " + //$NON-NLS-1$
			"lastupdate BIGINT," + //$NON-NLS-1$
			"deleted CHAR(1) default '0'," + //$NON-NLS-1$

			"name VARCHAR(255)," + //$NON-NLS-1$
			"timestamp VARCHAR(16)," + //$NON-NLS-1$
			"patient VARCHAR(128)," + //$NON-NLS-1$
			"location VARCHAR(255)" + //$NON-NLS-1$

			");" + //$NON-NLS-1$
			"CREATE INDEX ehcdoc1 ON " + TABLENAME + " (" + FLD_PATIENT + ");" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"INSERT INTO " + TABLENAME + " (ID," + FLD_PATIENT + ") VALUES (" + JdbcLink.wrap(VERSIONID) + "," + JdbcLink.wrap(VERSION) + ");"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	// @formatter:on

	static {
		addMapping(TABLENAME, FLD_NAME, FLD_TIMESTAMP, FLD_PATIENT, FLD_LOCATION);

		if (!tableExists(TABLENAME)) {
			createOrModifyTable(create);
		} else {
			EhcDocument version = load(VERSIONID);
			VersionInfo vi = new VersionInfo(version.get(FLD_PATIENT));
			if (vi.isOlder(VERSION)) {
				// we should update eg. with createOrModifyTable(update.sql);
				// And then set the new version
				version.set(FLD_PATIENT, VERSION);
			}
		}
	}

	public EhcDocument() {
		// TODO Auto-generated constructor stub
	}

	public EhcDocument(String id) {
		super(id);
	}

	public static EhcDocument load(final String id) {
		return new EhcDocument(id);
	}

	public EhcDocument(String name, URL location, TimeTool creation) {
		create(null);
		String[] fields = { FLD_NAME, FLD_TIMESTAMP, FLD_LOCATION };
		String[] vals = new String[] { name, creation.toString(TimeTool.TIMESTAMP), location.toString() };
		set(fields, vals);
		Patient patient = null;
		if (isEhcType(EhcDocType.CLINICALDOCUMENT)) {
			patient = getPatientFromDocument(location);
		}
		if (patient != null) {
			setPatient(patient);
		}
	}

	public boolean isEhcType(EhcDocType type) {
		return getName().contains("[" + type + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private Patient getPatientFromDocument(URL location) {
		Patient ret = null;
		try {
			if (EhcDocument.isEhcXml(location)) {
				POCDMT000040ClinicalDocument clinicalDocument = ServiceComponent.getEhcService()
						.loadDocument(location.openStream());
				if (clinicalDocument != null) {
					org.projecthusky.common.model.Patient patient = ServiceComponent.getEhcService()
							.getPatient(clinicalDocument);
					if (patient != null) {
						ret = EhcCoreMapper.getElexisPatient(patient, false);
					}
				}
			}
		} catch (IOException e) {
			logger.error("Could not open location.", e); //$NON-NLS-1$
		}
		return ret;
	}

	public String getName() {
		return get(FLD_NAME);
	}

	public void setName(String name) {
		set(FLD_NAME, name);
	}

	public URL getLocation() {
		try {
			return new URL(get(FLD_LOCATION));
		} catch (MalformedURLException e) {
			logger.error("Could create URL for location.", e); //$NON-NLS-1$
		}
		return null;
	}

	public void setLocation(URL location) {
		set(FLD_LOCATION, location.toString());
	}

	public Patient getPatient() {
		String id = get(FLD_PATIENT);
		if (id != null && !id.isEmpty()) {
			Patient patient = Patient.load(id);
			if (patient.exists()) {
				return patient;
			}
		}
		return null;
	}

	public void setPatient(Patient patient) {
		set(FLD_PATIENT, patient.getId());
	}

	public static boolean documentExists(URL url) {
		Query<EhcDocument> qd = new Query<EhcDocument>(EhcDocument.class);
		qd.add(FLD_LOCATION, Query.EQUALS, url.toString());
		List<EhcDocument> existing = qd.execute();
		return !existing.isEmpty();
	}

	public static boolean isEhcXml(URL url) {
		if (url.getPath().endsWith(".xml")) { //$NON-NLS-1$
			try (InputStream stream = url.openStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(stream));) {
				String line = null;
				for (int i = 0; i < 100; i++) {
					line = br.readLine();
					if (line != null) {
						if (line.contains("<ClinicalDocument")) { //$NON-NLS-1$
							return true;
						}
					} else {
						break;
					}
				}
			} catch (IOException e) {
				// just fall through to returning false
			}
		}
		return false;
	}

	public static boolean isEhcXdm(URL url) {
		if (url.getPath().endsWith(".zip") || url.getPath().endsWith(".xdm")) { //$NON-NLS-1$ //$NON-NLS-2$
			String fileName = url.getFile();
			if (fileName != null && !fileName.isEmpty()) {
				File file = new File(fileName);
				if (file.exists()) {
					List<org.projecthusky.common.model.Patient> patients = ServiceComponent.getEhcService()
							.getXdmPatients(file);
					return patients != null;
				}
			}
		}
		return false;
	}

	/**
	 * Factory Method for creating an EhcDocument entry from an CDA ClinicalDocument
	 * XML.
	 *
	 * @param fileUrl
	 * @return
	 */
	public static EhcDocument createFromXml(URL fileUrl) {
		File file = new File(fileUrl.getFile());
		return new EhcDocument(file.getName() + " [" + EhcDocType.CLINICALDOCUMENT + "]", fileUrl, new TimeTool()); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Factory Method for EhcDocument entry from an XDM. The ClinicalDocuments
	 * contained in the XDM file are extracted. The InboxWatcher will handle them
	 * separately.
	 *
	 * @param fileUrl
	 * @return
	 */
	public static EhcDocument createFromXdm(URL fileUrl) {
		File xdmFile = new File(fileUrl.getPath());
		EhcDocument ret = new EhcDocument(xdmFile.getName() + " [" + EhcDocType.XDM + "]", fileUrl, new TimeTool()); //$NON-NLS-1$ //$NON-NLS-2$
		List<DocumentContentAndMetadata> documents = ServiceComponent.getEhcService().getXdmDocuments(xdmFile);
		for (DocumentContentAndMetadata clinicalDocument : documents) {
			File documentFile = getXdmDocumentFile(xdmFile);
			try (FileOutputStream outputStream = new FileOutputStream(documentFile)) {
				POCDMT000040ClinicalDocument cdaDocument = ServiceComponent.getEhcService()
						.getDocument(clinicalDocument);
				ServiceComponent.getEhcService().saveDocument(cdaDocument, outputStream);
			} catch (Exception e) {
				logger.error("Could not create EhcDocument from xdm.", e); //$NON-NLS-1$
			}
		}
		return ret;
	}

	private static File getXdmDocumentFile(File xdmFile) {
		String rootPath = xdmFile.getParent();
		String rootName = xdmFile.getName();

		rootName = rootName.replaceAll("\\.", StringUtils.EMPTY); //$NON-NLS-1$
		File file = null;
		for (int i = 0; i < 100; i++) {
			file = new File(rootPath + File.separator + rootName + "_" + i + ".xml"); //$NON-NLS-1$ //$NON-NLS-2$
			if (!file.exists()) {
				break;
			}
		}

		return file;
	}

	@Override
	public String getLabel() {
		return getName().replaceAll("\\[[A-Z]+\\]", StringUtils.EMPTY); //$NON-NLS-1$
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}
}
