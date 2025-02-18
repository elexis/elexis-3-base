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

import gs1.shared.shared_common.xsd.Description1000Type;
import gs1.shared.shared_common.xsd.IdentifierType;


/**
 * <p>Java-Klasse für ShipmentTransportationInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ShipmentTransportationInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="handlingInstructionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}HandlingInstructionCodeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transportMeansType" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportMeansTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="transportMeansID" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType" minOccurs="0"/&gt;
 *         &lt;element name="transportServiceCategoryType" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportServiceCategoryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="transportServiceLevelCode" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportServiceLevelCodeType" minOccurs="0"/&gt;
 *         &lt;element name="routeID" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType" minOccurs="0"/&gt;
 *         &lt;element name="additionalHandlingInstruction" type="{urn:gs1:shared:shared_common:xsd:3}Description1000Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="carrier" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="freightForwarder" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ShipmentTransportationInformationType", propOrder = {
    "handlingInstructionCode",
    "transportMeansType",
    "transportMeansID",
    "transportServiceCategoryType",
    "transportServiceLevelCode",
    "routeID",
    "additionalHandlingInstruction",
    "carrier",
    "freightForwarder"
})
public class ShipmentTransportationInformationType {

    protected List<HandlingInstructionCodeType> handlingInstructionCode;
    protected TransportMeansTypeCodeType transportMeansType;
    protected IdentifierType transportMeansID;
    protected TransportServiceCategoryCodeType transportServiceCategoryType;
    protected TransportServiceLevelCodeType transportServiceLevelCode;
    protected IdentifierType routeID;
    protected List<Description1000Type> additionalHandlingInstruction;
    protected TransactionalPartyType carrier;
    protected TransactionalPartyType freightForwarder;

    /**
     * Gets the value of the handlingInstructionCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the handlingInstructionCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHandlingInstructionCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link HandlingInstructionCodeType }
     * 
     * 
     */
    public List<HandlingInstructionCodeType> getHandlingInstructionCode() {
        if (handlingInstructionCode == null) {
            handlingInstructionCode = new ArrayList<HandlingInstructionCodeType>();
        }
        return this.handlingInstructionCode;
    }

    /**
     * Ruft den Wert der transportMeansType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportMeansTypeCodeType }
     *     
     */
    public TransportMeansTypeCodeType getTransportMeansType() {
        return transportMeansType;
    }

    /**
     * Legt den Wert der transportMeansType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportMeansTypeCodeType }
     *     
     */
    public void setTransportMeansType(TransportMeansTypeCodeType value) {
        this.transportMeansType = value;
    }

    /**
     * Ruft den Wert der transportMeansID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getTransportMeansID() {
        return transportMeansID;
    }

    /**
     * Legt den Wert der transportMeansID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setTransportMeansID(IdentifierType value) {
        this.transportMeansID = value;
    }

    /**
     * Ruft den Wert der transportServiceCategoryType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportServiceCategoryCodeType }
     *     
     */
    public TransportServiceCategoryCodeType getTransportServiceCategoryType() {
        return transportServiceCategoryType;
    }

    /**
     * Legt den Wert der transportServiceCategoryType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportServiceCategoryCodeType }
     *     
     */
    public void setTransportServiceCategoryType(TransportServiceCategoryCodeType value) {
        this.transportServiceCategoryType = value;
    }

    /**
     * Ruft den Wert der transportServiceLevelCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportServiceLevelCodeType }
     *     
     */
    public TransportServiceLevelCodeType getTransportServiceLevelCode() {
        return transportServiceLevelCode;
    }

    /**
     * Legt den Wert der transportServiceLevelCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportServiceLevelCodeType }
     *     
     */
    public void setTransportServiceLevelCode(TransportServiceLevelCodeType value) {
        this.transportServiceLevelCode = value;
    }

    /**
     * Ruft den Wert der routeID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getRouteID() {
        return routeID;
    }

    /**
     * Legt den Wert der routeID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setRouteID(IdentifierType value) {
        this.routeID = value;
    }

    /**
     * Gets the value of the additionalHandlingInstruction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalHandlingInstruction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalHandlingInstruction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description1000Type }
     * 
     * 
     */
    public List<Description1000Type> getAdditionalHandlingInstruction() {
        if (additionalHandlingInstruction == null) {
            additionalHandlingInstruction = new ArrayList<Description1000Type>();
        }
        return this.additionalHandlingInstruction;
    }

    /**
     * Ruft den Wert der carrier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getCarrier() {
        return carrier;
    }

    /**
     * Legt den Wert der carrier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setCarrier(TransactionalPartyType value) {
        this.carrier = value;
    }

    /**
     * Ruft den Wert der freightForwarder-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getFreightForwarder() {
        return freightForwarder;
    }

    /**
     * Legt den Wert der freightForwarder-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setFreightForwarder(TransactionalPartyType value) {
        this.freightForwarder = value;
    }

}
