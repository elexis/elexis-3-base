package at.medevit.elexis.corona123.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.corona123.preference.PreferenceConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.types.Gender;
import ch.elexis.data.Fall;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.rgw.tools.TimeTool;

public class UrlBuilder {
	
	public static String getPatientParameters(Patient patient){
		String parameters = "";
		try {
			parameters += "firstName=" + URLEncoder.encode(patient.getVorname(), "UTF-8");
			parameters += "&lastName=" + URLEncoder.encode(patient.getName(), "UTF-8");
			if (patient.getGeburtsdatum() != null) {
				parameters += "&dateOfBirth=" + URLEncoder.encode(
					new TimeTool(patient.getGeburtsdatum()).toString(TimeTool.DATE_ISO),
					"UTF-8");
			}
			parameters +=
				"&gender=" + URLEncoder.encode(genderAsString(patient.getGender()), "UTF-8");
			
			if (StringUtils.isNotBlank(patient.getAnschrift().getLand())) {
				parameters +=
					"&nationality=" + URLEncoder.encode(patient.getAnschrift().getLand(), "UTF-8");
			}
			if (StringUtils.isNotBlank(patient.getAnschrift().getStrasse())) {
				parameters += "&street=" + URLEncoder.encode(patient.getAnschrift().getStrasse(), "UTF-8");
			}
			if (StringUtils.isNotBlank(patient.getAnschrift().getPlz())) {
				parameters +=
					"&postalCode=" + URLEncoder.encode(patient.getAnschrift().getPlz(), "UTF-8");
			}
			if (StringUtils.isNotBlank(patient.getAnschrift().getOrt())) {
				parameters +=
					"&city=" + URLEncoder.encode(patient.getAnschrift().getOrt(), "UTF-8");
			}
			
			if (StringUtils.isNotBlank(patient.get(Person.FLD_PHONE1))) {
				parameters +=
					"&phone=" + URLEncoder.encode(patient.get(Person.FLD_PHONE1), "UTF-8");
			} else if (StringUtils.isNotBlank(patient.get(Person.FLD_PHONE2))) {
				parameters +=
					"&phone=" + URLEncoder.encode(patient.get(Person.FLD_PHONE2), "UTF-8");
			}
			
			if (StringUtils.isNotBlank(patient.get(Person.FLD_E_MAIL))) {
				parameters +=
					"&email=" + URLEncoder.encode(patient.get(Person.FLD_E_MAIL), "UTF-8");
			}
			
			if (patient.getStammarzt() != null) {
				Kontakt familyDoctor = patient.getStammarzt();
				parameters += "&generalPractitioner="
					+ URLEncoder.encode(getContactNameWithTitle(familyDoctor),
					"UTF-8");
			} else if (ElexisEventDispatcher.getSelectedMandator() != null) {
				parameters += "&generalPractitioner=" + URLEncoder.encode(
					getContactNameWithTitle(ElexisEventDispatcher.getSelectedMandator()), "UTF-8");
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
	
	private static String getInsurance(Patient patient){
		String ret = "";
		Fall activeCoverage = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
		if (activeCoverage != null) {
			if (activeCoverage.isOpen()) {
				if (activeCoverage.getCostBearer() != null
					&& activeCoverage.getCostBearer().istOrganisation()) {
					Organisation costBearer =
						Organisation.load(activeCoverage.getCostBearer().getId());
					return costBearer.get(Kontakt.FLD_NAME1) + " "
						+ StringUtils.defaultString(costBearer.get(Kontakt.FLD_NAME2));
				}
			}
		}
		for (Fall coverage : patient.getFaelle()) {
			if (coverage.isOpen()) {
				if (coverage.getCostBearer() != null
					&& coverage.getCostBearer().istOrganisation()) {
					Organisation costBearer = Organisation.load(coverage.getCostBearer().getId());
					return costBearer.get(Kontakt.FLD_NAME1) + " "
						+ StringUtils.defaultString(costBearer.get(Kontakt.FLD_NAME2));
				}
			}
		}
		return ret;
	}
	
	private static String getInsuranceCardNumber(Patient patient){
		String ret = "";
		Fall activeCoverage = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
		if (activeCoverage != null) {
			if (activeCoverage.isOpen()) {
				if (StringUtils
					.isNotBlank((String) activeCoverage.getExtInfoStoredObjectByKey("VEKANr"))) {
					return (String) activeCoverage.getExtInfoStoredObjectByKey("VEKANr");
				}
				if (StringUtils
					.isNotBlank((String) activeCoverage
						.getExtInfoStoredObjectByKey("Versicherten-Nummer"))) {
					return (String) activeCoverage
						.getExtInfoStoredObjectByKey("Versicherten-Nummer");
				}
				if (StringUtils.isNotBlank(
					(String) activeCoverage.getExtInfoStoredObjectByKey("Versicherungsnummer"))) {
					return (String) activeCoverage
						.getExtInfoStoredObjectByKey("Versicherungsnummer");
				}
			}
		}
		for (Fall coverage : patient.getFaelle()) {
			if (coverage.isOpen()) {
				if (StringUtils
					.isNotBlank((String) coverage.getExtInfoStoredObjectByKey("VEKANr"))) {
					return (String) coverage.getExtInfoStoredObjectByKey("VEKANr");
				}
				if (StringUtils.isNotBlank(
					(String) coverage.getExtInfoStoredObjectByKey("Versicherten-Nummer"))) {
					return (String) coverage.getExtInfoStoredObjectByKey("Versicherten-Nummer");
				}
				if (StringUtils.isNotBlank(
					(String) coverage.getExtInfoStoredObjectByKey("Versicherungsnummer"))) {
					return (String) coverage.getExtInfoStoredObjectByKey("Versicherungsnummer");
				}
			}
		}
		return ret;
	}
	
	private static String getContactNameWithTitle(Kontakt familyDoctor){
		if (familyDoctor.istPerson()) {
			Person person = Person.load(familyDoctor.getId());
			return person.get(Person.TITLE) + " " + person.getVorname() + " " + person.getName()
				+ ", " + person.getAnschrift().getOrt();
		} else {
			return familyDoctor.get(Kontakt.FLD_NAME1);
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
		return CoreHub.globalCfg.get(PreferenceConstants.CFG_CORONA123_ORGID, "");
	}
	
	public static boolean isOrgId(){
		return StringUtils.isNotBlank(getOrgId());
	}
}
