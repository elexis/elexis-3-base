package at.medevit.elexis.ehc.vacdoc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Medication;
import org.junit.Test;
import org.osgi.framework.ServiceReference;

import at.medevit.elexis.ehc.vacdoc.test.AllTests;
import ch.elexis.core.findings.util.fhir.MedicamentCoding;

public class VacdocServiceTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testImport() throws Exception{
		BufferedInputStream input =
				new BufferedInputStream(getClass().getResourceAsStream("/rsc/test.json"));
		assertNotNull(input);
		ServiceReference<VacdocService> serviceRef =
			(ServiceReference<VacdocService>) AllTests.getService(VacdocService.class);
		VacdocService service = AllTests.context.getService(serviceRef);
		Optional<Bundle> document = service.loadVacdocDocument(input);
		assertNotNull(document);
		assertTrue(document.isPresent());
		List<Immunization> immunizations = service.getImmunizations(document.get());
		assertNotNull(immunizations);
		assertEquals(1, immunizations.size());
		assertEquals("Havrix 1440", immunizations.get(0).getVaccineCode().getCodingFirstRep().getDisplay());
		AllTests.ungetService(serviceRef);
		Optional<Medication> medication = service.getMedication(immunizations.get(0));
		assertTrue(medication.isPresent());
		
		Optional<Coding> gtinCoding = medication.get().getCode().getCoding().stream()
				.filter(c -> MedicamentCoding.GTIN.isCodeSystemOf(c))
				.findFirst();
		assertTrue(gtinCoding.isPresent());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testImport1() throws Exception {
		BufferedInputStream input = new BufferedInputStream(getClass().getResourceAsStream("/rsc/test1.json"));
		assertNotNull(input);
		ServiceReference<VacdocService> serviceRef = (ServiceReference<VacdocService>) AllTests
				.getService(VacdocService.class);
		VacdocService service = AllTests.context.getService(serviceRef);
		Optional<Bundle> document = service.loadVacdocDocument(input);
		assertNotNull(document);
		assertTrue(document.isPresent());
		List<Immunization> immunizations = service.getImmunizations(document.get());
		assertNotNull(immunizations);
		assertEquals(2, immunizations.size());
		assertEquals("FSME-Immun CC", immunizations.get(0).getVaccineCode().getCodingFirstRep().getDisplay());
		AllTests.ungetService(serviceRef);
		Optional<Medication> medication = service.getMedication(immunizations.get(0));
		assertTrue(medication.isPresent());

		Optional<Coding> gtinCoding = medication.get().getCode().getCoding().stream()
				.filter(c -> MedicamentCoding.GTIN.isCodeSystemOf(c)).findFirst();
		assertTrue(gtinCoding.isPresent());
	}
}
