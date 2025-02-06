//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.IdentifierType;


/**
 * <p>Java-Klasse für ReturnablePackagingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ReturnablePackagingType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="packagingQuantity" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/&gt;
 *         &lt;element name="currentHolderRegistration" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType" minOccurs="0"/&gt;
 *         &lt;element name="newHolderRegistration" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType" minOccurs="0"/&gt;
 *         &lt;element name="packagingConditionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackagingConditionCodeType" minOccurs="0"/&gt;
 *         &lt;element name="returnableAssetIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ReturnableAssetIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="individualReturnableAssetIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ReturnableAssetIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnablePackagingType", propOrder = {
    "packagingQuantity",
    "currentHolderRegistration",
    "newHolderRegistration",
    "packagingConditionCode",
    "returnableAssetIdentification",
    "individualReturnableAssetIdentification"
})
public class ReturnablePackagingType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger packagingQuantity;
    protected IdentifierType currentHolderRegistration;
    protected IdentifierType newHolderRegistration;
    protected PackagingConditionCodeType packagingConditionCode;
    protected EcomReturnableAssetIdentificationType returnableAssetIdentification;
    protected List<EcomReturnableAssetIdentificationType> individualReturnableAssetIdentification;

    /**
     * Ruft den Wert der packagingQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPackagingQuantity() {
        return packagingQuantity;
    }

    /**
     * Legt den Wert der packagingQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPackagingQuantity(BigInteger value) {
        this.packagingQuantity = value;
    }

    /**
     * Ruft den Wert der currentHolderRegistration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getCurrentHolderRegistration() {
        return currentHolderRegistration;
    }

    /**
     * Legt den Wert der currentHolderRegistration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setCurrentHolderRegistration(IdentifierType value) {
        this.currentHolderRegistration = value;
    }

    /**
     * Ruft den Wert der newHolderRegistration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getNewHolderRegistration() {
        return newHolderRegistration;
    }

    /**
     * Legt den Wert der newHolderRegistration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setNewHolderRegistration(IdentifierType value) {
        this.newHolderRegistration = value;
    }

    /**
     * Ruft den Wert der packagingConditionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackagingConditionCodeType }
     *     
     */
    public PackagingConditionCodeType getPackagingConditionCode() {
        return packagingConditionCode;
    }

    /**
     * Legt den Wert der packagingConditionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackagingConditionCodeType }
     *     
     */
    public void setPackagingConditionCode(PackagingConditionCodeType value) {
        this.packagingConditionCode = value;
    }

    /**
     * Ruft den Wert der returnableAssetIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomReturnableAssetIdentificationType }
     *     
     */
    public EcomReturnableAssetIdentificationType getReturnableAssetIdentification() {
        return returnableAssetIdentification;
    }

    /**
     * Legt den Wert der returnableAssetIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomReturnableAssetIdentificationType }
     *     
     */
    public void setReturnableAssetIdentification(EcomReturnableAssetIdentificationType value) {
        this.returnableAssetIdentification = value;
    }

    /**
     * Gets the value of the individualReturnableAssetIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the individualReturnableAssetIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndividualReturnableAssetIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomReturnableAssetIdentificationType }
     * 
     * 
     */
    public List<EcomReturnableAssetIdentificationType> getIndividualReturnableAssetIdentification() {
        if (individualReturnableAssetIdentification == null) {
            individualReturnableAssetIdentification = new ArrayList<EcomReturnableAssetIdentificationType>();
        }
        return this.individualReturnableAssetIdentification;
    }

}
