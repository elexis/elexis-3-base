//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.18 at 01:35:39 PM CET 
//

package ch.fd.invoice400.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for payantType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="payantType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="biller" type="{http://www.xmlData.ch/xmlInvoice/XSD}billerAddressType"/>
 *         &lt;element name="provider" type="{http://www.xmlData.ch/xmlInvoice/XSD}providerAddressType"/>
 *         &lt;element name="insurance" type="{http://www.xmlData.ch/xmlInvoice/XSD}insuranceAddressType"/>
 *         &lt;element name="patient" type="{http://www.xmlData.ch/xmlInvoice/XSD}patientAddressType"/>
 *         &lt;element name="guarantor" type="{http://www.xmlData.ch/xmlInvoice/XSD}guarantorAddressType"/>
 *         &lt;element name="referrer" type="{http://www.xmlData.ch/xmlInvoice/XSD}referrerAddressType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="invoice_modification" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="purpose" default="invoice">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="invoice"/>
 *             &lt;enumeration value="annulment"/>
 *             &lt;enumeration value="creditAdvice"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payantType", propOrder = {
	"biller", "provider", "insurance", "patient", "guarantor", "referrer"
})
public class PayantType {
	
	@XmlElement(required = true)
	protected BillerAddressType biller;
	@XmlElement(required = true)
	protected ProviderAddressType provider;
	@XmlElement(required = true)
	protected InsuranceAddressType insurance;
	@XmlElement(required = true)
	protected PatientAddressType patient;
	@XmlElement(required = true)
	protected GuarantorAddressType guarantor;
	protected ReferrerAddressType referrer;
	@XmlAttribute(name = "invoice_modification")
	protected Boolean invoiceModification;
	@XmlAttribute(name = "purpose")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String purpose;
	
	/**
	 * Gets the value of the biller property.
	 * 
	 * @return possible object is {@link BillerAddressType }
	 * 
	 */
	public BillerAddressType getBiller(){
		return biller;
	}
	
	/**
	 * Sets the value of the biller property.
	 * 
	 * @param value
	 *            allowed object is {@link BillerAddressType }
	 * 
	 */
	public void setBiller(BillerAddressType value){
		this.biller = value;
	}
	
	/**
	 * Gets the value of the provider property.
	 * 
	 * @return possible object is {@link ProviderAddressType }
	 * 
	 */
	public ProviderAddressType getProvider(){
		return provider;
	}
	
	/**
	 * Sets the value of the provider property.
	 * 
	 * @param value
	 *            allowed object is {@link ProviderAddressType }
	 * 
	 */
	public void setProvider(ProviderAddressType value){
		this.provider = value;
	}
	
	/**
	 * Gets the value of the insurance property.
	 * 
	 * @return possible object is {@link InsuranceAddressType }
	 * 
	 */
	public InsuranceAddressType getInsurance(){
		return insurance;
	}
	
	/**
	 * Sets the value of the insurance property.
	 * 
	 * @param value
	 *            allowed object is {@link InsuranceAddressType }
	 * 
	 */
	public void setInsurance(InsuranceAddressType value){
		this.insurance = value;
	}
	
	/**
	 * Gets the value of the patient property.
	 * 
	 * @return possible object is {@link PatientAddressType }
	 * 
	 */
	public PatientAddressType getPatient(){
		return patient;
	}
	
	/**
	 * Sets the value of the patient property.
	 * 
	 * @param value
	 *            allowed object is {@link PatientAddressType }
	 * 
	 */
	public void setPatient(PatientAddressType value){
		this.patient = value;
	}
	
	/**
	 * Gets the value of the guarantor property.
	 * 
	 * @return possible object is {@link GuarantorAddressType }
	 * 
	 */
	public GuarantorAddressType getGuarantor(){
		return guarantor;
	}
	
	/**
	 * Sets the value of the guarantor property.
	 * 
	 * @param value
	 *            allowed object is {@link GuarantorAddressType }
	 * 
	 */
	public void setGuarantor(GuarantorAddressType value){
		this.guarantor = value;
	}
	
	/**
	 * Gets the value of the referrer property.
	 * 
	 * @return possible object is {@link ReferrerAddressType }
	 * 
	 */
	public ReferrerAddressType getReferrer(){
		return referrer;
	}
	
	/**
	 * Sets the value of the referrer property.
	 * 
	 * @param value
	 *            allowed object is {@link ReferrerAddressType }
	 * 
	 */
	public void setReferrer(ReferrerAddressType value){
		this.referrer = value;
	}
	
	/**
	 * Gets the value of the invoiceModification property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isInvoiceModification(){
		if (invoiceModification == null) {
			return false;
		} else {
			return invoiceModification;
		}
	}
	
	/**
	 * Sets the value of the invoiceModification property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setInvoiceModification(Boolean value){
		this.invoiceModification = value;
	}
	
	/**
	 * Gets the value of the purpose property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPurpose(){
		if (purpose == null) {
			return "invoice";
		} else {
			return purpose;
		}
	}
	
	/**
	 * Sets the value of the purpose property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPurpose(String value){
		this.purpose = value;
	}
	
}
