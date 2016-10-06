/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.messages;

import java.util.HashMap;
import java.util.LinkedHashMap;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class GDTSatzNachricht6300 extends GDTSatzNachricht {

	/**
	 * Erstelle eine neue Nachricht der Satzart STAMMDATEN ANFORDERN
	 * 
	 * @param patientenkennung wenn null fordere Daten des aktuellen Patienten an
	 * @param gdtIdReceiver optional
	 * @param gdtIdSender optional
	 * @param zeichensatz optional
	 * @param gdtVersion - default: {@link GDTConstants#GDT_VERSION}
	 */
	public GDTSatzNachricht6300(String patientenkennung, String gdtIdReceiver, String gdtIdSender, String zeichensatz, String gdtVersion){
		super(GDTConstants.SATZART_STAMMDATEN_ANFORDERN, gdtIdReceiver, gdtIdSender, zeichensatz, gdtVersion);
		
//		this.patientenkennung = patientenkennung;
		values.put(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG, patientenkennung);
	}
	
	@Override
	protected void createMessage(){
		super.createMessage();
		addLine(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG);
	}
	
	/**
	 * Bequemlichkeits Konstruktor für Datensatz 6300 - Beinhaltet nur die MUSS Felder
	 * 
	 * @param patientenkennung
	 * @param gdtVersion - default: {@link GDTConstants#GDT_VERSION}
	 */
	public GDTSatzNachricht6300(String patientenkennung, String gdtVersion) {
		this(patientenkennung, null, null, null, gdtVersion);
	}

	/**
	 * Parse an incoming 6300 Message from a String array
	 * 
	 * @param satznachricht
	 * @return class if successful else null
	 */
	public static GDTSatzNachricht6300 createfromStringArray(String[] satznachricht){
		HashMap<Integer, String> ds = new LinkedHashMap<Integer, String>();
		
		for (int i = 0; i < satznachricht.length; i++) {
			int length = Integer.parseInt(satznachricht[i].substring(0, 3));
			int feldkennung = Integer.parseInt(satznachricht[i].substring(3, 7));
			String value = satznachricht[i].substring(7, length-2);
			ds.put(feldkennung, value);
		}
		
		int satzkennung = Integer.parseInt(ds.get(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION));
		if(satzkennung!=GDTConstants.SATZART_STAMMDATEN_ANFORDERN) return null;
		
		return new GDTSatzNachricht6300(ds.get(GDTConstants.FELDKENNUNG_PATIENT_KENNUNG), 
			ds.get(GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER), 
			ds.get(GDTConstants.FELDKENNUNG_GDT_ID_SENDER), 
			ds.get(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ), 
			ds.get(GDTConstants.FELDKENNUNG_VERSION_GDT));
	}
	
}
