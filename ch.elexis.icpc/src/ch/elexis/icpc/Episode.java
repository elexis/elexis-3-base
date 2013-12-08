/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    D. Lutz - extended table
 *******************************************************************************/
package ch.elexis.icpc;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.VersionInfo;

public class Episode extends PersistentObject implements Comparable<Episode> {
	public static final String FLD_PATIENT_ID = "PatientID";
	public static final String FLD_STATUS = "Status";
	public static final String FLD_NUMBER = "Number";
	public static final String FLD_START_DATE = "StartDate";
	public static final String FLD_TITLE = "Title";
	public static final int INACTIVE = 0;
	public static final int ACTIVE = 1;
	
	protected static final String VERSION = "0.4.2";
	protected final static String TABLENAME = "CH_ELEXIS_ICPC_EPISODES";
	
	protected static final String INACTIVE_VALUE = "0";
	protected static final String ACTIVE_VALUE = "1";
	
	private final static String createDB = "CREATE TABLE " + TABLENAME + " ("
		+ "ID				VARCHAR(25)," + "lastupdate		BIGINT," + "deleted 		CHAR(1) default '0',"
		+ "PatientID		VARCHAR(25)," + "Title			VARCHAR(256)," + "StartDate      VARCHAR(20)," + // date
																								// of
																								// first
																								// occurrence;
		// may be simply a year or
		// any text
		"Number         VARCHAR(10)," + // number for individual, possibly hierarchical,
										// organization
		"Status         CHAR(1) DEFAULT '1'," + // status, '1' == active, '0' == inactive
		"ExtInfo		BLOB" + ");" +
		
		"CREATE INDEX " + TABLENAME + "1 ON " + TABLENAME + " (PatientID);" +
		
		"INSERT INTO " + TABLENAME + " (ID,Title) VALUES ('1'," + JdbcLink.wrap(VERSION) + ");";
	
	private static final String LINKNAME = TABLENAME + "_DIAGNOSES_LINK";
	private final static String createLink = "CREATE TABLE " + LINKNAME + " ("
		+ "ID				VARCHAR(25)," + "lastupdate 	BIGINT," + "deleted		char(1) default '0',"
		+ "Episode		VARCHAR(25)," + "Diagnosis		VARCHAR(80)" + ");";
	
	private static final String upd041 = "ALTER TABLE " + TABLENAME + " add lastupdate BIGINT;"
		+ "ALTER TABLE " + LINKNAME + " ADD lastupdate BIGINT;";
	
	private static final String upd042 = "CREATE INDEX " + TABLENAME + "2 ON " + TABLENAME
		+ " (Title);";
	
	static {
		addMapping(TABLENAME, FLD_PATIENT_ID, FLD_TITLE, FLD_START_DATE, FLD_NUMBER, FLD_STATUS,
			FLD_EXTINFO, "DiagLink=JOINT:Diagnosis:Episode:" + LINKNAME);
		JdbcLink j = getConnection();
		Episode version = load(StringConstants.ONE);
		if (!version.exists()) {
			createOrModifyTable(createDB);
			createOrModifyTable(createLink);
		} else {
			VersionInfo vi = new VersionInfo(version.get(FLD_TITLE));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("0.2.0")) {
					j.exec(j.translateFlavor("ALTER TABLE " + TABLENAME
						+ " ADD deleted CHAR(1) default '0';"));
					version.set(FLD_TITLE, VERSION);
				}
				
				if (vi.isOlder("0.3.0")) {
					String sql;
					
					// add column StartDate
					sql = "ALTER TABLE " + TABLENAME + " ADD StartDate VARCHAR(20);";
					j.exec(j.translateFlavor(sql));
					
					// add column Number
					sql = "ALTER TABLE " + TABLENAME + " ADD Number VARCHAR(10);";
					j.exec(j.translateFlavor(sql));
					
					// add column Status
					sql = "ALTER TABLE " + TABLENAME + " ADD Status CHAR(1) DEFAULT '1';";
					j.exec(j.translateFlavor(sql));
					
					version.set(FLD_TITLE, VERSION);
				}
				
				if (vi.isOlder("0.3.1")) {
					String sql = "ALTER TABLE " + TABLENAME + " ADD ExtInfo BLOB;";
					j.exec(j.translateFlavor(sql));
					version.set(FLD_TITLE, VERSION);
				}
				
				if (vi.isOlder("0.3.2")) {
					String sql = "ALTER TABLE " + TABLENAME + " MODIFY Title VARCHAR(256);";
					j.exec(j.translateFlavor(sql));
					version.set(FLD_TITLE, VERSION);
				}
				if (vi.isOlder("0.4.0")) {
					createOrModifyTable(createLink);
					version.set(FLD_TITLE, VERSION);
				}
				if (vi.isOlder("0.4.1")) {
					createOrModifyTable(upd041);
					version.set(FLD_TITLE, VERSION);
				}
				if (vi.isOlder("0.4.2")) {
					createOrModifyTable(upd042);
					version.set(FLD_TITLE, VERSION);
				}
			}
		}
	}
	
	public Episode(final Patient pat, final String title){
		create(null);
		set(new String[] {
			FLD_PATIENT_ID, FLD_TITLE
		}, pat.getId(), title);
	}
	
	public static Episode findEpisode(Patient pat, String title){
		Query<Episode> qbe = new Query<Episode>(Episode.class);
		qbe.add(FLD_PATIENT_ID, Query.EQUALS, pat.getId());
		qbe.add(FLD_TITLE, Query.EQUALS, title);
		List<Episode> result = qbe.execute();
		if (result == null || result.size() == 0) {
			return null;
		}
		return result.get(0);
	}
	
	@Override
	public String getLabel(){
		String title = get(FLD_TITLE);
		// String startDate = get("StartDate");
		String number = get(FLD_NUMBER);
		int status = getStatus();
		
		StringBuffer sb = new StringBuffer();
		/*
		 * if (!StringTool.isNothing(startDate)) { sb.append(startDate); sb.append(": "); }
		 */
		if (!StringTool.isNothing(number)) {
			sb.append(number).append(": ");
		}
		sb.append(title);
		
		/*
		 * if (!StringTool.isNothing(number)) { sb.append(" (" + number + ")"); }
		 */
		if (status == INACTIVE) {
			sb.append(" [" + getStatusText() + "]");
		}
		
		return sb.toString();
	}
	
	public List<IDiagnose> getDiagnoses(){
		List<String[]> res = getList("DiagLink", null);
		List<IDiagnose> ret = new ArrayList<IDiagnose>(res.size());
		for (String[] diag : res) {
			IDiagnose id = (IDiagnose) CoreHub.poFactory.createFromString(diag[0]);
			if (id != null) {
				ret.add(id);
			}
		}
		return ret;
	}
	
	public void addDiagnosis(final IDiagnose id){
		String clazz = id.getClass().getName();
		addToList("DiagLink", clazz + "::" + id.getCode(), new String[0]);
	}
	
	public void removeDiagnosis(final IDiagnose id){
		String clazz = id.getClass().getName();
		removeFromList("DiagLink", clazz + "::" + id.getCode());
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Episode load(final String id){
		return new Episode(id);
	}
	
	protected Episode(final String id){
		super(id);
	}
	
	protected Episode(){}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	public Patient getPatient(){
		String id = get(FLD_PATIENT_ID);
		Patient patient = Patient.load(id);
		return patient;
	}
	
	/**
	 * Get the status of an episode
	 * 
	 * @return Episode.ACTIVE or Episode.INACTIVE
	 */
	public int getStatus(){
		String statusText = get(FLD_STATUS);
		if ((statusText != null) && statusText.equals(ACTIVE_VALUE)) {
			return ACTIVE;
		} else {
			return INACTIVE;
		}
	}
	
	/**
	 * Get the status localized text
	 * 
	 * @return the status as localized text
	 */
	public String getStatusText(){
		int status = getStatus();
		if (status == ACTIVE) {
			return Messages.Active;
		} else {
			return Messages.Inactive;
		}
	}
	
	/**
	 * Set the status of an episode
	 * 
	 * @param status
	 *            Episode.ACTIVE or Episode.INACTIVE
	 */
	public void setStatus(final int status){
		switch (status) {
		case ACTIVE:
			set(FLD_STATUS, ACTIVE_VALUE);
			break;
		case INACTIVE:
			set(FLD_STATUS, INACTIVE_VALUE);
			break;
		default:
			set(FLD_STATUS, ACTIVE_VALUE);
			break;
		}
	}
	
	public int compareTo(final Episode e2){
		VersionInfo v1 = new VersionInfo(get(FLD_NUMBER));
		VersionInfo v2 = new VersionInfo(e2.get(FLD_NUMBER));
		if (v1.isNewer(v2)) {
			return 1;
		} else if (v1.isOlder(v2)) {
			return -1;
		}
		return 0;
	}
	
	public String getStartDate(){
		return get(FLD_START_DATE);
	}
	
	public void setStartDate(final String startDate){
		set(FLD_START_DATE, startDate);
	}
	
	public String getTitle(){
		return get(FLD_TITLE);
	}
	
	public void setTitle(final String title){
		set(FLD_TITLE, title);
	}
	
	public String getNumber(){
		return get(FLD_NUMBER);
	}
	
	public void setNumber(final String number){
		set(FLD_NUMBER, number);
	}
	
	@SuppressWarnings("unchecked")
	public String getExtField(final String name){
		Map extInfo = getMap(FLD_EXTINFO);
		return (String) extInfo.get(name);
	}
	
	@SuppressWarnings("unchecked")
	public void setExtField(final String name, final String text){
		Map extInfo = getMap(FLD_EXTINFO);
		extInfo.put(name, text);
		setMap(FLD_EXTINFO, extInfo);
	}
	
	/**
	 * find an Episode with a given name
	 * 
	 * @param name
	 *            the name to find
	 * @return the Episode with that name or null if none or more than one exist
	 */
	public static Episode findEpisode(final String name){
		List<Episode> res = new Query<Episode>(Episode.class, FLD_TITLE, name).execute();
		if (res.size() == 1) {
			return res.get(0);
		}
		return null;
	}
}
