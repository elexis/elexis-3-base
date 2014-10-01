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

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.rgw.tools.TimeTool;
import ehealthconnector.cda.documents.ch.Address;
import ehealthconnector.cda.documents.ch.Author;
import ehealthconnector.cda.documents.ch.ConvenienceUtilsEnums.AdministrativeGenderCode;
import ehealthconnector.cda.documents.ch.ConvenienceUtilsEnums.UseCode;
import ehealthconnector.cda.documents.ch.Name;
import ehealthconnector.cda.documents.ch.Patient;

public class EhcCoreMapper {
	
	private static TimeTool timeTool = new TimeTool();
	
	private final static Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)[a-z]?$");
	
	public static Patient getEhcPatient(ch.elexis.data.Patient elexisPatient){
		Patient ret =
			new Patient(getEhcPersonName(elexisPatient), getEhcGenderCode(elexisPatient),
				getDate(elexisPatient.getGeburtsdatum()));
		
		// PHONE
		String value = elexisPatient.get(Kontakt.FLD_PHONE1);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			ret.cAddPhone(value, UseCode.Private);
		}
		value = elexisPatient.get(Kontakt.FLD_MOBILEPHONE);
		if (value != null && !value.isEmpty() && !value.equalsIgnoreCase("0")) {
			ret.cAddPhone(value, UseCode.Mobile);
		}
		// ADDRESS
		Anschrift elexisAddress = elexisPatient.getAnschrift();
		if (elexisAddress != null) {
			ret.cAddAddress(getEhcAddress(elexisAddress));
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
		ehcAddress.cSetAddressline1(elexisAddress.getStrasse());
		return ehcAddress;
	}
	
	public static Author getEhcAuthor(Mandant elexisMandant){
		String gln = elexisMandant.getXid(Xid.DOMAIN_EAN);
		Author ret = new Author(getEhcPersonName(elexisMandant), gln);
		
		return ret;
	}
	
	public static Name getEhcPersonName(Person elexisPerson){
		Name ret =
			new Name(elexisPerson.getName(), elexisPerson.getVorname(),
				elexisPerson.get(Person.TITLE));
		
		return ret;
	}
	
	public static AdministrativeGenderCode getEhcGenderCode(Person elexisPerson){
		if (elexisPerson.getGeschlecht().equals(Person.FEMALE)) {
			return AdministrativeGenderCode.Female;
		} else if (elexisPerson.getGeschlecht().equals(Person.MALE)) {
			return AdministrativeGenderCode.Male;
		}
		return AdministrativeGenderCode.Undifferentiated;
	}
	
	public static Date getDate(String elexisDate){
		timeTool.set(elexisDate);
		return timeTool.getTime();
	}
	
	public static ch.elexis.data.Patient getElexisPatient(
		ehealthconnector.cda.documents.ch.Patient ehcPatient){
		Query<ch.elexis.data.Patient> qpa =
			new Query<ch.elexis.data.Patient>(ch.elexis.data.Patient.class);
		// initialize data
		Name ehcName = ehcPatient.cGetName();
		String ehcBirthdate = ehcPatient.cGetBirthDate();
		String gender =
			ehcPatient.cGetGender() == AdministrativeGenderCode.Female ? Person.FEMALE
					: Person.MALE;
		TimeTool ttBirthdate = new TimeTool();
		// add data to query
		if (ehcName.cGetName() != null && !ehcName.cGetName().isEmpty()) {
			qpa.add(ch.elexis.data.Patient.FLD_NAME, Query.EQUALS, ehcName.cGetName());
		}
		if (ehcName.cGetFirstName() != null && !ehcName.cGetFirstName().isEmpty()) {
			qpa.add(ch.elexis.data.Patient.FLD_FIRSTNAME, Query.EQUALS, ehcName.cGetFirstName());
		}
		if (ehcBirthdate != null && !ehcBirthdate.isEmpty()) {
			if (ttBirthdate.set(ehcBirthdate)) {
				qpa.add(Person.BIRTHDATE, Query.EQUALS, ttBirthdate.toString(TimeTool.DATE_COMPACT));
			}
		}
		List<ch.elexis.data.Patient> existing = qpa.execute();
		// create or overwrite Patient
		ch.elexis.data.Patient ret = null;
		if (existing.isEmpty()) {
			ret =
				new ch.elexis.data.Patient(ehcName.cGetName(), ehcName.cGetFirstName(),
					ttBirthdate.toString(TimeTool.DATE_COMPACT), gender);
		} else {
			ret = existing.get(0);
		}
		
		return ret;
	}
	
	public static void importEhcAddress(ch.elexis.data.Kontakt kontakt,
		ehealthconnector.cda.documents.ch.Address address){
		Anschrift elexisAddress = kontakt.getAnschrift();
		elexisAddress.setOrt(address.cGetCity());
		elexisAddress.setPlz(address.cGetZip());
		elexisAddress.setStrasse(address.cGetStreet() + " " + address.cGetHouseNumber());
		kontakt.setAnschrift(elexisAddress);
	}
	
	public static void importEhcPhone(ch.elexis.data.Kontakt kontakt,
		ehealthconnector.cda.documents.ch.Phone phone){
		String existing = kontakt.get(Kontakt.FLD_PHONE1);
		if (existing == null || existing.isEmpty()) {
			kontakt.set(Kontakt.FLD_PHONE1, phone.cGetNumber());
		} else {
			if (!existing.equals(phone.cGetNumber())) {
				kontakt.set(Kontakt.FLD_PHONE2, phone.cGetNumber());
			}
		}
	}
}
