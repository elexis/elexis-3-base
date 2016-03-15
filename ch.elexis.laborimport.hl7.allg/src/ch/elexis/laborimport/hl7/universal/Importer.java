/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     G. Weirich - initial API and implementation
 ******************************************************************************/
package ch.elexis.laborimport.hl7.universal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Result;

public class Importer extends Action implements IAction {
	public static final String MY_LAB = "Eigenlabor";
	
	private HL7Parser hlp = new DefaultHL7Parser(MY_LAB);
	
	public Importer(){
		super("Hl7 Datei", Images.IMG_IMPORT.getImageDescriptor());
	}
	
	@Override
	public void run(){
		File dir = new File(CoreHub.localCfg.get(Preferences.CFG_DIRECTORY, File.separator));
		if ((!dir.exists()) || (!dir.isDirectory())) {
			SWTHelper.showError("bad directory for import", "Konfigurationsfehler",
				"Das Transferverzeichnis ist nicht korrekt eingestellt");
		} else {
			File archiveDir = new File(dir, "archive");
			if (!archiveDir.exists()) {
				archiveDir.mkdir();
			}
			File errorDir = new File(dir, "fehlerhaft");
			if (!errorDir.exists()) {
				errorDir.mkdir();
			}
			int err = 0;
			int files = 0;
			Result<?> r = null;
			for (String fn : dir.list(new FilenameFilter() {
				
				public boolean accept(File arg0, String arg1){
					if (arg1.toLowerCase().endsWith(".hl7")) {
						return true;
					}
					return false;
				}
			})) {
				files++;
				File hl7file = new File(dir, fn);
				try {
					r = hlp.importFile(hl7file, archiveDir, null, new LinkLabContactResolver(), false);
				} catch (IOException e) {
					err++;
					File errFile = new File(errorDir, fn);
					hl7file.renameTo(errFile);
				}
			}
			if (err > 0) {
				ResultAdapter.displayResult(r,
					Integer.toString(err) + " von " + Integer.toString(files)
						+ " Dateien hatten Fehler\n");
			} else if (files == 0) {
				SWTHelper.showInfo("Laborimport", "Es waren keine Dateien zum Import vorhanden");
			} else {
				SWTHelper.showInfo("Laborimport", Integer.toString(files)
					+ " Dateien wurden fehlerfrei verarbeitet.");
			}
		}
	}
	
}
