
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LicensedEntityRole.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="LicensedEntityRole">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="LIC"/>
 *     &lt;enumeration value="NOT"/>
 *     &lt;enumeration value="PROV"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "LicensedEntityRole")
@XmlEnum
public enum LicensedEntityRole {

    LIC,
    NOT,
    PROV;

    public String value() {
        return name();
    }

    public static LicensedEntityRole fromValue(String v) {
        return valueOf(v);
    }

}
