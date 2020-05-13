package ch.elexis.ebanking.qr;

import ch.elexis.core.model.IContact;
import ch.elexis.ebanking.qr.model.QRBillData;

public class QRBillDataBuilder {
	
	private String headerQRType;
	private String headerVersion;
	private String headerCoding;
	
	private IContact cdtrInfContact;
	
	public QRBillDataBuilder(){
		// default header values
		this.headerQRType = "SPC";
		this.headerVersion = "0200";
		this.headerCoding = "1";
	}
	
	public QRBillDataBuilder cdtrInf(IContact contact){
		this.cdtrInfContact = contact;
		return this;
	}
	
	public QRBillData build(){
		QRBillData ret = new QRBillData();
		
		ret.setHeaderQRType(headerQRType);
		ret.setHeaderVersion(headerVersion);
		ret.setHeaderCoding(headerCoding);
		
		ret.setCdtrInfIBAN((String) cdtrInfContact.getExtInfo("IBAN"));
		
		return ret;
	}
}
