package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for x_ParticipationAuthorPerformer.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="x_ParticipationAuthorPerformer">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="PRF"/>
 *     &lt;enumeration value="AUT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "x_ParticipationAuthorPerformer")
@XmlEnum
public enum XParticipationAuthorPerformer {

	PRF, AUT;

	public String value() {
		return name();
	}

	public static XParticipationAuthorPerformer fromValue(String v) {
		return valueOf(v);
	}

}
