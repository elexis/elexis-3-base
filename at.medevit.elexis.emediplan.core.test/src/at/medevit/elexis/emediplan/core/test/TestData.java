package at.medevit.elexis.emediplan.core.test;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Prescription;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.rgw.tools.TimeTool;

public class TestData {
	
	private static TestSzenario testSzenarioInstance = null;
	
	private static int patientCount = 0;
	
	public static TestSzenario getTestSzenarioInstance(){
		if (testSzenarioInstance == null) {
			testSzenarioInstance = new TestSzenario();
		}
		
		return testSzenarioInstance;
	}
	
	public static class TestSzenario {
		private Mandant mandator;
		private List<Patient> patients = new ArrayList<Patient>();
		private ArtikelstammItem artikelstammItem;
		private BAGMedi bagMedi;
		
		TestSzenario(){
			createMandanten();
			
			Patient pat = createPatient("Beatrice", "Spitzkiel", "14.04.1957", "w");
			pat.set(Kontakt.FLD_STREET, "Testweg 14");
			pat.set(Kontakt.FLD_ZIP, "1234");
			pat.set(Kontakt.FLD_PLACE, "Testhausen");
			pat.set(Kontakt.FLD_PHONE1, "+41 79 123 45 67");
			createPatient("Karin", "Zirbelkiefer", "24.04.1951", "w");
			
			createPrescriptions();
			
		}
		
		private void createPrescriptions(){
			artikelstammItem = new ArtikelstammItem(0, TYPE.P, "7680336700282",
				BigInteger.valueOf(58985l), "ASPIRIN C Brausetabl 10 Stk", "");
			bagMedi = new BAGMedi("IBUPROFEN Actavis Filmtabl 400mg 20 Stk", "5390827", "", "");
			
			Prescription prescription =
				new Prescription(artikelstammItem, patients.get(0), "1-1-1-1", "");
			prescription.setEntryType(EntryType.FIXED_MEDICATION);
			
			ArtikelstammItem item = new ArtikelstammItem(0, TYPE.P, "7680475040157",
				BigInteger.valueOf(1336630l), "DAFALGAN Tabl 500 mg (16 Stk)", "");
			prescription = new Prescription(item, patients.get(0), "1-1/2-1", "");
			prescription.setEntryType(EntryType.FIXED_MEDICATION);
			// Einnahmevorschrift
			prescription.setBemerkung("Einnahmevorschrift");
			TimeTool endDate = new TimeTool();
			endDate.addDays(7);
			prescription.setEndDate(endDate.toString(TimeTool.TIMESTAMP));
			
			item = new ArtikelstammItem(0, TYPE.N, "7680565540017", BigInteger.valueOf(3823877l),
				"MORPHIN HCL 1 % Streuli Tropfen 10 mg/ml (20 ml)", "");
			prescription = new Prescription(item, patients.get(0), "2-0-2", "");
			prescription.setEntryType(EntryType.FIXED_MEDICATION);
			// Einnahmevorschrift
			prescription.setBemerkung("Einnahmevorschrift");
			
			prescription = new Prescription(bagMedi, patients.get(0), "freetext dosis", "");
			prescription.setEntryType(EntryType.RESERVE_MEDICATION);
			// Einnahmevorschrift
			prescription.setBemerkung("Einnahmevorschrift");
			
			prescription =
				new Prescription(artikelstammItem, patients.get(1), "1/2-1/2-1/2-1/2", "");
			// Abgabekommentar
			prescription.setDisposalComment("Abgabekommentar");
			prescription.setEntryType(EntryType.FIXED_MEDICATION);
			prescription =
				new Prescription(bagMedi, patients.get(1), "1/4-1/4-1/4", "");
			// Einnahmevorschrift
			prescription.setBemerkung("Einnahmevorschrift");
			// Abgabekommentar
			prescription.setDisposalComment("Abgabekommentar");
			prescription.setEntryType(EntryType.SYMPTOMATIC_MEDICATION);
			
			prescription = new Prescription(prescription);
			prescription.setEntryType(EntryType.RECIPE);
			
			prescription = new Prescription(prescription);
			prescription.setEntryType(EntryType.SELF_DISPENSED);
		}
		
		public Mandant getMandator(){
			return mandator;
		}
		
		/**
		 * Get the patients of the test scenario. <br />
		 * <br />
		 * 0) 2 prescriptions, 1 x fix "1-1-1-1", 1 x reserve "freetext dosis"<br />
		 * 1) 4 prescriptions, 1 x fix "1/2-1/2-1/2-1/2", 1 x symtomatic "1/4-1/4-1/4", 1 x recipe,
		 * 1 x self-dispensed<br />
		 * 
		 * @return
		 */
		public List<Patient> getPatients(){
			return patients;
		}
		
		private void createMandanten(){
			mandator = new Mandant("Testinger", "Hannelore", "01.01.1999", "w");
			mandator.setLabel("mt");
			mandator.set(Kontakt.FLD_STREET, "Testweg 15");
			mandator.set(Kontakt.FLD_ZIP, "1234");
			mandator.set(Kontakt.FLD_PLACE, "Testhausen");
			mandator.set(Kontakt.FLD_PHONE1, "+41 79 123 45 90");
			mandator.set(Person.TITLE, "Dr. med.");
			
			mandator.setExtInfoStoredObjectByKey("Anrede", "Frau");
			mandator.setExtInfoStoredObjectByKey("Kanton", "AG");
			
			mandator.addXid(DOMAIN_EAN, "2000000000002", true);
			
			CoreHub.setMandant(mandator);
		}
		
		public Patient createPatient(String firstname, String lastname, String birthdate,
			String gender){
			Patient pat = new Patient(lastname, firstname, birthdate, gender);
			addNextAHV(pat);
			patients.add(pat);
			return pat;
		}
		
		private void addNextAHV(Patient pat){
			String country = "756";
			String number = String.format("%09d", ++patientCount);
			StringBuilder ahvBuilder = new StringBuilder(country + number);
			ahvBuilder.append(getCheckNumber(ahvBuilder.toString()));
			
			pat.addXid(DOMAIN_AHV, ahvBuilder.toString(), true);
		}
		
		private String getCheckNumber(String string){
			int sum = 0;
			for (int i = 0; i < string.length(); i++) {
				// reveresd order
				char character = string.charAt((string.length() - 1) - i);
				int intValue = Character.getNumericValue(character);
				if (i % 2 == 0) {
					sum += intValue * 3;
				} else {
					sum += intValue;
				}
			}
			return Integer.toString(sum % 10);
		}
	}
}
