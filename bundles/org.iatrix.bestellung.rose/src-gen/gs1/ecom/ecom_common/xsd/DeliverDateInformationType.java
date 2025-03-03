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
 * <p>Java-Klasse für DeliverDateInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DeliverDateInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="deliverDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="deliverDateType" type="{urn:gs1:ecom:ecom_common:xsd:3}DeliverDateTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeliverDateInformationType", propOrder = {
    "deliverDate",
    "deliverDateType"
})
public class DeliverDateInformationType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar deliverDate;
    @XmlElement(required = true)
    protected DeliverDateTypeCodeType deliverDateType;

    /**
     * Ruft den Wert der deliverDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDeliverDate() {
        return deliverDate;
    }

    /**
     * Legt den Wert der deliverDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDeliverDate(XMLGregorianCalendar value) {
        this.deliverDate = value;
    }

    /**
     * Ruft den Wert der deliverDateType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DeliverDateTypeCodeType }
     *     
     */
    public DeliverDateTypeCodeType getDeliverDateType() {
        return deliverDateType;
    }

    /**
     * Legt den Wert der deliverDateType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliverDateTypeCodeType }
     *     
     */
    public void setDeliverDateType(DeliverDateTypeCodeType value) {
        this.deliverDateType = value;
    }

}
