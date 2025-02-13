//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.QuantityType;


/**
 * <p>Java-Klasse für InventoryStatusQuantitySpecificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="InventoryStatusQuantitySpecificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="inventoryStatusType" type="{urn:gs1:ecom:ecom_common:xsd:3}InventoryStatusCodeType"/&gt;
 *         &lt;element name="quantityOfUnits" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType"/&gt;
 *         &lt;element name="actualProcessedQuantity" type="{urn:gs1:shared:shared_common:xsd:3}QuantityType" minOccurs="0"/&gt;
 *         &lt;element name="transactionalItemData" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalItemDataType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventoryStatusQuantitySpecificationType", propOrder = {
    "inventoryStatusType",
    "quantityOfUnits",
    "actualProcessedQuantity",
    "transactionalItemData"
})
public class InventoryStatusQuantitySpecificationType {

    @XmlElement(required = true)
    protected InventoryStatusCodeType inventoryStatusType;
    @XmlElement(required = true)
    protected QuantityType quantityOfUnits;
    protected QuantityType actualProcessedQuantity;
    protected List<TransactionalItemDataType> transactionalItemData;

    /**
     * Ruft den Wert der inventoryStatusType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InventoryStatusCodeType }
     *     
     */
    public InventoryStatusCodeType getInventoryStatusType() {
        return inventoryStatusType;
    }

    /**
     * Legt den Wert der inventoryStatusType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InventoryStatusCodeType }
     *     
     */
    public void setInventoryStatusType(InventoryStatusCodeType value) {
        this.inventoryStatusType = value;
    }

    /**
     * Ruft den Wert der quantityOfUnits-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getQuantityOfUnits() {
        return quantityOfUnits;
    }

    /**
     * Legt den Wert der quantityOfUnits-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setQuantityOfUnits(QuantityType value) {
        this.quantityOfUnits = value;
    }

    /**
     * Ruft den Wert der actualProcessedQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link QuantityType }
     *     
     */
    public QuantityType getActualProcessedQuantity() {
        return actualProcessedQuantity;
    }

    /**
     * Legt den Wert der actualProcessedQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link QuantityType }
     *     
     */
    public void setActualProcessedQuantity(QuantityType value) {
        this.actualProcessedQuantity = value;
    }

    /**
     * Gets the value of the transactionalItemData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transactionalItemData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransactionalItemData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionalItemDataType }
     * 
     * 
     */
    public List<TransactionalItemDataType> getTransactionalItemData() {
        if (transactionalItemData == null) {
            transactionalItemData = new ArrayList<TransactionalItemDataType>();
        }
        return this.transactionalItemData;
    }

}
