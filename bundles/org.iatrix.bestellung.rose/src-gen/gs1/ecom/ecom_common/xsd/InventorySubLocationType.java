//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import gs1.shared.shared_common.xsd.AdditionalPartyIdentificationType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für InventorySubLocationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="InventorySubLocationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="gln" type="{urn:gs1:shared:shared_common:xsd:3}GLNType" minOccurs="0"/&gt;
 *         &lt;element name="glnExtension" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="additionalPartyIdentification" type="{urn:gs1:shared:shared_common:xsd:3}AdditionalPartyIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="inventorySubLocationTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}InventorySubLocationTypeCodeType" minOccurs="0"/&gt;
 *         &lt;element name="inventorySubLocationFunctionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}InventorySubLocationFunctionCodeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InventorySubLocationType", propOrder = {
    "gln",
    "glnExtension",
    "additionalPartyIdentification",
    "inventorySubLocationTypeCode",
    "inventorySubLocationFunctionCode"
})
public class InventorySubLocationType {

    protected String gln;
    protected String glnExtension;
    protected List<AdditionalPartyIdentificationType> additionalPartyIdentification;
    protected InventorySubLocationTypeCodeType inventorySubLocationTypeCode;
    protected List<InventorySubLocationFunctionCodeType> inventorySubLocationFunctionCode;

    /**
     * Ruft den Wert der gln-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGln() {
        return gln;
    }

    /**
     * Legt den Wert der gln-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGln(String value) {
        this.gln = value;
    }

    /**
     * Ruft den Wert der glnExtension-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlnExtension() {
        return glnExtension;
    }

    /**
     * Legt den Wert der glnExtension-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlnExtension(String value) {
        this.glnExtension = value;
    }

    /**
     * Gets the value of the additionalPartyIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the additionalPartyIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalPartyIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AdditionalPartyIdentificationType }
     * 
     * 
     */
    public List<AdditionalPartyIdentificationType> getAdditionalPartyIdentification() {
        if (additionalPartyIdentification == null) {
            additionalPartyIdentification = new ArrayList<AdditionalPartyIdentificationType>();
        }
        return this.additionalPartyIdentification;
    }

    /**
     * Ruft den Wert der inventorySubLocationTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link InventorySubLocationTypeCodeType }
     *     
     */
    public InventorySubLocationTypeCodeType getInventorySubLocationTypeCode() {
        return inventorySubLocationTypeCode;
    }

    /**
     * Legt den Wert der inventorySubLocationTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link InventorySubLocationTypeCodeType }
     *     
     */
    public void setInventorySubLocationTypeCode(InventorySubLocationTypeCodeType value) {
        this.inventorySubLocationTypeCode = value;
    }

    /**
     * Gets the value of the inventorySubLocationFunctionCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the inventorySubLocationFunctionCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInventorySubLocationFunctionCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InventorySubLocationFunctionCodeType }
     * 
     * 
     */
    public List<InventorySubLocationFunctionCodeType> getInventorySubLocationFunctionCode() {
        if (inventorySubLocationFunctionCode == null) {
            inventorySubLocationFunctionCode = new ArrayList<InventorySubLocationFunctionCodeType>();
        }
        return this.inventorySubLocationFunctionCode;
    }

}
