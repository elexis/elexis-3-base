package ch.elexis.mednet.webapi.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private IConfigService configService;
	private static final Logger logger = LoggerFactory.getLogger(PreferenceInitializer.class);
	public PreferenceInitializer() {

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
			logger.error("PreferenceInitializer: ConfigService could not be retrieved.");
		}
    }

    public void initializeDefaultPreferences() {
		if (configService != null) {
			configService.setActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");
			configService.setActiveUserContact(PreferenceConstants.MEDNET_USER_STRING, "");
		} else {
			logger.error("initializeDefaultPreferences: ConfigService is null.");
		}
    }
}
