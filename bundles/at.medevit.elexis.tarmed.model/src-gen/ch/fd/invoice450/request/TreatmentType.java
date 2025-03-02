//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für treatmentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="treatmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="diagnosis" type="{http://www.forum-datenaustausch.ch/invoice}diagnosisType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="xtra_hospital" type="{http://www.forum-datenaustausch.ch/invoice}xtraHospitalType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="date_begin" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="date_end" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="gestation_week13" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="canton" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="AG"/>
 *             &lt;enumeration value="AI"/>
 *             &lt;enumeration value="AR"/>
 *             &lt;enumeration value="BE"/>
 *             &lt;enumeration value="BL"/>
 *             &lt;enumeration value="BS"/>
 *             &lt;enumeration value="FR"/>
 *             &lt;enumeration value="GE"/>
 *             &lt;enumeration value="GL"/>
 *             &lt;enumeration value="GR"/>
 *             &lt;enumeration value="JU"/>
 *             &lt;enumeration value="LU"/>
 *             &lt;enumeration value="NE"/>
 *             &lt;enumeration value="NW"/>
 *             &lt;enumeration value="OW"/>
 *             &lt;enumeration value="SG"/>
 *             &lt;enumeration value="SH"/>
 *             &lt;enumeration value="SO"/>
 *             &lt;enumeration value="SZ"/>
 *             &lt;enumeration value="TI"/>
 *             &lt;enumeration value="TG"/>
 *             &lt;enumeration value="UR"/>
 *             &lt;enumeration value="VD"/>
 *             &lt;enumeration value="VS"/>
 *             &lt;enumeration value="ZG"/>
 *             &lt;enumeration value="ZH"/>
 *             &lt;enumeration value="LI"/>
 *             &lt;enumeration value="A"/>
 *             &lt;enumeration value="D"/>
 *             &lt;enumeration value="F"/>
 *             &lt;enumeration value="I"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="treatment" default="ambulatory">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="ambulatory"/>
 *             &lt;enumeration value="stationary"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="reason" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="disease"/>
 *             &lt;enumeration value="accident"/>
 *             &lt;enumeration value="maternity"/>
 *             &lt;enumeration value="prevention"/>
 *             &lt;enumeration value="birthdefect"/>
 *             &lt;enumeration value="unknown"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="apid" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *       &lt;attribute name="acid" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "treatmentType", propOrder = {
    "diagnosis",
    "xtraHospital"
})
public class TreatmentType {

    protected List<DiagnosisType> diagnosis;
    @XmlElement(name = "xtra_hospital")
    protected XtraHospitalType xtraHospital;
    @XmlAttribute(name = "date_begin", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateBegin;
    @XmlAttribute(name = "date_end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateEnd;
    @XmlAttribute(name = "gestation_week13")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar gestationWeek13;
    @XmlAttribute(name = "canton", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String canton;
    @XmlAttribute(name = "treatment")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String treatment;
    @XmlAttribute(name = "reason", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String reason;
    @XmlAttribute(name = "apid")
    protected String apid;
    @XmlAttribute(name = "acid")
    protected String acid;

    /**
     * Gets the value of the diagnosis property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diagnosis property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiagnosis().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiagnosisType }
     * 
     * 
     */
    public List<DiagnosisType> getDiagnosis() {
        if (diagnosis == null) {
            diagnosis = new ArrayList<DiagnosisType>();
        }
        return this.diagnosis;
    }

    /**
     * Ruft den Wert der xtraHospital-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XtraHospitalType }
     *     
     */
    public XtraHospitalType getXtraHospital() {
        return xtraHospital;
    }

    /**
     * Legt den Wert der xtraHospital-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XtraHospitalType }
     *     
     */
    public void setXtraHospital(XtraHospitalType value) {
        this.xtraHospital = value;
    }

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
