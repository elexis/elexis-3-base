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

import java.time.format.DateTimeFormatter;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.Gender;
import ch.elexis.data.Kontakt;

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

	public static ContactInfo fromPatient(IPatient pat){
		ContactInfo ret = new ContactInfo();
		ret.setBirthdate(pat.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
		ret.setCity(pat.getCity());
		ret.setFirstname(pat.getFirstName());
		ret.setGender(pat.getGender() == Gender.FEMALE ? "W" : "M");
		ret.setLastname(pat.getLastName());
		ret.setStreet1(pat.getStreet());
		ret.setZip(pat.getZip());
		ret.setTel(pat.getPhone1());
		return ret;
	}
	
	/**
	 * Change the information contained by this Object to
	 * the Information provided by the {@link Kontakt} or its subclasses.
	 * 
	 * @param kon
	 */
	public static ContactInfo fromKontakt(IContact kon){
		ContactInfo ret = new ContactInfo();
		if (kon.isOrganization()) {
			IOrganization org =
				CoreModelServiceHolder.get().load(kon.getId(), IOrganization.class).orElse(null);
			ret.setZip(org.getZip());
			ret.setCity(org.getCity());
			ret.setStreet1(org.getStreet());
			ret.setLastname(org.getDescription2());
			ret.setFirstname(org.getDescription1());
			ret.setTel(org.getPhone1());
		} else if (kon.isPerson()) {
			IPerson per =
				CoreModelServiceHolder.get().load(kon.getId(), IPerson.class).orElse(null);
			ret.setZip(per.getZip());
			ret.setCity(per.getCity());
			ret.setStreet1(per.getStreet());
			ret.setLastname(per.getLastName());
			ret.setFirstname(per.getFirstName());
			ret.setBirthdate(
				per.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			ret.setGender(per.getGender() == Gender.FEMALE ? "W" : "M");
			ret.setTel(per.getPhone1());
			ret.setTitle(per.getTitel());
		} else {
			ret.setZip(kon.getZip());
			ret.setCity(kon.getCity());
			ret.setStreet1(kon.getStreet());
			ret.setLastname(kon.getDescription1());
			ret.setFirstname(kon.getDescription2());
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
