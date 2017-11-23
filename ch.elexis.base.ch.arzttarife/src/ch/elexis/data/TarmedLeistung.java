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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.interfaces.IFall;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.data.UiVerrechenbarAdapter;
import ch.elexis.data.TarmedExclusion.TarmedExclusionType;
import ch.elexis.views.TarmedDetailDialog;
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
	
	private static String MANDANT_TYPE_EXTINFO_KEY = "ch.elexis.data.tarmed.mandant.type";
	
	public enum MandantType {
			SPECIALIST, PRACTITIONER
	}
	
	public static final String TABLENAME = "TARMED";
	
	public static final String FLD_CODE = "code";
	public static final String FLD_GUELTIG_BIS = "GueltigBis";
	public static final String FLD_GUELTIG_VON = "GueltigVon";
	public static final String FLD_SPARTE = "Sparte";
	public static final String FLD_DIGNI_QUANTI = "DigniQuanti";
	public static final String FLD_DIGNI_QUALI = "DigniQuali";
	public static final String FLD_TEXT = "Text";
	public static final String FLD_NICK = "Nickname";
	public static final String FLD_PARENT = "Parent";
	public static final String FLD_LAW = "Law";
	public static final String FLD_ISCHAPTER = "ischapter";
	
	public static final String EXT_FLD_TP_TL = "TP_TL";
	public static final String EXT_FLD_TP_AL = "TP_AL";
	public static final String EXT_FLD_F_AL_R = "F_AL_R";
	public static final String EXT_FLD_HIERARCHY_SLAVES = "HierarchySlaves";
	public static final String EXT_FLD_SERVICE_GROUPS = "ServiceGroups";
	public static final String EXT_FLD_SERVICE_BLOCKS = "ServiceBlocks";
	public static final String EXT_FLD_SERVICE_AGE = "ServiceAge";
	
	private static final String EXT_VERRRECHNET_TL = "TL"; //$NON-NLS-1$
	private static final String EXT_VERRRECHNET_AL = "AL"; //$NON-NLS-1$
	
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
	
	private static final String VERSION = "1.3.0";
	private static final String VERSION_110 = "1.1.0";
	private static final String VERSION_111 = "1.1.1";
	private static final String VERSION_120 = "1.2.0";
	private static final String VERSION_130 = "1.3.0";
	
	public static final String ROW_VERSION = "Version";
	public static final String CODESYSTEM_NAME = "Tarmed";
	
	private TarmedExtension extension;
	
	// @formatter:off
	private static final String upd110 = "ALTER TABLE TARMED ADD lastupdate BIGINT";
	private static final String upd120 = "ALTER TABLE TARMED ADD code VARCHAR(25);"
			+ " ALTER TABLE TARMED MODIFY ID VARCHAR(25);"
			+ " ALTER TABLE TARMED_EXTENSION MODIFY CODE VARCHAR(25);";

	private static final String upd130 = " ALTER TABLE " + TABLENAME + " MODIFY Parent VARCHAR(32);"
			+ " ALTER TABLE " + TABLENAME + " MODIFY ID VARCHAR(32);"
			+ " ALTER TABLE " + TABLENAME + " ADD PRIMARY KEY (" + FLD_ID + ");"
			+ " ALTER TABLE " + TABLENAME + " ADD Law VARCHAR(3);"
			+ " ALTER TABLE " + TABLENAME + " ADD ischapter CHAR(1) default '0';";

	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" 
		+"ID 					VARCHAR(32) primary key,"  
		+"lastupdate 			BIGINT," 
		+"deleted 				CHAR(1) default '0',"
		
		+ "Parent				VARCHAR(32),"
		+ "DigniQuanti 			CHAR(5)," 
		+ "DigniQuali 			CHAR(4)," 
		+ "Sparte				CHAR(4),"
		+ "GueltigVon			CHAR(8),"
		+ "GueltigBis			CHAR(8)," 
		+ "Nickname				VARCHAR(25)," 
		+ "tx255				VARCHAR(255),"
		+ "code 				VARCHAR(25),"
		+ "Law					VARCHAR(3),"
		+ "ischapter			CHAR(1)"
		+ ");"
		+ "CREATE INDEX tarmed_id on " + TABLENAME + " (" + FLD_ID + ");"		
		+ "CREATE INDEX tarmed2 on " + TABLENAME + " (" + FLD_PARENT + ");"		
		+ "CREATE INDEX tarmed3 on " + TABLENAME + " (" + FLD_LAW + ");"		
		+ "INSERT INTO " + TABLENAME + " (" + FLD_ID + ", " + FLD_NICK + ") VALUES (" + JdbcLink.wrap(ROW_VERSION) + ", " + JdbcLink.wrap(VERSION) + ");";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, "Ziffer=" + FLD_CODE, FLD_CODE, FLD_PARENT, FLD_DIGNI_QUALI, //$NON-NLS-1$
			FLD_DIGNI_QUANTI, //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			FLD_SPARTE, FLD_TEXT + "=tx255", "Name=tx255", FLD_NICK, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"GueltigVon=S:D:GueltigVon", "GueltigBis=S:D:GueltigBis", //$NON-NLS-1$ //$NON-NLS-2$
			"deleted", FLD_LAW, FLD_ISCHAPTER //$NON-NLS-1$ 
		);
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(createDB);
		} else {
			TarmedLeistung version = load(ROW_VERSION);
			VersionInfo vi = new VersionInfo(version.get(FLD_NICK));
			if (vi.isOlder(VERSION_110)) {
				createOrModifyTable(upd110);
				version.set(FLD_NICK, VERSION_110);
			}
			if (vi.isOlder(VERSION_111)) {
				createOrModifyTable("Update TARMED set gueltigbis='20993112' where id='39.0305'");
				version.set(FLD_NICK, VERSION_111);
			}
			if (vi.isOlder(VERSION_120)) {
				createOrModifyTable(upd120);
				version.set(FLD_NICK, VERSION_120);
			}
			if (vi.isOlder(VERSION_130)) {
				createOrModifyTable(upd130);
				version.set(FLD_NICK, VERSION_130);
			}
		}
		tarmedComparator = new TarmedComparator();
		tarmedOptifier = new TarmedOptifier();
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Tarmed", Xid.ASSIGNMENT_LOCAL);
	}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	/** Text zu einem Code der qualitativen Dignität holen */
	public static String getTextForDigniQuali(final String kuerzel){
		return TarmedDefinitionen.getTitle("DIGNI_QUALI", kuerzel);
	}
	
	/** Kurz-Code für eine qualitative Dignität holen */
	public static String getCodeForDigniQuali(final String titel){
		return TarmedDefinitionen.getKuerzel("DIGNI_QUALI", titel);
	}
	
	/** Text für einen Code für quantitative Dignität holen */
	public static String getTextForDigniQuanti(final String kuerzel){
		return TarmedDefinitionen.getTitle("DIGNI_QUALI", kuerzel);
	}
	
	/** Text für einen Sparten-Code holen */
	public static String getTextForSparte(final String kuerzel){
		return TarmedDefinitionen.getTitle("SPARTE", kuerzel);
	}
	
	/** Text für eine Anästhesie-Risikoklasse holen */
	public static String getTextForRisikoKlasse(final String kuerzel){
		return TarmedDefinitionen.getTitle("ANAESTHESIE", kuerzel);
	}
	
	/** Text für einen ZR_EINHEIT-Code holen (Sitzung, Monat usw.) */
	public static String getTextForZR_Einheit(final String kuerzel){
		return TarmedDefinitionen.getTitle("ZR_EINHEIT", kuerzel);
	}
	
	/** Alle Codes für Quantitative Dignität holen */
	public static String[] getDigniQuantiCodes(){
		return null;
	}
	
	/** Konstruktor wird nur vom Importer gebraucht */
	public TarmedLeistung(final String id, final String code, final String parent,
		final String DigniQuali, final String DigniQuanti, final String sparte, boolean isChapter){
		create(id, new String[] {
			FLD_CODE, FLD_PARENT, FLD_DIGNI_QUALI, FLD_DIGNI_QUANTI, FLD_SPARTE, FLD_ISCHAPTER
		}, new String[] {
			code, parent, DigniQuali, DigniQuanti, sparte, isChapter ? "1" : "0"
		});
		extension = new TarmedExtension(this);
	}
	
	public boolean isChapter(){
		return get(FLD_ISCHAPTER).equals("1");
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_CODE, FLD_TEXT, FLD_LAW);
		if (vals[0].isEmpty()) {
			vals[0] = getId();
		}
		return vals[0] + " " + vals[1]
			+ ((vals[2] != null && !vals[2].isEmpty()) ? " (" + vals[2] + ")" : "");
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
		if (getExtension() != null) {
			return (Hashtable<String, String>) extension.getMap(TarmedExtension.FLD_LIMITS);
		}
		return new Hashtable<>();
	}
	
	/** Erweiterte Informationen rückspeichern */
	@SuppressWarnings("unchecked")
	public void setExtension(Hashtable<? extends Object, ? extends Object> map){
		if (getExtension() != null) {
			extension.setMap(TarmedExtension.FLD_LIMITS, (Map<Object, Object>) map);
		} else {
			throw new IllegalStateException("No Extension available for tarmed [" + getId() + "]");
		}
	}
	
	/** Medizinische Interpretation auslesen */
	public String getMedInterpretation(){
		if (getExtension() != null) {
			extension.get(TarmedExtension.FLD_MED_INTERPRET);
		}
		return "";
	}
	
	/** Medizinische Interpretation setzen (Wird nur vom Importer gebraucht) */
	public void setMedInterpretation(final String text){
		if (getExtension() != null) {
			extension.set(TarmedExtension.FLD_MED_INTERPRET, text);
		}
		throw new IllegalStateException("No Extension available for tarmed [" + getId() + "]");
	}
	
	/** Technische Interpretation auslesen */
	public String getTechInterpretation(){
		if (getExtension() != null) {
			extension.get(TarmedExtension.FLD_TECH_INTERPRET);
		}
		return "";
	}
	
	/** Technische Intepretation setzen (Wird nur vom Importer gebraucht */
	public void setTechInterpretation(final String text){
		if (getExtension() != null) {
			extension.set(TarmedExtension.FLD_TECH_INTERPRET, text);
		}
		throw new IllegalStateException("No Extension available for tarmed [" + getId() + "]");
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
		return CODESYSTEM_NAME;
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
	
	/**
	 * Query for a {@link TarmedLeistung} using the code. The returned {@link TarmedLeistung} will
	 * be valid today and will have no specific law.
	 * 
	 * @param code
	 * @return null if no matching {@link TarmedLeistung} found
	 * @deprecated law should always be specified, use getFromCode with law parameter
	 */
	public static IVerrechenbar getFromCode(final String code){
		return getFromCode(code, new TimeTool());
	}
	
	/**
	 * Query for a {@link TarmedLeistung} using the code. The returned {@link TarmedLeistung} will
	 * be valid on date. It will have no specific law.
	 *
	 * @param code
	 * @param date
	 * @return null if no matching {@link TarmedLeistung} found
	 * @deprecated law should always be specified, use getFromCode with law parameter
	 */
	public static IVerrechenbar getFromCode(final String code, TimeTool date){
		return getFromCode(code, date, "");
	}
	
	/**
	 * Query for a {@link TarmedLeistung} using the code. The returned {@link TarmedLeistung} will
	 * be valid on date, and will be from the cataloge specified by law.
	 * 
	 * @param code
	 * @param date
	 * @param law
	 * @return null if no matching {@link TarmedLeistung} found
	 * @since 3.4
	 */
	public static IVerrechenbar getFromCode(@NonNull
	final String code, @NonNull TimeTool date, @Nullable String law){
		Query<TarmedLeistung> query = new Query<TarmedLeistung>(TarmedLeistung.class, FLD_CODE, code, TarmedLeistung.TABLENAME, new String[] {
			TarmedLeistung.FLD_GUELTIG_VON, TarmedLeistung.FLD_GUELTIG_BIS,
				TarmedLeistung.FLD_LAW, TarmedLeistung.FLD_ISCHAPTER
		});
		if (law != null) {
			query.add(FLD_LAW, Query.EQUALS, law, true);
		}
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
	
	/**
	 * Get the AL value of the {@link TarmedLeistung}.
	 * 
	 * @return
	 * @deprecated always use the method with {@link Mandant} parameter for correct billing
	 */
	public int getAL(){
		Hashtable<String, String> map = loadExtension();
		return (int) Math.round(checkZeroDouble(map.get(EXT_FLD_TP_AL)) * 100); //$NON-NLS-1$
	}
	
	/**
	 * Get the AL value of the {@link TarmedLeistung}. The {@link Mandant} is needed to determine
	 * special scaling factors. On billing of the {@link TarmedLeistung} the values for AL and TL
	 * should be set to the ExtInfo of the {@link Verrechnet} for later use.
	 * 
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	public int getAL(Mandant mandant){
		Hashtable<String, String> map = loadExtension();
		double scaling = 100;
		if (mandant != null) {
			MandantType type = getMandantType(mandant);
			if (type == MandantType.PRACTITIONER) {
				double alScaling = checkZeroDouble(map.get(EXT_FLD_F_AL_R));
				if (scaling > 0.1) {
					scaling *= alScaling;
				}
			}
		}
		return (int) Math.round(checkZeroDouble(map.get(EXT_FLD_TP_AL)) * scaling); //$NON-NLS-1$
	}
	
	/**
	 * Get the AL points used to calculate the value of the {@link Verrechnet}. The value is set by
	 * the {@link TarmedOptifier}. If not found, the value of {@link TarmedLeistung#getAL()} is
	 * returned. If no information is found 0 is returned.
	 * 
	 * @param verrechnet
	 * @return
	 * @since 3.4
	 */
	public static int getAL(Verrechnet verrechnet){
		String alString = verrechnet.getDetail(EXT_VERRRECHNET_AL);
		if (alString != null) {
			try {
				return Integer.parseInt(alString);
			} catch (NumberFormatException ne) {
				// ignore, try resolve from IVerrechenbar
			}
		}
		IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
		if (verrechenbar instanceof TarmedLeistung) {
			Konsultation kons = verrechnet.getKons();
			return kons != null ? ((TarmedLeistung) verrechenbar).getAL(kons.getMandant())
					: ((TarmedLeistung) verrechenbar).getAL();
		}
		return 0;
	}
	
	/**
	 * Get the TL points used to calculate the value of the {@link Verrechnet}. The value is set by
	 * the {@link TarmedOptifier}. If not found, the value of {@link TarmedLeistung#getTL()} is
	 * returned. If no information is found 0 is returned.
	 * 
	 * @param verrechnet
	 * @return
	 * @since 3.4
	 */
	public static int getTL(Verrechnet verrechnet){
		String tlString = verrechnet.getDetail(EXT_VERRRECHNET_TL);
		if (tlString != null) {
			try {
				return Integer.parseInt(tlString);
			} catch (NumberFormatException ne) {
				// ignore, try resolve from IVerrechenbar
			}
		}
		IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
		if (verrechenbar instanceof TarmedLeistung) {
			return ((TarmedLeistung) verrechenbar).getTL();
		}
		return 0;
	}
	
	/**
	 * Get the {@link MandantType} of the {@link Mandant}. If not found the default value is
	 * {@link MandantType#SPECIALIST}.
	 * 
	 * @param mandant
	 * @return
	 * @since 3.4
	 */
	public static MandantType getMandantType(Mandant mandant){
		Object typeObj = mandant.getExtInfoStoredObjectByKey(MANDANT_TYPE_EXTINFO_KEY);
		if (typeObj instanceof String) {
			return MandantType.valueOf((String) typeObj);
		}
		return MandantType.SPECIALIST;
	}
	
	/**
	 * Set the {@link MandantType} of the {@link Mandant}.
	 * 
	 * @param mandant
	 * @param type
	 */
	public static void setMandantType(Mandant mandant, MandantType type){
		mandant.setExtInfoStoredObjectByKey(MANDANT_TYPE_EXTINFO_KEY, type.name());
	}
	
	public int getTL(){
		Hashtable<String, String> map = loadExtension();
		return (int) Math.round(checkZeroDouble(map.get(EXT_FLD_TP_TL)) * 100); //$NON-NLS-1$
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
		Hashtable<String, String> map = loadExtension();
		double min = checkZeroDouble(map.get("LSTGIMES_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(map.get("VBNB_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(map.get("BEFUND_MIN")); //$NON-NLS-1$
		min += checkZeroDouble(map.get("WECHSEL_MIN")); //$NON-NLS-1$
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
	 * Get the exclusions valid now as String, containing the service and chapter codes. Group
	 * exclusions are NOT part of the String.
	 * 
	 * @param kons
	 * 
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(Konsultation kons){
		if (kons == null) {
			curTimeHelper.setTime(new Date());
		} else {
			curTimeHelper.set(kons.getDatum());
		}
		return getExclusions(curTimeHelper);
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
			Hashtable<String, String> map = loadExtension();
			if (map == null) {
				return "";
			}
			return checkNull(map.get("exclusion"));
		}
		return checkNull(exclusions);
	}
	
	/**
	 * Get {@link TarmedExclusion} objects with this {@link TarmedLeistung} as master.
	 * 
	 * @param date
	 * @return
	 */
	public List<TarmedExclusion> getExclusions(TimeTool date){
		return TarmedKumulation.getExclusions(getCode(),
			isChapter() ? TarmedExclusionType.CHAPTER : TarmedExclusionType.SERVICE, date,
			get(TarmedLeistung.FLD_LAW));
	}
	
	public int getTP(final TimeTool date, final IFall fall){
		return (getTL() + getAL());
	}
	
	@Override
	public int getTP(TimeTool date, Konsultation kons){
		if (kons != null) {
			return (getTL() + getAL(kons.getMandant()));
		}
		return (getTL() + getAL());
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
		Hashtable<String, String> map = loadExtension();
		if (checkZero(map.get(SIDE.toUpperCase())) == 1) {
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
		try {
			return Integer.parseInt(versionVal);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}
	
	/**
	 * @param versionVal
	 *            sets the version of the contained data set
	 */
	public static void setVersion(String versionVal){
		TarmedLeistung version = load(ROW_VERSION);
		if (!version.exists()) {
			version = new TarmedLeistung();
			version.create(ROW_VERSION);
			version.set(FLD_NICK, VERSION);
		}
		version.set(FLD_CODE, versionVal);
	}
	
	@Override
	public int getCacheTime(){
		return DBConnection.CACHE_TIME_MAX;
	}
	
	/**
	 * Get the list of codes of the possible slave services allowed by tarmed.
	 * 
	 * @return
	 */
	public List<String> getHierarchy(TimeTool date){
		List<String> ret = new ArrayList<>();
		List<String> hierarchy = getExtStringListField(TarmedLeistung.EXT_FLD_HIERARCHY_SLAVES);
		if (!hierarchy.isEmpty()) {
			for (String string : hierarchy) {
				int dateStart = string.indexOf('[');
				String datesString = string.substring(dateStart + 1, string.length() - 1);
				String codeString = string.substring(0, dateStart);
				if (isDateWithinDatesString(date, datesString)) {
					ret.add(codeString);
				}
			}
		}
		return ret;
	}
	
	private boolean isDateWithinDatesString(TimeTool date, String datesString){
		String[] parts = datesString.split("\\|");
		if (parts.length == 2) {
			LocalDate from = LocalDate.parse(parts[0]);
			LocalDate to = LocalDate.parse(parts[1]);
			LocalDate localDate = date.toLocalDate();
			return (from.isBefore(localDate) || from.isEqual(localDate))
				&& (to.isAfter(localDate) || to.isEqual(localDate));
		}
		return false;
	}
	
	private List<String> getExtStringListField(String extKey){
		List<String> ret = new ArrayList<>();
		Hashtable<String, String> map = loadExtension();
		String values = map.get(extKey);
		if (values != null && !values.isEmpty()) {
			String[] parts = values.split(", ");
			for (String string : parts) {
				ret.add(string);
			}
		}
		return ret;
	}
	
	/**
	 * Get the list of service groups this service is part of.
	 * 
	 * @return
	 */
	public List<String> getServiceGroups(TimeTool date){
		List<String> ret = new ArrayList<>();
		List<String> groups = getExtStringListField(TarmedLeistung.EXT_FLD_SERVICE_GROUPS);
		if (!groups.isEmpty()) {
			for (String string : groups) {
				int dateStart = string.indexOf('[');
				String datesString = string.substring(dateStart + 1, string.length() - 1);
				String groupString = string.substring(0, dateStart);
				if (isDateWithinDatesString(date, datesString)) {
					ret.add(groupString);
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the list of service blocks this service is part of.
	 * 
	 * @return
	 */
	public List<String> getServiceBlocks(TimeTool date){
		List<String> ret = new ArrayList<>();
		List<String> blocks = getExtStringListField(TarmedLeistung.EXT_FLD_SERVICE_BLOCKS);
		if (!blocks.isEmpty()) {
			for (String string : blocks) {
				int dateStart = string.indexOf('[');
				String datesString = string.substring(dateStart + 1, string.length() - 1);
				String blockString = string.substring(0, dateStart);
				if (isDateWithinDatesString(date, datesString)) {
					ret.add(blockString);
				}
			}
		}
		return ret;
	}
	
	public String getServiceTyp(){
		Hashtable<String, String> map = loadExtension();
		return map.get("LEISTUNG_TYP");
	}
	
	/**
	 * Remove the entry from the database. If entry should only be marked as deleted, use
	 * {@link TarmedLeistung#delete()}.
	 * 
	 * @since 3.4
	 */
	public boolean remove(){
		return getDBConnection()
			.exec("DELETE FROM " + getTableName() + " WHERE ID='" + getId() + "'") > 0;
	}
	
	/**
	 * Get the {@link TarmedExtension} object for this {@link TarmedLeistung}.
	 * 
	 * @return
	 * @since 3.4
	 */
	public TarmedExtension getExtension(){
		if (extension == null) {
			extension = TarmedExtension.getExtension(this);
		}
		return extension;
	}
	
	/**
	 * Check if there is a parentId value not created with the new importer. Old imports created
	 * parent reference using the code attribute.
	 * 
	 * @return
	 * @since 3.4
	 */
	public static boolean hasParentIdReference(){
		DBConnection connection = PersistentObject.getDefaultConnection();
		Stm stm = connection.getStatement();
		try {
			ResultSet res = stm.query("SELECT DISTINCT Parent FROM tarmed where ID <> '"
				+ TarmedLeistung.ROW_VERSION + "' AND Parent <> 'NIL';");
			while (res.next()) {
				String distinctParentId = res.getString(1);
				int contains = distinctParentId.indexOf('-');
				if (contains == -1) {
					return false;
				}
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(TarmedLeistung.class).error("Error checking parent ids", e);
		} finally {
			connection.releaseStatement(stm);
		}
		return true;
	}
	
	public static List<String> getAvailableLaws(){
		List<String> ret = new ArrayList<>();
		DBConnection connection = PersistentObject.getDefaultConnection();
		Stm stm = connection.getStatement();
		try {
			ResultSet res = stm.query("SELECT DISTINCT Law FROM tarmed where ID <> '"
				+ TarmedLeistung.ROW_VERSION + "';");
			while (res.next()) {
				String law = res.getString(1);
				if (law != null) {
					ret.add(law);
				}
			}
		} catch (SQLException e) {
			LoggerFactory.getLogger(TarmedLeistung.class).error("Error getting laws", e);
		} finally {
			connection.releaseStatement(stm);
		}
		return ret;
	}
	
	public List<TarmedLimitation> getLimitations(){
		String lim = (String) loadExtension().get("limits"); //$NON-NLS-1$
		if (lim != null && !lim.isEmpty()) {
			List<TarmedLimitation> ret = new ArrayList<>();
			String[] lines = lim.split("#"); //$NON-NLS-1$
			for (String line : lines) {
				ret.add(TarmedLimitation.of(line).setTarmedLeistung(this));
			}
			return ret;
		}
		return Collections.emptyList();
	}
}
