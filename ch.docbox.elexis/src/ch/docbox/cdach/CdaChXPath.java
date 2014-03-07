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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CdaChXPath {
	
	static final public String oid_loinc = "2.16.840.1.113883.6.1";
	static final public String oid_ean = "1.3.88";
	static final private String oid_docbox_old = "2.25.327919736312109525688528068157180855579";
	static final private String oid_docbox = "2.16.756.5.30.1.105";
	static final public String oid_ahv13 = "2.16.756.5.32";
	
	public enum DOCBOXCDATYPE {
		Eingang_Anmeldung, Datum_Aufgebot, Eintritt, Austritt, Dossier_Freigabe,
			Dossier_Freigabe_Beendet, Docbox_Spital_Anmeldung, Docbox_Spital_Arzt,
			Docbox_Arzt_Arzt, Docbox_Nachricht, Docbox_Terminvereinbarung
	};
	
	public enum Options {
		JA, NEIN, UNBEKANNT
	};
	
	public static String getOidUserDocboxId(){
		return oid_docbox + ".1.1";
	}
	
	public static String getOidPraxisSoftwareId(){
		return oid_docbox + ".1.3";
	}
	
	private String getOidOrganizationId(){
		return oid_docbox + ".1.2";
	}
	
	private String getOidDepartmentId(){
		return oid_docbox + ".1.2.1";
	}
	
	private String getOidDocboxLeistungId(){
		return oid_docbox + ".2.1";
	}
	
	public static String getOidDocboxSectionId(){
		return oid_docbox + ".2.2";
	}
	
	public static String getOidUserDocboxIdOld(){
		return oid_docbox_old + ".1.1";
	}
	
	private String getOidDepartmentIdOld(){
		return oid_docbox_old + ".1.2.1";
	}
	
	private String getOidOrganizationIdOld(){
		return oid_docbox_old + ".1.2";
	}
	
	private String getOidDocboxLeistungIdOld(){
		return oid_docbox_old + ".2.1";
	}
	
	private String getOidDocboxSectionIdOld(){
		return oid_docbox_old + ".2.2";
	}
	
	private static XPathFactory factory;
	private static XPath xpath;
	
	private Document doc;
	private String xml;
	
	static private Map<String, XPathExpression> expressionCache =
		new HashMap<String, XPathExpression>();
	
	public CdaChXPath(){
		if (factory == null) {
			factory = XPathFactory.newInstance();
			xpath = factory.newXPath();
			xpath.setNamespaceContext(new CdaNamespaceContext());
		}
	}
	
	public Options getOptions(String value){
		if (value != null) {
			if ("ja".equals(value)) {
				return Options.JA;
			}
			if ("nein".equals(value)) {
				return Options.NEIN;
			}
			if ("unbekannt".equals(value)) {
				return Options.UNBEKANNT;
			}
		}
		return null;
	}
	
	public Options getFieldValueOptions(String expression){
		return getOptions(getFieldValue(expression));
	}
	
	public String getFieldValue(String expression){
		return getFieldValue(expression, false);
	}
	
	public String getFieldValue(final String expression, final boolean convertCdaBrToBr){
		Object result = null;
		try {
			XPathExpression xpathCheckTypeId = expressionCache.get(expression);
			if (xpathCheckTypeId == null) {
				xpathCheckTypeId = xpath.compile(expression);
				try {
					expressionCache.put(expression, xpathCheckTypeId);
				} catch (final Exception e) {}
			}
			result = xpathCheckTypeId.evaluate(doc, XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			System.out.println(e.getMessage());
			return null;
		}
		if (result == null) {
			return null;
		} else {
			final NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				String text = "";
				Node node = nodes.item(0).getFirstChild();
				do {
					if (node != null) {
						if (node.getNodeType() == Node.TEXT_NODE) {
							text += node.getNodeValue().trim();
						}
						if ((node.getNodeType() == Node.ELEMENT_NODE)
							&& node.getLocalName().equals("br") && convertCdaBrToBr) {
							text += "<br />";
						}
						node = node.getNextSibling();
					}
				} while (node != null);
				return text;
			}
			return null;
		}
	}
	
	public String[] getFieldValues(final String expression){
		Object result = null;
		try {
			XPathExpression xpathCheckTypeId = expressionCache.get(expression);
			if (xpathCheckTypeId == null) {
				xpathCheckTypeId = xpath.compile(expression);
				expressionCache.put(expression, xpathCheckTypeId);
			}
			result = xpathCheckTypeId.evaluate(doc, XPathConstants.NODESET);
		} catch (final XPathExpressionException e) {
			System.out.println(e.getMessage());
			return null;
		}
		if (result == null) {
			return null;
		} else {
			final NodeList nodes = (NodeList) result;
			if (nodes.getLength() > 0) {
				final String results[] = new String[nodes.getLength()];
				for (int i = 0; i < nodes.getLength(); ++i) {
					final Node node = nodes.item(i);
					results[i] = node.getFirstChild().getNodeValue();
				}
				return results;
			}
			return null;
		}
	}
	
	public String setPatientDocument(String document){
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new ByteArrayInputStream(document.getBytes("UTF-8")));
		} catch (SAXException e) {
			System.out.println(e.getMessage());
			return e.toString();
		} catch (ParserConfigurationException e) {
			System.out.println(e.getMessage());
			return e.toString();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return e.toString();
		}
		xml = document;
		return null;
	}
	
	public String getTitle(){
		String str = "//cda:ClinicalDocument/cda:title";
		return (getFieldValue(str));
	}
	
	public String getPatientHospitalPid(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:id[@root = '"
				+ DocboxCDA.getOidPidHospital() + "']/@extension";
		return (getFieldValue(str));
	}
	
	public String getPatientHospitalFid(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:id[@root = '"
				+ DocboxCDA.getOidFidHospital() + "']/@extension";
		return (getFieldValue(str));
	}
	
	public String getCodeCode(){
		String str = "//cda:ClinicalDocument/cda:code/@code";
		// If Null, get code attribute of translation subelement.
		if (getFieldValue(str) == null)
			str = "//cda:ClinicalDocument/cda:code/cda:translation/@code";
		return (getFieldValue(str));
	}
	
	public String getCodeCodeSystem(){
		String str = "//cda:ClinicalDocument/cda:code/@codeSystem";
		// If Null, get code attribute of translation subelement.
		if (getFieldValue(str) == null)
			str = "//cda:ClinicalDocument/cda:code/cda:translation/@codeSystem";
		return (getFieldValue(str));
	}
	
	public String getCodeDisplayName(){
		String str = "//cda:ClinicalDocument/cda:code/@displayName";
		return (getFieldValue(str));
	}
	
	public String getDossierId(){
		String str =
			"//cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section[cda:code[@nullFlavor=\"NA\"]/cda:translation[@code=\"DOSSIERID\" and (@codeSystem=\""
				+ getOidDocboxSectionIdOld()
				+ "\" or @codeSystem=\""
				+ getOidDocboxSectionId()
				+ "\")]]/cda:text";
		return (getFieldValue(str));
	}
	
	public String getDossierUrl(){
		String str =
			"//cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section[cda:code[@nullFlavor=\"NA\"]/cda:translation[@code=\"DOSSIERURL\" and (@codeSystem=\""
				+ getOidDocboxSectionIdOld()
				+ "\" or @codeSystem=\""
				+ getOidDocboxSectionId()
				+ "\")]]/cda:text/cda:linkHtml";
		return (getFieldValue(str));
	}
	
	private String getSubStringBefore(String str, String niddle){
		if ((str == null) || (str.trim().length() == 0)) {
			return "";
		}
		if (str.lastIndexOf(niddle) > -1) {
			String result = str.substring(0, str.lastIndexOf(niddle));
			
			return result;
		}
		if ((str.equals("ja")) || (str.equals("nein")) || (str.equals("unbekannt"))) {
			return str;
		}
		return "";
	}
	
	private String getSubStringAfter(String str, String niddle){
		if ((str == null) || (str.trim().length() == 0)) {
			return "";
		}
		if (str.lastIndexOf(niddle) > -1) {
			String result = str.substring(str.lastIndexOf(niddle) + 1);
			return result;
		}
		if ((str.equals("ja")) || (str.equals("nein")) || (str.equals("unbekannt"))) {
			return "";
		}
		return str;
	}
	
	private String formatString(String str){
		
		if (str == null || "".equals(str)) {
			return "";
		}
		StringBuffer str1 = new StringBuffer();
		String[] stringArray = explodeString(str);
		int len = stringArray.length;
		
		if (len < 2) {
			return str;
		}
		
		for (int i = 0; i < len; i++) {
			str1.append(stringArray[i]);
			if (i != (len - 1)) {
				str1.append("\n");
			}
			
		}
		
		return str1.toString();
		
	}
	
	private String[] explodeString(String str){
		
		str = str.replaceAll("\\n|\\r", "");
		
		String exp1 = "(&lt;|<)(?i)br[\\s]*/?(&gt;|>)";
		String exp2 = "^" + exp1;
		
		while (str.split(exp1)[0].length() == 0 && !"".equals(str)) {
			str = str.replaceAll(exp2, "");
		}
		
		return str.split(exp1);
		
	}
	
	public String getDocumentIdDocbox(){
		return getFieldValue("//cda:ClinicalDocument/cda:id[@root='" + oid_docbox + "'or @root='"
			+ oid_docbox_old + "']/@extension");
	}
	
	public boolean isDocboxReferral(){
		return "28616-1".equals(getCodeCode()) && getDocumentIdDocbox() != null;
	}
	
	public String getReferralHospitalName(){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[@typeCode='PRCP' or string-length(normalize-space(@typeCode)) = 0]/cda:intendedRecipient/cda:receivedOrganization/cda:name[1]");
	}
	
	public String getReferralHospitalId(){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[@typeCode='PRCP' or string-length(normalize-space(@typeCode)) = 0]/cda:intendedRecipient/cda:receivedOrganization/cda:id[@root='"
			+ getOidOrganizationId() + "' or @root='" + getOidOrganizationIdOld() + "']/@extension");
	}
	
	public String getReferralDepartmentName(){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[@typeCode='PRCP' or string-length(normalize-space(@typeCode)) = 0]/cda:intendedRecipient/cda:receivedOrganization/cda:name[2]");
	}
	
	public String getReferralDepartmentId(){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[@typeCode='PRCP' or string-length(normalize-space(@typeCode)) = 0]/cda:intendedRecipient/cda:receivedOrganization/cda:id[@root='"
			+ getOidDepartmentId() + "' or @root='" + getOidDepartmentIdOld() + "']/@extension");
	}
	
	public String getAuthorDocboxId(){
		return getFieldValue("//cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:id[@root='"
			+ getOidUserDocboxId() + "' or @root='" + getOidUserDocboxIdOld() + "']/@extension");
	}
	
	public String getAuthorEancodeId(){
		return getFieldValue("//cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:id[@root='"
			+ oid_ean + "']/@extension");
	}
	
	public String getAuthorHospitalId(){
		return getFieldValue("//cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:id[@root='"
			+ DocboxCDA.getOidDoctorHospitalId() + "']/@extension");
	}
	
	public String getAuthorLastName(){
		return getFieldValue("//cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:assignedPerson/cda:name/cda:family");
	}
	
	public String getAuthorFirstName(){
		return getFieldValue("//cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:assignedPerson/cda:name/cda:given");
	}
	
	public String getAuthorName(){
		if (getAuthorLastName() == null) {
			return getFieldValue("//cda:ClinicalDocument/cda:author/cda:assignedAuthor/cda:assignedPerson/cda:name");
		}
		if (this.getAuthorFirstName() == null) {
			return getAuthorLastName();
		}
		return this.getAuthorFirstName() + " " + getAuthorLastName();
	}
	
	public int getReceivers(){
		String receivers[] = getFieldValues("//cda:ClinicalDocument/cda:informationRecipient");
		return receivers != null ? receivers.length : 0;
	}
	
	public String getReceiverDocboxId(int i){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[position() = "
			+ (i + 1) + "]/cda:intendedRecipient/cda:id[@root='" + getOidUserDocboxId()
			+ "' or @root='" + getOidUserDocboxIdOld() + "']/@extension");
	}
	
	public String getReceiverEanCodeId(int i){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[position() = "
			+ (i + 1) + "]/cda:intendedRecipient/cda:id[@root='" + oid_ean + "']/@extension");
	}
	
	public String getReceiverLastName(int i){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[position() = "
			+ (i + 1) + "]/cda:intendedRecipient/cda:informationRecipient/cda:name/cda:family");
	}
	
	public String getReceiverFirstName(int i){
		return getFieldValue("//cda:ClinicalDocument/cda:informationRecipient[position() = "
			+ (i + 1) + "]/cda:intendedRecipient/cda:informationRecipient/cda:name/cda:given");
	}
	
	public String getCustodianHospitalName(){
		return getFieldValue("//cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:name[1]");
	}
	
	public String getCustodianHospitalId(){
		return getFieldValue("//cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:id[@root='"
			+ getOidOrganizationId() + "' or @root='" + getOidOrganizationIdOld() + "']/@extension");
	}
	
	public String getCustodianDocboxId(){
		return getFieldValue("//cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:id[@root='"
			+ getOidUserDocboxId() + "' or @root='" + getOidUserDocboxIdOld() + "']/@extension");
	}
	
	public String getCustodianEanCode(){
		return getFieldValue("//cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:id[@root='"
			+ oid_ean + "']/@extension");
	}
	
	public String getCustodianDepartmentName(){
		return getFieldValue("//cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:name[2]");
	}
	
	public String getCustodianDepartmentId(){
		return getFieldValue("//cda:ClinicalDocument/cda:custodian/cda:assignedCustodian/cda:representedCustodianOrganization/cda:id[@root='"
			+ getOidDepartmentId() + "' or @root='" + getOidDepartmentIdOld() + "']/@extension");
	}
	
	public String getDocboxReferralInfo(){
		StringBuffer strBuf = new StringBuffer();
		
		strBuf.append("Docbox Dokument ID:\t" + getDocumentIdDocbox() + "\n");
		strBuf.append("Auftrags ID docbox:\t" + getAuftragsnummmer() + "\n");
		strBuf.append("Spital:\t" + getReferralHospitalName() + "\n");
		strBuf.append("SpitalId:\t" + getReferralHospitalId() + "\n");
		strBuf.append("Fachbereich:\t" + getReferralDepartmentName() + "\n");
		strBuf.append("FachbereichId:\t" + getReferralDepartmentId() + "\n");
		strBuf.append("Sender Arzt ID docbox:\t" + getAuthorDocboxId() + "\n");
		strBuf.append("Sender Arzt Nachname:\t" + getAuthorLastName() + "\n");
		strBuf.append("Sender Arzt Vorname:\t" + getAuthorFirstName() + "\n");
		strBuf.append("Empfänger ID docbox:\t" + getReceiverDocboxId(0) + "\n");
		strBuf.append("Empfänger Nachname:\t" + getReceiverLastName(0) + "\n");
		strBuf.append("Empfänger Vorname:\t" + getReceiverFirstName(0) + "\n");
		strBuf.append("PID-Hausarzt docbox:\t" + getPatientNumber() + "\n");
		strBuf.append("Patient Nachname:\t" + getPatientLastName() + "\n");
		strBuf.append("Patient Vorname:\t" + getPatientFirstName() + "\n");
		strBuf.append("Patient Geburtstag:\t" + getPatientDateOfBirth() + "\n");
		
		return strBuf.toString();
	}
	
	@Override
	public String toString(){
		if (doc == null) {
			return "no document set";
		}
		StringBuffer strBuf = new StringBuffer();
		if (oid_loinc.equals(getCodeCodeSystem())) {
			if (isDocboxReferral()) {
				strBuf.append("Docbox Spitalüberweisung\n");
				strBuf.append(getDocboxReferralInfo());
				return strBuf.toString();
			}
		} else if ("NA".equals(getCodeCode())) {
			strBuf.append("TOBEDONE");
		} else {
			strBuf.append("WARNING: code not identified for document");
		}
		strBuf.append(xml);
		return strBuf.toString();
	}
	
	/**
	 * Patientendaten Name - cda Header
	 * 
	 * @return String
	 */
	public String getPatientLastName(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name/cda:family";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Vorname - cda Header
	 * 
	 * @return String
	 */
	public String getPatientFirstName(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name/cda:given";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Strasse/Nr. - cda Header
	 * 
	 * @return String
	 */
	public String getPatientStreet(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:streetAddressLine";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten PLZ - cda Header
	 * 
	 * @return String
	 */
	public String getPatientPlz(){
		
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:postalCode";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Ort - cda Header
	 * 
	 * @return String
	 */
	public String getPatientCity(){
		String str = "//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:city";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Ort - cda Header
	 * 
	 * @return String
	 */
	public String getPatientAhv13(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:id[@root = '" + oid_ahv13
				+ "']/@extension";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Geburtsdatum - cda Header
	 * 
	 * @return [M|F|UN]
	 */
	public String getPatientDateOfBirth(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:birthTime/@value";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Geschlecht - cda Header
	 * 
	 * @return [M|F|UN]
	 */
	public String getPatientGender(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:administrativeGenderCode[@codeSystem = '2.16.840.1.113883.5.1']/@code";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Patientennummer - cda Header
	 * 
	 * @return String
	 */
	public String getPatientNumber(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:id[@root = '"
				+ DocboxCDA.getOidPraxisSoftwareId() + "' or @root = '"
				+ DocboxCDA.getOidPraxisSoftwareIdOld()
				+ "' or string-length(normalize-space(@root)) = 0]/@extension";
		return (getFieldValue(str));
	}
	
	/**
	 * Patientendaten Telefonnummer - cda Header
	 * 
	 * @return String
	 */
	public String getPatientHomePhone(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:telecom[substring(@value, 1, 3) = 'tel' and @use='HP']/@value";
		return getSubStringAfter(getFieldValue(str), ":");
	}
	
	/**
	 * Patientendaten Telefonnummer Geschäft - cda Header
	 * 
	 * @return String
	 */
	public String getPatientOfficePhone(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:telecom[substring(@value, 1, 3) = 'tel' and @use='WP']/@value";
		return getSubStringAfter(getFieldValue(str), ":");
	}
	
	/**
	 * Patientendaten Mobilnummer - cda Header
	 * 
	 * @return String
	 */
	public String getPatientMobile(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:telecom[substring(@value, 1, 3) = 'tel' and string-length(normalize-space(@use)) = 0]/@value";
		return getSubStringAfter(getFieldValue(str), ":");
	}
	
	/**
	 * Patientendaten E-Mail- cda Header
	 * 
	 * @return String
	 */
	public String getPatientEmail(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:telecom[substring(@value, 1, 4) = 'mail']/@value";
		return getSubStringAfter(getFieldValue(str), ":");
	}
	
	// FIXME needs to be done
	private String getSectionTextPath(String code, String title, String codeSystem,
		String oldCodeSystem){
		if (title == null && code == null && codeSystem != null) {
			return "//cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section[cda:code[@nullFlavor='NA']/cda:translation[@codeSystem='"
				+ codeSystem + "' or @codeSystem='" + oldCodeSystem + "']]/cda:text";
		}
		if (title == null) {
			return "//cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section[cda:code[@nullFlavor='NA']/cda:translation[@code='"
				+ code
				+ "' and (@codeSystem='"
				+ codeSystem
				+ "' or @codeSystem='"
				+ oldCodeSystem
				+ "')]]/cda:text";
		}
		return "//cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section[cda:title='"
			+ title
			+ "' or cda:code[@nullFlavor='NA']/cda:translation[@code='"
			+ code
			+ "' and (@codeSystem='"
			+ codeSystem
			+ "' or @codeSystem='"
			+ oldCodeSystem
			+ "')]]/cda:text";
	}
	
	private String getSectionTextPath(String code){
		return this.getSectionTextPath(code, null, getOidDocboxSectionId(),
			getOidDocboxSectionIdOld());
	}
	
	/**
	 * Administrative Angaben AuftragsID docbox - AOK
	 * 
	 * @return String
	 */
	public String getAuftragsnummmer(){
		return getFieldValue(getSectionTextPath("AOK"));
	}
	
	/**
	 * Administrative Angaben Versicherungsklasse - AVK
	 * 
	 * @return [Allgemein|Halbprivat|Privat|Allgemein CH|Selbstzahler]
	 */
	public String getVersicherungsklasse(){
		return (getFieldValue(getSectionTextPath("AVK")));
	}
	
	/**
	 * Administrative Angaben Krankenkasse - AKK
	 * 
	 * @return String
	 */
	public String getKrankenkasse(){
		return (getFieldValue(getSectionTextPath("AKK")));
	}
	
	/**
	 * Administrative Angaben Policennummer Krankenkasse AKP String
	 * 
	 * @return String
	 */
	public String getKrankenkassePolicenummer(){
		return (getFieldValue(getSectionTextPath("AKP")));
	}
	
	/**
	 * Administrative Angaben Zusatzversicherung KK AZK
	 * 
	 * @return String
	 */
	public String getKrankenkasseZusatzversicherung(){
		return (getFieldValue(getSectionTextPath("AZK")));
	}
	
	/**
	 * Administrative Angaben Policennummer Zusatzversicherung KK AZP
	 * 
	 * @return String
	 */
	public String getKrankenkasseZusatzversicherungPolicenummer(){
		return (getFieldValue(getSectionTextPath("AZP")));
	}
	
	/**
	 * Administrative Angaben Unfallversicherung AUV String
	 * 
	 * @return String
	 */
	public String getUnfallversicherung(){
		return (getFieldValue(getSectionTextPath("AUV")));
	}
	
	/**
	 * Administrative Angaben Policennummer Unfallversicherung AUP String
	 * 
	 * @return String
	 */
	public String getUnfallversicherungPolicenummer(){
		return (getFieldValue(getSectionTextPath("AUP")));
	}
	
	/**
	 * Administrative Angaben Zusatzversicherung Unfall AZU
	 * 
	 * @return String
	 */
	public String getUnfallZusatzversicherung(){
		return (getFieldValue(getSectionTextPath("AZU")));
	}
	
	/**
	 * Administrative Angaben Policennummer Zusatzversicherung Unfall AZPU String
	 * 
	 * @return String
	 */
	public String getUnfallZusatzversicherungPolicenummer(){
		return (getFieldValue(getSectionTextPath("AZPU")));
	}
	
	/**
	 * Administrative Angaben Arbeitgeber AAG String
	 * 
	 * @return String
	 */
	public String getArbeitgeber(){
		return (getFieldValue(getSectionTextPath("AAG")));
	}
	
	/**
	 * Administrative Angaben Einweisungsgrund AEG [Krankheit|Unfall|Geburt|Mutterschaft|Anderer]
	 * 
	 * @return String
	 */
	public String getEinweisungsgrund(){
		return (getFieldValue(getSectionTextPath("AEG")));
	}
	
	/**
	 * Administrative Angaben Aufenthaltsart AAA [ambulant|stationär|kurzstationär]
	 * 
	 * @return String
	 */
	public String getAufenthaltsart(){
		return (getFieldValue(getSectionTextPath("AAA")));
	}
	
	/**
	 * Administrative Angaben Eintritt Datum AED String
	 * 
	 * @return String
	 */
	public String getEintrittDatum(){
		return (getFieldValue(getSectionTextPath("AED")));
	}
	
	/**
	 * Administrative Angaben Eintritt Zeit AEZ String
	 * 
	 * @return String
	 */
	public String getEintrittZeit(){
		return (getFieldValue(getSectionTextPath("AEZ")));
	}
	
	/**
	 * Administrative Angaben Eintritt Vortag AEV
	 * 
	 * @return [ja|nein|unbekannt]
	 */
	public Options getEintrittVortag(){
		return (getFieldValueOptions(getSectionTextPath("AEV")));
	}
	
	/**
	 * Administrative Angaben Eintritt Nüchtern AEN
	 * 
	 * @return [ja|nein|unbekannt]
	 */
	public Options getEintrittNuechtern(){
		return (getFieldValueOptions(getSectionTextPath("AEN")));
	}
	
	/**
	 * Administrative Angaben Operation Datum AOPD String
	 * 
	 * @return String
	 */
	public String getOperationDatum(){
		return (getFieldValue(getSectionTextPath("AOPD")));
	}
	
	/**
	 * Administrative Angaben Operation Zeit AOPZ String
	 * 
	 * @return String
	 */
	public String getOperationZeit(){
		return (getFieldValue(getSectionTextPath("AOPZ")));
	}
	
	/**
	 * Administrative Angaben Fix-Zeit Operation AOPFZ String
	 * 
	 * @return String
	 */
	public String getOperationFixZeit(){
		return (getFieldValue(getSectionTextPath("AOPFZ")));
	}
	
	/**
	 * Administrative Angaben Dringlichkeit ADRINDGLICHKEIT Selekion aus Liste: Verfügbare
	 * Dringlichkeiten abhängig von Leistung
	 * 
	 * @return String
	 */
	public String getDringlichkeit(){
		return (getFieldValue(getSectionTextPath("ADRINDGLICHKEIT")));
	}
	
	/**
	 * Administrative Angaben Frühere Aufenthalte im Spital bei der die Anmeldung gemacht wird
	 * (Klinik/Datum):
	 * 
	 * @return String
	 */
	public String getFruehereAufenthalte(){
		return (getFieldValue(getSectionTextPath("AFAS")));
	}
	
	/**
	 * Administrative Angaben Name Hausarzt ANH String
	 * 
	 * @return String
	 */
	public String getHauszart(){
		return (getFieldValue(getSectionTextPath("ANH")));
	}
	
	/**
	 * Administrative Angaben Patient muss präoperativ zum Hausarzt APH
	 * 
	 * @return [ja|nein|unbekannt]
	 */
	public Options getPraeoperativHausarzt(){
		return (getFieldValueOptions(getSectionTextPath("APH")));
	}
	
	/**
	 * Administrative Angaben Bemerkungen/Beilagen ABB Text
	 * 
	 * @return String
	 */
	public String getBemerkungenBeilagen(){
		return (getFieldValue(getSectionTextPath("ABB")));
	}
	
	/**
	 * Leistung/Fragestellung Gewünschte Leistungen
	 * 
	 * @return String Array
	 */
	public String[] getGewuenschteLeistungen(){
		return getFieldValues(getSectionTextPath(null, null, getOidDocboxLeistungId(),
			getOidDocboxLeistungIdOld()));
	}
	
	/**
	 * Leistung/Fragestellung Ergänzungen zur Leistung LE Text
	 * 
	 * @return String
	 */
	public String getErgaenzungenLeistung(){
		return (getFieldValue(getSectionTextPath("LE")));
	}
	
	/**
	 * Leistung/Fragestellung Klinische Angaben/Fragestellung LKA Text
	 * 
	 * @return String
	 */
	public String getKlinischeAngabeFragestellung(){
		return (getFieldValue(getSectionTextPath("LKA")));
	}
	
	/**
	 * Leistung/Fragestellung Diagnose LD String
	 * 
	 * @return String
	 */
	public String getDiagnose(){
		return (getFieldValue(getSectionTextPath("LD")));
	}
	
	/**
	 * Leistung/Fragestellung Diagnose Verlauf LDV String
	 * 
	 * @return String
	 */
	public String getDiagnoseVerlauf(){
		return (getFieldValue(getSectionTextPath("LDV")));
	}
	
	/**
	 * Notiz
	 * 
	 * @return String
	 */
	public String getNotiz(){
		return (getFieldValue(getSectionTextPath("NOTIZ")));
	}
	
	/**
	 * Leistung/Fragestellung Anamnese LA String
	 * 
	 * @return String
	 */
	public String getAnamnese(){
		return (getFieldValue(getSectionTextPath("LA")));
	}
	
	/**
	 * Leistung/Fragestellung Gewünschte Körperregion LK
	 * 
	 * @return String
	 */
	public String getGewuenschteKoerperregion(){
		return (getFieldValue(getSectionTextPath("LK")));
	}
	
	/**
	 * Klinische Angaben Medikamente KMED Text mit <cda:br/> (oder in alter Version br Elementen)
	 * Elementen getrennt für Einzeleinträge von Medikamenten
	 * 
	 * @return String
	 */
	public String getMedikamente(){
		return formatString((getFieldValue(
			getSectionTextPath("KMED", "Medikamente", getOidDocboxSectionId(),
				getOidDocboxSectionIdOld()), true)));
	}
	
	/**
	 * Klinische Angaben Schwangerschaft/Stillzeit KSCH [ja:|nein:]Bemerkung
	 */
	public String getSchwangerschaft(){
		return getFieldValue(getSectionTextPath("KSCH", "Schwangerschaft/Stillzeit",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	public Options getHasSchwangerschaft(){
		return getOptions(getSubStringBefore(getSchwangerschaft(), ":"));
	}
	
	public String getSchwangerschaftBemerkung(){
		return getSubStringAfter(getSchwangerschaft(), ":");
	}
	
	/**
	 * Klinische Angaben Allergien KALL [ja:|nein:|unbekannt:]Text mit <cda:br/> (oder in alter
	 * Version br Elementen) getrennt für Einzeleinträge von Allergien
	 */
	public String getAllergie(){
		return formatString(getFieldValue(
			getSectionTextPath("KALL", "Allergien", getOidDocboxSectionId(),
				getOidDocboxSectionIdOld()), true));
	}
	
	public Options getHasAllergie(){
		return getOptions(getSubStringBefore(getAllergie(), ":"));
	}
	
	public String getAllergieBemerkung(){
		return getSubStringAfter(getAllergie(), ":");
	}
	
	/**
	 * Klinische Angaben Kreatininwert KKRET String
	 */
	public String getKreatininwert(){
		return getFieldValue(getSectionTextPath("KKRET", "Kreatininwert", getOidDocboxSectionId(),
			getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben INR KINR String
	 */
	public String getInr(){
		return getFieldValue(getSectionTextPath("KINR", "INR", getOidDocboxSectionId(),
			getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Thrombozyten KTHROMBO String
	 */
	public String getThrombozyten(){
		return getFieldValue(getSectionTextPath("KTHROMBO", "Thrombozyten",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Schilddrüsen-Überfunktion KSCHILD [ja|nein|unbekannt]
	 */
	public Options getSchilddruesenUeberfunktion(){
		return getFieldValueOptions(getSectionTextPath("KSCHILD", "Schilddrüsen-Überfunktion",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Biguanid Medikation KBIUGANID [ja|nein|unbekannt]
	 */
	public Options getBiguanidMedication(){
		return getFieldValueOptions(getSectionTextPath("KBIUGANID", "Biguanid Medikation",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Herzschrittmacher KHERZSCHRITT [ja|nein|unbekannt]
	 */
	public Options getHerzschrittmacher(){
		return getFieldValueOptions(getSectionTextPath("KHERZSCHRITT", "Herzschrittmacher",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Metallimplantate KMETALL [ja|nein|unbekannt]
	 */
	public Options getMetallImplantate(){
		return getFieldValueOptions(getSectionTextPath("KMETALL", "Metallimplantate",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Clips/Metallsplitter KCLIPS [ja|nein|unbekannt]
	 */
	public Options getClipsMetallSplitter(){
		return getFieldValueOptions(getSectionTextPath("KCLIPS", "Clips/Metallsplitter",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Gehörimplantat KGEHÖRIMPL [ja|nein|unbekannt]
	 */
	public Options getGehoerImplantat(){
		return getFieldValueOptions(getSectionTextPath("KGEHÖRIMPL", "Gehörimplantat",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Platzangst KPLATZANGST [ja|nein|unbekannt]
	 */
	public Options getPlatzangst(){
		return getFieldValueOptions(getSectionTextPath("KPLATZANGST", "Platzangst",
			getOidDocboxSectionId(), getOidDocboxSectionIdOld()));
	}
	
	/**
	 * Klinische Angaben Kind zur Sedation KKINDSEDATION [ja|nein|unbekannt]
	 */
	public Options getKindZurSedation(){
		return getFieldValueOptions(getSectionTextPath("KKINDSEDATION"));
	}
	
	/**
	 * Leistung/Fragestellung OP-Dauer (Std/Min) KOPD String
	 * 
	 * @return String
	 */
	public String getOpDauer(){
		return (getFieldValue(getSectionTextPath("KOPD")));
	}
	
	/**
	 * Leistung/Fragestellung Aufenthaltsdauer (in Tagen) KOPAUF String
	 * 
	 * @return String
	 */
	public String getAufenthaltsdauer(){
		return (getFieldValue(getSectionTextPath("KOPAUF")));
	}
	
	/**
	 * Leistung/Fragestellung Operateur KOPERATEUR String
	 * 
	 * @return String
	 */
	public String getOperateur(){
		return (getFieldValue(getSectionTextPath("KOPERATEUR")));
	}
	
	/**
	 * Leistung/Fragestellung OP-Assistent KOPASSISTANT String
	 * 
	 * @return String
	 */
	public String getOpAssitent(){
		return (getFieldValue(getSectionTextPath("KOPASSISTANT")));
	}
	
	/**
	 * Leistung/Fragestellung Instrumente KINSTRUMENT Selekion aus Liste: Verfügbare Instrumente
	 * abhängig von Leistung
	 * 
	 * @return String
	 */
	public String getInstrumente(){
		return formatString(getFieldValue(getSectionTextPath("KINSTRUMENT"), true));
	}
	
	/**
	 * Leistung/Fragestellung Lagerung KLAGERUNG Selekion aus Liste: Verfügbare Lagerungen abhängig
	 * von Leistung
	 * 
	 * @return String
	 */
	public String getLagerung(){
		return formatString(getFieldValue(getSectionTextPath("KLAGERUNG"), true));
	}
	
	/**
	 * Leistung/Fragestellung Anästhesie KANÄSTHESIE Selekion aus Liste: Verfügbare Anästhesie
	 * abhängig von Leistung
	 * 
	 * @return String
	 */
	public String getAnaesthesie(){
		return formatString(getFieldValue(getSectionTextPath("KANÄSTHESIE"), true));
	}
	
	/**
	 * Leistung/Fragestellung Bitte zur Anästhesieprechstunde aufbieten KANÄSTHESIEAUFBIETEN
	 * [ja|nein|unbekannt]
	 * 
	 * @return String
	 */
	public Options getAnaesthesieSprechstunde(){
		return (getFieldValueOptions(getSectionTextPath("KANÄSTHESIEAUFBIETEN")));
	}
	
	/**
	 * docbox Generell Beilagen ATT String
	 */
	public String[] getAttachments(){
		return getFieldValues("//cda:ClinicalDocument/cda:component/cda:structuredBody/cda:component/cda:section[cda:code[@nullFlavor='NA']/cda:translation[@code='ATT' and (@codeSystem='"
			+ getOidDocboxSectionId()
			+ "' or @codeSystem='"
			+ getOidDocboxSectionIdOld()
			+ "')]]/cda:text/cda:linkHtml");
	}
	
	/**
	 * docbox Spitalkommunikation DocumentID DOCID String
	 * 
	 * @return String
	 */
	public String getDocumentIdInSection(){
		return getFieldValue(getSectionTextPath("DOCID"));
	}
	
	/**
	 * docbox Spitalkommunikation Datum Aufgebot MDATUMAUFGEBOT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public String getDatumAufgebot(){
		return getFieldValue(getSectionTextPath("MDATUMAUFGEBOT"));
	}
	
	/**
	 * docbox Spitalkommunikation Änderung Datum Aufgebot MDATUMAUFGEBOTÄNDERUNG [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public String getDatumAufgebotAenderung(){
		return getFieldValue(getSectionTextPath("MDATUMAUFGEBOTÄNDERUNG"));
	}
	
	/**
	 * docbox Spitalkommunikation Datum Eintritt MDATUMEINTRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public String getDatumEintritt(){
		return getFieldValue(getSectionTextPath("MDATUMEINTRITT"));
	}
	
	/**
	 * docbox Spitalkommunikation Datum Storno Eintritt MDATUMSTORNOEINTRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public String getDatumStornoEintritt(){
		return getFieldValue(getSectionTextPath("MDATUMSTORNOEINTRITT"));
	}
	
	/**
	 * docbox Spitalkommunikation Datum Austritt MDATUMAUSRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public String getDatumAustritt(){
		return getFieldValue(getSectionTextPath("MDATUMAUSRITT"));
	}
	
	/**
	 * docbox Spitalkommunikation Datum Storno Austritt MDATUMSTORNOAUSTRITT [JJJJMMTThhmmss]
	 * 
	 * @return String
	 */
	public String getDatumStornoAustritt(){
		return getFieldValue(getSectionTextPath("MDATUMSTORNOAUSTRITT"));
	}
	
	private Element getId(String root, String extension, String prefix){
		Element id = getElement("id", prefix);
		id.setAttribute("root", root);
		id.setAttribute("extension", extension);
		return id;
	}
	
	private Element getElement(String name, String prefix){
		Element element = doc.createElementNS("urn:hl7-org:v3", name);
		element.setPrefix(prefix);
		return element;
	}
	
	/**
	 * <cda:author> <cda:time/> <cda:assignedAuthor> <cda:id extension='docboxid'
	 * root='2.25.327919736312109525688528068157180855579.1.1'/> <cda:id extension='eancode'
	 * root='1.3.88'/> <cda:assignedPerson> <cda:name> <cda:given>Carsten</cda:given>
	 * <cda:family>Flöter</cda:family> </cda:name> </cda:assignedPerson> </cda:assignedAuthor>
	 * </cda:author>
	 */
	
	private Node getId(NodeList nodeList, String root){
		for (int i = 0; i < nodeList.getLength(); ++i) {
			Node node = nodeList.item(i);
			Node attribute = node.getAttributes().getNamedItem("root");
			if (attribute == null) {
				return null;
			}
			String value = attribute.getNodeValue();
			if (root.equals(value)) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * adds the author information to the current dom object
	 * 
	 * @param docboxId
	 * @param eanCode
	 * @param firstName
	 * @param lastName
	 * @return true if successfull, false if there was a mismatch, attention: part of dom
	 *         modifications before are still done, so document needs to be reloaded with
	 *         setPatientDocument if wanted
	 * 
	 */
	public boolean addAuthorToDocument(String docboxId, String eanCode, String firstName,
		String lastName){
		// create author tag
		NodeList nodeList = doc.getElementsByTagNameNS("urn:hl7-org:v3", "author");
		String prefix = "";
		Element author = null;
		if (nodeList.getLength() == 0) {
			// we do not have an author tag yet, we have to create on
			NodeList listRecordTargets =
				doc.getElementsByTagNameNS("urn:hl7-org:v3", "recordTarget");
			if (listRecordTargets.getLength() == 0) {
				return false;
			}
			Node recordTarget = listRecordTargets.item(listRecordTargets.getLength() - 1);
			prefix = recordTarget.getPrefix();
			author = getElement("author", prefix);
			if (recordTarget.getNextSibling() == null) {
				recordTarget.getParentNode().appendChild(author);
			} else {
				recordTarget.getParentNode().insertBefore(author, recordTarget.getNextSibling());
			}
		} else {
			author = (Element) nodeList.item(0);
			prefix = author.getPrefix();
		}
		
		getOrCreateChild(author, "time", prefix, true);
		Element assignedAuthor = getOrCreateChild(author, "assignedAuthor", prefix, false);
		
		nodeList = assignedAuthor.getElementsByTagNameNS("urn:hl7-org:v3", "id");
		if (docboxId != null) {
			Node node = getId(nodeList, getOidUserDocboxId());
			if (node == null) {
				Element idDocbox = getId(getOidUserDocboxId(), docboxId, prefix);
				if (assignedAuthor.getFirstChild() == null) {
					assignedAuthor.appendChild(idDocbox);
				} else {
					assignedAuthor.insertBefore(idDocbox, assignedAuthor.getFirstChild());
				}
			} else {
				if (!docboxId.equals(node.getAttributes().getNamedItem("extension").getNodeValue())) {
					return false;
				}
			}
		}
		if (eanCode != null) {
			Node node = getId(nodeList, oid_ean);
			if (node == null) {
				Element idEanCode = getId(oid_ean, eanCode, prefix);
				if (assignedAuthor.getFirstChild() == null) {
					assignedAuthor.appendChild(idEanCode);
				} else {
					assignedAuthor.insertBefore(idEanCode, assignedAuthor.getFirstChild());
				}
			} else {
				if (!eanCode.equals(node.getAttributes().getNamedItem("extension").getNodeValue())) {
					return false;
				}
			}
		}
		
		Element assignedPerson = getOrCreateChild(assignedAuthor, "assignedPerson", prefix, false);
		Element name = getOrCreateChild(assignedPerson, "name", prefix, false);
		Element given = getOrCreateChild(name, "given", prefix, false);
		if (given.getLastChild() != null && firstName != null) {
			if (!firstName.equals(given.getLastChild().getNodeValue())) {
				// maybe just a warning?
				// return false;
			}
		} else {
			if (firstName != null) {
				given.appendChild(doc.createTextNode(firstName));
			}
		}
		
		Element family = getOrCreateChild(name, "family", prefix, false);
		if (family.getLastChild() != null && lastName != null) {
			if (!lastName.equals(family.getLastChild().getNodeValue())) {
				// maybe just a warning?
				// return false;
			}
		} else {
			if (lastName != null) {
				family.appendChild(doc.createTextNode(lastName));
			}
		}
		
		return true;
	}
	
	/**
	 * adds the custodian information to the current dom object
	 * 
	 * @param docboxId
	 * @param organizationId
	 * @param name
	 * @return true if successfull, false if there was a mismatch, attention: part of dom
	 *         modifications before are still done, so document needs to be reloaded with
	 *         setPatientDocument if wanted
	 * 
	 */
	public boolean addCustodianToDocument(String docboxId, String organizationId, String name){
		NodeList nodeList = doc.getElementsByTagNameNS("urn:hl7-org:v3", "custodian");
		String prefix = "";
		Element custodian = null;
		if (nodeList.getLength() == 0) {
			// we do not have an author tag yet, we have to create on
			NodeList listAuthors = doc.getElementsByTagNameNS("urn:hl7-org:v3", "author");
			if (listAuthors.getLength() == 0) {
				return false;
			}
			Node author = listAuthors.item(listAuthors.getLength() - 1);
			prefix = author.getPrefix();
			custodian = getElement("custodian", prefix);
			if (author.getNextSibling() == null) {
				author.getParentNode().appendChild(custodian);
			} else {
				author.getParentNode().insertBefore(custodian, author.getNextSibling());
			}
		} else {
			custodian = (Element) nodeList.item(0);
			prefix = custodian.getPrefix();
		}
		
		Element assignedCustodian = getOrCreateChild(custodian, "assignedCustodian", prefix, false);
		Element representedCustodian =
			getOrCreateChild(assignedCustodian, "representedCustodianOrganization", prefix, false);
		
		nodeList = representedCustodian.getElementsByTagNameNS("urn:hl7-org:v3", "id");
		if (docboxId != null) {
			Node node = getId(nodeList, getOidUserDocboxId());
			if (node == null) {
				Element idDocbox = getId(getOidUserDocboxId(), docboxId, prefix);
				if (representedCustodian.getFirstChild() == null) {
					representedCustodian.appendChild(idDocbox);
				} else {
					representedCustodian.insertBefore(idDocbox,
						representedCustodian.getFirstChild());
				}
			} else {
				if (!docboxId.equals(node.getAttributes().getNamedItem("extension").getNodeValue())) {
					return false;
				}
			}
		}
		if (organizationId != null) {
			Node node = getId(nodeList, getOidOrganizationId());
			if (node == null) {
				Element idEanCode = getId(getOidOrganizationId(), organizationId, prefix);
				if (representedCustodian.getFirstChild() == null) {
					representedCustodian.appendChild(idEanCode);
				} else {
					representedCustodian.insertBefore(idEanCode,
						representedCustodian.getFirstChild());
				}
			} else {
				if (!organizationId.equals(node.getAttributes().getNamedItem("extension")
					.getNodeValue())) {
					return false;
				}
			}
		}
		NodeList listNames = representedCustodian.getElementsByTagNameNS("urn:hl7-org:v3", "name");
		if (listNames.getLength() == 0) {
			Element nameElement = getOrCreateChild(representedCustodian, "name", prefix, false);
			if (name != null) {
				nameElement.appendChild(doc.createTextNode(name));
			}
		}
		return true;
	}
	
	private Element getOrCreateChild(Element parent, String childName, String prefix, boolean first){
		NodeList nodeList = parent.getElementsByTagNameNS("urn:hl7-org:v3", childName);
		Element child = null;
		if (nodeList.getLength() > 0) {
			child = (Element) nodeList.item(0);
		} else {
			child = getElement(childName, prefix);
			if (!first || parent.getFirstChild() == null) {
				parent.appendChild(child);
			} else {
				parent.insertBefore(child, parent.getFirstChild());
			}
		}
		return child;
	}
	
	public String getDocumentSerialized(){
		// Serialisation through Tranform.
		StringWriter stringWriter = new StringWriter();
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamResult = new StreamResult(stringWriter);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer;
		try {
			serializer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		serializer.setOutputProperty(OutputKeys.ENCODING, "UTF8");
		try {
			serializer.transform(domSource, streamResult);
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
		return stringWriter.toString();
	}
	
	public boolean isCdaDocboxType(final DOCBOXCDATYPE docboxCdaType){
		return docboxCdaType.name().equals(
			getFieldValue("//cda:ClinicalDocument/cda:templateId[@root='" + oid_docbox
				+ "' or @root='" + oid_docbox_old + "']/@extension"));
	}
	
	public boolean isCdaDocboxSpitalArzt(){
		return isCdaDocboxType(DOCBOXCDATYPE.Docbox_Spital_Arzt);
	}
	
	public boolean isCdaDocboxSpitalAnmeldung(){
		return isCdaDocboxType(DOCBOXCDATYPE.Docbox_Spital_Anmeldung);
	}
	
	public boolean isCdaDocboxTeminvereinbarung(){
		return isCdaDocboxType(DOCBOXCDATYPE.Docbox_Terminvereinbarung);
	}
	
	public boolean isCdaDocboxArztArzt(){
		return isCdaDocboxType(DOCBOXCDATYPE.Docbox_Arzt_Arzt);
	}
}
