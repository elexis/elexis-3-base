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

/**
 * <p>
 * Java class for providerAddressType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="providerAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="company" type="{http://www.xmlData.ch/xmlInvoice/XSD}providerCompanyType"/>
 *         &lt;element name="person" type="{http://www.xmlData.ch/xmlInvoice/XSD}providerPersonType"/>
 *       &lt;/choice>
 *       &lt;attribute name="ean_party" use="required" type="{http://www.xmlData.ch/xmlInvoice/XSD}eanPartyType" />
 *       &lt;attribute name="zsr" use="required" type="{http://www.xmlData.ch/xmlInvoice/XSD}zsrPartyType" />
 *       &lt;attribute name="specialty" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_350" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "providerAddressType", propOrder = {
	"company", "person"
})
public class ProviderAddressType {
	
	protected ProviderCompanyType company;
	protected ProviderPersonType person;
	@XmlAttribute(name = "ean_party", required = true)
	protected String eanParty;
	@XmlAttribute(name = "zsr", required = true)
	protected String zsr;
	@XmlAttribute(name = "specialty")
	protected String specialty;
	
	/**
	 * Gets the value of the company property.
	 * 
	 * @return possible object is {@link ProviderCompanyType }
	 * 
	 */
	public ProviderCompanyType getCompany(){
		return company;
	}
	
	/**
	 * Sets the value of the company property.
	 * 
	 * @param value
	 *            allowed object is {@link ProviderCompanyType }
	 * 
	 */
	public void setCompany(ProviderCompanyType value){
		this.company = value;
	}
	
	/**
	 * Gets the value of the person property.
	 * 
	 * @return possible object is {@link ProviderPersonType }
	 * 
	 */
	public ProviderPersonType getPerson(){
		return person;
	}
	
	/**
	 * Sets the value of the person property.
	 * 
	 * @param value
	 *            allowed object is {@link ProviderPersonType }
	 * 
	 */
	public void setPerson(ProviderPersonType value){
		this.person = value;
	}
	
	/**
	 * Gets the value of the eanParty property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEanParty(){
		return eanParty;
	}
	
	/**
	 * Sets the value of the eanParty property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEanParty(String value){
		this.eanParty = value;
	}
	
	/**
	 * Gets the value of the zsr property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getZsr(){
		return zsr;
	}
	
	/**
	 * Sets the value of the zsr property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setZsr(String value){
		this.zsr = value;
	}
	
	/**
	 * Gets the value of the specialty property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getSpecialty(){
		return specialty;
	}
	
	/**
	 * Sets the value of the specialty property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setSpecialty(String value){
		this.specialty = value;
	}
	
}
