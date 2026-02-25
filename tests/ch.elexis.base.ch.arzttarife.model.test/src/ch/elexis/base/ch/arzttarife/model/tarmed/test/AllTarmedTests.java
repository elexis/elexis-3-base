package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.elexis.service.test.IEncounterServiceTest;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IBillingSystemService;

@RunWith(Suite.class)
@SuiteClasses({ TarmedDefinitionenTest.class, TarmedLeistungTest.class, TarmedKumulationTest.class,
		TarmedLimitationTest.class, TarmedGroupTest.class, TarmedOptifierTest.class, TarmedBillingTest.class,
		IEncounterServiceTest.class })
public class AllTarmedTests {

	public static final String UVG_NAME = Messages.Case_UVG_Short;
	public static final String UVG_REQUIREMENTS = Messages.Fall_UVGRequirements;
	public static final String CONST_TARMED_DRUCKER = Messages.Fall_TarmedPrinter;
	public static final String CONST_TARMED_LEISTUNG = Messages.Fall_TarmedLeistung;

	@BeforeClass
	public static void beforeClass() {
		// initialize the required billing systems
		IBillingSystemService billingSystemService = OsgiServiceUtil.getService(IBillingSystemService.class).get();
		billingSystemService.addOrModifyBillingSystem(UVG_NAME, CONST_TARMED_DRUCKER, UVG_REQUIREMENTS, BillingLaw.UVG);
	}

}
