
package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActClassEntry.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActClassEntry">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="ENTRY"/>
 *     &lt;enumeration value="BATTERY"/>
 *     &lt;enumeration value="CLUSTER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActClassEntry")
@XmlEnum
public enum ActClassEntry {

    ENTRY,
    BATTERY,
    CLUSTER;

    public String value() {
        return name();
    }

    public static ActClassEntry fromValue(String v) {
        return valueOf(v);
    }

}
