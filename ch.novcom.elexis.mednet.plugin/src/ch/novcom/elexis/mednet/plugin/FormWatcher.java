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

import ch.novcom.elexis.mednet.plugin.data.DocumentImporter;
import ch.novcom.elexis.mednet.plugin.messages.MedNetMessages;

public class FormWatcher {

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
        this.watcher = FileSystems.getDefault().newWatchService();
        MedNet.getSettings().getFormsPath().register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
    	
    	//If they are already files into the folder process them first
    	List<Path> files = new ArrayList<Path>();
    	try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(MedNet.getSettings().getFormsPath())) {
            for (Path path : directoryStream) {
            	if(		Files.isReadable(path)
            		&&	Files.isRegularFile(path)
            		&& 	path.getFileName().toString().toLowerCase().endsWith(".pdf")
            			){
            		files.add(path);
            	}
            }
        } catch (IOException ex) {}
    	//Order the list by time
    	Collections.sort(files, new DateTimeAscending());
    	
    	for(Path file : files){
    		this.importForm(file);
    	}
    	
        while(true) {
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = MedNet.getSettings().getFormsPath().resolve(name);
                if(child.getFileName().toString().toLowerCase().endsWith(".pdf")){
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
	 * Import a form file to the patient
	 * @param file
	 */
	private void importForm(Path file){
		
		if(			file != null 
				&&	Files.isReadable(file)
        		&&	Files.isRegularFile(file)
        		&& 	file.getFileName().toString().toLowerCase().endsWith(".pdf")
        	){
			try {
				boolean success = DocumentImporter.processForm(
						file,
						MedNetMessages.FormWatcher_FormCategory,
						true
				);
				
				if(success){
					//Archiv the file
					MedNet.getLogger().info("importForm() Successfully imported document: "+file.toString());
					Files.move(file, MedNet.getSettings().getFormsArchivePath().resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
				}
				else {
					//Move the file to the error folder
					MedNet.getLogger().error("importForm() Failed importing document: "+file.toString());
					Files.move(file, MedNet.getSettings().getFormsErrorPath().resolve(file.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
				}
				
				
			} catch (IOException e) {
				MedNet.getLogger().error("importForm() IOException importing document: "+file.toString()+" ",e);
			}
		}
		else if (file != null){
			MedNet.getLogger().warn("importForm() Following file is not valid: "+file.toString());
		}
		else {
			MedNet.getLogger().warn("importForm() the file is null ");
		}
		
	}

	
	
	/**
	 * We need to order the files in ascending order according to their creation dateTime.
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
