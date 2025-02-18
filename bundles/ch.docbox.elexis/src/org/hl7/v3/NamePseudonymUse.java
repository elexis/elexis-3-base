package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for NamePseudonymUse.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="NamePseudonymUse">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="P"/>
 *     &lt;enumeration value="A"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "NamePseudonymUse")
@XmlEnum
public enum NamePseudonymUse {

	P, A;

	public String value() {
		return name();
	}

	public static NamePseudonymUse fromValue(String v) {
		return valueOf(v);
	}

}
