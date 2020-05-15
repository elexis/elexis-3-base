package ch.elexis.ebanking.qr;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.format.AddressFormatUtil;
import ch.elexis.ebanking.qr.model.QRBillData;
import ch.rgw.tools.Money;

public class QRBillDataBuilder {
	
	private String headerQRType;
	private String headerVersion;
	private String headerCoding;
	
	private IContact cdtrInfContact;
	
	private Money amount;
	private String amountCurrency;
	
	private IContact ultmtDbtrContact;
	
	private String referenceType;
	
	private String reference;
	
	private String referenceUnstructuredRemark;
	
	private String referenceTrailer;
	
	public QRBillDataBuilder(IContact cdtrInf, Money money, String currency, IContact ultmtDbtr){
		// default header values
		this.headerQRType = "SPC";
		this.headerVersion = "0200";
		this.headerCoding = "1";
		
		cdtrInf(cdtrInf);
		amount(money, currency);
		ultmtDbtr(ultmtDbtr);
		
		// default remark value
		this.referenceType = "NON";
		this.referenceTrailer = "EPD";
	}
	
	public QRBillDataBuilder cdtrInf(IContact contact){
		this.cdtrInfContact = contact;
		return this;
	}
	
	public QRBillDataBuilder amount(Money money, String currency) {
		this.amount = money;
		this.amountCurrency = currency;
		return this;
	}
	
	public QRBillDataBuilder ultmtDbtr(IContact contact){
		this.ultmtDbtrContact = contact;
		return this;
	}
	
	public QRBillDataBuilder reference(String reference){
		this.referenceType = "SCOR";
		this.reference = reference;
		return this;
	}
	
	public QRBillDataBuilder unstructuredRemark(String remark){
		this.referenceUnstructuredRemark = remark;
		return this;
	}
	
	public QRBillData build(){
		QRBillData ret = new QRBillData();
		
		ret.setHeaderQRType(headerQRType);
		ret.setHeaderVersion(headerVersion);
		ret.setHeaderCoding(headerCoding);
		
		ret.setCdtrInfIBAN(StringUtils.defaultString((String) cdtrInfContact.getExtInfo("IBAN")));
		setAddress(ret, "cdtrInf", cdtrInfContact);
		
		ret.setCcyAmtAmt(amount.getAmountAsString().replaceAll(",", "."));
		ret.setCcyAmtCcy(amountCurrency);
		
		setAddress(ret, "ultmtDbtr", ultmtDbtrContact);

		ret.setRmtInfTp(referenceType);
		ret.setRmtInfRef(reference);
		
		ret.setRmtInfUstrd(referenceUnstructuredRemark);
		ret.setRmtInfTrailer(referenceTrailer);
		
		return ret;
	}
	
	private void setAddress(QRBillData qrBillData, String prefix, IContact contact){
		try {
			BeanUtils.setProperty(qrBillData, prefix + "AdrTp", "K");
			
			BeanUtils.setProperty(qrBillData, prefix + "Name",
				AddressFormatUtil.getFullnameWithSalutation(contact).replaceAll("\n", " ").trim());
			
			BeanUtils.setProperty(qrBillData, prefix + "StrtNmOrAdrLine1",
				contact.getStreet().trim());
			
			BeanUtils.setProperty(qrBillData, prefix + "StrtNmOrAdrLine2",
				contact.getZip().trim() + " " + contact.getCity().trim());
			
			BeanUtils.setProperty(qrBillData, prefix + "Ctry",
				contact.getCountry());
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Error setting contact", e);
		}
	}
}
