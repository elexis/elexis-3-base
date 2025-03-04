package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for RelatedLinkType.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="RelatedLinkType">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="REL"/>
 *     &lt;enumeration value="BACKUP"/>
 *     &lt;enumeration value="DIRAUTH"/>
 *     &lt;enumeration value="INDAUTH"/>
 *     &lt;enumeration value="PART"/>
 *     &lt;enumeration value="REPL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "RelatedLinkType")
@XmlEnum
public enum RelatedLinkType {

	REL, BACKUP, DIRAUTH, INDAUTH, PART, REPL;

	public String value() {
		return name();
	}

	public static RelatedLinkType fromValue(String v) {
		return valueOf(v);
	}

}
