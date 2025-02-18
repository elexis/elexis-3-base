
package ch.pharmedsolutions.www.interactionservice;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für ArrayOfInteractions complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ArrayOfInteractions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="interaction" type="{https://www.pharmedsolutions.ch/InteractionService}Interaction" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfInteractions", propOrder = { "interaction" })
public class ArrayOfInteractions {

	protected List<Interaction> interaction;

	/**
	 * Gets the value of the interaction property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the interaction property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getInteraction().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Interaction }
	 *
	 *
	 */
	public List<Interaction> getInteraction() {
		if (interaction == null) {
			interaction = new ArrayList<Interaction>();
		}
		return this.interaction;
	}

}
