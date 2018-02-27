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
package ch.novcom.elexis.mednet.plugin.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.data.Kontakt;
import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;


/**
 * This class is used to import a Documents to a Patient
 * It is used by the DocumentImporter class
 *
 */
public class PatientDocumentManager {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentImporter.class.getName());
	
	public static String DEFAULT_PRIO = "0";
	
	private static String FLD_ORGIN = "Quelle";
	
	private static int MAX_LEN_RESULT = 80; // Length of the column LABORWERTE.Result
	
	private final Patient patient;
	
	private IDocumentManager omnivoreDocManager;
	
	private boolean overwriteResults = false;
	
	private static SimpleDateFormat LABRESULT_TIMESTAMP_FORMATTER = new SimpleDateFormat("yyyyMMddHHmmss");
	private static SimpleDateFormat LABRESULT_TIME_FORMATTER = new SimpleDateFormat("HHmmss");
	
	/**
	 * Constructor
	 * @param patient
	 */
	public PatientDocumentManager(Patient patient){
		super();
		this.patient = patient;
		this.initDocumentManager();
	}
	
	/**
	 * Try to initialize Omnivore
	 */
	private void initDocumentManager(){
		String logPrefix = "initDocumentManager() - ";//$NON-NLS-1$
		Object omnivore = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (omnivore != null) {
			this.omnivoreDocManager = (IDocumentManager) omnivore;
		}
		else {
			LOGGER.error(logPrefix+"Omnivore has not been loaded");//$NON-NLS-1$
		}
	}
	
	/**
	 * Check if the given Category already exists into Omnivore
	 * If not, create it
	 */
	private void checkCreateCategory(final String category){
		String logPrefix = "checkCreateCategory() - ";//$NON-NLS-1$
		if (category != null) {
			boolean catExists = false;
			String[] categories = this.omnivoreDocManager.getCategories();
			if (categories != null) {
				for (String cat : categories) {
					if (category.equals(cat)) {
						catExists = true;
						break;
					}
				}
			}
			//If the category doesn't exist create it
			if (!catExists) {
				Boolean success = this.omnivoreDocManager.addCategorie(category);
				if (success) {
					LOGGER.info(logPrefix+"New Document category created in Omnivore: " + category);//$NON-NLS-1$
				} else {
					LOGGER.error(logPrefix+"Failed creating new document category in Omnivore: " + category);//$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Add a new document to Omnivore
	 * @param title
	 * @param category
	 * @param dateStr
	 * @param file
	 * @param keywords
	 * @return true if was successful
	 * @throws IOException
	 * @throws ElexisException
	 */
	private boolean addDocumentToOmnivore(
			final String title,
			final String category,
			final String dateStr,
			final Path file,
			final String keywords
		) throws IOException, ElexisException{
		String logPrefix = "addDocumentToOmnivore() - ";//$NON-NLS-1$
		
		this.checkCreateCategory(category);
		
		//First check if the document is not already in the database
		List<IOpaqueDocument> documentList =
			this.omnivoreDocManager.listDocuments(
				this.patient,
				category,
				title,
				null,
				new TimeSpan(dateStr+ "-" + dateStr),
				null
		);
		
		//If no document has been found, we can add it to the database
		if (documentList == null || documentList.size() == 0) {
			
			//Get the document mimeType
			String mimeType = null;
			try {
				mimeType = Files.probeContentType(file);
				LOGGER.debug(logPrefix+"Mimetype for "+file.toString()+"  is "+mimeType);//$NON-NLS-1$
			}
			catch(IOException | SecurityException e) {
				//ignore exceptions
				LOGGER.warn(logPrefix+"Unable to find the mimetype of the following file "+file.toString());//$NON-NLS-1$
			}
			
			this.omnivoreDocManager.addDocument(
				new GenericDocument(
						this.patient,
						title,
						category,
						file.toFile(),
						dateStr,
						keywords,
						mimeType
				)
			);
			//If the document has successfully been added
			LOGGER.debug(logPrefix+"This document has successfully been added to the omnivore database."+file.toString());//$NON-NLS-1$
			
			return true;
		}
		else {
			//If the document already exists, log it
			LOGGER.warn(logPrefix+"This document already exists in the omnivore database. It will not be imported: " + title +" "+file.toString());//$NON-NLS-1$
			return false;
		}
	}
	
	/**
	 * Search for a LabItem
	 * A LabItem is an AnalyzeCode that is linked to an institution
	 * For the documents we will define an AnalyzeCode (shortname) and name based on the category
	 * @param shortname
	 * @param name
	 * @param type
	 * @return the LabItem found or null
	 */
	private LabItem getLabItem(String institutionId, String shortname, LabItemTyp type){
		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		qli.add(LabItem.SHORTNAME, "=", shortname);
		qli.and();
		qli.add(LabItem.LAB_ID, "=", institutionId);
		qli.and();
		qli.add(LabItem.TYPE, "=", new Integer(type.ordinal()).toString()); //$NON-NLS-1$
		
		LabItem labItem = null;
		List<LabItem> itemList = qli.execute();
		
		if (itemList.size() > 0) {
			//If we find multiple items, take the first one
			labItem = itemList.get(0);
		}
		return labItem;
	}
	
	/**
	 * Look for a result
	 * @param labItem the corresponding labItem this result should be part of
	 * @param value the value of the result
	 * @param date the date of the result
	 * @return The result we found or null
	 */
	private LabResult getLabResult(LabItem labItem, String value, Date date){
		Query<LabResult> qli = new Query<LabResult>(LabResult.class);
		qli.add(LabResult.ITEM_ID, "=", labItem.getId());//$NON-NLS-1$
		qli.and();
		qli.add(LabResult.OBSERVATIONTIME, "=", PatientDocumentManager.LABRESULT_TIMESTAMP_FORMATTER.format(date));//$NON-NLS-1$
		qli.and();
		qli.add(LabResult.PATIENT_ID, "=", patient.getId());//$NON-NLS-1$
		qli.and();
		qli.add(LabResult.RESULT, "=", value);//$NON-NLS-1$
		
		LabResult labResult = null;
		List<LabResult> resultList = qli.execute();
		if (resultList.size() > 0) {
			//If we find multiple items, take the first one
			labResult = resultList.get(0);
		}
		return labResult;
	}
	
	
	/**
	 * 
	 * Save a LaborItem to the Database
	 * @param institutionId
	 * @param institutionName
	 * @param category
	 * @param orderId
	 * @param file
	 * @param documentDateTime
	 * @param keyword
	 * @throws IOException
	 */
	
	public void addDocument(
			String institutionId,
			String institutionName,
			String category,
			String orderId,
			Path file,
			Date documentDateTime,
			String keywords
		) throws IOException{
		String logPrefix = "addDocument() - ";//$NON-NLS-1$
		
		//First of all check if Omnivore exists
		if (this.omnivoreDocManager == null) {
			throw new IOException(
				MessageFormat.format(
						MedNetMessages.PatientDocumentManager_omnivoreNotInitialized,
						file.toString(),
						this.patient.getLabel()
				)
			);
		}
		
		//Create the category if doesn't exists
		this.checkCreateCategory(category);
		
		//Check if there is already a labitem of this category
		LabItem labItem = getLabItem(institutionId, MedNetMessages.PatientDocumentManager_documentId, LabItemTyp.DOCUMENT);

		//Get the kontakt linked with the institution
		Kontakt institution = null;
		String group = "";
		if(institutionId != null && !institutionId.isEmpty()){
			institution = PatientDocumentManager.getInstitution(institutionId);
			group = institution.getLabel(true);
		}
		
		//If no LabItem has been found, create one
		if (labItem == null) {
			labItem =
				new LabItem(
						MedNetMessages.PatientDocumentManager_documentId, //Test Code
						MedNetMessages.PatientDocumentManager_documentTitel, //Test Name
						institution, //If institution is empty, the labitem will be linked to the internal laboratory
						"",
						"",
						"pdf",
						LabItemTyp.DOCUMENT,
						group ,
						DEFAULT_PRIO
				);//$NON-NLS-1$
		}
		
		
		//Finally create a new laboratory Result to save to the database
		
		//First of all define a title we will use for this result:
		//Set a title to the document
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(MedNetMessages.PatientDocumentManager_LabResultTitleTransactionFormat);
		
		String title = 
				MessageFormat.format(
						MedNetMessages.PatientDocumentManager_LabResultTitle,
						orderId,
						dateTimeFormatter.format(documentDateTime),
						DocumentImporter.getBaseName(file),
						DocumentImporter.getExtension(file),
						category
				);
		
		
		//Limit the length of the title
		//If it is too long, cut it
		if (title.length() > MAX_LEN_RESULT)
			title = title.substring(0,title.length() - MAX_LEN_RESULT - 3) + "..."  ;//$NON-NLS-1$
		
		//Since the labResult Object uses TimeTool,
		//We will convert the documentDateTime into a TimeTool
		TimeTool documentDate = new TimeTool();
		documentDate.setTime(documentDateTime);
		String documentTime = PatientDocumentManager.LABRESULT_TIME_FORMATTER.format(documentDateTime);
		
		boolean saved = false;
		
		//Check if a result already exists with this title, and this time
		LabResult labResult = this.getLabResult(labItem, title, documentDateTime);
		if (labResult == null) {
			//If no results exists, create one
			labResult = new LabResult(patient, documentDate, labItem, title, null);
			labResult.set(FLD_ORGIN, orderId);
			labResult.set(LabResult.TIME, documentTime);
			labResult.setObservationTime(documentDate);
			saved = true;
		} else {
			//If there is already a labresult
			//We should check if we should overwrite it
			
			//If the option overwrite results is set to true, we overwrite the result
			//If the given document is newer than the one in the database
			if (
					overwriteResults
				|| (labResult.getObservationTime().getTimeInMillis() < documentDate.getTimeInMillis())
				) {
				LOGGER.warn(logPrefix+
						"An older version of this document will be overwritten:"
								+ labItem.getKuerzel() + "-" 
								+ labItem.getName() + " " 
								+ labResult.getObservationTime().toDBString(true)+ " " 
								+ documentDate.toDBString(true)+ " " 
								+ labResult.getResult()+ " " 
								+ title+ " " 
						);//$NON-NLS-1$
				labResult.setResult(title);
				labResult.set(LabResult.TIME, documentTime);
				labResult.setObservationTime(documentDate);
				saved = true;
			} else {
				
				LOGGER.warn(
					logPrefix+
					"An new version of this document is still in the database:"
							+ labItem.getKuerzel() + "-" 
							+ labItem.getName() + " " 
							+ labResult.getObservationTime().toDBString(true)+ " " 
							+ documentDate.toDBString(true)+ " " 
							+ labResult.getResult()+ " " 
							+ title+ " " 
					);//$NON-NLS-1$
			}
		}
		
		//If we were able to create a labResult
		//We can archive the document into Omnivore
		if (saved) {
			try {
				String dateTimeDocumentString = documentDate.toString(TimeTool.DATE_GER);
				
				this.addDocumentToOmnivore(title, institutionName, dateTimeDocumentString, file, keywords);
				
				
				LOGGER.info(
						logPrefix+
						"Document successfully saved to omnivore:"+ title 
						);//$NON-NLS-1$
			} catch (ElexisException e) {
				throw new IOException(
					MessageFormat.format(
						MedNetMessages.PatientLabor_errorAddingDocumentToOmnivore, file.toString()
					),
					e
				);
			}
		}
	}
	
	
	
	/**
	 * 
	 * Save a LaborItem to the Database
	 * @param institutionId
	 * @param institutionName
	 * @param category
	 * @param orderId
	 * @param file
	 * @param documentDateTime
	 * @param keyword
	 * @throws IOException
	 */
	public void addForm(
			String category,
			String institutionName,
			String formularName,
			String orderId,
			Path file,
			Date documentDateTime,
			String keywords
		) throws IOException{
		String logPrefix = "addForm() - ";//$NON-NLS-1$
		
		//First of all check if Omnivore exists
		if (this.omnivoreDocManager == null) {
			throw new IOException(
				MessageFormat.format(
						MedNetMessages.PatientDocumentManager_omnivoreNotInitialized,
						file.toString(),
						this.patient.getLabel()
				)
			);
		}
		
		//Create the category if doesn't exists
		this.checkCreateCategory(category);
		
		//Finally create a new laboratory Result to save to the database
		
		//First of all define a title we will use for this result:
		//Set a title to the document
		SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(MedNetMessages.PatientDocumentManager_LabResultTitleTransactionFormat);
		
		String title = 
				MessageFormat.format(
						MedNetMessages.PatientDocumentManager_FormTitle,
						institutionName,
						formularName,
						orderId,
						dateTimeFormatter.format(documentDateTime),
						DocumentImporter.getBaseName(file),
						DocumentImporter.getExtension(file),
						category
				);
		
		//Limit the length of the title
		//If it is too long, cut it
		if (title.length() > MAX_LEN_RESULT)
			title = title.substring(0,title.length() - MAX_LEN_RESULT - 3) + "..."  ;//$NON-NLS-1$
		
		//Since the labResult Object uses TimeTool,
		//We will convert the documentDateTime into a TimeTool
		TimeTool documentDate = new TimeTool();
		documentDate.setTime(documentDateTime);
		
		//Archive the document into Omnivore
		try {
			String dateTimeDocumentString = documentDate.toString(TimeTool.DATE_GER);
			
			this.addDocumentToOmnivore(title, category, dateTimeDocumentString, file, keywords);
			
			LOGGER.info(
					logPrefix +
					"Document successfully saved to omnivore:"+ title 
					);//$NON-NLS-1$
		} catch (ElexisException e) {
			throw new IOException(
				MessageFormat.format(
					MedNetMessages.PatientLabor_errorAddingDocumentToOmnivore, file.toString()
				),
				e
			);
		}
		
	}
	
	
	/**
	 * This function will return the contact corresponding to the given institution id
	 * @param id
	 * @return the contact or null if nothing or multiple contacts has been found
	 */
	public static Kontakt getInstitution(String id){
		String logPrefix = "getInstitution() - ";//$NON-NLS-1$
		
		Query<Kontakt> qbe = new Query<Kontakt>(Labor.class);
		qbe.startGroup();
		qbe.add(Kontakt.FLD_ID, Query.EQUALS, id);
		qbe.endGroup();
		List<Kontakt> results = qbe.execute();
		if (results.size() == 1) {
			return results.get(0);
		} 
		else if (results.size() <= 0){
			LOGGER.warn(
					logPrefix +
					"No institution with following id found:"+id
			);
			return null;
		}
		else {
			LOGGER.warn(
					logPrefix +
					"More than one institution with following id found:"+id
			);
			return null;
		}
	}
	
	
}
