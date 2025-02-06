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

import gs1.shared.shared_common.xsd.MeasurementType;


/**
 * <p>Java-Klasse für UnitMeasurementType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="UnitMeasurementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="measurementType" type="{urn:gs1:ecom:ecom_common:xsd:3}MeasurementTypeCodeType"/&gt;
 *         &lt;element name="measurementValue" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnitMeasurementType", propOrder = {
    "measurementType",
    "measurementValue"
})
public class UnitMeasurementType {

    @XmlElement(required = true)
    protected MeasurementTypeCodeType measurementType;
    @XmlElement(required = true)
    protected MeasurementType measurementValue;

    /**
     * Ruft den Wert der measurementType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementTypeCodeType }
     *     
     */
    public MeasurementTypeCodeType getMeasurementType() {
        return measurementType;
    }

    /**
     * Legt den Wert der measurementType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementTypeCodeType }
     *     
     */
    public void setMeasurementType(MeasurementTypeCodeType value) {
        this.measurementType = value;
    }

    /**
     * Ruft den Wert der measurementValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getMeasurementValue() {
        return measurementValue;
    }

    /**
     * Legt den Wert der measurementValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setMeasurementValue(MeasurementType value) {
        this.measurementValue = value;
    }

}
