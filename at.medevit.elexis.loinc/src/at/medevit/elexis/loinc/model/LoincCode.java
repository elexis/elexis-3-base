package at.medevit.elexis.loinc.model;

import java.util.List;

import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class LoincCode extends PersistentObject implements ch.elexis.core.model.ICodeElement {
	public static final String TABLENAME = "at_medevit_elexis_loinc"; //$NON-NLS-1$
	public static final String VERSION = "1.0.0"; //$NON-NLS-1$
	
	public static final String VERSIONTOPID = "TOP2000VERSION"; //$NON-NLS-1$
	public static final String VERSIONID = "VERSION"; //$NON-NLS-1$
	
	public static final String FLD_CODE = "code"; //$NON-NLS-1$
	public static final String FLD_LONGNAME = "longname"; //$NON-NLS-1$
	public static final String FLD_SHORTNAME = "shortname"; //$NON-NLS-1$
	public static final String FLD_CLASS = "class"; //$NON-NLS-1$
	public static final String FLD_UNIT = "unit"; //$NON-NLS-1$
	
	// @formatter:off
	static final String create = 
			"CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$
			"ID VARCHAR(25) primary key, " + //$NON-NLS-1$
			"lastupdate BIGINT," +
			"deleted CHAR(1) default '0'," + //$NON-NLS-1$
			
			"code VARCHAR(128)," + //$NON-NLS-1$
			"longname TEXT," + //$NON-NLS-1$
			"shortname VARCHAR(255)," + //$NON-NLS-1$
			"class VARCHAR(128)," + //$NON-NLS-1$
			"unit VARCHAR(128)" + //$NON-NLS-1$
			
			");" + //$NON-NLS-1$
			"CREATE INDEX loinc1 ON " + TABLENAME + " (" + FLD_CODE + ");" + //$NON-NLS-1$
			"INSERT INTO " + TABLENAME + " (ID," + FLD_CODE + ") VALUES (" + JdbcLink.wrap(VERSIONID) + "," + JdbcLink.wrap(VERSION) + ");" + //$NON-NLS-1$
			"INSERT INTO " + TABLENAME + " (ID," + FLD_CODE + ") VALUES (" + JdbcLink.wrap(VERSIONTOPID) + "," + JdbcLink.wrap("0.0.0") + ");"; //$NON-NLS-1$
	// @formatter:on
	
	static {
		addMapping(TABLENAME, FLD_CODE, FLD_LONGNAME, FLD_SHORTNAME, FLD_CLASS, FLD_UNIT);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(create);
		} else {
			LoincCode version = load(VERSIONID);
			VersionInfo vi = new VersionInfo(version.get(FLD_CODE));
			if (vi.isOlder(VERSION)) {
				// we should update eg. with createOrModifyTable(update.sql);
				// And then set the new version
				version.set(FLD_CODE, VERSION);
			}
		}
	}
	
	public LoincCode(String id){
		super(id);
	}
	
	public LoincCode(String code, String longname, String shortname, String clazz, String unit){
		create(null);
		set(FLD_CODE, code);
		set(FLD_LONGNAME, longname);
		set(FLD_SHORTNAME, shortname);
		set(FLD_CLASS, clazz);
		set(FLD_UNIT, unit);
	}
	
	public LoincCode(){
		// TODO Auto-generated constructor stub
	}
	
	public static LoincCode load(final String id){
		return new LoincCode(id);
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_CODE, FLD_LONGNAME);
		
		if (vals[1].trim().length() > 0)
			return vals[0] + " - " + vals[1].trim();
		else
			return vals[0];
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public String getCodeSystemName(){
		return "LOINC";
	}
	
	public String getCodeSystemCode(){
		return "999";
	}
	
	public String getCode(){
		return get(FLD_CODE);
	}
	
	public String getText(){
		return get(FLD_LONGNAME);
	}
	
	public static VersionInfo getTop2000Verion(){
		LoincCode top2000version = load(VERSIONTOPID);
		return new VersionInfo(top2000version.get(FLD_CODE));
	}
	
	public static void setTop2000Version(String version){
		LoincCode top2000version = load(VERSIONTOPID);
		top2000version.set(LoincCode.FLD_CODE, version);
	}
	
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}
}
