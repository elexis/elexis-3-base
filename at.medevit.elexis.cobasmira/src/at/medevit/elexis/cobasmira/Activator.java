package at.medevit.elexis.cobasmira;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
	public static String PLUGIN_ID = "at.medevit.elexis.cobasmira";
	
	public static int COBAS_MIRA_STATE_NOT_RUNNING = 0;
	public static int COBAS_MIRA_STAT_RUNNING = 1;
	
	private static BundleContext context;
	private static int state = COBAS_MIRA_STATE_NOT_RUNNING;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}
	
	

}
