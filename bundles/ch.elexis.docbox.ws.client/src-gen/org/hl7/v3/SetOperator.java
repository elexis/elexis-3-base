
package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SetOperator.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SetOperator">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="A"/>
 *     &lt;enumeration value="E"/>
 *     &lt;enumeration value="H"/>
 *     &lt;enumeration value="I"/>
 *     &lt;enumeration value="P"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SetOperator")
@XmlEnum
public enum SetOperator {

    A,
    E,
    H,
    I,
    P;

    public String value() {
        return name();
    }

    public static SetOperator fromValue(String v) {
        return valueOf(v);
    }

}
