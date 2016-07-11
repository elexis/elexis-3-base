//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.07.11 um 09:24:40 AM CEST 
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
 *       &lt;attribute name="smoke" use="required" type="{}tSmokerType" />
 *       &lt;attribute name="death" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="doctorPatientRelation" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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

    @XmlAttribute(name = "smoke", required = true)
    protected short smoke;
    @XmlAttribute(name = "death", required = true)
    protected boolean death;
    @XmlAttribute(name = "doctorPatientRelation", required = true)
    protected boolean doctorPatientRelation;

    /**
     * Ruft den Wert der smoke-Eigenschaft ab.
     * 
     */
    public short getSmoke() {
        return smoke;
    }

    /**
     * Legt den Wert der smoke-Eigenschaft fest.
     * 
     */
    public void setSmoke(short value) {
        this.smoke = value;
    }

    /**
     * Ruft den Wert der death-Eigenschaft ab.
     * 
     */
    public boolean isDeath() {
        return death;
    }

    /**
     * Legt den Wert der death-Eigenschaft fest.
     * 
     */
    public void setDeath(boolean value) {
        this.death = value;
    }

    /**
     * Ruft den Wert der doctorPatientRelation-Eigenschaft ab.
     * 
     */
    public boolean isDoctorPatientRelation() {
        return doctorPatientRelation;
    }

    /**
     * Legt den Wert der doctorPatientRelation-Eigenschaft fest.
     * 
     */
    public void setDoctorPatientRelation(boolean value) {
        this.doctorPatientRelation = value;
    }

}
