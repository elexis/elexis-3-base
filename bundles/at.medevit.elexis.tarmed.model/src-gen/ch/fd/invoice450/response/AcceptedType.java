//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:13:04 PM CEST 
//


package ch.fd.invoice450.response;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für acceptedType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="acceptedType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.forum-datenaustausch.ch/invoice}statusType">
 *       &lt;sequence>
 *         &lt;element name="explanation" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_700" minOccurs="0"/>
 *         &lt;element name="reimbursement" type="{http://www.forum-datenaustausch.ch/invoice}reimbursementType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "acceptedType", propOrder = {
    "explanation",
    "reimbursement"
})
public class AcceptedType
    extends StatusType
{

    protected String explanation;
    protected ReimbursementType reimbursement;

    /**
     * Ruft den Wert der explanation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Legt den Wert der explanation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExplanation(String value) {
        this.explanation = value;
    }

    /**
     * Ruft den Wert der reimbursement-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ReimbursementType }
     *     
     */
    public ReimbursementType getReimbursement() {
        return reimbursement;
    }

    /**
     * Legt den Wert der reimbursement-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ReimbursementType }
     *     
     */
    public void setReimbursement(ReimbursementType value) {
        this.reimbursement = value;
    }

}
