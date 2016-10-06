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

import at.medevit.elexis.gdt.data.GDTProtokoll;
import at.medevit.elexis.gdt.handler.response.GDTResponseIn6300Out6301;
import at.medevit.elexis.gdt.interfaces.IGDTCommunicationPartner;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6300;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6301;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6302;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6310;
import at.medevit.elexis.gdt.messages.GDTSatzNachricht6311;

public class GDTInputHandler {
	
	// TODO: Bei DateiTyp : Ist die Datei für mich bestimmt?
	// TODO: Bei Mutation von eingehenden Stammdaten, sollen diese in Elexis aktualisiert werden?
	
	/**
	 * Bearbeite eine einkommende Nachricht der Satzart STAMMDATEN ÜBERMITTELN
	 */
	static boolean handleSatznachricht6301(String[] lines, String incomingFilename,
		IGDTCommunicationPartner cp){
		GDTSatzNachricht6301 in = GDTSatzNachricht6301.createfromStringArray(lines);
		// TODO GDT Client: Verarbeite eingehend STAMMDATEN ÜBERMITTELN		
		GDTProtokoll.addEntry(GDTProtokoll.MESSAGE_DIRECTION_IN, cp, in);
		return true;
	}
	
	/**
	 * Bearbeite eine einkommende Nachricht der Satzart DATEN EINER UNTERSUCHUNG ZEIGEN
	 */
	static boolean handleSatznachricht6311(String[] lines, String incomingFilename,
		IGDTCommunicationPartner cp){
		GDTSatzNachricht6311 in = GDTSatzNachricht6311.createfromStringArray(lines);
		// TODO GDT Client: Verarbeite eingehend DATEN EINER UNTERSUCHUNG ZEIGEN
		GDTProtokoll.addEntry(GDTProtokoll.MESSAGE_DIRECTION_IN, cp, in);
		return true;
	}
	
	/**
	 * Bearbeite eine einkommende Nachricht der Satzart DATEN EINER UNTERSUCHUNG ÜBERMITTELN
	 */
	static boolean handleSatznachricht6310(String[] lines, String incomingFilename,
		IGDTCommunicationPartner cp){
		GDTSatzNachricht6310 in = GDTSatzNachricht6310.createfromStringArray(lines);
		// TODO GDT Client: Verarbeite eingehend DATEN EINER UNTERSUCHUNG ÜBERMITTELN
		GDTProtokoll.addEntry(GDTProtokoll.MESSAGE_DIRECTION_IN, cp, in);
		return true;
	}
	
	/**
	 * Bearbeite eine einkommende Nachricht der Satzart STAMMDATEN ANFORDERN
	 * 
	 * @param lines
	 * @param cp
	 * @return true if successfully handled, false if error during handling
	 */
	static boolean handleSatznachricht6300(String[] lines, String incomingFilename,
		IGDTCommunicationPartner cp){
		
		GDTSatzNachricht6300 in = GDTSatzNachricht6300.createfromStringArray(lines);
		GDTProtokoll.addEntry(GDTProtokoll.MESSAGE_DIRECTION_IN, cp, in);
		
		// TODO Alert user about incoming message? Query whether should be answered?
		
		GDTSatzNachricht6301 out = GDTResponseIn6300Out6301.createResponse(in);
		if (out == null)
			return false;
		
		GDTOutputHandler.handleOutput(out, cp);
		return true;
	}
	
	/**
	 * Bearbeite eine einkommende Nachricht der Satzart NEUE UNTERSUCHUNG ANFORDERN
	 * 
	 * @param lines
	 */
	static boolean handleSatznachricht6302(String[] lines, String incomingFilename,
		IGDTCommunicationPartner cp){
		GDTSatzNachricht6302 in = GDTSatzNachricht6302.createfromStringArray(lines);
		GDTProtokoll.addEntry(GDTProtokoll.MESSAGE_DIRECTION_IN, cp, in);
		return true;
		// TODO: Was für Untersuchungen soll Elexis hier unterstützen??
	}
}
