//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://estudio.clustertec.ch/schemas/prescription}addressComplexType"&gt;
 *       &lt;attribute name="langCode" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}langCode" /&gt;
 *       &lt;attribute name="clientNrClustertec" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string6" /&gt;
 *       &lt;attribute name="zsrId" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string7" /&gt;
 *       &lt;attribute name="eanId" type="{http://estudio.clustertec.ch/schemas/prescription}eanId" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "prescriptorAddress")
public class PrescriptorAddress
    extends AddressComplexType
{

    @XmlAttribute(name = "langCode", required = true)
    protected int langCode;
    @XmlAttribute(name = "clientNrClustertec", required = true)
    protected String clientNrClustertec;
    @XmlAttribute(name = "zsrId", required = true)
    protected String zsrId;
    @XmlAttribute(name = "eanId")
    protected Long eanId;

    /**
     * Ruft den Wert der langCode-Eigenschaft ab.
     * 
     */
    public int getLangCode() {
        return langCode;
    }

    /**
     * Legt den Wert der langCode-Eigenschaft fest.
     * 
     */
    public void setLangCode(int value) {
        this.langCode = value;
    }

    /**
     * Ruft den Wert der clientNrClustertec-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNrClustertec() {
        return clientNrClustertec;
    }

    /**
     * Legt den Wert der clientNrClustertec-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNrClustertec(String value) {
        this.clientNrClustertec = value;
    }

    /**
     * Ruft den Wert der zsrId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZsrId() {
        return zsrId;
    }

    /**
     * Legt den Wert der zsrId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZsrId(String value) {
        this.zsrId = value;
    }

    /**
     * Ruft den Wert der eanId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getEanId() {
        return eanId;
    }

    /**
     * Legt den Wert der eanId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setEanId(Long value) {
        this.eanId = value;
    }

}
