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
 * <p>Java-Klasse für Ecom_AttributeValuePairListType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Ecom_AttributeValuePairListType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eComStringAttributeValuePairList" type="{urn:gs1:ecom:ecom_common:xsd:3}EcomStringAttributeValuePairListType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ecom_AttributeValuePairListType", propOrder = {
    "eComStringAttributeValuePairList"
})
public class EcomAttributeValuePairListType {

    protected List<EcomStringAttributeValuePairListType> eComStringAttributeValuePairList;

    /**
     * Gets the value of the eComStringAttributeValuePairList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the eComStringAttributeValuePairList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEComStringAttributeValuePairList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EcomStringAttributeValuePairListType }
     * 
     * 
     */
    public List<EcomStringAttributeValuePairListType> getEComStringAttributeValuePairList() {
        if (eComStringAttributeValuePairList == null) {
            eComStringAttributeValuePairList = new ArrayList<EcomStringAttributeValuePairListType>();
        }
        return this.eComStringAttributeValuePairList;
    }

}
