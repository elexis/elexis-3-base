/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package net.medshare.connector.aerztekasse.data;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Mandant;
import ch.rgw.io.Settings;

public class AerztekasseSettings {
	public static final String PLUGIN_ID = "net.medshare.connector.aerztekasse"; //$NON-NLS-1$
	public static final String cfgBase = "net/medshare/connector/finance/aerztekasse"; //$NON-NLS-1$

	Settings machineCfg = CoreHub.localCfg; // Settings: lokal auf dem PC (Windows: Registry)

	Mandant mandant;

	// Globale Einstellungen
	public static final String cfgUsername = cfgBase + "/username"; //$NON-NLS-1$
	public static final String cfgPassword = cfgBase + "/password"; //$NON-NLS-1$
	public static final String cfgUrl = cfgBase + "/url"; //$NON-NLS-1$
	String globalUsername;
	String globalPassword;
	String globalUrl;

	// Mandanten Einstellungen

	public static final String cfgMandantUseGlobalSettings = cfgBase + "/mandantUseGlobalSettings"; //$NON-NLS-1$
	Boolean mandantUseGlobalSettings = true;
	String mandantUsername;
	String mandantPassword;

	// Machine Einstellungen
	public static final String cfgMachineUseGlobalSettings = cfgBase + "/machineUseGlobalSettings"; //$NON-NLS-1$
	Boolean machineUseGlobalSettings = true;
	String machineUrl;

	public AerztekasseSettings(Mandant m) {
		mandant = m;
		loadSettings();
	}

	public Mandant getMandant() {
		return mandant;
	}

	public Boolean isMandantUsingGlobalSettings() {
		return mandantUseGlobalSettings;
	}

	public void setMandantUsingGlobalSettings(Boolean setting) {
		mandantUseGlobalSettings = setting;
	}

	public Boolean isMachineUsingGlobalSettings() {
		return machineUseGlobalSettings;
	}

	public void setMachineUsingGlobalSettings(Boolean setting) {
		machineUseGlobalSettings = setting;
	}

	public String getUsername() {
		if (mandantUseGlobalSettings)
			return globalUsername;
		else
			return mandantUsername;
	}

	public String getGlobalUsername() {
		return globalUsername;
	}

	public String getMandantUsername() {
		return mandantUsername;
	}

	public void setGlobalUsername(String username) {
		globalUsername = username;
	}

	public void setMandantUsername(String username) {
		mandantUsername = username;
	}

	public String getPassword() {
		if (mandantUseGlobalSettings)
			return globalPassword;
		else
			return mandantPassword;
	}

	public String getGlobalPassword() {
		return globalPassword;
	}

	public String getMandantPasword() {
		return mandantPassword;
	}

	public void setGlobalPassword(String password) {
		globalPassword = password;
	}

	public void setMandantPassword(String password) {
		mandantPassword = password;
	}

	public String getUrl() {
		if (machineUseGlobalSettings)
			return globalUrl;
		else
			return machineUrl;
	}

	public String getGlobalUrl() {
		return globalUrl;
	}

	public String getMachineUrl() {
		return machineUrl;
	}

	public void setGlobalUrl(String url) {
		globalUrl = url;
	}

	public void setMachineUrl(String url) {
		machineUrl = url;
	}

	public void loadSettings() {
		String settingText;

		globalUrl = null;

		// Globale Settings
		globalUsername = ConfigServiceHolder.getGlobal(cfgUsername, StringUtils.EMPTY);
		globalPassword = ConfigServiceHolder.getGlobal(cfgPassword, StringUtils.EMPTY);
		globalUrl = ConfigServiceHolder.getGlobal(cfgUrl, StringUtils.EMPTY);

		// Mandanten Settings
		settingText = ConfigServiceHolder.getMandator(cfgMandantUseGlobalSettings, "true"); //$NON-NLS-1$
		mandantUseGlobalSettings = Boolean.parseBoolean(settingText);
		mandantUsername = ConfigServiceHolder.getMandator(cfgUsername, StringUtils.EMPTY);
		mandantPassword = ConfigServiceHolder.getMandator(cfgPassword, StringUtils.EMPTY);

		// Machine Settings
		settingText = machineCfg.get(cfgMachineUseGlobalSettings, "true"); //$NON-NLS-1$
		machineUseGlobalSettings = Boolean.parseBoolean(settingText);
		machineUrl = machineCfg.get(cfgUrl, StringUtils.EMPTY);
	}

	public void saveSettings() {

		// Globale Settings
		ConfigServiceHolder.setGlobal(cfgUsername, globalUsername);
		ConfigServiceHolder.setGlobal(cfgPassword, globalPassword);
		ConfigServiceHolder.setGlobal(cfgUrl, globalUrl);

		// Mandanten Settings
		ConfigServiceHolder.setMandator(cfgMandantUseGlobalSettings, mandantUseGlobalSettings.toString());
		ConfigServiceHolder.setMandator(cfgUsername, mandantUsername);
		ConfigServiceHolder.setMandator(cfgPassword, mandantPassword);

		// Machine Settings
		machineCfg.set(cfgMachineUseGlobalSettings, machineUseGlobalSettings.toString());
		machineCfg.set(cfgUrl, machineUrl);
		machineCfg.flush();
	}

}
