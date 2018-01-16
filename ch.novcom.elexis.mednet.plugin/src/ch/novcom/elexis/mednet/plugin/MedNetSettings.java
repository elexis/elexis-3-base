/*******************************************************************************
 * Copyright (c) 2018 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht - novcom AG
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.io.Settings;

/**
 * Object that manage and store the simple MedNet settings
 */
public class MedNetSettings {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(MedNetSettings.class.getName());
	public static final String PLUGIN_ID = "ch.novcom.elexis.mednet.plugin"; //$NON-NLS-1$
	public static final String cfgBase = "ch/novcom/elexis/mednet/plugin"; //$NON-NLS-1$
	
	Settings configuration = CoreHub.globalCfg; // Settings: DB for all PCs
	
	// Globale Einstellungen
	public static final String cfgExePath = cfgBase + "/exe"; //$NON-NLS-1$
	public static final String cfgLogsPath = cfgBase + "/logs/path"; //$NON-NLS-1$
	public static final String cfgLogsLevel = cfgBase + "/logs/level"; //$NON-NLS-1$
	public static final String cfgFormsPath = cfgBase + "/forms/in"; //$NON-NLS-1$
	public static final String cfgFormsErrorPath = cfgBase + "/forms/error"; //$NON-NLS-1$
	public static final String cfgFormsArchivePath = cfgBase + "/forms/archive"; //$NON-NLS-1$
	public static final String cfgFormsArchivePurgeInterval = cfgBase + "/forms/archivePurgeIntervalDays"; //$NON-NLS-1$
	
	/**
	 * The link to the MedNet.exe file
	 */
	private Path exePath;
	/**
	 * The folder were MedNet save the filled forms
	 */
	private Path formsPath;
	/**
	 * The folder were a forms should be moved if it cannot been integrated into Elexis
	 */
	private Path formsErrorPath;
	/**
	 * The folder were the forms will be moved after they were successfully integrated into elexis
	 */
	private Path formsArchivePath;
	/**
	 * The number of days after which a form can be removed from the archive folder
	 */
	private int formsArchivePurgeInterval;
	
	
	public MedNetSettings(){
		this.loadSettings();
	}
	
	public Path getExePath() {
		return this.exePath;
	}

	public void setExePath(Path path) {
		this.exePath = path;
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
	 * Load all saved configuration informations
	 */
	public void loadSettings(){
		String logPrefix = "loadSettings() - ";//$NON-NLS-1$
		
		// Global Settings
		String exePathString = configuration.get(cfgExePath, "");
		if(	exePathString != null && 
			!exePathString.isEmpty()	) {
			exePath = Paths.get(exePathString); 
			if(!Files.isRegularFile(exePath)) {
				//If the exe does no more exists
				LOGGER.error(logPrefix+"MedNet exe path: "+exePath.toString()+" is not a valid file");//$NON-NLS-1$
				exePath = null;
			}
		}
		
		String formsPathString =configuration.get(cfgFormsPath, "");//$NON-NLS-1$
		if(	formsPathString != null && 
			!formsPathString.isEmpty()	) {
			formsPath = Paths.get(formsPathString);
			if(!Files.isDirectory(formsPath)) {
				//If the formPath does no more exists
				LOGGER.error(logPrefix+"Form path: "+formsPath.toString()+" is not a valid directory");//$NON-NLS-1$
				formsPath = null;
			}
		}
		else {
			formsPath = null;
		}
		
		String formsArchivePathString = configuration.get(cfgFormsArchivePath, "");

		if(	formsArchivePathString != null && 
			!formsArchivePathString.isEmpty()	) {
			formsArchivePath = Paths.get(formsArchivePathString); //$NON-NLS-1$
			if(!Files.isDirectory(formsArchivePath)) {
				//If the formsArchivePath does no more exists
				LOGGER.error(logPrefix+"Form archive path: "+formsArchivePath.toString()+" is not a valid directory");//$NON-NLS-1$
				formsArchivePath = null;
			}
		}
		else {
			formsArchivePath = null;
		}

		String cfgFormsArchivePurgeIntervalString = configuration.get(cfgFormsArchivePurgeInterval, ""); //$NON-NLS-1$
		if(	cfgFormsArchivePurgeIntervalString != null && 
				!cfgFormsArchivePurgeIntervalString.isEmpty()	) {
			try {
				formsArchivePurgeInterval = Integer.parseInt(cfgFormsArchivePurgeIntervalString);
			} catch (Exception e) {
				formsArchivePurgeInterval = -1 ;
				LOGGER.error(logPrefix+"Form archive purge interval: "+cfgFormsArchivePurgeIntervalString+" is not a valid number");//$NON-NLS-1$
			}
		}
		else {
			formsArchivePurgeInterval = -1 ;
		}
		
		String formsErrorPathString = configuration.get(cfgFormsErrorPath, "");//$NON-NLS-1$
		if(	formsErrorPathString != null && 
			!formsErrorPathString.isEmpty()	) {
			formsErrorPath = Paths.get(formsErrorPathString);
			if(!Files.isDirectory(formsArchivePath)) {
				//If the formsErrorPath does no more exists
				LOGGER.error(logPrefix+"Form error path: "+formsErrorPath.toString()+" is not a valid directory");//$NON-NLS-1$
				formsErrorPath = null;
			}
		}
		else {
			formsErrorPath = null;
		}
	}
	
	/**
	 * Save all settings
	 */
	public void saveSettings(){
		
		// Global Settings
		if(exePath != null) {
			configuration.set(cfgExePath, exePath.toString());
		}
		if(formsPath != null) {
			configuration.set(cfgFormsPath, formsPath.toString());
		}
		if(formsArchivePath != null) {
			configuration.set(cfgFormsArchivePath, formsArchivePath.toString());
		}
		configuration.set(cfgFormsArchivePurgeInterval, formsArchivePurgeInterval);
		if(formsErrorPath != null) {
			configuration.set(cfgFormsErrorPath, formsErrorPath.toString());
		}
		
		configuration.flush();
	}
	
	
}
