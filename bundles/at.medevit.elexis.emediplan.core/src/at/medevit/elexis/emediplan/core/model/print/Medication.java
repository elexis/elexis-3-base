/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core.model.print;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.EntryType;

@XmlRootElement(name = "medication")
@XmlAccessorType(XmlAccessType.FIELD)
public class Medication {

	@XmlElement(name = "date")
	String date;
	@XmlElement(name = "patient")
	ContactInfo patientInfo;
	@XmlElement(name = "mandant")
	ContactInfo mandantInfo;

	@XmlElementWrapper(name = "fix")
	@XmlElement(name = "medicament")
	List<Medicament> fixMedication;

	@XmlElementWrapper(name = "reserve")
	@XmlElement(name = "medicament")
	List<Medicament> reserveMedication;

	@XmlElementWrapper(name = "symptomatic")
	@XmlElement(name = "medicament")
	List<Medicament> symptomaticMedication;

	public static Medication fromPrescriptions(@NonNull IMandator author, @NonNull IPatient patient,
			@NonNull List<IPrescription> prescriptions) {
		Medication ret = new Medication();
		ret.date = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(LocalDateTime.now()); //$NON-NLS-1$

		ret.patientInfo = ContactInfo.fromPatient(patient);
		ret.mandantInfo = ContactInfo.fromKontakt(author);

		for (IPrescription prescription : prescriptions) {
			EntryType type = prescription.getEntryType();
			if (type == EntryType.FIXED_MEDICATION || type == EntryType.RECIPE) {
				if (ret.fixMedication == null) {
					ret.fixMedication = new ArrayList<>();
				}
				ret.fixMedication.add(Medicament.fromPrescription(prescription));
			} else if (type == EntryType.RESERVE_MEDICATION) {
				if (ret.reserveMedication == null) {
					ret.reserveMedication = new ArrayList<>();
				}
				ret.reserveMedication.add(Medicament.fromPrescription(prescription));
			} else if (type == EntryType.SYMPTOMATIC_MEDICATION) {
				if (ret.symptomaticMedication == null) {
					ret.symptomaticMedication = new ArrayList<>();
				}
				ret.symptomaticMedication.add(Medicament.fromPrescription(prescription));
			}
		}

		return ret;
	}
}
