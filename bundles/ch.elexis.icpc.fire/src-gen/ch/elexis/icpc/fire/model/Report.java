//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.12.07 um 12:49:37 PM CET 
//


package ch.elexis.icpc.fire.model;

import org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.elexis.icpc.fire.model.jaxb.DateTimeAdapter;

/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="consultations">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="consultation" type="{}tConsultation" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="patients">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="patient" type="{}tPatient" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="doctors">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="doctor" type="{}tDoctor" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="exportDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="exportDelay" type="{http://www.w3.org/2001/XMLSchema}long" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = StringUtils.EMPTY, propOrder = {
    "consultations",
    "patients",
    "doctors"
})
@XmlRootElement(name = "report")
public class Report {
	
	public static int EXPORT_DELAY = 168;

    @XmlElement(required = true)
    protected Report.Consultations consultations;
    @XmlElement(required = true)
    protected Report.Patients patients;
    @XmlElement(required = true)
	protected Report.Doctors doctors;
    @XmlAttribute(name = "exportDate")
    @XmlSchemaType(name = "dateTime")
    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    protected XMLGregorianCalendar exportDate;
    @XmlAttribute(name = "exportDelay")
	protected Long exportDelay = Long.valueOf(EXPORT_DELAY);

    /**
     * Ruft den Wert der consultations-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Report.Consultations }
     *     
     */
    public Report.Consultations getConsultations() {
        return consultations;
    }

    /**
     * Legt den Wert der consultations-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Report.Consultations }
     *     
     */
    public void setConsultations(Report.Consultations value) {
        this.consultations = value;
    }

    /**
     * Ruft den Wert der patients-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Report.Patients }
     *     
     */
    public Report.Patients getPatients() {
        return patients;
    }

    /**
     * Legt den Wert der patients-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Report.Patients }
     *     
     */
    public void setPatients(Report.Patients value) {
        this.patients = value;
    }

    /**
     * Ruft den Wert der doctors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Report.Doctors }
     *     
     */
    public Report.Doctors getDoctors() {
        return doctors;
    }

    /**
     * Legt den Wert der doctors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Report.Doctors }
     *     
     */
    public void setDoctors(Report.Doctors value) {
        this.doctors = value;
    }

    /**
     * Ruft den Wert der exportDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExportDate() {
        return exportDate;
    }

    /**
     * Legt den Wert der exportDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExportDate(XMLGregorianCalendar value) {
        this.exportDate = value;
    }

    /**
     * Ruft den Wert der exportDelay-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getExportDelay() {
        return exportDelay;
    }

    /**
     * Legt den Wert der exportDelay-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setExportDelay(Long value) {
        this.exportDelay = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="consultation" type="{}tConsultation" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = StringUtils.EMPTY, propOrder = {
        "consultation"
    })
    public static class Consultations {

        protected List<TConsultation> consultation;

        /**
         * Gets the value of the consultation property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the consultation property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getConsultation().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TConsultation }
         * 
         * 
         */
        public List<TConsultation> getConsultation() {
            if (consultation == null) {
                consultation = new ArrayList<TConsultation>();
            }
            return this.consultation;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="doctor" type="{}tDoctor" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = StringUtils.EMPTY, propOrder = {
        "doctor"
    })
    public static class Doctors {

        protected List<TDoctor> doctor;

        /**
         * Gets the value of the doctor property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the doctor property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDoctor().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TDoctor }
         * 
         * 
         */
        public List<TDoctor> getDoctor() {
            if (doctor == null) {
                doctor = new ArrayList<TDoctor>();
            }
            return this.doctor;
        }

    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="patient" type="{}tPatient" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = StringUtils.EMPTY, propOrder = {
        "patient"
    })
    public static class Patients {

        @XmlElement(required = true)
        protected List<TPatient> patient;

        /**
         * Gets the value of the patient property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the patient property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPatient().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TPatient }
         * 
         * 
         */
        public List<TPatient> getPatient() {
            if (patient == null) {
                patient = new ArrayList<TPatient>();
            }
            return this.patient;
        }

    }

}
