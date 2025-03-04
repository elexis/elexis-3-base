//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für TransportPlanningDateTypeEnumerationType.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <pre>
 * &lt;simpleType name="TransportPlanningDateTypeEnumerationType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="DELIVERY_DATE"/&gt;
 *     &lt;enumeration value="PICKUP_DATE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TransportPlanningDateTypeEnumerationType")
@XmlEnum
public enum TransportPlanningDateTypeEnumerationType {

    DELIVERY_DATE,
    PICKUP_DATE;

    public String value() {
        return name();
    }

    public static TransportPlanningDateTypeEnumerationType fromValue(String v) {
        return valueOf(v);
    }

}
