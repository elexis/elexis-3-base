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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.novcom.elexis.mednet.plugin.data.DocumentImporter;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;

/**
 * The FormWatcher will monitor some folder,
 * looking for PDF returned by MedNet
 * after a Formular has been sent
 */
public class FormWatcher {
	/**
	 * Logger used to log all activities of the module
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(FormWatcher.class.getName());

	/**
	 * The service used to monitor the folder
	 */
    private final WatchService watcher;
    
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
    
    /**
     * Creates a WatchService and registers the given directory
     * @throws IOException 
     */
    public FormWatcher() throws IOException{
		String logPrefix = "constructor() - ";//$NON-NLS-1$
    	
    	Map<String, MedNetConfigFormPath> configFormPaths = MedNet.getSettings().getConfigFormPaths();
    	
    	if(configFormPaths != null && configFormPaths.size() > 0) {
    		Set<Path> toWatch = new TreeSet<Path>();
    		for(MedNetConfigFormPath configFormPath : configFormPaths.values()) {
    			Path path = configFormPath.getPath();
    			if(Files.isDirectory(path)) {
    				toWatch.add(path);
    			}
        		else {
            		LOGGER.warn(logPrefix+"Configured Form Path is not a valid directory: "+path.toString());//$NON-NLS-1$
        		}	
    		}
    		
    		if(toWatch.size() > 0) {
                this.watcher = FileSystems.getDefault().newWatchService();
    			for(Path path : toWatch) {
                    LOGGER.info(logPrefix+"Following path will be monitored: "+path.toString());//$NON-NLS-1$
                    //Register the Watcher
                    path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
    			}
    		}
    		else {
    			LOGGER.warn(logPrefix+"no valid Form Path configured");//$NON-NLS-1$
    			this.watcher = null;
    		}
    	}
		else {
			LOGGER.warn(logPrefix+"MedNet is not configured");//$NON-NLS-1$
			this.watcher = null;
		}
    	
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
    	
    	//If the watcher is still null, 
    	//the folder to monitor is no valid
    	//simply return
    	if(this.watcher == null) {
    		return;
    	}
    	
    	//If they are already files into the folder 
    	// before the watcher has already been started, process them first
    	//We only look for pdf files
    	List<Path> files = new ArrayList<Path>();
    	

    	Map<String, MedNetConfigFormPath> configFormPaths = MedNet.getSettings().getConfigFormPaths();
    	
    	if(configFormPaths != null && configFormPaths.size() > 0) {
    		for(MedNetConfigFormPath configFormPath : configFormPaths.values()) {
    			Path path = configFormPath.getPath();
    			if(Files.isDirectory(path)) {
    		    	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(path)) {
    		            for (Path file : directoryStream) {
    		            	if(		Files.isReadable(file)
    		            		&&	Files.isRegularFile(file)
    		            		&& 	file.getFileName().toString().toLowerCase().endsWith(".pdf")//$NON-NLS-1$
    		            			){
    		            		files.add(file);
    		            	}
    		            }
    		        } catch (IOException ex) {}
    			}	
    		}
    	}
    	
    	//Order the list by time
    	Collections.sort(files, new DateTimeAscending());
    	
    	//Import the files one after the other, supposing each file is a Formular
    	for(Path file : files){
    		this.importForm(file);
    	}
    	
    	//Enter in a loop
        while(true) {
        	
            // wait for key to be signaled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path dir = (Path)key.watchable();
                Path file = dir.resolve(name);
                if(file.getFileName().toString().toLowerCase().endsWith(".pdf")){//$NON-NLS-1$
                	//Import the files one after the other, supposing each file is a Formular
                	this.importForm(file);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
            	break;
            }
        }
    }
    
	
	/**
	 * Import a PDF file supposing that it is a Formular in the PDF-Format
	 * @param file
	 */
	private void importForm(Path file){
		String logPrefix = "importForm() - ";//$NON-NLS-1$
		
		//Check if the file we should import is valid
		if(			file != null 
				&&	Files.isReadable(file)
        		&&	Files.isRegularFile(file)
        		&& 	file.getFileName().toString().toLowerCase().endsWith(".pdf")//$NON-NLS-1$
        	){
			try {
				
				//First move the file to a temporary folder
				//If move does't work the file is still used
				Path tempDir = Files.createTempDirectory("ch.novcom.elexis.mednet.plugin");
				Path tempFile = tempDir.resolve(file.getFileName());
				Path errorDir = file.getParent().resolve("error");
				Path archiveDir = file.getParent().resolve("archive");
				
				//Before doing anything ensure that the errorDir and the archive dir exists
				//or that we can create them
				if(!Files.exists(errorDir)) {
					LOGGER.info(logPrefix+"Error directory doesn't exist, create it. "+errorDir.toString());
					try {
						Files.createDirectory(errorDir);
					}
					catch(IOException | SecurityException ex) {
						LOGGER.error(logPrefix+"Unable to create the error directory. Abort import. "+errorDir.toString(), ex);
						return ;
					}
				}
				else if (!Files.isDirectory(errorDir)) {
					LOGGER.error(logPrefix+"Error directory is not a valid directory. Abort import. "+errorDir.toString());
					return;
				}
				
				if(!Files.exists(archiveDir)) {
					LOGGER.info(logPrefix+"Archive directory doesn't exist, create it. "+archiveDir.toString());
					try{
						Files.createDirectory(archiveDir);
					}
					catch(IOException | SecurityException ex) {
						LOGGER.error(logPrefix+"Unable to create the archive directory. Abort import. "+archiveDir.toString(), ex);
						return ;
					}
				}
				else if (!Files.isDirectory(archiveDir)) {
					LOGGER.error(logPrefix+"Archive directory is not a valid directory. Abort import. "+errorDir.toString());
					return;
				}
				
				
				int trying = 0;
				while ( trying < 50) {
					try {
						Files.move(file, tempFile, StandardCopyOption.REPLACE_EXISTING);
						break;
					}
					catch(IOException ioe) {
						
					}
					//If we cannot move since the file is not used
					//Try again later
					try {
						Thread.sleep(100);
					}
					catch(InterruptedException e) {
						LOGGER.error(logPrefix+"waiting the file to be moved interrupted");
						return;
					}
				}
				
				if(trying >= 50) {
					//The file is still used. Abort
					return;
				}
				
				//Run the import using the DocumentImporter
				boolean success = DocumentImporter.processForm(
						tempFile,
						MedNetMessages.FormWatcher_FormCategory,
						true
				);
				
				if(success){
					//If the import was successfull, archive the file
					LOGGER.info(logPrefix+"Successfully imported document: "+file.toString());//$NON-NLS-1$
					Files.move(tempFile, archiveDir.resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
				}
				else {
					//If the import was not successfull, move the file to the error folder
					LOGGER.error(logPrefix+"Failed importing document: "+file.toString());//$NON-NLS-1$
					Files.move(tempFile, errorDir.resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
				}
				
			} catch (IOException e) {
				LOGGER.error(logPrefix+"IOException importing document: "+file.toString()+" ",e);//$NON-NLS-1$
			}
		}
		else if (file != null && !Files.isReadable(file)){
			LOGGER.warn(logPrefix+"Following file is not readable: "+file.toString());//$NON-NLS-1$
		}
		else if (file != null){
			LOGGER.warn(logPrefix+"Following file is not valid: "+file.toString());//$NON-NLS-1$
		}
		else {
			LOGGER.warn(logPrefix+"The file is null ");//$NON-NLS-1$
		}
		
	}
	
	
	/**
	 * We need to order the files in ascending order according to their creation dateTime.
	 * That's why we need this class
	 */
	private final class DateTimeAscending implements Comparator<Path> {
		@Override
		public int compare(Path arg0, Path arg1){
			try {
				return Files.getLastModifiedTime(arg0).compareTo(Files.getLastModifiedTime(arg1));
			} catch (IOException e) {
				return 0;
			}
		}
	}
}
