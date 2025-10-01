//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für caseDetailType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="caseDetailType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="record_id" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="1"/&gt;
 *             &lt;maxInclusive value="999999999"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="tariff_type"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;pattern value="[0-9A-Z]{3}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="code" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_30" /&gt;
 *       &lt;attribute name="date_begin" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="date_end" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="acid" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" /&gt;
 *       &lt;attribute name="residence_after_discharge"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="home"/&gt;
 *             &lt;enumeration value="home_with_spitex_care"/&gt;
 *             &lt;enumeration value="hospital_same_place"/&gt;
 *             &lt;enumeration value="hospital_other_place"/&gt;
 *             &lt;enumeration value="retirement_home"/&gt;
 *             &lt;enumeration value="nursing_home"/&gt;
 *             &lt;enumeration value="psychiatric_clinic_same_place"/&gt;
 *             &lt;enumeration value="psychiatric_clinic_other_place"/&gt;
 *             &lt;enumeration value="rehabilitation_clinic_same_place"/&gt;
 *             &lt;enumeration value="rehabilitation_clinic_other_place"/&gt;
 *             &lt;enumeration value="birth_house"/&gt;
 *             &lt;enumeration value="correctional_facility"/&gt;
 *             &lt;enumeration value="other"/&gt;
 *             &lt;enumeration value="unknown"/&gt;
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
@XmlType(name = "caseDetailType")
public class CaseDetailType {

    @XmlAttribute(name = "record_id", required = true)
    protected int recordId;
    @XmlAttribute(name = "tariff_type")
    protected String tariffType;
    @XmlAttribute(name = "code")
    protected String code;
    @XmlAttribute(name = "date_begin", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateBegin;
    @XmlAttribute(name = "date_end", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateEnd;
    @XmlAttribute(name = "acid", required = true)
    protected String acid;
    @XmlAttribute(name = "residence_after_discharge")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String residenceAfterDischarge;

    /**
     * Ruft den Wert der recordId-Eigenschaft ab.
     * 
     */
    public int getRecordId() {
        return recordId;
    }

    /**
     * Legt den Wert der recordId-Eigenschaft fest.
     * 
     */
    public void setRecordId(int value) {
        this.recordId = value;
    }

    /**
     * Ruft den Wert der tariffType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTariffType() {
        return tariffType;
    }

    /**
     * Legt den Wert der tariffType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTariffType(String value) {
        this.tariffType = value;
    }

    /**
     * Ruft den Wert der code-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Legt den Wert der code-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
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

    /**
     * Ruft den Wert der residenceAfterDischarge-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResidenceAfterDischarge() {
        return residenceAfterDischarge;
    }

    /**
     * Legt den Wert der residenceAfterDischarge-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResidenceAfterDischarge(String value) {
        this.residenceAfterDischarge = value;
    }

}
