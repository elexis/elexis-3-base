package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TarmedDefinitionenTest.class, TarmedLeistungTest.class, TarmedKumulationTest.class,
	TarmedOptifierTest.class, TarmedGroupTest.class
})
public class AllTarmedTests {
	
}
