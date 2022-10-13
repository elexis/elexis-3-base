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

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.itmed.fop.printing.preferences.PreferenceConstants;
import ch.itmed.fop.printing.preferences.Setting;
import ch.itmed.fop.printing.resources.Messages;

public final class MedicationData {
	private IPrescription prescription;

	public MedicationData(IPrescription prescription) {
		this.prescription = prescription;
	}

	public void load() throws NullPointerException {
		prescription = ContextServiceHolder.get().getTyped(IPrescription.class).orElse(null);
		if (prescription == null) {
			throw new NullPointerException("No prescription selected"); //$NON-NLS-1$
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
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.YYYY").withZone(ZoneId.systemDefault()); //$NON-NLS-1$
		String currentDate = formatter.format(localDate);
		return currentDate;
	}

	public String getDose() {
		return prescription.getDosageInstruction();
	}

	public String[] getDoseArray() {
		return MedicationServiceHolder.get().getSignatureAsStringArray(prescription.getDosageInstruction());
	}

	public String getDosageInstructions() {
		return prescription.getRemark();
	}

	public String getPrescriptionDate() {
		return prescription.getDateFrom() != null
				? DateTimeFormatter.ofPattern("dd.MM.yyyy").format(prescription.getDateFrom()) //$NON-NLS-1$
				: StringUtils.EMPTY;
	}

	public String getPrescriptionAuthor() {
		return prescription.getPrescriptor() != null ? prescription.getPrescriptor().getLabel() : StringUtils.EMPTY;
	}

	public String getResponsiblePharmacist() {
		String docName = PreferenceConstants.MEDICATION_LABEL;
		return Setting.getString(docName, PreferenceConstants.getDocPreferenceConstant(docName, 13));
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
		return StringUtils.EMPTY;
	}

	public boolean isFreetext() {
		String[] signature = MedicationServiceHolder.get()
				.getSignatureAsStringArray(prescription.getDosageInstruction());
		return !signature[0].isEmpty() && signature[1].isEmpty() && signature[2].isEmpty() && signature[3].isEmpty();
	}
}
