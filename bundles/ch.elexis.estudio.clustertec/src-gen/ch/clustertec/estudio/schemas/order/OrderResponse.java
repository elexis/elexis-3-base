//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.09 um 01:08:44 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="error-message" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="error-code" type="{http://estudio.clustertec.ch/schemas/order}positiveInteger3" /&gt;
 *       &lt;attribute name="order-ref-nr" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "errorMessage"
})
@XmlRootElement(name = "order-response")
public class OrderResponse {

    @XmlElement(name = "error-message", required = true)
    protected String errorMessage;
    @XmlAttribute(name = "error-code")
    protected Integer errorCode;
    @XmlAttribute(name = "order-ref-nr")
    protected String orderRefNr;

    /**
     * Ruft den Wert der errorMessage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Legt den Wert der errorMessage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Ruft den Wert der errorCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getErrorCode() {
        return errorCode;
    }

    /**
     * Legt den Wert der errorCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setErrorCode(Integer value) {
        this.errorCode = value;
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

}
