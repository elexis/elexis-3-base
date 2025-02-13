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

import gs1.shared.shared_common.xsd.DayOfTheWeekEnumerationType;


/**
 * <p>Java-Klasse für OperatingHoursType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OperatingHoursType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dayOfTheWeekCode" type="{urn:gs1:shared:shared_common:xsd:3}DayOfTheWeekEnumerationType"/&gt;
 *         &lt;element name="isOperational" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="closingTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="openingTime" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OperatingHoursType", propOrder = {
    "dayOfTheWeekCode",
    "isOperational",
    "closingTime",
    "openingTime"
})
public class OperatingHoursType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected DayOfTheWeekEnumerationType dayOfTheWeekCode;
    protected boolean isOperational;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar closingTime;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar openingTime;

    /**
     * Ruft den Wert der dayOfTheWeekCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DayOfTheWeekEnumerationType }
     *     
     */
    public DayOfTheWeekEnumerationType getDayOfTheWeekCode() {
        return dayOfTheWeekCode;
    }

    /**
     * Legt den Wert der dayOfTheWeekCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DayOfTheWeekEnumerationType }
     *     
     */
    public void setDayOfTheWeekCode(DayOfTheWeekEnumerationType value) {
        this.dayOfTheWeekCode = value;
    }

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

}
