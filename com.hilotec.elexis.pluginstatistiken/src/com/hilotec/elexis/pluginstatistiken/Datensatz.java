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

package com.hilotec.elexis.pluginstatistiken;

import java.util.HashMap;

import com.hilotec.elexis.pluginstatistiken.config.KonfigurationQuery;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatensatz;

/**
 * Einzelner Datensatz einer Abfrage. Ein Datensatz besteht jeweils aus einem oder mehreren internen
 * Datensaetzen (einer pro Tabelle die in der Abfrage referenziert wird, also fuer From und alle
 * Joins)
 * 
 * @author Antoine Kaufmann
 */
public class Datensatz {
	HashMap<String, IDatensatz> internalRows;
	HashMap<String, String> spalten;
	
	/**
	 * Konstruktor fuer Datensatz
	 * 
	 * @param q
	 *            Abfrage zu der dieser Datensatz gehoeren soll
	 * @param ids
	 *            Roher Datensatz von der Datenquelle
	 * @param startDatum
	 *            Startdatum des Bereichs der als Parameter angegeben wurde.
	 * @param endDatum
	 *            Enddatum des Bereiches
	 */
	public Datensatz(KonfigurationQuery q, String startDatum, String endDatum){
		internalRows = new HashMap<String, IDatensatz>();
		spalten = new HashMap<String, String>();
	}
	
	/**
	 * Kopierkonstruktor
	 */
	public Datensatz(Datensatz orig){
		internalRows = new HashMap<String, IDatensatz>(orig.internalRows);
		spalten = new HashMap<String, String>(orig.spalten);
	}
	
	/**
	 * Neue Abfragespalte anhaengen
	 * 
	 * @param name
	 *            Spaltenname
	 * @param source
	 *            Datenquelle fuer die Spalte; Dabei kann es sich entweder um einen Verweis auf eine
	 *            andere Spalte, oder auf eine Tabellenspalte handeln.
	 */
	public void addSpalte(String name, String source){
		spalten.put(name, source);
	}
	
	/**
	 * Internen Datensatz hinzufuegen. Bei Joins, die zu NULL-Werten fuehren, muss trotzdem ein
	 * interner Datensatz angelegt werden mit dem entsprechenden Namen, und null als ids.
	 * 
	 * @param as
	 *            Alias der Tabelle die diesen Datensatz stellt
	 * @param ids
	 *            Interner Datensatz
	 */
	public void addIntDs(String as, IDatensatz ids){
		internalRows.put(as, ids);
	}
	
	/**
	 * Bestimmtes Feld des Datensatzes auslesen. Dabei koennen sowohl Felder ausgelesen werden, die
	 * direkt als Spalte in der Abfrage drin sind anhand des Namens, als auch Spalten aus anderen
	 * Tabellen in der Abfrage, die in der Form Alias.Spalte angesprochen werden koennen. Alias ist
	 * hierbei der Tabellen Name, der in der Konfiguration als "as" angegeben wurde.
	 * 
	 * @param name
	 *            Name des Felds
	 * 
	 * @return Wert des Feldes oder null, wenn das Feld nicht gefunden wurde.
	 */
	public String getFeld(String name){
		String source = spalten.get(name);
		if (source != null) {
			return getIntFeld(source);
		}
		return getIntFeld(name);
	}
	
	/**
	 * Wert aus einer Tabelle auslesen. Der Spaltenbezeichner muss die Form Alias.Spalte haben.
	 * 
	 * @param name
	 *            Spalte, die ausgelesen werden soll
	 * 
	 * @return Wert der Spalte, oder null, wenn diese nicht gefunden wurde
	 */
	private String getIntFeld(String name){
		if (name.indexOf('.') < 0) {
			return null;
		}
		String parts[] = name.split("\\.");
		String as = parts[0];
		String spalte = parts[1];
		
		IDatensatz ids = internalRows.get(as);
		if (ids == null) {
			return null;
		}
		return ids.getSpalte(spalte);
	}
}
