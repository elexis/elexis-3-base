package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for SXCM_MO complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="SXCM_MO">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}MO">
 *       &lt;attribute name="operator" type="{urn:hl7-org:v3}SetOperator" default="I" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SXCM_MO")
@XmlSeeAlso({ IVLMO.class })
public class SXCMMO extends MO {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	protected SetOperator operator;

	/**
	 * Gets the value of the operator property.
	 *
	 * @return possible object is {@link SetOperator }
	 *
	 */
	public SetOperator getOperator() {
		if (operator == null) {
			return SetOperator.I;
		} else {
			return operator;
		}
	}

	/**
	 * Sets the value of the operator property.
	 *
	 * @param value allowed object is {@link SetOperator }
	 *
	 */
	public void setOperator(SetOperator value) {
		this.operator = value;
	}

}
