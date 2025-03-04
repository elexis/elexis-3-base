//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.05.20 um 02:10:33 PM CEST 
//


package ch.fd.invoice450.request;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für patientAddressType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="patientAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="person" type="{http://www.forum-datenaustausch.ch/invoice}personType"/>
 *         &lt;element name="card" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="card_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *                 &lt;attribute name="expiry_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                 &lt;attribute name="validation_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                 &lt;attribute name="validation_id" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *                 &lt;attribute name="validation_server" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="gender" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="male"/>
 *             &lt;enumeration value="female"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="birthdate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="ssn" type="{http://www.forum-datenaustausch.ch/invoice}ssnPartyType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "patientAddressType", propOrder = {
    "person",
    "card"
})
public class PatientAddressType {

    @XmlElement(required = true)
    protected PersonType person;
    protected PatientAddressType.Card card;
    @XmlAttribute(name = "gender", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String gender;
    @XmlAttribute(name = "birthdate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar birthdate;
    @XmlAttribute(name = "ssn")
    protected String ssn;

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
     * Ruft den Wert der card-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PatientAddressType.Card }
     *     
     */
    public PatientAddressType.Card getCard() {
        return card;
    }

    /**
     * Legt den Wert der card-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PatientAddressType.Card }
     *     
     */
    public void setCard(PatientAddressType.Card value) {
        this.card = value;
    }

    /**
     * Ruft den Wert der gender-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGender() {
        return gender;
    }

    /**
     * Legt den Wert der gender-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGender(String value) {
        this.gender = value;
    }

    /**
     * Ruft den Wert der birthdate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBirthdate() {
        return birthdate;
    }

    /**
     * Legt den Wert der birthdate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBirthdate(XMLGregorianCalendar value) {
        this.birthdate = value;
    }

    /**
     * Ruft den Wert der ssn-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSsn() {
        return ssn;
    }

    /**
     * Legt den Wert der ssn-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSsn(String value) {
        this.ssn = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="card_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
     *       &lt;attribute name="expiry_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *       &lt;attribute name="validation_date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *       &lt;attribute name="validation_id" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
     *       &lt;attribute name="validation_server" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = StringUtils.EMPTY)
    public static class Card {

        @XmlAttribute(name = "card_id", required = true)
        protected String cardId;
        @XmlAttribute(name = "expiry_date")
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar expiryDate;
        @XmlAttribute(name = "validation_date")
        @XmlSchemaType(name = "dateTime")
        protected XMLGregorianCalendar validationDate;
        @XmlAttribute(name = "validation_id")
        protected String validationId;
        @XmlAttribute(name = "validation_server")
        protected String validationServer;

        /**
         * Ruft den Wert der cardId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCardId() {
            return cardId;
        }

        /**
         * Legt den Wert der cardId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCardId(String value) {
            this.cardId = value;
        }

        /**
         * Ruft den Wert der expiryDate-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getExpiryDate() {
            return expiryDate;
        }

        /**
         * Legt den Wert der expiryDate-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setExpiryDate(XMLGregorianCalendar value) {
            this.expiryDate = value;
        }

        /**
         * Ruft den Wert der validationDate-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getValidationDate() {
            return validationDate;
        }

        /**
         * Legt den Wert der validationDate-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setValidationDate(XMLGregorianCalendar value) {
            this.validationDate = value;
        }

        /**
         * Ruft den Wert der validationId-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValidationId() {
            return validationId;
        }

        /**
         * Legt den Wert der validationId-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValidationId(String value) {
            this.validationId = value;
        }

        /**
         * Ruft den Wert der validationServer-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValidationServer() {
            return validationServer;
        }

        /**
         * Legt den Wert der validationServer-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValidationServer(String value) {
            this.validationServer = value;
        }

    }

}
