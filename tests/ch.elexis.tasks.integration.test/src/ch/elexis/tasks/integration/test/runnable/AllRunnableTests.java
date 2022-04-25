package ch.elexis.tasks.integration.test.runnable;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ HL7ImporterIIdentifiedRunnableTest.class, BillLabResultOnCreationIdentifiedRunnableTest.class })
public class AllRunnableTests {

}
