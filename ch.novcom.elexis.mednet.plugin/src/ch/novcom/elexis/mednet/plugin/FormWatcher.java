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
    	Path pathToWatch = MedNet.getSettings().getFormsPath();
    	//If the Path to monitor exists
    	if(pathToWatch != null) {
    		if(Files.isDirectory(pathToWatch)) {
                this.watcher = FileSystems.getDefault().newWatchService();
                LOGGER.info(logPrefix+"Following path will be monitored: "+pathToWatch.toString());//$NON-NLS-1$
                //Register the Watcher
                pathToWatch.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);	
    		}
    		else {
        		LOGGER.warn(logPrefix+"Configured Form Path is not a valid directory: "+pathToWatch.toString());//$NON-NLS-1$
        		this.watcher = null;
    		}	
    	}
    	else {
    		LOGGER.warn(logPrefix+"no Form Path configured");//$NON-NLS-1$
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
    	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(MedNet.getSettings().getFormsPath())) {
            for (Path path : directoryStream) {
            	if(		Files.isReadable(path)
            		&&	Files.isRegularFile(path)
            		&& 	path.getFileName().toString().toLowerCase().endsWith(".pdf")//$NON-NLS-1$
            			){
            		files.add(path);
            	}
            }
        } catch (IOException ex) {}
    	
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
                Path child = MedNet.getSettings().getFormsPath().resolve(name);
                if(child.getFileName().toString().toLowerCase().endsWith(".pdf")){//$NON-NLS-1$
                	//Import the files one after the other, supposing each file is a Formular
                	this.importForm(child);
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
				//Run the import using the DocumentImporter
				boolean success = DocumentImporter.processForm(
						file,
						MedNetMessages.FormWatcher_FormCategory,
						true
				);
				
				if(success){
					//If the import was successfull, archive the file
					LOGGER.info(logPrefix+"Successfully imported document: "+file.toString());//$NON-NLS-1$
					Files.move(file, MedNet.getSettings().getFormsArchivePath().resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
				}
				else {
					//If the import was not successfull, move the file to the error folder
					LOGGER.error(logPrefix+"Failed importing document: "+file.toString());//$NON-NLS-1$
					Files.move(file, MedNet.getSettings().getFormsErrorPath().resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
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
