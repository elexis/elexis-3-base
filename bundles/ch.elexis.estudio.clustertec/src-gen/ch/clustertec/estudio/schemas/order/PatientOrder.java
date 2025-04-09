//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.09 um 01:08:44 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/order}product" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="titlePatient" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="titleCodePatient" type="{http://estudio.clustertec.ch/schemas/order}titleCode" /&gt;
 *       &lt;attribute name="lastNamePatient" use="required" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="firstNamePatient" use="required" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="birthdayPatient" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *       &lt;attribute name="sexPatient" use="required" type="{http://estudio.clustertec.ch/schemas/order}sex" /&gt;
 *       &lt;attribute name="patientNr" use="required" type="{http://estudio.clustertec.ch/schemas/order}string15" /&gt;
 *       &lt;attribute name="firstNamePrescriptor" use="required" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="lastNamePrescriptor" use="required" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="clientNrPrescriptor" use="required" type="{http://estudio.clustertec.ch/schemas/order}string6" /&gt;
 *       &lt;attribute name="eanNrPrescriptor" use="required" type="{http://estudio.clustertec.ch/schemas/order}eanId" /&gt;
 *       &lt;attribute name="firstNameSubstitutedPrescriptor" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="lastNameSubstitutedPrescriptor" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="clientNrSubstitutedPrescriptor" type="{http://estudio.clustertec.ch/schemas/order}string6" /&gt;
 *       &lt;attribute name="eanNrSubstitutedPrescriptor" type="{http://estudio.clustertec.ch/schemas/order}eanId" /&gt;
 *       &lt;attribute name="customText" type="{http://estudio.clustertec.ch/schemas/order}string250" /&gt;
 *       &lt;attribute name="orderNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "product"
})
@XmlRootElement(name = "patientOrder")
public class PatientOrder {

    @XmlElement(required = true)
    protected List<Product> product;
    @XmlAttribute(name = "titlePatient")
    protected String titlePatient;
    @XmlAttribute(name = "titleCodePatient")
    protected Integer titleCodePatient;
    @XmlAttribute(name = "lastNamePatient", required = true)
    protected String lastNamePatient;
    @XmlAttribute(name = "firstNamePatient", required = true)
    protected String firstNamePatient;
    @XmlAttribute(name = "birthdayPatient", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar birthdayPatient;
    @XmlAttribute(name = "sexPatient", required = true)
    protected int sexPatient;
    @XmlAttribute(name = "patientNr", required = true)
    protected String patientNr;
    @XmlAttribute(name = "firstNamePrescriptor", required = true)
    protected String firstNamePrescriptor;
    @XmlAttribute(name = "lastNamePrescriptor", required = true)
    protected String lastNamePrescriptor;
    @XmlAttribute(name = "clientNrPrescriptor", required = true)
    protected String clientNrPrescriptor;
    @XmlAttribute(name = "eanNrPrescriptor", required = true)
    protected String eanNrPrescriptor;
    @XmlAttribute(name = "firstNameSubstitutedPrescriptor")
    protected String firstNameSubstitutedPrescriptor;
    @XmlAttribute(name = "lastNameSubstitutedPrescriptor")
    protected String lastNameSubstitutedPrescriptor;
    @XmlAttribute(name = "clientNrSubstitutedPrescriptor")
    protected String clientNrSubstitutedPrescriptor;
    @XmlAttribute(name = "eanNrSubstitutedPrescriptor")
    protected String eanNrSubstitutedPrescriptor;
    @XmlAttribute(name = "customText")
    protected String customText;
    @XmlAttribute(name = "orderNumber", required = true)
    protected int orderNumber;

    /**
     * Gets the value of the product property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the product property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProduct().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Product }
     * 
     * 
     */
    public List<Product> getProduct() {
        if (product == null) {
            product = new ArrayList<Product>();
        }
        return this.product;
    }

    /**
     * Ruft den Wert der titlePatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitlePatient() {
        return titlePatient;
    }

    /**
     * Legt den Wert der titlePatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitlePatient(String value) {
        this.titlePatient = value;
    }

    /**
     * Ruft den Wert der titleCodePatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTitleCodePatient() {
        return titleCodePatient;
    }

    /**
     * Legt den Wert der titleCodePatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTitleCodePatient(Integer value) {
        this.titleCodePatient = value;
    }

    /**
     * Ruft den Wert der lastNamePatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastNamePatient() {
        return lastNamePatient;
    }

    /**
     * Legt den Wert der lastNamePatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastNamePatient(String value) {
        this.lastNamePatient = value;
    }

    /**
     * Ruft den Wert der firstNamePatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstNamePatient() {
        return firstNamePatient;
    }

    /**
     * Legt den Wert der firstNamePatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstNamePatient(String value) {
        this.firstNamePatient = value;
    }

    /**
     * Ruft den Wert der birthdayPatient-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthdayPatient() {
        return birthdayPatient;
    }

    /**
     * Legt den Wert der birthdayPatient-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthdayPatient(XMLGregorianCalendar value) {
        this.birthdayPatient = value;
    }

    /**
     * Ruft den Wert der sexPatient-Eigenschaft ab.
     * 
     */
    public int getSexPatient() {
        return sexPatient;
    }

    /**
     * Legt den Wert der sexPatient-Eigenschaft fest.
     * 
     */
    public void setSexPatient(int value) {
        this.sexPatient = value;
    }

    /**
     * Ruft den Wert der patientNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPatientNr() {
        return patientNr;
    }

    /**
     * Legt den Wert der patientNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPatientNr(String value) {
        this.patientNr = value;
    }

    /**
     * Ruft den Wert der firstNamePrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstNamePrescriptor() {
        return firstNamePrescriptor;
    }

    /**
     * Legt den Wert der firstNamePrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstNamePrescriptor(String value) {
        this.firstNamePrescriptor = value;
    }

    /**
     * Ruft den Wert der lastNamePrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastNamePrescriptor() {
        return lastNamePrescriptor;
    }

    /**
     * Legt den Wert der lastNamePrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastNamePrescriptor(String value) {
        this.lastNamePrescriptor = value;
    }

    /**
     * Ruft den Wert der clientNrPrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNrPrescriptor() {
        return clientNrPrescriptor;
    }

    /**
     * Legt den Wert der clientNrPrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNrPrescriptor(String value) {
        this.clientNrPrescriptor = value;
    }

    /**
     * Ruft den Wert der eanNrPrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEanNrPrescriptor() {
        return eanNrPrescriptor;
    }

    /**
     * Legt den Wert der eanNrPrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEanNrPrescriptor(String value) {
        this.eanNrPrescriptor = value;
    }

    /**
     * Ruft den Wert der firstNameSubstitutedPrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstNameSubstitutedPrescriptor() {
        return firstNameSubstitutedPrescriptor;
    }

    /**
     * Legt den Wert der firstNameSubstitutedPrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstNameSubstitutedPrescriptor(String value) {
        this.firstNameSubstitutedPrescriptor = value;
    }

    /**
     * Ruft den Wert der lastNameSubstitutedPrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastNameSubstitutedPrescriptor() {
        return lastNameSubstitutedPrescriptor;
    }

    /**
     * Legt den Wert der lastNameSubstitutedPrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastNameSubstitutedPrescriptor(String value) {
        this.lastNameSubstitutedPrescriptor = value;
    }

    /**
     * Ruft den Wert der clientNrSubstitutedPrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNrSubstitutedPrescriptor() {
        return clientNrSubstitutedPrescriptor;
    }

    /**
     * Legt den Wert der clientNrSubstitutedPrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNrSubstitutedPrescriptor(String value) {
        this.clientNrSubstitutedPrescriptor = value;
    }

    /**
     * Ruft den Wert der eanNrSubstitutedPrescriptor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEanNrSubstitutedPrescriptor() {
        return eanNrSubstitutedPrescriptor;
    }

    /**
     * Legt den Wert der eanNrSubstitutedPrescriptor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEanNrSubstitutedPrescriptor(String value) {
        this.eanNrSubstitutedPrescriptor = value;
    }

    /**
     * Ruft den Wert der customText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomText() {
        return customText;
    }

    /**
     * Legt den Wert der customText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomText(String value) {
        this.customText = value;
    }

    /**
     * Ruft den Wert der orderNumber-Eigenschaft ab.
     * 
     */
    public int getOrderNumber() {
        return orderNumber;
    }

    /**
     * Legt den Wert der orderNumber-Eigenschaft fest.
     * 
     */
    public void setOrderNumber(int value) {
        this.orderNumber = value;
    }

}
