package com.hilotec.elexis.messwerte.v2_test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	com.hilotec.elexis.messwerte.v2_test.data.MessungKonfigurationTest.class
})
public class AllPluginTests {
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Hilotec Messwerte V2 Tests");
	}
}
