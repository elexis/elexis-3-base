package ch.docbox.ws.cdachservices;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hl7.v3.CE;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for DocumentInfoType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="DocumentInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="code" type="{urn:hl7-org:v3}CE"/>
 *         &lt;element name="documentID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="creationDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastDownload" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentInfoType", propOrder = { "title", "message", "code", "documentID", "creationDate",
		"lastDownload" })
public class DocumentInfoType {

	@XmlElement(required = true)
	protected String title;
	@XmlElement(required = true)
	protected String message;
	@XmlElement(required = true)
	protected CE code;
	@XmlElement(required = true)
	protected String documentID;
	@XmlElement(required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar creationDate;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar lastDownload;

	/**
	 * Gets the value of the title property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the value of the title property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setTitle(String value) {
		this.title = value;
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
	 * Gets the value of the code property.
	 *
	 * @return possible object is {@link CE }
	 *
	 */
	public CE getCode() {
		return code;
	}

	/**
	 * Sets the value of the code property.
	 *
	 * @param value allowed object is {@link CE }
	 *
	 */
	public void setCode(CE value) {
		this.code = value;
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

	/**
	 * Gets the value of the creationDate property.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the value of the creationDate property.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setCreationDate(XMLGregorianCalendar value) {
		this.creationDate = value;
	}

	/**
	 * Gets the value of the lastDownload property.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getLastDownload() {
		return lastDownload;
	}

	/**
	 * Sets the value of the lastDownload property.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setLastDownload(XMLGregorianCalendar value) {
		this.lastDownload = value;
	}

}
