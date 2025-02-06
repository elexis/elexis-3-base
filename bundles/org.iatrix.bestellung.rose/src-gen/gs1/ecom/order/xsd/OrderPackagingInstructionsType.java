//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.order.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.AmountType;
import gs1.shared.shared_common.xsd.Description1000Type;


/**
 * <p>Java-Klasse für OrderPackagingInstructionsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="OrderPackagingInstructionsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="itemPriceForLabelling" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="additionalLabelText" type="{urn:gs1:shared:shared_common:xsd:3}Description1000Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="isArticleSurvaillanceEquipmentRequired" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OrderPackagingInstructionsType", propOrder = {
    "itemPriceForLabelling",
    "additionalLabelText",
    "isArticleSurvaillanceEquipmentRequired"
})
public class OrderPackagingInstructionsType {

    protected AmountType itemPriceForLabelling;
    protected List<Description1000Type> additionalLabelText;
    protected boolean isArticleSurvaillanceEquipmentRequired;

    /**
     * Ruft den Wert der itemPriceForLabelling-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getItemPriceForLabelling() {
        return itemPriceForLabelling;
    }

    /**
     * Legt den Wert der itemPriceForLabelling-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setItemPriceForLabelling(AmountType value) {
        this.itemPriceForLabelling = value;
    }

    /**
     * Gets the value of the additionalLabelText property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalLabelText property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalLabelText().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description1000Type }
     * 
     * 
     */
    public List<Description1000Type> getAdditionalLabelText() {
        if (additionalLabelText == null) {
            additionalLabelText = new ArrayList<Description1000Type>();
        }
        return this.additionalLabelText;
    }

    /**
     * Ruft den Wert der isArticleSurvaillanceEquipmentRequired-Eigenschaft ab.
     * 
     */
    public boolean isIsArticleSurvaillanceEquipmentRequired() {
        return isArticleSurvaillanceEquipmentRequired;
    }

    /**
     * Legt den Wert der isArticleSurvaillanceEquipmentRequired-Eigenschaft fest.
     * 
     */
    public void setIsArticleSurvaillanceEquipmentRequired(boolean value) {
        this.isArticleSurvaillanceEquipmentRequired = value;
    }

}
