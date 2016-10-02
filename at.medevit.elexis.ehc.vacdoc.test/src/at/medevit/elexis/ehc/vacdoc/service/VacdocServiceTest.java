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

public class VacdocServiceTest {
	
	@Test
	public void testImport() throws Exception{
		BufferedInputStream input =
			new BufferedInputStream(getClass().getResourceAsStream("/rsc/test.xml"));
		assertNotNull(input);
		VacdocService service = new VacdocService();
		Optional<CdaChVacd> document = service.getVacdocDocument(input);
		assertNotNull(document);
		assertTrue(document.isPresent());
		List<Immunization> immunizations = document.get().getImmunizations();
		assertNotNull(immunizations);
		assertEquals(3, immunizations.size());
	}
}
