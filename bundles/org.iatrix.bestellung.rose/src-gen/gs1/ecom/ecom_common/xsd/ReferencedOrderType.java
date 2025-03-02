//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ReferencedOrderType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReferencedOrderType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="orderRelationship" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderRelationshipTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferencedOrderType", propOrder = {
    "orderRelationship"
})
public class ReferencedOrderType
    extends EcomDocumentReferenceType
{

    @XmlElement(required = true)
    protected OrderRelationshipTypeCodeType orderRelationship;

    /**
     * Ruft den Wert der orderRelationship-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderRelationshipTypeCodeType }
     *     
     */
    public OrderRelationshipTypeCodeType getOrderRelationship() {
        return orderRelationship;
    }

    /**
     * Legt den Wert der orderRelationship-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderRelationshipTypeCodeType }
     *     
     */
    public void setOrderRelationship(OrderRelationshipTypeCodeType value) {
        this.orderRelationship = value;
    }

}
