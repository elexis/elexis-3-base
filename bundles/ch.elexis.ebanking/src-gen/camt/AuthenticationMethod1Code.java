//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.06.14 um 06:06:29 PM CEST 
//


package camt;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AuthenticationMethod1Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="AuthenticationMethod1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="UKNW"/>
 *     &lt;enumeration value="BYPS"/>
 *     &lt;enumeration value="NPIN"/>
 *     &lt;enumeration value="FPIN"/>
 *     &lt;enumeration value="CPSG"/>
 *     &lt;enumeration value="PPSG"/>
 *     &lt;enumeration value="MANU"/>
 *     &lt;enumeration value="MERC"/>
 *     &lt;enumeration value="SCRT"/>
 *     &lt;enumeration value="SNCT"/>
 *     &lt;enumeration value="SCNL"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "AuthenticationMethod1Code")
@XmlEnum
public enum AuthenticationMethod1Code {

    UKNW,
    BYPS,
    NPIN,
    FPIN,
    CPSG,
    PPSG,
    MANU,
    MERC,
    SCRT,
    SNCT,
    SCNL;

    public String value() {
        return name();
    }

    public static AuthenticationMethod1Code fromValue(String v) {
        return valueOf(v);
    }

}
