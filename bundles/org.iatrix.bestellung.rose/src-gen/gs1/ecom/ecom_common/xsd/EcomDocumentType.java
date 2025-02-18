//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.ecom.order.xsd.OrderType;
import gs1.shared.shared_common.xsd.DocumentType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für EcomDocumentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EcomDocumentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:shared:shared_common:xsd:3}DocumentType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="avpList" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_AttributeValuePairListType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EcomDocumentType", propOrder = {
    "avpList"
})
@XmlSeeAlso({
    OrderType.class
})
public class EcomDocumentType
    extends DocumentType
{

    protected EcomAttributeValuePairListType avpList;

    /**
     * Ruft den Wert der avpList-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomAttributeValuePairListType }
     *     
     */
    public EcomAttributeValuePairListType getAvpList() {
        return avpList;
    }

    /**
     * Legt den Wert der avpList-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomAttributeValuePairListType }
     *     
     */
    public void setAvpList(EcomAttributeValuePairListType value) {
        this.avpList = value;
    }

}
