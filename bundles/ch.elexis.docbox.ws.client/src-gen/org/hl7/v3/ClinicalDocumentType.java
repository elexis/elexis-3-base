
package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ClinicalDocumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ClinicalDocumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ClinicalDocument" type="{urn:hl7-org:v3}POCD_MT000040.ClinicalDocument"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ClinicalDocumentType", propOrder = {
    "clinicalDocument"
})
public class ClinicalDocumentType {

    @XmlElement(name = "ClinicalDocument", required = true)
    protected POCDMT000040ClinicalDocument clinicalDocument;

    /**
     * Gets the value of the clinicalDocument property.
     * 
     * @return
     *     possible object is
     *     {@link POCDMT000040ClinicalDocument }
     *     
     */
    public POCDMT000040ClinicalDocument getClinicalDocument() {
        return clinicalDocument;
    }

    /**
     * Sets the value of the clinicalDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link POCDMT000040ClinicalDocument }
     *     
     */
    public void setClinicalDocument(POCDMT000040ClinicalDocument value) {
        this.clinicalDocument = value;
    }

}
