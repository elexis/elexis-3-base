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

import org.eclipse.jface.preference.IPreferenceStore;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public final class SettingsProvider {
	public static IPreferenceStore getSettings(){
		String currentPreferences = CoreHub.localCfg.get(PreferenceConstants.SETTINGS_PROVIDER, "aktueller Benutzer");

		if (currentPreferences.equals("aktueller Benutzer")) {
			return new ConfigServicePreferenceStore(Scope.USER);
		}
		if (currentPreferences.equals("aktueller Mandant")) {
			return new ConfigServicePreferenceStore(Scope.MANDATOR);
		}
		if (currentPreferences.equals("Global")) {
			return new ConfigServicePreferenceStore(Scope.GLOBAL);
		}
		return new ConfigServicePreferenceStore(Scope.USER);
	}
}
