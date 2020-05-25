//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für xtraDrugType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="xtraDrugType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="indicated" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="iocm_category">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="A"/>
 *             &lt;enumeration value="B"/>
 *             &lt;enumeration value="C"/>
 *             &lt;enumeration value="D"/>
 *             &lt;enumeration value="E"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="delivery" default="first">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="first"/>
 *             &lt;enumeration value="repeated"/>
 *             &lt;enumeration value="permanent"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="regulation_attributes" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" default="0" />
 *       &lt;attribute name="limitation" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xtraDrugType")
public class XtraDrugType {

    @XmlAttribute(name = "indicated")
    protected Boolean indicated;
    @XmlAttribute(name = "iocm_category")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String iocmCategory;
    @XmlAttribute(name = "delivery")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String delivery;
    @XmlAttribute(name = "regulation_attributes")
    @XmlSchemaType(name = "unsignedInt")
    protected Long regulationAttributes;
    @XmlAttribute(name = "limitation")
    protected Boolean limitation;

    /**
     * Ruft den Wert der indicated-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIndicated() {
        return indicated;
    }

    /**
     * Legt den Wert der indicated-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIndicated(Boolean value) {
        this.indicated = value;
    }

    /**
     * Ruft den Wert der iocmCategory-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIocmCategory() {
        return iocmCategory;
    }

    /**
     * Legt den Wert der iocmCategory-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIocmCategory(String value) {
        this.iocmCategory = value;
    }

    /**
     * Ruft den Wert der delivery-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDelivery() {
        if (delivery == null) {
            return "first";
        } else {
            return delivery;
        }
    }

    /**
     * Legt den Wert der delivery-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDelivery(String value) {
        this.delivery = value;
    }

    /**
     * Ruft den Wert der regulationAttributes-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public long getRegulationAttributes() {
        if (regulationAttributes == null) {
            return  0L;
        } else {
            return regulationAttributes;
        }
    }

    /**
     * Legt den Wert der regulationAttributes-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRegulationAttributes(Long value) {
        this.regulationAttributes = value;
    }

    /**
     * Ruft den Wert der limitation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLimitation() {
        return limitation;
    }

    /**
     * Legt den Wert der limitation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLimitation(Boolean value) {
        this.limitation = value;
    }

}
