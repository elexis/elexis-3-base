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
package ch.elexis.labor.viollier.v2.data;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Mandant;
import ch.rgw.io.Settings;

/**
 * Klasse zum Verwalten der Einstellungen zum Viollier Labor-Importer Plugin
 */
public class ViollierLaborImportSettings {
	public static final String PLUGIN_ID =
		"ch.elexis.laborimport.viollier.v2.ViollierLaborImportSettings"; //$NON-NLS-1$
	public static final String cfgBase = "ch/elexis/laborimport/viollier/v2/ViollierLaborImport"; //$NON-NLS-1$
	
	Settings globalCfg = CoreHub.globalCfg; // Settings: DB für alle PCs und Mandanten
	Settings machineCfg = CoreHub.localCfg; // Settings: lokal auf dem PC (Windows: Registry)
	Settings mandantCfg = CoreHub.mandantCfg; // Settings: DB für einen Mandanten
	
	Mandant mandant;
	
	// Globale Einstellungen
	public static final String cfgJMedTransferJar = cfgBase + "/jmedtransfer/jar"; //$NON-NLS-1$
	public static final String cfgJMedTransferParam = cfgBase + "/jmedtransfer/param"; //$NON-NLS-1$
	public static final String cfgDirDownload = cfgBase + "/dir/download"; //$NON-NLS-1$
	public static final String cfgDirArchive = cfgBase + "/dir/archive"; //$NON-NLS-1$
	public static final String cfgDirError = cfgBase + "/dir/error"; //$NON-NLS-1$
	public static final String cfgDocumentCategory = cfgBase + "/documentCategory"; //$NON-NLS-1$
	public static final String cfgArchivePurgeInterval = cfgBase + "/archivePurgeIntervalDays"; //$NON-NLS-1$
	String globalJMedTransferJar;
	String globalJMedTransferParam;
	String globalDirDownload;
	String globalDirArchive;
	String globalDirError;
	String globalDocumentCategory;
	int globalArchivePurgeInterval;
	
	// Mandanten Einstellungen
	public static final String cfgMandantUseGlobalSettings = cfgBase + "/mandantUseGlobalSettings"; //$NON-NLS-1$
	Boolean mandantUseGlobalSettings = true;
	String mandantDocumentCategory;
	
	// Machine Einstellungen
	public static final String cfgMachineUseGlobalSettings = cfgBase + "/machineUseGlobalSettings"; //$NON-NLS-1$
	Boolean machineUseGlobalSettings = true;
	String machineJMedTransferJar;
	String machineJMedTransferParam;
	String machineDirDownload;
	String machineDirArchive;
	String machineDirError;
	int machineArchivePurgeInterval;
	
	/**
	 * Konstruktur mit Angabe des gewünschten Mandanten
	 * 
	 * @param m
	 *            gewünschter Mandant
	 */
	public ViollierLaborImportSettings(Mandant m){
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
	
	/**
	 * Gibt an, ob für den Mandanten die globalen Einstellungen verwendet werden sollen oder nicht
	 * 
	 * @return true: Mandant verwendet globale Einstellungen; false: Mandant verwendet eigene
	 *         Einstellungen
	 */
	public Boolean isMandantUsingGlobalSettings(){
		return mandantUseGlobalSettings;
	}
	
	/**
	 * Setzt die gewünschte Einstellung für die Verwendung der Mandatenparameter
	 * 
	 * @param setting
	 *            true: Mandant verwendet globale Einstellungen; false: Mandant verwendet eigene
	 *            Einstellungen
	 */
	public void setMandantUsingGlobalSettings(Boolean setting){
		mandantUseGlobalSettings = setting;
	}
	
	/**
	 * Gibt an, ob für den aktuellen PC die globalen Einstellungen verwendet werden sollen oder
	 * nicht
	 * 
	 * @return true: PC verwendet globale Einstellungen; false: PC verwendet eigene Einstellungen
	 */
	public Boolean isMachineUsingGlobalSettings(){
		return machineUseGlobalSettings;
	}
	
	/**
	 * Setzt die gewünschte Einstellung für die Verwendung der PC-parameter
	 * 
	 * @param setting
	 *            true: PC verwendet globale Einstellungen; false: PC verwendet eigene Einstellungen
	 */
	public void setMachineUsingGlobalSettings(Boolean setting){
		machineUseGlobalSettings = setting;
	}
	
	/**
	 * Gibt das gültige Download Verzeichnis zurück.
	 * 
	 * @return Download Verzeichnis
	 */
	public String getDirDownload(){
		if (machineUseGlobalSettings)
			return globalDirDownload;
		else
			return machineDirDownload;
	}
	
	/**
	 * Gibt das global konfigurierte Download Verzeichnis zurück.
	 * 
	 * @return global konfiguriertes Download Verzeichnis
	 */
	public String getGlobalDirDownload(){
		return globalDirDownload;
	}
	
	/**
	 * Setzt das global konfigurierte Download Verzeichnis
	 * 
	 * @param value
	 *            gewünschtes Verzeichnis, das gesetzt werden soll
	 */
	public void setGlobalDirDownload(String value){
		globalDirDownload = value;
	}
	
	/**
	 * Gibt das, für den lokalen PC konfigurierte Download Verzeichnis zurück.
	 * 
	 * @return für den lokalen PC konfiguriertes Download Verzeichnis
	 */
	public String getMachineDirDownload(){
		return machineDirDownload;
	}
	
	/**
	 * Setzt das, für den lokalen PC konfigurierte Download Verzeichnis
	 * 
	 * @param value
	 *            gewünschtes Verzeichnis, das gesetzt werden soll
	 */
	public void setMachineDirDownload(String value){
		machineDirDownload = value;
	}
	
	/**
	 * Gibt das gültige JAR zurück.
	 * 
	 * @return JMedTransferO.jar (Pfad und Dateiname)
	 */
	public String getJMedTransferJar(){
		if (machineUseGlobalSettings)
			return globalJMedTransferJar;
		else
			return machineJMedTransferJar;
	}
	
	/**
	 * Gibt das global konfigurierte JAR zurück.
	 * 
	 * @return global konfiguriertes JAR
	 */
	public String getGlobalJMedTransferJar(){
		return globalJMedTransferJar;
	}
	
	/**
	 * Setzt das global konfigurierte MedTransfer Jar
	 * 
	 * @param value
	 *            voller Pfad und Dateiname auf JMedTransferO.jar
	 */
	public void setGlobalJMedTransferJar(String value){
		globalJMedTransferJar = value;
	}
	
	/**
	 * Gibt das, für die lokale Maschine konfigurierte JAR zurück.
	 * 
	 * @return global konfiguriertes JAR
	 */
	public String getMachineJMedTransferJar(){
		return machineJMedTransferJar;
	}
	
	/**
	 * Setzt das, für die lokale Maschine konfigurierte MedTransfer Jar
	 * 
	 * @param value
	 *            voller Pfad und Dateiname auf JMedTransferO.jar
	 */
	public void setMachineJMedTransferJar(String value){
		machineJMedTransferJar = value;
	}
	
	/**
	 * Gibt die aktuell gültigen MedTransfer Parameter zurück.
	 * 
	 * @return aktuell gültige MedTransfer Parameter
	 */
	public String getJMedTransferParam(){
		if (machineUseGlobalSettings)
			return globalJMedTransferParam;
		else
			return machineJMedTransferParam;
	}
	
	/**
	 * Gibt die global konfigurierten MedTransfer Parameter zurück.
	 * 
	 * @return global konfigurierte MedTransfer Parameter
	 */
	public String getGlobalJMedTransferParam(){
		return globalJMedTransferParam;
	}
	
	/**
	 * Setzt die global konfigurierten MedTransfer Parameter
	 * 
	 * @param value
	 *            MedTransfer Parameter
	 */
	public void setGlobalJMedTransferParam(String value){
		globalJMedTransferParam = value;
	}
	
	/**
	 * Gibt die, für die aktuelle Maschine konfigurierten MedTransfer Parameter zurück.
	 * 
	 * @return für die aktuelle Maschine konfigurierte MedTransfer Parameter
	 */
	public String getMachineJMedTransferParam(){
		return machineJMedTransferParam;
	}
	
	/**
	 * Setzt die, für die aktuelle Maschine konfigurierten MedTransfer Parameter
	 * 
	 * @param value
	 *            MedTransfer Parameter
	 */
	public void setMachineJMedTransferParam(String value){
		machineJMedTransferParam = value;
	}
	
	/**
	 * Gibt das aktuell gültige Archiv-Verzeichnis zurück.
	 * 
	 * @return aktuell gültiges Archiv-Verzeichnis
	 */
	public String getDirArchive(){
		if (machineUseGlobalSettings)
			return globalDirArchive;
		else
			return machineDirArchive;
	}
	
	/**
	 * Gibt das global konfigurierte Archiv-Verzeichnis zurück.
	 * 
	 * @return global konfiguriertes Archiv-Verzeichnis
	 */
	public String getGlobalDirArchive(){
		return globalDirArchive;
	}
	
	/**
	 * Setzt das global konfigurierte Archiv-Verzeichnis
	 * 
	 * @param value
	 *            Archiv-Verzeichnis
	 */
	public void setGlobalDirArchive(String value){
		globalDirArchive = value;
	}
	
	/**
	 * Gibt das, für die aktuelle Maschine konfigurierte Archiv-Verzeichnis zurück.
	 * 
	 * @return für die aktuelle Maschine konfiguriertes Archiv-Verzeichnis
	 */
	public String getMachineDirArchive(){
		return machineDirArchive;
	}
	
	/**
	 * Setzt das, für die aktuelle Maschine konfigurierte Archiv-Verzeichnis
	 * 
	 * @param value
	 *            Archiv-Verzeichnis
	 */
	public void setMachineDirArchive(String value){
		machineDirArchive = value;
	}
	
	/**
	 * Gibt das aktuell gültige Error-Verzeichnis zurück.
	 * 
	 * @return aktuell gültiges Error-Verzeichnis
	 */
	public String getDirError(){
		if (machineUseGlobalSettings)
			return globalDirError;
		else
			return machineDirError;
	}
	
	/**
	 * Gibt das global konfigurierte Error-Verzeichnis zurück.
	 * 
	 * @return global konfiguriertes Error-Verzeichnis
	 */
	public String getGlobalDirError(){
		return globalDirError;
	}
	
	/**
	 * Setzt das global konfigurierte Error-Verzeichnis
	 * 
	 * @param value
	 *            Error-Verzeichnis
	 */
	public void setGlobalDirError(String value){
		globalDirError = value;
	}
	
	/**
	 * Gibt das, für die aktuelle Maschine konfigurierte Error-Verzeichnis zurück.
	 * 
	 * @return für die aktuelle Maschine konfiguriertes Error-Verzeichnis
	 */
	public String getMachineDirError(){
		return machineDirError;
	}
	
	/**
	 * Setzt das, für die aktuelle Maschine konfigurierte Error-Verzeichnis
	 * 
	 * @param value
	 *            Error-Verzeichnis
	 */
	public void setMachineDirError(String value){
		machineDirError = value;
	}
	
	/**
	 * Gibt die aktuell gültige Dokument-Kategorie zurück.
	 * 
	 * @return aktuell gültige Dokument-Kategorie
	 */
	public String getDocumentCategory(){
		if (mandantUseGlobalSettings)
			return globalDocumentCategory;
		else
			return mandantDocumentCategory;
	}
	
	/**
	 * Gibt die global konfigurierte Dokument-Kategorie zurück.
	 * 
	 * @return global konfigurierte Dokument-Kategorie
	 */
	public String getGlobalDocumentCategory(){
		return globalDocumentCategory;
	}
	
	/**
	 * Setzt die global konfigurierte Dokument-Kategorie
	 * 
	 * @param value
	 *            Dokument-Kategorie
	 */
	public void setGlobalDocumentCategory(String value){
		globalDocumentCategory = value;
	}
	
	/**
	 * Gibt die, für den aktuellen Mandanten konfigurierte Dokument-Kategorie zurück.
	 * 
	 * @return für den aktuellen Mandanten konfigurierte Dokument-Kategorie
	 */
	public String getMandantDocumentCategory(){
		return mandantDocumentCategory;
	}
	
	/**
	 * Setzt die, für den aktuellen Mandanten konfigurierte Dokument-Kategorie
	 * 
	 * @param value
	 *            Dokument-Kategorie
	 */
	public void setMandantDocumentCategory(String value){
		mandantDocumentCategory = value;
	}
	
	/**
	 * Gibt die aktuell gültige Einstellung zum Bereinigen des Archivverzeichnisses zurück.
	 * 
	 * @return aktuell gültige Einstellung zum Bereinigen des Archivverzeichnisses
	 */
	public int getArchivePurgeInterval(){
		if (machineUseGlobalSettings)
			return globalArchivePurgeInterval;
		else
			return machineArchivePurgeInterval;
	}
	
	/**
	 * Gibt die global konfigurierte Einstellung zum Bereinigen des Archivverzeichnisses zurück.
	 * 
	 * @return global konfigurierte Einstellung zum Bereinigen des Archivverzeichnisses
	 */
	public int getGlobalArchivePurgeInterval(){
		return globalArchivePurgeInterval;
	}
	
	/**
	 * Setzt die global konfigurierte Einstellung zum Bereinigen des Archivverzeichnisses
	 * 
	 * @param value
	 *            Einstellung zum Bereinigen des Archivverzeichnisses
	 */
	public void setGlobalArchivePurgeInterval(int value){
		globalArchivePurgeInterval = value;
	}
	
	/**
	 * Gibt die, für die akltuelle Maschine konfigurierte Einstellung zum Bereinigen des
	 * Archivverzeichnisses zurück.
	 * 
	 * @return für die akltuelle Maschine konfigurierte Einstellung zum Bereinigen des
	 *         Archivverzeichnisses
	 */
	public int getMachineArchivePurgeInterval(){
		return machineArchivePurgeInterval;
	}
	
	/**
	 * Setzt die, für die aktuelle Maschine konfigurierte Einstellung zum Bereinigen des
	 * Archivverzeichnisses
	 * 
	 * @param value
	 *            Einstellung zum Bereinigen des Archivverzeichnisses
	 */
	public void setMachineArchivePurgeInterval(int value){
		machineArchivePurgeInterval = value;
	}
	
	/**
	 * Lädt die aktuell gespeicherten Einstellungen
	 */
	public void loadSettings(){
		String settingText;
		String temp;
		
		globalDocumentCategory = null;
		
		// Globale Settings
		globalJMedTransferJar = globalCfg.get(cfgJMedTransferJar, ""); //$NON-NLS-1$
		globalJMedTransferParam = globalCfg.get(cfgJMedTransferParam, ""); //$NON-NLS-1$
		globalDirDownload = globalCfg.get(cfgDirDownload, ""); //$NON-NLS-1$
		globalDirArchive = globalCfg.get(cfgDirArchive, ""); //$NON-NLS-1$
		globalDirError = globalCfg.get(cfgDirError, ""); //$NON-NLS-1$
		temp = globalCfg.get(cfgArchivePurgeInterval, ""); //$NON-NLS-1$
		try {
			globalArchivePurgeInterval = Integer.parseInt(temp);
		} catch (Exception e) {}
		globalDocumentCategory = globalCfg.get(cfgDocumentCategory, ""); //$NON-NLS-1$
		
		// Mandanten Settings
		settingText = mandantCfg.get(cfgMandantUseGlobalSettings, "true"); //$NON-NLS-1$
		mandantUseGlobalSettings = Boolean.parseBoolean(settingText);
		mandantDocumentCategory = mandantCfg.get(cfgDocumentCategory, ""); //$NON-NLS-1$
		
		// Machine Settings
		settingText = machineCfg.get(cfgMachineUseGlobalSettings, "true"); //$NON-NLS-1$
		machineUseGlobalSettings = Boolean.parseBoolean(settingText);
		machineJMedTransferJar = machineCfg.get(cfgJMedTransferJar, ""); //$NON-NLS-1$
		machineJMedTransferParam = machineCfg.get(cfgJMedTransferParam, ""); //$NON-NLS-1$
		machineDirDownload = machineCfg.get(cfgDirDownload, ""); //$NON-NLS-1$
		machineDirArchive = machineCfg.get(cfgDirArchive, ""); //$NON-NLS-1$
		machineDirError = machineCfg.get(cfgDirError, ""); //$NON-NLS-1$
		temp = machineCfg.get(cfgArchivePurgeInterval, ""); //$NON-NLS-1$
		try {
			machineArchivePurgeInterval = Integer.parseInt(temp);
		} catch (Exception e) {}
	}
	
	/**
	 * Speichert die aktuellen Einstellungen
	 */
	public void saveSettings(){
		
		// Globale Settings
		globalCfg.set(cfgJMedTransferJar, globalJMedTransferJar);
		globalCfg.set(cfgJMedTransferParam, globalJMedTransferParam);
		globalCfg.set(cfgDirDownload, globalDirDownload);
		globalCfg.set(cfgDirArchive, globalDirArchive);
		globalCfg.set(cfgDirError, globalDirError);
		globalCfg.set(cfgArchivePurgeInterval, globalArchivePurgeInterval);
		globalCfg.set(cfgDocumentCategory, globalDocumentCategory);
		globalCfg.flush();
		
		// Mandanten Settings
		mandantCfg.set(cfgMandantUseGlobalSettings, mandantUseGlobalSettings.toString());
		mandantCfg.set(cfgDocumentCategory, mandantDocumentCategory);
		mandantCfg.flush();
		
		// Machine Settings
		machineCfg.set(cfgMachineUseGlobalSettings, machineUseGlobalSettings.toString());
		machineCfg.set(cfgJMedTransferJar, machineJMedTransferJar);
		machineCfg.set(cfgJMedTransferParam, machineJMedTransferParam);
		machineCfg.set(cfgDirDownload, machineDirDownload);
		machineCfg.set(cfgDirArchive, machineDirArchive);
		machineCfg.set(cfgDirError, machineDirError);
		machineCfg.set(cfgArchivePurgeInterval, machineArchivePurgeInterval);
		machineCfg.flush();
	}
}
