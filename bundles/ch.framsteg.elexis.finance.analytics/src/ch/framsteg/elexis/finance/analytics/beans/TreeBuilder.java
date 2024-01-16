package ch.framsteg.elexis.finance.analytics.beans;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class TreeBuilder {

	private final static int DATE = 0;
	private final static int PATIENT_NUMBER = 1;
	private final static int PATIENT_NAME = 2;
	private final static int PATIENT_FIRSTNAME = 3;
	private final static int PATIENT_SEX = 4;
	private final static int BILLING_NUMBER = 5;
	private final static int BILLING_AMOOUNT = 6;
	private final static int DELIVERY_CLASS = 7;
	private final static int DELIVERY_DESCRIPTION = 8;
	private final static int DELIVERY_POINTS = 9;
	private final static int CLEARING_FACTOR = 10;
	private final static int CLEARING_SCALE = 11;
	private final static int CLEARING_PRICE = 12;
	private final static int DELIVERY_CODE = 13;
	private final static int TREATMENT_ID = 14;
	private final static int CASE_ID = 15;
	private final static int BILLING_ID = 16;
	private final static int ID = 17;
	private final static int PATIENT_BIRTHDAY = 18;

	TreeMap<String, Day> days = new TreeMap<String, Day>();
	TreeMap<String, Patient> patients = new TreeMap<String, Patient>();
	TreeMap<String, Treatment> treatments = new TreeMap<String, Treatment>();
	TreeMap<String, Delivery> deliveries = new TreeMap<String, Delivery>();

	public void buildTree(ArrayList<String[]> lines) {
		for (String[] line : lines) {
			Delivery delivery = new Delivery(line);
			delivery.setTreatmentId(line[TREATMENT_ID]);
			delivery.setDeliveryCode(line[DELIVERY_CODE]);
			delivery.setDeliveryClass(line[DELIVERY_CLASS]);
			delivery.setDeliveryDescription(line[DELIVERY_DESCRIPTION]);
			delivery.setDeliveryPoints(Integer.parseInt(line[DELIVERY_POINTS]));
			delivery.setClearingFactor(Float.parseFloat(line[CLEARING_FACTOR]));
			delivery.setClearingScale(Float.parseFloat(line[CLEARING_SCALE]));
			delivery.setClearingPrice(new BigDecimal(line[CLEARING_PRICE]));
			delivery.setId(line[ID]);
			deliveries.put(delivery.getId(), delivery);
		}
	}

	public void buildHierarchy(ArrayList<String[]> lines) {
		for (String[] line : lines) {
			boolean treatmentExists = false;
			boolean patientExists = false;
			boolean dayExists=false;
			Delivery delivery = new Delivery(line);
			delivery.setDeliveryCode(line[DELIVERY_CODE]);
			delivery.setDeliveryClass(line[DELIVERY_CLASS]);
			delivery.setDeliveryDescription(line[DELIVERY_DESCRIPTION]);
			delivery.setDeliveryPoints(Integer.valueOf(line[DELIVERY_POINTS]));
			delivery.setClearingFactor(Float.valueOf(line[CLEARING_FACTOR]));
			delivery.setClearingScale(Float.valueOf(line[CLEARING_SCALE]));
			delivery.setClearingPrice(new BigDecimal(line[CLEARING_PRICE]));
			deliveries.put(delivery.getId(), delivery);
			for (Map.Entry<String, Treatment> treatments_entry : treatments.entrySet()) {
				if (treatments_entry.getKey().equalsIgnoreCase(delivery.getParentId())) {
					treatments_entry.getValue().getDeliveries().add(delivery);
					delivery.setTreatment(treatments_entry.getValue());
					treatmentExists = true;
					break;
				}
			}
			if (!treatmentExists) {
				Treatment treatment = new Treatment(delivery.getParentId());
				treatment.setPatientId(line[PATIENT_NUMBER]);
				treatment.setBillingNumber(line[BILLING_NUMBER]);
				treatment.setBillingAmount(line[BILLING_AMOOUNT]);
				treatment.setBillingId(line[BILLING_ID]);
				treatment.setCaseId(line[CASE_ID]);
				treatment.getDeliveries().add(delivery);
				treatments.put(treatment.getId(), treatment);
				for (Map.Entry<String, Patient> patients_entry : patients.entrySet()) {
					if (patients_entry.getKey().equalsIgnoreCase(treatment.getParentId())) {
						patients_entry.getValue().getTreatments().add(treatment);
						treatment.setPatient(patients_entry.getValue());
						patientExists=true;
						break;
					}
				}
				if (!patientExists) {
					Patient patient = new Patient(treatment.getParentId());
					patient.setDate(line[DATE]);
					patient.setName(line[PATIENT_NAME]);
					patient.setFirstname(line[PATIENT_FIRSTNAME]);
					patient.setSex(line[PATIENT_SEX]);
					patient.setBirthday(line[PATIENT_BIRTHDAY]);
					patient.getTreatments().add(treatment);
					patients.put(patient.getId(), patient);
					for (Map.Entry<String, Day> days_entry : days.entrySet()) {
						if (days_entry.getKey().equalsIgnoreCase(patient.getDate())) {
							days_entry.getValue().getPatients().add(patient);
							patient.setDay(days_entry.getValue());
							
							dayExists=true;
							break;
						}
					}
					if (!dayExists) {
						Day day = new Day(line[DATE]);
						day.getPatients().add(patient);
						days.put(line[DATE], day);
					}
				}
			}
		}
	}

	public TreeMap<String, Day> getHierarchy() {
		return days;
	}
}
