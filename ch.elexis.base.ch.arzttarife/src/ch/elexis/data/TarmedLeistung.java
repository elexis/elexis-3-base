/*******************************************************************************
 * Copyright (c) 2005-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/

package ch.elexis.data;

import java.io.File;
import java.io.FileInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.action.Action;

import ch.elexis.core.data.interfaces.IFall;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.data.UiVerrechenbarAdapter;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.views.TarmedDetailDialog;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.IFilter;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * Implementation des Tarmed-Systems. Besteht aus den eigentlichen Leistungen, statischen Methoden
 * zum auslesen der Textformen der einzelnen Codes, einem Validator und einem Mandantenfilter.
 * 
 * @author gerry
 * 
 */
public class TarmedLeistung extends UiVerrechenbarAdapter {
	public static final String FLD_CODE = "code";
	public static final String FLD_GUELTIG_BIS = "GueltigBis";
	public static final String FLD_GUELTIG_VON = "GueltigVon";
	public static final String FLD_TP_TL = "TP_TL";
	public static final String FLD_TP_AL = "TP_AL";
	public static final String FLD_SPARTE = "Sparte";
	public static final String FLD_DIGNI_QUANTI = "DigniQuanti";
	public static final String FLD_DIGNI_QUALI = "DigniQuali";
	public static final String FLD_TEXT = "Text";
	public static final String FLD_NICK = "Nick";
	public static final String FLD_PARENT = "Parent";
	
	public static final String XIDDOMAIN = "www.xid.ch/id/tarmedsuisse";
	
	public static final String SIDE = "Seite";
	public static final String SIDE_L = "l";
	public static final String SIDE_R = "r";
	public static final String LEFT = "left";
	public static final String RIGHT = "right";
	
	public static final String PFLICHTLEISTUNG = "obligation";

	public static final TarmedComparator tarmedComparator;
	public static final TarmedOptifier tarmedOptifier;
	public static final TimeTool INFINITE = new TimeTool("19991231");
	public static TimeTool curTimeHelper = new TimeTool();
	
	private static final String VERSION_110 = "1.1.0";
	private static final String VERSION_111 = "1.1.1";
	private static final String VERSION_120 = "1.2.0";
	private static final String upd110 = "ALTER TABLE TARMED ADD lastupdate BIGINT";
	private static final String upd120 = "ALTER TABLE TARMED ADD code VARCHAR(25);"
		+ " ALTER TABLE TARMED MODIFY ID VARCHAR(25);"
		+ " ALTER TABLE TARMED_EXTENSION MODIFY CODE VARCHAR(25);";
	
	private static final String ROW_VERSION = "Version";
	private static final JdbcLink j = getConnection();
	
	Hashtable<String, String> ext = new Hashtable<String, String>();
	
	static {
		createTables();
		tarmedComparator = new TarmedComparator();
		tarmedOptifier = new TarmedOptifier();
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Tarmed", Xid.ASSIGNMENT_LOCAL);
	}
	
	static void createTables(){
		TarmedLeistung version = load(ROW_VERSION);
		addMapping(
			"TARMED", "Ziffer=" + FLD_CODE, FLD_CODE, FLD_PARENT, FLD_DIGNI_QUALI, FLD_DIGNI_QUANTI, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FLD_SPARTE, FLD_TEXT + "=tx255", "Name=tx255", FLD_NICK + "=Nickname", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"GueltigVon=S:D:GueltigVon", "GueltigBis=S:D:GueltigBis", //$NON-NLS-1$ //$NON-NLS-2$
			"deleted" //$NON-NLS-1$ 
		);
		if (!version.exists()) {
			String filepath =
				PlatformHelper.getBasePath("ch.elexis.base.ch.arzttarife") + File.separator
					+ "createDB.script";
			Stm stm = j.getStatement();
			try {
				FileInputStream fis = new FileInputStream(filepath);
				stm.execScript(fis, true, true);
			} catch (Exception e) {
				ExHandler.handle(e);
				SWTHelper.showError("Kann Tarmed-Datenbank nicht erstellen",
					"create-Script nicht gefunden in " + filepath);
			} finally {
				j.releaseStatement(stm);
			}
			
		}
		TarmedLeistung tlv = TarmedLeistung.load(ROW_VERSION);
		if (!tlv.exists()) {
			tlv = new TarmedLeistung();
			tlv.create(ROW_VERSION);
		}
		VersionInfo vi = new VersionInfo(tlv.get(FLD_NICK));
		if (!tlv.exists() || vi.isOlder(VERSION_110)) {
			createOrModifyTable(upd110);
			tlv.set(FLD_NICK, VERSION_110);
		}
		if (vi.isOlder(VERSION_111)) {
			createOrModifyTable("Update TARMED set gueltigbis='20993112' where id='39.0305'");
			tlv.set(FLD_NICK, VERSION_111);
		}
		if (vi.isOlder(VERSION_120)) {
			createOrModifyTable(upd120);
			tlv.set(FLD_NICK, VERSION_120);
		}
	}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	/** Text zu einem Code der qualitativen Dignität holen */
	public static String getTextForDigniQuali(final String dql){
		if (dql == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='DIGNI_QUALI' AND KUERZEL=" + JdbcLink.wrap(dql))); //$NON-NLS-1$
	}
	
	/** Kurz-Code für eine qualitative Dignität holen */
	public static String getCodeForDigniQuali(final String kurz){
		if (kurz == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT KUERZEL FROM TARMED_DEFINITIONEN WHERE SPALTE='DIGNI_QUALI' AND TITEL=" + JdbcLink.wrap(kurz))); //$NON-NLS-1$
	}
	
	/** Text für einen Code für quantitative Dignität holen */
	public static String getTextForDigniQuanti(final String dqn){
		if (dqn == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='DIGNI_QUANTI' AND KUERZEL=" + JdbcLink.wrap(dqn))); //$NON-NLS-1$
	}
	
	/** Text für einen Sparten-Code holen */
	public static String getTextForSparte(final String sparte){
		if (sparte == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='SPARTE' AND KUERZEL=" + JdbcLink.wrap(sparte))); //$NON-NLS-1$
	}
	
	/** Text für eine Anästhesie-Risikoklasse holen */
	public static String getTextForRisikoKlasse(final String klasse){
		if (klasse == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='ANAESTHESIE' AND KUERZEL=" + JdbcLink.wrap(klasse))); //$NON-NLS-1$
	}
	
	/** Text für einen ZR_EINHEIT-Code holen (Sitzung, Monat usw.) */
	public static String getTextForZR_Einheit(final String einheit){
		if (einheit == null) {
			return ""; //$NON-NLS-1$
		}
		return checkNull(j
			.queryString("SELECT titel FROM TARMED_DEFINITIONEN WHERE SPALTE='ZR_EINHEIT' AND KUERZEL=" + JdbcLink.wrap(einheit))); //$NON-NLS-1$
	}
	
	/** Alle Codes für Quantitative Dignität holen */
	public static String[] getDigniQuantiCodes(){
		return null;
	}
	
	/** Konstruktor wird nur vom Importer gebraucht */
	public TarmedLeistung(final String code, final String parent, final String DigniQuali,
		final String DigniQuanti, final String sparte){
		create(code);
		j.exec("INSERT INTO TARMED_EXTENSION (CODE) VALUES (" + getWrappedId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		set(new String[] {
			FLD_PARENT, FLD_DIGNI_QUALI, FLD_DIGNI_QUANTI, FLD_SPARTE
		}, parent, DigniQuali, DigniQuanti, sparte); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	/** Konstruktor wird nur vom Importer gebraucht */
	public TarmedLeistung(final String id, final String code, final String parent,
		final String DigniQuali, final String DigniQuanti, final String sparte){
		create(id);
		j.exec("INSERT INTO TARMED_EXTENSION (CODE) VALUES (" + getWrappedId() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
		set(new String[] {
			FLD_CODE, FLD_PARENT, FLD_DIGNI_QUALI, FLD_DIGNI_QUANTI, FLD_SPARTE
		}, code, parent, DigniQuali, DigniQuanti, sparte); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_CODE, FLD_TEXT);
		if (vals[0].isEmpty()) {
			vals[0] = getId();
		}
		return vals[0] + " " + vals[1];
	}
	
	@Override
	protected String getTableName(){
		return "TARMED"; //$NON-NLS-1$
	}
	
	/** Code liefern */
	@Override
	public String getCode(){
		String code = get(FLD_CODE);
		if (code != null && !code.isEmpty())
			return code;
		else
			return getId();
	}
	
	/** Text liefern */
	@Override
	public String getText(){
		return get(FLD_TEXT); //$NON-NLS-1$
	}
	
	/** Text setzen (wird nur vom Importer gebraucht */
	public void setText(final String tx){
		set(FLD_TEXT, tx); //$NON-NLS-1$
	}
	
	/** Erweiterte Informationen laden */
	@SuppressWarnings("unchecked")
	public Hashtable<String, String> loadExtension(){
		Stm stm = j.getStatement();
		ResultSet res =
			stm.query("SELECT limits FROM TARMED_EXTENSION WHERE CODE=" + getWrappedId()); //$NON-NLS-1$
		try {
			if (res.next()) {
				byte[] in = res.getBytes(1);
				if ((in == null) || (in.length == 0)) {
					ext.clear();
				} else {
					ext = StringTool.fold(in, StringTool.GUESS, null);
				}
			}
		} catch (Exception ex) {
			log.error("Error loading tarmed extension for id [{}]", getId(), ex);
			ext.clear();
		} finally {
			j.releaseStatement(stm);
			
		}
		return ext;
	}
	
	/** Erweiterte Informationen rückspeichern */
	public void flushExtension(){
		if (ext != null) {
			byte[] flat = StringTool.flatten(ext, StringTool.ZIP, null);
			PreparedStatement preps =
				j.prepareStatement("UPDATE TARMED_EXTENSION SET limits=? WHERE CODE=" + getWrappedId()); //$NON-NLS-1$
			try {
				preps.setBytes(1, flat);
				preps.execute();
			} catch (Exception ex) {
				ExHandler.handle(ex);
			}
		}
	}
	
	/** Medizinische Interpretation auslesen */
	public String getMedInterpretation(){
		return checkNull(j
			.queryString("SELECT med_interpret FROM TARMED_EXTENSION WHERE CODE=" + getWrappedId())); //$NON-NLS-1$
	}
	
	/** Medizinische Interpretation setzen (Wird nur vom Importer gebraucht) */
	public void setMedInterpretation(final String text){
		j.exec("UPDATE TARMED_EXTENSION SET med_interpret=" + JdbcLink.wrap(text) + " WHERE CODE=" + getWrappedId()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/** Technische Interpretation auslesen */
	public String getTechInterpretation(){
		return checkNull(j
			.queryString("SELECT tech_interpret FROM TARMED_EXTENSION WHERE CODE=" + getWrappedId())); //$NON-NLS-1$
	}
	
	/** Technische Intepretation setzen (Wird nur vom Importer gebraucht */
	public void setTechInterpretation(final String text){
		j.exec("UPDATE TARMED_EXTENSION SET tech_interpret=" + JdbcLink.wrap(text) + " WHERE CODE=" + getWrappedId()); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/** Qualitative Dignität holen (als code) */
	public String getDigniQuali(){
		return checkNull(get(FLD_DIGNI_QUALI)); //$NON-NLS-1$
	}
	
	/** Qualitative Dignität als Text holen */
	public String getDigniQualiAsText(){
		return checkNull(getTextForDigniQuali(get(FLD_DIGNI_QUALI))); //$NON-NLS-1$
	}
	
	/** Qualitative Dinität setzen (Wird nur vom Importer gebraucht) */
	public void setDigniQuali(final String dql){
		set(FLD_DIGNI_QUALI, dql); //$NON-NLS-1$
	}
	
	/** Quantitative Dignität als code holen */
	public String getDigniQuanti(){
		return checkNull(get(FLD_DIGNI_QUANTI)); //$NON-NLS-1$
	}
	
	/** Quantitative Dignität als Text holen */
	public String getDigniQuantiAsText(){
		return checkNull(getTextForDigniQuanti(get(FLD_DIGNI_QUANTI))); //$NON-NLS-1$
	}
	
	/** Sparte holen (als Code) */
	public String getSparte(){
		return checkNull(get(FLD_SPARTE)); //$NON-NLS-1$
	}
	
	/** Sparte als Text holen */
	public String getSparteAsText(){
		return checkNull(getTextForSparte(get(FLD_SPARTE))); //$NON-NLS-1$
	}
	
	/** Name des verwendeten Codesystems holen (liefert immer "Tarmed") */
	@Override
	public String getCodeSystemName(){
		return "Tarmed"; //$NON-NLS-1$
	}
	
	protected TarmedLeistung(final String id){
		super(id);
	}
	
	public TarmedLeistung(){/* leer */
	}
	
	/** Eine Position einlesen */
	public static TarmedLeistung load(final String id){
		return new TarmedLeistung(id);
	}
	
	/** Eine Position vom code einlesen */
	public static IVerrechenbar getFromCode(final String code){
		return getFromCode(code, new TimeTool());
	}
	
	/** Eine Position vom code einlesen */
	public static IVerrechenbar getFromCode(final String code, TimeTool date){
		Query<TarmedLeistung> query = new Query<TarmedLeistung>(TarmedLeistung.class);
		query.add(FLD_CODE, "=", code);
		List<TarmedLeistung> leistungen = query.execute();
		for (TarmedLeistung tarmedLeistung : leistungen) {
			TimeTool validFrom = tarmedLeistung.getGueltigVon();
			TimeTool validTo = tarmedLeistung.getGueltigBis();
			if (date.isAfterOrEqual(validFrom) && date.isBeforeOrEqual(validTo))
				return tarmedLeistung;
		}
		return null;
	}
	
	/**
	 * Konfigurierbarer Filter für die Anzeige des Tarmed-Codebaums in Abhängigkeit vom gewählten
	 * Mandanten (Nur zur Dignität passende Einträge anzeigen)
	 * 
	 * @author gerry
	 */
	
	public static class MandantFilter implements IFilter {
		
		MandantFilter(final Mandant m){
			
		}
		
		public boolean select(final Object object){
			if (object instanceof TarmedLeistung) {
				/* TarmedLeistung tl = (TarmedLeistung) object; */
				return true;
			}
			return false;
		}
		
	}
	
	/**
	 * Komparator zum Sortieren der Codes. Es wird einfach nach Codeziffer sortiert. Wirft eine
	 * ClassCastException, wenn die Objekte nicht TarmedLeistungen sind.
	 * 
	 * @author gerry
	 */
	static class TarmedComparator implements Comparator {
		
		public int compare(final Object o1, final Object o2){
			TarmedLeistung tl1 = (TarmedLeistung) o1;
			TarmedLeistung tl2 = (TarmedLeistung) o2;
			return tl1.getCode().compareTo(tl2.getCode());
		}
		
	}
	
	@Override
	public IOptifier getOptifier(){
		return tarmedOptifier;
	}
	
	@Override
	public Comparator getComparator(){
		return tarmedComparator;
	}
	
	@Override
	public IFilter getFilter(final Mandant m){
		return new MandantFilter(m);
	}
	
	@Override
	public boolean isDragOK(){
		return (!StringTool.isNothing(getDigniQuali().trim()));
	}
	
	public int getAL(){
		loadExtension();
		return (int) Math.round(checkZeroDouble(ext.get(FLD_TP_AL)) * 100); //$NON-NLS-1$
	}
	
	public int getTL(){
		loadExtension();
		return (int) Math.round(checkZeroDouble(ext.get(FLD_TP_TL)) * 100); //$NON-NLS-1$
	}
	
	/**
	 * Preis der Leistung in Rappen public int getPreis(TimeTool date, String subgroup) {
	 * loadExtension(); String t=ext.get("TP_TL"); String a=ext.get("TP_AL"); double tl=0.0; double
	 * al=0.0; try{ tl= (t==null) ? 0.0 : Double.parseDouble(t); }catch(NumberFormatException ex){
	 * tl=0.0; } try{ al= (a==null) ? 0.0 : Double.parseDouble(a); }catch(NumberFormatException ex){
	 * al=0.0; } double tp=getVKMultiplikator(date, subgroup)*100; return
	 * (int)Math.round((tl+al)*tp); }
	 */
	@Override
	public int getMinutes(){
		loadExtension();
		double min = checkZeroDouble(ext.get("LSTGIMES_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(ext.get("VBNB_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(ext.get("BEFUND_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(ext.get("WECHSEL_MIN")); //$NON-NLS-1$
		return (int) Math.round(min);
	}
	
	/**
	 * Get the exclusions valid now as String, containing the service and chapter codes. Group
	 * exclusions are NOT part of the String.
	 * 
	 * @return
	 */
	public String getExclusion(){
		curTimeHelper.setTime(new Date());
		return getExclusion(curTimeHelper);
	}
	
	/**
	 * Get the exclusions valid at the paramater date as String, containing the service and chapter
	 * codes. Group exclusions are NOT part of the String.
	 * 
	 * @param date
	 * @return
	 */
	public String getExclusion(TimeTool date){
		String exclusions = TarmedKumulation.getExclusions(getCode(), date);
		if (exclusions == null) {
			loadExtension();
			if (ext == null) {
				return "";
			}
			return checkNull(ext.get("exclusion"));
		}
		return checkNull(exclusions);
	}
	
	public int getTP(final TimeTool date, final IFall fall){
		loadExtension();
		String t = ext.get(FLD_TP_TL); //$NON-NLS-1$
		String a = ext.get(FLD_TP_AL); //$NON-NLS-1$
		double tl = 0.0;
		double al = 0.0;
		try {
			tl = (t == null) ? 0.0 : Double.parseDouble(t);
		} catch (NumberFormatException ex) {
			tl = 0.0;
		}
		try {
			al = (a == null) ? 0.0 : Double.parseDouble(a);
		} catch (NumberFormatException ex) {
			al = 0.0;
		}
		return (int) Math.round((tl + al) * 100.0);
	}
	
	public double getFactor(final TimeTool date, final IFall fall){
		return getVKMultiplikator(date, fall);
	}
	
	/**
	 * Returns the GueltigVon value
	 * 
	 * @return the GueltigVon value as a TimeTool object, or null if the value is not defined
	 */
	public TimeTool getGueltigVon(){
		String value = get(FLD_GUELTIG_VON);
		if (!StringTool.isNothing(value)) {
			return new TimeTool(value);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns the GueltigBis value
	 * 
	 * @return the GueltigBis value as a TimeTool object, or null if the value is not defined
	 */
	public TimeTool getGueltigBis(){
		String value = get(FLD_GUELTIG_BIS);
		if (!StringTool.isNothing(value)) {
			TimeTool res = new TimeTool(value);
			res.set(TimeTool.HOUR_OF_DAY, 23);
			res.set(TimeTool.MINUTE, 59);
			res.set(TimeTool.SECOND, 59);
			return res;
		} else {
			return null;
		}
	}
	
	@Override
	public List<Object> getActions(final Object kontext){
		List<Object> ret = super.getActions((Verrechnet) kontext);
		if (kontext != null) {
			ret.add(new Action("Details") {
				@Override
				public void run(){
					new TarmedDetailDialog(UiDesk.getTopShell(), (Verrechnet) kontext).open();
				}
			});
		}
		return ret;
	}
	
	public static boolean isObligation(Verrechnet v){
		IVerrechenbar vv = v.getVerrechenbar();
		if (vv instanceof TarmedLeistung) {
			String obli = v.getDetail(PFLICHTLEISTUNG);
			if ((obli == null) || (Boolean.parseBoolean(obli))) {
				return true;
			}
		}
		return false;
	}
	
	public static String getSide(Verrechnet v){
		IVerrechenbar vv = v.getVerrechenbar();
		if (vv instanceof TarmedLeistung) {
			String side = v.getDetail(SIDE);
			if (SIDE_L.equalsIgnoreCase(side)) {
				return LEFT;
			} else if (SIDE_R.equalsIgnoreCase(side)) {
				return RIGHT;
			}
		}
		return "none";
	}
	
	public boolean requiresSide(){
		loadExtension();
		if (checkZero(ext.get(SIDE.toUpperCase())) == 1) {
			return true;
		}
		return false;
	}
	
	public String getParent(){
		return get(FLD_PARENT);
	}
	
	@Override
	public VatInfo getVatInfo(){
		// TarmedLeistung is a treatment per default
		return VatInfo.VAT_CH_ISTREATMENT;
	}

	/**
	 * @return the current data set version of the tarmed database
	 * @see http://tarmedsuisse.ch/
	 */
	public static int getCurrentVersion(){
		TarmedLeistung version = load(ROW_VERSION);
		String versionVal = version.get(FLD_CODE);
		try{
			return Integer.parseInt(versionVal);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	/**
	 * @param versionVal sets the version of the contained data set
	 */
	public static void setVersion(String versionVal){
		TarmedLeistung version = load(ROW_VERSION);
		if (!version.exists()) {
			version = new TarmedLeistung();
			version.create(ROW_VERSION);
			version.set(FLD_NICK, VERSION_120);
		}
		version.set(FLD_CODE, versionVal);
	}
	
	@Override
	public int getCacheTime(){
		return DBConnection.CACHE_TIME_MAX;
	}
}
