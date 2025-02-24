//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PackagingMarkingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PackagingMarkingType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="markingTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackagingMarkingTypeCodeType"/&gt;
 *         &lt;element name="markingContentDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="markingContentText" minOccurs="0"&gt;
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
@XmlType(name = "PackagingMarkingType", propOrder = {
    "markingTypeCode",
    "markingContentDateTime",
    "markingContentText"
})
public class PackagingMarkingType {

    @XmlElement(required = true)
    protected PackagingMarkingTypeCodeType markingTypeCode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar markingContentDateTime;
    protected String markingContentText;

    /**
     * Ruft den Wert der markingTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackagingMarkingTypeCodeType }
     *     
     */
    public PackagingMarkingTypeCodeType getMarkingTypeCode() {
        return markingTypeCode;
    }

    /**
     * Legt den Wert der markingTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackagingMarkingTypeCodeType }
     *     
     */
    public void setMarkingTypeCode(PackagingMarkingTypeCodeType value) {
        this.markingTypeCode = value;
    }

    /**
     * Ruft den Wert der markingContentDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getMarkingContentDateTime() {
        return markingContentDateTime;
    }

    /**
     * Legt den Wert der markingContentDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setMarkingContentDateTime(XMLGregorianCalendar value) {
        this.markingContentDateTime = value;
    }

    /**
     * Ruft den Wert der markingContentText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMarkingContentText() {
        return markingContentText;
    }

    /**
     * Legt den Wert der markingContentText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMarkingContentText(String value) {
        this.markingContentText = value;
    }

}
