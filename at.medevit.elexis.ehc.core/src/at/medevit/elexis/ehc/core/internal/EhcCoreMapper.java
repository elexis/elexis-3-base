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
package at.medevit.elexis.ehc.core.internal;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.elexis.data.Anschrift;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Person;
import ch.rgw.tools.TimeTool;
import ehealthconnector.cda.documents.ch.Address;
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
			ret.cAddAddress(ehcAddress);
		}
		
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
}
