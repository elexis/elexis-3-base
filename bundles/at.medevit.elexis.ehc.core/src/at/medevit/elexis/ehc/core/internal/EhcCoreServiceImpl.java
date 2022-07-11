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
package at.medevit.elexis.ehc.core.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.ehealth_connector.cda.ch.AbstractCdaChV1;
import org.ehealth_connector.communication.ConvenienceCommunication;
import org.ehealth_connector.communication.DocumentMetadata;
import org.ehealth_connector.communication.xd.xdm.DocumentContentAndMetadata;
import org.ehealth_connector.communication.xd.xdm.XdmContents;
import org.openhealthtools.ihe.xds.document.DocumentDescriptor;
import org.openhealthtools.ihe.xds.document.XDSDocument;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;
import org.openhealthtools.mdht.uml.cda.RecordTarget;
import org.openhealthtools.mdht.uml.cda.ch.ChFactory;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.core.EhcCoreMapper;
import at.medevit.elexis.ehc.core.EhcCoreService;
import at.medevit.elexis.ehc.core.internal.document.CdaChImpl;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

@Component
public class EhcCoreServiceImpl implements EhcCoreService {

	private static Logger logger = LoggerFactory.getLogger(EhcCoreServiceImpl.class);

	public EhcCoreServiceImpl() {
		// make sure CDACH is registered and initialized
		ChFactory.eINSTANCE.createCdaChV1().init();
	}

	@Override
	public AbstractCdaChV1<?> createCdaChDocument(Patient patient, Mandant mandant) {
		CdaChImpl ret = new CdaChImpl(ChFactory.eINSTANCE.createCdaChV1().init());

		ret.setPatient(EhcCoreMapper.getEhcPatient(patient));
		ret.addAuthor(EhcCoreMapper.getEhcAuthor(mandant));
		return ret;
	}

	@Override
	public ClinicalDocument loadDocument(InputStream document) {
		try {
			return CDAUtil.load(document);
		} catch (Exception e) {
			logger.warn("Error loading document.", e); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public AbstractCdaChV1<?> getAsCdaChDocument(ClinicalDocument clinicalDocument) {
		if (clinicalDocument instanceof ClinicalDocument) {
			return new CdaChImpl(clinicalDocument);
		}
		return null;
	}

	@Override
	public Patient getOrCreatePatient(org.ehealth_connector.common.mdht.Patient ehcPatient) {
		Patient patient = EhcCoreMapper.getElexisPatient(ehcPatient);
		EhcCoreMapper.importEhcAddress(patient, ehcPatient.getAddress());
		EhcCoreMapper.importEhcPhone(patient, ehcPatient.getTelecoms());
		return patient;
	}

	@Override
	public InputStream getXdmAsStream(ClinicalDocument document) throws Exception {
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		ByteArrayOutputStream outputStream = null;
		// write document and create an InputStream
		outputStream = new ByteArrayOutputStream();
		CDAUtil.save(document, outputStream);
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		outputStream.reset();
		// write XDM and create an InputStream
		DocumentMetadata metaData = conCom.addDocument(DocumentDescriptor.CDA_R2, inputStream);
		metaData.setPatient(getPatient(document));
		conCom.createXdmContents(outputStream);
		return new ByteArrayInputStream(outputStream.toByteArray());
	}

	private org.ehealth_connector.common.mdht.Patient getPatient(ClinicalDocument document) {
		EList<RecordTarget> targets = document.getRecordTargets();
		if (targets != null && !targets.isEmpty()) {
			if (targets.size() > 1) {
				logger.warn("Document " + document.getTitle() + " has more than one record target"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return new org.ehealth_connector.common.mdht.Patient(EcoreUtil.copy(targets.get(0)));
		}
		throw new IllegalStateException("Document " + document.getTitle() + " has no record target"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public List<ClinicalDocument> getXdmDocuments(File file) {
		List<ClinicalDocument> ret = null;
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		XdmContents contents = conCom.getXdmContents(file.getAbsolutePath());
		if (contents != null) {
			ret = new ArrayList<ClinicalDocument>();
			List<DocumentContentAndMetadata> dataList = contents.getDocumentAndMetadataList();
			for (DocumentContentAndMetadata documentContentAndMetadata : dataList) {
				XDSDocument xdsDocument = documentContentAndMetadata.getXdsDocument();
				if (xdsDocument != null) {
					ClinicalDocument clinicalDocument;
					try {
						clinicalDocument = CDAUtil.load(xdsDocument.getStream());
						if (clinicalDocument != null) {
							ret.add(clinicalDocument);
						}
					} catch (Exception e) {
						logger.error("Could not load document " + xdsDocument.getNewDocumentUniqueId() + " from xdm " //$NON-NLS-1$ //$NON-NLS-2$
								+ file.getAbsolutePath());
					}
				}
			}
		}
		return ret;
	}

	@Override
	public List<org.ehealth_connector.common.mdht.Patient> getXdmPatients(File file) {
		List<org.ehealth_connector.common.mdht.Patient> ret = null;
		ConvenienceCommunication conCom = new ConvenienceCommunication();
		XdmContents contents = conCom.getXdmContents(file.getAbsolutePath());
		if (contents != null) {
			ret = new ArrayList<org.ehealth_connector.common.mdht.Patient>();
			List<DocumentContentAndMetadata> dataList = contents.getDocumentAndMetadataList();
			for (DocumentContentAndMetadata documentContentAndMetadata : dataList) {
				ret.add(documentContentAndMetadata.getDocEntry().getPatient());
			}
		}
		return ret;
	}

	@Override
	public String createXdmContainer(Patient patient, Mandant mandant, List<File> attachments, String xdmPath) {
		if (patient != null && mandant != null && attachments != null && xdmPath != null) {
			ConvenienceCommunication conCom = new ConvenienceCommunication();
			org.ehealth_connector.common.mdht.Patient ehealthPatient = EhcCoreMapper.getEhcPatient(patient);
			System.out.println(ehealthPatient.getIds());
			StringBuilder retInfo = new StringBuilder();
			retInfo.append(xdmPath);
			for (File f : attachments) {
				try {
					if (f.exists()) {
						String attachmentPath = f.getAbsolutePath();
						DocumentDescriptor dc = null;
						if (attachmentPath.toLowerCase().endsWith("xml")) { //$NON-NLS-1$
							if (isCdaDocument(f)) {
								dc = DocumentDescriptor.CDA_R2;
							} else {
								dc = DocumentDescriptor.XML;
							}
						} else if (attachmentPath.toLowerCase().endsWith("pdf")) { //$NON-NLS-1$
							dc = DocumentDescriptor.PDF;
						} else {
							dc = new DocumentDescriptor(FilenameUtils.getExtension(attachmentPath),
									Files.probeContentType(f.toPath()));
						}

						FileInputStream in = FileUtils.openInputStream(f);
						DocumentMetadata metaData = conCom.addDocument(dc, in);
						metaData.setPatient(ehealthPatient);
						if (ehealthPatient.getIds() != null && !ehealthPatient.getIds().isEmpty()) {
							metaData.setDestinationPatientId(ehealthPatient.getIds().get(0));
						}
						IOUtils.closeQuietly(in);
						retInfo.append(":::"); //$NON-NLS-1$
						retInfo.append(attachmentPath);

					} else {
						LoggerFactory.getLogger(EhcCoreService.class).warn(
								"creating xdm - patient [{}] - file does not exists [{}]", patient.getId(), //$NON-NLS-1$
								f.getAbsolutePath());
					}
				} catch (IOException e) {
					LoggerFactory.getLogger(EhcCoreService.class).error(
							"creating xdm - patient [{}] - cannot add file [{}]", patient.getId(), f.getAbsolutePath(), //$NON-NLS-1$
							e);
				}
			}
			try {
				conCom.createXdmContents(xdmPath);
				if (retInfo.toString().contains(":::")) { //$NON-NLS-1$
					return retInfo.toString();
				}
			} catch (Exception e) {
				LoggerFactory.getLogger(EhcCoreService.class)
						.error("creating xdm - patient [{}] - cannot create xdm contents", patient.getId(), e); //$NON-NLS-1$
			}
		}
		return null;
	}

	@Override
	public boolean isCdaDocument(File file) {
		if (file != null) {
			try {
				FileInputStream in = FileUtils.openInputStream(file);
				ClinicalDocument c = CDAUtil.load(in);
				IOUtils.closeQuietly(in);
				return c != null;
			} catch (Exception e) {
				/* ignore */
			}
		}
		return false;
	}
}
