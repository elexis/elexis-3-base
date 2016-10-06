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
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import at.medevit.elexis.gdt.Activator;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.constants.GDTPreferenceConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.Log;

public class GDTFileHelper {
	
	private static Log logger = Log.get(GDTFileHelper.class.getName());
	
	static DecimalFormat threePlaces = new DecimalFormat("000");
	
	/**
	 * Identifies a file being a GDT Satznachricht or not
	 * 
	 * @param file
	 * @return boolean if file contains a SatzNachricht
	 */
	public static boolean containsSatzNachricht(File file){
		try {
			List<String> contents = FileUtils.readLines(file);
			if (contents.size() == 0)
				return false;
			if (contents.get(0).substring(3, 7).equalsIgnoreCase("8000"))
				return true;
		} catch (IOException e) {
			return false;
		}
		return false;
	}
	
	public static <U extends GDTSatzNachricht> boolean writeGDTSatzNachricht(U gdtSatzNachricht,
		IGDTCommunicationPartner cp){
		String[] outLines = gdtSatzNachricht.getMessage();
		String zeichensatz = GDTConstants.getCharsetStringByInt(cp.getOutgoingDefaultCharset());
		String directory = cp.getOutgoingDirectory();
		String outgoingFileName = determineOutgoingFileName(cp);
		
		try {
			File destination = new File(directory + File.separatorChar + outgoingFileName);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < outLines.length; i++) {
				sb.append(outLines[i]);
			}
			FileUtils.writeStringToFile(destination, sb.toString(), zeichensatz);
		} catch (IOException e) {
			String message = "GDT: Fehler beim Schreiben der Ausgangsdatei " + outgoingFileName;
			Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			logger.log(e, message, Log.WARNINGS);
			return false;
		}
		return true;
	}
	
	public static String determineOutgoingFileName(IGDTCommunicationPartner cp){
		String directory = cp.getOutgoingDirectory();
		String filenameHeader =
			cp.getShortIDReceiver()
				+ CoreHub.localCfg.get(GDTPreferenceConstants.CFG_GDT_FILETRANSFER_SHORTNAME,
					GDTConstants.GDT_SHORT_ID_DEFAULT);
		String filename = null;
		
		if (cp.getRequiredFileType().equalsIgnoreCase(GDTConstants.GDT_FILETRANSFER_TYP_FEST)) {
			if (cp.getFixedCommmunicationFileName() != null) {
				filename = cp.getFixedCommmunicationFileName();
			} else {
				filename = filenameHeader + ".GDT";
			}
		} else if (cp.getRequiredFileType().equalsIgnoreCase(
			GDTConstants.GDT_FILETRANSFER_TYPE_HOCHZAEHLEND)) {
			int counter = 0;
			while (true) {
				filename = filenameHeader + "." + threePlaces.format(counter);
				File file = new File(directory + File.separatorChar + filename);
				if (file.exists()) {
					counter++;
				} else {
					break;
				}
			}
		} else {
			logger.log("Invalid file transfer type returned, neither fest nor hochzaehlend!",
				Log.ERRORS);
		}
		return filename;
	}
}
