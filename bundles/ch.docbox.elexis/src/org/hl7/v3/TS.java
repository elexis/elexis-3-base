package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 *
 * A quantity specifying a point on the axis of natural time. A point in time is
 * most often represented as a calendar expression.
 *
 *
 * <p>
 * Java class for TS complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}QTY">
 *       &lt;attribute name="value" type="{urn:hl7-org:v3}ts" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TS")
@XmlSeeAlso({ UVPTS.class, IVXBTS.class, SXCMTS.class, PPDTS.class })
public class TS extends QTY {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	protected String value;

	/**
	 * Gets the value of the value property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value of the value property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
