//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CipherDataType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CipherDataType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="CipherValue" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/&gt;
 *         &lt;element ref="{http://www.w3.org/2001/04/xmlenc#}CipherReference"/&gt;
 *       &lt;/choice&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CipherDataType", namespace = "http://www.w3.org/2001/04/xmlenc#", propOrder = {
    "cipherValue",
    "cipherReference"
})
public class CipherDataType {

    @XmlElement(name = "CipherValue")
    protected byte[] cipherValue;
    @XmlElement(name = "CipherReference")
    protected CipherReferenceType cipherReference;

    /**
     * Ruft den Wert der cipherValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCipherValue() {
        return cipherValue;
    }

    /**
     * Legt den Wert der cipherValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCipherValue(byte[] value) {
        this.cipherValue = value;
    }

    /**
     * Ruft den Wert der cipherReference-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CipherReferenceType }
     *     
     */
    public CipherReferenceType getCipherReference() {
        return cipherReference;
    }

    /**
     * Legt den Wert der cipherReference-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CipherReferenceType }
     *     
     */
    public void setCipherReference(CipherReferenceType value) {
        this.cipherReference = value;
    }

}
