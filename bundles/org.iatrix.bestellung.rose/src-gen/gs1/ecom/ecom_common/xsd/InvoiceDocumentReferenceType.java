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
 * <p>Java-Klasse für InvoiceDocumentReferenceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="InvoiceDocumentReferenceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_DocumentReferenceType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="invoiceTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}InvoiceTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InvoiceDocumentReferenceType", propOrder = {
    "invoiceTypeCode"
})
public class InvoiceDocumentReferenceType
    extends EcomDocumentReferenceType
{

    @XmlElement(required = true)
    protected InvoiceTypeCodeType invoiceTypeCode;

    /**
     * Ruft den Wert der invoiceTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InvoiceTypeCodeType }
     *     
     */
    public InvoiceTypeCodeType getInvoiceTypeCode() {
        return invoiceTypeCode;
    }

    /**
     * Legt den Wert der invoiceTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InvoiceTypeCodeType }
     *     
     */
    public void setInvoiceTypeCode(InvoiceTypeCodeType value) {
        this.invoiceTypeCode = value;
    }

}
