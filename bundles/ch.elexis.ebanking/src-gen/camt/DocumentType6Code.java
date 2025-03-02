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
 * <p>Java-Klasse für DocumentType6Code.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;simpleType name="DocumentType6Code">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MSIN"/>
 *     &lt;enumeration value="CNFA"/>
 *     &lt;enumeration value="DNFA"/>
 *     &lt;enumeration value="CINV"/>
 *     &lt;enumeration value="CREN"/>
 *     &lt;enumeration value="DEBN"/>
 *     &lt;enumeration value="HIRI"/>
 *     &lt;enumeration value="SBIN"/>
 *     &lt;enumeration value="CMCN"/>
 *     &lt;enumeration value="SOAC"/>
 *     &lt;enumeration value="DISP"/>
 *     &lt;enumeration value="BOLD"/>
 *     &lt;enumeration value="VCHR"/>
 *     &lt;enumeration value="AROI"/>
 *     &lt;enumeration value="TSUT"/>
 *     &lt;enumeration value="PUOR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "DocumentType6Code")
@XmlEnum
public enum DocumentType6Code {

    MSIN,
    CNFA,
    DNFA,
    CINV,
    CREN,
    DEBN,
    HIRI,
    SBIN,
    CMCN,
    SOAC,
    DISP,
    BOLD,
    VCHR,
    AROI,
    TSUT,
    PUOR;

    public String value() {
        return name();
    }

    public static DocumentType6Code fromValue(String v) {
        return valueOf(v);
    }

}
