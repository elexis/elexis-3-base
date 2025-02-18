//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.math.BigInteger;

import gs1.shared.shared_common.xsd.AllowanceChargeTypeCodeType;
import gs1.shared.shared_common.xsd.AllowanceOrChargeEnumerationType;
import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.MeasurementType;
import gs1.shared.shared_common.xsd.MultiDescription70Type;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für AllowanceChargeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AllowanceChargeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="allowanceChargeType" type="{urn:gs1:shared:shared_common:xsd:3}AllowanceChargeTypeCodeType"/&gt;
 *         &lt;element name="allowanceOrChargeType" type="{urn:gs1:shared:shared_common:xsd:3}AllowanceOrChargeEnumerationType"/&gt;
 *         &lt;element name="settlementType" type="{urn:gs1:ecom:ecom_common:xsd:3}SettlementTypeCodeType"/&gt;
 *         &lt;element name="allowanceChargeAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="allowanceChargePercentage" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="amountPerUnit" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="baseAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="baseNumberOfUnits" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="bracketIdentifier" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="effectiveDateType" type="{urn:gs1:ecom:ecom_common:xsd:3}EffectiveDateTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="sequenceNumber" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/&gt;
 *         &lt;element name="specialServiceType" type="{urn:gs1:ecom:ecom_common:xsd:3}SpecialServiceTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="allowanceChargeDescription" type="{urn:gs1:shared:shared_common:xsd:3}MultiDescription70Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AllowanceChargeType", propOrder = {
    "allowanceChargeType",
    "allowanceOrChargeType",
    "settlementType",
    "allowanceChargeAmount",
    "allowanceChargePercentage",
    "amountPerUnit",
    "baseAmount",
    "baseNumberOfUnits",
    "bracketIdentifier",
    "effectiveDateType",
    "sequenceNumber",
    "specialServiceType",
    "allowanceChargeDescription"
})
public class AllowanceChargeType {

    @XmlElement(required = true)
    protected AllowanceChargeTypeCodeType allowanceChargeType;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected AllowanceOrChargeEnumerationType allowanceOrChargeType;
    @XmlElement(required = true)
    protected SettlementTypeCodeType settlementType;
    protected AmountType allowanceChargeAmount;
    protected Float allowanceChargePercentage;
    protected AmountType amountPerUnit;
    protected AmountType baseAmount;
    protected MeasurementType baseNumberOfUnits;
    protected String bracketIdentifier;
    protected EffectiveDateTypeCodeType effectiveDateType;
    @XmlSchemaType(name = "nonNegativeInteger")
    protected BigInteger sequenceNumber;
    protected SpecialServiceTypeCodeType specialServiceType;
    protected MultiDescription70Type allowanceChargeDescription;

    /**
     * Ruft den Wert der allowanceChargeType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AllowanceChargeTypeCodeType }
     *     
     */
    public AllowanceChargeTypeCodeType getAllowanceChargeType() {
        return allowanceChargeType;
    }

    /**
     * Legt den Wert der allowanceChargeType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowanceChargeTypeCodeType }
     *     
     */
    public void setAllowanceChargeType(AllowanceChargeTypeCodeType value) {
        this.allowanceChargeType = value;
    }

    /**
     * Ruft den Wert der allowanceOrChargeType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AllowanceOrChargeEnumerationType }
     *     
     */
    public AllowanceOrChargeEnumerationType getAllowanceOrChargeType() {
        return allowanceOrChargeType;
    }

    /**
     * Legt den Wert der allowanceOrChargeType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AllowanceOrChargeEnumerationType }
     *     
     */
    public void setAllowanceOrChargeType(AllowanceOrChargeEnumerationType value) {
        this.allowanceOrChargeType = value;
    }

    /**
     * Ruft den Wert der settlementType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SettlementTypeCodeType }
     *     
     */
    public SettlementTypeCodeType getSettlementType() {
        return settlementType;
    }

    /**
     * Legt den Wert der settlementType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SettlementTypeCodeType }
     *     
     */
    public void setSettlementType(SettlementTypeCodeType value) {
        this.settlementType = value;
    }

    /**
     * Ruft den Wert der allowanceChargeAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getAllowanceChargeAmount() {
        return allowanceChargeAmount;
    }

    /**
     * Legt den Wert der allowanceChargeAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setAllowanceChargeAmount(AmountType value) {
        this.allowanceChargeAmount = value;
    }

    /**
     * Ruft den Wert der allowanceChargePercentage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getAllowanceChargePercentage() {
        return allowanceChargePercentage;
    }

    /**
     * Legt den Wert der allowanceChargePercentage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setAllowanceChargePercentage(Float value) {
        this.allowanceChargePercentage = value;
    }

    /**
     * Ruft den Wert der amountPerUnit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getAmountPerUnit() {
        return amountPerUnit;
    }

    /**
     * Legt den Wert der amountPerUnit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setAmountPerUnit(AmountType value) {
        this.amountPerUnit = value;
    }

    /**
     * Ruft den Wert der baseAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getBaseAmount() {
        return baseAmount;
    }

    /**
     * Legt den Wert der baseAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setBaseAmount(AmountType value) {
        this.baseAmount = value;
    }

    /**
     * Ruft den Wert der baseNumberOfUnits-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getBaseNumberOfUnits() {
        return baseNumberOfUnits;
    }

    /**
     * Legt den Wert der baseNumberOfUnits-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setBaseNumberOfUnits(MeasurementType value) {
        this.baseNumberOfUnits = value;
    }

    /**
     * Ruft den Wert der bracketIdentifier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBracketIdentifier() {
        return bracketIdentifier;
    }

    /**
     * Legt den Wert der bracketIdentifier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBracketIdentifier(String value) {
        this.bracketIdentifier = value;
    }

    /**
     * Ruft den Wert der effectiveDateType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EffectiveDateTypeCodeType }
     *     
     */
    public EffectiveDateTypeCodeType getEffectiveDateType() {
        return effectiveDateType;
    }

    /**
     * Legt den Wert der effectiveDateType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EffectiveDateTypeCodeType }
     *     
     */
    public void setEffectiveDateType(EffectiveDateTypeCodeType value) {
        this.effectiveDateType = value;
    }

    /**
     * Ruft den Wert der sequenceNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Legt den Wert der sequenceNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSequenceNumber(BigInteger value) {
        this.sequenceNumber = value;
    }

    /**
     * Ruft den Wert der specialServiceType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SpecialServiceTypeCodeType }
     *     
     */
    public SpecialServiceTypeCodeType getSpecialServiceType() {
        return specialServiceType;
    }

    /**
     * Legt den Wert der specialServiceType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SpecialServiceTypeCodeType }
     *     
     */
    public void setSpecialServiceType(SpecialServiceTypeCodeType value) {
        this.specialServiceType = value;
    }

    /**
     * Ruft den Wert der allowanceChargeDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MultiDescription70Type }
     *     
     */
    public MultiDescription70Type getAllowanceChargeDescription() {
        return allowanceChargeDescription;
    }

    /**
     * Legt den Wert der allowanceChargeDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiDescription70Type }
     *     
     */
    public void setAllowanceChargeDescription(MultiDescription70Type value) {
        this.allowanceChargeDescription = value;
    }

}
