//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:56:51 AM CEST 
//


package ch.fd.invoice500.response;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für treatmentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="treatmentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="date_begin" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="date_end" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="canton" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="AG"/&gt;
 *             &lt;enumeration value="AI"/&gt;
 *             &lt;enumeration value="AR"/&gt;
 *             &lt;enumeration value="BE"/&gt;
 *             &lt;enumeration value="BL"/&gt;
 *             &lt;enumeration value="BS"/&gt;
 *             &lt;enumeration value="FR"/&gt;
 *             &lt;enumeration value="GE"/&gt;
 *             &lt;enumeration value="GL"/&gt;
 *             &lt;enumeration value="GR"/&gt;
 *             &lt;enumeration value="JU"/&gt;
 *             &lt;enumeration value="LU"/&gt;
 *             &lt;enumeration value="NE"/&gt;
 *             &lt;enumeration value="NW"/&gt;
 *             &lt;enumeration value="OW"/&gt;
 *             &lt;enumeration value="SG"/&gt;
 *             &lt;enumeration value="SH"/&gt;
 *             &lt;enumeration value="SO"/&gt;
 *             &lt;enumeration value="SZ"/&gt;
 *             &lt;enumeration value="TI"/&gt;
 *             &lt;enumeration value="TG"/&gt;
 *             &lt;enumeration value="UR"/&gt;
 *             &lt;enumeration value="VD"/&gt;
 *             &lt;enumeration value="VS"/&gt;
 *             &lt;enumeration value="ZG"/&gt;
 *             &lt;enumeration value="ZH"/&gt;
 *             &lt;enumeration value="LI"/&gt;
 *             &lt;enumeration value="A"/&gt;
 *             &lt;enumeration value="D"/&gt;
 *             &lt;enumeration value="F"/&gt;
 *             &lt;enumeration value="I"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="treatment" default="ambulatory"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="ambulatory"/&gt;
 *             &lt;enumeration value="stationary"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="reason" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="disease"/&gt;
 *             &lt;enumeration value="accident"/&gt;
 *             &lt;enumeration value="maternity"/&gt;
 *             &lt;enumeration value="prevention"/&gt;
 *             &lt;enumeration value="birthdefect"/&gt;
 *             &lt;enumeration value="unknown"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="gestation_week13" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="end_of_birth" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="apid" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" /&gt;
 *       &lt;attribute name="acid" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "treatmentType")
public class TreatmentType {

    @XmlAttribute(name = "date_begin", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateBegin;
    @XmlAttribute(name = "date_end", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateEnd;
    @XmlAttribute(name = "canton", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String canton;
    @XmlAttribute(name = "treatment")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String treatment;
    @XmlAttribute(name = "reason", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String reason;
    @XmlAttribute(name = "gestation_week13")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar gestationWeek13;
    @XmlAttribute(name = "end_of_birth")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar endOfBirth;
    @XmlAttribute(name = "apid")
    protected String apid;
    @XmlAttribute(name = "acid")
    protected String acid;

    /**
     * Ruft den Wert der dateBegin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateBegin() {
        return dateBegin;
    }

    /**
     * Legt den Wert der dateBegin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateBegin(XMLGregorianCalendar value) {
        this.dateBegin = value;
    }

    /**
     * Ruft den Wert der dateEnd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateEnd() {
        return dateEnd;
    }

    /**
     * Legt den Wert der dateEnd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateEnd(XMLGregorianCalendar value) {
        this.dateEnd = value;
    }

    /**
     * Ruft den Wert der canton-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanton() {
        return canton;
    }

    /**
     * Legt den Wert der canton-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanton(String value) {
        this.canton = value;
    }

    /**
     * Ruft den Wert der treatment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTreatment() {
        if (treatment == null) {
            return "ambulatory";
        } else {
            return treatment;
        }
    }

    /**
     * Legt den Wert der treatment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTreatment(String value) {
        this.treatment = value;
    }

    /**
     * Ruft den Wert der reason-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReason() {
        return reason;
    }

    /**
     * Legt den Wert der reason-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReason(String value) {
        this.reason = value;
    }

    /**
     * Ruft den Wert der gestationWeek13-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getGestationWeek13() {
        return gestationWeek13;
    }

    /**
     * Legt den Wert der gestationWeek13-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setGestationWeek13(XMLGregorianCalendar value) {
        this.gestationWeek13 = value;
    }

    /**
     * Ruft den Wert der endOfBirth-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndOfBirth() {
        return endOfBirth;
    }

    /**
     * Legt den Wert der endOfBirth-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndOfBirth(XMLGregorianCalendar value) {
        this.endOfBirth = value;
    }

    /**
     * Ruft den Wert der apid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApid() {
        return apid;
    }

    /**
     * Legt den Wert der apid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApid(String value) {
        this.apid = value;
    }

    /**
     * Ruft den Wert der acid-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAcid() {
        return acid;
    }

    /**
     * Legt den Wert der acid-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAcid(String value) {
        this.acid = value;
    }

}
