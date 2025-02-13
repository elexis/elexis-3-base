//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TypedEntityIdentificationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TypedEntityIdentificationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:shared:shared_common:xsd:3}EntityIdentificationType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="entityTypeCode" type="{urn:gs1:shared:shared_common:xsd:3}EntityTypeCodeType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TypedEntityIdentificationType", propOrder = {
    "entityTypeCode"
})
public class TypedEntityIdentificationType
    extends EntityIdentificationType
{

    @XmlElement(required = true)
    protected EntityTypeCodeType entityTypeCode;

    /**
     * Ruft den Wert der entityTypeCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link EntityTypeCodeType }
     *     
     */
    public EntityTypeCodeType getEntityTypeCode() {
        return entityTypeCode;
    }

    /**
     * Legt den Wert der entityTypeCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link EntityTypeCodeType }
     *     
     */
    public void setEntityTypeCode(EntityTypeCodeType value) {
        this.entityTypeCode = value;
    }

}
