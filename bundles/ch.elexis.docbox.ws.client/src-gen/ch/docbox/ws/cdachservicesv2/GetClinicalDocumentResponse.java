
package ch.docbox.ws.cdachservicesv2;

import org.apache.commons.lang3.StringUtils;
import org.hl7.v3.ClinicalDocumentType;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="document" type="{urn:hl7-org:v3}ClinicalDocumentType"/>
 *         &lt;element name="attachment" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = {
    "document",
    "attachment"
})
@XmlRootElement(name = "getClinicalDocumentResponse")
public class GetClinicalDocumentResponse {

    @XmlElement(required = true)
    protected ClinicalDocumentType document;
    protected byte[] attachment;

    /**
     * Ruft den Wert der document-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ClinicalDocumentType }
     *     
     */
    public ClinicalDocumentType getDocument() {
        return document;
    }

    /**
     * Legt den Wert der document-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ClinicalDocumentType }
     *     
     */
    public void setDocument(ClinicalDocumentType value) {
        this.document = value;
    }

    /**
     * Ruft den Wert der attachment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getAttachment() {
        return attachment;
    }

    /**
     * Legt den Wert der attachment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setAttachment(byte[] value) {
        this.attachment = value;
    }

}
