//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.2 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.10.01 um 10:54:57 AM CEST 
//


package ch.fd.invoice500.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für partnerAddressType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="partnerAddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="company" type="{http://www.forum-datenaustausch.ch/invoice}companyType"/&gt;
 *         &lt;element name="person" type="{http://www.forum-datenaustausch.ch/invoice}personType"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="gln" use="required" type="{http://www.forum-datenaustausch.ch/invoice}glnPartyType" /&gt;
 *       &lt;attribute name="type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *             &lt;enumeration value="employer"/&gt;
 *             &lt;enumeration value="referrer"/&gt;
 *             &lt;enumeration value="service_provider"/&gt;
 *             &lt;enumeration value="primary_clinician"/&gt;
 *             &lt;enumeration value="lead_doctor"/&gt;
 *             &lt;enumeration value="assistant_physician"/&gt;
 *             &lt;enumeration value="senior_physician"/&gt;
 *             &lt;enumeration value="chief_physician"/&gt;
 *             &lt;enumeration value="surgeon"/&gt;
 *             &lt;enumeration value="anaesthetist"/&gt;
 *             &lt;enumeration value="consultant_doctor"/&gt;
 *             &lt;enumeration value="internist"/&gt;
 *             &lt;enumeration value="co-care"/&gt;
 *             &lt;enumeration value="radiologist"/&gt;
 *             &lt;enumeration value="nuclear_medicine"/&gt;
 *             &lt;enumeration value="other"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="type_title" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_100" /&gt;
 *       &lt;attribute name="zsr" type="{http://www.forum-datenaustausch.ch/invoice}zsrPartyType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "partnerAddressType", propOrder = {
    "company",
    "person"
})
public class PartnerAddressType {

    protected CompanyType company;
    protected PersonType person;
    @XmlAttribute(name = "gln", required = true)
    protected String gln;
    @XmlAttribute(name = "type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "type_title")
    protected String typeTitle;
    @XmlAttribute(name = "zsr")
    protected String zsr;

    /**
     * Ruft den Wert der company-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CompanyType }
     *     
     */
    public CompanyType getCompany() {
        return company;
    }

    /**
     * Legt den Wert der company-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyType }
     *     
     */
    public void setCompany(CompanyType value) {
        this.company = value;
    }

    /**
     * Ruft den Wert der person-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PersonType }
     *     
     */
    public PersonType getPerson() {
        return person;
    }

    /**
     * Legt den Wert der person-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *     
     */
    public void setPerson(PersonType value) {
        this.person = value;
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
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der typeTitle-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTypeTitle() {
        return typeTitle;
    }

    /**
     * Legt den Wert der typeTitle-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTypeTitle(String value) {
        this.typeTitle = value;
    }

    /**
     * Ruft den Wert der zsr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZsr() {
        return zsr;
    }

    /**
     * Legt den Wert der zsr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZsr(String value) {
        this.zsr = value;
    }

}
