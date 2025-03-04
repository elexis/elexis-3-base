//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für serviceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="serviceType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="xtra_drg" type="{http://www.forum-datenaustausch.ch/invoice}xtraDRGType"/>
 *         &lt;element name="xtra_drug" type="{http://www.forum-datenaustausch.ch/invoice}xtraDrugType"/>
 *       &lt;/choice>
 *       &lt;attribute name="record_id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;minInclusive value="1"/>
 *             &lt;maxInclusive value="999999999"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="tariff_type" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="[0-9A-Z]{3}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="code" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_30" />
 *       &lt;attribute name="ref_code" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_30" />
 *       &lt;attribute name="name" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_350" />
 *       &lt;attribute name="session" default="1">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *             &lt;minInclusive value="1"/>
 *             &lt;maxInclusive value="999999"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="quantity" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="date_begin" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="date_end" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="provider_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}eanPartyType" />
 *       &lt;attribute name="responsible_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}eanPartyType" />
 *       &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="unit_factor" default="1.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minExclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="external_factor" default="1.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="vat_rate" default="0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="100"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="obligation" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="section_code" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_6" />
 *       &lt;attribute name="remark" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_350" />
 *       &lt;attribute name="service_attributes" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "serviceType", propOrder = {
    "xtraDrg",
    "xtraDrug"
})
public class ServiceType {

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
    @XmlAttribute(name = "obligation")
    protected Boolean obligation;
    @XmlAttribute(name = "section_code")
    protected String sectionCode;
    @XmlAttribute(name = "remark")
    protected String remark;
    @XmlAttribute(name = "service_attributes")
    @XmlSchemaType(name = "unsignedInt")
    protected Long serviceAttributes;

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
     * Ruft den Wert der obligation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isObligation() {
        if (obligation == null) {
            return true;
        } else {
            return obligation;
        }
    }

    /**
     * Legt den Wert der obligation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setObligation(Boolean value) {
        this.obligation = value;
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
