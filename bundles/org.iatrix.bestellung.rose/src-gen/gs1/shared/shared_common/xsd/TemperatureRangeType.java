//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TemperatureRangeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TemperatureRangeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="maximumTemperature" type="{urn:gs1:shared:shared_common:xsd:3}TemperatureMeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="minimumTemperature" type="{urn:gs1:shared:shared_common:xsd:3}TemperatureMeasurementType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TemperatureRangeType", propOrder = {
    "maximumTemperature",
    "minimumTemperature"
})
public class TemperatureRangeType {

    protected TemperatureMeasurementType maximumTemperature;
    protected TemperatureMeasurementType minimumTemperature;

    /**
     * Ruft den Wert der maximumTemperature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TemperatureMeasurementType }
     *     
     */
    public TemperatureMeasurementType getMaximumTemperature() {
        return maximumTemperature;
    }

    /**
     * Legt den Wert der maximumTemperature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TemperatureMeasurementType }
     *     
     */
    public void setMaximumTemperature(TemperatureMeasurementType value) {
        this.maximumTemperature = value;
    }

    /**
     * Ruft den Wert der minimumTemperature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TemperatureMeasurementType }
     *     
     */
    public TemperatureMeasurementType getMinimumTemperature() {
        return minimumTemperature;
    }

    /**
     * Legt den Wert der minimumTemperature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TemperatureMeasurementType }
     *     
     */
    public void setMinimumTemperature(TemperatureMeasurementType value) {
        this.minimumTemperature = value;
    }

}
