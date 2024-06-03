package ch.framsteg.elexis.covercard.dao;

import ch.framsteg.elexis.covercard.utilities.Capitalizer;

public class PatientInfoData {

	private String prename;
	private String name;
	private String birthday;
	private String sex;
	private String address;
	private String zip;
	private String location;
	private String cardholderIdentifier;
	private String insuredNumber;
	private String cardNumber;
	private String insuredPersonNumber;
	private String okpBsv;
	private String okpEan;
	private String vvgBsv;
	private String vvgEan;

	public String getPrename() {
		return Capitalizer.capitalize(prename);
	}

	public void setPrename(String prename) {
		this.prename = prename;
	}

	public String getName() {
		return Capitalizer.capitalize(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getSex() {
		return sex.toLowerCase();
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCardholderIdentifier() {
		return cardholderIdentifier;
	}

	public void setCardholderIdentifier(String cardholderIdentifier) {
		this.cardholderIdentifier = cardholderIdentifier;
	}

	public String getInsuredNumber() {
		return insuredNumber;
	}

	public void setInsuredNumber(String insuredNumber) {
		this.insuredNumber = insuredNumber;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getInsuredPersonNumber() {
		return insuredPersonNumber;
	}

	public void setInsuredPersonNumber(String insuredPersonNumber) {
		this.insuredPersonNumber = insuredPersonNumber;
	}

	public String getOkpBsv() {
		return okpBsv;
	}

	public void setOkpBsv(String okpBsv) {
		this.okpBsv = okpBsv;
	}

	public String getOkpEan() {
		return okpEan;
	}

	public void setOkpEan(String okpEan) {
		this.okpEan = okpEan;
	}

	public String getVvgBsv() {
		return vvgBsv;
	}

	public void setVvgBsv(String vvgBsv) {
		this.vvgBsv = vvgBsv;
	}

	public String getVvgEan() {
		return vvgEan;
	}

	public void setVvgEan(String vvgEan) {
		this.vvgEan = vvgEan;
	}
}
