//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:56:51 AM CEST 
//


package ch.fd.invoice500.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für providersAddressType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="providersAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="provider_gln" type="{http://www.forum-datenaustausch.ch/invoice}providerGLNAddressType"/&gt;
 *         &lt;element name="provider_zsr" type="{http://www.forum-datenaustausch.ch/invoice}zsrAddressType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "providersAddressType", propOrder = {
    "providerGln",
    "providerZsr"
})
public class ProvidersAddressType {

    @XmlElement(name = "provider_gln", required = true)
    protected ProviderGLNAddressType providerGln;
    @XmlElement(name = "provider_zsr")
    protected ZsrAddressType providerZsr;

    /**
     * Ruft den Wert der providerGln-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProviderGLNAddressType }
     *     
     */
    public ProviderGLNAddressType getProviderGln() {
        return providerGln;
    }

    /**
     * Legt den Wert der providerGln-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProviderGLNAddressType }
     *     
     */
    public void setProviderGln(ProviderGLNAddressType value) {
        this.providerGln = value;
    }

    /**
     * Ruft den Wert der providerZsr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ZsrAddressType }
     *     
     */
    public ZsrAddressType getProviderZsr() {
        return providerZsr;
    }

    /**
     * Legt den Wert der providerZsr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ZsrAddressType }
     *     
     */
    public void setProviderZsr(ZsrAddressType value) {
        this.providerZsr = value;
    }

}
