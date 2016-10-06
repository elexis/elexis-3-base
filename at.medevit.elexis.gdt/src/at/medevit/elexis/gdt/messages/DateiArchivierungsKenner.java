/*******************************************************************************
 * Copyright (c) 2011, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 *******************************************************************************/
package at.medevit.elexis.gdt.messages;

import java.util.HashMap;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class DateiArchivierungsKenner {
	
	HashMap<Integer, String> values = new HashMap<Integer, String>();
	
	/**
	 * Beinhaltet einen Ergebnisblock des Typs 6202 Datei-Archivierungskenner, welcher wiederum folgende Feldkennungen beinhaltet:
	 * 6203 Dateiformat
	 * 6204 Dateiinhalt
	 * 6205 Verweis auf die Datei
	 * 
	 * @param dateiArchivierungsKenner
	 * @param dateiformat
	 * @param dateiinhalt
	 * @param verweisAufDieDatei
	 */
	public DateiArchivierungsKenner(String dateiArchivierungsKenner, String dateiformat,
		String dateiinhalt, String verweisAufDieDatei){
		
		values.put(GDTConstants.FELDKENNUNG_DATEI_ARCHIVIERUNGSNUMMER, dateiArchivierungsKenner);
		values.put(GDTConstants.FELDKENNUNG_DATEIFORMAT, dateiformat);
		values.put(GDTConstants.FELDKENNUNG_DATEIINHALT, dateiinhalt);
		values.put(GDTConstants.FELDKENNUNG_VERWEIS_AUF_DATEI, verweisAufDieDatei);
	}
	
	/**
	 * @param feldkennung, where either 6302, 6303, 6304 or 6305
	 * @return
	 */
	public String getValue(int feldkennung) {
		return values.get(feldkennung);
	}
	public void setValue(int feldkennung, String value) {
		values.put(feldkennung, value);
	}
	
}
