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
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.Description5000Type;


/**
 * <p>Java-Klasse für IncidentCircumstancesType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="IncidentCircumstancesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="complaintID" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="circumstanceDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description5000Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="incidentTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}IncidentTypeCodeType" minOccurs="0"/&gt;
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
@XmlType(name = "IncidentCircumstancesType", propOrder = {
    "complaintID",
    "circumstanceDescription",
    "incidentTypeCode",
    "avpList"
})
public class IncidentCircumstancesType {

    protected String complaintID;
    protected List<Description5000Type> circumstanceDescription;
    protected IncidentTypeCodeType incidentTypeCode;
    protected List<EcomAttributeValuePairListType> avpList;

    /**
     * Ruft den Wert der complaintID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplaintID() {
        return complaintID;
    }

    /**
     * Legt den Wert der complaintID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplaintID(String value) {
        this.complaintID = value;
    }

    /**
     * Gets the value of the circumstanceDescription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the circumstanceDescription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCircumstanceDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description5000Type }
     * 
     * 
     */
    public List<Description5000Type> getCircumstanceDescription() {
        if (circumstanceDescription == null) {
            circumstanceDescription = new ArrayList<Description5000Type>();
        }
        return this.circumstanceDescription;
    }

    /**
     * Ruft den Wert der incidentTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IncidentTypeCodeType }
     *     
     */
    public IncidentTypeCodeType getIncidentTypeCode() {
        return incidentTypeCode;
    }

    /**
     * Legt den Wert der incidentTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IncidentTypeCodeType }
     *     
     */
    public void setIncidentTypeCode(IncidentTypeCodeType value) {
        this.incidentTypeCode = value;
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
