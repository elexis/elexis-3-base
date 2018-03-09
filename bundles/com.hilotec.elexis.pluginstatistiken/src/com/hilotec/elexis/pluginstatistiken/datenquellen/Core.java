/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    
 *******************************************************************************/

package com.hilotec.elexis.pluginstatistiken.datenquellen;

import java.util.LinkedList;
import java.util.List;

import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.data.Verrechnet;

import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatensatz;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle;

/**
 * Datenquelle fuer die wichtigsten Tabellen aus dem Core. Das koennte man eigentlich in ein eigenes
 * Fragment auslagern, aber ich will auch nicht das Elexis-Repo fluten. ;-)
 * 
 * @author Antoine Kaufmann
 */
public class Core implements IDatenquelle {
	List<ITabelle> tabellen;
	
	/**
	 * Basisklasse fuer all die Core-Tabellen, die direkt auf PersistentObject, aufsetzen. Fuer
	 * diese muss dann keine eigene Klasse geschrieben werden, wenn einfach nur die vom Objekt
	 * gestellten Spalten verfuegbar sein sollen.
	 * 
	 * @author Antoine Kaufmann
	 */
	private static class CoreTabelle implements ITabelle {
		Class<?> poClass;
		String name;
		
		/**
		 * Einzelner Datensatz, der direkt aus einem PO besteht.
		 * 
		 * @author Antoine Kaufmann
		 */
		private static class CoreDatensatz implements IDatensatz {
			PersistentObject obj;
			
			/**
			 * Datensatz erstellen
			 * 
			 * @param po
			 *            PersistentObject aus dem dieser Datensatz bestehen soll
			 */
			public CoreDatensatz(PersistentObject po){
				obj = po;
			}
			
			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatensatz#getSpalte(java.lang
			 * .String)
			 */
			public String getSpalte(String name){
				return obj.get(name);
			}
		}
		
		/**
		 * Konstruktor fuer eine Core-Tabelle, die rein nur auf PO basiert.
		 * 
		 * @param name
		 *            Gewuenschter Tabellenname
		 * @param cl
		 *            Klasse der Objekte dieser Tabelle
		 */
		public CoreTabelle(String name, Class<?> cl){
			this.name = name;
			poClass = cl;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle#getDatensaetze()
		 */
		@SuppressWarnings("unchecked")
		public List<IDatensatz> getDatensaetze(){
			Query<?> q = new Query(poClass);
			List<?> pol = q.execute();
			List<IDatensatz> datensaetze = new LinkedList<IDatensatz>();
			for (Object o : pol) {
				datensaetze.add(new CoreDatensatz((PersistentObject) o));
			}
			return datensaetze;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle#getName()
		 */
		public String getName(){
			return name;
		}
	}
	
	/**
	 * Konstruktor
	 */
	public Core(){
		tabellen = new LinkedList<ITabelle>();
		tabellen.add(new CoreTabelle("Patient", Patient.class));
		tabellen.add(new CoreTabelle("Fall", Fall.class));
		tabellen.add(new CoreTabelle("Konsultation", Konsultation.class));
		tabellen.add(new CoreTabelle("Verrechnet", Verrechnet.class));
		tabellen.add(new CoreTabelle("Kontakt", Kontakt.class));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle#getName()
	 */
	public String getName(){
		return "Core";
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
