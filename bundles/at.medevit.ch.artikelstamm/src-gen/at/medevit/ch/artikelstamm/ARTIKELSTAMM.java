//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren.
// Generiert: 2022.08.13 um 05:37:12 PM CEST
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
 * <p>
 * Java-Klasse für anonymous complex type.
 *
 * <p>
 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
 * Klasse enthalten ist.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PRODUCTS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PRODUCT" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="PRODNO" type="{http://elexis.ch/Elexis_Artikelstamm_v5}PRODNOType"/>
 *                             &lt;element name="SALECD" type="{http://elexis.ch/Elexis_Artikelstamm_v5}SALECDType"/>
 *                             &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
 *                             &lt;element name="DSCRF" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
 *                             &lt;element name="DSCRI" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
 *                             &lt;element name="ATC" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;maxLength value="8"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="SUBSTANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="SUBSTANCEF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="LIMITATIONS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="LIMITATION" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
 *                             &lt;element name="DSCRF" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
 *                             &lt;element name="DSCRI" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
 *                             &lt;element name="LIMITATION_PTS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ITEMS">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ITEM" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="GTIN" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="PHAR" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="SALECD" type="{http://elexis.ch/Elexis_Artikelstamm_v5}SALECDType"/>
 *                             &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
 *                             &lt;element name="DSCRF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="DSCRI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="COMP" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="NAME" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="101"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                       &lt;element name="GLN" minOccurs="0">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;maxLength value="13"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="PEXF" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                             &lt;element name="PPUB" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *                             &lt;element name="PKG_SIZE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="MEASURE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="MEASUREF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="DOSAGE_FORM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="DOSAGE_FORMF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="DOSAGE_FORMI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="SL_ENTRY" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                             &lt;element name="IKSCAT" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="A"/>
 *                                   &lt;enumeration value="B"/>
 *                                   &lt;enumeration value="C"/>
 *                                   &lt;enumeration value="D"/>
 *                                   &lt;enumeration value="E"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="GENERIC_TYPE" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="O"/>
 *                                   &lt;enumeration value="G"/>
 *                                   &lt;enumeration value="K"/>
 *                                   &lt;enumeration value="C"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="HAS_GENERIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                             &lt;element name="LPPV" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                             &lt;element name="DEDUCTIBLE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="NARCOTIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                             &lt;element name="NARCOTIC_CAS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="PRODNO" type="{http://elexis.ch/Elexis_Artikelstamm_v5}PRODNOType" minOccurs="0"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="PHARMATYPE">
 *                             &lt;simpleType>
 *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                 &lt;enumeration value="N"/>
 *                                 &lt;enumeration value="P"/>
 *                               &lt;/restriction>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="CREATION_DATETIME" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="BUILD_DATETIME" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="DATA_SOURCE" use="required" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DATASOURCEType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "products", "limitations", "items" })
@XmlRootElement(name = "ARTIKELSTAMM")
public class ARTIKELSTAMM {

	@XmlElement(name = "PRODUCTS", required = true)
	protected ARTIKELSTAMM.PRODUCTS products;
	@XmlElement(name = "LIMITATIONS", required = true)
	protected ARTIKELSTAMM.LIMITATIONS limitations;
	@XmlElement(name = "ITEMS", required = true)
	protected ARTIKELSTAMM.ITEMS items;
	@XmlAttribute(name = "CREATION_DATETIME", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar creationdatetime;
	@XmlAttribute(name = "BUILD_DATETIME", required = true)
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar builddatetime;
	@XmlAttribute(name = "DATA_SOURCE", required = true)
	protected DATASOURCEType datasource;

	/**
	 * Ruft den Wert der products-Eigenschaft ab.
	 *
	 * @return possible object is {@link ARTIKELSTAMM.PRODUCTS }
	 *
	 */
	public ARTIKELSTAMM.PRODUCTS getPRODUCTS() {
		return products;
	}

	/**
	 * Legt den Wert der products-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link ARTIKELSTAMM.PRODUCTS }
	 *
	 */
	public void setPRODUCTS(ARTIKELSTAMM.PRODUCTS value) {
		this.products = value;
	}

	/**
	 * Ruft den Wert der limitations-Eigenschaft ab.
	 *
	 * @return possible object is {@link ARTIKELSTAMM.LIMITATIONS }
	 *
	 */
	public ARTIKELSTAMM.LIMITATIONS getLIMITATIONS() {
		return limitations;
	}

	/**
	 * Legt den Wert der limitations-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link ARTIKELSTAMM.LIMITATIONS }
	 *
	 */
	public void setLIMITATIONS(ARTIKELSTAMM.LIMITATIONS value) {
		this.limitations = value;
	}

	/**
	 * Ruft den Wert der items-Eigenschaft ab.
	 *
	 * @return possible object is {@link ARTIKELSTAMM.ITEMS }
	 *
	 */
	public ARTIKELSTAMM.ITEMS getITEMS() {
		return items;
	}

	/**
	 * Legt den Wert der items-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link ARTIKELSTAMM.ITEMS }
	 *
	 */
	public void setITEMS(ARTIKELSTAMM.ITEMS value) {
		this.items = value;
	}

	/**
	 * Ruft den Wert der creationdatetime-Eigenschaft ab.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getCREATIONDATETIME() {
		return creationdatetime;
	}

	/**
	 * Legt den Wert der creationdatetime-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setCREATIONDATETIME(XMLGregorianCalendar value) {
		this.creationdatetime = value;
	}

	/**
	 * Ruft den Wert der builddatetime-Eigenschaft ab.
	 *
	 * @return possible object is {@link XMLGregorianCalendar }
	 *
	 */
	public XMLGregorianCalendar getBUILDDATETIME() {
		return builddatetime;
	}

	/**
	 * Legt den Wert der builddatetime-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link XMLGregorianCalendar }
	 *
	 */
	public void setBUILDDATETIME(XMLGregorianCalendar value) {
		this.builddatetime = value;
	}

	/**
	 * Ruft den Wert der datasource-Eigenschaft ab.
	 *
	 * @return possible object is {@link DATASOURCEType }
	 *
	 */
	public DATASOURCEType getDATASOURCE() {
		return datasource;
	}

	/**
	 * Legt den Wert der datasource-Eigenschaft fest.
	 *
	 * @param value allowed object is {@link DATASOURCEType }
	 *
	 */
	public void setDATASOURCE(DATASOURCEType value) {
		this.datasource = value;
	}

	/**
	 * <p>
	 * Java-Klasse für anonymous complex type.
	 *
	 * <p>
	 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
	 * Klasse enthalten ist.
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
	 *                   &lt;element name="GTIN" minOccurs="0">
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
	 *                   &lt;element name="SALECD" type="{http://elexis.ch/Elexis_Artikelstamm_v5}SALECDType"/>
	 *                   &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
	 *                   &lt;element name="DSCRF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="DSCRI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
	 *                   &lt;element name="MEASURE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="MEASUREF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="DOSAGE_FORM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="DOSAGE_FORMF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="DOSAGE_FORMI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
	 *                   &lt;element name="GENERIC_TYPE" minOccurs="0">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                         &lt;enumeration value="O"/>
	 *                         &lt;enumeration value="G"/>
	 *                         &lt;enumeration value="K"/>
	 *                         &lt;enumeration value="C"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="HAS_GENERIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
	 *                   &lt;element name="LPPV" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
	 *                   &lt;element name="DEDUCTIBLE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
	 *                   &lt;element name="NARCOTIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
	 *                   &lt;element name="NARCOTIC_CAS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="PRODNO" type="{http://elexis.ch/Elexis_Artikelstamm_v5}PRODNOType" minOccurs="0"/>
	 *                 &lt;/sequence>
	 *                 &lt;attribute name="PHARMATYPE">
	 *                   &lt;simpleType>
	 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                       &lt;enumeration value="N"/>
	 *                       &lt;enumeration value="P"/>
	 *                     &lt;/restriction>
	 *                   &lt;/simpleType>
	 *                 &lt;/attribute>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
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
	@XmlType(name = "", propOrder = { "item" })
	public static class ITEMS {

		@XmlElement(name = "ITEM")
		protected List<ARTIKELSTAMM.ITEMS.ITEM> item;

		/**
		 * Gets the value of the item property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot.
		 * Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
		 * for the item property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 *
		 * <pre>
		 * getITEM().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link ARTIKELSTAMM.ITEMS.ITEM }
		 *
		 *
		 */
		public List<ARTIKELSTAMM.ITEMS.ITEM> getITEM() {
			if (item == null) {
				item = new ArrayList<ARTIKELSTAMM.ITEMS.ITEM>();
			}
			return this.item;
		}

		/**
		 *
		 * Packungsgröße verrechnet, also Anzahl der beinhalteten Elemente (bspw. 100
		 * Tabletten)
		 *
		 *
		 * <p>
		 * Java-Klasse für anonymous complex type.
		 *
		 * <p>
		 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
		 * Klasse enthalten ist.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="GTIN" minOccurs="0">
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
		 *         &lt;element name="SALECD" type="{http://elexis.ch/Elexis_Artikelstamm_v5}SALECDType"/>
		 *         &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
		 *         &lt;element name="DSCRF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="DSCRI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
		 *         &lt;element name="MEASURE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="MEASUREF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="DOSAGE_FORM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="DOSAGE_FORMF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="DOSAGE_FORMI" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
		 *         &lt;element name="GENERIC_TYPE" minOccurs="0">
		 *           &lt;simpleType>
		 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *               &lt;enumeration value="O"/>
		 *               &lt;enumeration value="G"/>
		 *               &lt;enumeration value="K"/>
		 *               &lt;enumeration value="C"/>
		 *             &lt;/restriction>
		 *           &lt;/simpleType>
		 *         &lt;/element>
		 *         &lt;element name="HAS_GENERIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
		 *         &lt;element name="LPPV" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
		 *         &lt;element name="DEDUCTIBLE" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
		 *         &lt;element name="NARCOTIC" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
		 *         &lt;element name="NARCOTIC_CAS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="PRODNO" type="{http://elexis.ch/Elexis_Artikelstamm_v5}PRODNOType" minOccurs="0"/>
		 *       &lt;/sequence>
		 *       &lt;attribute name="PHARMATYPE">
		 *         &lt;simpleType>
		 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *             &lt;enumeration value="N"/>
		 *             &lt;enumeration value="P"/>
		 *           &lt;/restriction>
		 *         &lt;/simpleType>
		 *       &lt;/attribute>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "gtin", "phar", "salecd", "dscr", "dscrf", "dscri", "comp", "pexf", "ppub",
				"pkgsize", "measure", "measuref", "dosageform", "dosageformf", "dosageformi", "slentry", "ikscat",
				"generictype", "hasgeneric", "lppv", "deductible", "narcotic", "narcoticcas", "prodno" })
		public static class ITEM {

			@XmlElement(name = "GTIN")
			protected String gtin;
			@XmlElement(name = "PHAR")
			protected BigInteger phar;
			@XmlElement(name = "SALECD", required = true)
			@XmlSchemaType(name = "string")
			protected SALECDType salecd;
			@XmlElement(name = "DSCR", required = true)
			protected String dscr;
			@XmlElement(name = "DSCRF")
			protected String dscrf;
			@XmlElement(name = "DSCRI")
			protected String dscri;
			@XmlElement(name = "COMP")
			protected ARTIKELSTAMM.ITEMS.ITEM.COMP comp;
			@XmlElement(name = "PEXF")
			protected Double pexf;
			@XmlElement(name = "PPUB")
			protected Double ppub;
			@XmlElement(name = "PKG_SIZE")
			protected Integer pkgsize;
			@XmlElement(name = "MEASURE")
			protected String measure;
			@XmlElement(name = "MEASUREF")
			protected String measuref;
			@XmlElement(name = "DOSAGE_FORM")
			protected String dosageform;
			@XmlElement(name = "DOSAGE_FORMF")
			protected String dosageformf;
			@XmlElement(name = "DOSAGE_FORMI")
			protected String dosageformi;
			@XmlElement(name = "SL_ENTRY")
			protected Boolean slentry;
			@XmlElement(name = "IKSCAT")
			protected String ikscat;
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
			@XmlAttribute(name = "PHARMATYPE")
			protected String pharmatype;

			/**
			 * Ruft den Wert der gtin-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getGTIN() {
				return gtin;
			}

			/**
			 * Legt den Wert der gtin-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setGTIN(String value) {
				this.gtin = value;
			}

			/**
			 * Ruft den Wert der phar-Eigenschaft ab.
			 *
			 * @return possible object is {@link BigInteger }
			 *
			 */
			public BigInteger getPHAR() {
				return phar;
			}

			/**
			 * Legt den Wert der phar-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link BigInteger }
			 *
			 */
			public void setPHAR(BigInteger value) {
				this.phar = value;
			}

			/**
			 * Ruft den Wert der salecd-Eigenschaft ab.
			 *
			 * @return possible object is {@link SALECDType }
			 *
			 */
			public SALECDType getSALECD() {
				return salecd;
			}

			/**
			 * Legt den Wert der salecd-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link SALECDType }
			 *
			 */
			public void setSALECD(SALECDType value) {
				this.salecd = value;
			}

			/**
			 * Ruft den Wert der dscr-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCR() {
				return dscr;
			}

			/**
			 * Legt den Wert der dscr-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCR(String value) {
				this.dscr = value;
			}

			/**
			 * Ruft den Wert der dscrf-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCRF() {
				return dscrf;
			}

			/**
			 * Legt den Wert der dscrf-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCRF(String value) {
				this.dscrf = value;
			}

			/**
			 * Ruft den Wert der dscri-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCRI() {
				return dscri;
			}

			/**
			 * Legt den Wert der dscri-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCRI(String value) {
				this.dscri = value;
			}

			/**
			 * Ruft den Wert der comp-Eigenschaft ab.
			 *
			 * @return possible object is {@link ARTIKELSTAMM.ITEMS.ITEM.COMP }
			 *
			 */
			public ARTIKELSTAMM.ITEMS.ITEM.COMP getCOMP() {
				return comp;
			}

			/**
			 * Legt den Wert der comp-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link ARTIKELSTAMM.ITEMS.ITEM.COMP }
			 *
			 */
			public void setCOMP(ARTIKELSTAMM.ITEMS.ITEM.COMP value) {
				this.comp = value;
			}

			/**
			 * Ruft den Wert der pexf-Eigenschaft ab.
			 *
			 * @return possible object is {@link Double }
			 *
			 */
			public Double getPEXF() {
				return pexf;
			}

			/**
			 * Legt den Wert der pexf-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Double }
			 *
			 */
			public void setPEXF(Double value) {
				this.pexf = value;
			}

			/**
			 * Ruft den Wert der ppub-Eigenschaft ab.
			 *
			 * @return possible object is {@link Double }
			 *
			 */
			public Double getPPUB() {
				return ppub;
			}

			/**
			 * Legt den Wert der ppub-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Double }
			 *
			 */
			public void setPPUB(Double value) {
				this.ppub = value;
			}

			/**
			 * Ruft den Wert der pkgsize-Eigenschaft ab.
			 *
			 * @return possible object is {@link Integer }
			 *
			 */
			public Integer getPKGSIZE() {
				return pkgsize;
			}

			/**
			 * Legt den Wert der pkgsize-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Integer }
			 *
			 */
			public void setPKGSIZE(Integer value) {
				this.pkgsize = value;
			}

			/**
			 * Ruft den Wert der measure-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getMEASURE() {
				return measure;
			}

			/**
			 * Legt den Wert der measure-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setMEASURE(String value) {
				this.measure = value;
			}

			/**
			 * Ruft den Wert der measuref-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getMEASUREF() {
				return measuref;
			}

			/**
			 * Legt den Wert der measuref-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setMEASUREF(String value) {
				this.measuref = value;
			}

			/**
			 * Ruft den Wert der dosageform-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDOSAGEFORM() {
				return dosageform;
			}

			/**
			 * Legt den Wert der dosageform-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDOSAGEFORM(String value) {
				this.dosageform = value;
			}

			/**
			 * Ruft den Wert der dosageformf-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDOSAGEFORMF() {
				return dosageformf;
			}

			/**
			 * Legt den Wert der dosageformf-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDOSAGEFORMF(String value) {
				this.dosageformf = value;
			}

			/**
			 * Ruft den Wert der dosageformi-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDOSAGEFORMI() {
				return dosageformi;
			}

			/**
			 * Legt den Wert der dosageformi-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDOSAGEFORMI(String value) {
				this.dosageformi = value;
			}

			/**
			 * Ruft den Wert der slentry-Eigenschaft ab.
			 *
			 * @return possible object is {@link Boolean }
			 *
			 */
			public Boolean isSLENTRY() {
				return slentry;
			}

			/**
			 * Legt den Wert der slentry-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Boolean }
			 *
			 */
			public void setSLENTRY(Boolean value) {
				this.slentry = value;
			}

			/**
			 * Ruft den Wert der ikscat-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getIKSCAT() {
				return ikscat;
			}

			/**
			 * Legt den Wert der ikscat-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setIKSCAT(String value) {
				this.ikscat = value;
			}

			/**
			 * Ruft den Wert der generictype-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getGENERICTYPE() {
				return generictype;
			}

			/**
			 * Legt den Wert der generictype-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setGENERICTYPE(String value) {
				this.generictype = value;
			}

			/**
			 * Ruft den Wert der hasgeneric-Eigenschaft ab.
			 *
			 * @return possible object is {@link Boolean }
			 *
			 */
			public Boolean isHASGENERIC() {
				return hasgeneric;
			}

			/**
			 * Legt den Wert der hasgeneric-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Boolean }
			 *
			 */
			public void setHASGENERIC(Boolean value) {
				this.hasgeneric = value;
			}

			/**
			 * Ruft den Wert der lppv-Eigenschaft ab.
			 *
			 * @return possible object is {@link Boolean }
			 *
			 */
			public Boolean isLPPV() {
				return lppv;
			}

			/**
			 * Legt den Wert der lppv-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Boolean }
			 *
			 */
			public void setLPPV(Boolean value) {
				this.lppv = value;
			}

			/**
			 * Ruft den Wert der deductible-Eigenschaft ab.
			 *
			 * @return possible object is {@link Integer }
			 *
			 */
			public Integer getDEDUCTIBLE() {
				return deductible;
			}

			/**
			 * Legt den Wert der deductible-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Integer }
			 *
			 */
			public void setDEDUCTIBLE(Integer value) {
				this.deductible = value;
			}

			/**
			 * Ruft den Wert der narcotic-Eigenschaft ab.
			 *
			 * @return possible object is {@link Boolean }
			 *
			 */
			public Boolean isNARCOTIC() {
				return narcotic;
			}

			/**
			 * Legt den Wert der narcotic-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Boolean }
			 *
			 */
			public void setNARCOTIC(Boolean value) {
				this.narcotic = value;
			}

			/**
			 * Ruft den Wert der narcoticcas-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getNARCOTICCAS() {
				return narcoticcas;
			}

			/**
			 * Legt den Wert der narcoticcas-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setNARCOTICCAS(String value) {
				this.narcoticcas = value;
			}

			/**
			 * Ruft den Wert der prodno-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getPRODNO() {
				return prodno;
			}

			/**
			 * Legt den Wert der prodno-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setPRODNO(String value) {
				this.prodno = value;
			}

			/**
			 * Ruft den Wert der pharmatype-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getPHARMATYPE() {
				return pharmatype;
			}

			/**
			 * Legt den Wert der pharmatype-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setPHARMATYPE(String value) {
				this.pharmatype = value;
			}

			/**
			 * <p>
			 * Java-Klasse für anonymous complex type.
			 *
			 * <p>
			 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
			 * Klasse enthalten ist.
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
			@XmlType(name = "", propOrder = { "name", "gln" })
			public static class COMP {

				@XmlElement(name = "NAME")
				protected String name;
				@XmlElement(name = "GLN")
				protected String gln;

				/**
				 * Ruft den Wert der name-Eigenschaft ab.
				 *
				 * @return possible object is {@link String }
				 *
				 */
				public String getNAME() {
					return name;
				}

				/**
				 * Legt den Wert der name-Eigenschaft fest.
				 *
				 * @param value allowed object is {@link String }
				 *
				 */
				public void setNAME(String value) {
					this.name = value;
				}

				/**
				 * Ruft den Wert der gln-Eigenschaft ab.
				 *
				 * @return possible object is {@link String }
				 *
				 */
				public String getGLN() {
					return gln;
				}

				/**
				 * Legt den Wert der gln-Eigenschaft fest.
				 *
				 * @param value allowed object is {@link String }
				 *
				 */
				public void setGLN(String value) {
					this.gln = value;
				}

			}

		}

	}

	/**
	 * <p>
	 * Java-Klasse für anonymous complex type.
	 *
	 * <p>
	 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
	 * Klasse enthalten ist.
	 *
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="LIMITATION" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string"/>
	 *                   &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
	 *                   &lt;element name="DSCRF" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
	 *                   &lt;element name="DSCRI" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
	 *                   &lt;element name="LIMITATION_PTS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
	 *                 &lt;/sequence>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
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
	@XmlType(name = "", propOrder = { "limitation" })
	public static class LIMITATIONS {

		@XmlElement(name = "LIMITATION")
		protected List<ARTIKELSTAMM.LIMITATIONS.LIMITATION> limitation;

		/**
		 * Gets the value of the limitation property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot.
		 * Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
		 * for the limitation property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 *
		 * <pre>
		 * getLIMITATION().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link ARTIKELSTAMM.LIMITATIONS.LIMITATION }
		 *
		 *
		 */
		public List<ARTIKELSTAMM.LIMITATIONS.LIMITATION> getLIMITATION() {
			if (limitation == null) {
				limitation = new ArrayList<ARTIKELSTAMM.LIMITATIONS.LIMITATION>();
			}
			return this.limitation;
		}

		/**
		 * <p>
		 * Java-Klasse für anonymous complex type.
		 *
		 * <p>
		 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
		 * Klasse enthalten ist.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string"/>
		 *         &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
		 *         &lt;element name="DSCRF" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
		 *         &lt;element name="DSCRI" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
		 *         &lt;element name="LIMITATION_PTS" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "limnamebag", "dscr", "dscrf", "dscri", "limitationpts" })
		public static class LIMITATION {

			@XmlElement(name = "LIMNAMEBAG", required = true)
			protected String limnamebag;
			@XmlElement(name = "DSCR", required = true)
			protected String dscr;
			@XmlElement(name = "DSCRF")
			protected String dscrf;
			@XmlElement(name = "DSCRI")
			protected String dscri;
			@XmlElement(name = "LIMITATION_PTS")
			protected Integer limitationpts;

			/**
			 * Ruft den Wert der limnamebag-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getLIMNAMEBAG() {
				return limnamebag;
			}

			/**
			 * Legt den Wert der limnamebag-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setLIMNAMEBAG(String value) {
				this.limnamebag = value;
			}

			/**
			 * Ruft den Wert der dscr-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCR() {
				return dscr;
			}

			/**
			 * Legt den Wert der dscr-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCR(String value) {
				this.dscr = value;
			}

			/**
			 * Ruft den Wert der dscrf-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCRF() {
				return dscrf;
			}

			/**
			 * Legt den Wert der dscrf-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCRF(String value) {
				this.dscrf = value;
			}

			/**
			 * Ruft den Wert der dscri-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCRI() {
				return dscri;
			}

			/**
			 * Legt den Wert der dscri-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCRI(String value) {
				this.dscri = value;
			}

			/**
			 * Ruft den Wert der limitationpts-Eigenschaft ab.
			 *
			 * @return possible object is {@link Integer }
			 *
			 */
			public Integer getLIMITATIONPTS() {
				return limitationpts;
			}

			/**
			 * Legt den Wert der limitationpts-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link Integer }
			 *
			 */
			public void setLIMITATIONPTS(Integer value) {
				this.limitationpts = value;
			}

		}

	}

	/**
	 * <p>
	 * Java-Klasse für anonymous complex type.
	 *
	 * <p>
	 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
	 * Klasse enthalten ist.
	 *
	 * <pre>
	 * &lt;complexType>
	 *   &lt;complexContent>
	 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *       &lt;sequence>
	 *         &lt;element name="PRODUCT" maxOccurs="unbounded" minOccurs="0">
	 *           &lt;complexType>
	 *             &lt;complexContent>
	 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
	 *                 &lt;sequence>
	 *                   &lt;element name="PRODNO" type="{http://elexis.ch/Elexis_Artikelstamm_v5}PRODNOType"/>
	 *                   &lt;element name="SALECD" type="{http://elexis.ch/Elexis_Artikelstamm_v5}SALECDType"/>
	 *                   &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
	 *                   &lt;element name="DSCRF" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
	 *                   &lt;element name="DSCRI" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
	 *                   &lt;element name="ATC" minOccurs="0">
	 *                     &lt;simpleType>
	 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
	 *                         &lt;maxLength value="8"/>
	 *                       &lt;/restriction>
	 *                     &lt;/simpleType>
	 *                   &lt;/element>
	 *                   &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="SUBSTANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                   &lt;element name="SUBSTANCEF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
	 *                 &lt;/sequence>
	 *               &lt;/restriction>
	 *             &lt;/complexContent>
	 *           &lt;/complexType>
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
	@XmlType(name = "", propOrder = { "product" })
	public static class PRODUCTS {

		@XmlElement(name = "PRODUCT")
		protected List<ARTIKELSTAMM.PRODUCTS.PRODUCT> product;

		/**
		 * Gets the value of the product property.
		 *
		 * <p>
		 * This accessor method returns a reference to the live list, not a snapshot.
		 * Therefore any modification you make to the returned list will be present
		 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
		 * for the product property.
		 *
		 * <p>
		 * For example, to add a new item, do as follows:
		 *
		 * <pre>
		 * getPRODUCT().add(newItem);
		 * </pre>
		 *
		 *
		 * <p>
		 * Objects of the following type(s) are allowed in the list
		 * {@link ARTIKELSTAMM.PRODUCTS.PRODUCT }
		 *
		 *
		 */
		public List<ARTIKELSTAMM.PRODUCTS.PRODUCT> getPRODUCT() {
			if (product == null) {
				product = new ArrayList<ARTIKELSTAMM.PRODUCTS.PRODUCT>();
			}
			return this.product;
		}

		/**
		 * <p>
		 * Java-Klasse für anonymous complex type.
		 *
		 * <p>
		 * Das folgende Schemafragment gibt den erwarteten Content an, der in dieser
		 * Klasse enthalten ist.
		 *
		 * <pre>
		 * &lt;complexType>
		 *   &lt;complexContent>
		 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
		 *       &lt;sequence>
		 *         &lt;element name="PRODNO" type="{http://elexis.ch/Elexis_Artikelstamm_v5}PRODNOType"/>
		 *         &lt;element name="SALECD" type="{http://elexis.ch/Elexis_Artikelstamm_v5}SALECDType"/>
		 *         &lt;element name="DSCR" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType"/>
		 *         &lt;element name="DSCRF" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
		 *         &lt;element name="DSCRI" type="{http://elexis.ch/Elexis_Artikelstamm_v5}DSCRType" minOccurs="0"/>
		 *         &lt;element name="ATC" minOccurs="0">
		 *           &lt;simpleType>
		 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
		 *               &lt;maxLength value="8"/>
		 *             &lt;/restriction>
		 *           &lt;/simpleType>
		 *         &lt;/element>
		 *         &lt;element name="LIMNAMEBAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="SUBSTANCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *         &lt;element name="SUBSTANCEF" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
		 *       &lt;/sequence>
		 *     &lt;/restriction>
		 *   &lt;/complexContent>
		 * &lt;/complexType>
		 * </pre>
		 *
		 *
		 */
		@XmlAccessorType(XmlAccessType.FIELD)
		@XmlType(name = "", propOrder = { "prodno", "salecd", "dscr", "dscrf", "dscri", "atc", "limnamebag",
				"substance", "substancef" })
		public static class PRODUCT {

			@XmlElement(name = "PRODNO", required = true)
			protected String prodno;
			@XmlElement(name = "SALECD", required = true)
			@XmlSchemaType(name = "string")
			protected SALECDType salecd;
			@XmlElement(name = "DSCR", required = true)
			protected String dscr;
			@XmlElement(name = "DSCRF")
			protected String dscrf;
			@XmlElement(name = "DSCRI")
			protected String dscri;
			@XmlElement(name = "ATC")
			protected String atc;
			@XmlElement(name = "LIMNAMEBAG")
			protected String limnamebag;
			@XmlElement(name = "SUBSTANCE")
			protected String substance;
			@XmlElement(name = "SUBSTANCEF")
			protected String substancef;

			/**
			 * Ruft den Wert der prodno-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getPRODNO() {
				return prodno;
			}

			/**
			 * Legt den Wert der prodno-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setPRODNO(String value) {
				this.prodno = value;
			}

			/**
			 * Ruft den Wert der salecd-Eigenschaft ab.
			 *
			 * @return possible object is {@link SALECDType }
			 *
			 */
			public SALECDType getSALECD() {
				return salecd;
			}

			/**
			 * Legt den Wert der salecd-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link SALECDType }
			 *
			 */
			public void setSALECD(SALECDType value) {
				this.salecd = value;
			}

			/**
			 * Ruft den Wert der dscr-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCR() {
				return dscr;
			}

			/**
			 * Legt den Wert der dscr-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCR(String value) {
				this.dscr = value;
			}

			/**
			 * Ruft den Wert der dscrf-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCRF() {
				return dscrf;
			}

			/**
			 * Legt den Wert der dscrf-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCRF(String value) {
				this.dscrf = value;
			}

			/**
			 * Ruft den Wert der dscri-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getDSCRI() {
				return dscri;
			}

			/**
			 * Legt den Wert der dscri-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setDSCRI(String value) {
				this.dscri = value;
			}

			/**
			 * Ruft den Wert der atc-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getATC() {
				return atc;
			}

			/**
			 * Legt den Wert der atc-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setATC(String value) {
				this.atc = value;
			}

			/**
			 * Ruft den Wert der limnamebag-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getLIMNAMEBAG() {
				return limnamebag;
			}

			/**
			 * Legt den Wert der limnamebag-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setLIMNAMEBAG(String value) {
				this.limnamebag = value;
			}

			/**
			 * Ruft den Wert der substance-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getSUBSTANCE() {
				return substance;
			}

			/**
			 * Legt den Wert der substance-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setSUBSTANCE(String value) {
				this.substance = value;
			}

			/**
			 * Ruft den Wert der substancef-Eigenschaft ab.
			 *
			 * @return possible object is {@link String }
			 *
			 */
			public String getSUBSTANCEF() {
				return substancef;
			}

			/**
			 * Legt den Wert der substancef-Eigenschaft fest.
			 *
			 * @param value allowed object is {@link String }
			 *
			 */
			public void setSUBSTANCEF(String value) {
				this.substancef = value;
			}

		}

	}

}
