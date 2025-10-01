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
 * <p>Java-Klasse für acceptedTPType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="acceptedTPType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.forum-datenaustausch.ch/invoice}statusType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="explanation" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_2800" minOccurs="0"/&gt;
 *         &lt;element name="services" type="{http://www.forum-datenaustausch.ch/invoice}servicesType" minOccurs="0"/&gt;
 *         &lt;element name="balance" type="{http://www.forum-datenaustausch.ch/invoice}balanceTPType"/&gt;
 *         &lt;element name="reimbursement" type="{http://www.forum-datenaustausch.ch/invoice}reimbursementType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "acceptedTPType", propOrder = {
    "explanation",
    "services",
    "balance",
    "reimbursement"
})
public class AcceptedTPType
    extends StatusType
{

    protected String explanation;
    protected ServicesType services;
    @XmlElement(required = true)
    protected BalanceTPType balance;
    protected ReimbursementType reimbursement;

    /**
     * Ruft den Wert der explanation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Legt den Wert der explanation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExplanation(String value) {
        this.explanation = value;
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
     * Ruft den Wert der reimbursement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReimbursementType }
     *     
     */
    public ReimbursementType getReimbursement() {
        return reimbursement;
    }

    /**
     * Legt den Wert der reimbursement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReimbursementType }
     *     
     */
    public void setReimbursement(ReimbursementType value) {
        this.reimbursement = value;
    }

}
