package ch.elexis.tasks.integration.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.BeforeClass;

import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IUser;
import ch.elexis.tasks.integration.test.hl7import.Hl7ImporterTaskIntegrationTest;
import ch.elexis.tasks.integration.test.internal.Hl7ImporterTaskIntegrationTestUtil;

@RunWith(Suite.class)
@SuiteClasses({
	Hl7ImporterTaskIntegrationTest.class
})
public class AllTests {
	
	private static IUser owner;
	private static ILaboratory laboratory;
	
	@BeforeClass
	public static void beforeClass(){
		
		owner = Hl7ImporterTaskIntegrationTestUtil.prepareEnvironment();
		laboratory = Hl7ImporterTaskIntegrationTestUtil.configureLabAndLabItemBilling();
		Hl7ImporterTaskIntegrationTestUtil.importEal2009();
	}
	
	public static IUser getOwner(){
		return owner;
	}
	
	public static ILaboratory getLaboratory(){
		return laboratory;
	}
	
}
