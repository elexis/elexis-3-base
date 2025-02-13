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

import gs1.shared.shared_common.xsd.DimensionType;
import gs1.shared.shared_common.xsd.LogisticUnitIdentificationType;
import gs1.shared.shared_common.xsd.MeasurementType;
import gs1.shared.shared_common.xsd.QuantityType;


/**
 * <p>Java-Klasse für LogisticUnitType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LogisticUnitType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:shared:shared_common:xsd:3}LogisticUnitIdentificationType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="parentLogisticUnitId" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_LogisticUnitIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="grossWeight" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="packageLevelCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackageLevelCodeType" minOccurs="0"/&gt;
 *         &lt;element name="packageTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackageTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="tradeItemQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="packagingMarking" type="{urn:gs1:ecom:ecom_common:xsd:3}PackagingMarkingType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="referencedTransportEquipment" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportEquipmentType" minOccurs="0"/&gt;
 *         &lt;element name="returnablePackaging" type="{urn:gs1:ecom:ecom_common:xsd:3}ReturnablePackagingType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="dimension" type="{urn:gs1:shared:shared_common:xsd:3}DimensionType" minOccurs="0"/&gt;
 *         &lt;element name="unitMeasurement" type="{urn:gs1:ecom:ecom_common:xsd:3}UnitMeasurementType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogisticUnitType", propOrder = {
    "parentLogisticUnitId",
    "grossWeight",
    "packageLevelCode",
    "packageTypeCode",
    "tradeItemQuantity",
    "packagingMarking",
    "referencedTransportEquipment",
    "returnablePackaging",
    "dimension",
    "unitMeasurement"
})
@XmlSeeAlso({
    ExtendedLogisticUnitType.class
})
public class LogisticUnitType
    extends LogisticUnitIdentificationType
{

    protected EcomLogisticUnitIdentificationType parentLogisticUnitId;
    protected MeasurementType grossWeight;
    protected PackageLevelCodeType packageLevelCode;
    protected PackageTypeCodeType packageTypeCode;
    protected QuantityType tradeItemQuantity;
    protected List<PackagingMarkingType> packagingMarking;
    protected TransportEquipmentType referencedTransportEquipment;
    protected List<ReturnablePackagingType> returnablePackaging;
    protected DimensionType dimension;
    protected List<UnitMeasurementType> unitMeasurement;

    /**
     * Ruft den Wert der parentLogisticUnitId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomLogisticUnitIdentificationType }
     *     
     */
    public EcomLogisticUnitIdentificationType getParentLogisticUnitId() {
        return parentLogisticUnitId;
    }

    /**
     * Legt den Wert der parentLogisticUnitId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomLogisticUnitIdentificationType }
     *     
     */
    public void setParentLogisticUnitId(EcomLogisticUnitIdentificationType value) {
        this.parentLogisticUnitId = value;
    }

    /**
     * Ruft den Wert der grossWeight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getGrossWeight() {
        return grossWeight;
    }

    /**
     * Legt den Wert der grossWeight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setGrossWeight(MeasurementType value) {
        this.grossWeight = value;
    }

    /**
     * Ruft den Wert der packageLevelCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackageLevelCodeType }
     *     
     */
    public PackageLevelCodeType getPackageLevelCode() {
        return packageLevelCode;
    }

    /**
     * Legt den Wert der packageLevelCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageLevelCodeType }
     *     
     */
    public void setPackageLevelCode(PackageLevelCodeType value) {
        this.packageLevelCode = value;
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
     * Ruft den Wert der tradeItemQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getTradeItemQuantity() {
        return tradeItemQuantity;
    }

    /**
     * Legt den Wert der tradeItemQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setTradeItemQuantity(QuantityType value) {
        this.tradeItemQuantity = value;
    }

    /**
     * Gets the value of the packagingMarking property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the packagingMarking property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPackagingMarking().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PackagingMarkingType }
     * 
     * 
     */
    public List<PackagingMarkingType> getPackagingMarking() {
        if (packagingMarking == null) {
            packagingMarking = new ArrayList<PackagingMarkingType>();
        }
        return this.packagingMarking;
    }

    /**
     * Ruft den Wert der referencedTransportEquipment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportEquipmentType }
     *     
     */
    public TransportEquipmentType getReferencedTransportEquipment() {
        return referencedTransportEquipment;
    }

    /**
     * Legt den Wert der referencedTransportEquipment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportEquipmentType }
     *     
     */
    public void setReferencedTransportEquipment(TransportEquipmentType value) {
        this.referencedTransportEquipment = value;
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
     * Ruft den Wert der dimension-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DimensionType }
     *     
     */
    public DimensionType getDimension() {
        return dimension;
    }

    /**
     * Legt den Wert der dimension-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DimensionType }
     *     
     */
    public void setDimension(DimensionType value) {
        this.dimension = value;
    }

    /**
     * Gets the value of the unitMeasurement property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the unitMeasurement property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnitMeasurement().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnitMeasurementType }
     * 
     * 
     */
    public List<UnitMeasurementType> getUnitMeasurement() {
        if (unitMeasurement == null) {
            unitMeasurement = new ArrayList<UnitMeasurementType>();
        }
        return this.unitMeasurement;
    }

}
