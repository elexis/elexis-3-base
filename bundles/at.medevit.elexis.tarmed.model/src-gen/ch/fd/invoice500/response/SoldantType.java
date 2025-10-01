//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:56:51 AM CEST 
//


package ch.fd.invoice500.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für soldantType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="soldantType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="pending" type="{http://www.forum-datenaustausch.ch/invoice}pendingType"/&gt;
 *         &lt;element name="rejected" type="{http://www.forum-datenaustausch.ch/invoice}rejectedType"/&gt;
 *         &lt;element name="accepted" type="{http://www.forum-datenaustausch.ch/invoice}acceptedTSType"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "soldantType", propOrder = {
    "pending",
    "rejected",
    "accepted"
})
public class SoldantType {

    protected PendingType pending;
    protected RejectedType rejected;
    protected AcceptedTSType accepted;

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
     * Ruft den Wert der accepted-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AcceptedTSType }
     *     
     */
    public AcceptedTSType getAccepted() {
        return accepted;
    }

    /**
     * Legt den Wert der accepted-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AcceptedTSType }
     *     
     */
    public void setAccepted(AcceptedTSType value) {
        this.accepted = value;
    }

}
