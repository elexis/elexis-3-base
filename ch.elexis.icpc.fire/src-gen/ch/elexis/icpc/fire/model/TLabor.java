//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.10.31 um 06:30:04 PM CET 
//


package ch.elexis.icpc.fire.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.elexis.icpc.fire.model.jaxb.DateAdapter;

/**
 * <p>
 * Java-Klasse für tLabor complex type.
 * 
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="tLabor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="date" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="quelle" type="{}tString50" />
 *       &lt;attribute name="analyse" use="required" type="{}tString50" />
 *       &lt;attribute name="analyseKurz" type="{}tString50" />
 *       &lt;attribute name="einheit" type="{}tString50" />
 *       &lt;attribute name="wertebereich" type="{}tString255" />
 *       &lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="min" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="wert" type="{}tString50" />
 *       &lt;attribute name="wertNum" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="abnormalFlag" type="{}tString50" />
 *       &lt;attribute name="extern_ind" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>	
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tLabor")
public class TLabor {

    @XmlAttribute(name = "date", required = true)
    @XmlSchemaType(name = "date")
	@XmlJavaTypeAdapter(DateAdapter.class)
    protected XMLGregorianCalendar date;
    @XmlAttribute(name = "quelle")
    protected String quelle;
    @XmlAttribute(name = "analyse", required = true)
    protected String analyse;
    @XmlAttribute(name = "analyseKurz")
    protected String analyseKurz;
    @XmlAttribute(name = "einheit")
    protected String einheit;
    @XmlAttribute(name = "wertebereich")
    protected String wertebereich;
    @XmlAttribute(name = "max")
    protected Float max;
    @XmlAttribute(name = "min")
    protected Float min;
    @XmlAttribute(name = "wert")
    protected String wert;
    @XmlAttribute(name = "wertNum")
    protected Float wertNum;
    @XmlAttribute(name = "abnormalFlag")
    protected String abnormalFlag;
    @XmlAttribute(name = "extern_ind")
    protected Boolean externInd;

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
     * Ruft den Wert der quelle-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuelle() {
        return quelle;
    }

    /**
     * Legt den Wert der quelle-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuelle(String value) {
        this.quelle = value;
    }

    /**
     * Ruft den Wert der analyse-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalyse() {
        return analyse;
    }

    /**
     * Legt den Wert der analyse-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalyse(String value) {
        this.analyse = value;
    }

    /**
     * Ruft den Wert der analyseKurz-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalyseKurz() {
        return analyseKurz;
    }

    /**
     * Legt den Wert der analyseKurz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalyseKurz(String value) {
        this.analyseKurz = value;
    }

    /**
     * Ruft den Wert der einheit-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEinheit() {
        return einheit;
    }

    /**
     * Legt den Wert der einheit-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEinheit(String value) {
        this.einheit = value;
    }

    /**
     * Ruft den Wert der wertebereich-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWertebereich() {
        return wertebereich;
    }

    /**
     * Legt den Wert der wertebereich-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWertebereich(String value) {
        this.wertebereich = value;
    }

    /**
     * Ruft den Wert der max-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMax() {
        return max;
    }

    /**
     * Legt den Wert der max-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMax(Float value) {
        this.max = value;
    }

    /**
     * Ruft den Wert der min-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMin() {
        return min;
    }

    /**
     * Legt den Wert der min-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMin(Float value) {
        this.min = value;
    }

    /**
     * Ruft den Wert der wert-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWert() {
        return wert;
    }

    /**
     * Legt den Wert der wert-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWert(String value) {
        this.wert = value;
    }

    /**
     * Ruft den Wert der wertNum-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getWertNum() {
        return wertNum;
    }

    /**
     * Legt den Wert der wertNum-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setWertNum(Float value) {
        this.wertNum = value;
    }

    /**
     * Ruft den Wert der abnormalFlag-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAbnormalFlag() {
        return abnormalFlag;
    }

    /**
     * Legt den Wert der abnormalFlag-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAbnormalFlag(String value) {
        this.abnormalFlag = value;
    }

    /**
     * Ruft den Wert der externInd-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExternInd() {
        return externInd;
    }

    /**
     * Legt den Wert der externInd-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExternInd(Boolean value) {
        this.externInd = value;
    }

}
