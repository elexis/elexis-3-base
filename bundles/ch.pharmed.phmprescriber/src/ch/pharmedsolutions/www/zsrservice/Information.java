
package ch.pharmedsolutions.www.zsrservice;

import java.math.BigInteger;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="gln_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="first_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="last_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="street" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pobox" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zip" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="phone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="fax" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "glnId", "title", "firstName", "lastName", "street", "pobox", "zip",
		"city", "phone", "fax" })
@XmlRootElement(name = "Information")
public class Information {

	@XmlElement(name = "gln_id", required = true, nillable = true)
	protected String glnId;
	@XmlElement(required = true, nillable = true)
	protected String title;
	@XmlElement(name = "first_name", required = true, nillable = true)
	protected String firstName;
	@XmlElement(name = "last_name", required = true, nillable = true)
	protected String lastName;
	@XmlElement(required = true, nillable = true)
	protected String street;
	@XmlElement(required = true, nillable = true)
	protected String pobox;
	@XmlElement(required = true, nillable = true)
	protected BigInteger zip;
	@XmlElement(required = true, nillable = true)
	protected String city;
	@XmlElement(required = true, nillable = true)
	protected String phone;
	@XmlElement(required = true, nillable = true)
	protected String fax;

	/**
	 * Ruft den Wert der glnId-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getGlnId() {
		return glnId;
	}

	/**
	 * Legt den Wert der glnId-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setGlnId(String value) {
		this.glnId = value;
	}

	/**
	 * Ruft den Wert der title-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Legt den Wert der title-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setTitle(String value) {
		this.title = value;
	}

	/**
	 * Ruft den Wert der firstName-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Legt den Wert der firstName-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setFirstName(String value) {
		this.firstName = value;
	}

	/**
	 * Ruft den Wert der lastName-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Legt den Wert der lastName-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setLastName(String value) {
		this.lastName = value;
	}

	/**
	 * Ruft den Wert der street-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * Legt den Wert der street-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setStreet(String value) {
		this.street = value;
	}

	/**
	 * Ruft den Wert der pobox-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getPobox() {
		return pobox;
	}

	/**
	 * Legt den Wert der pobox-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setPobox(String value) {
		this.pobox = value;
	}

	/**
	 * Ruft den Wert der zip-Eigenschaft ab.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getZip() {
		return zip;
	}

	/**
	 * Legt den Wert der zip-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setZip(BigInteger value) {
		this.zip = value;
	}

	/**
	 * Ruft den Wert der city-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Legt den Wert der city-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setCity(String value) {
		this.city = value;
	}

	/**
	 * Ruft den Wert der phone-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * Legt den Wert der phone-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setPhone(String value) {
		this.phone = value;
	}

	/**
	 * Ruft den Wert der fax-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getFax() {
		return fax;
	}

	/**
	 * Legt den Wert der fax-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setFax(String value) {
		this.fax = value;
	}

}
