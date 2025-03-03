//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für EnergyQuantityCalculationConditionsType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EnergyQuantityCalculationConditionsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="countedMeasureandFactor" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="standardConditionConversion" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *         &lt;element name="calorificValue" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnergyQuantityCalculationConditionsType", propOrder = {
    "countedMeasureandFactor",
    "standardConditionConversion",
    "calorificValue"
})
public class EnergyQuantityCalculationConditionsType {

    protected BigInteger countedMeasureandFactor;
    protected BigDecimal standardConditionConversion;
    protected BigDecimal calorificValue;

    /**
     * Ruft den Wert der countedMeasureandFactor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getCountedMeasureandFactor() {
        return countedMeasureandFactor;
    }

    /**
     * Legt den Wert der countedMeasureandFactor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setCountedMeasureandFactor(BigInteger value) {
        this.countedMeasureandFactor = value;
    }

    /**
     * Ruft den Wert der standardConditionConversion-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getStandardConditionConversion() {
        return standardConditionConversion;
    }

    /**
     * Legt den Wert der standardConditionConversion-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setStandardConditionConversion(BigDecimal value) {
        this.standardConditionConversion = value;
    }

    /**
     * Ruft den Wert der calorificValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getCalorificValue() {
        return calorificValue;
    }

    /**
     * Legt den Wert der calorificValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setCalorificValue(BigDecimal value) {
        this.calorificValue = value;
    }

}
