
package ch.pharmedsolutions.www.rezeptserver;

import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="software" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="zsr_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="accident_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="accident_date" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="products" type="{https://www.pharmedsolutions.ch/RezeptServer}ArrayOfProduct"/>
 *         &lt;element name="patient" type="{https://www.pharmedsolutions.ch/RezeptServer}Patient"/>
 *         &lt;element name="GLN_targetpharmacy" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "software", "password", "zsrId", "date", "accidentId", "accidentDate",
		"products", "patient", "glnTargetpharmacy" })
@XmlRootElement(name = "Prescription")
public class Prescription {

	@XmlElement(required = true)
	protected BigInteger software;
	@XmlElement(required = true)
	protected String password;
	@XmlElement(name = "zsr_id", required = true)
	protected String zsrId;
	@XmlElement(required = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar date;
	@XmlElement(name = "accident_id", required = true, nillable = true)
	protected String accidentId;
	@XmlElement(name = "accident_date", required = true, nillable = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar accidentDate;
	@XmlElement(required = true)
	protected ArrayOfProduct products;
	@XmlElement(required = true)
	protected Patient patient;
	@XmlElement(name = "GLN_targetpharmacy", required = true, nillable = true)
	protected String glnTargetpharmacy;

	/**
	 * Ruft den Wert der software-Eigenschaft ab.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getSoftware() {
		return software;
	}

	/**
	 * Legt den Wert der software-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setSoftware(BigInteger value) {
		this.software = value;
	}

	/**
	 * Ruft den Wert der password-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Legt den Wert der password-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setPassword(String value) {
		this.password = value;
	}

	/**
	 * Ruft den Wert der zsrId-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getZsrId() {
		return zsrId;
	}

	/**
	 * Legt den Wert der zsrId-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setZsrId(String value) {
		this.zsrId = value;
	}

	/**
	 * Ruft den Wert der date-Eigenschaft ab.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getDate() {
		return date;
	}

	/**
	 * Legt den Wert der date-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setDate(XMLGregorianCalendar value) {
		this.date = value;
	}

	/**
	 * Ruft den Wert der accidentId-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getAccidentId() {
		return accidentId;
	}

	/**
	 * Legt den Wert der accidentId-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setAccidentId(String value) {
		this.accidentId = value;
	}

	/**
	 * Ruft den Wert der accidentDate-Eigenschaft ab.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getAccidentDate() {
		return accidentDate;
	}

	/**
	 * Legt den Wert der accidentDate-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setAccidentDate(XMLGregorianCalendar value) {
		this.accidentDate = value;
	}

	/**
	 * Ruft den Wert der products-Eigenschaft ab.
	 *
	 * @return possible object is {@link ArrayOfProduct }
	 *
	 */
	public ArrayOfProduct getProducts() {
		return products;
	}

	/**
	 * Legt den Wert der products-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link ArrayOfProduct }
	 *
	 */
	public void setProducts(ArrayOfProduct value) {
		this.products = value;
	}

	/**
	 * Ruft den Wert der patient-Eigenschaft ab.
	 *
	 * @return possible object is {@link Patient }
	 *
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Legt den Wert der patient-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link Patient }
	 *
	 */
	public void setPatient(Patient value) {
		this.patient = value;
	}

	/**
	 * Ruft den Wert der glnTargetpharmacy-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getGLNTargetpharmacy() {
		return glnTargetpharmacy;
	}

	/**
	 * Legt den Wert der glnTargetpharmacy-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setGLNTargetpharmacy(String value) {
		this.glnTargetpharmacy = value;
	}

}
