package ch.elexis.omnivore.data;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLinkException;

@RunWith(Suite.class)
@SuiteClasses({
	Test_Utils.class
})
public class AllOmnivoreDataTests {
	
	static {}
	
}