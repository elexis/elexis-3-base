//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import at.medevit.elexis.tarmed.model.jaxb.DoubleToStringAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für balanceTGType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="balanceTGType">
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
@XmlType(name = "balanceTGType", propOrder = {
    "vat"
})
public class BalanceTGType {

    @XmlElement(required = true)
    protected VatType vat;
    @XmlAttribute(name = "currency")
    protected String currency;
    @XmlAttribute(name = "amount", required = true)
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
	protected Double amount;
    @XmlAttribute(name = "amount_reminder")
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
    protected Double amountReminder;
    @XmlAttribute(name = "amount_prepaid")
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
    protected Double amountPrepaid;
    @XmlAttribute(name = "amount_due", required = true)
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
	protected Double amountDue;
    @XmlAttribute(name = "amount_obligations")
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
    protected Double amountObligations;

    /**
     * Ruft den Wert der vat-Eigenschaft ab.
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
     * Legt den Wert der vat-Eigenschaft fest.
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
     * Ruft den Wert der currency-Eigenschaft ab.
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
     * Legt den Wert der currency-Eigenschaft fest.
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
     * Ruft den Wert der amount-Eigenschaft ab.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Legt den Wert der amount-Eigenschaft fest.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Ruft den Wert der amountReminder-Eigenschaft ab.
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
     * Legt den Wert der amountReminder-Eigenschaft fest.
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
     * Ruft den Wert der amountPrepaid-Eigenschaft ab.
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
     * Legt den Wert der amountPrepaid-Eigenschaft fest.
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
     * Ruft den Wert der amountDue-Eigenschaft ab.
     * 
     */
    public double getAmountDue() {
        return amountDue;
    }

    /**
     * Legt den Wert der amountDue-Eigenschaft fest.
     * 
     */
    public void setAmountDue(double value) {
        this.amountDue = value;
    }

    /**
     * Ruft den Wert der amountObligations-Eigenschaft ab.
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
     * Legt den Wert der amountObligations-Eigenschaft fest.
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
