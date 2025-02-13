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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.MeasurementType;

/**
 * <p>Java-Klasse für DangerousGoodsAttributeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DangerousGoodsAttributeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dangerousGoodsAttributeTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}DangerousGoodsAttributeTypeCodeType"/&gt;
 *         &lt;element name="dangerousGoodsAttributeText" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dangerousGoodsAttributeMeasurement" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="dangerousGoodsAttributeIndicator" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="dangerousGoodsAttributeDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DangerousGoodsAttributeType", propOrder = {
    "dangerousGoodsAttributeTypeCode",
    "dangerousGoodsAttributeText",
    "dangerousGoodsAttributeMeasurement",
    "dangerousGoodsAttributeIndicator",
    "dangerousGoodsAttributeDateTime"
})
public class DangerousGoodsAttributeType {

    @XmlElement(required = true)
    protected DangerousGoodsAttributeTypeCodeType dangerousGoodsAttributeTypeCode;
    protected String dangerousGoodsAttributeText;
    protected MeasurementType dangerousGoodsAttributeMeasurement;
    protected Boolean dangerousGoodsAttributeIndicator;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dangerousGoodsAttributeDateTime;

    /**
     * Ruft den Wert der dangerousGoodsAttributeTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsAttributeTypeCodeType }
     *     
     */
    public DangerousGoodsAttributeTypeCodeType getDangerousGoodsAttributeTypeCode() {
        return dangerousGoodsAttributeTypeCode;
    }

    /**
     * Legt den Wert der dangerousGoodsAttributeTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsAttributeTypeCodeType }
     *     
     */
    public void setDangerousGoodsAttributeTypeCode(DangerousGoodsAttributeTypeCodeType value) {
        this.dangerousGoodsAttributeTypeCode = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsAttributeText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDangerousGoodsAttributeText() {
        return dangerousGoodsAttributeText;
    }

    /**
     * Legt den Wert der dangerousGoodsAttributeText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDangerousGoodsAttributeText(String value) {
        this.dangerousGoodsAttributeText = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsAttributeMeasurement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getDangerousGoodsAttributeMeasurement() {
        return dangerousGoodsAttributeMeasurement;
    }

    /**
     * Legt den Wert der dangerousGoodsAttributeMeasurement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setDangerousGoodsAttributeMeasurement(MeasurementType value) {
        this.dangerousGoodsAttributeMeasurement = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsAttributeIndicator-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDangerousGoodsAttributeIndicator() {
        return dangerousGoodsAttributeIndicator;
    }

    /**
     * Legt den Wert der dangerousGoodsAttributeIndicator-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDangerousGoodsAttributeIndicator(Boolean value) {
        this.dangerousGoodsAttributeIndicator = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsAttributeDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDangerousGoodsAttributeDateTime() {
        return dangerousGoodsAttributeDateTime;
    }

    /**
     * Legt den Wert der dangerousGoodsAttributeDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDangerousGoodsAttributeDateTime(XMLGregorianCalendar value) {
        this.dangerousGoodsAttributeDateTime = value;
    }

}
