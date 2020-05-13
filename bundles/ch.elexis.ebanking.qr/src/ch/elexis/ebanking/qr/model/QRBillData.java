package ch.elexis.ebanking.qr.model;

import java.util.StringJoiner;

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
	private String cdtrInfNamePattern = "[\\w]{70}";
	private String cdtrInfStrtNmOrAdrLine1;
	private String cdtrInfStrtNmOrAdrLine1Pattern = "[\\w]{70}";
	private String cdtrInfStrtNmOrAdrLine2;
	private String cdtrInfStrtNmOrAdrLine2Pattern = "[\\w]{16}";
	private String cdtrInfPstCd;
	private String cdtrInfPstCdPattern = "[\\w]{16}";
	private String cdtrInfTwnNm;
	private String cdtrInfTwnNmPattern = "[\\w]{35}";
	private String cdtrInfCtry;
	private String cdtrInfCtryPattern = "[\\w]{2}";
	
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

		sj.add(cdtrInfIBAN);
		sj.add(cdtrInfAdrTp);
		sj.add(cdtrInfName);
		sj.add(cdtrInfStrtNmOrAdrLine1);
		sj.add(cdtrInfStrtNmOrAdrLine2);
		sj.add(cdtrInfPstCd);
		sj.add(cdtrInfTwnNm);
		sj.add(cdtrInfCtry);

		return sj.toString();
	}
}
