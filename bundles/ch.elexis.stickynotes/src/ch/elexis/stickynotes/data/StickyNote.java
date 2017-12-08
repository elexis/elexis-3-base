/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    J, Kurath - Sponsoring
 *    
 *******************************************************************************/
package ch.elexis.stickynotes.data;

import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;

public class StickyNote extends PersistentObject {
	private static final String TABLENAME = "CH_ELEXIS_STICKYNOTES"; //$NON-NLS-1$
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$
	private static final String createDB = "CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
		"ID		VARCHAR(25) primary key," + //$NON-NLS-1$
		"lastupdate	BIGINT," + //$NON-NLS-1$
		"deleted	CHAR(1) default '0'," + //$NON-NLS-1$
		"PatientID	VARCHAR(25)," + //$NON-NLS-1$
		"contents	BLOB);" + //$NON-NLS-1$
		"INSERT INTO " + TABLENAME + " (ID,PatientID) VALUES('VERSION','" + VERSION + "');" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		"CREATE INDEX " + TABLENAME + "1 ON " + TABLENAME + " (PatientID);"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	static {
		addMapping(TABLENAME, "PatientID", "text=S:C:contents"); //$NON-NLS-1$ //$NON-NLS-2$
		StickyNote note = load("VERSION"); //$NON-NLS-1$
		if (!note.exists()) {
			createOrModifyTable(createDB);
		}
	}
	
	public StickyNote(Patient p){
		create(null);
		set("PatientID", p.getId()); //$NON-NLS-1$
	}
	
	public Patient getPatient(){
		Patient pat = Patient.load(checkNull(get("PatientID"))); //$NON-NLS-1$
		if (!pat.exists()) {
			return null;
		}
		return pat;
	}
	
	public void setText(String t){
		set("text", t); //$NON-NLS-1$
	}
	
	public String getText(){
		return checkNull(get("text")); //$NON-NLS-1$
	}
	
	@Override
	public String getLabel(){
		Patient p = getPatient();
		if (p == null) {
			return "??"; //$NON-NLS-1$
		}
		return p.getLabel() + ":" + StringTool.limitLength(getText(), 25); //$NON-NLS-1$
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static StickyNote load(Patient p){
		String id = new Query<StickyNote>(StickyNote.class).findSingle("PatientID", "=", p.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		if (id != null) {
			StickyNote ret = load(id);
			if (ret.exists()) {
				return ret;
			}
		}
		return new StickyNote(p);
	}
	
	public static StickyNote load(String id){
		return new StickyNote(id);
	}
	
	protected StickyNote(){}
	
	protected StickyNote(String id){
		super(id);
	}
}
