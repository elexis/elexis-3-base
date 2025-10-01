//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:56:51 AM CEST 
//


package ch.fd.invoice500.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für garantType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="garantType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="notification" type="{http://www.forum-datenaustausch.ch/invoice}pendingType"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="recipient" default="provider"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="provider"/&gt;
 *             &lt;enumeration value="debitor"/&gt;
 *             &lt;enumeration value="third_party"/&gt;
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
@XmlType(name = "garantType", propOrder = {
    "notification"
})
public class GarantType {

    @XmlElement(required = true)
    protected PendingType notification;
    @XmlAttribute(name = "recipient")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String recipient;

    /**
     * Ruft den Wert der notification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PendingType }
     *     
     */
    public PendingType getNotification() {
        return notification;
    }

    /**
     * Legt den Wert der notification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PendingType }
     *     
     */
    public void setNotification(PendingType value) {
        this.notification = value;
    }

    /**
     * Ruft den Wert der recipient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipient() {
        if (recipient == null) {
            return "provider";
        } else {
            return recipient;
        }
    }

    /**
     * Legt den Wert der recipient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipient(String value) {
        this.recipient = value;
    }

}
