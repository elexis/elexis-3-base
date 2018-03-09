package com.hilotec.elexis.kgview.diagnoseliste;

import java.util.List;

import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class DiagnoselisteItem extends PersistentObject {
	public static final String VERSION = "2";
	public static final String PLUGIN_ID = "com.hilotec.elexis.kgview";
	
	private static final String TABLENAME = "COM_HILOTEC_ELEXIS_KGVIEW_DIAGNOSE";
	public static final String FLD_PATIENT = "Patient";
	public static final String FLD_TYP = "Typ";
	public static final String FLD_SOURCE = "Source";
	public static final String FLD_PARENT = "Parent";
	public static final String FLD_POSITION = "Position";
	public static final String FLD_DATUM = "Datum";
	public static final String FLD_ICPC = "ICPC";
	public static final String FLD_TEXT = "Text";
	
	public static final int TYP_DIAGNOSELISTE = 0;
	public static final int TYP_PERSANAMNESE = 1;
	public static final int TYP_SYSANAMNESE = 2;

	static {
		addMapping(TABLENAME,
				FLD_PATIENT,
				FLD_TYP,
				FLD_SOURCE,
				FLD_PARENT,
				FLD_POSITION,
				"Datum=S:D:Datum",
				FLD_ICPC,
				FLD_TEXT);
		checkTable();
	}
	
	private static final String create =
		"CREATE TABLE " + TABLENAME + " ("
			+ "  ID				VARCHAR(25) PRIMARY KEY, "
			+ "  lastupdate 	BIGINT, "
			+ "  deleted		CHAR(1) DEFAULT '0', "
			+ "  Patient		VARCHAR(25), "
			+ "  Typ            INT DEFAULT 0, "
			+ "  Source	 		VARCHAR(25), "
			+ "  Parent	 		VARCHAR(25), "
			+ "  Position		INT DEFAULT 0, "
			+ "  Datum          CHAR(8) DEFAULT '00000000', "
			+ "  ICPC           TEXT, "
			+ "  Text			TEXT "
			+ ");"
			+ "INSERT INTO " + TABLENAME + " (ID, Position) VALUES "
			+ "	('VERSION', '" + VERSION + "');";
	
	private static final String up_1to2 =
		"ALTER TABLE " + TABLENAME
			+ "  ADD Typ    INT DEFAULT 0               AFTER Patient,"
			+ "  ADD Source VARCHAR(25)                 AFTER Patient,"
			+ "  ADD Datum  CHAR(8)  DEFAULT '00000000' AFTER Position,"
			+ "  ADD ICPC   TEXT                        AFTER Position;"
			+ "UPDATE " + TABLENAME + " SET Position = '2' WHERE"
			+ "  ID LIKE 'VERSION';";

	private static void checkTable() {	
		String fm = null;
		try { fm = getConnection().queryString(
				"SELECT Position FROM " + TABLENAME + " WHERE ID='VERSION';");
			if (fm.equals("1"))
				createOrModifyTable(up_1to2);
		} catch (Exception e) {}
		if (fm == null) {
			createOrModifyTable(create);
		}
	}

	protected DiagnoselisteItem(Patient pat, int typ) {
		create(null);
		setPatient(pat);
		setPosition(0);
		setTyp(typ);
		setDatum(new TimeTool().toString(TimeTool.DATE_GER));
	}
	
	protected DiagnoselisteItem(DiagnoselisteItem parent, int pos) {
		create(null);
		setPatient(parent.getPatient());
		setParent(parent);
		setPosition(pos);
		setTyp(parent.getTyp());
		setDatum(new TimeTool().toString(TimeTool.DATE_GER));
	}
	
	protected DiagnoselisteItem() {}
	
	protected DiagnoselisteItem(String id) {
		super(id);
	}
	
	public static DiagnoselisteItem load(String id) {
		DiagnoselisteItem di = new DiagnoselisteItem(id);
		if (!di.exists()) return null;
		return di;
	}
	
	public Patient getPatient() {
		return Patient.load(get(FLD_PATIENT));
	}
	
	public void setPatient(Patient pat) {
		if (pat == null)
			set(FLD_PATIENT, null);
		else
			set(FLD_PATIENT, pat.getId());
	}

	public int getTyp() {
		return getInt(FLD_TYP);
	}

	protected void setTyp(int typ) {
		setInt(FLD_TYP, typ);
	}

	public String getSource() {
		return get(FLD_SOURCE);
	}

	public void setSource(String src) {
		set(FLD_SOURCE, src);
	}

	public DiagnoselisteItem getParent() {
		String id = get(FLD_PARENT);
		if (id == null || id.isEmpty()) return null;
		return load(id);
	}
	
	public void setParent(DiagnoselisteItem parent) {
		set(FLD_PARENT, parent.getId());
	}
	
	public int getPosition() {
		return Integer.parseInt(get(FLD_POSITION));
	}
	
	public void setPosition(int pos) {
		set(FLD_POSITION, Integer.toString(pos));
	}

	public String getDatum() {
		return get(FLD_DATUM);
	}

	public void setDatum(String datum) {
		set(FLD_DATUM, datum);
	}

	public String getText() {
		return StringTool.unNull(get(FLD_TEXT));
	}
	
	public void setText(String text) {
		set(FLD_TEXT, text);
	}

	public String getICPC() {
		return StringTool.unNull(get(FLD_ICPC));
	}

	public void setICPC(String icpc) {
		set(FLD_ICPC, icpc);
	}

	public int nextChildPos() {
		int next = 0;
		for (DiagnoselisteItem di: getChildren()) {
			next = Math.max(next, di.getPosition() + 1);
		}
		return next;
	}
	
	public List<DiagnoselisteItem> getChildren() {
		Query<DiagnoselisteItem> q =
			new Query<DiagnoselisteItem>(DiagnoselisteItem.class);
		q.add(FLD_PATIENT, Query.EQUALS, get(FLD_PATIENT));
		q.and();
		q.add(FLD_PARENT, Query.EQUALS, getId());
		q.orderBy(false, FLD_POSITION);
		return q.execute();
	}
	
	public DiagnoselisteItem getBySrc(DiagnoselisteItem src) {
		Query<DiagnoselisteItem> q =
			new Query<DiagnoselisteItem>(DiagnoselisteItem.class);
		q.add(FLD_PATIENT, Query.EQUALS, get(FLD_PATIENT));
		q.and();
		q.add(FLD_SOURCE, Query.EQUALS, src.getId());
		List<DiagnoselisteItem> l = q.execute();
		if (l.isEmpty()) return null;
		return l.get(0);
	}

	public DiagnoselisteItem createChild() {
		return new DiagnoselisteItem(this, nextChildPos());
	}

	public DiagnoselisteItem createChildFrom(DiagnoselisteItem src) {
		DiagnoselisteItem i = createChild();
		i.setText(src.getText());
		i.setDatum(src.getDatum());
		i.setSource(src.getId());
		return i;
	}

	public static DiagnoselisteItem getRoot(Patient pat, int typ) {
		Query<DiagnoselisteItem> q =
			new Query<DiagnoselisteItem>(DiagnoselisteItem.class);
		q.add(FLD_PATIENT, Query.EQUALS, pat.getId());
		q.and();
		q.add(FLD_TYP, Query.EQUALS, Integer.toString(typ));
		q.add(FLD_PARENT, Query.EQUALS, null);
		List<DiagnoselisteItem> dis = q.execute();
		
		// Wenn noch kein root-Element existiert, eins anlegen
		if (dis.isEmpty()) return new DiagnoselisteItem(pat, typ);
		
		return dis.get(0);
	}
	
	/* FIXME: Generalisieren in ein remove() und insert() oder so */
	public void moveUp() {
		DiagnoselisteItem parent = getParent();
		if (parent == null) return;

		int pos = getPosition();
		if (pos == 0) return;
		pos -= 1;

		for (DiagnoselisteItem di: parent.getChildren()) {
			if (di.getPosition() == pos) {
				di.setPosition(pos + 1);
				setPosition(pos);
				break;
			}
		}
	}

	/**
	 * Prueft ob dieses Item ein (direktes or indirektes) Kindelement von p ist.
	 */
	public boolean isDescendantOf(DiagnoselisteItem p) {
		if (equals(p)) return true;
		DiagnoselisteItem par = getParent();
		if (par == null) return false;
		return par.isDescendantOf(p);
	}

	public void moveDown() {
		DiagnoselisteItem parent = getParent();
		if (parent == null) return;

		int pos = getPosition();
		pos += 1;

		for (DiagnoselisteItem di: parent.getChildren()) {
			if (di.getPosition() == pos) {
				di.setPosition(pos - 1);
				setPosition(pos);
				break;
			}
		}
	}
	
	/**
	 * Schiebt das Element c ganz nach unten, so dass es einfach geloescht
	 * werden kann.
	 * @param c Kindelement das zum loeschen vorbereitet werden soll
	 */
	public void removeChild(DiagnoselisteItem c) {
		int pos = c.getPosition();
		c.setPosition(nextChildPos());
		for (DiagnoselisteItem di: getChildren()) {
			int p = di.getPosition();
			if (p > pos) di.setPosition(p - 1);
		}
	}

	public boolean delete() {
		DiagnoselisteItem parent = getParent();
		if (parent == null) return false;
		if (super.delete()) parent.removeChild(this);
		return true;
	}

	public void deleteChildren() {
		for (DiagnoselisteItem i: getChildren()) {
			i.deleteChildren();
			i.delete();
		}
	}

	@Override
	public String getLabel() {
		return getText();
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	/**
	 * Drag ist erlaubt
	 */
	@Override
	public boolean isDragOK() {
		return true;
	}
}
