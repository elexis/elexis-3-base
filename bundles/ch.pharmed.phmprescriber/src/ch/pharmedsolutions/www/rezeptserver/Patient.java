
package ch.pharmedsolutions.www.rezeptserver;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für Patient complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="Patient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="last_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="first_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="birth_date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="covercard" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Patient", propOrder = {

})
public class Patient {

	@XmlElement(name = "last_name", required = true)
	protected String lastName;
	@XmlElement(name = "first_name", required = true)
	protected String firstName;
	@XmlElement(name = "birth_date", required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar birthDate;
	@XmlElement(required = true, nillable = true)
	protected String covercard;

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
	 * Ruft den Wert der birthDate-Eigenschaft ab.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getBirthDate() {
		return birthDate;
	}

	/**
	 * Legt den Wert der birthDate-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setBirthDate(XMLGregorianCalendar value) {
		this.birthDate = value;
	}

	/**
	 * Ruft den Wert der covercard-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getCovercard() {
		return covercard;
	}

	/**
	 * Legt den Wert der covercard-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setCovercard(String value) {
		this.covercard = value;
	}

}
