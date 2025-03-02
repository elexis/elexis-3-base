//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ServiceRelationIdentificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ServiceRelationIdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gsrn" type="{urn:gs1:shared:shared_common:xsd:3}GSRNType"/&gt;
 *         &lt;element name="additionalServiceRelationIdentification" type="{urn:gs1:shared:shared_common:xsd:3}AdditionalServiceRelationIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServiceRelationIdentificationType", propOrder = {
    "gsrn",
    "additionalServiceRelationIdentification"
})
public class ServiceRelationIdentificationType {

    @XmlElement(required = true)
    protected String gsrn;
    protected List<AdditionalServiceRelationIdentificationType> additionalServiceRelationIdentification;

    /**
     * Ruft den Wert der gsrn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGsrn() {
        return gsrn;
    }

    /**
     * Legt den Wert der gsrn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGsrn(String value) {
        this.gsrn = value;
    }

    /**
     * Gets the value of the additionalServiceRelationIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalServiceRelationIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalServiceRelationIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalServiceRelationIdentificationType }
     * 
     * 
     */
    public List<AdditionalServiceRelationIdentificationType> getAdditionalServiceRelationIdentification() {
        if (additionalServiceRelationIdentification == null) {
            additionalServiceRelationIdentification = new ArrayList<AdditionalServiceRelationIdentificationType>();
        }
        return this.additionalServiceRelationIdentification;
    }

}
