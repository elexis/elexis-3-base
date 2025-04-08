//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.08 um 02:13:03 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="pharmacode" use="required" type="{http://estudio.clustertec.ch/schemas/order}pharmaCode" /&gt;
 *       &lt;attribute name="eanId" type="{http://estudio.clustertec.ch/schemas/order}eanId" /&gt;
 *       &lt;attribute name="description" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="quantity" use="required" type="{http://estudio.clustertec.ch/schemas/order}positiveInteger3" /&gt;
 *       &lt;attribute name="positionType" use="required" type="{http://estudio.clustertec.ch/schemas/order}positionType" /&gt;
 *       &lt;attribute name="changedByFirstName" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="changedByLastName" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="changedByClientNr" type="{http://estudio.clustertec.ch/schemas/order}string6" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "product")
public class Product {

    @XmlAttribute(name = "pharmacode", required = true)
    protected String pharmacode;
    @XmlAttribute(name = "eanId")
    protected String eanId;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "quantity", required = true)
    protected int quantity;
    @XmlAttribute(name = "positionType", required = true)
    protected int positionType;
    @XmlAttribute(name = "changedByFirstName")
    protected String changedByFirstName;
    @XmlAttribute(name = "changedByLastName")
    protected String changedByLastName;
    @XmlAttribute(name = "changedByClientNr")
    protected String changedByClientNr;

    /**
     * Ruft den Wert der pharmacode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPharmacode() {
        return pharmacode;
    }

    /**
     * Legt den Wert der pharmacode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPharmacode(String value) {
        this.pharmacode = value;
    }

    /**
     * Ruft den Wert der eanId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEanId() {
        return eanId;
    }

    /**
     * Legt den Wert der eanId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEanId(String value) {
        this.eanId = value;
    }

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der quantity-Eigenschaft ab.
     * 
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Legt den Wert der quantity-Eigenschaft fest.
     * 
     */
    public void setQuantity(int value) {
        this.quantity = value;
    }

    /**
     * Ruft den Wert der positionType-Eigenschaft ab.
     * 
     */
    public int getPositionType() {
        return positionType;
    }

    /**
     * Legt den Wert der positionType-Eigenschaft fest.
     * 
     */
    public void setPositionType(int value) {
        this.positionType = value;
    }

    /**
     * Ruft den Wert der changedByFirstName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangedByFirstName() {
        return changedByFirstName;
    }

    /**
     * Legt den Wert der changedByFirstName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangedByFirstName(String value) {
        this.changedByFirstName = value;
    }

    /**
     * Ruft den Wert der changedByLastName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangedByLastName() {
        return changedByLastName;
    }

    /**
     * Legt den Wert der changedByLastName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangedByLastName(String value) {
        this.changedByLastName = value;
    }

    /**
     * Ruft den Wert der changedByClientNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChangedByClientNr() {
        return changedByClientNr;
    }

    /**
     * Legt den Wert der changedByClientNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChangedByClientNr(String value) {
        this.changedByClientNr = value;
    }

}
