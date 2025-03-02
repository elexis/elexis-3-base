package org.hl7.v3;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for POCD_MT000040.Patient complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="POCD_MT000040.Patient">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="realmCode" type="{urn:hl7-org:v3}CS" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="typeId" type="{urn:hl7-org:v3}POCD_MT000040.InfrastructureRoot.typeId" minOccurs="0"/>
 *         &lt;element name="templateId" type="{urn:hl7-org:v3}II" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="id" type="{urn:hl7-org:v3}II" minOccurs="0"/>
 *         &lt;element name="name" type="{urn:hl7-org:v3}PN" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="administrativeGenderCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="birthTime" type="{urn:hl7-org:v3}TS" minOccurs="0"/>
 *         &lt;element name="maritalStatusCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="religiousAffiliationCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="raceCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="ethnicGroupCode" type="{urn:hl7-org:v3}CE" minOccurs="0"/>
 *         &lt;element name="guardian" type="{urn:hl7-org:v3}POCD_MT000040.Guardian" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="birthplace" type="{urn:hl7-org:v3}POCD_MT000040.Birthplace" minOccurs="0"/>
 *         &lt;element name="languageCommunication" type="{urn:hl7-org:v3}POCD_MT000040.LanguageCommunication" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="nullFlavor" type="{urn:hl7-org:v3}NullFlavor" />
 *       &lt;attribute name="classCode" type="{urn:hl7-org:v3}EntityClass" fixed="PSN" />
 *       &lt;attribute name="determinerCode" type="{urn:hl7-org:v3}EntityDeterminer" fixed="INSTANCE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "POCD_MT000040.Patient", propOrder = { "realmCode", "typeId", "templateId", "id", "name",
		"administrativeGenderCode", "birthTime", "maritalStatusCode", "religiousAffiliationCode", "raceCode",
		"ethnicGroupCode", "guardian", "birthplace", "languageCommunication" })
public class POCDMT000040Patient {

	protected List<CS> realmCode;
	protected POCDMT000040InfrastructureRootTypeId typeId;
	protected List<II> templateId;
	protected II id;
	protected List<PN> name;
	protected CE administrativeGenderCode;
	protected TS birthTime;
	protected CE maritalStatusCode;
	protected CE religiousAffiliationCode;
	protected CE raceCode;
	protected CE ethnicGroupCode;
	protected List<POCDMT000040Guardian> guardian;
	protected POCDMT000040Birthplace birthplace;
	protected List<POCDMT000040LanguageCommunication> languageCommunication;
	@XmlAttribute
	protected List<String> nullFlavor;
	@XmlAttribute
	protected List<String> classCode;
	@XmlAttribute
	protected String determinerCode;

	/**
	 * Gets the value of the realmCode property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the realmCode property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getRealmCode().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link CS }
	 *
	 *
	 */
	public List<CS> getRealmCode() {
		if (realmCode == null) {
			realmCode = new ArrayList<CS>();
		}
		return this.realmCode;
	}

	/**
	 * Gets the value of the typeId property.
	 *
	 * @return possible object is {@link POCDMT000040InfrastructureRootTypeId }
	 *
	 */
	public POCDMT000040InfrastructureRootTypeId getTypeId() {
		return typeId;
	}

	/**
	 * Sets the value of the typeId property.
	 *
	 * @param value allowed object is {@link POCDMT000040InfrastructureRootTypeId }
	 *
	 */
	public void setTypeId(POCDMT000040InfrastructureRootTypeId value) {
		this.typeId = value;
	}

	/**
	 * Gets the value of the templateId property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the templateId property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getTemplateId().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link II }
	 *
	 *
	 */
	public List<II> getTemplateId() {
		if (templateId == null) {
			templateId = new ArrayList<II>();
		}
		return this.templateId;
	}

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is {@link II }
	 *
	 */
	public II getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value allowed object is {@link II }
	 *
	 */
	public void setId(II value) {
		this.id = value;
	}

	/**
	 * Gets the value of the name property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the name property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getName().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link PN }
	 *
	 *
	 */
	public List<PN> getName() {
		if (name == null) {
			name = new ArrayList<PN>();
		}
		return this.name;
	}

	/**
	 * Gets the value of the administrativeGenderCode property.
	 *
	 * @return possible object is {@link CE }
	 *
	 */
	public CE getAdministrativeGenderCode() {
		return administrativeGenderCode;
	}

	/**
	 * Sets the value of the administrativeGenderCode property.
	 *
	 * @param value allowed object is {@link CE }
	 *
	 */
	public void setAdministrativeGenderCode(CE value) {
		this.administrativeGenderCode = value;
	}

	/**
	 * Gets the value of the birthTime property.
	 *
	 * @return possible object is {@link TS }
	 *
	 */
	public TS getBirthTime() {
		return birthTime;
	}

	/**
	 * Sets the value of the birthTime property.
	 *
	 * @param value allowed object is {@link TS }
	 *
	 */
	public void setBirthTime(TS value) {
		this.birthTime = value;
	}

	/**
	 * Gets the value of the maritalStatusCode property.
	 *
	 * @return possible object is {@link CE }
	 *
	 */
	public CE getMaritalStatusCode() {
		return maritalStatusCode;
	}

	/**
	 * Sets the value of the maritalStatusCode property.
	 *
	 * @param value allowed object is {@link CE }
	 *
	 */
	public void setMaritalStatusCode(CE value) {
		this.maritalStatusCode = value;
	}

	/**
	 * Gets the value of the religiousAffiliationCode property.
	 *
	 * @return possible object is {@link CE }
	 *
	 */
	public CE getReligiousAffiliationCode() {
		return religiousAffiliationCode;
	}

	/**
	 * Sets the value of the religiousAffiliationCode property.
	 *
	 * @param value allowed object is {@link CE }
	 *
	 */
	public void setReligiousAffiliationCode(CE value) {
		this.religiousAffiliationCode = value;
	}

	/**
	 * Gets the value of the raceCode property.
	 *
	 * @return possible object is {@link CE }
	 *
	 */
	public CE getRaceCode() {
		return raceCode;
	}

	/**
	 * Sets the value of the raceCode property.
	 *
	 * @param value allowed object is {@link CE }
	 *
	 */
	public void setRaceCode(CE value) {
		this.raceCode = value;
	}

	/**
	 * Gets the value of the ethnicGroupCode property.
	 *
	 * @return possible object is {@link CE }
	 *
	 */
	public CE getEthnicGroupCode() {
		return ethnicGroupCode;
	}

	/**
	 * Sets the value of the ethnicGroupCode property.
	 *
	 * @param value allowed object is {@link CE }
	 *
	 */
	public void setEthnicGroupCode(CE value) {
		this.ethnicGroupCode = value;
	}

	/**
	 * Gets the value of the guardian property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the guardian property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getGuardian().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link POCDMT000040Guardian }
	 *
	 *
	 */
	public List<POCDMT000040Guardian> getGuardian() {
		if (guardian == null) {
			guardian = new ArrayList<POCDMT000040Guardian>();
		}
		return this.guardian;
	}

	/**
	 * Gets the value of the birthplace property.
	 *
	 * @return possible object is {@link POCDMT000040Birthplace }
	 *
	 */
	public POCDMT000040Birthplace getBirthplace() {
		return birthplace;
	}

	/**
	 * Sets the value of the birthplace property.
	 *
	 * @param value allowed object is {@link POCDMT000040Birthplace }
	 *
	 */
	public void setBirthplace(POCDMT000040Birthplace value) {
		this.birthplace = value;
	}

	/**
	 * Gets the value of the languageCommunication property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the languageCommunication property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getLanguageCommunication().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link POCDMT000040LanguageCommunication }
	 *
	 *
	 */
	public List<POCDMT000040LanguageCommunication> getLanguageCommunication() {
		if (languageCommunication == null) {
			languageCommunication = new ArrayList<POCDMT000040LanguageCommunication>();
		}
		return this.languageCommunication;
	}

	/**
	 * Gets the value of the nullFlavor property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the nullFlavor property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getNullFlavor().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 *
	 *
	 */
	public List<String> getNullFlavor() {
		if (nullFlavor == null) {
			nullFlavor = new ArrayList<String>();
		}
		return this.nullFlavor;
	}

	/**
	 * Gets the value of the classCode property.
	 *
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot.
	 * Therefore any modification you make to the returned list will be present
	 * inside the JAXB object. This is why there is not a <CODE>set</CODE> method
	 * for the classCode property.
	 *
	 * <p>
	 * For example, to add a new item, do as follows:
	 *
	 * <pre>
	 * getClassCode().add(newItem);
	 * </pre>
	 *
	 *
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 *
	 *
	 */
	public List<String> getClassCode() {
		if (classCode == null) {
			classCode = new ArrayList<String>();
		}
		return this.classCode;
	}

	/**
	 * Gets the value of the determinerCode property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getDeterminerCode() {
		if (determinerCode == null) {
			return "INSTANCE";
		} else {
			return determinerCode;
		}
	}

	/**
	 * Sets the value of the determinerCode property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setDeterminerCode(String value) {
		this.determinerCode = value;
	}

}
