//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.08 um 02:13:03 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import java.util.ArrayList;
import java.util.List;
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
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/order}patientOrderB2C" minOccurs="0"/&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/order}product" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="clientNrRose" use="required" type="{http://estudio.clustertec.ch/schemas/order}string6" /&gt;
 *       &lt;attribute name="user" use="required" type="{http://estudio.clustertec.ch/schemas/order}string16" /&gt;
 *       &lt;attribute name="password" use="required" type="{http://estudio.clustertec.ch/schemas/order}string16" /&gt;
 *       &lt;attribute name="deliveryType" use="required" type="{http://estudio.clustertec.ch/schemas/order}deliveryType" /&gt;
 *       &lt;attribute name="orderRefNr" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="rowa" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "patientOrderB2C",
    "product"
})
@XmlRootElement(name = "order")
public class Order {

    protected PatientOrderB2C patientOrderB2C;
    @XmlElement(required = true)
    protected List<Product> product;
    @XmlAttribute(name = "clientNrRose", required = true)
    protected String clientNrRose;
    @XmlAttribute(name = "user", required = true)
    protected String user;
    @XmlAttribute(name = "password", required = true)
    protected String password;
    @XmlAttribute(name = "deliveryType", required = true)
    protected int deliveryType;
    @XmlAttribute(name = "orderRefNr")
    protected String orderRefNr;
    @XmlAttribute(name = "rowa")
    protected Boolean rowa;

    /**
     * Ruft den Wert der patientOrderB2C-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PatientOrderB2C }
     *     
     */
    public PatientOrderB2C getPatientOrderB2C() {
        return patientOrderB2C;
    }

    /**
     * Legt den Wert der patientOrderB2C-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientOrderB2C }
     *     
     */
    public void setPatientOrderB2C(PatientOrderB2C value) {
        this.patientOrderB2C = value;
    }

    /**
     * Gets the value of the product property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the product property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProduct().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Product }
     * 
     * 
     */
    public List<Product> getProduct() {
        if (product == null) {
            product = new ArrayList<Product>();
        }
        return this.product;
    }

    /**
     * Ruft den Wert der clientNrRose-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNrRose() {
        return clientNrRose;
    }

    /**
     * Legt den Wert der clientNrRose-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNrRose(String value) {
        this.clientNrRose = value;
    }

    /**
     * Ruft den Wert der user-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Legt den Wert der user-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Ruft den Wert der password-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Legt den Wert der password-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Ruft den Wert der deliveryType-Eigenschaft ab.
     * 
     */
    public int getDeliveryType() {
        return deliveryType;
    }

    /**
     * Legt den Wert der deliveryType-Eigenschaft fest.
     * 
     */
    public void setDeliveryType(int value) {
        this.deliveryType = value;
    }

    /**
     * Ruft den Wert der orderRefNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderRefNr() {
        return orderRefNr;
    }

    /**
     * Legt den Wert der orderRefNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderRefNr(String value) {
        this.orderRefNr = value;
    }

    /**
     * Ruft den Wert der rowa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRowa() {
        return rowa;
    }

    /**
     * Legt den Wert der rowa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRowa(Boolean value) {
        this.rowa = value;
    }

}
