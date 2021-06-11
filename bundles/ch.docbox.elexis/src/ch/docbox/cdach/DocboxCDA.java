/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *    
 *******************************************************************************/
package ch.docbox.cdach;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.hl7.v3.AD;
import org.hl7.v3.AdxpCity;
import org.hl7.v3.AdxpPostalCode;
import org.hl7.v3.AdxpStreetAddressLine;
import org.hl7.v3.CD;
import org.hl7.v3.CE;
import org.hl7.v3.CS;
import org.hl7.v3.ENXP;
import org.hl7.v3.EnFamily;
import org.hl7.v3.EnGiven;
import org.hl7.v3.EnPrefix;
import org.hl7.v3.II;
import org.hl7.v3.ON;
import org.hl7.v3.PN;
import org.hl7.v3.POCDMT000040AssignedAuthor;
import org.hl7.v3.POCDMT000040AssignedCustodian;
import org.hl7.v3.POCDMT000040Author;
import org.hl7.v3.POCDMT000040AuthoringDevice;
import org.hl7.v3.POCDMT000040ClinicalDocument;
import org.hl7.v3.POCDMT000040Component2;
import org.hl7.v3.POCDMT000040Component3;
import org.hl7.v3.POCDMT000040Custodian;
import org.hl7.v3.POCDMT000040CustodianOrganization;
import org.hl7.v3.POCDMT000040InformationRecipient;
import org.hl7.v3.POCDMT000040InfrastructureRootTypeId;
import org.hl7.v3.POCDMT000040IntendedRecipient;
import org.hl7.v3.POCDMT000040Organization;
import org.hl7.v3.POCDMT000040Patient;
import org.hl7.v3.POCDMT000040PatientRole;
import org.hl7.v3.POCDMT000040Person;
import org.hl7.v3.POCDMT000040RecordTarget;
import org.hl7.v3.POCDMT000040Section;
import org.hl7.v3.POCDMT000040StructuredBody;
import org.hl7.v3.SC;
import org.hl7.v3.ST;
import org.hl7.v3.StrucDocBr;
import org.hl7.v3.StrucDocLinkHtml;
import org.hl7.v3.StrucDocText;
import org.hl7.v3.TEL;
import org.hl7.v3.TS;
import org.hl7.v3.XInformationRecipient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.rgw.io.Settings;

/**
 * Utility to generate a CDA V1.1 compliant document according to the CDA-CH standard described at
 * http://www.hl7.ch/default.asp?tab=2&item=standard
 * 
 * The org.hl7.v3 helper classes where generated with the metro java (se69 stack (jabx bindings)
 * with the wsdl2java tool and the schema binding file CDABindings.xjb (see content of file at end
 * of class).
 * 
 * 
 * @see org.hl7.v3.ANY, derived classes of
 * @see org.hl7.v3.ADXP and
 * @see org.hl7.v3.ENXP hat to be annotated to generate an xml document
 * 
 * @author oliver egger, visionary ag
 * 
 */
public class DocboxCDA {
	
	static final private String oid_hl7 = "2.16.756.5.30.1.1.1.1";
	static final private String oid_loinc = "2.16.840.1.113883.6.1";
	static final private String oid_ean = "1.3.88";
	
	static final private String oid_docbox_old = "2.25.327919736312109525688528068157180855579";
	static final private String oid_docbox = "2.16.756.5.30.1.105";
	
	static Marshaller marshaller = null;
	static Unmarshaller unmarshaller = null;
	
	static final public String oid_ahv13 = "2.16.756.5.32";
	
	public enum DOCBOXCDATYPE {
		Eingang_Anmeldung, Datum_Aufgebot, Eintritt, Austritt, Dossier_Freigabe,
			Dossier_Freigabe_Beendet, Docbox_Spital_Anmeldung, Docbox_Spital_Arzt,
			Docbox_Arzt_Arzt, Docbox_Nachricht
	};
	
	private List<POCDMT000040Component3> listComponent = new ArrayList<POCDMT000040Component3>();
	
	public String getOidUserDocboxId(){
		return oid_docbox + ".1.1";
	}
	
	public static String getOidPraxisSoftwareId(){
		return oid_docbox + ".1.3";
	}
	
	public static String getOidPraxisSoftwareIdOld(){
		return oid_docbox_old + ".1.3";
	}
	
	public static String getOidPidHospital(){
		return oid_docbox + ".1.4.1";
	}
	
	public static String getOidFidHospital(){
		return oid_docbox + ".1.4.2";
	}
	
	public static String getOidDoctorHospitalId(){
		return oid_docbox + ".1.4.3";
	}
	
	public static String getOidOrganiaztionId(){
		return oid_docbox + ".1.2";
	}
	
	public static String getOidDepartmentId(){
		return oid_docbox + ".1.2.1";
	}
	
	public static String getOidDocboxLeistungId(){
		return oid_docbox + ".2.1";
	}
	
	public static String getOidDocboxSectionId(){
		return oid_docbox + ".2.2";
	}
	
	public void clearCdaBody(){
		listComponent.clear();
	}
	
	public POCDMT000040Author getAuthor(String prefix, String given, String family, String mobile,
		String phone, String phoneBusiness, String email, String ean, String docboxId,
		String userHospitalId){
		POCDMT000040Author author = new POCDMT000040Author();
		author.setTime(new TS());
		
		POCDMT000040AssignedAuthor assignedAuthor = new POCDMT000040AssignedAuthor();
		
		POCDMT000040Person person = new POCDMT000040Person();
		assignedAuthor.setAssignedPerson(person);
		
		ArrayList<ENXP> nameList = new ArrayList<ENXP>();
		if (prefix != null && !prefix.equals("")) {
			EnPrefix name = new EnPrefix();
			name.setContent(prefix);
			nameList.add(name);
		}
		if (given != null && !given.equals("")) {
			EnGiven name = new EnGiven();
			name.setContent(given);
			nameList.add(name);
		}
		if (family != null && !family.equals("")) {
			EnFamily name = new EnFamily();
			name.setContent(family);
			nameList.add(name);
		}
		if (nameList.size() > 0) {
			PN pn = new PN();
			pn.getContent().addAll(nameList);
			person.getName().add(pn);
		}
		
		ArrayList<TEL> telecomList = getTelecomList(mobile, phone, phoneBusiness, email);
		if (telecomList.size() > 0) {
			assignedAuthor.getTelecom().addAll(telecomList);
		}
		
		author.setAssignedAuthor(assignedAuthor);
		
		if (ean != null && !ean.equals("")) {
			author.getAssignedAuthor().getId().add(getII(ean, oid_ean));
		}
		
		if (docboxId != null && !docboxId.equals("")) {
			author.getAssignedAuthor().getId().add(getII(docboxId, getOidUserDocboxId()));
		}
		
		if (userHospitalId != null && !userHospitalId.equals("")) {
			author.getAssignedAuthor().getId().add(getII(userHospitalId, getOidDoctorHospitalId()));
		}
		
		return author;
	}
	
	public POCDMT000040Custodian getCustodian(String name, AD addr, String docboxId, String ean,
		String hospitalId, String departmentId){
		POCDMT000040Custodian custodian = new POCDMT000040Custodian();
		POCDMT000040AssignedCustodian assignedCustodian = new POCDMT000040AssignedCustodian();
		custodian.setAssignedCustodian(assignedCustodian);
		
		POCDMT000040CustodianOrganization representedCustodianOrganization =
			new POCDMT000040CustodianOrganization();
		if (name != null) {
			ON on = new ON();
			on.getContent().add(name);
			representedCustodianOrganization.setName(on);
		}
		
		assignedCustodian.setRepresentedCustodianOrganization(representedCustodianOrganization);
		
		if (addr != null) {
			custodian.getAssignedCustodian().getRepresentedCustodianOrganization().setAddr(addr);
		}
		
		if (ean != null) {
			representedCustodianOrganization.getId().add(this.getII(ean, oid_ean));
		}
		
		if (docboxId != null) {
			representedCustodianOrganization.getId().add(getII(docboxId, getOidUserDocboxId()));
		}
		
		if (hospitalId != null) {
			representedCustodianOrganization.getId().add(getII(hospitalId, getOidOrganiaztionId()));
		}
		
		if (departmentId != null) {
			representedCustodianOrganization.getId().add(getII(departmentId, getOidDepartmentId()));
		}
		
		return custodian;
	}
	
	public void addComponentToBody(String title, String text, String docboxSectionCode){
		POCDMT000040Component3 component =
			getComponent(stripNonValidXMLCharacters(title), null,
				stripNonValidXMLCharacters(docboxSectionCode));
		component.getSection().setText(getStrucDocTextWithBreaks(text));
		listComponent.add(component);
	}
	
	public void addComponentToBody(String title, Boolean value, String docboxSectionCode){
		String text = "unbekannt";
		if (value != null && value.booleanValue() == true) {
			text = "ja";
		}
		if (value != null && value.booleanValue() == false) {
			text = "nein";
		}
		listComponent.add(getComponent(title, text, docboxSectionCode));
	}
	
	public void addAttachmentsDescriptionToBody(Vector<String> attachments){
		StrucDocText strucDocText = new StrucDocText();
		for (String attachment : attachments) {
			StrucDocLinkHtml strucDocLinkHtml = new StrucDocLinkHtml();
			strucDocLinkHtml.setHref(attachment);
			strucDocLinkHtml.getContent().add(attachment);
			strucDocText.getContent().add(strucDocLinkHtml);
			strucDocText.getContent().add(new StrucDocBr());
		}
		
		POCDMT000040Component3 component = getComponent(null, null, "ATT");
		component.getSection().setText(strucDocText);
		
		listComponent.add(component);
	}
	
	private POCDMT000040Component3 getComponent(String title, String text,
		String docboxSectionCode, String docboxSectionId){
		POCDMT000040Component3 component = new POCDMT000040Component3();
		POCDMT000040Section section = new POCDMT000040Section();
		
		if (docboxSectionCode != null) {
			CD cd = new CD();
			cd.setCode(docboxSectionCode);
			cd.setCodeSystem(docboxSectionId);
			
			CE ce = new CE();
			ce.getNullFlavor().add("NA");
			ce.getTranslation().add(cd);
			section.setCode(ce);
		}
		
		if (title != null) {
			ST st = new ST();
			st.setContent(title);
			section.setTitle(st);
		}
		
		if (text != null) {
			StrucDocText strucDocText = new StrucDocText();
			strucDocText.getContent().add(text);
			section.setText(strucDocText);
		}
		
		component.setSection(section);
		
		return component;
	}
	
	private POCDMT000040Component3 getComponent(String title, String text, String docboxSectionCode){
		return this.getComponent(title, text, docboxSectionCode, getOidDocboxSectionId());
	}
	
	public AD getAddress(String streetAddrLine, String streetAddrLine2, String postalCode,
		String city, String use){
		AD addr = new AD();
		addr.getUse().add(use);
		
		if (streetAddrLine != null && !streetAddrLine.equals("")) {
			AdxpStreetAddressLine item = new AdxpStreetAddressLine();
			item.setContent(streetAddrLine);
			addr.getContent().add(item);
		}
		
		if (streetAddrLine2 != null && !streetAddrLine2.equals("")) {
			AdxpStreetAddressLine item = new AdxpStreetAddressLine();
			item.setContent(streetAddrLine2);
			addr.getContent().add(item);
		}
		
		if (postalCode != null && !postalCode.equals("")) {
			AdxpPostalCode item = new AdxpPostalCode();
			item.setContent(postalCode);
			addr.getContent().add(item);
		}
		
		if (city != null && !city.equals("")) {
			AdxpCity item = new AdxpCity();
			item.setContent(city);
			addr.getContent().add(item);
		}
		return addr;
	}
	
	public ArrayList<TEL> getTelecomList(String mobile, String phone, String phoneBusiness,
		String email){
		ArrayList<TEL> telecomList = new ArrayList<TEL>();
		if (mobile != null && !mobile.equals("")) {
			TEL tel = new TEL();
			tel.setValue("tel:" + mobile);
			telecomList.add(tel);
		}
		if (phone != null && !phone.equals("")) {
			TEL tel = new TEL();
			tel.getUse().add("HP");
			tel.setValue("tel:" + phone);
			telecomList.add(tel);
		}
		if (phoneBusiness != null && !phoneBusiness.equals("")) {
			TEL tel = new TEL();
			tel.getUse().add("WP");
			tel.setValue("tel:" + phoneBusiness);
			telecomList.add(tel);
		}
		if (email != null && !email.equals("")) {
			TEL tel = new TEL();
			tel.setValue("mailto:" + email);
			telecomList.add(tel);
		}
		return telecomList;
		
	}
	
	public POCDMT000040RecordTarget getRecordTarget(String idpatient, String ahv13,
		String streetAddrLine, String postalCode, String city, String phone, String phoneBusiness,
		String mobile, String email, String given, String family, boolean isFemale, boolean isMale,
		boolean isGenderUnknown, Date birthDate){
		
		POCDMT000040RecordTarget recordTarget = new POCDMT000040RecordTarget();
		
		POCDMT000040PatientRole patientRole = new POCDMT000040PatientRole();
		recordTarget.setPatientRole(patientRole);
		
		if (idpatient != null && !idpatient.equals("")) {
			patientRole.getId().add(getII(idpatient, getOidPraxisSoftwareId()));
		}
		
		if (ahv13 != null && !ahv13.equals("")) {
			patientRole.getId().add(getII(ahv13, oid_ahv13));
		}
		
		POCDMT000040Patient patient = new POCDMT000040Patient();
		patientRole.setPatient(patient);
		
		patientRole.getAddr().add(getAddress(streetAddrLine, null, postalCode, city, "HP"));
		
		ArrayList<TEL> telecomList = this.getTelecomList(mobile, phone, phoneBusiness, email);
		if (telecomList.size() > 0) {
			patientRole.getTelecom().addAll(telecomList);
		}
		
		ArrayList<ENXP> nameList = new ArrayList<ENXP>();
		if (given != null && !given.equals("")) {
			EnGiven name = new EnGiven();
			name.setContent(given);
			nameList.add(name);
		}
		if (family != null && !family.equals("")) {
			EnFamily name = new EnFamily();
			name.setContent(family);
			nameList.add(name);
		}
		if (nameList.size() > 0) {
			PN pn = new PN();
			pn.getContent().addAll(nameList);
			patient.getName().add(pn);
		}
		
		if (isFemale || isMale || isGenderUnknown) {
			CE ce = new CE();
			patient.setAdministrativeGenderCode(ce);
			if (isFemale) {
				ce.setCode("F");
			}
			if (isMale) {
				ce.setCode("M");
			}
			if (isGenderUnknown) {
				ce.setCode("UN");
			}
			ce.setCodeSystem("2.16.840.1.113883.5.1");
		}
		
		if (birthDate != null) {
			TS ts = new TS();
			ts.setValue(new SimpleDateFormat("yyyyMMdd").format(birthDate));
			patient.setBirthTime(ts);
		}
		
		return recordTarget;
	}
	
	static public CE getCode(String codeLoinc, String displayName){
		CE code = new CE();
		code.setCode(codeLoinc);
		code.setCodeSystem(oid_loinc);
		code.setDisplayName(displayName);
		return code;
	}
	
	public CE getCodeCommunciation(){
		CE code = new CE();
		code.setCode("47049-2");
		code.setCodeSystem(oid_loinc);
		code.setDisplayName("Communication");
		return code;
	}
	
	public CE getCodeReferral(){
		CE code = new CE();
		code.setCode("28616-1");
		code.setCodeSystem(oid_loinc);
		code.setDisplayName("Verlegungsbrief");
		return code;
	}
	
	public CE getCodeDischarge(){
		CE code = new CE();
		code.setCode("34106-5");
		code.setCodeSystem(oid_loinc);
		code.setDisplayName("Zusammenfassung bei Entlassung");
		return code;
	}
	
	private II getII(String extension, String root){
		II ii = new II();
		ii.setRoot(root);
		ii.setExtension(extension);
		return ii;
	}
	
	public II getPidHospital(String id){
		return getII(id, getOidPidHospital());
	}
	
	public II getFidHospital(String id){
		return getII(id, getOidFidHospital());
	}
	
	public POCDMT000040Organization getOrganization(String organizationName, String organizationId,
		String departmentName, String departmentId, String streetAddrLine, String postalCode,
		String city){
		POCDMT000040Organization organization = new POCDMT000040Organization();
		
		if (organizationId != null) {
			organization.getId().add(getII(organizationId, getOidOrganiaztionId()));
		}
		if (departmentId != null) {
			organization.getId().add(getII(departmentId, getOidDepartmentId()));
		}
		
		if (organizationName != null) {
			ON onOrganizationName = new ON();
			onOrganizationName.getContent().add(organizationName);
			organization.getName().add(onOrganizationName);
		}
		
		if (departmentName != null) {
			ON onDepartmentName = new ON();
			onDepartmentName.getContent().add(departmentName);
			organization.getName().add(onDepartmentName);
		}
		
		organization.getAddr().add(getAddress(streetAddrLine, null, postalCode, city, "WP"));
		
		return organization;
		
	}
	
	public POCDMT000040InformationRecipient getInformationRecipient(String prefix, String given,
		String family, String userDocboxId, POCDMT000040Organization organization){
		POCDMT000040InformationRecipient informationRecipient =
			new POCDMT000040InformationRecipient();
		informationRecipient.setTypeCode(XInformationRecipient.PRCP);
		informationRecipient.setIntendedRecipient(getIntendedRecipient(prefix, given, family,
			userDocboxId, organization));
		return informationRecipient;
	}
	
	public POCDMT000040IntendedRecipient getIntendedRecipient(String prefix, String given,
		String family, String userDocboxId, POCDMT000040Organization organization){
		POCDMT000040IntendedRecipient intendedRecipient = new POCDMT000040IntendedRecipient();
		POCDMT000040Person person = new POCDMT000040Person();
		
		if (userDocboxId != null) {
			intendedRecipient.getId().add(getII(userDocboxId, getOidUserDocboxId()));
		}
		
		ArrayList<ENXP> nameList = new ArrayList<ENXP>();
		if (prefix != null && !prefix.equals("")) {
			EnPrefix name = new EnPrefix();
			name.setContent(prefix);
			nameList.add(name);
		}
		if (given != null && !given.equals("")) {
			EnGiven name = new EnGiven();
			name.setContent(given);
			nameList.add(name);
		}
		if (family != null && !family.equals("")) {
			EnFamily name = new EnFamily();
			name.setContent(family);
			nameList.add(name);
		}
		if (nameList.size() > 0) {
			PN pn = new PN();
			pn.getContent().addAll(nameList);
			person.getName().add(pn);
		}
		
		intendedRecipient.setInformationRecipient(person);
		intendedRecipient.setReceivedOrganization(organization);
		return intendedRecipient;
	}
	
	public POCDMT000040ClinicalDocument getClinicalDocument(String title,
		POCDMT000040RecordTarget recordTarget, POCDMT000040Author author,
		POCDMT000040Custodian custodian, POCDMT000040InformationRecipient informationRecipient,
		CE code, String documentId, DOCBOXCDATYPE cdaType){
		
		// ********************************************************
		// CDA Header
		// ********************************************************
		POCDMT000040ClinicalDocument cda = new POCDMT000040ClinicalDocument();
		
		// <typeId root="2.16.840.1.113883.1.3" extension="POCDHD000040"/>
		POCDMT000040InfrastructureRootTypeId typeId = new POCDMT000040InfrastructureRootTypeId();
		typeId.setRoot("2.16.840.1.113883.1.3");
		typeId.setExtension("POCD_HD000040");
		cda.setTypeId(typeId);
		
		// old <templateId extension="CDA-R2-CH001" root="1.2.756.5.30.1.1.1.1"/>
		// <templateId extension="CDA-CH" root="2.16.756.5.30.1.1.1.1"/>
		cda.getTemplateId().add(getII("CDA-CH", oid_hl7));
		
		if (cdaType != null) {
			cda.getTemplateId().add(getII(cdaType.name(), oid_docbox));
		}
		
		// Dokumententid, Anwendungsspezifisch <id
		// extension="807563C2-5146-11D5-A672-00B0D022E945"
		// root="1.2.756.999999.1.1.1.1"/>
		if (documentId == null || "".equals(documentId)) {
			cda.setId(getII(new java.rmi.dgc.VMID().toString(), oid_hl7));
		} else {
			cda.setId(getII(documentId, oid_docbox));
		}
		
		cda.setCode(code);
		
		// <!-- docbox: Titel des Dokuments-->
		ST stTitle = new ST();
		stTitle.setContent(title);
		cda.setTitle(stTitle);
		
		// <!-- docbox: Zeitpunkt Versendung der Anmeldung yyyymmddhhmm-->
		// <effectiveTime value="200803191208"/>
		
		TS ts = new TS();
		ts.setValue(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// cda.effectiveTime.value = dateTimeNow.ToString("yyyyMMddHHmmss");
		cda.setEffectiveTime(ts);
		
		// Normal confidentiality rules (according to good health care practice)
		// apply. That is, only authorized individuals with a legitimate medical
		// or business need may access this item
		// <confidentialityCode code="N" codeSystem="2.16.840.1.113883.5.25"/>
		CE confidentialityCode = new CE();
		confidentialityCode.setCode("N");
		confidentialityCode.setCodeSystem("2.16.840.1.113883.5.25");
		cda.setConfidentialityCode(confidentialityCode);
		//
		// // <languageCode code="de-CH"/>
		CS languageCode = new CS();
		languageCode.setCode("de-CH");
		cda.setLanguageCode(languageCode);
		
		POCDMT000040Component2 component = new POCDMT000040Component2();
		cda.setComponent(component);
		
		POCDMT000040StructuredBody structureBody = new POCDMT000040StructuredBody();
		component.setStructuredBody(structureBody);
		
		structureBody.getComponent().addAll(listComponent);
		
		// add patient master data id, if available
		if (isElexisInstallationIdAvailable() || isOidMedelexisProjectAvailable()) {
			structureBody.getComponent().add(createPatientMasterDataId());
		}
		
		cda.getAuthor().add(author);
		cda.setCustodian(custodian);
		
		if (informationRecipient != null) {
			cda.getInformationRecipient().add(informationRecipient);
		}
		
		cda.getRecordTarget().add(recordTarget);
		
		return cda;
	}
	
	private POCDMT000040Component3 createPatientMasterDataId(){
		POCDMT000040Component3 component = new POCDMT000040Component3();
		POCDMT000040Section section = new POCDMT000040Section();
		
		CD cd = new CD();
		cd.setCode("SOFTWARE");
		cd.setCodeSystem("2.16.756.5.30.1.105.2.2");
		
		CE ce = new CE();
		ce.getNullFlavor().add("NA");
		ce.getTranslation().add(cd);
		section.setCode(ce);
		
		POCDMT000040Author author = new POCDMT000040Author();
		POCDMT000040AssignedAuthor assignedAuthor = new POCDMT000040AssignedAuthor();
		POCDMT000040AuthoringDevice assignedDevice = new POCDMT000040AuthoringDevice();
		
		if (isOidMedelexisProjectAvailable()) {
			II ii = new II();
			ii.setExtension(getOidMedelexisProject());
			ii.setRoot("2.16.756.5.30.1.105.4.1.1");
			assignedDevice.getTemplateId().add(ii);
		} else {
			II ii = new II();
			ii.setExtension(getElexisInstallationId());
			ii.setRoot("2.16.756.5.30.1.105.4.1.2");
			assignedDevice.getTemplateId().add(ii);
		}
		
		if (isOidMedelexisProjectAvailable()) {
			SC modelName = new SC();
			modelName.setCode("medelexis");
			modelName.setCodeSystem("2.16.756.5.30.1.105.4");
			modelName.setContent("medelexis");
			assignedDevice.setManufacturerModelName(modelName);
		} else {
			SC modelName = new SC();
			modelName.setCode("elexis");
			modelName.setCodeSystem("2.16.756.5.30.1.105.4");
			modelName.setContent("elexis");
			assignedDevice.setManufacturerModelName(modelName);
		}
		
		SC softwareName = new SC();
		softwareName.setCode("elexis");
		softwareName.setCodeSystem("2.16.756.5.30.1.105.4");
		softwareName.setContent("elexis");
		assignedDevice.setSoftwareName(softwareName);
		
		assignedAuthor.setAssignedAuthoringDevice(assignedDevice);
		author.setAssignedAuthor(assignedAuthor);
		section.getAuthor().add(author);
		component.setSection(section);
		return component;
	}
	
	private String getElexisInstallationId(){
		return ConfigServiceHolder.getGlobal(Preferences.INSTALLATION_TIMESTAMP, null);
	}
	
	private boolean isElexisInstallationIdAvailable(){
		return ConfigServiceHolder.getGlobal(Preferences.INSTALLATION_TIMESTAMP, null) != null;
	}
	
	private boolean isOidMedelexisProjectAvailable(){
		return CoreHub.localCfg.get(ch.elexis.core.constants.Preferences.SOFTWARE_OID, null) != null
			&& CoreHub.localCfg.get("medelexis/projectid", null) != null;
	}
	
	private String getOidMedelexisProject(){
		String oid = CoreHub.localCfg.get(ch.elexis.core.constants.Preferences.SOFTWARE_OID, null);
		String projectId = CoreHub.localCfg.get("medelexis/projectid", null);
		// docbox OID ends with 105
		return oid + "." + ch.elexis.core.constants.Preferences.OID_SUBDOMAIN_PATIENTMASTERDATA
			+ "." + projectId;
	}
	
	static synchronized Marshaller getCdaMarshaller(){
		if (DocboxCDA.marshaller == null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance("org.hl7.v3");
				marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
				marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:hl7-org:v3 CDA.xsd");
				// omit the xml declaration
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, new Boolean(true));
			} catch (Exception e) {
				e.printStackTrace(System.out);
				return null;
			}
		}
		return marshaller;
	}
	
	static synchronized Unmarshaller getCdaUnmarshaller(){
		if (DocboxCDA.unmarshaller == null) {
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance("org.hl7.v3");
				unmarshaller = jaxbContext.createUnmarshaller();
			} catch (Exception e) {
				e.printStackTrace(System.out);
				return null;
			}
		}
		return unmarshaller;
	}
	
	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	public String marshallIntoString(POCDMT000040ClinicalDocument cdaType){
		StringWriter writer = new StringWriter();
		try {
			Marshaller marshaller = DocboxCDA.getCdaMarshaller();
			marshaller.marshal(new JAXBElement(new QName("urn:hl7-org:v3", "ClinicalDocument"),
				POCDMT000040ClinicalDocument.class, cdaType), writer);
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return writer.toString();
	}
	
	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	public boolean marshallIntoDom(POCDMT000040ClinicalDocument cdaType, Element element){
		try {
			Marshaller marshaller = DocboxCDA.getCdaMarshaller();
			marshaller.marshal(new JAXBElement(new QName("urn:hl7-org:v3", "ClinicalDocument"),
				POCDMT000040ClinicalDocument.class, cdaType), element);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	public boolean marshallIntoDom(POCDMT000040IntendedRecipient indendedRecipient,
		Document document){
		try {
			Marshaller marshaller = DocboxCDA.getCdaMarshaller();
			marshaller.marshal(new JAXBElement(new QName("urn:hl7-org:v3",
				"POCDMT000040IntendedRecipient"), POCDMT000040IntendedRecipient.class,
				indendedRecipient), document);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	@SuppressWarnings({
		"unchecked", "rawtypes"
	})
	public boolean marshallIntoDom(POCDMT000040ClinicalDocument cdaType, Document document){
		try {
			Marshaller marshaller = DocboxCDA.getCdaMarshaller();
			marshaller.marshal(new JAXBElement(new QName("urn:hl7-org:v3", "ClinicalDocument"),
				POCDMT000040ClinicalDocument.class, cdaType), document);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}
	
	public POCDMT000040ClinicalDocument unmarshall(String path){
		try {
			Unmarshaller unmarshaller = DocboxCDA.getCdaUnmarshaller();
			JAXBElement<POCDMT000040ClinicalDocument> doc =
				unmarshaller.unmarshal(new StreamSource(new File(path)),
					POCDMT000040ClinicalDocument.class);
			return doc.getValue();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
	
	/**
	 * Administrative Angaben AuftragsID docbox - AOK
	 */
	public boolean addAuftragsnummer(String number){
		addComponentToBody("Auftragsnummer", number, "AOK");
		return true;
	}
	
	/**
	 * Administrative Angaben Versicherungsklasse docbox - AVK
	 * 
	 * @param text
	 *            [Allgemein|Halbprivat|Privat|Allgemein CH|Selbstzahler]
	 */
	public boolean addVersicherungsklasse(String text){
		if ("Allgemein".equals(text) || "Halbprivat".equals(text) || "Privat".equals(text)
			|| "Allgemein CH".equals(text) || "Selbstzahler".equals(text)) {
			addComponentToBody("Versicherungsklasse", text, "AVK");
			return true;
		}
		return false;
	}
	
	/**
	 * Administrative Angaben Krankenkasse - AKK
	 */
	public boolean addKrankenkasse(String name){
		addComponentToBody("Krankenkasse", name, "AKK");
		return true;
	}
	
	/**
	 * Administrative Angaben Policennummer Krankenkasse AKP String
	 */
	public boolean addKrankenkassePolicenummer(String name){
		addComponentToBody("Policennummer Krankenkasse", name, "AKP");
		return true;
	}
	
	/**
	 * Administrative Angaben Zusatzversicherung KK AZK
	 */
	public boolean addKrankenkasseZusatzversicherung(String name){
		addComponentToBody("Zusatzversicherung KK", name, "AKZ");
		return true;
	}
	
	/**
	 * Administrative Angaben Policennummer Zusatzversicherung KK AZP
	 */
	public boolean addKrankenkasseZusatzversicherungPolicenummer(String name){
		addComponentToBody("Policennummer Zusatzversicherung KK", name, "AZP");
		return true;
	}
	
	/**
	 * Administrative Angaben Unfallversicherung AUV String
	 */
	public boolean addUnfallversicherung(String name){
		addComponentToBody("Unfallversicherung", name, "AUV");
		return true;
	}
	
	/**
	 * Administrative Angaben Policennummer Unfallversicherung AUP String
	 */
	public boolean addUnfallversicherungPolicenummer(String name){
		addComponentToBody("Policennummer Unfallversicherung", name, "AUP");
		return true;
	}
	
	/**
	 * Administrative Angaben Zusatzversicherung Unfall AZU String
	 */
	public boolean addUnfallZusatzversicherung(String name){
		addComponentToBody("Zusatzversicherung Unfall", name, "AZU");
		return true;
	}
	
	/**
	 * Administrative Angaben Policennummer Zusatzversicherung Unfall AZPU String
	 */
	public boolean addUnfallZusatzversicherungPolicenummer(String name){
		addComponentToBody("Policennummer Zusatzversicherung Unfall", name, "AZPU");
		return true;
	}
	
	/**
	 * Administrative Angaben Arbeitgeber AAG String
	 */
	public boolean addArbeitgeber(String name){
		addComponentToBody("Arbeitgeber", name, "AAG");
		return true;
	}
	
	/**
	 * Administrative Angaben Einweisungsgrund AEG
	 * 
	 * @param text
	 *            [Krankheit|Unfall|Geburt|Mutterschaft|Anderer]
	 */
	public boolean addEinweisungsgrund(String text){
		if ("Krankheit".equals(text) || "Unfall".equals(text) || "Geburt".equals(text)
			|| "Mutterschaft".equals(text) || "Anderer".equals(text)) {
			addComponentToBody("Einweisungsgrund", text, "AEG");
			return true;
		}
		return false;
	}
	
	/**
	 * Administrative Angaben Aufenthaltsart AAA
	 * 
	 * @param text
	 *            [ambulant|stationär|kurzstationär]
	 */
	public boolean addAufenthaltsart(String text){
		if ("ambulant".equals(text) || "stationär".equals(text) || "kurzstationär".equals(text)) {
			addComponentToBody("Aufenthaltsart", text, "AAA");
			return true;
		}
		return false;
	}
	
	/**
	 * Administrative Angaben Eintritt Datum AED String
	 */
	public boolean addEintrittDatum(String name){
		addComponentToBody("Eintritt Datum", name, "AED");
		return true;
	}
	
	/**
	 * Administrative Angaben Eintritt Zeit AEZ String
	 */
	public boolean addEintrittZeit(String name){
		addComponentToBody("Eintritt Zeit", name, "AEZ");
		return true;
	}
	
	/**
	 * Administrative Angaben Eintritt Vortag [ja|nein|unbekannt]
	 */
	public boolean addEintrittVortag(Boolean value){
		addComponentToBody("Eintritt Vortag", value, "AEV");
		return true;
	}
	
	/**
	 * Administrative Angaben Eintritt nüchtern [ja|nein|unbekannt]
	 */
	public boolean addEintrittNuechtern(Boolean value){
		addComponentToBody(null, value, "AEN");
		return true;
	}
	
	/**
	 * Administrative Angaben Operation Datum AOPD String
	 */
	public boolean addOperationDatum(String name){
		addComponentToBody("Operation Datum", name, "AOPD");
		return true;
	}
	
	/**
	 * Administrative Angaben Operation Zeit AOPZ String
	 */
	public boolean addOperationZeit(String name){
		addComponentToBody("Operation Zeit", name, "AOPZ");
		return true;
	}
	
	/**
	 * Administrative Angaben Fix-Zeit Operation AOPFZ String
	 */
	public boolean addOperationFixZeit(String name){
		addComponentToBody("Fix-Zeit Operation", name, "AOPFZ");
		return true;
	}
	
	/**
	 * Administrative Angaben Dringlichkeit ADRINDGLICHKEIT Selekion aus Liste: Verfügbare
	 * Dringlichkeiten abhängig von Leistung
	 */
	public boolean addDringlichkeit(String name){
		addComponentToBody("Dringlichkeit", name, "ADRINDGLICHKEIT");
		return true;
	}
	
	/**
	 * Administrative Angaben Frühere Aufenthalte im Spital bei der die Anmeldung gemacht wird
	 * (Klinik/Datum): String
	 */
	public boolean addFruehereAufenthalte(String name){
		addComponentToBody(null, name, "AFAS");
		return true;
	}
	
	/**
	 * Administrative Angaben Name Hausarzt ANH String
	 */
	public boolean addHauszarzt(String name){
		addComponentToBody("Name Hauszart", name, "ANH");
		return true;
	}
	
	/**
	 * Administrative Angaben Patient muss Präoperativ zum Hausarzt APH [ja|nein|unbekannt]
	 */
	public boolean addPraeoperativHausarzt(Boolean value){
		addComponentToBody(null, value, "APH");
		return true;
	}
	
	/**
	 * Administrative Angaben Bemerkungen/Beilagen ABB Text String
	 */
	public boolean addBemerkungenBeilagen(String name){
		addComponentToBody("Bemerkungen/Beilagen", name, "ABB");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Hinzufügen einer gewünschten Leistungen
	 */
	public boolean addGewuenschteLeistung(String nameLeistung, String codeLeistung){
		listComponent.add(this.getComponent(null, nameLeistung, codeLeistung,
			getOidDocboxLeistungId()));
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Ergänzungen zur Leistung LE Text
	 */
	public boolean addErgaenzungenLeistung(String name){
		addComponentToBody(null, name, "LE");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Ergänzungen zur Leistung LE Text
	 */
	public boolean addKlinischeAngabeFragestellung(String name){
		addComponentToBody("Klinische Angaben/Fragestellung", name, "LKA");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Diagnose LD String
	 */
	public boolean addDiagnose(String name){
		addComponentToBody("Diagnose", name, "LD");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Diagnose Verlauf LDV String
	 */
	public boolean addDiagnoseVerlauf(String name){
		addComponentToBody("Diagnose Verlauf", name, "LDV");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Anamnese LA String
	 */
	public boolean addAnamnese(String name){
		addComponentToBody("Anamnese", name, "LA");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Persönliche Anamnese LAP String
	 */
	public boolean addPerseoenlicheAnamnese(String name){
		addComponentToBody(null, name, "LAP");
		return true;
	}
	
	/**
	 * Leistung/Fragestellung Gewünschte Körperregion LK [Links:][Rechts:]String
	 */
	public boolean addGewuenschteKoerperregion(String name, boolean right, boolean left){
		if (name == null) {
			return false;
		}
		String text = "";
		if (left) {
			text += "Links:";
		}
		if (right) {
			text += "Rechts:";
		}
		text += name;
		addComponentToBody(null, text, "LK");
		return true;
	}
	
	/**
	 * This method ensures that the output String has only valid XML unicode characters as specified
	 * by the XML 1.0 standard. For reference, please see <a
	 * href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>. This method will
	 * return an empty String if the input is null or empty.
	 * 
	 * @param in
	 *            The String whose non-valid characters we want to remove.
	 * @return The in String, stripped of non-valid characters.
	 */
	public String stripNonValidXMLCharacters(String in){
		StringBuffer out = new StringBuffer(); // Used to hold the output.
		char current; // Used to reference the current character.
		
		if (in == null || ("".equals(in)))
			return ""; // vacancy test.
		for (int i = 0; i < in.length(); i++) {
			current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not
									// happen.
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
				|| ((current >= 0x20) && (current <= 0xD7FF))
				|| ((current >= 0xE000) && (current <= 0xFFFD))
				|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}
	
	private StrucDocText getStrucDocTextWithBreaks(String[] text){
		StrucDocText strucDocText = new StrucDocText();
		if (text != null) {
			for (int i = 0; i < text.length; ++i) {
				String textAdd = stripNonValidXMLCharacters(text[i]);
				strucDocText.getContent().add(textAdd);
				if (i != text.length - 1) {
					strucDocText.getContent().add(new StrucDocBr());
				}
			}
		}
		return strucDocText;
	}
	
	private StrucDocText getStrucDocTextWithBreaks(String text){
		String texts[] = text.split("[\\n\\r]+");
		return getStrucDocTextWithBreaks(texts);
	}
	
	/**
	 * Klinische Angaben Medikamente KMED Text mit <br/>
	 * Elementen getrennt für Einzeleinträge von Medikamenten
	 * 
	 * @return String
	 */
	public boolean addMedikamente(String[] medikamente){
		POCDMT000040Component3 component = getComponent("Medikamente", null, "KMED");
		component.getSection().setText(getStrucDocTextWithBreaks(medikamente));
		listComponent.add(component);
		return true;
	}
	
	/**
	 * Klinische Angaben Schwangerschaft/Stillzeit KSCH [ja:|nein:|unbekannt:]Bemerkung
	 */
	public boolean addSchwangerschaft(Boolean schwanger, String bemerkung){
		String first = "";
		if (schwanger != null && schwanger.booleanValue() == true) {
			first = "ja:";
		} else if (schwanger != null && schwanger.booleanValue() == false) {
			first = "nein:";
		} else {
			first = "unbekannt:";
		}
		String text = first + (bemerkung != null ? bemerkung : "");
		addComponentToBody("Schwangerschaft/Stillzeit", text, "KSCH");
		return true;
	}
	
	/**
	 * Klinische Angaben Allergien KALL [ja:|nein:|unbekannt:]Text mit <cda:br/> (oder in alter
	 * Version br Elementen) getrennt für Einzeleinträge von Allergien
	 */
	public boolean addAllergien(Boolean allergie, String allergieListe){
		String first = "";
		if (allergie != null && allergie.booleanValue() == true) {
			first = "ja:";
		} else if (allergie != null && allergie.booleanValue() == false) {
			first = "nein:";
		} else {
			first = "unbekannt:";
		}
		if (allergieListe == null) {
			allergieListe = "";
		}
		this.addComponentToBody("Allergien", first + allergieListe, "KALL");
		return true;
	}
	
	/**
	 * Klinische Angaben Kreatininwert KKRET String
	 */
	public boolean addKreatininwert(String value){
		addComponentToBody("Kreatininwert", value, "KKRET");
		return true;
	}
	
	/**
	 * Klinische Angaben INR KINR String
	 */
	public boolean addInr(String value){
		addComponentToBody("INR", value, "KINR");
		return true;
	}
	
	/**
	 * Klinische Angaben INR KINR String
	 */
	public boolean addThrombozyten(String value){
		addComponentToBody("Thrombozyten", value, "KTHROMBO");
		return true;
	}
	
	/**
	 * Klinische Angaben Schilddrüsen-Überfunktion KSCHILD [ja|nein|unbekannt]
	 */
	public boolean addSchilddruesenUeberfunktion(Boolean value){
		addComponentToBody(null, value, "KSCHILD");
		return true;
	}
	
	/**
	 * Klinische Angaben Biguanid Medikation KBIUGANID [ja|nein|unbekannt]
	 */
	public boolean addBiguanidMedikation(Boolean value){
		addComponentToBody("Biguanid Medikation", value, "KBIUGANID");
		return true;
	}
	
	/**
	 * Klinische Angaben Herzschrittmacher KHERZSCHRITT [ja|nein|unbekannt]
	 */
	public boolean addHerzschrittmacher(Boolean value){
		addComponentToBody("Herzschrittmacher", value, "KHERZSCHRITT");
		return true;
	}
	
	/**
	 * Klinische Angaben Metallimplantate KMETALL [ja|nein|unbekannt]
	 */
	public boolean addMetallimplantate(Boolean value){
		addComponentToBody("Metallimplantate", value, "KMETALL");
		return true;
	}
	
	/**
	 * Klinische Angaben Clips/Metallsplitter KCLIPS [ja|nein|unbekannt]
	 */
	public boolean addClipsMetallsplitter(Boolean value){
		addComponentToBody("Clips/Metallsplitter", value, "KCLIPS");
		return true;
	}
	
	/**
	 * Klinische Angaben Gehörimplantat KGEHÖRIMPL [ja|nein|unbekannt]
	 */
	public boolean addGehoerimplantat(Boolean value){
		addComponentToBody(null, value, "KGEHÖRIMPL");
		return true;
	}
	
	/**
	 * Klinische Angaben Platzangst KPLATZANGST [ja|nein|unbekannt]
	 */
	public boolean addPlatzangst(Boolean value){
		addComponentToBody("Platzangst", value, "KPLATZANGST");
		return true;
	}
	
	/**
	 * Klinische Angaben Kind zur Sedation KKINDSEDATION [ja|nein|unbekannt]
	 */
	public boolean addKindZurSedation(Boolean value){
		addComponentToBody("Kind zur Sedation", value, "KKINDSEDATION");
		return true;
	}
	
	/**
	 * Klinische Angaben OP-Dauer (Std/Min) KOPD String
	 */
	public boolean addOpDauer(String duration){
		addComponentToBody("OP-Dauer (Std/Min)", duration, "KOPD");
		return true;
	}
	
	/**
	 * Klinische Angaben Aufenthaltsdauer (in Tagen) KOPAUF String
	 */
	public boolean addAufenthaltsdauer(String days){
		addComponentToBody("Aufenthaltsdauer (in Tagen)", days, "KOPAUF");
		return true;
	}
	
	/**
	 * Klinische Angaben Operateur KOPERATEUR String
	 */
	public boolean addOperateur(String duration){
		addComponentToBody("Operateur", duration, "KOPERATEUR");
		return true;
	}
	
	/**
	 * Klinische Angaben OP-Assistent KOPASSISTANT String
	 */
	public boolean addOpAssistent(String value){
		addComponentToBody("OP-Assistent", value, "KOPASSISTANT");
		return true;
	}
	
	/**
	 * Klinische Angaben Instrumente KINSTRUMENT Selekion aus Liste: Verfügbare Instrumente abhängig
	 * von Leistung
	 * 
	 * @return String
	 */
	public boolean addInstrumente(String[] liste){
		POCDMT000040Component3 component = getComponent("Instrumente", null, "KINSTRUMENT");
		component.getSection().setText(getStrucDocTextWithBreaks(liste));
		listComponent.add(component);
		return true;
	}
	
	/**
	 * Klinische Angaben Lagerung KLAGERUNG Selekion aus Liste: Verfügbare Lagerungen abhängig von
	 * Leistung
	 * 
	 * @return String
	 */
	public boolean addLagerung(String[] liste){
		POCDMT000040Component3 component = getComponent("Lagerung", null, "KLAGERUNG");
		component.getSection().setText(getStrucDocTextWithBreaks(liste));
		listComponent.add(component);
		return true;
	}
	
	/**
	 * Klinische Angaben Anästhesie KANÄSTHESIE Selekion aus Liste: Verfügbare Anästhesie abhängig
	 * von Leistung
	 * 
	 * @return String
	 */
	public boolean addAnaesthesie(String[] liste){
		POCDMT000040Component3 component = getComponent(null, null, "KANÄSTHESIE");
		component.getSection().setText(this.getStrucDocTextWithBreaks(liste));
		listComponent.add(component);
		return true;
	}
	
	/**
	 * Klinische Angaben Bitte zur Anästhesieprechstunde aufbieten KANÄSTHESIEAUFBIETEN
	 * [ja|nein|unbekannt]
	 */
	public boolean addAnaesthesieSprechstunde(Boolean value){
		addComponentToBody(null, value, "KANÄSTHESIEAUFBIETEN");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation DocumentID DOCID String
	 */
	public boolean addDocumentIdInSection(String value){
		addComponentToBody("DocumentID", value, "DOCID");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation Datum Aufgebot MDATUMAUFGEBOT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public boolean addDatumAufgebot(String value){
		addComponentToBody("Datum Aufgebot", value, "MDATUMAUFGEBOT");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation Änderung Datum Aufgebot MDATUMAUFGEBOTÄNDERUNG [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public boolean addDatumAufgebotAenderung(String value){
		addComponentToBody(null, value, "MDATUMAUFGEBOTÄNDERUNG");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation Datum Eintritt MDATUMEINTRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public boolean addDatumEintritt(String value){
		addComponentToBody("Datum Eintritt", value, "MDATUMEINTRITT");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation Datum Storno Eintritt MDATUMSTORNOEINTRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public boolean addDatumStornoEintritt(String value){
		addComponentToBody("Datum Storno Eintritt", value, "MDATUMSTORNOEINTRITT");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation Datum Austritt MDATUMAUSRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public boolean addDatumAustritt(String value){
		addComponentToBody("Austritt", value, "MDATUMAUSRITT");
		return true;
	}
	
	/**
	 * docbox Spitalkommunikation Datum Storno Austritt MDATUMSTORNOAUSTRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public boolean addDatumStornoAustritt(String value){
		addComponentToBody("Storno Austritt", value, "MDATUMSTORNOAUSTRITT");
		return true;
	}
	
}

/*
 * content of CDASchemabinding.xjb: <jxb:bindings version="1.0"
 * xmlns:jxb="http://java.sun.com/xml/ns/jaxb" xmlns:xs="http://www.w3.org/2001/XMLSchema"
 * xmlns:xjc="http://java.sun.com/xml/ns/jaxb/xjc" jxb:extensionBindingPrefixes="xjc"> <jxb:bindings
 * schemaLocation="CDA.xsd" node="/xs:schema"> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Text']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Title']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Caption']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Col']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Colgroup']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Content']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.TitleContent']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Footnote']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.TitleFootnote']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.FootnoteRef']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Item']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.LinkHtml']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.List']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Paragraph']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.RenderMultiMedia']/xs:attribute[@name='ID']">
 * <jxb:property name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Table']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Tbody']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Td']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Tfoot']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Th']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Thead']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='StrucDoc.Tr']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='POCD_MT000040.ObservationMedia']/xs:attribute[@name='ID']">
 * <jxb:property name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='POCD_MT000040.RegionOfInterest']/xs:attribute[@name='ID']">
 * <jxb:property name="attributeId"/> </jxb:bindings> <jxb:bindings
 * node="//xs:complexType[@name='POCD_MT000040.Section']/xs:attribute[@name='ID']"> <jxb:property
 * name="attributeId"/> </jxb:bindings> </jxb:bindings> </jxb:bindings>
 */