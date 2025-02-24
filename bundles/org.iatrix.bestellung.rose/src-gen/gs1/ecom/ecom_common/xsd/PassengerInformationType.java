//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import gs1.shared.shared_common.xsd.Description80Type;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für PassengerInformationType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="PassengerInformationType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numberOfPassengers" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/&gt;
 *         &lt;element name="passengerCategoryCode" type="{urn:gs1:ecom:ecom_common:xsd:3}PassengerCategoryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="passengerTariffGroup" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" minOccurs="0"/&gt;
 *         &lt;element name="person" type="{urn:gs1:ecom:ecom_common:xsd:3}PersonType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PassengerInformationType", propOrder = {
    "numberOfPassengers",
    "passengerCategoryCode",
    "passengerTariffGroup",
    "person"
})
public class PassengerInformationType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger numberOfPassengers;
    protected PassengerCategoryCodeType passengerCategoryCode;
    protected Description80Type passengerTariffGroup;
    protected List<PersonType> person;

    /**
     * Ruft den Wert der numberOfPassengers-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberOfPassengers() {
        return numberOfPassengers;
    }

    /**
     * Legt den Wert der numberOfPassengers-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberOfPassengers(BigInteger value) {
        this.numberOfPassengers = value;
    }

    /**
     * Ruft den Wert der passengerCategoryCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link PassengerCategoryCodeType }
     *     
     */
    public PassengerCategoryCodeType getPassengerCategoryCode() {
        return passengerCategoryCode;
    }

    /**
     * Legt den Wert der passengerCategoryCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link PassengerCategoryCodeType }
     *     
     */
    public void setPassengerCategoryCode(PassengerCategoryCodeType value) {
        this.passengerCategoryCode = value;
    }

    /**
     * Ruft den Wert der passengerTariffGroup-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Description80Type }
     *     
     */
    public Description80Type getPassengerTariffGroup() {
        return passengerTariffGroup;
    }

    /**
     * Legt den Wert der passengerTariffGroup-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Description80Type }
     *     
     */
    public void setPassengerTariffGroup(Description80Type value) {
        this.passengerTariffGroup = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the person property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPerson().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PersonType }
     * 
     * 
     */
    public List<PersonType> getPerson() {
        if (person == null) {
            person = new ArrayList<PersonType>();
        }
        return this.person;
    }

}
