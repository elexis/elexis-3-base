/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.labortarif2009.data;

import java.util.List;

import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.ui.data.UiVerrechenbarAdapter;
import ch.elexis.data.Fall;
import ch.elexis.data.Query;
import ch.elexis.data.Xid;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class Labor2009Tarif extends UiVerrechenbarAdapter {
	public static final String FLD_GUELTIG_BIS = "GueltigBis";
	public static final String FLD_GUELTIG_VON = "GueltigVon";
	public static final String CODESYSTEM_CODE_LAB2009 = "317"; //$NON-NLS-1$
	public static final String CODESYSTEM_CODE_TARMED = "300"; //$NON-NLS-1$
	public static final String MULTIPLICATOR_NAME = "EAL2009"; //$NON-NLS-1$
	public static final String CODESYSTEM_NAME = "EAL 2009"; //$NON-NLS-1$
	public static final String FLD_FACHBEREICH = "fachbereich"; //$NON-NLS-1$
	public static final String FLD_LIMITATIO = "limitatio"; //$NON-NLS-1$
	public static final String FLD_NAME = "name"; //$NON-NLS-1$
	public static final String FLD_TP = "tp"; //$NON-NLS-1$
	public static final String FLD_CODE = "code"; //$NON-NLS-1$
	public static final String FLD_CHAPTER = "chapter"; //$NON-NLS-1$
	public static final String FLD_FACHSPEC = "praxistyp"; //$NON-NLS-1$
	public static final String XIDDOMAIN = "www.xid.ch/id/analysenliste_ch2009/"; //$NON-NLS-1$
	private final static String TABLENAME = "CH_MEDELEXIS_LABORTARIF2009"; //$NON-NLS-1$
	public static final String VERSION010 = "0.1.0"; //$NON-NLS-1$
	public static final String VERSION = "0.1.1"; //$NON-NLS-1$
	
	// @formatter:off
	private static final String createTable = "create table " + TABLENAME + "(" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ID		VARCHAR(25) primary key," //$NON-NLS-1$
		+ "lastupdate BIGINT," //$NON-NLS-1$
		+ "deleted	 CHAR(1) default '0'," //$NON-NLS-1$
		+ "chapter   VARCHAR(10)," //$NON-NLS-1$
		+ "code		 VARCHAR(12)," //$NON-NLS-1$
		+ "tp		 VARCHAR(10)," //$NON-NLS-1$
		+ "name		 VARCHAR(255)," //$NON-NLS-1$
		+ "limitatio TEXT," //$NON-NLS-1$
		+ "fachbereich VARCHAR(10)," //$NON-NLS-1$
		+ "GueltigVon CHAR(8)," //$NON-NLS-1$
		+ "GueltigBis CHAR(8)," //$NON-NLS-1$
		+ "praxistyp VARCHAR(2));" //$NON-NLS-1$
		+ "INSERT INTO " + TABLENAME + "(ID,code) VALUES (1,'" + VERSION + "');"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	// @formatter:on
	
	private static final IOptifier l09optifier = new Optifier();
	
	private static TimeTool future = new TimeTool("01.01.9999"); //$NON-NLS-1$

	static {
		createTable();
	}
	
	static void createTable(){
		addMapping(TABLENAME, FLD_CHAPTER, FLD_CODE, FLD_TP, FLD_NAME, FLD_LIMITATIO,
			FLD_FACHBEREICH, FLD_FACHSPEC, FLD_GUELTIG_BIS, FLD_GUELTIG_VON);
		Labor2009Tarif version = load("1"); //$NON-NLS-1$
		if (!version.exists()) {
			createOrModifyTable(createTable);
		} else if (version.get(FLD_CODE).equals(VERSION010)) {
			createOrModifyTable("ALTER TABLE " + TABLENAME
				+ " ADD GueltigVon CHAR(8); ALTER TABLE " + TABLENAME + " ADD GueltigBis CHAR(8);");
			version.set(FLD_CODE, VERSION);
		}
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN,
			"Analysenliste 2009", Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
	}
	
	/** Only needed by the importer */
	Labor2009Tarif(String chapter, String code, String tp, String name, String lim, String fach,
		int fachspec){
		create(null);
		set(new String[] {
			FLD_CHAPTER, FLD_CODE, FLD_TP, FLD_NAME, FLD_LIMITATIO, FLD_FACHBEREICH, FLD_FACHSPEC
		}, chapter, code, tp, name, lim, fach, Integer.toString(fachspec));
	}
	
	@Override
	public String getLabel(){
		String code = getCode();
		if (!StringTool.isNothing(code)) {
			StringBuilder sb = new StringBuilder(code).append(" ").append(getText()) //$NON-NLS-1$
				.append(" (").append(get(FLD_FACHBEREICH)).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
			
			TimeTool validFrom = null;
			TimeTool validTo = null;
			String validFromString = get(Labor2009Tarif.FLD_GUELTIG_VON);
			String validToString = get(Labor2009Tarif.FLD_GUELTIG_BIS);
			if (validFromString != null && validFromString.trim().length() > 0) {
				validFrom = new TimeTool(validFromString);
			}
			if (validToString != null && validToString.trim().length() > 0) {
				validTo = new TimeTool(validToString);
			}
			
			if (validFrom != null) {
				sb.append(" (").append(validFrom.toString(TimeTool.DATE_GER));
				if (validTo != null) {
					sb.append("-").append(validTo.toString(TimeTool.DATE_GER)).append(")");
				} else {
					sb.append("-").append(" ").append(")");
				}
			}
			
			return sb.toString();
		} else {
			return "?"; //$NON-NLS-1$
		}
	}
	
	@Override
	public String getCode(){
		return get(FLD_CODE);
	}
	
	@Override
	public String getText(){
		return StringTool.getFirstLine(get(FLD_NAME), 80);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public static Labor2009Tarif load(final String id){
		return new Labor2009Tarif(id);
	}
	
	protected Labor2009Tarif(final String id){
		super(id);
	}
	
	public Labor2009Tarif(){}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	public double getFactor(TimeTool date, Fall fall){
		double ret = getVKMultiplikator(date, MULTIPLICATOR_NAME);
		return ret;
	}
	
	public int getTP(TimeTool date, Fall fall){
		double tp = checkZeroDouble(get(FLD_TP));
		return (int) Math.round(tp * 100.0);
	}
	
	public boolean isValidOn(TimeTool date){
		String validFromString = get(FLD_GUELTIG_VON);
		String validToString = get(FLD_GUELTIG_BIS);
		if (validFromString != null && validFromString.trim().length() > 0) {
			TimeTool validFrom = new TimeTool(validFromString);
			if (validFrom.after(date))
				return false;
		}
		if (validToString != null && validToString.trim().length() > 0) {
			TimeTool validTo = new TimeTool(validToString);
			if (validTo.before(date) || validTo.equals(date))
				return false;
		}
		return true;
	}
	
	public static int getCurrentCodeVersion(){
		Labor2009Tarif version = load("1");
		String currentVersion = version.get(Labor2009Tarif.FLD_CHAPTER);
		if (currentVersion != null && !currentVersion.isEmpty()) {
			return Integer.parseInt(currentVersion);
		}
		return 0;
	}
	
	public static void setCurrentCodeVersion(int value){
		Labor2009Tarif version = load("1");
		version.set(Labor2009Tarif.FLD_CHAPTER, Integer.toString(value));
	}

	@Override
	public boolean isDragOK(){
		return true;
	}
	
	@Override
	public String getCodeSystemName(){
		return CODESYSTEM_NAME;
	}
	
	public String getCodeSystemCode(){
		return CODESYSTEM_CODE_LAB2009;
	}
	
	@Override
	public IOptifier getOptifier(){
		return l09optifier;
	}
	
	@Override
	public VatInfo getVatInfo(){
		// Code Mehrwertsteuer (CMWS) - 1stellig
		// 1: voller MWSt-Satz (zur Zeit 6.5%)
		// 2: reduzierter MWSt-Satz (zur Zeit 2%)
		// 3: von der MWSt befreit
		return VatInfo.VAT_NONE;
	}
	
	public static Labor2009Tarif getFromCode(String code, TimeTool date){
		Query<Labor2009Tarif> query = new Query<Labor2009Tarif>(Labor2009Tarif.class);
		query.add(Labor2009Tarif.FLD_CODE, "=", code);
		List<Labor2009Tarif> leistungen = query.execute();
		for (Labor2009Tarif laborLeistung : leistungen) {
			TimeTool validFrom = laborLeistung.getGueltigVon();
			TimeTool validTo = laborLeistung.getGueltigBis();
			if (validTo == null) {
				validTo = future;
			}
			if (date.isAfterOrEqual(validFrom) && date.isBeforeOrEqual(validTo))
				return laborLeistung;
		}
		return null;
	}
	
	private TimeTool getGueltigBis(){
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
	
	private TimeTool getGueltigVon(){
		String value = get(FLD_GUELTIG_VON);
		if (!StringTool.isNothing(value)) {
			return new TimeTool(value);
		} else {
			return null;
		}
	}
}