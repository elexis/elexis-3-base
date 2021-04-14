package at.medevit.elexis.corona123.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.corona123.preference.PreferenceConstants;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;

public class UrlBuilder {
	
	public static String getPatientParameters(IPatient patient){
		String parameters = "";
		try {
			parameters += "firstName=" + URLEncoder.encode(patient.getFirstName(), "UTF-8");
			parameters += "&lastName=" + URLEncoder.encode(patient.getLastName(), "UTF-8");
			if (patient.getDateOfBirth() != null) {
				parameters += "&dateOfBirth=" + URLEncoder.encode(
					DateTimeFormatter.ofPattern("yyyy-MM-dd").format(patient.getDateOfBirth()),
					"UTF-8");
			}
			parameters +=
				"&gender=" + URLEncoder.encode(genderAsString(patient.getGender()), "UTF-8");
			
			if (patient.getCountry() != null) {
				parameters +=
					"&nationality=" + URLEncoder.encode(patient.getCountry().toString(), "UTF-8");
			}
			if (StringUtils.isNotBlank(patient.getStreet())) {
				parameters += "&street=" + URLEncoder.encode(patient.getStreet(), "UTF-8");
			}
			if (StringUtils.isNotBlank(patient.getZip())) {
				parameters += "&postalCode=" + URLEncoder.encode(patient.getZip(), "UTF-8");
			}
			if (StringUtils.isNotBlank(patient.getCity())) {
				parameters += "&city=" + URLEncoder.encode(patient.getCity(), "UTF-8");
			}
			
			if (StringUtils.isNotBlank(patient.getPhone1())) {
				parameters += "&phone=" + URLEncoder.encode(patient.getPhone1(), "UTF-8");
			} else if (StringUtils.isNotBlank(patient.getPhone2())) {
				parameters += "&phone=" + URLEncoder.encode(patient.getPhone2(), "UTF-8");
			}
			
			if (StringUtils.isNotBlank(patient.getEmail())) {
				parameters += "&email=" + URLEncoder.encode(patient.getEmail(), "UTF-8");
			}
			
			if (patient.getFamilyDoctor() != null) {
				IContact familyDoctor = patient.getFamilyDoctor();
				parameters += "&generalPractitioner="
					+ URLEncoder.encode(getContactNameWithTitle(familyDoctor),
					"UTF-8");
			}
			
			String insuranceCardNumber = getInsuranceCardNumber(patient);
			if (StringUtils.isNotBlank(insuranceCardNumber)) {
				parameters +=
					"&identityNumberInsurance=" + URLEncoder.encode(insuranceCardNumber, "UTF-8");
			}
			
			String insurance = getInsurance(patient);
			if (StringUtils.isNotBlank(insurance)) {
				parameters += "&healthInsurance=" + URLEncoder.encode(insurance, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			LoggerFactory.getLogger(UrlBuilder.class).error("Error getting patient parameters", e);
		}
		return parameters;
	}
	
	private static String getInsurance(IPatient patient){
		String ret = "";
		Optional<ICoverage> activeCoverage = ContextServiceHolder.get().getActiveCoverage();
		if (activeCoverage.isPresent()) {
			ICoverage coverage = activeCoverage.get();
			if (coverage.isOpen()) {
				if (coverage.getCostBearer() != null
					&& coverage.getCostBearer().isOrganization()) {
					IOrganization costBearer = CoreModelServiceHolder.get()
						.load(coverage.getCostBearer().getId(), IOrganization.class).get();
					return costBearer.getDescription1() + " "
						+ StringUtils.defaultString(costBearer.getDescription2());
				}
			}
		}
		for (ICoverage coverage : patient.getCoverages()) {
			if (coverage.isOpen()) {
				if (coverage.getCostBearer() != null
					&& coverage.getCostBearer().isOrganization()) {
					IOrganization costBearer = CoreModelServiceHolder.get()
						.load(coverage.getCostBearer().getId(), IOrganization.class).get();
					return costBearer.getDescription1() + " "
						+ StringUtils.defaultString(costBearer.getDescription2());
				}
			}
		}
		return ret;
	}
	
	private static String getInsuranceCardNumber(IPatient patient){
		String ret = "";
		Optional<ICoverage> activeCoverage = ContextServiceHolder.get().getActiveCoverage();
		if (activeCoverage.isPresent()) {
			ICoverage coverage = activeCoverage.get();
			if (coverage.isOpen()) {
				if (StringUtils.isNotBlank((String) coverage.getExtInfo("VEKANr"))) {
					return (String) coverage.getExtInfo("VEKANr");
				}
				if (StringUtils.isNotBlank((String) coverage.getExtInfo("Versicherten-Nummer"))) {
					return (String) coverage.getExtInfo("Versicherten-Nummer");
				}
			}
		}
		for (ICoverage coverage : patient.getCoverages()) {
			if (coverage.isOpen()) {
				if (StringUtils.isNotBlank((String) coverage.getExtInfo("VEKANr"))) {
					return (String) coverage.getExtInfo("VEKANr");
				}
				if (StringUtils.isNotBlank((String) coverage.getExtInfo("Versicherten-Nummer"))) {
					return (String) coverage.getExtInfo("Versicherten-Nummer");
				}
			}
		}
		return ret;
	}
	
	private static String getContactNameWithTitle(IContact contact){
		if (contact.isPerson()) {
			IPerson person =
				CoreModelServiceHolder.get().load(contact.getId(), IPerson.class).get();
			return person.getTitel() + " " + person.getLastName();
		} else {
			return contact.getDescription1();
		}
	}
	
	private static String genderAsString(Gender gender){
		switch (gender) {
		case FEMALE:
			return "FEMALE";
		case MALE:
			return "MALE";
		default:
			break;
		}
		return "OTHER";
	}
	
	public static String getVaccinationBaseUrl(){
		// https://corona123.ch/corona-vaccine-form-prefill/{organization-id}
		return "https://corona123.ch/corona-vaccine-form-prefill/" + getOrgId();
	}
	
	public static String getTestBaseUrl(){
		// https://corona123.ch/corona-test-form-prefill/{organization-id}
		return "https://corona123.ch/corona-test-form-prefill/" + getOrgId();
	}
	
	private static String getOrgId(){
		return ConfigServiceHolder.get().get(PreferenceConstants.CFG_CORONA123_ORGID, "");
	}
	
	public static boolean isOrgId(){
		return StringUtils.isNotBlank(getOrgId());
	}
}
