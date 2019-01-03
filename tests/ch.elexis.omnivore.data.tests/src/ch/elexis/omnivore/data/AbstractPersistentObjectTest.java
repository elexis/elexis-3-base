package ch.elexis.omnivore.data;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.constants.ElexisSystemPropertyConstants;
import ch.elexis.data.Anwender;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.User;
import ch.rgw.tools.JdbcLink;

@Ignore
@RunWith(Parameterized.class)
public class AbstractPersistentObjectTest {
	protected static String testUserName;
	protected final static String PASSWORD = "password";
	protected static JdbcLink link = null;
	static boolean connectionOk = false;
	private static Logger log = LoggerFactory.getLogger(AbstractPersistentObjectTest.class);
	
	
	private static void initPO() {
		if (link != null ) {
			return;
		}
		System.setProperty(ElexisSystemPropertyConstants.RUN_MODE, 
			ElexisSystemPropertyConstants.RUN_MODE_FROM_SCRATCH);
		log.debug("AbstractPersistentObjectTest mode {} is {}", ElexisSystemPropertyConstants.RUN_MODE_FROM_SCRATCH,
			System.getProperty(ElexisSystemPropertyConstants.RUN_MODE));
		System.setProperty(ElexisSystemPropertyConstants.LOGIN_USERNAME, "007"); 
		System.setProperty(ElexisSystemPropertyConstants.LOGIN_PASSWORD, "topsecret"); 
		link = new JdbcLink("org.h2.Driver", "jdbc:h2:mem:test_mem", "h2");
		link = PersistentObject.getDefaultConnection().getJdbcLink();
		log.debug("AbstractPersistentObjectTest starting link {}", link );
		PersistentObject.connect(link);
		if (testUserName == null) {
			testUserName = "ut_user_" + link.DBFlavor;
		}
		
		User existingUser = User.load(testUserName);
		if (!existingUser.exists()) {
			new Anwender(testUserName, PASSWORD);
		} 
		
		boolean succ = Anwender.login(testUserName, PASSWORD);
		assertTrue(succ);
		log.debug("Anwender.login done link is {}", link);		
	}
	public AbstractPersistentObjectTest() {
	}
	
	public static JdbcLink getLink(){
		log.debug("Anwender.getLink {}", link);
		initPO();
		return link;
	}
}
