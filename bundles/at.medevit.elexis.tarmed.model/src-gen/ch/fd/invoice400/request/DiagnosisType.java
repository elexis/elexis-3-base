//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.18 at 01:35:39 PM CET 
//

package ch.fd.invoice400.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * <p>
 * Java class for diagnosisType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="diagnosisType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.xmlData.ch/xmlInvoice/XSD>stringType0_350">
 *       &lt;attribute name="type" default="by_contract">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="ICD10"/>
 *             &lt;enumeration value="cantonal"/>
 *             &lt;enumeration value="by_contract"/>
 *             &lt;enumeration value="freetext"/>
 *             &lt;enumeration value="birthdefect"/>
 *             &lt;enumeration value="ICPC"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="code" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_12" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "diagnosisType", propOrder = {
	"value"
})
public class DiagnosisType {
	
	@XmlValue
	protected String value;
	@XmlAttribute(name = "type")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String type;
	@XmlAttribute(name = "code")
	protected String code;
	
	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType(){
		if (type == null) {
			return "by_contract";
		} else {
			return type;
		}
	}
	
	/**
	 * Sets the value of the type property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value){
		this.type = value;
	}
	
	/**
	 * Gets the value of the code property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCode(){
		return code;
	}
	
	/**
	 * Sets the value of the code property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCode(String value){
		this.code = value;
	}
	
}
