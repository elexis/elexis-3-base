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
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.hl7.model.EncapsulatedData;
import ch.elexis.hl7.model.StringData;
import ch.elexis.hl7.model.TextData;
import ch.elexis.labor.viollier.v2.Messages;
import ch.elexis.labor.viollier.v2.data.ViollierLaborImportSettings;
import ch.elexis.laborimport.viollier.v2.data.LaborwerteOrderManagement;
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
		SUCCESS, REF_RANGE_MISMATCH, ERROR
	};
	
	public static String LABOR_NAME = Messages.PatientLabor_nameViollierLabor;
	public static String DEFAULT_PRIO = "50"; //$NON-NLS-1$
	public static String FORMAT_DATE = "yyyyMMdd"; //$NON-NLS-1$
	public static String FORMAT_TIME = "HHmmss"; //$NON-NLS-1$
	
	private static String KUERZEL = Messages.PatientLabor_kuerzelViollier;
	private static String FIELD_ORGIN = "Quelle"; //$NON-NLS-1$
	private static int MAX_LEN_RESULT = 80; // Spaltenlänge LABORWERTE.Result
	
	private ViollierLaborImportSettings settings;
	private Labor myLab = null;
	
	private final Patient patient;
	
	private IDocumentManager docManager;
	
	private boolean overwriteResults = false;
	
	/**
	 * Konstruktor mit Angabe des aktuellen Patienten
	 * 
	 * @param patient
	 */
	public PatientLabor(Patient patient){
		super();
		this.patient = patient;
		myLab = LabImportUtil.getOrCreateLabor(KUERZEL);
		initDocumentManager();
	}
	
	/**
	 * Setting zum Überschreiben von bestehenden Laborresultaten.
	 * 
	 * @param value
	 *            true, wenn Laborwerte überschrieben werden sollen, auch wenn bereits ein neuerer
	 *            Wert in der DB vorhanden ist. Sonst false (false ist Normalfall!)
	 */
	public void setOverwriteResults(boolean value){
		overwriteResults = value;
	}
	
	/**
	 * Initialisiert document manager (omnivore) falls vorhanden
	 */
	private void initDocumentManager(){
		settings = new ViollierLaborImportSettings((CoreHub.actMandant));
		Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (os != null) {
			this.docManager = (IDocumentManager) os;
		}
	}
	
	/**
	 * Prüft, ob die angegebene Kategorie in Omivore existiert. Falls dies nicht der Fall ist, wird
	 * sie gleich erstellt
	 */
	private void checkCreateCategory(final String category){
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
					ViollierLogger.getLogger().println(
						MessageFormat.format(Messages.LabOrderImport_InfoCategoryCreate, category));
				} else {
					ViollierLogger.getLogger().println(
						MessageFormat.format(Messages.LabOrderImport_WarnCategoryCreate, category));
				}
			}
		}
	}
	
	/**
	 * Fügt ein neues Dokument in Omnivore hinzu (falls es nicht bereits existierte)
	 * 
	 * @param title
	 *            Titel des Dokuments
	 * @param category
	 *            Gewünschte Kategorie, unter welcher das Dokument abgelegt werden soll
	 * @param dateStr
	 *            Zeitstempel des Dokuments
	 * @param file
	 *            Eigentliches Dokumente, das archiviert werden soll
	 * @param keywords
	 *            Schlüsselwörter zum Dokument
	 * @return true bei Erfolg. Sonst false
	 * @throws IOException
	 * @throws ElexisException
	 */
	private boolean addDocument(final String title, final String category, final String dateStr,
		final File file, String keywords) throws IOException, ElexisException{
		checkCreateCategory(category);
		
		List<IOpaqueDocument> documentList =
			this.docManager.listDocuments(this.patient, category, title, null, new TimeSpan(dateStr
				+ "-" + dateStr), null); //$NON-NLS-1$
		
		if (documentList == null || documentList.size() == 0) {
			this.docManager.addDocument(new GenericDocument(this.patient, title, category, file,
				dateStr, keywords, FileTool.getExtension(file.getName())));
			return true;
		}
		return false;
	}
	
	/**
	 * Liest gewünschtes LabItem
	 * 
	 * @param kuerzel
	 *            Kürzel, nach dem gesucht werden soll
	 * @param name
	 *            Name, nach dem gesucht werden soll
	 * @param type
	 *            Typ, nach dem gesucht werden soll
	 * @return LabItem falls exisitiert. Sonst null
	 */
	private LabItem getLabItem(String kuerzel, String name, LabItem.typ type){
		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		qli.add(LabItem.SHORTNAME, "=", kuerzel); //$NON-NLS-1$ //$NON-NLS-2$
		qli.and();
		qli.add(LabItem.LAB_ID, "=", myLab.get("ID")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			qli.add(LabItem.LAB_ID, "=", myLab.get("ID")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			qli.and();
			qli.add(LabItem.TYPE, "=", new Integer(type.ordinal()).toString()); //$NON-NLS-1$
			
			labItem = null;
			itemList = qli.execute();
			if (itemList.size() > 0) {
				labItem = itemList.get(0);
			}
		}
		return labItem;
	}
	
	/**
	 * Liest gewünschtes Laborresultat
	 * 
	 * @param labItem
	 *            Labortest, zu welchem ein Resultat gesucht werden soll
	 * @param date
	 *            Datum, nach welchem gesucht werden soll
	 * @return Gefundenes Resultat oder null
	 */
	private LabResult getLabResult(LabItem labItem, String date){
		Query<LabResult> qli = new Query<LabResult>(LabResult.class);
		qli.add(LabResult.ITEM_ID, "=", labItem.getId()); //$NON-NLS-1$
		qli.and();
		qli.add(LabResult.DATE, "=", date); //$NON-NLS-1$ //$NON-NLS-2$
		qli.and();
		qli.add(LabResult.PATIENT_ID, "=", patient.getId()); //$NON-NLS-1$
		
		LabResult labResult = null;
		List<LabResult> resultList = qli.execute();
		if (resultList.size() > 0) {
			labResult = resultList.get(0);
		}
		return labResult;
	}
	
	/**
	 * Liest gewünschtes Laborresultat
	 * 
	 * @param labItem
	 *            Labortest, zu welchem ein Resultat gesucht werden soll
	 * @param name
	 *            Resultatewert, nach welchem gesucht werden soll
	 * @param date
	 *            Datum, nach welchem gesucht werden soll
	 * @return Gefundenes Resultat oder null
	 */
	private LabResult getLabResult(LabItem labItem, String name, String date){
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
	 * 
	 * Fügt Laborwert zu Patientenlabor hinzu
	 * 
	 * @param data
	 * 
	 * @param timeStamp
	 *            Zeitpunkt der Messung
	 * @param orderId
	 *            Fremdschlüssel auf kontakt_order_management.id
	 * @param updateRefRange
	 *            true, wenn Referenzbereich überschrieben werden soll. Sonst false.
	 * @return SUCCESS, REF_RANGE_MISMATCH oder ERROR
	 */
	public SaveResult saveLaborItem(final StringData data, Date timeStamp, String orderId,
		boolean updateRefRange){
		SaveResult retVal = SaveResult.SUCCESS;
		String name = data.getName();
		String kuerzel = data.getName();
		if (kuerzel == null || "".equals(kuerzel)) { //$NON-NLS-1$
			kuerzel = data.getName();
		}
		
		LabItem.typ type = LabItem.typ.NUMERIC;
		LabItem labItem = getLabItem(kuerzel, name, type);
		if (labItem == null) {
			
			String group = data.getGroup();
			if (group == null || "".equals(group)) { //$NON-NLS-1$
				group = LABOR_NAME;
			}
			String sequence = data.getSequence();
			if (sequence == null || "".equals(sequence)) { //$NON-NLS-1$
				sequence = DEFAULT_PRIO;
			}
			labItem =
				new LabItem(kuerzel, name, myLab, null, null, data.getUnit(), type, group, sequence);
		}
		
		boolean refRangeOK = true;
		String refRangeHL7 = data.getRange();
		String refRangeElexis = ""; //$NON-NLS-1$
		// RefFrau, bzw. RefMann aktualisieren
		if (Patient.MALE.equals(patient.getGeschlecht())) {
			refRangeElexis = labItem.getRefM();
			if (updateRefRange || refRangeElexis == null || "".equals(refRangeElexis)) { //$NON-NLS-1$
				if (refRangeHL7 != null && !"".equals(refRangeHL7)) { //$NON-NLS-1$
					if (!refRangeHL7.equals(refRangeElexis)) {
						ViollierLogger
							.getLogger()
							.println(
								MessageFormat.format(
									Messages.PatientLabor_InfoOverwriteRefRange,
									labItem.getKuerzel() + "-" + labItem.getName(), patient.getGeschlecht(), refRangeElexis, refRangeHL7)); //$NON-NLS-2$
						labItem.setRefM(refRangeHL7);
						refRangeElexis = labItem.getRefM();
					}
				}
			}
		} else {
			refRangeElexis = labItem.getRefW();
			if (updateRefRange || refRangeElexis == null || "".equals(refRangeElexis)) { //$NON-NLS-1$
				if (refRangeHL7 != null && !"".equals(refRangeHL7)) { //$NON-NLS-1$
					if (!refRangeHL7.equals(refRangeElexis)) {
						ViollierLogger
							.getLogger()
							.println(
								MessageFormat.format(
									Messages.PatientLabor_InfoOverwriteRefRange,
									labItem.getKuerzel() + "-" + labItem.getName(), patient.getGeschlecht(), refRangeElexis, refRangeHL7)); //$NON-NLS-2$
						labItem.setRefW(refRangeHL7);
						refRangeElexis = labItem.getRefW();
					}
				}
			}
		}
		
		if ((refRangeElexis != null && !"".equals(refRangeElexis)) //$NON-NLS-1$
			&& (refRangeHL7 != null && !"".equals(refRangeHL7))) { //$NON-NLS-1$
			refRangeOK = (refRangeHL7.equals(refRangeElexis));
		}
		if (!refRangeOK) {
			ViollierLogger
				.getLogger()
				.println(
					MessageFormat.format(
						Messages.PatientLabor_WarningRefRangeMismatch,
						labItem.getKuerzel() + "-" + labItem.getName(), patient.getGeschlecht(), refRangeElexis, refRangeHL7)); //$NON-NLS-2$
			retVal = SaveResult.REF_RANGE_MISMATCH;
		}
		
		TimeTool dateTime = new TimeTool();
		dateTime.setTime(timeStamp);
		SimpleDateFormat sdfDatum = new SimpleDateFormat(FORMAT_DATE);
		SimpleDateFormat sdfZeit = new SimpleDateFormat(FORMAT_TIME);
		String datum = sdfDatum.format(timeStamp);
		String zeit = sdfZeit.format(timeStamp);
		
		String result = data.getValue();
		String comment = data.getComment();
		if ((result != null) || ("".equals(result))) { //$NON-NLS-1$
			if (result.length() > MAX_LEN_RESULT) {
				if (comment == null)
					comment = ""; //$NON-NLS-1$
				if (comment.length() > 0) {
					comment = result + "\\.br\\" + comment; //$NON-NLS-1$
				} else {
					comment = result;
				}
				result = Messages.PatientLabor_TextForComments;
			}
		}
		
		LabResult lr = getLabResult(labItem, datum);
		if (lr == null) {
			// Neues Laborresultat erstellen
			lr = new LabResult(patient, dateTime, labItem, result, comment); //$NON-NLS-1$
			lr.set(FIELD_ORGIN, orderId); //$NON-NLS-1$
			lr.set(LabResult.TIME, zeit); //$NON-NLS-1$
			
			// Jetzt noch in die LABORWERTE_ORDER_JOINT Tabelle reinspitzen
			new LaborwerteOrderManagement(lr.getId(), orderId);
		} else {
			// bestehendes Laborresultat ändern, sofern es neuer ist als das bereits gespeicherte
			if ((overwriteResults)
				|| (lr.getDateTime().getTimeInMillis() < dateTime.getTimeInMillis())) {
				ViollierLogger
					.getLogger()
					.println(
						MessageFormat.format(
							Messages.PatientLabor_InfoOverwriteValue,
							labItem.getKuerzel() + "-" + labItem.getName(), lr.getDateTime().toDBString(true), dateTime.toDBString(true), lr.getResult(), data.getValue())); //$NON-NLS-2$
				lr.setResult(data.getValue());
				lr.set(LabResult.TIME, zeit); //$NON-NLS-1$
			} else {
				ViollierLogger
					.getLogger()
					.println(
						MessageFormat.format(
							Messages.PatientLabor_InfoNewerResultAlreadyExists,
							labItem.getKuerzel() + "-" + labItem.getName(), lr.getDateTime().toDBString(true), dateTime.toDBString(true), lr.getResult(), data.getValue())); //$NON-NLS-2$
			}
		}
		
		return retVal;
	}
	
	/**
	 * Fügt Laborwert zu Patientenlabor hinzu
	 * 
	 * @param data
	 *            Text, welcher gespeichert werden soll
	 * @param timeStamp
	 *            Timestamp, welcher gespeichert werden soll
	 * @param orderId
	 *            Fremdschlüssel auf kontakt_order_management.id
	 */
	public void saveLaborItem(final TextData data, Date timeStamp, String orderId){
		String name = data.getName();
		String kuerzel = data.getName();
		if (kuerzel == null || "".equals(kuerzel)) { //$NON-NLS-1$
			kuerzel = data.getName();
		}
		LabItem labItem = getLabItem(kuerzel, name, LabItem.typ.TEXT);
		if (labItem == null) {
			String group = data.getGroup();
			if (group == null || group.length() == 0) {
				group = LABOR_NAME;
			}
			String sequence = data.getSequence();
			if (sequence == null || sequence.length() == 0) {
				sequence = DEFAULT_PRIO;
			}
			labItem =
				new LabItem(data.getName(), data.getName(), myLab, null, null,
					"", LabItem.typ.TEXT, group, sequence); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		TimeTool dateTime = new TimeTool();
		dateTime.setTime(timeStamp);
		SimpleDateFormat sdfDatum = new SimpleDateFormat(FORMAT_DATE);
		SimpleDateFormat sdfZeit = new SimpleDateFormat(FORMAT_TIME);
		String datum = sdfDatum.format(timeStamp);
		String zeit = sdfZeit.format(timeStamp);
		
		LabResult lr = getLabResult(labItem, datum);
		if (lr == null) {
			// Neues Laborresultat erstellen
			lr =
				new LabResult(patient, dateTime, labItem, Messages.PatientLabor_TextForComments,
					data.getText());
			lr.set(FIELD_ORGIN, orderId); //$NON-NLS-1$
			lr.set(LabResult.TIME, zeit); //$NON-NLS-1$
		} else {
			// bestehendes Laborresultat ändern, sofern es neuer ist als das bereits gespeicherte
			if ((overwriteResults)
				|| (lr.getDateTime().getTimeInMillis() < dateTime.getTimeInMillis())) {
				ViollierLogger
					.getLogger()
					.println(
						MessageFormat.format(
							Messages.PatientLabor_InfoOverwriteValue,
							labItem.getKuerzel() + "-" + labItem.getName(), lr.getDateTime().toDBString(true), dateTime.toDBString(true), lr.getResult(), data.getText())); //$NON-NLS-2$
				lr.set(LabResult.COMMENT, data.getText());
				lr.set(LabResult.TIME, zeit); //$NON-NLS-1$
			} else {
				ViollierLogger
					.getLogger()
					.println(
						MessageFormat.format(
							Messages.PatientLabor_InfoExistingValueIsValid,
							labItem.getKuerzel() + "-" + labItem.getName(), lr.getDateTime().toDBString(true), dateTime.toDBString(true), lr.getResult(), data.getText())); //$NON-NLS-2$
			}
		}
	}
	
	/**
	 * Fügt Laborwert zu Patientenlabor hinzu
	 * 
	 * @param data
	 *            Eingebettetes File, das gespeichert werden soll
	 * @param timeStamp
	 *            Timestamp, welcher gespeichert werden soll
	 * @param orderId
	 *            Fremdschlüssel auf kontakt_order_management.id
	 */
	public void saveLaborItem(EncapsulatedData data, Date timeStamp, String orderId)
		throws IOException{
		String downloadDir = settings.getDirDownload();
		
		// Tmp Verzeichnis überprüfen
		File tmpDir = new File(downloadDir + File.separator + "tmp"); //$NON-NLS-1$
		if (!tmpDir.exists()) {
			if (!tmpDir.mkdirs()) {
				throw new IOException(MessageFormat.format(
					Messages.PatientLabor_errorCreatingTmpDir, tmpDir.getName()));
			}
		}
		String filename = data.getName();
		File tmpPdfFile =
			new File(downloadDir + File.separator + "tmp" + File.separator + filename); //$NON-NLS-1$
		tmpPdfFile.deleteOnExit();
		FileTool.writeFile(tmpPdfFile, data.getData());
		
		String category = settings.getDocumentCategory();
		
		saveLaborItem(filename, category, tmpPdfFile, timeStamp, orderId, data.getGroup(),
			data.getSequence(), data.getComment());
		
	}
	
	public void saveLaborItem(String title, String category, File file, Date timeStamp,
		String orderId, String comment) throws IOException{
		saveLaborItem(title, category, file, timeStamp, orderId, comment, "", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Speichert externer Laborbefund
	 * 
	 * @param title
	 *            Titel, der gespeichert werden soll
	 * @param category
	 *            Kategorie, die verwendet werden soll
	 * @param file
	 *            File, das archiviert werden soll
	 * @param timeStamp
	 *            Timestamp, welcher gespeichert werden soll
	 * @param orderId
	 *            Fremdschlüssel auf kontakt_order_management.id
	 * @param keyword
	 *            Schlüsselwörter, welche gespeichert werden sollen
	 * @param group
	 *            Gruppierung der Labortests, welche gespeichert werden soll
	 * @param sequence
	 *            Sequenz des Labortests, welche gespeichert werden soll
	 * @throws IOException
	 */
	public void saveLaborItem(String title, String category, File file, Date timeStamp,
		String orderId, String keyword, String group, String sequence) throws IOException{
		String filename = file.getName();
		if (this.docManager == null) {
			throw new IOException(MessageFormat.format(
				Messages.PatientLabor_errorKeineDokumentablage, filename, this.patient.getLabel()));
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
		
		LabItem labItem = getLabItem(kuerzel, name, LabItem.typ.DOCUMENT);
		if (labItem == null) {
			if (group == null || group.length() == 0) {
				group = LABOR_NAME;
			}
			if (sequence == null || sequence.length() == 0) {
				sequence = DEFAULT_PRIO;
			}
			labItem =
				new LabItem(kuerzel, Messages.PatientLabor_nameDokumentLaborParameter, myLab,
					"", "", //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
					"pdf", LabItem.typ.DOCUMENT, group, sequence); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (orderId == null || "".equals(orderId)) { //$NON-NLS-1$
			orderId = LABOR_NAME;
		}
		
		boolean saved = false;
		LabResult lr = getLabResult(labItem, title, datum);
		if (lr == null) {
			// Neues Laborresultat erstellen
			lr = new LabResult(patient, dateTime, labItem, title, null); //$NON-NLS-1$
			lr.set(FIELD_ORGIN, orderId); //$NON-NLS-1$
			lr.set(LabResult.TIME, zeit); //$NON-NLS-1$
			saved = true;
		} else {
			// bestehendes Laborresultat ändern, sofern es neuer ist als das bereits gespeicherte
			if ((overwriteResults)
				|| (lr.getDateTime().getTimeInMillis() < dateTime.getTimeInMillis())) {
				ViollierLogger
					.getLogger()
					.println(
						MessageFormat.format(
							Messages.PatientLabor_InfoOverwriteValue,
							labItem.getKuerzel() + "-" + labItem.getName(), lr.getDateTime().toDBString(true), dateTime.toDBString(true), lr.getResult(), title)); //$NON-NLS-2$
				lr.setResult(title);
				lr.set(LabResult.TIME, zeit); //$NON-NLS-1$
				saved = true;
			} else {
				ViollierLogger
					.getLogger()
					.println(
						MessageFormat.format(
							Messages.PatientLabor_InfoExistingValueIsValid,
							labItem.getKuerzel() + "-" + labItem.getName(), lr.getDateTime().toDBString(true), dateTime.toDBString(true), lr.getResult(), title)); //$NON-NLS-2$
			}
		}
		
		if (saved) {
			// Dokument in Omnivore archivieren
			try {
				String dateTimeStr = dateTime.toString(TimeTool.DATE_GER);
				
				// Zu Dokumentablage hinzufügen
				addDocument(title, category, dateTimeStr, file, keyword);
				ViollierLogger.getLogger().println(
					MessageFormat.format(Messages.PatientLabor_InfoDocSavedToOmnivore, title));
				
			} catch (ElexisException e) {
				throw new IOException(MessageFormat.format(
					Messages.PatientLabor_errorAddingDocument, filename), e);
			}
		}
	}
}
