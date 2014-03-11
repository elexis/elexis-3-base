package com.hilotec.elexis.kgview.data;

import java.util.Arrays;
import java.util.HashSet;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.data.Anwender;
import ch.elexis.data.Konsultation;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class KonsData extends PersistentObject {
	public static final String VERSION = "9";
	public static final String PLUGIN_ID = "com.hilotec.elexis.kgview";
	
	private static final String TABLENAME = "COM_HILOTEC_ELEXIS_KGVIEW_KONSDATA";
	public static final String FLD_JETZLEIDEN = "JetzigesLeiden";
	public static final String FLD_JETZLEIDEN_ICPC = "JetzigesLeidenICPC";
	public static final String FLD_ALLGSTATUS = "AllgStatus";
	public static final String FLD_LOKSTATUS = "LokStatus";
	public static final String FLD_PROZEDERE = "Prozedere";
	public static final String FLD_PROZEDERE_ICPC = "ProzedereICPC";
	public static final String FLD_DIAGNOSE = "Diagnose";
	public static final String FLD_DIAGNOSE_ICPC = "DiagnoseICPC";
	public static final String FLD_THERAPIE = "Therapie";
	public static final String FLD_VERLAUF = "Verlauf";
	public static final String FLD_ROENTGEN = "Roentgen";
	public static final String FLD_EKG = "EKG";
	public static final String FLD_KONSZEIT = "KonsZeit";
	public static final String FLD_KONSBEGINN = "KonsBeginn";
	public static final String FLD_KONSTYP = "KonsTyp";
	public static final String FLD_AUTOR = "Autor";
	
	public static final int KONSTYP_NORMAL = 0;
	public static final int KONSTYP_TELEFON = 1;
	public static final int KONSTYP_HAUSBESUCH = 2;
	
	// Felder an denen Aenderungen nur durch den Autor durchgefuehrt werden
	// duerfen.
	private static final String[] KGFIELDS = {
		FLD_JETZLEIDEN, FLD_JETZLEIDEN_ICPC, FLD_ALLGSTATUS, FLD_LOKSTATUS, FLD_PROZEDERE,
		FLD_PROZEDERE_ICPC, FLD_DIAGNOSE, FLD_DIAGNOSE_ICPC, FLD_THERAPIE, FLD_VERLAUF,
		FLD_ROENTGEN, FLD_EKG,
	};
	private static final HashSet<String> KGFIELD_SET = new HashSet<String>(Arrays.asList(KGFIELDS));
	
	static {
		addMapping(TABLENAME, "JetzigesLeiden=JetzLeiden", "JetzigesLeidenICPC=JetzLeidenICPC",
			"AllgStatus", "LokStatus", "Prozedere", "ProzedereICPC", "Diagnose", "DiagnoseICPC",
			"Therapie", "Verlauf", "Roentgen", "EKG", "KonsZeit", "KonsBeginn", "KonsTyp", "Autor");
		checkTable();
	}
	
	private static final String create = "CREATE TABLE " + TABLENAME + " ("
		+ "  ID				VARCHAR(25) PRIMARY KEY, " + "  lastupdate 	BIGINT, "
		+ "  deleted		CHAR(1) DEFAULT '0', " + "  JetzLeiden		TEXT, " + "  JetzLeidenICPC	TEXT, "
		+ "  AllgStatus		TEXT, " + "  LokStatus		TEXT, " + "  Prozedere		TEXT, "
		+ "  ProzedereICPC	TEXT, " + "  Diagnose		TEXT, " + "  DiagnoseICPC	TEXT, "
		+ "  Therapie		TEXT, " + "  Verlauf		TEXT, " + "  Roentgen		TEXT, " + "  EKG			TEXT, "
		+ "  KonsZeit 		BIGINT DEFAULT 0, " + "  KonsBeginn     BIGINT,  "
		+ "  KonsTyp		SMALLINT DEFAULT 0, " + "  Autor          VARCHAR(25)  " + ");"
		+ "INSERT INTO " + TABLENAME + " (ID, JetzLeiden) VALUES " + "	('VERSION', '" + VERSION
		+ "');";
	
	private static final String up_1to2 = "ALTER TABLE " + TABLENAME
		+ "  ADD JetzLeidenICPC	TEXT AFTER JetzLeiden,"
		+ "  ADD ProzedereICPC 	TEXT AFTER Prozedere," + "  ADD DiagnoseICPC 	TEXT AFTER Diagnose;"
		+ "UPDATE " + TABLENAME + " SET JetzLeiden = '2' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_2to3 = "ALTER TABLE " + TABLENAME
		+ "  ADD Therapie	TEXT AFTER DiagnoseICPC," + "  ADD Verlauf 	TEXT AFTER Therapie;"
		+ "UPDATE " + TABLENAME + " SET JetzLeiden = '3' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_3to4 = "ALTER TABLE " + TABLENAME
		+ "  ADD Roentgen	TEXT AFTER Verlauf," + "  ADD EKG		TEXT AFTER Roentgen;" + "UPDATE "
		+ TABLENAME + " SET JetzLeiden = '4' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_4to5 = "ALTER TABLE " + TABLENAME
		+ "  ADD KonsBeginn	BIGINT AFTER KonsZeit;" + "UPDATE " + TABLENAME
		+ " SET JetzLeiden = '5' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_5to6 = "ALTER TABLE " + TABLENAME
		+ "  CHANGE KonsZeit KonsZeit BIGINT DEFAULT 0;" + "UPDATE " + TABLENAME
		+ " SET KonsZeit = 0 WHERE" + "  KonsZeit IS NULL;" + "UPDATE " + TABLENAME
		+ " SET JetzLeiden = '6' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_6to7 = "ALTER TABLE " + TABLENAME
		+ "  ADD IstTelefon		CHAR(1) DEFAULT '0' AFTER KonsBeginn;" + "UPDATE " + TABLENAME
		+ " SET JetzLeiden = '7' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_7to8 = "ALTER TABLE " + TABLENAME
		+ "  ADD Autor		VARCHAR(25) AFTER IstTelefon;" + "UPDATE " + TABLENAME
		+ " SET JetzLeiden = '8' WHERE" + "  ID LIKE 'VERSION';";
	
	private static final String up_8to9 = "ALTER TABLE " + TABLENAME
		+ "  ADD KonsTyp	SMALLINT DEFAULT 0 AFTER IstTelefon;" + "UPDATE " + TABLENAME
		+ " SET KonsTyp = 1 WHERE" + "  IstTelefon = 1;" + "ALTER TABLE " + TABLENAME
		+ "  DROP IstTelefon; " + "UPDATE " + TABLENAME + " SET JetzLeiden = '9' WHERE"
		+ "  ID LIKE 'VERSION';";
	
	private static void checkTable(){
		KonsData check = load("VERSION");
		if (!check.exists()) {
			createOrModifyTable(create);
		} else {
			if (check.getJetzigesLeiden().equals("1"))
				createOrModifyTable(up_1to2);
			if (check.getJetzigesLeiden().equals("2"))
				createOrModifyTable(up_2to3);
			if (check.getJetzigesLeiden().equals("3"))
				createOrModifyTable(up_3to4);
			if (check.getJetzigesLeiden().equals("4"))
				createOrModifyTable(up_4to5);
			if (check.getJetzigesLeiden().equals("5"))
				createOrModifyTable(up_5to6);
			if (check.getJetzigesLeiden().equals("6"))
				createOrModifyTable(up_6to7);
			if (check.getJetzigesLeiden().equals("7"))
				createOrModifyTable(up_7to8);
			if (check.getJetzigesLeiden().equals("8"))
				createOrModifyTable(up_8to9);
		}
	}
	
	public static KonsData load(final String id){
		return new KonsData(id);
	}
	
	public static KonsData load(Konsultation kons){
		return load(kons.getId());
	}
	
	protected KonsData(){}
	
	protected KonsData(final String id){
		super(id);
	}
	
	public KonsData(Konsultation kons){
		super(kons.getId());
		if (!exists()) {
			create(kons.getId());
			set(FLD_KONSBEGINN, Long.toString(System.currentTimeMillis()));
		}
	}
	
	@Override
	public String getLabel(){
		return getKonsultation().getLabel();
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public String getJetzigesLeiden(){
		return get(FLD_JETZLEIDEN);
	}
	
	public String getJetzigesLeidenICPC(){
		return get(FLD_JETZLEIDEN_ICPC);
	}
	
	public String getAllgemeinstatus(){
		return get(FLD_ALLGSTATUS);
	}
	
	public String getLokalstatus(){
		return get(FLD_LOKSTATUS);
	}
	
	public String getProzedere(){
		return get(FLD_PROZEDERE);
	}
	
	public String getProzedereICPC(){
		return get(FLD_PROZEDERE_ICPC);
	}
	
	public String getDiagnose(){
		return get(FLD_DIAGNOSE);
	}
	
	public String getDiagnoseICPC(){
		return get(FLD_DIAGNOSE_ICPC);
	}
	
	public String getTherapie(){
		return get(FLD_THERAPIE);
	}
	
	public String getVerlauf(){
		return get(FLD_VERLAUF);
	}
	
	public String getRoentgen(){
		return get(FLD_ROENTGEN);
	}
	
	public String getEKG(){
		return get(FLD_EKG);
	}
	
	public long getKonsZeit(){
		return Long.parseLong(get(FLD_KONSZEIT));
	}
	
	public String getKonsBeginn(){
		String ts = get(FLD_KONSBEGINN);
		if (ts == null || ts.equals(""))
			return "";
		
		TimeTool t = new TimeTool();
		t.setTimeInMillis(Long.parseLong(ts));
		return t.toString(TimeTool.TIME_SMALL);
	}
	
	public Konsultation getKonsultation(){
		return Konsultation.load(getId());
	}
	
	public int getKonsTyp(){
		String tel = get(FLD_KONSTYP);
		tel = (StringTool.isNothing(tel) ? "0" : tel);
		return Integer.parseInt(tel);
	}
	
	public Anwender getAutor(){
		String id = get(FLD_AUTOR);
		if (!StringTool.isNothing(id)) {
			Anwender aw = Anwender.load(id);
			return aw;
		}
		return null;
	}
	
	public void setJetzigesLeiden(String txt){
		set(FLD_JETZLEIDEN, txt);
	}
	
	public void setAllgemeinstatus(String txt){
		set(FLD_ALLGSTATUS, txt);
	}
	
	public void setLokalstatus(String txt){
		set(FLD_LOKSTATUS, txt);
	}
	
	public void setProzedere(String txt){
		set(FLD_PROZEDERE, txt);
	}
	
	public void setDiagnose(String txt){
		set(FLD_DIAGNOSE, txt);
	}
	
	public void setTherapie(String txt){
		set(FLD_THERAPIE, txt);
	}
	
	public void setVerlauf(String txt){
		set(FLD_VERLAUF, txt);
	}
	
	public void setRoentgen(String txt){
		set(FLD_ROENTGEN, txt);
	}
	
	public void setEKG(String txt){
		set(FLD_EKG, txt);
	}
	
	public void setKonsBeginn(long zeit){
		set(FLD_KONSBEGINN, Long.toString(zeit));
	}
	
	public void setKonsZeit(long zeit){
		set(FLD_KONSZEIT, Long.toString(zeit));
	}
	
	public void setKonsTyp(int typ){
		set(FLD_KONSTYP, Integer.toString(typ));
	}
	
	public void setAutor(Anwender anw){
		String id = "";
		if (anw != null)
			id = anw.getId();
		set(FLD_AUTOR, id);
	}
	
	/**
	 * Wir ueberschreiben hier set() um sicherzustellen dass nur der Autor einen KG-Eintrag anpassen
	 * kann, und um den Autor festzuhalten falls das noch nicht geschehen ist.
	 */
	@Override
	public boolean set(final String field, String value){
		if (KGFIELD_SET.contains(field)) {
			Anwender au = getAutor();
			if (au == null) {
				// Noch kein Autor gesetzt, auf dieser Kons, setze auf
				// aktuellen User
				setAutor(CoreHub.actUser);
			} else if (!au.equals(CoreHub.actUser)) {
				// Ungueltiger User
				throw new RuntimeException("Nur Autor kann " + "Krankengeschichte veraendern!");
			} else if (StringTool.isNothing(value)) {
				// Wenn das Feld leer ist, und alle anderen KG-Felder leer sind,
				// wird der Autor zurueckgesesetzt.
				boolean nonempty = false;
				for (int i = 0; i < KGFIELDS.length && !nonempty; i++) {
					nonempty |= !StringTool.isNothing(get(KGFIELDS[i]));
				}
				if (!nonempty)
					set(FLD_AUTOR, "");
			}
		}
		return super.set(field, value);
	}
	
	/**
	 * Prueft ob die KG dieser Konsultation vom aktuellen Benutzer bearbeitet werden darf
	 */
	public boolean isEditOK(){
		Anwender au = getAutor();
		return au == null || au.equals(CoreHub.actUser);
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
}
