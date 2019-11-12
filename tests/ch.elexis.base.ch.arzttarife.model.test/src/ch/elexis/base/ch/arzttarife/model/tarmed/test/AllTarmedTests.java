package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.elexis.service.test.IEncounterServiceTest;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IBillingSystemService;
import ch.elexis.core.utils.OsgiServiceUtil;

@RunWith(Suite.class)
@SuiteClasses({
	TarmedDefinitionenTest.class, TarmedLeistungTest.class, TarmedKumulationTest.class,
	TarmedLimitationTest.class, TarmedGroupTest.class, TarmedOptifierTest.class,
	TarmedBillingTest.class
})
public class AllTarmedTests {
	
	public static final String KVG_NAME = Messages.Fall_KVG_Name;
	public static final String UVG_NAME = Messages.Fall_UVG_Name;
	public static final String MV_NAME = Messages.Fall_MV_Name;
	public static final String IV_NAME = Messages.Fall_IV_Name;
	private static final String KVG_REQUIREMENTS = Messages.Fall_KVGRequirements; //$NON-NLS-1$
	public static final String UVG_REQUIREMENTS = Messages.Fall_UVGRequirements; //$NON-NLS-1$
	public static final String CONST_TARMED_DRUCKER = Messages.Fall_TarmedPrinter; //$NON-NLS-1$
	public static final String CONST_TARMED_LEISTUNG = Messages.Fall_TarmedLeistung; //$NON-NLS-1$
	public static final String VVG_NAME = Messages.Fall_VVG_Name;
	public static final String PRIVATE_NAME = Messages.Fall_Private_Name; //$NON-NLS-1$	
	
	@BeforeClass
	public static void beforeClass() {
		// initialize the required billing systems
		IBillingSystemService billingSystemService = OsgiServiceUtil.getService(IBillingSystemService.class).get();
		billingSystemService.addOrModifyBillingSystem(KVG_NAME, CONST_TARMED_LEISTUNG, CONST_TARMED_DRUCKER, KVG_REQUIREMENTS, BillingLaw.KVG);
		billingSystemService.addOrModifyBillingSystem(UVG_NAME, CONST_TARMED_LEISTUNG, CONST_TARMED_DRUCKER, UVG_REQUIREMENTS, BillingLaw.UVG);
	}
	
}
