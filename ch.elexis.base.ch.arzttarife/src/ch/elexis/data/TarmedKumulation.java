package ch.elexis.data;

import java.util.List;

import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class TarmedKumulation extends PersistentObject {
	private static final String TABLENAME = "TARMED_KUMULATION"; //$NON-NLS-1$
	private static final String VERSION = "1.0.0";
	private static final String VERSION_ID = "Version";
	
	public static final String FLD_MASTER_CODE = "MasterCode";
	public static final String FLD_MASTER_ART = "MasterArt";
	public static final String FLD_SLAVE_CODE = "SlaveCode";
	public static final String FLD_SLAVE_ART = "SlaveArt";
	public static final String FLD_TYP = "Typ";
	public static final String FLD_VIEW = "View";
	public static final String FLD_VALID_SIDE = "ValidSide";
	public static final String FLD_VALID_FROM = "ValidFrom";
	public static final String FLD_VALID_TO = "ValidTo";
	
	public static final String TYP_EXCLUSION = "E";
	public static final String TYP_INCLUSION = "I";
	public static final String TYP_EXCLUSIVE = "X";
	
	public static TimeTool timeHelper = new TimeTool();
	
	// @formatter:off
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" 
		+"ID VARCHAR(25) primary key, "  
		+"lastupdate BIGINT," 
		+"deleted CHAR(1) default '0'," 
		
		+ "MasterCode			VARCHAR(25),"
		+ "MasterArt 			CHAR(1)," 
		+ "SlaveCode			VARCHAR(25)," 
		+ "SlaveArt 			CHAR(1)," 
		+ "Typ					CHAR(1),"
		+ "View					CHAR(1),"
		+ "ValidSide			CHAR(1)," 
		+ "ValidFrom			CHAR(10)," 
		+ "ValidTo				CHAR(10)"
		+ ");"
		+ "CREATE INDEX tarmedKumulation on " + TABLENAME + " (" + FLD_MASTER_CODE + ");"		
		+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_MASTER_CODE + ") VALUES (" + JdbcLink.wrap(VERSION_ID) + ", " + JdbcLink.wrap(VERSION) + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_MASTER_CODE, FLD_MASTER_ART, FLD_SLAVE_CODE, FLD_SLAVE_ART,
			FLD_TYP, FLD_VIEW, FLD_VALID_SIDE, FLD_VALID_FROM, FLD_VALID_TO);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(createDB);
		} else {
			TarmedKumulation version = load(VERSION_ID);
			VersionInfo vi = new VersionInfo(version.get(FLD_MASTER_CODE));
			if (vi.isOlder(VERSION)) {
				version.set(FLD_MASTER_CODE, VERSION);
			}
		}
	}
	
	public TarmedKumulation(){
		// leer
	}
	
	public static TarmedKumulation load(String id){
		return new TarmedKumulation(id);
	}
	
	protected TarmedKumulation(String id){
		super(id);
	}
	
	public TarmedKumulation(String masterCode, String masterArt, String slaveCode, String slaveArt,
		String typ, String view, String validSide, String from, String to){
		create(null);
		
		String[] values = new String[] {
			masterCode, masterArt, slaveCode, slaveArt, typ, view, validSide, from, to
		};
		String[] fields =
			new String[] {
				FLD_MASTER_CODE, FLD_MASTER_ART, FLD_SLAVE_CODE, FLD_SLAVE_ART, FLD_TYP, FLD_VIEW,
				FLD_VALID_SIDE, FLD_VALID_FROM, FLD_VALID_TO
			};
		set(fields, values);
	}
	
	public static String getDBTableName(){
		return TABLENAME;
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	@Override
	public String getLabel(){
		return getMasterCode() + ": " + getTyp() + " -> " + getSlaveCode() + " ["
			+ getValidFromTime().toString(TimeTool.DATE_COMPACT) + " - "
			+ getValidToTime().toString(TimeTool.DATE_COMPACT) + "]";
	}
	
	/**
	 * Get the exclusions as String, containing the service and chapter codes. Group exclusions are
	 * NOT part of the String.
	 * 
	 * @param code
	 * @param date
	 * @return
	 */
	public static String getExclusions(String code, TimeTool date){
		Query<TarmedKumulation> query = new Query<TarmedKumulation>(TarmedKumulation.class);
		query.add(TarmedKumulation.FLD_MASTER_CODE, Query.EQUALS, code);
		query.add(TarmedKumulation.FLD_TYP, Query.EQUALS, TarmedKumulation.TYP_EXCLUSION);
		
		List<TarmedKumulation> exclusions = query.execute();
		if (exclusions == null || exclusions.isEmpty()) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();
		for (TarmedKumulation excl : exclusions) {
			if ("G".equals(excl.getSlaveArt())) {
				continue;
			}
			if (excl.isValidKumulation(date)) {
				if (!sb.toString().isEmpty()) {
					sb.append(",");
				}
				sb.append(excl.getSlaveCode());
			}
		}
		return sb.toString();
	}
	
	/**
	 * Checks if the kumulation is still/already valid on the given date
	 * 
	 * @param date
	 *            on which it should be valid
	 * @return true if valid, false otherwise
	 */
	public boolean isValidKumulation(TimeTool date){
		TimeTool from = new TimeTool(getValidFrom());
		TimeTool to = new TimeTool(getValidTo());
		
		if (date.isAfterOrEqual(from) && date.isBeforeOrEqual(to)) {
			return true;
		}
		return false;
	}
	
	private TimeTool parseDate(String date){
		if (!StringTool.isNothing(date)) {
			timeHelper.set(date);
			return timeHelper;
		} else {
			return null;
		}
	}
	
	public String getMasterCode(){
		return get(FLD_MASTER_CODE);
	}
	
	public String getSlaveCode(){
		return get(FLD_SLAVE_CODE);
	}
	
	public String getMasterArt(){
		return get(FLD_MASTER_ART);
	}
	
	public String getSlaveArt(){
		return get(FLD_SLAVE_ART);
	}
	
	public String getTyp(){
		return get(FLD_TYP);
	}
	
	public String getView(){
		return get(FLD_VIEW);
	}
	
	public String getValidSide(){
		return get(FLD_VALID_SIDE);
	}
	
	public String getValidFrom(){
		return get(FLD_VALID_FROM);
	}
	
	public TimeTool getValidFromTime(){
		return parseDate(getValidFrom());
	}
	
	public String getValidTo(){
		return get(FLD_VALID_TO);
	}
	
	public TimeTool getValidToTime(){
		return parseDate(getValidTo());
	}
	
	public void setMasterCode(final String mc){
		set(FLD_MASTER_CODE, mc);
	}
	
	public void setSlaveCode(String sc){
		set(FLD_SLAVE_CODE, sc);
	}
	
	public void setMasterArt(String mArt){
		set(FLD_MASTER_ART, mArt);
	}
	
	public void setSlaveArt(String sArt){
		set(FLD_SLAVE_ART, sArt);
	}
	
	public void setTyp(String typ){
		set(FLD_TYP, typ);
	}
	
	public void setView(String view){
		set(FLD_VIEW, view);
	}
	
	public void setValidSide(String validSide){
		set(FLD_VALID_SIDE, validSide);
	}
	
	public void setValidFrom(String vFrom){
		set(FLD_VALID_FROM, vFrom);
	}
	
	public void setValidTo(String vTo){
		set(FLD_VALID_TO, vTo);
	}
}
