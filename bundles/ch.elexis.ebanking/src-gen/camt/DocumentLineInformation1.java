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
 * <p>Java-Klasse für DocumentLineInformation1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="DocumentLineInformation1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}DocumentLineIdentification1" maxOccurs="unbounded"/>
 *         &lt;element name="Desc" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Max2048Text" minOccurs="0"/>
 *         &lt;element name="Amt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}RemittanceAmount3" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentLineInformation1", propOrder = {
    "id",
    "desc",
    "amt"
})
public class DocumentLineInformation1 {

    @XmlElement(name = "Id", required = true)
    protected List<DocumentLineIdentification1> id;
    @XmlElement(name = "Desc")
    protected String desc;
    @XmlElement(name = "Amt")
    protected RemittanceAmount3 amt;

    /**
     * Gets the value of the id property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the id property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DocumentLineIdentification1 }
     * 
     * 
     */
    public List<DocumentLineIdentification1> getId() {
        if (id == null) {
            id = new ArrayList<DocumentLineIdentification1>();
        }
        return this.id;
    }

    /**
     * Ruft den Wert der desc-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Legt den Wert der desc-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Ruft den Wert der amt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RemittanceAmount3 }
     *     
     */
    public RemittanceAmount3 getAmt() {
        return amt;
    }

    /**
     * Legt den Wert der amt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RemittanceAmount3 }
     *     
     */
    public void setAmt(RemittanceAmount3 value) {
        this.amt = value;
    }

}
