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
 * <p>Java-Klasse für QuantityRangeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="QuantityRangeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="maximumQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="minimumQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantityRangeType", propOrder = {
    "maximumQuantity",
    "minimumQuantity"
})
public class QuantityRangeType {

    protected QuantityType maximumQuantity;
    protected QuantityType minimumQuantity;

    /**
     * Ruft den Wert der maximumQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getMaximumQuantity() {
        return maximumQuantity;
    }

    /**
     * Legt den Wert der maximumQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setMaximumQuantity(QuantityType value) {
        this.maximumQuantity = value;
    }

    /**
     * Ruft den Wert der minimumQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getMinimumQuantity() {
        return minimumQuantity;
    }

    /**
     * Legt den Wert der minimumQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setMinimumQuantity(QuantityType value) {
        this.minimumQuantity = value;
    }

}
