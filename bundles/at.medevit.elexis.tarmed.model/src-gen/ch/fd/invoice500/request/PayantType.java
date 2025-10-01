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


/**
 * <p>Java-Klasse für payantType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="payantType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="billers" type="{http://www.forum-datenaustausch.ch/invoice}billersAddressType"/&gt;
 *         &lt;element name="debitor" type="{http://www.forum-datenaustausch.ch/invoice}debitorAddressType"/&gt;
 *         &lt;element name="providers" type="{http://www.forum-datenaustausch.ch/invoice}providersAddressType"/&gt;
 *         &lt;element name="insurance" type="{http://www.forum-datenaustausch.ch/invoice}insuranceAddressType"/&gt;
 *         &lt;element name="patient" type="{http://www.forum-datenaustausch.ch/invoice}patientAddressType"/&gt;
 *         &lt;element name="guarantor" type="{http://www.forum-datenaustausch.ch/invoice}guarantorAddressType"/&gt;
 *         &lt;element name="insured" type="{http://www.forum-datenaustausch.ch/invoice}insuredAddressType" minOccurs="0"/&gt;
 *         &lt;element name="partners" type="{http://www.forum-datenaustausch.ch/invoice}partnersAddressType"/&gt;
 *         &lt;element name="balance" type="{http://www.forum-datenaustausch.ch/invoice}balanceTPType"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="allowModification" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payantType", propOrder = {
    "billers",
    "debitor",
    "providers",
    "insurance",
    "patient",
    "guarantor",
    "insured",
    "partners",
    "balance"
})
public class PayantType {

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
    protected GuarantorAddressType guarantor;
    protected InsuredAddressType insured;
    @XmlElement(required = true)
    protected PartnersAddressType partners;
    @XmlElement(required = true)
    protected BalanceTPType balance;
    @XmlAttribute(name = "allowModification", required = true)
    protected boolean allowModification;

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
     * Ruft den Wert der guarantor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GuarantorAddressType }
     *     
     */
    public GuarantorAddressType getGuarantor() {
        return guarantor;
    }

    /**
     * Legt den Wert der guarantor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GuarantorAddressType }
     *     
     */
    public void setGuarantor(GuarantorAddressType value) {
        this.guarantor = value;
    }

    /**
     * Ruft den Wert der insured-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InsuredAddressType }
     *     
     */
    public InsuredAddressType getInsured() {
        return insured;
    }

    /**
     * Legt den Wert der insured-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InsuredAddressType }
     *     
     */
    public void setInsured(InsuredAddressType value) {
        this.insured = value;
    }

    /**
     * Ruft den Wert der partners-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartnersAddressType }
     *     
     */
    public PartnersAddressType getPartners() {
        return partners;
    }

    /**
     * Legt den Wert der partners-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartnersAddressType }
     *     
     */
    public void setPartners(PartnersAddressType value) {
        this.partners = value;
    }

    /**
     * Ruft den Wert der balance-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BalanceTPType }
     *     
     */
    public BalanceTPType getBalance() {
        return balance;
    }

    /**
     * Legt den Wert der balance-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BalanceTPType }
     *     
     */
    public void setBalance(BalanceTPType value) {
        this.balance = value;
    }

    /**
     * Ruft den Wert der allowModification-Eigenschaft ab.
     * 
     */
    public boolean isAllowModification() {
        return allowModification;
    }

    /**
     * Legt den Wert der allowModification-Eigenschaft fest.
     * 
     */
    public void setAllowModification(boolean value) {
        this.allowModification = value;
    }

}
