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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class PatientData {

	private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy"); //$NON-NLS-1$

	private IPatient patient;

	private boolean useLegalGuardian;

	private IContact legalGuardian;

	public PatientData(boolean useLegalGuardian) {
		this.useLegalGuardian = useLegalGuardian;
	}

	public void load(IPatient _patient) throws NullPointerException {
		if (_patient != null) {
			patient = _patient;
		} else {
			patient = ContextServiceHolder.get().getActivePatient().orElse(null);
			if (patient == null) {
				throw new NullPointerException("No patient selected"); //$NON-NLS-1$
			}
			if (useLegalGuardian) {
				initLegalGuardian();
			}
		}
	}

	public void loadFromAgenda() throws NullPointerException {
		Optional<IAppointment> iAppointment = ContextServiceHolder.get().getTyped(IAppointment.class);
		if (!iAppointment.isPresent()) {
			throw new NullPointerException("No appointment selected"); //$NON-NLS-1$
		}

		IContact contact = iAppointment.get().getContact();
		if (contact != null && contact.isPatient()) {
			patient = CoreModelServiceHolder.get().load(contact.getId(), IPatient.class).get();
		}

		if (useLegalGuardian) {
			initLegalGuardian();
		}
	}

	private void initLegalGuardian() {
		if (patient != null && !patient.isDeleted()) {
			legalGuardian = patient.getLegalGuardian();
		}
	}

	public String getFirstName() {
		if (legalGuardian != null && legalGuardian.isPerson()) {
			return legalGuardian.getDescription2();
		} else {
			return patient.getFirstName();
		}
	}

	public String getLastName() {
		if (legalGuardian != null && legalGuardian.isPerson()) {
			return legalGuardian.getDescription1();
		} else {
			return patient.getLastName();
		}
	}

	public String getBirthdate() {
		LocalDateTime date;
		if (legalGuardian != null && legalGuardian.isPerson()) {
			date = legalGuardian.asIPerson().getDateOfBirth();
		} else {
			date = patient.getDateOfBirth();
		}
		if (date != null) {
			return date.toLocalDate().format(dateFormat);
		}
		return "";
	}

	public String getSex() {
		if (legalGuardian != null && legalGuardian.isPerson()) {
			return legalGuardian.asIPerson().getGender().getLocaleText();
		} else {
			return patient.asIPerson().getGender().getLocaleText();
		}
	}

	public String getPid() {
		return StringUtils.defaultString(patient.getCode());
	}

	public String getSalutation() {
		String salutation;
		if (legalGuardian != null && legalGuardian.isPerson()) {
			Gender gender = legalGuardian.asIPerson().getGender();
			if (Gender.MALE == gender) {
				salutation = Messages.Contact_SalutationM;
			} else {
				salutation = Messages.Contact_SalutationF;
			}
		} else {
			if (patient.getGender().equals(Gender.MALE)) {
				salutation = Messages.Contact_SalutationM;
			} else {
				salutation = Messages.Contact_SalutationF;
			}
		}
		return salutation;
	}

	public String getTitle() {
		return patient.getTitel();
	}

	public String getPostalCode() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getZip());
		} else {
			return StringUtils.defaultString(patient.getZip());
		}
	}

	public String getCity() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getCity());
		} else {
			return StringUtils.defaultString(patient.getCity());
		}
	}

	public String getCountry() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getCountry().name());
		} else {
			return StringUtils.defaultString(patient.getCountry().name());
		}
	}

	public String getStreet() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getStreet());
		} else {
			return StringUtils.defaultString(patient.getStreet());
		}
	}

	public String getPhone1() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getPhone1());
		} else {
			return StringUtils.defaultString(patient.getPhone1());
		}
	}

	public String getPhone2() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getPhone2());
		} else {
			return StringUtils.defaultString(patient.getPhone2());
		}
	}

	public String getMobilePhone() {
		if (legalGuardian != null) {
			return StringUtils.defaultString(legalGuardian.getMobile());
		} else {
			return StringUtils.defaultString(patient.getMobile());
		}
	}

	public String getCompleteAddress() {
		if (legalGuardian != null) {
			return legalGuardian.getPostalAddress();
		} else {
			return patient.getPostalAddress();
		}
	}

	public String getOrderNumber() {
		return getAuftragsnummer(patient.getCode());
	}

	public String getEmail() {
		if (legalGuardian != null) {
			return legalGuardian.getEmail();
		} else {
			return patient.getEmail();
		}
	}

	/**
	 * extracted from ch.elexis.data.Patient#getAuftragsnummer
	 *
	 * @return
	 */
	private String getAuftragsnummer(String patCode) {
		String pid = StringTool.addModulo10(patCode) + "-" //$NON-NLS-1$
				+ new TimeTool().toString(TimeTool.TIME_COMPACT);
		return pid;
	}

	public IPatient getPatient() {
		return patient;
	}
}