package at.medevit.elexis.cobasmira.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CobasMiraMessageTest.class, CobasMiraPatientResultTest.class, ImportPatientResultTest.class
})
public class AllTests {
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("CobasMira Tests");
	}
}
