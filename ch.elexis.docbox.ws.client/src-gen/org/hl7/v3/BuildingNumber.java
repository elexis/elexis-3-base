
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BuildingNumber.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BuildingNumber">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="BNR"/>
 *     &lt;enumeration value="BNN"/>
 *     &lt;enumeration value="BNS"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BuildingNumber")
@XmlEnum
public enum BuildingNumber {

    BNR,
    BNN,
    BNS;

    public String value() {
        return name();
    }

    public static BuildingNumber fromValue(String v) {
        return valueOf(v);
    }

}
