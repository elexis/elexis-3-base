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
 * <p>Java-Klasse für TransportReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transportReferenceTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportReferenceTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportReferenceType", propOrder = {
    "transportReferenceTypeCode"
})
public class TransportReferenceType
    extends EcomDocumentReferenceType
{

    @XmlElement(required = true)
    protected TransportReferenceTypeCodeType transportReferenceTypeCode;

    /**
     * Ruft den Wert der transportReferenceTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportReferenceTypeCodeType }
     *     
     */
    public TransportReferenceTypeCodeType getTransportReferenceTypeCode() {
        return transportReferenceTypeCode;
    }

    /**
     * Legt den Wert der transportReferenceTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportReferenceTypeCodeType }
     *     
     */
    public void setTransportReferenceTypeCode(TransportReferenceTypeCodeType value) {
        this.transportReferenceTypeCode = value;
    }

}
