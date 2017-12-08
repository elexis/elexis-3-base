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

package com.hilotec.elexis.pluginstatistiken.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;

import com.hilotec.elexis.pluginstatistiken.Datensatz;
import com.hilotec.elexis.pluginstatistiken.PluginstatistikException;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatenquelle;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.IDatensatz;
import com.hilotec.elexis.pluginstatistiken.schnittstelle.ITabelle;

/**
 * Einzelne Abfrage
 * 
 * @author Antoine Kaufmann
 */
public class KonfigurationQuery {
	String title;
	String from;
	String fromAs;
	KonfigurationWhere where = null;
	List<String> colsName;
	List<String> colsSource;
	/**
	 * Liste mit allen Joins dieser Abfrage
	 */
	List<Join> joins;
	
	/**
	 * Klasse um einen Join in der Abfrage abzubilden
	 * 
	 * @author Antoine Kaufmann
	 */
	public static class Join {
		enum JType {
			JOIN_INNER,
		}
		
		/**
		 * Tabellenname
		 */
		String table;
		/**
		 * Alias der Tabelle in der Abfrage
		 */
		String as;
		/**
		 * Praedikat fuer den Join, oder null falls keines existiert
		 */
		KonfigurationWhere where;
		/**
		 * Typ des Joins
		 */
		JType type;
		
		/**
		 * Konstruktor fuer neuen Join
		 * 
		 * @param table
		 *            Tabelle aus der die Daten fuer den Join kommen
		 * @param as
		 *            Alias fuer die Tabelle in der Abfrage
		 * @param w
		 *            Praedikat fuer den Join, oder null wenn keines existiert
		 * @param typ
		 *            Typ des Joins (z.B. JOIN_INNER).
		 */
		public Join(String table, String as, KonfigurationWhere w, JType typ){
			this.table = table;
			this.as = as;
			this.where = w;
		}
		
		/**
		 * Tabellenname auslesen
		 * 
		 * @return Tabellenname
		 */
		public String getTable(){
			return table;
		}
		
		/**
		 * Tabellenalias auslesen
		 * 
		 * @return Tabellenalias
		 */
		public String getAs(){
			return as;
		}
		
		/**
		 * Praedikat des Joins auslesen
		 * 
		 * @return Praedikat als Where-Klausel oder null
		 */
		public KonfigurationWhere getWhere(){
			return where;
		}
	}
	
	/**
	 * Neue Abfrage anlegen
	 * 
	 * @param t
	 *            Titel der Abfrage
	 */
	public KonfigurationQuery(String t){
		title = t;
		colsName = new ArrayList<String>();
		colsSource = new ArrayList<String>();
		joins = new LinkedList<Join>();
	}
	
	/**
	 * Quelltabelle setzen
	 */
	public void setFrom(String table, String as){
		from = table;
		fromAs = as;
	}
	
	/**
	 * Der Abfrage eine neue Spalte anfuegen
	 * 
	 * @param name
	 *            Name der Spalte
	 * @param source
	 *            Quelle fuer diese Spalte (fuer IDataAcees-Schnittstelle)
	 */
	public void addCol(String name, String source){
		colsName.add(name);
		colsSource.add(source);
	}
	
	/**
	 * Liste mit den Namen aller Spalten
	 */
	public List<String> getColNames(){
		return colsName;
	}
	
	/**
	 * Quellen der Spalten in Liste zurueckgeben
	 * 
	 * @return
	 */
	public List<String> getColSources(){
		return colsSource;
	}
	
	/**
	 * Where-Klausel fuer diese Abfrage setzen
	 */
	public void setWhere(KonfigurationWhere w){
		where = w;
	}
	
	/**
	 * Weiteren Join zur Abfrage hinzufuegen
	 * 
	 * @param j
	 *            Der neue Join
	 */
	public void addJoin(Join j){
		joins.add(j);
	}
	
	/**
	 * @return Titel dieser Abfrage
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Tabelle anhand des Identifiers in der Form Plugin:Tabelle suchen
	 * 
	 * @param name
	 *            Bezeichner fuer die Tabelle
	 * 
	 * @return Tabelle
	 * @throws PluginstatistikException
	 */
	private ITabelle getTabelle(String name) throws PluginstatistikException{
		String fromParts[] = name.split(":");
		if (fromParts.length < 2) {
			throw new PluginstatistikException(
				"Abfrage: Tabellenbezeichner ungueltig formatiert: '" + from + "'");
		}
		String dqPart = fromParts[0];
		String tabPart = fromParts[1];
		IDatenquelle dq = Konfiguration.getInstance().getDatenquelle(dqPart);
		if (dq == null) {
			throw new PluginstatistikException("Abfrage: Unbekannte Datenquelle: '" + dqPart + "'");
		}
		ITabelle t = dq.getTabelle(tabPart);
		if (t == null) {
			throw new PluginstatistikException("Abfrage: Unbekannte Tabelle: '" + tabPart
				+ "' (in " + " Datenquelle '" + dqPart + "')");
		}
		return t;
	}
	
	/**
	 * Daten heraussuchen
	 * 
	 * @param startDatum
	 *            Startdatum des angegebenen Bereichs
	 * @param endDatum
	 *            Enddatum des angegebenen Bereichs
	 * @param monitor
	 *            Archie-ProgressMonitor der es ermoeglcht, dem Benutzer den auktuellen Status der
	 *            Abfrage angezeigt werden kann.
	 * 
	 * @return Liste mit den gefundenen Datensaetzen
	 * @throws PluginstatistikException
	 */
	public List<Datensatz> getDaten(String startDatum, String endDatum, IProgressMonitor monitor)
		throws PluginstatistikException{
		List<Datensatz> data = new LinkedList<Datensatz>();
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("startdatum", startDatum);
		parameters.put("enddatum", endDatum);
		
		monitor.beginTask("Initialisiere Datenquelle", 1);
		ITabelle from = getTabelle(this.from);
		monitor.done();
		
		monitor.beginTask("Sammle Datensaetze", 1);
		List<IDatensatz> dsl = from.getDatensaetze();
		monitor.done();
		
		monitor.beginTask("Verarbeite Datensätze", dsl.size());
		for (IDatensatz fromDs : dsl) {
			Datensatz ds = new Datensatz(this, startDatum, endDatum);
			for (int i = 0; i < colsName.size(); i++) {
				ds.addSpalte(colsName.get(i), colsSource.get(i));
			}
			ds.addIntDs(this.fromAs, fromDs);
			joinTable(ds, joins, data);
			
			monitor.worked(1);
			
		}
		monitor.done();
		return data;
	}
	
	/**
	 * Gejointe Tabellen fuer einen einzelnen Datensatz zusammensammeln. Der uebergebene Datensatz
	 * wird jeweils kopiert und dann die neue Tabelle angehaengt. Die Funktion arbeitet rekursiv die
	 * Liste joinsTodo ab. Bei jedem Aufruf wird ein Eintrag entfernt bis die Liste leer ist. Die
	 * Datensaetze, die durch die Kette von Joins entstehen werden letztendlich in der Liste result
	 * abgelegt.
	 * 
	 * @param ds
	 *            Datensatz auf den die Tabellen gejoint werden sollen.
	 * @param joinsTodo
	 *            Liste mit den noch ausstehenden Joins (wird nicht veraendert)
	 * @param result
	 *            Liste in der die Datensaetze, die sich am Ende ergeben, abgelegt werden sollen.
	 * @throws PluginstatistikException
	 */
	private void joinTable(Datensatz ds, final List<Join> joinsTodo, List<Datensatz> result)
		throws PluginstatistikException{
		if ((joinsTodo == null) || (joinsTodo.size() == 0)) {
			if ((where == null) || (where.matches(ds))) {
				result.add(ds);
			}
		} else {
			Join j = joinsTodo.get(0);
			LinkedList<Join> newTodo = null;
			if (joinsTodo.size() > 1) {
				newTodo = new LinkedList<Join>(joinsTodo);
				newTodo.remove(0);
			}
			
			ITabelle tab = getTabelle(j.getTable());
			List<IDatensatz> dsl = tab.getDatensaetze();
			for (IDatensatz ids : dsl) {
				Datensatz tmpDs = new Datensatz(ds);
				tmpDs.addIntDs(j.getAs(), ids);
				if (j.getWhere().matches(tmpDs)) {
					joinTable(tmpDs, newTodo, result);
				}
			}
		}
	}
	
}
