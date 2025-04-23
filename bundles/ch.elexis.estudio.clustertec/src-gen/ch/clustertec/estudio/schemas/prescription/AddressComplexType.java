//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für addressComplexType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="addressComplexType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="title" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *       &lt;attribute name="titleCode" type="{http://estudio.clustertec.ch/schemas/prescription}titleCode" /&gt;
 *       &lt;attribute name="lastName" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *       &lt;attribute name="firstName" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *       &lt;attribute name="street" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *       &lt;attribute name="zipCode" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *       &lt;attribute name="city" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *       &lt;attribute name="kanton" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string2" /&gt;
 *       &lt;attribute name="country" type="{http://estudio.clustertec.ch/schemas/prescription}string2" /&gt;
 *       &lt;attribute name="phoneNrBusiness" type="{http://estudio.clustertec.ch/schemas/prescription}string20" /&gt;
 *       &lt;attribute name="phoneNrHome" type="{http://estudio.clustertec.ch/schemas/prescription}string20" /&gt;
 *       &lt;attribute name="faxNr" type="{http://estudio.clustertec.ch/schemas/prescription}string20" /&gt;
 *       &lt;attribute name="email" type="{http://estudio.clustertec.ch/schemas/prescription}string32" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "addressComplexType")
@XmlSeeAlso({
    PatientAddress.class,
    PrescriptorAddress.class,
    BillingAddress.class,
    DeliveryAddress.class
})
public class AddressComplexType {

    @XmlAttribute(name = "title")
    protected String title;
    @XmlAttribute(name = "titleCode")
    protected Integer titleCode;
    @XmlAttribute(name = "lastName", required = true)
    protected String lastName;
    @XmlAttribute(name = "firstName")
    protected String firstName;
    @XmlAttribute(name = "street", required = true)
    protected String street;
    @XmlAttribute(name = "zipCode", required = true)
    protected String zipCode;
    @XmlAttribute(name = "city", required = true)
    protected String city;
    @XmlAttribute(name = "kanton", required = true)
    protected String kanton;
    @XmlAttribute(name = "country")
    protected String country;
    @XmlAttribute(name = "phoneNrBusiness")
    protected String phoneNrBusiness;
    @XmlAttribute(name = "phoneNrHome")
    protected String phoneNrHome;
    @XmlAttribute(name = "faxNr")
    protected String faxNr;
    @XmlAttribute(name = "email")
    protected String email;

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Ruft den Wert der titleCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTitleCode() {
        return titleCode;
    }

    /**
     * Legt den Wert der titleCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTitleCode(Integer value) {
        this.titleCode = value;
    }

    /**
     * Ruft den Wert der lastName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Legt den Wert der lastName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Ruft den Wert der firstName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Legt den Wert der firstName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Ruft den Wert der street-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreet() {
        return street;
    }

    /**
     * Legt den Wert der street-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreet(String value) {
        this.street = value;
    }

    /**
     * Ruft den Wert der zipCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Legt den Wert der zipCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZipCode(String value) {
        this.zipCode = value;
    }

    /**
     * Ruft den Wert der city-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCity() {
        return city;
    }

    /**
     * Legt den Wert der city-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCity(String value) {
        this.city = value;
    }

    /**
     * Ruft den Wert der kanton-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKanton() {
        return kanton;
    }

    /**
     * Legt den Wert der kanton-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKanton(String value) {
        this.kanton = value;
    }

    /**
     * Ruft den Wert der country-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountry() {
        return country;
    }

    /**
     * Legt den Wert der country-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountry(String value) {
        this.country = value;
    }

    /**
     * Ruft den Wert der phoneNrBusiness-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNrBusiness() {
        return phoneNrBusiness;
    }

    /**
     * Legt den Wert der phoneNrBusiness-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNrBusiness(String value) {
        this.phoneNrBusiness = value;
    }

    /**
     * Ruft den Wert der phoneNrHome-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNrHome() {
        return phoneNrHome;
    }

    /**
     * Legt den Wert der phoneNrHome-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNrHome(String value) {
        this.phoneNrHome = value;
    }

    /**
     * Ruft den Wert der faxNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFaxNr() {
        return faxNr;
    }

    /**
     * Legt den Wert der faxNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFaxNr(String value) {
        this.faxNr = value;
    }

    /**
     * Ruft den Wert der email-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Legt den Wert der email-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

}
