/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.tools;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.handler.GDTFileInputHandler;
import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.tools.Log;

public class DirectoryWatcher implements FileAlterationListener {

	private List<File> directories;
	private FileAlterationObserver observer;
	private FileAlterationMonitor monitor;
	private static DirectoryWatcher instance = null;
	private Log logger = Log.get(DirectoryWatcher.class.getName());
	private List<FileAlterationObserver> observers;

	private DirectoryWatcher() {
		observers = new LinkedList<FileAlterationObserver>();
		directories = new LinkedList<File>();
		monitor = new FileAlterationMonitor(2000);
		monitor.addObserver(observer);
	}

	public static DirectoryWatcher getInstance() {
		if (instance == null)
			instance = new DirectoryWatcher();
		return instance;
	}

	public void addDirectoryToWatch(File directory) {
		if (directory.isDirectory()) {
			logger.log("Adding directory: " + directory, Log.DEBUGMSG); //$NON-NLS-1$
			FileAlterationObserver observer = new FileAlterationObserver(directory);
			directories.add(directory);
			observer.addListener(this);
			observers.add(observer);
			monitor.addObserver(observer);
			this.unwatch();
			this.watch();
		} else {
			logger.log("Invalid directory entry passed: " + directory, Log.DEBUGMSG); //$NON-NLS-1$
		}
	}

	public void watch() {
		logger.log("Watching " + observers.size() + " directories.", Log.DEBUGMSG); //$NON-NLS-1$ //$NON-NLS-2$

		try {
			monitor.start();
		} catch (Exception e) {
			logger.log(e.getMessage(), Log.INFOS);
		}

		// Need to iterate through all registered directories
		for (File directory : directories) {
			List<File> files = (List<File>) FileUtils.listFiles(directory, null, false);
			Collections.sort(files, Comparator.comparingLong(File::lastModified));

			for (Iterator<File> iterator = files.iterator(); iterator.hasNext();) {
				File file = iterator.next();
				processFile(file);
			}
		}
	}

	public void unwatch() {
		logger.log("Unwatching " + observers.size() + " directories.", Log.DEBUGMSG); //$NON-NLS-1$ //$NON-NLS-2$
		try {
			monitor.stop();
		} catch (Exception e) {
			logger.log(e.getMessage(), Log.INFOS);
		}
	}

	@Override
	public void onFileCreate(File file) {
		logger.log("Incoming file " + file, Log.DEBUGMSG); //$NON-NLS-1$
		processFile(file);
	}

	private void processFile(File file) {
		if (GDTFileHelper.containsSatzNachricht(file)) {
			// TODO File may contain multiple independent GDT Messages, split!!
			GDTFileInputHandler.handle(file);
		} else {
			if (CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_FILETRANSFER_DELETE_NON_GDT_FILES, false)) {
				boolean success = file.delete();
				if (success) {
					logger.log("Deleted non GDT file " + file, Log.DEBUGMSG); //$NON-NLS-1$
				} else {
					logger.log("Error deleting file " + file, Log.WARNINGS); //$NON-NLS-1$
				}
			}
		}
	}

	@Override
	public void onFileChange(File arg0) {
	}

	@Override
	public void onFileDelete(File arg0) {
	}

	@Override
	public void onDirectoryChange(File arg0) {
	}

	@Override
	public void onDirectoryCreate(File arg0) {
	}

	@Override
	public void onDirectoryDelete(File arg0) {
	}

	@Override
	public void onStart(FileAlterationObserver arg0) {
	}

	@Override
	public void onStop(FileAlterationObserver arg0) {
	}

}
