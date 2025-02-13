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

import gs1.shared.shared_common.xsd.QuantityType;


/**
 * <p>Java-Klasse für QuantitySpecificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="QuantitySpecificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="quantitySpecificationType" type="{urn:gs1:ecom:ecom_common:xsd:3}QuantitySpecificationTypeCodeType"/&gt;
 *         &lt;element name="specificQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType"/&gt;
 *         &lt;element name="transactionalItemData" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemDataType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuantitySpecificationType", propOrder = {
    "quantitySpecificationType",
    "specificQuantity",
    "transactionalItemData"
})
public class QuantitySpecificationType {

    @XmlElement(required = true)
    protected QuantitySpecificationTypeCodeType quantitySpecificationType;
    @XmlElement(required = true)
    protected QuantityType specificQuantity;
    protected TransactionalItemDataType transactionalItemData;

    /**
     * Ruft den Wert der quantitySpecificationType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantitySpecificationTypeCodeType }
     *     
     */
    public QuantitySpecificationTypeCodeType getQuantitySpecificationType() {
        return quantitySpecificationType;
    }

    /**
     * Legt den Wert der quantitySpecificationType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantitySpecificationTypeCodeType }
     *     
     */
    public void setQuantitySpecificationType(QuantitySpecificationTypeCodeType value) {
        this.quantitySpecificationType = value;
    }

    /**
     * Ruft den Wert der specificQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getSpecificQuantity() {
        return specificQuantity;
    }

    /**
     * Legt den Wert der specificQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setSpecificQuantity(QuantityType value) {
        this.specificQuantity = value;
    }

    /**
     * Ruft den Wert der transactionalItemData-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalItemDataType }
     *     
     */
    public TransactionalItemDataType getTransactionalItemData() {
        return transactionalItemData;
    }

    /**
     * Legt den Wert der transactionalItemData-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalItemDataType }
     *     
     */
    public void setTransactionalItemData(TransactionalItemDataType value) {
        this.transactionalItemData = value;
    }

}
