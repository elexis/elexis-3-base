//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:13:04 PM CEST 
//


package ch.fd.invoice450.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für statusType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="statusType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="status_in" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="unknown"/>
 *             &lt;enumeration value="ambiguous"/>
 *             &lt;enumeration value="received"/>
 *             &lt;enumeration value="frozen"/>
 *             &lt;enumeration value="processed"/>
 *             &lt;enumeration value="granted"/>
 *             &lt;enumeration value="canceled"/>
 *             &lt;enumeration value="claimed"/>
 *             &lt;enumeration value="reimbursed"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="status_out" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="unknown"/>
 *             &lt;enumeration value="ambiguous"/>
 *             &lt;enumeration value="received"/>
 *             &lt;enumeration value="frozen"/>
 *             &lt;enumeration value="processed"/>
 *             &lt;enumeration value="granted"/>
 *             &lt;enumeration value="canceled"/>
 *             &lt;enumeration value="claimed"/>
 *             &lt;enumeration value="reimbursed"/>
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
@XmlType(name = "statusType")
@XmlSeeAlso({
    AcceptedType.class,
    PendingType.class,
    RejectedType.class
})
public class StatusType {

    @XmlAttribute(name = "status_in", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String statusIn;
    @XmlAttribute(name = "status_out", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String statusOut;

    /**
     * Ruft den Wert der statusIn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusIn() {
        return statusIn;
    }

    /**
     * Legt den Wert der statusIn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusIn(String value) {
        this.statusIn = value;
    }

    /**
     * Ruft den Wert der statusOut-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusOut() {
        return statusOut;
    }

    /**
     * Legt den Wert der statusOut-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusOut(String value) {
        this.statusOut = value;
    }

}
