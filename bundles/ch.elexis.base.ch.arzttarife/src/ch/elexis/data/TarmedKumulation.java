package ch.elexis.data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class TarmedKumulation extends PersistentObject {
	
	public enum TarmedKumulationType {
			SERVICE("L"), GROUP("G"), CHAPTER("K"), BLOCK("B");
		
		private String art;
		
		TarmedKumulationType(String art){
			this.art = art;
		}
		
		public String getArt(){
			return art;
		}
		
		public static TarmedKumulationType ofArt(String slaveArt){
			if ("L".equals(slaveArt)) {
				return SERVICE;
			} else if ("G".equals(slaveArt)) {
				return GROUP;
			} else if ("K".equals(slaveArt)) {
				return CHAPTER;
			} else if ("B".equals(slaveArt)) {
				return BLOCK;
			}
			return null;
		}
		
		public static String toString(TarmedKumulationType type){
			if (type == SERVICE) {
				return "Leistung";
			} else if (type == GROUP) {
				return "Gruppe";
			} else if (type == CHAPTER) {
				return "Kapitel";
			} else if (type == BLOCK) {
				return "Block";
			}
			return null;
		}
	}
	
	private static final String TABLENAME = "TARMED_KUMULATION"; //$NON-NLS-1$
	private static final String VERSION = "1.1.0";
	
	public static final String FLD_MASTER_CODE = "MasterCode";
	public static final String FLD_MASTER_ART = "MasterArt";
	public static final String FLD_SLAVE_CODE = "SlaveCode";
	public static final String FLD_SLAVE_ART = "SlaveArt";
	public static final String FLD_TYP = "Typ";
	public static final String FLD_VIEW = "View";
	public static final String FLD_VALID_SIDE = "ValidSide";
	public static final String FLD_VALID_FROM = "ValidFrom";
	public static final String FLD_VALID_TO = "ValidTo";
	public static final String FLD_LAW = "Law";
	
	private static final String VERSION_110 = "1.1.0";
	
	public static final String TYP_EXCLUSION = "E";
	public static final String TYP_INCLUSION = "I";
	public static final String TYP_EXCLUSIVE = "X";
	
	public static TimeTool timeHelper = new TimeTool();
	
	// @formatter:off
	private static final String upd110 = "ALTER TABLE " + TABLENAME + " ADD " + FLD_LAW + " VARCHAR(3);" 
			+ "CREATE INDEX tarmedKumulation_IDX1 on " + TABLENAME + " (" + FLD_LAW	+ ");"
			+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_MASTER_CODE + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION_110) + ")";

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
		+ "ValidTo				CHAR(10),"
		+ "Law					VARCHAR(3)"
		+ ");"
		+ "CREATE INDEX tarmedKumulation on " + TABLENAME + " (" + FLD_MASTER_CODE + ");"		
		+ "CREATE INDEX tarmedKumulation_IDX1 on " + TABLENAME + " (" + FLD_LAW + ");"		
		+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_MASTER_CODE + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_MASTER_CODE, FLD_MASTER_ART, FLD_SLAVE_CODE, FLD_SLAVE_ART,
			FLD_TYP, FLD_VIEW, FLD_VALID_SIDE, FLD_VALID_FROM, FLD_VALID_TO, FLD_LAW);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(createDB);
		} else {
			TarmedKumulation version = load(TarmedLeistung.ROW_VERSION);
			if (!version.exists()) {
				createOrModifyTable(upd110);
				version = load(TarmedLeistung.ROW_VERSION);
			}
			VersionInfo vi = new VersionInfo(version.get(FLD_MASTER_CODE));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder(VERSION_110)) {
					createOrModifyTable(upd110);
				}
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
		String typ, String view, String validSide, String from, String to, String law){
		create(null);
		
		String[] values = new String[] {
			masterCode, masterArt, slaveCode, slaveArt, typ, view, validSide, from, to, law
		};
		String[] fields =
			new String[] {
				FLD_MASTER_CODE, FLD_MASTER_ART, FLD_SLAVE_CODE, FLD_SLAVE_ART, FLD_TYP, FLD_VIEW,
				FLD_VALID_SIDE, FLD_VALID_FROM, FLD_VALID_TO, FLD_LAW
			};
		set(fields, values);
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
	 * Get {@link TarmedExclusion} objects for all exclusions defined as {@link TarmedKumulation},
	 * with code as master code and master type.
	 * 
	 * @param mastercode
	 * @param masterType
	 * @param date
	 * @param law
	 * @return
	 */
	public static List<TarmedExclusion> getExclusions(String mastercode,
		TarmedKumulationType masterType, TimeTool date, String law){
		Query<TarmedKumulation> query = new Query<TarmedKumulation>(TarmedKumulation.class);
		query.add(TarmedKumulation.FLD_MASTER_CODE, Query.EQUALS, mastercode);
		query.add(TarmedKumulation.FLD_MASTER_ART, Query.EQUALS, masterType.getArt());
		if (law != null && !law.isEmpty()) {
			query.add(TarmedKumulation.FLD_LAW, Query.EQUALS, law);
		}
		query.add(TarmedKumulation.FLD_TYP, Query.EQUALS, TarmedKumulation.TYP_EXCLUSION);
		
		List<TarmedKumulation> exclusions = query.execute();
		if (exclusions == null || exclusions.isEmpty()) {
			return Collections.emptyList();
		}
		exclusions = exclusions.stream().filter(k -> k.isValidKumulation(date)).collect(Collectors.toList());
		return exclusions.stream().map(k -> new TarmedExclusion(k)).collect(Collectors.toList());
	}
	
	/**
	 * Get {@link TarmedKumulation} objects with code and type as master or slave.
	 * 
	 * @param code
	 * @param type
	 * @param date
	 * @param law
	 * @param includeSlave
	 * @return
	 */
	public static List<TarmedKumulation> getKumulations(String code, TarmedKumulationType type,
		TimeTool date, String law){
		Query<TarmedKumulation> query = new Query<TarmedKumulation>(TarmedKumulation.class);
		query.startGroup();
		query.add(TarmedKumulation.FLD_MASTER_CODE, Query.EQUALS, code);
		query.add(TarmedKumulation.FLD_MASTER_ART, Query.EQUALS, type.getArt());
		query.endGroup();
		query.or();
		query.startGroup();
		query.add(TarmedKumulation.FLD_SLAVE_CODE, Query.EQUALS, code);
		query.add(TarmedKumulation.FLD_SLAVE_ART, Query.EQUALS, type.getArt());
		query.endGroup();
		if (law != null && !law.isEmpty()) {
			query.add(TarmedKumulation.FLD_LAW, Query.EQUALS, law);
		}
		
		List<TarmedKumulation> kumulations = query.execute();
		if (kumulations == null || kumulations.isEmpty()) {
			return Collections.emptyList();
		}
		return kumulations.stream().filter(k -> k.isValidKumulation(date))
			.collect(Collectors.toList());
	}
	
	/**
	 * Get {@link TarmedExclusion} objects for all exclusions defined as {@link TarmedKumulation},
	 * with code as master code and master type.
	 * 
	 * @param mastercode
	 * @param masterType
	 * @param date
	 * @param law
	 * @return
	 */
	public static List<TarmedExclusive> getExclusives(String mastercode,
		TarmedKumulationType masterType, TimeTool date, String law){
		Query<TarmedKumulation> query = new Query<TarmedKumulation>(TarmedKumulation.class);
		query.add(TarmedKumulation.FLD_MASTER_CODE, Query.EQUALS, mastercode);
		query.add(TarmedKumulation.FLD_MASTER_ART, Query.EQUALS, masterType.getArt());
		if (law != null && !law.isEmpty()) {
			query.add(TarmedKumulation.FLD_LAW, Query.EQUALS, law);
		}
		query.add(TarmedKumulation.FLD_TYP, Query.EQUALS, TarmedKumulation.TYP_EXCLUSIVE);
		
		List<TarmedKumulation> exclusives = query.execute();
		if (exclusives == null || exclusives.isEmpty()) {
			return Collections.emptyList();
		}
		exclusives =
			exclusives.stream().filter(k -> k.isValidKumulation(date)).collect(Collectors.toList());
		return exclusives.stream().map(k -> new TarmedExclusive(k)).collect(Collectors.toList());
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
	
	public boolean isSlaveType(TarmedKumulationType type){
		return TarmedKumulationType.ofArt(getSlaveArt()) == type;
	}
	
	public boolean isSlaveCode(String code){
		return getSlaveCode().equals(code);
	}
	
	public boolean isMasterType(TarmedKumulationType type){
		return TarmedKumulationType.ofArt(getMasterArt()) == type;
	}
	
	public boolean isMasterCode(String code){
		return getMasterCode().equals(code);
	}
	
	public boolean isTyp(String typ){
		return getTyp().equals(typ);
	}
	
	@Override
	public String toString(){
		return getMasterArt() + " " + getMasterCode() + " -> " + getSlaveArt() + " "
			+ getSlaveCode() + " [" + getTyp() + "]" + " (" + getValidFrom() + "-" + getValidTo()
			+ ")";
	}
}
