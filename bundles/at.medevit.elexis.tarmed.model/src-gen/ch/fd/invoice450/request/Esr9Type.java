//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für esr9Type complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="esr9Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="bank" type="{http://www.forum-datenaustausch.ch/invoice}esrAddressType" minOccurs="0"/>
 *         &lt;element name="creditor" type="{http://www.forum-datenaustausch.ch/invoice}esrAddressType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" default="16or27">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="16or27plus"/>
 *             &lt;enumeration value="16or27"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="participant_number" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="01-[1-9][0-9]{0,5}-[0-9]"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="reference_number" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="([0-9] [0-9]{5} [0-9]{5} [0-9]{5}|[0-9]{2} [0-9]{5} [0-9]{5} [0-9]{5} [0-9]{5} [0-9]{5})"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="coding_line" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="(01[0-9]{11}>[0-9]{16}\+ [0-9]{9}>|042>[0-9]{16}\+ [0-9]{9}>|01[0-9]{11}>[0-9]{27}\+ [0-9]{9}>|042>[0-9]{27}\+ [0-9]{9}>)"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "esr9Type", propOrder = {
    "bank",
    "creditor"
})
public class Esr9Type {

    protected EsrAddressType bank;
    @XmlElement(required = true)
    protected EsrAddressType creditor;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "participant_number", required = true)
    protected String participantNumber;
    @XmlAttribute(name = "reference_number", required = true)
    protected String referenceNumber;
    @XmlAttribute(name = "coding_line", required = true)
    protected String codingLine;

    /**
     * Ruft den Wert der bank-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrAddressType }
     *     
     */
    public EsrAddressType getBank() {
        return bank;
    }

    /**
     * Legt den Wert der bank-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrAddressType }
     *     
     */
    public void setBank(EsrAddressType value) {
        this.bank = value;
    }

    /**
     * Ruft den Wert der creditor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EsrAddressType }
     *     
     */
    public EsrAddressType getCreditor() {
        return creditor;
    }

    /**
     * Legt den Wert der creditor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EsrAddressType }
     *     
     */
    public void setCreditor(EsrAddressType value) {
        this.creditor = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "16or27";
        } else {
            return type;
        }
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der participantNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParticipantNumber() {
        return participantNumber;
    }

    /**
     * Legt den Wert der participantNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParticipantNumber(String value) {
        this.participantNumber = value;
    }

    /**
     * Ruft den Wert der referenceNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Legt den Wert der referenceNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferenceNumber(String value) {
        this.referenceNumber = value;
    }

    /**
     * Ruft den Wert der codingLine-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodingLine() {
        return codingLine;
    }

    /**
     * Legt den Wert der codingLine-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodingLine(String value) {
        this.codingLine = value;
    }

}
