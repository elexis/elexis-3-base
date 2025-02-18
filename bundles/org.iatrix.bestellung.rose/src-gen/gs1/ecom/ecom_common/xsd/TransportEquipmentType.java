//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.CodeType;


/**
 * <p>Java-Klasse für TransportEquipmentType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportEquipmentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transportEquipmentTypeCode" type="{urn:gs1:shared:shared_common:xsd:3}CodeType"/&gt;
 *         &lt;element name="returnableAssetTypeIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ReturnableAssetIdentificationType" minOccurs="0"/&gt;
 *         &lt;element name="individualReturnableAssetIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_ReturnableAssetIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="individualAssetIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_IndividualAssetIdentificationType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportEquipmentType", propOrder = {
    "transportEquipmentTypeCode",
    "returnableAssetTypeIdentification",
    "individualReturnableAssetIdentification",
    "individualAssetIdentification"
})
public class TransportEquipmentType {

    @XmlElement(required = true)
    protected CodeType transportEquipmentTypeCode;
    protected EcomReturnableAssetIdentificationType returnableAssetTypeIdentification;
    protected List<EcomReturnableAssetIdentificationType> individualReturnableAssetIdentification;
    protected List<EcomIndividualAssetIdentificationType> individualAssetIdentification;

    /**
     * Ruft den Wert der transportEquipmentTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CodeType }
     *     
     */
    public CodeType getTransportEquipmentTypeCode() {
        return transportEquipmentTypeCode;
    }

    /**
     * Legt den Wert der transportEquipmentTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CodeType }
     *     
     */
    public void setTransportEquipmentTypeCode(CodeType value) {
        this.transportEquipmentTypeCode = value;
    }

    /**
     * Ruft den Wert der returnableAssetTypeIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomReturnableAssetIdentificationType }
     *     
     */
    public EcomReturnableAssetIdentificationType getReturnableAssetTypeIdentification() {
        return returnableAssetTypeIdentification;
    }

    /**
     * Legt den Wert der returnableAssetTypeIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomReturnableAssetIdentificationType }
     *     
     */
    public void setReturnableAssetTypeIdentification(EcomReturnableAssetIdentificationType value) {
        this.returnableAssetTypeIdentification = value;
    }

    /**
     * Gets the value of the individualReturnableAssetIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the individualReturnableAssetIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndividualReturnableAssetIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomReturnableAssetIdentificationType }
     * 
     * 
     */
    public List<EcomReturnableAssetIdentificationType> getIndividualReturnableAssetIdentification() {
        if (individualReturnableAssetIdentification == null) {
            individualReturnableAssetIdentification = new ArrayList<EcomReturnableAssetIdentificationType>();
        }
        return this.individualReturnableAssetIdentification;
    }

    /**
     * Gets the value of the individualAssetIdentification property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the individualAssetIdentification property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIndividualAssetIdentification().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomIndividualAssetIdentificationType }
     * 
     * 
     */
    public List<EcomIndividualAssetIdentificationType> getIndividualAssetIdentification() {
        if (individualAssetIdentification == null) {
            individualAssetIdentification = new ArrayList<EcomIndividualAssetIdentificationType>();
        }
        return this.individualAssetIdentification;
    }

}
