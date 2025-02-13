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

import gs1.shared.shared_common.xsd.ColourType;
import gs1.shared.shared_common.xsd.Description200Type;
import gs1.shared.shared_common.xsd.QuantityType;
import gs1.shared.shared_common.xsd.SizeType;
import gs1.shared.shared_common.xsd.TradeItemClassificationType;


/**
 * <p>Java-Klasse für TransactionalTradeItemType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalTradeItemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_TradeItemIdentificationType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tradeItemQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="tradeItemDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description200Type" minOccurs="0"/&gt;
 *         &lt;element name="productVariantIdentifier" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="itemTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}ItemTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="tradeItemDataOwner" type="{urn:gs1:ecom:ecom_common:xsd:3}TradeItemDataOwnerCodeType" minOccurs="0"/&gt;
 *         &lt;element name="butterFatReference" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="35"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="transactionalItemData" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemDataType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="colour" type="{urn:gs1:shared:shared_common:xsd:3}ColourType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="size" type="{urn:gs1:shared:shared_common:xsd:3}SizeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tradeItemClassification" type="{urn:gs1:shared:shared_common:xsd:3}TradeItemClassificationType" minOccurs="0"/&gt;
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
@XmlType(name = "TransactionalTradeItemType", propOrder = {
    "tradeItemQuantity",
    "tradeItemDescription",
    "productVariantIdentifier",
    "itemTypeCode",
    "tradeItemDataOwner",
    "butterFatReference",
    "transactionalItemData",
    "colour",
    "size",
    "tradeItemClassification",
    "avpList"
})
public class TransactionalTradeItemType
    extends EcomTradeItemIdentificationType
{

    protected QuantityType tradeItemQuantity;
    protected Description200Type tradeItemDescription;
    protected String productVariantIdentifier;
    protected ItemTypeCodeType itemTypeCode;
    protected TradeItemDataOwnerCodeType tradeItemDataOwner;
    protected String butterFatReference;
    protected List<TransactionalItemDataType> transactionalItemData;
    protected List<ColourType> colour;
    protected List<SizeType> size;
    protected TradeItemClassificationType tradeItemClassification;
    protected EcomAttributeValuePairListType avpList;

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
     * Ruft den Wert der tradeItemDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description200Type }
     *     
     */
    public Description200Type getTradeItemDescription() {
        return tradeItemDescription;
    }

    /**
     * Legt den Wert der tradeItemDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description200Type }
     *     
     */
    public void setTradeItemDescription(Description200Type value) {
        this.tradeItemDescription = value;
    }

    /**
     * Ruft den Wert der productVariantIdentifier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductVariantIdentifier() {
        return productVariantIdentifier;
    }

    /**
     * Legt den Wert der productVariantIdentifier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductVariantIdentifier(String value) {
        this.productVariantIdentifier = value;
    }

    /**
     * Ruft den Wert der itemTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ItemTypeCodeType }
     *     
     */
    public ItemTypeCodeType getItemTypeCode() {
        return itemTypeCode;
    }

    /**
     * Legt den Wert der itemTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemTypeCodeType }
     *     
     */
    public void setItemTypeCode(ItemTypeCodeType value) {
        this.itemTypeCode = value;
    }

    /**
     * Ruft den Wert der tradeItemDataOwner-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TradeItemDataOwnerCodeType }
     *     
     */
    public TradeItemDataOwnerCodeType getTradeItemDataOwner() {
        return tradeItemDataOwner;
    }

    /**
     * Legt den Wert der tradeItemDataOwner-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TradeItemDataOwnerCodeType }
     *     
     */
    public void setTradeItemDataOwner(TradeItemDataOwnerCodeType value) {
        this.tradeItemDataOwner = value;
    }

    /**
     * Ruft den Wert der butterFatReference-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getButterFatReference() {
        return butterFatReference;
    }

    /**
     * Legt den Wert der butterFatReference-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setButterFatReference(String value) {
        this.butterFatReference = value;
    }

    /**
     * Gets the value of the transactionalItemData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalItemData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalItemData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionalItemDataType }
     * 
     * 
     */
    public List<TransactionalItemDataType> getTransactionalItemData() {
        if (transactionalItemData == null) {
            transactionalItemData = new ArrayList<TransactionalItemDataType>();
        }
        return this.transactionalItemData;
    }

    /**
     * Gets the value of the colour property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the colour property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColour().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColourType }
     * 
     * 
     */
    public List<ColourType> getColour() {
        if (colour == null) {
            colour = new ArrayList<ColourType>();
        }
        return this.colour;
    }

    /**
     * Gets the value of the size property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the size property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SizeType }
     * 
     * 
     */
    public List<SizeType> getSize() {
        if (size == null) {
            size = new ArrayList<SizeType>();
        }
        return this.size;
    }

    /**
     * Ruft den Wert der tradeItemClassification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TradeItemClassificationType }
     *     
     */
    public TradeItemClassificationType getTradeItemClassification() {
        return tradeItemClassification;
    }

    /**
     * Legt den Wert der tradeItemClassification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TradeItemClassificationType }
     *     
     */
    public void setTradeItemClassification(TradeItemClassificationType value) {
        this.tradeItemClassification = value;
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
