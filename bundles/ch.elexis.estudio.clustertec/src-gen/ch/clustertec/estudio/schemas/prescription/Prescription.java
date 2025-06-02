//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.23 um 10:18:39 AM CEST 
//


package ch.clustertec.estudio.schemas.prescription;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}prescriptorAddress"/&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}patientAddress"/&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}deliveryAddress"/&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}billingAddress" minOccurs="0"/&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/prescription}product" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="issueDate" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *       &lt;attribute name="validity" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string10" /&gt;
 *       &lt;attribute name="user" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string12" /&gt;
 *       &lt;attribute name="password" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}string12" /&gt;
 *       &lt;attribute name="prescriptionNr" type="{http://estudio.clustertec.ch/schemas/prescription}string9" /&gt;
 *       &lt;attribute name="deliveryType" use="required" type="{http://estudio.clustertec.ch/schemas/prescription}deliveryType" /&gt;
 *       &lt;attribute name="ignoreInteractions" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="interactionsWithOldPres" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "prescriptorAddress",
    "patientAddress",
    "deliveryAddress",
    "billingAddress",
    "product"
})
@XmlRootElement(name = "prescription")
public class Prescription {

    @XmlElement(required = true)
    protected PrescriptorAddress prescriptorAddress;
    @XmlElement(required = true)
    protected PatientAddress patientAddress;
    @XmlElement(required = true)
    protected DeliveryAddress deliveryAddress;
    protected BillingAddress billingAddress;
    @XmlElement(required = true)
    protected List<Product> product;
    @XmlAttribute(name = "issueDate", required = true)
    protected String issueDate;
    @XmlAttribute(name = "validity", required = true)
    protected String validity;
    @XmlAttribute(name = "user", required = true)
    protected String user;
    @XmlAttribute(name = "password", required = true)
    protected String password;
    @XmlAttribute(name = "prescriptionNr")
    protected String prescriptionNr;
    @XmlAttribute(name = "deliveryType", required = true)
    protected int deliveryType;
    @XmlAttribute(name = "ignoreInteractions", required = true)
    protected boolean ignoreInteractions;
    @XmlAttribute(name = "interactionsWithOldPres", required = true)
    protected boolean interactionsWithOldPres;

    /**
     * Ruft den Wert der prescriptorAddress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PrescriptorAddress }
     *     
     */
    public PrescriptorAddress getPrescriptorAddress() {
        return prescriptorAddress;
    }

    /**
     * Legt den Wert der prescriptorAddress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PrescriptorAddress }
     *     
     */
    public void setPrescriptorAddress(PrescriptorAddress value) {
        this.prescriptorAddress = value;
    }

    /**
     * Ruft den Wert der patientAddress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PatientAddress }
     *     
     */
    public PatientAddress getPatientAddress() {
        return patientAddress;
    }

    /**
     * Legt den Wert der patientAddress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientAddress }
     *     
     */
    public void setPatientAddress(PatientAddress value) {
        this.patientAddress = value;
    }

    /**
     * Ruft den Wert der deliveryAddress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryAddress }
     *     
     */
    public DeliveryAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Legt den Wert der deliveryAddress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryAddress }
     *     
     */
    public void setDeliveryAddress(DeliveryAddress value) {
        this.deliveryAddress = value;
    }

    /**
     * Ruft den Wert der billingAddress-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BillingAddress }
     *     
     */
    public BillingAddress getBillingAddress() {
        return billingAddress;
    }

    /**
     * Legt den Wert der billingAddress-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BillingAddress }
     *     
     */
    public void setBillingAddress(BillingAddress value) {
        this.billingAddress = value;
    }

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
     * Ruft den Wert der issueDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIssueDate() {
        return issueDate;
    }

    /**
     * Legt den Wert der issueDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIssueDate(String value) {
        this.issueDate = value;
    }

    /**
     * Ruft den Wert der validity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValidity() {
        return validity;
    }

    /**
     * Legt den Wert der validity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValidity(String value) {
        this.validity = value;
    }

    /**
     * Ruft den Wert der user-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUser() {
        return user;
    }

    /**
     * Legt den Wert der user-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * Ruft den Wert der password-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Legt den Wert der password-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Ruft den Wert der prescriptionNr-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrescriptionNr() {
        return prescriptionNr;
    }

    /**
     * Legt den Wert der prescriptionNr-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrescriptionNr(String value) {
        this.prescriptionNr = value;
    }

    /**
     * Ruft den Wert der deliveryType-Eigenschaft ab.
     * 
     */
    public int getDeliveryType() {
        return deliveryType;
    }

    /**
     * Legt den Wert der deliveryType-Eigenschaft fest.
     * 
     */
    public void setDeliveryType(int value) {
        this.deliveryType = value;
    }

    /**
     * Ruft den Wert der ignoreInteractions-Eigenschaft ab.
     * 
     */
    public boolean isIgnoreInteractions() {
        return ignoreInteractions;
    }

    /**
     * Legt den Wert der ignoreInteractions-Eigenschaft fest.
     * 
     */
    public void setIgnoreInteractions(boolean value) {
        this.ignoreInteractions = value;
    }

    /**
     * Ruft den Wert der interactionsWithOldPres-Eigenschaft ab.
     * 
     */
    public boolean isInteractionsWithOldPres() {
        return interactionsWithOldPres;
    }

    /**
     * Legt den Wert der interactionsWithOldPres-Eigenschaft fest.
     * 
     */
    public void setInteractionsWithOldPres(boolean value) {
        this.interactionsWithOldPres = value;
    }

}
