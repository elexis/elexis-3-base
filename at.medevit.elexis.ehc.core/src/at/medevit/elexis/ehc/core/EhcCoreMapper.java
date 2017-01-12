/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.core;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ehealth_connector.common.Address;
import org.ehealth_connector.common.Author;
import org.ehealth_connector.common.Identificator;
import org.ehealth_connector.common.Name;
import org.ehealth_connector.common.Organization;
import org.ehealth_connector.common.Patient;
import org.ehealth_connector.common.Telecoms;
import org.ehealth_connector.common.enums.AddressUse;
import org.ehealth_connector.common.enums.AdministrativeGender;
import org.ehealth_connector.common.enums.CodeSystems;

import ch.elexis.core.model.IPersistentObject;
import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnungssteller;
import ch.elexis.data.Xid;
import ch.rgw.tools.TimeTool;

/**
 * Utility class for mapping objects between the Elexis domain and the ehealthconnector domain.
 * 
 * @see <a href="https://sourceforge.net/projects/ehealthconnector/">ehealthconnector</a>
 * @author thomas
 *
 */
public class EhcCoreMapper {
	
	private static TimeTool timeTool = new TimeTool();
	
	private final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)[a-z]?$");
	
	public static Name getEhcName(String name){
		String[] parts = name.split(" ");
		if (parts.length == 1) {
			return new Name("", parts[0]);
		} else if (parts.length == 2) {
			return new Name(parts[0], parts[1]);
		} else if (parts.length >= 3) {
			return new Name(parts[0], parts[1], parts[2]);
		}
		return new Name("", "");
	}

	public static Patient getEhcPatient(ch.elexis.data.Patient elexisPatient){
		Patient ret =
			new Patient(getEhcPersonName(elexisPatient), getEhcGenderCode(elexisPatient),
				getDate(elexisPatient.getGeburtsdatum()));
		
		// PHONE
		Telecoms telecoms = new Telecoms();
		String value = elexisPatient.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			telecoms.addPhone(value, AddressUse.PRIVATE);
		}
		value = elexisPatient.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			telecoms.addPhone(value, AddressUse.MOBILE);
		}
		ret.setTelecoms(telecoms);
		// ADDRESS
		Anschrift elexisAddress = elexisPatient.getAnschrift();
		if (elexisAddress != null) {
			ret.addAddress(getEhcAddress(elexisAddress));
		}
		
		String socialSecurityNumber = elexisPatient.getXid(DOMAIN_AHV);
		if (socialSecurityNumber != null) {
			socialSecurityNumber = socialSecurityNumber.trim();
			socialSecurityNumber = socialSecurityNumber.replaceAll("\\.", "");
			if (socialSecurityNumber.length() == 11) {
				ret.addId(new Identificator(CodeSystems.SwissSSNDeprecated.getCodeSystemId(),
					socialSecurityNumber));
			} else if (socialSecurityNumber.length() == 13) {
				ret.addId(new Identificator(CodeSystems.SwissSSN.getCodeSystemId(),
					socialSecurityNumber));
			}
		}

		return ret;
	}
	
	public static Address getEhcAddress(Anschrift elexisAddress){
		String elexisStreet = elexisAddress.getStrasse();
		String houseNumber = "";
		// try to get the house number
		Matcher matcher = lastIntPattern.matcher(elexisStreet);
		if (matcher.find()) {
			houseNumber = matcher.group(1);
			elexisStreet = elexisStreet.substring(0, matcher.start(1));
		}
		
		Address ehcAddress =
			new Address(elexisStreet.trim(), houseNumber, elexisAddress.getPlz(),
				elexisAddress.getOrt());
		ehcAddress.setAddressline1(elexisAddress.getStrasse());
		return ehcAddress;
	}
	
	public static Author getEhcAuthor(Mandant elexisMandant){
		String gln = elexisMandant.getXid(DOMAIN_EAN);
		Author ret = new Author(getEhcPersonName(elexisMandant), gln);
		
		// PHONE
		Telecoms telecoms = new Telecoms();
		String value = elexisMandant.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			telecoms.addPhone(value, AddressUse.PRIVATE);
		}
		value = elexisMandant.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			telecoms.addPhone(value, AddressUse.MOBILE);
		}
		ret.setTelecoms(telecoms);
		// ADDRESS
		Anschrift elexisAddress = elexisMandant.getAnschrift();
		if (elexisAddress != null) {
			ret.addAddress(getEhcAddress(elexisAddress));
		}

		return ret;
	}
	
	public static Organization getEhcOrganization(Mandant elexisMandant){
		Rechnungssteller rechnungssteller = elexisMandant.getRechnungssteller();
		String gln = rechnungssteller.getXid(DOMAIN_EAN);
		Organization ret = new Organization(rechnungssteller.getLabel(), gln);
		
		// PHONE
		Telecoms telecoms = new Telecoms();
		String value = rechnungssteller.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			telecoms.addPhone(value, AddressUse.PRIVATE);
		}
		value = rechnungssteller.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			telecoms.addPhone(value, AddressUse.MOBILE);
		}
		ret.setTelecoms(telecoms);
		// ADDRESS
		Anschrift elexisAddress = rechnungssteller.getAnschrift();
		if (elexisAddress != null) {
			ret.addAddress(getEhcAddress(elexisAddress));
		}

		return ret;
	}

	public static Name getEhcPersonName(Person elexisPerson){
		Name ret =
			new Name(elexisPerson.getVorname(), elexisPerson.getName(),
				elexisPerson.get(Person.TITLE));
		
		return ret;
	}
	
	public static AdministrativeGender getEhcGenderCode(Person elexisPerson){
		if (elexisPerson.getGeschlecht().equals(Person.FEMALE)) {
			return AdministrativeGender.FEMALE;
		} else if (elexisPerson.getGeschlecht().equals(Person.MALE)) {
			return AdministrativeGender.MALE;
		}
		return AdministrativeGender.UNDIFFERENTIATED;
	}
	
	public static Date getDate(String elexisDate){
		timeTool.set(elexisDate);
		return timeTool.getTime();
	}
	
	public static ch.elexis.data.Patient getElexisPatient(Patient ehcPatient){
		// try to look up via ids
		List<Identificator> ids = ehcPatient.getIds();
		for (Identificator identificator : ids) {
			String idRoot = identificator.getRoot();
			if (idRoot.equals(CodeSystems.SwissSSNDeprecated.getCodeSystemId())
				|| idRoot.equals(CodeSystems.SwissSSN.getCodeSystemId())) {
				IPersistentObject ret =
					Xid.findObject(DOMAIN_AHV, identificator.getExtension());
				if (ret instanceof Kontakt) {
					if (((Kontakt) ret).istPatient()) {
						return ch.elexis.data.Patient.load(ret.getId());
					}
				}
				System.out.println("foud ret " + ret);
				if (ret instanceof ch.elexis.data.Patient) {
					return (ch.elexis.data.Patient) ret;
				}
			}
		}
		
		Query<ch.elexis.data.Patient> qpa =
			new Query<ch.elexis.data.Patient>(ch.elexis.data.Patient.class);
		// initialize data
		Name ehcName = ehcPatient.getName();
		Date ehcBirthdate = ehcPatient.getBirthday();
		String gender =
			ehcPatient.getAdministrativeGenderCode() == AdministrativeGender.FEMALE ? Person.FEMALE
					: Person.MALE;
		TimeTool ttBirthdate = new TimeTool();
		// add data to query
		if (ehcName.getFamilyName() != null && !ehcName.getFamilyName().isEmpty()) {
			qpa.add(ch.elexis.data.Patient.FLD_NAME, Query.EQUALS, ehcName.getFamilyName());
		}
		if (ehcName.getGivenNames() != null && !ehcName.getGivenNames().isEmpty()) {
			qpa.add(ch.elexis.data.Patient.FLD_FIRSTNAME, Query.EQUALS, ehcName.getGivenNames());
		}
		if (ehcBirthdate != null) {
			ttBirthdate.setTime(ehcBirthdate);
			qpa.add(Person.BIRTHDATE, Query.EQUALS, ttBirthdate.toString(TimeTool.DATE_COMPACT));
		}
		List<ch.elexis.data.Patient> existing = qpa.execute();
		// create or overwrite Patient
		ch.elexis.data.Patient ret = null;
		if (existing.isEmpty()) {
			ret =
				new ch.elexis.data.Patient(ehcName.getFamilyName(), ehcName.getGivenNames(),
					ttBirthdate.toString(TimeTool.DATE_COMPACT), gender);
		} else {
			ret = existing.get(0);
		}
		
		return ret;
	}
	
	public static void importEhcAddress(ch.elexis.data.Kontakt kontakt, Address address){
		Anschrift elexisAddress = kontakt.getAnschrift();
		elexisAddress.setOrt(address.getCity());
		elexisAddress.setPlz(address.getZip());
		elexisAddress.setStrasse(address.getStreet() + " " + address.getHouseNumber());
		kontakt.setAnschrift(elexisAddress);
	}
	
	public static void importEhcPhone(ch.elexis.data.Kontakt kontakt, Telecoms telecoms){
		Map<String, AddressUse> phones = telecoms.getPhones();
		Set<String> keys = phones.keySet();
		String existing = kontakt.get(Kontakt.FLD_PHONE1);
		
		for (String key : keys) {
			if (!key.equals(existing)) {
				if (existing == null || existing.isEmpty()) {
					kontakt.set(Kontakt.FLD_PHONE1, key);
				} else {
					kontakt.set(Kontakt.FLD_PHONE2, key);
				}
			}
		}
	}
}
