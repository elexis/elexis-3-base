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

import gs1.shared.shared_common.xsd.DimensionType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransactionalItemLogisticUnitInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalItemLogisticUnitInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numberOfLayers" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="numberOfUnitsPerLayer" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="numberOfUnitsPerPallet" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="packagingTerms" type="{urn:gs1:ecom:ecom_common:xsd:3}PackagingTermsAndConditionsCodeType" minOccurs="0"/&gt;
 *         &lt;element name="packageTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackageTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="maximumStackingFactor" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger"/&gt;
 *         &lt;element name="returnablePackageTransportCostPayment" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportChargesPaymentMethodCodeType" minOccurs="0"/&gt;
 *         &lt;element name="dimensionsOfLogisticUnit" type="{urn:gs1:shared:shared_common:xsd:3}DimensionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalItemLogisticUnitInformationType", propOrder = {
    "numberOfLayers",
    "numberOfUnitsPerLayer",
    "numberOfUnitsPerPallet",
    "packagingTerms",
    "packageTypeCode",
    "maximumStackingFactor",
    "returnablePackageTransportCostPayment",
    "dimensionsOfLogisticUnit"
})
public class TransactionalItemLogisticUnitInformationType {

    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger numberOfLayers;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger numberOfUnitsPerLayer;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger numberOfUnitsPerPallet;
    protected PackagingTermsAndConditionsCodeType packagingTerms;
    protected PackageTypeCodeType packageTypeCode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger maximumStackingFactor;
    protected TransportChargesPaymentMethodCodeType returnablePackageTransportCostPayment;
    protected List<DimensionType> dimensionsOfLogisticUnit;

    /**
     * Ruft den Wert der numberOfLayers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfLayers() {
        return numberOfLayers;
    }

    /**
     * Legt den Wert der numberOfLayers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfLayers(BigInteger value) {
        this.numberOfLayers = value;
    }

    /**
     * Ruft den Wert der numberOfUnitsPerLayer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfUnitsPerLayer() {
        return numberOfUnitsPerLayer;
    }

    /**
     * Legt den Wert der numberOfUnitsPerLayer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfUnitsPerLayer(BigInteger value) {
        this.numberOfUnitsPerLayer = value;
    }

    /**
     * Ruft den Wert der numberOfUnitsPerPallet-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfUnitsPerPallet() {
        return numberOfUnitsPerPallet;
    }

    /**
     * Legt den Wert der numberOfUnitsPerPallet-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfUnitsPerPallet(BigInteger value) {
        this.numberOfUnitsPerPallet = value;
    }

    /**
     * Ruft den Wert der packagingTerms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackagingTermsAndConditionsCodeType }
     *     
     */
    public PackagingTermsAndConditionsCodeType getPackagingTerms() {
        return packagingTerms;
    }

    /**
     * Legt den Wert der packagingTerms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackagingTermsAndConditionsCodeType }
     *     
     */
    public void setPackagingTerms(PackagingTermsAndConditionsCodeType value) {
        this.packagingTerms = value;
    }

    /**
     * Ruft den Wert der packageTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackageTypeCodeType }
     *     
     */
    public PackageTypeCodeType getPackageTypeCode() {
        return packageTypeCode;
    }

    /**
     * Legt den Wert der packageTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageTypeCodeType }
     *     
     */
    public void setPackageTypeCode(PackageTypeCodeType value) {
        this.packageTypeCode = value;
    }

    /**
     * Ruft den Wert der maximumStackingFactor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaximumStackingFactor() {
        return maximumStackingFactor;
    }

    /**
     * Legt den Wert der maximumStackingFactor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaximumStackingFactor(BigInteger value) {
        this.maximumStackingFactor = value;
    }

    /**
     * Ruft den Wert der returnablePackageTransportCostPayment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportChargesPaymentMethodCodeType }
     *     
     */
    public TransportChargesPaymentMethodCodeType getReturnablePackageTransportCostPayment() {
        return returnablePackageTransportCostPayment;
    }

    /**
     * Legt den Wert der returnablePackageTransportCostPayment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportChargesPaymentMethodCodeType }
     *     
     */
    public void setReturnablePackageTransportCostPayment(TransportChargesPaymentMethodCodeType value) {
        this.returnablePackageTransportCostPayment = value;
    }

    /**
     * Gets the value of the dimensionsOfLogisticUnit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dimensionsOfLogisticUnit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDimensionsOfLogisticUnit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DimensionType }
     * 
     * 
     */
    public List<DimensionType> getDimensionsOfLogisticUnit() {
        if (dimensionsOfLogisticUnit == null) {
            dimensionsOfLogisticUnit = new ArrayList<DimensionType>();
        }
        return this.dimensionsOfLogisticUnit;
    }

}
