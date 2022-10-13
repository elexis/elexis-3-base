package ch.itmed.fop.printing.preferences;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class Setting {

	public static boolean isGlobalSetting(String docName) {
		return ConfigServiceHolder.getLocal(PreferenceConstants.getDocPreferenceConstant(docName, 12), true);
	}

	public static String getString(String docName, String key) {
		if (isGlobalSetting(docName)) {
			String defValue = ConfigServiceHolder.getGlobal(key + Preferences.SETTINGS_PREFERENCE_STORE_DEFAULT, "");
			return ConfigServiceHolder.getGlobal(key, defValue);
		}
		String defValue = ConfigServiceHolder.getLocal(key + Preferences.SETTINGS_PREFERENCE_STORE_DEFAULT, "");
		return ConfigServiceHolder.getLocal(key, defValue);
	}

}
