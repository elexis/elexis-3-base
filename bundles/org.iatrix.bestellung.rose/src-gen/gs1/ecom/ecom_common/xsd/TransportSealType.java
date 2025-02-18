//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.IdentifierType;


/**
 * <p>Java-Klasse für TransportSealType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportSealType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sealIdentification" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType"/&gt;
 *         &lt;element name="sealTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}SealTypeCodeType"/&gt;
 *         &lt;element name="sealAffixingPartyRole" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportPartyRoleCodeType" minOccurs="0"/&gt;
 *         &lt;element name="sealConditionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}SealConditionCodeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportSealType", propOrder = {
    "sealIdentification",
    "sealTypeCode",
    "sealAffixingPartyRole",
    "sealConditionCode"
})
public class TransportSealType {

    @XmlElement(required = true)
    protected IdentifierType sealIdentification;
    @XmlElement(required = true)
    protected SealTypeCodeType sealTypeCode;
    protected TransportPartyRoleCodeType sealAffixingPartyRole;
    protected SealConditionCodeType sealConditionCode;

    /**
     * Ruft den Wert der sealIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getSealIdentification() {
        return sealIdentification;
    }

    /**
     * Legt den Wert der sealIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setSealIdentification(IdentifierType value) {
        this.sealIdentification = value;
    }

    /**
     * Ruft den Wert der sealTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SealTypeCodeType }
     *     
     */
    public SealTypeCodeType getSealTypeCode() {
        return sealTypeCode;
    }

    /**
     * Legt den Wert der sealTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SealTypeCodeType }
     *     
     */
    public void setSealTypeCode(SealTypeCodeType value) {
        this.sealTypeCode = value;
    }

    /**
     * Ruft den Wert der sealAffixingPartyRole-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportPartyRoleCodeType }
     *     
     */
    public TransportPartyRoleCodeType getSealAffixingPartyRole() {
        return sealAffixingPartyRole;
    }

    /**
     * Legt den Wert der sealAffixingPartyRole-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportPartyRoleCodeType }
     *     
     */
    public void setSealAffixingPartyRole(TransportPartyRoleCodeType value) {
        this.sealAffixingPartyRole = value;
    }

    /**
     * Ruft den Wert der sealConditionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SealConditionCodeType }
     *     
     */
    public SealConditionCodeType getSealConditionCode() {
        return sealConditionCode;
    }

    /**
     * Legt den Wert der sealConditionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SealConditionCodeType }
     *     
     */
    public void setSealConditionCode(SealConditionCodeType value) {
        this.sealConditionCode = value;
    }

}
