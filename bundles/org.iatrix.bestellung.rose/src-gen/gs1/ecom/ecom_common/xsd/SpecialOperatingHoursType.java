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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import gs1.shared.shared_common.xsd.Description80Type;


/**
 * <p>Java-Klasse für SpecialOperatingHoursType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SpecialOperatingHoursType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="isOperational" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="specialDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="closingTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="openingTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="specialDateName" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SpecialOperatingHoursType", propOrder = {
    "isOperational",
    "specialDate",
    "closingTime",
    "openingTime",
    "specialDateName"
})
public class SpecialOperatingHoursType {

    protected boolean isOperational;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar specialDate;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar closingTime;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar openingTime;
    protected Description80Type specialDateName;

    /**
     * Ruft den Wert der isOperational-Eigenschaft ab.
     * 
     */
    public boolean isIsOperational() {
        return isOperational;
    }

    /**
     * Legt den Wert der isOperational-Eigenschaft fest.
     * 
     */
    public void setIsOperational(boolean value) {
        this.isOperational = value;
    }

    /**
     * Ruft den Wert der specialDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getSpecialDate() {
        return specialDate;
    }

    /**
     * Legt den Wert der specialDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setSpecialDate(XMLGregorianCalendar value) {
        this.specialDate = value;
    }

    /**
     * Ruft den Wert der closingTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getClosingTime() {
        return closingTime;
    }

    /**
     * Legt den Wert der closingTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setClosingTime(XMLGregorianCalendar value) {
        this.closingTime = value;
    }

    /**
     * Ruft den Wert der openingTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getOpeningTime() {
        return openingTime;
    }

    /**
     * Legt den Wert der openingTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setOpeningTime(XMLGregorianCalendar value) {
        this.openingTime = value;
    }

    /**
     * Ruft den Wert der specialDateName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getSpecialDateName() {
        return specialDateName;
    }

    /**
     * Legt den Wert der specialDateName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setSpecialDateName(Description80Type value) {
        this.specialDateName = value;
    }

}
