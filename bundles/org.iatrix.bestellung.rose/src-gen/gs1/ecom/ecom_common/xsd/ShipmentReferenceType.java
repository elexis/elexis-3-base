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
 * <p>Java-Klasse für ShipmentReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ShipmentReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="shipmentIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ShipmentIdentificationType"/&gt;
 *         &lt;element name="shipper" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="receiver" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentReferenceType", propOrder = {
    "shipmentIdentification",
    "shipper",
    "receiver"
})
public class ShipmentReferenceType {

    @XmlElement(required = true)
    protected EcomShipmentIdentificationType shipmentIdentification;
    protected EcomPartyIdentificationType shipper;
    protected EcomPartyIdentificationType receiver;

    /**
     * Ruft den Wert der shipmentIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomShipmentIdentificationType }
     *     
     */
    public EcomShipmentIdentificationType getShipmentIdentification() {
        return shipmentIdentification;
    }

    /**
     * Legt den Wert der shipmentIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomShipmentIdentificationType }
     *     
     */
    public void setShipmentIdentification(EcomShipmentIdentificationType value) {
        this.shipmentIdentification = value;
    }

    /**
     * Ruft den Wert der shipper-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getShipper() {
        return shipper;
    }

    /**
     * Legt den Wert der shipper-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setShipper(EcomPartyIdentificationType value) {
        this.shipper = value;
    }

    /**
     * Ruft den Wert der receiver-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getReceiver() {
        return receiver;
    }

    /**
     * Legt den Wert der receiver-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setReceiver(EcomPartyIdentificationType value) {
        this.receiver = value;
    }

}
