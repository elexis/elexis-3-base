package ch.elexis.externe_dokumente;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	ch.elexis.externe_dokumente.Test_externe_dokumente.class
})
public class AllTests {
	public static Test suite() throws ClassNotFoundException{
		TestSuite suite = new TestSuite("ch.elexis.externe_dokumente tests");
		return suite;
	}
}
