package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ActRelationshipHasComponent.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="ActRelationshipHasComponent">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="COMP"/>
 *     &lt;enumeration value="ARR"/>
 *     &lt;enumeration value="CTRLV"/>
 *     &lt;enumeration value="DEP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "ActRelationshipHasComponent")
@XmlEnum
public enum ActRelationshipHasComponent {

	COMP, ARR, CTRLV, DEP;

	public String value() {
		return name();
	}

	public static ActRelationshipHasComponent fromValue(String v) {
		return valueOf(v);
	}

}
