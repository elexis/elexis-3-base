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

/**
 * <p>Java-Klasse für DangerousGoodsRegulationInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DangerousGoodsRegulationInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dangerousGoodsRegulationCode" type="{urn:gs1:ecom:ecom_common:xsd:3}DangerousGoodsRegulationCodeType"/&gt;
 *         &lt;element name="dangerousGoodsRegulationName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dangerousGoodsHazardClass"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dangerousGoodsPackingGroup" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dangerousGoodsAttribute" type="{urn:gs1:ecom:ecom_common:xsd:3}DangerousGoodsAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DangerousGoodsRegulationInformationType", propOrder = {
    "dangerousGoodsRegulationCode",
    "dangerousGoodsRegulationName",
    "dangerousGoodsHazardClass",
    "dangerousGoodsPackingGroup",
    "dangerousGoodsAttribute"
})
public class DangerousGoodsRegulationInformationType {

    @XmlElement(required = true)
    protected DangerousGoodsRegulationCodeType dangerousGoodsRegulationCode;
    protected String dangerousGoodsRegulationName;
    @XmlElement(required = true)
    protected String dangerousGoodsHazardClass;
    protected String dangerousGoodsPackingGroup;
    protected List<DangerousGoodsAttributeType> dangerousGoodsAttribute;

    /**
     * Ruft den Wert der dangerousGoodsRegulationCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DangerousGoodsRegulationCodeType }
     *     
     */
    public DangerousGoodsRegulationCodeType getDangerousGoodsRegulationCode() {
        return dangerousGoodsRegulationCode;
    }

    /**
     * Legt den Wert der dangerousGoodsRegulationCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DangerousGoodsRegulationCodeType }
     *     
     */
    public void setDangerousGoodsRegulationCode(DangerousGoodsRegulationCodeType value) {
        this.dangerousGoodsRegulationCode = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsRegulationName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDangerousGoodsRegulationName() {
        return dangerousGoodsRegulationName;
    }

    /**
     * Legt den Wert der dangerousGoodsRegulationName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDangerousGoodsRegulationName(String value) {
        this.dangerousGoodsRegulationName = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsHazardClass-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDangerousGoodsHazardClass() {
        return dangerousGoodsHazardClass;
    }

    /**
     * Legt den Wert der dangerousGoodsHazardClass-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDangerousGoodsHazardClass(String value) {
        this.dangerousGoodsHazardClass = value;
    }

    /**
     * Ruft den Wert der dangerousGoodsPackingGroup-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDangerousGoodsPackingGroup() {
        return dangerousGoodsPackingGroup;
    }

    /**
     * Legt den Wert der dangerousGoodsPackingGroup-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDangerousGoodsPackingGroup(String value) {
        this.dangerousGoodsPackingGroup = value;
    }

    /**
     * Gets the value of the dangerousGoodsAttribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the dangerousGoodsAttribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDangerousGoodsAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DangerousGoodsAttributeType }
     * 
     * 
     */
    public List<DangerousGoodsAttributeType> getDangerousGoodsAttribute() {
        if (dangerousGoodsAttribute == null) {
            dangerousGoodsAttribute = new ArrayList<DangerousGoodsAttributeType>();
        }
        return this.dangerousGoodsAttribute;
    }

}
