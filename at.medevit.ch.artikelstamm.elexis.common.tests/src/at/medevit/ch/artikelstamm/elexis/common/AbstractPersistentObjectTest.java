package at.medevit.ch.artikelstamm.elexis.common;

import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.constants.ElexisSystemPropertyConstants;
import ch.elexis.data.Anwender;
import ch.elexis.data.DBConnection;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.User;

@Ignore
@RunWith(Parameterized.class)
public class AbstractPersistentObjectTest {
	protected static String testUserName;
	protected final static String PASSWORD = "password";
	private static Logger log = LoggerFactory.getLogger(AbstractPersistentObjectTest.class);
	private static DBConnection dbConn = null;
	
	public AbstractPersistentObjectTest() {
	}
	
	public static boolean initFromScratch(){
		System.setProperty(ElexisSystemPropertyConstants.RUN_MODE, 
			ElexisSystemPropertyConstants.RUN_MODE_FROM_SCRATCH);
		log.debug("AbstractPersistentObjectTest mode {} is {}", ElexisSystemPropertyConstants.RUN_MODE_FROM_SCRATCH,
			System.getProperty(ElexisSystemPropertyConstants.RUN_MODE));
		System.setProperty(ElexisSystemPropertyConstants.LOGIN_USERNAME, "007"); 
		System.setProperty(ElexisSystemPropertyConstants.LOGIN_PASSWORD, "topsecret"); 
		if (dbConn == null ) {
	        PersistentObject.getDefaultConnection();
	        dbConn= PersistentObject.getDefaultConnection();
		} else {
			return true;
			/*
				PersistentObject.deleteAllTables();
				dbConn.setRunningFromScratch(true);
				PersistentObject.connect(dbConn);
			*/
		}
		if (testUserName == null) {
			testUserName = "ut_user_h2";
		}
		
		User existingUser = User.load(testUserName);
		if (!existingUser.exists()) {
			new Anwender(testUserName, PASSWORD);
		} 
		
		boolean succ = Anwender.login(testUserName, PASSWORD);
		assertTrue(succ);
		log.debug("Anwender.login done dbConn is {}" ,dbConn);
		return dbConn.isRunningFromScratch();
	}
}
