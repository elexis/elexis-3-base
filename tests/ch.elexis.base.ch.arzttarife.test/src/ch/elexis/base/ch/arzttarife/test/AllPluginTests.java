package ch.elexis.base.ch.arzttarife.test;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elexis.TarmedRechnung.XMLExporterTest;
import ch.elexis.TarmedRechnung.XMLExporterTiersTest;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IBillingSystemService;
import ch.elexis.core.utils.OsgiServiceUtil;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	XMLExporterTest.class, XMLExporterTiersTest.class
})
public class AllPluginTests {
	
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
	public static void beforeClass(){
		IReferenceDataImporter tarmedImporter =
			OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=tarmed_34)").get();
		tarmedImporter.performImport(new NullProgressMonitor(), AllPluginTests.class
			.getResourceAsStream("/rsc/TARMED_Datenbank_01.08.00_BR_UVG_IVG_MVG.mdb"), 171019);
		OsgiServiceUtil.ungetService(tarmedImporter);
		
		IBillingSystemService billingSystemService =
			OsgiServiceUtil.getService(IBillingSystemService.class).get();
		billingSystemService.addOrModifyBillingSystem(KVG_NAME, CONST_TARMED_LEISTUNG,
			CONST_TARMED_DRUCKER, KVG_REQUIREMENTS, BillingLaw.KVG);
		billingSystemService.addOrModifyBillingSystem(UVG_NAME, CONST_TARMED_LEISTUNG,
			CONST_TARMED_DRUCKER, UVG_REQUIREMENTS, BillingLaw.UVG);
		OsgiServiceUtil.ungetService(billingSystemService);
	}
}
