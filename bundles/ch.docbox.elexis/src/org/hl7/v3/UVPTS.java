package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for UVP_TS complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="UVP_TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}TS">
 *       &lt;attribute name="probability" type="{urn:hl7-org:v3}probability" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UVP_TS")
public class UVPTS extends TS {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	protected Double probability;

	/**
	 * Gets the value of the probability property.
	 *
	 * @return possible object is {@link Double }
	 *
	 */
	public Double getProbability() {
		return probability;
	}

	/**
	 * Sets the value of the probability property.
	 *
	 * @param value allowed object is {@link Double }
	 *
	 */
	public void setProbability(Double value) {
		this.probability = value;
	}

}
