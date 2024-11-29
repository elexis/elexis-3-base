package ch.elexis.mednet.webapi.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private IConfigService configService;

	// Standardkonstruktor, wie von Eclipse erwartet
	public PreferenceInitializer() {
		// ConfigService über das OSGi-Framework abrufen
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		if (context != null) {
			ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);
			if (serviceReference != null) {
				configService = context.getService(serviceReference);
			}
		}

		if (configService != null) {
			initializeDefaultPreferences();
		} else {
			System.err.println("PreferenceInitializer: ConfigService could not be retrieved.");
		}
    }

	// Methode zur Initialisierung der Standardwerte
    public void initializeDefaultPreferences() {
		if (configService != null) {
			configService.setActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");
			configService.setActiveUserContact(PreferenceConstants.MEDNET_USER_STRING, "");
		} else {
			System.err.println("initializeDefaultPreferences: ConfigService is null.");
		}
    }
}
