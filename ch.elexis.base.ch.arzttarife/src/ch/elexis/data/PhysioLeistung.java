/*******************************************************************************
 * Copyright (c) 2009-2011, G. Weirich, medshare and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.data;

import java.util.List;

import ch.elexis.core.data.interfaces.IOptifier;
import ch.elexis.core.ui.optifier.NoObligationOptifier;
import ch.elexis.data.Fall;
import ch.elexis.data.VerrechenbarAdapter;
import ch.elexis.data.Xid;
import ch.rgw.tools.TimeTool;

/**
 * Implementation of the swiss physiotherapy-tariff
 * 
 * @author gerry
 * 
 */
public class PhysioLeistung extends VerrechenbarAdapter {
	public static final String FLD_BIS = "bis";
	public static final String FLD_VON = "von";
	public static final String FLD_TP = "TP";
	private static final String VALUE_VERSION = "VERSION";
	public static final String FLD_TITEL = "Titel";
	public static final String FLD_ZIFFER = "Ziffer";
	public static final String VERSION = "0.0.1";
	private static final String TABLENAME = "CH_ELEXIS_ARZTTARIFE_CH_PHYSIO";
	private static final String XIDDOMAIN = "www.xid.ch/id/physiotarif";
	public static final String CODESYSTEMNAME = "Physiotherapie";
	
	private static IOptifier noObligationOptifier = new NoObligationOptifier();
	
	private static final String createDB = "CREATE TABLE " + TABLENAME + " ("
		+ "ID			VARCHAR(25) primary key," + "lastupdate BIGINT," + "deleted  CHAR(1) default '0',"
		+ "validFrom	CHAR(8)," + "validUntil CHAR(8)," + "TP CHAR(8)," + "ziffer		VARCHAR(6),"
		+ "titel		VARCHAR(255)," + "description TEXT);" + "CREATE INDEX cheacp on " + TABLENAME
		+ " (ziffer);";
	
	static {
		addMapping(TABLENAME, "von=S:D:validFrom", "bis=S:D:validUntil", FLD_ZIFFER, FLD_TITEL,
			"text=description", FLD_TP);
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN, "Physiotarif", Xid.ASSIGNMENT_LOCAL);
		PhysioLeistung pv = PhysioLeistung.load(VALUE_VERSION);
		if (!pv.exists()) {
			createOrModifyTable(createDB);
			pv.create(VALUE_VERSION);
			pv.set(FLD_ZIFFER, VERSION);
		}
	}
	
	public PhysioLeistung(String code, String text, String tp, String validFrom, String validUntil){
		create(null);
		set(new String[] {
			FLD_ZIFFER, FLD_TITEL, FLD_TP, FLD_VON, FLD_BIS
		}, code, text, tp, TimeTool.BEGINNING_OF_UNIX_EPOCH, TimeTool.END_OF_UNIX_EPOCH);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public String[] getDisplayedFields(){
		return new String[] {
			FLD_ZIFFER, FLD_TITEL
		};
	}
	
	public double getFactor(TimeTool date, Fall fall){
		return getVKMultiplikator(date, fall);
	}
	
	public int getTP(TimeTool date, Fall fall){
		return checkZero(get(FLD_TP));
	}
	
	public static PhysioLeistung load(String id){
		return new PhysioLeistung(id);
	}
	
	protected PhysioLeistung(String id){
		super(id);
	}
	
	protected PhysioLeistung(){}
	
	public String getXidDomain(){
		return XIDDOMAIN;
	}
	
	@Override
	public String getCodeSystemCode(){
		return "311";
	}
	
	@Override
	public String getLabel(){
		String[] vals = get(true, FLD_ZIFFER, FLD_TITEL);
		return new StringBuilder().append(vals[0]).append(" ").append(vals[1]).toString();
	}
	
	@Override
	public String getText(){
		return get(FLD_TITEL);
	}
	
	@Override
	public String getCode(){
		return get(FLD_ZIFFER);
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
		return noObligationOptifier;
	}
	
	@Override
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}
}
