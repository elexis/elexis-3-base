
package ch.docbox.ws.cdachservicesv2;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hl7.v3.POCDMT000040PatientRole;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AppointmentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AppointmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="salesrepresentative-open"/>
 *               &lt;enumeration value="salesrepresentative-booked"/>
 *               &lt;enumeration value="salesrepresentative-openrequest"/>
 *               &lt;enumeration value="salesrepresentative-openinvitation"/>
 *               &lt;enumeration value="salesrepresentative-phone"/>
 *               &lt;enumeration value="emergencyservice"/>
 *               &lt;enumeration value="canceled"/>
 *               &lt;enumeration value="terminierung-booked"/>
 *               &lt;enumeration value="terminierung-open"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="reasonTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reasonDetails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visitor" type="{urn:hl7-org:v3}POCD_MT000040.PatientRole" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppointmentType", propOrder = {
    "date",
    "duration",
    "id",
    "state",
    "reasonTitle",
    "reasonDetails",
    "info",
    "visitor"
})
public class AppointmentType {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    protected Long duration;
    @XmlElement(required = true)
    protected String id;
    @XmlElement(required = true)
    protected String state;
    protected String reasonTitle;
    protected String reasonDetails;
    protected String info;
    protected POCDMT000040PatientRole visitor;

    /**
     * Ruft den Wert der date-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Legt den Wert der date-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Ruft den Wert der duration-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * Legt den Wert der duration-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setDuration(Long value) {
        this.duration = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der state-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Legt den Wert der state-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Ruft den Wert der reasonTitle-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonTitle() {
        return reasonTitle;
    }

    /**
     * Legt den Wert der reasonTitle-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonTitle(String value) {
        this.reasonTitle = value;
    }

    /**
     * Ruft den Wert der reasonDetails-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReasonDetails() {
        return reasonDetails;
    }

    /**
     * Legt den Wert der reasonDetails-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReasonDetails(String value) {
        this.reasonDetails = value;
    }

    /**
     * Ruft den Wert der info-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfo() {
        return info;
    }

    /**
     * Legt den Wert der info-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfo(String value) {
        this.info = value;
    }

    /**
     * Ruft den Wert der visitor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link POCDMT000040PatientRole }
     *     
     */
    public POCDMT000040PatientRole getVisitor() {
        return visitor;
    }

    /**
     * Legt den Wert der visitor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link POCDMT000040PatientRole }
     *     
     */
    public void setVisitor(POCDMT000040PatientRole value) {
        this.visitor = value;
    }

}
