package com.hilotec.elexis.kgview.data;

import java.util.List;

import ch.elexis.data.Artikel;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

public class FavMedikament extends PersistentObject {
	public static final String VERSION = "2";
	public static final String PLUGIN_ID = "com.hilotec.elexis.kgview";
	
	private static final String TABLENAME = "COM_HILOTEC_ELEXIS_KGVIEW_FAVMEDIKAMENT";
	
	public static final String FLD_BEZEICHNUNG	= "Bezeichnung";
	public static final String FLD_ZWECK		= "Zweck";
	public static final String FLD_EINHEIT		= "Einheit";
	public static final String FLD_ORDNUNGSZAHL	= "Ordnungszahl";
	
	static {
		addMapping(TABLENAME,
			"Bezeichnung",
			"Zweck",
			"Einheit",
			"Ordnungszahl");
		checkTable();
	}

	private static final String create =
		"CREATE TABLE " + TABLENAME + " ("
			+ "  ID				VARCHAR(25) PRIMARY KEY, "
			+ "  lastupdate 	BIGINT, "
			+ "  deleted		CHAR(1) DEFAULT '0', "
			+ "  Artikel	 	VARCHAR(25), "
			+ "  Ordnungszahl	INT DEFAULT 0,  "
			+ "  Bezeichnung	TEXT, "
			+ "  Zweck			TEXT, "
			+ "  Einheit		TEXT  "
			+ ");"
			+ "INSERT INTO " + TABLENAME + " (ID, Bezeichnung) VALUES "
			+ "	('VERSION', '" + VERSION + "');";
	
	private static final String up_1to2 =
		"ALTER TABLE " + TABLENAME
			+ "  ADD Ordnungszahl 	INT DEFAULT 0 AFTER Artikel;"
			+ "UPDATE " + TABLENAME + " SET Bezeichnung = '2' WHERE"
			+ "  ID LIKE 'VERSION';";
	
	private static void checkTable() {
		
		String fm = null;
		try { fm = getConnection().queryString(
				"SELECT Bezeichnung FROM " + TABLENAME + " WHERE ID='VERSION';");
		} catch (Exception e) {}
		
		if (fm == null) {
			createOrModifyTable(create);
		} else {
			if (fm.equals("1"))
				createOrModifyTable(up_1to2);
		}
	}

	public FavMedikament(Artikel a, int o, String b, String z, String e) {
		super(a.getId());
		if (!exists()) {
			create(a.getId());
		}
		
		setOrdnungszahl(o);
		setBezeichnung(b);
		setZweck(z);
		setEinheit(e);
	}
	
	public static FavMedikament load(final String id) {
		// FIXME: Dirty hack um zu pruefen ob ein Medikament schon
		//        existiert, damit nicht versehentlich leere angelegt
		//        werden.
		String fm =	getConnection().queryString(
				"SELECT Bezeichnung FROM " + TABLENAME +
				" WHERE ID='"+id+"';");
		
		if (fm == null) return null;
		return new FavMedikament(id);
	}
	
	public static FavMedikament load(Artikel art) {
		return load(art.getId());
	}
	
	public static List<FavMedikament> getAll() {
		Query<FavMedikament> q =
			new Query<FavMedikament>(FavMedikament.class);
		List<FavMedikament> fm = q.execute();
		for (FavMedikament f: fm) {
			if (f.getId().equals("VERSION")) {
				fm.remove(f);
				break;
			}
		}
		return fm;
	}
	
	protected FavMedikament() {}
	
	protected FavMedikament(final String id) {
		super(id);
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	@Override
	public String getLabel() {
		return getBezeichnung();
	}
	
	public Artikel getArtikel() {
		return Artikel.load(getId());
	}

	public int getOrdnungszahl() {
		return getInt(FLD_ORDNUNGSZAHL);
	}

	public String getBezeichnung() {
		return get(FLD_BEZEICHNUNG);
	}
	
	public String getZweck() {
		return get(FLD_ZWECK);
	}
	
	public String getEinheit() {
		return get(FLD_EINHEIT);
	}
	
	
	public void setOrdnungszahl(int ord) {
		setInt(FLD_ORDNUNGSZAHL, ord);
	}
	
	public void setBezeichnung(String txt) {
		set(FLD_BEZEICHNUNG, txt);
	}

	public void setZweck(String txt) {
		set(FLD_ZWECK, txt);
	}
	
	public void setEinheit(String txt) {
		set(FLD_EINHEIT, txt);
	}
	
	/**
	 * Erstellt neues FM mit selben Eigenschaften aber neuem verknuepftem
	 * Artikel, das original FM wird geloescht.
	 */
	public FavMedikament relinkTo(Artikel a) {
		FavMedikament fm = new FavMedikament(a, getOrdnungszahl(),
				getBezeichnung(), getZweck(), getEinheit());
		delete();
		return fm;
	}
}
