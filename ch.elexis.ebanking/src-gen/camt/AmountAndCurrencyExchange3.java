//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AmountAndCurrencyExchange3 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AmountAndCurrencyExchange3">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="InstdAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountAndCurrencyExchangeDetails3" minOccurs="0"/>
 *         &lt;element name="TxAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountAndCurrencyExchangeDetails3" minOccurs="0"/>
 *         &lt;element name="CntrValAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountAndCurrencyExchangeDetails3" minOccurs="0"/>
 *         &lt;element name="AnncdPstngAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountAndCurrencyExchangeDetails3" minOccurs="0"/>
 *         &lt;element name="PrtryAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountAndCurrencyExchangeDetails4" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountAndCurrencyExchange3", propOrder = {
    "instdAmt",
    "txAmt",
    "cntrValAmt",
    "anncdPstngAmt",
    "prtryAmt"
})
public class AmountAndCurrencyExchange3 {

    @XmlElement(name = "InstdAmt")
    protected AmountAndCurrencyExchangeDetails3 instdAmt;
    @XmlElement(name = "TxAmt")
    protected AmountAndCurrencyExchangeDetails3 txAmt;
    @XmlElement(name = "CntrValAmt")
    protected AmountAndCurrencyExchangeDetails3 cntrValAmt;
    @XmlElement(name = "AnncdPstngAmt")
    protected AmountAndCurrencyExchangeDetails3 anncdPstngAmt;
    @XmlElement(name = "PrtryAmt")
    protected List<AmountAndCurrencyExchangeDetails4> prtryAmt;

    /**
     * Ruft den Wert der instdAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public AmountAndCurrencyExchangeDetails3 getInstdAmt() {
        return instdAmt;
    }

    /**
     * Legt den Wert der instdAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public void setInstdAmt(AmountAndCurrencyExchangeDetails3 value) {
        this.instdAmt = value;
    }

    /**
     * Ruft den Wert der txAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public AmountAndCurrencyExchangeDetails3 getTxAmt() {
        return txAmt;
    }

    /**
     * Legt den Wert der txAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public void setTxAmt(AmountAndCurrencyExchangeDetails3 value) {
        this.txAmt = value;
    }

    /**
     * Ruft den Wert der cntrValAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public AmountAndCurrencyExchangeDetails3 getCntrValAmt() {
        return cntrValAmt;
    }

    /**
     * Legt den Wert der cntrValAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public void setCntrValAmt(AmountAndCurrencyExchangeDetails3 value) {
        this.cntrValAmt = value;
    }

    /**
     * Ruft den Wert der anncdPstngAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public AmountAndCurrencyExchangeDetails3 getAnncdPstngAmt() {
        return anncdPstngAmt;
    }

    /**
     * Legt den Wert der anncdPstngAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountAndCurrencyExchangeDetails3 }
     *     
     */
    public void setAnncdPstngAmt(AmountAndCurrencyExchangeDetails3 value) {
        this.anncdPstngAmt = value;
    }

    /**
     * Gets the value of the prtryAmt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prtryAmt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrtryAmt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AmountAndCurrencyExchangeDetails4 }
     * 
     * 
     */
    public List<AmountAndCurrencyExchangeDetails4> getPrtryAmt() {
        if (prtryAmt == null) {
            prtryAmt = new ArrayList<AmountAndCurrencyExchangeDetails4>();
        }
        return this.prtryAmt;
    }

}
