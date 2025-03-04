//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PointOfInteraction1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PointOfInteraction1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}GenericIdentification32"/>
 *         &lt;element name="SysNm" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Max70Text" minOccurs="0"/>
 *         &lt;element name="GrpId" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Max35Text" minOccurs="0"/>
 *         &lt;element name="Cpblties" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}PointOfInteractionCapabilities1" minOccurs="0"/>
 *         &lt;element name="Cmpnt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}PointOfInteractionComponent1" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PointOfInteraction1", propOrder = {
    "id",
    "sysNm",
    "grpId",
    "cpblties",
    "cmpnt"
})
public class PointOfInteraction1 {

    @XmlElement(name = "Id", required = true)
    protected GenericIdentification32 id;
    @XmlElement(name = "SysNm")
    protected String sysNm;
    @XmlElement(name = "GrpId")
    protected String grpId;
    @XmlElement(name = "Cpblties")
    protected PointOfInteractionCapabilities1 cpblties;
    @XmlElement(name = "Cmpnt")
    protected List<PointOfInteractionComponent1> cmpnt;

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GenericIdentification32 }
     *     
     */
    public GenericIdentification32 getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GenericIdentification32 }
     *     
     */
    public void setId(GenericIdentification32 value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der sysNm-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSysNm() {
        return sysNm;
    }

    /**
     * Legt den Wert der sysNm-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSysNm(String value) {
        this.sysNm = value;
    }

    /**
     * Ruft den Wert der grpId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGrpId() {
        return grpId;
    }

    /**
     * Legt den Wert der grpId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGrpId(String value) {
        this.grpId = value;
    }

    /**
     * Ruft den Wert der cpblties-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PointOfInteractionCapabilities1 }
     *     
     */
    public PointOfInteractionCapabilities1 getCpblties() {
        return cpblties;
    }

    /**
     * Legt den Wert der cpblties-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PointOfInteractionCapabilities1 }
     *     
     */
    public void setCpblties(PointOfInteractionCapabilities1 value) {
        this.cpblties = value;
    }

    /**
     * Gets the value of the cmpnt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the cmpnt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCmpnt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PointOfInteractionComponent1 }
     * 
     * 
     */
    public List<PointOfInteractionComponent1> getCmpnt() {
        if (cmpnt == null) {
            cmpnt = new ArrayList<PointOfInteractionComponent1>();
        }
        return this.cmpnt;
    }

}
