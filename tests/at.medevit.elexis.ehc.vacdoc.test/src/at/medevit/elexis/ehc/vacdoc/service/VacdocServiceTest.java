package at.medevit.elexis.ehc.vacdoc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.util.List;
import java.util.Optional;

import org.ehealth_connector.cda.ch.vacd.CdaChVacd;
import org.ehealth_connector.cda.ch.vacd.Immunization;
import org.junit.Test;
import org.osgi.framework.ServiceReference;

import at.medevit.elexis.ehc.vacdoc.test.AllTests;

public class VacdocServiceTest {
	
	@SuppressWarnings("unchecked")
	@Test
	public void testImport() throws Exception{
		BufferedInputStream input =
			new BufferedInputStream(getClass().getResourceAsStream("/rsc/test.xml"));
		assertNotNull(input);
		ServiceReference<VacdocService> serviceRef =
			(ServiceReference<VacdocService>) AllTests.getService(VacdocService.class);
		VacdocService service = AllTests.context.getService(serviceRef);
		Optional<CdaChVacd> document = service.loadVacdocDocument(input);
		assertNotNull(document);
		assertTrue(document.isPresent());
		List<Immunization> immunizations = document.get().getImmunizations();
		assertNotNull(immunizations);
		assertEquals(3, immunizations.size());
		AllTests.ungetService(serviceRef);
	}
}
