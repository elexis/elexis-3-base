package at.medevit.elexis.ehc.vacdoc.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import at.medevit.elexis.ehc.vacdoc.service.MeineImpfungenServiceTest;
import at.medevit.elexis.ehc.vacdoc.service.VacdocServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	VacdocServiceTest.class, MeineImpfungenServiceTest.class
})
public class AllTests {
	
}
