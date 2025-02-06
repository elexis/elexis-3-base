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
import gs1.shared.shared_common.xsd.Description200Type;
import gs1.shared.shared_common.xsd.IdentifierType;


/**
 * <p>Java-Klasse für LogisticLocationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LogisticLocationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="unLocationCode" type="{urn:gs1:ecom:ecom_common:xsd:3}UNLocationCodeType" minOccurs="0"/&gt;
 *         &lt;element name="gln" type="{urn:gs1:shared:shared_common:xsd:3}GLNType" minOccurs="0"/&gt;
 *         &lt;element name="additionalLocationIdentification" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="sublocationIdentification" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="locationName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="locationSpecificInstructions" type="{urn:gs1:shared:shared_common:xsd:3}Description200Type" minOccurs="0"/&gt;
 *         &lt;element name="utcOffset" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{urn:gs1:shared:shared_common:xsd:3}AddressType" minOccurs="0"/&gt;
 *         &lt;element name="contact" type="{urn:gs1:shared:shared_common:xsd:3}ContactType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="regularOperatingHours" type="{urn:gs1:ecom:ecom_common:xsd:3}OperatingHoursType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="specialOperatingHours" type="{urn:gs1:ecom:ecom_common:xsd:3}SpecialOperatingHoursType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogisticLocationType", propOrder = {
    "unLocationCode",
    "gln",
    "additionalLocationIdentification",
    "sublocationIdentification",
    "locationName",
    "locationSpecificInstructions",
    "utcOffset",
    "address",
    "contact",
    "regularOperatingHours",
    "specialOperatingHours"
})
public class LogisticLocationType {

    protected UNLocationCodeType unLocationCode;
    protected String gln;
    protected List<IdentifierType> additionalLocationIdentification;
    protected String sublocationIdentification;
    protected String locationName;
    protected Description200Type locationSpecificInstructions;
    protected Float utcOffset;
    protected AddressType address;
    protected List<ContactType> contact;
    protected List<OperatingHoursType> regularOperatingHours;
    protected List<SpecialOperatingHoursType> specialOperatingHours;

    /**
     * Ruft den Wert der unLocationCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UNLocationCodeType }
     *     
     */
    public UNLocationCodeType getUnLocationCode() {
        return unLocationCode;
    }

    /**
     * Legt den Wert der unLocationCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UNLocationCodeType }
     *     
     */
    public void setUnLocationCode(UNLocationCodeType value) {
        this.unLocationCode = value;
    }

    /**
     * Ruft den Wert der gln-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGln() {
        return gln;
    }

    /**
     * Legt den Wert der gln-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGln(String value) {
        this.gln = value;
    }

    /**
     * Gets the value of the additionalLocationIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalLocationIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalLocationIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IdentifierType }
     * 
     * 
     */
    public List<IdentifierType> getAdditionalLocationIdentification() {
        if (additionalLocationIdentification == null) {
            additionalLocationIdentification = new ArrayList<IdentifierType>();
        }
        return this.additionalLocationIdentification;
    }

    /**
     * Ruft den Wert der sublocationIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSublocationIdentification() {
        return sublocationIdentification;
    }

    /**
     * Legt den Wert der sublocationIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSublocationIdentification(String value) {
        this.sublocationIdentification = value;
    }

    /**
     * Ruft den Wert der locationName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Legt den Wert der locationName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocationName(String value) {
        this.locationName = value;
    }

    /**
     * Ruft den Wert der locationSpecificInstructions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description200Type }
     *     
     */
    public Description200Type getLocationSpecificInstructions() {
        return locationSpecificInstructions;
    }

    /**
     * Legt den Wert der locationSpecificInstructions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description200Type }
     *     
     */
    public void setLocationSpecificInstructions(Description200Type value) {
        this.locationSpecificInstructions = value;
    }

    /**
     * Ruft den Wert der utcOffset-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getUtcOffset() {
        return utcOffset;
    }

    /**
     * Legt den Wert der utcOffset-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setUtcOffset(Float value) {
        this.utcOffset = value;
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
     * Gets the value of the regularOperatingHours property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the regularOperatingHours property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegularOperatingHours().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OperatingHoursType }
     * 
     * 
     */
    public List<OperatingHoursType> getRegularOperatingHours() {
        if (regularOperatingHours == null) {
            regularOperatingHours = new ArrayList<OperatingHoursType>();
        }
        return this.regularOperatingHours;
    }

    /**
     * Gets the value of the specialOperatingHours property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the specialOperatingHours property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSpecialOperatingHours().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SpecialOperatingHoursType }
     * 
     * 
     */
    public List<SpecialOperatingHoursType> getSpecialOperatingHours() {
        if (specialOperatingHours == null) {
            specialOperatingHours = new ArrayList<SpecialOperatingHoursType>();
        }
        return this.specialOperatingHours;
    }

}
