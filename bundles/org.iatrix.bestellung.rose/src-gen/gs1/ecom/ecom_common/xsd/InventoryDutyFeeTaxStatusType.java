//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import gs1.shared.shared_common.xsd.CodeType;
import gs1.shared.shared_common.xsd.Description80Type;
import gs1.shared.shared_common.xsd.TaxCategoryCodeType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für InventoryDutyFeeTaxStatusType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="InventoryDutyFeeTaxStatusType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dutyFeeTaxTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}DutyFeeTaxTypeCodeType"/&gt;
 *         &lt;element name="dutyFeeTaxAgencyName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dutyFeeTaxDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="dutyFeeTaxCategoryCode" type="{urn:gs1:shared:shared_common:xsd:3}TaxCategoryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="customsWarehouseStatusCode" type="{urn:gs1:ecom:ecom_common:xsd:3}CustomsWarehouseStatusCodeType" minOccurs="0"/&gt;
 *         &lt;element name="customsLicenseCode" type="{urn:gs1:shared:shared_common:xsd:3}CodeType" minOccurs="0"/&gt;
 *         &lt;element name="transactionalReference" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalReferenceType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryDutyFeeTaxStatusType", propOrder = {
    "dutyFeeTaxTypeCode",
    "dutyFeeTaxAgencyName",
    "dutyFeeTaxDescription",
    "dutyFeeTaxCategoryCode",
    "customsWarehouseStatusCode",
    "customsLicenseCode",
    "transactionalReference"
})
public class InventoryDutyFeeTaxStatusType {

    @XmlElement(required = true)
    protected DutyFeeTaxTypeCodeType dutyFeeTaxTypeCode;
    protected String dutyFeeTaxAgencyName;
    protected Description80Type dutyFeeTaxDescription;
    protected TaxCategoryCodeType dutyFeeTaxCategoryCode;
    protected CustomsWarehouseStatusCodeType customsWarehouseStatusCode;
    protected CodeType customsLicenseCode;
    protected List<TransactionalReferenceType> transactionalReference;

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
     * Ruft den Wert der customsWarehouseStatusCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CustomsWarehouseStatusCodeType }
     *     
     */
    public CustomsWarehouseStatusCodeType getCustomsWarehouseStatusCode() {
        return customsWarehouseStatusCode;
    }

    /**
     * Legt den Wert der customsWarehouseStatusCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomsWarehouseStatusCodeType }
     *     
     */
    public void setCustomsWarehouseStatusCode(CustomsWarehouseStatusCodeType value) {
        this.customsWarehouseStatusCode = value;
    }

    /**
     * Ruft den Wert der customsLicenseCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getCustomsLicenseCode() {
        return customsLicenseCode;
    }

    /**
     * Legt den Wert der customsLicenseCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setCustomsLicenseCode(CodeType value) {
        this.customsLicenseCode = value;
    }

    /**
     * Gets the value of the transactionalReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionalReferenceType }
     * 
     * 
     */
    public List<TransactionalReferenceType> getTransactionalReference() {
        if (transactionalReference == null) {
            transactionalReference = new ArrayList<TransactionalReferenceType>();
        }
        return this.transactionalReference;
    }

}
