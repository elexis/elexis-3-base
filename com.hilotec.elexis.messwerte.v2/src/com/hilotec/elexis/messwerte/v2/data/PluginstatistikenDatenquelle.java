/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    P. Chaubert - adapted to Messwerte V2
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data;

import java.util.LinkedList;
import java.util.List;

import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatensatz;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle;

/**
 * Schnittstelle zu hilotec-Pluginstatistiken. Diese Klasse dient als Datenquelle fuer das Plugin.
 * Auf die Messungen kann im Pluginstatistiken-Plugin ueber Messwert:Messungstyp zugegriffen werden.
 * Die Spaltennamen werden aus den Feldnamen im Messwertplugin uebernommen. Zusaetzlich sind bei
 * jedem Messwert zwei weitere Spalten mit Namen Patient und Datum vorhanden. Ersteres gibt die ID
 * des zugehoerigen Patienten zurueck, waehrend zweiters dem Datum der Messung entspricht. Wenn
 * gleichbenannte Felder existieren werden diese ignoriert, und die oben angegebenen
 * Spezialbedeutungen werden benutzt.
 * 
 * @author Antoine Kaufmann
 */
public class PluginstatistikenDatenquelle implements IDatenquelle {
	List<ITabelle> tabellen;
	
	/**
	 * Einzelne Tabelle in dieser Datenquelle. Diese repraesentiert jeweils einen bestimmten
	 * Messungstyp.
	 * 
	 * @author Antoine Kaufmann
	 */
	private static class MessungTabelle implements ITabelle {
		MessungTyp typ;
		
		/**
		 * Einzelner Datensatz in einer Tabelle, entspricht einer Messung
		 * 
		 * @author Antoine Kaufmann
		 */
		private static class MessungDatensatz implements IDatensatz {
			Messung messung;
			
			/**
			 * Neuer Datensatz auf Basis einer Messung erstellen
			 * 
			 * @param m
			 *            Messung
			 */
			public MessungDatensatz(Messung m){
				messung = m;
			}
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatensatz#getSpalte(java.lang
			 * .String)
			 */
			public String getSpalte(String name){
				if (name.equals("Patient")) {
					return messung.getPatient().getId();
				} else if (name.equals("Datum")) {
					return messung.getDatum();
				}
				
				Messwert mw = messung.getMesswert(name);
				if (mw == null) {
					return null;
				}
				return mw.getWert();
			}
		}
		
		/**
		 * Konstruktor. Initialisiert eine neue Tabelle auf Basis des uebergebenen Messungstyps.
		 * 
		 * @param typ
		 *            Typ den diese Tabelle beinhalten soll
		 */
		public MessungTabelle(MessungTyp typ){
			this.typ = typ;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle#getDatensaetze()
		 */
		public List<IDatensatz> getDatensaetze(){
			List<Messung> messungen = Messung.getMessungen(typ);
			List<IDatensatz> datensaetze = new LinkedList<IDatensatz>();
			for (Messung m : messungen) {
				datensaetze.add(new MessungDatensatz(m));
			}
			return datensaetze;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle#getName()
		 */
		public String getName(){
			return typ.getName();
		}
	}
	
	/**
	 * Konstruktor. Initialisiert die ganze Datenquelle mit Tabellen und allem.
	 */
	public PluginstatistikenDatenquelle(){
		tabellen = new LinkedList<ITabelle>();
		
		MessungKonfiguration konfig = MessungKonfiguration.getInstance();
		for (MessungTyp mt : konfig.getTypes()) {
			tabellen.add(new MessungTabelle(mt));
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle#getName()
	 */
	public String getName(){
		return "Messwert";
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle#getTabellen()
	 */
	public List<ITabelle> getTabellen(){
		return tabellen;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle#getTabelle(java.lang.String)
	 */
	public ITabelle getTabelle(String name){
		for (ITabelle tab : tabellen) {
			if (tab.getName().equals(name)) {
				return tab;
			}
		}
		return null;
	}
	
}
