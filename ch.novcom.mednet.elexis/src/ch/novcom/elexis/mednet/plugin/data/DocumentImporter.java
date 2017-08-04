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

package ch.novcom.elexis.mednet.plugin.data;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.elexis.data.Xid.XIDException;
import ch.elexis.hl7.model.ObservationMessage;
import ch.elexis.hl7.v22.HL7_ORU_R01;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.MedNetLabItemResolver;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;


/**
 * Manage the import of HL7 and PDF Documents into the Patient
 * @author David Gutknecht
 *
 */
public class DocumentImporter {

	private final static Pattern documentFilenamePattern = Pattern.compile("^([^_]*_)*(?<uniqueMessageId>[^_]+)_(?<caseNr>[^_]*)_(?<transactionDateTime>[^_]*)_(?<orderNr>[^_]+)_(?<samplingDateTime>[^_]*)_(?<PatientLastName>[^_]*)_(?<PatientBirthdate>[^_]*)_(?<PatientId>[^_]*)_(?<recipient>\\d+)$");
	private final static Pattern formFilenamePattern = Pattern.compile("^(?<transactionDateTime>[^_]*)_(?<sender>[^_]*)_(?<PatientId>[^_]*)_(?<PatientLastName>[^_]*)_(?<PatientFirstName>[^_]*)_(?<PatientBirthdate>[^_]*)_(?<formGroupId>[^_]*)_(?<formId>[^_]*)_(?<orderNr>[^_]*)$");
	private final static SimpleDateFormat documentDateTimeParser = new SimpleDateFormat("yyyyMMddHHmmss");

	private final static SimpleDateFormat birthdateParser = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat birthdateHumanReadableFormatter = new SimpleDateFormat("dd-MM-yyyy");
	
	
	private static Charset HL7_ENCODING = Charset.forName("ISO-8859-1");
	
	
	/**
	 * Try to import an hl7 and a pdf
	 * @param the hl7File to import
	 * @param the pdfFile to import
	 * @param institutionId
	 * @param institutionName
	 * @throws IOException
	 */
	public static boolean process(
			Path hl7File,
			Path pdfFile,
			String institutionId,
			String institutionName,
			String category,
			boolean overwriteOlderEntries
		) throws IOException{
		return process(
				hl7File,
				pdfFile,
				institutionId,
				institutionName,
				category,
				overwriteOlderEntries,
				true
		);
		
	}
	
	/**
	 * Import an hl7 and a pdfFile
	 * @param hl7File the hl7 to import
	 * @param pdfFile the pdf to import
	 * @param institutionId the kontaktId of the institution
	 * @param institutionName Institution Name
	 * @param overwriteOlderEntries
	 *            true if the document should be overwritten. Even if a newer version exists
	 *            By default it is false
	 * @param askUser
	 *            true (default), if the user interface will be shown to select the patient
	 * 
	 * @return true if the import was successful
	 * @throws IOException
	 */
	public static boolean process(
			Path hl7File,
			Path pdfFile,
			String institutionId,
			String institutionName,
			String category,
			boolean overwriteOlderEntries,
			boolean askUser
		) throws IOException{
		
		boolean success = false;
		Patient patient = null;
		
		MedNet.getLogger().info(
			"importDocument "+
			"import following documents." 
					+   " HL7: "+ hl7File == null ? "" : hl7File.toString()
					+" -- PDF: "+ pdfFile == null ? "" : pdfFile.toString()
		);
		
		//If we have an hl7 File try first to import the hl7 and to get all the Patient informations
		if(hl7File != null && Files.exists(hl7File) && Files.isRegularFile(hl7File)){
			HL7_ORU_R01 hl7OruR01 = new HL7_ORU_R01();
			ObservationMessage observation = null;
			
			HL7Parser hlp = new DefaultHL7Parser(institutionId);
			
			//Read HL7 file
			try {
				//Parse the HL7 File
				String text = String.join("\n", Files.readAllLines(hl7File, DocumentImporter.HL7_ENCODING));
				observation = hl7OruR01.readObservation(text);
				success = true;
				for (String error : hl7OruR01.getErrorList()) {
					success = false;
					MedNet.getLogger().error("process HL7 error: "+ error);
				}
				for (String warn : hl7OruR01.getWarningList()) {
					MedNet.getLogger().warn("process HL7 warning: "+ warn);
				}
				
				//If the HL7 has successfully been parsed, we can look for the Patient
				if (success == true) {
					
					//Try to get the patient using all the informations contained in the observation field of the hl7
					//orderNr Placer
					//orderNr Filler
					//external Patient ID
					patient = DocumentImporter.getPatient(observation, institutionId, askUser);
					if (patient != null) {
						
						//If the patient has been found, import the file
							hlp.importFile(
								hl7File.toFile(),
								null,
								new MedNetLabItemResolver(institutionName),
							false
							);
						
						//If the import was not successful we will have an Exception
						
						
						//If the HL7 contains the pid of the filler, store it in the XID
						if (!observation.getAlternatePatientId().isEmpty()) {
							DocumentImporter.addInstitutionPIDToXID(institutionId, institutionName, observation.getAlternatePatientId(), patient);
						}
					} else {
						success = false;
					}
				}
			} catch (ElexisException ex) {
				success = false;
				MedNet.getLogger().error("process Elexis Exception importing the hl7. ", ex);
			} catch (Exception ex) {
				success = false;
				MedNet.getLogger().error("process Exception importing the hl7. ", ex);
			}
		}
		
		//If there is a PDF File
		if (	pdfFile != null 
			&&	Files.exists(pdfFile)
			&&	Files.isRegularFile(pdfFile)
			) {
			
			//Pick the most informations from the PDF Filename
			String documentDateTime = "";
			String patientId = "";
			String patientLastName = "";
			String patientFirstName = "";
			String patientBirthDate = "";
			String orderNr = "";
			Matcher filenameMatcher = documentFilenamePattern.matcher(getBaseName(pdfFile));
			
			if(filenameMatcher.matches()){
				documentDateTime = filenameMatcher.group("transactionDateTime");
				patientId = filenameMatcher.group("PatientId");
				patientLastName = filenameMatcher.group("PatientLastName");
				patientBirthDate = filenameMatcher.group("PatientBirthdate");
				orderNr = filenameMatcher.group("orderNr");
			}
			else {
				//If it is not a document, maybe it is a form
				filenameMatcher = formFilenamePattern.matcher(getBaseName(pdfFile));
				
				if(filenameMatcher.matches()){
					documentDateTime = filenameMatcher.group("transactionDateTime");
					patientId = filenameMatcher.group("PatientId");
					patientLastName = filenameMatcher.group("PatientLastName");
					patientLastName = filenameMatcher.group("PatientFirstName");
					patientBirthDate = filenameMatcher.group("PatientBirthdate");
					orderNr = filenameMatcher.group("orderNr");
					
					//Since the forms are not linked with institution
					//The function has been called without institutionName
					//The function documentManager.saveLaborItem will use the institutionName as a Category
					//That's why we set the institutionName to "Form"
					institutionName = MedNetMessages.DocumentImporter_DocumentDefaultCategory;
				}
			}
			
			//If there is no hl7 File we first should search for the Patient
			if(hl7File == null){
				//Search the Patient
				patient = DocumentImporter.getPatient(patientId, patientLastName, patientFirstName ,patientBirthDate, "", askUser);
			}
			
			//If we found a patient we can add the PDF
			if(patient != null){
			
				//Initialize a DocumentManager
				PatientDocumentManager documentManager = new PatientDocumentManager(patient);	
				
				if (documentManager != null) {
					//Save the PDF file into Omnivore
					
					Date documentDateTimeObj = new Date();
					try{
						DocumentImporter.documentDateTimeParser.parse(documentDateTime);
					}
					catch(ParseException pe){
						MedNet.getLogger().warn("process Unable to parse documentDateTime:"+documentDateTime, pe);
					}
					
					documentManager.addDocument(
							institutionId,
							institutionName,
							category,
							orderNr,
							pdfFile,
							documentDateTimeObj
							);
					
				}
			}
		}
		
		return success;
	}
	
	/**
	 * Try to get a patient using the information contained in the observation fields of an hl7
	 * If has not been found the user will be asked for delivering the patient
	 * @see {@link #getPatient(String, String, String, String, String, boolean) getPatient}
	 * @param observation
	 * @param institutionID
	 * @param askUser
	 *            true (default), if the user interface will be shown to select the patient
	 * @return
	 */
	private static Patient getPatient(final ObservationMessage observation, String institutionID, boolean askUser){
		return getPatient(
				observation.getPatientId(),
				observation.getPatientLastName(),
				observation.getPatientFirstName(),
				observation.getPatientBirthdate(),
				observation.getPatientSex(),
				askUser
				);
	}
	
	/**
	 * 
	 * Try to get a patient using the given informations.
	 * First with the id, if the id is empty or it has not been found, try using the other parameters
	 * If no patient has been found, the user will be asked for delivering the patient
	 * 
	 * @param id
	 * @param lastname
	 * @param firstname
	 * @param birthdate
	 * @param sex
	 * @param askUser
	 *            true (default), if the user interface will be shown to select the patient
	 * @return the Patient or null
	 */
	private static Patient getPatient(
			String id,
			String lastname,
			String firstname,
			String birthdate,
			String sex,
			boolean askUser){
		
		Patient patient = null;
		
		//First of all try to find the patient with the id
		//Try using the id
		if (id != null && !id.isEmpty()){
			patient = DocumentImporter.getPatientFromDB(id);
		}

		//If we still didn't find the patient
		//Search for it using the lastname, firstname, birthdate and sex
		if (	patient == null){
			//Try to find the patient using all the parameters
			if (	lastname != null
				&&	!lastname.isEmpty()
				&&	firstname != null
				&&	!firstname.isEmpty()
				&&	birthdate != null
				&&	!birthdate.isEmpty()
					) {
				patient = DocumentImporter.getPatientFromDB(lastname, firstname, birthdate, sex);
				if (patient != null) {
					MedNet.getLogger().debug("getPatient() Patient found in the database. "+patient.getLabel());
				}
			}
			
			// If the patient has not been found
			// Ask the user
			if (patient == null) {
				if (askUser)
					patient = DocumentImporter.patientSelectorDialog(lastname, firstname, birthdate, sex);
				if (patient != null) {
					MedNet.getLogger().debug("getPatient() Patient identified by the user. "+patient.getLabel());
				} else {
					MedNet.getLogger().warn("getPatient() Patient idnetification aborded by the user. ");
				}
			}
		}
		
		return patient;
	}
	
	
	/**
	 * Open a Kontakt selector window in order to allow the user choosing the Patient
	 * @param lastname,
	 * @param firstname,
	 * @param birthdate,
	 * @param sex
	 * @return the selected Patient or null if the user canceled the dialog
	 */
	private static Patient patientSelectorDialog(
			String lastname,
			String firstname,
			String birthdate,
			String sex){
		Patient retVal = null;
		
		String birthdateString = birthdate;
		try {
			birthdateString = birthdateHumanReadableFormatter.format(
					DocumentImporter.birthdateParser.parse(birthdate)
					);
		} catch (ParseException e) {
			MedNet.getLogger().error("patientSelectorDialog() Unable to parse birthdate "+birthdate);
		}
		
		retVal =
			(Patient) KontaktSelektor.showInSync(Patient.class,
				MedNetMessages.DocumentImporter_SelectPatient,
				MessageFormat.format(
						MedNetMessages.DocumentImporter_WhoIs,
						lastname,
						firstname, 
						birthdateString,
						sex));
		return retVal;
	}
	
	
	/**
	 * Get the Patients with the patId given as parameter
	 * It should not be possible to get multiple patients
	 * @param patId
	 *            Patienten-ID
	 * @return List der gefundenen Patienten
	 */
	private static Patient getPatientFromDB(final String patId){
		Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		patientQuery.add(Patient.FLD_PATID, Query.EQUALS, patId);
		
		List<Patient> result = patientQuery.execute(); 
		if(result.size() == 1){
			return result.get(0);
		}
		else if(result.size() >= 1){
			//If the get more than one Patient, we should log it
			MedNet.getLogger().error("getPatientFromDB() Multiple patients found with the id :" +patId);
			return null;
		}
		else {
			return null;
		}
	}

	/**
	 * Try to find a patient in the database with the following parameters
	 * @param firstname
	 * @param lastname
	 * @param birthdate
	 * @param sex
	 * @return the patient found or null if no patient or multiple patients has been found
	 */
	public static Patient getPatientFromDB(
			final String lastname,
			final String firstname,
			final String birthdate,
			final String sex
		){
		
		Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		if(lastname != null && !lastname.isEmpty()){
			patientQuery.add(Patient.FLD_NAME, Query.EQUALS, lastname);
		}
		if(firstname != null && !firstname.isEmpty()){
			patientQuery.add(Patient.FLD_FIRSTNAME, Query.EQUALS, firstname);
		}
		if(birthdate != null && !birthdate.isEmpty()){
			patientQuery.add(Patient.FLD_DOB, Query.EQUALS, birthdate);
		}
		if(sex != null && !sex.isEmpty()){
			String sexParam = sex.toLowerCase();
			if ("f".equals(sexParam))
				sexParam = "w";
			patientQuery.add(Patient.FLD_SEX, Query.EQUALS, sexParam);
		}
		
		List<Patient> result = patientQuery.execute();
		if(result.size() == 1){
			return result.get(0);
		}
		else if(result.size() >= 1){
			//If the get more than one Patient, we should log it
			MedNet.getLogger().error(
					"getPatientFromDB() " +
					"Multiple patients found for :" 
						+lastname+" "
						+firstname+" "
						+birthdate+" "
						+sex
			);
			return null;
		}
		else {
			return null;
		}
		
	}
	
	
	/**
	 * If the Patient has an external id by an Institution, this reference will be added to the Xid table
	 * @param institutionID the institution Kontakt id
	 * @param institutionName the institution name
	 * @param id the id of the patient by this institution
	 * @param patient the patient this number should be linked
	 * @return true if it was successful
	 */
	private static boolean addInstitutionPIDToXID(String institutionID, String institutionName, String id, Patient patient){
		boolean success = false;
		try {
			Xid.localRegisterXIDDomainIfNotExists(institutionID, "externePID", Xid.ASSIGNMENT_LOCAL);
			if ("".equals(DocumentImporter.getExternalPID(institutionID,patient))) {
				new Xid(patient, institutionID, id);
				success = true;
				MedNet.getLogger().info("addFillerPIDToXID() Add the Xid "+id+" for " + institutionName +" to the patient"+patient.getLabel());
			}
			return success;
			
		} catch (XIDException e) {
			MedNet.getLogger().info("addFillerPIDToXID() Unable to add the Xid "+id+" for " + institutionName +" to the patient"+patient.getLabel()+". Exception: ",e);
			return success;
		}
	}
	
	/**
	 * Return the id an institution gives to a patient
	 * @param the institution
	 * @param the patient
	 * @return the id found or an empty string
	 */
	public static String getExternalPID(String institutionID, Patient patient){
		
		Query<Xid> patientExternePIDQuery = new Query<Xid>(Xid.class);
		patientExternePIDQuery.add(Xid.FLD_OBJECT, Query.EQUALS, patient.getId());
		patientExternePIDQuery.add(Xid.FLD_DOMAIN, Query.EQUALS, institutionID);
		List<Xid> patienten = patientExternePIDQuery.execute();
		if (patienten.isEmpty()) {
			return "";
		} else {
			return ((Xid) patienten.get(0)).getDomainId();
		}
		
	}
	
	
	/**
	 * Returns the BaseName of a Path Object
	 * @param file
	 * @return
	 */
	public static String getBaseName(Path file){
		
		int pos = file.getFileName().toString().lastIndexOf(".");
		if (pos > 0) {
		    return file.getFileName().toString().substring(0, pos);
		}
		else {
			return file.getFileName().toString();
		}
	}
	
	/**
	 * Returns the Extension of a Path Object
	 * @param file
	 * @return
	 */
	public static String getExtension(Path file){
		
		int pos = file.getFileName().toString().lastIndexOf(".");
		if (pos > 1) {
		    return file.getFileName().toString().substring(pos+1);
		}
		else {
			return file.getFileName().toString();
		}
		
	}
	
}
