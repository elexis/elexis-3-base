
package org.hl7.v3;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PersonNamePartChangeQualifier.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PersonNamePartChangeQualifier">
 *   &lt;restriction base="{urn:hl7-org:v3}cs">
 *     &lt;enumeration value="AD"/>
 *     &lt;enumeration value="BR"/>
 *     &lt;enumeration value="SP"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PersonNamePartChangeQualifier")
@XmlEnum
public enum PersonNamePartChangeQualifier {

    AD,
    BR,
    SP;

    public String value() {
        return name();
    }

    public static PersonNamePartChangeQualifier fromValue(String v) {
        return valueOf(v);
    }

}
