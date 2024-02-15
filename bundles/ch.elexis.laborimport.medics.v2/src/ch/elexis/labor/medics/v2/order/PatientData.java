package ch.elexis.labor.medics.v2.order;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.model.format.PersonFormatUtil;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoverageServiceHolder;

public class PatientData {

	private String lastName;
	private String firstName;
	private String title;
	private String salutation;
	private String gender; // "W"
	private String birthdate; // "2000-12-12"
	private String street;
	private String houseNumber;
	private String city;
	private String postalCode;
	private String country;
	private String phoneNumber; // "+49 1234 5556777, +44 1234 5556799, +44 1234 5556999",
	private String eMail; // "jane@doe.co.uk, jane.doe@doe.co.uk",
	private String externalPID; // "9192343681",
	private String externalSystem; // "vitomed2",
	private String externalOrderNumber; // "334308681",
	private String insuranceNumber; // "01049203",
	private String insuranceName; // "Healthclassic",
	private String insuranceInstituteNumber;
	private String requesterKey; // "TestPhys1",
	private String officialPatientId; // "0123456789",
	private String officialPatientRegistry; // "AHV"
	
	public static PatientData of(IPatient patient) {
		PatientData ret = new PatientData();

		ret.externalSystem = "elexis";
		ret.externalPID = patient.getPatientNr();

		ret.lastName = patient.getLastName();
		ret.firstName = patient.getFirstName();
		if (StringUtils.isNotBlank(patient.getTitel())) {
			ret.title = patient.getTitel();
		}
		if (StringUtils.isNotBlank(PersonFormatUtil.getSalutation(patient))) {
			ret.salutation = PersonFormatUtil.getSalutation(patient);
		}
		if (StringUtils.isNotBlank(PersonFormatUtil.getSalutation(patient))) {
			ret.salutation = PersonFormatUtil.getSalutation(patient);
		}
		ret.gender = patient.getGender().value();
		if (patient.getDateOfBirth() != null) {
			ret.birthdate = patient.getDateOfBirth().toLocalDate().toString();
		}
		if (StringUtils.isNotBlank(patient.getStreet())) {
			ret.street = patient.getStreet();
		}
		if (StringUtils.isNotBlank(patient.getCity())) {
			ret.city = patient.getCity();
		}
		if (StringUtils.isNotBlank(patient.getZip())) {
			ret.postalCode = patient.getZip();
		}
		if (patient.getCountry() != null) {
			ret.country = patient.getCountry().name();
		}
		StringJoiner sj = new StringJoiner(", ");
		if (StringUtils.isNotBlank(patient.getPhone1())) {
			sj.add(patient.getPhone1());
		}
		if (StringUtils.isNotBlank(patient.getMobile())) {
			sj.add(patient.getMobile());
		}
		if (StringUtils.isNotBlank(sj.toString())) {
			ret.phoneNumber = sj.toString();
		}
		if (StringUtils.isNotBlank(patient.getEmail())) {
			ret.eMail = patient.getEmail();
		}

		ICoverage insuranceInfoFall = getFallWithInsuranceInfo(patient);
		if (insuranceInfoFall != null) {
			String insuredNumber = getInsuredNumber(insuranceInfoFall);
			if (StringUtils.isNotBlank(insuredNumber)) {
				ret.insuranceNumber = insuredNumber;
			}
			IContact costbearer = insuranceInfoFall.getCostBearer();
			if (costbearer != null && costbearer.isOrganization()) {
				if (StringUtils.isNotBlank(costbearer.getDescription1())) {
					ret.insuranceName = costbearer.getDescription1();
				}
				if (costbearer.getXid(DOMAIN_EAN) != null) {
					String ean = costbearer.getXid(DOMAIN_EAN).getDomainId().trim();
					if (StringUtils.isNotBlank(ean)) {
						ret.insuranceInstituteNumber = ean;
					}
				}
			}
		}

		IXid socialSecurityNumber = patient.getXid(DOMAIN_AHV);
		if (socialSecurityNumber != null) {
			String ssn = socialSecurityNumber.getDomainId().trim();
			String ssnNoDots = ssn.replaceAll("\\.", StringUtils.EMPTY); //$NON-NLS-1$
			if (ssnNoDots.length() == 11) {
				ret.officialPatientId = ssnNoDots;
				ret.officialPatientRegistry = "AHV";
			} else if (ssnNoDots.length() == 13) {
				ret.officialPatientId = ssnNoDots;
				ret.officialPatientRegistry = "AHV";
			}
		}
		return ret;
	}

	private static ICoverage getFallWithInsuranceInfo(IPatient patient) {
		ICoverage selected = ContextServiceHolder.get().getTyped(ICoverage.class).orElse(null);
		if (selected != null && hasInsuranceInfo(selected) && selected.getBillingSystem().getLaw() == BillingLaw.KVG) {
			return selected;
		} else {
			ICoverage ret = null;
			for (ICoverage coverage : patient.getCoverages()) {
				if (coverage.isOpen() && hasInsuranceInfo(coverage)) {
					// prefer KVG
					if (ret == null) {
						ret = coverage;
					} else if (coverage.getBillingSystem().getLaw() == BillingLaw.KVG) {
						ret = coverage;
					}
				}
			}
			return ret;
		}
	}

	private static boolean hasInsuranceInfo(ICoverage fall) {
		if (StringUtils.isNotBlank((String) fall.getExtInfo("Versicherungsnummer"))) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	private static String getInsuredNumber(ICoverage insuranceInfoFall) {
		if (StringUtils.isNotBlank((String) insuranceInfoFall.getExtInfo("Versicherungsnummer"))) { //$NON-NLS-1$
			return CoverageServiceHolder.get().getRequiredString(insuranceInfoFall, "Versicherungsnummer"); //$NON-NLS-1$
		}
		return StringUtils.EMPTY;
	}

	public PatientData withRequesterKey(String requesterKey) {
		this.requesterKey = requesterKey;
		return this;
	}
}
