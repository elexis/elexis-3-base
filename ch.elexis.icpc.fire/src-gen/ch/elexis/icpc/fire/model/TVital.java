//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.07.03 um 12:21:08 PM CEST 
//


package ch.elexis.icpc.fire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für tVital complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="tVital">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="bpSyst" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *       &lt;attribute name="bpDiast" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *       &lt;attribute name="groesse" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="gewicht" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="puls" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *       &lt;attribute name="bauchumfang" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="bmi" type="{http://www.w3.org/2001/XMLSchema}float" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tVital")
public class TVital {

    @XmlAttribute(name = "bpSyst")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer bpSyst;
    @XmlAttribute(name = "bpDiast")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer bpDiast;
    @XmlAttribute(name = "groesse")
    protected Float groesse;
    @XmlAttribute(name = "gewicht")
    protected Float gewicht;
    @XmlAttribute(name = "puls")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer puls;
    @XmlAttribute(name = "bauchumfang")
    protected Float bauchumfang;
    @XmlAttribute(name = "bmi")
    protected Float bmi;

    /**
     * Ruft den Wert der bpSyst-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBpSyst() {
        return bpSyst;
    }

    /**
     * Legt den Wert der bpSyst-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBpSyst(Integer value) {
        this.bpSyst = value;
    }

    /**
     * Ruft den Wert der bpDiast-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBpDiast() {
        return bpDiast;
    }

    /**
     * Legt den Wert der bpDiast-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBpDiast(Integer value) {
        this.bpDiast = value;
    }

    /**
     * Ruft den Wert der groesse-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getGroesse() {
        return groesse;
    }

    /**
     * Legt den Wert der groesse-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setGroesse(Float value) {
        this.groesse = value;
    }

    /**
     * Ruft den Wert der gewicht-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getGewicht() {
        return gewicht;
    }

    /**
     * Legt den Wert der gewicht-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setGewicht(Float value) {
        this.gewicht = value;
    }

    /**
     * Ruft den Wert der puls-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPuls() {
        return puls;
    }

    /**
     * Legt den Wert der puls-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPuls(Integer value) {
        this.puls = value;
    }

    /**
     * Ruft den Wert der bauchumfang-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBauchumfang() {
        return bauchumfang;
    }

    /**
     * Legt den Wert der bauchumfang-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBauchumfang(Float value) {
        this.bauchumfang = value;
    }

    /**
     * Ruft den Wert der bmi-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getBmi() {
        return bmi;
    }

    /**
     * Legt den Wert der bmi-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setBmi(Float value) {
        this.bmi = value;
    }

}
