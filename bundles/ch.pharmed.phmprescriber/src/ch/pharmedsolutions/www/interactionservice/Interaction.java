
package ch.pharmedsolutions.www.interactionservice;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für Interaction complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="Interaction">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="atc_code1" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="atc_code2" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="descr" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Interaction", propOrder = {

})
public class Interaction {

	@XmlElement(name = "atc_code1", required = true)
	protected String atcCode1;
	@XmlElement(name = "atc_code2", required = true)
	protected String atcCode2;
	@XmlElement(required = true)
	protected String descr;

	/**
	 * Ruft den Wert der atcCode1-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getAtcCode1() {
		return atcCode1;
	}

	/**
	 * Legt den Wert der atcCode1-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setAtcCode1(String value) {
		this.atcCode1 = value;
	}

	/**
	 * Ruft den Wert der atcCode2-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getAtcCode2() {
		return atcCode2;
	}

	/**
	 * Legt den Wert der atcCode2-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setAtcCode2(String value) {
		this.atcCode2 = value;
	}

	/**
	 * Ruft den Wert der descr-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getDescr() {
		return descr;
	}

	/**
	 * Legt den Wert der descr-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setDescr(String value) {
		this.descr = value;
	}

}
