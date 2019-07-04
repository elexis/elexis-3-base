package ch.itmed.fop.printing.preferences;

import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.io.Settings;

public final class SettingsProvider {
	public static Settings getSettings(String cfgStoreName) {
		// String currentPreferences =
		// CoreHub.localCfg.get(PreferenceConstants.SETTINGS_PROVIDER, "aktueller
		// Benutzer");
		String currentPreferences = "";

		if (currentPreferences.equals("user")) {
			return CoreHub.userCfg;
		}

		if (currentPreferences.equals("global")) {
			return CoreHub.globalCfg;
		}

		return CoreHub.globalCfg;
	}

	public static Settings setPagesLocalStore() {
		switchToLocalStore(3, 11);
		return CoreHub.localCfg;
	}

	public static Settings setPrintersLocalStore() {
		switchToLocalStore(0, 0);
		return CoreHub.localCfg;
	}

	public static Settings setTemplatesLocalStore() {
		switchToLocalStore(1, 2);
		return CoreHub.localCfg;
	}

	private static void switchToLocalStore(int indexBegin, int indexEnd) {
		for (String doc : PreferenceConstants.getDocumentNames()) {
			for (int i = indexBegin; i < indexEnd + 1; i++) {
				String constant = PreferenceConstants.getDocPreferenceConstant(doc, i);
				CoreHub.localCfg.set(constant, CoreHub.globalCfg.get(constant, ""));
				// CoreHub.localCfg.set(constant, "foo"); //Only a value is written, if a non
				// empty string was returned
			}
		}
		CoreHub.localCfg.flush();
	}

	public static Settings getStore(String docName) {
		Settings settingsStore;

		if (CoreHub.localCfg.get(PreferenceConstants.getDocPreferenceConstant(docName, 12), true)) {
			settingsStore = CoreHub.globalCfg;
		} else {
			settingsStore = CoreHub.localCfg;
		}

		return settingsStore;
	}
}
