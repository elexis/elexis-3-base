//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.shared.shared_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für ColourType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="ColourType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="colourCode" type="{urn:gs1:shared:shared_common:xsd:3}ColourCodeType" minOccurs="0"/&gt;
 *         &lt;element name="colourDescription" type="{urn:gs1:shared:shared_common:xsd:3}Description80Type" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ColourType", propOrder = {
    "colourCode",
    "colourDescription"
})
public class ColourType {

    protected ColourCodeType colourCode;
    protected List<Description80Type> colourDescription;

    /**
     * Ruft den Wert der colourCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ColourCodeType }
     *     
     */
    public ColourCodeType getColourCode() {
        return colourCode;
    }

    /**
     * Legt den Wert der colourCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ColourCodeType }
     *     
     */
    public void setColourCode(ColourCodeType value) {
        this.colourCode = value;
    }

    /**
     * Gets the value of the colourDescription property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the colourDescription property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColourDescription().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Description80Type }
     * 
     * 
     */
    public List<Description80Type> getColourDescription() {
        if (colourDescription == null) {
            colourDescription = new ArrayList<Description80Type>();
        }
        return this.colourDescription;
    }

}
