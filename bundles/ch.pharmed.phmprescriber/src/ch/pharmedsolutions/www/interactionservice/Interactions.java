
package ch.pharmedsolutions.www.interactionservice;

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
 *         &lt;element name="interactions" type="{https://www.pharmedsolutions.ch/InteractionService}ArrayOfInteractions"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "interactions" })
@XmlRootElement(name = "Interactions")
public class Interactions {

	@XmlElement(required = true)
	protected ArrayOfInteractions interactions;

	/**
	 * Ruft den Wert der interactions-Eigenschaft ab.
	 *
	 * @return possible object is {@link ArrayOfInteractions }
	 *
	 */
	public ArrayOfInteractions getInteractions() {
		return interactions;
	}

	/**
	 * Legt den Wert der interactions-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link ArrayOfInteractions }
	 *
	 */
	public void setInteractions(ArrayOfInteractions value) {
		this.interactions = value;
	}

}
