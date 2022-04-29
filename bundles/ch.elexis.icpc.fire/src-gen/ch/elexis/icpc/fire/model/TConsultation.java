//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.12.07 um 12:49:37 PM CET 
//


package ch.elexis.icpc.fire.model;

import org.apache.commons.lang3.StringUtils;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.elexis.icpc.fire.model.jaxb.DateTimeAdapter;

/**
 * <p>Java-Klasse für tConsultation complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="tConsultation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vital" type="{}tVital" minOccurs="0"/>
 *         &lt;element name="diagnoses" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="reason" type="{}tDiagnose" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="diagnose" type="{}tDiagnose" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="labors" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="labor" type="{}tLabor" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="medis" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="medi" type="{}tMedi" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="patId" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="docId" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="consType" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tConsultation", propOrder = {
    "vital",
    "diagnoses",
    "labors",
    "medis"
})
public class TConsultation {

    protected TVital vital;
    protected TConsultation.Diagnoses diagnoses;
    protected TConsultation.Labors labors;
    protected TConsultation.Medis medis;
    @XmlAttribute(name = "date")
    @XmlSchemaType(name = "dateTime")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
    protected XMLGregorianCalendar date;
    @XmlAttribute(name = "patId")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger patId;
    @XmlAttribute(name = "docId")
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger docId;
	@XmlAttribute(name = "consType")
	protected String consType = "0";

    /**
     * Ruft den Wert der vital-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TVital }
     *     
     */
    public TVital getVital() {
        return vital;
    }

    /**
     * Legt den Wert der vital-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TVital }
     *     
     */
    public void setVital(TVital value) {
        this.vital = value;
    }

    /**
     * Ruft den Wert der diagnoses-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TConsultation.Diagnoses }
     *     
     */
    public TConsultation.Diagnoses getDiagnoses() {
        return diagnoses;
    }

    /**
     * Legt den Wert der diagnoses-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TConsultation.Diagnoses }
     *     
     */
    public void setDiagnoses(TConsultation.Diagnoses value) {
        this.diagnoses = value;
    }

    /**
     * Ruft den Wert der labors-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TConsultation.Labors }
     *     
     */
    public TConsultation.Labors getLabors() {
        return labors;
    }

    /**
     * Legt den Wert der labors-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TConsultation.Labors }
     *     
     */
    public void setLabors(TConsultation.Labors value) {
        this.labors = value;
    }

    /**
     * Ruft den Wert der medis-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link TConsultation.Medis }
     *     
     */
    public TConsultation.Medis getMedis() {
        return medis;
    }

    /**
     * Legt den Wert der medis-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link TConsultation.Medis }
     *     
     */
    public void setMedis(TConsultation.Medis value) {
        this.medis = value;
    }

    /**
     * Ruft den Wert der date-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Legt den Wert der date-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Ruft den Wert der patId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPatId() {
        return patId;
    }

    /**
     * Legt den Wert der patId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPatId(BigInteger value) {
        this.patId = value;
    }

    /**
     * Ruft den Wert der docId-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDocId() {
        return docId;
    }

    /**
     * Legt den Wert der docId-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDocId(BigInteger value) {
        this.docId = value;
    }

    /**
     * Ruft den Wert der consType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsType() {
        return consType;
    }

    /**
     * Legt den Wert der consType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsType(String value) {
        this.consType = value;
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
     *         &lt;element name="reason" type="{}tDiagnose" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="diagnose" type="{}tDiagnose" maxOccurs="unbounded" minOccurs="0"/>
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
        "reason",
        "diagnose"
    })
    public static class Diagnoses {

        protected List<TDiagnose> reason;
        protected List<TDiagnose> diagnose;

        /**
         * Gets the value of the reason property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the reason property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getReason().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TDiagnose }
         * 
         * 
         */
        public List<TDiagnose> getReason() {
            if (reason == null) {
                reason = new ArrayList<TDiagnose>();
            }
            return this.reason;
        }

        /**
         * Gets the value of the diagnose property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the diagnose property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDiagnose().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TDiagnose }
         * 
         * 
         */
        public List<TDiagnose> getDiagnose() {
            if (diagnose == null) {
                diagnose = new ArrayList<TDiagnose>();
            }
            return this.diagnose;
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
     *         &lt;element name="labor" type="{}tLabor" maxOccurs="unbounded" minOccurs="0"/>
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
        "labor"
    })
    public static class Labors {

        protected List<TLabor> labor;

        /**
         * Gets the value of the labor property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the labor property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getLabor().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TLabor }
         * 
         * 
         */
        public List<TLabor> getLabor() {
            if (labor == null) {
                labor = new ArrayList<TLabor>();
            }
            return this.labor;
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
     *         &lt;element name="medi" type="{}tMedi" maxOccurs="unbounded" minOccurs="0"/>
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
        "medi"
    })
    public static class Medis {

        protected List<TMedi> medi;

        /**
         * Gets the value of the medi property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the medi property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMedi().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TMedi }
         * 
         * 
         */
        public List<TMedi> getMedi() {
            if (medi == null) {
                medi = new ArrayList<TMedi>();
            }
            return this.medi;
        }

    }

}
