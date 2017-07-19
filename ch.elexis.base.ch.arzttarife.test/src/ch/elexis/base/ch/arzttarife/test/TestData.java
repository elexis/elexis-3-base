package ch.elexis.base.ch.arzttarife.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Eigenleistung;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.NamedBlob;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.TICode;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;
import ch.elexis.data.Xid;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Money;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class TestData {
	
	public static final String EXISTING_44_RNR = "4400";
	
	public static final String EXISTING_44_2_RNR = "4402";
	
	public static final String EXISTING_44_3_RNR = "4403";
	
	public static final String ERRONEOUS_44_1_RNR = "4412";
	
	public static String EXISTING_4_RNR = "4000";
	
	public static String EXISTING_4_2_RNR = "4002";
	
	public static String EXISTING_4_3_RNR = "4003";
	
	private static TestSzenario testSzenarioInstance = null;
	
	private static int patientCount = 0;
	
	public static TestSzenario getTestSzenarioInstance() throws IOException{
		if (testSzenarioInstance == null) {
			testSzenarioInstance = new TestSzenario();
		}
		
		return testSzenarioInstance;
	}
	
	public static class TestSzenario {
		List<Mandant> mandanten = new ArrayList<>();
		List<Patient> patienten = new ArrayList<>();
		List<Fall> faelle = new ArrayList<>();
		List<Konsultation> konsultationen = new ArrayList<>();
		List<IVerrechenbar> leistungen = new ArrayList<>();
		List<Rechnung> rechnungen = new ArrayList<>();
		
		TestSzenario() throws IOException{
			createMandanten();
			
			createPatientWithFall("Beatrice", "Spitzkiel", "14.04.1957", "w", false);
			createPatientWithFall("Karin", "Zirbelkiefer", "24.04.1951", "w", true);
			
			createLeistungen();
			
			for (int j = 0; j < faelle.size(); j++) {
				Konsultation kons = createKons(faelle.get(j), mandanten.get(0));
				konsultationen.add(kons);
				kons.addDiagnose(TICode.getFromCode("A1"));
				for (IVerrechenbar leistung : leistungen) {
					Result<IVerrechenbar> result = kons.addLeistung(leistung);
					if (!result.isOK()) {
						throw new IllegalStateException(result.toString());
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
			
			for (Fall fall : faelle) {
				List<Konsultation> kons =
					new ArrayList<>(Arrays.asList(fall.getBehandlungen(false)));
				Result<Rechnung> result = Rechnung.build(kons);
				if (result.isOK()) {
					rechnungen.add(result.get());
				} else {
					throw new IllegalStateException(result.toString());
				}
			}
			
			importExistingXml();
		}
		
		private void importExistingXml() throws IOException{
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
		}
		
		private void createLeistungen(){
			TarmedLeistung leistung = new TarmedLeistung("00", null, "NIL", "", "", "");
			leistung.setText("Grundleistungen");
			
			leistung =
				new TarmedLeistung("00.0010-20010101", "00.0010", "00", "9999", "FMH05", "0001");
			leistung.setText("Konsultation, erste 5 Min. (Grundkonsultation)");
			leistung.set(TarmedLeistung.FLD_GUELTIG_VON, "20010101");
			leistung.set(TarmedLeistung.FLD_GUELTIG_BIS, "21991231");
			
			Hashtable<String, String> ext = leistung.loadExtension();
			ext.put("LEISTUNG_TYP", "H");
			ext.put("SEITE", "0");
			ext.put("K_PFL", "01");
			ext.put("BEHANDLUNGSART", "N");
			ext.put("TP_AL", "9.57");
			ext.put("TP_ASSI", "0.0");
			ext.put("TP_TL", "8.19");
			ext.put("ANZ_ASSI", "0.0");
			ext.put("LSTGIMES_MIN", "5.0");
			ext.put("VBNB_MIN", "0.0");
			ext.put("BEFUND_MIN", "0.0");
			ext.put("RAUM_MIN", "5.0");
			ext.put("WECHSEL_MIN", "0.0");
			ext.put("F_AL", "1.0");
			ext.put("F_TL", "1.0");
			
			ext.put("limits", "<=,1.0,1,P,07#");
			ext.put("exclusion",
				"00.0060,00.0110,02.0010,02.0020,02.0030,02.0040,02.0050,08.0500,12");
			
			leistung.flushExtension();
			
			leistungen.add(leistung);
			
			// vat 8.00
			Eigenleistung eigenleistung =
				new Eigenleistung("GA", "Gutachten A", "270000", "270000");
			leistungen.add(eigenleistung);
			// vat 2.50
			eigenleistung = new Eigenleistung("GB", "Gutachten B", "250000", "250000");
			leistungen.add(eigenleistung);
		}
		
		public List<Mandant> getMandanten(){
			return mandanten;
		}
		
		public List<Patient> getPatienten(){
			return patienten;
		}
		
		public List<Fall> getFaelle(){
			return faelle;
		}
		
		public List<Konsultation> getKonsultationen(){
			return konsultationen;
		}
		
		private void createMandanten(){
			
			Mandant mandant = new Mandant("Mandant.tarmed", "Mandant.tarmed", "01.01.1900", "w");
			mandant.setLabel("mt");
			
			TarmedACL ta = TarmedACL.getInstance();
			mandant.setExtInfoStoredObjectByKey("Anrede", "Frau");
			mandant.setExtInfoStoredObjectByKey("Kanton", "AG");
			
			mandant.addXid(Xid.DOMAIN_EAN, "2000000000002", true);
			// make sure somains are registered
			TarmedRequirements.getEAN(mandant);
			
			mandant.addXid(TarmedRequirements.DOMAIN_KSK, "C000002", true);
			
			mandant.setExtInfoStoredObjectByKey(ta.ESR5OR9, "esr9");
			mandant.setExtInfoStoredObjectByKey(ta.ESRPLUS, "esr16or27");
			mandant.setExtInfoStoredObjectByKey(ta.LOCAL, "praxis");
			mandant.setExtInfoStoredObjectByKey(ta.KANTON, "AG");
			mandant.setExtInfoStoredObjectByKey(ta.SPEC, "Allgemein");
			mandant.setExtInfoStoredObjectByKey(ta.TIERS, "payant");
			
			mandant.setExtInfoStoredObjectByKey(ta.ESRNUMBER, "01-12648-2");
			mandant.setExtInfoStoredObjectByKey(ta.ESRSUB, "15453");
			
			mandanten.add(mandant);
			
			CoreHub.setMandant(mandant);
		}
		
		public Patient createPatientWithFall(String firstname, String lastname, String birthdate,
			String gender, boolean addKostentraeger){
			Patient pat = new Patient(lastname, firstname, birthdate, gender);
			addNextAHV(pat);
			patienten.add(pat);
			
			// move required fields to non required ... we are testing xml not Rechnung.build
			moveRequiredToOptional(Fall.getDefaultCaseLaw());
			
			Fall fall = pat.neuerFall(Fall.getDefaultCaseLabel(), Fall.getDefaultCaseReason(),
				Fall.getDefaultCaseLaw());
			if (addKostentraeger) {
				fall.setInfoElement("Kostenträger", pat.getId());
			}
			faelle.add(fall);
			return pat;
		}
		
		private void addNextAHV(Patient pat){
			String country = "756";
			String number = String.format("%09d", ++patientCount);
			StringBuilder ahvBuilder = new StringBuilder(country + number);
			ahvBuilder.append(getCheckNumber(ahvBuilder.toString()));
			
			pat.addXid(Xid.DOMAIN_AHV, ahvBuilder.toString(), true);
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
		
		private void moveRequiredToOptional(String defaultCaseLaw){
			String requirements = Fall.getRequirements(defaultCaseLaw);
			if (requirements != null) {
				CoreHub.globalCfg.set(Preferences.LEISTUNGSCODES_CFG_KEY + "/" //$NON-NLS-1$
					+ defaultCaseLaw + "/bedingungen", ""); //$NON-NLS-1$
				CoreHub.globalCfg.set(Preferences.LEISTUNGSCODES_CFG_KEY + "/" //$NON-NLS-1$
					+ defaultCaseLaw + "/fakultativ", requirements); //$NON-NLS-1$
			}
		}
		
		private Konsultation createKons(Fall fall, Mandant mandant){
			Konsultation kons = new Konsultation(fall);
			return kons;
		}
		
		public List<Rechnung> getRechnungen(){
			return rechnungen;
		}
		
		public Rechnung getExistingRechnung(String rechnungNr){
			Konsultation kons = createKons(faelle.get(0), mandanten.get(0));
			kons.addDiagnose(TICode.getFromCode("A1"));
			// add leistungen according to rsc/*.xml
			if (rechnungNr.equals(EXISTING_4_RNR) || rechnungNr.equals(EXISTING_4_2_RNR)
				|| rechnungNr.equals(EXISTING_4_3_RNR)) {
				for (IVerrechenbar leistung : leistungen) {
					if (leistung instanceof TarmedLeistung
						&& leistung.getCode().equals("00.0010")) {
						Result<IVerrechenbar> result = kons.addLeistung(leistung);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					}
				}
			} else if (rechnungNr.equals(EXISTING_44_RNR) || rechnungNr.equals(EXISTING_44_2_RNR)
				|| rechnungNr.equals(ERRONEOUS_44_1_RNR) || rechnungNr.equals(EXISTING_44_3_RNR)) {
				for (IVerrechenbar leistung : leistungen) {
					if (leistung instanceof TarmedLeistung
						&& leistung.getCode().equals("00.0010")) {
						Result<IVerrechenbar> result = kons.addLeistung(leistung);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					} else if (leistung instanceof Eigenleistung
						&& (leistung.getCode().equals("GA") || leistung.getCode().equals("GB"))) {
						Result<IVerrechenbar> result = kons.addLeistung(leistung);
						if (!result.isOK()) {
							throw new IllegalStateException(result.toString());
						}
					}
				}
				
			}
			Result<Rechnung> result = Rechnung.build(Collections.singletonList(kons));
			Rechnung ret = result.get();
			
			// add prepaid according to rsc/*.xml
			if (rechnungNr.equals(EXISTING_4_2_RNR)) {
				ret.addZahlung(new Money(10.00), "test", new TimeTool());
			} else if (rechnungNr.equals(EXISTING_44_2_RNR)
				|| rechnungNr.equals(ERRONEOUS_44_1_RNR)) {
				ret.addZahlung(new Money(4000.00), "test", new TimeTool());
			}
			
			ret.set(Rechnung.BILL_NUMBER, rechnungNr);
			
			return ret;
		}
	}
}
