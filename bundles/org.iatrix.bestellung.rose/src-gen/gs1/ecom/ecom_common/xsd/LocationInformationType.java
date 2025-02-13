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

import gs1.shared.shared_common.xsd.AddressType;
import gs1.shared.shared_common.xsd.ContactType;


/**
 * <p>Java-Klasse für LocationInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LocationInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="partyIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{urn:gs1:shared:shared_common:xsd:3}AddressType" minOccurs="0"/&gt;
 *         &lt;element name="contactInformation" type="{urn:gs1:shared:shared_common:xsd:3}ContactType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LocationInformationType", propOrder = {
    "partyIdentification",
    "address",
    "contactInformation"
})
public class LocationInformationType {

    protected EcomPartyIdentificationType partyIdentification;
    protected AddressType address;
    protected List<ContactType> contactInformation;

    /**
     * Ruft den Wert der partyIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getPartyIdentification() {
        return partyIdentification;
    }

    /**
     * Legt den Wert der partyIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setPartyIdentification(EcomPartyIdentificationType value) {
        this.partyIdentification = value;
    }

    /**
     * Ruft den Wert der address-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Legt den Wert der address-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setAddress(AddressType value) {
        this.address = value;
    }

    /**
     * Gets the value of the contactInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the contactInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContactInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactType }
     * 
     * 
     */
    public List<ContactType> getContactInformation() {
        if (contactInformation == null) {
            contactInformation = new ArrayList<ContactType>();
        }
        return this.contactInformation;
    }

}
