/*******************************************************************************
 * Copyright (c) 2017 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.novcom.elexis.mednet.plugin.logging.LogFileHandler;

/**
 * Integrate the calls to the MedNet functions
 * @author david.gutknecht
 */
public class MedNet {

	protected final static SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$
	private static Logger logger = null;
	
	private static MedNetSettings settings = new MedNetSettings();
	
	/**
	 * Return a logger that will log everything in a dedicated file
	 * @return
	 */
	public static Logger getLogger(){
		if (MedNet.logger == null) {
			MedNet.logger = Logger.getLogger("MedNet");
			LogFileHandler fileHandler = new LogFileHandler(MedNet.getSettings().getLogsPath());
			fileHandler.setLevel(MedNet.getSettings().getLogsLevel());
			MedNet.logger.addHandler(fileHandler);
		}
		
		return logger;
	}
	
	public static MedNetSettings getSettings(){
		if (MedNet.settings == null) {
			MedNet.settings = new MedNetSettings();
		}
		
		return MedNet.settings;
	}
	
	/**
	 * Call the MedNet function -getResults that will download documents
	 */
	public static void getDocuments(){
		MedNet.getLogger().entering(MedNet.class.getName(), "getDocuments()");
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-getResults");
		
		ProcessBuilder probuilder = new ProcessBuilder( command );
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());
		
		try {
			Process process = probuilder.start();
			
	        //Wait to get exit value
	        int exitValue =1;
	        try {
	            exitValue = process.waitFor();
	            
				//If it returns 0 -> All is OK the files has successfully been received
				if(exitValue >= 0 ){
					MedNet.getLogger().logp(Level.FINE, MedNet.class.getName(), "getResults()","Successful");
				}
				else {
					MedNet.getLogger().logp(Level.SEVERE, MedNet.class.getName(), "getResults()","Failed");
				}
	            
	        } catch (InterruptedException ie) {
	        	MedNet.getLogger().logp(Level.WARNING, MedNet.class.getName(), "getResults()","Has been interrupted");
	        }
		} catch (IOException ioe) {
        	MedNet.getLogger().logp(Level.SEVERE, MedNet.class.getName(), "getResults()","IOException: ", ioe);
        }
		
	}
	
	/**
	 * Call the MedNet function -openformview that will open the formview 
	 * pass the gdt file set in the settings as parameter if it exists
	 */
	public static void openFormview(){
		MedNet.getLogger().entering(MedNet.class.getName(), "openFormview()");
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-openformview");
		if(Files.exists(MedNet.getSettings().getFormsGDTPath()) &&Files.isRegularFile(MedNet.getSettings().getFormsGDTPath())){
			command.add("-patientinfo:\""+MedNet.getSettings().getFormsGDTPath().toString()+"\"");
		}
		ProcessBuilder probuilder = new ProcessBuilder( command );
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());
		
		try {
			Process process = probuilder.start();
			
	        //Wait to get exit value
	        int exitValue =1;
	        try {
	            exitValue = process.waitFor();
	            
				//If it returns 0 -> All is OK the files has successfully been received
				if(exitValue >= 0 ){
		        	MedNet.getLogger().logp(Level.FINE, MedNet.class.getName(), "openFormview()","Successful");
				}
				else {
		        	MedNet.getLogger().logp(Level.SEVERE, MedNet.class.getName(), "openFormview()","Failed");
				}
	            
	        } catch (InterruptedException ie) {
	        	MedNet.getLogger().logp(Level.WARNING, MedNet.class.getName(), "openFormview()","Has been interrupted");
	        }
		} catch (IOException ioe) {
        	MedNet.getLogger().logp(Level.SEVERE, MedNet.class.getName(), "openFormview()","IOException: ", ioe);
        }
		
	}
		
}
