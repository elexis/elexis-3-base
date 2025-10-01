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
 * &lt;complexType name="statusType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="status_in" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="unknown"/&gt;
 *             &lt;enumeration value="ambiguous"/&gt;
 *             &lt;enumeration value="received"/&gt;
 *             &lt;enumeration value="frozen"/&gt;
 *             &lt;enumeration value="processed"/&gt;
 *             &lt;enumeration value="granted"/&gt;
 *             &lt;enumeration value="canceled"/&gt;
 *             &lt;enumeration value="claimed"/&gt;
 *             &lt;enumeration value="reimbursed"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="status_out" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="unknown"/&gt;
 *             &lt;enumeration value="ambiguous"/&gt;
 *             &lt;enumeration value="received"/&gt;
 *             &lt;enumeration value="frozen"/&gt;
 *             &lt;enumeration value="processed"/&gt;
 *             &lt;enumeration value="granted"/&gt;
 *             &lt;enumeration value="canceled"/&gt;
 *             &lt;enumeration value="claimed"/&gt;
 *             &lt;enumeration value="reimbursed"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "statusType")
@XmlSeeAlso({
    PendingType.class,
    AcceptedTSType.class,
    AcceptedTPType.class,
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
