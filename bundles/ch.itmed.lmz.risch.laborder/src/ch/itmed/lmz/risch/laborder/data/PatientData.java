/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.data;

import java.util.ArrayList;
import java.util.Arrays;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.itmed.lmz.risch.laborder.ui.CaseWindow;
import ch.itmed.lmz.risch.laborder.ui.MessageBoxUtil;

public final class PatientData {
	private Patient patient;
	private Fall fall;

	/**
	 * 
	 * @param loadCase if true, the user can select the open case for the patient
	 */
	public PatientData(boolean loadCase) throws UnsupportedOperationException {
		patient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);

		if (loadCase) {
			getCase();
		}
	}

	private void getCase() throws UnsupportedOperationException {
		ArrayList<Fall> cases = new ArrayList<>(Arrays.asList(patient.getFaelle()));
		cases.removeIf(fall -> fall.isOpen() != true);
		if (cases.size() > 1) {
			// If multiple open cases are available, the user needs to select
			// the corresponding case
			int caseIndex = new CaseWindow().open(cases);

			if (caseIndex > 0) {
				fall = cases.get(caseIndex);
			} else {
				MessageBoxUtil.showErrorDialog("Kein Fall ausgewählt", "Es wurde kein gültiger Fall ausgewählt");
				throw new UnsupportedOperationException("No open case for patient found");
			}
		} else if (cases.size() == 1) {
			fall = cases.get(0);
		} else {
			MessageBoxUtil.showErrorDialog("Kein offener Fall vorhanden",
					"Für den aktuellen Patienten gibt es keinen offenen Fall");
			throw new UnsupportedOperationException("No open case for patient found");
		}
	}

	public String getNumber() {
		return patient.get("PatientNr");
	}

	public String getLastName() {
		return patient.getName();
	}

	public String getFirstName() {
		return patient.getVorname();
	}

	public String getBirthDate() {
		return patient.getGeburtsdatum().replace(".", "");
	}

	public String getStreet() {
		return patient.get("Strasse");
	}

	public String getSex() {
		if (patient.getGeschlecht().equals("m")) {
			return "1";
		} else {
			// female
			return "2";
		}
	}

	public String getZip() {
		return patient.get("Plz");
	}

	public String getCity() {
		return patient.get("Ort");
	}

	public String getCountry() {
		return patient.get("Land");
	}

	public String getCostObjectName() {
		if (fall != null) {
			Kontakt kontakt = fall.getRequiredContact("Kostenträger");
			if (kontakt != null) {
				return kontakt.get("Bezeichnung1");
			}
		}
		return "";
	}

	public String getInsurancePolicyNumber() {
		if (fall != null) {
			return fall.getRequiredString("Versicherungsnummer").isEmpty() ? fall.getRequiredString("Unfallnummer")
					: fall.getRequiredString("Versicherungsnummer");
		} else {
			return "";
		}
	}

	public String getInsuranceType() {
		if (fall != null) {
			String insuranceType = fall.getAbrechnungsSystem();

			if (insuranceType.equals("KVG")) {
				return "0";
			} else if (insuranceType.equals("VVG")) {
				return "1";
			} else if (insuranceType.equals("UVG")) {
				return "2";
			} else if (insuranceType.equals("IV")) {
				return "3";
			} else if (insuranceType.equals("MV")) {
				return "4";
			} else {
				return "";
			}
		} else {
			return "";
		}
	}

}
