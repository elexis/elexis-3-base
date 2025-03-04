package ch.docbox.ws.cdachservices;

import javax.xml.datatype.XMLGregorianCalendar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for AppointmentType complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="AppointmentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="duration" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="state">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="salesrepresentative-open"/>
 *               &lt;enumeration value="salesrepresentative-booked"/>
 *               &lt;enumeration value="salesrepresentative-openrequest"/>
 *               &lt;enumeration value="salesrepresentative-openinvitation"/>
 *               &lt;enumeration value="salesrepresentative-phone"/>
 *               &lt;enumeration value="emergencyservice"/>
 *               &lt;enumeration value="canceled"/>
 *               &lt;enumeration value="terminierung-booked"/>
 *               &lt;enumeration value="terminierung-open"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="reasonTitle" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="reasonDetails" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="visitor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="info" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppointmentType", propOrder = { "date", "duration", "id", "state", "reasonTitle", "reasonDetails",
		"visitor", "info" })
public class AppointmentType {

	@XmlElement(required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar date;
	protected long duration;
	@XmlElement(required = true)
	protected String id;
	@XmlElement(required = true)
	protected String state;
	protected String reasonTitle;
	protected String reasonDetails;
	protected String visitor;
	protected String info;

	/**
	 * Gets the value of the date property.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getDate() {
		return date;
	}

	/**
	 * Sets the value of the date property.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setDate(XMLGregorianCalendar value) {
		this.date = value;
	}

	/**
	 * Gets the value of the duration property.
	 *
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Sets the value of the duration property.
	 *
	 */
	public void setDuration(long value) {
		this.duration = value;
	}

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the state property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getState() {
		return state;
	}

	/**
	 * Sets the value of the state property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setState(String value) {
		this.state = value;
	}

	/**
	 * Gets the value of the reasonTitle property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getReasonTitle() {
		return reasonTitle;
	}

	/**
	 * Sets the value of the reasonTitle property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setReasonTitle(String value) {
		this.reasonTitle = value;
	}

	/**
	 * Gets the value of the reasonDetails property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getReasonDetails() {
		return reasonDetails;
	}

	/**
	 * Sets the value of the reasonDetails property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setReasonDetails(String value) {
		this.reasonDetails = value;
	}

	/**
	 * Gets the value of the visitor property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getVisitor() {
		return visitor;
	}

	/**
	 * Sets the value of the visitor property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setVisitor(String value) {
		this.visitor = value;
	}

	/**
	 * Gets the value of the info property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Sets the value of the info property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setInfo(String value) {
		this.info = value;
	}

}
