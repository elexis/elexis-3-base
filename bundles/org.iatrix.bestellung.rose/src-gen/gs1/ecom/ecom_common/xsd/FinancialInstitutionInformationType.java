//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.AddressType;
import gs1.shared.shared_common.xsd.FinancialAccountType;
import gs1.shared.shared_common.xsd.FinancialRoutingNumberType;
import gs1.shared.shared_common.xsd.MultiDescription70Type;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FinancialInstitutionInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="FinancialInstitutionInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="financialInstitutionName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="financialInstitutionBranchName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="financialAccount" type="{urn:gs1:shared:shared_common:xsd:3}FinancialAccountType" minOccurs="0"/&gt;
 *         &lt;element name="financialRoutingNumber" type="{urn:gs1:shared:shared_common:xsd:3}FinancialRoutingNumberType" minOccurs="0"/&gt;
 *         &lt;element name="additionalFinancialInformation" type="{urn:gs1:shared:shared_common:xsd:3}MultiDescription70Type" minOccurs="0"/&gt;
 *         &lt;element name="address" type="{urn:gs1:shared:shared_common:xsd:3}AddressType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialInstitutionInformationType", propOrder = {
    "financialInstitutionName",
    "financialInstitutionBranchName",
    "financialAccount",
    "financialRoutingNumber",
    "additionalFinancialInformation",
    "address"
})
public class FinancialInstitutionInformationType {

    protected String financialInstitutionName;
    protected String financialInstitutionBranchName;
    protected FinancialAccountType financialAccount;
    protected FinancialRoutingNumberType financialRoutingNumber;
    protected MultiDescription70Type additionalFinancialInformation;
    protected AddressType address;

    /**
     * Ruft den Wert der financialInstitutionName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialInstitutionName() {
        return financialInstitutionName;
    }

    /**
     * Legt den Wert der financialInstitutionName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialInstitutionName(String value) {
        this.financialInstitutionName = value;
    }

    /**
     * Ruft den Wert der financialInstitutionBranchName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialInstitutionBranchName() {
        return financialInstitutionBranchName;
    }

    /**
     * Legt den Wert der financialInstitutionBranchName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialInstitutionBranchName(String value) {
        this.financialInstitutionBranchName = value;
    }

    /**
     * Ruft den Wert der financialAccount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialAccountType }
     *     
     */
    public FinancialAccountType getFinancialAccount() {
        return financialAccount;
    }

    /**
     * Legt den Wert der financialAccount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialAccountType }
     *     
     */
    public void setFinancialAccount(FinancialAccountType value) {
        this.financialAccount = value;
    }

    /**
     * Ruft den Wert der financialRoutingNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialRoutingNumberType }
     *     
     */
    public FinancialRoutingNumberType getFinancialRoutingNumber() {
        return financialRoutingNumber;
    }

    /**
     * Legt den Wert der financialRoutingNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialRoutingNumberType }
     *     
     */
    public void setFinancialRoutingNumber(FinancialRoutingNumberType value) {
        this.financialRoutingNumber = value;
    }

    /**
     * Ruft den Wert der additionalFinancialInformation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MultiDescription70Type }
     *     
     */
    public MultiDescription70Type getAdditionalFinancialInformation() {
        return additionalFinancialInformation;
    }

    /**
     * Legt den Wert der additionalFinancialInformation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiDescription70Type }
     *     
     */
    public void setAdditionalFinancialInformation(MultiDescription70Type value) {
        this.additionalFinancialInformation = value;
    }

    /**
     * Ruft den Wert der address-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AddressType }
     *     
     */
    public AddressType getAddress() {
        return address;
    }

    /**
     * Legt den Wert der address-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AddressType }
     *     
     */
    public void setAddress(AddressType value) {
        this.address = value;
    }

}
