//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2022.08.13 um 05:37:12 PM CEST
//


package at.medevit.ch.artikelstamm;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für DATASOURCEType.
 *
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DATASOURCEType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="oddb2xml"/>
 *     &lt;enumeration value="medindex"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
