//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ExtendedLogisticUnitType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ExtendedLogisticUnitType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:gs1:ecom:ecom_common:xsd:3}LogisticUnitType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="logisticUnitDetails" type="{urn:gs1:ecom:ecom_common:xsd:3}LogisticUnitDetailsType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedLogisticUnitType", propOrder = {
    "logisticUnitDetails"
})
public class ExtendedLogisticUnitType
    extends LogisticUnitType
{

    protected List<LogisticUnitDetailsType> logisticUnitDetails;

    /**
     * Gets the value of the logisticUnitDetails property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the logisticUnitDetails property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLogisticUnitDetails().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LogisticUnitDetailsType }
     * 
     * 
     */
    public List<LogisticUnitDetailsType> getLogisticUnitDetails() {
        if (logisticUnitDetails == null) {
            logisticUnitDetails = new ArrayList<LogisticUnitDetailsType>();
        }
        return this.logisticUnitDetails;
    }

}
