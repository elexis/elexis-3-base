//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="medikamentA" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string50" /&gt;
 *       &lt;attribute name="medikamentB" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string50" /&gt;
 *       &lt;attribute name="relevanzText" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string30" /&gt;
 *       &lt;attribute name="interaktionseffekt" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string120" /&gt;
 *       &lt;attribute name="pharmacodeA" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}pharmaCode" /&gt;
 *       &lt;attribute name="pharmacodeB" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}pharmaCode" /&gt;
 *       &lt;attribute name="ausstellungsdatumA" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *       &lt;attribute name="ausstellungsdatumB" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *       &lt;attribute name="wirkstoffgruppe1" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string60" /&gt;
 *       &lt;attribute name="wirkstoffgruppe2" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string60" /&gt;
 *       &lt;attribute name="status" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string30" /&gt;
 *       &lt;attribute name="docKey" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *       &lt;attribute name="relevanz" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "interaction")
public class Interaction {

    @XmlAttribute(name = "medikamentA", required = true)
    protected String medikamentA;
    @XmlAttribute(name = "medikamentB", required = true)
    protected String medikamentB;
    @XmlAttribute(name = "relevanzText", required = true)
    protected String relevanzText;
    @XmlAttribute(name = "interaktionseffekt", required = true)
    protected String interaktionseffekt;
    @XmlAttribute(name = "pharmacodeA", required = true)
    protected String pharmacodeA;
    @XmlAttribute(name = "pharmacodeB", required = true)
    protected String pharmacodeB;
    @XmlAttribute(name = "ausstellungsdatumA", required = true)
    protected String ausstellungsdatumA;
    @XmlAttribute(name = "ausstellungsdatumB", required = true)
    protected String ausstellungsdatumB;
    @XmlAttribute(name = "wirkstoffgruppe1", required = true)
    protected String wirkstoffgruppe1;
    @XmlAttribute(name = "wirkstoffgruppe2", required = true)
    protected String wirkstoffgruppe2;
    @XmlAttribute(name = "status", required = true)
    protected String status;
    @XmlAttribute(name = "docKey", required = true)
    protected int docKey;
    @XmlAttribute(name = "relevanz", required = true)
    protected int relevanz;

    /**
     * Ruft den Wert der medikamentA-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedikamentA() {
        return medikamentA;
    }

    /**
     * Legt den Wert der medikamentA-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedikamentA(String value) {
        this.medikamentA = value;
    }

    /**
     * Ruft den Wert der medikamentB-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedikamentB() {
        return medikamentB;
    }

    /**
     * Legt den Wert der medikamentB-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedikamentB(String value) {
        this.medikamentB = value;
    }

    /**
     * Ruft den Wert der relevanzText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRelevanzText() {
        return relevanzText;
    }

    /**
     * Legt den Wert der relevanzText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRelevanzText(String value) {
        this.relevanzText = value;
    }

    /**
     * Ruft den Wert der interaktionseffekt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInteraktionseffekt() {
        return interaktionseffekt;
    }

    /**
     * Legt den Wert der interaktionseffekt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInteraktionseffekt(String value) {
        this.interaktionseffekt = value;
    }

    /**
     * Ruft den Wert der pharmacodeA-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPharmacodeA() {
        return pharmacodeA;
    }

    /**
     * Legt den Wert der pharmacodeA-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPharmacodeA(String value) {
        this.pharmacodeA = value;
    }

    /**
     * Ruft den Wert der pharmacodeB-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPharmacodeB() {
        return pharmacodeB;
    }

    /**
     * Legt den Wert der pharmacodeB-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPharmacodeB(String value) {
        this.pharmacodeB = value;
    }

    /**
     * Ruft den Wert der ausstellungsdatumA-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusstellungsdatumA() {
        return ausstellungsdatumA;
    }

    /**
     * Legt den Wert der ausstellungsdatumA-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusstellungsdatumA(String value) {
        this.ausstellungsdatumA = value;
    }

    /**
     * Ruft den Wert der ausstellungsdatumB-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAusstellungsdatumB() {
        return ausstellungsdatumB;
    }

    /**
     * Legt den Wert der ausstellungsdatumB-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAusstellungsdatumB(String value) {
        this.ausstellungsdatumB = value;
    }

    /**
     * Ruft den Wert der wirkstoffgruppe1-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWirkstoffgruppe1() {
        return wirkstoffgruppe1;
    }

    /**
     * Legt den Wert der wirkstoffgruppe1-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWirkstoffgruppe1(String value) {
        this.wirkstoffgruppe1 = value;
    }

    /**
     * Ruft den Wert der wirkstoffgruppe2-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWirkstoffgruppe2() {
        return wirkstoffgruppe2;
    }

    /**
     * Legt den Wert der wirkstoffgruppe2-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWirkstoffgruppe2(String value) {
        this.wirkstoffgruppe2 = value;
    }

    /**
     * Ruft den Wert der status-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Legt den Wert der status-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Ruft den Wert der docKey-Eigenschaft ab.
     * 
     */
    public int getDocKey() {
        return docKey;
    }

    /**
     * Legt den Wert der docKey-Eigenschaft fest.
     * 
     */
    public void setDocKey(int value) {
        this.docKey = value;
    }

    /**
     * Ruft den Wert der relevanz-Eigenschaft ab.
     * 
     */
    public int getRelevanz() {
        return relevanz;
    }

    /**
     * Legt den Wert der relevanz-Eigenschaft fest.
     * 
     */
    public void setRelevanz(int value) {
        this.relevanz = value;
    }

}
