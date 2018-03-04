/*******************************************************************************
 * Copyright (c) 2013-2016 MEDEVIT.
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import at.medevit.ch.artikelstamm.ArtikelstammConstants;
import at.medevit.ch.artikelstamm.ArtikelstammConstants.TYPE;
import at.medevit.ch.artikelstamm.ArtikelstammHelper;
import at.medevit.ch.artikelstamm.BlackBoxReason;
import at.medevit.ch.artikelstamm.DATASOURCEType;
import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.preference.MargePreference;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.interfaces.IFall;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.jdt.NonNull;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.ui.optifier.NoObligationOptifier;
import ch.elexis.data.Artikel;
import ch.elexis.data.DBConnection;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
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
	private static final String VERSION_ENTRY_ID = "VERSION";
	static final String VERSION = "1.3.0";
	private static int IS_USER_DEFINED_PKG_SIZE = -999999;
	
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
	/** Produkt-Nummer */ 	  	public static final String FLD_PRODNO = "PRODNO";
	
	public static final String EXTINFO_VAL_VAT_OVERRIDEN = "VAT_OVERRIDE";
	public static final String EXTINFO_VAL_PPUB_OVERRIDE_STORE ="PPUB_OVERRIDE_STORE";
	public static final String EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE ="PKG_SIZE_OVERRIDE_STORE";
	
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
			+ FLD_DSCR	+" VARCHAR(100)," // id(VERSION) creation date of current P dataset
			+ FLD_ADDDSCR	+" VARCHAR(50)," // id(VERSION) creation date of current N dataset
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
			+ VERKAUFSEINHEIT + " VARCHAR(4),"  // Stück pro Abgabe
			+ FLD_PRODNO+" VARCHAR(10),"
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
	
	static final String dbUpdateFrom11to12 =
		"ALTER TABLE "+TABLENAME+" ADD "+FLD_PRODNO+" VARCHAR(10);";
	
	static final String dbUpdateFrom12to13 =
			"ALTER TABLE "+TABLENAME+" MODIFY "+FLD_DSCR+" VARCHAR(100);";
	
	//@formatter:on
	
	static {
		/**
		 * FLD_ITEM_TYPE, FLD_PHAR, FLD_PEXF, FLD_PPUB have two mappings on purpose! The mappings
		 * with the Artikel-Fields are essential in order to make the ArtikelDetailDialog work with
		 * ArtikelstammItems
		 */
		addMapping(TABLENAME, FLD_ITEM_TYPE, Artikel.FLD_TYP + "=" + FLD_ITEM_TYPE,
			FLD_CUMMULATED_VERSION, FLD_BLACKBOXED, FLD_GTIN, FLD_PHAR,
			Artikel.FLD_PHARMACODE + "=" + FLD_PHAR, FLD_DSCR, FLD_ADDDSCR, FLD_ATC, FLD_COMP_GLN,
			FLD_COMP_NAME, FLD_PEXF, Artikel.FLD_EK_PREIS + "=" + FLD_PEXF, FLD_PPUB,
			Artikel.FLD_VK_PREIS + "=" + FLD_PPUB, FLD_PKG_SIZE, FLD_SL_ENTRY, FLD_IKSCAT,
			FLD_LIMITATION, FLD_LIMITATION_PTS, FLD_LIMITATION_TEXT, FLD_GENERIC_TYPE,
			FLD_HAS_GENERIC, FLD_LPPV, FLD_DEDUCTIBLE, FLD_NARCOTIC, FLD_NARCOTIC_CAS, FLD_VACCINE,
			VERKAUFSEINHEIT, FLD_PRODNO, Artikel.LIEFERANT_ID, Artikel.MINBESTAND,
			Artikel.ISTBESTAND, Artikel.MAXBESTAND, PersistentObject.FLD_EXTINFO);
		ArtikelstammItem version = load(VERSION_ENTRY_ID);
		if (!version.exists()) {
			createOrModifyTable(createDB);
		} else {
			VersionInfo vi = new VersionInfo(version.get(FLD_GTIN));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder("1.1.0")) {
					createOrModifyTable(dbUpdateFrom10to11);
					version.set(FLD_GTIN, "1.1.0");
				}
				if (vi.isOlder("1.2.0")) {
					createOrModifyTable(dbUpdateFrom11to12);
					version.set(FLD_GTIN, VERSION);
				}
				if (vi.isOlder("1.3.0")) {
					createOrModifyTable(dbUpdateFrom12to13);
					version.set(FLD_GTIN, VERSION);
				}
			}
		}
		
		Artikel.transferAllStockInformationToNew32StockModel(
			new Query<ArtikelstammItem>(ArtikelstammItem.class), ArtikelstammItem.class);
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_DSCR, FLD_ADDDSCR);
		
		return (vals[1].length() > 0) ? vals[0] + " (" + vals[1] + ")" : vals[0];
	}
	
	@Override
	protected String getTableName(){
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
	 *            the item type: P (Pharma), N (NonPharma) or X (Product)
	 * @param gtin
	 *            global trade index number
	 * @param code
	 *            pharmacode if type P or N, if X the Product Number (PRODNO)
	 * @param dscr
	 *            description
	 * @param addscr
	 *            additional description
	 */
	public ArtikelstammItem(int version, TYPE type, String gtin, BigInteger code, String dscr,
		String addscr){
		
		if (dscr.length() > 100) {
			dscr = dscr.substring(0, 100);
			log.warn("Delimiting dscr to 100 chars for [{}] info [{}]", dscr,
				type + "/" + version + "/" + code);
		}
		
		if (TYPE.X == type) {
			// Product
			create(gtin);
			set(new String[] {
				FLD_ITEM_TYPE, FLD_DSCR, FLD_CUMMULATED_VERSION, FLD_BLACKBOXED
			}, type.name(), dscr, Integer.toString(version), StringConstants.ZERO);
		} else {
			code = (code != null) ? code : BigInteger.ZERO;
			String pharmaCode = String.format("%07d", code);
			
			create(ArtikelstammHelper.createUUID(version, gtin, code));
			
			set(new String[] {
				FLD_ITEM_TYPE, FLD_GTIN, FLD_PHAR, FLD_DSCR, FLD_ADDDSCR, FLD_CUMMULATED_VERSION,
				FLD_BLACKBOXED
			}, type.name(), gtin, pharmaCode, dscr, addscr, Integer.toString(version),
				StringConstants.ZERO);
		}
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
	public boolean isProduct(){
		return (TYPE.X == getType());
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
	public int getAbgabeEinheit(){
		int val = 0;
		try {
			val = Integer.parseInt(get(VERKAUFSEINHEIT));
		} catch (NumberFormatException nfe) {}
		return val;
	}
	
	@Override
	public String getPackungsGroesseDesc(){
		return checkNull(get(FLD_ADDDSCR));
	}
	
	@Override
	public Money getKosten(final TimeTool dat){
		double vkt = checkZeroDouble(getTP(dat, (IFall) null) + "");
		double vpe = checkZeroDouble(get(FLD_PKG_SIZE));
		double vke = checkZeroDouble(get(VERKAUFSEINHEIT));
		if (vpe != vke) {
			return new Money((int) Math.round(vke * (vkt / vpe)));
		} else {
			return new Money((int) Math.round(vkt));
		}
	}
	
	@Override
	public int getVerpackungsEinheit(){
		return Math.abs(checkZero(get(FLD_PKG_SIZE)));
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
	
	@Override
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
		
	/**
	 * De-/activate the manual price override.
	 * 
	 * @param activate
	 */
	public void setUserDefinedPrice(boolean activate){
		if (activate) {
			String ppub = get(FLD_PPUB);
			setExtInfoStoredObjectByKey(EXTINFO_VAL_PPUB_OVERRIDE_STORE, ppub);
			double value = 0;
			try {
				value = Double.valueOf(ppub);
			} catch (NumberFormatException nfe) {
				log.error("Error #setUserDefinedPrice [{}] value is [{}], setting 0", getId(),
					ppub);
			}
			setUserDefinedPriceValue(value);
		} else {
			String ppubStored =
				(String) getExtInfoStoredObjectByKey(EXTINFO_VAL_PPUB_OVERRIDE_STORE);
			set(FLD_PPUB, ppubStored);
			setExtInfoStoredObjectByKey(EXTINFO_VAL_PPUB_OVERRIDE_STORE, null);
		}
	}
	
	/**
	 * Set the price as user-defined (i.e. overridden) price. This will internally store the price
	 * as negative value.
	 * 
	 * @param value
	 */
	public void setUserDefinedPriceValue(Double value){
		if (value < 0) {
			throw new IllegalArgumentException("value must not be lower than 0");
		}
		set(FLD_PPUB, "-" + value);
	}
	
	/**
	 * @return the overridden public price value if overridden and not null. If the price
	 * was not overridden, also <code>null</code> is returned.
	 */
	public Double getUserDefinedPriceValue(){
		String ppub = get(FLD_PPUB);
		if (ppub != null && ppub.startsWith("-")) {
			try {
				return Double.valueOf(ppub);
			} catch (NumberFormatException nfe) {
				log.error("Error #getUserDefinedPrice [{}] value is [{}], setting 0", getId(),
					ppub);
			}
		}
		return null;
	}
	
	@Override
	public boolean isUserDefinedPrice(){
		return getUserDefinedPriceValue() != null;
	}
	
	public void overrideVatInfo(VatInfo overridenVat){
		VatInfo originalVatInfo = getOriginalVatInfo();
		if (overridenVat == originalVatInfo) {
			setExtInfoStoredObjectByKey(EXTINFO_VAL_VAT_OVERRIDEN, null);
		} else {
			setExtInfoStoredObjectByKey(EXTINFO_VAL_VAT_OVERRIDEN, overridenVat.toString());
		}
	}
	
	/**
	 * Ticket #3050 - need to override for ch.elexis.data.XMLExporter#849 to return correct
	 * obligation type
	 */
	@Override
	public String getExt(String name){
		// "Kassentyp" = ch.elexis.artikel_ch.data. MedikamentImporter.KASSENTYP
		if (name.equals("Kassentyp")) {
			return isInSLList() ? StringConstants.ONE : StringConstants.ZERO;
		}
		return super.getExt(name);
	}
	
	private VatInfo getOriginalVatInfo(){
		switch (getType()) {
		case P:
			return VatInfo.VAT_CH_ISMEDICAMENT;
		case N:
			return VatInfo.VAT_CH_NOTMEDICAMENT;
		case X:
		}
		return VatInfo.VAT_NONE;
	}
	
	// -- VERRECHENBAR ADAPTER and ARTIKEL ---
	@Override
	public VatInfo getVatInfo(){
		String overridenVat = (String) getExtInfoStoredObjectByKey(EXTINFO_VAL_VAT_OVERRIDEN);
		if (overridenVat != null) {
			return VatInfo.valueOf(overridenVat);
		}
		
		return getOriginalVatInfo();
	}
	
	@Override
	public IOptifier getOptifier(){
		if (!isInSLList()) {
			return noObligationOptifier;
		}
		return defaultOptifier;
	}
	
	@Override
	public int getPreis(TimeTool dat, IFall fall){
		double vkPreis = checkZeroDouble(getVKPreis().getCentsAsString());
		double pkgSize = checkZeroDouble(get(FLD_PKG_SIZE));
		double vkUnits = checkZeroDouble(get(VERKAUFSEINHEIT));
		if ((pkgSize > ZERO) && (vkUnits > ZERO) && (pkgSize != vkUnits)) {
			return (int) Math.round(vkUnits * (vkPreis / pkgSize));
		} else {
			return (int) Math.round(vkPreis);
		}
	}
	
	@Override
	public int getTP(TimeTool date, IFall fall){
		return getPreis(date, fall);
	}
	
	@Override
	public double getFactor(TimeTool date, IFall fall){
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
		String gtin = getGTIN();
		if (gtin != null && gtin.length() > 3) {
			if (getType() == TYPE.P) {
				return "402";
			} else if (getType() == TYPE.N) {
				return "406";
			}
		}
		return super.getCodeSystemCode();
	}
	
	@Override
	public String getCode(){
		String gtin = getGTIN();
		if (gtin != null && gtin.length() > 3) {
			return gtin;
		}
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
	
	@Override
	public int getVerkaufseinheit(){
		return checkZero(get(VERKAUFSEINHEIT));
	}
	
	// --------------------
	
	/**
	 * 
	 * @return the data source this set is populated from
	 * @throws IllegalArgumentException
	 *             if not yet set
	 * @since 3.3
	 */
	public static DATASOURCEType getDatasourceType(){
		String dst = load(VERSION_ENTRY_ID).get(FLD_ADDDSCR);
		return DATASOURCEType.fromValue(dst);
	}
	
	/**
	 * Set the data-source information. Do execute only ONCE!
	 * 
	 * @param datasource
	 * @since 3.3
	 */
	public static void setDataSourceType(DATASOURCEType datasource){
		log.info("Setting data source type [{}]", datasource.value());
		load(VERSION_ENTRY_ID).set(FLD_ADDDSCR, datasource.value());
	}
	
	/**
	 * @param stammType
	 * @return The version of the current imported data-set
	 */
	public static int getCurrentVersion(){
		ArtikelstammItem version = load(VERSION_ENTRY_ID);
		return version.getInt(FLD_PPUB);
	}
	
	public static void setCurrentVersion(int importStammVersion){
		ArtikelstammItem version = load(VERSION_ENTRY_ID);
		version.setInt(FLD_PPUB, importStammVersion);
	}
	
	public static void setImportSetCreationDate(Date creationDate){
		ArtikelstammItem version = load(VERSION_ENTRY_ID);
		version.set(FLD_DSCR, df.format(creationDate.getTime()));
	}
	
	public static @Nullable Date getImportSetCreationDate(){
		ArtikelstammItem version = load(VERSION_ENTRY_ID);
		try {
			return df.parse(version.get(FLD_DSCR));
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static boolean purgeEntries(List<ArtikelstammItem> list){
		if (list.size() == 0) {
			return true;
		}
		String string = list.stream().map(o -> o.getWrappedId())
			.reduce((u, t) -> u + StringConstants.COMMA + t).get();
		
		Stm stm = getConnection().getStatement();
		stm.exec("DELETE FROM " + TABLENAME + " WHERE ID IN (" + string + ")");
		getConnection().releaseStatement(stm);
		
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
		return !(StringConstants.ZERO.equals(val));
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
	
	public void setATCCode(String ATC_code){
		set(FLD_ATC, ATC_code);
	}
	
	@Override
	public TYPE getType(){
		try {
			String string = get(FLD_ITEM_TYPE);
			if (string != null && string.length() > 0) {
				return Enum.valueOf(TYPE.class, Character.toString(string.charAt(0)).toUpperCase());
			}
			log.warn("No TYPE argument for " + getId() + " found, defaulting to N (NonPharma).");
			return TYPE.N;
		} catch (IllegalArgumentException iae) {
			// be more resilient on wrong database entries (e.g. #3193)
			log.error("Invalid TYPE argument for " + getId() + ": " + get(FLD_ITEM_TYPE));
			return null;
		}
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
		if (isUserDefinedPrice()) {
			setUserDefinedPriceValue(amount);
		} else {
			set(FLD_PPUB, Double.toString(amount));
		}
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
		String val = get(FLD_DEDUCTIBLE);
		if (val == null || val.length() < 1)
			if (isInSLList()) {
				return 0;
			} else {
				return -1;
			}
		try {
			return new Integer(val.trim());
		} catch (NumberFormatException nfe) {
			log.warn("Error parsing deductible [{}]", val.trim());
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
	
	@Override
	public boolean isUserDefinedPkgSize(){
		return getUserDefinedPkgSize() != IS_USER_DEFINED_PKG_SIZE;
	}

	@Override
	public void setUserDefinedPkgSize(boolean activate) {
		if (activate) {
			int value = IS_USER_DEFINED_PKG_SIZE;
			String pkgSize = get(FLD_PKG_SIZE);
			try {
				value = new Integer(pkgSize.trim());
			} catch (NumberFormatException nfe) {
				log.error("Error #setUserDefinedPrice [{}] value is [{}], setting 0", getId(),
					pkgSize);
				pkgSize = "0";
			}
			setExtInfoStoredObjectByKey(EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE, pkgSize);
			setUserDefinedPkgSizeValue(value);

		} else {
			String ppubStored =
				(String) getExtInfoStoredObjectByKey(EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE);
			set(FLD_PKG_SIZE, ppubStored);
			setExtInfoStoredObjectByKey(EXTINFO_VAL_PKG_SIZE_OVERRIDE_STORE, null);
		}
	}
	/**
	 * Set the price as user-defined (i.e. overridden) price. This will internally store the price
	 * as negative value.
	 * 
	 * @param value
	 */
	public void setUserDefinedPkgSizeValue(int value){
		if (value < 0) {
			throw new IllegalArgumentException("value must not be lower than 0");
		}
		log.debug("setUserDefinedPkgSizeValue Verpackungseinheit gtin  {}  value {}", getGTIN(), value);
		set(FLD_PKG_SIZE, "-" + value);
	}
	

	/**
	 * @return the overridden public price value if overridden and not null. If the price
	 * was not overridden, also <code>null</code> is returned.
	 */
	public int getUserDefinedPkgSize(){
		String oldValue  = get(FLD_PKG_SIZE);
		if (oldValue != null && oldValue.startsWith("-")) {
			try {
				return -(new Integer(oldValue.trim()));
			} catch (NumberFormatException nfe) {
				log.error("Error #getUserDefinedVerpackungseinheit [{}] value is [{}], setting 0", getId(),
					oldValue);
				return IS_USER_DEFINED_PKG_SIZE;
			}
		}
		return IS_USER_DEFINED_PKG_SIZE;
	}
	
	public int getVerpackungseinheit(){
		return Math.abs(getVerpackungsEinheit());
	}
	
	public void setVerpackungseinheit(int vpe){
		log.debug("phar {} gtin {} setVerpackungseinheit {} is {} ", getPHAR(), getGTIN(), vpe, getVerpackungseinheit());
		if (vpe != getVerpackungseinheit()) {
			setUserDefinedPkgSizeValue(vpe);
		} else {
			set(FLD_PKG_SIZE, vpe + "");
		}
	}
	
	@Override
	public void setVerkaufseinheit(int vse){
		set(VERKAUFSEINHEIT, vse + "");
	}
	
	public static int findNumberOfArtikelstammItemsForATCWildcard(String atcCode){
		return PersistentObject.getConnection()
			.queryInt("SELECT COUNT(*) FROM " + ArtikelstammItem.TABLENAME + " WHERE "
				+ ArtikelstammItem.FLD_ATC + " " + Query.LIKE + " " + JdbcLink.wrap(atcCode + "%"));
	}
	
	/**
	 * @return alternative {@link ArtikelstammItem} objects that match the ATC code
	 */
	public @NonNull List<ArtikelstammItem> getAlternativeArticlesByATCGroup(){
		String code = getATC_code();
		if (code == null || code.length() < 1) {
			return Collections.emptyList();
		}
		
		Query<ArtikelstammItem> qre = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		qre.add(ArtikelstammItem.FLD_ATC, Query.EQUALS, code);
		return qre.execute();
	}
	
	/**
	 * @param ean
	 *            the European Article Number or GTIN
	 * @return the ArtikelstammItem that fits the provided EAN/GTIN or <code>null</code> if none or
	 *         multiple found
	 */
	public static @Nullable ArtikelstammItem findByEANorGTIN(@NonNull String ean){
		Query<ArtikelstammItem> qre = new Query<ArtikelstammItem>(ArtikelstammItem.class);
		qre.add(ArtikelstammItem.FLD_GTIN, Query.LIKE, ean);
		qre.add(ArtikelstammItem.FLD_BLACKBOXED, Query.EQUALS,
			Integer.toString(BlackBoxReason.NOT_BLACKBOXED.getNumercialReason()));
		List<ArtikelstammItem> result = qre.execute();
		if (result.size() == 1) {
			return result.get(0);
		}
		
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
	
	@Override
	public int getCacheTime(){
		return DBConnection.CACHE_TIME_MAX;
	}

	@Override
	public String getProductId(){
		if (isProduct()) {
			return getId();
		}
		return checkNull(get(FLD_PRODNO));
	}
	
}
