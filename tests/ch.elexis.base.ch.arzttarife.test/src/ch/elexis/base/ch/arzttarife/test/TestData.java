package ch.elexis.base.ch.arzttarife.test;

import static ch.elexis.core.constants.XidConstants.DOMAIN_AHV;
import static ch.elexis.core.constants.XidConstants.DOMAIN_EAN;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.ICustomService;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.model.IDocumentLetter;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IArticleBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.InvoiceServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.ArticleTyp;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.data.BillingSystem;
import ch.elexis.data.Eigenleistung;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.NamedBlob;
import ch.elexis.data.Organisation;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.Verrechnet;
import ch.elexis.data.Xid;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TestData {

	public static final String EXISTING_45_RNR = "4500";

	public static final String EXISTING_45_3_RNR = "4503";

	public static final String EXISTING_50_RNR = "5000";

	public static final String EXISTING_50_3_RNR = "5003";

	public static final String EXISTING_44_RNR = "4400";

	public static final String EXISTING_44_2_RNR = "4402";

	public static final String EXISTING_44_3_RNR = "4403";

	public static final String ERRONEOUS_44_1_RNR = "4412";

	public static String EXISTING_4_RNR = "4000";

	public static String EXISTING_4_2_RNR = "4002";

	public static String EXISTING_4_3_RNR = "4003";

	private static TestSzenario testSzenarioInstance = null;

	private static int patientCount = 0;

	public static TestSzenario getTestSzenarioInstance() throws IOException {
		if (testSzenarioInstance == null) {
			testSzenarioInstance = new TestSzenario();
			validateSzenario(testSzenarioInstance);
		}

		return testSzenarioInstance;
	}

	public static void disposeTestSzenarioInstance() {
		testSzenarioInstance.dispose();
		testSzenarioInstance = null;
	}

	private static void validateSzenario(TestSzenario szenario) {
		List<IInvoice> invoices = szenario.getInvoices();
		assertNotNull(invoices);
		assertFalse(invoices.isEmpty());
		for (IInvoice iInvoice : invoices) {
			// reload all invoices from db to get po changes
			CoreModelServiceHolder.get().refresh(iInvoice, true);
			List<IBilled> invoiceBilled = iInvoice.getBilled();
			List<IEncounter> encounters = iInvoice.getEncounters();
			assertFalse(encounters.isEmpty());
			List<IBilled> encounterBilled = encounters.stream().map(enc -> enc.getBilled()).flatMap(List::stream)
					.collect(Collectors.toList());
			assertEquals(encounterBilled.size(), invoiceBilled.size());
			for (IBilled iBilled : invoiceBilled) {
				assertFalse(iBilled.getText().isEmpty());
				if (iBilled.getText().equals("Gutachten A")) {
					String vatscale = (String) iBilled.getExtInfo(Verrechnet.VATSCALE);
					assertNotNull(vatscale);
					assertEquals("8.00", vatscale);
				} else if (iBilled.getText().equals("Gutachten B")) {
					String vatscale = (String) iBilled.getExtInfo(Verrechnet.VATSCALE);
					assertNotNull(vatscale);
					assertEquals("2.50", vatscale);
				}
			}
		}
	}

	public static class TestSzenario {
		List<Mandant> mandanten = new ArrayList<Mandant>();
		List<Patient> patienten = new ArrayList<Patient>();
		List<Fall> faelle = new ArrayList<Fall>();
		List<Konsultation> konsultationen = new ArrayList<Konsultation>();
		List<IBillable> leistungen = new ArrayList<IBillable>();
		List<IInvoice> invoices = new ArrayList<IInvoice>();

		TestSzenario() throws IOException {
			initBillingSystems();

			createMandanten();

			// disable strict billing for tests
			Optional<IContact> userContact = ContextServiceHolder.get().getActiveUserContact();
			assertTrue(userContact.isPresent());
			userContact.ifPresent(uc -> {
				ConfigServiceHolder.get().set(uc, ch.elexis.core.constants.Preferences.LEISTUNGSCODES_BILLING_STRICT,
						false);
			});

			List<Fall> _fall = createPatientWithFaelle("Beatrice", "Spitzkiel", "14.04.1957", "w", false, false);
			_fall.get(0).getPatient().set(Patient.FLD_PHONE1, "555-555 55 55");
			_fall.get(0).getPatient().set(Patient.FLD_MOBILEPHONE, "444-444 44 44");
			// load and refresh jpa cache after using PO set
			CoreModelServiceHolder.get().load(_fall.get(0).getPatient().getId(), IPatient.class);
			createPatientWithFaelle("Karin", "Zirbelkiefer", "24.04.1951", "w", true, false);

			createLeistungen();

			for (int j = 0; j < faelle.size(); j++) {
				Konsultation kons = createKons(faelle.get(j), mandanten.get(0));
				konsultationen.add(kons);
				IEncounter encounter = CoreModelServiceHolder.get().load(kons.getId(), IEncounter.class).get();
				encounter.addDiagnosis(getDiagnosis());
				CoreModelServiceHolder.get().save(encounter);
				assertEquals(1, encounter.getDiagnoses().size());
				for (IBillable leistung : leistungen) {
					if (leistung instanceof IArticle && ((IArticle) leistung).getName().endsWith("fractional")) {
						Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 0.5);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					} else {
						Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 1);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					}
				}
				// apply vat
				for (Verrechnet verrechnet : kons.getLeistungen()) {
					if (verrechnet.getVerrechenbar() instanceof Eigenleistung) {
						if ("GA".equals(verrechnet.getCode())) {
							verrechnet.setDetail(Verrechnet.VATSCALE, "8.00");
						}
						if ("GB".equals(verrechnet.getCode())) {
							verrechnet.setDetail(Verrechnet.VATSCALE, "2.50");
						}
					}
				}
			}

			createInvoices();

			importExistingXml();
		}

		public void dispose() {
			CoreModelServiceHolder.get().remove(invoices);
			konsultationen.forEach(k -> k.removeFromDatabase());
			faelle.forEach(f -> f.removeFromDatabase());
			patienten.forEach(p -> p.removeFromDatabase());
			mandanten.forEach(m -> m.removeFromDatabase());
		}

		private void createInvoices() throws IOException {
			for (Fall fall : faelle) {
				ICoverage coverage = CoreModelServiceHolder.get().load(fall.getId(), ICoverage.class).get();
				Result<IInvoice> result = InvoiceServiceHolder.get().invoice(coverage.getEncounters());
				if (result.isOK()) {
					invoices.add(result.get());
				} else {
					throw new IllegalStateException(result.toString());
				}
			}

			IInvoice invoice = invoices.get(0);
			IDocument document = CoreModelServiceHolder.get().create(IDocumentLetter.class);
			document.setPatient(invoice.getCoverage().getPatient());
			document.setTitle("testDocument_longerThan_35Characters_Title.pdf");
			document.setMimeType("application/pdf");

			try (InputStream resourceAsStream = getClass().getClassLoader()
					.getResourceAsStream("/rsc/testdocument.pdf")) {
				document.setContent(resourceAsStream);
			}

			CoreModelServiceHolder.get().save(document);

			invoice.addAttachment(document);
			CoreModelServiceHolder.get().save(invoice);
		}

		private void initBillingSystems() {
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/KVG/name", //$NON-NLS-1$
					BillingSystem.KVG_NAME);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/KVG/leistungscodes", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_LEISTUNG);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/KVG/standardausgabe", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_DRUCKER);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/KVG/bedingungen", //$NON-NLS-1$
					"Versicherungsnummer:T");
			BillingSystem.setConfigurationValue("KVG", BillingSystem.CFG_BILLINGLAW, BillingLaw.KVG.name());

			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/IV/name", //$NON-NLS-1$
					BillingSystem.IV_NAME);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/IV/leistungscodes", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_LEISTUNG);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/IV/standardausgabe", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_DRUCKER);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/IV/bedingungen", //$NON-NLS-1$
					"Fallnummer:T"); //$NON-NLS-1$
			BillingSystem.setConfigurationValue("IV", BillingSystem.CFG_BILLINGLAW, BillingLaw.IV.name());

			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/MV/name", //$NON-NLS-1$
					BillingSystem.MV_NAME);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/MV/leistungscodes", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_LEISTUNG);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/MV/standardausgabe", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_DRUCKER);
			BillingSystem.setConfigurationValue("MV", BillingSystem.CFG_BILLINGLAW, BillingLaw.MV.name());

			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/privat/name", //$NON-NLS-1$
					BillingSystem.PRIVATE_NAME);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/privat/leistungscodes", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_LEISTUNG);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/privat/standardausgabe", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_DRUCKER);
			BillingSystem.setConfigurationValue("privat", BillingSystem.CFG_BILLINGLAW, BillingLaw.VVG.name());
			BillingSystem.setConfigurationValue("privat", BillingSystem.CFG_NOCOSTBEARER, StringConstants.ONE);

			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/VVG/name", //$NON-NLS-1$
					BillingSystem.VVG_NAME);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/VVG/leistungscodes", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_LEISTUNG);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/VVG/standardausgabe", //$NON-NLS-1$
					BillingSystem.CONST_TARMED_DRUCKER);
			ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/VVG/bedingungen", //$NON-NLS-1$
					"Versicherungsnummer:T");
			BillingSystem.setConfigurationValue("VVG", BillingSystem.CFG_BILLINGLAW, BillingLaw.VVG.name());
		}

		private IDiagnosis getDiagnosis() {
			ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
			assertNotNull(codeElementService);
			ICodeElement loadFromString = codeElementService.loadFromString("TI-Code", "A1", null).get();
			OsgiServiceUtil.ungetService(codeElementService);
			return (IDiagnosis) loadFromString;
		}

		private void importExistingXml() throws IOException {
			InputStream xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing4_1.xml");
			StringWriter stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			NamedBlob blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_4_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing4_2.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_4_2_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing4_3.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_4_3_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing44_1.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_44_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing44_3.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_44_3_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing44_2.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_44_2_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/erroneous44_1.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + ERRONEOUS_44_1_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing45_1.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_45_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing45_3.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_45_3_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing50_1.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_50_RNR);
			blob.putString(stringWriter.toString());

			xmlIn = TestSzenario.class.getResourceAsStream("/rsc/existing50_3.xml");
			stringWriter = new StringWriter();
			IOUtils.copy(xmlIn, stringWriter, "UTF-8");
			blob = NamedBlob.load(XMLExporter.PREFIX + EXISTING_50_3_RNR);
			blob.putString(stringWriter.toString());
		}

		private void createLeistungen() {
			ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
			assertNotNull(codeElementService);
			ICodeElement loadedCode = codeElementService.loadFromString("Tarmed", "00.0010", null).get();
			assertTrue(loadedCode instanceof ITarmedLeistung);
			leistungen.add((IBillable) loadedCode);

			// vat 8.00
			Eigenleistung eigenleistung = new Eigenleistung("GA", "Gutachten A", "270000", "270000");
			ICustomService customService = CoreModelServiceHolder.get()
					.load(eigenleistung.getId(), ICustomService.class).get();
			leistungen.add(customService);
			// vat 2.50
			eigenleistung = new Eigenleistung("GB", "Gutachten B", "250000", "250000");
			customService = CoreModelServiceHolder.get().load(eigenleistung.getId(), ICustomService.class).get();
			leistungen.add(customService);

			IArticle localArticle = new IArticleBuilder(CoreModelServiceHolder.get(), "test article", "123456789",
					ArticleTyp.EIGENARTIKEL).build();
			localArticle.setGtin("0000001111111");
			localArticle.setPackageSize(12);
			localArticle.setSellingSize(12);
			localArticle.setPurchasePrice(new Money(8.15));
			localArticle.setSellingPrice(new Money(10.15));
			CoreModelServiceHolder.get().save(localArticle);
			leistungen.add(localArticle);

			localArticle = new IArticleBuilder(CoreModelServiceHolder.get(), "test article fractional", "1234567890",
					ArticleTyp.EIGENARTIKEL).build();
			localArticle.setGtin("0000001111112");
			localArticle.setPackageSize(12);
			localArticle.setSellingSize(12);
			localArticle.setPurchasePrice(new Money(8.15));
			localArticle.setSellingPrice(new Money(10.15));
			CoreModelServiceHolder.get().save(localArticle);
			leistungen.add(localArticle);
		}

		public List<Mandant> getMandanten() {
			return mandanten;
		}

		public List<Patient> getPatienten() {
			return patienten;
		}

		public List<Fall> getFaelle() {
			return faelle;
		}

		public List<Konsultation> getKonsultationen() {
			return konsultationen;
		}

		private void createMandanten() {

			Organisation bank = new Organisation("Test Bank", "Test");
			bank.set(Kontakt.FLD_STREET, "Bankweg 1");
			bank.set(Kontakt.FLD_ZIP, "2501");
			bank.set(Kontakt.FLD_PLACE, "Biel");

			Mandant mandant = new Mandant("Mandant.tarmed", "Mandant.tarmed", "01.01.1900", "w");
			mandant.setLabel("mt");

			TarmedACL ta = TarmedACL.getInstance();
			mandant.setExtInfoStoredObjectByKey("Anrede", "Frau");
			mandant.setExtInfoStoredObjectByKey("Kanton", "AG");

			mandant.addXid(DOMAIN_EAN, "2000000000002", true);
			Xid.localRegisterXIDDomainIfNotExists(TarmedRequirements.DOMAIN_KSK, "KSK/ZSR-Nr", Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
			mandant.addXid(TarmedRequirements.DOMAIN_KSK, "C000002", true);

			mandant.setExtInfoStoredObjectByKey(ta.ESR5OR9, "esr9");
			mandant.setExtInfoStoredObjectByKey(ta.ESRPLUS, "esr16or27");
			mandant.setExtInfoStoredObjectByKey(ta.LOCAL, "praxis");
			mandant.setExtInfoStoredObjectByKey(ta.KANTON, "AG");
			mandant.setExtInfoStoredObjectByKey(ta.SPEC, "Allgemein");
			mandant.setExtInfoStoredObjectByKey(ta.TIERS, "payant");

			mandant.setExtInfoStoredObjectByKey(ta.ESRNUMBER, "01-12648-2");
			mandant.setExtInfoStoredObjectByKey(ta.ESRSUB, "15453");

			mandant.setExtInfoStoredObjectByKey(ta.RNBANK, bank.getId());

			mandant.setExtInfoStoredObjectByKey("IBAN", "CH5800791123000889012");

			XidServiceHolder.get().localRegisterXIDDomainIfNotExists(TarmedRequirements.DOMAIN_NIF, "NIF",
					XidConstants.ASSIGNMENT_REGIONAL);
			TarmedRequirements.setNIF(NoPoUtil.loadAsIdentifiable(mandant, IContact.class).get(), "12345");

			mandanten.add(mandant);

			CoreHub.setMandant(mandant);

			// make sure domains are registered
			assertTrue(CoreModelServiceHolder.get() != null);
			Optional<IMandator> mandator = CoreModelServiceHolder.get().load(mandant.getId(), IMandator.class);
			assertTrue(mandator.isPresent());
			TarmedRequirements.getEAN(mandator.get());

			assertEquals("2000000000002", TarmedRequirements.getEAN(mandator.get()));

			Object extInfoStoredObjectByKey = mandant.getExtInfoStoredObjectByKey(ta.ESR5OR9);
			Object extInfo = mandator.get().getExtInfo(ta.ESR5OR9);
			assertEquals(extInfoStoredObjectByKey, extInfo);
		}

		public List<Fall> createPatientWithFall(String firstname, String lastname, String birthdate, String gender,
				boolean addKostentraeger) {
			return createPatientWithFaelle(firstname, lastname, birthdate, gender, addKostentraeger, true);
		}

		/**
		 *
		 * @param firstname
		 * @param lastname
		 * @param birthdate
		 * @param gender
		 * @param addKostentraeger set the cost bearer to the created patient
		 * @return
		 */
		public List<Fall> createPatientWithFaelle(String firstname, String lastname, String birthdate, String gender,
				boolean addKostentraeger, boolean onlyDefault) {
			Patient pat = new Patient(lastname, firstname, birthdate, gender);
			addNextAHV(pat);
			patienten.add(pat);
			String[] billingSystems = BillingSystem.getAbrechnungsSysteme();
			List<Fall> created = new ArrayList<Fall>();
			if (onlyDefault) {
				// move required fields to non required ... we are testing xml not
				// Rechnung.build
				moveRequiredToOptional(Fall.getDefaultCaseLaw());

				Fall fall = pat.neuerFall(Fall.getDefaultCaseLabel(), Fall.getDefaultCaseReason(),
						Fall.getDefaultCaseLaw());
				if (addKostentraeger) {
					fall.setCostBearer(pat);
				}
				fall.setExtInfoStoredObjectByKey(TarmedRequirements.CASE_NUMBER, "123456");

				faelle.add(fall);
				created.add(fall);
			} else {
				for (String string : billingSystems) {
					// move required fields to non required ... we are testing xml not
					// Rechnung.build
					moveRequiredToOptional(string);

					Fall fall = pat.neuerFall(Fall.getDefaultCaseLabel(), Fall.getDefaultCaseReason(), string);
					if (addKostentraeger) {
						fall.setCostBearer(pat);
					}
					fall.setExtInfoStoredObjectByKey(TarmedRequirements.CASE_NUMBER, "123456");

					faelle.add(fall);
					created.add(fall);
				}
			}
			return created;
		}

		private void addNextAHV(Patient pat) {
			String country = "756";
			String number = String.format("%09d", ++patientCount);
			StringBuilder ahvBuilder = new StringBuilder(country + number);
			ahvBuilder.append(getCheckNumber(ahvBuilder.toString()));

			pat.addXid(DOMAIN_AHV, ahvBuilder.toString(), true);
		}

		private String getCheckNumber(String string) {
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

		private void moveRequiredToOptional(String defaultCaseLaw) {
			String requirements = BillingSystem.getRequirements(defaultCaseLaw);
			if (requirements != null) {
				ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/" //$NON-NLS-1$
						+ defaultCaseLaw + "/bedingungen", ""); //$NON-NLS-1$
				ConfigServiceHolder.setGlobal(Preferences.LEISTUNGSCODES_CFG_KEY + "/" //$NON-NLS-1$
						+ defaultCaseLaw + "/fakultativ", requirements); //$NON-NLS-1$
			}
		}

		private Konsultation createKons(Fall fall, Mandant mandant) {
			Konsultation kons = new Konsultation(fall);
			// test expect xml 4.5, so set encounter date before 01.01.2026
			kons.setDateTime(LocalDateTime.of(2025, 12, 31, 8, 00), false);
			return kons;
		}

		public List<IInvoice> getInvoices() {
			return invoices;
		}

		public Rechnung getExistingRechnung(String rechnungNr) {
			Konsultation kons = createKons(faelle.get(0), mandanten.get(0));
			IEncounter encounter = CoreModelServiceHolder.get().load(kons.getId(), IEncounter.class).get();
			encounter.addDiagnosis(getDiagnosis());
			CoreModelServiceHolder.get().save(encounter);
			// add leistungen according to rsc/*.xml
			if (rechnungNr.equals(EXISTING_4_RNR) || rechnungNr.equals(EXISTING_4_2_RNR)
					|| rechnungNr.equals(EXISTING_4_3_RNR)) {
				for (IBillable leistung : leistungen) {
					if (leistung instanceof ITarmedLeistung && leistung.getCode().equals("00.0010")) {
						Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 1);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					}
				}
			} else if (rechnungNr.equals(EXISTING_44_RNR) || rechnungNr.equals(EXISTING_44_2_RNR)
					|| rechnungNr.equals(ERRONEOUS_44_1_RNR) || rechnungNr.equals(EXISTING_44_3_RNR)) {
				for (IBillable leistung : leistungen) {
					if (leistung instanceof ITarmedLeistung && leistung.getCode().equals("00.0010")) {
						Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 1);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					} else if (leistung instanceof ICustomService
							&& (leistung.getCode().equals("GA") || leistung.getCode().equals("GB"))) {
						Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 1);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					}
				}
			} else if (rechnungNr.equals(EXISTING_45_RNR) || rechnungNr.equals(EXISTING_45_3_RNR)) {
				for (IBillable leistung : leistungen) {
					Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 1);
					if (!result.isOK()) {
						throw new IllegalStateException(result.toString());
					}
				}
			} else if (rechnungNr.equals(EXISTING_50_RNR) || rechnungNr.equals(EXISTING_50_3_RNR)) {
				for (IBillable leistung : leistungen) {
					Result<IBilled> result = BillingServiceHolder.get().bill(leistung, encounter, 1);
					if (!result.isOK()) {
						throw new IllegalStateException(result.toString());
					}
				}
			}
			Result<IInvoice> result = InvoiceServiceHolder.get()
					.invoice(NoPoUtil.loadAsIdentifiable(Collections.singletonList(kons), IEncounter.class));
			assertTrue(result.toString(), result.isOK());
			Rechnung ret = Rechnung.load(result.get().getId());

			// add prepaid according to rsc/*.xml
			if (rechnungNr.equals(EXISTING_4_2_RNR)) {
				ret.addZahlung(new Money(10.00), "test", new TimeTool());
			} else if (rechnungNr.equals(EXISTING_44_2_RNR) || rechnungNr.equals(ERRONEOUS_44_1_RNR)) {
				ret.addZahlung(new Money(4000.00), "test", new TimeTool());
			}

			ret.set(Rechnung.BILL_NUMBER, rechnungNr);

			return ret;
		}
	}

	public static Document loadXml(String resource) throws IOException, JDOMException {
		try (InputStream input = TestData.class.getResourceAsStream(resource)) {
			String text = IOUtils.toString(input, "UTF-8");
			SAXBuilder builder = new SAXBuilder();
			Document ret = builder.build(new StringReader(text));
			return ret;
		}
	}

	public static void removeExistingXml(IInvoice invoice) {
		if (invoice.getNumber().length() < 3) {
			NamedBlob blob = NamedBlob.load(XMLExporter.PREFIX + invoice.getNumber());
			if (blob.exists()) {
				blob.delete();
			}
		}
	}

	public static IBlob createTestInvoiceBlobWithXml(String invoiceNumber, String resource) throws IOException {
		InputStream xmlIn = TestSzenario.class.getResourceAsStream(resource);
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(xmlIn, stringWriter, "UTF-8");

		IBlob blob = CoreModelServiceHolder.get().load(XMLExporter.PREFIX + invoiceNumber, IBlob.class)
				.orElseGet(() -> {
					IBlob newBlob = CoreModelServiceHolder.get().create(IBlob.class);
					newBlob.setId(XMLExporter.PREFIX + invoiceNumber);
					return newBlob;
				});
		blob.setStringContent(stringWriter.toString());
		CoreModelServiceHolder.get().save(blob);
		return blob;
	}
}
