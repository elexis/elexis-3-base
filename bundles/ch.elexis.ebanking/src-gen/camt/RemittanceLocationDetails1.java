//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für RemittanceLocationDetails1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="RemittanceLocationDetails1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Mtd" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}RemittanceLocationMethod2Code"/>
 *         &lt;element name="ElctrncAdr" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Max2048Text" minOccurs="0"/>
 *         &lt;element name="PstlAdr" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}NameAndAddress10" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemittanceLocationDetails1", propOrder = {
    "mtd",
    "elctrncAdr",
    "pstlAdr"
})
public class RemittanceLocationDetails1 {

    @XmlElement(name = "Mtd", required = true)
    @XmlSchemaType(name = "string")
    protected RemittanceLocationMethod2Code mtd;
    @XmlElement(name = "ElctrncAdr")
    protected String elctrncAdr;
    @XmlElement(name = "PstlAdr")
    protected NameAndAddress10 pstlAdr;

    /**
     * Ruft den Wert der mtd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceLocationMethod2Code }
     *     
     */
    public RemittanceLocationMethod2Code getMtd() {
        return mtd;
    }

    /**
     * Legt den Wert der mtd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceLocationMethod2Code }
     *     
     */
    public void setMtd(RemittanceLocationMethod2Code value) {
        this.mtd = value;
    }

    /**
     * Ruft den Wert der elctrncAdr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElctrncAdr() {
        return elctrncAdr;
    }

    /**
     * Legt den Wert der elctrncAdr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElctrncAdr(String value) {
        this.elctrncAdr = value;
    }

    /**
     * Ruft den Wert der pstlAdr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NameAndAddress10 }
     *     
     */
    public NameAndAddress10 getPstlAdr() {
        return pstlAdr;
    }

    /**
     * Legt den Wert der pstlAdr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NameAndAddress10 }
     *     
     */
    public void setPstlAdr(NameAndAddress10 value) {
        this.pstlAdr = value;
    }

}
