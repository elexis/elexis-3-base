//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für bodyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="bodyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="prolog" type="{http://www.forum-datenaustausch.ch/invoice}prologType"/>
 *         &lt;element name="remark" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_350" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="tiers_garant" type="{http://www.forum-datenaustausch.ch/invoice}garantType"/>
 *           &lt;element name="tiers_payant" type="{http://www.forum-datenaustausch.ch/invoice}payantType"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="esr9" type="{http://www.forum-datenaustausch.ch/invoice}esr9Type"/>
 *           &lt;element name="esrRed" type="{http://www.forum-datenaustausch.ch/invoice}esrRedType"/>
 *           &lt;element name="esrQR" type="{http://www.forum-datenaustausch.ch/invoice}esrQRType"/>
 *         &lt;/choice>
 *         &lt;choice>
 *           &lt;element name="kvg" type="{http://www.forum-datenaustausch.ch/invoice}kvgLawType"/>
 *           &lt;element name="vvg" type="{http://www.forum-datenaustausch.ch/invoice}vvgLawType"/>
 *           &lt;element name="uvg" type="{http://www.forum-datenaustausch.ch/invoice}uvgLawType"/>
 *           &lt;element name="ivg" type="{http://www.forum-datenaustausch.ch/invoice}ivgLawType"/>
 *           &lt;element name="mvg" type="{http://www.forum-datenaustausch.ch/invoice}mvgLawType"/>
 *           &lt;element name="org" type="{http://www.forum-datenaustausch.ch/invoice}orgLawType"/>
 *         &lt;/choice>
 *         &lt;element name="treatment" type="{http://www.forum-datenaustausch.ch/invoice}treatmentType"/>
 *         &lt;element name="services" type="{http://www.forum-datenaustausch.ch/invoice}servicesType"/>
 *         &lt;element name="documents" type="{http://www.forum-datenaustausch.ch/invoice}documentsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="role_title" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *       &lt;attribute name="role" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="physician"/>
 *             &lt;enumeration value="physiotherapist"/>
 *             &lt;enumeration value="chiropractor"/>
 *             &lt;enumeration value="ergotherapist"/>
 *             &lt;enumeration value="nutritionist"/>
 *             &lt;enumeration value="midwife"/>
 *             &lt;enumeration value="logotherapist"/>
 *             &lt;enumeration value="hospital"/>
 *             &lt;enumeration value="pharmacist"/>
 *             &lt;enumeration value="dentist"/>
 *             &lt;enumeration value="labtechnician"/>
 *             &lt;enumeration value="dentaltechnician"/>
 *             &lt;enumeration value="othertechnician"/>
 *             &lt;enumeration value="psychologist"/>
 *             &lt;enumeration value="wholesaler"/>
 *             &lt;enumeration value="nursingstaff"/>
 *             &lt;enumeration value="transport"/>
 *             &lt;enumeration value="druggist"/>
 *             &lt;enumeration value="naturopathicdoctor"/>
 *             &lt;enumeration value="naturopathictherapist"/>
 *             &lt;enumeration value="other"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="place" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="practice"/>
 *             &lt;enumeration value="hospital"/>
 *             &lt;enumeration value="lab"/>
 *             &lt;enumeration value="association"/>
 *             &lt;enumeration value="company"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bodyType", propOrder = {
    "prolog",
    "remark",
    "tiersGarant",
    "tiersPayant",
    "esr9",
    "esrRed",
    "esrQR",
    "kvg",
    "vvg",
    "uvg",
    "ivg",
    "mvg",
    "org",
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
    @XmlElement(name = "tiers_payant")
    protected PayantType tiersPayant;
    protected Esr9Type esr9;
    protected EsrRedType esrRed;
    protected EsrQRType esrQR;
    protected KvgLawType kvg;
    protected VvgLawType vvg;
    protected UvgLawType uvg;
    protected IvgLawType ivg;
    protected MvgLawType mvg;
    protected OrgLawType org;
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
     * Ruft den Wert der esr9-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Esr9Type }
     *     
     */
    public Esr9Type getEsr9() {
        return esr9;
    }

    /**
     * Legt den Wert der esr9-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Esr9Type }
     *     
     */
    public void setEsr9(Esr9Type value) {
        this.esr9 = value;
    }

    /**
     * Ruft den Wert der esrRed-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrRedType }
     *     
     */
    public EsrRedType getEsrRed() {
        return esrRed;
    }

    /**
     * Legt den Wert der esrRed-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrRedType }
     *     
     */
    public void setEsrRed(EsrRedType value) {
        this.esrRed = value;
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
     * Ruft den Wert der kvg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link KvgLawType }
     *     
     */
    public KvgLawType getKvg() {
        return kvg;
    }

    /**
     * Legt den Wert der kvg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link KvgLawType }
     *     
     */
    public void setKvg(KvgLawType value) {
        this.kvg = value;
    }

    /**
     * Ruft den Wert der vvg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link VvgLawType }
     *     
     */
    public VvgLawType getVvg() {
        return vvg;
    }

    /**
     * Legt den Wert der vvg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link VvgLawType }
     *     
     */
    public void setVvg(VvgLawType value) {
        this.vvg = value;
    }

    /**
     * Ruft den Wert der uvg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link UvgLawType }
     *     
     */
    public UvgLawType getUvg() {
        return uvg;
    }

    /**
     * Legt den Wert der uvg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link UvgLawType }
     *     
     */
    public void setUvg(UvgLawType value) {
        this.uvg = value;
    }

    /**
     * Ruft den Wert der ivg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IvgLawType }
     *     
     */
    public IvgLawType getIvg() {
        return ivg;
    }

    /**
     * Legt den Wert der ivg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IvgLawType }
     *     
     */
    public void setIvg(IvgLawType value) {
        this.ivg = value;
    }

    /**
     * Ruft den Wert der mvg-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MvgLawType }
     *     
     */
    public MvgLawType getMvg() {
        return mvg;
    }

    /**
     * Legt den Wert der mvg-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MvgLawType }
     *     
     */
    public void setMvg(MvgLawType value) {
        this.mvg = value;
    }

    /**
     * Ruft den Wert der org-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OrgLawType }
     *     
     */
    public OrgLawType getOrg() {
        return org;
    }

    /**
     * Legt den Wert der org-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OrgLawType }
     *     
     */
    public void setOrg(OrgLawType value) {
        this.org = value;
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
