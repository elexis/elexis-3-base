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
 *       &lt;attribute name="eanId" type="{http://estudio.clustertec.ch/schemas/prescription}eanId" /&gt;
 *       &lt;attribute name="bsvNr" type="{http://estudio.clustertec.ch/schemas/prescription}string7" /&gt;
 *       &lt;attribute name="insuranceName" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *       &lt;attribute name="billingType" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}billingType" /&gt;
 *       &lt;attribute name="insureeNr" type="{http://estudio.clustertec.ch/schemas/prescription}string20" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "insurance")
public class Insurance {

    @XmlAttribute(name = "eanId")
    protected Long eanId;
    @XmlAttribute(name = "bsvNr")
    protected String bsvNr;
    @XmlAttribute(name = "insuranceName")
    protected String insuranceName;
    @XmlAttribute(name = "billingType", required = true)
    protected int billingType;
    @XmlAttribute(name = "insureeNr")
    protected String insureeNr;

    /**
     * Ruft den Wert der eanId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEanId() {
        return eanId;
    }

    /**
     * Legt den Wert der eanId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEanId(Long value) {
        this.eanId = value;
    }

    /**
     * Ruft den Wert der bsvNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBsvNr() {
        return bsvNr;
    }

    /**
     * Legt den Wert der bsvNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBsvNr(String value) {
        this.bsvNr = value;
    }

    /**
     * Ruft den Wert der insuranceName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsuranceName() {
        return insuranceName;
    }

    /**
     * Legt den Wert der insuranceName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsuranceName(String value) {
        this.insuranceName = value;
    }

    /**
     * Ruft den Wert der billingType-Eigenschaft ab.
     * 
     */
    public int getBillingType() {
        return billingType;
    }

    /**
     * Legt den Wert der billingType-Eigenschaft fest.
     * 
     */
    public void setBillingType(int value) {
        this.billingType = value;
    }

    /**
     * Ruft den Wert der insureeNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInsureeNr() {
        return insureeNr;
    }

    /**
     * Legt den Wert der insureeNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInsureeNr(String value) {
        this.insureeNr = value;
    }

}
