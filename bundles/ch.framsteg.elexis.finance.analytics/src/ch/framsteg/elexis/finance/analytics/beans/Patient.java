package ch.framsteg.elexis.finance.analytics.beans;

import java.util.ArrayList;

public class Patient {
	
	private String id;
	private int number;
	private String name;
	private String firstname;
	private String sex;
	private String birthday;
	
	
	private Day day;
	private String date;
	
	private ArrayList<Treatment> treatments;
	
	public Patient(String id) {
		setId(id);
		setTreatments(new ArrayList<Treatment>());
	}
	
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Treatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(ArrayList<Treatment> treatments) {
		this.treatments = treatments;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	
	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ID: ");
		stringBuilder.append(getId());
		stringBuilder.append("\r");
		stringBuilder.append("Name: ");
		stringBuilder.append(getName());
		stringBuilder.append("\r");
		stringBuilder.append("Vorname: ");
		stringBuilder.append(getFirstname());
		stringBuilder.append("\r");
		stringBuilder.append("Sex: ");
		stringBuilder.append(getSex());
		stringBuilder.append("\r");
		return stringBuilder.toString();
	}
}
