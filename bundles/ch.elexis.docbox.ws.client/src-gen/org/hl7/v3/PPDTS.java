
package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PPD_TS complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PPD_TS">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}TS">
 *       &lt;sequence>
 *         &lt;element name="standardDeviation" type="{urn:hl7-org:v3}PQ" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="distributionType" type="{urn:hl7-org:v3}ProbabilityDistributionType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PPD_TS", propOrder = {
    "standardDeviation"
})
@XmlSeeAlso({
    IVXBPPDTS.class,
    SXCMPPDTS.class
})
public class PPDTS
    extends TS
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected PQ standardDeviation;
    @XmlAttribute
    protected ProbabilityDistributionType distributionType;

    /**
     * Gets the value of the standardDeviation property.
     * 
     * @return
     *     possible object is
     *     {@link PQ }
     *     
     */
    public PQ getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * Sets the value of the standardDeviation property.
     * 
     * @param value
     *     allowed object is
     *     {@link PQ }
     *     
     */
    public void setStandardDeviation(PQ value) {
        this.standardDeviation = value;
    }

    /**
     * Gets the value of the distributionType property.
     * 
     * @return
     *     possible object is
     *     {@link ProbabilityDistributionType }
     *     
     */
    public ProbabilityDistributionType getDistributionType() {
        return distributionType;
    }

    /**
     * Sets the value of the distributionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProbabilityDistributionType }
     *     
     */
    public void setDistributionType(ProbabilityDistributionType value) {
        this.distributionType = value;
    }

}
