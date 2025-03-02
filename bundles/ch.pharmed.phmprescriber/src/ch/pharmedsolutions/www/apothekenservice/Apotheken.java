
package ch.pharmedsolutions.www.apothekenservice;

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
 *         &lt;element name="apotheken" type="{https://www.pharmedsolutions.ch/ApothekenService}ArrayOfApotheken"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "apotheken" })
@XmlRootElement(name = "Apotheken")
public class Apotheken {

	@XmlElement(required = true)
	protected ArrayOfApotheken apotheken;

	/**
	 * Ruft den Wert der apotheken-Eigenschaft ab.
	 *
	 * @return possible object is {@link ArrayOfApotheken }
	 *
	 */
	public ArrayOfApotheken getApotheken() {
		return apotheken;
	}

	/**
	 * Legt den Wert der apotheken-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link ArrayOfApotheken }
	 *
	 */
	public void setApotheken(ArrayOfApotheken value) {
		this.apotheken = value;
	}

}
