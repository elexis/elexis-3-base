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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
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

	private boolean settingOverwrite = false;

	protected final SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); //$NON-NLS-1$

	private final static Pattern hl7FilenamePattern = Pattern.compile("^([^_]*_)*(?<transactionDateTime>[^_]+)_(?<orderNr>[^_]+)_(?<recipient>\\d+)$");
	private final static Pattern pdfFilenamePattern = Pattern.compile("^([^_]*_)*(?<uniqueMessageId>[^_]+)_(?<caseNr>[^_]*)_(?<transactionDateTime>[^_]*)_(?<orderNr>[^_]+)_(?<samplingDateTime>[^_]*)_(?<PatientLastName>[^_]*)_(?<PatientBirthdate>[^_]*)_(?<PatientId>[^_]*)_(?<recipient>\\d+)$");
	
	
	/**
	 * Call the MedNet getResult function, and import each received document into the database
	 */
	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception{
		MedNet.getLogger().debug("doImport()");
		
		//List the path were we will have to collect the files to import
		List<DocumentSettingRecord> receivingsPaths = DocumentSettingRecord.getAllDocumentSettingRecords();

		//Add the information to the monitor that we start
		//And also set the monitor number of units for the total work
		if(monitor != null){
			monitor.beginTask(MedNetMessages.DocumentImporterPage_callMedNet,(receivingsPaths.size()+1)*100);
		}

		MedNet.getLogger().info("doImport() call MedNet getResults()");
		
		//Call MedNet
		MedNet.getDocuments();
		
		if(monitor != null){
			monitor.worked(100);
		}
		//We can have multiple Download Folders.
		//We will process them One after the other
		
		
		for(DocumentSettingRecord documentSettingItem: receivingsPaths){
			
			if(monitor != null && monitor.isCanceled()){
				//If the monitor has been canceled we should breakMedNetLogger.getLogger().println(
				MedNet.getLogger().info("doImport() import canceled");
				break;
			}
			

			MedNet.getLogger().info("doImport() Processing Institution "+documentSettingItem.getInstitutionName());
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
				MedNet.getLogger().info("doImport() The following directory is not valid:"+directory.toString());
				if(monitor != null){
					monitor.worked(100);
				}
			}
			else if(!Files.exists(archiveDir) || !Files.isDirectory(archiveDir)){
				//If this directory doesn't exists or is not a directory
				//continue
				MedNet.getLogger().info("doImport() The following directory is not valid:"+archiveDir.toString());
				if(monitor != null){
					monitor.worked(100);
				}
				
			}
			else if(!Files.exists(errorDir) || !Files.isDirectory(errorDir)){
				//If this directory doesn't exists or is not a directory
				//continue
				MedNet.getLogger().info("doImport() The following directory is not valid:"+errorDir.toString());
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
			            		&&	entry.getFileName().toString().toLowerCase().endsWith(".hl7");
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
			            		&&	entry.getFileName().toString().toLowerCase().endsWith(".pdf");
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
							
							//Remove the hl7 and the pdf Files from the lists
							pdfFilesIterator.remove();
							hl7FilesIterator.remove();
							
							//Go to the next hl7
							break;
						}
					}
				}
				
				distinguishedFileNameLoop:
				//If we were not able to link hl7 and PDF with their filenames, use the Transaction we find in the PDF filename and in the HL7 fileName
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
						hl7_transactionDateTime = filenameMatcher.group("transactionDateTime");
						hl7_orderNr = filenameMatcher.group("orderNr");
						hl7_recipient = filenameMatcher.group("recipient");
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
							String pdf_transactionDateTime = filenameMatcher.group("transactionDateTime");
							String pdf_orderNr = filenameMatcher.group("orderNr");
							String pdf_recipient = filenameMatcher.group("recipient");
							
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
					pair.hl7 = pdfFile;
					pair.fileTime = Files.getLastModifiedTime(pdfFile);
					pairList.add(pair);
				}
				
				//And we sort the list regarding the fileTime
				Collections.sort(pairList, new FilePairDateComparator());
				
				//Then we are ready for importing the files in the database

				int errorCount = 0;
				int errorMovedCount = 0;
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
							settingOverwrite,
							true
					);
					
					if (success) {
						// Archivieren
						if (pair.hl7 != null){
							try {
								Files.move(pair.hl7, archiveDir.resolve(pair.hl7.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								MedNet.getLogger().info("doImport() IOException moving this file to the archive "+pair.hl7.toString(), ioe);
							}
						}
						if (pair.pdf != null){
							try{
								Files.move(pair.pdf, archiveDir.resolve(pair.pdf.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								MedNet.getLogger().info("doImport() IOException moving this file to the archive "+pair.pdf.toString(), ioe);
							}
						}
					} else {
						if (pair.hl7 != null){
							try {
								Files.move(pair.hl7, errorDir.resolve(pair.hl7.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								MedNet.getLogger().info("doImport() IOException moving this file to the error "+pair.hl7.toString(), ioe);
							}
							errorMovedCount++;
						}
						if (pair.pdf != null){
							try{
								Files.move(pair.pdf, errorDir.resolve(pair.pdf.getFileName()), StandardCopyOption.REPLACE_EXISTING);
							}
							catch(IOException ioe){
								MedNet.getLogger().info("doImport() IOException moving this file to the error "+pair.pdf.toString(), ioe);
							}
							errorMovedCount++;
						}
						errorCount++;
						if(monitor != null){
							monitor.subTask(MessageFormat.format(
									MedNetMessages.DocumentImporterPage_ErrorWhileParsingFile, filename)
							);
						}
					}	
					
				}
				
				if(monitor != null){
					monitor.worked(100);
				}
				
				if (errorCount > 0) {
					//If we had errors, open a MessageBox
					SWTHelper.showError(
						MedNetMessages.DocumentImporterPage_errorTitle,
						MessageFormat.format(
								MedNetMessages.DocumentImporterPage_errorMsgVerarbeitung,
								errorCount,
								errorMovedCount,
								documentSettingItem.getErrorPath()
						)
					);
				} else {
						SWTHelper.showInfo(
							MedNetMessages.DocumentImporterPage_ImportCompletedTitle,
							MedNetMessages.DocumentImporterPage_ImportCompletedSSuccessText
						);
				}
				
				//Clear old archivFiles
				this.deleteOldArchivFiles(documentSettingItem);
				
			}
		}
		
		MedNet.getLogger().info("doImport() Import completed");
		
		
		return Status.OK_STATUS;
	}
	
	
	
	
	/**
	 * Anhand der Einstellungen (Default 30 Tage) werden alle Dateien im Archiv Verzeichnis gelöscht
	 * die älter als die konfigurierten Tage sind.
	 */
	private void deleteOldArchivFiles( DocumentSettingRecord documentSettingItem){
		
		MedNet.getLogger().info("deleteOldArchivFiles() Purge archive dir "+documentSettingItem.getArchivingPath().toString());
		
		//Prepare the FileTime representing the limit. All the files older than this limit will be deleted
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
						MedNet.getLogger().info("deleteOldArchivFiles() Following file has been deleted "+path.toString());
					}
					catch (IOException ioe) {
						MedNet.getLogger().error("deleteOldArchivFiles() IOException deleting file "+path.toString(), ioe);
					}			
				}
			}
			catch (IOException ex) {
				MedNet.getLogger().error("deleteOldArchivFiles() IOException walking throw archiv directory "+archivDir.toString(), ex);
			}
		}
		
		MedNet.getLogger().info("deleteOldArchivFiles() Purge of following archive completed"+archivDir.toString());
		
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
		return null;
	}
	
	
	/**
	 * This class represents a pair of two files, hl7 and pdf
	 * @author david.gutknecht
	 *
	 */
	private class FilePair{
		Path hl7 = null;
		Path pdf = null;
		FileTime fileTime = null;
	}
	
	/**
	 * A comparator in order to compare the files using the fileTime
	 * @author david.gutknecht
	 *
	 */
	private class FilePairDateComparator implements Comparator<FilePair>{
		
		@Override
		public int compare(FilePair pair1, FilePair pair2) throws NullPointerException {
			return pair1.fileTime.compareTo(pair2.fileTime);
		}

	}
	
	/**
	 * A DirectoryStream.Filter that looks for files older than the given TimeLimit
	 * @author david.gutknecht
	 *
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
