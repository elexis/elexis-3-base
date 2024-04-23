package ch.elexis.fire.core;

import static org.junit.Assert.assertNotNull;

import org.hl7.fhir.r4.model.Bundle;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.utils.OsgiServiceUtil;

public class FIREServiceTest {

	private static IFIREService fireService;
	
	@BeforeClass
	public static void beforeClass() {
		fireService = OsgiServiceUtil.getService(IFIREService.class).get();
	}

	@Test
	public void initialExport() {
		Bundle bundle = fireService.initialExport();

		assertNotNull(bundle);
		
		String bundleJson = ModelUtil.getFhirJson(bundle);
		assertNotNull(bundleJson);
		System.out.println(bundleJson);
	}
}
