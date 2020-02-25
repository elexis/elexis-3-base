package ch.elexis.tasks.integration.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.BeforeClass;

import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.tasks.integration.test.hl7import.Hl7ImporterTaskIntegrationTest;
import ch.elexis.tasks.integration.test.internal.Hl7ImporterTaskIntegrationTestUtil;
import ch.elexis.tasks.integration.test.runnable.AllRunnableTests;

@RunWith(Suite.class)
@SuiteClasses({
	Hl7ImporterTaskIntegrationTest.class, AllRunnableTests.class
})
public class AllTests {
	
	private static Hl7ImporterTaskIntegrationTestUtil util;
	
	
	@BeforeClass
	public static void beforeClass(){
		
		util = new Hl7ImporterTaskIntegrationTestUtil();
		
		util.prepareEnvironment();
		util.configureLabAndLabItemBilling();
		util.importEal2009();
	}
	
	public static IUser getUser(){
		return util.getUser();
	}
	
	public static ILaboratory getLaboratory(){
		return util.getLaboratory();
	}

	public static ICoverage getCoverage(){
		return util.getCoverage();
	}

	public static IPatient getPatient(){
		return util.getPatient();
	}

	public static ILabItem getLabItem(){
		return util.getLabItem();
	}
	
}
