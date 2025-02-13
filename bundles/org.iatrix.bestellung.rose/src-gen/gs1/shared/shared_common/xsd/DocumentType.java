//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

import gs1.ecom.ecom_common.xsd.EcomDocumentType;


/**
 * <p>Java-Klasse für DocumentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="creationDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="documentStatusCode" type="{urn:gs1:shared:shared_common:xsd:3}DocumentStatusEnumerationType"/&gt;
 *         &lt;element name="documentActionCode" type="{urn:gs1:shared:shared_common:xsd:3}DocumentActionEnumerationType" minOccurs="0"/&gt;
 *         &lt;element name="documentStructureVersion" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="lastUpdateDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="revisionNumber" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/&gt;
 *         &lt;element name="extension" type="{urn:gs1:shared:shared_common:xsd:3}ExtensionType" minOccurs="0"/&gt;
 *         &lt;element name="documentEffectiveDate" type="{urn:gs1:shared:shared_common:xsd:3}DateOptionalTimeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentType", propOrder = {
    "creationDateTime",
    "documentStatusCode",
    "documentActionCode",
    "documentStructureVersion",
    "lastUpdateDateTime",
    "revisionNumber",
    "extension",
    "documentEffectiveDate"
})
@XmlSeeAlso({
    EcomDocumentType.class
})
public abstract class DocumentType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationDateTime;
    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected DocumentStatusEnumerationType documentStatusCode;
    @XmlSchemaType(name = "string")
    protected DocumentActionEnumerationType documentActionCode;
    protected String documentStructureVersion;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdateDateTime;
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger revisionNumber;
    protected ExtensionType extension;
    protected DateOptionalTimeType documentEffectiveDate;

    /**
     * Ruft den Wert der creationDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCreationDateTime() {
        return creationDateTime;
    }

    /**
     * Legt den Wert der creationDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCreationDateTime(XMLGregorianCalendar value) {
        this.creationDateTime = value;
    }

    /**
     * Ruft den Wert der documentStatusCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DocumentStatusEnumerationType }
     *     
     */
    public DocumentStatusEnumerationType getDocumentStatusCode() {
        return documentStatusCode;
    }

    /**
     * Legt den Wert der documentStatusCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentStatusEnumerationType }
     *     
     */
    public void setDocumentStatusCode(DocumentStatusEnumerationType value) {
        this.documentStatusCode = value;
    }

    /**
     * Ruft den Wert der documentActionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DocumentActionEnumerationType }
     *     
     */
    public DocumentActionEnumerationType getDocumentActionCode() {
        return documentActionCode;
    }

    /**
     * Legt den Wert der documentActionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DocumentActionEnumerationType }
     *     
     */
    public void setDocumentActionCode(DocumentActionEnumerationType value) {
        this.documentActionCode = value;
    }

    /**
     * Ruft den Wert der documentStructureVersion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentStructureVersion() {
        return documentStructureVersion;
    }

    /**
     * Legt den Wert der documentStructureVersion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentStructureVersion(String value) {
        this.documentStructureVersion = value;
    }

    /**
     * Ruft den Wert der lastUpdateDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    /**
     * Legt den Wert der lastUpdateDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastUpdateDateTime(XMLGregorianCalendar value) {
        this.lastUpdateDateTime = value;
    }

    /**
     * Ruft den Wert der revisionNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRevisionNumber() {
        return revisionNumber;
    }

    /**
     * Legt den Wert der revisionNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRevisionNumber(BigInteger value) {
        this.revisionNumber = value;
    }

    /**
     * Ruft den Wert der extension-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ExtensionType }
     *     
     */
    public ExtensionType getExtension() {
        return extension;
    }

    /**
     * Legt den Wert der extension-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtensionType }
     *     
     */
    public void setExtension(ExtensionType value) {
        this.extension = value;
    }

    /**
     * Ruft den Wert der documentEffectiveDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public DateOptionalTimeType getDocumentEffectiveDate() {
        return documentEffectiveDate;
    }

    /**
     * Legt den Wert der documentEffectiveDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DateOptionalTimeType }
     *     
     */
    public void setDocumentEffectiveDate(DateOptionalTimeType value) {
        this.documentEffectiveDate = value;
    }

}
