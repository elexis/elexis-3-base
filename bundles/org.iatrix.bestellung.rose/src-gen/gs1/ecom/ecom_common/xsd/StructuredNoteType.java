//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.CodeType;
import gs1.shared.shared_common.xsd.Description500Type;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für StructuredNoteType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="StructuredNoteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="structuredNoteIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_EntityIdentificationType"/&gt;
 *         &lt;element name="noteText" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type"/&gt;
 *         &lt;element name="structuredNoteType" type="{urn:gs1:shared:shared_common:xsd:3}CodeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StructuredNoteType", propOrder = {
    "structuredNoteIdentification",
    "noteText",
    "structuredNoteType"
})
public class StructuredNoteType {

    @XmlElement(required = true)
    protected EcomEntityIdentificationType structuredNoteIdentification;
    @XmlElement(required = true)
    protected Description500Type noteText;
    protected CodeType structuredNoteType;

    /**
     * Ruft den Wert der structuredNoteIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomEntityIdentificationType }
     *     
     */
    public EcomEntityIdentificationType getStructuredNoteIdentification() {
        return structuredNoteIdentification;
    }

    /**
     * Legt den Wert der structuredNoteIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomEntityIdentificationType }
     *     
     */
    public void setStructuredNoteIdentification(EcomEntityIdentificationType value) {
        this.structuredNoteIdentification = value;
    }

    /**
     * Ruft den Wert der noteText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description500Type }
     *     
     */
    public Description500Type getNoteText() {
        return noteText;
    }

    /**
     * Legt den Wert der noteText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description500Type }
     *     
     */
    public void setNoteText(Description500Type value) {
        this.noteText = value;
    }

    /**
     * Ruft den Wert der structuredNoteType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getStructuredNoteType() {
        return structuredNoteType;
    }

    /**
     * Legt den Wert der structuredNoteType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setStructuredNoteType(CodeType value) {
        this.structuredNoteType = value;
    }

}
