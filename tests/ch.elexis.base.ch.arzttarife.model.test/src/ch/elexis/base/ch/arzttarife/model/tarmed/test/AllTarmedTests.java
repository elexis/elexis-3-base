package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TarmedDefinitionenTest.class, TarmedLeistungTest.class, TarmedKumulationTest.class,
	TarmedLimitationTest.class, TarmedGroupTest.class, TarmedOptifierTest.class,
	TarmedBillingTest.class
})
public class AllTarmedTests {
	
}
