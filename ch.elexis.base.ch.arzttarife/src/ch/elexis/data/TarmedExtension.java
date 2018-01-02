package ch.elexis.data;

import java.util.List;

import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class TarmedExtension extends PersistentObject {
	
	private static final String TABLENAME = "TARMED_EXTENSION"; //$NON-NLS-1$
	private static final String VERSION = "1.0.0";
	
	public static final String FLD_CODE = "Code";
	public static final String FLD_LIMITS = "limits";
	public static final String FLD_MED_INTERPRET = "med_interpret";
	public static final String FLD_TECH_INTERPRET = "tech_interpret";
	
	// @formatter:off
	private static final String upd100_h2 =
			"ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LASTUPDATE + " BIGINT;"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_DELETED + " CHAR(1) default '0';"
			+ "UPDATE " + TABLENAME + " SET " + FLD_ID + "=(SELECT LPAD(random_uuid(), 25));"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD PRIMARY KEY (" + FLD_ID + ");"
			+ "ALTER TABLE " + TABLENAME + " MODIFY Code VARCHAR(32);"
			+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_CODE + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";

	private static final String upd100_mysql =
			"ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LASTUPDATE + " BIGINT;"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_DELETED + " CHAR(1) default '0';"
			+ "UPDATE " + TABLENAME + " SET " + FLD_ID + "=(SELECT LEFT(UUID(), 25));"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD PRIMARY KEY (" + FLD_ID + ");"
			+ "ALTER TABLE " + TABLENAME + " MODIFY Code VARCHAR(32);"
			+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_CODE + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";

	private static final String upd100_postgresql =
			"ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_LASTUPDATE + " BIGINT;"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_DELETED + " CHAR(1) default '0';"
			+ "UPDATE " + TABLENAME + " SET " + FLD_ID + "=(SELECT lpad(random()::text, 25));"
			+ "ALTER TABLE " + TABLENAME + " ADD " + FLD_ID + " VARCHAR(25);"
			+ "ALTER TABLE " + TABLENAME + " ADD PRIMARY KEY (" + FLD_ID + ");"
			+ "ALTER TABLE " + TABLENAME + " MODIFY Code VARCHAR(32);"
			+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_CODE + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";
	
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" 
		+"ID 					VARCHAR(25) primary key, "  
		+"lastupdate 			BIGINT," 
		+"deleted 				CHAR(1) default '0'," 
		
		+ "Code					VARCHAR(32),"
		+ "limits	 			BLOB," 
		+ "med_interpret		TEXT," 
		+ "tech_interpret 		TEXT" 
		+ ");"
		+ "CREATE INDEX tarmed4 on " + TABLENAME + " (" + FLD_CODE + ");"		
		+ "INSERT INTO " + TABLENAME + " (ID, " + FLD_CODE + ") VALUES (" + JdbcLink.wrap(TarmedLeistung.ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_CODE, FLD_LIMITS, FLD_MED_INTERPRET, FLD_TECH_INTERPRET);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(createDB);
		} else {
			TarmedExtension version = load(TarmedLeistung.ROW_VERSION);
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
			VersionInfo vi = new VersionInfo(version.get(FLD_CODE));
			if (vi.isOlder(VERSION)) {
				// add update code here ...
				version.set(FLD_CODE, VERSION);
			}
		}
	}
	
	protected TarmedExtension(){
		// leer
	}
	
	public TarmedExtension(TarmedLeistung tarmedLeistung){
		create(null, new String[] {
			FLD_CODE
		}, new String[] {
			tarmedLeistung.getId()
		});
	}
	
	public TarmedExtension(TarmedGroup tarmedGruppe){
		create(null, new String[] {
			FLD_CODE
		}, new String[] {
			tarmedGruppe.getId()
		});
	}
	
	public static TarmedExtension load(String id){
		return new TarmedExtension(id);
	}
	
	protected TarmedExtension(String id){
		super(id);
	}
	
	public static TarmedExtension getExtension(TarmedLeistung tarmedLeistung){
		Query<TarmedExtension> query = new Query<>(TarmedExtension.class);
		query.add(FLD_CODE, Query.EQUALS, tarmedLeistung.getId());
		List<TarmedExtension> result = query.execute();
		if (!result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	public static TarmedExtension getExtension(TarmedGroup tarmedGroup){
		Query<TarmedExtension> query = new Query<>(TarmedExtension.class);
		query.add(FLD_CODE, Query.EQUALS, tarmedGroup.getId());
		List<TarmedExtension> result = query.execute();
		if (!result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	@Override
	public String getLabel(){
		return toString();
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
}
