package org.hl7.v3;

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for GLIST_TS complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="GLIST_TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}ANY">
 *       &lt;sequence>
 *         &lt;element name="head" type="{urn:hl7-org:v3}TS"/>
 *         &lt;element name="increment" type="{urn:hl7-org:v3}PQ"/>
 *       &lt;/sequence>
 *       &lt;attribute name="period" type="{urn:hl7-org:v3}int" />
 *       &lt;attribute name="denominator" type="{urn:hl7-org:v3}int" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GLIST_TS", propOrder = { "head", "increment" })
public class GLISTTS extends ANY {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement(required = true)
	protected TS head;
	@XmlElement(required = true)
	protected PQ increment;
	@XmlAttribute
	protected BigInteger period;
	@XmlAttribute
	protected BigInteger denominator;

	/**
	 * Gets the value of the head property.
	 *
	 * @return possible object is {@link TS }
	 *
	 */
	public TS getHead() {
		return head;
	}

	/**
	 * Sets the value of the head property.
	 *
	 * @param value allowed object is {@link TS }
	 *
	 */
	public void setHead(TS value) {
		this.head = value;
	}

	/**
	 * Gets the value of the increment property.
	 *
	 * @return possible object is {@link PQ }
	 *
	 */
	public PQ getIncrement() {
		return increment;
	}

	/**
	 * Sets the value of the increment property.
	 *
	 * @param value allowed object is {@link PQ }
	 *
	 */
	public void setIncrement(PQ value) {
		this.increment = value;
	}

	/**
	 * Gets the value of the period property.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getPeriod() {
		return period;
	}

	/**
	 * Sets the value of the period property.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setPeriod(BigInteger value) {
		this.period = value;
	}

	/**
	 * Gets the value of the denominator property.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getDenominator() {
		return denominator;
	}

	/**
	 * Sets the value of the denominator property.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setDenominator(BigInteger value) {
		this.denominator = value;
	}

}
