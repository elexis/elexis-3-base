//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.DateOptionalTimeType;
import gs1.shared.shared_common.xsd.DateTimeRangeType;
import gs1.shared.shared_common.xsd.TimeMeasurementType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für LogisticEventType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LogisticEventType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="logisticEventTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticEventTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="logisticEventDuration" type="{urn:gs1:shared:shared_common:xsd:3}TimeMeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="logisticLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticLocationType" minOccurs="0"/&gt;
 *         &lt;element name="logisticEventPeriod" type="{urn:gs1:shared:shared_common:xsd:3}DateTimeRangeType" minOccurs="0"/&gt;
 *         &lt;element name="logisticEventDateTime" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogisticEventType", propOrder = {
    "logisticEventTypeCode",
    "logisticEventDuration",
    "logisticLocation",
    "logisticEventPeriod",
    "logisticEventDateTime"
})
public class LogisticEventType {

    protected LogisticEventTypeCodeType logisticEventTypeCode;
    protected TimeMeasurementType logisticEventDuration;
    protected LogisticLocationType logisticLocation;
    protected DateTimeRangeType logisticEventPeriod;
    protected DateOptionalTimeType logisticEventDateTime;

    /**
     * Ruft den Wert der logisticEventTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LogisticEventTypeCodeType }
     *     
     */
    public LogisticEventTypeCodeType getLogisticEventTypeCode() {
        return logisticEventTypeCode;
    }

    /**
     * Legt den Wert der logisticEventTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LogisticEventTypeCodeType }
     *     
     */
    public void setLogisticEventTypeCode(LogisticEventTypeCodeType value) {
        this.logisticEventTypeCode = value;
    }

    /**
     * Ruft den Wert der logisticEventDuration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TimeMeasurementType }
     *     
     */
    public TimeMeasurementType getLogisticEventDuration() {
        return logisticEventDuration;
    }

    /**
     * Legt den Wert der logisticEventDuration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeMeasurementType }
     *     
     */
    public void setLogisticEventDuration(TimeMeasurementType value) {
        this.logisticEventDuration = value;
    }

    /**
     * Ruft den Wert der logisticLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LogisticLocationType }
     *     
     */
    public LogisticLocationType getLogisticLocation() {
        return logisticLocation;
    }

    /**
     * Legt den Wert der logisticLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LogisticLocationType }
     *     
     */
    public void setLogisticLocation(LogisticLocationType value) {
        this.logisticLocation = value;
    }

    /**
     * Ruft den Wert der logisticEventPeriod-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeRangeType }
     *     
     */
    public DateTimeRangeType getLogisticEventPeriod() {
        return logisticEventPeriod;
    }

    /**
     * Legt den Wert der logisticEventPeriod-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeRangeType }
     *     
     */
    public void setLogisticEventPeriod(DateTimeRangeType value) {
        this.logisticEventPeriod = value;
    }

    /**
     * Ruft den Wert der logisticEventDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getLogisticEventDateTime() {
        return logisticEventDateTime;
    }

    /**
     * Legt den Wert der logisticEventDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setLogisticEventDateTime(DateOptionalTimeType value) {
        this.logisticEventDateTime = value;
    }

}
