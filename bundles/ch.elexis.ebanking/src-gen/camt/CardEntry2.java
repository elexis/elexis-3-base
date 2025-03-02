//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für CardEntry2 complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CardEntry2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Card" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}PaymentCard4" minOccurs="0"/>
 *         &lt;element name="POI" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}PointOfInteraction1" minOccurs="0"/>
 *         &lt;element name="AggtdNtry" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}CardAggregated1" minOccurs="0"/>
 *         &lt;element name="PrePdAcct" type="{urn:iso:std:iso:20022:tech:xsd:camt.054.001.06}CashAccount24" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardEntry2", propOrder = {
    "card",
    "poi",
    "aggtdNtry",
    "prePdAcct"
})
public class CardEntry2 {

    @XmlElement(name = "Card")
    protected PaymentCard4 card;
    @XmlElement(name = "POI")
    protected PointOfInteraction1 poi;
    @XmlElement(name = "AggtdNtry")
    protected CardAggregated1 aggtdNtry;
    @XmlElement(name = "PrePdAcct")
    protected CashAccount24 prePdAcct;

    /**
     * Ruft den Wert der card-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard4 }
     *     
     */
    public PaymentCard4 getCard() {
        return card;
    }

    /**
     * Legt den Wert der card-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard4 }
     *     
     */
    public void setCard(PaymentCard4 value) {
        this.card = value;
    }

    /**
     * Ruft den Wert der poi-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PointOfInteraction1 }
     *     
     */
    public PointOfInteraction1 getPOI() {
        return poi;
    }

    /**
     * Legt den Wert der poi-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PointOfInteraction1 }
     *     
     */
    public void setPOI(PointOfInteraction1 value) {
        this.poi = value;
    }

    /**
     * Ruft den Wert der aggtdNtry-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CardAggregated1 }
     *     
     */
    public CardAggregated1 getAggtdNtry() {
        return aggtdNtry;
    }

    /**
     * Legt den Wert der aggtdNtry-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CardAggregated1 }
     *     
     */
    public void setAggtdNtry(CardAggregated1 value) {
        this.aggtdNtry = value;
    }

    /**
     * Ruft den Wert der prePdAcct-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CashAccount24 }
     *     
     */
    public CashAccount24 getPrePdAcct() {
        return prePdAcct;
    }

    /**
     * Legt den Wert der prePdAcct-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CashAccount24 }
     *     
     */
    public void setPrePdAcct(CashAccount24 value) {
        this.prePdAcct = value;
    }

}
