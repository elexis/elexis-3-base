/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2013
 * 
 *******************************************************************************/
package net.medshare.connector.viollier.data;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.data.Mandant;
import ch.rgw.io.Settings;
import net.medshare.connector.viollier.Messages;

/**
 * Klasse zum Verwalten der Einstellungen zum Viollier Connector Plugin
 */
public class ViollierConnectorSettings {
	public static final String PLUGIN_ID = "net.medshare.connector.data.ViollierConnectorSettings"; //$NON-NLS-1$
	public static final String cfgBase = "viollier/portal"; //$NON-NLS-1$
	
	Settings machineCfg = CoreHub.localCfg; // Settings: lokal auf dem PC (Windows: Registry)
	
	Mandant mandant;
	
	// Globale Einstellungen
	public static final String cfgLoginUrl = cfgBase + "/login/url"; //$NON-NLS-1$
	public static final String cfgConsultItUrl = cfgBase + "/consultit/url"; //$NON-NLS-1$
	public static final String cfgOrderItUrl = cfgBase + "/orderit/url"; //$NON-NLS-1$
	public static final String cfgUserName = cfgBase + "/user/name"; //$NON-NLS-1$
	public static final String cfgUserPassword = cfgBase + "/user/password"; //$NON-NLS-1$
	public static final String cfgViollierClientId = cfgBase + "/viollier/clientid"; //$NON-NLS-1$
	public static final String cfgCumulativePresentation = cfgBase + "/cumulativepresentation"; //$NON-NLS-1$
	String globalLoginUrl;
	String globalConsultItUrl;
	String globalOrderItUrl;
	String globalUserName;
	String globalUserPassword;
	String globalViollierClientId;
	Boolean globalPreferedPresentation;
	
	// Mandanten Einstellungen
	public static final String cfgMandantUseGlobalSettings = cfgBase + "/mandantUseGlobalSettings"; //$NON-NLS-1$
	Boolean mandantUseGlobalSettings = true;
	String mandantUserName;
	String mandantUserPassword;
	String mandantViollierClientId;
	
	// Machine Einstellungen
	public static final String cfgMachineUseGlobalSettings = cfgBase + "/machineUseGlobalSettings"; //$NON-NLS-1$
	Boolean machineUseGlobalSettings = true;
	Boolean machinePreferedPresentation;
	
	/**
	 * Konstruktur mit Angabe des gewünschten Mandanten
	 * 
	 * @param m
	 *            gewünschter Mandant
	 */
	public ViollierConnectorSettings(Mandant m){
		mandant = m;
		loadSettings();
	}
	
	/**
	 * Gibt den aktuellen Mandanten zurück
	 * 
	 * @return aktueller Mandant
	 */
	public Mandant getMandant(){
		return mandant;
	}
	
	public Boolean isMandantUsingGlobalSettings(){
		return mandantUseGlobalSettings;
	}
	
	public void setMandantUsingGlobalSettings(Boolean setting){
		mandantUseGlobalSettings = setting;
	}
	
	public Boolean isMachineUsingGlobalSettings(){
		return machineUseGlobalSettings;
	}
	
	public void setMachineUsingGlobalSettings(Boolean setting){
		machineUseGlobalSettings = setting;
	}
	
	/**
	 * @return SSO-Login URL von Viollier
	 */
	public String getGlobalLoginUrl(){
		return globalLoginUrl;
	}
	
	/**
	 * Setzt die globale SSO-Login URL von Viollier
	 * 
	 * @param globalLoginUrl
	 */
	public void setGlobalLoginUrl(String globalLoginUrl){
		this.globalLoginUrl = globalLoginUrl;
	}
	
	/**
	 * @return globale ConsultIT-URL Viollier Portal
	 */
	public String getGlobalConsultItUrl(){
		return globalConsultItUrl;
	}
	
	/**
	 * Setzt die globale ConsultIT-URL Viollier Portal
	 * 
	 * @param globalConsultItUrl
	 */
	public void setGlobalConsultItUrl(String globalConsultItUrl){
		this.globalConsultItUrl = globalConsultItUrl;
	}
	
	/**
	 * @return globale OrderIT-URL Viollier Portal
	 */
	public String getGlobalOrderItUrl(){
		return globalOrderItUrl;
	}
	
	/**
	 * Setzt die globale OrderIT-URL Viollier Portal
	 * 
	 * @param globalConsultItUrl
	 */
	public void setGlobalOrderItUrl(String globalOrderItUrl){
		this.globalOrderItUrl = globalOrderItUrl;
	}
	
	public String getGlobalUserName(){
		return globalUserName;
	}
	
	public void setGlobalUserName(String globalUserName){
		this.globalUserName = globalUserName;
	}
	
	public String getGlobalUserPassword(){
		return globalUserPassword;
	}
	
	public void setGlobalUserPassword(String globalUserPassword){
		this.globalUserPassword = globalUserPassword;
	}
	
	public String getGlobalViollierClientId(){
		return globalViollierClientId;
	}
	
	public void setGlobalViollierClientId(String globalViollierClientId){
		this.globalViollierClientId = globalViollierClientId;
	}
	
	public Boolean getGlobalPreferedPresentation(){
		return globalPreferedPresentation;
	}
	
	public void setGlobalPreferedPresentation(Boolean globalPreferedPresentation){
		this.globalPreferedPresentation = globalPreferedPresentation;
	}
	
	public Boolean getMandantUseGlobalSettings(){
		return mandantUseGlobalSettings;
	}
	
	public void setMandantUseGlobalSettings(Boolean mandantUseGlobalSettings){
		this.mandantUseGlobalSettings = mandantUseGlobalSettings;
	}
	
	/**
	 * @return User Name Viollier Portal je Mandant
	 */
	public String getMandantUserName(){
		return mandantUserName;
	}
	
	/**
	 * Setzt User Name Viollier Portal pro Mandant
	 * 
	 * @param mandantUserName
	 */
	public void setMandantUserName(String mandantUserName){
		this.mandantUserName = mandantUserName;
	}
	
	/**
	 * @return Passwort Viollier Portal je Mandant
	 */
	public String getMandantUserPassword(){
		return mandantUserPassword;
	}
	
	/**
	 * Setzt Passwort Viollier Portal je Mandant
	 * 
	 * @param mandantUserPassword
	 */
	public void setMandantUserPassword(String mandantUserPassword){
		this.mandantUserPassword = mandantUserPassword;
	}
	
	/**
	 * @return Viollier Kundennummer je Mandant
	 */
	public String getMandantViollierClientId(){
		return mandantViollierClientId;
	}
	
	/**
	 * Setzt Viollier Kundennummer je Mandant
	 * 
	 * @param mandantViollierClientId
	 */
	public void setMandantViollierClientId(String mandantViollierClientId){
		this.mandantViollierClientId = mandantViollierClientId;
	}
	
	public Boolean getMachineUseGlobalSettings(){
		return machineUseGlobalSettings;
	}
	
	public void setMachineUseGlobalSettings(Boolean machineUseGlobalSettings){
		this.machineUseGlobalSettings = machineUseGlobalSettings;
	}
	
	/**
	 * @return Präsentationsmodus (kumulativ/normal)
	 */
	public Boolean getMachinePreferedPresentation(){
		return machinePreferedPresentation;
	}
	
	/**
	 * Setzt Präsentationsmodus (kumulativ/normal)
	 * 
	 * @param machinePreferedPresentation
	 */
	public void setMachinePreferedPresentation(Boolean machinePreferedPresentation){
		this.machinePreferedPresentation = machinePreferedPresentation;
	}
	
	/**
	 * Lädt die aktuell gespeicherten Einstellungen
	 */
	public void loadSettings(){
		
		String settingText;
		
		// Globale Settings
		globalLoginUrl = ConfigServiceHolder.getGlobal(cfgLoginUrl, Messages.DefaultSetting_LoginUrl);
		globalConsultItUrl =
			ConfigServiceHolder.getGlobal(cfgConsultItUrl, Messages.DefaultSetting_ConsultItUrl);
		globalOrderItUrl =
			ConfigServiceHolder.getGlobal(cfgOrderItUrl, Messages.DefaultSetting_OrderItUrl);
		globalUserName = ConfigServiceHolder.getGlobal(cfgUserName, "");
		globalUserPassword = ConfigServiceHolder.getGlobal(cfgUserPassword, "");
		globalViollierClientId = ConfigServiceHolder.getGlobal(cfgViollierClientId, "");
		globalPreferedPresentation = false;
		settingText = ConfigServiceHolder.getGlobal(cfgCumulativePresentation, "1");
		if (settingText.equals("1"))
			globalPreferedPresentation = true;
		
		// Mandanten Settings
		settingText = ConfigServiceHolder.getMandator(cfgMandantUseGlobalSettings, "true"); //$NON-NLS-1$
		mandantUseGlobalSettings = Boolean.parseBoolean(settingText);
		mandantUserName = ConfigServiceHolder.getMandator(cfgUserName, "");
		mandantUserPassword = ConfigServiceHolder.getMandator(cfgUserPassword, "");
		mandantViollierClientId = ConfigServiceHolder.getMandator(cfgViollierClientId, "");
		
		// Machine Settings
		settingText = machineCfg.get(cfgMachineUseGlobalSettings, "true"); //$NON-NLS-1$
		machineUseGlobalSettings = Boolean.parseBoolean(settingText);
		machinePreferedPresentation = false;
		settingText = machineCfg.get(cfgCumulativePresentation, "true");
		if (settingText.equals("1"))
			machinePreferedPresentation = true;
	}
	
	/**
	 * Speichert die aktuellen Einstellungen
	 */
	public void saveSettings(){
		
		// Globale Settings
		ConfigServiceHolder.setGlobal(cfgLoginUrl, globalLoginUrl);
		ConfigServiceHolder.setGlobal(cfgConsultItUrl, globalConsultItUrl);
		ConfigServiceHolder.setGlobal(cfgOrderItUrl, globalOrderItUrl);
		ConfigServiceHolder.setGlobal(cfgUserName, globalUserName);
		ConfigServiceHolder.setGlobal(cfgUserPassword, globalUserPassword);
		ConfigServiceHolder.setGlobal(cfgViollierClientId, globalViollierClientId);
		ConfigServiceHolder.setGlobal(cfgCumulativePresentation, globalPreferedPresentation);
		
		// Mandanten Settings
		ConfigServiceHolder.setMandator(cfgMandantUseGlobalSettings,
			mandantUseGlobalSettings.toString());
		ConfigServiceHolder.setMandator(cfgUserName, mandantUserName);
		ConfigServiceHolder.setMandator(cfgUserPassword, mandantUserPassword);
		ConfigServiceHolder.setMandator(cfgViollierClientId, mandantViollierClientId);
		
		// Machine Settings
		machineCfg.set(cfgMachineUseGlobalSettings, machineUseGlobalSettings.toString());
		machineCfg.set(cfgCumulativePresentation, machinePreferedPresentation);
		machineCfg.flush();
	}
}
