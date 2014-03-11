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
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data;

import java.util.ArrayList;
import java.util.List;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.Activator;
import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;
import com.hilotec.elexis.messwerte.v2.views.Messages;

public class Messung extends PersistentObject {
	private static final String VERSION = "3"; // 05.02.2012 //$NON-NLS-1$
	public static final String PLUGIN_ID = Activator.PLUGIN_ID;
	private static final String TABLENAME = "COM_HILOTEC_ELEXIS_MESSWERTE_MESSUNGEN"; //$NON-NLS-1$
	
	static {
		addMapping(TABLENAME, "PatientID", "TypName", "Datum=S:D:Datum"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		checkTable();
	}
	
	private static final String setVersionSQL = "UPDATE " + TABLENAME + " SET TypName='" + VERSION //$NON-NLS-1$ //$NON-NLS-2$
		+ "' WHERE ID='VERSION'; "; //$NON-NLS-1$
	private static final String index1SQL = "CREATE INDEX idx1 on " + TABLENAME + " (PatientID);"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String index2SQL = "CREATE INDEX idx2 on " + TABLENAME + " (Datum);"; //$NON-NLS-1$ //$NON-NLS-2$
	private static final String index3SQL = "CREATE INDEX idx3 on " + TABLENAME //$NON-NLS-1$
		+ " (PatientID, Datum);"; //$NON-NLS-1$
	
	private static final String lengthTypName = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " MODIFY TypName VARCHAR(255);"; //$NON-NLS-1$
	
	private static final String create = "CREATE TABLE " + TABLENAME + " (" //$NON-NLS-1$ //$NON-NLS-2$
		+ "  ID			VARCHAR(25) PRIMARY KEY, " + "  lastupdate 	BIGINT, " //$NON-NLS-1$ //$NON-NLS-2$
		+ "  deleted		CHAR(1) DEFAULT '0', " + "  PatientID	VARCHAR(25), " //$NON-NLS-1$ //$NON-NLS-2$
		+ "  TypName		VARCHAR(255), " + "  Datum		CHAR(8) " + ");" + "INSERT INTO " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		+ " (ID, TypName) VALUES " + "	('VERSION', '" + VERSION + "');" + index1SQL + index2SQL //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		+ index3SQL;
	
	/**
	 * Pruefen ob die Tabelle existiert
	 */
	private static void checkTable(){
		Messung check = load("VERSION"); //$NON-NLS-1$
		if (!check.exists()) {
			createOrModifyTable(create);
		}
		try {
			int ver = Integer.parseInt(check.get("TypName")); //$NON-NLS-1$
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
		createOrModifyTable(lengthTypName);
		createOrModifyTable(index1SQL);
		createOrModifyTable(index2SQL);
		createOrModifyTable(index3SQL);
	}
	
	@Override
	public String getLabel(){
		return get("TypName"); //$NON-NLS-1$
	}
	
	@Override
	public String getTableName(){
		return TABLENAME;
	}
	
	/**
	 * Dieser Konstruktor darf nicht oeffentlich sein
	 */
	protected Messung(){}
	
	/**
	 * Vorhandene Messung anhand der ID erstellen
	 * 
	 * @param id
	 */
	protected Messung(String id){
		super(id);
	}
	
	/**
	 * Neue Messung erstellen
	 * 
	 * @param patient
	 *            Patient, dem diese Messung zugeordnet werden soll
	 * @param typ
	 *            Typ der Messung
	 */
	public Messung(Patient patient, MessungTyp typ){
		create(null);
		set("PatientID", patient.getId()); //$NON-NLS-1$
		set("TypName", typ.getName()); //$NON-NLS-1$
		set("Datum", new TimeTool().toString(TimeTool.DATE_GER)); //$NON-NLS-1$
		set("deleted", "1"); // sonst wird der Wert über den IDataAccessor schon gefunden... //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Messung anhand der ID laden
	 * 
	 * @param id
	 *            ID der Messung
	 * @return Messung
	 */
	public static Messung load(final String id){
		return new Messung(id);
	}
	
	/**
	 * Datum dieser Messung
	 */
	public String getDatum(){
		return get("Datum"); //$NON-NLS-1$
	}
	
	/**
	 * Messwert in dieser Messung anhand seines Namens holen
	 * 
	 * @param name
	 *            Name des Messwerttyps
	 * @return Messwert
	 */
	public Messwert getMesswert(String name){
		return getMesswert(name, true);
	}
	
	public Messwert getMesswert(String name, boolean create){
		Query<Messwert> query = new Query<Messwert>(Messwert.class);
		query.add("MessungID", Query.EQUALS, getId()); //$NON-NLS-1$
		query.and();
		query.add("Name", Query.EQUALS, name); //$NON-NLS-1$
		List<Messwert> list = query.execute();
		
		if (list.size() == 0) {
			if (create) {
				// Messwert existiert noch nicht, wir legen ihn neu an
				return new Messwert(this, name);
			}
			return null;
		}
		
		return list.get(0);
	}
	
	/**
	 * @return Liste aller Messwerte
	 */
	public List<Messwert> getMesswerte(){
		ArrayList<Messwert> messwerte = new ArrayList<Messwert>();
		MessungTyp typ = getTyp();
		if (typ != null) {
			List<IMesswertTyp> fields = typ.getMesswertTypen();
			
			for (IMesswertTyp dft : fields) {
				messwerte.add(getMesswert(dft.getName()));
			}
		}
		
		return messwerte;
	}
	
	/**
	 * @param datum
	 *            Datum neu setzen
	 */
	public void setDatum(String datum){
		set("Datum", datum); //$NON-NLS-1$
	}
	
	/**
	 * @return Typ der Messung
	 */
	public MessungTyp getTyp(){
		MessungKonfiguration config = MessungKonfiguration.getInstance();
		return config.getTypeByName(get("TypName")); //$NON-NLS-1$
	}
	
	/**
	 * @return Patient zu dem diese Messung gehoert
	 */
	public Patient getPatient(){
		return Patient.load(get("PatientID")); //$NON-NLS-1$
	}
	
	/**
	 * Alle Messungen eines bestimmten Typs zu einem bestimmten Patienten zusammensuchen.
	 * 
	 * @param patient
	 *            Der Patient
	 * @param typ
	 *            Typ der zu suchenden Messungen
	 * 
	 * @return Liste mit den Messungen
	 */
	public static List<Messung> getPatientMessungen(Patient patient, MessungTyp typ){
		Query<Messung> query = new Query<Messung>(Messung.class);
		query.add("PatientID", Query.EQUALS, patient.getId()); //$NON-NLS-1$
		if (typ != null) {
			query.and();
			query.add("TypName", Query.EQUALS, typ.getName()); //$NON-NLS-1$
		}
		query.orderBy(true, "Datum"); //$NON-NLS-1$
		return query.execute();
	}
	
	/**
	 * Alle Messungen zu einem bestimmten Patienten zusammensuchen.
	 * 
	 * @param patient
	 *            Der Patient
	 * 
	 * @return Liste mit den Messungen
	 */
	public static List<Messung> getAllePatientMessungen(Patient patient){
		Query<Messung> query = new Query<Messung>(Messung.class);
		query.add("PatientID", Query.EQUALS, patient.getId()); //$NON-NLS-1$
		query.orderBy(true, "Datum"); //$NON-NLS-1$
		return query.execute();
	}
	
	/**
	 * Alle Messungen eines bestimmten Typs zusammensuchen.
	 * 
	 * @param typ
	 *            Typ der zu suchenden Messung
	 * 
	 * @return Liste mit den Messungen
	 */
	public static List<Messung> getMessungen(MessungTyp typ){
		Query<Messung> query = new Query<Messung>(Messung.class);
		query.add("TypName", Query.EQUALS, typ.getName()); //$NON-NLS-1$
		return query.execute();
	}
	
	public static List<Messung> getMessungenForExport(MessungTyp t, TimeTool dateFrom,
		TimeTool dateTo){
		dateTo.addDays(1);
		Query<Messung> query = new Query<Messung>(Messung.class);
		query.add("TypName", Query.EQUALS, t.getName()); //$NON-NLS-1$
		query.add("Datum", Query.GREATER_OR_EQUAL, dateFrom.toDBString(false)); //$NON-NLS-1$
		query.add("Datum", Query.LESS, dateTo.toDBString(false)); //$NON-NLS-1$
		query.orderBy(true, "Datum", "PatientID"); //$NON-NLS-1$ //$NON-NLS-2$
		return query.execute();
	}
}
