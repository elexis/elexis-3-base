package at.medevit.elexis.emediplan.core.test;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;

import java.util.ArrayList;
import java.util.List;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.constants.ExtInfoConstants;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.IPrescriptionBuilder;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.elexis.core.types.ArticleSubTyp;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.TimeTool;

public class TestData {
	
	private static TestSzenario testSzenarioInstance = null;
	
	private static int patientCount = 0;
	
	private static IModelService artikelstammModelService;
	
	public static TestSzenario getTestSzenarioInstance(){
		if (testSzenarioInstance == null) {
			testSzenarioInstance = new TestSzenario();
		}
		return testSzenarioInstance;
	}
	
	public static class TestSzenario {
		private IMandator mandator;
		private List<IPatient> patients = new ArrayList<>();
		private IArtikelstammItem artikelstammAspirin;
		private IArtikelstammItem artikelstammIbuprofen;
		
		TestSzenario(){
			if (artikelstammModelService == null) {
				artikelstammModelService = OsgiServiceUtil
					.getService(IModelService.class,
						"(" + IModelService.SERVICEMODELNAME + "=at.medevit.ch.artikelstamm.model)")
					.get();
				
			}
			
			createMandanten();
			
			IPatient pat = createPatient("Beatrice", "Spitzkiel", "14.04.1957", "w");
			pat.setStreet("Testweg 14");
			pat.setZip("1234");
			pat.setCity("Testhausen");
			pat.setPhone1("+41 79 123 45 67");
			
			createPatient("Karin", "Zirbelkiefer", "24.04.1951", "w");
			
			createPrescriptions();
		}
		
		private void createPrescriptions(){
			artikelstammAspirin = artikelstammModelService.create(IArtikelstammItem.class);
			artikelstammAspirin.setTyp(ArticleTyp.ARTIKELSTAMM);
			artikelstammAspirin.setSubTyp(ArticleSubTyp.PHARMA);
			artikelstammAspirin.setGtin("7680336700282");
			artikelstammAspirin.setCode("58985");
			artikelstammAspirin.setName("ASPIRIN C Brausetabl 10 Stk");
			artikelstammModelService.save(artikelstammAspirin);
			
			artikelstammIbuprofen = artikelstammModelService.create(IArtikelstammItem.class);
			artikelstammIbuprofen.setTyp(ArticleTyp.ARTIKELSTAMM);
			artikelstammIbuprofen.setSubTyp(ArticleSubTyp.PHARMA);
			artikelstammIbuprofen.setGtin("");
			artikelstammIbuprofen.setCode("4881026");
			artikelstammIbuprofen.setName("IBUPROFEN Sandoz Filmtabl 400mg 20 Stk");
			artikelstammModelService.save(artikelstammIbuprofen);
			
			IPrescription prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(),
				null, artikelstammAspirin, patients.get(0), "1-1-1-1")
					.entryType(EntryType.FIXED_MEDICATION).buildAndSave();
			
			IArtikelstammItem item = artikelstammModelService.create(IArtikelstammItem.class);
			item.setTyp(ArticleTyp.ARTIKELSTAMM);
			item.setSubTyp(ArticleSubTyp.PHARMA);
			item.setGtin("7680475040157");
			item.setCode("1336630");
			item.setName("DAFALGAN Tabl 500 mg (16 Stk)");
			artikelstammModelService.save(item);
			
			prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(), null, item,
				patients.get(0), "1-1/2-1").entryType(EntryType.FIXED_MEDICATION)
					.remark("Anwendungsinstruktion").build();
			// Anwendungsinstruktion
			TimeTool endDate = new TimeTool();
			endDate.addDays(7);
			prescription.setDateTo(endDate.toLocalDateTime());
			CoreModelServiceHolder.get().save(prescription);
			
			item = artikelstammModelService.create(IArtikelstammItem.class);
			item.setTyp(ArticleTyp.ARTIKELSTAMM);
			item.setSubTyp(ArticleSubTyp.NONPHARMA);
			item.setGtin("7680565540017");
			item.setCode("3823877");
			item.setName("MORPHIN HCL 1 % Streuli Tropfen 10 mg/ml (20 ml)");
			artikelstammModelService.save(item);
			prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(), null, item,
				patients.get(0), "2-0-2").entryType(EntryType.FIXED_MEDICATION)
					.remark("Anwendungsinstruktion").buildAndSave();
			
			prescription =
				new IPrescriptionBuilder(CoreModelServiceHolder.get(), null, artikelstammIbuprofen,
					patients.get(0), "freetext dosis").entryType(EntryType.RESERVE_MEDICATION)
						.remark("Anwendungsinstruktion").buildAndSave();
			
			prescription = new IPrescriptionBuilder(CoreModelServiceHolder.get(), null,
				artikelstammAspirin, patients.get(1), "1/2-1/2-1/2-1/2")
					.entryType(EntryType.FIXED_MEDICATION).remark("Anwendungsinstruktion").build();
			prescription.setDisposalComment("Abgabekommentar");
			CoreModelServiceHolder.get().save(prescription);
			
			prescription =
				new IPrescriptionBuilder(CoreModelServiceHolder.get(), null, artikelstammIbuprofen,
					patients.get(1), "1/4-1/4-1/4").entryType(EntryType.SYMPTOMATIC_MEDICATION)
						.remark("Anwendungsinstruktion").build();
			prescription.setDisposalComment("Anwendungsgrund");
			CoreModelServiceHolder.get().save(prescription);
			
			prescription = MedicationServiceHolder.get().createPrescriptionCopy(prescription);
			prescription.setEntryType(EntryType.RECIPE);
			CoreModelServiceHolder.get().save(prescription);
			
			prescription = MedicationServiceHolder.get().createPrescriptionCopy(prescription);
			prescription.setEntryType(EntryType.SELF_DISPENSED);
			CoreModelServiceHolder.get().save(prescription);
		}
		
		public IMandator getMandator(){
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
		public List<IPatient> getPatients(){
			return patients;
		}
		
		private void createMandanten(){
			IPerson mandatorPerson =
				new IContactBuilder.PersonBuilder(CoreModelServiceHolder.get(), "Testinger",
					"Hannelore", new TimeTool("01.01.1999").toLocalDate(), Gender.fromValue("w"))
						.build();
			mandatorPerson.setMandator(true);
			mandatorPerson.setDescription3("mt");
			mandatorPerson.setStreet("Testweg 15");
			mandatorPerson.setZip("1234");
			mandatorPerson.setCity("Testhausen");
			mandatorPerson.setPhone1("+41 79 123 45 90");
			mandatorPerson.setTitel("Dr. med.");
			
			mandatorPerson.setExtInfo(ExtInfoConstants.ANREDE, "Frau");
			mandatorPerson.setExtInfo(ExtInfoConstants.KANTON, "AG");
			
			mandatorPerson.addXid(DOMAIN_EAN, "2000000000002", true);
			CoreModelServiceHolder.get().save(mandatorPerson);
			mandator =
				CoreModelServiceHolder.get().load(mandatorPerson.getId(), IMandator.class).get();
			ContextServiceHolder.get().setActiveMandator(mandator);
		}
		
		public IPatient createPatient(String firstname, String lastname, String birthdate,
			String gender){
			IPatient pat =
				new IContactBuilder.PatientBuilder(CoreModelServiceHolder.get(), firstname,
					lastname, new TimeTool(birthdate).toLocalDate(), Gender.fromValue(gender))
						.buildAndSave();
			addNextAHV(pat);
			patients.add(pat);
			return pat;
		}
		
		private void addNextAHV(IPatient pat){
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
