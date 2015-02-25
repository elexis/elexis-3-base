
package org.hl7.v3;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for POCD_MT000040.RegionOfInterest.value complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="POCD_MT000040.RegionOfInterest.value">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:hl7-org:v3}INT">
 *       &lt;attribute name="unsorted" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POCD_MT000040.RegionOfInterest.value")
public class POCDMT000040RegionOfInterestValue
    extends INT
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlAttribute
    protected Boolean unsorted;

    /**
     * Gets the value of the unsorted property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUnsorted() {
        if (unsorted == null) {
            return false;
        } else {
            return unsorted;
        }
    }

    /**
     * Sets the value of the unsorted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUnsorted(Boolean value) {
        this.unsorted = value;
    }

}
