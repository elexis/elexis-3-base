//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für FinancialAccountType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="FinancialAccountType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="financialAccountNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="financialAccountNumberTypeCode" type="{urn:gs1:shared:shared_common:xsd:3}FinancialAccountNumberTypeCodeType"/&gt;
 *         &lt;element name="financialAccountName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialAccountType", propOrder = {
    "financialAccountNumber",
    "financialAccountNumberTypeCode",
    "financialAccountName"
})
public class FinancialAccountType {

    @XmlElement(required = true)
    protected String financialAccountNumber;
    @XmlElement(required = true)
    protected FinancialAccountNumberTypeCodeType financialAccountNumberTypeCode;
    protected String financialAccountName;

    /**
     * Ruft den Wert der financialAccountNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialAccountNumber() {
        return financialAccountNumber;
    }

    /**
     * Legt den Wert der financialAccountNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialAccountNumber(String value) {
        this.financialAccountNumber = value;
    }

    /**
     * Ruft den Wert der financialAccountNumberTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialAccountNumberTypeCodeType }
     *     
     */
    public FinancialAccountNumberTypeCodeType getFinancialAccountNumberTypeCode() {
        return financialAccountNumberTypeCode;
    }

    /**
     * Legt den Wert der financialAccountNumberTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialAccountNumberTypeCodeType }
     *     
     */
    public void setFinancialAccountNumberTypeCode(FinancialAccountNumberTypeCodeType value) {
        this.financialAccountNumberTypeCode = value;
    }

    /**
     * Ruft den Wert der financialAccountName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialAccountName() {
        return financialAccountName;
    }

    /**
     * Legt den Wert der financialAccountName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialAccountName(String value) {
        this.financialAccountName = value;
    }

}
