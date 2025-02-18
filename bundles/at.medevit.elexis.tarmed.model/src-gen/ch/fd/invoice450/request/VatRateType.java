//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import at.medevit.elexis.tarmed.model.jaxb.DoubleToStringAdapter;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für vatRateType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="vatRateType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="vat_rate" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="100"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="vat" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "vatRateType")
public class VatRateType {

    @XmlAttribute(name = "vat_rate", required = true)
    protected double vatRate;
    @XmlAttribute(name = "amount", required = true)
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
	protected Double amount;
    @XmlAttribute(name = "vat", required = true)
	@XmlJavaTypeAdapter(DoubleToStringAdapter.class)
	protected Double vat;

    /**
     * Ruft den Wert der vatRate-Eigenschaft ab.
     * 
     */
    public double getVatRate() {
        return vatRate;
    }

    /**
     * Legt den Wert der vatRate-Eigenschaft fest.
     * 
     */
    public void setVatRate(double value) {
        this.vatRate = value;
    }

    /**
     * Ruft den Wert der amount-Eigenschaft ab.
     * 
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Legt den Wert der amount-Eigenschaft fest.
     * 
     */
    public void setAmount(double value) {
        this.amount = value;
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
