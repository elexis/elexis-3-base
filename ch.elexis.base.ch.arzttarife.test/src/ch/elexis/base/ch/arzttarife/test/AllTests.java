package ch.elexis.base.ch.arzttarife.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TarmedReferenceDataImporterTest.class
})
public class AllTests {
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Arzttarife ReferenceDataImporter Tests");
	}
}
