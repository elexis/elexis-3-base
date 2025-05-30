//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.13 at 12:17:21 PM MEZ 
//

package ch.fd.invoice400.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for esrRedType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="esrRedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="payment_for" type="{http://www.xmlData.ch/xmlInvoice/XSD}bankAddressType" minOccurs="0"/>
 *         &lt;element name="in_favor_of" type="{http://www.xmlData.ch/xmlInvoice/XSD}bankAddressType" minOccurs="0"/>
 *         &lt;element name="payment_reason" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_35" maxOccurs="4" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="amount_due" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="bank_account_number" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_35" />
 *       &lt;attribute name="bank_clearing_number" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_35" />
 *       &lt;attribute name="coding_line1" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_70" />
 *       &lt;attribute name="coding_line2" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_70" />
 *       &lt;attribute name="participant_number" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_35" />
 *       &lt;attribute name="payment_to" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="postal_account"/>
 *             &lt;enumeration value="bank_account"/>
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
@XmlType(name = "esrRedType", propOrder = {
	"paymentFor", "inFavorOf", "paymentReason"
})
public class EsrRedType {
	
	@XmlElement(name = "payment_for", namespace = "http://www.xmlData.ch/xmlInvoice/XSD")
	protected BankAddressType paymentFor;
	@XmlElement(name = "in_favor_of", namespace = "http://www.xmlData.ch/xmlInvoice/XSD")
	protected BankAddressType inFavorOf;
	@XmlElement(name = "payment_reason", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected List<String> paymentReason;
	@XmlAttribute(name = "amount_due", required = true)
	protected double amountDue;
	@XmlAttribute(name = "bank_account_number")
	protected String bankAccountNumber;
	@XmlAttribute(name = "bank_clearing_number")
	protected String bankClearingNumber;
	@XmlAttribute(name = "coding_line1")
	protected String codingLine1;
	@XmlAttribute(name = "coding_line2")
	protected String codingLine2;
	@XmlAttribute(name = "participant_number")
	protected String participantNumber;
	@XmlAttribute(name = "payment_to", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String paymentTo;
	
	/**
	 * Gets the value of the paymentFor property.
	 * 
	 * @return possible object is {@link BankAddressType }
	 * 
	 */
	public BankAddressType getPaymentFor(){
		return paymentFor;
	}
	
	/**
	 * Sets the value of the paymentFor property.
	 * 
	 * @param value
	 *            allowed object is {@link BankAddressType }
	 * 
	 */
	public void setPaymentFor(BankAddressType value){
		this.paymentFor = value;
	}
	
	/**
	 * Gets the value of the inFavorOf property.
	 * 
	 * @return possible object is {@link BankAddressType }
	 * 
	 */
	public BankAddressType getInFavorOf(){
		return inFavorOf;
	}
	
	/**
	 * Sets the value of the inFavorOf property.
	 * 
	 * @param value
	 *            allowed object is {@link BankAddressType }
	 * 
	 */
	public void setInFavorOf(BankAddressType value){
		this.inFavorOf = value;
	}
	
	/**
	 * Gets the value of the paymentReason property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any
	 * modification you make to the returned list will be present inside the JAXB object. This is
	 * why there is not a <CODE>set</CODE> method for the paymentReason property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getPaymentReason().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getPaymentReason(){
		if (paymentReason == null) {
			paymentReason = new ArrayList<String>();
		}
		return this.paymentReason;
	}
	
	/**
	 * Gets the value of the amountDue property.
	 * 
	 */
	public double getAmountDue(){
		return amountDue;
	}
	
	/**
	 * Sets the value of the amountDue property.
	 * 
	 */
	public void setAmountDue(double value){
		this.amountDue = value;
	}
	
	/**
	 * Gets the value of the bankAccountNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBankAccountNumber(){
		return bankAccountNumber;
	}
	
	/**
	 * Sets the value of the bankAccountNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBankAccountNumber(String value){
		this.bankAccountNumber = value;
	}
	
	/**
	 * Gets the value of the bankClearingNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBankClearingNumber(){
		return bankClearingNumber;
	}
	
	/**
	 * Sets the value of the bankClearingNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBankClearingNumber(String value){
		this.bankClearingNumber = value;
	}
	
	/**
	 * Gets the value of the codingLine1 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodingLine1(){
		return codingLine1;
	}
	
	/**
	 * Sets the value of the codingLine1 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodingLine1(String value){
		this.codingLine1 = value;
	}
	
	/**
	 * Gets the value of the codingLine2 property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCodingLine2(){
		return codingLine2;
	}
	
	/**
	 * Sets the value of the codingLine2 property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCodingLine2(String value){
		this.codingLine2 = value;
	}
	
	/**
	 * Gets the value of the participantNumber property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getParticipantNumber(){
		return participantNumber;
	}
	
	/**
	 * Sets the value of the participantNumber property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setParticipantNumber(String value){
		this.participantNumber = value;
	}
	
	/**
	 * Gets the value of the paymentTo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getPaymentTo(){
		return paymentTo;
	}
	
	/**
	 * Sets the value of the paymentTo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setPaymentTo(String value){
		this.paymentTo = value;
	}
	
}
