package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for IVXB_TS complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="IVXB_TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}TS">
 *       &lt;attribute name="inclusive" type="{urn:hl7-org:v3}bl" default="true" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IVXB_TS")
public class IVXBTS extends TS {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
	protected Boolean inclusive;

	/**
	 * Gets the value of the inclusive property.
	 *
	 * @return possible object is {@link Boolean }
	 *
	 */
	public boolean isInclusive() {
		if (inclusive == null) {
			return true;
		} else {
			return inclusive;
		}
	}

	/**
	 * Sets the value of the inclusive property.
	 *
	 * @param value allowed object is {@link Boolean }
	 *
	 */
	public void setInclusive(Boolean value) {
		this.inclusive = value;
	}

}
