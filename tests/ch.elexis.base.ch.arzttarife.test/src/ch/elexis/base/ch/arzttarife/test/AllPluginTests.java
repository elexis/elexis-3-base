package ch.elexis.base.ch.arzttarife.test;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elexis.TarmedRechnung.XMLExporterTest;
import ch.elexis.TarmedRechnung.XMLExporterTiersTest;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.utils.OsgiServiceUtil;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	XMLExporterTest.class, XMLExporterTiersTest.class
})
public class AllPluginTests {
	
	@BeforeClass
	public static void beforeClass(){
		IReferenceDataImporter tarmedImporter =
			OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=tarmed_34)").get();
		tarmedImporter.performImport(new NullProgressMonitor(), AllPluginTests.class
			.getResourceAsStream("/rsc/TARMED_Datenbank_01.08.00_BR_UVG_IVG_MVG.mdb"), 171019);
		OsgiServiceUtil.ungetService(tarmedImporter);
	}
}
