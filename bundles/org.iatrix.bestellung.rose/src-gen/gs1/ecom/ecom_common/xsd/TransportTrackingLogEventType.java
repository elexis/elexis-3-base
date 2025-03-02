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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransportTrackingLogEventType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportTrackingLogEventType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="logEventDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="transportTrackingObservation" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportTrackingObservationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transportTrackingSensorObservation" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportTrackingSensorObservationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportTrackingLogEventType", propOrder = {
    "logEventDateTime",
    "transportTrackingObservation",
    "transportTrackingSensorObservation"
})
public class TransportTrackingLogEventType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar logEventDateTime;
    protected List<TransportTrackingObservationType> transportTrackingObservation;
    protected List<TransportTrackingSensorObservationType> transportTrackingSensorObservation;

    /**
     * Ruft den Wert der logEventDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLogEventDateTime() {
        return logEventDateTime;
    }

    /**
     * Legt den Wert der logEventDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLogEventDateTime(XMLGregorianCalendar value) {
        this.logEventDateTime = value;
    }

    /**
     * Gets the value of the transportTrackingObservation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transportTrackingObservation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportTrackingObservation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportTrackingObservationType }
     * 
     * 
     */
    public List<TransportTrackingObservationType> getTransportTrackingObservation() {
        if (transportTrackingObservation == null) {
            transportTrackingObservation = new ArrayList<TransportTrackingObservationType>();
        }
        return this.transportTrackingObservation;
    }

    /**
     * Gets the value of the transportTrackingSensorObservation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transportTrackingSensorObservation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportTrackingSensorObservation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportTrackingSensorObservationType }
     * 
     * 
     */
    public List<TransportTrackingSensorObservationType> getTransportTrackingSensorObservation() {
        if (transportTrackingSensorObservation == null) {
            transportTrackingSensorObservation = new ArrayList<TransportTrackingSensorObservationType>();
        }
        return this.transportTrackingSensorObservation;
    }

}
