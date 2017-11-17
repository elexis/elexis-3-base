//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.11.17 um 10:56:23 AM CET 
//


package ch.elexis.icpc.fire.model;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import ch.elexis.icpc.fire.model.jaxb.BooleanAdapter;


/**
 * <p>Java-Klasse für tPatient complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="tPatient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="status" type="{}tStatus"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="birthYear" type="{http://www.w3.org/2001/XMLSchema}unsignedShort" />
 *       &lt;attribute name="gender" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="docId" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tPatient", propOrder = {
    "status"
})
public class TPatient {

    @XmlElement(required = true)
    protected TStatus status;
    @XmlAttribute(name = "id", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger id;
    @XmlAttribute(name = "birthYear")
    @XmlSchemaType(name = "unsignedShort")
    protected Integer birthYear;
    @XmlAttribute(name = "gender")
	@XmlJavaTypeAdapter(BooleanAdapter.class)
    protected Boolean gender;
    @XmlAttribute(name = "docId")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger docId;

    /**
	 * Ruft den Wert der status-Eigenschaft ab.
	 * 
	 * @return possible object is {@link TStatus }
	 * 
	 */
    public TStatus getStatus() {
        return status;
    }

    /**
     * Legt den Wert der status-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TStatus }
     *     
     */
    public void setStatus(TStatus value) {
        this.status = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setId(BigInteger value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der birthYear-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBirthYear() {
        return birthYear;
    }

    /**
     * Legt den Wert der birthYear-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBirthYear(Integer value) {
        this.birthYear = value;
    }

    /**
     * Ruft den Wert der gender-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isGender() {
        return gender;
    }

    /**
     * Legt den Wert der gender-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGender(Boolean value) {
        this.gender = value;
    }

    /**
     * Ruft den Wert der docId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDocId() {
        return docId;
    }

    /**
     * Legt den Wert der docId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDocId(BigInteger value) {
        this.docId = value;
    }

}
