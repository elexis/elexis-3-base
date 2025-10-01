//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für vatType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="vatType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="vat_rate" type="{http://www.forum-datenaustausch.ch/invoice}vatRateType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="vat_number" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_15" /&gt;
 *       &lt;attribute name="vat" use="required" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vatType", propOrder = {
    "vatRate"
})
public class VatType {

    @XmlElement(name = "vat_rate", required = true)
    protected List<VatRateType> vatRate;
    @XmlAttribute(name = "vat_number")
    protected String vatNumber;
    @XmlAttribute(name = "vat", required = true)
    protected double vat;

    /**
     * Gets the value of the vatRate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the vatRate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVatRate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VatRateType }
     * 
     * 
     */
    public List<VatRateType> getVatRate() {
        if (vatRate == null) {
            vatRate = new ArrayList<VatRateType>();
        }
        return this.vatRate;
    }

    /**
     * Ruft den Wert der vatNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVatNumber() {
        return vatNumber;
    }

    /**
     * Legt den Wert der vatNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVatNumber(String value) {
        this.vatNumber = value;
    }

    /**
     * Ruft den Wert der vat-Eigenschaft ab.
     * 
     */
    public double getVat() {
        return vat;
    }

    /**
     * Legt den Wert der vat-Eigenschaft fest.
     * 
     */
    public void setVat(double value) {
        this.vat = value;
    }

}
