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

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.slf4j.LoggerFactory;

import ch.elexis.core.events.MessageEvent;
import ch.elexis.core.importer.div.importers.DefaultPersistenceHandler;
import ch.elexis.core.importer.div.importers.HL7Parser;
import ch.elexis.core.importer.div.importers.multifile.MultiFileParser;
import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.importer.div.importers.DefaultHL7Parser;
import ch.elexis.core.ui.importer.div.importers.multifile.strategy.DefaultImportStrategyFactory;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Result;

public class Importer extends Action implements IAction {
	public static final String MY_LAB = "Eigenlabor";

	private MultiFileParser mfParser = new MultiFileParser(MY_LAB);
	private HL7Parser hlp = new DefaultHL7Parser(MY_LAB);

	public Importer() {
		super("Hl7 Datei", Images.IMG_IMPORT.getImageDescriptor());
	}

	@Override
	public void run() {
		if (ConfigServiceHolder.getLocal(Preferences.CFG_DIRECTORY_AUTOIMPORT, false)) {
			MessageEvent.fireInformation("HL7 Import", "Automatischer Import ist aktiviert.");
			return;
		}

		Optional<IVirtualFilesystemHandle> directory = HL7ImportDirectory.getDirectoryHandle();
		if (directory.isEmpty()) {
			SWTHelper.showError("bad directory for import", "Konfigurationsfehler",
					"Das Transferverzeichnis ist nicht korrekt eingestellt.");
			return;
		}

		IVirtualFilesystemHandle[] hl7Files;
		try {
			hl7Files = directory.get().listHandles(handle -> "hl7".equalsIgnoreCase(handle.getExtension()));
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).warn("Could not read HL7 import directory", e);
			hl7Files = null;
		}
		if (hl7Files == null) {
			SWTHelper.showError("bad directory for import", "Konfigurationsfehler",
					"Das Transferverzeichnis ist nicht korrekt eingestellt, bzw. kann nicht gelesen werden.");
			return;
		}
		Arrays.sort(hl7Files, Comparator.comparing(IVirtualFilesystemHandle::getName));

		int err = 0;
		int files = 0;
		Result<?> r = null;
		for (IVirtualFilesystemHandle hl7File : hl7Files) {
			files++;
			r = mfParser.importFromHandle(hl7File, new DefaultImportStrategyFactory().setMoveAfterImport(true)
					.setLabContactResolver(new LinkLabContactResolver()), hlp, new DefaultPersistenceHandler());
			if (r != null && !r.isOK()) {
				err++;
			}
		}
		if (err > 0) {
			SWTHelper.showError("HL7 Import Fehler",
					Integer.toString(err) + " von " + Integer.toString(files) + " Dateien hatten Fehler");
		} else if (files == 0) {
			SWTHelper.showInfo("Laborimport", "Es waren keine Dateien zum Import vorhanden");
		} else {
			SWTHelper.showInfo("Laborimport", Integer.toString(files) + " Dateien wurden fehlerfrei verarbeitet.");
		}
	}
}
