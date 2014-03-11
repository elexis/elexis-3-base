/*******************************************************************************
 * Copyright (c) 2009-2010, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data;

import org.eclipse.swt.widgets.Label;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;

import com.hilotec.elexis.messwerte.v2.Activator;
import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;
import com.hilotec.elexis.messwerte.v2.views.Messages;

/**
 * @author Antoine Kaufmann
 */
public class Messwert extends PersistentObject {
	public static final String VERSION = "3"; // 05.02.2012
	public static final String PLUGIN_ID = Activator.PLUGIN_ID;
	private static final String TABLENAME = "COM_HILOTEC_ELEXIS_MESSWERTE_MESSWERTE";
	
	private Label iconLabel = null;
	
	static {
		addMapping(TABLENAME, "MessungID", "Name", "Wert");
		checkTable();
	}
	
	private static final String setVersionSQL = "UPDATE " + TABLENAME + " SET Name='" + VERSION
		+ "' WHERE ID='VERSION'; ";
	private static final String index1SQL = "CREATE INDEX idx1 on " + TABLENAME + " (MessungID);";
	private static final String index2SQL = "CREATE INDEX idx2 on " + TABLENAME + " (Name);";
	private static final String index3SQL = "CREATE INDEX idx3 on " + TABLENAME
		+ " (MessungID, Name);";
	
	private static final String lengthName = "ALTER TABLE " + TABLENAME
		+ " MODIFY Name VARCHAR(255);";
	private static final String lengthWert = "ALTER TABLE " + TABLENAME
		+ " MODIFY Wert VARCHAR(255);";
	
	private static final String create = "CREATE TABLE " + TABLENAME + " ("
		+ "  ID			VARCHAR(25) PRIMARY KEY, " + "  lastupdate 	BIGINT, "
		+ "  deleted		CHAR(1) DEFAULT '0', " + "  MessungID	VARCHAR(25), "
		+ "  Name			VARCHAR(255), " + "  Wert			VARCHAR(255) " + ");" + "INSERT INTO " + TABLENAME
		+ " (ID, Name) VALUES " + "	('VERSION', '" + VERSION + "');" + index1SQL + index2SQL
		+ index3SQL;
	
	/**
	 * Pruefen ob die Tabelle existiert, und wenn nein, anlegen
	 */
	private static void checkTable(){
		Messwert check = load("VERSION");
		if (!check.exists()) {
			createOrModifyTable(create);
		}
		try {
			int ver = Integer.parseInt(check.get("Name"));
			switch (ver) {
			case 1:
				// No known changes between Version 1 and 2
				updateToVersion3();
				createOrModifyTable(setVersionSQL);
				break;
			case 2:
				updateToVersion3();
				createOrModifyTable(setVersionSQL);
				break;
			}
		} catch (Exception e) {
			SWTHelper.showError(Messages.DBError, String.format(Messages.DBErrorTable, TABLENAME));
		}
	}
	
	private static void updateToVersion3(){
		createOrModifyTable(lengthName);
		createOrModifyTable(lengthWert);
		createOrModifyTable(index1SQL);
		createOrModifyTable(index2SQL);
		createOrModifyTable(index3SQL);
	}
	
	@Override
	public String getLabel(){
		return get("Name");
	}
	
	@Override
	public String getTableName(){
		return TABLENAME;
	}
	
	/**
	 * Dieser Konstruktor darf nicht von aussen erreichbar sein
	 */
	protected Messwert(){}
	
	/**
	 * Bereits existierenden Messwert anhand seiner ID erstellen
	 * 
	 * @param id
	 *            ID
	 */
	protected Messwert(String id){
		super(id);
	}
	
	/**
	 * Neuen Messwert anglegen
	 * 
	 * @param messung
	 *            Messung zu der dieser Messwert gehoeren soll
	 * @param name
	 *            Name des Messwertes
	 * @param wert
	 *            Zu speichernder Wert
	 */
	public Messwert(Messung messung, String name, String wert){
		create(null);
		set("MessungID", messung.getId());
		set("Name", name);
		set("Wert", wert);
	}
	
	/**
	 * Neuen Messwert anlegen
	 * 
	 * @param messung
	 *            Messung zu der dieser Messwert gehoeren soll
	 * @param name
	 *            Name dieses Messwertes
	 */
	public Messwert(Messung messung, String name){
		create(null);
		set("MessungID", messung.getId());
		set("Name", name);
		set("Wert", getTyp().getDefault(this));
	}
	
	/**
	 * Messwert anhand seiner ID aus der Datenbank laden
	 * 
	 * @param id
	 *            ID des gewuenschten Messwerts
	 * 
	 * @return Messwert
	 */
	public static Messwert load(final String id){
		return new Messwert(id);
	}
	
	/**
	 * @return Name dieses Messwertes
	 */
	public String getName(){
		return get("Name");
	}
	
	/**
	 * @return Eigentlicher Messwert
	 */
	public String getWert(){
		return get("Wert");
	}
	
	/**
	 * @return Dem Benutzer anzeigbare Form dieses Messwertes
	 */
	public String getDarstellungswert(){
		return getTyp().getDarstellungswert(getWert());
	}
	
	/**
	 * Messwert aendern
	 * 
	 * @param wert
	 *            Neuer Wert
	 */
	public void setWert(String wert){
		set("Wert", wert);
	}
	
	/**
	 * @return Die Messung zu der diese Messung gehoert
	 */
	public Messung getMessung(){
		return new Messung(get("MessungID"));
	}
	
	/**
	 * @return Typ des Messwertes
	 */
	public IMesswertTyp getTyp(){
		return getMessung().getTyp().getMesswertTyp(getName());
	}
	
	public void setIconLabel(Label il){
		iconLabel = il;
	}
	
	public Label getIconLabel(){
		return iconLabel;
	}
	
}
