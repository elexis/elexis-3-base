//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.Description80Type;
import gs1.shared.shared_common.xsd.IdentifierType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für DutyFeeTaxRegistrationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DutyFeeTaxRegistrationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dutyFeeTaxRegistrationID" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType"/&gt;
 *         &lt;element name="dutyFeeTaxTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}DutyFeeTaxTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxAgencyName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dutyFeeTaxDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxRegistrationType" type="{urn:gs1:ecom:ecom_common:xsd:3}DutyFeeTaxRegistrationTypeCodeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DutyFeeTaxRegistrationType", propOrder = {
    "dutyFeeTaxRegistrationID",
    "dutyFeeTaxTypeCode",
    "dutyFeeTaxAgencyName",
    "dutyFeeTaxDescription",
    "dutyFeeTaxRegistrationType"
})
public class DutyFeeTaxRegistrationType {

    @XmlElement(required = true)
    protected IdentifierType dutyFeeTaxRegistrationID;
    protected DutyFeeTaxTypeCodeType dutyFeeTaxTypeCode;
    protected String dutyFeeTaxAgencyName;
    protected Description80Type dutyFeeTaxDescription;
    protected DutyFeeTaxRegistrationTypeCodeType dutyFeeTaxRegistrationType;

    /**
     * Ruft den Wert der dutyFeeTaxRegistrationID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getDutyFeeTaxRegistrationID() {
        return dutyFeeTaxRegistrationID;
    }

    /**
     * Legt den Wert der dutyFeeTaxRegistrationID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setDutyFeeTaxRegistrationID(IdentifierType value) {
        this.dutyFeeTaxRegistrationID = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DutyFeeTaxTypeCodeType }
     *     
     */
    public DutyFeeTaxTypeCodeType getDutyFeeTaxTypeCode() {
        return dutyFeeTaxTypeCode;
    }

    /**
     * Legt den Wert der dutyFeeTaxTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DutyFeeTaxTypeCodeType }
     *     
     */
    public void setDutyFeeTaxTypeCode(DutyFeeTaxTypeCodeType value) {
        this.dutyFeeTaxTypeCode = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxAgencyName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDutyFeeTaxAgencyName() {
        return dutyFeeTaxAgencyName;
    }

    /**
     * Legt den Wert der dutyFeeTaxAgencyName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDutyFeeTaxAgencyName(String value) {
        this.dutyFeeTaxAgencyName = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getDutyFeeTaxDescription() {
        return dutyFeeTaxDescription;
    }

    /**
     * Legt den Wert der dutyFeeTaxDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setDutyFeeTaxDescription(Description80Type value) {
        this.dutyFeeTaxDescription = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxRegistrationType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DutyFeeTaxRegistrationTypeCodeType }
     *     
     */
    public DutyFeeTaxRegistrationTypeCodeType getDutyFeeTaxRegistrationType() {
        return dutyFeeTaxRegistrationType;
    }

    /**
     * Legt den Wert der dutyFeeTaxRegistrationType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DutyFeeTaxRegistrationTypeCodeType }
     *     
     */
    public void setDutyFeeTaxRegistrationType(DutyFeeTaxRegistrationTypeCodeType value) {
        this.dutyFeeTaxRegistrationType = value;
    }

}
