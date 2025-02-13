//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.QuantityType;


/**
 * <p>Java-Klasse für AcceptableOverAllocationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AcceptableOverAllocationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="overAllocationQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="overAllocationPercentage" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcceptableOverAllocationType", propOrder = {
    "overAllocationQuantity",
    "overAllocationPercentage"
})
public class AcceptableOverAllocationType {

    protected QuantityType overAllocationQuantity;
    protected BigDecimal overAllocationPercentage;

    /**
     * Ruft den Wert der overAllocationQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getOverAllocationQuantity() {
        return overAllocationQuantity;
    }

    /**
     * Legt den Wert der overAllocationQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setOverAllocationQuantity(QuantityType value) {
        this.overAllocationQuantity = value;
    }

    /**
     * Ruft den Wert der overAllocationPercentage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getOverAllocationPercentage() {
        return overAllocationPercentage;
    }

    /**
     * Legt den Wert der overAllocationPercentage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setOverAllocationPercentage(BigDecimal value) {
        this.overAllocationPercentage = value;
    }

}
