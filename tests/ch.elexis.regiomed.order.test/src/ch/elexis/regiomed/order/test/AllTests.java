package ch.elexis.regiomed.order.test;

import java.sql.SQLException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.data.PersistentObject;

@RunWith(Suite.class)
@SuiteClasses({
        RegiomedOrderClientTest.class, RegiomedSenderTest.class })
public class AllTests {

    @BeforeClass
    public static void beforeClass() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:elexisTest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");
        
        PersistentObject.connect(dataSource);
    }
}