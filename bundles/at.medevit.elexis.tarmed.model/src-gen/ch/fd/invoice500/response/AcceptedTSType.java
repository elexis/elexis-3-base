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
 * <p>Java-Klasse für acceptedTSType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="acceptedTSType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.forum-datenaustausch.ch/invoice}statusType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="explanation" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_2800" minOccurs="0"/&gt;
 *         &lt;element name="balance" type="{http://www.forum-datenaustausch.ch/invoice}balanceTSType"/&gt;
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
@XmlType(name = "acceptedTSType", propOrder = {
    "explanation",
    "balance",
    "reimbursement"
})
public class AcceptedTSType
    extends StatusType
{

    protected String explanation;
    @XmlElement(required = true)
    protected BalanceTSType balance;
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
     * Ruft den Wert der balance-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BalanceTSType }
     *     
     */
    public BalanceTSType getBalance() {
        return balance;
    }

    /**
     * Legt den Wert der balance-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BalanceTSType }
     *     
     */
    public void setBalance(BalanceTSType value) {
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
