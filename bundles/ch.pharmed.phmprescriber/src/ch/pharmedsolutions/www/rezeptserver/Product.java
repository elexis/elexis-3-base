
package ch.pharmedsolutions.www.rezeptserver;

import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für Product complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType name="Product">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="pharmacode" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="ean_id" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="product_name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="prescriptor_qty" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="prescriptor_repetition_end" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *         &lt;element name="remark" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="posology" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Product", propOrder = {

})
public class Product {

	@XmlElement(required = true, nillable = true)
	protected BigInteger pharmacode;
	@XmlElement(name = "ean_id", required = true, nillable = true)
	protected BigInteger eanId;
	@XmlElement(name = "product_name", required = true, nillable = true)
	protected String productName;
	@XmlElement(name = "prescriptor_qty", required = true, type = Integer.class, nillable = true)
	protected Integer prescriptorQty;
	@XmlElement(name = "prescriptor_repetition_end", required = true, nillable = true)
	@XmlSchemaType(name = "date")
	protected XMLGregorianCalendar prescriptorRepetitionEnd;
	@XmlElement(required = true, nillable = true)
	protected String remark;
	@XmlElement(required = true, nillable = true)
	protected String posology;

	/**
	 * Ruft den Wert der pharmacode-Eigenschaft ab.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getPharmacode() {
		return pharmacode;
	}

	/**
	 * Legt den Wert der pharmacode-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setPharmacode(BigInteger value) {
		this.pharmacode = value;
	}

	/**
	 * Ruft den Wert der eanId-Eigenschaft ab.
	 *
	 * @return possible object is {@link BigInteger }
	 *
	 */
	public BigInteger getEanId() {
		return eanId;
	}

	/**
	 * Legt den Wert der eanId-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link BigInteger }
	 *
	 */
	public void setEanId(BigInteger value) {
		this.eanId = value;
	}

	/**
	 * Ruft den Wert der productName-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getProductName() {
		return productName;
	}

	/**
	 * Legt den Wert der productName-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setProductName(String value) {
		this.productName = value;
	}

	/**
	 * Ruft den Wert der prescriptorQty-Eigenschaft ab.
	 *
	 * @return possible object is {@link Integer }
	 *
	 */
	public Integer getPrescriptorQty() {
		return prescriptorQty;
	}

	/**
	 * Legt den Wert der prescriptorQty-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link Integer }
	 *
	 */
	public void setPrescriptorQty(Integer value) {
		this.prescriptorQty = value;
	}

	/**
	 * Ruft den Wert der prescriptorRepetitionEnd-Eigenschaft ab.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getPrescriptorRepetitionEnd() {
		return prescriptorRepetitionEnd;
	}

	/**
	 * Legt den Wert der prescriptorRepetitionEnd-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setPrescriptorRepetitionEnd(XMLGregorianCalendar value) {
		this.prescriptorRepetitionEnd = value;
	}

	/**
	 * Ruft den Wert der remark-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * Legt den Wert der remark-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setRemark(String value) {
		this.remark = value;
	}

	/**
	 * Ruft den Wert der posology-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getPosology() {
		return posology;
	}

	/**
	 * Legt den Wert der posology-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setPosology(String value) {
		this.posology = value;
	}

}
