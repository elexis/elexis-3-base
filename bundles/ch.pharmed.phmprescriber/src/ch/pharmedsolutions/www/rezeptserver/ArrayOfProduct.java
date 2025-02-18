
package ch.pharmedsolutions.www.rezeptserver;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für ArrayOfProduct complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="ArrayOfProduct">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="item" type="{https://www.pharmedsolutions.ch/RezeptServer}Product" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfProduct", propOrder = { "item" })
public class ArrayOfProduct {

	protected List<Product> item;

	/**
	 * Gets the value of the item property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the item property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getItem().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Product }
	 *
	 *
	 */
	public List<Product> getItem() {
		if (item == null) {
			item = new ArrayList<Product>();
		}
		return this.item;
	}

}
