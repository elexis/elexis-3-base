//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java-Klasse für IdentifierType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="IdentifierType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;urn:gs1:shared:shared_common:xsd:3&gt;String80Type"&gt;
 *       &lt;attribute name="identificationSchemeAgencyCode"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="80"/&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="identificationSchemeAgencyCodeCodeListVersion"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="35"/&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="identificationSchemeAgencyName"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="80"/&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="identificationSchemeName"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="80"/&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IdentifierType", propOrder = {
    "value"
})
public class IdentifierType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "identificationSchemeAgencyCode")
    protected String identificationSchemeAgencyCode;
    @XmlAttribute(name = "identificationSchemeAgencyCodeCodeListVersion")
    protected String identificationSchemeAgencyCodeCodeListVersion;
    @XmlAttribute(name = "identificationSchemeAgencyName")
    protected String identificationSchemeAgencyName;
    @XmlAttribute(name = "identificationSchemeName")
    protected String identificationSchemeName;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der identificationSchemeAgencyCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificationSchemeAgencyCode() {
        return identificationSchemeAgencyCode;
    }

    /**
     * Legt den Wert der identificationSchemeAgencyCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificationSchemeAgencyCode(String value) {
        this.identificationSchemeAgencyCode = value;
    }

    /**
     * Ruft den Wert der identificationSchemeAgencyCodeCodeListVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificationSchemeAgencyCodeCodeListVersion() {
        return identificationSchemeAgencyCodeCodeListVersion;
    }

    /**
     * Legt den Wert der identificationSchemeAgencyCodeCodeListVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificationSchemeAgencyCodeCodeListVersion(String value) {
        this.identificationSchemeAgencyCodeCodeListVersion = value;
    }

    /**
     * Ruft den Wert der identificationSchemeAgencyName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificationSchemeAgencyName() {
        return identificationSchemeAgencyName;
    }

    /**
     * Legt den Wert der identificationSchemeAgencyName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificationSchemeAgencyName(String value) {
        this.identificationSchemeAgencyName = value;
    }

    /**
     * Ruft den Wert der identificationSchemeName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentificationSchemeName() {
        return identificationSchemeName;
    }

    /**
     * Legt den Wert der identificationSchemeName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentificationSchemeName(String value) {
        this.identificationSchemeName = value;
    }

}
