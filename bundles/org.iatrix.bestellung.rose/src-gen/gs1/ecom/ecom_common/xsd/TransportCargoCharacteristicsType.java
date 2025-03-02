//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.CountryCodeType;
import gs1.shared.shared_common.xsd.Description200Type;
import gs1.shared.shared_common.xsd.MeasurementType;
import gs1.shared.shared_common.xsd.QuantityType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransportCargoCharacteristicsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportCargoCharacteristicsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cargoTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}CargoTypeCodeType"/&gt;
 *         &lt;element name="harmonizedSystemCode" type="{urn:gs1:ecom:ecom_common:xsd:3}HarmonizedSystemCodeType" minOccurs="0"/&gt;
 *         &lt;element name="cargoTypeDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description200Type" minOccurs="0"/&gt;
 *         &lt;element name="countryOfOriginCode" type="{urn:gs1:shared:shared_common:xsd:3}CountryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="finalDestinationCountry" type="{urn:gs1:shared:shared_common:xsd:3}CountryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="totalGrossVolume" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="totalGrossWeight" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="totalTransportNetWeight" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="totalChargeableWeight" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="declaredWeightForCustoms" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="totalLoadingLength" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="associatedInvoiceAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="declaredValueForCustoms" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="totalPackageQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="totalItemQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportCargoCharacteristicsType", propOrder = {
    "cargoTypeCode",
    "harmonizedSystemCode",
    "cargoTypeDescription",
    "countryOfOriginCode",
    "finalDestinationCountry",
    "totalGrossVolume",
    "totalGrossWeight",
    "totalTransportNetWeight",
    "totalChargeableWeight",
    "declaredWeightForCustoms",
    "totalLoadingLength",
    "associatedInvoiceAmount",
    "declaredValueForCustoms",
    "totalPackageQuantity",
    "totalItemQuantity"
})
public class TransportCargoCharacteristicsType {

    @XmlElement(required = true)
    protected CargoTypeCodeType cargoTypeCode;
    protected HarmonizedSystemCodeType harmonizedSystemCode;
    protected Description200Type cargoTypeDescription;
    protected CountryCodeType countryOfOriginCode;
    protected CountryCodeType finalDestinationCountry;
    protected MeasurementType totalGrossVolume;
    protected MeasurementType totalGrossWeight;
    protected MeasurementType totalTransportNetWeight;
    protected MeasurementType totalChargeableWeight;
    protected MeasurementType declaredWeightForCustoms;
    protected MeasurementType totalLoadingLength;
    protected AmountType associatedInvoiceAmount;
    protected AmountType declaredValueForCustoms;
    protected QuantityType totalPackageQuantity;
    protected QuantityType totalItemQuantity;

    /**
     * Ruft den Wert der cargoTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CargoTypeCodeType }
     *     
     */
    public CargoTypeCodeType getCargoTypeCode() {
        return cargoTypeCode;
    }

    /**
     * Legt den Wert der cargoTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CargoTypeCodeType }
     *     
     */
    public void setCargoTypeCode(CargoTypeCodeType value) {
        this.cargoTypeCode = value;
    }

    /**
     * Ruft den Wert der harmonizedSystemCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HarmonizedSystemCodeType }
     *     
     */
    public HarmonizedSystemCodeType getHarmonizedSystemCode() {
        return harmonizedSystemCode;
    }

    /**
     * Legt den Wert der harmonizedSystemCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HarmonizedSystemCodeType }
     *     
     */
    public void setHarmonizedSystemCode(HarmonizedSystemCodeType value) {
        this.harmonizedSystemCode = value;
    }

    /**
     * Ruft den Wert der cargoTypeDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description200Type }
     *     
     */
    public Description200Type getCargoTypeDescription() {
        return cargoTypeDescription;
    }

    /**
     * Legt den Wert der cargoTypeDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description200Type }
     *     
     */
    public void setCargoTypeDescription(Description200Type value) {
        this.cargoTypeDescription = value;
    }

    /**
     * Ruft den Wert der countryOfOriginCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CountryCodeType }
     *     
     */
    public CountryCodeType getCountryOfOriginCode() {
        return countryOfOriginCode;
    }

    /**
     * Legt den Wert der countryOfOriginCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryCodeType }
     *     
     */
    public void setCountryOfOriginCode(CountryCodeType value) {
        this.countryOfOriginCode = value;
    }

    /**
     * Ruft den Wert der finalDestinationCountry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CountryCodeType }
     *     
     */
    public CountryCodeType getFinalDestinationCountry() {
        return finalDestinationCountry;
    }

    /**
     * Legt den Wert der finalDestinationCountry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryCodeType }
     *     
     */
    public void setFinalDestinationCountry(CountryCodeType value) {
        this.finalDestinationCountry = value;
    }

    /**
     * Ruft den Wert der totalGrossVolume-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalGrossVolume() {
        return totalGrossVolume;
    }

    /**
     * Legt den Wert der totalGrossVolume-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalGrossVolume(MeasurementType value) {
        this.totalGrossVolume = value;
    }

    /**
     * Ruft den Wert der totalGrossWeight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalGrossWeight() {
        return totalGrossWeight;
    }

    /**
     * Legt den Wert der totalGrossWeight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalGrossWeight(MeasurementType value) {
        this.totalGrossWeight = value;
    }

    /**
     * Ruft den Wert der totalTransportNetWeight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalTransportNetWeight() {
        return totalTransportNetWeight;
    }

    /**
     * Legt den Wert der totalTransportNetWeight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalTransportNetWeight(MeasurementType value) {
        this.totalTransportNetWeight = value;
    }

    /**
     * Ruft den Wert der totalChargeableWeight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalChargeableWeight() {
        return totalChargeableWeight;
    }

    /**
     * Legt den Wert der totalChargeableWeight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalChargeableWeight(MeasurementType value) {
        this.totalChargeableWeight = value;
    }

    /**
     * Ruft den Wert der declaredWeightForCustoms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getDeclaredWeightForCustoms() {
        return declaredWeightForCustoms;
    }

    /**
     * Legt den Wert der declaredWeightForCustoms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setDeclaredWeightForCustoms(MeasurementType value) {
        this.declaredWeightForCustoms = value;
    }

    /**
     * Ruft den Wert der totalLoadingLength-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalLoadingLength() {
        return totalLoadingLength;
    }

    /**
     * Legt den Wert der totalLoadingLength-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalLoadingLength(MeasurementType value) {
        this.totalLoadingLength = value;
    }

    /**
     * Ruft den Wert der associatedInvoiceAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getAssociatedInvoiceAmount() {
        return associatedInvoiceAmount;
    }

    /**
     * Legt den Wert der associatedInvoiceAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setAssociatedInvoiceAmount(AmountType value) {
        this.associatedInvoiceAmount = value;
    }

    /**
     * Ruft den Wert der declaredValueForCustoms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDeclaredValueForCustoms() {
        return declaredValueForCustoms;
    }

    /**
     * Legt den Wert der declaredValueForCustoms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDeclaredValueForCustoms(AmountType value) {
        this.declaredValueForCustoms = value;
    }

    /**
     * Ruft den Wert der totalPackageQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getTotalPackageQuantity() {
        return totalPackageQuantity;
    }

    /**
     * Legt den Wert der totalPackageQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setTotalPackageQuantity(QuantityType value) {
        this.totalPackageQuantity = value;
    }

    /**
     * Ruft den Wert der totalItemQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getTotalItemQuantity() {
        return totalItemQuantity;
    }

    /**
     * Legt den Wert der totalItemQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setTotalItemQuantity(QuantityType value) {
        this.totalItemQuantity = value;
    }

}
