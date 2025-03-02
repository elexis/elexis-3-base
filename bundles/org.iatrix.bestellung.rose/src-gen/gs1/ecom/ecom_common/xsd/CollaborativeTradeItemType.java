//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für CollaborativeTradeItemType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="CollaborativeTradeItemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tradeItemIdentification" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_TradeItemIdentificationType"/&gt;
 *         &lt;element name="sellerLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType"/&gt;
 *         &lt;element name="buyerLocation" type="{urn:gs1:ecom:ecom_common:xsd:3}Ecom_PartyIdentificationType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CollaborativeTradeItemType", propOrder = {
    "tradeItemIdentification",
    "sellerLocation",
    "buyerLocation"
})
public class CollaborativeTradeItemType {

    @XmlElement(required = true)
    protected EcomTradeItemIdentificationType tradeItemIdentification;
    @XmlElement(required = true)
    protected EcomPartyIdentificationType sellerLocation;
    @XmlElement(required = true)
    protected EcomPartyIdentificationType buyerLocation;

    /**
     * Ruft den Wert der tradeItemIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomTradeItemIdentificationType }
     *     
     */
    public EcomTradeItemIdentificationType getTradeItemIdentification() {
        return tradeItemIdentification;
    }

    /**
     * Legt den Wert der tradeItemIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomTradeItemIdentificationType }
     *     
     */
    public void setTradeItemIdentification(EcomTradeItemIdentificationType value) {
        this.tradeItemIdentification = value;
    }

    /**
     * Ruft den Wert der sellerLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getSellerLocation() {
        return sellerLocation;
    }

    /**
     * Legt den Wert der sellerLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setSellerLocation(EcomPartyIdentificationType value) {
        this.sellerLocation = value;
    }

    /**
     * Ruft den Wert der buyerLocation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public EcomPartyIdentificationType getBuyerLocation() {
        return buyerLocation;
    }

    /**
     * Legt den Wert der buyerLocation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EcomPartyIdentificationType }
     *     
     */
    public void setBuyerLocation(EcomPartyIdentificationType value) {
        this.buyerLocation = value;
    }

}
