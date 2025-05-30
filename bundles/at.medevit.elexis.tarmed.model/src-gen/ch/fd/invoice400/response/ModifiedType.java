//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.13 at 12:17:21 PM MEZ 
//

package ch.fd.invoice400.response;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for modifiedType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modifiedType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="explanation" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_700"/>
 *         &lt;element name="error" type="{http://www.xmlData.ch/xmlInvoice/XSD}modifiedErrorType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="balance" type="{http://www.xmlData.ch/xmlInvoice/XSD}balanceType"/>
 *         &lt;element name="balance_corrected" type="{http://www.xmlData.ch/xmlInvoice/XSD}balanceType"/>
 *         &lt;element name="services" type="{http://www.xmlData.ch/xmlInvoice/XSD}servicesType"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" fixed="final" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modifiedType", propOrder = {
	"explanation", "error", "balance", "balanceCorrected", "services"
})
public class ModifiedType {
	
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected String explanation;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected List<ModifiedErrorType> error;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected BalanceType balance;
	@XmlElement(name = "balance_corrected", namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected BalanceType balanceCorrected;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected ServicesType services;
	@XmlAttribute
	protected String type;
	
	/**
	 * Gets the value of the explanation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getExplanation(){
		return explanation;
	}
	
	/**
	 * Sets the value of the explanation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setExplanation(String value){
		this.explanation = value;
	}
	
	/**
	 * Gets the value of the error property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any
	 * modification you make to the returned list will be present inside the JAXB object. This is
	 * why there is not a <CODE>set</CODE> method for the error property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getError().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link ModifiedErrorType }
	 * 
	 * 
	 */
	public List<ModifiedErrorType> getError(){
		if (error == null) {
			error = new ArrayList<ModifiedErrorType>();
		}
		return this.error;
	}
	
	/**
	 * Gets the value of the balance property.
	 * 
	 * @return possible object is {@link BalanceType }
	 * 
	 */
	public BalanceType getBalance(){
		return balance;
	}
	
	/**
	 * Sets the value of the balance property.
	 * 
	 * @param value
	 *            allowed object is {@link BalanceType }
	 * 
	 */
	public void setBalance(BalanceType value){
		this.balance = value;
	}
	
	/**
	 * Gets the value of the balanceCorrected property.
	 * 
	 * @return possible object is {@link BalanceType }
	 * 
	 */
	public BalanceType getBalanceCorrected(){
		return balanceCorrected;
	}
	
	/**
	 * Sets the value of the balanceCorrected property.
	 * 
	 * @param value
	 *            allowed object is {@link BalanceType }
	 * 
	 */
	public void setBalanceCorrected(BalanceType value){
		this.balanceCorrected = value;
	}
	
	/**
	 * Gets the value of the services property.
	 * 
	 * @return possible object is {@link ServicesType }
	 * 
	 */
	public ServicesType getServices(){
		return services;
	}
	
	/**
	 * Sets the value of the services property.
	 * 
	 * @param value
	 *            allowed object is {@link ServicesType }
	 * 
	 */
	public void setServices(ServicesType value){
		this.services = value;
	}
	
	/**
	 * Gets the value of the type property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType(){
		if (type == null) {
			return "final";
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
	
}
