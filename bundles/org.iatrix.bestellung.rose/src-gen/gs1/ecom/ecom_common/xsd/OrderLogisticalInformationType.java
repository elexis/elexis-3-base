//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.CodeType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für OrderLogisticalInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrderLogisticalInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="commodityTypeCode" type="{urn:gs1:shared:shared_common:xsd:3}CodeType" minOccurs="0"/&gt;
 *         &lt;element name="shipmentSplitMethodCode" type="{urn:gs1:ecom:ecom_common:xsd:3}ShipmentSplitMethodCodeType" minOccurs="0"/&gt;
 *         &lt;element name="shipFrom" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="shipTo" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="inventoryLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="ultimateConsignee" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="intermediateDeliveryParty" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="orderLogisticalDateInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderLogisticalDateInformationType" minOccurs="0"/&gt;
 *         &lt;element name="shipmentTransportationInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}ShipmentTransportationInformationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderLogisticalInformationType", propOrder = {
    "commodityTypeCode",
    "shipmentSplitMethodCode",
    "shipFrom",
    "shipTo",
    "inventoryLocation",
    "ultimateConsignee",
    "intermediateDeliveryParty",
    "orderLogisticalDateInformation",
    "shipmentTransportationInformation"
})
public class OrderLogisticalInformationType {

    protected CodeType commodityTypeCode;
    protected ShipmentSplitMethodCodeType shipmentSplitMethodCode;
    protected TransactionalPartyType shipFrom;
    protected TransactionalPartyType shipTo;
    protected TransactionalPartyType inventoryLocation;
    protected TransactionalPartyType ultimateConsignee;
    protected TransactionalPartyType intermediateDeliveryParty;
    protected OrderLogisticalDateInformationType orderLogisticalDateInformation;
    protected ShipmentTransportationInformationType shipmentTransportationInformation;

    /**
     * Ruft den Wert der commodityTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getCommodityTypeCode() {
        return commodityTypeCode;
    }

    /**
     * Legt den Wert der commodityTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setCommodityTypeCode(CodeType value) {
        this.commodityTypeCode = value;
    }

    /**
     * Ruft den Wert der shipmentSplitMethodCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentSplitMethodCodeType }
     *     
     */
    public ShipmentSplitMethodCodeType getShipmentSplitMethodCode() {
        return shipmentSplitMethodCode;
    }

    /**
     * Legt den Wert der shipmentSplitMethodCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentSplitMethodCodeType }
     *     
     */
    public void setShipmentSplitMethodCode(ShipmentSplitMethodCodeType value) {
        this.shipmentSplitMethodCode = value;
    }

    /**
     * Ruft den Wert der shipFrom-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getShipFrom() {
        return shipFrom;
    }

    /**
     * Legt den Wert der shipFrom-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setShipFrom(TransactionalPartyType value) {
        this.shipFrom = value;
    }

    /**
     * Ruft den Wert der shipTo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getShipTo() {
        return shipTo;
    }

    /**
     * Legt den Wert der shipTo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setShipTo(TransactionalPartyType value) {
        this.shipTo = value;
    }

    /**
     * Ruft den Wert der inventoryLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getInventoryLocation() {
        return inventoryLocation;
    }

    /**
     * Legt den Wert der inventoryLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setInventoryLocation(TransactionalPartyType value) {
        this.inventoryLocation = value;
    }

    /**
     * Ruft den Wert der ultimateConsignee-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getUltimateConsignee() {
        return ultimateConsignee;
    }

    /**
     * Legt den Wert der ultimateConsignee-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setUltimateConsignee(TransactionalPartyType value) {
        this.ultimateConsignee = value;
    }

    /**
     * Ruft den Wert der intermediateDeliveryParty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getIntermediateDeliveryParty() {
        return intermediateDeliveryParty;
    }

    /**
     * Legt den Wert der intermediateDeliveryParty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setIntermediateDeliveryParty(TransactionalPartyType value) {
        this.intermediateDeliveryParty = value;
    }

    /**
     * Ruft den Wert der orderLogisticalDateInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderLogisticalDateInformationType }
     *     
     */
    public OrderLogisticalDateInformationType getOrderLogisticalDateInformation() {
        return orderLogisticalDateInformation;
    }

    /**
     * Legt den Wert der orderLogisticalDateInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderLogisticalDateInformationType }
     *     
     */
    public void setOrderLogisticalDateInformation(OrderLogisticalDateInformationType value) {
        this.orderLogisticalDateInformation = value;
    }

    /**
     * Ruft den Wert der shipmentTransportationInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentTransportationInformationType }
     *     
     */
    public ShipmentTransportationInformationType getShipmentTransportationInformation() {
        return shipmentTransportationInformation;
    }

    /**
     * Legt den Wert der shipmentTransportationInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentTransportationInformationType }
     *     
     */
    public void setShipmentTransportationInformation(ShipmentTransportationInformationType value) {
        this.shipmentTransportationInformation = value;
    }

}
