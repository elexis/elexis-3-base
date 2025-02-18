//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AddressType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AddressType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="city" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cityCode" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="countryCode" type="{urn:gs1:shared:shared_common:xsd:3}CountryCodeType" minOccurs="0"/&gt;
 *         &lt;element name="countyCode" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="crossStreet" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="currencyOfPartyCode" type="{urn:gs1:shared:shared_common:xsd:3}CurrencyCodeType" minOccurs="0"/&gt;
 *         &lt;element name="languageOfThePartyCode" type="{urn:gs1:shared:shared_common:xsd:3}LanguageCodeType" minOccurs="0"/&gt;
 *         &lt;element name="name" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="pOBoxNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="postalCode" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="provinceCode" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="state" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="streetAddressOne" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="streetAddressTwo" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="streetAddressThree" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="200"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="geographicalCoordinates" type="{urn:gs1:shared:shared_common:xsd:3}GeographicalCoordinatesType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AddressType", propOrder = {
    "city",
    "cityCode",
    "countryCode",
    "countyCode",
    "crossStreet",
    "currencyOfPartyCode",
    "languageOfThePartyCode",
    "name",
    "poBoxNumber",
    "postalCode",
    "provinceCode",
    "state",
    "streetAddressOne",
    "streetAddressTwo",
    "streetAddressThree",
    "geographicalCoordinates"
})
public class AddressType {

    protected String city;
    protected String cityCode;
    protected CountryCodeType countryCode;
    protected String countyCode;
    protected String crossStreet;
    protected CurrencyCodeType currencyOfPartyCode;
    protected LanguageCodeType languageOfThePartyCode;
    protected String name;
    @XmlElement(name = "pOBoxNumber")
    protected String poBoxNumber;
    protected String postalCode;
    protected String provinceCode;
    protected String state;
    protected String streetAddressOne;
    protected String streetAddressTwo;
    protected String streetAddressThree;
    protected GeographicalCoordinatesType geographicalCoordinates;

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
     * Ruft den Wert der cityCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCityCode() {
        return cityCode;
    }

    /**
     * Legt den Wert der cityCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCityCode(String value) {
        this.cityCode = value;
    }

    /**
     * Ruft den Wert der countryCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CountryCodeType }
     *     
     */
    public CountryCodeType getCountryCode() {
        return countryCode;
    }

    /**
     * Legt den Wert der countryCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CountryCodeType }
     *     
     */
    public void setCountryCode(CountryCodeType value) {
        this.countryCode = value;
    }

    /**
     * Ruft den Wert der countyCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCountyCode() {
        return countyCode;
    }

    /**
     * Legt den Wert der countyCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCountyCode(String value) {
        this.countyCode = value;
    }

    /**
     * Ruft den Wert der crossStreet-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrossStreet() {
        return crossStreet;
    }

    /**
     * Legt den Wert der crossStreet-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrossStreet(String value) {
        this.crossStreet = value;
    }

    /**
     * Ruft den Wert der currencyOfPartyCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyCodeType }
     *     
     */
    public CurrencyCodeType getCurrencyOfPartyCode() {
        return currencyOfPartyCode;
    }

    /**
     * Legt den Wert der currencyOfPartyCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyCodeType }
     *     
     */
    public void setCurrencyOfPartyCode(CurrencyCodeType value) {
        this.currencyOfPartyCode = value;
    }

    /**
     * Ruft den Wert der languageOfThePartyCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link LanguageCodeType }
     *     
     */
    public LanguageCodeType getLanguageOfThePartyCode() {
        return languageOfThePartyCode;
    }

    /**
     * Legt den Wert der languageOfThePartyCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link LanguageCodeType }
     *     
     */
    public void setLanguageOfThePartyCode(LanguageCodeType value) {
        this.languageOfThePartyCode = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der poBoxNumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOBoxNumber() {
        return poBoxNumber;
    }

    /**
     * Legt den Wert der poBoxNumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOBoxNumber(String value) {
        this.poBoxNumber = value;
    }

    /**
     * Ruft den Wert der postalCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Legt den Wert der postalCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostalCode(String value) {
        this.postalCode = value;
    }

    /**
     * Ruft den Wert der provinceCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProvinceCode() {
        return provinceCode;
    }

    /**
     * Legt den Wert der provinceCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProvinceCode(String value) {
        this.provinceCode = value;
    }

    /**
     * Ruft den Wert der state-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Legt den Wert der state-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Ruft den Wert der streetAddressOne-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetAddressOne() {
        return streetAddressOne;
    }

    /**
     * Legt den Wert der streetAddressOne-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetAddressOne(String value) {
        this.streetAddressOne = value;
    }

    /**
     * Ruft den Wert der streetAddressTwo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetAddressTwo() {
        return streetAddressTwo;
    }

    /**
     * Legt den Wert der streetAddressTwo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetAddressTwo(String value) {
        this.streetAddressTwo = value;
    }

    /**
     * Ruft den Wert der streetAddressThree-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStreetAddressThree() {
        return streetAddressThree;
    }

    /**
     * Legt den Wert der streetAddressThree-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStreetAddressThree(String value) {
        this.streetAddressThree = value;
    }

    /**
     * Ruft den Wert der geographicalCoordinates-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GeographicalCoordinatesType }
     *     
     */
    public GeographicalCoordinatesType getGeographicalCoordinates() {
        return geographicalCoordinates;
    }

    /**
     * Legt den Wert der geographicalCoordinates-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GeographicalCoordinatesType }
     *     
     */
    public void setGeographicalCoordinates(GeographicalCoordinatesType value) {
        this.geographicalCoordinates = value;
    }

}
