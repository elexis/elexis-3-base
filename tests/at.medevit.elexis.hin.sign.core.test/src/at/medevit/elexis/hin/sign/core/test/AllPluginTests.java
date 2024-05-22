package at.medevit.elexis.hin.sign.core.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import at.medevit.elexis.hin.sign.core.internal.CliProcessTest;
import at.medevit.elexis.hin.sign.core.internal.HinSignServiceTest;

/**
 * User interaction and running HIN client required for tests. Automatically
 * running on build is not possible.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ CliProcessTest.class, HinSignServiceTest.class })
public class AllPluginTests {

}
