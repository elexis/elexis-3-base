/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */
package ch.medelexis.templator.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.medelexis.templator.ui.Preferences;
import ch.rgw.io.FileTool;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class StorageController extends Job implements HeartListener {
	private static final Logger log = LoggerFactory.getLogger(StorageController.class);
	private IDocumentManager dm;
	private List<Metafile> metafiles = new ArrayList<StorageController.Metafile>();
	private static StorageController theInstance;
	private String category = null;

	public static StorageController getInstance() {
		if (theInstance == null) {
			theInstance = new StorageController();
		}
		return theInstance;
	}

	private StorageController() {
		super("Medelexis-Text-Templator");
		dm = (IDocumentManager) Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		category = CoreHub.localCfg.get(Preferences.PREF_CATEGORY, "-");
		setPriority(DECORATE);
		setSystem(true);
		setUser(false);
		CoreHub.heart.addListener(this);
	}

	public File createFile(Patient pat, String name) throws IOException {
		String ext = "templator." + FileTool.getExtension(name);
		File dest = File.createTempFile("elexis", ext);
		dest.deleteOnExit();
		if (CoreHub.localCfg.get(Preferences.PREF_DOSAVE, true) && dm != null && pat != null) {
			Metafile mf = new Metafile(pat, name, category, System.currentTimeMillis(), dest);
			metafiles.add(mf);
		} else {
			log.debug("DocumentManager null [" + (dm == null) + "], Patient null ([" + (pat == null) + "])");
		}
		return dest;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Iterator<Metafile> it = metafiles.iterator();
		while (it.hasNext()) {
			try {
				Metafile mf = it.next();
				if (mf.guid == null) {
					addDocument(mf);
				} else {
					File exists = mf.fileOnDisk;
					if (exists.lastModified() > mf.timestamp) {
						dm.removeDocument(mf.guid);
						addDocument(mf);

					}
				}
			} catch (Exception ex) {
				SWTHelper.showError("Templator", "Fehler bei der Verarbeitung:", ex.getMessage());
			}

		}
		return Status.OK_STATUS;
	}

	private void addDocument(Metafile mf) throws Exception {
		if (mf.category == null) {
			mf.category = "-";
		} else {
			if (StringTool.getIndex(dm.getCategories(), mf.category) == -1) {
				dm.addCategorie(mf.category);
			}
		}
		GenericDocument gd = new GenericDocument(mf.pat, mf.name, mf.category, mf.fileOnDisk,
				new TimeTool().toString(TimeTool.DATE_GER), "", null);
		dm.addDocument(gd);
		mf.guid = gd.getGUID();
		mf.timestamp = mf.fileOnDisk.lastModified();
	}

	@Override
	public void heartbeat() {
		schedule();
	}

	private class Metafile {
		String name;
		long timestamp;
		String category;
		String guid = null;
		Patient pat;
		File fileOnDisk;

		Metafile(Patient p, String n, String c, long t, File f) {
			name = n;
			category = c;
			timestamp = t;
			pat = p;
			fileOnDisk = f;
		}
	}

}
