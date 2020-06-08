package ch.elexis.openoffice;

import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;

public class OOActivator extends AbstractUIPlugin {
	private static Logger log = LoggerFactory.getLogger(OOActivator.class);
	public static String PLUGIN_ID = "ch.medelexis.openoffice";

	// The shared instance.
	private static OOActivator plugin;

	private String librariesLocation = null;

	private IOfficeApplication localOfficeApplication = null;

	private boolean librariesOk = false;

	public OOActivator() {
		super();
		plugin = this;
		System.setProperty(IOfficeApplication.NOA_NATIVE_LIB_PATH,
				getLibrariesLocation());
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return shared instance
	 */
	public static OOActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns office application. The office application is not activated.
	 * Before using the office application the client should check the
	 * activation state.
	 * 
	 * @param shell
	 *            shell to be used for dialogs
	 * 
	 * @return office application
	 */
	public synchronized IOfficeApplication getLocalOfficeApplication(Shell shell)
			throws OfficeApplicationException {
		log.debug(MessageFormat.format("getLocalOfficeApplication({0})", shell));

		if (localOfficeApplication == null && librariesOk) {
			Map<String, String> configuration = new HashMap<String, String>(1);
			configuration.put(IOfficeApplication.APPLICATION_TYPE_KEY,
					IOfficeApplication.LOCAL_APPLICATION);
			localOfficeApplication = OfficeApplicationRuntime
					.getApplication(configuration);
		}
		return localOfficeApplication;
	}

	/**
	 * Returns location of the libraries of the plugin. Returns null if the
	 * location can not be provided.
	 * 
	 * @return location of the libraries of the plugin or null if the location
	 *         can not be provided
	 */
	public String getLibrariesLocation() {
		if (librariesLocation == null) {
			try {
				URL url = Platform.getBundle("ag.ion.noa").getEntry("/");
				url = FileLocator.toFileURL(url);		
				String bundleLocation = url.getPath();				
				File file = new File(bundleLocation);			
				bundleLocation = file.getAbsolutePath();			
				bundleLocation = bundleLocation
						.replace('/', File.separatorChar)
						+ File.separator
						+ "lib";

				librariesLocation = bundleLocation;
				librariesOk = true;

				if (!new File(librariesLocation + File.separator
						+ "ICE_JNIRegistry.dll").exists()) {
					if (!new File("ICE_JNIRegistry.dll").exists()) {
						librariesOk = false;
						librariesLocation = null;
					}
				}
			} catch (Throwable throwable) {
				log.error(throwable.getLocalizedMessage(), throwable);
				librariesOk = false;
				librariesLocation = null;
				return null;
			}
		}
		return librariesLocation;
	}

	@Override
	public void stop(BundleContext context) throws Exception {

		if (localOfficeApplication != null) {
			try {
				localOfficeApplication.getDesktopService().terminate();
				// Give some time to close all open documents
				try {
					Thread.yield();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error("Interrupted", e);
				}
			} catch (Throwable t) {
				log.warn(t.getLocalizedMessage(), t);
			} finally {
				localOfficeApplication.dispose();
			}
			localOfficeApplication = null;
		}
	}
}
