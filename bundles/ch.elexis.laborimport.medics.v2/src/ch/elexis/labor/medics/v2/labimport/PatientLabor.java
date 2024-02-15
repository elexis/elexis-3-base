package ch.elexis.labor.medics.v2.labimport;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.hl7.model.EncapsulatedData;
import ch.elexis.hl7.model.StringData;
import ch.elexis.hl7.model.TextData;
import ch.elexis.labor.medics.v2.MedicsPreferencePage;
import ch.elexis.labor.medics.v2.Messages;
import ch.rgw.io.FileTool;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class PatientLabor {
	public static String KUERZEL = Messages.PatientLabor_kuerzelMedics;
	public static String LABOR_NAME = Messages.PatientLabor_nameMedicsLabor;

	private Labor myLab = null;

	private final Patient patient;

	private IDocumentManager docManager;

	public PatientLabor(Patient patient) {
		super();
		this.patient = patient;
		initLabor();
		initDocumentManager();
	}

	/**
	 * Initialisiert document manager (omnivore) falls vorhanden
	 */
	private void initDocumentManager() {
		Object os = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (os != null) {
			this.docManager = (IDocumentManager) os;
		}
	}

	/**
	 * Check if category exists. If not, the category is created
	 */
	private void checkCreateCategory(final String category) {
		if (category != null) {
			boolean catExists = false;
			for (String cat : this.docManager.getCategories()) {
				if (category.equals(cat)) {
					catExists = true;
				}
			}
			if (!catExists) {
				this.docManager.addCategorie(category);
			}
		}
	}

	/**
	 * Adds a document to omnivore (if it not already exists)
	 *
	 * @return boolean. True if added false if not
	 * @throws ElexisException
	 * @throws IOException
	 */
	private boolean addDocument(final String title, final String category, final String dateStr, final File file)
			throws IOException, ElexisException {
		checkCreateCategory(category);

		List<IOpaqueDocument> documentList = this.docManager.listDocuments(this.patient, category, title, null,
				new TimeSpan(dateStr + "-" + dateStr), null);

		if (documentList == null || documentList.size() == 0) {
			this.docManager.addDocument(new GenericDocument(this.patient, title, category, file, dateStr, null, null));
			return true;
		}
		return false;
	}

	private void initLabor() {
		Query<Labor> qbe = new Query<Labor>(Labor.class);
		qbe.add("Kuerzel", "LIKE", "%" + KUERZEL + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		List<Labor> list = qbe.execute();

		if (list.size() < 1) {
			myLab = new Labor(KUERZEL, LABOR_NAME); // $NON-NLS-1$
		} else {
			myLab = list.get(0);
		}
	}

	/**
	 * Liest LabItem
	 *
	 * @param kuerzel
	 * @param type
	 * @return LabItem falls exisitiert. Sonst null
	 */
	private LabItem getLabItem(String kuerzel, LabItemTyp type) {
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
		}
		return labItem;
	}

	/**
	 * Liest LabItem
	 *
	 * @param kuerzel
	 * @param type
	 * @return LabItem falls exisitiert. Sonst null
	 */
	private LabResult getLabResult(LabItem labItem, String name, TimeTool date) {
		Query<LabResult> qli = new Query<LabResult>(LabResult.class);
		qli.add(LabResult.ITEM_ID, "=", labItem.getId()); //$NON-NLS-1$
		qli.and();
		qli.add(LabResult.DATE, "=", date.toDBString(false)); //$NON-NLS-1$ //$NON-NLS-2$
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
	 * Fügt Laborwert zu Patientenlabor hinzu
	 *
	 * @param data
	 */
	public void addLaborItem(final StringData data) {
		LabItem labItem = getLabItem(data.getName(), LabItemTyp.NUMERIC);
		if (labItem == null) {
			String group = data.getGroup();
			if (group == null || group.length() == 0) {
				group = LABOR_NAME;
			}
			String sequence = data.getSequence();
			if (sequence == null || sequence.length() == 0) {
				sequence = "50";
			}
			labItem = new LabItem(data.getName(), data.getName(), myLab, null, null, data.getUnit(), LabItemTyp.NUMERIC,
					group, sequence);
		}

		// RefFrau, bzw. RefMann aktualisieren
		if (Patient.MALE.equals(patient.getGeschlecht())) {
			String labRefMann = labItem.getRefM();
			if (labRefMann == null || labRefMann.length() == 0) {
				String newRefMann = data.getRange();
				if (newRefMann != null && newRefMann.length() > 0) {
					labItem.setRefM(newRefMann);
				}
			}
		} else {
			String labRefFrau = labItem.getRefW();
			if (labRefFrau == null || labRefFrau.length() == 0) {
				String newRefFrau = data.getRange();
				if (newRefFrau != null && newRefFrau.length() > 0) {
					labItem.setRefW(newRefFrau);
				}
			}
		}

		TimeTool dateTime = new TimeTool();
		dateTime.setTime(data.getDate());
		LabResult lr = new LabResult(patient, dateTime, labItem, data.getValue(), data.getComment()); // $NON-NLS-1$
		lr.set("Quelle", LABOR_NAME); //$NON-NLS-1$
	}

	/**
	 * Fügt Laborwert zu Patientenlabor hinzu
	 *
	 * @param data
	 */
	public void addLaborItem(final TextData data) {
		LabItem labItem = getLabItem(data.getName(), LabItemTyp.TEXT);
		if (labItem == null) {
			String group = data.getGroup();
			if (group == null || group.length() == 0) {
				group = LABOR_NAME;
			}
			String sequence = data.getSequence();
			if (sequence == null || sequence.length() == 0) {
				sequence = "50";
			}
			labItem = new LabItem(data.getName(), data.getName(), myLab, null, null, StringUtils.EMPTY, LabItemTyp.TEXT,
					group, sequence); // $NON-NLS-2$
		}

		TimeTool dateTime = new TimeTool();
		dateTime.setTime(data.getDate());
		LabResult lr = getLabResult(labItem, data.getName(), dateTime);
		if (lr != null) {
			// Wenn Text noch nicht an diesem Tag für diesen Patient vorhanden, dann wird er
			// zum
			// bestehenden Text hinzugefügt
			if (lr.getComment().indexOf(data.getText()) < 0) {
				lr.set(LabResult.COMMENT, lr.getComment() + "\n-----------\n\n" + data.getText());
			}
		} else {
			lr = new LabResult(patient, dateTime, labItem, "Text", data.getText()); //$NON-NLS-1$
		}
		lr.set("Quelle", LABOR_NAME); //$NON-NLS-1$
	}

	/**
	 * Fügt Dokument zu Patientenlabor hinzu
	 *
	 * @param data
	 */
	public void addDocument(EncapsulatedData data) throws IOException {
		if (this.docManager == null) {
			throw new IOException(MessageFormat.format(Messages.PatientLabor_errorKeineDokumentablage, data.getName(),
					this.patient.getLabel()));
		}

		// Kategorie überprüfen/ erstellen
		String category = MedicsPreferencePage.getDokumentKategorie();
		checkCreateCategory(category);

		String downloadDir = MedicsPreferencePage.getDownloadDir();

		// Tmp Verzeichnis überprüfen
		File tmpDir = new File(downloadDir + File.separator + "tmp"); //$NON-NLS-1$
		if (!tmpDir.exists()) {
			if (!tmpDir.mkdirs()) {
				throw new IOException(
						MessageFormat.format(Messages.PatientLabor_errorCreatingTmpDir, tmpDir.getName()));
			}
		}
		String filename = data.getName();
		File tmpPdfFile = new File(downloadDir + File.separator + "tmp" + File.separator + filename); //$NON-NLS-1$
		tmpPdfFile.deleteOnExit();
		FileTool.writeFile(tmpPdfFile, data.getData());

		TimeTool dateTime = new TimeTool();
		dateTime.setTime(data.getDate());
		String dateTimeStr = dateTime.toString(TimeTool.DATE_GER);

		try {
			// Zu Dokumentablage hinzufügen
			addDocument(filename, category, dateTimeStr, tmpPdfFile);

			// Labor Item erstellen
			String kuerzel = "doc"; //$NON-NLS-1$
			LabItem labItem = getLabItem(kuerzel, LabItemTyp.DOCUMENT);
			if (labItem == null) {
				String group = data.getGroup();
				if (group == null || group.length() == 0) {
					group = LABOR_NAME;
				}
				String sequence = data.getSequence();
				if (sequence == null || sequence.length() == 0) {
					sequence = "50";
				}
				labItem = new LabItem(kuerzel, Messages.PatientLabor_nameDokumentLaborParameter, myLab,
						StringUtils.EMPTY, StringUtils.EMPTY, // $NON-NLS-1$
						// //$NON-NLS-3$
						FileTool.getExtension(filename), LabItemTyp.DOCUMENT, group, sequence); // $NON-NLS-1$
																								// //$NON-NLS-2$
			}

			LabResult lr = new LabResult(patient, dateTime, labItem, filename, data.getComment()); // $NON-NLS-1$
			lr.set("Quelle", LABOR_NAME); //$NON-NLS-1$
		} catch (ElexisException e) {
			throw new IOException(MessageFormat.format(Messages.PatientLabor_errorAddingDocument, tmpPdfFile.getName()),
					e);
		}
	}
}
