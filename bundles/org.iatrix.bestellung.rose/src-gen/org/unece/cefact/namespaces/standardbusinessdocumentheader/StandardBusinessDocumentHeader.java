//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package org.unece.cefact.namespaces.standardbusinessdocumentheader;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für StandardBusinessDocumentHeader complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="StandardBusinessDocumentHeader"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="HeaderVersion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Sender" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner" maxOccurs="unbounded"/&gt;
 *         &lt;element name="Receiver" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Partner" maxOccurs="unbounded"/&gt;
 *         &lt;element name="DocumentIdentification" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}DocumentIdentification"/&gt;
 *         &lt;element name="Manifest" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}Manifest" minOccurs="0"/&gt;
 *         &lt;element name="BusinessScope" type="{http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader}BusinessScope" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandardBusinessDocumentHeader", propOrder = {
    "headerVersion",
    "sender",
    "receiver",
    "documentIdentification",
    "manifest",
    "businessScope"
})
public class StandardBusinessDocumentHeader {

    @XmlElement(name = "HeaderVersion", required = true)
    protected String headerVersion;
    @XmlElement(name = "Sender", required = true)
    protected List<Partner> sender;
    @XmlElement(name = "Receiver", required = true)
    protected List<Partner> receiver;
    @XmlElement(name = "DocumentIdentification", required = true)
    protected DocumentIdentification documentIdentification;
    @XmlElement(name = "Manifest")
    protected Manifest manifest;
    @XmlElement(name = "BusinessScope")
    protected BusinessScope businessScope;

    /**
     * Ruft den Wert der headerVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeaderVersion() {
        return headerVersion;
    }

    /**
     * Legt den Wert der headerVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeaderVersion(String value) {
        this.headerVersion = value;
    }

    /**
     * Gets the value of the sender property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the sender property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSender().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Partner }
     * 
     * 
     */
    public List<Partner> getSender() {
        if (sender == null) {
            sender = new ArrayList<Partner>();
        }
        return this.sender;
    }

    /**
     * Gets the value of the receiver property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the receiver property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReceiver().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Partner }
     * 
     * 
     */
    public List<Partner> getReceiver() {
        if (receiver == null) {
            receiver = new ArrayList<Partner>();
        }
        return this.receiver;
    }

    /**
     * Ruft den Wert der documentIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DocumentIdentification }
     *     
     */
    public DocumentIdentification getDocumentIdentification() {
        return documentIdentification;
    }

    /**
     * Legt den Wert der documentIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentIdentification }
     *     
     */
    public void setDocumentIdentification(DocumentIdentification value) {
        this.documentIdentification = value;
    }

    /**
     * Ruft den Wert der manifest-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Manifest }
     *     
     */
    public Manifest getManifest() {
        return manifest;
    }

    /**
     * Legt den Wert der manifest-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Manifest }
     *     
     */
    public void setManifest(Manifest value) {
        this.manifest = value;
    }

    /**
     * Ruft den Wert der businessScope-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BusinessScope }
     *     
     */
    public BusinessScope getBusinessScope() {
        return businessScope;
    }

    /**
     * Legt den Wert der businessScope-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessScope }
     *     
     */
    public void setBusinessScope(BusinessScope value) {
        this.businessScope = value;
    }

}
