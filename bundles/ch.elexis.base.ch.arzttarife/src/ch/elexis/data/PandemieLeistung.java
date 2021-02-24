/*******************************************************************************
 * Copyright (c) 2009-2020, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    T. Huster - initial implementation
 *******************************************************************************/
package ch.elexis.data;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import ch.elexis.core.data.interfaces.IFall;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

/**
 * Implementation of the swiss pandemie-tariff
 * 
 * @author thomas
 * 
 */
public class PandemieLeistung extends VerrechenbarAdapter {
	public static final String VALUE_VERSION = "VERSION";
	public static final String VERSION = "0.0.1";
	public static final String TABLENAME = "CH_ELEXIS_ARZTTARIFE_CH_PANDEMIC";
	public static final String CODESYSTEMNAME = "Pandemie";
	
	private static final String XIDDOMAIN = "www.xid.ch/id/pandemietarif";
	
	public static final String FLD_VALIDFROM = "validfrom";
	public static final String FLD_VALIDUNTIL = "validuntil";
	public static final String FLD_CODE = "code";
	public static final String FLD_PANDEMIC = "pandemic";
	public static final String FLD_CHAPTER = "chapter";
	public static final String FLD_TITLE = "title";
	public static final String FLD_ORG = "org";
	public static final String FLD_RULES = "rules";
	public static final String FLD_TAXPOINTS = "taxpoints";
	public static final String FLD_CENTS = "cents";
	public static final String FLD_DESCRIPTION = "description";
	
	private static IOptifier defaultOptifier = new DefaultOptifier();
	
	// @formatter:off
	private static final String createDB = 
			"CREATE TABLE " + TABLENAME + " ("
					+ "ID			VARCHAR(25) primary key," 
					+ "lastupdate 	BIGINT," 
					+ "deleted  	CHAR(1) default '0',"
					+ "validfrom	CHAR(8)," 
					+ "validuntil   CHAR(8)," 
					+ "code		    VARCHAR(25),"
					+ "pandemic		VARCHAR(25)," 
					+ "chapter		VARCHAR(255)," 
					+ "title		VARCHAR(255),"
					+ "org			VARCHAR(255)," 
					+ "rules		VARCHAR(255)," 
					+ "taxpoints	VARCHAR(25),"
					+ "cents		VARCHAR(25),"
					+ "description 	TEXT);" 
					+ "CREATE INDEX pandemic_code_idx on " + TABLENAME + " (code);";
	//@formatter:on
	
	static {
		addMapping(TABLENAME, FLD_VALIDFROM, FLD_VALIDUNTIL, FLD_CODE, FLD_PANDEMIC, FLD_CHAPTER,
			FLD_TITLE, FLD_ORG, FLD_RULES, FLD_TAXPOINTS, FLD_CENTS, FLD_DESCRIPTION);
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Pandemietarif", Xid.ASSIGNMENT_LOCAL);
		PandemieLeistung pv = PandemieLeistung.load(VALUE_VERSION);
		if (!pv.exists()) {
			createOrModifyTable(createDB);
			pv.create(VALUE_VERSION);
			pv.set(FLD_CODE, VERSION);
		}
	}
	
	public PandemieLeistung(String pandemie, String chapter, String code, String title,
		TimeTool validFrom){
		create(null);
		set(new String[] {
			FLD_PANDEMIC, FLD_CHAPTER, FLD_CODE, FLD_TITLE, FLD_VALIDFROM
		}, pandemie, chapter, code, title,
			validFrom != null ? validFrom.toString(TimeTool.DATE_COMPACT)
					: TimeTool.BEGINNING_OF_UNIX_EPOCH);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public double getFactor(TimeTool date, IFall fall){
		String cents = get(FLD_CENTS);
		if (StringUtils.isNotBlank(cents)) {
			return 1;
		}
		return getVKMultiplikator(date, fall);
	}
	
	public int getTP(TimeTool date, IFall fall){
		String cents = get(FLD_CENTS);
		if (StringUtils.isNotBlank(cents)) {
			return checkZero(get(FLD_CENTS));
		}
		return checkZero(get(FLD_TAXPOINTS));
	}
	
	public static PandemieLeistung load(String id){
		return new PandemieLeistung(id);
	}
	
	protected PandemieLeistung(String id){
		super(id);
	}
	
	protected PandemieLeistung(){}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	@Override
	public String getCodeSystemCode(){
		return "351";
	}
	
	@Override
	public VatInfo getVatInfo(){
		return VatInfo.VAT_NONE;
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_CODE, FLD_TITLE);
		return new StringBuilder().append(vals[0]).append(" ").append(vals[1]).toString();
	}
	
	@Override
	public String getText(){
		return StringUtils.defaultString(get(FLD_TITLE));
	}
	
	@Override
	public String getCode(){
		return StringUtils.defaultString(get(FLD_CODE));
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEMNAME;
	}
	
	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public IOptifier getOptifier(){
		return defaultOptifier;
	}
	
	@Override
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int getCacheTime(){
		return DBConnection.CACHE_TIME_MAX;
	}
	
	private TimeTool getValidFrom(){
		String value = get(FLD_VALIDFROM);
		if (!StringTool.isNothing(value)) {
			return new TimeTool(value);
		} else {
			return new TimeTool(TimeTool.BEGINNING_OF_UNIX_EPOCH);
		}
	}
	
	private TimeTool getValidTo(){
		String value = get(FLD_VALIDUNTIL);
		if (!StringTool.isNothing(value)) {
			TimeTool res = new TimeTool(value);
			res.set(TimeTool.HOUR_OF_DAY, 23);
			res.set(TimeTool.MINUTE, 59);
			res.set(TimeTool.SECOND, 59);
			return res;
		} else {
			return new TimeTool(TimeTool.END_OF_UNIX_EPOCH);
		}
	}
	
	/**
	 * Load a {@link PandemieLeistung} from code that is valid at the date.
	 * 
	 * @param code
	 * @param date
	 * @return
	 */
	public static IVerrechenbar getFromCode(final String code, TimeTool date){
		Query<PandemieLeistung> query = new Query<PandemieLeistung>(PandemieLeistung.class,
			FLD_CODE, code, TABLENAME, new String[] {
				FLD_VALIDFROM, FLD_VALIDUNTIL
			});
		List<PandemieLeistung> leistungen = query.execute();
		for (PandemieLeistung leistung : leistungen) {
			TimeTool validFrom = leistung.getValidFrom();
			TimeTool validTo = leistung.getValidTo();
			if (date.isAfterOrEqual(validFrom) && date.isBeforeOrEqual(validTo))
				return leistung;
		}
		return null;
	}
	
	public boolean isValid(TimeTool now){
		if (now.isAfterOrEqual(getValidFrom()) && now.isBeforeOrEqual(getValidTo())) {
			return true;
		}
		return false;
	}
	
	public static int getCurrentCodeVersion(){
		PandemieLeistung version = load(VALUE_VERSION);
		String currentVersion = version.get(PandemieLeistung.FLD_CHAPTER);
		if (currentVersion != null && !currentVersion.isEmpty()) {
			return Integer.parseInt(currentVersion);
		}
		return 0;
	}
	
	public static void setCurrentCodeVersion(int value){
		PandemieLeistung version = load(VALUE_VERSION);
		version.set(PandemieLeistung.FLD_CHAPTER, Integer.toString(value));
	}
}
