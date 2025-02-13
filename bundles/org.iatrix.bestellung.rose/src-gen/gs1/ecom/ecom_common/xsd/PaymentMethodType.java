//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.PaymentMethodCodeType;


/**
 * <p>Java-Klasse für PaymentMethodType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentMethodType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paymentMethodCode" type="{urn:gs1:shared:shared_common:xsd:3}PaymentMethodCodeType"/&gt;
 *         &lt;element name="paymentMethodIdentification" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="automatedClearingHousePaymentFormat" type="{urn:gs1:ecom:ecom_common:xsd:3}PaymentFormatCodeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentMethodType", propOrder = {
    "paymentMethodCode",
    "paymentMethodIdentification",
    "automatedClearingHousePaymentFormat"
})
public class PaymentMethodType {

    @XmlElement(required = true)
    protected PaymentMethodCodeType paymentMethodCode;
    protected String paymentMethodIdentification;
    protected PaymentFormatCodeType automatedClearingHousePaymentFormat;

    /**
     * Ruft den Wert der paymentMethodCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentMethodCodeType }
     *     
     */
    public PaymentMethodCodeType getPaymentMethodCode() {
        return paymentMethodCode;
    }

    /**
     * Legt den Wert der paymentMethodCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentMethodCodeType }
     *     
     */
    public void setPaymentMethodCode(PaymentMethodCodeType value) {
        this.paymentMethodCode = value;
    }

    /**
     * Ruft den Wert der paymentMethodIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentMethodIdentification() {
        return paymentMethodIdentification;
    }

    /**
     * Legt den Wert der paymentMethodIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentMethodIdentification(String value) {
        this.paymentMethodIdentification = value;
    }

    /**
     * Ruft den Wert der automatedClearingHousePaymentFormat-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentFormatCodeType }
     *     
     */
    public PaymentFormatCodeType getAutomatedClearingHousePaymentFormat() {
        return automatedClearingHousePaymentFormat;
    }

    /**
     * Legt den Wert der automatedClearingHousePaymentFormat-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentFormatCodeType }
     *     
     */
    public void setAutomatedClearingHousePaymentFormat(PaymentFormatCodeType value) {
        this.automatedClearingHousePaymentFormat = value;
    }

}
