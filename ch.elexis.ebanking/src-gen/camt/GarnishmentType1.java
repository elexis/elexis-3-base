//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für GarnishmentType1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GarnishmentType1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CdOrPrtry" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}GarnishmentType1Choice"/>
 *         &lt;element name="Issr" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Max35Text" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GarnishmentType1", propOrder = {
    "cdOrPrtry",
    "issr"
})
public class GarnishmentType1 {

    @XmlElement(name = "CdOrPrtry", required = true)
    protected GarnishmentType1Choice cdOrPrtry;
    @XmlElement(name = "Issr")
    protected String issr;

    /**
     * Ruft den Wert der cdOrPrtry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GarnishmentType1Choice }
     *     
     */
    public GarnishmentType1Choice getCdOrPrtry() {
        return cdOrPrtry;
    }

    /**
     * Legt den Wert der cdOrPrtry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GarnishmentType1Choice }
     *     
     */
    public void setCdOrPrtry(GarnishmentType1Choice value) {
        this.cdOrPrtry = value;
    }

    /**
     * Ruft den Wert der issr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssr() {
        return issr;
    }

    /**
     * Legt den Wert der issr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssr(String value) {
        this.issr = value;
    }

}
