package ch.elexis.ebanking.qr;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IContact;
import ch.elexis.core.model.format.AddressFormatUtil;
import ch.elexis.core.types.Country;
import ch.elexis.ebanking.qr.QRBillDataException.SourceType;
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
		this.amount.roundTo5();
		this.amountCurrency = currency;
		return this;
	}
	
	public QRBillDataBuilder ultmtDbtr(IContact contact){
		this.ultmtDbtrContact = contact;
		return this;
	}
	
	public QRBillDataBuilder reference(String reference){
		this.reference = reference;
		if (StringUtils.isNotBlank(reference)) {
			if (reference.length() == 27) {
				this.referenceType = "QRR";
			} else if (reference.length() <= 25) {
				this.referenceType = "SCOR";
			}
		}
		return this;
	}
	
	public QRBillDataBuilder unstructuredRemark(String remark){
		this.referenceUnstructuredRemark = remark;
		return this;
	}
	
	public QRBillData build() throws QRBillDataException{
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
		
		ret.setRmtInfUstrd(StringUtils.defaultString(referenceUnstructuredRemark));
		ret.setRmtInfTrailer(referenceTrailer);
		
		return ret;
	}
	
	private void setAddress(QRBillData qrBillData, String prefix, IContact contact)
		throws QRBillDataException{
		try {
			BeanUtils.setProperty(qrBillData, prefix + "AdrTp", "K");
			
			BeanUtils.setProperty(qrBillData, prefix + "Name",
				AddressFormatUtil.getFullnameWithSalutation(contact).replaceAll("\n", " ").trim());
			
			BeanUtils.setProperty(qrBillData, prefix + "StrtNmOrAdrLine1",
				contact.getStreet().trim());
			
			BeanUtils.setProperty(qrBillData, prefix + "StrtNmOrAdrLine2",
				StringUtils.left(contact.getZip().trim() + " " + contact.getCity().trim(), 16));
			
			Country country = contact.getCountry();
			if (Country.NDF == country) {
				country = Country.CH;
			}
			BeanUtils.setProperty(qrBillData, prefix + "Ctry",
				StringUtils.left(country.toString(), 2));
		} catch (IllegalAccessException | InvocationTargetException e) {
			Throwable cause = e.getCause();
			if (cause != null && cause instanceof QRBillDataException) {
				((QRBillDataException) cause).setContact(contact);
				throw (QRBillDataException) cause;
			} else {
				throw new QRBillDataException(SourceType.UNKNOWN, e.getMessage());
			}
		}
	}
}
