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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.ui.util.SWTHelper;
import ch.novcom.elexis.mednet.plugin.data.DocumentImporter;
import ch.novcom.elexis.mednet.plugin.data.DocumentSettingRecord;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;

/**
 * This Object will call the MedNet getResults function
 * and will import all the received files into the Patient database
 * 
 */
public class DocumentImporterPage extends ImporterPage {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(DocumentImporterPage.class.getName());
	
	/**
	 * The standard way used when importing files. 
	 */
	private final static boolean OVERWRITEOLDERENTRIES = true;
	/**
	 * The way HL7 filenames are built in some old Versions of the conversion module
	 */
	private final static Pattern hl7FilenamePattern = Pattern.compile("^([^_]*_)*(?<transactionDateTime>[^_]+)_(?<orderNr>[^_]+)_(?<recipient>\\d+)$"); //$NON-NLS-1$
	/**
	 * The way PDF are built in all result transmission cases, and in most HL7 transmission cases
	 */
	private final static Pattern pdfFilenamePattern = Pattern.compile("^([^_]*_)*(?<uniqueMessageId>[^_]+)_(?<caseNr>[^_]*)_(?<transactionDateTime>[^_]*)_(?<orderNr>[^_]+)_(?<samplingDateTime>[^_]*)_(?<PatientLastName>[^_]*)_(?<PatientBirthdate>[^_]*)_(?<PatientId>[^_]*)_(?<recipient>\\d+)$"); //$NON-NLS-1$
	
	
	/**
	 * Call the MedNet getResult function, and import each received document into the database
	 */
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		String logPrefix = "doImport() - ";
		
		//List the path were we will have to collect the files to import
		List<DocumentSettingRecord> receivingsPaths = DocumentSettingRecord.getAllDocumentSettingRecords();

		//Add the information to the monitor that we start
		//And also set the monitor number of units for the total work
		if(monitor != null){
			monitor.beginTask(MedNetMessages.DocumentImporterPage_callMedNet,(receivingsPaths.size()+1)*100);
		}

		LOGGER.info(logPrefix+"call MedNet getResults()");//$NON-NLS-1$
		
		//Call MedNet
		MedNet.getDocuments();
		
		if(monitor != null){
			monitor.worked(100);
		}
		
		List<String> importFailures = new ArrayList<String>();
		List<String> importSuccess = new ArrayList<String>();
		
		
		//We can have multiple Download Folders.
		//We will process them One after the other
		for(DocumentSettingRecord documentSettingItem: receivingsPaths){
			
			if(monitor != null && monitor.isCanceled()){
				//If the monitor has been canceled we should break
				LOGGER.info(logPrefix+"import canceled");//$NON-NLS-1$
				break;
			}
			
			LOGGER.info(logPrefix+"Processing Institution "+documentSettingItem.getInstitutionName());//$NON-NLS-1$
			//We write to the monitor the name of the institution we will check
			if(monitor != null){
				monitor.subTask(
					MessageFormat.format(
							MedNetMessages.DocumentImporterPage_checkInstitution,
							documentSettingItem.getInstitutionName()
						)
				);
			}
			
			//List all the hl7 and pdf files from this folder
			Path directory = documentSettingItem.getPath();
			Path archiveDir = documentSettingItem.getArchivingPath();
			Path errorDir = documentSettingItem.getErrorPath();
			
			if(!Files.exists(directory) || !Files.isDirectory(directory)){
				//If this directory doesn't exists or is not a directory
				//continue
				LOGGER.warn(logPrefix+"The following directory is not valid:"+directory.toString());//$NON-NLS-1$
				if(monitor != null){
					monitor.worked(100);
				}
			}
			else if(!Files.exists(archiveDir) || !Files.isDirectory(archiveDir)){
				//If this directory doesn't exists or is not a directory
				//continue
				LOGGER.warn(logPrefix+"The following directory is not valid:"+archiveDir.toString());//$NON-NLS-1$
				if(monitor != null){
					monitor.worked(100);
				}
			}
			else if(!Files.exists(errorDir) || !Files.isDirectory(errorDir)){
				//If this directory doesn't exists or is not a directory
				//continue
				LOGGER.warn(logPrefix+"The following directory is not valid:"+errorDir.toString());//$NON-NLS-1$
				if(monitor != null){
					monitor.worked(100);
				}
			}
			else {
				
				//We can list all the hl7 and the pdfs of the folder
				DirectoryStream<Path> hl7Stream = Files.newDirectoryStream(directory, new DirectoryStream.Filter<Path>() {
			        @Override
			        public boolean accept(Path entry) throws IOException 
			        {
			            return 		Files.isRegularFile(entry)
			            		&&	entry.getFileName().toString().toLowerCase().endsWith(".hl7");//$NON-NLS-1$
			        }
			    });
				
				List<Path> hl7Files = new ArrayList<Path>();
				for(Path file : hl7Stream){
					hl7Files.add(file);
				}
				
				DirectoryStream<Path> pdfStream = Files.newDirectoryStream(directory, new DirectoryStream.Filter<Path>() {
			        @Override
			        public boolean accept(Path entry) throws IOException 
			        {
			            return 		Files.isRegularFile(entry)
			            		&&	entry.getFileName().toString().toLowerCase().endsWith(".pdf");//$NON-NLS-1$
			        }
			    });
				
				List<Path> pdfFiles = new ArrayList<Path>();
				for(Path file : pdfStream){
					pdfFiles.add(file);
				}
				
				
				//We listed PDF and HL7
				//Then we will try to link them together
				List<FilePair> pairList = new ArrayList<FilePair>();

				sameFileNameLoop:
				for(ListIterator<Path> hl7FilesIterator = hl7Files.listIterator(); hl7FilesIterator.hasNext();){
					
					if(monitor != null && monitor.isCanceled()){
						break sameFileNameLoop;
					}
					
					Path hl7File = hl7FilesIterator.next();
					String hl7FileName = DocumentImporter.getBaseName(hl7File);
							
					//First of all look for a PDF with the same filename
					for(ListIterator<Path> pdfFilesIterator = pdfFiles.listIterator(); pdfFilesIterator.hasNext();){

						if(monitor != null && monitor.isCanceled()){
							break sameFileNameLoop;
						}
						
						Path pdfFile = pdfFilesIterator.next();
						String pdfFileName = DocumentImporter.getBaseName(pdfFile);
						
						if(hl7FileName.equals(pdfFileName)){
							FilePair pair = new FilePair();
							pair.hl7 = hl7File;
							pair.pdf = pdfFile;
							pairList.add(pair);
							
							FileTime hl7FileTime = Files.getLastModifiedTime(hl7File);
							FileTime pdfFileTime = Files.getLastModifiedTime(pdfFile);
							
							//We set the fileTime to the oldest one of the two files
							if(hl7FileTime.compareTo(pdfFileTime) > 0){
								pair.fileTime = hl7FileTime;
							}
							
							//Remove the hl7 and the pdf Files from the queue
							pdfFilesIterator.remove();
							hl7FilesIterator.remove();
							
							//Go to the next hl7
							break;
						}
					}
				}

				//If we were not able to link hl7 and PDF with their filenames, use the Transaction we find in the PDF filename and in the HL7 fileName
				distinguishedFileNameLoop:
				for(ListIterator<Path> hl7FilesIterator = hl7Files.listIterator(); hl7FilesIterator.hasNext();){

					if(monitor != null && monitor.isCanceled()){
						break distinguishedFileNameLoop;
					}
					
					Path hl7File = hl7FilesIterator.next();
					String hl7FileName = DocumentImporter.getBaseName(hl7File);
					
					String hl7_transactionDateTime = "";
					String hl7_orderNr = "";
					String hl7_recipient = "";
					
					//If the HL7 has not the same structure as the pdf, maybe it is the old structure
					Matcher filenameMatcher = hl7FilenamePattern.matcher(hl7FileName);
					if(filenameMatcher.matches()){
						hl7_transactionDateTime = filenameMatcher.group("transactionDateTime");//$NON-NLS-1$
						hl7_orderNr = filenameMatcher.group("orderNr");//$NON-NLS-1$
						hl7_recipient = filenameMatcher.group("recipient");//$NON-NLS-1$
					}
					
					//Look for a pdf with the sameTransactionDateTime, orderNr and recipientNr
					for(ListIterator<Path> pdfFilesIterator = pdfFiles.listIterator(); pdfFilesIterator.hasNext();){
						if(monitor != null && monitor.isCanceled()){
							break distinguishedFileNameLoop;
						}
						
						Path pdfFile = pdfFilesIterator.next();
						String pdfFileName = DocumentImporter.getBaseName(pdfFile);
						
						filenameMatcher = pdfFilenamePattern.matcher(pdfFileName);
						if(filenameMatcher.matches()){
							String pdf_transactionDateTime = filenameMatcher.group("transactionDateTime");//$NON-NLS-1$
							String pdf_orderNr = filenameMatcher.group("orderNr");//$NON-NLS-1$
							String pdf_recipient = filenameMatcher.group("recipient");//$NON-NLS-1$
							
							if(		hl7_transactionDateTime.equals(pdf_transactionDateTime)
								&&	hl7_orderNr.equals(pdf_orderNr)
								&&	hl7_recipient.equals(pdf_recipient)
									
								){
								FilePair pair = new FilePair();
								pair.hl7 = hl7File;
								pair.pdf = pdfFile;
								pairList.add(pair);
								
								FileTime hl7FileTime = Files.getLastModifiedTime(hl7File);
								FileTime pdfFileTime = Files.getLastModifiedTime(pdfFile);
								
								//We set the fileTime to the oldest one of the two files
								if(hl7FileTime.compareTo(pdfFileTime) > 0){
									pair.fileTime = hl7FileTime;
								}
								
								//Remove the hl7 and the pdf Files from the lists
								pdfFilesIterator.remove();
								hl7FilesIterator.remove();
								
								//Go to the next hl7
								break;
							}
						}
					}
				}
				
				//Finally we have a list of Pair HL7/PDF, a list of HL7s and a list of PDFs
				
				//We will add the orphaned HL7 and PDF to the Pair list
				//In order to be able to sort the list by FileTime
				
				for(Path hl7File : hl7Files){

					if(monitor != null && monitor.isCanceled()){
						break;
					}
					
					FilePair pair = new FilePair();
					pair.hl7 = hl7File;
					pair.fileTime = Files.getLastModifiedTime(hl7File);
					pairList.add(pair);
				}
				
				for(Path pdfFile : pdfFiles){

					if(monitor != null && monitor.isCanceled()){
						break;
					}
					
					FilePair pair = new FilePair();
					pair.pdf = pdfFile;
					pair.fileTime = Files.getLastModifiedTime(pdfFile);
					pairList.add(pair);
				}
				
				//And we sort the list regarding the fileTime
				Collections.sort(pairList, new FilePairDateComparator());
				
				//Then we are ready for importing the files in the database
				for(FilePair pair : pairList){
					if(monitor != null && monitor.isCanceled()){
						break;
					}
					
					//If we have a HL7 File we will read informations from the HL7 File
					String filename = "";
					if(pair.hl7 != null){
						filename = pair.hl7.getFileName().toString();
					}
					else if(pair.pdf != null){
						filename = pair.pdf.getFileName().toString();
					}

					//We write to the monitor the information that we are parsing the file
					if(monitor != null){
						monitor.subTask(
							MessageFormat.format(
									MedNetMessages.DocumentImporterPage_parseFile,
									filename
								)
						);
					}
						
					boolean success = DocumentImporter.process(
							pair.hl7,
							pair.pdf,
							documentSettingItem.getInstitutionID(),
							documentSettingItem.getInstitutionName(),
							documentSettingItem.getCategory(),
							documentSettingItem.getXIDDomain(),
							OVERWRITEOLDERENTRIES,
							true
					);
					
					
					if (success) {
						//If the import was successful we can archive the files 
						if (pair.hl7 != null){
							try {
								Files.move(pair.hl7, archiveDir.resolve(pair.hl7.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								LOGGER.error(logPrefix+"IOException moving this file to the archive "+pair.hl7.toString(), ioe);//$NON-NLS-1$
							}
						}
						if (pair.pdf != null){
							try{
								Files.move(pair.pdf, archiveDir.resolve(pair.pdf.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								LOGGER.error(logPrefix+"IOException moving this file to the archive "+pair.pdf.toString(), ioe);//$NON-NLS-1$
							}
						}
						
						importSuccess.add(MessageFormat.format(
								MedNetMessages.DocumentImporterPage_FileSuccess, documentSettingItem.getInstitutionName(), pair.toString())
						);
						
					} else {
						//If the import was not successful we move the files to the error folder 
						if (pair.hl7 != null){
							try {
								Files.move(pair.hl7, errorDir.resolve(pair.hl7.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								LOGGER.error(logPrefix+"IOException moving this file to the error "+pair.hl7.toString(), ioe);//$NON-NLS-1$
							}
						}
						if (pair.pdf != null){
							try{
								Files.move(pair.pdf, errorDir.resolve(pair.pdf.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								LOGGER.error(logPrefix+"IOException moving this file to the error "+pair.pdf.toString(), ioe);//$NON-NLS-1$
							}
						}
						
						if(monitor != null){
							monitor.subTask(MessageFormat.format(
									MedNetMessages.DocumentImporterPage_ErrorWhileParsingFile, filename)
							);
						}
						
						importFailures.add(MessageFormat.format(
								MedNetMessages.DocumentImporterPage_FileFailure, documentSettingItem.getInstitutionName(), pair.toString())
						);
						
					}	
					
				}
				
				if(monitor != null){
					monitor.worked(100);
				}
				
				//Clear old archived files
				this.deleteOldArchiveFiles(documentSettingItem);
			}
		}
		
		
		
		if(importFailures.size() <= 0) {
			
			//If everything has been successfully imported 
			SWTHelper.showInfo(
				MedNetMessages.DocumentImporterPage_ImportCompletedTitle,
				MessageFormat.format(
						MedNetMessages.DocumentImporterPage_ImportCompletedSSuccessText,
						String.valueOf(importSuccess.size()),
						String.join("\n",importSuccess)
				)
			);
			
		}
		else {
			//If we had errors, open a MessageBox
			SWTHelper.showError(
				MedNetMessages.DocumentImporterPage_errorTitle,
				MessageFormat.format(
						MedNetMessages.DocumentImporterPage_ImportError,
						String.valueOf(importSuccess.size()),
						String.valueOf(importFailures.size()),
						String.join("\n",importFailures)
				)
			);
			
		}
		if(monitor != null){
			monitor.done();
		}
		
		
		LOGGER.info(logPrefix+"Import completed");
		
		
		return Status.OK_STATUS;
	}
	
	
	
	
	/**
	 * This function is called after each import procedure, in order to clean the archive, and remove
	 * all the old files.
	 * The number of days a file should stay in the archive is given by the documentSettingItem.
	 * By default it is 30 Days.
	 * @param documentSettingsItem specify the archive folder to check and also the number of days the files should be kept in it
	 */
	private void deleteOldArchiveFiles( DocumentSettingRecord documentSettingItem){
		String logPrefix = "deleteOldArchivFiles() - ";
		LOGGER.info(logPrefix+"Purge archive dir "+documentSettingItem.getArchivingPath().toString());//$NON-NLS-1$
		
		//Prepare the FileTime representing the limit.
		//All the files older than this limit will be deleted
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 0-documentSettingItem.getPurgeInterval());
		FileTime timeLimit = FileTime.fromMillis(cal.getTimeInMillis());
		
		
		// Clear the files older than the purgeInterval
		Path archivDir = documentSettingItem.getArchivingPath();
		if (Files.exists(archivDir) && Files.isDirectory(archivDir)) {
			
			try{
				DirectoryStream<Path> fileStream = Files.newDirectoryStream(archivDir, new TimeFilter(timeLimit));
				for(Path path : fileStream){
					try{
						Files.delete(path);
						LOGGER.info(logPrefix+"Following file has been deleted "+path.toString());//$NON-NLS-1$
					}
					catch (IOException ioe) {
						LOGGER.error(logPrefix+"IOException deleting file "+path.toString(), ioe);//$NON-NLS-1$
					}			
				}
			}
			catch (IOException ex) {
				LOGGER.error(logPrefix+"IOException walking throw archiv directory "+archivDir.toString(), ex);//$NON-NLS-1$
			}
		}
		
		LOGGER.info(logPrefix+"Purge of following archive completed"+archivDir.toString());//$NON-NLS-1$
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.elexis.util.ImporterPage#getTitle()
	 */
	@Override
	public String getTitle(){
		return MedNetMessages.DocumentImporterPage_titleImport;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.elexis.util.ImporterPage#getDescription()
	 */
	@Override
	public String getDescription(){
		return MedNetMessages.DocumentImporterPage_descriptionImport;
	}

	
	@Override
	public Composite createPage(Composite parent) {
		//We don't need to create any Page
		return null;
	}
	
	
	/**
	 * This class represents a pair of two files, hl7 and pdf
	 */
	private class FilePair{
		Path hl7 = null;
		Path pdf = null;
		FileTime fileTime = null;
		
		public String toString(){
			List<String> files = new ArrayList<String>();
			if(hl7 != null) {
				files.add(hl7.getFileName().toString());
			}
			if(pdf != null) {
				files.add(pdf.getFileName().toString());
			}
			return String.join(", ",files);
		}
	}
	
	/**
	 * A comparator in order to compare the files using the fileTime
	 *
	 */
	private class FilePairDateComparator implements Comparator<FilePair>{
		
		@Override
		public int compare(FilePair pair1, FilePair pair2) throws NullPointerException {
			if(pair1 == null || pair1.fileTime == null) {
				return -1;
			}
			else if(pair2 == null || pair2.fileTime == null) {
				return 1;
			}
			else {
				return pair1.fileTime.compareTo(pair2.fileTime);
			}
		}

	}
	
	/**
	 * A DirectoryStream.Filter that looks for files older than the given TimeLimit
	 */
	private class TimeFilter implements DirectoryStream.Filter<Path> {
		
		private FileTime timeLimit = null;
		
		
		public TimeFilter(FileTime timeLimit){
			this.timeLimit = timeLimit;
		}
		
        @Override
        public boolean accept(Path entry) throws IOException 
        {
            return 		Files.isRegularFile(entry)
            			&&	(Files.getLastModifiedTime(entry).compareTo(this.timeLimit) < 0);
        }
    }
	
	
}
