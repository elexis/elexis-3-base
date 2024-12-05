/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package ch.elexis.labor.viollier.v2.labimport;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.ILabOrder;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.importer.div.importers.ILabItemResolver;
import ch.elexis.core.model.ILabResult;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.core.ui.importer.div.importers.TestHL7Parser;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabOrder;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.elexis.hl7.model.AbstractData;
import ch.elexis.hl7.model.ObservationMessage;
import ch.elexis.hl7.v22.HL7_ORU_R01;
import ch.elexis.importers.openmedical.MedTransfer;
import ch.elexis.labor.viollier.v2.Messages;
import ch.elexis.labor.viollier.v2.ViollierActivator;
import ch.elexis.labor.viollier.v2.data.ViollierLaborImportSettings;
import ch.elexis.laborimport.viollier.v2.data.KontaktOrderManagement;
import ch.elexis.laborimport.viollier.v2.data.LaborwerteOrderManagement;
import ch.elexis.laborimport.viollier.v2.util.ViollierLogger;
import ch.rgw.io.FileTool;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

/**
 * Klasse für den eigenlichen Labor Import
 *
 */
public class LabOrderImport extends ImporterPage {

	private static String DOMAIN_VIONR = "viollier.ch/vioNumber";

	public enum SaveResult {
		SUCCESS, ERROR
	};

	// Info zu JMedTransferO.jar
	// =========================
	// java -jar JMedTransferO.jar --help
	// Usage: JMedTransfer [-options]
	// Where options include :
	//
	// -ln <file name> Specify the file name where logging data will be stored
	// --logName <file name>
	//
	// -lp <path> Specify the directory where logging data will be stored
	// --logPath <path>
	//
	// --verbose ERR|WNG|INF Enable verbose output (Default is ERR)
	//
	// -i <file name> Specify the ini file to use
	// --ini <file name>
	//
	// -d <path> Specify the path where the data has to be downloaded
	// --download <path>
	//
	// -h Show this help
	// --help
	//
	// -v Show the product version
	// --version
	//
	// -allInOne
	// In case more accounts are defined, download all data in the same path
	// Without this parameter, when more accounts are defined,the downloaded data
	// are copied
	// in subdirectories defined by the name of the each account in the form
	// lastname_firstname

	// Als Domain für die Filler-Auftragsnummer die GLN von Viollier verwenden
	public static final String ORDER_NR_DOMAIN_FILLER = KontaktOrderManagement.ORDER_DOMAIN_LAB_ORDER_FILLER_VIOLLIER;

	private static String KUERZEL = Messages.PatientLabor_kuerzelViollier;

	private boolean doAllFiles = true;
	private Text tSingleHL7Filename;
	private String singleHL7Filename = StringUtils.EMPTY;;
	private Button bOverwrite;
	private Button bFile;
	private Button bDirect;
	private boolean settingProcessMode = false;
	private boolean settingOverwrite = false;
	private static TimeTool dateTime = new TimeTool();

	private static HL7Parser hlp = new DefaultHL7Parser(KUERZEL);

	protected final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
	private ViollierLaborImportSettings settings;

	public static void setTestMode(boolean value) {
		if (value) {
			LabOrderImport.hlp = new TestHL7Parser(KUERZEL);
		} else {
			LabOrderImport.hlp = new DefaultHL7Parser(KUERZEL);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.util.ImporterPage#doImport(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {

		// Aktuelles Log-File sichern und dann in ein neues Log-File speichern
		ViollierLogger.getLogger().backupLog(settings.getDirArchive());

		ViollierLogger.getLogger()
				.println(MessageFormat.format(Messages.LabOrderImport_StartImport, df.format(new Date())));
		ViollierLogger.getLogger().println("=============================================================="); //$NON-NLS-1$

		File downloadDir = null;
		boolean refRangeMismatch = false;
		int errorCount = 0;
		int errorMovedCount = 0;
		if (settings == null)
			settings = new ViollierLaborImportSettings((CoreHub.actMandant));

		File[] hl7Files = null;
		if (doAllFiles) {

			// JMedTransfer
			ViollierLogger.getLogger().println(Messages.LabOrderImport_StartMedTransfer);
			int count = MedTransfer.doDownload(settings.getJMedTransferJar(), settings.getDirDownload(),
					settings.getGlobalJMedTransferParam());
			ViollierLogger.getLogger()
					.println(MessageFormat.format(Messages.LabOrderImport_InfoNumberDonloadedFiles, count));

			// Eigentlicher Import
			downloadDir = new File(settings.getDirDownload());
			ViollierLogger.getLogger()
					.println(MessageFormat.format(Messages.LabOrderImport_InfoReadDownloadDir, downloadDir));
			if (downloadDir.isDirectory()) {
				hl7Files = downloadDir.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".hl7"); //$NON-NLS-1$
					}
				});
			}
		} else {
			try {
				File file = new File(singleHL7Filename);
				if (file.isFile())
					downloadDir = file.getParentFile();
				hl7Files = new File[] { file

				};
			} catch (Exception ex) {
			}
			if (hl7Files == null) {
				String err = MessageFormat.format(Messages.LabOrderImport_ErrorFileNotFound, singleHL7Filename);
				ViollierLogger.getLogger().println(err);
				SWTHelper.showError(Messages.LabOrderImport_errorTitle, err);
			}

		}
		if (hl7Files != null) {
			ViollierLogger.getLogger()
					.println(MessageFormat.format(Messages.LabOrderImport_InfoProcessFiles, hl7Files.length));
			monitor.beginTask(Messages.LabOrderImport_monitorImportiereHL7, hl7Files.length);
			if (hl7Files != null) {
				for (File hl7File : hl7Files) {
					if (monitor.isCanceled()) {
						break;
					}
					String msg = MessageFormat.format(Messages.LabOrderImport_InfoParseFile, hl7File.getName());
					ViollierLogger.getLogger().println(msg);
					monitor.subTask(msg);

					AtomicReference<File> pdfFileRef = new AtomicReference<File>();

					SaveResult importResult = doImportOneFile(hl7File, pdfFileRef, settings, settingOverwrite);

					File pdfFile = pdfFileRef.get();
					if (importResult != SaveResult.ERROR) {
						// Archivieren
						if (doAllFiles) {
							moveToArchiv(hl7File);
							if (pdfFile != null)
								moveToArchiv(pdfFile);
						}
					} else {
						if (doAllFiles) {
							if (moveToError(hl7File)) {
								errorMovedCount++;
							}
							if (pdfFile != null)
								moveToError(pdfFile);
						}
						errorCount++;
						monitor.subTask(MessageFormat.format(Messages.LabOrderImport_ErrorWhileParsingHL7File,
								hl7File.getName()));
					}
					ViollierLogger.getLogger().println("--------------------------------------------------"); //$NON-NLS-1$
					monitor.worked(1);
				}
			}
		}

		if (errorCount > 0) {
			String errorDir = settings.getDirError();
			SWTHelper.showError(Messages.LabOrderImport_errorTitle, MessageFormat
					.format(Messages.LabOrderImport_errorMsgVerarbeitung, errorCount, errorMovedCount, errorDir));
		} else {
			if (refRangeMismatch) {
				SWTHelper.showInfo(Messages.LabOrderImport_ReferenceRangeWarningTitle,
						Messages.LabOrderImport_ReferenceRangeWarningText + ViollierLogger.getLogger().getLocation()
								+ "'"); //$NON-NLS-1$
			} else {
				SWTHelper.showInfo(Messages.LabOrderImport_ImportCompletedTitle,
						Messages.LabOrderImport_ImportCompletedSSuccessText + ViollierLogger.getLogger().getLocation()
								+ "'"); //$NON-NLS-1$
			}
		}

		ViollierLogger.getLogger()
				.println(MessageFormat.format(Messages.LabOrderImport_EndImport, df.format(new Date())));
		ViollierLogger.getLogger().println(StringUtils.EMPTY);

		// Bereinigung der alten Archiv Dateien
		deleteOldArchivFiles();

		return Status.OK_STATUS;
	}

	/**
	 * Führt den Import eines einzelnen HL7 Files durch
	 *
	 * @param hl7File               HL7 Datei, welche importiert werden soll
	 * @param pdfFileRef            By Reference Parameter, der in der Methode
	 *                              gesetzt wird, wenn ein PDF Befund zum
	 *                              angegebenen HL7 File importiert wird
	 * @param settings              Aktuell gültige Einstellungen
	 * @param overwriteOlderEntries true, wenn Laborwerte überschrieben werden
	 *                              sollen, auch wenn bereits ein neuerer Wert in
	 *                              der DB vorhanden ist. Sonst false (false ist
	 *                              Normalfall!)
	 * @return SUCCESS oder ERROR
	 * @throws IOException
	 */
	public static SaveResult doImportOneFile(File hl7File, AtomicReference<File> pdfFileRef,
			ViollierLaborImportSettings settings, boolean overwriteOlderEntries) throws IOException {
		return doImportOneFile(hl7File, pdfFileRef, settings, overwriteOlderEntries, true);

	}

	/**
	 * Führt den Import eines einzelnen HL7 Files durch
	 *
	 * @param hl7File               HL7 Datei, welche importiert werden soll
	 * @param pdfFileRef            By Reference Parameter, der in der Methode
	 *                              gesetzt wird, wenn ein PDF Befund zum
	 *                              angegebenen HL7 File importiert wird
	 * @param settings              Aktuell gültige Einstellungen
	 * @param overwriteOlderEntries true, wenn Laborwerte überschrieben werden
	 *                              sollen, auch wenn bereits ein neuerer Wert in
	 *                              der DB vorhanden ist. Sonst false (false ist
	 *                              Normalfall!)
	 * @param askUser               true, wenn Benutzerinterface verwendet werden
	 *                              soll (Normalfall!). falls, wenn Ablauf ohne GUI
	 *                              gewünscht ist (für JUnit Tests)
	 *
	 * @return SUCCESS oder ERROR
	 * @throws IOException
	 */
	public static SaveResult doImportOneFile(File hl7File, AtomicReference<File> pdfFileRef,
			ViollierLaborImportSettings settings, boolean overwriteOlderEntries, boolean askUser) throws IOException {

		SaveResult saveResult = SaveResult.ERROR;

		HL7_ORU_R01 hl7OruR01 = new HL7_ORU_R01();
		ObservationMessage observation = null;
		Patient patient = null;
		PatientLabor labor = null;
		String orderId = null;
		File downloadDir = hl7File.getParentFile();

		// HL7 Datei lesen
		try {
			String text = FileTool.readTextFile(hl7File, ViollierActivator.TEXT_ENCODING);
			observation = hl7OruR01.readObservation(text);
			saveResult = SaveResult.SUCCESS;
			for (String error : hl7OruR01.getErrorList()) {
				saveResult = SaveResult.ERROR;
				ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_Error, error));
			}
			for (String warn : hl7OruR01.getWarningList()) {
				ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_Warning, warn));
			}
			if (saveResult == SaveResult.SUCCESS) {
				patient = getPatient(observation, askUser);
				if (patient != null) {

					labor = new PatientLabor(patient);

					Result<?> result = hlp.importFile(hl7File, new File(settings.getDirArchive()),
							new ILabItemResolver() {
								@Override
								public String getTestName(AbstractData data) {
									return data.getName();
								}

								@Override
								public String getTestGroupName(AbstractData data) {
									return "Labor Viollier";
								}

								@Override
								public String getNextTestGroupSequence(AbstractData data) {
									return PatientLabor.DEFAULT_PRIO;
								}
							}, false);
					if (result.isOK()) {
						// get created results using the orderId
						Object obj = result.get();
						if (obj instanceof String) {
							List<ILabOrder> orders = LabOrder.getLabOrdersByOrderId((String) obj);
							if (orders != null && !orders.isEmpty()) {
								resolveTimeForPdf((LabResult) orders.get(0).getLabResult(), observation);
								for (ILabOrder labOrder : orders) {
									new LaborwerteOrderManagement(labOrder.getLabResult().getId(), orderId);
								}
							}
						}
					}

					orderId = getAuftragsId(observation);
					// Wenn HL7 Import VioNumber enthält --> in XID speichern
					if (!observation.getAlternatePatientId().isEmpty()) {
						addVioNumber(observation.getAlternatePatientId(), patient);
					}
				} else {
					saveResult = SaveResult.ERROR;
				}
			}
		} catch (ElexisException ex) {
			saveResult = SaveResult.ERROR;
			String cause = StringUtils.EMPTY;
			if (ex.getCause() != null) {
				if (ex.getCause().getMessage() != null) {
					cause = ex.getCause().getMessage();
				}
			}
			ViollierLogger.getLogger()
					.println(MessageFormat.format(Messages.LabOrderImport_ErrorHL7Exception, ex.getMessage(), cause));
		} catch (Exception ex) {
			saveResult = SaveResult.ERROR;
			ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_Error,
					ex.getClass().getName() + ": " + ex.getMessage()));
		}

		if ((patient != null) && (labor != null) && (observation != null)) {
			// PDF in Omnivore aufnehmen, falls vorhanden
			// 3: Transaktionsnummer (entpricht MSH-10) --> Prüfen
			final String crit1 = observation.getMessageControlID();
			// 9: Geburtsdatum Patient (entpricht PID.7) --> Prüfen
			final String crit4 = observation.getPatientBirthdate();

			File[] pdfFiles = downloadDir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					boolean crit1found = false;
					boolean crit4found = false;
					if (name.toLowerCase().endsWith(".pdf")) { //$NON-NLS-1$
						String items[] = name.toLowerCase().split("[_]"); //$NON-NLS-1$
						if (items.length > 2) {
							// look for the criteria in all found items
							for (int i = 0; i < items.length; i++) {
								if (items[i].equalsIgnoreCase(crit1)) {
									crit1found = true;
								} else if (items[i].equalsIgnoreCase(crit4)) {
									crit4found = true;
								}
							}
						}
					}
					return crit1found && crit4found;
				}
			});
			if (pdfFiles.length > 0) {
				if (pdfFiles.length > 1) {
					ViollierLogger.getLogger().println(
							MessageFormat.format(Messages.LabOrderImport_ErrorMultiplePDFFilesFound, pdfFiles.length));
				} else {
					// PDF in Omnivore aufnehmen
					File pdfFile = pdfFiles[0];
					pdfFileRef.set(pdfFile);
					String filename = pdfFile.getName();

					SimpleDateFormat sdfTitle = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
					String title = Messages.LabOrderImport_LabResult + sdfTitle.format(dateTime.getTime()) + "." //$NON-NLS-1$
							+ FileTool.getExtension(filename);

					labor.saveLaborItem(title, settings.getDocumentCategory(), pdfFile, dateTime.getTime(), orderId,
							filename, StringUtils.EMPTY, StringUtils.EMPTY);

				}
			}
		}
		return saveResult;
	}

	/**
	 * Prio 1: use observation time of {@link LabResult}<br>
	 * Prio 2: use analyze time of {@link LabResult}<br>
	 * Prio 3: use transmission time of {@link LabResult}<br>
	 * Prio 4: use transaction time of {@link ObservationMessage}<br>
	 * Prio 5: use time of message {@link ObservationMessage}<br>
	 * Prio 6: use current date time<br>
	 *
	 * @param labResult
	 * @param observation
	 */
	private static void resolveTimeForPdf(LabResult labResult, ObservationMessage observation) {
		boolean timeSet = false;
		if (labResult != null) {
			TimeTool obsTime = labResult.getObservationTime();
			if (obsTime != null) {
				dateTime.set(obsTime);
				timeSet = true;
			} else if (labResult.getAnalyseTime() != null) {
				dateTime.set(labResult.getAnalyseTime());
				timeSet = true;
			} else if (labResult.getTransmissionTime() != null) {
				dateTime.set(labResult.getTransmissionTime());
				timeSet = true;
			}
		}

		if (!timeSet) {
			Date timeStamp = observation.getDateTimeOfTransaction();
			if (timeStamp == null) {
				timeStamp = observation.getDateTimeOfMessage();
				if (timeStamp == null) {
					timeStamp = new Date();
				}
			}
			dateTime.setTime(timeStamp);
		}
	}

	/**
	 * Anhand der Einstellungen (Default 30 Tage) werden alle Dateien im Archiv
	 * Verzeichnis gelöscht die älter als die konfigurierten Tage sind.
	 */
	private void deleteOldArchivFiles() {
		int archivDeleted = 0;
		ViollierLogger.getLogger().println(Messages.LabOrderImport_InfoPurgingArchiveDir);

		int days = settings.getArchivePurgeInterval();
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DATE, -days);
		long lastTime = cal.getTime().getTime();

		// Archiv löschen
		String archivDirName = settings.getDirArchive();
		if (archivDirName != null) {
			File archivDir = new File(archivDirName);
			if (archivDir.exists() && archivDir.isDirectory()) {
				for (File archivFile : archivDir.listFiles()) {
					if (archivFile.lastModified() < lastTime) {
						if (archivFile.delete()) {
							archivDeleted++;
						}
					}
				}
			}
			ViollierLogger.getLogger()
					.println(MessageFormat.format(Messages.LabOrderImport_PurgeArchiveDir, archivDeleted));
		}

		ViollierLogger.getLogger().println(StringUtils.EMPTY);
	}

	/**
	 * Datei wird ins Archiv Verzeichnis verschoben
	 *
	 * @param file Datei, welche ins Archiv Verzeichnis verschoben werden soll
	 * @return true bei Erfolg, sonst false.
	 */
	private boolean moveToArchiv(final File file) {
		String archivDir = settings.getDirArchive();
		boolean ok = false;
		if (FileTool.copyFile(file, new File(archivDir + File.separator + file.getName()),
				FileTool.REPLACE_IF_EXISTS)) {
			ok = file.delete();
		}
		return ok;
	}

	/**
	 * Datei wird ins Error Verzeichnis verschoben
	 *
	 * @param file Datei, welche ins Error Verzeichnis verschoben werden soll
	 * @return true bei Erfolg, sonst false.
	 */
	private boolean moveToError(final File file) {
		String errorDir = settings.getDirError();
		boolean ok = false;
		if (errorDir != null && errorDir.length() > 0) {
			if (FileTool.copyFile(file, new File(errorDir + File.separator + file.getName()),
					FileTool.REPLACE_IF_EXISTS)) {
				ok = file.delete();
			}
		}
		return ok;
	}

	/**
	 * Wenn der Patient eine VioNummer hat, wird diese in der XID-Tabelle
	 * gespeichert.
	 *
	 * @param vioNumber Die VioNummer aus dem HL7-Import
	 * @param patient   Patient, dem die VioNumber zugeordnet werden soll
	 * @return SUCCESS oder ERROR
	 */
	private static SaveResult addVioNumber(String vioNumber, Patient patient) {
		SaveResult retVal = SaveResult.ERROR;
		XidServiceHolder.get().localRegisterXIDDomainIfNotExists(DOMAIN_VIONR, "vioNumber", Xid.ASSIGNMENT_LOCAL);
		if (StringUtils.EMPTY.equals(getVioNr(patient))) {
			IPatient iPatient = NoPoUtil.loadAsIdentifiable(patient, IPatient.class).orElse(null);
			if (iPatient != null) {
				iPatient.addXid(DOMAIN_VIONR, vioNumber, true);
				retVal = SaveResult.SUCCESS;
				ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_InfoSaveXid, vioNumber,
						patient.getId(), patient.getLabel()));
			}
		}
		return retVal;
	}

	/**
	 * Liest alle Patient mit einer bestimmten PatientenNr. Eigentlich sollte es nur
	 * 1 Patient geben, aber man weiss ja nie!
	 *
	 * @param patId Patienten-ID
	 * @return List der gefundenen Patienten
	 */
	private static List<Patient> readPatienten(final String patId) {
		Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		patientQuery.add(Patient.FLD_PATID, Query.EQUALS, patId);
		return patientQuery.execute();
	}

	/**
	 * Liest Patienten anhand Name, Vorname und Geburtsdatum
	 *
	 * @param patVorname  Vorname des Patienten, der gesucht werden soll
	 * @param patNachname Nachname des Patienten, der gesucht werden soll
	 * @param patBirthday Geburtsdatum des Patienten, der gesucht werden soll
	 * @param patSex      Geschlecht des Patienten, der gesucht werden soll
	 * @return List der gefundenen Patienten
	 */
	public static List<Patient> readPatienten(final String patNachname, final String patVorname,
			final String patBirthday, final String patSex) {
		String sex = patSex.toLowerCase();
		if ("f".equals(sex)) //$NON-NLS-1$
			sex = "w"; //$NON-NLS-1$
		Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		patientQuery.add(Patient.FLD_NAME, Query.EQUALS, patNachname);
		patientQuery.add(Patient.FLD_FIRSTNAME, Query.EQUALS, patVorname);
		patientQuery.add(Patient.FLD_SEX, Query.EQUALS, sex);
		patientQuery.add(Patient.FLD_DOB, Query.EQUALS, patBirthday);
		return patientQuery.execute();
	}

	/**
	 * Sucht Elexis Patient. <br>
	 * Falls Auftragsnummer (ORC-2) existiert, dann wird anhand der Tabelle
	 * KONTAKT_ORDER_MANAGEMENT der zugehörige Patient gesucht. <br>
	 * Wenn keine Auftragsnummer (ORC-2) vorhanden ist, dann wird über der Patient
	 * über die PatientNr (PID-3) gesucht.
	 *
	 * @param observation Angaben aus der HL7 Nachricht
	 * @param askUser     true, wenn Benutzerinterface verwendet werden soll
	 *                    (Normalfall!). falls, wenn Ablauf ohne GUI gewünscht ist
	 *                    (für JUnit Tests)
	 * @return gefundener Patient oder null
	 */
	private static Patient getPatient(final ObservationMessage observation, boolean askUser) {
		// Suche Patient anhand internal PID oder Auftragsnummer
		Patient patient = null;
		boolean fillerAuftragExists = false;

		String patientId = observation.getPatientId();
		String auftragsNrPlacerString = observation.getOrderNumberPlacer();
		String auftragsNrFillerString = observation.getOrderNumberFiller();

		long auftragsNrPlacer = -1;
		if (auftragsNrPlacerString != null && auftragsNrPlacerString.length() > 0) {
			try {
				auftragsNrPlacer = Long.parseLong(auftragsNrPlacerString);
			} catch (Exception ex) {
				ViollierLogger.getLogger().println(MessageFormat.format(
						Messages.LabOrderImport_ErrorNonNumericPlacerOrderNumber, auftragsNrPlacerString, patientId));
				return null;
			}
		}

		long auftragsNrFiller = -1;
		if (auftragsNrFillerString != null && auftragsNrFillerString.length() > 0) {
			try {
				auftragsNrFiller = Long.parseLong(auftragsNrFillerString);
			} catch (Exception ex) {
				ViollierLogger.getLogger().println(MessageFormat.format(
						Messages.LabOrderImport_ErrorNonNumericFillerOrderNumber, auftragsNrFillerString, patientId));
				return null;
			}
		}

		if (auftragsNrPlacer > 0) {
			// Patient anhand eigener Auftragsnummer identifizieren
			Query<KontaktOrderManagement> patientOrderNrQuery = new Query<KontaktOrderManagement>(
					KontaktOrderManagement.class);
			patientOrderNrQuery.add(KontaktOrderManagement.FLD_ORDER_NR, Query.EQUALS, auftragsNrPlacerString);
			List<KontaktOrderManagement> patientOrderNrList = patientOrderNrQuery.execute();
			if (patientOrderNrList.size() > 0) {
				List<Patient> patientList = readPatienten(patientId);
				for (KontaktOrderManagement kontaktOrderMgt : patientOrderNrList) {
					String kontaktId = kontaktOrderMgt.getKontaktId();
					for (Patient pat : patientList) {
						if (kontaktId.equals(pat.getId())) {
							patient = pat;
						}
					}
				}

				if (patient == null)
					patient = findPatient(observation, askUser);

				if (patient == null) {
					ViollierLogger.getLogger()
							.println(MessageFormat.format(Messages.LabOrderImport_ErrorMatchingPatientWithOrderNr,
									auftragsNrPlacerString, patientId));
				}
			}
		} else if (auftragsNrFiller > 0) {
			// Patient anhand Auftragsnummer des Labors identifizieren
			List<KontaktOrderManagement> patientOrderNrList = getByNumberAndDomain(Long.toString(auftragsNrFiller),
					ORDER_NR_DOMAIN_FILLER);
			if (patientOrderNrList.size() > 0) {
				fillerAuftragExists = true;
				List<Patient> patientList = readPatienten(patientId);
				for (KontaktOrderManagement kontaktOrderMgt : patientOrderNrList) {
					String kontaktId = kontaktOrderMgt.getKontaktId();
					for (Patient pat : patientList) {
						if (kontaktId.equals(pat.getId())) {
							patient = pat;
						}
					}
				}
			}

			if (patient == null)
				patient = findPatient(observation, askUser);

			if (patient == null) {
				ViollierLogger.getLogger().println(MessageFormat.format(
						Messages.LabOrderImport_ErrorMatchingPatientWithOrderNr, auftragsNrFillerString, patientId));
			}
		} else {
			// Versuche Patient anhand ID in PID-2 zu identifizieren
			String patName = observation.getPatientName();
			List<Patient> patientList = readPatienten(patientId);
			for (Patient listPat : patientList) {
				if (patName.equalsIgnoreCase(listPat.getName() + StringUtils.SPACE + listPat.getVorname())) {
					patient = listPat;
				}
			}

			if (patient == null)
				patient = findPatient(observation, askUser);
		}

		if (patient == null) {
			ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_ErrorIdentifyingPatient,
					observation.getPatientName(), patientId, auftragsNrPlacerString, auftragsNrFillerString));
		} else {
			if ((!fillerAuftragExists) && (auftragsNrFiller > 0)) {
				// Laborauftragsnummer zu Patient nacherfassen
				ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_InfoStoredFillerOrderNr,
						auftragsNrFillerString, patientId, patient.getLabel()));

				List<KontaktOrderManagement> orders = getByNumberAndDomain(auftragsNrFillerString,
						ORDER_NR_DOMAIN_FILLER);
				if (orders.isEmpty()) {
					// neuen Eintrag in DB erstellen:
					new KontaktOrderManagement(patient, auftragsNrFillerString, ORDER_NR_DOMAIN_FILLER);
				}
			}
		}

		return patient;
	}

	private static List<KontaktOrderManagement> getByNumberAndDomain(String number, String domain) {
		Query<KontaktOrderManagement> patientOrderNrQuery = new Query<KontaktOrderManagement>(
				KontaktOrderManagement.class);

		patientOrderNrQuery.add(KontaktOrderManagement.FLD_ORDER_NR, Query.EQUALS, number);
		patientOrderNrQuery.add(KontaktOrderManagement.FLD_ORDER_NR_DOMAIN, Query.EQUALS, ORDER_NR_DOMAIN_FILLER);
		return patientOrderNrQuery.execute();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.util.ImporterPage#getTitle()
	 */
	@Override
	public String getTitle() {
		return Messages.LabOrderImport_titleImport;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see ch.elexis.util.ImporterPage#getDescription()
	 */
	@Override
	public String getDescription() {
		return Messages.LabOrderImport_descriptionImport;
	}

	@Override
	public List<java.lang.String> getObjectClass() {
		return Arrays.asList(ILabResult.class.getName(), "ch.elexis.omnivore.model.IDocumentHandle");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * ch.elexis.util.ImporterPage#createPage(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Composite createPage(final Composite parent) {

		if (settings == null)
			settings = new ViollierLaborImportSettings((CoreHub.actMandant));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		composite.setLayout(new GridLayout(3, false));

		bOverwrite = new Button(composite, SWT.CHECK);
		bOverwrite.setText(Messages.LabOrderImport_OverwriteOlderValues);
		bOverwrite.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		GridData gd = SWTHelper.getFillGridData(1, false, 1, false);
		gd.horizontalAlignment = GridData.END;
		gd.widthHint = bOverwrite.getSize().x + 20;

		bFile = new Button(composite, SWT.RADIO);
		bFile.setText(Messages.LabOrderImport_ImportFromHL7);
		bFile.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		Label lFile = new Label(composite, SWT.NONE);
		lFile.setText(Messages.LabOrderImport_HL7File);

		tSingleHL7Filename = new Text(composite, SWT.BORDER);
		tSingleHL7Filename.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Button bBrowse = new Button(composite, SWT.PUSH);
		bBrowse.setText(Messages.LabOrderImport_Browse);

		bDirect = new Button(composite, SWT.RADIO);
		bDirect.setText(Messages.LabOrderImport_AutomaticMedTransfer);
		bDirect.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		// JMedTransfer
		Label lblJMedTransfer = new Label(composite, SWT.NONE);
		lblJMedTransfer.setText(Messages.LabOrderImport_JMedTransfer);
		final Text txtJMedTransfer = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtJMedTransfer.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String jmedTransfer = settings.getJMedTransferJar() + StringUtils.SPACE + settings.getJMedTransferParam();
		if (jmedTransfer != null) {
			txtJMedTransfer.setText(jmedTransfer);
		}
		new Label(composite, SWT.NONE); // Dummy zum auffüllen des Grid

		// Download Dir
		Label lblDirDownload = new Label(composite, SWT.NONE);
		lblDirDownload.setText(Messages.Preferences_DirDownload);
		final Text txtDirDownload = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtDirDownload.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String dirDownload = settings.getDirDownload();
		if (dirDownload != null) {
			txtDirDownload.setText(dirDownload);
		}
		new Label(composite, SWT.NONE); // Dummy zum auffüllen des Grid

		// Archive Dir
		Label lblDirArchive = new Label(composite, SWT.NONE);
		lblDirArchive.setText(Messages.Preferences_DirArchive);
		final Text txtDirArchive = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtDirArchive.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String dirArchive = settings.getDirArchive();
		if (dirArchive != null) {
			txtDirArchive.setText(dirArchive);
		}
		new Label(composite, SWT.NONE); // Dummy zum auffüllen des Grid

		// Error Dir
		Label lblDirError = new Label(composite, SWT.NONE);
		lblDirError.setText(Messages.Preferences_DirError);
		final Text txtDirError = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtDirError.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String dirError = settings.getDirError();
		if (dirError != null) {
			txtDirError.setText(dirError);
		}
		new Label(composite, SWT.NONE); // Dummy zum auffüllen des Grid

		// Kategorie Verzeichnis
		Label lblKategorie = new Label(composite, SWT.NONE);
		lblKategorie.setText(Messages.Preferences_DocumentCategory);

		final Text txtKategorie = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
		txtKategorie.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		String kategorie = settings.getDocumentCategory();
		if (kategorie != null) {
			txtKategorie.setText(kategorie);
		}

		setProcessModeAllFiles();

		bOverwrite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				Button button = (Button) e.getSource();

				if (button == bOverwrite)
					settingOverwrite = button.getSelection();
			}

		});

		SelectionAdapter sa = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button button = (Button) e.getSource();

				if (button.getSelection()) {
					settingProcessMode = true;
					if (button == bFile)
						setProcessModeSingleFile();
					if (button == bDirect)
						setProcessModeAllFiles();
					settingProcessMode = false;
				}
			}
		};

		bFile.addSelectionListener(sa);
		bDirect.addSelectionListener(sa);

		bBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				FileDialog fdl = new FileDialog(parent.getShell(), SWT.OPEN);
				fdl.setFilterExtensions(new String[] { "*.hl7", "*" }); //$NON-NLS-1$ //$NON-NLS-2$
				fdl.setFilterNames(new String[] { Messages.LabOrderImport_HL7Files, Messages.LabOrderImport_AllFiles });
				fdl.setFilterPath(txtDirDownload.getText());
				singleHL7Filename = fdl.open();
				if (singleHL7Filename == null) {
					singleHL7Filename = StringUtils.EMPTY;
				}

				tSingleHL7Filename.setText(singleHL7Filename);
				setProcessModeSingleFile();
			}

		});

		tSingleHL7Filename.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				setProcessModeSingleFile();
				singleHL7Filename = tSingleHL7Filename.getText();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});

		return composite;
	}

	/**
	 * Sucht den, zur HL7 Nachricht passenden Patienten
	 *
	 * @param observation Angaben aus der HL7 Nachricht
	 * @param askUser     true, wenn Benutzerinterface verwendet werden soll
	 *                    (Normalfall!). falls, wenn Ablauf ohne GUI gewünscht ist
	 *                    (für JUnit Tests)
	 * @return Gefundener Patient oder null
	 */
	private static Patient findPatient(ObservationMessage observation, boolean askUser) {
		Patient patient = null;

		// Versuche Patient anhand Name, Vorname und Geburtsdatum zu identifizieren
		if (patient == null) {
			patient = findExactPatient(observation);
			if (patient != null) {
				ViollierLogger.getLogger().println(MessageFormat
						.format(Messages.LabOrderImport_InfoPatientIdentifiedByExactDemographics, patient.getLabel()));
			}
		}

		// Versuche Patient anhand Benutzerabfrage zu identifizieren
		if (patient == null) {
			if (askUser)
				patient = selectPatient(observation);
			if (patient != null) {
				ViollierLogger.getLogger().println(MessageFormat.format(Messages.LabOrderImport_PatientIdentifiedByUser,
						observation.getPatientName(), patient.getLabel()));
			} else {
				ViollierLogger.getLogger().println(MessageFormat.format(
						Messages.LabOrderImport_WarningUserAbortWhileIdentifyingPatient, observation.getPatientName()));
			}
		}

		return patient;
	}

	/**
	 * Lässt den Patienten vom Benutzer auswählen
	 *
	 * @param observation Angaben aus der HL7 Nachricht
	 * @return Patient, den der Benutzer ausgewählt hat. Bei Abbruch durch den
	 *         Benutzer: null
	 */
	private static Patient selectPatient(ObservationMessage observation) {
		Patient retVal = null;
		String patDemographics = observation.getPatientName() + StringUtils.SPACE + observation.getPatientBirthdate();
		retVal = (Patient) KontaktSelektor.showInSync(Patient.class, Messages.LabOrderImport_SelectPatient,
				MessageFormat.format(Messages.LabOrderImport_WhoIs, patDemographics));
		return retVal;
	}

	/**
	 * Sucht den Patienten nach exakter Übereinstimmung von Name, Vorname,
	 * Geburtsdatum und Geschlecht
	 *
	 * @param observation Angaben aus der HL7 Nachricht
	 * @return Gefundener Patient oder null
	 */
	private static Patient findExactPatient(ObservationMessage observation) {
		Patient retVal = null;
		String patNachname = observation.getPatientLastName();
		String patVorname = observation.getPatientFirstName();
		String patSex = observation.getPatientSex();
		String patBirthday = observation.getPatientBirthdate();
		List<Patient> patientList = readPatienten(patNachname, patVorname, patBirthday, patSex);
		if (patientList.size() == 1)
			retVal = patientList.get(0);
		return retVal;
	}

	/**
	 * Handler für Radiobutton
	 */
	private void setProcessModeAllFiles() {
		doAllFiles = true;
		if (!settingProcessMode) {
			settingProcessMode = true;
			bFile.setSelection(false);
			bDirect.setSelection(true);
			settingProcessMode = false;
		}
	}

	/**
	 * Handler für Radiobutton
	 */
	private void setProcessModeSingleFile() {
		doAllFiles = false;
		if (!settingProcessMode) {
			settingProcessMode = true;
			bFile.setSelection(true);
			bDirect.setSelection(false);
			settingProcessMode = false;
		}
	}

	/**
	 * Liefert die ID des Eintrags in KontaktOrderManagement zurück, sofern ein
	 * Eintrag existiert
	 *
	 * @param observation Angaben aus der HL7 Nachricht
	 * @return ID des Eintrags in KontaktOrderManagement oder null
	 */
	private static String getAuftragsId(ObservationMessage observation) {
		String orderId = StringUtils.EMPTY;
		String auftragsNrFiller = StringUtils.EMPTY;
		try {
			auftragsNrFiller = observation.getOrderNumberFiller();
		} catch (Exception ex) {
		}
		List<KontaktOrderManagement> orderNrList = getByNumberAndDomain(auftragsNrFiller, ORDER_NR_DOMAIN_FILLER);
		if (orderNrList.size() > 0) {
			orderId = orderNrList.get(0).getId();
		}
		return orderId;
	}

	/**
	 * Liefert die VioNummer des Patienten zurück, sofern eine erfasst ist.
	 *
	 * @param patient Gewünschter Patient
	 * @return VioNummer des Patienten, sofern eine erfasst ist. Sonst leerer String
	 */
	public static String getVioNr(Patient patient) {
		IXid found = XidServiceHolder.get().getXid(NoPoUtil.loadAsIdentifiable(patient, IPatient.class).orElse(null),
				DOMAIN_VIONR);
		if (found != null) {
			return found.getDomainId();
		}
		return StringUtils.EMPTY;
	}
}
