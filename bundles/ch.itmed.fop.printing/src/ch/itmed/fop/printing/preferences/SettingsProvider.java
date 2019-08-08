/*******************************************************************************
 * Copyright (c) 2019 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.fop.printing.preferences;

import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.io.Settings;

public final class SettingsProvider {
	public static Settings getSettings(String cfgStoreName) {
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
