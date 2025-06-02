//
// Diese Datei wurde mit der Eclipse Implementation of JAXB, v3.0.0 generiert 
// Siehe https://eclipse-ee4j.github.io/jaxb-ri 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2025.04.09 um 01:08:44 PM CEST 
//


package ch.clustertec.estudio.schemas.order;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://estudio.clustertec.ch/schemas/order}patientOrder" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="nameInstitution" use="required" type="{http://estudio.clustertec.ch/schemas/order}string50" /&gt;
 *       &lt;attribute name="clientNrInstitution" use="required" type="{http://estudio.clustertec.ch/schemas/order}string6" /&gt;
 *       &lt;attribute name="issueDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "patientOrder"
})
@XmlRootElement(name = "patientOrders")
public class PatientOrders {

    @XmlElement(required = true)
    protected List<PatientOrder> patientOrder;
    @XmlAttribute(name = "nameInstitution", required = true)
    protected String nameInstitution;
    @XmlAttribute(name = "clientNrInstitution", required = true)
    protected String clientNrInstitution;
    @XmlAttribute(name = "issueDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar issueDate;

    /**
     * Gets the value of the patientOrder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the patientOrder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPatientOrder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PatientOrder }
     * 
     * 
     */
    public List<PatientOrder> getPatientOrder() {
        if (patientOrder == null) {
            patientOrder = new ArrayList<PatientOrder>();
        }
        return this.patientOrder;
    }

    /**
     * Ruft den Wert der nameInstitution-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameInstitution() {
        return nameInstitution;
    }

    /**
     * Legt den Wert der nameInstitution-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameInstitution(String value) {
        this.nameInstitution = value;
    }

    /**
     * Ruft den Wert der clientNrInstitution-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClientNrInstitution() {
        return clientNrInstitution;
    }

    /**
     * Legt den Wert der clientNrInstitution-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClientNrInstitution(String value) {
        this.clientNrInstitution = value;
    }

    /**
     * Ruft den Wert der issueDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getIssueDate() {
        return issueDate;
    }

    /**
     * Legt den Wert der issueDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setIssueDate(XMLGregorianCalendar value) {
        this.issueDate = value;
    }

}
