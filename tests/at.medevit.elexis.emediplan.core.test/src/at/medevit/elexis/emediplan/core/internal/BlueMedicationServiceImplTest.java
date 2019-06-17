package at.medevit.elexis.emediplan.core.internal;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import at.medevit.elexis.emediplan.core.BlueMedicationService;
import at.medevit.elexis.emediplan.core.test.AllTests;
import ch.elexis.data.Patient;
import ch.rgw.tools.Result;

public class BlueMedicationServiceImplTest {
	
	private BlueMedicationService service;
	
	private ServiceReference<BlueMedicationService> serviceRef;
	
	private BundleContext bundleContext;
	
	@Before
	public void before() {
		bundleContext = FrameworkUtil.getBundle(BlueMedicationService.class).getBundleContext();
		serviceRef = bundleContext.getServiceReference(BlueMedicationService.class);
		assertNotNull(serviceRef);
		service = bundleContext.getService(serviceRef);
	}
	
	@After
	public void after(){
		bundleContext.ungetService(serviceRef);
		service = null;
	}
	
	@Ignore("Test needs active HIN")
	@Test
	public void uploadDocument() throws IOException{
		Patient patient = new Patient("Nachname", "Vorname", "01.01.1999", "M");
		Result result = service.uploadDocument(patient, AllTests.getAsFile("/rsc/test_spez.pdf"));
		assertTrue(result.isOK());
	}
}
