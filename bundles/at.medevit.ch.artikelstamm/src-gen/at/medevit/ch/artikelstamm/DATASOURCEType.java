//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2026.06.17 um 03:18:08 PM CEST 
//


package at.medevit.ch.artikelstamm;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DATASOURCEType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <pre>
 * &lt;simpleType name="DATASOURCEType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="oddb2xml"/&gt;
 *     &lt;enumeration value="medindex"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "DATASOURCEType")
@XmlEnum
public enum DATASOURCEType {

    @XmlEnumValue("oddb2xml")
    ODDB_2_XML("oddb2xml"),
    @XmlEnumValue("medindex")
    MEDINDEX("medindex");
    private final String value;

    DATASOURCEType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static DATASOURCEType fromValue(String v) {
        for (DATASOURCEType c: DATASOURCEType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
