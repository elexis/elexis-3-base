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
 * <p>Java-Klasse für processingType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="processingType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transport" type="{http://www.forum-datenaustausch.ch/invoice}transportType"/&gt;
 *         &lt;element name="instructions" type="{http://www.forum-datenaustausch.ch/invoice}instructionsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="print_copy_to_guarantor" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="print_patient_invoice_only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="send_copy_to_trustcenter" type="{http://www.forum-datenaustausch.ch/invoice}glnPartyType" /&gt;
 *       &lt;attribute name="define_gln_printcenter" type="{http://www.forum-datenaustausch.ch/invoice}glnPartyType" fixed="2005555555003" /&gt;
 *       &lt;attribute name="define_gln_norecipient" type="{http://www.forum-datenaustausch.ch/invoice}glnPartyType" fixed="2006666666008" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "processingType", propOrder = {
    "transport",
    "instructions"
})
public class ProcessingType {

    @XmlElement(required = true)
    protected TransportType transport;
    protected InstructionsType instructions;
    @XmlAttribute(name = "print_copy_to_guarantor")
    protected Boolean printCopyToGuarantor;
    @XmlAttribute(name = "print_patient_invoice_only")
    protected Boolean printPatientInvoiceOnly;
    @XmlAttribute(name = "send_copy_to_trustcenter")
    protected String sendCopyToTrustcenter;
    @XmlAttribute(name = "define_gln_printcenter")
    protected String defineGlnPrintcenter;
    @XmlAttribute(name = "define_gln_norecipient")
    protected String defineGlnNorecipient;

    /**
     * Ruft den Wert der transport-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportType }
     *     
     */
    public TransportType getTransport() {
        return transport;
    }

    /**
     * Legt den Wert der transport-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportType }
     *     
     */
    public void setTransport(TransportType value) {
        this.transport = value;
    }

    /**
     * Ruft den Wert der instructions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InstructionsType }
     *     
     */
    public InstructionsType getInstructions() {
        return instructions;
    }

    /**
     * Legt den Wert der instructions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InstructionsType }
     *     
     */
    public void setInstructions(InstructionsType value) {
        this.instructions = value;
    }

    /**
     * Ruft den Wert der printCopyToGuarantor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPrintCopyToGuarantor() {
        if (printCopyToGuarantor == null) {
            return false;
        } else {
            return printCopyToGuarantor;
        }
    }

    /**
     * Legt den Wert der printCopyToGuarantor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrintCopyToGuarantor(Boolean value) {
        this.printCopyToGuarantor = value;
    }

    /**
     * Ruft den Wert der printPatientInvoiceOnly-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isPrintPatientInvoiceOnly() {
        if (printPatientInvoiceOnly == null) {
            return false;
        } else {
            return printPatientInvoiceOnly;
        }
    }

    /**
     * Legt den Wert der printPatientInvoiceOnly-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setPrintPatientInvoiceOnly(Boolean value) {
        this.printPatientInvoiceOnly = value;
    }

    /**
     * Ruft den Wert der sendCopyToTrustcenter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSendCopyToTrustcenter() {
        return sendCopyToTrustcenter;
    }

    /**
     * Legt den Wert der sendCopyToTrustcenter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendCopyToTrustcenter(String value) {
        this.sendCopyToTrustcenter = value;
    }

    /**
     * Ruft den Wert der defineGlnPrintcenter-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefineGlnPrintcenter() {
        if (defineGlnPrintcenter == null) {
            return "2005555555003";
        } else {
            return defineGlnPrintcenter;
        }
    }

    /**
     * Legt den Wert der defineGlnPrintcenter-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefineGlnPrintcenter(String value) {
        this.defineGlnPrintcenter = value;
    }

    /**
     * Ruft den Wert der defineGlnNorecipient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefineGlnNorecipient() {
        if (defineGlnNorecipient == null) {
            return "2006666666008";
        } else {
            return defineGlnNorecipient;
        }
    }

    /**
     * Legt den Wert der defineGlnNorecipient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefineGlnNorecipient(String value) {
        this.defineGlnNorecipient = value;
    }

}
