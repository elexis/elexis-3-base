//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import gs1.shared.shared_common.xsd.ContactType;
import gs1.shared.shared_common.xsd.Description1000Type;
import gs1.shared.shared_common.xsd.Description200Type;
import gs1.shared.shared_common.xsd.IdentifierType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für DangerousGoodsInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DangerousGoodsInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dangerousGoodsUNIdentifier" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType"/&gt;
 *         &lt;element name="dangerousGoodsShippingName" type="{urn:gs1:shared:shared_common:xsd:3}Description200Type"/&gt;
 *         &lt;element name="dangerousGoodsTechnicalName" type="{urn:gs1:shared:shared_common:xsd:3}Description200Type" minOccurs="0"/&gt;
 *         &lt;element name="dangerousGoodsDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description1000Type" minOccurs="0"/&gt;
 *         &lt;element name="contact" type="{urn:gs1:shared:shared_common:xsd:3}ContactType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dangerousGoodsRegulationInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}DangerousGoodsRegulationInformationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DangerousGoodsInformationType", propOrder = {
    "dangerousGoodsUNIdentifier",
    "dangerousGoodsShippingName",
    "dangerousGoodsTechnicalName",
    "dangerousGoodsDescription",
    "contact",
    "dangerousGoodsRegulationInformation"
})
public class DangerousGoodsInformationType {

    @XmlElement(required = true)
    protected IdentifierType dangerousGoodsUNIdentifier;
    @XmlElement(required = true)
    protected Description200Type dangerousGoodsShippingName;
    protected Description200Type dangerousGoodsTechnicalName;
    protected Description1000Type dangerousGoodsDescription;
    protected List<ContactType> contact;
    protected List<DangerousGoodsRegulationInformationType> dangerousGoodsRegulationInformation;

    /**
     * Ruft den Wert der dangerousGoodsUNIdentifier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getDangerousGoodsUNIdentifier() {
        return dangerousGoodsUNIdentifier;
    }

    /**
     * Legt den Wert der dangerousGoodsUNIdentifier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setDangerousGoodsUNIdentifier(IdentifierType value) {
        this.dangerousGoodsUNIdentifier = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsShippingName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description200Type }
     *     
     */
    public Description200Type getDangerousGoodsShippingName() {
        return dangerousGoodsShippingName;
    }

    /**
     * Legt den Wert der dangerousGoodsShippingName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description200Type }
     *     
     */
    public void setDangerousGoodsShippingName(Description200Type value) {
        this.dangerousGoodsShippingName = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsTechnicalName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description200Type }
     *     
     */
    public Description200Type getDangerousGoodsTechnicalName() {
        return dangerousGoodsTechnicalName;
    }

    /**
     * Legt den Wert der dangerousGoodsTechnicalName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description200Type }
     *     
     */
    public void setDangerousGoodsTechnicalName(Description200Type value) {
        this.dangerousGoodsTechnicalName = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description1000Type }
     *     
     */
    public Description1000Type getDangerousGoodsDescription() {
        return dangerousGoodsDescription;
    }

    /**
     * Legt den Wert der dangerousGoodsDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description1000Type }
     *     
     */
    public void setDangerousGoodsDescription(Description1000Type value) {
        this.dangerousGoodsDescription = value;
    }

    /**
     * Gets the value of the contact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the contact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactType }
     * 
     * 
     */
    public List<ContactType> getContact() {
        if (contact == null) {
            contact = new ArrayList<ContactType>();
        }
        return this.contact;
    }

    /**
     * Gets the value of the dangerousGoodsRegulationInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dangerousGoodsRegulationInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDangerousGoodsRegulationInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DangerousGoodsRegulationInformationType }
     * 
     * 
     */
    public List<DangerousGoodsRegulationInformationType> getDangerousGoodsRegulationInformation() {
        if (dangerousGoodsRegulationInformation == null) {
            dangerousGoodsRegulationInformation = new ArrayList<DangerousGoodsRegulationInformationType>();
        }
        return this.dangerousGoodsRegulationInformation;
    }

}
