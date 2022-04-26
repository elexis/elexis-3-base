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
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.labor.viollier.v2.Messages;
import ch.elexis.labor.viollier.v2.data.ViollierLaborImportSettings;
import ch.elexis.laborimport.viollier.v2.util.ViollierLogger;
import ch.rgw.io.FileTool;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

/**
 * Hilfsklasse für den Viollier Laborimporter
 *
 */
public class PatientLabor {

	public enum SaveResult {
		SUCCESS, ERROR
	};

	public static String LABOR_NAME = Messages.PatientLabor_nameViollierLabor;
	public static String DEFAULT_PRIO = "50"; //$NON-NLS-1$
	public static String FORMAT_DATE = "yyyyMMdd"; //$NON-NLS-1$
	public static String FORMAT_TIME = "HHmmss"; //$NON-NLS-1$

	private static String KUERZEL = Messages.PatientLabor_kuerzelViollier;
	private static String FIELD_ORGIN = "Quelle"; //$NON-NLS-1$
	private static int MAX_LEN_RESULT = 80; // Spaltenlänge LABORWERTE.Result

	private ViollierLaborImportSettings settings;
	private ILaboratory myLab = null;

	private final Patient patient;

	private IDocumentManager docManager;

	private boolean overwriteResults = false;

	/**
	 * Konstruktor mit Angabe des aktuellen Patienten
	 *
	 * @param patient
	 */
	public PatientLabor(Patient patient) {
		super();
		this.patient = patient;
		myLab = LabImportUtilHolder.get().getOrCreateLabor(KUERZEL);
		initDocumentManager();
	}

	/**
	 * Initialisiert document manager (omnivore) falls vorhanden
	 */
	private void initDocumentManager() {
		settings = new ViollierLaborImportSettings((CoreHub.actMandant));
		Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (os != null) {
			this.docManager = (IDocumentManager) os;
		}
	}

	/**
	 * Prüft, ob die angegebene Kategorie in Omivore existiert. Falls dies nicht der
	 * Fall ist, wird sie gleich erstellt
	 */
	private void checkCreateCategory(final String category) {
		if (category != null) {
			boolean catExists = false;
			String[] categories = this.docManager.getCategories();
			if (categories != null) {
				for (String cat : categories) {
					if (category.equals(cat)) {
						catExists = true;
					}
				}
			}
			if (!catExists) {
				Boolean result = this.docManager.addCategorie(category);
				if (result) {
					ViollierLogger.getLogger()
							.println(MessageFormat.format(Messages.LabOrderImport_InfoCategoryCreate, category));
				} else {
					ViollierLogger.getLogger()
							.println(MessageFormat.format(Messages.LabOrderImport_WarnCategoryCreate, category));
				}
			}
		}
	}

	/**
	 * Fügt ein neues Dokument in Omnivore hinzu (falls es nicht bereits existierte)
	 *
	 * @param title    Titel des Dokuments
	 * @param category Gewünschte Kategorie, unter welcher das Dokument abgelegt
	 *                 werden soll
	 * @param dateStr  Zeitstempel des Dokuments
	 * @param file     Eigentliches Dokumente, das archiviert werden soll
	 * @param keywords Schlüsselwörter zum Dokument
	 * @return true bei Erfolg. Sonst false
	 * @throws IOException
	 * @throws ElexisException
	 */
	private boolean addDocument(final String title, final String category, final String dateStr, final File file,
			String keywords) throws IOException, ElexisException {
		checkCreateCategory(category);

		List<IOpaqueDocument> documentList = this.docManager.listDocuments(this.patient, category, title, null,
				new TimeSpan(dateStr + "-" + dateStr), null); //$NON-NLS-1$

		if (documentList == null || documentList.size() == 0) {
			this.docManager.addDocument(new GenericDocument(this.patient, title, category, file, dateStr, keywords,
					FileTool.getExtension(file.getName())));
			return true;
		}
		return false;
	}

	/**
	 * Liest gewünschtes LabItem
	 *
	 * @param kuerzel Kürzel, nach dem gesucht werden soll
	 * @param name    Name, nach dem gesucht werden soll
	 * @param type    Typ, nach dem gesucht werden soll
	 * @return LabItem falls exisitiert. Sonst null
	 */
	private ILabItem getLabItem(String kuerzel, String name, LabItemTyp type) {
		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		qli.add(LabItem.SHORTNAME, "=", kuerzel); //$NON-NLS-1$ //$NON-NLS-2$
		qli.and();
		qli.add(LabItem.LAB_ID, "=", myLab.getId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		qli.and();
		qli.add(LabItem.TYPE, "=", new Integer(type.ordinal()).toString()); //$NON-NLS-1$

		LabItem labItem = null;
		List<LabItem> itemList = qli.execute();
		if (itemList.size() > 0) {
			labItem = itemList.get(0);
		} else {
			// Rückwärtskompatibilität
			qli = new Query<LabItem>(LabItem.class);
			qli.add(LabItem.SHORTNAME, "=", name); //$NON-NLS-1$ //$NON-NLS-2$
			qli.and();
			qli.add(LabItem.LAB_ID, "=", myLab.getId()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			qli.and();
			qli.add(LabItem.TYPE, "=", new Integer(type.ordinal()).toString()); //$NON-NLS-1$

			labItem = null;
			itemList = qli.execute();
			if (itemList.size() > 0) {
				labItem = itemList.get(0);
			}
		}
		if (labItem != null) {
			return CoreModelServiceHolder.get().load(labItem.getId(), ILabItem.class).orElse(null);
		}
		return null;
	}

	/**
	 * Liest gewünschtes Laborresultat
	 *
	 * @param labItem Labortest, zu welchem ein Resultat gesucht werden soll
	 * @param name    Resultatewert, nach welchem gesucht werden soll
	 * @param date    Datum, nach welchem gesucht werden soll
	 * @return Gefundenes Resultat oder null
	 */
	private LabResult getLabResult(ILabItem labItem, String name, String date) {
		Query<LabResult> qli = new Query<LabResult>(LabResult.class);
		qli.add(LabResult.ITEM_ID, "=", labItem.getId()); //$NON-NLS-1$
		qli.and();
		qli.add(LabResult.DATE, "=", date); //$NON-NLS-1$ //$NON-NLS-2$
		qli.and();
		qli.add(LabResult.PATIENT_ID, "=", patient.getId()); //$NON-NLS-1$
		qli.and();
		qli.add(LabResult.RESULT, "=", name); //$NON-NLS-1$ //$NON-NLS-2$

		LabResult labResult = null;
		List<LabResult> resultList = qli.execute();
		if (resultList.size() > 0) {
			labResult = resultList.get(0);
		}
		return labResult;
	}

	/**
	 * Speichert externer Laborbefund
	 *
	 * @param title     Titel, der gespeichert werden soll
	 * @param category  Kategorie, die verwendet werden soll
	 * @param file      File, das archiviert werden soll
	 * @param timeStamp Timestamp, welcher gespeichert werden soll
	 * @param orderId   Fremdschlüssel auf kontakt_order_management.id
	 * @param keyword   Schlüsselwörter, welche gespeichert werden sollen
	 * @param group     Gruppierung der Labortests, welche gespeichert werden soll
	 * @param sequence  Sequenz des Labortests, welche gespeichert werden soll
	 * @throws IOException
	 */
	public void saveLaborItem(String title, String category, File file, Date timeStamp, String orderId, String keyword,
			String group, String sequence) throws IOException {
		String filename = file.getName();
		if (this.docManager == null) {
			throw new IOException(MessageFormat.format(Messages.PatientLabor_errorKeineDokumentablage, filename,
					this.patient.getLabel()));
		}

		// Kategorie überprüfen/ erstellen
		checkCreateCategory(category);

		TimeTool dateTime = new TimeTool();
		dateTime.setTime(timeStamp);
		SimpleDateFormat sdfDatum = new SimpleDateFormat(FORMAT_DATE);
		SimpleDateFormat sdfZeit = new SimpleDateFormat(FORMAT_TIME);
		String datum = sdfDatum.format(timeStamp);
		String zeit = sdfZeit.format(timeStamp);

		if (title.length() > MAX_LEN_RESULT)
			title = "..." + title.substring(title.length() - MAX_LEN_RESULT + 3, title.length()); //$NON-NLS-1$

		// Labor Item erstellen
		String name = Messages.PatientLabor_DocumentLabItemName;
		String kuerzel = "doc"; //$NON-NLS-1$

		ILabItem labItem = getLabItem(kuerzel, name, LabItemTyp.DOCUMENT);
		if (labItem == null) {
			if (group == null || group.length() == 0) {
				group = LABOR_NAME;
			}
			if (sequence == null || sequence.length() == 0) {
				sequence = DEFAULT_PRIO;
			}
			labItem = LabImportUtilHolder.get().createLabItem(kuerzel, Messages.PatientLabor_nameDokumentLaborParameter,
					myLab, "", "", //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					"pdf", LabItemTyp.DOCUMENT, group, sequence); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (orderId == null || "".equals(orderId)) { //$NON-NLS-1$
			orderId = LABOR_NAME;
		}

		boolean saved = false;
		LabResult lr = getLabResult(labItem, title, datum);
		if (lr == null) {
			// Neues Laborresultat erstellen
			lr = new LabResult(patient, dateTime, LabItem.load(labItem.getId()), title, null); // $NON-NLS-1$
			lr.set(FIELD_ORGIN, orderId); // $NON-NLS-1$
			lr.set(LabResult.TIME, zeit); // $NON-NLS-1$
			saved = true;
		} else {
			// bestehendes Laborresultat ändern, sofern es neuer ist als das bereits
			// gespeicherte
			if ((overwriteResults) || (lr.getDateTime().getTimeInMillis() < dateTime.getTimeInMillis())) {
				ViollierLogger.getLogger()
						.println(MessageFormat.format(Messages.PatientLabor_InfoOverwriteValue,
								labItem.getCode() + "-" + labItem.getName(), lr.getDateTime().toDBString(true),
								dateTime.toDBString(true), lr.getResult(), title)); // $NON-NLS-2$
				lr.setResult(title);
				lr.set(LabResult.TIME, zeit); // $NON-NLS-1$
				saved = true;
			} else {
				ViollierLogger.getLogger()
						.println(MessageFormat.format(Messages.PatientLabor_InfoExistingValueIsValid,
								labItem.getCode() + "-" + labItem.getName(), lr.getDateTime().toDBString(true),
								dateTime.toDBString(true), lr.getResult(), title)); // $NON-NLS-2$
			}
		}

		if (saved) {
			// Dokument in Omnivore archivieren
			try {
				String dateTimeStr = dateTime.toString(TimeTool.DATE_GER);

				// Zu Dokumentablage hinzufügen
				addDocument(title, category, dateTimeStr, file, keyword);
				ViollierLogger.getLogger()
						.println(MessageFormat.format(Messages.PatientLabor_InfoDocSavedToOmnivore, title));

			} catch (ElexisException e) {
				throw new IOException(MessageFormat.format(Messages.PatientLabor_errorAddingDocument, filename), e);
			}
		}
	}
}
