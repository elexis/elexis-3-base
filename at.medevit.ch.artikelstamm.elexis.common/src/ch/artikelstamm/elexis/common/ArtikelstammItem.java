/*******************************************************************************
 * Copyright (c) 2013-2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package ch.artikelstamm.elexis.common;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.elexis.common.preference.MargePreference;
import at.medevit.ch.artikelstamm.ui.IArtikelstammItem;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.ui.optifier.NoObligationOptifier;
import ch.elexis.data.Artikel;
import ch.elexis.data.Fall;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.Money;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

/**
 * {@link ArtikelstammItem} persistent object implementation. This class conforms both to the
 * requirements of an {@link Artikel} as required by Elexis v2.1 and to the requirements given by
 * the common base in form of {@link IArtikelstammItem}
 */
public class ArtikelstammItem extends Artikel implements IArtikelstammItem {
	private static DateFormat df = new SimpleDateFormat("ddMMyy HH:mm");
	
	private static IOptifier noObligationOptifier = new NoObligationOptifier();
	private static IOptifier defaultOptifier = new DefaultOptifier();
	private final double ZERO = 0.0;
	
	public static final String TABLENAME = "ARTIKELSTAMM_CH";
	static final String VERSION = "1.1.0";
	
	//@formatter:off
	/** Eintrag zugeh. zu  */ public static final String FLD_CUMMULATED_VERSION = "CUMM_VERSION";
	/** Blackboxed item */ public static final String FLD_BLACKBOXED = "BB";
	/** (P)harma / (N)on Pharma */ public static final String FLD_ITEM_TYPE = "TYPE";
	
	/** Global Trade Index Number **/ public static final String FLD_GTIN = "GTIN";
	/** Pharmacode, unique record identifier */ public static final String FLD_PHAR = "PHAR";
	/** Artikelbeschreibung */ 		public static final String FLD_DSCR = "DSCR";
	/** Additional descr. of the dataset */ public static final String FLD_ADDDSCR = "ADDDSCR";
	/** ATC Code */ 				public static final String FLD_ATC = "ATC";
	/** Manufacturer GLN Number */ 	public static final String FLD_COMP_GLN = "COMP_GLN";
	/** Manufacturer Name */		public static final String FLD_COMP_NAME = "COMP_NAME";
	/** Ex-factory price */ 		public static final String FLD_PEXF = "PEXF";
	/** Public price */ 			public static final String FLD_PPUB = "PPUB";
	/** package size */ 			public static final String FLD_PKG_SIZE = "PKG_SIZE";
	/** Spezialitätenliste Entry */ public static final String FLD_SL_ENTRY = "SL_ENTRY";
	/** Abgabekategorie */ 		public static final String FLD_IKSCAT = "IKSCAT";
	/** Limitation ja/nein */ 	public static final String FLD_LIMITATION = "LIMITATION";
	/** Limitationspunkte */ 	public static final String FLD_LIMITATION_PTS = "LIMITIATION_PTS";
	/** Limitationstext */ 		public static final String FLD_LIMITATION_TEXT = "LIMITATION_TXT";
	/** Generiktyp O/G */ 		public static final String FLD_GENERIC_TYPE = "GENERIC_TYPE";
	/** Generikum verfügbar */ 	public static final String FLD_HAS_GENERIC = "HAS_GENERIC";
	/** LPPV Eintrag ja/nein */	public static final String FLD_LPPV = "LPPV";
	/** Selbstbehalt in % */	public static final String FLD_DEDUCTIBLE = "DEDUCTIBLE";
	/** Ist Betäubungsmittel */	public static final String FLD_NARCOTIC = "NARCOTIC";
	/** CAS Nr wenn Betäub */	public static final String FLD_NARCOTIC_CAS = "NARCOTIC_CAS";
	/** Ist Impfstoff */		public static final String FLD_VACCINE = "VACCINE";
	
	
	public static final String EXTINFO_VAL_VAT_OVERRIDEN = "VAT_OVERRIDE";
	
	/** Definition of the database table */
	static final String createDB =
		"CREATE TABLE " + TABLENAME
			+ "("
			+ "ID VARCHAR(25) primary key," // has to be of type varchar else version.exists fails
			+ "lastupdate BIGINT,"
			+ "deleted CHAR(1) default '0'," // will never be set to 1
			+ FLD_ITEM_TYPE + " CHAR(1)," // id(VERSION) DataQuality of last import P
			+ FLD_BLACKBOXED + " CHAR(1),"// id(VERSION) DataQuality of last import N
			+ FLD_CUMMULATED_VERSION + " CHAR(4),"
			+ FLD_GTIN + " VARCHAR(14)," // id(VERSION) contains table version, has to be varchar else vi.isolder() fails
			+ FLD_PHAR 	+" CHAR(7),"
			+ FLD_DSCR	+" VARCHAR(50)," // id(VERSION) filename of last P import
			+ FLD_ADDDSCR	+" VARCHAR(50)," // id(VERSION) filename of last N import
			+ FLD_ATC +" CHAR(10),"
			+ FLD_COMP_GLN + " CHAR(13),"
			+ FLD_COMP_NAME + " VARCHAR(255),"
			+ FLD_PEXF + " CHAR(10)," // id(VERSION) contains cummulated P dataset vers
			+ FLD_PPUB + " CHAR(10)," // id(VERSION) contains cummulated N dataset vers
			+ FLD_PKG_SIZE + " CHAR(6),"
			+ FLD_SL_ENTRY + " CHAR(1),"
			+ FLD_IKSCAT + " CHAR(1),"
			+ FLD_LIMITATION + " CHAR(1),"
			+ FLD_LIMITATION_PTS+ " CHAR(4),"
			+ FLD_LIMITATION_TEXT + " TEXT,"
			+ FLD_GENERIC_TYPE + " CHAR(1),"
			+ FLD_HAS_GENERIC + " CHAR(1),"
			+ FLD_LPPV + " CHAR(1),"
			+ FLD_DEDUCTIBLE + " CHAR(6),"
			+ FLD_NARCOTIC + " CHAR(1),"
			+ FLD_NARCOTIC_CAS + " VARCHAR(20)," // id(VERSION) contains table creation date
			+ FLD_VACCINE + " CHAR(1),"
			+ FLD_LIEFERANT_ID +" VARCHAR(25),"
			+ MAXBESTAND + " VARCHAR(4),"
			+ MINBESTAND + " VARCHAR(4),"
			+ ISTBESTAND + " VARCHAR(4),"
			+ VERKAUFSEINHEIT + " VARCHAR(4),"  // Stück pro Abgabe
			+ ANBRUCH + " VARCHAR(4),"			// Aktuell am Lager
			+ PersistentObject.FLD_EXTINFO + " BLOB"
			+ "); "
			+ "CREATE INDEX idxAiPHAR ON " + TABLENAME + " ("+FLD_PHAR+"); "
			+ "CREATE INDEX idxAiITEMTYPE ON " + TABLENAME + " ("+FLD_ITEM_TYPE+"); "
			+ "CREATE INDEX idxAiGTIN ON "+ TABLENAME + " ("+FLD_GTIN+"); "
			+ "CREATE INDEX idxAiMONTH ON "+ TABLENAME + " ("+FLD_CUMMULATED_VERSION+"); "
			+ "CREATE INDEX idxAiBB ON "+ TABLENAME + " ("+FLD_BLACKBOXED+"); "
			+ "INSERT INTO " + TABLENAME + " (ID,"+FLD_GTIN+","+FLD_NARCOTIC_CAS+","+FLD_PEXF+","+FLD_PPUB+") VALUES ('VERSION',"
			+ JdbcLink.wrap(VERSION) +","+JdbcLink.wrap(df.format(new Date()))+",0,0);";

	
	static final String dbUpdateFrom10to11 =
		"ALTER TABLE "+TABLENAME+" ADD "+PersistentObject.FLD_EXTINFO+" BLOB;";
	//@formatter:on
	
	static {
		/**
		 * FLD_ITEM_TYPE, FLD_PHAR, FLD_PEXF, FLD_PPUB have two mappings on purpose! The mappings
		 * with the Artikel-Fields are essential in order to make the ArtikelDetailDialog work with
		 * ArtikelstammItems
		 */
		addMapping(TABLENAME, FLD_ITEM_TYPE, Artikel.FLD_TYP + "=" + FLD_ITEM_TYPE,
			FLD_CUMMULATED_VERSION, FLD_BLACKBOXED, FLD_GTIN, FLD_PHAR, Artikel.FLD_PHARMACODE
				+ "=" + FLD_PHAR, FLD_DSCR, FLD_ADDDSCR, FLD_ATC, FLD_COMP_GLN, FLD_COMP_NAME,
			FLD_PEXF, Artikel.FLD_EK_PREIS + "=" + FLD_PEXF, FLD_PPUB, Artikel.FLD_VK_PREIS + "="
				+ FLD_PPUB, FLD_PKG_SIZE, FLD_SL_ENTRY, FLD_IKSCAT, FLD_LIMITATION,
			FLD_LIMITATION_PTS, FLD_LIMITATION_TEXT, FLD_GENERIC_TYPE, FLD_HAS_GENERIC, FLD_LPPV,
			FLD_DEDUCTIBLE, FLD_NARCOTIC, FLD_NARCOTIC_CAS, FLD_VACCINE, FLD_LIEFERANT_ID,
			MAXBESTAND, MINBESTAND, ISTBESTAND, VERKAUFSEINHEIT, ANBRUCH,
			PersistentObject.FLD_EXTINFO);
		ArtikelstammItem version = load("VERSION"); //$NON-NLS-1$
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(version.get(FLD_GTIN));
			
			if (vi.isOlder(VERSION)) {
				createOrModifyTable(dbUpdateFrom10to11);
				version.set(FLD_GTIN, VERSION);
			}
		}
	}
	
	@Override
	public String getLabel(){
		return get(FLD_DSCR) + " (" + get(FLD_ADDDSCR) + ")";
	}
	
	@Override
	protected String getTableName(){
		//
		return TABLENAME;
	}
	
	ArtikelstammItem(){}
	
	public static ArtikelstammItem load(String id){
		return new ArtikelstammItem(id);
	}
	
	protected ArtikelstammItem(String id){
		super(id);
	}
	
	/**
	 * Main import constructor containing the required properties to create the deterministic id
	 * 
	 * @param cummulatedVersion
	 *            dataset version for type
	 * @param type
	 *            the data set type
	 * @param gtin
	 *            global trade index number
	 * @param phar
	 *            pharmacode
	 * @param dscr
	 *            description
	 * @param addscr
	 *            additional description
	 */
	public ArtikelstammItem(int cummulatedVersion, TYPE type, String gtin, BigInteger phar,
		String dscr, String addscr){
		create(ArtikelstammHelper.createUUID(cummulatedVersion, type, gtin, phar));
		
		// fix pharmacode length < 7 chars
		String pharmacode = (phar != null) ? String.format("%07d", phar) : "0000000";
		
		set(new String[] {
			FLD_ITEM_TYPE, FLD_GTIN, FLD_PHAR, FLD_DSCR, FLD_ADDDSCR, FLD_CUMMULATED_VERSION,
			FLD_BLACKBOXED
		}, type.name(), gtin, pharmacode, dscr, addscr, cummulatedVersion + "",
			StringConstants.ZERO);
	}
	
	// -------------------------------------------------
	/**
	 * Load an article according to its Pharmacode (unique identifier)
	 * 
	 * @param pharNo
	 *            Pharmacode, unique record identifier
	 * @return element if exists, else null
	 */
	public static ArtikelstammItem loadByPHARNo(String pharNo){
		Query<ArtikelstammItem> qbe = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		String res = qbe.findSingle(FLD_PHAR, Query.EQUALS, pharNo);
		if (res == null)
			return null;
		return ArtikelstammItem.load(res);
	}
	
	@Override
	public String getName(){
		return checkNull(get(FLD_DSCR));
	}
	
	// --- ARTIKEL OVERRIDE
	
	@Override
	public int getTotalCount(){
		int pkgUnits = getIstbestand();
		int pkgSize = getPackungsGroesse();
		if (pkgSize == 0) {
			return pkgUnits;
		}
		int vkUnits = getVerkaufseinheit();
		if (vkUnits < pkgSize) {
			return (pkgUnits * pkgSize) + (getBruchteile() * vkUnits);
		}
		return pkgUnits;
	}
	
	@Override
	public int getPackungsGroesse(){
		int val = 0;
		try {
			val = Integer.parseInt(get(FLD_PKG_SIZE));
		} catch (NumberFormatException nfe) {
			
		}
		return val;
	}
	
	@Override
	public String getPackungsGroesseDesc(){
		return checkNull(get(FLD_ADDDSCR));
	}
	
	@Override
	public Money getKosten(final TimeTool dat){
		double vkt = checkZeroDouble(getTP(dat, null) + "");
		double vpe = checkZeroDouble((String) get(FLD_PKG_SIZE));
		double vke = checkZeroDouble((String) get(VERKAUFSEINHEIT));
		if (vpe != vke) {
			return new Money((int) Math.round(vke * (vkt / vpe)));
		} else {
			return new Money((int) Math.round(vkt));
		}
	}
	
	@Override
	public int getBruchteile(){
		return checkZero(get(ANBRUCH));
	}
	
	@Override
	public void setBruchteile(int number){
		set(ANBRUCH, Integer.toString(number));
	}
	
	@Override
	public void einzelAbgabe(final int n){
		int anbruch = getBruchteile();
		int ve = getVerkaufseinheit();
		int vk = getVerpackungsEinheit();
		if (vk == 0) {
			if (ve != 0) {
				vk = ve;
			}
		}
		if (ve == 0) {
			if (vk != 0) {
				ve = vk;
				setVerkaufseinheit(ve);
			}
		}
		int num = n * ve;
		if (vk == ve) {
			setIstbestand(getIstbestand() - n);
		} else {
			int rest = anbruch - num;
			while (rest < 0) {
				rest = rest + vk;
				setIstbestand(getIstbestand() - 1);
			}
			setBruchteile(rest);
		}
	}
	
	@Override
	public void einzelRuecknahme(int n){
		int anbruch = getBruchteile();
		int ve = getVerkaufseinheit();
		int vk = getVerpackungsEinheit();
		int num = n * ve;
		if (vk == ve) {
			setIstbestand(getIstbestand() + n);
		} else {
			int rest = anbruch + num;
			while (rest >= vk) {
				rest = rest - vk;
				setIstbestand(getIstbestand() + 1);
			}
			setBruchteile(rest);
		}
	}
	
	@Override
	public int getVerpackungsEinheit(){
		return checkZero(get(FLD_PKG_SIZE));
	}
	
	@Override
	public Money getEKPreis(){
		String value = get(FLD_PEXF);
		if (value != null && !value.isEmpty())
			return new Money(Double.parseDouble(value));
		return new Money();
	}
	
	@Override
	public Money getVKPreis(){
		String value = get(FLD_PPUB);
		if (value != null && !value.isEmpty()) {
			double dValue = Double.parseDouble(value);
			// user defined prices are represented in negative
			// values, we always need positive values however
			return new Money(Math.abs(dValue));
		}
		
		return MargePreference.calculateVKP(getEKPreis());
	}
	
	public boolean isCalculatedPrice(){
		String value = get(FLD_PPUB);
		if (value != null && !value.isEmpty()) {
			return false;
		}
		String exfValue = get(FLD_PEXF);
		if (exfValue != null && !exfValue.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public boolean isUserDefinedPrice(){
		String value = get(FLD_PPUB);
		if (value != null && !value.isEmpty()) {
			double pricePub = Double.parseDouble(value);
			// we need to differentiate -0.0 and 0.0
			return Double.doubleToRawLongBits(pricePub) < 0;
		}
		return false;
	}
	
	public void overrideVatInfo(VatInfo overridenVat){
		setExtInfoStoredObjectByKey(EXTINFO_VAL_VAT_OVERRIDEN, overridenVat.toString());
	}
	
	// -- VERRECHENBAR ADAPTER and ARTIKEL ---
	@Override
	public VatInfo getVatInfo(){
		String overridenVat = (String) getExtInfoStoredObjectByKey(EXTINFO_VAL_VAT_OVERRIDEN);
		if (overridenVat != null) {
			return VatInfo.valueOf(overridenVat);
		}
		
		switch (getType()) {
		case P:
			return VatInfo.VAT_CH_ISMEDICAMENT;
		case N:
			return VatInfo.VAT_CH_NOTMEDICAMENT;
		}
		return VatInfo.VAT_NONE;
	}
	
	@Override
	public IOptifier getOptifier(){
		VatInfo vatInfo = getVatInfo();
		if (!vatInfo.equals(VatInfo.VAT_CH_ISMEDICAMENT))
			return noObligationOptifier;
		return defaultOptifier;
	}
	
	@Override
	public int getPreis(TimeTool dat, Fall fall){
		double vkPreis = checkZeroDouble(getVKPreis().getCentsAsString());
		double pkgSize = checkZeroDouble((String) get(FLD_PKG_SIZE));
		double vkUnits = checkZeroDouble((String) get(VERKAUFSEINHEIT));
		if ((pkgSize > ZERO) && (vkUnits > ZERO) && (pkgSize != vkUnits)) {
			return (int) Math.round(vkUnits * (vkPreis / pkgSize));
		} else {
			return (int) Math.round(vkPreis);
		}
	}
	
	@Override
	public int getTP(TimeTool date, Fall fall){
		return getPreis(date, fall);
	}
	
	@Override
	public double getFactor(TimeTool date, Fall fall){
		// TODO
		return 1;
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public String getXidDomain(){
		return ArtikelstammConstants.CODESYSTEM_NAME;
	}
	
	@Override
	public String getCodeSystemName(){
		return ArtikelstammConstants.CODESYSTEM_NAME;
	}
	
	@Override
	public String getCodeSystemCode(){
		String pharmaCode = getPharmaCode();
		if (pharmaCode != null && pharmaCode.length() > 3) {
			return "400";
		}
		return super.getCodeSystemCode();
	}
	
	@Override
	public String getCode(){
		return get(FLD_PHAR);
	}
	
	@Override
	public String getPharmaCode(){
		return get(FLD_PHAR);
	};
	
	@Override
	public String getText(){
		return getLabel();
	}
	
	@Override
	public String getEAN(){
		return get(FLD_GTIN);
	}
	
	@Override
	public String getATC_code(){
		return getATCCode();
	}
	
	public int getVerkaufseinheit(){
		return checkZero(get(VERKAUFSEINHEIT));
	}
	
	// --------------------
	
	/**
	 * @param stammType
	 * @return The version of the resp {@link TYPE}, or 99999 if not found
	 */
	public static int getImportSetCumulatedVersion(TYPE stammType){
		ArtikelstammItem version = load("VERSION");
		switch (stammType) {
		case N:
			return version.getInt(FLD_PEXF);
		case P:
			return version.getInt(FLD_PPUB);
		}
		return 99999;
	}
	
	public static void setImportSetCumulatedVersion(TYPE stammType, int importStammVersion){
		ArtikelstammItem version = load("VERSION");
		switch (stammType) {
		case N:
			version.setInt(FLD_PEXF, importStammVersion);
			return;
		case P:
			version.setInt(FLD_PPUB, importStammVersion);
			return;
		}
	}
	
	public static void setImportSetDataQuality(TYPE p, int dq){
		ArtikelstammItem version = load("VERSION");
		switch (p) {
		case N:
			version.setInt(FLD_BLACKBOXED, dq);
			return;
		case P:
			version.setInt(FLD_ITEM_TYPE, dq);
			return;
		}
	}
	
	public static int getImportSetDataQuality(TYPE p){
		ArtikelstammItem version = load("VERSION");
		switch (p) {
		case N:
			return version.getInt(FLD_BLACKBOXED);
		case P:
			return version.getInt(FLD_ITEM_TYPE);
		default:
			return 0;
		}
	}
	
	/**
	 * Stores the filename of the last import source file
	 * 
	 * @param stammType
	 * @param importFilename
	 */
	public static void setImportSetLastImportFileName(TYPE stammType, String importFilename){
		ArtikelstammItem version = load("VERSION");
		switch (stammType) {
		case N:
			version.set(FLD_ADDDSCR, importFilename);
			return;
		case P:
			version.set(FLD_DSCR, importFilename);
			return;
		}
	}
	
	/**
	 * Retrieves the filename of the last import source file
	 * 
	 * @param stammType
	 */
	public static String getImportSetLastImportFileName(TYPE stammType){
		ArtikelstammItem version = load("VERSION");
		switch (stammType) {
		case N:
			return version.get(FLD_ADDDSCR);
		case P:
			return version.get(FLD_DSCR);
		default:
			return "";
		}
	}
	
	public static boolean purgeEntries(List<ArtikelstammItem> qre){
		JdbcLink link = getConnection();
		
		for (ArtikelstammItem artikelstammItem : qre) {
			link.exec("DELETE FROM " + TABLENAME + " WHERE ID=" + artikelstammItem.getWrappedId());
		}
		
		return true;
	}
	
	/**
	 * The article is marked as black-boxed if the value within {@link #FLD_BLACKBOXED} does not
	 * equal 0
	 * 
	 * @return
	 */
	public boolean isBlackBoxed(){
		String val = get(FLD_BLACKBOXED);
		if (!val.equals(StringConstants.ZERO))
			return true;
		return false;
	}
	
	/**
	 * 
	 * @return {@link BlackBoxReason} or <code>null</code> if invalid value
	 */
	public BlackBoxReason getBlackBoxReason(){
		int value = Integer.parseInt(get(FLD_BLACKBOXED));
		return BlackBoxReason.getByInteger(value);
	}
	
	// requirements for IArtikelstammItem
	@Override
	public String getDSCR(){
		return get(FLD_DSCR);
	}
	
	@Override
	public String getGTIN(){
		return get(FLD_GTIN);
	}
	
	@Override
	public String getPHAR(){
		return get(FLD_PHAR);
	}
	
	@Override
	public String getATCCode(){
		return get(FLD_ATC);
	}
	
	@Override
	public TYPE getType(){
		return ArtikelstammConstants.TYPE.valueOf(get(FLD_ITEM_TYPE));
	}
	
	public String getTyp(){
		return get(FLD_ITEM_TYPE);
	}
	
	@Override
	public String getManufacturerLabel(){
		StringBuilder sb = new StringBuilder();
		if (get(FLD_COMP_NAME) != null && get(FLD_COMP_NAME).length() > 1)
			sb.append(get(FLD_COMP_NAME));
		if (get(FLD_COMP_GLN) != null && get(FLD_COMP_GLN).length() > 1)
			sb.append(" (GLN " + get(FLD_COMP_GLN) + ")");
		return sb.toString();
	}
	
	@Override
	public Double getExFactoryPrice(){
		return getEKPreis().doubleValue();
		
	}
	
	@Override
	public Double getPublicPrice(){
		return getVKPreis().doubleValue();
	}
	
	@Override
	public void setPublicPrice(Double amount){
		if (amount < 0)
			throw new IllegalArgumentException("No negative values allowed");
		set(FLD_PPUB, "-" + amount);
	}
	
	@Override
	public boolean isInSLList(){
		return (get(FLD_SL_ENTRY) != null && get(FLD_SL_ENTRY).equals(StringConstants.ONE)) ? true
				: false;
	}
	
	@Override
	public String getSwissmedicCategory(){
		return get(FLD_IKSCAT);
	}
	
	@Override
	public String getGenericType(){
		return get(FLD_GENERIC_TYPE);
	}
	
	@Override
	public Integer getDeductible(){
		// Medikament wenn SL nicht 20 % dann 10 %
		String val = get(FLD_DEDUCTIBLE).trim();
		if (val == null || val.length() < 1)
			if (isInSLList()) {
				return 0;
			} else {
				return -1;
			}
		try {
			return new Integer(val);
		} catch (NumberFormatException nfe) {
			return 0;
		}
	}
	
	@Override
	public boolean isNarcotic(){
		return (get(FLD_NARCOTIC).equals(StringConstants.ONE)) ? true : false;
	}
	
	@Override
	public boolean isLimited(){
		return (get(FLD_LIMITATION).equals(StringConstants.ONE)) ? true : false;
	}
	
	@Override
	public String getLimitationPoints(){
		return get(FLD_LIMITATION_PTS);
	}
	
	@Override
	public String getLimitationText(){
		return get(FLD_LIMITATION_TEXT);
	}
	
	@Override
	public boolean isInLPPV(){
		return (get(FLD_LPPV).equals(StringConstants.ONE)) ? true : false;
	}
	
	public int getVerpackungseinheit(){
		return getVerpackungsEinheit();
	}
	
	public void setVerpackungseinheit(int vpe){
		set(FLD_PKG_SIZE, vpe + "");
	}
	
	public void setVerkaufseinheit(int vse){
		set(VERKAUFSEINHEIT, vse + "");
	}
	
	/**
	 * @param ean
	 *            the European Article Number or GTIN
	 * @return the ArtikelstammItem that fits the provided EAN/GTIN or <code>null</code> if not
	 *         found
	 */
	public static @Nullable ArtikelstammItem findByEANorGTIN(@NonNull String ean){
		Query<ArtikelstammItem> qre = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		qre.add(ArtikelstammItem.FLD_GTIN, Query.LIKE, ean);
		List<ArtikelstammItem> result = qre.execute();
		if (result.size() == 1)
			return result.get(0);
		return null;
	}
	
	/**
	 * 
	 * @param pharmaCode
	 * @return the ArtikelstammItem for the given pharma code or <code>null</code> if not found
	 */
	public static @Nullable ArtikelstammItem findByPharmaCode(@NonNull String pharmaCode){
		Query<ArtikelstammItem> qre = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		qre.add(ArtikelstammItem.FLD_PHAR, Query.LIKE, pharmaCode);
		List<ArtikelstammItem> result = qre.execute();
		if (result.size() == 1)
			return result.get(0);
		
		if (!pharmaCode.startsWith(String.valueOf(0))) {
			return ArtikelstammItem.findByPharmaCode(String.valueOf(0) + pharmaCode);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return all {@link ArtikelstammItem} currently on stock
	 */
	public static List<ArtikelstammItem> getAllArticlesOnStock(){
		Query<ArtikelstammItem> qbe = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		qbe.add(MINBESTAND, Query.GREATER, StringConstants.ZERO);
		qbe.or();
		qbe.add(MAXBESTAND, Query.GREATER, StringConstants.ZERO);
		List<ArtikelstammItem> l = qbe.execute();
		return l == null ? new ArrayList<ArtikelstammItem>(0) : l;
	}
}
