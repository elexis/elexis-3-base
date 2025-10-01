//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:56:51 AM CEST 
//


package ch.fd.invoice500.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


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
 *         &lt;element name="billers" type="{http://www.forum-datenaustausch.ch/invoice}billersAddressType"/&gt;
 *         &lt;element name="debitor" type="{http://www.forum-datenaustausch.ch/invoice}debitorAddressType"/&gt;
 *         &lt;element name="providers" type="{http://www.forum-datenaustausch.ch/invoice}providersAddressType"/&gt;
 *         &lt;element name="insurance" type="{http://www.forum-datenaustausch.ch/invoice}insuranceAddressType"/&gt;
 *         &lt;element name="patient" type="{http://www.forum-datenaustausch.ch/invoice}patientAddressType"/&gt;
 *         &lt;element name="contact" type="{http://www.forum-datenaustausch.ch/invoice}contactAddressType"/&gt;
 *         &lt;element name="treatment" type="{http://www.forum-datenaustausch.ch/invoice}treatmentType"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="tiers_garant" type="{http://www.forum-datenaustausch.ch/invoice}garantType"/&gt;
 *           &lt;element name="tiers_soldant" type="{http://www.forum-datenaustausch.ch/invoice}soldantType"/&gt;
 *           &lt;element name="tiers_payant" type="{http://www.forum-datenaustausch.ch/invoice}payantType"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="documents" type="{http://www.forum-datenaustausch.ch/invoice}documentsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
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
    "billers",
    "debitor",
    "providers",
    "insurance",
    "patient",
    "contact",
    "treatment",
    "tiersGarant",
    "tiersSoldant",
    "tiersPayant",
    "documents"
})
public class BodyType {

    @XmlElement(required = true)
    protected PrologType prolog;
    @XmlElement(required = true)
    protected BillersAddressType billers;
    @XmlElement(required = true)
    protected DebitorAddressType debitor;
    @XmlElement(required = true)
    protected ProvidersAddressType providers;
    @XmlElement(required = true)
    protected InsuranceAddressType insurance;
    @XmlElement(required = true)
    protected PatientAddressType patient;
    @XmlElement(required = true)
    protected ContactAddressType contact;
    @XmlElement(required = true)
    protected TreatmentType treatment;
    @XmlElement(name = "tiers_garant")
    protected GarantType tiersGarant;
    @XmlElement(name = "tiers_soldant")
    protected SoldantType tiersSoldant;
    @XmlElement(name = "tiers_payant")
    protected PayantType tiersPayant;
    protected DocumentsType documents;

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
     * Ruft den Wert der billers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BillersAddressType }
     *     
     */
    public BillersAddressType getBillers() {
        return billers;
    }

    /**
     * Legt den Wert der billers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BillersAddressType }
     *     
     */
    public void setBillers(BillersAddressType value) {
        this.billers = value;
    }

    /**
     * Ruft den Wert der debitor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DebitorAddressType }
     *     
     */
    public DebitorAddressType getDebitor() {
        return debitor;
    }

    /**
     * Legt den Wert der debitor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DebitorAddressType }
     *     
     */
    public void setDebitor(DebitorAddressType value) {
        this.debitor = value;
    }

    /**
     * Ruft den Wert der providers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProvidersAddressType }
     *     
     */
    public ProvidersAddressType getProviders() {
        return providers;
    }

    /**
     * Legt den Wert der providers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProvidersAddressType }
     *     
     */
    public void setProviders(ProvidersAddressType value) {
        this.providers = value;
    }

    /**
     * Ruft den Wert der insurance-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InsuranceAddressType }
     *     
     */
    public InsuranceAddressType getInsurance() {
        return insurance;
    }

    /**
     * Legt den Wert der insurance-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuranceAddressType }
     *     
     */
    public void setInsurance(InsuranceAddressType value) {
        this.insurance = value;
    }

    /**
     * Ruft den Wert der patient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PatientAddressType }
     *     
     */
    public PatientAddressType getPatient() {
        return patient;
    }

    /**
     * Legt den Wert der patient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientAddressType }
     *     
     */
    public void setPatient(PatientAddressType value) {
        this.patient = value;
    }

    /**
     * Ruft den Wert der contact-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ContactAddressType }
     *     
     */
    public ContactAddressType getContact() {
        return contact;
    }

    /**
     * Legt den Wert der contact-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactAddressType }
     *     
     */
    public void setContact(ContactAddressType value) {
        this.contact = value;
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

}
