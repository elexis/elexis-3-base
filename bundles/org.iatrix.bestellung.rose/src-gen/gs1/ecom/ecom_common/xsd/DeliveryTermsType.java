//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.CodeType;
import gs1.shared.shared_common.xsd.Description500Type;
import gs1.shared.shared_common.xsd.IncotermsCodeType;
import gs1.shared.shared_common.xsd.NonBinaryLogicEnumerationType;

/**
 * <p>Java-Klasse für DeliveryTermsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DeliveryTermsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="incotermsCode" type="{urn:gs1:shared:shared_common:xsd:3}IncotermsCodeType" minOccurs="0"/&gt;
 *         &lt;element name="alternateDeliveryTermsCode" type="{urn:gs1:shared:shared_common:xsd:3}CodeType" minOccurs="0"/&gt;
 *         &lt;element name="deliveryInstructions" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type" minOccurs="0"/&gt;
 *         &lt;element name="deliveryCostPayment" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportChargesPaymentMethodCodeType" minOccurs="0"/&gt;
 *         &lt;element name="isSignatureRequired" type="{urn:gs1:shared:shared_common:xsd:3}NonBinaryLogicEnumerationType" minOccurs="0"/&gt;
 *         &lt;element name="deliveryTermsLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticLocationType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeliveryTermsType", propOrder = {
    "incotermsCode",
    "alternateDeliveryTermsCode",
    "deliveryInstructions",
    "deliveryCostPayment",
    "isSignatureRequired",
    "deliveryTermsLocation"
})
public class DeliveryTermsType {

    protected IncotermsCodeType incotermsCode;
    protected CodeType alternateDeliveryTermsCode;
    protected Description500Type deliveryInstructions;
    protected TransportChargesPaymentMethodCodeType deliveryCostPayment;
    @XmlSchemaType(name = "string")
    protected NonBinaryLogicEnumerationType isSignatureRequired;
    protected LogisticLocationType deliveryTermsLocation;

    /**
     * Ruft den Wert der incotermsCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IncotermsCodeType }
     *     
     */
    public IncotermsCodeType getIncotermsCode() {
        return incotermsCode;
    }

    /**
     * Legt den Wert der incotermsCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IncotermsCodeType }
     *     
     */
    public void setIncotermsCode(IncotermsCodeType value) {
        this.incotermsCode = value;
    }

    /**
     * Ruft den Wert der alternateDeliveryTermsCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getAlternateDeliveryTermsCode() {
        return alternateDeliveryTermsCode;
    }

    /**
     * Legt den Wert der alternateDeliveryTermsCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setAlternateDeliveryTermsCode(CodeType value) {
        this.alternateDeliveryTermsCode = value;
    }

    /**
     * Ruft den Wert der deliveryInstructions-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description500Type }
     *     
     */
    public Description500Type getDeliveryInstructions() {
        return deliveryInstructions;
    }

    /**
     * Legt den Wert der deliveryInstructions-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description500Type }
     *     
     */
    public void setDeliveryInstructions(Description500Type value) {
        this.deliveryInstructions = value;
    }

    /**
     * Ruft den Wert der deliveryCostPayment-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportChargesPaymentMethodCodeType }
     *     
     */
    public TransportChargesPaymentMethodCodeType getDeliveryCostPayment() {
        return deliveryCostPayment;
    }

    /**
     * Legt den Wert der deliveryCostPayment-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportChargesPaymentMethodCodeType }
     *     
     */
    public void setDeliveryCostPayment(TransportChargesPaymentMethodCodeType value) {
        this.deliveryCostPayment = value;
    }

    /**
     * Ruft den Wert der isSignatureRequired-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link NonBinaryLogicEnumerationType }
     *     
     */
    public NonBinaryLogicEnumerationType getIsSignatureRequired() {
        return isSignatureRequired;
    }

    /**
     * Legt den Wert der isSignatureRequired-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link NonBinaryLogicEnumerationType }
     *     
     */
    public void setIsSignatureRequired(NonBinaryLogicEnumerationType value) {
        this.isSignatureRequired = value;
    }

    /**
     * Ruft den Wert der deliveryTermsLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LogisticLocationType }
     *     
     */
    public LogisticLocationType getDeliveryTermsLocation() {
        return deliveryTermsLocation;
    }

    /**
     * Legt den Wert der deliveryTermsLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LogisticLocationType }
     *     
     */
    public void setDeliveryTermsLocation(LogisticLocationType value) {
        this.deliveryTermsLocation = value;
    }

}
