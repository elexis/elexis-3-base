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
 * <p>Java-Klasse für CardPaymentServiceType2Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="CardPaymentServiceType2Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AGGR"/>
 *     &lt;enumeration value="DCCV"/>
 *     &lt;enumeration value="GRTT"/>
 *     &lt;enumeration value="INSP"/>
 *     &lt;enumeration value="LOYT"/>
 *     &lt;enumeration value="NRES"/>
 *     &lt;enumeration value="PUCO"/>
 *     &lt;enumeration value="RECP"/>
 *     &lt;enumeration value="SOAF"/>
 *     &lt;enumeration value="UNAF"/>
 *     &lt;enumeration value="VCAU"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "CardPaymentServiceType2Code")
@XmlEnum
public enum CardPaymentServiceType2Code {

    AGGR,
    DCCV,
    GRTT,
    INSP,
    LOYT,
    NRES,
    PUCO,
    RECP,
    SOAF,
    UNAF,
    VCAU;

    public String value() {
        return name();
    }

    public static CardPaymentServiceType2Code fromValue(String v) {
        return valueOf(v);
    }

}
