//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.DateTimeRangeType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für DespatchInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DespatchInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="actualShipDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="despatchDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="estimatedDeliveryDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="estimatedDeliveryDateTimeAtUltimateConsignee" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="loadingDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="pickUpDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="releaseDateTimeOfSupplier" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="estimatedDeliveryPeriod" type="{urn:gs1:shared:shared_common:xsd:3}DateTimeRangeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DespatchInformationType", propOrder = {
    "actualShipDateTime",
    "despatchDateTime",
    "estimatedDeliveryDateTime",
    "estimatedDeliveryDateTimeAtUltimateConsignee",
    "loadingDateTime",
    "pickUpDateTime",
    "releaseDateTimeOfSupplier",
    "estimatedDeliveryPeriod"
})
public class DespatchInformationType {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar actualShipDateTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar despatchDateTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar estimatedDeliveryDateTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar estimatedDeliveryDateTimeAtUltimateConsignee;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar loadingDateTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar pickUpDateTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar releaseDateTimeOfSupplier;
    protected DateTimeRangeType estimatedDeliveryPeriod;

    /**
     * Ruft den Wert der actualShipDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getActualShipDateTime() {
        return actualShipDateTime;
    }

    /**
     * Legt den Wert der actualShipDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setActualShipDateTime(XMLGregorianCalendar value) {
        this.actualShipDateTime = value;
    }

    /**
     * Ruft den Wert der despatchDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDespatchDateTime() {
        return despatchDateTime;
    }

    /**
     * Legt den Wert der despatchDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDespatchDateTime(XMLGregorianCalendar value) {
        this.despatchDateTime = value;
    }

    /**
     * Ruft den Wert der estimatedDeliveryDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEstimatedDeliveryDateTime() {
        return estimatedDeliveryDateTime;
    }

    /**
     * Legt den Wert der estimatedDeliveryDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEstimatedDeliveryDateTime(XMLGregorianCalendar value) {
        this.estimatedDeliveryDateTime = value;
    }

    /**
     * Ruft den Wert der estimatedDeliveryDateTimeAtUltimateConsignee-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEstimatedDeliveryDateTimeAtUltimateConsignee() {
        return estimatedDeliveryDateTimeAtUltimateConsignee;
    }

    /**
     * Legt den Wert der estimatedDeliveryDateTimeAtUltimateConsignee-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEstimatedDeliveryDateTimeAtUltimateConsignee(XMLGregorianCalendar value) {
        this.estimatedDeliveryDateTimeAtUltimateConsignee = value;
    }

    /**
     * Ruft den Wert der loadingDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLoadingDateTime() {
        return loadingDateTime;
    }

    /**
     * Legt den Wert der loadingDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLoadingDateTime(XMLGregorianCalendar value) {
        this.loadingDateTime = value;
    }

    /**
     * Ruft den Wert der pickUpDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPickUpDateTime() {
        return pickUpDateTime;
    }

    /**
     * Legt den Wert der pickUpDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPickUpDateTime(XMLGregorianCalendar value) {
        this.pickUpDateTime = value;
    }

    /**
     * Ruft den Wert der releaseDateTimeOfSupplier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReleaseDateTimeOfSupplier() {
        return releaseDateTimeOfSupplier;
    }

    /**
     * Legt den Wert der releaseDateTimeOfSupplier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReleaseDateTimeOfSupplier(XMLGregorianCalendar value) {
        this.releaseDateTimeOfSupplier = value;
    }

    /**
     * Ruft den Wert der estimatedDeliveryPeriod-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeRangeType }
     *     
     */
    public DateTimeRangeType getEstimatedDeliveryPeriod() {
        return estimatedDeliveryPeriod;
    }

    /**
     * Legt den Wert der estimatedDeliveryPeriod-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeRangeType }
     *     
     */
    public void setEstimatedDeliveryPeriod(DateTimeRangeType value) {
        this.estimatedDeliveryPeriod = value;
    }

}
