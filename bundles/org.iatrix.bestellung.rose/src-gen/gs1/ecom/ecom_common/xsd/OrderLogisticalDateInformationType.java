//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.DateOptionalTimeType;
import gs1.shared.shared_common.xsd.DateTimeRangeType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für OrderLogisticalDateInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrderLogisticalDateInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="requestedDeliveryDateRange" type="{urn:gs1:shared:shared_common:xsd:3}DateTimeRangeType" minOccurs="0"/&gt;
 *         &lt;element name="requestedShipDateRange" type="{urn:gs1:shared:shared_common:xsd:3}DateTimeRangeType" minOccurs="0"/&gt;
 *         &lt;element name="requestedDeliveryDateRangeAtUltimateConsignee" type="{urn:gs1:shared:shared_common:xsd:3}DateTimeRangeType" minOccurs="0"/&gt;
 *         &lt;element name="requestedDeliveryDateTime" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="requestedShipDateTime" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="requestedPickUpDateTime" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="requestedDeliveryDateTimeAtUltimateConsignee" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderLogisticalDateInformationType", propOrder = {
    "requestedDeliveryDateRange",
    "requestedShipDateRange",
    "requestedDeliveryDateRangeAtUltimateConsignee",
    "requestedDeliveryDateTime",
    "requestedShipDateTime",
    "requestedPickUpDateTime",
    "requestedDeliveryDateTimeAtUltimateConsignee"
})
public class OrderLogisticalDateInformationType {

    protected DateTimeRangeType requestedDeliveryDateRange;
    protected DateTimeRangeType requestedShipDateRange;
    protected DateTimeRangeType requestedDeliveryDateRangeAtUltimateConsignee;
    protected DateOptionalTimeType requestedDeliveryDateTime;
    protected DateOptionalTimeType requestedShipDateTime;
    protected DateOptionalTimeType requestedPickUpDateTime;
    protected DateOptionalTimeType requestedDeliveryDateTimeAtUltimateConsignee;

    /**
     * Ruft den Wert der requestedDeliveryDateRange-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeRangeType }
     *     
     */
    public DateTimeRangeType getRequestedDeliveryDateRange() {
        return requestedDeliveryDateRange;
    }

    /**
     * Legt den Wert der requestedDeliveryDateRange-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeRangeType }
     *     
     */
    public void setRequestedDeliveryDateRange(DateTimeRangeType value) {
        this.requestedDeliveryDateRange = value;
    }

    /**
     * Ruft den Wert der requestedShipDateRange-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeRangeType }
     *     
     */
    public DateTimeRangeType getRequestedShipDateRange() {
        return requestedShipDateRange;
    }

    /**
     * Legt den Wert der requestedShipDateRange-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeRangeType }
     *     
     */
    public void setRequestedShipDateRange(DateTimeRangeType value) {
        this.requestedShipDateRange = value;
    }

    /**
     * Ruft den Wert der requestedDeliveryDateRangeAtUltimateConsignee-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeRangeType }
     *     
     */
    public DateTimeRangeType getRequestedDeliveryDateRangeAtUltimateConsignee() {
        return requestedDeliveryDateRangeAtUltimateConsignee;
    }

    /**
     * Legt den Wert der requestedDeliveryDateRangeAtUltimateConsignee-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeRangeType }
     *     
     */
    public void setRequestedDeliveryDateRangeAtUltimateConsignee(DateTimeRangeType value) {
        this.requestedDeliveryDateRangeAtUltimateConsignee = value;
    }

    /**
     * Ruft den Wert der requestedDeliveryDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getRequestedDeliveryDateTime() {
        return requestedDeliveryDateTime;
    }

    /**
     * Legt den Wert der requestedDeliveryDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setRequestedDeliveryDateTime(DateOptionalTimeType value) {
        this.requestedDeliveryDateTime = value;
    }

    /**
     * Ruft den Wert der requestedShipDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getRequestedShipDateTime() {
        return requestedShipDateTime;
    }

    /**
     * Legt den Wert der requestedShipDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setRequestedShipDateTime(DateOptionalTimeType value) {
        this.requestedShipDateTime = value;
    }

    /**
     * Ruft den Wert der requestedPickUpDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getRequestedPickUpDateTime() {
        return requestedPickUpDateTime;
    }

    /**
     * Legt den Wert der requestedPickUpDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setRequestedPickUpDateTime(DateOptionalTimeType value) {
        this.requestedPickUpDateTime = value;
    }

    /**
     * Ruft den Wert der requestedDeliveryDateTimeAtUltimateConsignee-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getRequestedDeliveryDateTimeAtUltimateConsignee() {
        return requestedDeliveryDateTimeAtUltimateConsignee;
    }

    /**
     * Legt den Wert der requestedDeliveryDateTimeAtUltimateConsignee-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setRequestedDeliveryDateTimeAtUltimateConsignee(DateOptionalTimeType value) {
        this.requestedDeliveryDateTimeAtUltimateConsignee = value;
    }

}
