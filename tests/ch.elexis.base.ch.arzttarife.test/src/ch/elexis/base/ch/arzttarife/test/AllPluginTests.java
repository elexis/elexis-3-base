package ch.elexis.base.ch.arzttarife.test;

import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elexis.TarmedRechnung.XMLExporterTest;
import ch.elexis.TarmedRechnung.XMLExporterTiersTest;
import ch.elexis.base.ch.arzttarife.xml.exporter.Tarmed45ExporterTest;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IBillingSystemService;
import ch.elexis.core.utils.OsgiServiceUtil;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	XMLExporterTest.class, XMLExporterTiersTest.class, Tarmed45ExporterTest.class
})
public class AllPluginTests {
	
	public static final String UVG_NAME = Messages.Fall_UVG_Name;
	public static final String UVG_REQUIREMENTS = Messages.Fall_UVGRequirements;
	public static final String CONST_TARMED_DRUCKER = Messages.Fall_TarmedPrinter;
	public static final String CONST_TARMED_LEISTUNG = Messages.Fall_TarmedLeistung;
	
	@BeforeClass
	public static void beforeClass(){
		IReferenceDataImporter tarmedImporter =
			OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=tarmed_34)").get();
		IStatus status = tarmedImporter.performImport(new NullProgressMonitor(), AllPluginTests.class
			.getResourceAsStream("/rsc/TARMED_Datenbank_01.08.00_BR_UVG_IVG_MVG.mdb"), 171019);
		assertTrue(status.isOK());
		OsgiServiceUtil.ungetService(tarmedImporter);
		
		IBillingSystemService billingSystemService =
			OsgiServiceUtil.getService(IBillingSystemService.class).get();
		billingSystemService.addOrModifyBillingSystem(UVG_NAME, CONST_TARMED_LEISTUNG,
			CONST_TARMED_DRUCKER, UVG_REQUIREMENTS, BillingLaw.UVG);
		OsgiServiceUtil.ungetService(billingSystemService);
		
	}
}
