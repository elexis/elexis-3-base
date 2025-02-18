//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.Description500Type;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransportStatusType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportStatusType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transportStatusConditionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportStatusConditionCodeType" maxOccurs="unbounded"/&gt;
 *         &lt;element name="transportStatusDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="transportStatusDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type" minOccurs="0"/&gt;
 *         &lt;element name="transportStatusReasonCode" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportStatusReasonCodeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transportStatusReasonDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type" minOccurs="0"/&gt;
 *         &lt;element name="logisticLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticLocationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportStatusType", propOrder = {
    "transportStatusConditionCode",
    "transportStatusDateTime",
    "transportStatusDescription",
    "transportStatusReasonCode",
    "transportStatusReasonDescription",
    "logisticLocation"
})
public class TransportStatusType {

    @XmlElement(required = true)
    protected List<TransportStatusConditionCodeType> transportStatusConditionCode;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar transportStatusDateTime;
    protected Description500Type transportStatusDescription;
    protected List<TransportStatusReasonCodeType> transportStatusReasonCode;
    protected Description500Type transportStatusReasonDescription;
    protected LogisticLocationType logisticLocation;

    /**
     * Gets the value of the transportStatusConditionCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transportStatusConditionCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportStatusConditionCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportStatusConditionCodeType }
     * 
     * 
     */
    public List<TransportStatusConditionCodeType> getTransportStatusConditionCode() {
        if (transportStatusConditionCode == null) {
            transportStatusConditionCode = new ArrayList<TransportStatusConditionCodeType>();
        }
        return this.transportStatusConditionCode;
    }

    /**
     * Ruft den Wert der transportStatusDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransportStatusDateTime() {
        return transportStatusDateTime;
    }

    /**
     * Legt den Wert der transportStatusDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransportStatusDateTime(XMLGregorianCalendar value) {
        this.transportStatusDateTime = value;
    }

    /**
     * Ruft den Wert der transportStatusDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description500Type }
     *     
     */
    public Description500Type getTransportStatusDescription() {
        return transportStatusDescription;
    }

    /**
     * Legt den Wert der transportStatusDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description500Type }
     *     
     */
    public void setTransportStatusDescription(Description500Type value) {
        this.transportStatusDescription = value;
    }

    /**
     * Gets the value of the transportStatusReasonCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transportStatusReasonCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportStatusReasonCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportStatusReasonCodeType }
     * 
     * 
     */
    public List<TransportStatusReasonCodeType> getTransportStatusReasonCode() {
        if (transportStatusReasonCode == null) {
            transportStatusReasonCode = new ArrayList<TransportStatusReasonCodeType>();
        }
        return this.transportStatusReasonCode;
    }

    /**
     * Ruft den Wert der transportStatusReasonDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description500Type }
     *     
     */
    public Description500Type getTransportStatusReasonDescription() {
        return transportStatusReasonDescription;
    }

    /**
     * Legt den Wert der transportStatusReasonDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description500Type }
     *     
     */
    public void setTransportStatusReasonDescription(Description500Type value) {
        this.transportStatusReasonDescription = value;
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

}
