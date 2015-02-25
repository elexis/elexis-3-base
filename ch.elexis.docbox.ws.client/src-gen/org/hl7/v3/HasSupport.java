
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for hasSupport.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="hasSupport">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="SPRT"/>
 *     &lt;enumeration value="SPRTBND"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "hasSupport")
@XmlEnum
public enum HasSupport {

    SPRT,
    SPRTBND;

    public String value() {
        return name();
    }

    public static HasSupport fromValue(String v) {
        return valueOf(v);
    }

}
