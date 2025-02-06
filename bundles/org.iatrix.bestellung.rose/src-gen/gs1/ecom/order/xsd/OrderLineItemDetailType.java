//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.order.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.ecom.ecom_common.xsd.EcomAttributeValuePairListType;
import gs1.ecom.ecom_common.xsd.EcomDocumentReferenceType;
import gs1.ecom.ecom_common.xsd.OrderLogisticalInformationType;
import gs1.shared.shared_common.xsd.QuantityType;


/**
 * <p>Java-Klasse für OrderLineItemDetailType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrderLineItemDetailType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="requestedQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType"/&gt;
 *         &lt;element name="orderLogisticalInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderLogisticalInformationType"/&gt;
 *         &lt;element name="purchaseConditions" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="avpList" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_AttributeValuePairListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderLineItemDetailType", propOrder = {
    "requestedQuantity",
    "orderLogisticalInformation",
    "purchaseConditions",
    "avpList"
})
public class OrderLineItemDetailType {

    @XmlElement(required = true)
    protected QuantityType requestedQuantity;
    @XmlElement(required = true)
    protected OrderLogisticalInformationType orderLogisticalInformation;
    protected EcomDocumentReferenceType purchaseConditions;
    protected EcomAttributeValuePairListType avpList;

    /**
     * Ruft den Wert der requestedQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getRequestedQuantity() {
        return requestedQuantity;
    }

    /**
     * Legt den Wert der requestedQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setRequestedQuantity(QuantityType value) {
        this.requestedQuantity = value;
    }

    /**
     * Ruft den Wert der orderLogisticalInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderLogisticalInformationType }
     *     
     */
    public OrderLogisticalInformationType getOrderLogisticalInformation() {
        return orderLogisticalInformation;
    }

    /**
     * Legt den Wert der orderLogisticalInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderLogisticalInformationType }
     *     
     */
    public void setOrderLogisticalInformation(OrderLogisticalInformationType value) {
        this.orderLogisticalInformation = value;
    }

    /**
     * Ruft den Wert der purchaseConditions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getPurchaseConditions() {
        return purchaseConditions;
    }

    /**
     * Legt den Wert der purchaseConditions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setPurchaseConditions(EcomDocumentReferenceType value) {
        this.purchaseConditions = value;
    }

    /**
     * Ruft den Wert der avpList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomAttributeValuePairListType }
     *     
     */
    public EcomAttributeValuePairListType getAvpList() {
        return avpList;
    }

    /**
     * Legt den Wert der avpList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomAttributeValuePairListType }
     *     
     */
    public void setAvpList(EcomAttributeValuePairListType value) {
        this.avpList = value;
    }

}
