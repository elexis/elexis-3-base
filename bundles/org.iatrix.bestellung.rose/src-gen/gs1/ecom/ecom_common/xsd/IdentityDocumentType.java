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
 * <p>Java-Klasse für IdentityDocumentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="IdentityDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="identityDocumentNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="identityDocumentType" type="{urn:gs1:ecom:ecom_common:xsd:3}IdentityDocumentTypeCodeType"/&gt;
 *         &lt;element name="identityDocumentIssuer" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
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
@XmlType(name = "IdentityDocumentType", propOrder = {
    "identityDocumentNumber",
    "identityDocumentType",
    "identityDocumentIssuer"
})
public class IdentityDocumentType {

    @XmlElement(required = true)
    protected String identityDocumentNumber;
    @XmlElement(required = true)
    protected IdentityDocumentTypeCodeType identityDocumentType;
    protected String identityDocumentIssuer;

    /**
     * Ruft den Wert der identityDocumentNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityDocumentNumber() {
        return identityDocumentNumber;
    }

    /**
     * Legt den Wert der identityDocumentNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityDocumentNumber(String value) {
        this.identityDocumentNumber = value;
    }

    /**
     * Ruft den Wert der identityDocumentType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentityDocumentTypeCodeType }
     *     
     */
    public IdentityDocumentTypeCodeType getIdentityDocumentType() {
        return identityDocumentType;
    }

    /**
     * Legt den Wert der identityDocumentType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentityDocumentTypeCodeType }
     *     
     */
    public void setIdentityDocumentType(IdentityDocumentTypeCodeType value) {
        this.identityDocumentType = value;
    }

    /**
     * Ruft den Wert der identityDocumentIssuer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentityDocumentIssuer() {
        return identityDocumentIssuer;
    }

    /**
     * Legt den Wert der identityDocumentIssuer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentityDocumentIssuer(String value) {
        this.identityDocumentIssuer = value;
    }

}
