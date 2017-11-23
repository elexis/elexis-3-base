package ch.elexis.data;

import java.util.List;

import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

/**
 * Group the various informations from the CT_* tables of the tarmed database into one
 * {@link TarmedDefinitionen} in the elexis database. The origin table is set in the FLD_SPALTE. The
 * origin code is set in the FLD_KUERZEL, and the origin title is set in the FLD_TITEL. Currently
 * the valid from and to values are ignored.
 * 
 * The law field is set to support different tarmed databases.
 * 
 * @author thomas
 *
 */
public class TarmedDefinitionen extends PersistentObject {
	private static final String TABLENAME = "TARMED_DEFINITIONEN"; //$NON-NLS-1$
	private static final String VERSION = "1.0.0";
	
	public static final String FLD_SPALTE = "Spalte";
	public static final String FLD_KUERZEL = "Kuerzel";
	public static final String FLD_TITEL = "titel";
	public static final String FLD_LAW = "Law";
	
	// @formatter:off
	private static final String upd100_h2 =
		"ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
		+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LASTUPDATE + " BIGINT;"
		+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_DELETED + " CHAR(1) default '0';"
		+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LAW + " VARCHAR(3);" 
		+ "CREATE INDEX tarmedDefinitionen on " + TABLENAME + " (" + FLD_KUERZEL + ");"
		+ "CREATE INDEX tarmedDefinitionen_IDX1 on " + TABLENAME + " (" + FLD_LAW + ");"
		+ "UPDATE " + TABLENAME + " SET " + FLD_ID + "=(SELECT LPAD(random_uuid(), 25));"
		+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25) primary key;"
		+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_KUERZEL + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";

	private static final String upd100_mysql =
			"ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LASTUPDATE + " BIGINT;"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_DELETED + " CHAR(1) default '0';"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LAW + " VARCHAR(3);" 
			+ "CREATE INDEX tarmedDefinitionen on " + TABLENAME + " (" + FLD_KUERZEL + ");"
			+ "CREATE INDEX tarmedDefinitionen_IDX1 on " + TABLENAME + " (" + FLD_LAW + ");"
			+ "UPDATE " + TABLENAME + " SET " + FLD_ID + "=(SELECT LEFT(UUID(), 25));"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25) primary key;"
			+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_KUERZEL + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";

	private static final String upd100_postgresql =
			"ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LASTUPDATE + " BIGINT;"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_DELETED + " CHAR(1) default '0';"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LAW + " VARCHAR(3);" 
			+ "CREATE INDEX tarmedDefinitionen on " + TABLENAME + " (" + FLD_KUERZEL + ");"
			+ "CREATE INDEX tarmedDefinitionen_IDX1 on " + TABLENAME + " (" + FLD_LAW + ");"
			+ "UPDATE " + TABLENAME + " SET " + FLD_ID + "=(SELECT lpad(random()::text, 25));"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25) primary key;"
			+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_KUERZEL + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";

	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" 
		+"ID VARCHAR(25) primary key, "  
		+"lastupdate BIGINT," 
		+"deleted CHAR(1) default '0'," 
		
		+ "Spalte			VARCHAR(20),"
		+ "Kuerzel 			VARCHAR(5)," 
		+ "titel			VARCHAR(255),"
		+ "Law				VARCHAR(3)"
		+ ");"
		+ "CREATE INDEX tarmedDefinitionen on " + TABLENAME + " (" + FLD_KUERZEL + ");"		
		+ "CREATE INDEX tarmedDefinitionen_IDX1 on " + TABLENAME + " (" + FLD_LAW + ");"		
		+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_KUERZEL + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_SPALTE, FLD_KUERZEL, FLD_TITEL, FLD_LAW);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(createDB);
		} else {
			TarmedDefinitionen version = load(TarmedLeistung.ROW_VERSION);
			if (!version.exists()) {
				String dbFlavor = getDefaultConnection().getDBFlavor();
				if ("h2".equals(dbFlavor)) {
					createOrModifyTable(upd100_h2);
				} else if ("mysql".equals(dbFlavor)) {
					createOrModifyTable(upd100_mysql);
				} else if ("postgresql".equals(dbFlavor)) {
					createOrModifyTable(upd100_postgresql);
				}
			}
			VersionInfo vi = new VersionInfo(version.get(FLD_KUERZEL));
			if (vi.isOlder(VERSION)) {
				// add update code here ...
				version.set(FLD_KUERZEL, VERSION);
			}
		}
	}
	
	protected TarmedDefinitionen(){
		// leer
	}
	
	public static TarmedDefinitionen load(String id){
		return new TarmedDefinitionen(id);
	}
	
	protected TarmedDefinitionen(String id){
		super(id);
	}
	
	public TarmedDefinitionen(String spalte, String kuerzel, String titel, String law){
		create(null, new String[] {
			FLD_SPALTE, FLD_KUERZEL, FLD_TITEL, FLD_LAW
		}, new String[] {
			spalte, kuerzel, titel, law
		});
	}
	
	@Override
	public String getLabel(){
		return toString();
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static String getTitle(String spalte, String kuerzel){
		Query<TarmedDefinitionen> query = new Query<>(TarmedDefinitionen.class);
		query.add(FLD_SPALTE, Query.EQUALS, spalte);
		query.add(FLD_KUERZEL, Query.EQUALS, kuerzel);
		List<TarmedDefinitionen> result = query.execute();
		if(!result.isEmpty()) {
			return result.get(0).get(FLD_TITEL);
		}
		return "";
	}
	
	public static String getKuerzel(String spalte, String titel){
		Query<TarmedDefinitionen> query = new Query<>(TarmedDefinitionen.class);
		query.add(FLD_SPALTE, Query.EQUALS, spalte);
		query.add(FLD_TITEL, Query.EQUALS, titel);
		List<TarmedDefinitionen> result = query.execute();
		if (!result.isEmpty()) {
			return result.get(0).get(FLD_KUERZEL);
		}
		return "";
	}
	
}
