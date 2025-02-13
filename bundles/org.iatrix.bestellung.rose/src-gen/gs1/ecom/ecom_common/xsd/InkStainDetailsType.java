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
 * <p>Java-Klasse für InkStainDetailsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="InkStainDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="theftDeterrenceSystemID"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="theftDeterrenceSystemName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="theftDeterrenceSystemType"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="incidentCircumstances" type="{urn:gs1:ecom:ecom_common:xsd:3}IncidentCircumstancesType" minOccurs="0"/&gt;
 *         &lt;element name="avpList" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_AttributeValuePairListType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InkStainDetailsType", propOrder = {
    "theftDeterrenceSystemID",
    "theftDeterrenceSystemName",
    "theftDeterrenceSystemType",
    "incidentCircumstances",
    "avpList"
})
public class InkStainDetailsType {

    @XmlElement(required = true)
    protected String theftDeterrenceSystemID;
    protected String theftDeterrenceSystemName;
    @XmlElement(required = true)
    protected String theftDeterrenceSystemType;
    protected IncidentCircumstancesType incidentCircumstances;
    protected List<EcomAttributeValuePairListType> avpList;

    /**
     * Ruft den Wert der theftDeterrenceSystemID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheftDeterrenceSystemID() {
        return theftDeterrenceSystemID;
    }

    /**
     * Legt den Wert der theftDeterrenceSystemID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheftDeterrenceSystemID(String value) {
        this.theftDeterrenceSystemID = value;
    }

    /**
     * Ruft den Wert der theftDeterrenceSystemName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheftDeterrenceSystemName() {
        return theftDeterrenceSystemName;
    }

    /**
     * Legt den Wert der theftDeterrenceSystemName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheftDeterrenceSystemName(String value) {
        this.theftDeterrenceSystemName = value;
    }

    /**
     * Ruft den Wert der theftDeterrenceSystemType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTheftDeterrenceSystemType() {
        return theftDeterrenceSystemType;
    }

    /**
     * Legt den Wert der theftDeterrenceSystemType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTheftDeterrenceSystemType(String value) {
        this.theftDeterrenceSystemType = value;
    }

    /**
     * Ruft den Wert der incidentCircumstances-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IncidentCircumstancesType }
     *     
     */
    public IncidentCircumstancesType getIncidentCircumstances() {
        return incidentCircumstances;
    }

    /**
     * Legt den Wert der incidentCircumstances-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IncidentCircumstancesType }
     *     
     */
    public void setIncidentCircumstances(IncidentCircumstancesType value) {
        this.incidentCircumstances = value;
    }

    /**
     * Gets the value of the avpList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the avpList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAvpList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomAttributeValuePairListType }
     * 
     * 
     */
    public List<EcomAttributeValuePairListType> getAvpList() {
        if (avpList == null) {
            avpList = new ArrayList<EcomAttributeValuePairListType>();
        }
        return this.avpList;
    }

}
