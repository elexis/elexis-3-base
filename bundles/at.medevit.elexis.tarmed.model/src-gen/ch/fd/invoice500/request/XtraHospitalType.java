//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für xtraHospitalType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="xtraHospitalType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="ambulatory" type="{http://www.forum-datenaustausch.ch/invoice}xtraAmbulatoryType"/&gt;
 *         &lt;element name="stationary" type="{http://www.forum-datenaustausch.ch/invoice}xtraStationaryType"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
