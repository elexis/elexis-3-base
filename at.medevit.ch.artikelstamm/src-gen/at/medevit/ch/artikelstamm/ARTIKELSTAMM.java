//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2015.07.22 um 01:22:22 PM CEST 
//


package at.medevit.ch.artikelstamm;

import java.math.BigInteger;
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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ITEM" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="GTIN">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="PHAR" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="SWISSMEDIC_NO" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="DSCR">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="50"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ADDSCR" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="50"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="ATC" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="8"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="COMP" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="NAME" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="101"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="GLN" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="13"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="PEXF" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                   &lt;element name="PPUB" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                   &lt;element name="PKG_SIZE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                   &lt;element name="SL_ENTRY" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="IKSCAT" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="A"/>
 *                         &lt;enumeration value="B"/>
 *                         &lt;enumeration value="C"/>
 *                         &lt;enumeration value="D"/>
 *                         &lt;enumeration value="E"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="LIMITATION" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="LIMITATION_PTS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                   &lt;element name="LIMITATION_TEXT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="GENERIC_TYPE" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;enumeration value="O"/>
 *                         &lt;enumeration value="G"/>
 *                         &lt;enumeration value="K"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="HAS_GENERIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="LPPV" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="DEDUCTIBLE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                   &lt;element name="NARCOTIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                   &lt;element name="NARCOTIC_CAS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="PRODNO" minOccurs="0">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="CREATION_DATETIME" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="LANG" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="MONTH" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="YEAR" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="CUMUL_VER" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="TYPE" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="P"/>
 *             &lt;enumeration value="N"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="DATA_QUALITY" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "item"
})
@XmlRootElement(name = "ARTIKELSTAMM")
public class ARTIKELSTAMM {

    @XmlElement(name = "ITEM")
    protected List<ARTIKELSTAMM.ITEM> item;
    @XmlAttribute(name = "CREATION_DATETIME", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar creationdatetime;
    @XmlAttribute(name = "LANG")
    protected String lang;
    @XmlAttribute(name = "MONTH")
    protected Integer month;
    @XmlAttribute(name = "YEAR")
    protected Integer year;
    @XmlAttribute(name = "CUMUL_VER", required = true)
    protected int cumulver;
    @XmlAttribute(name = "TYPE", required = true)
    protected String type;
    @XmlAttribute(name = "DATA_QUALITY")
    protected Integer dataquality;

    /**
     * Gets the value of the item property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getITEM().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ARTIKELSTAMM.ITEM }
     * 
     * 
     */
    public List<ARTIKELSTAMM.ITEM> getITEM() {
        if (item == null) {
            item = new ArrayList<ARTIKELSTAMM.ITEM>();
        }
        return this.item;
    }

    /**
     * Ruft den Wert der creationdatetime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getCREATIONDATETIME() {
        return creationdatetime;
    }

    /**
     * Legt den Wert der creationdatetime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setCREATIONDATETIME(XMLGregorianCalendar value) {
        this.creationdatetime = value;
    }

    /**
     * Ruft den Wert der lang-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLANG() {
        return lang;
    }

    /**
     * Legt den Wert der lang-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLANG(String value) {
        this.lang = value;
    }

    /**
     * Ruft den Wert der month-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMONTH() {
        return month;
    }

    /**
     * Legt den Wert der month-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMONTH(Integer value) {
        this.month = value;
    }

    /**
     * Ruft den Wert der year-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getYEAR() {
        return year;
    }

    /**
     * Legt den Wert der year-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setYEAR(Integer value) {
        this.year = value;
    }

    /**
     * Ruft den Wert der cumulver-Eigenschaft ab.
     * 
     */
    public int getCUMULVER() {
        return cumulver;
    }

    /**
     * Legt den Wert der cumulver-Eigenschaft fest.
     * 
     */
    public void setCUMULVER(int value) {
        this.cumulver = value;
    }

    /**
     * Ruft den Wert der type-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPE() {
        return type;
    }

    /**
     * Legt den Wert der type-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTYPE(String value) {
        this.type = value;
    }

    /**
     * Ruft den Wert der dataquality-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDATAQUALITY() {
        return dataquality;
    }

    /**
     * Legt den Wert der dataquality-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDATAQUALITY(Integer value) {
        this.dataquality = value;
    }


    /**
     * 
     *     						Packungsgröße verrechnet, also Anzahl der
     *     						beinhalteten Elemente (bspw. 100 Tabletten)
     *     					
     * 
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="GTIN">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="PHAR" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="SWISSMEDIC_NO" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="DSCR">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="50"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="ADDSCR" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="50"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="ATC" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="8"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="COMP" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="NAME" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="101"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="GLN" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;maxLength value="13"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="PEXF" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
     *         &lt;element name="PPUB" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
     *         &lt;element name="PKG_SIZE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *         &lt;element name="SL_ENTRY" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="IKSCAT" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="A"/>
     *               &lt;enumeration value="B"/>
     *               &lt;enumeration value="C"/>
     *               &lt;enumeration value="D"/>
     *               &lt;enumeration value="E"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="LIMITATION" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="LIMITATION_PTS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *         &lt;element name="LIMITATION_TEXT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="GENERIC_TYPE" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;enumeration value="O"/>
     *               &lt;enumeration value="G"/>
     *               &lt;enumeration value="K"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="HAS_GENERIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="LPPV" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="DEDUCTIBLE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *         &lt;element name="NARCOTIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *         &lt;element name="NARCOTIC_CAS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="PRODNO" minOccurs="0">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "gtin",
        "phar",
        "swissmedicno",
        "dscr",
        "addscr",
        "atc",
        "comp",
        "pexf",
        "ppub",
        "pkgsize",
        "slentry",
        "ikscat",
        "limitation",
        "limitationpts",
        "limitationtext",
        "generictype",
        "hasgeneric",
        "lppv",
        "deductible",
        "narcotic",
        "narcoticcas",
        "prodno",
        "limnamebag"
    })
    public static class ITEM {

        @XmlElement(name = "GTIN", required = true)
        protected String gtin;
        @XmlElement(name = "PHAR")
        protected BigInteger phar;
        @XmlElement(name = "SWISSMEDIC_NO")
        protected String swissmedicno;
        @XmlElement(name = "DSCR", required = true)
        protected String dscr;
        @XmlElement(name = "ADDSCR")
        protected String addscr;
        @XmlElement(name = "ATC")
        protected String atc;
        @XmlElement(name = "COMP")
        protected ARTIKELSTAMM.ITEM.COMP comp;
        @XmlElement(name = "PEXF")
        protected Double pexf;
        @XmlElement(name = "PPUB")
        protected Double ppub;
        @XmlElement(name = "PKG_SIZE")
        protected Integer pkgsize;
        @XmlElement(name = "SL_ENTRY")
        protected Boolean slentry;
        @XmlElement(name = "IKSCAT")
        protected String ikscat;
        @XmlElement(name = "LIMITATION")
        protected Boolean limitation;
        @XmlElement(name = "LIMITATION_PTS")
        protected Integer limitationpts;
        @XmlElement(name = "LIMITATION_TEXT")
        protected String limitationtext;
        @XmlElement(name = "GENERIC_TYPE")
        protected String generictype;
        @XmlElement(name = "HAS_GENERIC")
        protected Boolean hasgeneric;
        @XmlElement(name = "LPPV")
        protected Boolean lppv;
        @XmlElement(name = "DEDUCTIBLE")
        protected Integer deductible;
        @XmlElement(name = "NARCOTIC")
        protected Boolean narcotic;
        @XmlElement(name = "NARCOTIC_CAS")
        protected String narcoticcas;
        @XmlElement(name = "PRODNO")
        protected String prodno;
        @XmlElement(name = "LIMNAMEBAG")
        protected String limnamebag;

        /**
         * Ruft den Wert der gtin-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGTIN() {
            return gtin;
        }

        /**
         * Legt den Wert der gtin-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGTIN(String value) {
            this.gtin = value;
        }

        /**
         * Ruft den Wert der phar-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getPHAR() {
            return phar;
        }

        /**
         * Legt den Wert der phar-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setPHAR(BigInteger value) {
            this.phar = value;
        }

        /**
         * Ruft den Wert der swissmedicno-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSWISSMEDICNO() {
            return swissmedicno;
        }

        /**
         * Legt den Wert der swissmedicno-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSWISSMEDICNO(String value) {
            this.swissmedicno = value;
        }

        /**
         * Ruft den Wert der dscr-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDSCR() {
            return dscr;
        }

        /**
         * Legt den Wert der dscr-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDSCR(String value) {
            this.dscr = value;
        }

        /**
         * Ruft den Wert der addscr-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getADDSCR() {
            return addscr;
        }

        /**
         * Legt den Wert der addscr-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setADDSCR(String value) {
            this.addscr = value;
        }

        /**
         * Ruft den Wert der atc-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getATC() {
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
        public void setATC(String value) {
            this.atc = value;
        }

        /**
         * Ruft den Wert der comp-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link ARTIKELSTAMM.ITEM.COMP }
         *     
         */
        public ARTIKELSTAMM.ITEM.COMP getCOMP() {
            return comp;
        }

        /**
         * Legt den Wert der comp-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link ARTIKELSTAMM.ITEM.COMP }
         *     
         */
        public void setCOMP(ARTIKELSTAMM.ITEM.COMP value) {
            this.comp = value;
        }

        /**
         * Ruft den Wert der pexf-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getPEXF() {
            return pexf;
        }

        /**
         * Legt den Wert der pexf-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setPEXF(Double value) {
            this.pexf = value;
        }

        /**
         * Ruft den Wert der ppub-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Double }
         *     
         */
        public Double getPPUB() {
            return ppub;
        }

        /**
         * Legt den Wert der ppub-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Double }
         *     
         */
        public void setPPUB(Double value) {
            this.ppub = value;
        }

        /**
         * Ruft den Wert der pkgsize-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getPKGSIZE() {
            return pkgsize;
        }

        /**
         * Legt den Wert der pkgsize-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setPKGSIZE(Integer value) {
            this.pkgsize = value;
        }

        /**
         * Ruft den Wert der slentry-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSLENTRY() {
            return slentry;
        }

        /**
         * Legt den Wert der slentry-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSLENTRY(Boolean value) {
            this.slentry = value;
        }

        /**
         * Ruft den Wert der ikscat-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getIKSCAT() {
            return ikscat;
        }

        /**
         * Legt den Wert der ikscat-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setIKSCAT(String value) {
            this.ikscat = value;
        }

        /**
         * Ruft den Wert der limitation-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isLIMITATION() {
            return limitation;
        }

        /**
         * Legt den Wert der limitation-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setLIMITATION(Boolean value) {
            this.limitation = value;
        }

        /**
         * Ruft den Wert der limitationpts-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getLIMITATIONPTS() {
            return limitationpts;
        }

        /**
         * Legt den Wert der limitationpts-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setLIMITATIONPTS(Integer value) {
            this.limitationpts = value;
        }

        /**
         * Ruft den Wert der limitationtext-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLIMITATIONTEXT() {
            return limitationtext;
        }

        /**
         * Legt den Wert der limitationtext-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLIMITATIONTEXT(String value) {
            this.limitationtext = value;
        }

        /**
         * Ruft den Wert der generictype-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getGENERICTYPE() {
            return generictype;
        }

        /**
         * Legt den Wert der generictype-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setGENERICTYPE(String value) {
            this.generictype = value;
        }

        /**
         * Ruft den Wert der hasgeneric-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isHASGENERIC() {
            return hasgeneric;
        }

        /**
         * Legt den Wert der hasgeneric-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setHASGENERIC(Boolean value) {
            this.hasgeneric = value;
        }

        /**
         * Ruft den Wert der lppv-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isLPPV() {
            return lppv;
        }

        /**
         * Legt den Wert der lppv-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setLPPV(Boolean value) {
            this.lppv = value;
        }

        /**
         * Ruft den Wert der deductible-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getDEDUCTIBLE() {
            return deductible;
        }

        /**
         * Legt den Wert der deductible-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setDEDUCTIBLE(Integer value) {
            this.deductible = value;
        }

        /**
         * Ruft den Wert der narcotic-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isNARCOTIC() {
            return narcotic;
        }

        /**
         * Legt den Wert der narcotic-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setNARCOTIC(Boolean value) {
            this.narcotic = value;
        }

        /**
         * Ruft den Wert der narcoticcas-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNARCOTICCAS() {
            return narcoticcas;
        }

        /**
         * Legt den Wert der narcoticcas-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNARCOTICCAS(String value) {
            this.narcoticcas = value;
        }

        /**
         * Ruft den Wert der prodno-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPRODNO() {
            return prodno;
        }

        /**
         * Legt den Wert der prodno-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPRODNO(String value) {
            this.prodno = value;
        }

        /**
         * Ruft den Wert der limnamebag-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLIMNAMEBAG() {
            return limnamebag;
        }

        /**
         * Legt den Wert der limnamebag-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLIMNAMEBAG(String value) {
            this.limnamebag = value;
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
         *         &lt;element name="NAME" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="101"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="GLN" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;maxLength value="13"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "name",
            "gln"
        })
        public static class COMP {

            @XmlElement(name = "NAME")
            protected String name;
            @XmlElement(name = "GLN")
            protected String gln;

            /**
             * Ruft den Wert der name-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getNAME() {
                return name;
            }

            /**
             * Legt den Wert der name-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setNAME(String value) {
                this.name = value;
            }

            /**
             * Ruft den Wert der gln-Eigenschaft ab.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getGLN() {
                return gln;
            }

            /**
             * Legt den Wert der gln-Eigenschaft fest.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setGLN(String value) {
                this.gln = value;
            }

        }

    }

}
