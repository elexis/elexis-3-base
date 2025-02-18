//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.CurrencyCodeType;
import gs1.shared.shared_common.xsd.Description80Type;
import gs1.shared.shared_common.xsd.ExtensionType;
import gs1.shared.shared_common.xsd.TaxCategoryCodeType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für LeviedDutyFeeTaxType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LeviedDutyFeeTaxType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dutyFeeTaxAccountingCurrency" type="{urn:gs1:shared:shared_common:xsd:3}CurrencyCodeType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxAgencyName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dutyFeeTaxAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxAmountInAccountingCurrency" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxBasisAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxBasisAmountInAccountingCurrency" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxCategoryCode" type="{urn:gs1:shared:shared_common:xsd:3}TaxCategoryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxExemptionDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxExemptionReason" type="{urn:gs1:ecom:ecom_common:xsd:3}DutyFeeTaxExemptionReasonCodeType" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxPercentage" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxPointDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}DutyFeeTaxTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="extension" type="{urn:gs1:shared:shared_common:xsd:3}ExtensionType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LeviedDutyFeeTaxType", propOrder = {
    "dutyFeeTaxAccountingCurrency",
    "dutyFeeTaxAgencyName",
    "dutyFeeTaxAmount",
    "dutyFeeTaxAmountInAccountingCurrency",
    "dutyFeeTaxBasisAmount",
    "dutyFeeTaxBasisAmountInAccountingCurrency",
    "dutyFeeTaxCategoryCode",
    "dutyFeeTaxDescription",
    "dutyFeeTaxExemptionDescription",
    "dutyFeeTaxExemptionReason",
    "dutyFeeTaxPercentage",
    "dutyFeeTaxPointDate",
    "dutyFeeTaxTypeCode",
    "extension"
})
public class LeviedDutyFeeTaxType {

    protected CurrencyCodeType dutyFeeTaxAccountingCurrency;
    protected String dutyFeeTaxAgencyName;
    protected AmountType dutyFeeTaxAmount;
    protected AmountType dutyFeeTaxAmountInAccountingCurrency;
    protected AmountType dutyFeeTaxBasisAmount;
    protected AmountType dutyFeeTaxBasisAmountInAccountingCurrency;
    protected TaxCategoryCodeType dutyFeeTaxCategoryCode;
    protected Description80Type dutyFeeTaxDescription;
    protected Description80Type dutyFeeTaxExemptionDescription;
    protected DutyFeeTaxExemptionReasonCodeType dutyFeeTaxExemptionReason;
    protected Float dutyFeeTaxPercentage;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dutyFeeTaxPointDate;
    protected DutyFeeTaxTypeCodeType dutyFeeTaxTypeCode;
    protected ExtensionType extension;

    /**
     * Ruft den Wert der dutyFeeTaxAccountingCurrency-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *     
     */
    public CurrencyCodeType getDutyFeeTaxAccountingCurrency() {
        return dutyFeeTaxAccountingCurrency;
    }

    /**
     * Legt den Wert der dutyFeeTaxAccountingCurrency-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *     
     */
    public void setDutyFeeTaxAccountingCurrency(CurrencyCodeType value) {
        this.dutyFeeTaxAccountingCurrency = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxAgencyName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDutyFeeTaxAgencyName() {
        return dutyFeeTaxAgencyName;
    }

    /**
     * Legt den Wert der dutyFeeTaxAgencyName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDutyFeeTaxAgencyName(String value) {
        this.dutyFeeTaxAgencyName = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDutyFeeTaxAmount() {
        return dutyFeeTaxAmount;
    }

    /**
     * Legt den Wert der dutyFeeTaxAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDutyFeeTaxAmount(AmountType value) {
        this.dutyFeeTaxAmount = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxAmountInAccountingCurrency-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDutyFeeTaxAmountInAccountingCurrency() {
        return dutyFeeTaxAmountInAccountingCurrency;
    }

    /**
     * Legt den Wert der dutyFeeTaxAmountInAccountingCurrency-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDutyFeeTaxAmountInAccountingCurrency(AmountType value) {
        this.dutyFeeTaxAmountInAccountingCurrency = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxBasisAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDutyFeeTaxBasisAmount() {
        return dutyFeeTaxBasisAmount;
    }

    /**
     * Legt den Wert der dutyFeeTaxBasisAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDutyFeeTaxBasisAmount(AmountType value) {
        this.dutyFeeTaxBasisAmount = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxBasisAmountInAccountingCurrency-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDutyFeeTaxBasisAmountInAccountingCurrency() {
        return dutyFeeTaxBasisAmountInAccountingCurrency;
    }

    /**
     * Legt den Wert der dutyFeeTaxBasisAmountInAccountingCurrency-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDutyFeeTaxBasisAmountInAccountingCurrency(AmountType value) {
        this.dutyFeeTaxBasisAmountInAccountingCurrency = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxCategoryCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TaxCategoryCodeType }
     *     
     */
    public TaxCategoryCodeType getDutyFeeTaxCategoryCode() {
        return dutyFeeTaxCategoryCode;
    }

    /**
     * Legt den Wert der dutyFeeTaxCategoryCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TaxCategoryCodeType }
     *     
     */
    public void setDutyFeeTaxCategoryCode(TaxCategoryCodeType value) {
        this.dutyFeeTaxCategoryCode = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getDutyFeeTaxDescription() {
        return dutyFeeTaxDescription;
    }

    /**
     * Legt den Wert der dutyFeeTaxDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setDutyFeeTaxDescription(Description80Type value) {
        this.dutyFeeTaxDescription = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxExemptionDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getDutyFeeTaxExemptionDescription() {
        return dutyFeeTaxExemptionDescription;
    }

    /**
     * Legt den Wert der dutyFeeTaxExemptionDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setDutyFeeTaxExemptionDescription(Description80Type value) {
        this.dutyFeeTaxExemptionDescription = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxExemptionReason-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DutyFeeTaxExemptionReasonCodeType }
     *     
     */
    public DutyFeeTaxExemptionReasonCodeType getDutyFeeTaxExemptionReason() {
        return dutyFeeTaxExemptionReason;
    }

    /**
     * Legt den Wert der dutyFeeTaxExemptionReason-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DutyFeeTaxExemptionReasonCodeType }
     *     
     */
    public void setDutyFeeTaxExemptionReason(DutyFeeTaxExemptionReasonCodeType value) {
        this.dutyFeeTaxExemptionReason = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxPercentage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDutyFeeTaxPercentage() {
        return dutyFeeTaxPercentage;
    }

    /**
     * Legt den Wert der dutyFeeTaxPercentage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDutyFeeTaxPercentage(Float value) {
        this.dutyFeeTaxPercentage = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxPointDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDutyFeeTaxPointDate() {
        return dutyFeeTaxPointDate;
    }

    /**
     * Legt den Wert der dutyFeeTaxPointDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDutyFeeTaxPointDate(XMLGregorianCalendar value) {
        this.dutyFeeTaxPointDate = value;
    }

    /**
     * Ruft den Wert der dutyFeeTaxTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DutyFeeTaxTypeCodeType }
     *     
     */
    public DutyFeeTaxTypeCodeType getDutyFeeTaxTypeCode() {
        return dutyFeeTaxTypeCode;
    }

    /**
     * Legt den Wert der dutyFeeTaxTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DutyFeeTaxTypeCodeType }
     *     
     */
    public void setDutyFeeTaxTypeCode(DutyFeeTaxTypeCodeType value) {
        this.dutyFeeTaxTypeCode = value;
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

}
