/*******************************************************************************
 * Copyright (c) 2017 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.emediplan.core.model.print;

import ch.elexis.core.types.Gender;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;

public class ContactInfo {
	String title;
	String lastname;
	String firstname;
	String street1;
	String street2;
	String city;
	String zip;
	String email;
	String menumber;
	String birthdate;
	String gender;
	String tel;
	String insurancenumber;

	public static ContactInfo fromPatient(Patient pat){
		ContactInfo ret = new ContactInfo();
		ret.setBirthdate(pat.getGeburtsdatum());
		ret.setCity(pat.get(Patient.FLD_PLACE));
		ret.setFirstname(pat.getVorname());
		ret.setGender(pat.getGender() == Gender.FEMALE ? "W" : "M");
		ret.setLastname(pat.getName());
		ret.setStreet1(pat.get(Patient.FLD_STREET));
		ret.setZip(pat.get(Patient.FLD_ZIP));
		ret.setTel(pat.get(Kontakt.FLD_PHONE1));
		return ret;
	}
	
	/**
	 * Change the information contained by this Object to
	 * the Information provided by the {@link Kontakt} or its subclasses.
	 * 
	 * @param kon
	 */
	public static ContactInfo fromKontakt(Kontakt kon){
		ContactInfo ret = new ContactInfo();
		if(kon.istOrganisation()) {
			ret.setZip(kon.get(Kontakt.FLD_ZIP));
			ret.setCity(kon.get(Kontakt.FLD_PLACE));
			ret.setStreet1(kon.get(Kontakt.FLD_STREET));
			ret.setLastname(kon.get(Kontakt.FLD_NAME2));
			ret.setFirstname(kon.get(Kontakt.FLD_NAME1));
			ret.setTel(kon.get(Kontakt.FLD_PHONE1));
		} else if (kon.istPerson()) {
			ret.setZip(kon.get(Person.FLD_ZIP));
			ret.setCity(kon.get(Person.FLD_PLACE));
			ret.setStreet1(kon.get(Person.FLD_STREET));
			ret.setLastname(kon.get(Person.FLD_NAME1));
			ret.setFirstname(kon.get(Person.FLD_NAME2));
			ret.setBirthdate(kon.get(Person.BIRTHDATE));
			ret.setGender(kon.get(Person.SEX));
			ret.setTel(kon.get(Kontakt.FLD_PHONE1));
			ret.setTitle(kon.get(Person.TITLE));
		} else {
			ret.setZip(kon.get(Kontakt.FLD_ZIP));
			ret.setCity(kon.get(Kontakt.FLD_PLACE));
			ret.setStreet1(kon.get(Kontakt.FLD_STREET));
			ret.setLastname(kon.get(Kontakt.FLD_NAME1));
			ret.setFirstname(kon.get(Kontakt.FLD_NAME2));
		}
		return ret;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getStreet1() {
		return street1;
	}
	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	public String getStreet2() {
		return street2;
	}
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMenumber() {
		return menumber;
	}
	public void setMenumber(String menumber) {
		this.menumber = menumber;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	public String getInsurancenumber() {
		return insurancenumber;
	}
	public void setInsurancenumber(String inumber) {
		insurancenumber = inumber;
	}
	@Override
	public String toString() {
		return "ContactInfo: " + firstname + " " + lastname + " " + city + " " + zip + " " + menumber;
	}
}
