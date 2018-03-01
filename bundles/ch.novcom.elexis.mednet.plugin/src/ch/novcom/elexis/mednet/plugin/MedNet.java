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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * The default encoding used by MedNet when exporting configuration files
	 */
	private final static Charset OUTPUT_ENCONDING = StandardCharsets.UTF_8;
	
	
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
	
	
	/**
	 * Call the MedNet function -listForms in order to get the list of available formulars and there names
	 */
	public static Map<String,Map<String,MedNetConfigFormItem>> listForms(){
		String logPrefix = "listForms() - ";//$NON-NLS-1$
		
		LOGGER.debug(logPrefix+"start");//$NON-NLS-1$
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-listForms");//$NON-NLS-1$
		
		//Create a tempFile that will contain the result
		Path file = null;
		try {
			file = Files.createTempFile("mednet-listform", ".dt");//$NON-NLS-1$
		}
		catch(IOException ioe){
			//If there are some ioeException logs it
			LOGGER.error(logPrefix+"IOException creating list form file.",ioe);//$NON-NLS-1$
			return null;
		}
		command.add("-output:"+file.toString());		
		
		LOGGER.debug(logPrefix + "launch MedNet "+String.join(" ", command));//$NON-NLS-1$
		
		ProcessBuilder probuilder = new ProcessBuilder(command);
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());

        //Wait to get exit value
        int exitValue = -1;
        //The list were we will put the content of the generated file
		List<String> resultStringList = new ArrayList<String>();
		
		try {
			//Call MedNet -listForms
			Process process = probuilder.start();
			
	        try {
	            exitValue = process.waitFor();
	        } catch (InterruptedException ie) {
	        	LOGGER.warn(logPrefix + "Has been interrupted");//$NON-NLS-1$
	        }
		} catch (IOException ioe) {
        	LOGGER.error(logPrefix + "IOException: ", ioe);//$NON-NLS-1$
        }

		//If it returns 0 -> All is OK the files has successfully been received
		if(exitValue >= 0 ){
			LOGGER.debug(logPrefix + "Successful");//$NON-NLS-1$

			//Read the file line by line 
			//and add each line into the resulting list
			try {
				resultStringList = Files.readAllLines(file, MedNet.OUTPUT_ENCONDING);
			}
			catch(IOException | SecurityException ioe) {
	        	LOGGER.error(logPrefix + "Exception reading configuration File ", ioe);//$NON-NLS-1$
			}
			
		}
		else {
			LOGGER.debug(logPrefix + "Failed");//$NON-NLS-1$
		}
		
		//remove the temporary file
		try {
			Files.deleteIfExists(file);
		}
		catch(IOException | SecurityException ioe) {
			//Just ignore this
		}
		
		Map<String,Map<String,MedNetConfigFormItem>> result = new TreeMap<String,Map<String,MedNetConfigFormItem>>();
		
		//Parse the String list
		for(String line : resultStringList) {
			MedNetConfigFormItem item = new MedNetConfigFormItem(line);
			if(!result.containsKey(item.getInstitutionID())) {
				Map<String,MedNetConfigFormItem> institutionMap = new TreeMap<String,MedNetConfigFormItem>();
				result.put(item.getInstitutionID(), institutionMap ); 
			}
			result.get(item.getInstitutionID()).put(item.getFormID(), item);
		}
		
		//finally return the temporary fileCreated
		return result;
	}
	

	/**
	 * Call the MedNet function -export_ConfigForms in order to get the configuration of formulars exported
	 */
	public static Map<String,MedNetConfigFormPath> export_ConfigForms(){
		String logPrefix = "export_ConfigForms() - ";//$NON-NLS-1$
		
		LOGGER.debug(logPrefix+"start");//$NON-NLS-1$
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-export_ConfigForms");//$NON-NLS-1$
		
		//Create a tempFile that will contain the result
		Path file = null;
		try {
			file = Files.createTempFile("mednet-export_ConfigForms", ".dt");//$NON-NLS-1$
		}
		catch(IOException ioe){
			//If there are some ioeException logs it
			LOGGER.error(logPrefix+"IOException creating config form file.",ioe);//$NON-NLS-1$
			return null;
		}
		command.add("-output:"+file.toString());
		
		ProcessBuilder probuilder = new ProcessBuilder( command );
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());

        //Wait to get exit value
        int exitValue = -1;
        //The list were we will put the content of the generated file
		List<String> resultStringList = new ArrayList<String>();
		
		try {
			//Call MedNet -export_ConfigForms
			Process process = probuilder.start();
			
	        try {
	            exitValue = process.waitFor();
	        } catch (InterruptedException ie) {
	        	LOGGER.warn(logPrefix + "Has been interrupted");//$NON-NLS-1$
	        }
		} catch (IOException ioe) {
        	LOGGER.error(logPrefix + "IOException: ", ioe);//$NON-NLS-1$
        }

		//If it returns 0 -> All is OK the files has successfully been received
		if(exitValue >= 0 ){
			LOGGER.debug(logPrefix + "Successful");//$NON-NLS-1$

			//Read the file line by line 
			//and add each line into the resulting list
			try {
				resultStringList = Files.readAllLines(file, MedNet.OUTPUT_ENCONDING);
			}
			catch(IOException | SecurityException ioe) {
	        	LOGGER.error(logPrefix + "Exception reading configuration File ", ioe);//$NON-NLS-1$
			}
			
		}
		else {
			LOGGER.debug(logPrefix + "Failed");//$NON-NLS-1$
		}
		
		//remove the temporary file
		try {
			Files.deleteIfExists(file);
		}
		catch(IOException | SecurityException ioe) {
			//Just ignore this
		}
		
		Map<String,MedNetConfigFormPath> result = new TreeMap<String,MedNetConfigFormPath>();
		
		//Parse the String list
		for(String line : resultStringList) {
			MedNetConfigFormPath item = new MedNetConfigFormPath(line);
			result.put(item.getAccountID(), item);
		}
		
		//finally return the temporary fileCreated
		return result;
	}

	
	/**
	 * Call the MedNet function -export_ConfigResults in order to get the configuration of received documents
	 */
	public static Set<MedNetConfigDocumentPath> export_ConfigResults(){
		String logPrefix = "export_ConfigResults() - ";//$NON-NLS-1$
		
		LOGGER.debug(logPrefix+"start");//$NON-NLS-1$
		
		//Prepare the parameters List
		ArrayList<String> command = new ArrayList<String>();
		command.add(MedNet.getSettings().getExePath().toString());
		command.add("-export_ConfigResults");//$NON-NLS-1$
		
		//Create a tempFile that will contain the result
		Path file = null;
		try {
			file = Files.createTempFile("mednet-export_ConfigResults", ".dt");//$NON-NLS-1$
		}
		catch(IOException ioe){
			//If there are some ioeException logs it
			LOGGER.error(logPrefix+"IOException creating config result file.",ioe);//$NON-NLS-1$
			return null;
		}
		command.add("-output:"+file.toString());
		
		ProcessBuilder probuilder = new ProcessBuilder( command );
		probuilder.directory(MedNet.getSettings().getExePath().getParent().toFile());

        //Wait to get exit value
        int exitValue = -1;
        //The list were we will put the content of the generated file
		List<String> resultStringList = new ArrayList<String>();
		
		try {
			//Call MedNet -export_ConfigResults
			Process process = probuilder.start();
			
	        try {
	            exitValue = process.waitFor();
	        } catch (InterruptedException ie) {
	        	LOGGER.warn(logPrefix + "Has been interrupted");//$NON-NLS-1$
	        }
		} catch (IOException ioe) {
        	LOGGER.error(logPrefix + "IOException: ", ioe);//$NON-NLS-1$
        }

		//If it returns 0 -> All is OK the files has successfully been received
		if(exitValue >= 0 ){
			LOGGER.debug(logPrefix + "Successful");//$NON-NLS-1$

			//Read the file line by line 
			//and add each line into the resulting list
			try {
				resultStringList = Files.readAllLines(file, MedNet.OUTPUT_ENCONDING);
			}
			catch(IOException | SecurityException ioe) {
	        	LOGGER.error(logPrefix + "Exception reading configuration File ", ioe);//$NON-NLS-1$
			}
			
		}
		else {
			LOGGER.debug(logPrefix + "Failed");//$NON-NLS-1$
		}
		
		//remove the temporary file
		try {
			Files.deleteIfExists(file);
		}
		catch(IOException | SecurityException ioe) {
			//Just ignore this
		}
		
		Set<MedNetConfigDocumentPath> result = new TreeSet<MedNetConfigDocumentPath>();
		
		//Parse the String list
		for(String line : resultStringList) {
			MedNetConfigDocumentPath item = new MedNetConfigDocumentPath(line);
			result.add(item);
		}
		
		//finally return the temporary fileCreated
		return result;
	}
	
		
}
