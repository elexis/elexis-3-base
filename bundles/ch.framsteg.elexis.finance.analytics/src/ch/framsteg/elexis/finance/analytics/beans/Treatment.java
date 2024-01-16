package ch.framsteg.elexis.finance.analytics.beans;

import java.util.ArrayList;

public class Treatment {

	private String id;
	private String patientId;
	private String billingNumber;
	private String billingAmount;
	private String billingId;
	private String caseId;
	private ArrayList<Delivery> deliveries;
	private Patient patient;

	public Treatment() {
		setDeliveries(new ArrayList<Delivery>());
	}

	public Treatment(String id) {
		setId(id);
		setDeliveries(new ArrayList<Delivery>());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void assignChild() {

	}

	public ArrayList<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(ArrayList<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getParentId() {
		return getPatientId();
	}

	public String getBillingNumber() {
		return billingNumber;
	}

	public void setBillingNumber(String billingNumber) {
		this.billingNumber = billingNumber;
	}

	public String getBillingAmount() {
		return billingAmount;
	}

	public void setBillingAmount(String billingAmount) {
		this.billingAmount = billingAmount;
	}

	public String getBillingId() {
		return billingId;
	}

	public void setBillingId(String billingId) {
		this.billingId = billingId;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("ID: ");
		stringBuilder.append(getId());
		stringBuilder.append("\r");
		stringBuilder.append("Patient: ");
		stringBuilder.append(getPatientId());
		stringBuilder.append("\r");
		stringBuilder.append("Billing Number: ");
		stringBuilder.append(getBillingNumber());
		stringBuilder.append("\r");
		stringBuilder.append("Billing Amount: ");
		stringBuilder.append(getBillingAmount());
		stringBuilder.append("\r");
		stringBuilder.append("Billing ID: ");
		stringBuilder.append(getBillingId());
		stringBuilder.append("\r");
		stringBuilder.append("Case ID: ");
		stringBuilder.append(getCaseId());
		stringBuilder.append("\r");
		return stringBuilder.toString();
	}
}
