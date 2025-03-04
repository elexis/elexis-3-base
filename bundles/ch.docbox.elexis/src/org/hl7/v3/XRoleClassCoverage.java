package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for x_RoleClassCoverage.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 *
 * <pre>
 * &lt;simpleType name="x_RoleClassCoverage">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="COVPTY"/>
 *     &lt;enumeration value="POLHOLD"/>
 *     &lt;enumeration value="SPNSR"/>
 *     &lt;enumeration value="UNDWRT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "x_RoleClassCoverage")
@XmlEnum
public enum XRoleClassCoverage {

	COVPTY, POLHOLD, SPNSR, UNDWRT;

	public String value() {
		return name();
	}

	public static XRoleClassCoverage fromValue(String v) {
		return valueOf(v);
	}

}
