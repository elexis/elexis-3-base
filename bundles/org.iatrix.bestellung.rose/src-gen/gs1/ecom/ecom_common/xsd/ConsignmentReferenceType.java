//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für ConsignmentReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ConsignmentReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="consignmentIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ConsignmentIdentificationType"/&gt;
 *         &lt;element name="consignor" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="consignee" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConsignmentReferenceType", propOrder = {
    "consignmentIdentification",
    "consignor",
    "consignee"
})
public class ConsignmentReferenceType {

    @XmlElement(required = true)
    protected EcomConsignmentIdentificationType consignmentIdentification;
    protected EcomPartyIdentificationType consignor;
    protected EcomPartyIdentificationType consignee;

    /**
     * Ruft den Wert der consignmentIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomConsignmentIdentificationType }
     *     
     */
    public EcomConsignmentIdentificationType getConsignmentIdentification() {
        return consignmentIdentification;
    }

    /**
     * Legt den Wert der consignmentIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomConsignmentIdentificationType }
     *     
     */
    public void setConsignmentIdentification(EcomConsignmentIdentificationType value) {
        this.consignmentIdentification = value;
    }

    /**
     * Ruft den Wert der consignor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getConsignor() {
        return consignor;
    }

    /**
     * Legt den Wert der consignor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setConsignor(EcomPartyIdentificationType value) {
        this.consignor = value;
    }

    /**
     * Ruft den Wert der consignee-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getConsignee() {
        return consignee;
    }

    /**
     * Legt den Wert der consignee-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setConsignee(EcomPartyIdentificationType value) {
        this.consignee = value;
    }

}
