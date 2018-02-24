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
import java.util.Map;
import java.util.Set;

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
	public static final String cfgFormsArchivePurgeInterval = cfgBase + "/forms/archivePurgeIntervalDays"; //$NON-NLS-1$
	
	/**
	 * The link to the MedNet.exe file
	 */
	private Path exePath;
	/**
	 * The number of days after which a form can be removed from the archive folder
	 */
	private int formsArchivePurgeInterval;
	
	/**
	 * This configuration is delivered by MedNet
	 * Those are the Paths were the Formulars will be stored after been sent
	 */
	private Map<String,MedNetConfigFormPath> configFormPaths = null;
	
	/**
	 * This configuration is delivered by MedNet
	 * Those are the Paths were the Document will be stored when importing document
	 */
	private Set<MedNetConfigDocumentPath> configDocumentPaths = null;

	/**
	 * This configuration is delivered by MedNet
	 * Those are the name of the formulars and of the institutions
	 */
	private Map<String,Map<String,MedNetConfigFormItem>> configFormItems = null;
			
	public MedNetSettings(){
		this.loadSettings();
	}
	
	public Path getExePath() {
		return this.exePath;
	}

	public void setExePath(Path path) {
		this.exePath = path;
	}
	
	public int getFormsArchivePurgeInterval() {
		return this.formsArchivePurgeInterval;
	}

	public void setFormsArchivePurgeInterval(int interval) {
		this.formsArchivePurgeInterval = interval;
	}

	public Map<String, MedNetConfigFormPath> getConfigFormPaths() {
		if(this.configFormPaths == null && this.exePath != null) {
			//Load the configuration by calling MedNet
			this.configFormPaths = MedNet.export_ConfigForms();
		}
		return this.configFormPaths;
	}

	public Set<MedNetConfigDocumentPath> getConfigDocumentPaths() {
		if(this.configDocumentPaths == null && this.exePath != null) {
			//Load the configuration by calling MedNet
			this.configDocumentPaths = MedNet.export_ConfigResults();
		}
		return this.configDocumentPaths;
	}

	public Map<String, Map<String, MedNetConfigFormItem>> getConfigFormItems() {
		if(this.configFormItems == null && this.exePath != null) {
			//Load the configuration by calling MedNet
			this.configFormItems = MedNet.listForms();
		}
		return this.configFormItems;
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
		
	}
	
	/**
	 * Save all settings
	 */
	public void saveSettings(){
		
		// Global Settings
		if(exePath != null) {
			configuration.set(cfgExePath, exePath.toString());
		}
		configuration.set(cfgFormsArchivePurgeInterval, formsArchivePurgeInterval);
		
		configuration.flush();
	}
	
}
