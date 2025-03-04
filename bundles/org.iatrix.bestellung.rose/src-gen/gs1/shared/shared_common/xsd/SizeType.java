//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für SizeType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="SizeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="descriptiveSize" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="sizeCode" type="{urn:gs1:shared:shared_common:xsd:3}SizeCodeType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SizeType", propOrder = {
    "descriptiveSize",
    "sizeCode"
})
public class SizeType {

    protected Description80Type descriptiveSize;
    protected SizeCodeType sizeCode;

    /**
     * Ruft den Wert der descriptiveSize-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getDescriptiveSize() {
        return descriptiveSize;
    }

    /**
     * Legt den Wert der descriptiveSize-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setDescriptiveSize(Description80Type value) {
        this.descriptiveSize = value;
    }

    /**
     * Ruft den Wert der sizeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SizeCodeType }
     *     
     */
    public SizeCodeType getSizeCode() {
        return sizeCode;
    }

    /**
     * Legt den Wert der sizeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SizeCodeType }
     *     
     */
    public void setSizeCode(SizeCodeType value) {
        this.sizeCode = value;
    }

}
