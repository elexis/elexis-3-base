package at.medevit.elexis.ehc.vacdoc.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import at.medevit.elexis.ehc.vacdoc.service.VacdocServiceTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		VacdocServiceTest.class
})
public class AllTests {
	public static BundleContext context =
		FrameworkUtil.getBundle(VacdocServiceTest.class).getBundleContext();
	
	public static ServiceReference<?> getService(Class<?> clazz){
		return (ServiceReference<?>) context.getServiceReference(clazz);
	}
	
	public static void ungetService(ServiceReference<?> reference){
		context.ungetService(reference);
	}
}
