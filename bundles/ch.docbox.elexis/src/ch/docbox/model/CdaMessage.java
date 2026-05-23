/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.docbox.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.docbox.cdach.CdaChXPath;
import ch.docbox.elexis.UserDocboxPreferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.VirtualFilesystemServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class CdaMessage extends PersistentObject {
	private static Logger logger = LoggerFactory.getLogger(CdaMessage.class);

	public static final String TABLENAME = "CH_DOCBOX_ELEXIS_CDAMESSAGE";
	public static final String DBVERSION = "1.1.0";
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" + "ID			VARCHAR(25) primary key,"
			+ "CreationDate VARCHAR(15)," + "Deleted      CHAR(1) default '0'," + "DeletedDocs  CHAR(1) default '0',"
			+ "lastupdate   BIGINT," + "PatID		VARCHAR(25)," + "DocumentID	VARCHAR(25),"
			+ "AnwenderID	VARCHAR(25)," + "KonsultationID	VARCHAR(25)," + "Downloaded   CHAR(1) default '0',"
			+ "Date 		CHAR(24)," + "Unread       CHAR(1) default '1'," + "Title 		VARCHAR(255),"
			+ "Sender 		VARCHAR(255)," + "Patient 		VARCHAR(255)," + "FilesListing	VARCHAR(2048),"
			+ "Cda			BLOB);" + "CREATE INDEX CH_DOCBOX_ELEXIS_CDAMESSAGEI1 ON " + TABLENAME + " (PatID);"
			+ "CREATE INDEX CH_DOCBOX_ELEXIS_CDAMESSAGEI2 ON " + TABLENAME + " (DocumentID);"
			+ "CREATE INDEX CH_DOCBOX_ELEXIS_CDAMESSAGEI3 ON " + TABLENAME + " (AnwenderID);"
			+ "CREATE INDEX CH_DOCBOX_ELEXIS_CDAMESSAGEI4 ON " + TABLENAME + " (ID);" + "INSERT INTO " + TABLENAME
			+ " (ID, TITLE) VALUES ('1','" + DBVERSION + "');";

	public static final String upd110 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
			+ " MODIFY Title VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
			+ " MODIFY Sender VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
			+ " MODIFY Patient VARCHAR(255);"; //$NON-NLS-1$

	static {
		addMapping(TABLENAME, "CreationDate", "DeletedDocs", "PatID", "DocumentID", "AnwenderID", "KonsultationID",
				"Downloaded", "Unread", "Date", "Title", "Sender", "Patient", "FilesListing", "Cda");
		CdaMessage start = load("1");
		if (start == null) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get("Title"));
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					createOrModifyTable(upd110);
					start.set("Title", DBVERSION);
				} else {
					MessageDialog.openError(UiDesk.getTopShell(), "Versionskonsflikt", "Die Datentabelle für "
							+ TABLENAME + " hat eine zu alte Versionsnummer. Dies kann zu Fehlern führen");
				}
			}
		}
	}

	public static CdaMessage load(String id) {
		CdaMessage ret = new CdaMessage(id);
		if (ret.exists()) {
			return ret;
		}
		return null;
	}

	public static CdaMessage getCdaMessageEvenIfDocsDeleted(String documentId) {
		return getCdaMessage(CoreHub.actMandant, documentId, true);
	}

	public static CdaMessage getCdaMessage(String documentId) {
		return getCdaMessage(CoreHub.actMandant, documentId, false);
	}

	public static CdaMessage getCdaMessage(Anwender anwender, String documentId, boolean alsoDeletedDoc) {
		Query<CdaMessage> cdaMessageQuery = new Query<CdaMessage>(CdaMessage.class);
		cdaMessageQuery.add("AnwenderID", "=", anwender.getId());
		cdaMessageQuery.add("DocumentID", "=", documentId);
		if (!alsoDeletedDoc) {
			cdaMessageQuery.add("DeletedDocs", "=", "0");
		}
		Object[] cdaMessages = cdaMessageQuery.execute().toArray();
		if (cdaMessages == null || cdaMessages.length == 0) {
			return null;
		}
		if (cdaMessages.length > 1) {
			logger.error("CdaMessage Query should give only one object back but got multiple with AnwenderID=  "
					+ anwender.getId() + ", documentID " + documentId);
		}
		return (CdaMessage) cdaMessages[0];
	}

	public static Object[] getCdaMessages() {
		return getCdaMessages(CoreHub.actMandant, false);
	}

	public static Object[] getCdaMessages(Anwender anwender, boolean alsoDeletedDoc) {
		if (anwender != null) {
			logger.debug("getCdaMessages for " + anwender.getId());
			Query<CdaMessage> cdaMessageQuery = new Query<CdaMessage>(CdaMessage.class);
			cdaMessageQuery.add("AnwenderID", "=", anwender.getId());
			if (!alsoDeletedDoc) {
				cdaMessageQuery.add("DeletedDocs", "=", "0");
			}
			cdaMessageQuery.orderBy(true, "CreationDate");
			Object[] objects = cdaMessageQuery.execute().toArray();
			logger.debug("returned cdaMessages" + objects.length);
			return objects;
		}
		return new CdaMessage[0];
	}

	public CdaMessage(String documentId, String title, GregorianCalendar date) {
		create(null);
		TimeTool timeTool = new TimeTool();
		timeTool.set(date);
		set(new String[] { "AnwenderID", "DocumentID", "Title", "Date", "CreationDate", "Unread" },
				CoreHub.actMandant.getId(), documentId, title, timeTool.toString(TimeTool.DATE_GER),
				timeTool.toString(TimeTool.TIMESTAMP), "1");
	}

	public boolean setDownloaded(String sender, String patient) {
		return set(new String[] { "Sender", "Patient", "Downloaded" }, sender, patient, "1");
	}

	public boolean setCda(String cda) {
		if (cda != null) {
			try {
				setBinary("Cda", cda.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				return false;
			}
		}
		return true;
	}

	public String getCda() {
		try {
			return new String(getBinary("Cda"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		} catch (NullPointerException e) {
		}
		return null;
	}

	public static void init() {
		createOrModifyTable(createDB);
	}

	@Override
	public String getLabel() {
		StringBuilder sb = new StringBuilder();
		sb.append(get("Date")).append(StringUtils.SPACE).append(get("Title"));
		return sb.toString();
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public boolean deleteDocs() {
		if (this.getFiles() != null && this.getFiles().length > 0) {
			try {
				deleteDirectory(new File(this.getPath()));
			} catch (Exception e) {
			}
		}
		setDeletedDocs();
		return true;
	}

	/**
	 * currently all files are opened, if multiple we have not yet a selection
	 * possiblity
	 *
	 * @return
	 */
	public boolean execute() {
		String files[] = this.getFiles();
		if (files != null && files.length > 0) {
			for (String file : files) {
				int pos = file.lastIndexOf(".");
				String ext = StringUtils.EMPTY;
				if (pos > 0) {
					ext = file.substring(pos);
				}
				if (ext != null) {
					ext = ext.trim();
				}
				String path = getPath(file);
				if (path != null) {
					path = path.trim();
				}
				try {
					String osPath = path;
					if (StringUtils.isNotBlank(path)) {
						IVirtualFilesystemHandle h = VirtualFilesystemServiceHolder.get().of(path, true);
						java.util.Optional<File> local = h.toFile();
						if (local.isPresent()) {
							osPath = local.get().getAbsolutePath();
						}
					}

					Program program = Program.findProgram(ext);
					if (program != null) {
						program.execute(osPath);
					} else {
						if (!Program.launch(osPath)) {
							Runtime.getRuntime().exec(osPath);
						}
					}
				} catch (Exception ex) {
					ExHandler.handle(ex);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	protected CdaMessage(String id) {
		super(id);
	}

	protected CdaMessage() {
	}

	public boolean isDownloaded() {
		return getInt("Downloaded") != 0;
	}

	public boolean isDeletedDocs() {
		return getInt("DeletedDocs") != 0;
	}

	public void setDeletedDocs() {
		set(new String[] { "DeletedDocs" }, "1");
	}

	public void setRead() {
		set(new String[] { "Unread" }, "0");
	}

	public boolean isUnread() {
		return getInt("Unread") != 0;
	}

	public String getDate() {
		return get("Date");
	}

	public String getCreationDate() {
		return get("CreationDate");
	}

	public String getTitle() {
		return get("Title");
	}

	public String getSender() {
		return get("Sender");
	}

	public String getPatient() {
		return get("Patient");
	}

	public String getFilesListing() {
		return get("FilesListing");
	}

	public String[] getFiles() {
		return getFilesListing().split(StringUtils.LF);
	}

	public boolean hasAssignedToOmnivore() {
		return "ok".equals(getKonsultationId());
	}

	private String getKonsultationId() {
		return get("KonsultationID");
	}

	public void setAssignedToOmnivore() {
		set(new String[] { "KonsultationID" }, "ok");
	}

	/**
	 * returns the path where we will store the attachmetns
	 */
	public String getPath(String fileName) {
		String path = UserDocboxPreferences.getPathFiles();
		String pathSeparator = System.getProperty("file.separator");
		if (!path.endsWith(pathSeparator)) {
			path = path + pathSeparator;
		}
		path = path + getId();
		if (fileName != null) {
			path += pathSeparator + fileName;
		}
		return path;
	}

	private String getPath() {
		return getPath(null);
	}

	/**
	 * unzips the attachments to the specified directory in a subdirectory and sets
	 * the files extracted in the field fileslistings
	 *
	 * @param attachment byte array of a zip file
	 * @return true if successful false otherwise
	 */
	public boolean unzipAttachment(byte[] attachment) {
		ArrayList<String> fileList = new ArrayList<String>();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(attachment);
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		ZipEntry zipEntry = null;
		String path = this.getPath();
		try {
			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdir();
			}
			while ((zipEntry = zipInputStream.getNextEntry()) != null) {
				if (!zipEntry.isDirectory()) {
					String fileName = zipEntry.getName();
					fileList.add(fileName);
					if (fileName.contains("/")) {
						fileName = fileName.replaceAll("/", StringUtils.EMPTY);
					}
					if (fileName.contains("\\")) {
						fileName = fileName.replaceAll("\\\\", StringUtils.EMPTY);
					}
					logger.debug("exporting file out of attachment to " + path + "," + fileName);
					File file = new File(directory, fileName);
					if (!file.exists()) {
						file.createNewFile();
					}
					FileOutputStream fileOutputStream = new FileOutputStream(file);
					byte[] bytesEntry = new byte[1024];
					int read = 0;
					while ((read = zipInputStream.read(bytesEntry)) != -1) {
						fileOutputStream.write(bytesEntry, 0, read);
					}
					fileOutputStream.close();
				}
				zipInputStream.closeEntry();
			}
			zipInputStream.close();
			String fileListConcatenated = StringUtils.EMPTY;
			for (int i = 0; i < fileList.size(); ++i) {
				fileListConcatenated += fileList.get(i);
				if (i < fileList.size() - 1) {
					fileListConcatenated += " \n";
				}
			}
			return set("FilesListing", fileListConcatenated);
		} catch (Exception e) {
			logger.error("Exception " + e.toString());
		}
		return false;
	}

	@Override
	public boolean isDragOK() {
		return true;
	}

	public boolean isEqualsPatient(Patient patient) {
		CdaChXPath cdaChXPath = new CdaChXPath();
		String cda = getCda();
		if (patient == null) {
			return false;
		}
		if (cda != null) {
			cdaChXPath.setPatientDocument(cda);
			String lastName = cdaChXPath.getPatientLastName();
			String firstName = cdaChXPath.getPatientFirstName();
			String patientId = cdaChXPath.getPatientNumber();
			if (patientId != null && patientId.equals(patient.getId())) {
				return true;
			}
			if ((lastName == null || lastName.equals(patient.getName()))
					&& (firstName == null || firstName.equals(patient.getVorname()))) {
				return true;
			}
		}
		return false;
	}

	static public boolean deleteCdaMessages(Anwender anwender) {
		Object[] cdaMessages = getCdaMessages(anwender, true);
		if (cdaMessages != null) {
			logger.debug("trying to remove cdamessage " + cdaMessages.length);
			try {
				for (Object cdaMessageObject : cdaMessages) {
					CdaMessage cdaMessage = (CdaMessage) cdaMessageObject;
					logger.debug("deleting docs with id" + cdaMessage.getId());
					cdaMessage.deleteDocs();
					logger.debug("deleting cdaMessage with id" + cdaMessage.getId());
					cdaMessage.delete();
				}
			} catch (Exception e) {
				logger.debug("deleting message failed");
			}
		}
		return true;
	}

}
