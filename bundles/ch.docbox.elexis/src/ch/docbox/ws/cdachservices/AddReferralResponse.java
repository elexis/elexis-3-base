package ch.docbox.ws.cdachservices;

import org.apache.commons.lang3.StringUtils;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="documentID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "success", "message", "documentID" })
@XmlRootElement(name = "addReferralResponse")
public class AddReferralResponse {

	protected boolean success;
	@XmlElement(required = true)
	protected String message;
	@XmlElement(required = true)
	protected String documentID;

	/**
	 * Gets the value of the success property.
	 *
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Sets the value of the success property.
	 *
	 */
	public void setSuccess(boolean value) {
		this.success = value;
	}

	/**
	 * Gets the value of the message property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Sets the value of the message property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setMessage(String value) {
		this.message = value;
	}

	/**
	 * Gets the value of the documentID property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getDocumentID() {
		return documentID;
	}

	/**
	 * Sets the value of the documentID property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setDocumentID(String value) {
		this.documentID = value;
	}

}
