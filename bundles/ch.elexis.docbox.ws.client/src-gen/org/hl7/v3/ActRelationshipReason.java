
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ActRelationshipReason.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ActRelationshipReason">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="RSON"/>
 *     &lt;enumeration value="MITGT"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ActRelationshipReason")
@XmlEnum
public enum ActRelationshipReason {

    RSON,
    MITGT;

    public String value() {
        return name();
    }

    public static ActRelationshipReason fromValue(String v) {
        return valueOf(v);
    }

}
