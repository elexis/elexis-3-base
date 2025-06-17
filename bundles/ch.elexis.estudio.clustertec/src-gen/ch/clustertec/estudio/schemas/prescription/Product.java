//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
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
 *       &lt;all&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}posology" minOccurs="0"/&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}insurance"/&gt;
 *       &lt;/all&gt;
 *       &lt;attribute name="pharmacode" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}pharmaCode" /&gt;
 *       &lt;attribute name="eanId" type="{http://estudio.clustertec.ch/schemas/prescription}eanId" /&gt;
 *       &lt;attribute name="description" type="{http://estudio.clustertec.ch/schemas/prescription}string50" /&gt;
 *       &lt;attribute name="repetition" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="nrOfRepetitions" type="{http://estudio.clustertec.ch/schemas/prescription}positiveInteger2" /&gt;
 *       &lt;attribute name="quantity" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}positiveInteger3" /&gt;
 *       &lt;attribute name="validityRepetition" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "product")
public class Product {

    protected Posology posology;
    @XmlElement(required = true)
    protected Insurance insurance;
    @XmlAttribute(name = "pharmacode", required = true)
    protected String pharmacode;
    @XmlAttribute(name = "eanId")
    protected Long eanId;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "repetition", required = true)
    protected boolean repetition;
    @XmlAttribute(name = "nrOfRepetitions")
    protected Integer nrOfRepetitions;
    @XmlAttribute(name = "quantity", required = true)
    protected int quantity;
    @XmlAttribute(name = "validityRepetition")
    protected String validityRepetition;

    /**
     * Ruft den Wert der posology-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Posology }
     *     
     */
    public Posology getPosology() {
        return posology;
    }

    /**
     * Legt den Wert der posology-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Posology }
     *     
     */
    public void setPosology(Posology value) {
        this.posology = value;
    }

    /**
     * Ruft den Wert der insurance-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Insurance }
     *     
     */
    public Insurance getInsurance() {
        return insurance;
    }

    /**
     * Legt den Wert der insurance-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Insurance }
     *     
     */
    public void setInsurance(Insurance value) {
        this.insurance = value;
    }

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
     *     {@link Long }
     *     
     */
    public Long getEanId() {
        return eanId;
    }

    /**
     * Legt den Wert der eanId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEanId(Long value) {
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
     * Ruft den Wert der repetition-Eigenschaft ab.
     * 
     */
    public boolean isRepetition() {
        return repetition;
    }

    /**
     * Legt den Wert der repetition-Eigenschaft fest.
     * 
     */
    public void setRepetition(boolean value) {
        this.repetition = value;
    }

    /**
     * Ruft den Wert der nrOfRepetitions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNrOfRepetitions() {
        return nrOfRepetitions;
    }

    /**
     * Legt den Wert der nrOfRepetitions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNrOfRepetitions(Integer value) {
        this.nrOfRepetitions = value;
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
     * Ruft den Wert der validityRepetition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidityRepetition() {
        return validityRepetition;
    }

    /**
     * Legt den Wert der validityRepetition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidityRepetition(String value) {
        this.validityRepetition = value;
    }

}
