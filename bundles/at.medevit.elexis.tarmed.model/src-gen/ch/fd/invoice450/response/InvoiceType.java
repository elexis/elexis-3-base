//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:13:04 PM CEST 
//


package ch.fd.invoice450.response;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für invoiceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="invoiceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="request_timestamp" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}unsignedLong">
 *             &lt;minInclusive value="1420066800"/>
 *             &lt;maxInclusive value="1924902000"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="request_date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="request_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invoiceType")
public class InvoiceType {

    @XmlAttribute(name = "request_timestamp", required = true)
    protected int requestTimestamp;
    @XmlAttribute(name = "request_date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDate;
    @XmlAttribute(name = "request_id", required = true)
    protected String requestId;

    /**
     * Ruft den Wert der requestTimestamp-Eigenschaft ab.
     * 
     */
    public int getRequestTimestamp() {
        return requestTimestamp;
    }

    /**
     * Legt den Wert der requestTimestamp-Eigenschaft fest.
     * 
     */
    public void setRequestTimestamp(int value) {
        this.requestTimestamp = value;
    }

    /**
     * Ruft den Wert der requestDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDate() {
        return requestDate;
    }

    /**
     * Legt den Wert der requestDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDate(XMLGregorianCalendar value) {
        this.requestDate = value;
    }

    /**
     * Ruft den Wert der requestId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Legt den Wert der requestId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

}
