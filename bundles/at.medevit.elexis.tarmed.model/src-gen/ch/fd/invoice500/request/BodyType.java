//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für bodyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="bodyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="prolog" type="{http://www.forum-datenaustausch.ch/invoice}prologType"/&gt;
 *         &lt;element name="remark" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_350" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="tiers_garant" type="{http://www.forum-datenaustausch.ch/invoice}garantType"/&gt;
 *           &lt;element name="tiers_soldant" type="{http://www.forum-datenaustausch.ch/invoice}soldantType"/&gt;
 *           &lt;element name="tiers_payant" type="{http://www.forum-datenaustausch.ch/invoice}payantType"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice&gt;
 *           &lt;element name="esrQR" type="{http://www.forum-datenaustausch.ch/invoice}esrQRType"/&gt;
 *           &lt;element name="esrQRRed" type="{http://www.forum-datenaustausch.ch/invoice}esrQRRedType"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="law" type="{http://www.forum-datenaustausch.ch/invoice}lawType"/&gt;
 *         &lt;element name="treatment" type="{http://www.forum-datenaustausch.ch/invoice}treatmentType"/&gt;
 *         &lt;element name="services" type="{http://www.forum-datenaustausch.ch/invoice}servicesType"/&gt;
 *         &lt;element name="documents" type="{http://www.forum-datenaustausch.ch/invoice}documentsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="role_title" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" /&gt;
 *       &lt;attribute name="role" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="physician"/&gt;
 *             &lt;enumeration value="physiotherapist"/&gt;
 *             &lt;enumeration value="chiropractor"/&gt;
 *             &lt;enumeration value="ergotherapist"/&gt;
 *             &lt;enumeration value="nutritionist"/&gt;
 *             &lt;enumeration value="midwife"/&gt;
 *             &lt;enumeration value="logotherapist"/&gt;
 *             &lt;enumeration value="hospital"/&gt;
 *             &lt;enumeration value="rehab_clinic"/&gt;
 *             &lt;enumeration value="psychiatric_clinic"/&gt;
 *             &lt;enumeration value="pharmacist"/&gt;
 *             &lt;enumeration value="dentist"/&gt;
 *             &lt;enumeration value="labtechnician"/&gt;
 *             &lt;enumeration value="dentaltechnician"/&gt;
 *             &lt;enumeration value="othertechnician"/&gt;
 *             &lt;enumeration value="psychologist"/&gt;
 *             &lt;enumeration value="wholesaler"/&gt;
 *             &lt;enumeration value="nursingstaff"/&gt;
 *             &lt;enumeration value="transport"/&gt;
 *             &lt;enumeration value="druggist"/&gt;
 *             &lt;enumeration value="naturopathicdoctor"/&gt;
 *             &lt;enumeration value="naturopathictherapist"/&gt;
 *             &lt;enumeration value="other"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="place" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="practice"/&gt;
 *             &lt;enumeration value="hospital"/&gt;
 *             &lt;enumeration value="lab"/&gt;
 *             &lt;enumeration value="association"/&gt;
 *             &lt;enumeration value="company"/&gt;
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
@XmlType(name = "bodyType", propOrder = {
    "prolog",
    "remark",
    "tiersGarant",
    "tiersSoldant",
    "tiersPayant",
    "esrQR",
    "esrQRRed",
    "law",
    "treatment",
    "services",
    "documents"
})
public class BodyType {

    @XmlElement(required = true)
    protected PrologType prolog;
    protected String remark;
    @XmlElement(name = "tiers_garant")
    protected GarantType tiersGarant;
    @XmlElement(name = "tiers_soldant")
    protected SoldantType tiersSoldant;
    @XmlElement(name = "tiers_payant")
    protected PayantType tiersPayant;
    protected EsrQRType esrQR;
    protected EsrQRRedType esrQRRed;
    @XmlElement(required = true)
    protected LawType law;
    @XmlElement(required = true)
    protected TreatmentType treatment;
    @XmlElement(required = true)
    protected ServicesType services;
    protected DocumentsType documents;
    @XmlAttribute(name = "role_title")
    protected String roleTitle;
    @XmlAttribute(name = "role", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String role;
    @XmlAttribute(name = "place", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String place;

    /**
     * Ruft den Wert der prolog-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PrologType }
     *     
     */
    public PrologType getProlog() {
        return prolog;
    }

    /**
     * Legt den Wert der prolog-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PrologType }
     *     
     */
    public void setProlog(PrologType value) {
        this.prolog = value;
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
     * Ruft den Wert der tiersGarant-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GarantType }
     *     
     */
    public GarantType getTiersGarant() {
        return tiersGarant;
    }

    /**
     * Legt den Wert der tiersGarant-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GarantType }
     *     
     */
    public void setTiersGarant(GarantType value) {
        this.tiersGarant = value;
    }

    /**
     * Ruft den Wert der tiersSoldant-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SoldantType }
     *     
     */
    public SoldantType getTiersSoldant() {
        return tiersSoldant;
    }

    /**
     * Legt den Wert der tiersSoldant-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SoldantType }
     *     
     */
    public void setTiersSoldant(SoldantType value) {
        this.tiersSoldant = value;
    }

    /**
     * Ruft den Wert der tiersPayant-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PayantType }
     *     
     */
    public PayantType getTiersPayant() {
        return tiersPayant;
    }

    /**
     * Legt den Wert der tiersPayant-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PayantType }
     *     
     */
    public void setTiersPayant(PayantType value) {
        this.tiersPayant = value;
    }

    /**
     * Ruft den Wert der esrQR-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrQRType }
     *     
     */
    public EsrQRType getEsrQR() {
        return esrQR;
    }

    /**
     * Legt den Wert der esrQR-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrQRType }
     *     
     */
    public void setEsrQR(EsrQRType value) {
        this.esrQR = value;
    }

    /**
     * Ruft den Wert der esrQRRed-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrQRRedType }
     *     
     */
    public EsrQRRedType getEsrQRRed() {
        return esrQRRed;
    }

    /**
     * Legt den Wert der esrQRRed-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrQRRedType }
     *     
     */
    public void setEsrQRRed(EsrQRRedType value) {
        this.esrQRRed = value;
    }

    /**
     * Ruft den Wert der law-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LawType }
     *     
     */
    public LawType getLaw() {
        return law;
    }

    /**
     * Legt den Wert der law-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LawType }
     *     
     */
    public void setLaw(LawType value) {
        this.law = value;
    }

    /**
     * Ruft den Wert der treatment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TreatmentType }
     *     
     */
    public TreatmentType getTreatment() {
        return treatment;
    }

    /**
     * Legt den Wert der treatment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TreatmentType }
     *     
     */
    public void setTreatment(TreatmentType value) {
        this.treatment = value;
    }

    /**
     * Ruft den Wert der services-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ServicesType }
     *     
     */
    public ServicesType getServices() {
        return services;
    }

    /**
     * Legt den Wert der services-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicesType }
     *     
     */
    public void setServices(ServicesType value) {
        this.services = value;
    }

    /**
     * Ruft den Wert der documents-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DocumentsType }
     *     
     */
    public DocumentsType getDocuments() {
        return documents;
    }

    /**
     * Legt den Wert der documents-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentsType }
     *     
     */
    public void setDocuments(DocumentsType value) {
        this.documents = value;
    }

    /**
     * Ruft den Wert der roleTitle-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoleTitle() {
        return roleTitle;
    }

    /**
     * Legt den Wert der roleTitle-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoleTitle(String value) {
        this.roleTitle = value;
    }

    /**
     * Ruft den Wert der role-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Legt den Wert der role-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(String value) {
        this.role = value;
    }

    /**
     * Ruft den Wert der place-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlace() {
        return place;
    }

    /**
     * Legt den Wert der place-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlace(String value) {
        this.place = value;
    }

}
