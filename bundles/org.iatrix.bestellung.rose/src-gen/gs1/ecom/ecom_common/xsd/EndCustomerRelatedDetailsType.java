//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für EndCustomerRelatedDetailsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EndCustomerRelatedDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="deliveryMethod" type="{urn:gs1:ecom:ecom_common:xsd:3}DeliveryMethodCodeType" minOccurs="0"/&gt;
 *         &lt;element name="ultimateCustomer" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="avpList" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_AttributeValuePairListType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EndCustomerRelatedDetailsType", propOrder = {
    "deliveryMethod",
    "ultimateCustomer",
    "avpList"
})
public class EndCustomerRelatedDetailsType {

    protected DeliveryMethodCodeType deliveryMethod;
    protected TransactionalPartyType ultimateCustomer;
    protected List<EcomAttributeValuePairListType> avpList;

    /**
     * Ruft den Wert der deliveryMethod-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryMethodCodeType }
     *     
     */
    public DeliveryMethodCodeType getDeliveryMethod() {
        return deliveryMethod;
    }

    /**
     * Legt den Wert der deliveryMethod-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryMethodCodeType }
     *     
     */
    public void setDeliveryMethod(DeliveryMethodCodeType value) {
        this.deliveryMethod = value;
    }

    /**
     * Ruft den Wert der ultimateCustomer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getUltimateCustomer() {
        return ultimateCustomer;
    }

    /**
     * Legt den Wert der ultimateCustomer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setUltimateCustomer(TransactionalPartyType value) {
        this.ultimateCustomer = value;
    }

    /**
     * Gets the value of the avpList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the avpList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvpList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomAttributeValuePairListType }
     * 
     * 
     */
    public List<EcomAttributeValuePairListType> getAvpList() {
        if (avpList == null) {
            avpList = new ArrayList<EcomAttributeValuePairListType>();
        }
        return this.avpList;
    }

}
