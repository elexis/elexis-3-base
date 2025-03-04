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
 * <p>Java-Klasse für CarrierPickUpDropOffDetailsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CarrierPickUpDropOffDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="carrier" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="transportResponsiblePerson" type="{urn:gs1:ecom:ecom_common:xsd:3}PersonType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transportMeans" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportMeansType" minOccurs="0"/&gt;
 *         &lt;element name="transportEquipment" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportEquipmentType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CarrierPickUpDropOffDetailsType", propOrder = {
    "carrier",
    "transportResponsiblePerson",
    "transportMeans",
    "transportEquipment"
})
public class CarrierPickUpDropOffDetailsType {

    protected TransactionalPartyType carrier;
    protected List<PersonType> transportResponsiblePerson;
    protected TransportMeansType transportMeans;
    protected List<TransportEquipmentType> transportEquipment;

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
     * Gets the value of the transportResponsiblePerson property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transportResponsiblePerson property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportResponsiblePerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonType }
     * 
     * 
     */
    public List<PersonType> getTransportResponsiblePerson() {
        if (transportResponsiblePerson == null) {
            transportResponsiblePerson = new ArrayList<PersonType>();
        }
        return this.transportResponsiblePerson;
    }

    /**
     * Ruft den Wert der transportMeans-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportMeansType }
     *     
     */
    public TransportMeansType getTransportMeans() {
        return transportMeans;
    }

    /**
     * Legt den Wert der transportMeans-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportMeansType }
     *     
     */
    public void setTransportMeans(TransportMeansType value) {
        this.transportMeans = value;
    }

    /**
     * Gets the value of the transportEquipment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transportEquipment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransportEquipment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransportEquipmentType }
     * 
     * 
     */
    public List<TransportEquipmentType> getTransportEquipment() {
        if (transportEquipment == null) {
            transportEquipment = new ArrayList<TransportEquipmentType>();
        }
        return this.transportEquipment;
    }

}
