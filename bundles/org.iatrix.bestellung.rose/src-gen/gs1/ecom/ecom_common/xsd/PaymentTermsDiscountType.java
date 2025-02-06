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

import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.MultiDescription70Type;
import gs1.shared.shared_common.xsd.PaymentTimePeriodType;


/**
 * <p>Java-Klasse für PaymentTermsDiscountType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentTermsDiscountType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="discountType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="discountAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="discountPercent" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/&gt;
 *         &lt;element name="paymentTimePeriod" type="{urn:gs1:shared:shared_common:xsd:3}PaymentTimePeriodType"/&gt;
 *         &lt;element name="discountDescription" type="{urn:gs1:shared:shared_common:xsd:3}MultiDescription70Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTermsDiscountType", propOrder = {
    "discountType",
    "discountAmount",
    "discountPercent",
    "paymentTimePeriod",
    "discountDescription"
})
public class PaymentTermsDiscountType {

    @XmlElement(required = true)
    protected String discountType;
    protected AmountType discountAmount;
    protected Float discountPercent;
    @XmlElement(required = true)
    protected PaymentTimePeriodType paymentTimePeriod;
    protected MultiDescription70Type discountDescription;

    /**
     * Ruft den Wert der discountType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscountType() {
        return discountType;
    }

    /**
     * Legt den Wert der discountType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscountType(String value) {
        this.discountType = value;
    }

    /**
     * Ruft den Wert der discountAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getDiscountAmount() {
        return discountAmount;
    }

    /**
     * Legt den Wert der discountAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setDiscountAmount(AmountType value) {
        this.discountAmount = value;
    }

    /**
     * Ruft den Wert der discountPercent-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDiscountPercent() {
        return discountPercent;
    }

    /**
     * Legt den Wert der discountPercent-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDiscountPercent(Float value) {
        this.discountPercent = value;
    }

    /**
     * Ruft den Wert der paymentTimePeriod-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTimePeriodType }
     *     
     */
    public PaymentTimePeriodType getPaymentTimePeriod() {
        return paymentTimePeriod;
    }

    /**
     * Legt den Wert der paymentTimePeriod-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTimePeriodType }
     *     
     */
    public void setPaymentTimePeriod(PaymentTimePeriodType value) {
        this.paymentTimePeriod = value;
    }

    /**
     * Ruft den Wert der discountDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MultiDescription70Type }
     *     
     */
    public MultiDescription70Type getDiscountDescription() {
        return discountDescription;
    }

    /**
     * Legt den Wert der discountDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiDescription70Type }
     *     
     */
    public void setDiscountDescription(MultiDescription70Type value) {
        this.discountDescription = value;
    }

}
