
package org.hl7.v3;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 *             The BooleanNonNull type is used where a Boolean cannot
 *             have a null value. A Boolean value can be either
 *             true or false.
 *          
 * 
 * <p>Java class for ANYNonNull complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ANYNonNull">
 *   &lt;complexContent>
 *     &lt;restriction base="{urn:hl7-org:v3}ANY">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ANYNonNull")
@XmlSeeAlso({
    BN.class
})
public class ANYNonNull
    extends ANY
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}
