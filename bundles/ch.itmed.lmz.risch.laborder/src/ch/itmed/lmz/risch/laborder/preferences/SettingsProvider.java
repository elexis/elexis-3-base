/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.preferences;

import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.io.Settings;

public final class SettingsProvider {
	public static Settings getSettings() {
		String currentPreferences = CoreHub.localCfg.get(PreferenceConstants.SETTINGS_PROVIDER, "aktueller Benutzer");

		if (currentPreferences.equals("aktueller Benutzer")) {
			return CoreHub.userCfg;
		}
		if (currentPreferences.equals("aktueller Mandant")) {
			return CoreHub.mandantCfg;
		}
		if (currentPreferences.equals("Global")) {
			return CoreHub.globalCfg;
		}
		return CoreHub.userCfg;
	}
}
