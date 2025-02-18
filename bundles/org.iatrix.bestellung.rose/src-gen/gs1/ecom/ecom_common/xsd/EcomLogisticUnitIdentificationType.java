//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.AdditionalLogisticUnitIdentificationType;

/**
 * <p>Java-Klasse für Ecom_LogisticUnitIdentificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Ecom_LogisticUnitIdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sscc" type="{urn:gs1:shared:shared_common:xsd:3}SSCCType" minOccurs="0"/&gt;
 *         &lt;element name="additionalLogisticUnitIdentification" type="{urn:gs1:shared:shared_common:xsd:3}AdditionalLogisticUnitIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ecom_LogisticUnitIdentificationType", propOrder = {
    "sscc",
    "additionalLogisticUnitIdentification"
})
public class EcomLogisticUnitIdentificationType {

    protected String sscc;
    protected List<AdditionalLogisticUnitIdentificationType> additionalLogisticUnitIdentification;

    /**
     * Ruft den Wert der sscc-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSscc() {
        return sscc;
    }

    /**
     * Legt den Wert der sscc-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSscc(String value) {
        this.sscc = value;
    }

    /**
     * Gets the value of the additionalLogisticUnitIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalLogisticUnitIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalLogisticUnitIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalLogisticUnitIdentificationType }
     * 
     * 
     */
    public List<AdditionalLogisticUnitIdentificationType> getAdditionalLogisticUnitIdentification() {
        if (additionalLogisticUnitIdentification == null) {
            additionalLogisticUnitIdentification = new ArrayList<AdditionalLogisticUnitIdentificationType>();
        }
        return this.additionalLogisticUnitIdentification;
    }

}
