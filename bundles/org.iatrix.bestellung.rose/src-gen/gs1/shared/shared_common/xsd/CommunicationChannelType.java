//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CommunicationChannelType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CommunicationChannelType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="communicationChannelCode" type="{urn:gs1:shared:shared_common:xsd:3}CommunicationChannelCodeType"/&gt;
 *         &lt;element name="communicationValue"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="communicationChannelName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CommunicationChannelType", propOrder = {
    "communicationChannelCode",
    "communicationValue",
    "communicationChannelName"
})
public class CommunicationChannelType {

    @XmlElement(required = true)
    protected CommunicationChannelCodeType communicationChannelCode;
    @XmlElement(required = true)
    protected String communicationValue;
    protected String communicationChannelName;

    /**
     * Ruft den Wert der communicationChannelCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CommunicationChannelCodeType }
     *     
     */
    public CommunicationChannelCodeType getCommunicationChannelCode() {
        return communicationChannelCode;
    }

    /**
     * Legt den Wert der communicationChannelCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CommunicationChannelCodeType }
     *     
     */
    public void setCommunicationChannelCode(CommunicationChannelCodeType value) {
        this.communicationChannelCode = value;
    }

    /**
     * Ruft den Wert der communicationValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommunicationValue() {
        return communicationValue;
    }

    /**
     * Legt den Wert der communicationValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommunicationValue(String value) {
        this.communicationValue = value;
    }

    /**
     * Ruft den Wert der communicationChannelName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCommunicationChannelName() {
        return communicationChannelName;
    }

    /**
     * Legt den Wert der communicationChannelName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCommunicationChannelName(String value) {
        this.communicationChannelName = value;
    }

}
