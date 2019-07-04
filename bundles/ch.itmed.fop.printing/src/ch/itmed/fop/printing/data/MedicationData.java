package ch.itmed.fop.printing.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Anwender;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.resources.Messages;
import ch.rgw.io.Settings;
import ch.rgw.tools.TimeTool;

public final class MedicationData {
	private Prescription prescription;

	public void load() throws NullPointerException {
		prescription = (Prescription) ElexisEventDispatcher.getSelected(Prescription.class);
		if (prescription == null) {
			SWTHelper.showInfo("Keine Medikation ausgewählt", "Bitte wählen Sie vor dem Drucken eine Medikation.");
			throw new NullPointerException("No prescription selected");
		}
	}

	public String getArticleName() {
		return prescription.getArtikel().getName();
	}

	public String getArticlePrice() {
		return prescription.getArtikel().getVKPreis().toString();
	}

	public String getDeliveryDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(localDate);
		return currentDate;
	}

	public String getDose() {
		return prescription.getDosis();
	}

	public String[] getDoseArray() {
		return Prescription.getSignatureAsStringArray(getDose());
	}

	public String getDosageInstructions() {
		return prescription.getBemerkung();
	}

	public String getPrescriptionDate() {
		String date = PersistentObject.checkNull(prescription.get(Prescription.FLD_DATE_FROM));
		if (!date.isEmpty()) {
			TimeTool timetool = new TimeTool(date);
			return timetool.toString(TimeTool.DATE_GER);
		}
		return "";
	}

	public String getPrescriptionAuthor() {
		String authorId = PersistentObject.checkNull(prescription.get(Prescription.FLD_PRESCRIPTOR));
		Anwender author = Anwender.load(authorId);
		return author.getLabel(true);
	}

	public String getResponsiblePharmacist() {
		String docName = PreferenceConstants.MEDICATION_LABEL;
		Settings settingsStore = SettingsProvider.getStore(docName);
		return settingsStore.get(PreferenceConstants.getDocPreferenceConstant(docName, 13), "");
	}

	public String getMedicationType() {
		EntryType entryType = prescription.getEntryType();
		switch (entryType.numericValue()) {
		case 0:
			return Messages.Medication_FixedMedication; // FIXED_MEDICATION
		case 1:
			return Messages.Medication_ReserveMedication; // RESERVE_MEDICATION
		case 2:
			return Messages.Medication_Recipe; // RECIPE
		case 3:
			return Messages.Medication_SelfDispensed; // SELF_DISPENSED
		case 5:
			return Messages.Medication_SymptomaticMedication; // SYMPTOMATIC_MEDICATION
		}
		return "";
	}
}
