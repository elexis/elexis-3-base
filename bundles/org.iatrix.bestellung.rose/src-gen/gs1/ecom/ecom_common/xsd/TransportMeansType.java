//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.01.23 um 10:57:07 AM CET 
//


package gs1.ecom.ecom_common.xsd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import gs1.shared.shared_common.xsd.CommunicationChannelType;
import gs1.shared.shared_common.xsd.IdentifierType;


/**
 * <p>Java-Klasse für TransportMeansType complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="TransportMeansType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transportMeansType" type="{urn:gs1:ecom:ecom_common:xsd:3}TransportMeansTypeCodeType"/&gt;
 *         &lt;element name="transportMeansID" type="{urn:gs1:shared:shared_common:xsd:3}IdentifierType" minOccurs="0"/&gt;
 *         &lt;element name="transportMeansName" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="80"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="communicationChannel" type="{urn:gs1:shared:shared_common:xsd:3}CommunicationChannelType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransportMeansType", propOrder = {
    "transportMeansType",
    "transportMeansID",
    "transportMeansName",
    "communicationChannel"
})
public class TransportMeansType {

    @XmlElement(required = true)
    protected TransportMeansTypeCodeType transportMeansType;
    protected IdentifierType transportMeansID;
    protected String transportMeansName;
    protected List<CommunicationChannelType> communicationChannel;

    /**
     * Ruft den Wert der transportMeansType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TransportMeansTypeCodeType }
     *     
     */
    public TransportMeansTypeCodeType getTransportMeansType() {
        return transportMeansType;
    }

    /**
     * Legt den Wert der transportMeansType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TransportMeansTypeCodeType }
     *     
     */
    public void setTransportMeansType(TransportMeansTypeCodeType value) {
        this.transportMeansType = value;
    }

    /**
     * Ruft den Wert der transportMeansID-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link IdentifierType }
     *     
     */
    public IdentifierType getTransportMeansID() {
        return transportMeansID;
    }

    /**
     * Legt den Wert der transportMeansID-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentifierType }
     *     
     */
    public void setTransportMeansID(IdentifierType value) {
        this.transportMeansID = value;
    }

    /**
     * Ruft den Wert der transportMeansName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransportMeansName() {
        return transportMeansName;
    }

    /**
     * Legt den Wert der transportMeansName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransportMeansName(String value) {
        this.transportMeansName = value;
    }

    /**
     * Gets the value of the communicationChannel property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the communicationChannel property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCommunicationChannel().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommunicationChannelType }
     * 
     * 
     */
    public List<CommunicationChannelType> getCommunicationChannel() {
        if (communicationChannel == null) {
            communicationChannel = new ArrayList<CommunicationChannelType>();
        }
        return this.communicationChannel;
    }

}
