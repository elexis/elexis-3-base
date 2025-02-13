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
 * <p>Java-Klasse für FinancialRoutingNumberType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="FinancialRoutingNumberType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="financialRoutingNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="financialRoutingNumberTypeCode" type="{urn:gs1:shared:shared_common:xsd:3}FinancialRoutingNumberTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FinancialRoutingNumberType", propOrder = {
    "financialRoutingNumber",
    "financialRoutingNumberTypeCode"
})
public class FinancialRoutingNumberType {

    @XmlElement(required = true)
    protected String financialRoutingNumber;
    @XmlElement(required = true)
    protected FinancialRoutingNumberTypeCodeType financialRoutingNumberTypeCode;

    /**
     * Ruft den Wert der financialRoutingNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinancialRoutingNumber() {
        return financialRoutingNumber;
    }

    /**
     * Legt den Wert der financialRoutingNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinancialRoutingNumber(String value) {
        this.financialRoutingNumber = value;
    }

    /**
     * Ruft den Wert der financialRoutingNumberTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link FinancialRoutingNumberTypeCodeType }
     *     
     */
    public FinancialRoutingNumberTypeCodeType getFinancialRoutingNumberTypeCode() {
        return financialRoutingNumberTypeCode;
    }

    /**
     * Legt den Wert der financialRoutingNumberTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link FinancialRoutingNumberTypeCodeType }
     *     
     */
    public void setFinancialRoutingNumberTypeCode(FinancialRoutingNumberTypeCodeType value) {
        this.financialRoutingNumberTypeCode = value;
    }

}
