//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:13:04 PM CEST 
//


package ch.fd.invoice450.response;

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
 * &lt;complexType name="bodyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="prolog" type="{http://www.forum-datenaustausch.ch/invoice}prologType"/>
 *         &lt;element name="biller" type="{http://www.forum-datenaustausch.ch/invoice}partyType"/>
 *         &lt;element name="debitor" type="{http://www.forum-datenaustausch.ch/invoice}partyType"/>
 *         &lt;element name="provider" type="{http://www.forum-datenaustausch.ch/invoice}partyType"/>
 *         &lt;element name="insurance" type="{http://www.forum-datenaustausch.ch/invoice}partyType"/>
 *         &lt;element name="patient" type="{http://www.forum-datenaustausch.ch/invoice}patientAddressType"/>
 *         &lt;element name="treatment" type="{http://www.forum-datenaustausch.ch/invoice}treatmentType" minOccurs="0"/>
 *         &lt;element name="contact" type="{http://www.forum-datenaustausch.ch/invoice}contactAddressType"/>
 *         &lt;choice>
 *           &lt;element name="pending" type="{http://www.forum-datenaustausch.ch/invoice}pendingType"/>
 *           &lt;element name="accepted" type="{http://www.forum-datenaustausch.ch/invoice}acceptedType"/>
 *           &lt;element name="rejected" type="{http://www.forum-datenaustausch.ch/invoice}rejectedType"/>
 *         &lt;/choice>
 *         &lt;element name="documents" type="{http://www.forum-datenaustausch.ch/invoice}documentsType" minOccurs="0"/>
 *       &lt;/sequence>
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
    "biller",
    "debitor",
    "provider",
    "insurance",
    "patient",
    "treatment",
    "contact",
    "pending",
    "accepted",
    "rejected",
    "documents"
})
public class BodyType {

    @XmlElement(required = true)
    protected PrologType prolog;
    @XmlElement(required = true)
    protected PartyType biller;
    @XmlElement(required = true)
    protected PartyType debitor;
    @XmlElement(required = true)
    protected PartyType provider;
    @XmlElement(required = true)
    protected PartyType insurance;
    @XmlElement(required = true)
    protected PatientAddressType patient;
    protected TreatmentType treatment;
    @XmlElement(required = true)
    protected ContactAddressType contact;
    protected PendingType pending;
    protected AcceptedType accepted;
    protected RejectedType rejected;
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
     * Ruft den Wert der biller-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getBiller() {
        return biller;
    }

    /**
     * Legt den Wert der biller-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setBiller(PartyType value) {
        this.biller = value;
    }

    /**
     * Ruft den Wert der debitor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getDebitor() {
        return debitor;
    }

    /**
     * Legt den Wert der debitor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setDebitor(PartyType value) {
        this.debitor = value;
    }

    /**
     * Ruft den Wert der provider-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getProvider() {
        return provider;
    }

    /**
     * Legt den Wert der provider-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setProvider(PartyType value) {
        this.provider = value;
    }

    /**
     * Ruft den Wert der insurance-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyType }
     *     
     */
    public PartyType getInsurance() {
        return insurance;
    }

    /**
     * Legt den Wert der insurance-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyType }
     *     
     */
    public void setInsurance(PartyType value) {
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
     * Ruft den Wert der pending-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PendingType }
     *     
     */
    public PendingType getPending() {
        return pending;
    }

    /**
     * Legt den Wert der pending-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PendingType }
     *     
     */
    public void setPending(PendingType value) {
        this.pending = value;
    }

    /**
     * Ruft den Wert der accepted-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AcceptedType }
     *     
     */
    public AcceptedType getAccepted() {
        return accepted;
    }

    /**
     * Legt den Wert der accepted-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptedType }
     *     
     */
    public void setAccepted(AcceptedType value) {
        this.accepted = value;
    }

    /**
     * Ruft den Wert der rejected-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RejectedType }
     *     
     */
    public RejectedType getRejected() {
        return rejected;
    }

    /**
     * Legt den Wert der rejected-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RejectedType }
     *     
     */
    public void setRejected(RejectedType value) {
        this.rejected = value;
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
