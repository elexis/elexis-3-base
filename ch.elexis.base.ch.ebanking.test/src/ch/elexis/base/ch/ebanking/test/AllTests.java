package ch.elexis.base.ch.ebanking.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.ebanking.esr.ESRFileTest;

@RunWith(Suite.class)
@SuiteClasses({
	ESRFileTest.class
})
public class AllTests {
	
	public static final String ESR_FILE_DIR = "";
	
}
