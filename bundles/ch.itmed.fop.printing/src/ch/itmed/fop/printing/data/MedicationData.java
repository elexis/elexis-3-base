/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.data;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.jface.preference.IPreferenceStore;

import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Prescription;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.SettingsProvider;
import ch.itmed.fop.printing.resources.Messages;

public final class MedicationData {
	private IPrescription prescription;

	public void load() throws NullPointerException {
		prescription = ContextServiceHolder.get().getTyped(IPrescription.class).orElse(null);
		if (prescription == null) {
			SWTHelper.showInfo("Keine Medikation ausgewählt", "Bitte wählen Sie vor dem Drucken eine Medikation.");
			throw new NullPointerException("No prescription selected");
		}
	}

	public String getArticleName() {
		return prescription.getArticle().getName();
	}

	public String getArticlePrice() {
		return prescription.getArticle().getSellingPrice().toString();
	}

	public String getDeliveryDate() {
		LocalDate localDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault());
		String currentDate = formatter.format(localDate);
		return currentDate;
	}

	public String getDose() {
		return prescription.getDosageInstruction();
	}

	public String[] getDoseArray() {
		return Prescription.getSignatureAsStringArray(getDose());
	}

	public String getDosageInstructions() {
		return prescription.getRemark();
	}

	public String getPrescriptionDate() {
		return prescription.getDateFrom() != null
				? DateTimeFormatter.ofPattern("dd.MM.yyyy").format(prescription.getDateFrom())
				: "";
	}

	public String getPrescriptionAuthor() {
		return prescription.getPrescriptor() != null ? prescription.getPrescriptor().getLabel()
				: "";
	}

	public String getResponsiblePharmacist() {
		String docName = PreferenceConstants.MEDICATION_LABEL;
		IPreferenceStore settingsStore = SettingsProvider.getStore(docName);
		return settingsStore.getString(PreferenceConstants.getDocPreferenceConstant(docName, 13));
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
