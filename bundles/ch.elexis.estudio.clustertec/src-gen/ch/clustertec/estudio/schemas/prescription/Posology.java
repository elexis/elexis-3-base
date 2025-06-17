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
 *       &lt;attribute name="qtyMorning" type="{http://estudio.clustertec.ch/schemas/prescription}positiveInteger3" /&gt;
 *       &lt;attribute name="qtyMidday" type="{http://estudio.clustertec.ch/schemas/prescription}positiveInteger3" /&gt;
 *       &lt;attribute name="qtyEvening" type="{http://estudio.clustertec.ch/schemas/prescription}positiveInteger3" /&gt;
 *       &lt;attribute name="qtyNight" type="{http://estudio.clustertec.ch/schemas/prescription}positiveInteger3" /&gt;
 *       &lt;attribute name="qtyMorningString" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="qtyMiddayString" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="qtyEveningString" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="qtyNightString" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="posologyText" type="{http://estudio.clustertec.ch/schemas/prescription}string80" /&gt;
 *       &lt;attribute name="label" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "posology")
public class Posology {

    @XmlAttribute(name = "qtyMorning")
    protected Integer qtyMorning;
    @XmlAttribute(name = "qtyMidday")
    protected Integer qtyMidday;
    @XmlAttribute(name = "qtyEvening")
    protected Integer qtyEvening;
    @XmlAttribute(name = "qtyNight")
    protected Integer qtyNight;
    @XmlAttribute(name = "qtyMorningString")
    protected String qtyMorningString;
    @XmlAttribute(name = "qtyMiddayString")
    protected String qtyMiddayString;
    @XmlAttribute(name = "qtyEveningString")
    protected String qtyEveningString;
    @XmlAttribute(name = "qtyNightString")
    protected String qtyNightString;
    @XmlAttribute(name = "posologyText")
    protected String posologyText;
    @XmlAttribute(name = "label")
    protected Boolean label;

    /**
     * Ruft den Wert der qtyMorning-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQtyMorning() {
        return qtyMorning;
    }

    /**
     * Legt den Wert der qtyMorning-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQtyMorning(Integer value) {
        this.qtyMorning = value;
    }

    /**
     * Ruft den Wert der qtyMidday-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQtyMidday() {
        return qtyMidday;
    }

    /**
     * Legt den Wert der qtyMidday-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQtyMidday(Integer value) {
        this.qtyMidday = value;
    }

    /**
     * Ruft den Wert der qtyEvening-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQtyEvening() {
        return qtyEvening;
    }

    /**
     * Legt den Wert der qtyEvening-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQtyEvening(Integer value) {
        this.qtyEvening = value;
    }

    /**
     * Ruft den Wert der qtyNight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getQtyNight() {
        return qtyNight;
    }

    /**
     * Legt den Wert der qtyNight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setQtyNight(Integer value) {
        this.qtyNight = value;
    }

    /**
     * Ruft den Wert der qtyMorningString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyMorningString() {
        return qtyMorningString;
    }

    /**
     * Legt den Wert der qtyMorningString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyMorningString(String value) {
        this.qtyMorningString = value;
    }

    /**
     * Ruft den Wert der qtyMiddayString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyMiddayString() {
        return qtyMiddayString;
    }

    /**
     * Legt den Wert der qtyMiddayString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyMiddayString(String value) {
        this.qtyMiddayString = value;
    }

    /**
     * Ruft den Wert der qtyEveningString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyEveningString() {
        return qtyEveningString;
    }

    /**
     * Legt den Wert der qtyEveningString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyEveningString(String value) {
        this.qtyEveningString = value;
    }

    /**
     * Ruft den Wert der qtyNightString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQtyNightString() {
        return qtyNightString;
    }

    /**
     * Legt den Wert der qtyNightString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQtyNightString(String value) {
        this.qtyNightString = value;
    }

    /**
     * Ruft den Wert der posologyText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPosologyText() {
        return posologyText;
    }

    /**
     * Legt den Wert der posologyText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPosologyText(String value) {
        this.posologyText = value;
    }

    /**
     * Ruft den Wert der label-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLabel() {
        return label;
    }

    /**
     * Legt den Wert der label-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLabel(Boolean value) {
        this.label = value;
    }

}
