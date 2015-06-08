package at.medevit.elexis.cobasmira;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	CobasMiraMessageTest.class, CobasMiraPatientResultTest.class, ImportPatientResultTest.class
})
public class AllTests {
	
}
