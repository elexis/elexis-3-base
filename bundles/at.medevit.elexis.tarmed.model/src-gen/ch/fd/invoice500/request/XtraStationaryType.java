//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.Duration;
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
 * <p>Java-Klasse für xtraStationaryType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="xtraStationaryType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="case_detail" type="{http://www.forum-datenaustausch.ch/invoice}caseDetailType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="hospitalization_date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *       &lt;attribute name="treatment_days" use="required" type="{http://www.w3.org/2001/XMLSchema}duration" /&gt;
 *       &lt;attribute name="hospitalization_type" default="regular"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="regular"/&gt;
 *             &lt;enumeration value="emergency"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="hospitalization_mode" default="cantonal"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="cantonal"/&gt;
 *             &lt;enumeration value="noncantonal_indicated"/&gt;
 *             &lt;enumeration value="noncantonal_nonindicated"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="class" default="general"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="private"/&gt;
 *             &lt;enumeration value="semi_private"/&gt;
 *             &lt;enumeration value="general"/&gt;
 *             &lt;enumeration value="hospital_comfort"/&gt;
 *             &lt;enumeration value="md_free_choice"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="admission_type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="normal"/&gt;
 *             &lt;enumeration value="relocated_stayed_more_24h"/&gt;
 *             &lt;enumeration value="relocated_stayed_less_24h"/&gt;
 *             &lt;enumeration value="birth"/&gt;
 *             &lt;enumeration value="unknown"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="residence_before_admission" use="required"&gt;
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
 *       &lt;attribute name="discharge_type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="normal"/&gt;
 *             &lt;enumeration value="transferred"/&gt;
 *             &lt;enumeration value="deceased"/&gt;
 *             &lt;enumeration value="terminated"/&gt;
 *             &lt;enumeration value="unknown"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="residence_after_discharge" use="required"&gt;
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
 *       &lt;attribute name="provider_type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="acute_hospital"/&gt;
 *             &lt;enumeration value="birthhouse"/&gt;
 *             &lt;enumeration value="psychiatric_clinic"/&gt;
 *             &lt;enumeration value="rehabilitation_clinic"/&gt;
 *             &lt;enumeration value="medical_practice"/&gt;
 *             &lt;enumeration value="other"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="section_major" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_9" /&gt;
 *       &lt;attribute name="has_expense_loading" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="HPSG" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_30" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xtraStationaryType", propOrder = {
    "caseDetail"
})
public class XtraStationaryType {

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
    @XmlAttribute(name = "admission_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String admissionType;
    @XmlAttribute(name = "residence_before_admission", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String residenceBeforeAdmission;
    @XmlAttribute(name = "discharge_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String dischargeType;
    @XmlAttribute(name = "residence_after_discharge", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String residenceAfterDischarge;
    @XmlAttribute(name = "provider_type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String providerType;
    @XmlAttribute(name = "section_major")
    protected String sectionMajor;
    @XmlAttribute(name = "has_expense_loading")
    protected Boolean hasExpenseLoading;
    @XmlAttribute(name = "HPSG")
    protected String hpsg;

    /**
     * Gets the value of the caseDetail property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
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
     * Ruft den Wert der admissionType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdmissionType() {
        return admissionType;
    }

    /**
     * Legt den Wert der admissionType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdmissionType(String value) {
        this.admissionType = value;
    }

    /**
     * Ruft den Wert der residenceBeforeAdmission-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResidenceBeforeAdmission() {
        return residenceBeforeAdmission;
    }

    /**
     * Legt den Wert der residenceBeforeAdmission-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResidenceBeforeAdmission(String value) {
        this.residenceBeforeAdmission = value;
    }

    /**
     * Ruft den Wert der dischargeType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDischargeType() {
        return dischargeType;
    }

    /**
     * Legt den Wert der dischargeType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDischargeType(String value) {
        this.dischargeType = value;
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

    /**
     * Ruft den Wert der providerType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProviderType() {
        return providerType;
    }

    /**
     * Legt den Wert der providerType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProviderType(String value) {
        this.providerType = value;
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

    /**
     * Ruft den Wert der hpsg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHPSG() {
        return hpsg;
    }

    /**
     * Legt den Wert der hpsg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHPSG(String value) {
        this.hpsg = value;
    }

}
