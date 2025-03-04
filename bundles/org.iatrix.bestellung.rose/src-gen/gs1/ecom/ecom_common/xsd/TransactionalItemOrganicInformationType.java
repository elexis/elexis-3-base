//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransactionalItemOrganicInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalItemOrganicInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="isTradeItemOrganic" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="organicCertification" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemCertificationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalItemOrganicInformationType", propOrder = {
    "isTradeItemOrganic",
    "organicCertification"
})
public class TransactionalItemOrganicInformationType {

    protected boolean isTradeItemOrganic;
    protected TransactionalItemCertificationType organicCertification;

    /**
     * Ruft den Wert der isTradeItemOrganic-Eigenschaft ab.
     * 
     */
    public boolean isIsTradeItemOrganic() {
        return isTradeItemOrganic;
    }

    /**
     * Legt den Wert der isTradeItemOrganic-Eigenschaft fest.
     * 
     */
    public void setIsTradeItemOrganic(boolean value) {
        this.isTradeItemOrganic = value;
    }

    /**
     * Ruft den Wert der organicCertification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalItemCertificationType }
     *     
     */
    public TransactionalItemCertificationType getOrganicCertification() {
        return organicCertification;
    }

    /**
     * Legt den Wert der organicCertification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalItemCertificationType }
     *     
     */
    public void setOrganicCertification(TransactionalItemCertificationType value) {
        this.organicCertification = value;
    }

}
