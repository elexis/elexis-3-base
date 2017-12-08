package at.medevit.elexis.tarmed.model.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	InvoiceRequest400Tests.class, InvoiceResponse400Tests.class, InvoiceRequest440Tests.class,
	InvoiceResponse440Tests.class
})
public class AllTests {
	
}
