//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import gs1.shared.shared_common.xsd.AmountType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für LogisticServiceType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="LogisticServiceType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="logisticServiceRequirementCode" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticServiceRequirementCodeType"/&gt;
 *         &lt;element name="cashOnDeliveryAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="insuranceValue" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="logisticServiceChargeAmount" type="{urn:gs1:shared:shared_common:xsd:3}AmountType" minOccurs="0"/&gt;
 *         &lt;element name="cashOnDeliveryPayer" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *         &lt;element name="cashOnDeliveryBillTo" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LogisticServiceType", propOrder = {
    "logisticServiceRequirementCode",
    "cashOnDeliveryAmount",
    "insuranceValue",
    "logisticServiceChargeAmount",
    "cashOnDeliveryPayer",
    "cashOnDeliveryBillTo"
})
public class LogisticServiceType {

    @XmlElement(required = true)
    protected LogisticServiceRequirementCodeType logisticServiceRequirementCode;
    protected AmountType cashOnDeliveryAmount;
    protected AmountType insuranceValue;
    protected AmountType logisticServiceChargeAmount;
    protected TransactionalPartyType cashOnDeliveryPayer;
    protected TransactionalPartyType cashOnDeliveryBillTo;

    /**
     * Ruft den Wert der logisticServiceRequirementCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LogisticServiceRequirementCodeType }
     *     
     */
    public LogisticServiceRequirementCodeType getLogisticServiceRequirementCode() {
        return logisticServiceRequirementCode;
    }

    /**
     * Legt den Wert der logisticServiceRequirementCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LogisticServiceRequirementCodeType }
     *     
     */
    public void setLogisticServiceRequirementCode(LogisticServiceRequirementCodeType value) {
        this.logisticServiceRequirementCode = value;
    }

    /**
     * Ruft den Wert der cashOnDeliveryAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getCashOnDeliveryAmount() {
        return cashOnDeliveryAmount;
    }

    /**
     * Legt den Wert der cashOnDeliveryAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setCashOnDeliveryAmount(AmountType value) {
        this.cashOnDeliveryAmount = value;
    }

    /**
     * Ruft den Wert der insuranceValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getInsuranceValue() {
        return insuranceValue;
    }

    /**
     * Legt den Wert der insuranceValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setInsuranceValue(AmountType value) {
        this.insuranceValue = value;
    }

    /**
     * Ruft den Wert der logisticServiceChargeAmount-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AmountType }
     *     
     */
    public AmountType getLogisticServiceChargeAmount() {
        return logisticServiceChargeAmount;
    }

    /**
     * Legt den Wert der logisticServiceChargeAmount-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AmountType }
     *     
     */
    public void setLogisticServiceChargeAmount(AmountType value) {
        this.logisticServiceChargeAmount = value;
    }

    /**
     * Ruft den Wert der cashOnDeliveryPayer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getCashOnDeliveryPayer() {
        return cashOnDeliveryPayer;
    }

    /**
     * Legt den Wert der cashOnDeliveryPayer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setCashOnDeliveryPayer(TransactionalPartyType value) {
        this.cashOnDeliveryPayer = value;
    }

    /**
     * Ruft den Wert der cashOnDeliveryBillTo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getCashOnDeliveryBillTo() {
        return cashOnDeliveryBillTo;
    }

    /**
     * Legt den Wert der cashOnDeliveryBillTo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setCashOnDeliveryBillTo(TransactionalPartyType value) {
        this.cashOnDeliveryBillTo = value;
    }

}
