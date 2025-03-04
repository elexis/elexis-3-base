//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import javax.xml.datatype.Duration;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für payantType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="payantType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="biller" type="{http://www.forum-datenaustausch.ch/invoice}billerAddressType"/>
 *         &lt;element name="debitor" type="{http://www.forum-datenaustausch.ch/invoice}debitorAddressType"/>
 *         &lt;element name="provider" type="{http://www.forum-datenaustausch.ch/invoice}providerAddressType"/>
 *         &lt;element name="insurance" type="{http://www.forum-datenaustausch.ch/invoice}insuranceAddressType"/>
 *         &lt;element name="patient" type="{http://www.forum-datenaustausch.ch/invoice}patientAddressType"/>
 *         &lt;element name="insured" type="{http://www.forum-datenaustausch.ch/invoice}patientAddressType" minOccurs="0"/>
 *         &lt;element name="guarantor" type="{http://www.forum-datenaustausch.ch/invoice}guarantorAddressType"/>
 *         &lt;element name="referrer" type="{http://www.forum-datenaustausch.ch/invoice}referrerAddressType" minOccurs="0"/>
 *         &lt;element name="employer" type="{http://www.forum-datenaustausch.ch/invoice}employerAddressType" minOccurs="0"/>
 *         &lt;element name="balance" type="{http://www.forum-datenaustausch.ch/invoice}balanceTPType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="payment_period" type="{http://www.w3.org/2001/XMLSchema}duration" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "payantType", propOrder = {
    "biller",
    "debitor",
    "provider",
    "insurance",
    "patient",
    "insured",
    "guarantor",
    "referrer",
    "employer",
    "balance"
})
public class PayantType {

    @XmlElement(required = true)
    protected BillerAddressType biller;
    @XmlElement(required = true)
    protected DebitorAddressType debitor;
    @XmlElement(required = true)
    protected ProviderAddressType provider;
    @XmlElement(required = true)
    protected InsuranceAddressType insurance;
    @XmlElement(required = true)
    protected PatientAddressType patient;
    protected PatientAddressType insured;
    @XmlElement(required = true)
    protected GuarantorAddressType guarantor;
    protected ReferrerAddressType referrer;
    protected EmployerAddressType employer;
    @XmlElement(required = true)
    protected BalanceTPType balance;
    @XmlAttribute(name = "payment_period")
    protected Duration paymentPeriod;

    /**
     * Ruft den Wert der biller-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BillerAddressType }
     *     
     */
    public BillerAddressType getBiller() {
        return biller;
    }

    /**
     * Legt den Wert der biller-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BillerAddressType }
     *     
     */
    public void setBiller(BillerAddressType value) {
        this.biller = value;
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
     * Ruft den Wert der provider-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProviderAddressType }
     *     
     */
    public ProviderAddressType getProvider() {
        return provider;
    }

    /**
     * Legt den Wert der provider-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProviderAddressType }
     *     
     */
    public void setProvider(ProviderAddressType value) {
        this.provider = value;
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
     * Ruft den Wert der insured-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PatientAddressType }
     *     
     */
    public PatientAddressType getInsured() {
        return insured;
    }

    /**
     * Legt den Wert der insured-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientAddressType }
     *     
     */
    public void setInsured(PatientAddressType value) {
        this.insured = value;
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
     * Ruft den Wert der referrer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReferrerAddressType }
     *     
     */
    public ReferrerAddressType getReferrer() {
        return referrer;
    }

    /**
     * Legt den Wert der referrer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferrerAddressType }
     *     
     */
    public void setReferrer(ReferrerAddressType value) {
        this.referrer = value;
    }

    /**
     * Ruft den Wert der employer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EmployerAddressType }
     *     
     */
    public EmployerAddressType getEmployer() {
        return employer;
    }

    /**
     * Legt den Wert der employer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EmployerAddressType }
     *     
     */
    public void setEmployer(EmployerAddressType value) {
        this.employer = value;
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
     * Ruft den Wert der paymentPeriod-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Duration }
     *     
     */
    public Duration getPaymentPeriod() {
        return paymentPeriod;
    }

    /**
     * Legt den Wert der paymentPeriod-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Duration }
     *     
     */
    public void setPaymentPeriod(Duration value) {
        this.paymentPeriod = value;
    }

}
