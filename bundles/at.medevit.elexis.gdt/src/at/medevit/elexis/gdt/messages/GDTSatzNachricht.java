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
package at.medevit.elexis.gdt.messages;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import at.medevit.elexis.gdt.constants.GDTConstants;

public class GDTSatzNachricht {
	
	final static String CRLF = "\r\n";
	
	LinkedList<String> message = null;
	HashMap<Integer, String> values = new LinkedHashMap<Integer, String>();
	
	boolean addAllNotAddedIfSet = false;
	HashSet<Integer> added = new HashSet<>();
	
	int charCounterSatzLaenge = 0;
	
	DecimalFormat threePlaces = new DecimalFormat("000");
	DecimalFormat fivePlaces = new DecimalFormat("00000");
	
	/**
	 * Erzeugt das Grundgerüst einer GDT Satznachricht, beinhaltet bereits folgende Felder
	 * SATZIDENTIFIKATION, GDT_ID_EMPFÄNGER, GDT_ID_SENDER, VERWENDETER ZEICHENSATZ, GDT VERSION
	 * 
	 * @param gdtIdReceiver optional
	 * @param gdtIdSender optional
	 * @param zeichensatz optional
	 * @param gdtVersion - default: {@link GDTConstants#GDT_VERSION}
	 */
	public GDTSatzNachricht(int satzart, String gdtIdReceiver, String gdtIdSender, String zeichensatz, String gdtVersion){
		charCounterSatzLaenge = 0;
		
		values.put(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION, satzart+"");
		values.put(GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER, gdtIdReceiver);
		values.put(GDTConstants.FELDKENNUNG_GDT_ID_SENDER, gdtIdSender);
		values.put(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ, zeichensatz);
		values.put(GDTConstants.FELDKENNUNG_VERSION_GDT, gdtVersion);	
	}

	protected void addLine(String str){
		int length = str.length();
		length+= threePlaces.format(length).length(); // CRLF+3 chars for the length itself
		length+=CRLF.length();

		String line = threePlaces.format(length)+str+CRLF;
		message.add(line);
		charCounterSatzLaenge += line.length();
	}
	
	protected void addLine(int feldkennung){
		added.add(feldkennung);
		addLine(feldkennung+""+values.get(feldkennung));
	}
	
	/**
	 * If a value for the key feldkennung can be resolved (i.e. != null), add it to the message
	 * @param feldkennung
	 */
	protected void ifSetAddLine(int feldkennung){
		added.add(feldkennung);
		if(values.get(feldkennung)!=null) addLine(feldkennung);
		
	}
	
	protected void addAllNotAddedIfSet(){
		TreeSet<Integer> sorted = new TreeSet<>(values.keySet());
		for (Integer feldkennung : sorted) {
			if (!added.contains(feldkennung)) {
				ifSetAddLine(feldkennung);
			}
		}
	}
	
	protected void createMessage() {
		addLine(GDTConstants.FELDKENNUNG_SATZIDENTIFIKATION);
		ifSetAddLine(GDTConstants.FELDKENNUNG_GDT_ID_EMPFAENGER);
		ifSetAddLine(GDTConstants.FELDKENNUNG_GDT_ID_SENDER);
		ifSetAddLine(GDTConstants.FELDKENNUNG_VERWENDETER_ZEICHENSATZ);
		ifSetAddLine(GDTConstants.FELDKENNUNG_VERSION_GDT);
	}
	
	/**
	 * Liefert den Eintrag für eine bestimmte Feldkennung, falls verfügbar.
	 * 
	 * @param feldkennung see {@link GDTConstants}
	 * @return returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public String getValue(int feldkennung) {
		return values.get(feldkennung);
	}
	public void setValue(int feldkennung, String value) {
		values.put(feldkennung, value);
	}
	
	public HashMap<Integer, String> getValues() {
		return values;
	}
	
	public void setValues(HashMap<Integer, String> values) {
		this.values = values;
	}
	
	/**
	 * Erstellt eine Textnachricht aus dem gewählten Typ und liefert diese als String Array
	 * 
	 * @return
	 */
	public String[] getMessage(){
		if(message == null) {
			message = new LinkedList<String>();
			createMessage();
			if (addAllNotAddedIfSet) {
				addAllNotAddedIfSet();
			}
			finalizeMessage();
		}
		return message.toArray(new String[]{});
	}
	
	protected void finalizeMessage(){
		charCounterSatzLaenge+=14;
		String line = "014"+GDTConstants.FELDKENNUNG_SATZLAENGE+""+fivePlaces.format(charCounterSatzLaenge)+CRLF;
		message.add(1, line);
	}
	
	/**
	 * Set true if additional values have been set and should be included in the message.
	 * 
	 * @param value
	 */
	public void setAddAllNotAddedIfSet(boolean value){
		this.addAllNotAddedIfSet = value;
	}
}
