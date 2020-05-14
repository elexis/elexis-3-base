package ch.elexis.ebanking.qr.model;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

public class QRBillData {
	
	public static String SEPARATOR = "\r\n";
	
	private String headerQRType;
	private String headerQRTypePattern = "[\\w]{3}";
	private String headerVersion;
	private String headerVersionPattern = "[\\d]{4}";
	private String headerCoding;
	private String headerCodingPattern = "[\\d]{1}";
	
	private String cdtrInfIBAN;
	private String cdtrInfIBANPattern = "[\\w]{21}";
	
	private String cdtrInfAdrTp;
	private String cdtrInfAdrTpPattern = "[\\w]{1}";
	private String cdtrInfName;
	private String cdtrInfNamePattern = "[\\w ]{1,70}";
	private String cdtrInfStrtNmOrAdrLine1;
	private String cdtrInfStrtNmOrAdrLine1Pattern = "[\\w ]{1,70}";
	private String cdtrInfStrtNmOrAdrLine2;
	private String cdtrInfStrtNmOrAdrLine2Pattern = "[\\w ]{1,16}";
	private String cdtrInfPstCd;
	private String cdtrInfPstCdPattern = "[\\w]{0,16}";
	private String cdtrInfTwnNm;
	private String cdtrInfTwnNmPattern = "[\\w]{0,35}";
	private String cdtrInfCtry;
	private String cdtrInfCtryPattern = "[\\w]{1,2}";
	
	private String ultmtCdtrAdrTp;
	private String ultmtCdtrAdrTpPattern = "[\\w]{1}";
	private String ultmtCdtrName;
	private String ultmtCdtrNamePattern = "[\\w ]{1,70}";
	private String ultmtCdtrStrtNmOrAdrLine1;
	private String ultmtCdtrStrtNmOrAdrLine1Pattern = "[\\w ]{1,70}";
	private String ultmtCdtrStrtNmOrAdrLine2;
	private String ultmtCdtrStrtNmOrAdrLine2Pattern = "[\\w ]{1,16}";
	private String ultmtCdtrPstCd;
	private String ultmtCdtrPstCdPattern = "[\\w]{0,16}";
	private String ultmtCdtrTwnNm;
	private String ultmtCdtrTwnNmPattern = "[\\w]{0,35}";
	private String ultmtCdtrCtry;
	private String ultmtCdtrCtryPattern = "[\\w]{1,2}";
	
	private String ccyAmtAmt;
	private String ccyAmtAmtPattern = "[0-9\\.]{1,12}";
	private String ccyAmtCcy;
	private String ccyAmtCcyPattern = "[A-Z]{3}";
	
	private String ultmtDbtrAdrTp;
	private String ultmtDbtrAdrTpPattern = "[\\w]{1}";
	private String ultmtDbtrName;
	private String ultmtDbtrNamePattern = "[\\w ]{1,70}";
	private String ultmtDbtrStrtNmOrAdrLine1;
	private String ultmtDbtrStrtNmOrAdrLine1Pattern = "[\\w ]{1,70}";
	private String ultmtDbtrStrtNmOrAdrLine2;
	private String ultmtDbtrStrtNmOrAdrLine2Pattern = "[\\w ]{1,16}";
	private String ultmtDbtrPstCd;
	private String ultmtDbtrPstCdPattern = "[\\w]{0,16}";
	private String ultmtDbtrTwnNm;
	private String ultmtDbtrTwnNmPattern = "[\\w]{0,35}";
	private String ultmtDbtrCtry;
	private String ultmtDbtrCtryPattern = "[\\w]{1,2}";
	
	private String rmtInfTp;
	private String rmtInfTpPattern = "[\\w]{1,4}";
	private String rmtInfRef;
	private String rmtInfRefPattern = "[\\w]{1,27}";
	private String rmtInfUstrd;
	private String rmtInfUstrdPattern = "[\\u0020-\\uFFFF]{1,140}";
	private String rmtInfTrailer;
	private String rmtInfTrailerPattern = "[\\w]{3}";
	
	private String rmtInfStrdBkgInf;
	private String rmtInfStrdBkgInfPattern = "[\\u0020-\\uFFFF]{140}";
	
	// CREDITOR INFO - cdtrInf
	
	public String getCdtrInfIBAN(){
		return cdtrInfIBAN;
	}
	
	public void setCdtrInfIBAN(String cdtrInfIBAN){
		if (!cdtrInfIBAN.matches(cdtrInfIBANPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + cdtrInfIBAN + "] for [" + cdtrInfIBANPattern + "]");
		}
		this.cdtrInfIBAN = cdtrInfIBAN;
	}
	
	public String getCdtrInfAdrTp(){
		return cdtrInfAdrTp;
	}
	
	public void setCdtrInfAdrTp(String cdtrInfAdrTp){
		if (!cdtrInfAdrTp.matches(cdtrInfAdrTpPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + cdtrInfAdrTp + "] for [" + cdtrInfAdrTpPattern + "]");
		}
		this.cdtrInfAdrTp = cdtrInfAdrTp;
	}
	
	public String getCdtrInfName(){
		return cdtrInfName;
	}
	
	public void setCdtrInfName(String cdtrInfName){
		if (!cdtrInfName.matches(cdtrInfNamePattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + cdtrInfName + "] for [" + cdtrInfNamePattern + "]");
		}
		this.cdtrInfName = cdtrInfName;
	}
	
	public String getCdtrInfStrtNmOrAdrLine1(){
		return cdtrInfStrtNmOrAdrLine1;
	}
	
	public void setCdtrInfStrtNmOrAdrLine1(String cdtrInfStrtNmOrAdrLine1){
		if (!cdtrInfStrtNmOrAdrLine1.matches(cdtrInfStrtNmOrAdrLine1Pattern)) {
			throw new IllegalArgumentException("Invalid value [" + cdtrInfStrtNmOrAdrLine1
				+ "] for [" + cdtrInfStrtNmOrAdrLine1Pattern + "]");
		}
		this.cdtrInfStrtNmOrAdrLine1 = cdtrInfStrtNmOrAdrLine1;
	}
	
	public String getCdtrInfStrtNmOrAdrLine2(){
		return cdtrInfStrtNmOrAdrLine2;
	}
	
	public void setCdtrInfStrtNmOrAdrLine2(String cdtrInfStrtNmOrAdrLine2){
		if (!cdtrInfStrtNmOrAdrLine2.matches(cdtrInfStrtNmOrAdrLine2Pattern)) {
			throw new IllegalArgumentException("Invalid value [" + cdtrInfStrtNmOrAdrLine2
				+ "] for [" + cdtrInfStrtNmOrAdrLine2Pattern + "]");
		}
		this.cdtrInfStrtNmOrAdrLine2 = cdtrInfStrtNmOrAdrLine2;
	}
	
	public String getCdtrInfPstCd(){
		return cdtrInfPstCd;
	}
	
	public void setCdtrInfPstCd(String cdtrInfPstCd){
		if (!cdtrInfPstCd.matches(cdtrInfPstCdPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + cdtrInfPstCd + "] for [" + cdtrInfPstCdPattern + "]");
		}
		this.cdtrInfPstCd = cdtrInfPstCd;
	}
	
	public String getCdtrInfTwnNm(){
		return cdtrInfTwnNm;
	}
	
	public void setCdtrInfTwnNm(String cdtrInfTwnNm){
		if (!cdtrInfTwnNm.matches(cdtrInfTwnNmPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + cdtrInfTwnNm + "] for [" + cdtrInfTwnNmPattern + "]");
		}
		this.cdtrInfTwnNm = cdtrInfTwnNm;
	}
	
	public String getCdtrInfCtry(){
		return cdtrInfCtry;
	}
	
	public void setCdtrInfCtry(String cdtrInfCtry){
		if (!cdtrInfCtry.matches(cdtrInfCtryPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + cdtrInfCtry + "] for [" + cdtrInfCtryPattern + "]");
		}
		this.cdtrInfCtry = cdtrInfCtry;
	}
	
	// ULTIMATE CREDITOR - ultmtCdtr
	
	public String getUltmtCdtrAdrTp(){
		return ultmtCdtrAdrTp;
	}
	
	public void setUltmtCdtrAdrTp(String ultmtCdtrAdrTp){
		if (!ultmtCdtrAdrTp.matches(ultmtCdtrAdrTpPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtCdtrAdrTp + "] for [" + ultmtCdtrAdrTpPattern + "]");
		}
		this.ultmtCdtrAdrTp = ultmtCdtrAdrTp;
	}
	
	public String getUltmtCdtrName(){
		return ultmtCdtrName;
	}
	
	public void setUltmtCdtrName(String ultmtCdtrName){
		if (!ultmtCdtrName.matches(ultmtCdtrNamePattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtCdtrName + "] for [" + ultmtCdtrNamePattern + "]");
		}
		this.ultmtCdtrName = ultmtCdtrName;
	}
	
	public String getUltmtCdtrStrtNmOrAdrLine1(){
		return ultmtCdtrStrtNmOrAdrLine1;
	}
	
	public void setUltmtCdtrStrtNmOrAdrLine1(String ultmtCdtrStrtNmOrAdrLine1){
		if (!ultmtCdtrStrtNmOrAdrLine1.matches(ultmtCdtrStrtNmOrAdrLine1Pattern)) {
			throw new IllegalArgumentException("Invalid value [" + ultmtCdtrStrtNmOrAdrLine1
				+ "] for [" + ultmtCdtrStrtNmOrAdrLine1Pattern + "]");
		}
		this.ultmtCdtrStrtNmOrAdrLine1 = ultmtCdtrStrtNmOrAdrLine1;
	}
	
	public String getUltmtCdtrStrtNmOrAdrLine2(){
		return ultmtCdtrStrtNmOrAdrLine2;
	}
	
	public void setUltmtCdtrStrtNmOrAdrLine2(String ultmtCdtrStrtNmOrAdrLine2){
		if (!ultmtCdtrStrtNmOrAdrLine2.matches(ultmtCdtrStrtNmOrAdrLine2Pattern)) {
			throw new IllegalArgumentException("Invalid value [" + ultmtCdtrStrtNmOrAdrLine2
				+ "] for [" + ultmtCdtrStrtNmOrAdrLine2Pattern + "]");
		}
		this.ultmtCdtrStrtNmOrAdrLine2 = ultmtCdtrStrtNmOrAdrLine2;
	}
	
	public String getUltmtCdtrPstCd(){
		return ultmtCdtrPstCd;
	}
	
	public void setUltmtCdtrPstCd(String ultmtCdtrPstCd){
		if (!ultmtCdtrPstCd.matches(ultmtCdtrPstCdPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtCdtrPstCd + "] for [" + ultmtCdtrPstCdPattern + "]");
		}
		this.ultmtCdtrPstCd = ultmtCdtrPstCd;
	}
	
	public String getUltmtCdtrTwnNm(){
		return ultmtCdtrTwnNm;
	}
	
	public void setUltmtCdtrTwnNm(String ultmtCdtrTwnNm){
		if (!ultmtCdtrTwnNm.matches(ultmtCdtrTwnNmPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtCdtrTwnNm + "] for [" + ultmtCdtrTwnNmPattern + "]");
		}
		this.ultmtCdtrTwnNm = ultmtCdtrTwnNm;
	}
	
	public String getUltmtCdtrCtry(){
		return ultmtCdtrCtry;
	}
	
	public void setUltmtCdtrCtry(String ultmtCdtrCtry){
		if (!ultmtCdtrCtry.matches(ultmtCdtrCtryPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtCdtrCtry + "] for [" + ultmtCdtrCtryPattern + "]");
		}
		this.ultmtCdtrCtry = ultmtCdtrCtry;
	}
	
	// CURRENCY AMOUNT - ccyAmt
	
	public String getCcyAmtAmt(){
		return ccyAmtAmt;
	}
	
	public void setCcyAmtAmt(String ccyAmtAmt){
		if (!ccyAmtAmt.matches(ccyAmtAmtPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ccyAmtAmt + "] for [" + ccyAmtAmtPattern + "]");
		}
		this.ccyAmtAmt = ccyAmtAmt;
	}
	
	public String getCcyAmtCcy(){
		return ccyAmtCcy;
	}
	
	public void setCcyAmtCcy(String ccyAmtCcy){
		if (!ccyAmtCcy.matches(ccyAmtCcyPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ccyAmtCcy + "] for [" + ccyAmtCcyPattern + "]");
		}
		this.ccyAmtCcy = ccyAmtCcy;
	}
	
	// ULTIMATE DEBITOR - ultmtDbtr
	
	public String getUltmtDbtrAdrTp(){
		return ultmtDbtrAdrTp;
	}
	
	public void setUltmtDbtrAdrTp(String ultmtDbtrAdrTp){
		if (!ultmtDbtrAdrTp.matches(ultmtDbtrAdrTpPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtDbtrAdrTp + "] for [" + ultmtDbtrAdrTpPattern + "]");
		}
		this.ultmtDbtrAdrTp = ultmtDbtrAdrTp;
	}
	
	public String getUltmtDbtrName(){
		return ultmtDbtrName;
	}
	
	public void setUltmtDbtrName(String ultmtDbtrName){
		if (!ultmtDbtrName.matches(ultmtDbtrNamePattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtDbtrName + "] for [" + ultmtDbtrNamePattern + "]");
		}
		this.ultmtDbtrName = ultmtDbtrName;
	}
	
	public String getUltmtDbtrStrtNmOrAdrLine1(){
		return ultmtDbtrStrtNmOrAdrLine1;
	}
	
	public void setUltmtDbtrStrtNmOrAdrLine1(String ultmtDbtrStrtNmOrAdrLine1){
		if (!ultmtDbtrStrtNmOrAdrLine1.matches(ultmtDbtrStrtNmOrAdrLine1Pattern)) {
			throw new IllegalArgumentException("Invalid value [" + ultmtDbtrStrtNmOrAdrLine1
				+ "] for [" + ultmtDbtrStrtNmOrAdrLine1Pattern + "]");
		}
		this.ultmtDbtrStrtNmOrAdrLine1 = ultmtDbtrStrtNmOrAdrLine1;
	}
	
	public String getUltmtDbtrStrtNmOrAdrLine2(){
		return ultmtDbtrStrtNmOrAdrLine2;
	}
	
	public void setUltmtDbtrStrtNmOrAdrLine2(String ultmtDbtrStrtNmOrAdrLine2){
		if (!ultmtDbtrStrtNmOrAdrLine2.matches(ultmtDbtrStrtNmOrAdrLine2Pattern)) {
			throw new IllegalArgumentException("Invalid value [" + ultmtDbtrStrtNmOrAdrLine2
				+ "] for [" + ultmtDbtrStrtNmOrAdrLine2Pattern + "]");
		}
		this.ultmtDbtrStrtNmOrAdrLine2 = ultmtDbtrStrtNmOrAdrLine2;
	}
	
	public String getUltmtDbtrPstCd(){
		return ultmtDbtrPstCd;
	}
	
	public void setUltmtDbtrPstCd(String ultmtDbtrPstCd){
		if (!ultmtDbtrPstCd.matches(ultmtDbtrPstCdPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtDbtrPstCd + "] for [" + ultmtDbtrPstCdPattern + "]");
		}
		this.ultmtDbtrPstCd = ultmtDbtrPstCd;
	}
	
	public String getUltmtDbtrTwnNm(){
		return ultmtDbtrTwnNm;
	}
	
	public void setUltmtDbtrTwnNm(String ultmtDbtrTwnNm){
		if (!ultmtDbtrTwnNm.matches(ultmtDbtrTwnNmPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtDbtrTwnNm + "] for [" + ultmtDbtrTwnNmPattern + "]");
		}
		this.ultmtDbtrTwnNm = ultmtDbtrTwnNm;
	}
	
	public String getUltmtDbtrCtry(){
		return ultmtDbtrCtry;
	}
	
	public void setUltmtDbtrCtry(String ultmtDbtrCtry){
		if (!ultmtDbtrCtry.matches(ultmtDbtrCtryPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + ultmtDbtrCtry + "] for [" + ultmtDbtrCtryPattern + "]");
		}
		this.ultmtDbtrCtry = ultmtDbtrCtry;
	}
	
	// REMARK - rmtInf
	
	public String getRmtInfTp(){
		return rmtInfTp;
	}
	
	public void setRmtInfTp(String rmtInfTp){
		if (!rmtInfTp.matches(rmtInfTpPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + rmtInfTp + "] for [" + rmtInfTpPattern + "]");
		}
		this.rmtInfTp = rmtInfTp;
	}
	
	public String getRmtInfRef(){
		return rmtInfRef;
	}
	
	public void setRmtInfRef(String rmtInfRef){
		if (!rmtInfRef.matches(rmtInfRefPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + rmtInfRef + "] for [" + rmtInfRefPattern + "]");
		}
		this.rmtInfRef = rmtInfRef;
	}
	
	public String getRmtInfUstrd(){
		return rmtInfUstrd;
	}
	
	public void setRmtInfUstrd(String rmtInfUstrd){
		if (!rmtInfUstrd.matches(rmtInfUstrdPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + rmtInfUstrd + "] for [" + rmtInfUstrdPattern + "]");
		}
		this.rmtInfUstrd = rmtInfUstrd;
	}
	
	public String getRmtInfTrailer(){
		return rmtInfTrailer;
	}
	
	public void setRmtInfTrailer(String rmtInfTrailer){
		if (!rmtInfTrailer.matches(rmtInfTrailerPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + rmtInfTrailer + "] for [" + rmtInfTrailerPattern + "]");
		}
		this.rmtInfTrailer = rmtInfTrailer;
	}
	
	public String getRmtInfStrdBkgInf(){
		return rmtInfStrdBkgInf;
	}
	
	public void setRmtInfStrdBkgInf(String rmtInfStrdBkgInf){
		if (!rmtInfStrdBkgInf.matches(rmtInfStrdBkgInfPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + rmtInfStrdBkgInf + "] for [" + rmtInfStrdBkgInfPattern + "]");
		}
		this.rmtInfStrdBkgInf = rmtInfStrdBkgInf;
	}
	
	// HEADER - header
	
	public String getHeaderQRType(){
		return headerQRType;
	}
	
	public void setHeaderQRType(String headerQRType){
		if (!headerQRType.matches(headerQRTypePattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + headerQRType + "] for [" + headerQRTypePattern + "]");
		}
		this.headerQRType = headerQRType;
	}
	
	public String getHeaderVersion(){
		return headerVersion;
	}
	
	public void setHeaderVersion(String headerVersion){
		if (!headerVersion.matches(headerVersionPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + headerVersion + "] for [" + headerVersionPattern + "]");
		}
		this.headerVersion = headerVersion;
	}
	
	public String getHeaderCoding(){
		return headerCoding;
	}
	
	public void setHeaderCoding(String headerCoding){
		if (!headerCoding.matches(headerCodingPattern)) {
			throw new IllegalArgumentException(
				"Invalid value [" + headerCoding + "] for [" + headerCodingPattern + "]");
		}
		this.headerCoding = headerCoding;
	}
	
	@Override
	public String toString(){
		StringJoiner sj = new StringJoiner(SEPARATOR);
		
		sj.add(headerQRType);
		sj.add(headerVersion);
		sj.add(headerCoding);

		sj.add(StringUtils.defaultString(cdtrInfIBAN));
		
		sj.add(StringUtils.defaultString(cdtrInfAdrTp));
		sj.add(StringUtils.defaultString(cdtrInfName));
		sj.add(StringUtils.defaultString(cdtrInfStrtNmOrAdrLine1));
		sj.add(StringUtils.defaultString(cdtrInfStrtNmOrAdrLine2));
		sj.add(StringUtils.defaultString(cdtrInfPstCd));
		sj.add(StringUtils.defaultString(cdtrInfTwnNm));
		sj.add(StringUtils.defaultString(cdtrInfCtry));

		sj.add(StringUtils.defaultString(ultmtCdtrAdrTp));
		sj.add(StringUtils.defaultString(ultmtCdtrName));
		sj.add(StringUtils.defaultString(ultmtCdtrStrtNmOrAdrLine1));
		sj.add(StringUtils.defaultString(ultmtCdtrStrtNmOrAdrLine2));
		sj.add(StringUtils.defaultString(ultmtCdtrPstCd));
		sj.add(StringUtils.defaultString(ultmtCdtrTwnNm));
		sj.add(StringUtils.defaultString(ultmtCdtrCtry));
		
		sj.add(ccyAmtAmt);
		sj.add(ccyAmtCcy);
		
		sj.add(StringUtils.defaultString(ultmtDbtrAdrTp));
		sj.add(StringUtils.defaultString(ultmtDbtrName));
		sj.add(StringUtils.defaultString(ultmtDbtrStrtNmOrAdrLine1));
		sj.add(StringUtils.defaultString(ultmtDbtrStrtNmOrAdrLine2));
		sj.add(StringUtils.defaultString(ultmtDbtrPstCd));
		sj.add(StringUtils.defaultString(ultmtDbtrTwnNm));
		sj.add(StringUtils.defaultString(ultmtDbtrCtry));
		
		sj.add(StringUtils.defaultString(rmtInfTp));
		sj.add(StringUtils.defaultString(rmtInfRef));
		sj.add(StringUtils.defaultString(rmtInfUstrd));
		sj.add(StringUtils.defaultString(rmtInfTrailer));
		sj.add(StringUtils.defaultString(rmtInfStrdBkgInf));
		
		return sj.toString();
	}
}
