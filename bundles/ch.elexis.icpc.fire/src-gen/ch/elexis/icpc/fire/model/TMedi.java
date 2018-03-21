//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.12.07 um 12:49:37 PM CET 
//


package ch.elexis.icpc.fire.model;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import ch.elexis.icpc.fire.model.jaxb.DateAdapter;

/**
 * <p>Java-Klasse für tMedi complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="tMedi">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="pharmacode" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="GTIN" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="atc" type="{}tString7" />
 *       &lt;attribute name="dosisMo" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="dosisMi" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="dosisAb" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="dosisNa" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="beginDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="endDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="stopGrund" type="{http://www.w3.org/2001/XMLSchema}unsignedByte" />
 *       &lt;attribute name="mediDauer" type="{}tString50" />
 *       &lt;attribute name="therapieWechsel" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="folgeMedPharmacode" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="folgeMedAtc" type="{}tString7" />
 *       &lt;attribute name="folgeMedGtin" type="{}tString50" />
 *       &lt;attribute name="selbstdisp" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tMedi")
public class TMedi {

	@XmlTransient
	protected String id;
    @XmlAttribute(name = "pharmacode", required = true)
    @XmlSchemaType(name = "unsignedInt")
    protected long pharmacode;
    @XmlAttribute(name = "GTIN", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger gtin;
    @XmlAttribute(name = "atc")
    protected String atc;
    @XmlAttribute(name = "dosisMo")
    protected Float dosisMo;
    @XmlAttribute(name = "dosisMi")
    protected Float dosisMi;
    @XmlAttribute(name = "dosisAb")
    protected Float dosisAb;
    @XmlAttribute(name = "dosisNa")
    protected Float dosisNa;
    @XmlAttribute(name = "beginDate")
    @XmlSchemaType(name = "date")
	@XmlJavaTypeAdapter(DateAdapter.class)
    protected XMLGregorianCalendar beginDate;
    @XmlAttribute(name = "endDate")
    @XmlSchemaType(name = "date")
	@XmlJavaTypeAdapter(DateAdapter.class)
    protected XMLGregorianCalendar endDate;
    @XmlAttribute(name = "stopGrund")
    @XmlSchemaType(name = "unsignedByte")
    protected Short stopGrund;
    @XmlAttribute(name = "mediDauer")
    protected String mediDauer;
    @XmlAttribute(name = "therapieWechsel")
    protected Boolean therapieWechsel;
    @XmlAttribute(name = "folgeMedPharmacode")
    @XmlSchemaType(name = "unsignedInt")
    protected Long folgeMedPharmacode;
    @XmlAttribute(name = "folgeMedAtc")
    protected String folgeMedAtc;
    @XmlAttribute(name = "folgeMedGtin")
    protected String folgeMedGtin;
    @XmlAttribute(name = "selbstdisp")
    protected Boolean selbstdisp;
    
    public String getId(){
		return id;
	}
    
    public void setId(String id){
		this.id = id;
	}

    /**
     * Ruft den Wert der pharmacode-Eigenschaft ab.
     * 
     */
    public long getPharmacode() {
        return pharmacode;
    }

    /**
     * Legt den Wert der pharmacode-Eigenschaft fest.
     * 
     */
    public void setPharmacode(long value) {
        this.pharmacode = value;
    }

    /**
     * Ruft den Wert der gtin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGTIN() {
        return gtin;
    }

    /**
     * Legt den Wert der gtin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGTIN(BigInteger value) {
        this.gtin = value;
    }

    /**
     * Ruft den Wert der atc-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAtc() {
        return atc;
    }

    /**
     * Legt den Wert der atc-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAtc(String value) {
        this.atc = value;
    }

    /**
     * Ruft den Wert der dosisMo-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDosisMo() {
        return dosisMo;
    }

    /**
     * Legt den Wert der dosisMo-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDosisMo(Float value) {
        this.dosisMo = value;
    }

    /**
     * Ruft den Wert der dosisMi-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDosisMi() {
        return dosisMi;
    }

    /**
     * Legt den Wert der dosisMi-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDosisMi(Float value) {
        this.dosisMi = value;
    }

    /**
     * Ruft den Wert der dosisAb-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDosisAb() {
        return dosisAb;
    }

    /**
     * Legt den Wert der dosisAb-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDosisAb(Float value) {
        this.dosisAb = value;
    }

    /**
     * Ruft den Wert der dosisNa-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getDosisNa() {
        return dosisNa;
    }

    /**
     * Legt den Wert der dosisNa-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setDosisNa(Float value) {
        this.dosisNa = value;
    }

    /**
     * Ruft den Wert der beginDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBeginDate() {
        return beginDate;
    }

    /**
     * Legt den Wert der beginDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBeginDate(XMLGregorianCalendar value) {
        this.beginDate = value;
    }

    /**
     * Ruft den Wert der endDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Legt den Wert der endDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Ruft den Wert der stopGrund-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Short }
     *     
     */
    public Short getStopGrund() {
        return stopGrund;
    }

    /**
     * Legt den Wert der stopGrund-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Short }
     *     
     */
    public void setStopGrund(Short value) {
        this.stopGrund = value;
    }

    /**
     * Ruft den Wert der mediDauer-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMediDauer() {
        return mediDauer;
    }

    /**
     * Legt den Wert der mediDauer-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMediDauer(String value) {
        this.mediDauer = value;
    }

    /**
     * Ruft den Wert der therapieWechsel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTherapieWechsel() {
        return therapieWechsel;
    }

    /**
     * Legt den Wert der therapieWechsel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTherapieWechsel(Boolean value) {
        this.therapieWechsel = value;
    }

    /**
     * Ruft den Wert der folgeMedPharmacode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getFolgeMedPharmacode() {
        return folgeMedPharmacode;
    }

    /**
     * Legt den Wert der folgeMedPharmacode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setFolgeMedPharmacode(Long value) {
        this.folgeMedPharmacode = value;
    }

    /**
     * Ruft den Wert der folgeMedAtc-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolgeMedAtc() {
        return folgeMedAtc;
    }

    /**
     * Legt den Wert der folgeMedAtc-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolgeMedAtc(String value) {
        this.folgeMedAtc = value;
    }

    /**
     * Ruft den Wert der folgeMedGtin-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFolgeMedGtin() {
        return folgeMedGtin;
    }

    /**
     * Legt den Wert der folgeMedGtin-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFolgeMedGtin(String value) {
        this.folgeMedGtin = value;
    }

    /**
     * Ruft den Wert der selbstdisp-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSelbstdisp() {
        return selbstdisp;
    }

    /**
     * Legt den Wert der selbstdisp-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSelbstdisp(Boolean value) {
        this.selbstdisp = value;
    }

}
