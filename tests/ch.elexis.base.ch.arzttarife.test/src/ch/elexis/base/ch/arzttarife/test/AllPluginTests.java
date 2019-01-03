package ch.elexis.base.ch.arzttarife.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import ch.elexis.TarmedRechnung.XMLExporterTest;
import ch.elexis.TarmedRechnung.XMLExporterTiersTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	XMLExporterTest.class, XMLExporterTiersTest.class
})
public class AllPluginTests {
	
}
