//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <p>Java-Klasse für EcomStringAttributeValuePairListType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EcomStringAttributeValuePairListType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema&gt;string"&gt;
 *       &lt;attribute name="attributeName" use="required" type="{urn:gs1:shared:shared_common:xsd:3}String70Type" /&gt;
 *       &lt;attribute name="qualifierCodeName" type="{urn:gs1:shared:shared_common:xsd:3}String70Type" /&gt;
 *       &lt;attribute name="qualifierCodeList" type="{urn:gs1:shared:shared_common:xsd:3}String70Type" /&gt;
 *       &lt;attribute name="qualifierCodeListVersion" type="{urn:gs1:shared:shared_common:xsd:3}String70Type" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EcomStringAttributeValuePairListType", propOrder = {
    "value"
})
public class EcomStringAttributeValuePairListType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "attributeName", required = true)
    protected String attributeName;
    @XmlAttribute(name = "qualifierCodeName")
    protected String qualifierCodeName;
    @XmlAttribute(name = "qualifierCodeList")
    protected String qualifierCodeList;
    @XmlAttribute(name = "qualifierCodeListVersion")
    protected String qualifierCodeListVersion;

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
     * Ruft den Wert der attributeName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * Legt den Wert der attributeName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttributeName(String value) {
        this.attributeName = value;
    }

    /**
     * Ruft den Wert der qualifierCodeName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualifierCodeName() {
        return qualifierCodeName;
    }

    /**
     * Legt den Wert der qualifierCodeName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualifierCodeName(String value) {
        this.qualifierCodeName = value;
    }

    /**
     * Ruft den Wert der qualifierCodeList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualifierCodeList() {
        return qualifierCodeList;
    }

    /**
     * Legt den Wert der qualifierCodeList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualifierCodeList(String value) {
        this.qualifierCodeList = value;
    }

    /**
     * Ruft den Wert der qualifierCodeListVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQualifierCodeListVersion() {
        return qualifierCodeListVersion;
    }

    /**
     * Legt den Wert der qualifierCodeListVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQualifierCodeListVersion(String value) {
        this.qualifierCodeListVersion = value;
    }

}
