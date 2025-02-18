//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import gs1.ecom.ecom_common.xsd.*;


/**
 * <p>Java-Klasse für GS1CodeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="GS1CodeType"&gt;
 *   &lt;simpleContent&gt;
 *     &lt;extension base="&lt;urn:gs1:shared:shared_common:xsd:3&gt;String80Type"&gt;
 *       &lt;attribute name="codeListVersion"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="35"/&gt;
 *             &lt;minLength value="1"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/extension&gt;
 *   &lt;/simpleContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GS1CodeType", propOrder = {
    "value"
})
@XmlSeeAlso({
    AdditionalConsignmentIdentificationTypeCodeType.class,
    AdditionalIndividualAssetIdentificationTypeCodeType.class,
    AdditionalLogisticUnitIdentificationTypeCodeType.class,
    AdditionalPartyIdentificationTypeCodeType.class,
    AdditionalReturnableAssetIdentificationTypeCodeType.class,
    AdditionalServiceRelationIdentificationTypeCodeType.class,
    AdditionalShipmentIdentificationTypeCodeType.class,
    AdditionalTradeItemIdentificationTypeCodeType.class,
    AllowanceChargeTypeCodeType.class,
    BarCodeTypeCodeType.class,
    CommunicationChannelCodeType.class,
    ContactTypeCodeType.class,
    CountryCodeType.class,
    CountrySubdivisionCodeType.class,
    CurrencyCodeType.class,
    DateFormatCodeType.class,
    EntityTypeCodeType.class,
    FinancialAccountNumberTypeCodeType.class,
    FinancialRoutingNumberTypeCodeType.class,
    IncotermsCodeType.class,
    LanguageCodeType.class,
    MeasurementUnitCodeType.class,
    NutrientTypeCodeType.class,
    PartyRoleCodeType.class,
    PaymentMethodCodeType.class,
    PaymentTermsTypeCodeType.class,
    TaxCategoryCodeType.class,
    TemperatureMeasurementUnitCodeType.class,
    TimeMeasurementUnitCodeType.class,
    AdministrativeUnitTypeCodeType.class,
    AppointmentTimeMeasurementBasisTypeCodeType.class,
    CargoTypeCodeType.class,
    CreditReasonCodeType.class,
    CustomsWarehouseStatusCodeType.class,
    DangerousGoodsAttributeTypeCodeType.class,
    DangerousGoodsRegulationCodeType.class,
    DataCarrierTypeCodeType.class,
    DeliverDateTypeCodeType.class,
    DeliveryMethodCodeType.class,
    DeliveryTimeMeasurementBasisTypeCodeType.class,
    DeliveryTypeCodeType.class,
    DemandEstimationTypeCodeType.class,
    DiscountAgreementCodeType.class,
    DockTypeCodeType.class,
    DutyFeeTaxExemptionReasonCodeType.class,
    DutyFeeTaxLiabilityCodeType.class,
    DutyFeeTaxRegistrationTypeCodeType.class,
    DutyFeeTaxTypeCodeType.class,
    EffectiveDateTypeCodeType.class,
    ErrorOrWarningCodeType.class,
    EuUniqueIDTypeCodeType.class,
    FinancialAdjustmentReasonCodeType.class,
    ForecastPurposeCodeType.class,
    ForecastTypeCodeType.class,
    GoodsReceiptReportingCodeType.class,
    GS1ItemIdentificationKeyCodeType.class,
    HandlingInstructionCodeType.class,
    HarmonizedSystemCodeType.class,
    IdentityDocumentTypeCodeType.class,
    IncidentTypeCodeType.class,
    InventoryActivityTypeCodeType.class,
    InventoryMeasurementBasisTypeCodeType.class,
    InventoryMovementTypeCodeType.class,
    InventoryStatusCodeType.class,
    InventorySubLocationFunctionCodeType.class,
    InventorySubLocationTypeCodeType.class,
    InvoiceTypeCodeType.class,
    ItemScopeTypeCodeType.class,
    ItemSourceCodeType.class,
    ItemTypeCodeType.class,
    LegalRegistrationCodeType.class,
    LineItemActionCodeType.class,
    LocationScopeParameterTypeCodeType.class,
    LocationScopeTypeCodeType.class,
    LogisticEventTypeCodeType.class,
    LogisticServiceReferenceTypeCodeType.class,
    LogisticServiceRequirementCodeType.class,
    MarginSchemeCodeType.class,
    MaterialTypeCodeType.class,
    MeasurementTypeCodeType.class,
    ObservationTypeCodeType.class,
    OrderEntryTypeCodeType.class,
    OrderInstructionCodeType.class,
    OrderRelationshipTypeCodeType.class,
    OrderTypeCodeType.class,
    OutOfStockMeasurementTypeCodeType.class,
    OwnershipTransferConditionCodeType.class,
    PackageLevelCodeType.class,
    PackageTypeCodeType.class,
    PackagingConditionCodeType.class,
    PackagingMarkingTypeCodeType.class,
    PackagingTermsAndConditionsCodeType.class,
    PassengerCategoryCodeType.class,
    PaymentFormatCodeType.class,
    PaymentTermsEventCodeType.class,
    PerformanceMeasureTypeCodeType.class,
    PeriodicityTypeCodeType.class,
    PhysicalOrLogicalStateDescriptionCodeType.class,
    PlanBucketSizeCodeType.class,
    PlanCommitmentLevelCodeType.class,
    PrintingInstructionCodeType.class,
    PurchaseConditionsCommitmentTypeCodeType.class,
    QualityControlCodeType.class,
    QuantitySpecificationTypeCodeType.class,
    ReceivingConditionCodeType.class,
    RemainingQuantityStatusCodeType.class,
    ResponseStatusCodeType.class,
    SalesMeasurementTypeCodeType.class,
    SealConditionCodeType.class,
    SealTypeCodeType.class,
    ServiceLevelBasisTypeCodeType.class,
    ServiceLevelMeasurementBasisTypeCodeType.class,
    SettlementHandlingTypeCodeType.class,
    SettlementTypeCodeType.class,
    ShipmentSplitMethodCodeType.class,
    SpecialServiceTypeCodeType.class,
    StockRequirementTypeCodeType.class,
    StructureTypeCodeType.class,
    SymbolComponentCodeType.class,
    SynchronisationCalculationTypeCodeType.class,
    TimePeriodScopeTypeCodeType.class,
    TradeItemDataOwnerCodeType.class,
    TradeItemUnitDescriptorCodeType.class,
    TransactionalReferenceTypeCodeType.class,
    TransportChargesPaymentMethodCodeType.class,
    TransportInstructionStatusReasonCodeType.class,
    TransportMeansTypeCodeType.class,
    TransportModeCodeType.class,
    TransportPartyRoleCodeType.class,
    TransportPaymentMethodCodeType.class,
    TransportReferenceTypeCodeType.class,
    TransportServiceCategoryCodeType.class,
    TransportServiceConditionTypeCodeType.class,
    TransportServiceLevelCodeType.class,
    TransportStatusConditionCodeType.class,
    TransportStatusReasonCodeType.class,
    UNLocationCodeType.class,
    VarianceReasonCodeType.class,
    WarehouseABCClassificationCodeType.class
})
public class GS1CodeType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "codeListVersion")
    protected String codeListVersion;

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Ruft den Wert der codeListVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodeListVersion() {
        return codeListVersion;
    }

    /**
     * Legt den Wert der codeListVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodeListVersion(String value) {
        this.codeListVersion = value;
    }

}
