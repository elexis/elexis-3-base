//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für serviceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="serviceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xtra_service" type="{http://www.forum-datenaustausch.ch/invoice}xtraServiceType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="xtra_drg" type="{http://www.forum-datenaustausch.ch/invoice}xtraDRGType" minOccurs="0"/&gt;
 *         &lt;element name="xtra_drug" type="{http://www.forum-datenaustausch.ch/invoice}xtraDrugType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="record_id" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="1"/&gt;
 *             &lt;maxInclusive value="999999999"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="tariff_type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;pattern value="[0-9A-Z]{3}"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="code" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_30" /&gt;
 *       &lt;attribute name="ref_code" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_30" /&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_350" /&gt;
 *       &lt;attribute name="session" default="1"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="1"/&gt;
 *             &lt;maxInclusive value="999999"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="group_size" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" /&gt;
 *       &lt;attribute name="quantity" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="date_begin" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="date_end" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="provider_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}glnPartyType" /&gt;
 *       &lt;attribute name="responsible_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}glnPartyType" /&gt;
 *       &lt;attribute name="body_location" default="none"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="none"/&gt;
 *             &lt;enumeration value="left"/&gt;
 *             &lt;enumeration value="right"/&gt;
 *             &lt;enumeration value="both"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="unit_factor" default="1.0"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *             &lt;minExclusive value="0.0"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="external_factor" default="1.0"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *             &lt;minInclusive value="0.0"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="vat_rate" default="0"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="100"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="section_code" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_9" /&gt;
 *       &lt;attribute name="remark" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_350" /&gt;
 *       &lt;attribute name="service_attributes" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" default="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceType", propOrder = {
    "xtraService",
    "xtraDrg",
    "xtraDrug"
})
public class ServiceType {

    @XmlElement(name = "xtra_service")
    protected List<XtraServiceType> xtraService;
    @XmlElement(name = "xtra_drg")
    protected XtraDRGType xtraDrg;
    @XmlElement(name = "xtra_drug")
    protected XtraDrugType xtraDrug;
    @XmlAttribute(name = "record_id", required = true)
    protected int recordId;
    @XmlAttribute(name = "tariff_type", required = true)
    protected String tariffType;
    @XmlAttribute(name = "code", required = true)
    protected String code;
    @XmlAttribute(name = "ref_code")
    protected String refCode;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "session")
    protected Integer session;
    @XmlAttribute(name = "group_size")
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger groupSize;
    @XmlAttribute(name = "quantity", required = true)
    protected double quantity;
    @XmlAttribute(name = "date_begin", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateBegin;
    @XmlAttribute(name = "date_end")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateEnd;
    @XmlAttribute(name = "provider_id", required = true)
    protected String providerId;
    @XmlAttribute(name = "responsible_id", required = true)
    protected String responsibleId;
    @XmlAttribute(name = "body_location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String bodyLocation;
    @XmlAttribute(name = "unit", required = true)
    protected double unit;
    @XmlAttribute(name = "unit_factor")
    protected Double unitFactor;
    @XmlAttribute(name = "external_factor")
    protected Double externalFactor;
    @XmlAttribute(name = "amount", required = true)
    protected double amount;
    @XmlAttribute(name = "vat_rate")
    protected Double vatRate;
    @XmlAttribute(name = "section_code")
    protected String sectionCode;
    @XmlAttribute(name = "remark")
    protected String remark;
    @XmlAttribute(name = "service_attributes")
    @XmlSchemaType(name = "unsignedInt")
    protected Long serviceAttributes;

    /**
     * Gets the value of the xtraService property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the xtraService property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXtraService().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XtraServiceType }
     * 
     * 
     */
    public List<XtraServiceType> getXtraService() {
        if (xtraService == null) {
            xtraService = new ArrayList<XtraServiceType>();
        }
        return this.xtraService;
    }

    /**
     * Ruft den Wert der xtraDrg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XtraDRGType }
     *     
     */
    public XtraDRGType getXtraDrg() {
        return xtraDrg;
    }

    /**
     * Legt den Wert der xtraDrg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XtraDRGType }
     *     
     */
    public void setXtraDrg(XtraDRGType value) {
        this.xtraDrg = value;
    }

    /**
     * Ruft den Wert der xtraDrug-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XtraDrugType }
     *     
     */
    public XtraDrugType getXtraDrug() {
        return xtraDrug;
    }

    /**
     * Legt den Wert der xtraDrug-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XtraDrugType }
     *     
     */
    public void setXtraDrug(XtraDrugType value) {
        this.xtraDrug = value;
    }

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
     * Ruft den Wert der refCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefCode() {
        return refCode;
    }

    /**
     * Legt den Wert der refCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefCode(String value) {
        this.refCode = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der session-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getSession() {
        if (session == null) {
            return  1;
        } else {
            return session;
        }
    }

    /**
     * Legt den Wert der session-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSession(Integer value) {
        this.session = value;
    }

    /**
     * Ruft den Wert der groupSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGroupSize() {
        if (groupSize == null) {
            return new BigInteger("1");
        } else {
            return groupSize;
        }
    }

    /**
     * Legt den Wert der groupSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGroupSize(BigInteger value) {
        this.groupSize = value;
    }

    /**
     * Ruft den Wert der quantity-Eigenschaft ab.
     * 
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Legt den Wert der quantity-Eigenschaft fest.
     * 
     */
    public void setQuantity(double value) {
        this.quantity = value;
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
     * Ruft den Wert der providerId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * Legt den Wert der providerId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderId(String value) {
        this.providerId = value;
    }

    /**
     * Ruft den Wert der responsibleId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponsibleId() {
        return responsibleId;
    }

    /**
     * Legt den Wert der responsibleId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponsibleId(String value) {
        this.responsibleId = value;
    }

    /**
     * Ruft den Wert der bodyLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBodyLocation() {
        if (bodyLocation == null) {
            return "none";
        } else {
            return bodyLocation;
        }
    }

    /**
     * Legt den Wert der bodyLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBodyLocation(String value) {
        this.bodyLocation = value;
    }

    /**
     * Ruft den Wert der unit-Eigenschaft ab.
     * 
     */
    public double getUnit() {
        return unit;
    }

    /**
     * Legt den Wert der unit-Eigenschaft fest.
     * 
     */
    public void setUnit(double value) {
        this.unit = value;
    }

    /**
     * Ruft den Wert der unitFactor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getUnitFactor() {
        if (unitFactor == null) {
            return  1.0D;
        } else {
            return unitFactor;
        }
    }

    /**
     * Legt den Wert der unitFactor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setUnitFactor(Double value) {
        this.unitFactor = value;
    }

    /**
     * Ruft den Wert der externalFactor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getExternalFactor() {
        if (externalFactor == null) {
            return  1.0D;
        } else {
            return externalFactor;
        }
    }

    /**
     * Legt den Wert der externalFactor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setExternalFactor(Double value) {
        this.externalFactor = value;
    }

    /**
     * Ruft den Wert der amount-Eigenschaft ab.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Legt den Wert der amount-Eigenschaft fest.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
    }

    /**
     * Ruft den Wert der vatRate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getVatRate() {
        if (vatRate == null) {
            return  0.0D;
        } else {
            return vatRate;
        }
    }

    /**
     * Legt den Wert der vatRate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVatRate(Double value) {
        this.vatRate = value;
    }

    /**
     * Ruft den Wert der sectionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSectionCode() {
        return sectionCode;
    }

    /**
     * Legt den Wert der sectionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSectionCode(String value) {
        this.sectionCode = value;
    }

    /**
     * Ruft den Wert der remark-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark() {
        return remark;
    }

    /**
     * Legt den Wert der remark-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemark(String value) {
        this.remark = value;
    }

    /**
     * Ruft den Wert der serviceAttributes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getServiceAttributes() {
        if (serviceAttributes == null) {
            return  0L;
        } else {
            return serviceAttributes;
        }
    }

    /**
     * Legt den Wert der serviceAttributes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setServiceAttributes(Long value) {
        this.serviceAttributes = value;
    }

}
