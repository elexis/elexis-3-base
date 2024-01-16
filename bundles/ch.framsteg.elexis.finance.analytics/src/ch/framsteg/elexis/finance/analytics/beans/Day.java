package ch.framsteg.elexis.finance.analytics.beans;

import java.util.ArrayList;

public class Day {
	
	private String id;
	private String date;
	private ArrayList<Patient> patients;
	
	public Day() {
		setPatients(new ArrayList<Patient>());
	}
	
	public Day(String date) {
		setDate(date);
		setPatients(new ArrayList<Patient>());
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	public ArrayList<Patient> getPatients() {
		return patients;
	}

	public void setPatients(ArrayList<Patient> patients) {
		this.patients = patients;
	}	
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Date: ");
		stringBuilder.append(getDate());
		stringBuilder.append("\r");
		return stringBuilder.toString();
	}
}
