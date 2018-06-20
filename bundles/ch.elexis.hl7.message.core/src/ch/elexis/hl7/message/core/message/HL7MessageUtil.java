package ch.elexis.hl7.message.core.message;

import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.hl7.data.HL7Konsultation;
import ch.elexis.hl7.data.HL7Kontakt;
import ch.elexis.hl7.data.HL7Mandant;
import ch.elexis.hl7.data.HL7Patient;
import ch.rgw.tools.TimeTool;

public class HL7MessageUtil {
	
	/**
	 * Get a {@link HL7Mandant} representing the {@link Mandant}
	 * 
	 * @param eMandant
	 * @return
	 */
	public static HL7Mandant mandantOf(Mandant eMandant){
		HL7Mandant ret = new HL7Mandant();
		
		ret.setLabel(eMandant.get(Anwender.FLD_LABEL));
		ret.setEan(eMandant.getXid(DOMAIN_EAN));
		
		return ret;
	}
	
	/**
	 * Get a {@link HL7Patient} representing the {@link Patient}
	 * 
	 * @param ePatient
	 * @return
	 */
	public static HL7Patient patientOf(Patient ePatient){
		HL7Patient ret = new HL7Patient();
		
		setContactInfo(ret, ePatient);
		String geschlecht = ePatient.getGeschlecht();
		if (geschlecht != null && geschlecht.length() > 0) {
			ret.setIsMale(
				Patient.MALE.toUpperCase().equals(ePatient.getGeschlecht().toUpperCase()));
		}
		ret.setBirthdate(new TimeTool(ePatient.getGeburtsdatum()).getTime());
		ret.setPatCode(ePatient.getPatCode());
		
		return ret;
	}
	
	public static HL7Konsultation consultationOf(Konsultation eConsultation){
		HL7Konsultation ret = new HL7Konsultation();
		
		ret.setId(eConsultation.getId());
		TimeTool timeTool = new TimeTool(eConsultation.getDatum());
		ret.setZeitpunkt(timeTool.toLocalDateTime());
		
		return ret;
	}
	
	private static void setContactInfo(HL7Kontakt hKontakt, Kontakt eKontakt){
		String name = eKontakt.get(Kontakt.FLD_NAME1);
		if (name == null) {
			name = ""; //$NON-NLS-1$
		}
		hKontakt.setName(name.trim());
		
		String firstname = eKontakt.get(Kontakt.FLD_NAME2);
		if (firstname == null) {
			firstname = ""; //$NON-NLS-1$
		}
		hKontakt.setFirstname(firstname.trim());
		
		String title = eKontakt.get("Titel"); //$NON-NLS-1$
		if (title == null) {
			title = ""; //$NON-NLS-1$
		}
		hKontakt.setTitle(title.trim());
		
		String phone1 = eKontakt.get(Kontakt.FLD_PHONE1);
		if (phone1 == null) {
			phone1 = ""; //$NON-NLS-1$
		}
		hKontakt.setPhone1(phone1.trim());
		
		String phone2 = eKontakt.get(Kontakt.FLD_PHONE2);
		if (phone2 == null) {
			phone2 = ""; //$NON-NLS-1$
		}
		hKontakt.setPhone2(phone2.trim());
		
		String email = eKontakt.get(Kontakt.FLD_E_MAIL);
		if (email == null) {
			email = ""; //$NON-NLS-1$
		}
		hKontakt.setEmail(email.trim());
		
		String fax = eKontakt.get(Kontakt.FLD_FAX);
		if (fax == null) {
			fax = ""; //$NON-NLS-1$
		}
		hKontakt.setFax(fax.trim());
		
		String street = eKontakt.get(Kontakt.FLD_STREET);
		if (street == null) {
			street = ""; //$NON-NLS-1$
		}
		hKontakt.setAddress1(street.trim());
		
		String other = eKontakt.get(Patient.FLD_NAME3);
		if (other == null) {
			other = ""; //$NON-NLS-1$
		}
		hKontakt.setAddress2(other.trim());
		
		String city = eKontakt.get(Patient.FLD_PLACE);
		if (city == null) {
			city = ""; //$NON-NLS-1$
		}
		hKontakt.setCity(city.trim());
		
		String zip = eKontakt.get(Patient.FLD_ZIP);
		if (zip == null) {
			zip = ""; //$NON-NLS-1$
		}
		hKontakt.setZip(zip.trim());
		
		String country = eKontakt.get(Patient.FLD_COUNTRY);
		if (country == null) {
			country = ""; //$NON-NLS-1$
		}
		hKontakt.setCountry(country.trim());
	}
}
