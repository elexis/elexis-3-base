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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.CountryCodeType;
import gs1.shared.shared_common.xsd.DimensionType;
import gs1.shared.shared_common.xsd.QuantityType;
import gs1.shared.shared_common.xsd.StringRangeType;


/**
 * <p>Java-Klasse für TransactionalItemDataType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalItemDataType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="availableForSaleDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="batchNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="bestBeforeDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="countryOfOrigin" type="{urn:gs1:shared:shared_common:xsd:3}CountryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="itemExpirationDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="lotNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="packagingDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="productionDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="productQualityIndication" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="sellByDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="serialNumber" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="shelfLife" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="tradeItemQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="itemInContactWithFoodProduct" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemWeight" type="{urn:gs1:ecom:ecom_common:xsd:3}UnitMeasurementType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemVolume" type="{urn:gs1:ecom:ecom_common:xsd:3}UnitMeasurementType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="serialNumberRange" type="{urn:gs1:shared:shared_common:xsd:3}StringRangeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemDimensions" type="{urn:gs1:shared:shared_common:xsd:3}DimensionType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemLogisticUnitInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemLogisticUnitInformationType" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemDataCarrierAndIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemDataCarrierAndIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="tradeItemWaste" type="{urn:gs1:ecom:ecom_common:xsd:3}WasteDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemOrganicInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemOrganicInformationType" minOccurs="0"/&gt;
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
@XmlType(name = "TransactionalItemDataType", propOrder = {
    "availableForSaleDate",
    "batchNumber",
    "bestBeforeDate",
    "countryOfOrigin",
    "itemExpirationDate",
    "lotNumber",
    "packagingDate",
    "productionDate",
    "productQualityIndication",
    "sellByDate",
    "serialNumber",
    "shelfLife",
    "tradeItemQuantity",
    "itemInContactWithFoodProduct",
    "transactionalItemWeight",
    "transactionalItemVolume",
    "serialNumberRange",
    "transactionalItemDimensions",
    "transactionalItemLogisticUnitInformation",
    "transactionalItemDataCarrierAndIdentification",
    "tradeItemWaste",
    "transactionalItemOrganicInformation",
    "avpList"
})
public class TransactionalItemDataType {

    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar availableForSaleDate;
    protected String batchNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar bestBeforeDate;
    protected CountryCodeType countryOfOrigin;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar itemExpirationDate;
    protected String lotNumber;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar packagingDate;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar productionDate;
    protected QuantityType productQualityIndication;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar sellByDate;
    protected List<String> serialNumber;
    protected String shelfLife;
    protected QuantityType tradeItemQuantity;
    protected Boolean itemInContactWithFoodProduct;
    protected List<UnitMeasurementType> transactionalItemWeight;
    protected List<UnitMeasurementType> transactionalItemVolume;
    protected List<StringRangeType> serialNumberRange;
    protected List<DimensionType> transactionalItemDimensions;
    protected TransactionalItemLogisticUnitInformationType transactionalItemLogisticUnitInformation;
    protected TransactionalItemDataCarrierAndIdentificationType transactionalItemDataCarrierAndIdentification;
    protected List<WasteDetailsType> tradeItemWaste;
    protected TransactionalItemOrganicInformationType transactionalItemOrganicInformation;
    protected EcomAttributeValuePairListType avpList;

    /**
     * Ruft den Wert der availableForSaleDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAvailableForSaleDate() {
        return availableForSaleDate;
    }

    /**
     * Legt den Wert der availableForSaleDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAvailableForSaleDate(XMLGregorianCalendar value) {
        this.availableForSaleDate = value;
    }

    /**
     * Ruft den Wert der batchNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * Legt den Wert der batchNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatchNumber(String value) {
        this.batchNumber = value;
    }

    /**
     * Ruft den Wert der bestBeforeDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBestBeforeDate() {
        return bestBeforeDate;
    }

    /**
     * Legt den Wert der bestBeforeDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBestBeforeDate(XMLGregorianCalendar value) {
        this.bestBeforeDate = value;
    }

    /**
     * Ruft den Wert der countryOfOrigin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CountryCodeType }
     *     
     */
    public CountryCodeType getCountryOfOrigin() {
        return countryOfOrigin;
    }

    /**
     * Legt den Wert der countryOfOrigin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryCodeType }
     *     
     */
    public void setCountryOfOrigin(CountryCodeType value) {
        this.countryOfOrigin = value;
    }

    /**
     * Ruft den Wert der itemExpirationDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getItemExpirationDate() {
        return itemExpirationDate;
    }

    /**
     * Legt den Wert der itemExpirationDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setItemExpirationDate(XMLGregorianCalendar value) {
        this.itemExpirationDate = value;
    }

    /**
     * Ruft den Wert der lotNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * Legt den Wert der lotNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLotNumber(String value) {
        this.lotNumber = value;
    }

    /**
     * Ruft den Wert der packagingDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPackagingDate() {
        return packagingDate;
    }

    /**
     * Legt den Wert der packagingDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPackagingDate(XMLGregorianCalendar value) {
        this.packagingDate = value;
    }

    /**
     * Ruft den Wert der productionDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProductionDate() {
        return productionDate;
    }

    /**
     * Legt den Wert der productionDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProductionDate(XMLGregorianCalendar value) {
        this.productionDate = value;
    }

    /**
     * Ruft den Wert der productQualityIndication-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getProductQualityIndication() {
        return productQualityIndication;
    }

    /**
     * Legt den Wert der productQualityIndication-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setProductQualityIndication(QuantityType value) {
        this.productQualityIndication = value;
    }

    /**
     * Ruft den Wert der sellByDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSellByDate() {
        return sellByDate;
    }

    /**
     * Legt den Wert der sellByDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSellByDate(XMLGregorianCalendar value) {
        this.sellByDate = value;
    }

    /**
     * Gets the value of the serialNumber property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the serialNumber property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerialNumber().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSerialNumber() {
        if (serialNumber == null) {
            serialNumber = new ArrayList<String>();
        }
        return this.serialNumber;
    }

    /**
     * Ruft den Wert der shelfLife-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShelfLife() {
        return shelfLife;
    }

    /**
     * Legt den Wert der shelfLife-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShelfLife(String value) {
        this.shelfLife = value;
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
     * Ruft den Wert der itemInContactWithFoodProduct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isItemInContactWithFoodProduct() {
        return itemInContactWithFoodProduct;
    }

    /**
     * Legt den Wert der itemInContactWithFoodProduct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setItemInContactWithFoodProduct(Boolean value) {
        this.itemInContactWithFoodProduct = value;
    }

    /**
     * Gets the value of the transactionalItemWeight property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalItemWeight property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalItemWeight().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnitMeasurementType }
     * 
     * 
     */
    public List<UnitMeasurementType> getTransactionalItemWeight() {
        if (transactionalItemWeight == null) {
            transactionalItemWeight = new ArrayList<UnitMeasurementType>();
        }
        return this.transactionalItemWeight;
    }

    /**
     * Gets the value of the transactionalItemVolume property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalItemVolume property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalItemVolume().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link UnitMeasurementType }
     * 
     * 
     */
    public List<UnitMeasurementType> getTransactionalItemVolume() {
        if (transactionalItemVolume == null) {
            transactionalItemVolume = new ArrayList<UnitMeasurementType>();
        }
        return this.transactionalItemVolume;
    }

    /**
     * Gets the value of the serialNumberRange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the serialNumberRange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSerialNumberRange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StringRangeType }
     * 
     * 
     */
    public List<StringRangeType> getSerialNumberRange() {
        if (serialNumberRange == null) {
            serialNumberRange = new ArrayList<StringRangeType>();
        }
        return this.serialNumberRange;
    }

    /**
     * Gets the value of the transactionalItemDimensions property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalItemDimensions property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalItemDimensions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DimensionType }
     * 
     * 
     */
    public List<DimensionType> getTransactionalItemDimensions() {
        if (transactionalItemDimensions == null) {
            transactionalItemDimensions = new ArrayList<DimensionType>();
        }
        return this.transactionalItemDimensions;
    }

    /**
     * Ruft den Wert der transactionalItemLogisticUnitInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalItemLogisticUnitInformationType }
     *     
     */
    public TransactionalItemLogisticUnitInformationType getTransactionalItemLogisticUnitInformation() {
        return transactionalItemLogisticUnitInformation;
    }

    /**
     * Legt den Wert der transactionalItemLogisticUnitInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalItemLogisticUnitInformationType }
     *     
     */
    public void setTransactionalItemLogisticUnitInformation(TransactionalItemLogisticUnitInformationType value) {
        this.transactionalItemLogisticUnitInformation = value;
    }

    /**
     * Ruft den Wert der transactionalItemDataCarrierAndIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalItemDataCarrierAndIdentificationType }
     *     
     */
    public TransactionalItemDataCarrierAndIdentificationType getTransactionalItemDataCarrierAndIdentification() {
        return transactionalItemDataCarrierAndIdentification;
    }

    /**
     * Legt den Wert der transactionalItemDataCarrierAndIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalItemDataCarrierAndIdentificationType }
     *     
     */
    public void setTransactionalItemDataCarrierAndIdentification(TransactionalItemDataCarrierAndIdentificationType value) {
        this.transactionalItemDataCarrierAndIdentification = value;
    }

    /**
     * Gets the value of the tradeItemWaste property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the tradeItemWaste property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTradeItemWaste().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WasteDetailsType }
     * 
     * 
     */
    public List<WasteDetailsType> getTradeItemWaste() {
        if (tradeItemWaste == null) {
            tradeItemWaste = new ArrayList<WasteDetailsType>();
        }
        return this.tradeItemWaste;
    }

    /**
     * Ruft den Wert der transactionalItemOrganicInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalItemOrganicInformationType }
     *     
     */
    public TransactionalItemOrganicInformationType getTransactionalItemOrganicInformation() {
        return transactionalItemOrganicInformation;
    }

    /**
     * Legt den Wert der transactionalItemOrganicInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalItemOrganicInformationType }
     *     
     */
    public void setTransactionalItemOrganicInformation(TransactionalItemOrganicInformationType value) {
        this.transactionalItemOrganicInformation = value;
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
