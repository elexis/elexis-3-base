/*******************************************************************************
 * Copyright (c) 2009-2010, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    G. Weirich  - added layout option
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data;

import java.util.ArrayList;

import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;

/**
 * Typ einer Messung
 * 
 * @author Antoine Kaufmann
 */
public class MessungTyp {
	String name;
	String title;
	String description;
	ArrayList<IMesswertTyp> fields;
	Panel panel;
	
	public MessungTyp(String n, String t, String d){
		name = n;
		title = t;
		description = d;
		fields = new ArrayList<IMesswertTyp>();
	}
	
	public MessungTyp(String n, String t, String d, Panel p){
		this(n, t, d);
		panel = p;
	}
	
	/**
	 * Neuen Messwerttyp hinzufügen
	 */
	public void addField(IMesswertTyp f){
		fields.add(f);
	}
	
	/**
	 * @return Interner Name dieses Messungstyps
	 */
	public String getName(){
		return name;
	}
	
	public Panel getPanel(){
		return panel;
	}
	
	/**
	 * @return Beschriftung die dem Benutzer angezeigt werden kann
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * @return Beschreibungdie dem Benutzer angezeigt werden kann
	 */
	public String getDescription(){
		return description;
	}
	
	/**
	 * Typen saemtlicher Messwerte in dieser Messung holen
	 * 
	 * @return Liste aller Messwert-Typen
	 */
	public ArrayList<IMesswertTyp> getMesswertTypen(){
		return fields;
	}
	
	/**
	 * Bestimmten Messwert-Typ dieser Messung anhand seines Namens heraussuchen
	 * 
	 * @param name
	 * @return
	 */
	public IMesswertTyp getMesswertTyp(String name){
		for (IMesswertTyp f : fields) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}
}
