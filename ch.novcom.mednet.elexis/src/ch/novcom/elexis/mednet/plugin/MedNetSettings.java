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
package ch.novcom.elexis.mednet.plugin;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.io.Settings;

/**
 * Object that manage and store the simple MedNet settings
 */
public class MedNetSettings {
	public static final String PLUGIN_ID = "ch.novcom.elexis.mednet.plugin"; //$NON-NLS-1$
	public static final String cfgBase = "ch/novcom/elexis/mednet/plugin"; //$NON-NLS-1$
	
	Settings configuration = CoreHub.globalCfg; // Settings: DB für alle PCs und Mandanten
	
	// Globale Einstellungen
	public static final String cfgExePath = cfgBase + "/exe"; //$NON-NLS-1$
	public static final String cfgLogsPath = cfgBase + "/logs/path"; //$NON-NLS-1$
	public static final String cfgLogsLevel = cfgBase + "/logs/level"; //$NON-NLS-1$
	public static final String cfgFormsGDTPath = cfgBase + "/forms/gdt"; //$NON-NLS-1$
	public static final String cfgFormsPath = cfgBase + "/forms/in"; //$NON-NLS-1$
	public static final String cfgFormsErrorPath = cfgBase + "/forms/error"; //$NON-NLS-1$
	public static final String cfgFormsArchivePath = cfgBase + "/forms/archive"; //$NON-NLS-1$
	public static final String cfgFormsArchivePurgeInterval = cfgBase + "/forms/archivePurgeIntervalDays"; //$NON-NLS-1$
	
	Path exePath;
	Path logsPath;
	Level logsLevel;
	Path formsGDTPath;
	Path formsPath;
	Path formsErrorPath;
	Path formsArchivePath;
	int formsArchivePurgeInterval;
	
	public Path getExePath() {
		return this.exePath;
	}

	public void setExePath(Path path) {
		this.exePath = path;
	}
	
	public Path getLogsPath() {
		return this.logsPath;
	}

	public void setLogsPath(Path path) {
		this.logsPath = path;
	}


	public Level getLogsLevel() {
		return this.logsLevel;
	}

	public void setLogsLevel(Level level) {
		this.logsLevel = level;
	}
	
	public Path getFormsGDTPath() {
		return this.formsGDTPath;
	}

	public void setFormsGDTPath(Path path) {
		this.formsGDTPath = path;
	}

	public Path getFormsPath() {
		return this.formsPath;
	}

	public void setFormsPath(Path path) {
		this.formsPath = path;
	}

	public Path getFormsErrorPath() {
		return formsErrorPath;
	}

	public void setFormsErrorPath(Path path) {
		this.formsErrorPath = path;
	}
	
	public Path getFormsArchivePath() {
		return formsArchivePath;
	}

	public void setFormsArchivePath(Path path) {
		this.formsArchivePath = path;
	}

	public int getFormsArchivePurgeInterval() {
		return formsArchivePurgeInterval;
	}

	public void setFormsArchivePurgeInterval(int interval) {
		this.formsArchivePurgeInterval = interval;
	}

	/**
	 * Lädt die aktuell gespeicherten Einstellungen
	 */
	public void loadSettings(){
		String temp;
		
		// Globale Settings
		exePath = Paths.get(configuration.get(cfgExePath, "")); //$NON-NLS-1$
		logsPath = Paths.get(configuration.get(cfgLogsPath, "")); //$NON-NLS-1$
		logsLevel = logLevelFromString(configuration.get(cfgLogsLevel, "")); //$NON-NLS-1$
		formsGDTPath = Paths.get(configuration.get(cfgFormsGDTPath, "")); //$NON-NLS-1$
		formsPath = Paths.get(configuration.get(cfgFormsPath, "")); //$NON-NLS-1$
		formsArchivePath = Paths.get(configuration.get(cfgFormsArchivePath, "")); //$NON-NLS-1$

		temp = configuration.get(cfgFormsArchivePurgeInterval, ""); //$NON-NLS-1$
		try {
			formsArchivePurgeInterval = Integer.parseInt(temp);
		} catch (Exception e) {}
		
		formsErrorPath = Paths.get(configuration.get(cfgFormsErrorPath, "")); //$NON-NLS-1$
		
	}
	
	/**
	 * Speichert die aktuellen Einstellungen
	 */
	public void saveSettings(){
		
		// Globale Settings
		configuration.set(cfgExePath, exePath.toString());
		configuration.set(cfgLogsPath, logsPath.toString());
		configuration.set(cfgLogsLevel, logLevelToString(logsLevel));
		configuration.set(cfgFormsGDTPath, formsGDTPath.toString());
		configuration.set(cfgFormsPath, formsPath.toString());
		configuration.set(cfgFormsArchivePath, formsArchivePath.toString());
		configuration.set(cfgFormsArchivePurgeInterval, formsArchivePurgeInterval);
		configuration.set(cfgFormsErrorPath, formsErrorPath.toString());
		
		configuration.flush();
		
	}
	
	public static String[] getAvailableLogLevels(){
		return new String[]{
				"info",
				"warning",
				"severe",
				"fine",
				"finer",
				"finest",
				"all",
				"off",
				"config"
		};
	}
	
	private Level logLevelFromString(String level){
		switch(level){
			case "info": return Level.INFO;
			case "warning": return Level.WARNING;
			case "severe": return Level.SEVERE;
			case "fine": return Level.FINE;
			case "finer": return Level.FINER;
			case "finest": return Level.FINEST;
			case "all": return Level.ALL;
			case "off": return Level.OFF;
			case "config": return Level.CONFIG;
			default: return Level.INFO;
		}
	}
	
	private String logLevelToString(Level level){
		if(level.equals(Level.INFO)){
			return "info";
		}
		else if(level.equals(Level.WARNING)){
			return "warning";
		}
		else if(level.equals(Level.SEVERE)){
			return "severe";
		}
		else if(level.equals(Level.FINE)){
			return "fine";
		}
		else if(level.equals(Level.FINER)){
			return "finer";
		}
		else if(level.equals(Level.FINEST)){
			return "finest";
		}
		else if(level.equals(Level.ALL)){
			return "all";
		}
		else if(level.equals(Level.OFF)){
			return "off";
		}
		else if(level.equals(Level.CONFIG)){
			return "config";
		}
		else {
			return "info";
		}
	}
}
