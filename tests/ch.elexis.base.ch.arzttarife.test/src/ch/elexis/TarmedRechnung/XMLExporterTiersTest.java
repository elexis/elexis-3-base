package ch.elexis.TarmedRechnung;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;

import org.junit.Test;

import ch.elexis.base.ch.arzttarife.test.TestData;
import ch.elexis.base.ch.arzttarife.test.TestData.TestSzenario;
import ch.elexis.data.Fall;
import ch.elexis.data.Fall.Tiers;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Organisation;
import ch.elexis.data.Person;

public class XMLExporterTiersTest {
	
	@Test
	public void testGetGuarantor() throws IOException{
		TestSzenario szenario = TestData.getTestSzenarioInstance();
		
		Person legalGuardian = new Person("Legal", "Guardian", "14.04.1927", "m");
		Organisation insurer = new Organisation("ACME Insurance", "We keep you healthy");
		
		// TP mit gesetzlichem Vertreter -> GV
		Fall TP_GV = szenario.createPatientWithFall("TP", "TP_GV", "1.1.1980", "m", false);
		TP_GV.setCostBearer(insurer);
		TP_GV.setGarant(insurer);
		TP_GV.getPatient().setLegalGuardian(legalGuardian);
		assertEquals(Tiers.PAYANT, TP_GV.getTiersType());
		Kontakt guarantor =
			XMLExporterTiers.getGuarantor(XMLExporter.TIERS_PAYANT, TP_GV.getPatient(), TP_GV);
		assertEquals(legalGuardian, guarantor);
		
		// TP ohne gesetzlichen Vertreter -> Patient
		Fall TP_NOGV = szenario.createPatientWithFall("TP", "TP_NOGV", "1.1.1980", "m", false);
		TP_NOGV.setCostBearer(insurer);
		TP_NOGV.setGarant(insurer);
		assertEquals(Tiers.PAYANT, TP_NOGV.getTiersType());
		guarantor =
			XMLExporterTiers.getGuarantor(XMLExporter.TIERS_PAYANT, TP_NOGV.getPatient(), TP_NOGV);
		assertEquals(TP_NOGV.getPatient(), guarantor);
		
		// TG mit Rechnungsempfaenger (Garant) GLEICH Patient  -------
		// Gesetzlicher Vertreter definiert -> GV
		Fall TG_REisPat_GV =
			szenario.createPatientWithFall("TG", "TG_REisPat_GV", "14.04.1957", "w", true);
		TG_REisPat_GV.getPatient().setLegalGuardian(legalGuardian);
		assertEquals(Tiers.GARANT, TG_REisPat_GV.getTiersType());
		assertEquals(TG_REisPat_GV.getPatient(), TG_REisPat_GV.getGarant());
		guarantor = XMLExporterTiers.getGuarantor(XMLExporter.TIERS_GARANT,
			TG_REisPat_GV.getPatient(), TG_REisPat_GV);
		assertEquals(legalGuardian, guarantor);
		
		// Kein Gesetzlicher Vertreter definiert -> Patient
		Fall TG_REisPat_NOGV =
			szenario.createPatientWithFall("TG", "TG_REisPat_NOGV", "14.04.1957", "w", true);
		assertEquals(Tiers.GARANT, TG_REisPat_NOGV.getTiersType());
		assertEquals(TG_REisPat_NOGV.getPatient(), TG_REisPat_NOGV.getGarant());
		guarantor = XMLExporterTiers.getGuarantor(XMLExporter.TIERS_GARANT,
			TG_REisPat_NOGV.getPatient(), TG_REisPat_NOGV);
		assertEquals(TG_REisPat_NOGV.getPatient(), guarantor);
		
		// TG mit Rechnungsempfaenger (Garant) ungleich Patient -> Rechnungsempfaenger (Garant)
		Fall TG_REisnotPat =
			szenario.createPatientWithFall("TG", "TG_REisnotPat", "14.04.1957", "w", true);
		TG_REisnotPat.setGarant(insurer);
		assertNotEquals(TG_REisnotPat.getPatient(), TG_REisnotPat.getGarant());
		assertEquals(Tiers.GARANT, TG_REisnotPat.getTiersType());
		guarantor = XMLExporterTiers.getGuarantor(XMLExporter.TIERS_GARANT,
			TG_REisnotPat.getPatient(), TG_REisnotPat);
		assertEquals(TG_REisnotPat.getGarant(), guarantor);
	}
	
}
