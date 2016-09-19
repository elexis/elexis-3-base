package at.medevit.elexis.ehc.vacdoc.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedInputStream;
import java.util.List;

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
		CdaChVacd document = service.getVacdocDocument(input);
		assertNotNull(document);
		List<Immunization> immunizations = document.getImmunizations();
		assertNotNull(immunizations);
		assertEquals(3, immunizations.size());
	}
}
