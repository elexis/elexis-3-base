//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import java.util.ArrayList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PlainCardData1 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PlainCardData1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PAN" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Min8Max28NumericText"/>
 *         &lt;element name="CardSeqNb" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Min2Max3NumericText" minOccurs="0"/>
 *         &lt;element name="FctvDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}ISOYearMonth" minOccurs="0"/>
 *         &lt;element name="XpryDt" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}ISOYearMonth"/>
 *         &lt;element name="SvcCd" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}Exact3NumericText" minOccurs="0"/>
 *         &lt;element name="TrckData" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}TrackData1" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="CardSctyCd" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}CardSecurityInformation1" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PlainCardData1", propOrder = {
    "pan",
    "cardSeqNb",
    "fctvDt",
    "xpryDt",
    "svcCd",
    "trckData",
    "cardSctyCd"
})
public class PlainCardData1 {

    @XmlElement(name = "PAN", required = true)
    protected String pan;
    @XmlElement(name = "CardSeqNb")
    protected String cardSeqNb;
    @XmlElement(name = "FctvDt")
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar fctvDt;
    @XmlElement(name = "XpryDt", required = true)
    @XmlSchemaType(name = "gYearMonth")
    protected XMLGregorianCalendar xpryDt;
    @XmlElement(name = "SvcCd")
    protected String svcCd;
    @XmlElement(name = "TrckData")
    protected List<TrackData1> trckData;
    @XmlElement(name = "CardSctyCd")
    protected CardSecurityInformation1 cardSctyCd;

    /**
     * Ruft den Wert der pan-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAN() {
        return pan;
    }

    /**
     * Legt den Wert der pan-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPAN(String value) {
        this.pan = value;
    }

    /**
     * Ruft den Wert der cardSeqNb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardSeqNb() {
        return cardSeqNb;
    }

    /**
     * Legt den Wert der cardSeqNb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardSeqNb(String value) {
        this.cardSeqNb = value;
    }

    /**
     * Ruft den Wert der fctvDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFctvDt() {
        return fctvDt;
    }

    /**
     * Legt den Wert der fctvDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFctvDt(XMLGregorianCalendar value) {
        this.fctvDt = value;
    }

    /**
     * Ruft den Wert der xpryDt-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getXpryDt() {
        return xpryDt;
    }

    /**
     * Legt den Wert der xpryDt-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setXpryDt(XMLGregorianCalendar value) {
        this.xpryDt = value;
    }

    /**
     * Ruft den Wert der svcCd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSvcCd() {
        return svcCd;
    }

    /**
     * Legt den Wert der svcCd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSvcCd(String value) {
        this.svcCd = value;
    }

    /**
     * Gets the value of the trckData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the trckData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTrckData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrackData1 }
     * 
     * 
     */
    public List<TrackData1> getTrckData() {
        if (trckData == null) {
            trckData = new ArrayList<TrackData1>();
        }
        return this.trckData;
    }

    /**
     * Ruft den Wert der cardSctyCd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardSecurityInformation1 }
     *     
     */
    public CardSecurityInformation1 getCardSctyCd() {
        return cardSctyCd;
    }

    /**
     * Legt den Wert der cardSctyCd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardSecurityInformation1 }
     *     
     */
    public void setCardSctyCd(CardSecurityInformation1 value) {
        this.cardSctyCd = value;
    }

}
