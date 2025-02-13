package org.iatrix.bestellung.rose.test.gs1;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.utils.OsgiServiceUtil;


@RunWith(Suite.class)
@SuiteClasses({ BestellungRoseGS1Test.class })
public class AllTests {

	private static IModelService coreModelService;
	private static IConfigService configService;


	@BeforeClass
	public static void beforeClass() {

		coreModelService = OsgiServiceUtil
				.getService(IModelService.class, "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();

		configService = OsgiServiceUtil.getService(IConfigService.class).get();
	}


	@Test
	public void testServicesNotNull() {
		assertNotNull("coreModelService ist null!", coreModelService);
		assertNotNull("configService ist null!", configService);
	}

	/**
	 * Getter, aufrufbar in anderen Testklassen:
	 */
	public static IModelService getModelService() {
		return coreModelService;
	}

	public static IConfigService getConfigService() {
		return configService;
	}
}
