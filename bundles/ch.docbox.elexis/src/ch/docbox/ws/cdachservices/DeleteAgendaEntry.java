package ch.docbox.ws.cdachservices;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

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
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="msgTitle" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="msgBody" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="param" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = { "id", "msgTitle", "msgBody", "param" })
@XmlRootElement(name = "deleteAgendaEntry")
public class DeleteAgendaEntry {

	@XmlElement(required = true)
	protected String id;
	@XmlElement(required = true)
	protected String msgTitle;
	@XmlElement(required = true)
	protected String msgBody;
	@XmlElement(required = true)
	protected String param;

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
	 * Gets the value of the msgTitle property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getMsgTitle() {
		return msgTitle;
	}

	/**
	 * Sets the value of the msgTitle property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setMsgTitle(String value) {
		this.msgTitle = value;
	}

	/**
	 * Gets the value of the msgBody property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getMsgBody() {
		return msgBody;
	}

	/**
	 * Sets the value of the msgBody property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setMsgBody(String value) {
		this.msgBody = value;
	}

	/**
	 * Gets the value of the param property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getParam() {
		return param;
	}

	/**
	 * Sets the value of the param property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setParam(String value) {
		this.param = value;
	}

}
