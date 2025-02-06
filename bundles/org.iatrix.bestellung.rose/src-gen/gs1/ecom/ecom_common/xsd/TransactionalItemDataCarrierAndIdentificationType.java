//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransactionalItemDataCarrierAndIdentificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalItemDataCarrierAndIdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gs1TransactionalItemIdentificationKey" type="{urn:gs1:ecom:ecom_common:xsd:3}GS1ItemIdentificationKeyCodeType" minOccurs="0"/&gt;
 *         &lt;element name="dataCarrier" type="{urn:gs1:ecom:ecom_common:xsd:3}DataCarrierTypeCodeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalItemDataCarrierAndIdentificationType", propOrder = {
    "gs1TransactionalItemIdentificationKey",
    "dataCarrier"
})
public class TransactionalItemDataCarrierAndIdentificationType {

    protected GS1ItemIdentificationKeyCodeType gs1TransactionalItemIdentificationKey;
    protected DataCarrierTypeCodeType dataCarrier;

    /**
     * Ruft den Wert der gs1TransactionalItemIdentificationKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GS1ItemIdentificationKeyCodeType }
     *     
     */
    public GS1ItemIdentificationKeyCodeType getGs1TransactionalItemIdentificationKey() {
        return gs1TransactionalItemIdentificationKey;
    }

    /**
     * Legt den Wert der gs1TransactionalItemIdentificationKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GS1ItemIdentificationKeyCodeType }
     *     
     */
    public void setGs1TransactionalItemIdentificationKey(GS1ItemIdentificationKeyCodeType value) {
        this.gs1TransactionalItemIdentificationKey = value;
    }

    /**
     * Ruft den Wert der dataCarrier-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DataCarrierTypeCodeType }
     *     
     */
    public DataCarrierTypeCodeType getDataCarrier() {
        return dataCarrier;
    }

    /**
     * Legt den Wert der dataCarrier-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DataCarrierTypeCodeType }
     *     
     */
    public void setDataCarrier(DataCarrierTypeCodeType value) {
        this.dataCarrier = value;
    }

}
