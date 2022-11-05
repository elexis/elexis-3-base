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

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.model.EhcDocument;
import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.l10n.Messages;
import ch.elexis.data.Mandant;

public class InboxWatcher {
	private static Logger logger = LoggerFactory.getLogger(InboxWatcher.class);

	private MandantChangedListener mandantListener;

	private ExecutorService executor;

	private WatchService watcher;
	private HashMap<String, WatchKey> watchKeys;
	private String activeInboxString;
	private static String LinuxIoNtifyHint = "The inbox will not work correctly.\n\nHINT: Under linux calling 'echo 256 > /proc/sys/fs/inotify/max_user_instances' might fix the problem";

	private List<InboxListener> listeners;

	public InboxWatcher() {
		executor = Executors.newFixedThreadPool(2);

		mandantListener = new MandantChangedListener();
		watchKeys = new HashMap<String, WatchKey>();

		listeners = new ArrayList<InboxListener>();

		ElexisEventDispatcher.getInstance().addListeners(mandantListener);
	}

	public void start() {
		try {
			watcher = FileSystems.getDefault().newWatchService();

			executor.execute(new DirectoryWatcher());
		} catch (IOException e) {
			logger.error(LinuxIoNtifyHint);
			logger.error("Error creating filesystem watcher", e); //$NON-NLS-1$
		}
	}

	public void stop() {
		ElexisEventDispatcher.getInstance().removeListeners(mandantListener);
		try {
			executor.shutdown();
			if (watcher != null) {
				watcher.close();
			}
		} catch (IOException e) {
			logger.error("Error closing filesystem watcher", e); //$NON-NLS-1$
		}
	}

	public synchronized void addInboxListener(InboxListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public synchronized void removeInboxListener(InboxListener listener) {
		listeners.remove(listener);
	}

	private void fireInboxCreated(EhcDocument newDocument) {
		for (InboxListener inboxListener : listeners) {
			inboxListener.documentCreated(newDocument);
		}
	}

	private class DirectoryDocumentStrategy {
		public void execute(URL fileUrl) {
			if (!EhcDocument.documentExists(fileUrl)) {
				if (EhcDocument.isEhcXml(fileUrl)) {
					fireInboxCreated(EhcDocument.createFromXml(fileUrl));
				} else if (EhcDocument.isEhcXdm(fileUrl)) {
					// no inbox element for the XDM
					EhcDocument.createFromXdm(fileUrl);
				}
			}
		}
	}

	private class DirectoryWatcher implements Runnable {

		private DirectoryDocumentStrategy documentStrategy;

		public DirectoryWatcher() {
			documentStrategy = new DirectoryDocumentStrategy();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
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
							String newInboxPath = activeInboxString + File.separator + newPath.getFileName().toString();
							URL fileUrl = new URL("file:///" + newInboxPath); //$NON-NLS-1$
							documentStrategy.execute(fileUrl);
						}
					}
					if (!key.reset()) {
						break; // loop
					}
				}
			} catch (InterruptedException | MalformedURLException e) {
				logger.error("Filesystem watching interrupted stopping", e); //$NON-NLS-1$
			}
		}
	}

	private class DirectoryInitializer implements Runnable {

		private DirectoryDocumentStrategy documentStrategy;

		public DirectoryInitializer() {
			documentStrategy = new DirectoryDocumentStrategy();
		}

		@Override
		public void run() {
			File inboxFolder = new File(activeInboxString);
			if (!inboxFolder.exists()) {
				PreferencePage.initDirectories();
			}
			File[] files = inboxFolder.listFiles();
			if (files != null) {
				for (File file : files) {
					if (!file.isDirectory()) {
						try {
							URL fileUrl = new URL("file:///" + file.getAbsolutePath()); //$NON-NLS-1$
							documentStrategy.execute(fileUrl);
						} catch (MalformedURLException e) {
							logger.error("Error initializing inbox.", e); //$NON-NLS-1$
						}
					}
				}
			}
		}
	}

	private class MandantChangedListener extends ElexisUiEventListenerImpl {
		public MandantChangedListener() {
			super(Mandant.class, ElexisEvent.EVENT_MANDATOR_CHANGED);
		}

		@Override
		public void runInUi(ElexisEvent ev) {
			activeInboxString = ConfigServiceHolder.getUser(PreferencePage.EHC_INPUTDIR,
					PreferencePage.getDefaultInputDir());
			executor.execute(new DirectoryInitializer());
			if (watchKeys.get(activeInboxString) == null) {
				try {
					Path inboxPath = Paths.get(activeInboxString);
					if (!inboxPath.toFile().exists()) {
						inboxPath.toFile().mkdirs();
					}

					WatchKey key;
					if (watcher == null) {
						String errorTitle = "Unable to create file watcher";
						Status status = new Status(Status.ERROR, this.getClass().getSimpleName(), Status.ERROR,
								errorTitle, null);
						ErrorDialog.openError(null, "Unable to create file watcher", LinuxIoNtifyHint, status);
					} else {
						key = inboxPath.register(watcher, StandardWatchEventKinds.ENTRY_CREATE,
								StandardWatchEventKinds.ENTRY_DELETE);
						watchKeys.put(activeInboxString, key);
					}
				} catch (IOException e) {
					logger.error("Error creating filesystem key", e); //$NON-NLS-1$
				}
			}
		}
	}
}
