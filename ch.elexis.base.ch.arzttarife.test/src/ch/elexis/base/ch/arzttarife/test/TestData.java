package ch.elexis.base.ch.arzttarife.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import ch.elexis.TarmedRechnung.TarmedACL;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Rechnung;
import ch.elexis.data.TICode;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Xid;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.rgw.tools.Result;

public class TestData {
	
	private static TestSzenario testSzenarioInstance = null;
	
	public static TestSzenario getTestSzenarioInstance(){
		if (testSzenarioInstance == null) {
			testSzenarioInstance = new TestSzenario();
		}
		
		return testSzenarioInstance;
	}
	
	public static class TestSzenario {
		List<Mandant> mandanten = new ArrayList<Mandant>();
		List<Patient> patienten = new ArrayList<Patient>();
		List<Fall> faelle = new ArrayList<Fall>();
		List<Konsultation> konsultationen = new ArrayList<Konsultation>();
		List<TarmedLeistung> leistungen = new ArrayList<TarmedLeistung>();
		List<Rechnung> rechnungen = new ArrayList<Rechnung>();
		
		TestSzenario(){
			createMandanten();
			
			createPatientWithFall("Beatrice", "Spitzkiel", "14.04.1957", "w");
			createPatientWithFall("Karin", "Zirbelkiefer", "24.04.1951", "w");
			
			createLeistungen();
			
			for (int j = 0; j < faelle.size(); j++) {
				Konsultation kons = createKons(faelle.get(j), mandanten.get(0));
				konsultationen.add(kons);
				kons.addDiagnose(TICode.getFromCode("A1"));
				for (TarmedLeistung leistung : leistungen) {
					Result<IVerrechenbar> result = kons.addLeistung(leistung);
					if (!result.isOK()) {
						throw new IllegalStateException(result.toString());
					}
				}
			}
			
			for (Fall fall : faelle) {
				List<Konsultation> kons =
					new ArrayList<Konsultation>(Arrays.asList(fall.getBehandlungen(false)));
				Result<Rechnung> result = Rechnung.build(kons);
				if (result.isOK()) {
					rechnungen.add(result.get());
				} else {
					throw new IllegalStateException(result.toString());
				}
			}
			
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
			
			mandant.addXid(Xid.DOMAIN_EAN, "0000000000002", true);
			mandant.addXid(TarmedRequirements.DOMAIN_KSK, "0000000000002", true);
			
			mandant.setExtInfoStoredObjectByKey(ta.ESR5OR9, "esr9");
			mandant.setExtInfoStoredObjectByKey(ta.ESRPLUS, "esr16or27");
			mandant.setExtInfoStoredObjectByKey(ta.LOCAL, "praxis");
			mandant.setExtInfoStoredObjectByKey(ta.KANTON, "AG");
			mandant.setExtInfoStoredObjectByKey(ta.SPEC, "Allgemein");
			mandant.setExtInfoStoredObjectByKey(ta.TIERS, "payant");
			
			mandanten.add(mandant);
			
			CoreHub.setMandant(mandant);
		}
		
		public Patient createPatientWithFall(String firstname, String lastname, String birthdate,
			String gender){
			Patient pat = new Patient(lastname, firstname, birthdate, gender);
			patienten.add(pat);
			Fall fall =
				pat.neuerFall(Fall.getDefaultCaseLabel(), Fall.getDefaultCaseReason(),
					Fall.getDefaultCaseLaw());
			fall.setInfoElement("Kostenträger", pat.getId());
			faelle.add(fall);
			return pat;
		}
		
		private Konsultation createKons(Fall fall, Mandant mandant){
			Konsultation kons = new Konsultation(fall);
			return kons;
		}
		
		public List<Rechnung> getRechnungen(){
			return rechnungen;
		}
	}
}
