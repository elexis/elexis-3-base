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
package at.medevit.elexis.gdt.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import at.medevit.elexis.gdt.Activator;
import at.medevit.elexis.gdt.constants.GDTConstants;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.tools.GDTCommPartnerCollector;
import at.medevit.elexis.gdt.tools.GDTSatzNachrichtHelper;
import ch.elexis.core.ui.util.Log;

public class GDTFileInputHandler {

	private static Log logger = Log.get(GDTFileInputHandler.class.getName());

	public static void handle(File file) {
		String[] lines = readFileGetUTF8(file);
		int satzkennung = 0;

		if (lines != null) {
			String satzkennungString = GDTSatzNachrichtHelper
					.getValueIfExists(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION, lines);
			satzkennung = Integer.parseInt(satzkennungString);
		}

		IGDTCommunicationPartner cp = GDTCommPartnerCollector
				.identifyCommunicationPartnerByIncomingDirectory(file.getParent());
		if (cp == null) {
			logger.log("IGDTCommunicationPartner for file " + file.getAbsolutePath() + " is null, skipping.",
					Log.ERRORS);
			return;
		}

		boolean delivered = false;

		switch (satzkennung) {
		case GDTConstants.SATZART_STAMMDATEN_ANFORDERN:
			delivered = GDTInputHandler.handleSatznachricht6300(lines, file.getName(), cp);
			if (delivered)
				delete(file);
			break;
		case GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_UEBERMITTELN:
			delivered = GDTInputHandler.handleSatznachricht6310(lines, file.getName(), cp);
			if (delivered)
				delete(file);
			break;
		case GDTConstants.SATZART_DATEN_EINER_UNTERSUCHUNG_ZEIGEN:
			GDTInputHandler.handleSatznachricht6311(lines, file.getName(), cp);
			break;
		case GDTConstants.SATZART_STAMMDATEN_UEBERMITTELN:
			GDTInputHandler.handleSatznachricht6301(lines, file.getName(), cp);
			break;
		case GDTConstants.SATZART_UNTERSUCHUNG_ANFORDERN:
			GDTInputHandler.handleSatznachricht6302(lines, file.getName(), cp);
		default:
			break;
		}
	}

	private static void delete(File file) {
		try {
			boolean deleted = file.delete();
			if (deleted) {
				logger.log("Deleted " + file.getAbsolutePath(), Log.DEBUGMSG);
			} else {
				logger.log("Error deleting " + file.getAbsolutePath(), Log.WARNINGS);
			}
		} catch (SecurityException e) {
			logger.log(e, "Error deleting " + file.getAbsolutePath(), Log.WARNINGS);
		}
	}

	public static String[] readFileGetUTF8(File file) {
		int encoding = GDTConstants.ZEICHENSATZ_IBM_CP_437;
		try {
			List<String> dataList = FileUtils.readLines(file, "cp437");
			String[] data = dataList.toArray(new String[] {});
			String usedEncoding = GDTSatzNachrichtHelper
					.getValueIfExists(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ, data);
			if (usedEncoding == null)
				return data; // Not set return default encoding

			int usedEncodingInt = Integer.parseInt(usedEncoding);
			if (encoding == usedEncodingInt)
				return data; // Set, but default

			if (usedEncodingInt == GDTConstants.ZEICHENSATZ_7BIT) {
				return FileUtils.readLines(file, GDTConstants.ZEICHENSATZ_7BIT_CHARSET_STRING).toArray(new String[] {});
			} else if (usedEncodingInt == GDTConstants.ZEICHENSATZ_ISO8859_1_ANSI_CP_1252) {
				return FileUtils.readLines(file, "Cp1252").toArray(new String[] {});
			}
		} catch (IOException e) {
			String message = "GDT: Ein-/Ausgabe Fehler beim Lesen von " + file.getAbsolutePath();
			Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, message, e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			logger.log(e, message, Log.WARNINGS);
		}
		return null;
	}
}
