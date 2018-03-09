package ch.elexis.base.ch.arzttarife.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.data.TarmedOptifierTest;
import junit.framework.Test;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({
	TarmedOptifierTest.class
})

public class AllOptifierTests {
	public static Test suite() throws ClassNotFoundException{
		return new TestSuite("Arzttarife TarmedOptifierTests");
	}
}
