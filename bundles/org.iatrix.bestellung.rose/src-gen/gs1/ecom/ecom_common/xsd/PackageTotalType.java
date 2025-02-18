//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import gs1.shared.shared_common.xsd.MeasurementType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PackageTotalType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PackageTotalType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="packageTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PackageTypeCodeType"/&gt;
 *         &lt;element name="totalPackageQuantity" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/&gt;
 *         &lt;element name="totalGrossVolume" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="totalGrossWeight" type="{urn:gs1:shared:shared_common:xsd:3}MeasurementType" minOccurs="0"/&gt;
 *         &lt;element name="returnablePackaging" type="{urn:gs1:ecom:ecom_common:xsd:3}ReturnablePackagingType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PackageTotalType", propOrder = {
    "packageTypeCode",
    "totalPackageQuantity",
    "totalGrossVolume",
    "totalGrossWeight",
    "returnablePackaging"
})
public class PackageTotalType {

    @XmlElement(required = true)
    protected PackageTypeCodeType packageTypeCode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger totalPackageQuantity;
    protected MeasurementType totalGrossVolume;
    protected MeasurementType totalGrossWeight;
    protected List<ReturnablePackagingType> returnablePackaging;

    /**
     * Ruft den Wert der packageTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PackageTypeCodeType }
     *     
     */
    public PackageTypeCodeType getPackageTypeCode() {
        return packageTypeCode;
    }

    /**
     * Legt den Wert der packageTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PackageTypeCodeType }
     *     
     */
    public void setPackageTypeCode(PackageTypeCodeType value) {
        this.packageTypeCode = value;
    }

    /**
     * Ruft den Wert der totalPackageQuantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalPackageQuantity() {
        return totalPackageQuantity;
    }

    /**
     * Legt den Wert der totalPackageQuantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalPackageQuantity(BigInteger value) {
        this.totalPackageQuantity = value;
    }

    /**
     * Ruft den Wert der totalGrossVolume-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalGrossVolume() {
        return totalGrossVolume;
    }

    /**
     * Legt den Wert der totalGrossVolume-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalGrossVolume(MeasurementType value) {
        this.totalGrossVolume = value;
    }

    /**
     * Ruft den Wert der totalGrossWeight-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MeasurementType }
     *     
     */
    public MeasurementType getTotalGrossWeight() {
        return totalGrossWeight;
    }

    /**
     * Legt den Wert der totalGrossWeight-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MeasurementType }
     *     
     */
    public void setTotalGrossWeight(MeasurementType value) {
        this.totalGrossWeight = value;
    }

    /**
     * Gets the value of the returnablePackaging property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the returnablePackaging property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReturnablePackaging().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ReturnablePackagingType }
     * 
     * 
     */
    public List<ReturnablePackagingType> getReturnablePackaging() {
        if (returnablePackaging == null) {
            returnablePackaging = new ArrayList<ReturnablePackagingType>();
        }
        return this.returnablePackaging;
    }

}
