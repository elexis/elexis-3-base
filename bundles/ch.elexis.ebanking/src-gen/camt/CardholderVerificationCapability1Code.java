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
 * <p>Java-Klasse für CardholderVerificationCapability1Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="CardholderVerificationCapability1Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MNSG"/>
 *     &lt;enumeration value="NPIN"/>
 *     &lt;enumeration value="FCPN"/>
 *     &lt;enumeration value="FEPN"/>
 *     &lt;enumeration value="FDSG"/>
 *     &lt;enumeration value="FBIO"/>
 *     &lt;enumeration value="MNVR"/>
 *     &lt;enumeration value="FBIG"/>
 *     &lt;enumeration value="APKI"/>
 *     &lt;enumeration value="PKIS"/>
 *     &lt;enumeration value="CHDT"/>
 *     &lt;enumeration value="SCEC"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CardholderVerificationCapability1Code")
@XmlEnum
public enum CardholderVerificationCapability1Code {

    MNSG,
    NPIN,
    FCPN,
    FEPN,
    FDSG,
    FBIO,
    MNVR,
    FBIG,
    APKI,
    PKIS,
    CHDT,
    SCEC;

    public String value() {
        return name();
    }

    public static CardholderVerificationCapability1Code fromValue(String v) {
        return valueOf(v);
    }

}
