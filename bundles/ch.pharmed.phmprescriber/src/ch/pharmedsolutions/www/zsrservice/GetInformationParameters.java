
package ch.pharmedsolutions.www.zsrservice;

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
 *         &lt;element name="zsr_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "zsrId" })
@XmlRootElement(name = "getInformationParameters")
public class GetInformationParameters {

	@XmlElement(name = "zsr_id", required = true)
	protected String zsrId;

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

}
