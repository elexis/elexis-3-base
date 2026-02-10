package ch.elexis.global_inbox.core.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ FilePrefixStrategyTest.class, PatientFolderStrategyTest.class, HierarchyStrategyTest.class,
		FallbackStrategyTest.class })
public class AllTests {
}