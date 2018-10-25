package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.model.tarmed.test.AllTarmedTests;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.test.util.TestUtil;
import ch.elexis.core.utils.OsgiServiceUtil;

@RunWith(Suite.class)
@SuiteClasses({
	PhysioLeistungTest.class, ComplementaryLeistungTest.class, AllTarmedTests.class
})
public class AllTestsSuite {
	
	private static IElexisEntityManager entityManager;
	private static IModelService modelService;
	private static IModelService coreModelService;
	private static IBillingService billingService;
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		modelService = OsgiServiceUtil
			.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.arzttarife.model)")
			.get();
		coreModelService = OsgiServiceUtil.getService(IModelService.class,
			"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();
		billingService = OsgiServiceUtil.getService(IBillingService.class).get();
		entityManager = OsgiServiceUtil.getService(IElexisEntityManager.class).get();
		entityManager.getEntityManager(); // initialize the db
		
		assertTrue(entityManager.executeSQLScript("test_initPhysiotarif",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/physioTarif.sql")));
		assertTrue(entityManager.executeSQLScript("test_initComplementaryTarif",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/complementaryTarif.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmed",
			TestUtil.loadFile(TestDatabaseInitializer.class, "/rsc/dbScripts/Tarmed.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedExtension", TestUtil
			.loadFile(TestDatabaseInitializer.class, "/rsc/dbScripts/TarmedExtension.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedDefinitionen", TestUtil
			.loadFile(TestDatabaseInitializer.class, "/rsc/dbScripts/TarmedDefinitionen.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedKumulationen", TestUtil
			.loadFile(TestDatabaseInitializer.class, "/rsc/dbScripts/TarmedKumulation.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedGroup", TestUtil
			.loadFile(TestDatabaseInitializer.class, "/rsc/dbScripts/TarmedGroup.sql")));
	}
	
	public static IModelService getModelService(){
		return modelService;
	}
	
	public static IModelService getCoreModelService(){
		return coreModelService;
	}
	
	public static IBillingService getBillingService(){
		return billingService;
	}
}
