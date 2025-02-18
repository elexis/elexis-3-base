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
 * <p>Java-Klasse für reimbursementType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="reimbursementType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="debitor" type="{http://www.forum-datenaustausch.ch/invoice}esrAddressType"/>
 *         &lt;element name="balance" type="{http://www.forum-datenaustausch.ch/invoice}balanceType"/>
 *         &lt;choice>
 *           &lt;element name="esr9" type="{http://www.forum-datenaustausch.ch/invoice}esr9Type"/>
 *           &lt;element name="esrRed" type="{http://www.forum-datenaustausch.ch/invoice}esrRedType"/>
 *           &lt;element name="esrQR" type="{http://www.forum-datenaustausch.ch/invoice}esrQRType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reimbursementType", propOrder = {
    "debitor",
    "balance",
    "esr9",
    "esrRed",
    "esrQR"
})
public class ReimbursementType {

    @XmlElement(required = true)
    protected EsrAddressType debitor;
    @XmlElement(required = true)
    protected BalanceType balance;
    protected Esr9Type esr9;
    protected EsrRedType esrRed;
    protected EsrQRType esrQR;

    /**
     * Ruft den Wert der debitor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrAddressType }
     *     
     */
    public EsrAddressType getDebitor() {
        return debitor;
    }

    /**
     * Legt den Wert der debitor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrAddressType }
     *     
     */
    public void setDebitor(EsrAddressType value) {
        this.debitor = value;
    }

    /**
     * Ruft den Wert der balance-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BalanceType }
     *     
     */
    public BalanceType getBalance() {
        return balance;
    }

    /**
     * Legt den Wert der balance-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BalanceType }
     *     
     */
    public void setBalance(BalanceType value) {
        this.balance = value;
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

}
