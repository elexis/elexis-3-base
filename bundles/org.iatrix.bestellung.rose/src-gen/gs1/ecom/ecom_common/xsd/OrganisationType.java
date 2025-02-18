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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.AddressType;
import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.Description80Type;


/**
 * <p>Java-Klasse für OrganisationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrganisationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="organisationName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="issuedCapital" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="legalStructure" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="officialAddress" type="{urn:gs1:shared:shared_common:xsd:3}AddressType" minOccurs="0"/&gt;
 *         &lt;element name="legalRegistration" type="{urn:gs1:ecom:ecom_common:xsd:3}LegalRegistrationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrganisationType", propOrder = {
    "organisationName",
    "issuedCapital",
    "legalStructure",
    "officialAddress",
    "legalRegistration"
})
public class OrganisationType {

    @XmlElement(required = true)
    protected String organisationName;
    protected AmountType issuedCapital;
    protected Description80Type legalStructure;
    protected AddressType officialAddress;
    protected List<LegalRegistrationType> legalRegistration;

    /**
     * Ruft den Wert der organisationName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * Legt den Wert der organisationName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganisationName(String value) {
        this.organisationName = value;
    }

    /**
     * Ruft den Wert der issuedCapital-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getIssuedCapital() {
        return issuedCapital;
    }

    /**
     * Legt den Wert der issuedCapital-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setIssuedCapital(AmountType value) {
        this.issuedCapital = value;
    }

    /**
     * Ruft den Wert der legalStructure-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getLegalStructure() {
        return legalStructure;
    }

    /**
     * Legt den Wert der legalStructure-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setLegalStructure(Description80Type value) {
        this.legalStructure = value;
    }

    /**
     * Ruft den Wert der officialAddress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getOfficialAddress() {
        return officialAddress;
    }

    /**
     * Legt den Wert der officialAddress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setOfficialAddress(AddressType value) {
        this.officialAddress = value;
    }

    /**
     * Gets the value of the legalRegistration property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the legalRegistration property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLegalRegistration().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LegalRegistrationType }
     * 
     * 
     */
    public List<LegalRegistrationType> getLegalRegistration() {
        if (legalRegistration == null) {
            legalRegistration = new ArrayList<LegalRegistrationType>();
        }
        return this.legalRegistration;
    }

}
