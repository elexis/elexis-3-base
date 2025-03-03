//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für Ecom_EntityIdentificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Ecom_EntityIdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="entityIdentification"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="contentOwner" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ecom_EntityIdentificationType", propOrder = {
    "entityIdentification",
    "contentOwner"
})
@XmlSeeAlso({
    EcomDocumentReferenceType.class
})
public class EcomEntityIdentificationType {

    @XmlElement(required = true)
    protected String entityIdentification;
    protected EcomPartyIdentificationType contentOwner;

    /**
     * Ruft den Wert der entityIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityIdentification() {
        return entityIdentification;
    }

    /**
     * Legt den Wert der entityIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityIdentification(String value) {
        this.entityIdentification = value;
    }

    /**
     * Ruft den Wert der contentOwner-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getContentOwner() {
        return contentOwner;
    }

    /**
     * Legt den Wert der contentOwner-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setContentOwner(EcomPartyIdentificationType value) {
        this.contentOwner = value;
    }

}
