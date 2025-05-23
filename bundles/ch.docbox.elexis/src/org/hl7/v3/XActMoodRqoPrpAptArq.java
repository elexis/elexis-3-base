package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for x_ActMoodRqoPrpAptArq.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="x_ActMoodRqoPrpAptArq">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="APT"/>
 *     &lt;enumeration value="ARQ"/>
 *     &lt;enumeration value="PRP"/>
 *     &lt;enumeration value="RQO"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "x_ActMoodRqoPrpAptArq")
@XmlEnum
public enum XActMoodRqoPrpAptArq {

	APT, ARQ, PRP, RQO;

	public String value() {
		return name();
	}

	public static XActMoodRqoPrpAptArq fromValue(String v) {
		return valueOf(v);
	}

}
