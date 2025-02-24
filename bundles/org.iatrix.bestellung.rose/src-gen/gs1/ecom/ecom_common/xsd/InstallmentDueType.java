//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.PaymentTimePeriodType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für InstallmentDueType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="InstallmentDueType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="percentOfPaymentDue" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;element name="paymentTimePeriod" type="{urn:gs1:shared:shared_common:xsd:3}PaymentTimePeriodType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstallmentDueType", propOrder = {
    "percentOfPaymentDue",
    "paymentTimePeriod"
})
public class InstallmentDueType {

    protected float percentOfPaymentDue;
    @XmlElement(required = true)
    protected PaymentTimePeriodType paymentTimePeriod;

    /**
     * Ruft den Wert der percentOfPaymentDue-Eigenschaft ab.
     * 
     */
    public float getPercentOfPaymentDue() {
        return percentOfPaymentDue;
    }

    /**
     * Legt den Wert der percentOfPaymentDue-Eigenschaft fest.
     * 
     */
    public void setPercentOfPaymentDue(float value) {
        this.percentOfPaymentDue = value;
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

}
