package org.iatrix.messwerte;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.iatrix.messwerte.acl.ACLContributor;
import org.osgi.framework.BundleContext;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.iatrix.messwerte";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		initACL();
	}

	/**
	 * Initialize ACLs for the first time, if not yet done.
	 */
	private void initACL() {
		ConfigServiceHolder.runIfConfigServiceAvailable(() -> {
			boolean isInitialized = ConfigServiceHolder.getGlobal(Constants.ACL_INITIALIZED, false);
			if (!isInitialized) {
				new ACLContributor().initializeDefaults(AccessControlServiceHolder.get());
				ConfigServiceHolder.setGlobal(Constants.ACL_INITIALIZED, true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
