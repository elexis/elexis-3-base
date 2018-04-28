/*******************************************************************************
 * Copyright (c) 2006-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    M. Descher / MEDEVIT - minor refactorings
 *    
 *******************************************************************************/

package ch.elexis.data;

import java.util.List;
import java.util.Map;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class ICD10 extends PersistentObject implements IDiagnose {
	
	public static final String CODESYSTEM_NAME = "ICD-10";
	
	public static final String VERSION = "1.0.2";
	public static final String TABLENAME = "ICD10";
	
	public static final String FLD_CODE = "Code";
	public static final String FLD_TEXT = "Text";
	
	static final String drop = "DROP INDEX icd1;" + //$NON-NLS-1$
		"DROP INDEX icd2;" + //$NON-NLS-1$
		"DROP TABLE ICD10;"; //$NON-NLS-1$
	
	static final String create = "CREATE TABLE ICD10 (" + //$NON-NLS-1$
		"ID       VARCHAR(25) primary key, " + //$NON-NLS-1$
		"lastupdate BIGINT," + "deleted  CHAR(1) default '0'," + //$NON-NLS-1$
		"parent   VARCHAR(25)," + //$NON-NLS-1$
		"ICDCode  VARCHAR(10)," + //$NON-NLS-1$
		"encoded  TEXT," + //$NON-NLS-1$
		"ICDTxt   TEXT," + //$NON-NLS-1$
		"ExtInfo  BLOB);" + //$NON-NLS-1$
		"CREATE INDEX icd1 ON ICD10 (parent);" + //$NON-NLS-1$
		"CREATE INDEX icd2 ON ICD10 (ICDCode);" + //$NON-NLS-1$
		"INSERT INTO " + TABLENAME + " (ID,ICDTxt) VALUES ('1'," + JdbcLink.wrap(VERSION) + ");";
	
	public static void initialize(){
		if (PersistentObject.tableExists("ICD10")) {
			createOrModifyTable(drop);
		}
		createOrModifyTable(create);
		
	}
	
	static {
		addMapping(
			"ICD10", "parent", FLD_CODE + "=ICDCode", FLD_TEXT + "=ICDTxt", "encoded", "ExtInfo"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		if (!PersistentObject.tableExists("ICD10")) {
			initialize();
		}
		
		ICD10 check = load("1");
		if (check.exists()) {
			VersionInfo vi = new VersionInfo(check.get("Text"));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("1.0.1")) {
					getConnection().exec(
						"ALTER TABLE " + TABLENAME + " ADD deleted CHAR(1) default '0';");
					check.set("Text", VERSION);
				}
				if (vi.isOlder("1.0.2")) {
					getConnection().exec("ALTER TABLE " + TABLENAME + " ADD lastupdate BIGINT;");
					check.set("Text", VERSION);
				}
			}
		}
	}
	static final int LEVEL = 0;
	static final int TERMINAL = 1;
	static final int GENERATED = 2;
	static final int KIND = 3;
	static final int CHAPTER = 4;
	static final int GROUP = 5;
	static final int SUPERCODE = 6;
	static final int CODE = 7;
	static final int CODE_SHORT = 8;
	static final int CODE_COMPACT = 9;
	static final int TEXT = 10;
	
	public static ICD10 load(final String id){
		ICD10 ret = new ICD10(id);
		if (!ret.exists()) {
			String dbId = new Query<ICD10>(ICD10.class).findSingle("Code", "=", id); //$NON-NLS-1$ //$NON-NLS-2$
			if (dbId != null) {
				ret = new ICD10(dbId);
			}
		}
		return ret;
	}
	
	public ICD10(final String parent, final String code, final String shortCode){
		create(null);
		set("Code", code); //$NON-NLS-1$
		set("encoded", shortCode); //$NON-NLS-1$
		set("parent", parent); //$NON-NLS-1$
		set("Text", getField(TEXT)); //$NON-NLS-1$
	}
	
	/*
	 * public String createParentCode(){ String code=getField(CODE); String ret="NIL"; String
	 * chapter=getField(CHAPTER); String group=chapter+":"+getField(GROUP); String
	 * supercode=getField(SUPERCODE); if(code.equals(supercode)){ if(code.equals(group)){
	 * if(code.equals(chapter)){ ret="NIL"; }else{ ret=chapter; } }else{ ret=group; } }else{
	 * ret=supercode; } return ret; }
	 */
	
	public String getEncoded(){
		return get("encoded"); //$NON-NLS-1$
	}
	
	public String getField(final int f){
		return getEncoded().split(";")[f]; //$NON-NLS-1$
	}
	
	public ICD10(){}
	
	protected ICD10(final String id){
		super(id);
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_CODE, FLD_TEXT);
		return vals[0]+StringConstants.SPACE+vals[1];
	}
	
	@Override
	protected String getTableName(){
		return "ICD10"; //$NON-NLS-1$
	}
	
	public String getCode(){
		return get(FLD_CODE); //$NON-NLS-1$
	}
	
	public String getText(){
		return get(FLD_TEXT); //$NON-NLS-1$
	}
	
	public String getCodeSystemName(){
		return CODESYSTEM_NAME; //$NON-NLS-1$
	}
	
	@SuppressWarnings("unchecked")
	public void setExt(final String name, final String value){
		Map ext = getExtInfo();
		ext.put(name, value);
		writeExtInfo(ext);
	}
	
	public String getExt(final String name){
		Map ext = getExtInfo();
		String ret = (String) ext.get(name);
		return checkNull(ret);
	}
	
	public Map getExtInfo(){
		return getMap(FLD_EXTINFO); //$NON-NLS-1$
	}
	
	public void writeExtInfo(final Map ext){
		setMap(FLD_EXTINFO, ext); //$NON-NLS-1$
	}
	
	@Override
	public boolean isDragOK(){
		if (getField(TERMINAL).equals("T")) { //$NON-NLS-1$
			return true;
		}
		return false;
	}
	
	public String getCodeSystemCode(){
		return "999"; //$NON-NLS-1$
	}
	
	public List<Object> getActions(Object kontext){
		// TODO Auto-generated method stub
		return null;
	}
	
}
