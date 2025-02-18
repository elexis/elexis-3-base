//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.Duration;
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
 * <p>Java-Klasse für xtraStationaryType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="xtraStationaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="admission_type" type="{http://www.forum-datenaustausch.ch/invoice}grouperDataType"/>
 *         &lt;element name="discharge_type" type="{http://www.forum-datenaustausch.ch/invoice}grouperDataType"/>
 *         &lt;element name="provider_type" type="{http://www.forum-datenaustausch.ch/invoice}grouperDataType"/>
 *         &lt;element name="bfs_residence_before_admission" type="{http://www.forum-datenaustausch.ch/invoice}bfsDataType"/>
 *         &lt;element name="bfs_admission_type" type="{http://www.forum-datenaustausch.ch/invoice}bfsDataType"/>
 *         &lt;element name="bfs_decision_for_discharge" type="{http://www.forum-datenaustausch.ch/invoice}bfsDataType"/>
 *         &lt;element name="bfs_residence_after_discharge" type="{http://www.forum-datenaustausch.ch/invoice}bfsDataType"/>
 *         &lt;element name="case_detail" type="{http://www.forum-datenaustausch.ch/invoice}caseDetailType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hospitalization_date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="treatment_days" use="required" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *       &lt;attribute name="hospitalization_type" default="regular">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="regular"/>
 *             &lt;enumeration value="emergency"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="hospitalization_mode" default="cantonal">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="cantonal"/>
 *             &lt;enumeration value="noncantonal_indicated"/>
 *             &lt;enumeration value="noncantonal_nonindicated"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="class" default="general">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="private"/>
 *             &lt;enumeration value="semi_private"/>
 *             &lt;enumeration value="general"/>
 *             &lt;enumeration value="hospital_comfort"/>
 *             &lt;enumeration value="md_free_choice"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="section_major" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_6" />
 *       &lt;attribute name="has_expense_loading" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xtraStationaryType", propOrder = {
    "admissionType",
    "dischargeType",
    "providerType",
    "bfsResidenceBeforeAdmission",
    "bfsAdmissionType",
    "bfsDecisionForDischarge",
    "bfsResidenceAfterDischarge",
    "caseDetail"
})
public class XtraStationaryType {

    @XmlElement(name = "admission_type", required = true)
    protected GrouperDataType admissionType;
    @XmlElement(name = "discharge_type", required = true)
    protected GrouperDataType dischargeType;
    @XmlElement(name = "provider_type", required = true)
    protected GrouperDataType providerType;
    @XmlElement(name = "bfs_residence_before_admission", required = true)
    protected BfsDataType bfsResidenceBeforeAdmission;
    @XmlElement(name = "bfs_admission_type", required = true)
    protected BfsDataType bfsAdmissionType;
    @XmlElement(name = "bfs_decision_for_discharge", required = true)
    protected BfsDataType bfsDecisionForDischarge;
    @XmlElement(name = "bfs_residence_after_discharge", required = true)
    protected BfsDataType bfsResidenceAfterDischarge;
    @XmlElement(name = "case_detail")
    protected List<CaseDetailType> caseDetail;
    @XmlAttribute(name = "hospitalization_date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar hospitalizationDate;
    @XmlAttribute(name = "treatment_days", required = true)
    protected Duration treatmentDays;
    @XmlAttribute(name = "hospitalization_type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String hospitalizationType;
    @XmlAttribute(name = "hospitalization_mode")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String hospitalizationMode;
    @XmlAttribute(name = "class")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String clazz;
    @XmlAttribute(name = "section_major")
    protected String sectionMajor;
    @XmlAttribute(name = "has_expense_loading")
    protected Boolean hasExpenseLoading;

    /**
     * Ruft den Wert der admissionType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GrouperDataType }
     *     
     */
    public GrouperDataType getAdmissionType() {
        return admissionType;
    }

    /**
     * Legt den Wert der admissionType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GrouperDataType }
     *     
     */
    public void setAdmissionType(GrouperDataType value) {
        this.admissionType = value;
    }

    /**
     * Ruft den Wert der dischargeType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GrouperDataType }
     *     
     */
    public GrouperDataType getDischargeType() {
        return dischargeType;
    }

    /**
     * Legt den Wert der dischargeType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GrouperDataType }
     *     
     */
    public void setDischargeType(GrouperDataType value) {
        this.dischargeType = value;
    }

    /**
     * Ruft den Wert der providerType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GrouperDataType }
     *     
     */
    public GrouperDataType getProviderType() {
        return providerType;
    }

    /**
     * Legt den Wert der providerType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GrouperDataType }
     *     
     */
    public void setProviderType(GrouperDataType value) {
        this.providerType = value;
    }

    /**
     * Ruft den Wert der bfsResidenceBeforeAdmission-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BfsDataType }
     *     
     */
    public BfsDataType getBfsResidenceBeforeAdmission() {
        return bfsResidenceBeforeAdmission;
    }

    /**
     * Legt den Wert der bfsResidenceBeforeAdmission-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BfsDataType }
     *     
     */
    public void setBfsResidenceBeforeAdmission(BfsDataType value) {
        this.bfsResidenceBeforeAdmission = value;
    }

    /**
     * Ruft den Wert der bfsAdmissionType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BfsDataType }
     *     
     */
    public BfsDataType getBfsAdmissionType() {
        return bfsAdmissionType;
    }

    /**
     * Legt den Wert der bfsAdmissionType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BfsDataType }
     *     
     */
    public void setBfsAdmissionType(BfsDataType value) {
        this.bfsAdmissionType = value;
    }

    /**
     * Ruft den Wert der bfsDecisionForDischarge-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BfsDataType }
     *     
     */
    public BfsDataType getBfsDecisionForDischarge() {
        return bfsDecisionForDischarge;
    }

    /**
     * Legt den Wert der bfsDecisionForDischarge-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BfsDataType }
     *     
     */
    public void setBfsDecisionForDischarge(BfsDataType value) {
        this.bfsDecisionForDischarge = value;
    }

    /**
     * Ruft den Wert der bfsResidenceAfterDischarge-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BfsDataType }
     *     
     */
    public BfsDataType getBfsResidenceAfterDischarge() {
        return bfsResidenceAfterDischarge;
    }

    /**
     * Legt den Wert der bfsResidenceAfterDischarge-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BfsDataType }
     *     
     */
    public void setBfsResidenceAfterDischarge(BfsDataType value) {
        this.bfsResidenceAfterDischarge = value;
    }

    /**
     * Gets the value of the caseDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the caseDetail property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCaseDetail().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CaseDetailType }
     * 
     * 
     */
    public List<CaseDetailType> getCaseDetail() {
        if (caseDetail == null) {
            caseDetail = new ArrayList<CaseDetailType>();
        }
        return this.caseDetail;
    }

    /**
     * Ruft den Wert der hospitalizationDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHospitalizationDate() {
        return hospitalizationDate;
    }

    /**
     * Legt den Wert der hospitalizationDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHospitalizationDate(XMLGregorianCalendar value) {
        this.hospitalizationDate = value;
    }

    /**
     * Ruft den Wert der treatmentDays-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getTreatmentDays() {
        return treatmentDays;
    }

    /**
     * Legt den Wert der treatmentDays-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setTreatmentDays(Duration value) {
        this.treatmentDays = value;
    }

    /**
     * Ruft den Wert der hospitalizationType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHospitalizationType() {
        if (hospitalizationType == null) {
            return "regular";
        } else {
            return hospitalizationType;
        }
    }

    /**
     * Legt den Wert der hospitalizationType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHospitalizationType(String value) {
        this.hospitalizationType = value;
    }

    /**
     * Ruft den Wert der hospitalizationMode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHospitalizationMode() {
        if (hospitalizationMode == null) {
            return "cantonal";
        } else {
            return hospitalizationMode;
        }
    }

    /**
     * Legt den Wert der hospitalizationMode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHospitalizationMode(String value) {
        this.hospitalizationMode = value;
    }

    /**
     * Ruft den Wert der clazz-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        if (clazz == null) {
            return "general";
        } else {
            return clazz;
        }
    }

    /**
     * Legt den Wert der clazz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Ruft den Wert der sectionMajor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSectionMajor() {
        return sectionMajor;
    }

    /**
     * Legt den Wert der sectionMajor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSectionMajor(String value) {
        this.sectionMajor = value;
    }

    /**
     * Ruft den Wert der hasExpenseLoading-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isHasExpenseLoading() {
        if (hasExpenseLoading == null) {
            return true;
        } else {
            return hasExpenseLoading;
        }
    }

    /**
     * Legt den Wert der hasExpenseLoading-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHasExpenseLoading(Boolean value) {
        this.hasExpenseLoading = value;
    }

}
