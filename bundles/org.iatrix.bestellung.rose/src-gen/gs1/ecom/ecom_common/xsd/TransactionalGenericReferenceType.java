//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransactionalGenericReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalGenericReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transactionalReferenceTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalReferenceTypeCodeType"/&gt;
 *         &lt;element name="transactionalReferenceValue"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="transactionalReferenceDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalGenericReferenceType", propOrder = {
    "transactionalReferenceTypeCode",
    "transactionalReferenceValue",
    "transactionalReferenceDateTime"
})
public class TransactionalGenericReferenceType {

    @XmlElement(required = true)
    protected TransactionalReferenceTypeCodeType transactionalReferenceTypeCode;
    @XmlElement(required = true)
    protected String transactionalReferenceValue;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar transactionalReferenceDateTime;

    /**
     * Ruft den Wert der transactionalReferenceTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalReferenceTypeCodeType }
     *     
     */
    public TransactionalReferenceTypeCodeType getTransactionalReferenceTypeCode() {
        return transactionalReferenceTypeCode;
    }

    /**
     * Legt den Wert der transactionalReferenceTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalReferenceTypeCodeType }
     *     
     */
    public void setTransactionalReferenceTypeCode(TransactionalReferenceTypeCodeType value) {
        this.transactionalReferenceTypeCode = value;
    }

    /**
     * Ruft den Wert der transactionalReferenceValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionalReferenceValue() {
        return transactionalReferenceValue;
    }

    /**
     * Legt den Wert der transactionalReferenceValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionalReferenceValue(String value) {
        this.transactionalReferenceValue = value;
    }

    /**
     * Ruft den Wert der transactionalReferenceDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionalReferenceDateTime() {
        return transactionalReferenceDateTime;
    }

    /**
     * Legt den Wert der transactionalReferenceDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransactionalReferenceDateTime(XMLGregorianCalendar value) {
        this.transactionalReferenceDateTime = value;
    }

}
