package ch.elexis.ebanking.qr.model;

import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.ebanking.qr.QRBillDataException;
import ch.elexis.ebanking.qr.QRBillDataException.SourceType;

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
	private String rmtInfUstrdPattern = "[\\u0020-\\uFFFF]{0,140}";
	private String rmtInfTrailer;
	private String rmtInfTrailerPattern = "[\\w]{3}";
	
	private String rmtInfStrdBkgInf;
	private String rmtInfStrdBkgInfPattern = "[\\u0020-\\uFFFF]{140}";
	
	// CREDITOR INFO - cdtrInf
	
	public String getCdtrInfIBAN(){
		return cdtrInfIBAN;
	}
	
	public void setCdtrInfIBAN(String cdtrInfIBAN) throws QRBillDataException{
		if (!cdtrInfIBAN.matches(cdtrInfIBANPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + cdtrInfIBAN + "] for [" + cdtrInfIBANPattern + "]");
		}
		this.cdtrInfIBAN = cdtrInfIBAN;
	}
	
	public String getCdtrInfAdrTp(){
		return cdtrInfAdrTp;
	}
	
	public void setCdtrInfAdrTp(String cdtrInfAdrTp) throws QRBillDataException{
		if (!cdtrInfAdrTp.matches(cdtrInfAdrTpPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + cdtrInfAdrTp + "] for [" + cdtrInfAdrTpPattern + "]");
		}
		this.cdtrInfAdrTp = cdtrInfAdrTp;
	}
	
	public String getCdtrInfName(){
		return cdtrInfName;
	}
	
	public void setCdtrInfName(String cdtrInfName) throws QRBillDataException{
		if (!cdtrInfName.matches(cdtrInfNamePattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + cdtrInfName + "] for [" + cdtrInfNamePattern + "]");
		}
		this.cdtrInfName = cdtrInfName;
	}
	
	public String getCdtrInfStrtNmOrAdrLine1(){
		return cdtrInfStrtNmOrAdrLine1;
	}
	
	public void setCdtrInfStrtNmOrAdrLine1(String cdtrInfStrtNmOrAdrLine1)
		throws QRBillDataException{
		if (!cdtrInfStrtNmOrAdrLine1.matches(cdtrInfStrtNmOrAdrLine1Pattern)) {
			throw new QRBillDataException(SourceType.CREDITOR,
				"Invalid value ["
				+ cdtrInfStrtNmOrAdrLine1
				+ "] for [" + cdtrInfStrtNmOrAdrLine1Pattern + "]");
		}
		this.cdtrInfStrtNmOrAdrLine1 = cdtrInfStrtNmOrAdrLine1;
	}
	
	public String getCdtrInfStrtNmOrAdrLine2(){
		return cdtrInfStrtNmOrAdrLine2;
	}
	
	public void setCdtrInfStrtNmOrAdrLine2(String cdtrInfStrtNmOrAdrLine2)
		throws QRBillDataException{
		if (!cdtrInfStrtNmOrAdrLine2.matches(cdtrInfStrtNmOrAdrLine2Pattern)) {
			throw new QRBillDataException(SourceType.CREDITOR,
				"Invalid value ["
				+ cdtrInfStrtNmOrAdrLine2
				+ "] for [" + cdtrInfStrtNmOrAdrLine2Pattern + "]");
		}
		this.cdtrInfStrtNmOrAdrLine2 = cdtrInfStrtNmOrAdrLine2;
	}
	
	public String getCdtrInfPstCd(){
		return cdtrInfPstCd;
	}
	
	public void setCdtrInfPstCd(String cdtrInfPstCd) throws QRBillDataException{
		if (!cdtrInfPstCd.matches(cdtrInfPstCdPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + cdtrInfPstCd + "] for [" + cdtrInfPstCdPattern + "]");
		}
		this.cdtrInfPstCd = cdtrInfPstCd;
	}
	
	public String getCdtrInfTwnNm(){
		return cdtrInfTwnNm;
	}
	
	public void setCdtrInfTwnNm(String cdtrInfTwnNm) throws QRBillDataException{
		if (!cdtrInfTwnNm.matches(cdtrInfTwnNmPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + cdtrInfTwnNm + "] for [" + cdtrInfTwnNmPattern + "]");
		}
		this.cdtrInfTwnNm = cdtrInfTwnNm;
	}
	
	public String getCdtrInfCtry(){
		return cdtrInfCtry;
	}
	
	public void setCdtrInfCtry(String cdtrInfCtry) throws QRBillDataException{
		if (!cdtrInfCtry.matches(cdtrInfCtryPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + cdtrInfCtry + "] for [" + cdtrInfCtryPattern + "]");
		}
		this.cdtrInfCtry = cdtrInfCtry;
	}
	
	// ULTIMATE CREDITOR - ultmtCdtr
	
	public String getUltmtCdtrAdrTp(){
		return ultmtCdtrAdrTp;
	}
	
	public void setUltmtCdtrAdrTp(String ultmtCdtrAdrTp) throws QRBillDataException{
		if (!ultmtCdtrAdrTp.matches(ultmtCdtrAdrTpPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + ultmtCdtrAdrTp + "] for [" + ultmtCdtrAdrTpPattern + "]");
		}
		this.ultmtCdtrAdrTp = ultmtCdtrAdrTp;
	}
	
	public String getUltmtCdtrName(){
		return ultmtCdtrName;
	}
	
	public void setUltmtCdtrName(String ultmtCdtrName) throws QRBillDataException{
		if (!ultmtCdtrName.matches(ultmtCdtrNamePattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + ultmtCdtrName + "] for [" + ultmtCdtrNamePattern + "]");
		}
		this.ultmtCdtrName = ultmtCdtrName;
	}
	
	public String getUltmtCdtrStrtNmOrAdrLine1(){
		return ultmtCdtrStrtNmOrAdrLine1;
	}
	
	public void setUltmtCdtrStrtNmOrAdrLine1(String ultmtCdtrStrtNmOrAdrLine1)
		throws QRBillDataException{
		if (!ultmtCdtrStrtNmOrAdrLine1.matches(ultmtCdtrStrtNmOrAdrLine1Pattern)) {
			throw new QRBillDataException(SourceType.CREDITOR,
				"Invalid value ["
				+ ultmtCdtrStrtNmOrAdrLine1
				+ "] for [" + ultmtCdtrStrtNmOrAdrLine1Pattern + "]");
		}
		this.ultmtCdtrStrtNmOrAdrLine1 = ultmtCdtrStrtNmOrAdrLine1;
	}
	
	public String getUltmtCdtrStrtNmOrAdrLine2(){
		return ultmtCdtrStrtNmOrAdrLine2;
	}
	
	public void setUltmtCdtrStrtNmOrAdrLine2(String ultmtCdtrStrtNmOrAdrLine2)
		throws QRBillDataException{
		if (!ultmtCdtrStrtNmOrAdrLine2.matches(ultmtCdtrStrtNmOrAdrLine2Pattern)) {
			throw new QRBillDataException(SourceType.CREDITOR,
				"Invalid value ["
				+ ultmtCdtrStrtNmOrAdrLine2
				+ "] for [" + ultmtCdtrStrtNmOrAdrLine2Pattern + "]");
		}
		this.ultmtCdtrStrtNmOrAdrLine2 = ultmtCdtrStrtNmOrAdrLine2;
	}
	
	public String getUltmtCdtrPstCd(){
		return ultmtCdtrPstCd;
	}
	
	public void setUltmtCdtrPstCd(String ultmtCdtrPstCd) throws QRBillDataException{
		if (!ultmtCdtrPstCd.matches(ultmtCdtrPstCdPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + ultmtCdtrPstCd + "] for [" + ultmtCdtrPstCdPattern + "]");
		}
		this.ultmtCdtrPstCd = ultmtCdtrPstCd;
	}
	
	public String getUltmtCdtrTwnNm(){
		return ultmtCdtrTwnNm;
	}
	
	public void setUltmtCdtrTwnNm(String ultmtCdtrTwnNm) throws QRBillDataException{
		if (!ultmtCdtrTwnNm.matches(ultmtCdtrTwnNmPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + ultmtCdtrTwnNm + "] for [" + ultmtCdtrTwnNmPattern + "]");
		}
		this.ultmtCdtrTwnNm = ultmtCdtrTwnNm;
	}
	
	public String getUltmtCdtrCtry(){
		return ultmtCdtrCtry;
	}
	
	public void setUltmtCdtrCtry(String ultmtCdtrCtry) throws QRBillDataException{
		if (!ultmtCdtrCtry.matches(ultmtCdtrCtryPattern)) {
			throw new QRBillDataException(
				SourceType.CREDITOR,
				"Invalid value [" + ultmtCdtrCtry + "] for [" + ultmtCdtrCtryPattern + "]");
		}
		this.ultmtCdtrCtry = ultmtCdtrCtry;
	}
	
	// CURRENCY AMOUNT - ccyAmt
	
	public String getCcyAmtAmt(){
		return ccyAmtAmt;
	}
	
	public void setCcyAmtAmt(String ccyAmtAmt) throws QRBillDataException{
		if (!ccyAmtAmt.matches(ccyAmtAmtPattern)) {
			throw new QRBillDataException(
				SourceType.AMOUNT,
				"Invalid value [" + ccyAmtAmt + "] for [" + ccyAmtAmtPattern + "]");
		}
		this.ccyAmtAmt = ccyAmtAmt;
	}
	
	public String getCcyAmtCcy(){
		return ccyAmtCcy;
	}
	
	public void setCcyAmtCcy(String ccyAmtCcy) throws QRBillDataException{
		if (!ccyAmtCcy.matches(ccyAmtCcyPattern)) {
			throw new QRBillDataException(
				SourceType.AMOUNT,
				"Invalid value [" + ccyAmtCcy + "] for [" + ccyAmtCcyPattern + "]");
		}
		this.ccyAmtCcy = ccyAmtCcy;
	}
	
	// ULTIMATE DEBITOR - ultmtDbtr
	
	public String getUltmtDbtrAdrTp(){
		return ultmtDbtrAdrTp;
	}
	
	public void setUltmtDbtrAdrTp(String ultmtDbtrAdrTp) throws QRBillDataException{
		if (!ultmtDbtrAdrTp.matches(ultmtDbtrAdrTpPattern)) {
			throw new QRBillDataException(
				SourceType.DEBITOR,
				"Invalid value [" + ultmtDbtrAdrTp + "] for [" + ultmtDbtrAdrTpPattern + "]");
		}
		this.ultmtDbtrAdrTp = ultmtDbtrAdrTp;
	}
	
	public String getUltmtDbtrName(){
		return ultmtDbtrName;
	}
	
	public void setUltmtDbtrName(String ultmtDbtrName) throws QRBillDataException{
		if (!ultmtDbtrName.matches(ultmtDbtrNamePattern)) {
			throw new QRBillDataException(
				SourceType.DEBITOR,
				"Invalid value [" + ultmtDbtrName + "] for [" + ultmtDbtrNamePattern + "]");
		}
		this.ultmtDbtrName = ultmtDbtrName;
	}
	
	public String getUltmtDbtrStrtNmOrAdrLine1(){
		return ultmtDbtrStrtNmOrAdrLine1;
	}
	
	public void setUltmtDbtrStrtNmOrAdrLine1(String ultmtDbtrStrtNmOrAdrLine1)
		throws QRBillDataException{
		if (!ultmtDbtrStrtNmOrAdrLine1.matches(ultmtDbtrStrtNmOrAdrLine1Pattern)) {
			throw new QRBillDataException(SourceType.DEBITOR,
				"Invalid value ["
				+ ultmtDbtrStrtNmOrAdrLine1
				+ "] for [" + ultmtDbtrStrtNmOrAdrLine1Pattern + "]");
		}
		this.ultmtDbtrStrtNmOrAdrLine1 = ultmtDbtrStrtNmOrAdrLine1;
	}
	
	public String getUltmtDbtrStrtNmOrAdrLine2(){
		return ultmtDbtrStrtNmOrAdrLine2;
	}
	
	public void setUltmtDbtrStrtNmOrAdrLine2(String ultmtDbtrStrtNmOrAdrLine2)
		throws QRBillDataException{
		if (!ultmtDbtrStrtNmOrAdrLine2.matches(ultmtDbtrStrtNmOrAdrLine2Pattern)) {
			throw new QRBillDataException(SourceType.DEBITOR,
				"Invalid value ["
				+ ultmtDbtrStrtNmOrAdrLine2
				+ "] for [" + ultmtDbtrStrtNmOrAdrLine2Pattern + "]");
		}
		this.ultmtDbtrStrtNmOrAdrLine2 = ultmtDbtrStrtNmOrAdrLine2;
	}
	
	public String getUltmtDbtrPstCd(){
		return ultmtDbtrPstCd;
	}
	
	public void setUltmtDbtrPstCd(String ultmtDbtrPstCd) throws QRBillDataException{
		if (!ultmtDbtrPstCd.matches(ultmtDbtrPstCdPattern)) {
			throw new QRBillDataException(
				SourceType.DEBITOR,
				"Invalid value [" + ultmtDbtrPstCd + "] for [" + ultmtDbtrPstCdPattern + "]");
		}
		this.ultmtDbtrPstCd = ultmtDbtrPstCd;
	}
	
	public String getUltmtDbtrTwnNm(){
		return ultmtDbtrTwnNm;
	}
	
	public void setUltmtDbtrTwnNm(String ultmtDbtrTwnNm) throws QRBillDataException{
		if (!ultmtDbtrTwnNm.matches(ultmtDbtrTwnNmPattern)) {
			throw new QRBillDataException(
				SourceType.DEBITOR,
				"Invalid value [" + ultmtDbtrTwnNm + "] for [" + ultmtDbtrTwnNmPattern + "]");
		}
		this.ultmtDbtrTwnNm = ultmtDbtrTwnNm;
	}
	
	public String getUltmtDbtrCtry(){
		return ultmtDbtrCtry;
	}
	
	public void setUltmtDbtrCtry(String ultmtDbtrCtry) throws QRBillDataException{
		if (!ultmtDbtrCtry.matches(ultmtDbtrCtryPattern)) {
			throw new QRBillDataException(
				SourceType.DEBITOR,
				"Invalid value [" + ultmtDbtrCtry + "] for [" + ultmtDbtrCtryPattern + "]");
		}
		this.ultmtDbtrCtry = ultmtDbtrCtry;
	}
	
	// REMARK - rmtInf
	
	public String getRmtInfTp(){
		return rmtInfTp;
	}
	
	public void setRmtInfTp(String rmtInfTp) throws QRBillDataException{
		if (!rmtInfTp.matches(rmtInfTpPattern)) {
			throw new QRBillDataException(
				SourceType.REMARK,
				"Invalid value [" + rmtInfTp + "] for [" + rmtInfTpPattern + "]");
		}
		this.rmtInfTp = rmtInfTp;
	}
	
	public String getRmtInfRef(){
		return rmtInfRef;
	}
	
	public void setRmtInfRef(String rmtInfRef) throws QRBillDataException{
		if (!rmtInfRef.matches(rmtInfRefPattern)) {
			throw new QRBillDataException(
				SourceType.REMARK,
				"Invalid value [" + rmtInfRef + "] for [" + rmtInfRefPattern + "]");
		}
		this.rmtInfRef = rmtInfRef;
	}
	
	public String getRmtInfUstrd(){
		return rmtInfUstrd;
	}
	
	public void setRmtInfUstrd(String rmtInfUstrd) throws QRBillDataException{
		if (!rmtInfUstrd.matches(rmtInfUstrdPattern)) {
			throw new QRBillDataException(
				SourceType.REMARK,
				"Invalid value [" + rmtInfUstrd + "] for [" + rmtInfUstrdPattern + "]");
		}
		this.rmtInfUstrd = rmtInfUstrd;
	}
	
	public String getRmtInfTrailer(){
		return rmtInfTrailer;
	}
	
	public void setRmtInfTrailer(String rmtInfTrailer) throws QRBillDataException{
		if (!rmtInfTrailer.matches(rmtInfTrailerPattern)) {
			throw new QRBillDataException(
				SourceType.REMARK,
				"Invalid value [" + rmtInfTrailer + "] for [" + rmtInfTrailerPattern + "]");
		}
		this.rmtInfTrailer = rmtInfTrailer;
	}
	
	public String getRmtInfStrdBkgInf(){
		return rmtInfStrdBkgInf;
	}
	
	public void setRmtInfStrdBkgInf(String rmtInfStrdBkgInf) throws QRBillDataException{
		if (!rmtInfStrdBkgInf.matches(rmtInfStrdBkgInfPattern)) {
			throw new QRBillDataException(
				SourceType.REMARK,
				"Invalid value [" + rmtInfStrdBkgInf + "] for [" + rmtInfStrdBkgInfPattern + "]");
		}
		this.rmtInfStrdBkgInf = rmtInfStrdBkgInf;
	}
	
	// HEADER - header
	
	public String getHeaderQRType(){
		return headerQRType;
	}
	
	public void setHeaderQRType(String headerQRType) throws QRBillDataException{
		if (!headerQRType.matches(headerQRTypePattern)) {
			throw new QRBillDataException(
				SourceType.HEADER,
				"Invalid value [" + headerQRType + "] for [" + headerQRTypePattern + "]");
		}
		this.headerQRType = headerQRType;
	}
	
	public String getHeaderVersion(){
		return headerVersion;
	}
	
	public void setHeaderVersion(String headerVersion) throws QRBillDataException{
		if (!headerVersion.matches(headerVersionPattern)) {
			throw new QRBillDataException(
				SourceType.HEADER,
				"Invalid value [" + headerVersion + "] for [" + headerVersionPattern + "]");
		}
		this.headerVersion = headerVersion;
	}
	
	public String getHeaderCoding(){
		return headerCoding;
	}
	
	public void setHeaderCoding(String headerCoding) throws QRBillDataException{
		if (!headerCoding.matches(headerCodingPattern)) {
			throw new QRBillDataException(
				SourceType.HEADER,
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
