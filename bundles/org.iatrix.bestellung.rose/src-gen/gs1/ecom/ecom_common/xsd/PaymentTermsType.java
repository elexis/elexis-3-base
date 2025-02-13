//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.PaymentTermsTypeCodeType;
import gs1.shared.shared_common.xsd.PaymentTimePeriodType;


/**
 * <p>Java-Klasse für PaymentTermsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PaymentTermsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="paymentTermsEventCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PaymentTermsEventCodeType"/&gt;
 *         &lt;element name="paymentTermsTypeCode" type="{urn:gs1:shared:shared_common:xsd:3}PaymentTermsTypeCodeType"/&gt;
 *         &lt;element name="proximoCutOffDay" type="{http://www.w3.org/2001/XMLSchema}gDay" minOccurs="0"/&gt;
 *         &lt;element name="netPaymentDue" type="{urn:gs1:shared:shared_common:xsd:3}PaymentTimePeriodType" minOccurs="0"/&gt;
 *         &lt;element name="installmentDue" type="{urn:gs1:ecom:ecom_common:xsd:3}InstallmentDueType" minOccurs="0"/&gt;
 *         &lt;element name="paymentTermsDiscount" type="{urn:gs1:ecom:ecom_common:xsd:3}PaymentTermsDiscountType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="paymentMethod" type="{urn:gs1:ecom:ecom_common:xsd:3}PaymentMethodType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="sEPAReference" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalGenericReferenceType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentTermsType", propOrder = {
    "paymentTermsEventCode",
    "paymentTermsTypeCode",
    "proximoCutOffDay",
    "netPaymentDue",
    "installmentDue",
    "paymentTermsDiscount",
    "paymentMethod",
    "sepaReference"
})
public class PaymentTermsType {

    @XmlElement(required = true)
    protected PaymentTermsEventCodeType paymentTermsEventCode;
    @XmlElement(required = true)
    protected PaymentTermsTypeCodeType paymentTermsTypeCode;
    @XmlSchemaType(name = "gDay")
    protected XMLGregorianCalendar proximoCutOffDay;
    protected PaymentTimePeriodType netPaymentDue;
    protected InstallmentDueType installmentDue;
    protected List<PaymentTermsDiscountType> paymentTermsDiscount;
    protected List<PaymentMethodType> paymentMethod;
    @XmlElement(name = "sEPAReference")
    protected List<TransactionalGenericReferenceType> sepaReference;

    /**
     * Ruft den Wert der paymentTermsEventCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTermsEventCodeType }
     *     
     */
    public PaymentTermsEventCodeType getPaymentTermsEventCode() {
        return paymentTermsEventCode;
    }

    /**
     * Legt den Wert der paymentTermsEventCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTermsEventCodeType }
     *     
     */
    public void setPaymentTermsEventCode(PaymentTermsEventCodeType value) {
        this.paymentTermsEventCode = value;
    }

    /**
     * Ruft den Wert der paymentTermsTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTermsTypeCodeType }
     *     
     */
    public PaymentTermsTypeCodeType getPaymentTermsTypeCode() {
        return paymentTermsTypeCode;
    }

    /**
     * Legt den Wert der paymentTermsTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTermsTypeCodeType }
     *     
     */
    public void setPaymentTermsTypeCode(PaymentTermsTypeCodeType value) {
        this.paymentTermsTypeCode = value;
    }

    /**
     * Ruft den Wert der proximoCutOffDay-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getProximoCutOffDay() {
        return proximoCutOffDay;
    }

    /**
     * Legt den Wert der proximoCutOffDay-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setProximoCutOffDay(XMLGregorianCalendar value) {
        this.proximoCutOffDay = value;
    }

    /**
     * Ruft den Wert der netPaymentDue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentTimePeriodType }
     *     
     */
    public PaymentTimePeriodType getNetPaymentDue() {
        return netPaymentDue;
    }

    /**
     * Legt den Wert der netPaymentDue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentTimePeriodType }
     *     
     */
    public void setNetPaymentDue(PaymentTimePeriodType value) {
        this.netPaymentDue = value;
    }

    /**
     * Ruft den Wert der installmentDue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InstallmentDueType }
     *     
     */
    public InstallmentDueType getInstallmentDue() {
        return installmentDue;
    }

    /**
     * Legt den Wert der installmentDue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InstallmentDueType }
     *     
     */
    public void setInstallmentDue(InstallmentDueType value) {
        this.installmentDue = value;
    }

    /**
     * Gets the value of the paymentTermsDiscount property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the paymentTermsDiscount property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentTermsDiscount().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentTermsDiscountType }
     * 
     * 
     */
    public List<PaymentTermsDiscountType> getPaymentTermsDiscount() {
        if (paymentTermsDiscount == null) {
            paymentTermsDiscount = new ArrayList<PaymentTermsDiscountType>();
        }
        return this.paymentTermsDiscount;
    }

    /**
     * Gets the value of the paymentMethod property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the paymentMethod property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentMethod().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentMethodType }
     * 
     * 
     */
    public List<PaymentMethodType> getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = new ArrayList<PaymentMethodType>();
        }
        return this.paymentMethod;
    }

    /**
     * Gets the value of the sepaReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the sepaReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSEPAReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionalGenericReferenceType }
     * 
     * 
     */
    public List<TransactionalGenericReferenceType> getSEPAReference() {
        if (sepaReference == null) {
            sepaReference = new ArrayList<TransactionalGenericReferenceType>();
        }
        return this.sepaReference;
    }

}
