package ch.elexis.mednet.webapi.ui.preferences;

import java.util.Optional;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

	private static final Logger logger = LoggerFactory.getLogger(PreferenceInitializer.class);
	private IConfigService configService;

	public PreferenceInitializer() {
		Optional<IConfigService> optionalService = OsgiServiceUtil.getServiceWait(IConfigService.class, 1000);
		if (optionalService.isPresent()) {
			this.configService = optionalService.get();
			initializeDefaultPreferences();
		} else {
			logger.error("PreferenceInitializer: Could not retrieve IConfigService.");
		}
    }

	@Override
	public void initializeDefaultPreferences() {
		if (configService == null) {
			logger.error("initializeDefaultPreferences: ConfigService is null, cannot set defaults.");
			return;
		}

		setDefaultIfEmpty(PreferenceConstants.MEDNET_DOWNLOAD_PATH);
		setDefaultIfEmpty(PreferenceConstants.MEDNET_USER_STRING);
	}

	private void setDefaultIfEmpty(String key) {
		String currentVal = configService.getActiveUserContact(key, null);
		if (currentVal == null || currentVal.isEmpty()) {
			configService.setActiveUserContact(key, "");
		}
    }
}
