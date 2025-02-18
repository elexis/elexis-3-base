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

/**
 * <p>Java-Klasse für AdministrativeUnitType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AdministrativeUnitType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="administrativeUnitTypeCode" type="{urn:gs1:ecom:ecom_common:xsd:3}AdministrativeUnitTypeCodeType"/&gt;
 *         &lt;element name="gln" type="{urn:gs1:shared:shared_common:xsd:3}GLNType" minOccurs="0"/&gt;
 *         &lt;element name="internalAdministrativeUnitIdentification" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdministrativeUnitType", propOrder = {
    "administrativeUnitTypeCode",
    "gln",
    "internalAdministrativeUnitIdentification"
})
public class AdministrativeUnitType {

    @XmlElement(required = true)
    protected AdministrativeUnitTypeCodeType administrativeUnitTypeCode;
    protected String gln;
    protected String internalAdministrativeUnitIdentification;

    /**
     * Ruft den Wert der administrativeUnitTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link AdministrativeUnitTypeCodeType }
     *     
     */
    public AdministrativeUnitTypeCodeType getAdministrativeUnitTypeCode() {
        return administrativeUnitTypeCode;
    }

    /**
     * Legt den Wert der administrativeUnitTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link AdministrativeUnitTypeCodeType }
     *     
     */
    public void setAdministrativeUnitTypeCode(AdministrativeUnitTypeCodeType value) {
        this.administrativeUnitTypeCode = value;
    }

    /**
     * Ruft den Wert der gln-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGln() {
        return gln;
    }

    /**
     * Legt den Wert der gln-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGln(String value) {
        this.gln = value;
    }

    /**
     * Ruft den Wert der internalAdministrativeUnitIdentification-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalAdministrativeUnitIdentification() {
        return internalAdministrativeUnitIdentification;
    }

    /**
     * Legt den Wert der internalAdministrativeUnitIdentification-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalAdministrativeUnitIdentification(String value) {
        this.internalAdministrativeUnitIdentification = value;
    }

}
