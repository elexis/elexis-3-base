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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.AddressType;
import gs1.shared.shared_common.xsd.ContactType;


/**
 * <p>Java-Klasse für TransactionalPartyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalPartyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="address" type="{urn:gs1:shared:shared_common:xsd:3}AddressType" minOccurs="0"/&gt;
 *         &lt;element name="contact" type="{urn:gs1:shared:shared_common:xsd:3}ContactType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxRegistration" type="{urn:gs1:ecom:ecom_common:xsd:3}DutyFeeTaxRegistrationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="organisationDetails" type="{urn:gs1:ecom:ecom_common:xsd:3}OrganisationType" minOccurs="0"/&gt;
 *         &lt;element name="financialInstitutionInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}FinancialInstitutionInformationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="avpList" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_AttributeValuePairListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalPartyType", propOrder = {
    "address",
    "contact",
    "dutyFeeTaxRegistration",
    "organisationDetails",
    "financialInstitutionInformation",
    "avpList"
})
@XmlSeeAlso({
    TransactionalPartyInRoleType.class
})
public class TransactionalPartyType
    extends EcomPartyIdentificationType
{

    protected AddressType address;
    protected List<ContactType> contact;
    protected List<DutyFeeTaxRegistrationType> dutyFeeTaxRegistration;
    protected OrganisationType organisationDetails;
    protected List<FinancialInstitutionInformationType> financialInstitutionInformation;
    protected EcomAttributeValuePairListType avpList;

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
     * Gets the value of the dutyFeeTaxRegistration property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dutyFeeTaxRegistration property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDutyFeeTaxRegistration().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DutyFeeTaxRegistrationType }
     * 
     * 
     */
    public List<DutyFeeTaxRegistrationType> getDutyFeeTaxRegistration() {
        if (dutyFeeTaxRegistration == null) {
            dutyFeeTaxRegistration = new ArrayList<DutyFeeTaxRegistrationType>();
        }
        return this.dutyFeeTaxRegistration;
    }

    /**
     * Ruft den Wert der organisationDetails-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrganisationType }
     *     
     */
    public OrganisationType getOrganisationDetails() {
        return organisationDetails;
    }

    /**
     * Legt den Wert der organisationDetails-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganisationType }
     *     
     */
    public void setOrganisationDetails(OrganisationType value) {
        this.organisationDetails = value;
    }

    /**
     * Gets the value of the financialInstitutionInformation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the financialInstitutionInformation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFinancialInstitutionInformation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FinancialInstitutionInformationType }
     * 
     * 
     */
    public List<FinancialInstitutionInformationType> getFinancialInstitutionInformation() {
        if (financialInstitutionInformation == null) {
            financialInstitutionInformation = new ArrayList<FinancialInstitutionInformationType>();
        }
        return this.financialInstitutionInformation;
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
