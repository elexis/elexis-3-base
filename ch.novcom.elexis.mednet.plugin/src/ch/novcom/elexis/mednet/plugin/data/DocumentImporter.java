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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.elexis.data.Xid.XIDException;
import ch.novcom.elexis.mednet.plugin.MedNet;
import ch.novcom.elexis.mednet.plugin.MedNetConfigFormItem;
import ch.novcom.elexis.mednet.plugin.MedNetLabItemResolver;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

/**
 * Manage the import of HL7 and PDF Documents into the Patient
 * @author David Gutknecht
 *
 */
public class DocumentImporter {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentImporter.class.getName());

	/**
	 * The filename structure of the document that can be imported
	 * this is useful to extract different informations without looking at the content of the file
	 * It can also be used if a PDF has no HL7 linked to it.
	 */
	private final static Pattern documentFilenamePattern = Pattern.compile("^([^_]*_)*(?<uniqueMessageId>[^_]+)_(?<caseNr>[^_]*)_(?<transactionDateTime>[^_]*)_(?<orderNr>[^_]+)_(?<samplingDateTime>[^_]*)_(?<PatientLastName>[^_]*)_(?<PatientBirthdate>[0-9]{8})?_(?<PatientId>[^_]*)_(?<recipient>\\d+)$");//$NON-NLS-1$
	/**
	 * The filename structure of the Forms that can be imported
	 * This is important to identify which patient the file should be added
	 */
	private final static Pattern formFilenamePattern = Pattern.compile("^(?<transactionDateTime>[0-9]{14})_(?<sender>[^_]*)_(?<PatientId>[^_]*)_(?<PatientLastName>[^_]*)_(?<PatientFirstName>[^_]*)_(?<PatientBirthdate>[0-9]{8})?_(?<institutionId>[^_]*)_(?<formId>[^_]*)_(?<orderNr>[^_]*)$");//$NON-NLS-1$
	
	/**
	 * This DateFormat is used to convert the transactionDateTime available in the filenames of the files to import
	 * into a DateTime Object.
	 */
	private final static SimpleDateFormat documentDateTimeParser = new SimpleDateFormat("yyyyMMddHHmmss");//$NON-NLS-1$
	private final static SimpleDateFormat documentDateParser = new SimpleDateFormat("yyyyMMdd");//$NON-NLS-1$

	/**
	 * This DateFormat is used to convert the birthdate available in the filename of the files to import into 
	 * a Date Object
	 */
	private final static SimpleDateFormat birthdateParser = new SimpleDateFormat("yyyyMMdd");//$NON-NLS-1$
	
	/**
	 * This DateFormat is used to convert a Date Object into a format that can be used by the elexis KontaktSelektor
	 */
	private final static SimpleDateFormat birthdateHumanReadableFormatter = new SimpleDateFormat("dd-MM-yyyy");//$NON-NLS-1$
	
	/**
	 * The Pattern to search for laboratory PID in the HL7 if xidDomain is set 
	 */
	private final static Pattern hl7PatientPattern = Pattern.compile("^PID\\|[^\\|]*\\|(?<id>[^\\|]*)\\|(?<institutionId>[^\\|]*)\\|[^\\|]*\\|(?<lastname>[^\\|\\^]*)\\^?(?<firstname>[^\\|\\^]*)[^\\|]*\\|[^\\|]*\\|(?<birthdate>[^\\|]*)\\|(?<gender>[^\\|]*)\\|.*$");
	
	/**
	 * The default encoding for opening HL7
	 */
	private final static Charset DEFAULT_HL7_INPUTENCODING = Charset.forName("ISO-8859-1");
	
	
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
	 *            true (default), if the patient cannot been identified, show a dialog to select the patient
	 * @return true if the import was successful
	 * @throws IOException
	 */
	public static boolean process(
			Path hl7File,
			Path pdfFile,
			String institutionId,
			String institutionName,
			String category,
			String xidDomain,
			boolean overwriteOlderEntries,
			boolean askUser
		) throws IOException{
		String logPrefix = "process() - ";//$NON-NLS-1$
		
		boolean success = true;
		Patient patient = null;

		if(hl7File != null) {
			LOGGER.info(logPrefix + "Import document -- HL7: "+ hl7File.toString());//$NON-NLS-1$
		}
		if(pdfFile != null) {
			LOGGER.info(logPrefix + "Import document -- PDF: "+ pdfFile.toString());//$NON-NLS-1$
		}
		
		//If we have an hl7 File try first to import the hl7 and to get all the Patient informations
		if(hl7File != null && Files.exists(hl7File) && Files.isRegularFile(hl7File)){
			
			HL7Parser hlp = new DefaultHL7Parser(institutionId);
			try {
				//Import the HL7. If the patient has not been found in the DB, the parser will ask for it 
				Result<?> res = hlp.importFile(
					hl7File.toFile(),
					null,
					new MedNetLabItemResolver(institutionName),
					false
				);
				
				if(res.isOK()) {
					//If the result has successfully been imported
					//Get the Patient found in the HL7 or selected by the user
					IPatient ipat = hlp.hl7Reader.getPatient();
					patient = DocumentImporter.getPatient(ipat.getId(), ipat.getFamilyName(), ipat.getFirstName() , ipat.getDateOfBirth().toString(TimeTool.DATE_COMPACT), ipat.getGender().value(), false);
					success = true;
				}
				else {
					//If the import was not successful
					LOGGER.error(logPrefix + "Unable to import the hl7.");//$NON-NLS-1$
					success = false;
				}					
				
				
			} catch (Exception ex) {
				success = false;
				LOGGER.error(logPrefix + "Exception importing the hl7. ", ex);//$NON-NLS-1$
			}
			
			
			if(		success
				&&	patient != null
				&&	xidDomain != null 
				&&	!xidDomain.isEmpty()) {
			
				
				
				String patient_institutionId = null;
				
				//if xid_Domain is set, we should extract the institution Patient ID
				//We look for the Patient Informations and Order informations in the file
				try {
					BufferedReader lineReader = Files.newBufferedReader(hl7File, DocumentImporter.DEFAULT_HL7_INPUTENCODING);
					
					//The informations we are looking for is in the first 4 lines.
					//We don't need to read more
					int nbMatches = 0;
					for(int i=0; 
									i<4 
								&&	lineReader.ready()
								&&	nbMatches < 3
							;
						i++
						){
						String line = lineReader.readLine();
						
						Matcher patientMatcher = DocumentImporter.hl7PatientPattern.matcher(line);
						if (patientMatcher.matches()){
							nbMatches ++;
							patient_institutionId = patientMatcher.group("institutionId");//$NON-NLS-1$
							break;
						}
					}
					lineReader.close();
					
				} catch (IOException ioe) {
					LOGGER.error(logPrefix + "Unable to load the hl7 file. ", ioe);//$NON-NLS-1$
				}
				
				if(patient_institutionId != null && !patient_institutionId.isEmpty()) {
					try {
						Xid.localRegisterXIDDomainIfNotExists(xidDomain, institutionName , Xid.ASSIGNMENT_LOCAL);
						String db_patient_institutionId = DocumentImporter.getInstitutionXID(xidDomain, patient);
						if(db_patient_institutionId == null) {
							new Xid(patient, xidDomain, patient_institutionId);
							LOGGER.info(
								MessageFormat.format("xid {0} ({2}) successfully saved to Patient {1}", patient_institutionId,
									patient.getLabel(), institutionName)
							);
						}
						else if( db_patient_institutionId.equals(patient_institutionId)) {
							//If the institution ID we have in the database is not the same as the one we got,
							//Update the one we got
							DocumentImporter.deleteInstitutionXID(xidDomain, patient);
							new Xid(patient, xidDomain, patient_institutionId);
							LOGGER.info(
									MessageFormat.format("xid {0} ({2}) from Patient {1} successfully updated (old value {3})", patient_institutionId,
										patient.getLabel(), institutionName, db_patient_institutionId)
								);
						}
					} catch (XIDException e) {
						LOGGER.error(
							MessageFormat.format("xid {0} ({2}) has not been saved to Patient {1}", patient_institutionId,
								patient.getLabel(),institutionName),e);
					}
				}
				
			}
			
		}
		
		//If there is a PDF File
		if (	success //It doesn t make sense to import the pdf if the import of the HL7 didn't work
			&&	pdfFile != null 
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
				documentDateTime = filenameMatcher.group("samplingDateTime");
				if(documentDateTime == null || documentDateTime.isEmpty()) {
					documentDateTime = filenameMatcher.group("transactionDateTime");
				}
				patientId = filenameMatcher.group("PatientId");
				patientLastName = filenameMatcher.group("PatientLastName");
				patientBirthDate = filenameMatcher.group("PatientBirthdate");
				orderNr = filenameMatcher.group("orderNr");
			}
			
			//If there is no hl7 File we first should search for the Patient
			if(patient == null){
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
						documentDateTimeObj = DocumentImporter.documentDateTimeParser.parse(documentDateTime);
					}
					catch(ParseException pe1){
						//If we are not able to parse a DateTime, maybe it is only a Date
						try {
							documentDateTimeObj = DocumentImporter.documentDateParser.parse(documentDateTime);
						}
						catch(ParseException pe2) {
							LOGGER.warn("process Unable to parse documentDateTime:"+documentDateTime, pe2);
						}
					}
					
					String keywords = orderNr;
					
					documentManager.addDocument(
							institutionId,
							institutionName,
							category,
							orderNr,
							pdfFile,
							documentDateTimeObj, 
							keywords
							);
					
					success = true;
					
				}
				
			}
			else {
				success = false;
			}
		}
		
		return success;
	}
	
	/**
	 * Import a Form
	 * @param pdfFile the pdf to import
	 * @param category the category were the Form will be saved
	 * @param askUser
	 *            true (default), if the patient cannot been identified, show a dialog to select the patient
	 * 
	 * @return true if the import was successful
	 * @throws IOException
	 */
	public static boolean processForm(
			Path pdfFile,
			String category,
			boolean askUser
		) throws IOException{
		String logPrefix = "processForm() - ";//$NON-NLS-1$
		
		boolean success = false;
		Patient patient = null;
		
		
		//If there is a PDF File
		if (	pdfFile != null 
			&&	Files.exists(pdfFile)
			&&	Files.isRegularFile(pdfFile)
			) {
			LOGGER.info(logPrefix+"import form -- PDF: "+pdfFile.toString());//$NON-NLS-1$
			
			//Pick the most informations from the PDF Filename
			String documentDateTime = "";
			String patientId = "";
			String patientLastName = "";
			String patientFirstName = "";
			String patientBirthDate = "";
			String orderNr = "";
			String institutionId = "";
			String institutionName = "";
			String formularId = "";
			String formularName = "";
			
			//If it is not a document, maybe it is a form
			Matcher filenameMatcher = formFilenamePattern.matcher(getBaseName(pdfFile));
				
			if(filenameMatcher.matches()){
				documentDateTime = filenameMatcher.group("transactionDateTime");
				patientId = filenameMatcher.group("PatientId");
				patientLastName = filenameMatcher.group("PatientLastName");
				patientLastName = filenameMatcher.group("PatientFirstName");
				patientBirthDate = filenameMatcher.group("PatientBirthdate");
				orderNr = filenameMatcher.group("orderNr");
				institutionId = filenameMatcher.group("institutionId");
				formularId = filenameMatcher.group("formId");
				
				//Try to get the formularName and the institutionName from the configuration
				Map<String, Map<String, MedNetConfigFormItem>> configFormItems = MedNet.getSettings().getConfigFormItems();
				if(		configFormItems.containsKey(institutionId)
					&&	configFormItems.get(institutionId).containsKey(formularId)
						) {
					MedNetConfigFormItem item = configFormItems.get(institutionId).get(formularId);
					institutionName = item.getInstitutionName();
					formularName = item.getFormName();
				}
				
				
			}
			
			//search for the Patient
			patient = DocumentImporter.getPatient(patientId, patientLastName, patientFirstName ,patientBirthDate, "", askUser);
			
			//If we found a patient we can add the PDF
			if(patient != null){
			
				//Initialize a DocumentManager
				PatientDocumentManager documentManager = new PatientDocumentManager(patient);	
				
				if (documentManager != null) {
					//Save the PDF file into Omnivore
					
					Date documentDateTimeObj = new Date();
					try{
						documentDateTimeObj = DocumentImporter.documentDateTimeParser.parse(documentDateTime);
					}
					catch(ParseException pe){
						LOGGER.warn(logPrefix + "Unable to parse documentDateTime:"+documentDateTime, pe);//$NON-NLS-1$
					}
					
					String keywords = orderNr;
					
					documentManager.addForm(
							category,
							institutionName,
							formularName,
							orderNr,
							pdfFile,
							documentDateTimeObj,
							keywords
							);
					
					success = true;
				}
			}
		}
		else if(pdfFile != null){
			LOGGER.error(logPrefix+"following file is not valid: "+pdfFile.toString());//$NON-NLS-1$
		}
		else {
			LOGGER.error(logPrefix+"the file is null");//$NON-NLS-1$
		}
		
		return success;
	}
	
	/**
	 * 
	 * Try to get a patient using the given informations.
	 * First with the id, if the id is empty or it has not been found, try using the other parameters
	 * If no patient has been found, the user will be asked for delivering the patient
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
		String logPrefix = "getPatient() - ";//$NON-NLS-1$
		
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
					LOGGER.debug(logPrefix+"Patient found in the database. "+patient.getLabel());//$NON-NLS-1$
				}
			}
			
			// If the patient has not been found
			// Ask the user
			if (patient == null) {
				if (askUser)
					patient = DocumentImporter.patientSelectorDialog(lastname, firstname, birthdate, sex);
				if (patient != null) {
					LOGGER.debug(logPrefix+"Patient identified by the user. "+patient.getLabel());//$NON-NLS-1$
				} else {
					LOGGER.warn(logPrefix+"Patient identification aborded by the user. ");//$NON-NLS-1$
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
		String logPrefix = "patientSelectorDialog() - ";//$NON-NLS-1$
		Patient retVal = null;
		
		String birthdateString = birthdate;
		try {
			birthdateString = birthdateHumanReadableFormatter.format(
					DocumentImporter.birthdateParser.parse(birthdate)
					);
		} catch (ParseException e) {
			LOGGER.error(logPrefix+"Unable to parse birthdate "+birthdate);//$NON-NLS-1$
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
		String logPrefix = "getPatientFromDB() - ";//$NON-NLS-1$
		Query<Patient> patientQuery = new Query<Patient>(Patient.class);
		patientQuery.add(Patient.FLD_PATID, Query.EQUALS, patId);
		
		List<Patient> result = patientQuery.execute(); 
		if(result.size() == 1){
			return result.get(0);
		}
		else if(result.size() >= 1){
			//If the get more than one Patient, we should log it
			LOGGER.error(logPrefix+"Multiple patients found with the id :" +patId);//$NON-NLS-1$
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
			LOGGER.error(
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
	

	public static String getInstitutionXID(String xidDomain, Patient patient){
		
		Query<Xid> patientInstitutionXIDQuery = new Query<Xid>(Xid.class);
		patientInstitutionXIDQuery.add(Xid.FLD_OBJECT, Query.EQUALS, patient.getId());
		patientInstitutionXIDQuery.add(Xid.FLD_DOMAIN, Query.EQUALS, xidDomain);
		List<Xid> patienten = patientInstitutionXIDQuery.execute();
		if (patienten.isEmpty()) {
			return null;
		} else {
			return ((Xid) patienten.get(0)).getDomainId();
		}
		
	}
	
	public static void deleteInstitutionXID(String xidDomain, Patient patient){
		
		Query<Xid> patientInstitutionXIDQuery = new Query<Xid>(Xid.class);
		patientInstitutionXIDQuery.add(Xid.FLD_OBJECT, Query.EQUALS, patient.getId());
		patientInstitutionXIDQuery.add(Xid.FLD_DOMAIN, Query.EQUALS, xidDomain);
		List<Xid> patientenXids = patientInstitutionXIDQuery.execute();
		if (patientenXids.isEmpty()) {
			return ;
		} else {
			for (Xid xid : patientenXids) {
				xid.delete();
			}
		}		
	}
	
	
}
