//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://estudio.clustertec.ch/schemas/prescription}addressComplexType"&gt;
 *       &lt;attribute name="birthday" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *       &lt;attribute name="langCode" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}langCode" /&gt;
 *       &lt;attribute name="coverCardId" type="{http://estudio.clustertec.ch/schemas/prescription}string25" /&gt;
 *       &lt;attribute name="sex" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}sex" /&gt;
 *       &lt;attribute name="patientNr" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string15" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "patientAddress")
public class PatientAddress
    extends AddressComplexType
{

    @XmlAttribute(name = "birthday", required = true)
    protected String birthday;
    @XmlAttribute(name = "langCode", required = true)
    protected int langCode;
    @XmlAttribute(name = "coverCardId")
    protected String coverCardId;
    @XmlAttribute(name = "sex", required = true)
    protected int sex;
    @XmlAttribute(name = "patientNr", required = true)
    protected String patientNr;

    /**
     * Ruft den Wert der birthday-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBirthday() {
        return birthday;
    }

    /**
     * Legt den Wert der birthday-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBirthday(String value) {
        this.birthday = value;
    }

    /**
     * Ruft den Wert der langCode-Eigenschaft ab.
     * 
     */
    public int getLangCode() {
        return langCode;
    }

    /**
     * Legt den Wert der langCode-Eigenschaft fest.
     * 
     */
    public void setLangCode(int value) {
        this.langCode = value;
    }

    /**
     * Ruft den Wert der coverCardId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCoverCardId() {
        return coverCardId;
    }

    /**
     * Legt den Wert der coverCardId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCoverCardId(String value) {
        this.coverCardId = value;
    }

    /**
     * Ruft den Wert der sex-Eigenschaft ab.
     * 
     */
    public int getSex() {
        return sex;
    }

    /**
     * Legt den Wert der sex-Eigenschaft fest.
     * 
     */
    public void setSex(int value) {
        this.sex = value;
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

}
