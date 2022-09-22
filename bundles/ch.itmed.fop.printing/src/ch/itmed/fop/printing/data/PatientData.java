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

import static ch.elexis.core.model.PatientConstants.FLD_EXTINFO_LEGAL_GUARDIAN;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Messages;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Person;

public class PatientData {
	private Patient patient;

	private boolean useLegalGuardian;

	private Kontakt legalGuardian;

	public PatientData(boolean useLegalGuardian) {
		this.useLegalGuardian = useLegalGuardian;
	}

	public void load() throws NullPointerException {
		patient = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		if (patient == null) {
			SWTHelper.showInfo(ch.itmed.fop.printing.resources.Messages.Info_NoPatient_Title,
					ch.itmed.fop.printing.resources.Messages.Info_NoPatient_Message);
			throw new NullPointerException("No patient selected"); //$NON-NLS-1$
		}
		if (useLegalGuardian) {
			initLegalGuardian();
		}
	}

	public void loadFromAgenda() throws NullPointerException {
		Termin t = (Termin) ElexisEventDispatcher.getSelected(Termin.class);
		if (t == null) {
			Optional<IAppointment> iAppointment = ContextServiceHolder.get().getTyped(IAppointment.class);
			if (iAppointment.isPresent()) {
				PersistentObject po = NoPoUtil.loadAsPersistentObject(iAppointment.get());
				if (po instanceof Termin) {
					t = (Termin) po;
				}
			}
			if (t == null) {
				throw new NullPointerException("No appointment selected"); //$NON-NLS-1$
			}
		}

		String pid = t.getKontakt().getId();
		patient = Patient.load(pid);

		if (useLegalGuardian) {
			initLegalGuardian();
		}
	}

	private void initLegalGuardian() {
		if (patient != null && patient.exists() && hasLegalGuardian()) {
			legalGuardian = getLegalGuardian();
		}
	}

	private boolean hasLegalGuardian() {
		if (patient.istPerson()) {
			String guardianId = (String) patient.getExtInfoStoredObjectByKey(FLD_EXTINFO_LEGAL_GUARDIAN);
			return StringUtils.isNotBlank(guardianId);
		}
		return false;
	}

	private Kontakt getLegalGuardian() {
		String guardianId = (String) patient.getExtInfoStoredObjectByKey(FLD_EXTINFO_LEGAL_GUARDIAN);
		if (StringUtils.isNotBlank(guardianId)) {
			Kontakt guardian = Kontakt.load((String) guardianId);
			if (guardian.exists()) {
				return guardian;
			}
		}
		return null;
	}

	public String getFirstName() {
		if (legalGuardian.istPerson()) {
			return legalGuardian.get(Person.FIRSTNAME);
		} else {
			return patient.getVorname();
		}
	}

	public String getLastName() {
		if (legalGuardian.istPerson()) {
			return legalGuardian.get(Person.NAME);
		} else {
			return patient.getName();
		}
	}

	public String getBirthdate() {
		if (legalGuardian.istPerson()) {
			return legalGuardian.get(Person.BIRTHDATE);
		} else {
			return patient.getGeburtsdatum();
		}
	}

	public String getSex() {
		if (legalGuardian.istPerson()) {
			return legalGuardian.get(Person.SEX);
		} else {
			return patient.getGeschlecht();
		}
	}

	public String getPid() {
		return PersistentObject.checkNull(patient.get(Patient.FLD_PATID));
	}

	public String getSalutation() {
		String salutation;
		if (legalGuardian.istPerson()) {
			if (legalGuardian.get(Person.SEX).equals(Person.MALE)) {
				salutation = Messages.Contact_SalutationM;
			} else {
				salutation = Messages.Contact_SalutationF;
			}
		} else {
			if (patient.getGeschlecht().equals(Person.MALE)) {
				salutation = Messages.Contact_SalutationM;
			} else {
				salutation = Messages.Contact_SalutationF;
			}
		}
		return salutation;
	}

	public String getTitle() {
		return PersistentObject.checkNull(patient.get("Titel")); //$NON-NLS-1$
	}

	public String getPostalCode() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get(Patient.FLD_ZIP));
		} else {
			return PersistentObject.checkNull(patient.get(Patient.FLD_ZIP));
		}
	}

	public String getCity() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get(Patient.FLD_PLACE));
		} else {
			return PersistentObject.checkNull(patient.get(Patient.FLD_PLACE));
		}
	}

	public String getCountry() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get("Land")); //$NON-NLS-1$
		} else {
			return PersistentObject.checkNull(patient.get("Land")); //$NON-NLS-1$
		}
	}

	public String getStreet() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get(Patient.FLD_STREET));
		} else {
			return PersistentObject.checkNull(patient.get(Patient.FLD_STREET));
		}
	}

	public String getPhone1() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get("Telefon1")); //$NON-NLS-1$
		} else {
			return PersistentObject.checkNull(patient.get("Telefon1")); //$NON-NLS-1$
		}
	}

	public String getPhone2() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get("Telefon2")); //$NON-NLS-1$
		} else {
			return PersistentObject.checkNull(patient.get("Telefon2")); //$NON-NLS-1$
		}
	}

	public String getMobilePhone() {
		if (legalGuardian != null) {
			return PersistentObject.checkNull(legalGuardian.get("NatelNr")); //$NON-NLS-1$
		} else {
			return PersistentObject.checkNull(patient.get("NatelNr")); //$NON-NLS-1$
		}
	}

	public String getCompleteAddress() {
		if (legalGuardian != null) {
			return legalGuardian.getPostAnschrift(true);
		} else {
			return patient.getPostAnschrift(true);
		}
	}

	public String getOrderNumber() {
		return patient.getAuftragsnummer();
	}

	public String getEmail() {
		if (legalGuardian != null) {
			return legalGuardian.getMailAddress();
		} else {
			return patient.getMailAddress();
		}
	}
}