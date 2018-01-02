package ch.elexis.ebanking;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	Test_Camt054Parser.class, Test_Camt054andEsr.class
})
public class AllTests {
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Ebanking Tests");
	}
}