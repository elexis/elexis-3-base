//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:56:51 AM CEST 
//


package ch.fd.invoice500.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für reimbursementType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="reimbursementType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="debitor" type="{http://www.forum-datenaustausch.ch/invoice}esrAddressType"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="esrQR" type="{http://www.forum-datenaustausch.ch/invoice}esrQRType"/&gt;
 *           &lt;element name="esrQRRed" type="{http://www.forum-datenaustausch.ch/invoice}esrQRRedType"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="vat" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="vat_number" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_15" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reimbursementType", propOrder = {
    "debitor",
    "esrQR",
    "esrQRRed"
})
public class ReimbursementType {

    @XmlElement(required = true)
    protected EsrAddressType debitor;
    protected EsrQRType esrQR;
    protected EsrQRRedType esrQRRed;
    @XmlAttribute(name = "vat", required = true)
    protected double vat;
    @XmlAttribute(name = "vat_number")
    protected String vatNumber;

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

    /**
     * Ruft den Wert der esrQRRed-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrQRRedType }
     *     
     */
    public EsrQRRedType getEsrQRRed() {
        return esrQRRed;
    }

    /**
     * Legt den Wert der esrQRRed-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrQRRedType }
     *     
     */
    public void setEsrQRRed(EsrQRRedType value) {
        this.esrQRRed = value;
    }

    /**
     * Ruft den Wert der vat-Eigenschaft ab.
     * 
     */
    public double getVat() {
        return vat;
    }

    /**
     * Legt den Wert der vat-Eigenschaft fest.
     * 
     */
    public void setVat(double value) {
        this.vat = value;
    }

    /**
     * Ruft den Wert der vatNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVatNumber() {
        return vatNumber;
    }

    /**
     * Legt den Wert der vatNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVatNumber(String value) {
        this.vatNumber = value;
    }

}
