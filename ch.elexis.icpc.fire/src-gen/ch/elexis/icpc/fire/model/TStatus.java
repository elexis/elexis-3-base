//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.02.21 um 02:32:18 PM CET 
//


package ch.elexis.icpc.fire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für tStatus complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="tStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="smoke" type="{}tSmokerType" />
 *       &lt;attribute name="death" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="mc" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="insurer" type="{}tString255" />
 *       &lt;attribute name="doctorPatientRelation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tStatus")
public class TStatus {

    @XmlAttribute(name = "smoke")
    protected Short smoke;
    @XmlAttribute(name = "death")
    protected Boolean death;
    @XmlAttribute(name = "mc")
    protected Boolean mc;
    @XmlAttribute(name = "insurer")
    protected String insurer;
    @XmlAttribute(name = "doctorPatientRelation")
    protected Boolean doctorPatientRelation;

    /**
     * Ruft den Wert der smoke-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getSmoke() {
        return smoke;
    }

    /**
     * Legt den Wert der smoke-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setSmoke(Short value) {
        this.smoke = value;
    }

    /**
     * Ruft den Wert der death-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeath() {
        return death;
    }

    /**
     * Legt den Wert der death-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeath(Boolean value) {
        this.death = value;
    }

    /**
     * Ruft den Wert der mc-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isMc() {
        return mc;
    }

    /**
     * Legt den Wert der mc-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setMc(Boolean value) {
        this.mc = value;
    }

    /**
     * Ruft den Wert der insurer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsurer() {
        return insurer;
    }

    /**
     * Legt den Wert der insurer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsurer(String value) {
        this.insurer = value;
    }

    /**
     * Ruft den Wert der doctorPatientRelation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDoctorPatientRelation() {
        return doctorPatientRelation;
    }

    /**
     * Legt den Wert der doctorPatientRelation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDoctorPatientRelation(Boolean value) {
        this.doctorPatientRelation = value;
    }

}
