
package ch.pharmedsolutions.www.apothekenservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für Apotheke complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="Apotheke">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="city" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gln_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Apotheke", propOrder = {

})
public class Apotheke {

	@XmlElement(required = true)
	protected String name;
	@XmlElement(required = true)
	protected String city;
	@XmlElement(name = "gln_id", required = true)
	protected String glnId;

	/**
	 * Ruft den Wert der name-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * Legt den Wert der name-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setName(String value) {
		this.name = value;
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

}
