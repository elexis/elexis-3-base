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
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für serviceExType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="serviceExType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="xtra_service_ex" type="{http://www.forum-datenaustausch.ch/invoice}xtraServiceExType"/>
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
 *       &lt;attribute name="billing_role" default="both">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="mt"/>
 *             &lt;enumeration value="tt"/>
 *             &lt;enumeration value="both"/>
 *             &lt;enumeration value="none"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="medical_role" default="self_employed">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="self_employed"/>
 *             &lt;enumeration value="employee"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="body_location" default="none">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="none"/>
 *             &lt;enumeration value="left"/>
 *             &lt;enumeration value="right"/>
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
 *       &lt;attribute name="unit_mt" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="unit_factor_mt" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minExclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="scale_factor_mt" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *       &lt;attribute name="external_factor_mt" default="1.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="amount_mt" type="{http://www.w3.org/2001/XMLSchema}double" default="0.0" />
 *       &lt;attribute name="unit_tt" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="unit_factor_tt" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minExclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="scale_factor_tt" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *       &lt;attribute name="external_factor_tt" default="1.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="amount_tt" type="{http://www.w3.org/2001/XMLSchema}double" default="0.0" />
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="vat_rate" default="0.0">
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
@XmlType(name = "serviceExType", propOrder = {
    "xtraServiceEx"
})
public class ServiceExType {

    @XmlElement(name = "xtra_service_ex")
    protected XtraServiceExType xtraServiceEx;
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
    @XmlAttribute(name = "billing_role")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String billingRole;
    @XmlAttribute(name = "medical_role")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String medicalRole;
    @XmlAttribute(name = "body_location")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String bodyLocation;
    @XmlAttribute(name = "treatment")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String treatment;
    @XmlAttribute(name = "unit_mt", required = true)
    protected double unitMt;
    @XmlAttribute(name = "unit_factor_mt", required = true)
    protected double unitFactorMt;
    @XmlAttribute(name = "scale_factor_mt")
    protected Double scaleFactorMt;
    @XmlAttribute(name = "external_factor_mt")
    protected Double externalFactorMt;
    @XmlAttribute(name = "amount_mt")
    protected Double amountMt;
    @XmlAttribute(name = "unit_tt", required = true)
    protected double unitTt;
    @XmlAttribute(name = "unit_factor_tt", required = true)
    protected double unitFactorTt;
    @XmlAttribute(name = "scale_factor_tt")
    protected Double scaleFactorTt;
    @XmlAttribute(name = "external_factor_tt")
    protected Double externalFactorTt;
    @XmlAttribute(name = "amount_tt")
    protected Double amountTt;
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
     * Ruft den Wert der xtraServiceEx-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XtraServiceExType }
     *     
     */
    public XtraServiceExType getXtraServiceEx() {
        return xtraServiceEx;
    }

    /**
     * Legt den Wert der xtraServiceEx-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XtraServiceExType }
     *     
     */
    public void setXtraServiceEx(XtraServiceExType value) {
        this.xtraServiceEx = value;
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
     * Ruft den Wert der billingRole-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBillingRole() {
        if (billingRole == null) {
            return "both";
        } else {
            return billingRole;
        }
    }

    /**
     * Legt den Wert der billingRole-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBillingRole(String value) {
        this.billingRole = value;
    }

    /**
     * Ruft den Wert der medicalRole-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMedicalRole() {
        if (medicalRole == null) {
            return "self_employed";
        } else {
            return medicalRole;
        }
    }

    /**
     * Legt den Wert der medicalRole-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMedicalRole(String value) {
        this.medicalRole = value;
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
     * Ruft den Wert der unitMt-Eigenschaft ab.
     * 
     */
    public double getUnitMt() {
        return unitMt;
    }

    /**
     * Legt den Wert der unitMt-Eigenschaft fest.
     * 
     */
    public void setUnitMt(double value) {
        this.unitMt = value;
    }

    /**
     * Ruft den Wert der unitFactorMt-Eigenschaft ab.
     * 
     */
    public double getUnitFactorMt() {
        return unitFactorMt;
    }

    /**
     * Legt den Wert der unitFactorMt-Eigenschaft fest.
     * 
     */
    public void setUnitFactorMt(double value) {
        this.unitFactorMt = value;
    }

    /**
     * Ruft den Wert der scaleFactorMt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getScaleFactorMt() {
        if (scaleFactorMt == null) {
            return  1.0D;
        } else {
            return scaleFactorMt;
        }
    }

    /**
     * Legt den Wert der scaleFactorMt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setScaleFactorMt(Double value) {
        this.scaleFactorMt = value;
    }

    /**
     * Ruft den Wert der externalFactorMt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getExternalFactorMt() {
        if (externalFactorMt == null) {
            return  1.0D;
        } else {
            return externalFactorMt;
        }
    }

    /**
     * Legt den Wert der externalFactorMt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setExternalFactorMt(Double value) {
        this.externalFactorMt = value;
    }

    /**
     * Ruft den Wert der amountMt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getAmountMt() {
        if (amountMt == null) {
            return  0.0D;
        } else {
            return amountMt;
        }
    }

    /**
     * Legt den Wert der amountMt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmountMt(Double value) {
        this.amountMt = value;
    }

    /**
     * Ruft den Wert der unitTt-Eigenschaft ab.
     * 
     */
    public double getUnitTt() {
        return unitTt;
    }

    /**
     * Legt den Wert der unitTt-Eigenschaft fest.
     * 
     */
    public void setUnitTt(double value) {
        this.unitTt = value;
    }

    /**
     * Ruft den Wert der unitFactorTt-Eigenschaft ab.
     * 
     */
    public double getUnitFactorTt() {
        return unitFactorTt;
    }

    /**
     * Legt den Wert der unitFactorTt-Eigenschaft fest.
     * 
     */
    public void setUnitFactorTt(double value) {
        this.unitFactorTt = value;
    }

    /**
     * Ruft den Wert der scaleFactorTt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getScaleFactorTt() {
        if (scaleFactorTt == null) {
            return  1.0D;
        } else {
            return scaleFactorTt;
        }
    }

    /**
     * Legt den Wert der scaleFactorTt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setScaleFactorTt(Double value) {
        this.scaleFactorTt = value;
    }

    /**
     * Ruft den Wert der externalFactorTt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getExternalFactorTt() {
        if (externalFactorTt == null) {
            return  1.0D;
        } else {
            return externalFactorTt;
        }
    }

    /**
     * Legt den Wert der externalFactorTt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setExternalFactorTt(Double value) {
        this.externalFactorTt = value;
    }

    /**
     * Ruft den Wert der amountTt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public double getAmountTt() {
        if (amountTt == null) {
            return  0.0D;
        } else {
            return amountTt;
        }
    }

    /**
     * Legt den Wert der amountTt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setAmountTt(Double value) {
        this.amountTt = value;
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
