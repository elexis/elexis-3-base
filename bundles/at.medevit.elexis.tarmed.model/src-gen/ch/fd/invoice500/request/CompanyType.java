//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für companyType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="companyType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="companyname" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35"/&gt;
 *         &lt;element name="department" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" minOccurs="0"/&gt;
 *         &lt;element name="subaddressing" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" minOccurs="0"/&gt;
 *         &lt;element name="postal" type="{http://www.forum-datenaustausch.ch/invoice}postalAddressType"/&gt;
 *         &lt;element name="telecom" type="{http://www.forum-datenaustausch.ch/invoice}telecomAddressType" minOccurs="0"/&gt;
 *         &lt;element name="online" type="{http://www.forum-datenaustausch.ch/invoice}onlineAddressType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "companyType", propOrder = {
    "companyname",
    "department",
    "subaddressing",
    "postal",
    "telecom",
    "online"
})
public class CompanyType {

    @XmlElement(required = true)
    protected String companyname;
    protected String department;
    protected String subaddressing;
    @XmlElement(required = true)
    protected PostalAddressType postal;
    protected TelecomAddressType telecom;
    protected OnlineAddressType online;

    /**
     * Ruft den Wert der companyname-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyname() {
        return companyname;
    }

    /**
     * Legt den Wert der companyname-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyname(String value) {
        this.companyname = value;
    }

    /**
     * Ruft den Wert der department-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Legt den Wert der department-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartment(String value) {
        this.department = value;
    }

    /**
     * Ruft den Wert der subaddressing-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubaddressing() {
        return subaddressing;
    }

    /**
     * Legt den Wert der subaddressing-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubaddressing(String value) {
        this.subaddressing = value;
    }

    /**
     * Ruft den Wert der postal-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PostalAddressType }
     *     
     */
    public PostalAddressType getPostal() {
        return postal;
    }

    /**
     * Legt den Wert der postal-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PostalAddressType }
     *     
     */
    public void setPostal(PostalAddressType value) {
        this.postal = value;
    }

    /**
     * Ruft den Wert der telecom-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TelecomAddressType }
     *     
     */
    public TelecomAddressType getTelecom() {
        return telecom;
    }

    /**
     * Legt den Wert der telecom-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TelecomAddressType }
     *     
     */
    public void setTelecom(TelecomAddressType value) {
        this.telecom = value;
    }

    /**
     * Ruft den Wert der online-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link OnlineAddressType }
     *     
     */
    public OnlineAddressType getOnline() {
        return online;
    }

    /**
     * Legt den Wert der online-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link OnlineAddressType }
     *     
     */
    public void setOnline(OnlineAddressType value) {
        this.online = value;
    }

}
