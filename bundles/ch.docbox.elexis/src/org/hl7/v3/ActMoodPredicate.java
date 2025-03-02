package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ActMoodPredicate.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="ActMoodPredicate">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="EVN.CRT"/>
 *     &lt;enumeration value="GOL"/>
 *     &lt;enumeration value="OPT"/>
 *     &lt;enumeration value="PERM"/>
 *     &lt;enumeration value="PERMRQ"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ActMoodPredicate")
@XmlEnum
public enum ActMoodPredicate {

	@XmlEnumValue("EVN.CRT")
	EVN_CRT("EVN.CRT"), GOL("GOL"), OPT("OPT"), PERM("PERM"), PERMRQ("PERMRQ");

	private final String value;

	ActMoodPredicate(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ActMoodPredicate fromValue(String v) {
		for (ActMoodPredicate c : ActMoodPredicate.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}

}
