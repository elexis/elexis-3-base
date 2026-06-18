//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2026.06.17 um 03:18:08 PM CEST 
//


package at.medevit.ch.artikelstamm;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für SALECDType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <pre>
 * &lt;simpleType name="SALECDType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="I"/&gt;
 *     &lt;enumeration value="A"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "SALECDType")
@XmlEnum
public enum SALECDType {

    I,
    A;

    public String value() {
        return name();
    }

    public static SALECDType fromValue(String v) {
        return valueOf(v);
    }

}
