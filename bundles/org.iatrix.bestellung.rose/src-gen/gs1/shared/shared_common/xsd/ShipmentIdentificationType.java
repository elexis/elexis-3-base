//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ShipmentIdentificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ShipmentIdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gsin" type="{urn:gs1:shared:shared_common:xsd:3}GSINType"/&gt;
 *         &lt;element name="additionalShipmentIdentification" type="{urn:gs1:shared:shared_common:xsd:3}AdditionalShipmentIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentIdentificationType", propOrder = {
    "gsin",
    "additionalShipmentIdentification"
})
public class ShipmentIdentificationType {

    @XmlElement(required = true)
    protected String gsin;
    protected List<AdditionalShipmentIdentificationType> additionalShipmentIdentification;

    /**
     * Ruft den Wert der gsin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGsin() {
        return gsin;
    }

    /**
     * Legt den Wert der gsin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGsin(String value) {
        this.gsin = value;
    }

    /**
     * Gets the value of the additionalShipmentIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalShipmentIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalShipmentIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalShipmentIdentificationType }
     * 
     * 
     */
    public List<AdditionalShipmentIdentificationType> getAdditionalShipmentIdentification() {
        if (additionalShipmentIdentification == null) {
            additionalShipmentIdentification = new ArrayList<AdditionalShipmentIdentificationType>();
        }
        return this.additionalShipmentIdentification;
    }

}
