//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ImpliedCurrencyAmountRangeChoice complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ImpliedCurrencyAmountRangeChoice">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="FrAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountRangeBoundary1"/>
 *         &lt;element name="ToAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}AmountRangeBoundary1"/>
 *         &lt;element name="FrToAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}FromToAmountRange"/>
 *         &lt;element name="EQAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}ImpliedCurrencyAndAmount"/>
 *         &lt;element name="NEQAmt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}ImpliedCurrencyAndAmount"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImpliedCurrencyAmountRangeChoice", propOrder = {
    "frAmt",
    "toAmt",
    "frToAmt",
    "eqAmt",
    "neqAmt"
})
public class ImpliedCurrencyAmountRangeChoice {

    @XmlElement(name = "FrAmt")
    protected AmountRangeBoundary1 frAmt;
    @XmlElement(name = "ToAmt")
    protected AmountRangeBoundary1 toAmt;
    @XmlElement(name = "FrToAmt")
    protected FromToAmountRange frToAmt;
    @XmlElement(name = "EQAmt")
    protected BigDecimal eqAmt;
    @XmlElement(name = "NEQAmt")
    protected BigDecimal neqAmt;

    /**
     * Ruft den Wert der frAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public AmountRangeBoundary1 getFrAmt() {
        return frAmt;
    }

    /**
     * Legt den Wert der frAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public void setFrAmt(AmountRangeBoundary1 value) {
        this.frAmt = value;
    }

    /**
     * Ruft den Wert der toAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public AmountRangeBoundary1 getToAmt() {
        return toAmt;
    }

    /**
     * Legt den Wert der toAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountRangeBoundary1 }
     *     
     */
    public void setToAmt(AmountRangeBoundary1 value) {
        this.toAmt = value;
    }

    /**
     * Ruft den Wert der frToAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FromToAmountRange }
     *     
     */
    public FromToAmountRange getFrToAmt() {
        return frToAmt;
    }

    /**
     * Legt den Wert der frToAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FromToAmountRange }
     *     
     */
    public void setFrToAmt(FromToAmountRange value) {
        this.frToAmt = value;
    }

    /**
     * Ruft den Wert der eqAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getEQAmt() {
        return eqAmt;
    }

    /**
     * Legt den Wert der eqAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setEQAmt(BigDecimal value) {
        this.eqAmt = value;
    }

    /**
     * Ruft den Wert der neqAmt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getNEQAmt() {
        return neqAmt;
    }

    /**
     * Legt den Wert der neqAmt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setNEQAmt(BigDecimal value) {
        this.neqAmt = value;
    }

}
