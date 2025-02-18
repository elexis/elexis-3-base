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


/**
 * <p>Java-Klasse für EuUniqueIDType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EuUniqueIDType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="euUniqueIDTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}EuUniqueIDTypeCodeType"/&gt;
 *         &lt;element name="unitPacketLevelUniqueIdentifier" type="{urn:gs1:shared:shared_common:xsd:3}String500Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="aggregatedLevelUniqueIdentifier" type="{urn:gs1:shared:shared_common:xsd:3}String500Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EuUniqueIDType", propOrder = {
    "euUniqueIDTypeCode",
    "unitPacketLevelUniqueIdentifier",
    "aggregatedLevelUniqueIdentifier"
})
public class EuUniqueIDType {

    @XmlElement(required = true)
    protected EuUniqueIDTypeCodeType euUniqueIDTypeCode;
    protected List<String> unitPacketLevelUniqueIdentifier;
    protected List<String> aggregatedLevelUniqueIdentifier;

    /**
     * Ruft den Wert der euUniqueIDTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EuUniqueIDTypeCodeType }
     *     
     */
    public EuUniqueIDTypeCodeType getEuUniqueIDTypeCode() {
        return euUniqueIDTypeCode;
    }

    /**
     * Legt den Wert der euUniqueIDTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EuUniqueIDTypeCodeType }
     *     
     */
    public void setEuUniqueIDTypeCode(EuUniqueIDTypeCodeType value) {
        this.euUniqueIDTypeCode = value;
    }

    /**
     * Gets the value of the unitPacketLevelUniqueIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the unitPacketLevelUniqueIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnitPacketLevelUniqueIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getUnitPacketLevelUniqueIdentifier() {
        if (unitPacketLevelUniqueIdentifier == null) {
            unitPacketLevelUniqueIdentifier = new ArrayList<String>();
        }
        return this.unitPacketLevelUniqueIdentifier;
    }

    /**
     * Gets the value of the aggregatedLevelUniqueIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the aggregatedLevelUniqueIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAggregatedLevelUniqueIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAggregatedLevelUniqueIdentifier() {
        if (aggregatedLevelUniqueIdentifier == null) {
            aggregatedLevelUniqueIdentifier = new ArrayList<String>();
        }
        return this.aggregatedLevelUniqueIdentifier;
    }

}
