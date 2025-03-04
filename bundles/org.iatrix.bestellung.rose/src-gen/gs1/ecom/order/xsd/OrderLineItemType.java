//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.order.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import gs1.ecom.ecom_common.xsd.AcceptableOverAllocationType;
import gs1.ecom.ecom_common.xsd.AdministrativeUnitType;
import gs1.ecom.ecom_common.xsd.AllowanceChargeType;
import gs1.ecom.ecom_common.xsd.EcomAttributeValuePairListType;
import gs1.ecom.ecom_common.xsd.EcomDocumentReferenceType;
import gs1.ecom.ecom_common.xsd.EcomReturnableAssetIdentificationType;
import gs1.ecom.ecom_common.xsd.EndCustomerRelatedDetailsType;
import gs1.ecom.ecom_common.xsd.EuUniqueIDType;
import gs1.ecom.ecom_common.xsd.ItemSourceCodeType;
import gs1.ecom.ecom_common.xsd.LeviedDutyFeeTaxType;
import gs1.ecom.ecom_common.xsd.LineItemActionCodeType;
import gs1.ecom.ecom_common.xsd.OrderInstructionCodeType;
import gs1.ecom.ecom_common.xsd.PhysicalOrLogicalStateDescriptionCodeType;
import gs1.ecom.ecom_common.xsd.ReferencedOrderType;
import gs1.ecom.ecom_common.xsd.ShipmentTransportationInformationType;
import gs1.ecom.ecom_common.xsd.TransactionalGenericReferenceType;
import gs1.ecom.ecom_common.xsd.TransactionalPartyType;
import gs1.ecom.ecom_common.xsd.TransactionalTradeItemType;
import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.ContactType;
import gs1.shared.shared_common.xsd.DateOptionalTimeType;
import gs1.shared.shared_common.xsd.Description200Type;
import gs1.shared.shared_common.xsd.Description500Type;
import gs1.shared.shared_common.xsd.ExtensionType;
import gs1.shared.shared_common.xsd.QuantityType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für OrderLineItemType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrderLineItemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="lineItemNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/&gt;
 *         &lt;element name="requestedQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType"/&gt;
 *         &lt;element name="lineItemActionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}LineItemActionCodeType" minOccurs="0"/&gt;
 *         &lt;element name="additionalOrderLineInstruction" type="{urn:gs1:shared:shared_common:xsd:3}Description200Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="netAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="netPrice" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="listPrice" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="monetaryAmountExcludingTaxes" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="monetaryAmountIncludingTaxes" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="itemPriceBaseQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="parentLineItemNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="recommendedRetailPrice" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="orderLineItemInstructionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderInstructionCodeType" minOccurs="0"/&gt;
 *         &lt;element name="orderLineItemPriority" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="35"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="freeGoodsQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="note" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type" minOccurs="0"/&gt;
 *         &lt;element name="extension" type="{urn:gs1:shared:shared_common:xsd:3}ExtensionType" minOccurs="0"/&gt;
 *         &lt;element name="itemSourceCode" type="{urn:gs1:ecom:ecom_common:xsd:3}ItemSourceCodeType" minOccurs="0"/&gt;
 *         &lt;element name="orderInstructionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderInstructionCodeType" minOccurs="0"/&gt;
 *         &lt;element name="returnReasonCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PhysicalOrLogicalStateDescriptionCodeType" minOccurs="0"/&gt;
 *         &lt;element name="transactionalTradeItem" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalTradeItemType"/&gt;
 *         &lt;element name="allowanceCharge" type="{urn:gs1:ecom:ecom_common:xsd:3}AllowanceChargeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="shipmentTransportationInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}ShipmentTransportationInformationType" minOccurs="0"/&gt;
 *         &lt;element name="preferredManufacturer" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="endCustomerRelatedDetails" type="{urn:gs1:ecom:ecom_common:xsd:3}EndCustomerRelatedDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="deliveryDateAccordingToSchedule" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="latestDeliveryDate" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="orderPackagingInstructions" type="{urn:gs1:ecom:order:xsd:3}OrderPackagingInstructionsType" minOccurs="0"/&gt;
 *         &lt;element name="administrativeUnit" type="{urn:gs1:ecom:ecom_common:xsd:3}AdministrativeUnitType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="acceptableOverAllocation" type="{urn:gs1:ecom:ecom_common:xsd:3}AcceptableOverAllocationType" minOccurs="0"/&gt;
 *         &lt;element name="returnableAssetIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ReturnableAssetIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="euUniqueID" type="{urn:gs1:ecom:ecom_common:xsd:3}EuUniqueIDType" minOccurs="0"/&gt;
 *         &lt;element name="promotionalDeal" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="purchaseConditions" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="materialSpecification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="contract" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="despatchAdvice" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="customerDocumentReference" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="leviedDutyFeeTax" type="{urn:gs1:ecom:ecom_common:xsd:3}LeviedDutyFeeTaxType" minOccurs="0"/&gt;
 *         &lt;element name="orderLineItemContact" type="{urn:gs1:shared:shared_common:xsd:3}ContactType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="referencedOrder" type="{urn:gs1:ecom:ecom_common:xsd:3}ReferencedOrderType" minOccurs="0"/&gt;
 *         &lt;element name="transactionalGenericReference" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalGenericReferenceType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="orderLineItemDetail" type="{urn:gs1:ecom:order:xsd:3}OrderLineItemDetailType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "OrderLineItemType", propOrder = {
    "lineItemNumber",
    "requestedQuantity",
    "lineItemActionCode",
    "additionalOrderLineInstruction",
    "netAmount",
    "netPrice",
    "listPrice",
    "monetaryAmountExcludingTaxes",
    "monetaryAmountIncludingTaxes",
    "itemPriceBaseQuantity",
    "parentLineItemNumber",
    "recommendedRetailPrice",
    "orderLineItemInstructionCode",
    "orderLineItemPriority",
    "freeGoodsQuantity",
    "note",
    "extension",
    "itemSourceCode",
    "orderInstructionCode",
    "returnReasonCode",
    "transactionalTradeItem",
    "allowanceCharge",
    "shipmentTransportationInformation",
    "preferredManufacturer",
    "endCustomerRelatedDetails",
    "deliveryDateAccordingToSchedule",
    "latestDeliveryDate",
    "orderPackagingInstructions",
    "administrativeUnit",
    "acceptableOverAllocation",
    "returnableAssetIdentification",
    "euUniqueID",
    "promotionalDeal",
    "purchaseConditions",
    "materialSpecification",
    "contract",
    "despatchAdvice",
    "customerDocumentReference",
    "leviedDutyFeeTax",
    "orderLineItemContact",
    "referencedOrder",
    "transactionalGenericReference",
    "orderLineItemDetail",
    "avpList"
})
public class OrderLineItemType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger lineItemNumber;
    @XmlElement(required = true)
    protected QuantityType requestedQuantity;
    protected LineItemActionCodeType lineItemActionCode;
    protected List<Description200Type> additionalOrderLineInstruction;
    protected AmountType netAmount;
    protected AmountType netPrice;
    protected AmountType listPrice;
    protected AmountType monetaryAmountExcludingTaxes;
    protected AmountType monetaryAmountIncludingTaxes;
    protected QuantityType itemPriceBaseQuantity;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger parentLineItemNumber;
    protected AmountType recommendedRetailPrice;
    protected OrderInstructionCodeType orderLineItemInstructionCode;
    protected String orderLineItemPriority;
    protected QuantityType freeGoodsQuantity;
    protected Description500Type note;
    protected ExtensionType extension;
    protected ItemSourceCodeType itemSourceCode;
    protected OrderInstructionCodeType orderInstructionCode;
    protected PhysicalOrLogicalStateDescriptionCodeType returnReasonCode;
    @XmlElement(required = true)
    protected TransactionalTradeItemType transactionalTradeItem;
    protected List<AllowanceChargeType> allowanceCharge;
    protected ShipmentTransportationInformationType shipmentTransportationInformation;
    protected TransactionalPartyType preferredManufacturer;
    protected EndCustomerRelatedDetailsType endCustomerRelatedDetails;
    protected DateOptionalTimeType deliveryDateAccordingToSchedule;
    protected DateOptionalTimeType latestDeliveryDate;
    protected OrderPackagingInstructionsType orderPackagingInstructions;
    protected List<AdministrativeUnitType> administrativeUnit;
    protected AcceptableOverAllocationType acceptableOverAllocation;
    protected EcomReturnableAssetIdentificationType returnableAssetIdentification;
    protected EuUniqueIDType euUniqueID;
    protected EcomDocumentReferenceType promotionalDeal;
    protected EcomDocumentReferenceType purchaseConditions;
    protected EcomDocumentReferenceType materialSpecification;
    protected EcomDocumentReferenceType contract;
    protected EcomDocumentReferenceType despatchAdvice;
    protected EcomDocumentReferenceType customerDocumentReference;
    protected LeviedDutyFeeTaxType leviedDutyFeeTax;
    protected List<ContactType> orderLineItemContact;
    protected ReferencedOrderType referencedOrder;
    protected List<TransactionalGenericReferenceType> transactionalGenericReference;
    protected List<OrderLineItemDetailType> orderLineItemDetail;
    protected EcomAttributeValuePairListType avpList;

    /**
     * Ruft den Wert der lineItemNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLineItemNumber() {
        return lineItemNumber;
    }

    /**
     * Legt den Wert der lineItemNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLineItemNumber(BigInteger value) {
        this.lineItemNumber = value;
    }

    /**
     * Ruft den Wert der requestedQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getRequestedQuantity() {
        return requestedQuantity;
    }

    /**
     * Legt den Wert der requestedQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setRequestedQuantity(QuantityType value) {
        this.requestedQuantity = value;
    }

    /**
     * Ruft den Wert der lineItemActionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LineItemActionCodeType }
     *     
     */
    public LineItemActionCodeType getLineItemActionCode() {
        return lineItemActionCode;
    }

    /**
     * Legt den Wert der lineItemActionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LineItemActionCodeType }
     *     
     */
    public void setLineItemActionCode(LineItemActionCodeType value) {
        this.lineItemActionCode = value;
    }

    /**
     * Gets the value of the additionalOrderLineInstruction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalOrderLineInstruction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalOrderLineInstruction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description200Type }
     * 
     * 
     */
    public List<Description200Type> getAdditionalOrderLineInstruction() {
        if (additionalOrderLineInstruction == null) {
            additionalOrderLineInstruction = new ArrayList<Description200Type>();
        }
        return this.additionalOrderLineInstruction;
    }

    /**
     * Ruft den Wert der netAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getNetAmount() {
        return netAmount;
    }

    /**
     * Legt den Wert der netAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setNetAmount(AmountType value) {
        this.netAmount = value;
    }

    /**
     * Ruft den Wert der netPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getNetPrice() {
        return netPrice;
    }

    /**
     * Legt den Wert der netPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setNetPrice(AmountType value) {
        this.netPrice = value;
    }

    /**
     * Ruft den Wert der listPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getListPrice() {
        return listPrice;
    }

    /**
     * Legt den Wert der listPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setListPrice(AmountType value) {
        this.listPrice = value;
    }

    /**
     * Ruft den Wert der monetaryAmountExcludingTaxes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getMonetaryAmountExcludingTaxes() {
        return monetaryAmountExcludingTaxes;
    }

    /**
     * Legt den Wert der monetaryAmountExcludingTaxes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setMonetaryAmountExcludingTaxes(AmountType value) {
        this.monetaryAmountExcludingTaxes = value;
    }

    /**
     * Ruft den Wert der monetaryAmountIncludingTaxes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getMonetaryAmountIncludingTaxes() {
        return monetaryAmountIncludingTaxes;
    }

    /**
     * Legt den Wert der monetaryAmountIncludingTaxes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setMonetaryAmountIncludingTaxes(AmountType value) {
        this.monetaryAmountIncludingTaxes = value;
    }

    /**
     * Ruft den Wert der itemPriceBaseQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getItemPriceBaseQuantity() {
        return itemPriceBaseQuantity;
    }

    /**
     * Legt den Wert der itemPriceBaseQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setItemPriceBaseQuantity(QuantityType value) {
        this.itemPriceBaseQuantity = value;
    }

    /**
     * Ruft den Wert der parentLineItemNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getParentLineItemNumber() {
        return parentLineItemNumber;
    }

    /**
     * Legt den Wert der parentLineItemNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setParentLineItemNumber(BigInteger value) {
        this.parentLineItemNumber = value;
    }

    /**
     * Ruft den Wert der recommendedRetailPrice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getRecommendedRetailPrice() {
        return recommendedRetailPrice;
    }

    /**
     * Legt den Wert der recommendedRetailPrice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setRecommendedRetailPrice(AmountType value) {
        this.recommendedRetailPrice = value;
    }

    /**
     * Ruft den Wert der orderLineItemInstructionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderInstructionCodeType }
     *     
     */
    public OrderInstructionCodeType getOrderLineItemInstructionCode() {
        return orderLineItemInstructionCode;
    }

    /**
     * Legt den Wert der orderLineItemInstructionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderInstructionCodeType }
     *     
     */
    public void setOrderLineItemInstructionCode(OrderInstructionCodeType value) {
        this.orderLineItemInstructionCode = value;
    }

    /**
     * Ruft den Wert der orderLineItemPriority-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderLineItemPriority() {
        return orderLineItemPriority;
    }

    /**
     * Legt den Wert der orderLineItemPriority-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderLineItemPriority(String value) {
        this.orderLineItemPriority = value;
    }

    /**
     * Ruft den Wert der freeGoodsQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getFreeGoodsQuantity() {
        return freeGoodsQuantity;
    }

    /**
     * Legt den Wert der freeGoodsQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setFreeGoodsQuantity(QuantityType value) {
        this.freeGoodsQuantity = value;
    }

    /**
     * Ruft den Wert der note-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description500Type }
     *     
     */
    public Description500Type getNote() {
        return note;
    }

    /**
     * Legt den Wert der note-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description500Type }
     *     
     */
    public void setNote(Description500Type value) {
        this.note = value;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *     
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *     
     */
    public void setExtension(ExtensionType value) {
        this.extension = value;
    }

    /**
     * Ruft den Wert der itemSourceCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ItemSourceCodeType }
     *     
     */
    public ItemSourceCodeType getItemSourceCode() {
        return itemSourceCode;
    }

    /**
     * Legt den Wert der itemSourceCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ItemSourceCodeType }
     *     
     */
    public void setItemSourceCode(ItemSourceCodeType value) {
        this.itemSourceCode = value;
    }

    /**
     * Ruft den Wert der orderInstructionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderInstructionCodeType }
     *     
     */
    public OrderInstructionCodeType getOrderInstructionCode() {
        return orderInstructionCode;
    }

    /**
     * Legt den Wert der orderInstructionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderInstructionCodeType }
     *     
     */
    public void setOrderInstructionCode(OrderInstructionCodeType value) {
        this.orderInstructionCode = value;
    }

    /**
     * Ruft den Wert der returnReasonCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PhysicalOrLogicalStateDescriptionCodeType }
     *     
     */
    public PhysicalOrLogicalStateDescriptionCodeType getReturnReasonCode() {
        return returnReasonCode;
    }

    /**
     * Legt den Wert der returnReasonCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PhysicalOrLogicalStateDescriptionCodeType }
     *     
     */
    public void setReturnReasonCode(PhysicalOrLogicalStateDescriptionCodeType value) {
        this.returnReasonCode = value;
    }

    /**
     * Ruft den Wert der transactionalTradeItem-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalTradeItemType }
     *     
     */
    public TransactionalTradeItemType getTransactionalTradeItem() {
        return transactionalTradeItem;
    }

    /**
     * Legt den Wert der transactionalTradeItem-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalTradeItemType }
     *     
     */
    public void setTransactionalTradeItem(TransactionalTradeItemType value) {
        this.transactionalTradeItem = value;
    }

    /**
     * Gets the value of the allowanceCharge property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the allowanceCharge property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllowanceCharge().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllowanceChargeType }
     * 
     * 
     */
    public List<AllowanceChargeType> getAllowanceCharge() {
        if (allowanceCharge == null) {
            allowanceCharge = new ArrayList<AllowanceChargeType>();
        }
        return this.allowanceCharge;
    }

    /**
     * Ruft den Wert der shipmentTransportationInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ShipmentTransportationInformationType }
     *     
     */
    public ShipmentTransportationInformationType getShipmentTransportationInformation() {
        return shipmentTransportationInformation;
    }

    /**
     * Legt den Wert der shipmentTransportationInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ShipmentTransportationInformationType }
     *     
     */
    public void setShipmentTransportationInformation(ShipmentTransportationInformationType value) {
        this.shipmentTransportationInformation = value;
    }

    /**
     * Ruft den Wert der preferredManufacturer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getPreferredManufacturer() {
        return preferredManufacturer;
    }

    /**
     * Legt den Wert der preferredManufacturer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setPreferredManufacturer(TransactionalPartyType value) {
        this.preferredManufacturer = value;
    }

    /**
     * Ruft den Wert der endCustomerRelatedDetails-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EndCustomerRelatedDetailsType }
     *     
     */
    public EndCustomerRelatedDetailsType getEndCustomerRelatedDetails() {
        return endCustomerRelatedDetails;
    }

    /**
     * Legt den Wert der endCustomerRelatedDetails-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EndCustomerRelatedDetailsType }
     *     
     */
    public void setEndCustomerRelatedDetails(EndCustomerRelatedDetailsType value) {
        this.endCustomerRelatedDetails = value;
    }

    /**
     * Ruft den Wert der deliveryDateAccordingToSchedule-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getDeliveryDateAccordingToSchedule() {
        return deliveryDateAccordingToSchedule;
    }

    /**
     * Legt den Wert der deliveryDateAccordingToSchedule-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setDeliveryDateAccordingToSchedule(DateOptionalTimeType value) {
        this.deliveryDateAccordingToSchedule = value;
    }

    /**
     * Ruft den Wert der latestDeliveryDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getLatestDeliveryDate() {
        return latestDeliveryDate;
    }

    /**
     * Legt den Wert der latestDeliveryDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setLatestDeliveryDate(DateOptionalTimeType value) {
        this.latestDeliveryDate = value;
    }

    /**
     * Ruft den Wert der orderPackagingInstructions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderPackagingInstructionsType }
     *     
     */
    public OrderPackagingInstructionsType getOrderPackagingInstructions() {
        return orderPackagingInstructions;
    }

    /**
     * Legt den Wert der orderPackagingInstructions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderPackagingInstructionsType }
     *     
     */
    public void setOrderPackagingInstructions(OrderPackagingInstructionsType value) {
        this.orderPackagingInstructions = value;
    }

    /**
     * Gets the value of the administrativeUnit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the administrativeUnit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdministrativeUnit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdministrativeUnitType }
     * 
     * 
     */
    public List<AdministrativeUnitType> getAdministrativeUnit() {
        if (administrativeUnit == null) {
            administrativeUnit = new ArrayList<AdministrativeUnitType>();
        }
        return this.administrativeUnit;
    }

    /**
     * Ruft den Wert der acceptableOverAllocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AcceptableOverAllocationType }
     *     
     */
    public AcceptableOverAllocationType getAcceptableOverAllocation() {
        return acceptableOverAllocation;
    }

    /**
     * Legt den Wert der acceptableOverAllocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptableOverAllocationType }
     *     
     */
    public void setAcceptableOverAllocation(AcceptableOverAllocationType value) {
        this.acceptableOverAllocation = value;
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
     * Ruft den Wert der euUniqueID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EuUniqueIDType }
     *     
     */
    public EuUniqueIDType getEuUniqueID() {
        return euUniqueID;
    }

    /**
     * Legt den Wert der euUniqueID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EuUniqueIDType }
     *     
     */
    public void setEuUniqueID(EuUniqueIDType value) {
        this.euUniqueID = value;
    }

    /**
     * Ruft den Wert der promotionalDeal-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getPromotionalDeal() {
        return promotionalDeal;
    }

    /**
     * Legt den Wert der promotionalDeal-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setPromotionalDeal(EcomDocumentReferenceType value) {
        this.promotionalDeal = value;
    }

    /**
     * Ruft den Wert der purchaseConditions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getPurchaseConditions() {
        return purchaseConditions;
    }

    /**
     * Legt den Wert der purchaseConditions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setPurchaseConditions(EcomDocumentReferenceType value) {
        this.purchaseConditions = value;
    }

    /**
     * Ruft den Wert der materialSpecification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getMaterialSpecification() {
        return materialSpecification;
    }

    /**
     * Legt den Wert der materialSpecification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setMaterialSpecification(EcomDocumentReferenceType value) {
        this.materialSpecification = value;
    }

    /**
     * Ruft den Wert der contract-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getContract() {
        return contract;
    }

    /**
     * Legt den Wert der contract-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setContract(EcomDocumentReferenceType value) {
        this.contract = value;
    }

    /**
     * Ruft den Wert der despatchAdvice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getDespatchAdvice() {
        return despatchAdvice;
    }

    /**
     * Legt den Wert der despatchAdvice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setDespatchAdvice(EcomDocumentReferenceType value) {
        this.despatchAdvice = value;
    }

    /**
     * Ruft den Wert der customerDocumentReference-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getCustomerDocumentReference() {
        return customerDocumentReference;
    }

    /**
     * Legt den Wert der customerDocumentReference-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setCustomerDocumentReference(EcomDocumentReferenceType value) {
        this.customerDocumentReference = value;
    }

    /**
     * Ruft den Wert der leviedDutyFeeTax-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LeviedDutyFeeTaxType }
     *     
     */
    public LeviedDutyFeeTaxType getLeviedDutyFeeTax() {
        return leviedDutyFeeTax;
    }

    /**
     * Legt den Wert der leviedDutyFeeTax-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LeviedDutyFeeTaxType }
     *     
     */
    public void setLeviedDutyFeeTax(LeviedDutyFeeTaxType value) {
        this.leviedDutyFeeTax = value;
    }

    /**
     * Gets the value of the orderLineItemContact property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the orderLineItemContact property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderLineItemContact().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContactType }
     * 
     * 
     */
    public List<ContactType> getOrderLineItemContact() {
        if (orderLineItemContact == null) {
            orderLineItemContact = new ArrayList<ContactType>();
        }
        return this.orderLineItemContact;
    }

    /**
     * Ruft den Wert der referencedOrder-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferencedOrderType }
     *     
     */
    public ReferencedOrderType getReferencedOrder() {
        return referencedOrder;
    }

    /**
     * Legt den Wert der referencedOrder-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferencedOrderType }
     *     
     */
    public void setReferencedOrder(ReferencedOrderType value) {
        this.referencedOrder = value;
    }

    /**
     * Gets the value of the transactionalGenericReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalGenericReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalGenericReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionalGenericReferenceType }
     * 
     * 
     */
    public List<TransactionalGenericReferenceType> getTransactionalGenericReference() {
        if (transactionalGenericReference == null) {
            transactionalGenericReference = new ArrayList<TransactionalGenericReferenceType>();
        }
        return this.transactionalGenericReference;
    }

    /**
     * Gets the value of the orderLineItemDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the orderLineItemDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderLineItemDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderLineItemDetailType }
     * 
     * 
     */
    public List<OrderLineItemDetailType> getOrderLineItemDetail() {
        if (orderLineItemDetail == null) {
            orderLineItemDetail = new ArrayList<OrderLineItemDetailType>();
        }
        return this.orderLineItemDetail;
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
