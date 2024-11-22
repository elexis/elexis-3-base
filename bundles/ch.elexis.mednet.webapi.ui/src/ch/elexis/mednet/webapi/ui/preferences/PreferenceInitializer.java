package ch.elexis.mednet.webapi.ui.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.FrameworkUtil;

import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, String.valueOf(FrameworkUtil.getBundle(getClass()).getBundleId()));

		store.setDefault(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
				"Default value");
		store.setDefault(PreferenceConstants.MEDNET_USER_STRING, "User Name");
	}

}
