package ch.elexis.regiomed.order.test;

import java.sql.SQLException;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.BeforeClass;

import ch.elexis.data.PersistentObject;

public abstract class AbstractRegiomedTest {

	@BeforeClass
	public static void initPersistence() throws SQLException {
		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setURL("jdbc:h2:mem:elexisTest;DB_CLOSE_DELAY=-1");
		dataSource.setUser("sa");
		dataSource.setPassword("");

		PersistentObject.connect(dataSource);
	}
}
