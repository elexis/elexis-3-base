
package ch.pharmedsolutions.www.interactionservice;

import java.util.ArrayList;
import java.util.List;

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
 *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="atc_codes" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "key", "atcCodes" })
@XmlRootElement(name = "InteractionRequest")
public class InteractionRequest {

	@XmlElement(required = true)
	protected String key;
	@XmlElement(name = "atc_codes", required = true)
	protected List<String> atcCodes;

	/**
	 * Ruft den Wert der key-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Legt den Wert der key-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setKey(String value) {
		this.key = value;
	}

	/**
	 * Gets the value of the atcCodes property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the atcCodes property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getAtcCodes().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 *
	 *
	 */
	public List<String> getAtcCodes() {
		if (atcCodes == null) {
			atcCodes = new ArrayList<String>();
		}
		return this.atcCodes;
	}

}
