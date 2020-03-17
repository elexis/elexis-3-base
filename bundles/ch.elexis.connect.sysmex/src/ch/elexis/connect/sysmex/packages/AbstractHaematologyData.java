package ch.elexis.connect.sysmex.packages;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public abstract class AbstractHaematologyData implements IProbe {
	private TimeTool date;
	private String patId;
	private String wbc;
	private String rbc;
	private String hgb;
	private String hct;
	private String mcv;
	private String mch;
	private String mchc;
	private String plt;
	private String lym_percent;
	private String mxd_percent;
	private String neut_percent;
	private String lym_volume;
	private String mxd_volume;
	private String neut_volume;
	private String rdw_sd;
	private String rdw_cv;
	private String pdw;
	private String mpv;
	private String p_lcr;
	
	/**
	 * Liest Probendaten aus Array
	 * 
	 */
	public void parse(final String content){
		// Datum
		date = getDate(content);
		patId = getPatientId(content);
		
		wbc = getWBC(content);
		rbc = getRBC(content);
		hgb = getHGB(content);
		hct = getHCT(content);
		mcv = getMCV(content);
		mch = getMCH(content);
		mchc = getMCHC(content);
		plt = getPLT(content);
		lym_percent = getLYMPercent(content);
		mxd_percent = getMXDPercent(content);
		neut_percent = getNEUTPercent(content);
		lym_volume = getLYMVolume(content);
		mxd_volume = getMXDVolume(content);
		neut_volume = getNEUTVolume(content);
		rdw_sd = getRDWSD(content);
		rdw_cv = getRDWCV(content);
		pdw = getPDW(content);
		mpv = getMPV(content);
		p_lcr = getPLCR(content);
	}
	
	/**
	 * Schreibt Labordaten
	 */
	public void write(Patient patient) throws PackageException{
		
		Value wbcVal = getValue("WBC"); //$NON-NLS-1$
		wbcVal.fetchValue(patient, wbc, 0, getDate()); //$NON-NLS-1$
		
		Value rbcVal = getValue("RBC"); //$NON-NLS-1$
		rbcVal.fetchValue(patient, rbc, 0, getDate()); //$NON-NLS-1$
		
		Value hgbVal = getValue("HGB"); //$NON-NLS-1$
		hgbVal.fetchValue(patient, hgb, 0, getDate()); //$NON-NLS-1$
		
		Value hctVal = getValue("HCT"); //$NON-NLS-1$
		hctVal.fetchValue(patient, hct, 0, getDate()); //$NON-NLS-1$
		
		Value mcvVal = getValue("MCV"); //$NON-NLS-1$
		mcvVal.fetchValue(patient, mcv, 0, getDate()); //$NON-NLS-1$
		
		Value mchVal = getValue("MCH"); //$NON-NLS-1$
		mchVal.fetchValue(patient, mch, 0, getDate()); //$NON-NLS-1$
		
		Value mchcVal = getValue("MCHC"); //$NON-NLS-1$
		mchcVal.fetchValue(patient, mchc, 0, getDate()); //$NON-NLS-1$
		
		Value pltVal = getValue("PLT"); //$NON-NLS-1$
		pltVal.fetchValue(patient, plt, 0, getDate()); //$NON-NLS-1$
		
		Value lymPercentVal = getValue("LYM%"); //$NON-NLS-1$
		lymPercentVal.fetchValue(patient, lym_percent, 0, getDate()); //$NON-NLS-1$
		
		Value mxdPercentVal = getValue("MXD%"); //$NON-NLS-1$
		mxdPercentVal.fetchValue(patient, mxd_percent, 0, getDate()); //$NON-NLS-1$
		
		Value neutPercentVal = getValue("NEUT%"); //$NON-NLS-1$
		neutPercentVal.fetchValue(patient, neut_percent, 0, getDate()); //$NON-NLS-1$
		
		Value lymVolumeVal = getValue("LYM#"); //$NON-NLS-1$
		lymVolumeVal.fetchValue(patient, lym_volume, 0, getDate()); //$NON-NLS-1$
		
		Value mxdVolumeVal = getValue("MXD#"); //$NON-NLS-1$
		mxdVolumeVal.fetchValue(patient, mxd_volume, 0, getDate()); //$NON-NLS-1$
		
		Value neutVolumeVal = getValue("NEUT#"); //$NON-NLS-1$
		neutVolumeVal.fetchValue(patient, neut_volume, 0, getDate()); //$NON-NLS-1$
		
		if (rdw_sd != null) {
			Value rdwSdVal = getValue("RDW-SD"); //$NON-NLS-1$
			rdwSdVal.fetchValue(patient, rdw_sd, 0, getDate()); //$NON-NLS-1$
		}
		
		if (rdw_cv != null) {
			Value rdwCvVal = getValue("RDW-CV"); //$NON-NLS-1$
			rdwCvVal.fetchValue(patient, rdw_cv, 0, getDate()); //$NON-NLS-1$
		}
		
		Value pdwVal = getValue("PDW"); //$NON-NLS-1$
		pdwVal.fetchValue(patient, pdw, 0, getDate()); //$NON-NLS-1$
		
		Value mpvVal = getValue("MPV"); //$NON-NLS-1$
		mpvVal.fetchValue(patient, mpv, 0, getDate()); //$NON-NLS-1$
		
		Value pLcrVal = getValue("P-LCR"); //$NON-NLS-1$
		pLcrVal.fetchValue(patient, p_lcr, 0, getDate()); //$NON-NLS-1$
	}
	
	public TimeTool getDate(){
		return date;
	}
	
	public String getPatientId(){
		return patId;
	}
	
	protected String getValueStr(final String content, final int pos, String pattern){
		int l1 = 0;
		int l2 = 0;
		
		// Create Pattern
		String[] parts = pattern.split("\\."); //$NON-NLS-1$
		String decimalPattern = parts[0].replace('X', '#').replace('F', ' ').trim();
		decimalPattern = decimalPattern.substring(0, decimalPattern.length() - 1) + "0";
		l1 = decimalPattern.length();
		if (parts.length > 1) {
			decimalPattern += "." //$NON-NLS-1$
				+ parts[1].replace('X', '0').replace('F', ' ').trim();
			l2 = decimalPattern.length() - l1 - 1;
		}
		
		// Read content
		String strValue = content.substring(pos, pos + l1);
		if (l2 > 0) {
			strValue += "." + content.substring(pos + l1, pos + l1 + l2); //$NON-NLS-1$
		}
		
		// Parse number
		double value = 0;
		try {
			value = Double.parseDouble(strValue);
		} catch (NumberFormatException e) {
			// Do nothing
		}
		
		NumberFormat nf = new DecimalFormat(decimalPattern);
		return nf.format(value);
	}
	
	protected String getWBC(final String content){
		int pos = getDataIndex();
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getRBC(final String content){
		int pos = getDataIndex() + 5;
		return getValueStr(content, pos, "XX.XXF"); //$NON-NLS-1$
	}
	
	protected String getHGB(final String content){
		int pos = getDataIndex() + 10;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getHCT(final String content){
		int pos = getDataIndex() + 15;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMCV(final String content){
		int pos = getDataIndex() + 20;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMCH(final String content){
		int pos = getDataIndex() + 25;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMCHC(final String content){
		int pos = getDataIndex() + 30;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getPLT(final String content){
		int pos = getDataIndex() + 35;
		return getValueStr(content, pos, "XXXXF"); //$NON-NLS-1$
	}
	
	protected String getLYMPercent(final String content){
		int pos = getDataIndex() + 40;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMXDPercent(final String content){
		int pos = getDataIndex() + 45;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getNEUTPercent(final String content){
		int pos = getDataIndex() + 50;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getLYMVolume(final String content){
		int pos = getDataIndex() + 55;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMXDVolume(final String content){
		int pos = getDataIndex() + 60;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getNEUTVolume(final String content){
		int pos = getDataIndex() + 65;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected abstract String getRDWSD(final String content);
	
	protected abstract String getRDWCV(final String content);
	
	protected abstract String getPDW(final String content);
	
	protected abstract String getMPV(final String content);
	
	protected abstract String getPLCR(final String content);
	
	public abstract int getSize();
	
	protected abstract int getDataIndex();
	
	protected abstract TimeTool getDate(final String content);
	
	protected abstract Value getValue(final String paramName) throws PackageException;
	
	protected abstract String getPatientId(final String content);
}
