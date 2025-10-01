//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für billersAddressType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="billersAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="biller_gln" type="{http://www.forum-datenaustausch.ch/invoice}billerGLNAddressType"/&gt;
 *         &lt;element name="biller_zsr" type="{http://www.forum-datenaustausch.ch/invoice}zsrAddressType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "billersAddressType", propOrder = {
    "billerGln",
    "billerZsr"
})
public class BillersAddressType {

    @XmlElement(name = "biller_gln", required = true)
    protected BillerGLNAddressType billerGln;
    @XmlElement(name = "biller_zsr")
    protected ZsrAddressType billerZsr;

    /**
     * Ruft den Wert der billerGln-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BillerGLNAddressType }
     *     
     */
    public BillerGLNAddressType getBillerGln() {
        return billerGln;
    }

    /**
     * Legt den Wert der billerGln-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BillerGLNAddressType }
     *     
     */
    public void setBillerGln(BillerGLNAddressType value) {
        this.billerGln = value;
    }

    /**
     * Ruft den Wert der billerZsr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ZsrAddressType }
     *     
     */
    public ZsrAddressType getBillerZsr() {
        return billerZsr;
    }

    /**
     * Legt den Wert der billerZsr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ZsrAddressType }
     *     
     */
    public void setBillerZsr(ZsrAddressType value) {
        this.billerZsr = value;
    }

}
