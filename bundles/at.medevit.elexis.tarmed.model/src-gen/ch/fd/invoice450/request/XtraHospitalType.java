//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für xtraHospitalType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="xtraHospitalType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ambulatory" type="{http://www.forum-datenaustausch.ch/invoice}xtraAmbulatoryType"/>
 *         &lt;element name="stationary" type="{http://www.forum-datenaustausch.ch/invoice}xtraStationaryType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xtraHospitalType", propOrder = {
    "ambulatory",
    "stationary"
})
public class XtraHospitalType {

    protected XtraAmbulatoryType ambulatory;
    protected XtraStationaryType stationary;

    /**
     * Ruft den Wert der ambulatory-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XtraAmbulatoryType }
     *     
     */
    public XtraAmbulatoryType getAmbulatory() {
        return ambulatory;
    }

    /**
     * Legt den Wert der ambulatory-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XtraAmbulatoryType }
     *     
     */
    public void setAmbulatory(XtraAmbulatoryType value) {
        this.ambulatory = value;
    }

    /**
     * Ruft den Wert der stationary-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XtraStationaryType }
     *     
     */
    public XtraStationaryType getStationary() {
        return stationary;
    }

    /**
     * Legt den Wert der stationary-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XtraStationaryType }
     *     
     */
    public void setStationary(XtraStationaryType value) {
        this.stationary = value;
    }

}
