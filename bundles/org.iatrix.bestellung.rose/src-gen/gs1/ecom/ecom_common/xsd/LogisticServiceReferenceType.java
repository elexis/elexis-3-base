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
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für LogisticServiceReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LogisticServiceReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="logisticServiceReferenceTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticServiceReferenceTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogisticServiceReferenceType", propOrder = {
    "logisticServiceReferenceTypeCode"
})
public class LogisticServiceReferenceType
    extends EcomDocumentReferenceType
{

    @XmlElement(required = true)
    protected LogisticServiceReferenceTypeCodeType logisticServiceReferenceTypeCode;

    /**
     * Ruft den Wert der logisticServiceReferenceTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LogisticServiceReferenceTypeCodeType }
     *     
     */
    public LogisticServiceReferenceTypeCodeType getLogisticServiceReferenceTypeCode() {
        return logisticServiceReferenceTypeCode;
    }

    /**
     * Legt den Wert der logisticServiceReferenceTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LogisticServiceReferenceTypeCodeType }
     *     
     */
    public void setLogisticServiceReferenceTypeCode(LogisticServiceReferenceTypeCodeType value) {
        this.logisticServiceReferenceTypeCode = value;
    }

}
