//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import gs1.shared.shared_common.xsd.Description500Type;
import gs1.shared.shared_common.xsd.TemperatureRangeType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für HandlingInstructionType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="HandlingInstructionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="handlingInstructionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}HandlingInstructionCodeType" minOccurs="0"/&gt;
 *         &lt;element name="handlingInstructionText" type="{urn:gs1:shared:shared_common:xsd:3}Description500Type" minOccurs="0"/&gt;
 *         &lt;element name="printingInstructionCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PrintingInstructionCodeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="storageTemperature" type="{urn:gs1:shared:shared_common:xsd:3}TemperatureRangeType" minOccurs="0"/&gt;
 *         &lt;element name="transportTemperature" type="{urn:gs1:shared:shared_common:xsd:3}TemperatureRangeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HandlingInstructionType", propOrder = {
    "handlingInstructionCode",
    "handlingInstructionText",
    "printingInstructionCode",
    "storageTemperature",
    "transportTemperature"
})
public class HandlingInstructionType {

    protected HandlingInstructionCodeType handlingInstructionCode;
    protected Description500Type handlingInstructionText;
    protected List<PrintingInstructionCodeType> printingInstructionCode;
    protected TemperatureRangeType storageTemperature;
    protected TemperatureRangeType transportTemperature;

    /**
     * Ruft den Wert der handlingInstructionCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link HandlingInstructionCodeType }
     *     
     */
    public HandlingInstructionCodeType getHandlingInstructionCode() {
        return handlingInstructionCode;
    }

    /**
     * Legt den Wert der handlingInstructionCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link HandlingInstructionCodeType }
     *     
     */
    public void setHandlingInstructionCode(HandlingInstructionCodeType value) {
        this.handlingInstructionCode = value;
    }

    /**
     * Ruft den Wert der handlingInstructionText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description500Type }
     *     
     */
    public Description500Type getHandlingInstructionText() {
        return handlingInstructionText;
    }

    /**
     * Legt den Wert der handlingInstructionText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description500Type }
     *     
     */
    public void setHandlingInstructionText(Description500Type value) {
        this.handlingInstructionText = value;
    }

    /**
     * Gets the value of the printingInstructionCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the printingInstructionCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrintingInstructionCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PrintingInstructionCodeType }
     * 
     * 
     */
    public List<PrintingInstructionCodeType> getPrintingInstructionCode() {
        if (printingInstructionCode == null) {
            printingInstructionCode = new ArrayList<PrintingInstructionCodeType>();
        }
        return this.printingInstructionCode;
    }

    /**
     * Ruft den Wert der storageTemperature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TemperatureRangeType }
     *     
     */
    public TemperatureRangeType getStorageTemperature() {
        return storageTemperature;
    }

    /**
     * Legt den Wert der storageTemperature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TemperatureRangeType }
     *     
     */
    public void setStorageTemperature(TemperatureRangeType value) {
        this.storageTemperature = value;
    }

    /**
     * Ruft den Wert der transportTemperature-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TemperatureRangeType }
     *     
     */
    public TemperatureRangeType getTransportTemperature() {
        return transportTemperature;
    }

    /**
     * Legt den Wert der transportTemperature-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TemperatureRangeType }
     *     
     */
    public void setTransportTemperature(TemperatureRangeType value) {
        this.transportTemperature = value;
    }

}
