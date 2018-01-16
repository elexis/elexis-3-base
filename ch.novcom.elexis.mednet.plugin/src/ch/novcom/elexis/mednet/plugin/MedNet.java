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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will 
 * Integrate the calls to the MedNet functions
 */
public class MedNet {

	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(MedNet.class.getName());
	
	/**
	 * The Object that will store all the Settings needed by the plugin
	 */
	private static MedNetSettings settings = new MedNetSettings();
	
	/**
	 * Returns a MedNetSettings Object that contains all the Settings needed for the plugin
	 * @return
	 */
	public static MedNetSettings getSettings(){
		if (MedNet.settings == null) {
			MedNet.settings = new MedNetSettings();
		}
		return MedNet.settings;
	}
	
	/**
	 * Call the MedNet function -getResults in order to Download new documents available throw MedNet
	 */
	public static void getDocuments(){
		String logPrefix = "getDocuments() - ";//$NON-NLS-1$
		
		LOGGER.debug(logPrefix + "start");//$NON-NLS-1$
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-getResults");//$NON-NLS-1$
		
		ProcessBuilder probuilder = new ProcessBuilder( command );
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());
		
		try {
			//Call MedNet -getResults
			Process process = probuilder.start();
			
	        //Wait to get exit value
	        int exitValue = 1;
	        try {
	            exitValue = process.waitFor();
	            
				//If it returns 0 -> All is OK the files has successfully been received
				if(exitValue >= 0 ){
					LOGGER.debug(logPrefix + "Successful");//$NON-NLS-1$
				}
				else {
					LOGGER.debug(logPrefix + "Failed");//$NON-NLS-1$
				}
	            
	        } catch (InterruptedException ie) {
	        	LOGGER.warn(logPrefix + "Has been interrupted");//$NON-NLS-1$
	        }
		} catch (IOException ioe) {
        	LOGGER.error(logPrefix + "IOException: ", ioe);//$NON-NLS-1$
        }
		
	}
	
	/**
	 * Call the MedNet function -openformview in order to open the formview 
	 * If a gdt file exists, it will be passed as parameter in order to prefill the form with 
	 * the patient informations
	 * @param gdtFile the gdt file that contains the patient information to forward to mednet 
	 */
	public static void openFormview(Path gdtFile){
		String logPrefix = "openFormview() - ";//$NON-NLS-1$
		
		LOGGER.debug(logPrefix+"start");//$NON-NLS-1$
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-openformview");//$NON-NLS-1$
		if(gdtFile != null && Files.exists(gdtFile) && Files.isRegularFile(gdtFile)){
			command.add("-patientinfo:\""+gdtFile.toString()+"\"");//$NON-NLS-1$
		}
		ProcessBuilder probuilder = new ProcessBuilder( command );
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());
		
		try {
			//We don't wait for openFormView to close
			//Just launch MedNet
			probuilder.start();
			LOGGER.debug(logPrefix+"done");//$NON-NLS-1$
		} catch (IOException ioe) {
        	LOGGER.error(logPrefix+"IOException: ", ioe);//$NON-NLS-1$
        }
	}
		
}
