package org.hl7.v3;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for StrucDoc.FootnoteRef complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="StrucDoc.FootnoteRef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="ID" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       &lt;attribute name="language" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" />
 *       &lt;attribute name="styleCode" type="{http://www.w3.org/2001/XMLSchema}NMTOKENS" />
 *       &lt;attribute name="IDREF" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StrucDoc.FootnoteRef")
public class StrucDocFootnoteRef {

	@XmlAttribute(name = "ID")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String attributeId;
	@XmlAttribute
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlSchemaType(name = "NMTOKEN")
	protected String language;
	@XmlAttribute
	@XmlSchemaType(name = "NMTOKENS")
	protected List<String> styleCode;
	@XmlAttribute(name = "IDREF", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object idref;

	/**
	 * Gets the value of the attributeId property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getAttributeId() {
		return attributeId;
	}

	/**
	 * Sets the value of the attributeId property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setAttributeId(String value) {
		this.attributeId = value;
	}

	/**
	 * Gets the value of the language property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the value of the language property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setLanguage(String value) {
		this.language = value;
	}

	/**
	 * Gets the value of the styleCode property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the styleCode property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getStyleCode().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 *
	 *
	 */
	public List<String> getStyleCode() {
		if (styleCode == null) {
			styleCode = new ArrayList<String>();
		}
		return this.styleCode;
	}

	/**
	 * Gets the value of the idref property.
	 *
	 * @return possible object is {@link Object }
	 *
	 */
	public Object getIDREF() {
		return idref;
	}

	/**
	 * Sets the value of the idref property.
	 *
	 * @param value allowed object is {@link Object }
	 *
	 */
	public void setIDREF(Object value) {
		this.idref = value;
	}

}
