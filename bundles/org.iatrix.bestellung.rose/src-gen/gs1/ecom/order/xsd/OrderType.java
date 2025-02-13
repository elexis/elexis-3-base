//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.order.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.ecom.ecom_common.xsd.AdministrativeUnitType;
import gs1.ecom.ecom_common.xsd.AllowanceChargeType;
import gs1.ecom.ecom_common.xsd.DeliveryTermsType;
import gs1.ecom.ecom_common.xsd.EcomDocumentReferenceType;
import gs1.ecom.ecom_common.xsd.EcomDocumentType;
import gs1.ecom.ecom_common.xsd.EcomEntityIdentificationType;
import gs1.ecom.ecom_common.xsd.ErrorOrWarningCodeType;
import gs1.ecom.ecom_common.xsd.OrderEntryTypeCodeType;
import gs1.ecom.ecom_common.xsd.OrderInstructionCodeType;
import gs1.ecom.ecom_common.xsd.OrderLogisticalInformationType;
import gs1.ecom.ecom_common.xsd.OrderTypeCodeType;
import gs1.ecom.ecom_common.xsd.PaymentTermsType;
import gs1.ecom.ecom_common.xsd.ReferencedOrderType;
import gs1.ecom.ecom_common.xsd.StructuredNoteType;
import gs1.ecom.ecom_common.xsd.TransactionalGenericReferenceType;
import gs1.ecom.ecom_common.xsd.TransactionalPartyType;
import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.CurrencyExchangeRateInformationType;
import gs1.shared.shared_common.xsd.DateOptionalTimeType;
import gs1.shared.shared_common.xsd.Description1000Type;
import gs1.shared.shared_common.xsd.Description500Type;


/**
 * <p>Java-Klasse für OrderType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrderType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}EcomDocumentType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="orderIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_EntityIdentificationType"/&gt;
 *         &lt;element name="orderTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="isApplicationReceiptAcknowledgementRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="orderInstructionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderInstructionCodeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="additionalOrderInstruction" type="{urn:gs1:shared:shared_common:xsd:3}Description1000Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="isOrderFreeOfExciseTaxDuty" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="totalMonetaryAmountExcludingTaxes" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="totalMonetaryAmountIncludingTaxes" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="totalTaxAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="orderEntryType" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderEntryTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="orderPriority" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="35"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="note" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type" minOccurs="0"/&gt;
 *         &lt;element name="orderChangeReasonCode" type="{urn:gs1:ecom:ecom_common:xsd:3}ErrorOrWarningCodeType" minOccurs="0"/&gt;
 *         &lt;element name="buyer" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType"/&gt;
 *         &lt;element name="seller" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType"/&gt;
 *         &lt;element name="billTo" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="pickupFrom" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="customsBroker" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="orderLogisticalInformation" type="{urn:gs1:ecom:ecom_common:xsd:3}OrderLogisticalInformationType"/&gt;
 *         &lt;element name="paymentTerms" type="{urn:gs1:ecom:ecom_common:xsd:3}PaymentTermsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="allowanceCharge" type="{urn:gs1:ecom:ecom_common:xsd:3}AllowanceChargeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="referencedOrder" type="{urn:gs1:ecom:ecom_common:xsd:3}ReferencedOrderType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="administrativeUnit" type="{urn:gs1:ecom:ecom_common:xsd:3}AdministrativeUnitType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="structuredNote" type="{urn:gs1:ecom:ecom_common:xsd:3}StructuredNoteType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tradeAgreement" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="promotionalDeal" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="quoteNumber" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="contract" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="customerDocumentReference" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType" minOccurs="0"/&gt;
 *         &lt;element name="currencyExchangeRateInformation" type="{urn:gs1:shared:shared_common:xsd:3}CurrencyExchangeRateInformationType" minOccurs="0"/&gt;
 *         &lt;element name="deliveryTerms" type="{urn:gs1:ecom:ecom_common:xsd:3}DeliveryTermsType" minOccurs="0"/&gt;
 *         &lt;element name="transactionalGenericReference" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalGenericReferenceType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="deliveryDateAccordingToSchedule" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="latestDeliveryDate" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *         &lt;element name="orderLineItem" type="{urn:gs1:ecom:order:xsd:3}OrderLineItemType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderType", propOrder = {
    "orderIdentification",
    "orderTypeCode",
    "isApplicationReceiptAcknowledgementRequired",
    "orderInstructionCode",
    "additionalOrderInstruction",
    "isOrderFreeOfExciseTaxDuty",
    "totalMonetaryAmountExcludingTaxes",
    "totalMonetaryAmountIncludingTaxes",
    "totalTaxAmount",
    "orderEntryType",
    "orderPriority",
    "note",
    "orderChangeReasonCode",
    "buyer",
    "seller",
    "billTo",
    "pickupFrom",
    "customsBroker",
    "orderLogisticalInformation",
    "paymentTerms",
    "allowanceCharge",
    "referencedOrder",
    "administrativeUnit",
    "structuredNote",
    "tradeAgreement",
    "promotionalDeal",
    "quoteNumber",
    "contract",
    "customerDocumentReference",
    "currencyExchangeRateInformation",
    "deliveryTerms",
    "transactionalGenericReference",
    "deliveryDateAccordingToSchedule",
    "latestDeliveryDate",
    "orderLineItem"
})
public class OrderType
    extends EcomDocumentType
{

    @XmlElement(required = true)
    protected EcomEntityIdentificationType orderIdentification;
    protected OrderTypeCodeType orderTypeCode;
    protected Boolean isApplicationReceiptAcknowledgementRequired;
    protected List<OrderInstructionCodeType> orderInstructionCode;
    protected List<Description1000Type> additionalOrderInstruction;
    protected Boolean isOrderFreeOfExciseTaxDuty;
    protected AmountType totalMonetaryAmountExcludingTaxes;
    protected AmountType totalMonetaryAmountIncludingTaxes;
    protected AmountType totalTaxAmount;
    protected OrderEntryTypeCodeType orderEntryType;
    protected String orderPriority;
    protected Description500Type note;
    protected ErrorOrWarningCodeType orderChangeReasonCode;
    @XmlElement(required = true)
    protected TransactionalPartyType buyer;
    @XmlElement(required = true)
    protected TransactionalPartyType seller;
    protected TransactionalPartyType billTo;
    protected TransactionalPartyType pickupFrom;
    protected TransactionalPartyType customsBroker;
    @XmlElement(required = true)
    protected OrderLogisticalInformationType orderLogisticalInformation;
    protected List<PaymentTermsType> paymentTerms;
    protected List<AllowanceChargeType> allowanceCharge;
    protected List<ReferencedOrderType> referencedOrder;
    protected List<AdministrativeUnitType> administrativeUnit;
    protected List<StructuredNoteType> structuredNote;
    protected EcomDocumentReferenceType tradeAgreement;
    protected EcomDocumentReferenceType promotionalDeal;
    protected EcomDocumentReferenceType quoteNumber;
    protected EcomDocumentReferenceType contract;
    protected EcomDocumentReferenceType customerDocumentReference;
    protected CurrencyExchangeRateInformationType currencyExchangeRateInformation;
    protected DeliveryTermsType deliveryTerms;
    protected List<TransactionalGenericReferenceType> transactionalGenericReference;
    protected DateOptionalTimeType deliveryDateAccordingToSchedule;
    protected DateOptionalTimeType latestDeliveryDate;
    @XmlElement(required = true)
    protected List<OrderLineItemType> orderLineItem;

    /**
     * Ruft den Wert der orderIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomEntityIdentificationType }
     *     
     */
    public EcomEntityIdentificationType getOrderIdentification() {
        return orderIdentification;
    }

    /**
     * Legt den Wert der orderIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomEntityIdentificationType }
     *     
     */
    public void setOrderIdentification(EcomEntityIdentificationType value) {
        this.orderIdentification = value;
    }

    /**
     * Ruft den Wert der orderTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderTypeCodeType }
     *     
     */
    public OrderTypeCodeType getOrderTypeCode() {
        return orderTypeCode;
    }

    /**
     * Legt den Wert der orderTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderTypeCodeType }
     *     
     */
    public void setOrderTypeCode(OrderTypeCodeType value) {
        this.orderTypeCode = value;
    }

    /**
     * Ruft den Wert der isApplicationReceiptAcknowledgementRequired-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsApplicationReceiptAcknowledgementRequired() {
        return isApplicationReceiptAcknowledgementRequired;
    }

    /**
     * Legt den Wert der isApplicationReceiptAcknowledgementRequired-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsApplicationReceiptAcknowledgementRequired(Boolean value) {
        this.isApplicationReceiptAcknowledgementRequired = value;
    }

    /**
     * Gets the value of the orderInstructionCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the orderInstructionCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderInstructionCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderInstructionCodeType }
     * 
     * 
     */
    public List<OrderInstructionCodeType> getOrderInstructionCode() {
        if (orderInstructionCode == null) {
            orderInstructionCode = new ArrayList<OrderInstructionCodeType>();
        }
        return this.orderInstructionCode;
    }

    /**
     * Gets the value of the additionalOrderInstruction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalOrderInstruction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalOrderInstruction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description1000Type }
     * 
     * 
     */
    public List<Description1000Type> getAdditionalOrderInstruction() {
        if (additionalOrderInstruction == null) {
            additionalOrderInstruction = new ArrayList<Description1000Type>();
        }
        return this.additionalOrderInstruction;
    }

    /**
     * Ruft den Wert der isOrderFreeOfExciseTaxDuty-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsOrderFreeOfExciseTaxDuty() {
        return isOrderFreeOfExciseTaxDuty;
    }

    /**
     * Legt den Wert der isOrderFreeOfExciseTaxDuty-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsOrderFreeOfExciseTaxDuty(Boolean value) {
        this.isOrderFreeOfExciseTaxDuty = value;
    }

    /**
     * Ruft den Wert der totalMonetaryAmountExcludingTaxes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getTotalMonetaryAmountExcludingTaxes() {
        return totalMonetaryAmountExcludingTaxes;
    }

    /**
     * Legt den Wert der totalMonetaryAmountExcludingTaxes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setTotalMonetaryAmountExcludingTaxes(AmountType value) {
        this.totalMonetaryAmountExcludingTaxes = value;
    }

    /**
     * Ruft den Wert der totalMonetaryAmountIncludingTaxes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getTotalMonetaryAmountIncludingTaxes() {
        return totalMonetaryAmountIncludingTaxes;
    }

    /**
     * Legt den Wert der totalMonetaryAmountIncludingTaxes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setTotalMonetaryAmountIncludingTaxes(AmountType value) {
        this.totalMonetaryAmountIncludingTaxes = value;
    }

    /**
     * Ruft den Wert der totalTaxAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getTotalTaxAmount() {
        return totalTaxAmount;
    }

    /**
     * Legt den Wert der totalTaxAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setTotalTaxAmount(AmountType value) {
        this.totalTaxAmount = value;
    }

    /**
     * Ruft den Wert der orderEntryType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderEntryTypeCodeType }
     *     
     */
    public OrderEntryTypeCodeType getOrderEntryType() {
        return orderEntryType;
    }

    /**
     * Legt den Wert der orderEntryType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderEntryTypeCodeType }
     *     
     */
    public void setOrderEntryType(OrderEntryTypeCodeType value) {
        this.orderEntryType = value;
    }

    /**
     * Ruft den Wert der orderPriority-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderPriority() {
        return orderPriority;
    }

    /**
     * Legt den Wert der orderPriority-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderPriority(String value) {
        this.orderPriority = value;
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
     * Ruft den Wert der orderChangeReasonCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ErrorOrWarningCodeType }
     *     
     */
    public ErrorOrWarningCodeType getOrderChangeReasonCode() {
        return orderChangeReasonCode;
    }

    /**
     * Legt den Wert der orderChangeReasonCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorOrWarningCodeType }
     *     
     */
    public void setOrderChangeReasonCode(ErrorOrWarningCodeType value) {
        this.orderChangeReasonCode = value;
    }

    /**
     * Ruft den Wert der buyer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getBuyer() {
        return buyer;
    }

    /**
     * Legt den Wert der buyer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setBuyer(TransactionalPartyType value) {
        this.buyer = value;
    }

    /**
     * Ruft den Wert der seller-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getSeller() {
        return seller;
    }

    /**
     * Legt den Wert der seller-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setSeller(TransactionalPartyType value) {
        this.seller = value;
    }

    /**
     * Ruft den Wert der billTo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getBillTo() {
        return billTo;
    }

    /**
     * Legt den Wert der billTo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setBillTo(TransactionalPartyType value) {
        this.billTo = value;
    }

    /**
     * Ruft den Wert der pickupFrom-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getPickupFrom() {
        return pickupFrom;
    }

    /**
     * Legt den Wert der pickupFrom-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setPickupFrom(TransactionalPartyType value) {
        this.pickupFrom = value;
    }

    /**
     * Ruft den Wert der customsBroker-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getCustomsBroker() {
        return customsBroker;
    }

    /**
     * Legt den Wert der customsBroker-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setCustomsBroker(TransactionalPartyType value) {
        this.customsBroker = value;
    }

    /**
     * Ruft den Wert der orderLogisticalInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrderLogisticalInformationType }
     *     
     */
    public OrderLogisticalInformationType getOrderLogisticalInformation() {
        return orderLogisticalInformation;
    }

    /**
     * Legt den Wert der orderLogisticalInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrderLogisticalInformationType }
     *     
     */
    public void setOrderLogisticalInformation(OrderLogisticalInformationType value) {
        this.orderLogisticalInformation = value;
    }

    /**
     * Gets the value of the paymentTerms property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the paymentTerms property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentTerms().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentTermsType }
     * 
     * 
     */
    public List<PaymentTermsType> getPaymentTerms() {
        if (paymentTerms == null) {
            paymentTerms = new ArrayList<PaymentTermsType>();
        }
        return this.paymentTerms;
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
     * Gets the value of the referencedOrder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the referencedOrder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReferencedOrder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReferencedOrderType }
     * 
     * 
     */
    public List<ReferencedOrderType> getReferencedOrder() {
        if (referencedOrder == null) {
            referencedOrder = new ArrayList<ReferencedOrderType>();
        }
        return this.referencedOrder;
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
     * Gets the value of the structuredNote property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the structuredNote property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStructuredNote().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StructuredNoteType }
     * 
     * 
     */
    public List<StructuredNoteType> getStructuredNote() {
        if (structuredNote == null) {
            structuredNote = new ArrayList<StructuredNoteType>();
        }
        return this.structuredNote;
    }

    /**
     * Ruft den Wert der tradeAgreement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getTradeAgreement() {
        return tradeAgreement;
    }

    /**
     * Legt den Wert der tradeAgreement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setTradeAgreement(EcomDocumentReferenceType value) {
        this.tradeAgreement = value;
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
     * Ruft den Wert der quoteNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public EcomDocumentReferenceType getQuoteNumber() {
        return quoteNumber;
    }

    /**
     * Legt den Wert der quoteNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomDocumentReferenceType }
     *     
     */
    public void setQuoteNumber(EcomDocumentReferenceType value) {
        this.quoteNumber = value;
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
     * Ruft den Wert der currencyExchangeRateInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyExchangeRateInformationType }
     *     
     */
    public CurrencyExchangeRateInformationType getCurrencyExchangeRateInformation() {
        return currencyExchangeRateInformation;
    }

    /**
     * Legt den Wert der currencyExchangeRateInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyExchangeRateInformationType }
     *     
     */
    public void setCurrencyExchangeRateInformation(CurrencyExchangeRateInformationType value) {
        this.currencyExchangeRateInformation = value;
    }

    /**
     * Ruft den Wert der deliveryTerms-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryTermsType }
     *     
     */
    public DeliveryTermsType getDeliveryTerms() {
        return deliveryTerms;
    }

    /**
     * Legt den Wert der deliveryTerms-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryTermsType }
     *     
     */
    public void setDeliveryTerms(DeliveryTermsType value) {
        this.deliveryTerms = value;
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
     * Gets the value of the orderLineItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the orderLineItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOrderLineItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrderLineItemType }
     * 
     * 
     */
    public List<OrderLineItemType> getOrderLineItem() {
        if (orderLineItem == null) {
            orderLineItem = new ArrayList<OrderLineItemType>();
        }
        return this.orderLineItem;
    }

}
