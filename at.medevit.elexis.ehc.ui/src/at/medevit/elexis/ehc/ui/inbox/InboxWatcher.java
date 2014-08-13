/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.ehc.ui.inbox;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.model.EhcDocument;
import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Mandant;
import ch.rgw.tools.TimeTool;

public class InboxWatcher {
	private static Logger logger = LoggerFactory.getLogger(InboxWatcher.class);
	
	private MandantChangedListener mandantListener;
	
	private ExecutorService executor;
	
	private WatchService watcher;
	private HashMap<String, WatchKey> watchKeys;
	private String activeInboxString;
	
	private List<InboxListener> listeners;
	
	public InboxWatcher(){
		executor = Executors.newFixedThreadPool(2);
		
		mandantListener = new MandantChangedListener();
		watchKeys = new HashMap<String, WatchKey>();
		
		listeners = new ArrayList<InboxListener>();
		
		ElexisEventDispatcher.getInstance().addListeners(mandantListener);
	}
	
	public void start(){
		try {
			watcher = FileSystems.getDefault().newWatchService();
			
			executor.execute(new DirectoryWatcher());
		} catch (IOException e) {
			logger.error("Error creating filesystem watcher", e);
		}
	}
	
	public void stop(){
		ElexisEventDispatcher.getInstance().removeListeners(mandantListener);
		try {
			executor.shutdown();
			watcher.close();
		} catch (IOException e) {
			logger.error("Error closing filesystem watcher", e);
		}
	}
	
	public synchronized void addInboxListener(InboxListener listener){
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public synchronized void removeInboxListener(InboxListener listener){
		listeners.remove(listener);
	}
	
	private void fireInboxCreated(EhcDocument newDocument){
		for (InboxListener inboxListener : listeners) {
			inboxListener.documentCreated(newDocument);
		}
	}
	
	private class DirectoryWatcher implements Runnable {
		
		@SuppressWarnings("unchecked")
		@Override
		public void run(){
			WatchKey key = null;
			try {
				while (true) {
					// wait for key to be signaled
					key = watcher.take();
					
					// Dequeueing events
					Kind<?> kind = null;
					for (WatchEvent<?> watchEvent : key.pollEvents()) {
						// Get the type of the event
						kind = watchEvent.kind();
						if (OVERFLOW == kind) {
							continue; // loop
						} else if (ENTRY_CREATE == kind) {
							// A new Path was created
							Path newPath = ((WatchEvent<Path>) watchEvent).context();
							String newInboxPath =
								activeInboxString + File.separator
									+ newPath.getFileName().toString();
							URL fileURL = new URL("file:///" + newInboxPath);
							EhcDocument newDocument =
								new EhcDocument(newPath.getFileName().toString(), fileURL,
									new TimeTool());
							fireInboxCreated(newDocument);
						}
					}
					if (!key.reset()) {
						break; // loop
					}
				}
			} catch (InterruptedException | MalformedURLException e) {
				logger.error("Filesystem watching interrupted stopping", e);
			}
		}
	}
	
	private class DirectoryInitializer implements Runnable {
		
		@Override
		public void run(){
			File inboxFolder = new File(activeInboxString);
			File[] files = inboxFolder.listFiles();
			for (File file : files) {
				if (!file.isDirectory()) {
					try {
						URL url = new URL("file:///" + file.getAbsolutePath());
						if (!EhcDocument.documentExists(url)) {
							EhcDocument newDocument =
								new EhcDocument(file.getName(), url, new TimeTool());
							fireInboxCreated(newDocument);
						}
					} catch (MalformedURLException e) {
						logger.error("Error initializing inbox.", e);
					}
				}
			}
		}
	}
	
	private class MandantChangedListener extends ElexisUiEventListenerImpl {
		public MandantChangedListener(){
			super(Mandant.class, ElexisEvent.EVENT_MANDATOR_CHANGED);
		}
		
		@Override
		public void runInUi(ElexisEvent ev){
			activeInboxString =
				CoreHub.userCfg.get(PreferencePage.EHC_INPUTDIR,
					PreferencePage.getDefaultInputDir());
			executor.execute(new DirectoryInitializer());
			if (watchKeys.get(activeInboxString) == null) {
				try {
					Path inboxPath = Paths.get(activeInboxString);
					WatchKey key;
					key =
						inboxPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
							StandardWatchEventKinds.ENTRY_DELETE);
					watchKeys.put(activeInboxString, key);
				} catch (IOException e) {
					logger.error("Error creating filesystem key", e);
				}
			}
		}
	}
}
