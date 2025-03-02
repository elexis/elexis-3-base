//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für IncidentDetailsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="IncidentDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="incidentDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="depositDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="incidentDetectedDuringBankNoteRecycling" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="incidentDetected" type="{urn:gs1:shared:shared_common:xsd:3}String500Type" minOccurs="0"/&gt;
 *         &lt;element name="incidentLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType" minOccurs="0"/&gt;
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
@XmlType(name = "IncidentDetailsType", propOrder = {
    "incidentDateTime",
    "depositDateTime",
    "incidentDetectedDuringBankNoteRecycling",
    "incidentDetected",
    "incidentLocation",
    "avpList"
})
public class IncidentDetailsType {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar incidentDateTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar depositDateTime;
    protected Boolean incidentDetectedDuringBankNoteRecycling;
    protected String incidentDetected;
    protected TransactionalPartyType incidentLocation;
    protected List<EcomAttributeValuePairListType> avpList;

    /**
     * Ruft den Wert der incidentDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIncidentDateTime() {
        return incidentDateTime;
    }

    /**
     * Legt den Wert der incidentDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIncidentDateTime(XMLGregorianCalendar value) {
        this.incidentDateTime = value;
    }

    /**
     * Ruft den Wert der depositDateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDepositDateTime() {
        return depositDateTime;
    }

    /**
     * Legt den Wert der depositDateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDepositDateTime(XMLGregorianCalendar value) {
        this.depositDateTime = value;
    }

    /**
     * Ruft den Wert der incidentDetectedDuringBankNoteRecycling-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncidentDetectedDuringBankNoteRecycling() {
        return incidentDetectedDuringBankNoteRecycling;
    }

    /**
     * Legt den Wert der incidentDetectedDuringBankNoteRecycling-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncidentDetectedDuringBankNoteRecycling(Boolean value) {
        this.incidentDetectedDuringBankNoteRecycling = value;
    }

    /**
     * Ruft den Wert der incidentDetected-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIncidentDetected() {
        return incidentDetected;
    }

    /**
     * Legt den Wert der incidentDetected-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIncidentDetected(String value) {
        this.incidentDetected = value;
    }

    /**
     * Ruft den Wert der incidentLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransactionalPartyType }
     *     
     */
    public TransactionalPartyType getIncidentLocation() {
        return incidentLocation;
    }

    /**
     * Legt den Wert der incidentLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionalPartyType }
     *     
     */
    public void setIncidentLocation(TransactionalPartyType value) {
        this.incidentLocation = value;
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
