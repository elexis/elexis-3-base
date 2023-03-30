package ch.docbox.ws.cdachservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.hl7.v3.ClinicalDocumentType;

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
 *         &lt;element name="document" type="{urn:hl7-org:v3}ClinicalDocumentType"/>
 *         &lt;element name="attachment" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "document", "attachment" })
@XmlRootElement(name = "addReferral")
public class AddReferral {

	@XmlElement(required = true)
	protected ClinicalDocumentType document;
	protected byte[] attachment;

	/**
	 * Gets the value of the document property.
	 *
	 * @return possible object is {@link ClinicalDocumentType }
	 *
	 */
	public ClinicalDocumentType getDocument() {
		return document;
	}

	/**
	 * Sets the value of the document property.
	 *
	 * @param value allowed object is {@link ClinicalDocumentType }
	 *
	 */
	public void setDocument(ClinicalDocumentType value) {
		this.document = value;
	}

	/**
	 * Gets the value of the attachment property.
	 *
	 * @return possible object is byte[]
	 */
	public byte[] getAttachment() {
		return attachment;
	}

	/**
	 * Sets the value of the attachment property.
	 *
	 * @param value allowed object is byte[]
	 */
	public void setAttachment(byte[] value) {
		this.attachment = ((byte[]) value);
	}

}
