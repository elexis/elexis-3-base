//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CurrencyExchangeRateInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CurrencyExchangeRateInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="currencyConversionFromCode" type="{urn:gs1:shared:shared_common:xsd:3}CurrencyCodeType"/&gt;
 *         &lt;element name="currencyConversionToCode" type="{urn:gs1:shared:shared_common:xsd:3}CurrencyCodeType"/&gt;
 *         &lt;element name="exchangeRate" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="exchangeRateDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CurrencyExchangeRateInformationType", propOrder = {
    "currencyConversionFromCode",
    "currencyConversionToCode",
    "exchangeRate",
    "exchangeRateDateTime"
})
public class CurrencyExchangeRateInformationType {

    @XmlElement(required = true)
    protected CurrencyCodeType currencyConversionFromCode;
    @XmlElement(required = true)
    protected CurrencyCodeType currencyConversionToCode;
    protected Float exchangeRate;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar exchangeRateDateTime;

    /**
     * Ruft den Wert der currencyConversionFromCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *     
     */
    public CurrencyCodeType getCurrencyConversionFromCode() {
        return currencyConversionFromCode;
    }

    /**
     * Legt den Wert der currencyConversionFromCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *     
     */
    public void setCurrencyConversionFromCode(CurrencyCodeType value) {
        this.currencyConversionFromCode = value;
    }

    /**
     * Ruft den Wert der currencyConversionToCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *     
     */
    public CurrencyCodeType getCurrencyConversionToCode() {
        return currencyConversionToCode;
    }

    /**
     * Legt den Wert der currencyConversionToCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *     
     */
    public void setCurrencyConversionToCode(CurrencyCodeType value) {
        this.currencyConversionToCode = value;
    }

    /**
     * Ruft den Wert der exchangeRate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getExchangeRate() {
        return exchangeRate;
    }

    /**
     * Legt den Wert der exchangeRate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setExchangeRate(Float value) {
        this.exchangeRate = value;
    }

    /**
     * Ruft den Wert der exchangeRateDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExchangeRateDateTime() {
        return exchangeRateDateTime;
    }

    /**
     * Legt den Wert der exchangeRateDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExchangeRateDateTime(XMLGregorianCalendar value) {
        this.exchangeRateDateTime = value;
    }

}
