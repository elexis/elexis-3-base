//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:13:04 PM CEST 
//


package ch.fd.invoice450.response;

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
 * &lt;complexType name="payloadType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="credit" type="{http://www.forum-datenaustausch.ch/invoice}creditType" minOccurs="0"/>
 *         &lt;element name="invoice" type="{http://www.forum-datenaustausch.ch/invoice}invoiceType"/>
 *         &lt;element name="reminder" type="{http://www.forum-datenaustausch.ch/invoice}reminderType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="body" type="{http://www.forum-datenaustausch.ch/invoice}bodyType"/>
 *           &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}EncryptedData"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="type" default="invoice">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="invoice"/>
 *             &lt;enumeration value="reminder"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="storno" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="copy" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="response_timestamp" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
 *             &lt;minInclusive value="1420066800"/>
 *             &lt;maxInclusive value="1924902000"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "storno")
    protected Boolean storno;
    @XmlAttribute(name = "copy")
    protected Boolean copy;
    @XmlAttribute(name = "response_timestamp", required = true)
    protected int responseTimestamp;

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
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "invoice";
        } else {
            return type;
        }
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der storno-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isStorno() {
        if (storno == null) {
            return false;
        } else {
            return storno;
        }
    }

    /**
     * Legt den Wert der storno-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setStorno(Boolean value) {
        this.storno = value;
    }

    /**
     * Ruft den Wert der copy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCopy() {
        if (copy == null) {
            return false;
        } else {
            return copy;
        }
    }

    /**
     * Legt den Wert der copy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCopy(Boolean value) {
        this.copy = value;
    }

    /**
     * Ruft den Wert der responseTimestamp-Eigenschaft ab.
     * 
     */
    public int getResponseTimestamp() {
        return responseTimestamp;
    }

    /**
     * Legt den Wert der responseTimestamp-Eigenschaft fest.
     * 
     */
    public void setResponseTimestamp(int value) {
        this.responseTimestamp = value;
    }

}
