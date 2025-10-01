//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für payloadType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="payloadType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="credit" type="{http://www.forum-datenaustausch.ch/invoice}creditType" minOccurs="0"/&gt;
 *         &lt;element name="invoice" type="{http://www.forum-datenaustausch.ch/invoice}invoiceType"/&gt;
 *         &lt;element name="reminder" type="{http://www.forum-datenaustausch.ch/invoice}reminderType" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="body" type="{http://www.forum-datenaustausch.ch/invoice}bodyType"/&gt;
 *           &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}EncryptedData"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="request_type" default="invoice"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="invoice"/&gt;
 *             &lt;enumeration value="reminder"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="request_subtype" default="normal"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="normal"/&gt;
 *             &lt;enumeration value="copy"/&gt;
 *             &lt;enumeration value="refund"/&gt;
 *             &lt;enumeration value="storno"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payloadType", propOrder = {
    "credit",
    "invoice",
    "reminder",
    "body",
    "encryptedData"
})
public class PayloadType {

    protected CreditType credit;
    @XmlElement(required = true)
    protected InvoiceType invoice;
    protected ReminderType reminder;
    protected BodyType body;
    @XmlElement(name = "EncryptedData", namespace = "http://www.w3.org/2001/04/xmlenc#")
    protected EncryptedDataType encryptedData;
    @XmlAttribute(name = "request_type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String requestType;
    @XmlAttribute(name = "request_subtype")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String requestSubtype;

    /**
     * Ruft den Wert der credit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CreditType }
     *     
     */
    public CreditType getCredit() {
        return credit;
    }

    /**
     * Legt den Wert der credit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CreditType }
     *     
     */
    public void setCredit(CreditType value) {
        this.credit = value;
    }

    /**
     * Ruft den Wert der invoice-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceType }
     *     
     */
    public InvoiceType getInvoice() {
        return invoice;
    }

    /**
     * Legt den Wert der invoice-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceType }
     *     
     */
    public void setInvoice(InvoiceType value) {
        this.invoice = value;
    }

    /**
     * Ruft den Wert der reminder-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReminderType }
     *     
     */
    public ReminderType getReminder() {
        return reminder;
    }

    /**
     * Legt den Wert der reminder-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReminderType }
     *     
     */
    public void setReminder(ReminderType value) {
        this.reminder = value;
    }

    /**
     * Ruft den Wert der body-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BodyType }
     *     
     */
    public BodyType getBody() {
        return body;
    }

    /**
     * Legt den Wert der body-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BodyType }
     *     
     */
    public void setBody(BodyType value) {
        this.body = value;
    }

    /**
     * Ruft den Wert der encryptedData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EncryptedDataType }
     *     
     */
    public EncryptedDataType getEncryptedData() {
        return encryptedData;
    }

    /**
     * Legt den Wert der encryptedData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EncryptedDataType }
     *     
     */
    public void setEncryptedData(EncryptedDataType value) {
        this.encryptedData = value;
    }

    /**
     * Ruft den Wert der requestType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestType() {
        if (requestType == null) {
            return "invoice";
        } else {
            return requestType;
        }
    }

    /**
     * Legt den Wert der requestType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestType(String value) {
        this.requestType = value;
    }

    /**
     * Ruft den Wert der requestSubtype-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestSubtype() {
        if (requestSubtype == null) {
            return "normal";
        } else {
            return requestSubtype;
        }
    }

    /**
     * Legt den Wert der requestSubtype-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestSubtype(String value) {
        this.requestSubtype = value;
    }

}
