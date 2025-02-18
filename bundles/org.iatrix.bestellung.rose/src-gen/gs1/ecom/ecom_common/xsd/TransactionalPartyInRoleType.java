//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.PartyRoleCodeType;


/**
 * <p>Java-Klasse für TransactionalPartyInRoleType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransactionalPartyInRoleType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}TransactionalPartyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="partyRoleCode" type="{urn:gs1:shared:shared_common:xsd:3}PartyRoleCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionalPartyInRoleType", propOrder = {
    "partyRoleCode"
})
public class TransactionalPartyInRoleType
    extends TransactionalPartyType
{

    @XmlElement(required = true)
    protected PartyRoleCodeType partyRoleCode;

    /**
     * Ruft den Wert der partyRoleCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PartyRoleCodeType }
     *     
     */
    public PartyRoleCodeType getPartyRoleCode() {
        return partyRoleCode;
    }

    /**
     * Legt den Wert der partyRoleCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PartyRoleCodeType }
     *     
     */
    public void setPartyRoleCode(PartyRoleCodeType value) {
        this.partyRoleCode = value;
    }

}
