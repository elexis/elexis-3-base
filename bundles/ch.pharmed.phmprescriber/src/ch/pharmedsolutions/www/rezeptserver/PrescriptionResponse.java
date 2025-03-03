
package ch.pharmedsolutions.www.rezeptserver;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java-Klasse für anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PrescriptionID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="QRCodeString" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "prescriptionID", "qrCodeString" })
@XmlRootElement(name = "PrescriptionResponse")
public class PrescriptionResponse {

	@XmlElement(name = "PrescriptionID", required = true)
	protected String prescriptionID;
	@XmlElement(name = "QRCodeString", required = true)
	protected String qrCodeString;

	/**
	 * Ruft den Wert der prescriptionID-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getPrescriptionID() {
		return prescriptionID;
	}

	/**
	 * Legt den Wert der prescriptionID-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setPrescriptionID(String value) {
		this.prescriptionID = value;
	}

	/**
	 * Ruft den Wert der qrCodeString-Eigenschaft ab.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getQRCodeString() {
		return qrCodeString;
	}

	/**
	 * Legt den Wert der qrCodeString-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setQRCodeString(String value) {
		this.qrCodeString = value;
	}

}
