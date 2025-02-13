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


/**
 * <p>Java-Klasse für LegalRegistrationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LegalRegistrationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="legalRegistrationNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="legalRegistrationType" type="{urn:gs1:ecom:ecom_common:xsd:3}LegalRegistrationCodeType"/&gt;
 *         &lt;element name="legalRegistrationAdditionalInformation" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LegalRegistrationType", propOrder = {
    "legalRegistrationNumber",
    "legalRegistrationType",
    "legalRegistrationAdditionalInformation"
})
public class LegalRegistrationType {

    @XmlElement(required = true)
    protected String legalRegistrationNumber;
    @XmlElement(required = true)
    protected LegalRegistrationCodeType legalRegistrationType;
    protected String legalRegistrationAdditionalInformation;

    /**
     * Ruft den Wert der legalRegistrationNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegalRegistrationNumber() {
        return legalRegistrationNumber;
    }

    /**
     * Legt den Wert der legalRegistrationNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegalRegistrationNumber(String value) {
        this.legalRegistrationNumber = value;
    }

    /**
     * Ruft den Wert der legalRegistrationType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LegalRegistrationCodeType }
     *     
     */
    public LegalRegistrationCodeType getLegalRegistrationType() {
        return legalRegistrationType;
    }

    /**
     * Legt den Wert der legalRegistrationType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LegalRegistrationCodeType }
     *     
     */
    public void setLegalRegistrationType(LegalRegistrationCodeType value) {
        this.legalRegistrationType = value;
    }

    /**
     * Ruft den Wert der legalRegistrationAdditionalInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegalRegistrationAdditionalInformation() {
        return legalRegistrationAdditionalInformation;
    }

    /**
     * Legt den Wert der legalRegistrationAdditionalInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegalRegistrationAdditionalInformation(String value) {
        this.legalRegistrationAdditionalInformation = value;
    }

}
