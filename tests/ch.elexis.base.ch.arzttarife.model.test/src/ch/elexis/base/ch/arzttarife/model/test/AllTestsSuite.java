package ch.elexis.base.ch.arzttarife.model.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.base.ch.arzttarife.model.tarmed.test.AllTarmedTests;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IElexisEntityManager;
import ch.elexis.core.services.IModelService;
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
//	private static IContextService contextService;
	
	@BeforeClass
	public static void beforeClass() throws IOException{
		modelService = OsgiServiceUtil
			.getService(IModelService.class,
				"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.base.ch.arzttarife.model)")
			.get();
		coreModelService = OsgiServiceUtil.getService(IModelService.class,
			"(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)").get();
//		contextService = OsgiServiceUtil.getService(IContextService.class).get();
		entityManager = OsgiServiceUtil.getService(IElexisEntityManager.class).get();
		entityManager.getEntityManager(); // initialize the db
		
		assertTrue(entityManager.executeSQLScript("test_initPhysiotarif",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/physioTarif.sql")));
		assertTrue(entityManager.executeSQLScript("test_initComplementaryTarif",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/complementaryTarif.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmed",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/tarmedTarif.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedExtension",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/tarmedExtension.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedDefinitionen",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/tarmedDefinitionen.sql")));
		assertTrue(entityManager.executeSQLScript("test_initTarmedKumulationen",
			TestUtil.loadFile(AllTestsSuite.class, "/rsc/tarmedKumulationen.sql")));
	}
	
	public static IModelService getModelService(){
		return modelService;
	}
	
	public static IModelService getCoreModelService(){
		return coreModelService;
	}
	
//	public static IContextService getContextService(){
//		return contextService;
//	}
}
