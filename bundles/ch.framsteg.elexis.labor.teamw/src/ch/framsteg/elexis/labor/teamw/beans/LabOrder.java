/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.labor.teamw.beans;

import java.util.Base64;
import java.util.Properties;

public class LabOrder {

	private Properties teamwProperties;

	private final static String SENTENCE_ID = "props.teamw.gdt.code.sentence_id";
	private final static String SOFTWARE = "props.teamw.gdt.code.software";
	private final static String PATIENT_ID = "props.teamw.gdt.code.patient.number";
	private final static String PATIENT_NAME = "props.teamw.gdt.code.patient.name";
	private final static String PATIENT_PRENAME = "props.teamw.gdt.code.patient.firstname";
	private final static String PATIENT_BIRTHDATE = "props.teamw.gdt.code.patient.birthdate";
	private final static String PATIENT_TITLE = "props.teamw.gdt.code.patient.title";
	private final static String PATIENT_SOCIALNUMBER = "props.teamw.gdt.code.patient.socialnumber";
	private final static String PATIENT_RESIDENCE = "props.teamw.gdt.code.patient.residence";
	private final static String PATIENT_STREET = "props.teamw.gdt.code.patient.street";
	private final static String PATIENT_SEX = "props.teamw.gdt.code.patient.sex";
	private final static String PATIENT_ZIP = "props.teamw.gdt.code.patient.zip";
	private final static String PATIENT_CITY = "props.teamw.gdt.code.patient.city";
	private final static String PATIENT_COUNTRY = "props.teamw.gdt.code.patient.country";
	private final static String PATIENT_CARDNUMBER = "props.teamw.gdt.code.patient.cardnumber";
	private final static String GUARANTOR_TYPE = "props.teamw.gdt.code.guarantor.type";
	private final static String CONTACT_TYPE = "props.teamw.gdt.code.contact.type";
	private final static String INSURANCE_NAME = "props.teamw.gdt.code.insurance.name";
	private final static String INSURANCE_EAN = "props.teamw.gdt.code.insurance.ean";
	private final static String INSURANCE_TYPE = "props.teamw.gdt.code.insurance.type";
	private final static String INSURANCE_NUMBER = "props.teamw.gdt.code.insurance.number";
	private final static String PATIENT_PHONE_NUMBER = "props.teamw.gdt.code.patient.private.phone.number";
	private final static String PATIENT_MOBILE_NUMBER = "props.teamw.gdt.code.patient.mobile.number";
	private final static String PATIENT_EMAIL = "props.teamw.gdt.code.patient.email";
	private final static String OFFICE_PHONE_NUMBER = "props.teamw.gdt.code.patient.office.phone.number";
	private final static String REPRESENTATIVE_TITLE = "props.teamw.gdt.code.representative.title";
	private final static String REPRESENTATIVE_PRENAME = "props.teamw.gdt.code.representative.firstname";
	private final static String REPRESENTATIVE_NAME = "props.teamw.gdt.code.representative.name";
	private final static String REPRESENTATIVE_STREET = "props.teamw.gdt.code.representative.street";
	private final static String REPRESENTATIVE_ZIP = "props.teamw.gdt.code.representative.zip";
	private final static String REPRESENTATIVE_CITY = "props.teamw.gdt.code.representative.city";
	private final static String REPRESENTATIVE_COUNTRY = "props.teamw.gdt.code.representative.country";
	private final static String REFFERING_DOCTOR_ID = "props.teamw.gdt.code.referring.doctor.id";
	private final static String ANIMAL_KIND = "props.teamw.gdt.code.animal.kind";
	private final static String ANIMAL_RACE = "props.teamw.gdt.code.animal.race";
	private final static String ANIMAL_CHIP_NUMBER = "props.teamw.gdt.code.animal.chip.number";
	private final static String PATIENT_VISIT_NUMBER = "props.teamw.gdt.code.patient.visit.number";
	private final static String ASSIGNING_FACILITY_NAMESPACE = "props.teamw.gdt.code.assigning.facility.namespace";
	private final static String ASSIGNING_JURISDICTION_IDENTIFIER = "props.teamw.gdt.code.assigning.jurisdiction.identifier";
	private final static String FINANCIAL_CLASS = "props.teamw.gdt.code.financial.class";
	private final static String PLACER_ORDER_NUMBER = "props.teamw.gdt.code.placer.order.number";
	private final static String NEWLINE = "props.teamw.gdt.newline";
	private final static String GDT_CODE_LENGTH = "props.teamw.gdt.code.length";
	private final static String GDT_MALE_HR = "props.teamw.gdt.male.hr";
	private final static String GDT_MALE_TITLE = "props.teamw.gdt.male.title";
	private final static String GDT_MALE_ABB = "props.teamw.gdt.male.abb";
	private final static String GDT_MALE_CODE = "props.teamw.gdt.male.code";
	private final static String GDT_FEMALE_CODE = "props.teamw.gdt.female.code";
	private final static String GDT_FEMALE_TITLE = "props.teamw.gdt.female.title";
	private final static String EMPTY = "";
	private final static String DOT_REGEX="\\.";
	
	private String sentenceId = "";
	private String software = "";
	private String patientNumberLabel = "";
	private String patientName = "";
	private String patientPrename = "";
	private String patientBirthday = "";
	private String patientTitle = "";
	private String patientAhv = "";
	private String patientResidence = "";
	private String patientStreet = "";
	private String patientSex = "";
	private String patientZip = "";
	private String patientCity = "";
	private String patientCountry = "";
	private String patientCardNumber = "";
	private String guarantorType = "";
	private String contactType = "";
	private String insuranceName = "";
	private String insuranceEan = "";
	private String insuranceType = "";
	private String patientInsuranceNumber = "";
	private String patientPrivatePhone = "";
	private String patientPrivateMobile = "";
	private String patientEmail = "";
	private String patientOfficePhone = "";
	private String representativeTitle = "";
	private String representativePrename = "";
	private String representativeName = "";
	private String representativeStreet = "";
	private String representativeZip = "";
	private String representativeCity = "";
	private String representativeCountry = "";
	private String referringDoctor = "";
	private String animalKind = "";
	private String animalRace = "";
	private String animalChipNumber = "";
	private String patientVisitNumber = "";
	private String assiginingFacilityNamespace = "";
	private String assigningJurisdictionIdentifier = "";
	private String financialClass = "";
	private String placerOrderNumber = "";

	public LabOrder(Properties gdtProperties) {
		setTeamwProperties(gdtProperties);
	}

	public String getGdtInstance() {
		String sentenceIdGdt = getTeamwProperties().getProperty(SENTENCE_ID);
		String softwareGdt = getTeamwProperties().getProperty(SOFTWARE);
		String patientNumberLabelGdt = getTeamwProperties().getProperty(PATIENT_ID);
		String patientNameGdt = getTeamwProperties().getProperty(PATIENT_NAME);
		String patientPrenameGdt = getTeamwProperties().getProperty(PATIENT_PRENAME);
		String patientBirthdayGdt = getTeamwProperties().getProperty(PATIENT_BIRTHDATE);
		String patientTitleGdf = getTeamwProperties().getProperty(PATIENT_TITLE);
		String patientAhvGdt = getTeamwProperties().getProperty(PATIENT_SOCIALNUMBER);
		String patientResidenceGdt = getTeamwProperties().getProperty(PATIENT_RESIDENCE);
		String patientStreetGdt = getTeamwProperties().getProperty(PATIENT_STREET);
		String patientSexGdt = getTeamwProperties().getProperty(PATIENT_SEX);
		String patientZipGdt = getTeamwProperties().getProperty(PATIENT_ZIP);
		String patientCityGdt = getTeamwProperties().getProperty(PATIENT_CITY);
		String patientCountryGdt = getTeamwProperties().getProperty(PATIENT_COUNTRY);
		String patientCardNumberGdt = getTeamwProperties().getProperty(PATIENT_CARDNUMBER);
		String guarantorTypeGdt = getTeamwProperties().getProperty(GUARANTOR_TYPE);
		String contactTypeGdt = getTeamwProperties().getProperty(CONTACT_TYPE);
		String insuranceNameGdt = getTeamwProperties().getProperty(INSURANCE_NAME);
		String insuranceEanGdt = getTeamwProperties().getProperty(INSURANCE_EAN);
		String insuranceTypeGdt = getTeamwProperties().getProperty(INSURANCE_TYPE);
		String patientInsuranceNumberGdt = getTeamwProperties().getProperty(INSURANCE_NUMBER);
		String patientPrivatePhoneGdt = getTeamwProperties().getProperty(PATIENT_PHONE_NUMBER);
		String patientPrivateMobileGdt = getTeamwProperties().getProperty(PATIENT_MOBILE_NUMBER);
		String patientEmailGdt = getTeamwProperties().getProperty(PATIENT_EMAIL);
		String patientOfficePhoneGdt = getTeamwProperties().getProperty(OFFICE_PHONE_NUMBER);
		String representativeTitleGdt = getTeamwProperties().getProperty(REPRESENTATIVE_TITLE);
		String representativePrenameGdt = getTeamwProperties().getProperty(REPRESENTATIVE_PRENAME);
		String representativeNameGdt = getTeamwProperties().getProperty(REPRESENTATIVE_NAME);
		String representativeStreetGdt = getTeamwProperties().getProperty(REPRESENTATIVE_STREET);
		String representativeZipGdt = getTeamwProperties().getProperty(REPRESENTATIVE_ZIP);
		String representativeCityGdt = getTeamwProperties().getProperty(REPRESENTATIVE_CITY);
		String representativeCountryGdt = getTeamwProperties().getProperty(REPRESENTATIVE_COUNTRY);
		String referringDoctorGdt = getTeamwProperties().getProperty(REFFERING_DOCTOR_ID);
		String animalKindGdt = getTeamwProperties().getProperty(ANIMAL_KIND);
		String animalRaceGdt = getTeamwProperties().getProperty(ANIMAL_RACE);
		String animalChuipNumberGdt = getTeamwProperties().getProperty(ANIMAL_CHIP_NUMBER);
		String patientVisitNumberGdt = getTeamwProperties().getProperty(PATIENT_VISIT_NUMBER);
		String assiginingFacilityNamespaceGdt = getTeamwProperties().getProperty(ASSIGNING_FACILITY_NAMESPACE);
		String assigningJurisdictionIdentifierGdt = getTeamwProperties().getProperty(ASSIGNING_JURISDICTION_IDENTIFIER);
		String financialClassGdt = getTeamwProperties().getProperty(FINANCIAL_CLASS);
		String placerOrderNumberGdt = getTeamwProperties().getProperty(PLACER_ORDER_NUMBER);
		String cr = getTeamwProperties().getProperty(NEWLINE);
		String codeLentgh = getTeamwProperties().getProperty(GDT_CODE_LENGTH);
		String maleHr = getTeamwProperties().getProperty(GDT_MALE_HR);
		String maleTitle = getTeamwProperties().getProperty(GDT_MALE_TITLE);
		String maleAbb = getTeamwProperties().getProperty(GDT_MALE_ABB);
		String maleCode = getTeamwProperties().getProperty(GDT_MALE_CODE);
		String femaleCode = getTeamwProperties().getProperty(GDT_FEMALE_CODE);
		String femaleTitle = getTeamwProperties().getProperty(GDT_FEMALE_TITLE);

		StringBuilder message = new StringBuilder();

		if (!getSentenceId().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + sentenceIdGdt.length() + getSentenceId().length() + 2)
					+ sentenceIdGdt + getSentenceId() + cr);
		}
		if (!getSoftware().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + softwareGdt.length() + getSoftware().length() + 2)
					+ softwareGdt + getSoftware() + cr);
		}
		if (!getPatientNumberLabel().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + patientNumberLabelGdt.length() + getPatientNumberLabel().length() + 2)
							+ patientNumberLabelGdt + getPatientNumberLabel() + cr);
		}
		if (!getPatientName().isEmpty()) {

			message.append(String.format(codeLentgh, 3 + patientNameGdt.length() + getPatientName().length() + 2)
					+ patientNameGdt + getPatientName() + cr);
		}
		if (!getPatientPrename().isEmpty()) {

			message.append(String.format(codeLentgh, 3 + patientPrenameGdt.length() + getPatientPrename().length() + 2)
					+ patientPrenameGdt + getPatientPrename() + cr);
		}
		if (!getPatientBirthday().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + patientBirthdayGdt.length() + formatBirthdate(getPatientBirthday()).length() + 2)
					+ patientBirthdayGdt + formatBirthdate(getPatientBirthday()) + cr);
		}
		if (!getPatientTitle().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + patientTitleGdf.length()
							+ formatTitle(getPatientTitle(), maleHr, maleTitle, femaleTitle).length() + 2)
					+ patientTitleGdf + formatTitle(getPatientTitle(), maleHr, maleTitle, femaleTitle) + cr);
		}
		if (!getPatientAhv().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + patientAhvGdt.length() + getPatientAhv().length() + 2)
					+ patientAhvGdt + getPatientAhv() + cr);
		}
		if (!getPatientResidence().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + patientResidenceGdt.length() + getPatientResidence().length() + 2)
							+ patientResidenceGdt + getPatientResidence() + cr);
		}
		if (!getPatientStreet().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + patientStreetGdt.length() + getPatientStreet().length() + 2)
					+ patientStreetGdt + getPatientStreet() + cr);
		}
		if (!getPatientSex().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + patientSexGdt.length() + formatSex(getPatientSex(), maleAbb, maleCode, femaleCode).length() + 2)
					+ patientSexGdt + formatSex(getPatientSex(), maleAbb, maleCode, femaleCode) + cr);
		}
		if (!getPatientZip().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + patientZipGdt.length() + getPatientZip().length() + 2)
					+ patientZipGdt + getPatientZip() + cr);
		}
		if (!getPatientCity().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + patientCityGdt.length() + getPatientCity().length() + 2)
					+ patientCityGdt + getPatientCity() + cr);
		}
		if (!getPatientCountry().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + patientCountryGdt.length() + getPatientCountry().length() + 2)
					+ patientCountryGdt + getPatientCountry() + cr);
		}
		if (!getPatientCardNumber().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + patientCardNumberGdt.length() + getPatientCardNumber().length() + 2)
							+ patientCardNumberGdt + getPatientCardNumber() + cr);
		}
		if (!getGuarantorType().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + guarantorTypeGdt.length() + getGuarantorType().length() + 2)
					+ guarantorTypeGdt + getGuarantorType() + cr);
		}
		if (!getContactType().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + contactTypeGdt.length() + getContactType().length() + 2)
					+ contactTypeGdt + getContactType() + cr);
		}
		if (!getInsuranceName().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + insuranceNameGdt.length() + getInsuranceName().length() + 2)
					+ insuranceNameGdt + getInsuranceName() + cr);
		}
		if (!getInsuranceEan().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + insuranceEanGdt.length() + getInsuranceEan().length() + 2)
					+ insuranceEanGdt + getInsuranceEan() + cr);
		}
		if (!getInsuranceType().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + insuranceTypeGdt.length() + getInsuranceType().length() + 2)
					+ insuranceTypeGdt + getInsuranceType() + cr);
		}
		if (!getPatientInsuranceNumber().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + patientInsuranceNumberGdt.length() + getPatientInsuranceNumber().length() + 2)
					+ patientInsuranceNumberGdt + getPatientInsuranceNumber() + cr);
		}

		if (!getPatientPrivatePhone().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + patientPrivatePhoneGdt.length() + getPatientPrivatePhone().length() + 2)
					+ patientPrivatePhoneGdt + getPatientPrivatePhone() + cr);
		}
		if (!getPatientPrivateMobile().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + patientPrivateMobileGdt.length() + getPatientPrivateMobile().length() + 2)
					+ patientPrivateMobileGdt + getPatientPrivateMobile() + cr);
		}
		if (!getPatientEmail().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + patientEmailGdt.length() + getPatientEmail().length() + 2)
					+ patientEmailGdt + getPatientEmail() + cr);
		}
		if (!getPatientOfficePhone().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + patientOfficePhoneGdt.length() + getPatientOfficePhone().length() + 2)
							+ patientOfficePhoneGdt + getPatientOfficePhone() + cr);
		}
		if (!getRepresentativeTitle().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + representativeTitleGdt.length() + getRepresentativeTitle().length() + 2)
					+ representativeTitleGdt + getRepresentativeTitle() + cr);
		}
		if (!getRepresentativePrename().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + representativePrenameGdt.length() + getRepresentativePrename().length() + 2)
					+ representativePrenameGdt + getRepresentativePrename() + cr);
		}
		if (!getRepresentativeName().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + representativeNameGdt.length() + getRepresentativeName().length() + 2)
							+ representativeNameGdt + getRepresentativeName() + cr);
		}
		if (!getRepresentativeStreet().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + representativeStreetGdt.length() + getRepresentativeStreet().length() + 2)
					+ representativeStreetGdt + getRepresentativeStreet() + cr);
		}
		if (!getRepresentativeZip().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + representativeZipGdt.length() + getRepresentativeZip().length() + 2)
							+ representativeZipGdt + getRepresentativeZip() + cr);
		}
		if (!getRepresentativeCity().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + representativeCityGdt.length() + getRepresentativeCity().length() + 2)
							+ representativeCityGdt + getRepresentativeCity() + cr);
		}
		if (!getRepresentativeCountry().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + representativeCountryGdt.length() + getRepresentativeCountry().length() + 2)
					+ representativeCountryGdt + getRepresentativeCountry() + cr);
		}
		if (!getReferringDoctor().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + referringDoctorGdt.length() + getReferringDoctor().length() + 2)
							+ referringDoctorGdt + getReferringDoctor() + cr);
		}
		if (!getAnimalKind().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + animalKindGdt.length() + getAnimalKind().length() + 2)
					+ animalKindGdt + getAnimalKind() + cr);
		}
		if (!getAnimalRace().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + animalRaceGdt.length() + getAnimalRace().length() + 2)
					+ animalRaceGdt + getAnimalRace() + cr);
		}
		if (!getAnimalChipNumber().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + animalChuipNumberGdt.length() + getAnimalChipNumber().length() + 2)
							+ animalChuipNumberGdt + getAnimalChipNumber() + cr);
		}
		if (!getPatientVisitNumber().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + patientVisitNumberGdt.length() + getPatientVisitNumber().length() + 2)
							+ patientVisitNumberGdt + getPatientVisitNumber() + cr);
		}
		if (!getAssiginingFacilityNamespace().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + assiginingFacilityNamespaceGdt.length() + getAssiginingFacilityNamespace().length() + 2)
					+ assiginingFacilityNamespaceGdt + getAssiginingFacilityNamespace() + cr);
		}
		if (!getAssigningJurisdictionIdentifier().isEmpty()) {
			message.append(String.format(codeLentgh,
					3 + assigningJurisdictionIdentifierGdt.length() + getAssigningJurisdictionIdentifier().length() + 2)
					+ assigningJurisdictionIdentifierGdt + getAssigningJurisdictionIdentifier() + cr);
		}
		if (!getFinancialClass().isEmpty()) {
			message.append(String.format(codeLentgh, 3 + financialClassGdt.length() + getFinancialClass().length() + 2)
					+ financialClassGdt + getFinancialClass() + cr);
		}
		if (!getPlacerOrderNumber().isEmpty()) {
			message.append(
					String.format(codeLentgh, 3 + placerOrderNumberGdt.length() + getPlacerOrderNumber().length() + 2)
							+ placerOrderNumberGdt + getPlacerOrderNumber() + cr);
		}
		return message.toString();
	}

	private String formatBirthdate(String birthdate) {
		String modifiedBirthdate = birthdate.replaceAll(DOT_REGEX, EMPTY);
		return modifiedBirthdate;
	}

	private String formatSex(String sex, String maleAbb, String maleCode, String femaleCode) {
		return sex.equalsIgnoreCase(maleAbb) ? maleCode : femaleCode;
	}

	private String formatTitle(String title, String maleHr, String maleTitle, String femaleTitle) {
		return title.equalsIgnoreCase(maleHr) ? maleTitle : femaleTitle;
	}

	public String getGdtInstanceBase64() {
		return Base64.getEncoder().encodeToString(getGdtInstance().getBytes());
	}

	public Properties getTeamwProperties() {
		return teamwProperties;
	}

	public void setTeamwProperties(Properties teamwProperties) {
		this.teamwProperties = teamwProperties;
	}

	public String getSentenceId() {
		return sentenceId;
	}

	public void setSentenceId(String sentenceId) {
		this.sentenceId = sentenceId;
	}

	public String getSoftware() {
		return software;
	}

	public void setSoftware(String software) {
		this.software = software;
	}

	public String getPatientNumberLabel() {
		return patientNumberLabel;
	}

	public void setPatientNumberLabel(String patientNumberLabel) {
		this.patientNumberLabel = patientNumberLabel;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientPrename() {
		return patientPrename;
	}

	public void setPatientPrename(String patientPrename) {
		this.patientPrename = patientPrename;
	}

	public String getPatientBirthday() {
		return patientBirthday;
	}

	public void setPatientBirthday(String patientBirthday) {
		this.patientBirthday = patientBirthday;
	}

	public String getPatientTitle() {
		return patientTitle;
	}

	public void setPatientTitle(String patientTitle) {
		this.patientTitle = patientTitle;
	}

	public String getPatientAhv() {
		return patientAhv;
	}

	public void setPatientAhv(String patientAhv) {
		this.patientAhv = patientAhv;
	}

	public String getPatientResidence() {
		return patientResidence;
	}

	public void setPatientResidence(String patientResidence) {
		this.patientResidence = patientResidence;
	}

	public String getPatientStreet() {
		return patientStreet;
	}

	public void setPatientStreet(String patientStreet) {
		this.patientStreet = patientStreet;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
	}

	public String getPatientZip() {
		return patientZip;
	}

	public void setPatientZip(String patientZip) {
		this.patientZip = patientZip;
	}

	public String getPatientCity() {
		return patientCity;
	}

	public void setPatientCity(String patientCity) {
		this.patientCity = patientCity;
	}

	public String getPatientCountry() {
		return patientCountry;
	}

	public void setPatientCountry(String patientCountry) {
		this.patientCountry = patientCountry;
	}

	public String getPatientCardNumber() {
		return patientCardNumber;
	}

	public void setPatientCardNumber(String patientCardNumber) {
		this.patientCardNumber = patientCardNumber;
	}

	public String getGuarantorType() {
		return guarantorType;
	}

	public void setGuarantorType(String guarantorType) {
		this.guarantorType = guarantorType;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public String getInsuranceName() {
		return insuranceName;
	}

	public void setInsuranceName(String insuranceName) {
		this.insuranceName = insuranceName;
	}

	public String getInsuranceEan() {
		return insuranceEan;
	}

	public void setInsuranceEan(String insuranceEan) {
		this.insuranceEan = insuranceEan;
	}

	public String getInsuranceType() {
		return insuranceType;
	}

	public void setInsuranceType(String insuranceType) {
		this.insuranceType = insuranceType;
	}

	public String getPatientInsuranceNumber() {
		return patientInsuranceNumber;
	}

	public void setPatientInsuranceNumber(String patientInsuranceNumber) {
		this.patientInsuranceNumber = patientInsuranceNumber;
	}

	public String getPatientPrivatePhone() {
		return patientPrivatePhone;
	}

	public void setPatientPrivatePhone(String patientPrivatePhone) {
		this.patientPrivatePhone = patientPrivatePhone;
	}

	public String getPatientPrivateMobile() {
		return patientPrivateMobile;
	}

	public void setPatientPrivateMobile(String patientPrivateMobile) {
		this.patientPrivateMobile = patientPrivateMobile;
	}

	public String getPatientEmail() {
		return patientEmail;
	}

	public void setPatientEmail(String patientEmail) {
		this.patientEmail = patientEmail;
	}

	public String getPatientOfficePhone() {
		return patientOfficePhone;
	}

	public void setPatientOfficePhone(String patientOfficePhone) {
		this.patientOfficePhone = patientOfficePhone;
	}

	public String getRepresentativeTitle() {
		return representativeTitle;
	}

	public void setRepresentativeTitle(String representativeTitle) {
		this.representativeTitle = representativeTitle;
	}

	public String getRepresentativePrename() {
		return representativePrename;
	}

	public void setRepresentativePrename(String representativePrename) {
		this.representativePrename = representativePrename;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public String getRepresentativeStreet() {
		return representativeStreet;
	}

	public void setRepresentativeStreet(String representativeStreet) {
		this.representativeStreet = representativeStreet;
	}

	public String getRepresentativeZip() {
		return representativeZip;
	}

	public void setRepresentativeZip(String representativeZip) {
		this.representativeZip = representativeZip;
	}

	public String getRepresentativeCity() {
		return representativeCity;
	}

	public void setRepresentativeCity(String representativeCity) {
		this.representativeCity = representativeCity;
	}

	public String getRepresentativeCountry() {
		return representativeCountry;
	}

	public void setRepresentativeCountry(String representativeCountry) {
		this.representativeCountry = representativeCountry;
	}

	public String getReferringDoctor() {
		return referringDoctor;
	}

	public void setReferringDoctor(String referringDoctor) {
		this.referringDoctor = referringDoctor;
	}

	public String getAnimalKind() {
		return animalKind;
	}

	public void setAnimalKind(String animalKind) {
		this.animalKind = animalKind;
	}

	public String getAnimalRace() {
		return animalRace;
	}

	public void setAnimalRace(String animalRace) {
		this.animalRace = animalRace;
	}

	public String getAnimalChipNumber() {
		return animalChipNumber;
	}

	public void setAnimalChipNumber(String animalChipNumber) {
		this.animalChipNumber = animalChipNumber;
	}

	public String getPatientVisitNumber() {
		return patientVisitNumber;
	}

	public void setPatientVisitNumber(String patientVisitNumber) {
		this.patientVisitNumber = patientVisitNumber;
	}

	public String getAssiginingFacilityNamespace() {
		return assiginingFacilityNamespace;
	}

	public void setAssiginingFacilityNamespace(String assiginingFacilityNamespace) {
		this.assiginingFacilityNamespace = assiginingFacilityNamespace;
	}

	public String getAssigningJurisdictionIdentifier() {
		return assigningJurisdictionIdentifier;
	}

	public void setAssigningJurisdictionIdentifier(String assigningJurisdictionIdentifier) {
		this.assigningJurisdictionIdentifier = assigningJurisdictionIdentifier;
	}

	public String getFinancialClass() {
		return financialClass;
	}

	public void setFinancialClass(String financialClass) {
		this.financialClass = financialClass;
	}

	public String getPlacerOrderNumber() {
		return placerOrderNumber;
	}

	public void setPlacerOrderNumber(String placerOrderNumber) {
		this.placerOrderNumber = placerOrderNumber;
	}
}
