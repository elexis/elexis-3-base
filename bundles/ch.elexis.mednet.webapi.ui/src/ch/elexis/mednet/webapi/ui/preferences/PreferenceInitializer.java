package ch.elexis.mednet.webapi.ui.preferences;

import java.util.Optional;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

/**
 * Initializes default global preferences for MedNet Web API.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private static final Logger log = LoggerFactory.getLogger(PreferenceInitializer.class);

	private IConfigService configService;

	public PreferenceInitializer() {
		Optional<IConfigService> optionalService = OsgiServiceUtil.getServiceWait(IConfigService.class, 1000);
		if (optionalService.isPresent()) {
			this.configService = optionalService.get();
		} else {
			log.error("Failed to retrieve IConfigService during PreferenceInitializer instantiation.");
		}
	}

	@Override
	public void initializeDefaultPreferences() {
		if (configService == null) {
			log.error("ConfigService is null. Cannot initialize default preferences.");
			return;
		}

		setDefaultIfEmpty(PreferenceConstants.MEDNET_DOWNLOAD_PATH);
		setDefaultIfEmpty(PreferenceConstants.MEDNET_USER_STRING);

		// Set default operating mode if none is configured
		String currentMode = configService.get(PreferenceConstants.MEDNET_MODE, null);
		if (currentMode == null || currentMode.isEmpty()) {
			configService.set(PreferenceConstants.MEDNET_MODE, MedNetWebPreferencePage.PRODUKTIV);
		}
	}

	/**
	 * Sets an empty string as a global fallback if the configuration key is
	 * currently unassigned. * @param key the preference key to check and initialize
	 */
	private void setDefaultIfEmpty(String key) {
		String currentValue = configService.get(key, null);
		if (currentValue == null || currentValue.isEmpty()) {
			configService.set(key, "");
		}
	}
}