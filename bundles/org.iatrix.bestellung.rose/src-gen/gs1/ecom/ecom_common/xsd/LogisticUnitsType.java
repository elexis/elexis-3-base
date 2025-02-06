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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für LogisticUnitsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LogisticUnitsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="levelIdentification" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="parentLevelIdentification" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="packageTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackageTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="quantityOfLogisticUnits" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="childPackageTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackageTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="quantityOfChildren" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="logisticUnitIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_LogisticUnitIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="logisticUnitMeasurement" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticUnitMeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="returnablePackaging" type="{urn:gs1:ecom:ecom_common:xsd:3}ReturnablePackagingType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="individualAssetIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_IndividualAssetIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="carrierTrackAndTraceInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}CarrierTrackAndTraceInformationType" minOccurs="0"/&gt;
 *         &lt;element name="avpList" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_AttributeValuePairListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogisticUnitsType", propOrder = {
    "levelIdentification",
    "parentLevelIdentification",
    "packageTypeCode",
    "quantityOfLogisticUnits",
    "childPackageTypeCode",
    "quantityOfChildren",
    "logisticUnitIdentification",
    "logisticUnitMeasurement",
    "returnablePackaging",
    "individualAssetIdentification",
    "carrierTrackAndTraceInformation",
    "avpList"
})
public class LogisticUnitsType {

    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger levelIdentification;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger parentLevelIdentification;
    protected PackageTypeCodeType packageTypeCode;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger quantityOfLogisticUnits;
    protected PackageTypeCodeType childPackageTypeCode;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger quantityOfChildren;
    protected EcomLogisticUnitIdentificationType logisticUnitIdentification;
    protected LogisticUnitMeasurementType logisticUnitMeasurement;
    protected List<ReturnablePackagingType> returnablePackaging;
    protected List<EcomIndividualAssetIdentificationType> individualAssetIdentification;
    protected CarrierTrackAndTraceInformationType carrierTrackAndTraceInformation;
    protected EcomAttributeValuePairListType avpList;

    /**
     * Ruft den Wert der levelIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLevelIdentification() {
        return levelIdentification;
    }

    /**
     * Legt den Wert der levelIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLevelIdentification(BigInteger value) {
        this.levelIdentification = value;
    }

    /**
     * Ruft den Wert der parentLevelIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getParentLevelIdentification() {
        return parentLevelIdentification;
    }

    /**
     * Legt den Wert der parentLevelIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setParentLevelIdentification(BigInteger value) {
        this.parentLevelIdentification = value;
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
     * Ruft den Wert der quantityOfLogisticUnits-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQuantityOfLogisticUnits() {
        return quantityOfLogisticUnits;
    }

    /**
     * Legt den Wert der quantityOfLogisticUnits-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQuantityOfLogisticUnits(BigInteger value) {
        this.quantityOfLogisticUnits = value;
    }

    /**
     * Ruft den Wert der childPackageTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackageTypeCodeType }
     *     
     */
    public PackageTypeCodeType getChildPackageTypeCode() {
        return childPackageTypeCode;
    }

    /**
     * Legt den Wert der childPackageTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageTypeCodeType }
     *     
     */
    public void setChildPackageTypeCode(PackageTypeCodeType value) {
        this.childPackageTypeCode = value;
    }

    /**
     * Ruft den Wert der quantityOfChildren-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getQuantityOfChildren() {
        return quantityOfChildren;
    }

    /**
     * Legt den Wert der quantityOfChildren-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setQuantityOfChildren(BigInteger value) {
        this.quantityOfChildren = value;
    }

    /**
     * Ruft den Wert der logisticUnitIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomLogisticUnitIdentificationType }
     *     
     */
    public EcomLogisticUnitIdentificationType getLogisticUnitIdentification() {
        return logisticUnitIdentification;
    }

    /**
     * Legt den Wert der logisticUnitIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomLogisticUnitIdentificationType }
     *     
     */
    public void setLogisticUnitIdentification(EcomLogisticUnitIdentificationType value) {
        this.logisticUnitIdentification = value;
    }

    /**
     * Ruft den Wert der logisticUnitMeasurement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LogisticUnitMeasurementType }
     *     
     */
    public LogisticUnitMeasurementType getLogisticUnitMeasurement() {
        return logisticUnitMeasurement;
    }

    /**
     * Legt den Wert der logisticUnitMeasurement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LogisticUnitMeasurementType }
     *     
     */
    public void setLogisticUnitMeasurement(LogisticUnitMeasurementType value) {
        this.logisticUnitMeasurement = value;
    }

    /**
     * Gets the value of the returnablePackaging property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the returnablePackaging property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturnablePackaging().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReturnablePackagingType }
     * 
     * 
     */
    public List<ReturnablePackagingType> getReturnablePackaging() {
        if (returnablePackaging == null) {
            returnablePackaging = new ArrayList<ReturnablePackagingType>();
        }
        return this.returnablePackaging;
    }

    /**
     * Gets the value of the individualAssetIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the individualAssetIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndividualAssetIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomIndividualAssetIdentificationType }
     * 
     * 
     */
    public List<EcomIndividualAssetIdentificationType> getIndividualAssetIdentification() {
        if (individualAssetIdentification == null) {
            individualAssetIdentification = new ArrayList<EcomIndividualAssetIdentificationType>();
        }
        return this.individualAssetIdentification;
    }

    /**
     * Ruft den Wert der carrierTrackAndTraceInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CarrierTrackAndTraceInformationType }
     *     
     */
    public CarrierTrackAndTraceInformationType getCarrierTrackAndTraceInformation() {
        return carrierTrackAndTraceInformation;
    }

    /**
     * Legt den Wert der carrierTrackAndTraceInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CarrierTrackAndTraceInformationType }
     *     
     */
    public void setCarrierTrackAndTraceInformation(CarrierTrackAndTraceInformationType value) {
        this.carrierTrackAndTraceInformation = value;
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
