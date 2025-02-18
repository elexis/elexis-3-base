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
 * <p>Java-Klasse für TransactionalReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transactionalReferenceTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalReferenceTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalReferenceType", propOrder = {
    "transactionalReferenceTypeCode"
})
public class TransactionalReferenceType
    extends EcomDocumentReferenceType
{

    @XmlElement(required = true)
    protected TransactionalReferenceTypeCodeType transactionalReferenceTypeCode;

    /**
     * Ruft den Wert der transactionalReferenceTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalReferenceTypeCodeType }
     *     
     */
    public TransactionalReferenceTypeCodeType getTransactionalReferenceTypeCode() {
        return transactionalReferenceTypeCode;
    }

    /**
     * Legt den Wert der transactionalReferenceTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalReferenceTypeCodeType }
     *     
     */
    public void setTransactionalReferenceTypeCode(TransactionalReferenceTypeCodeType value) {
        this.transactionalReferenceTypeCode = value;
    }

}
