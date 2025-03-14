//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.18 at 03:48:09 PM CET 
//


package ch.fd.invoice440.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for balanceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="balanceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vat" type="{http://www.forum-datenaustausch.ch/invoice}vatType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="currency" type="{http://www.w3.org/2001/XMLSchema}string" fixed="CHF" />
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="amount_reminder" type="{http://www.w3.org/2001/XMLSchema}double" default="0" />
 *       &lt;attribute name="amount_prepaid" type="{http://www.w3.org/2001/XMLSchema}double" default="0" />
 *       &lt;attribute name="amount_due" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="amount_obligations" type="{http://www.w3.org/2001/XMLSchema}double" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "balanceType", propOrder = {
    "vat"
})
public class BalanceType {

    @XmlElement(required = true)
    protected VatType vat;
    @XmlAttribute(name = "currency")
    protected String currency;
    @XmlAttribute(name = "amount", required = true)
    protected double amount;
    @XmlAttribute(name = "amount_reminder")
    protected Double amountReminder;
    @XmlAttribute(name = "amount_prepaid")
    protected Double amountPrepaid;
    @XmlAttribute(name = "amount_due", required = true)
    protected double amountDue;
    @XmlAttribute(name = "amount_obligations")
    protected Double amountObligations;

    /**
     * Gets the value of the vat property.
     * 
     * @return
     *     possible object is
     *     {@link VatType }
     *     
     */
    public VatType getVat() {
        return vat;
    }

    /**
     * Sets the value of the vat property.
     * 
     * @param value
     *     allowed object is
     *     {@link VatType }
     *     
     */
    public void setVat(VatType value) {
        this.vat = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        if (currency == null) {
            return "CHF";
        } else {
            return currency;
        }
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Gets the value of the amountReminder property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getAmountReminder() {
        if (amountReminder == null) {
            return  0.0D;
        } else {
            return amountReminder;
        }
    }

    /**
     * Sets the value of the amountReminder property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmountReminder(Double value) {
        this.amountReminder = value;
    }

    /**
     * Gets the value of the amountPrepaid property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getAmountPrepaid() {
        if (amountPrepaid == null) {
            return  0.0D;
        } else {
            return amountPrepaid;
        }
    }

    /**
     * Sets the value of the amountPrepaid property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmountPrepaid(Double value) {
        this.amountPrepaid = value;
    }

    /**
     * Gets the value of the amountDue property.
     * 
     */
    public double getAmountDue() {
        return amountDue;
    }

    /**
     * Sets the value of the amountDue property.
     * 
     */
    public void setAmountDue(double value) {
        this.amountDue = value;
    }

    /**
     * Gets the value of the amountObligations property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getAmountObligations() {
        if (amountObligations == null) {
            return  0.0D;
        } else {
            return amountObligations;
        }
    }

    /**
     * Sets the value of the amountObligations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmountObligations(Double value) {
        this.amountObligations = value;
    }

}
