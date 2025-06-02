//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.09 um 01:08:44 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="patientOrderFlag" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://estudio.clustertec.ch/schemas/order}string10" /&gt;
 *       &lt;attribute name="mobileNr" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="lastNamePatient" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="firstNamePatient" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="birthdayPatient" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="patientNr" type="{http://estudio.clustertec.ch/schemas/order}string15" /&gt;
 *       &lt;attribute name="vendor" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="instanceId" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "patientOrderB2C")
public class PatientOrderB2C {

    @XmlAttribute(name = "patientOrderFlag", required = true)
    protected boolean patientOrderFlag;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "mobileNr")
    protected String mobileNr;
    @XmlAttribute(name = "lastNamePatient")
    protected String lastNamePatient;
    @XmlAttribute(name = "firstNamePatient")
    protected String firstNamePatient;
    @XmlAttribute(name = "birthdayPatient")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthdayPatient;
    @XmlAttribute(name = "patientNr")
    protected String patientNr;
    @XmlAttribute(name = "vendor")
    protected String vendor;
    @XmlAttribute(name = "instanceId")
    protected String instanceId;

    /**
     * Ruft den Wert der patientOrderFlag-Eigenschaft ab.
     * 
     */
    public boolean isPatientOrderFlag() {
        return patientOrderFlag;
    }

    /**
     * Legt den Wert der patientOrderFlag-Eigenschaft fest.
     * 
     */
    public void setPatientOrderFlag(boolean value) {
        this.patientOrderFlag = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der mobileNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobileNr() {
        return mobileNr;
    }

    /**
     * Legt den Wert der mobileNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobileNr(String value) {
        this.mobileNr = value;
    }

    /**
     * Ruft den Wert der lastNamePatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastNamePatient() {
        return lastNamePatient;
    }

    /**
     * Legt den Wert der lastNamePatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastNamePatient(String value) {
        this.lastNamePatient = value;
    }

    /**
     * Ruft den Wert der firstNamePatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstNamePatient() {
        return firstNamePatient;
    }

    /**
     * Legt den Wert der firstNamePatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstNamePatient(String value) {
        this.firstNamePatient = value;
    }

    /**
     * Ruft den Wert der birthdayPatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthdayPatient() {
        return birthdayPatient;
    }

    /**
     * Legt den Wert der birthdayPatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthdayPatient(XMLGregorianCalendar value) {
        this.birthdayPatient = value;
    }

    /**
     * Ruft den Wert der patientNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatientNr() {
        return patientNr;
    }

    /**
     * Legt den Wert der patientNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientNr(String value) {
        this.patientNr = value;
    }

    /**
     * Ruft den Wert der vendor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Legt den Wert der vendor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    /**
     * Ruft den Wert der instanceId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Legt den Wert der instanceId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceId(String value) {
        this.instanceId = value;
    }

}
