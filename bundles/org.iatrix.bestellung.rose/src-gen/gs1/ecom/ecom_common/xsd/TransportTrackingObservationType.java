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

import gs1.shared.shared_common.xsd.CodeType;
import gs1.shared.shared_common.xsd.MeasurementType;


/**
 * <p>Java-Klasse für TransportTrackingObservationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportTrackingObservationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transportObservationTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}ObservationTypeCodeType"/&gt;
 *         &lt;element name="transportObservationValueCode" type="{urn:gs1:shared:shared_common:xsd:3}CodeType" minOccurs="0"/&gt;
 *         &lt;element name="transportObservationValueMeasurement" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="transportObservationValueNumeric" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportTrackingObservationType", propOrder = {
    "transportObservationTypeCode",
    "transportObservationValueCode",
    "transportObservationValueMeasurement",
    "transportObservationValueNumeric"
})
public class TransportTrackingObservationType {

    @XmlElement(required = true)
    protected ObservationTypeCodeType transportObservationTypeCode;
    protected CodeType transportObservationValueCode;
    protected MeasurementType transportObservationValueMeasurement;
    protected Float transportObservationValueNumeric;

    /**
     * Ruft den Wert der transportObservationTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ObservationTypeCodeType }
     *     
     */
    public ObservationTypeCodeType getTransportObservationTypeCode() {
        return transportObservationTypeCode;
    }

    /**
     * Legt den Wert der transportObservationTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ObservationTypeCodeType }
     *     
     */
    public void setTransportObservationTypeCode(ObservationTypeCodeType value) {
        this.transportObservationTypeCode = value;
    }

    /**
     * Ruft den Wert der transportObservationValueCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getTransportObservationValueCode() {
        return transportObservationValueCode;
    }

    /**
     * Legt den Wert der transportObservationValueCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setTransportObservationValueCode(CodeType value) {
        this.transportObservationValueCode = value;
    }

    /**
     * Ruft den Wert der transportObservationValueMeasurement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTransportObservationValueMeasurement() {
        return transportObservationValueMeasurement;
    }

    /**
     * Legt den Wert der transportObservationValueMeasurement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTransportObservationValueMeasurement(MeasurementType value) {
        this.transportObservationValueMeasurement = value;
    }

    /**
     * Ruft den Wert der transportObservationValueNumeric-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getTransportObservationValueNumeric() {
        return transportObservationValueNumeric;
    }

    /**
     * Legt den Wert der transportObservationValueNumeric-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setTransportObservationValueNumeric(Float value) {
        this.transportObservationValueNumeric = value;
    }

}
