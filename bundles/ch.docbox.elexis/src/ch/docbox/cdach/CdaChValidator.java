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
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CdaChValidator {
	
	private XPathFactory factory;
	private XPath xpath;
	
	private Document doc;
	
	private String xmlFileName;
	private Map<String, XPathExpression> expressionCache = new HashMap<String, XPathExpression>();
	
	public CdaChValidator(){
		factory = XPathFactory.newInstance();
		xpath = factory.newXPath();
		xpath.setNamespaceContext(new CdaNamespaceContext());
	}
	
	public String getFieldValue(String expression){
		Object result = null;
		try {
			XPathExpression xpathCheckTypeId = expressionCache.get(expression);
			if (xpathCheckTypeId == null) {
				xpathCheckTypeId = xpath.compile(expression);
				expressionCache.put(expression, xpathCheckTypeId);
			}
			result = xpathCheckTypeId.evaluate(doc, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			System.out.println(e);
		}
		if (result == null) {
			return null;
		} else {
			NodeList nodes = (NodeList) result;
			if ((nodes != null) && (nodes.item(0) != null)) {
				return nodes.item(0).getNodeValue();
			} else {
				Node node = (Node) result;
				return node.getNodeValue();
			}
		}
	}
	
	public String setPatientDocument(String document){
		xmlFileName = document;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			doc = docBuilder.parse(new ByteArrayInputStream(xmlFileName.getBytes("UTF-8")));
		} catch (SAXException e) {
			System.err.println(e);
			return e.toString();
		} catch (ParserConfigurationException e) {
			System.err.println(e);
			return e.toString();
		} catch (IOException e) {
			System.err.println(e);
			return e.toString();
		}
		return null;
	}
	
	public String getPatientLastName(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name/cda:family";
		return (getFieldValue(str));
	}
	
	public String getPatientFirstName(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:patient/cda:name/cda:given";
		return (getFieldValue(str));
	}
	
	public String getCity(){
		String str = "//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:addr/cda:city";
		return (getFieldValue(str));
	}
	
	public String getPatientNumber(){
		String str =
			"//cda:ClinicalDocument/cda:recordTarget/cda:patientRole/cda:id[@root = '"
				+ DocboxCDA.getOidPraxisSoftwareId()
				+ "' or string-length(normalize-space(@root)) = 0]/@extension";
		return (getFieldValue(str));
	}
	
}
