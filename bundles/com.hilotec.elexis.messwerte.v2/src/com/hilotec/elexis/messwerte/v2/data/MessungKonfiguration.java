/*******************************************************************************
 * Copyright (c) 2007-2010, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    G. Weirich - added layout option
 *    P. Chaubert - adapted to Messwerte V2
 *    medshare GmbH - adapted to Messwerte V2.1 in February 2012
 *    
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.data;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;

import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypBool;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypCalc;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypCount;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypData;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypDate;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypEnum;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypNum;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypScale;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypStr;
import com.hilotec.elexis.messwerte.v2.views.Preferences;

public class MessungKonfiguration {
	public static final String CONFIG_FILENAME = "messwerte_v2.xml"; //$NON-NLS-1$
	
	public static final String ELEMENT_VAR = "var"; //$NON-NLS-1$
	public static final String ELEMENT_FORMULA = "formula"; //$NON-NLS-1$
	public static final String ELEMENT_DATATYPE = "datatype"; //$NON-NLS-1$
	
	// Design
	public static final String ELEMENT_LAYOUTDESIGN = "design"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTLABEL = "label"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTLABEL_TEXT = "text"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTPANEL = "panel"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTPANEL_PLAIN = "plain"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTGRID = "grid"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTGRID_COLUMNS = "columns"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTDISPLAY = "display"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTDISPLAY_URL = "url"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTDISPLAY_SIZE = "size"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTFIELD = "field"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTFIELD_REF = "ref"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTFIELD_EDITABLE = "editable"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTFIELD_VALIDPATTERN = "validpattern"; //$NON-NLS-1$
	public static final String ELEMENT_LAYOUTFIELD_INVALIDMESSAGE = "invalidmessage"; //$NON-NLS-1$
	public static final String ELEMENT_ENUM_OPTION = "option"; //$NON-NLS-1$
	public static final String ATTR_TYPE = "type"; //$NON-NLS-1$
	public static final String ATTR_VALUE = "value"; //$NON-NLS-1$
	public static final String ATTR_ATTRIBUTE = "attribute"; //$NON-NLS-1$
	
	// Formulas
	public static final String ATTR_SOURCE = "source"; //$NON-NLS-1$
	public static final String ATTR_INTERPRETER = "interpreter"; //$NON-NLS-1$
	
	// Datatypes
	public static final String ATTR_DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String ATTR_DEFAULT = "default"; //$NON-NLS-1$
	public static final String ATTR_UNIT = "unit"; //$NON-NLS-1$
	public static final String ATTR_TITLE = "title"; //$NON-NLS-1$
	public static final String ATTR_NAME = "name"; //$NON-NLS-1$
	public static final String ATTR_MAX = "max"; //$NON-NLS-1$
	public static final String ATTR_MIN = "min"; //$NON-NLS-1$
	public static final String ATTR_LINES = "lines"; //$NON-NLS-1$
	public static final String ATTR_LOW_ALERT = "lowAlert"; //$NON-NLS-1$
	public static final String ATTR_LOW_WARNING = "lowWarning"; //$NON-NLS-1$
	public static final String ATTR_HIGH_WARNING = "highWarning"; //$NON-NLS-1$
	public static final String ATTR_HIGH_ALERT = "highAlert"; //$NON-NLS-1$
	public static final String ATTR_FORMATPATTERN = "formatpattern"; //$NON-NLS-1$
	public static final String ATTR_ROUNDMODE = "roundingmode"; //$NON-NLS-1$
	public static final String ATTR_COUNTERMODE = "countermode"; //$NON-NLS-1$
	public static final String ATTR_STARTVALUE = "startvalue"; //$NON-NLS-1$
	
	public static final String NAME_BOOLFIELD = "boolfield"; //$NON-NLS-1$
	public static final String NAME_CALCFIELD = "calcfield"; //$NON-NLS-1$
	public static final String NAME_COUNTERFIELD = "counterfield"; //$NON-NLS-1$
	public static final String NAME_DATAFIELD = "datafield"; //$NON-NLS-1$
	public static final String NAME_DATEFIELD = "datefield"; //$NON-NLS-1$
	public static final String NAME_ENUMFIELD = "enumfield"; //$NON-NLS-1$
	public static final String NAME_SCALEFIELD = "scalefield"; //$NON-NLS-1$
	public static final String NAME_NUMFIELD = "numfield"; //$NON-NLS-1$
	public static final String NAME_STRINGFIELD = "strfield"; //$NON-NLS-1$
	
	private static MessungKonfiguration the_one_and_only_instance = null;
	ArrayList<MessungTyp> types;
	private final Log log = Log.get("DataConfiguration"); //$NON-NLS-1$
	private final String defaultFile;
	
	public static MessungKonfiguration getInstance(){
		if (the_one_and_only_instance == null) {
			the_one_and_only_instance = new MessungKonfiguration();
		}
		return the_one_and_only_instance;
	}
	
	private MessungKonfiguration(){
		types = new ArrayList<MessungTyp>();
		defaultFile =
			CoreHub.localCfg.get(Preferences.CONFIG_FILE, CoreHub.getWritableUserDir()
				+ File.separator + CONFIG_FILENAME);
	}
	
	private Panel createPanelFromNode(Element n){
		String type = n.getAttribute(ATTR_TYPE);
		Panel ret = new Panel(type);
		LinkedList<String> attributeList = new LinkedList<String>();
		LinkedList<Panel> panelsList = new LinkedList<Panel>();
		Node node = n.getFirstChild();
		while (node != null) {
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String nodename = node.getNodeName();
				if (nodename.equals(ATTR_ATTRIBUTE)) {
					NamedNodeMap na = node.getAttributes();
					String nx = na.getNamedItem(ATTR_NAME).getNodeValue() + "=" //$NON-NLS-1$
						+ na.getNamedItem(ATTR_VALUE).getNodeValue();
					attributeList.add(nx);
				} else if (nodename.equals(ELEMENT_LAYOUTPANEL)) {
					panelsList.add(createPanelFromNode((Element) node));
				}
			}
			node = node.getNextSibling();
		}
		ret.setAttributes(attributeList.toArray(new String[0]));
		ret.setPanels(panelsList.toArray(new Panel[0]));
		return ret;
		
	}
	
	public Boolean readFromXML(){
		return readFromXML(defaultFile);
	}
	
	public Boolean readFromXML(String path){
		types.clear();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		Document doc;
		SchemaFactory sfac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		if (path == null) {
			path = defaultFile;
		}
		try {
			String schemapath =
				PlatformHelper.getBasePath("com.hilotec.elexis.messwerte.v2") + File.separator //$NON-NLS-1$
					+ "rsc" + File.separator + "messwerte.xsd"; //$NON-NLS-1$ //$NON-NLS-2$
			Schema s = sfac.newSchema(new File(schemapath));
			factory.setSchema(s);
			
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new ErrorHandler() {
				public void error(SAXParseException exception) throws SAXException{
					throw exception;
				}
				
				public void fatalError(SAXParseException exception) throws SAXException{
					throw exception;
				}
				
				public void warning(SAXParseException exception) throws SAXException{
					throw exception;
				}
			});
			doc = builder.parse(new FileInputStream(path));
			
			Element rootel = doc.getDocumentElement();
			
			// datatype-Deklarationen durchgehen und einlesen
			NodeList nl = rootel.getElementsByTagName(ELEMENT_DATATYPE);
			for (int i = 0; i < nl.getLength(); i++) {
				Element edt = (Element) nl.item(i);
				String name = edt.getAttribute(ATTR_NAME);
				String title = edt.getAttribute(ATTR_TITLE);
				String description = edt.getAttribute(ATTR_DESCRIPTION);
				if (title.length() == 0) {
					title = name;
				}
				NodeList nll = edt.getElementsByTagName(ELEMENT_LAYOUTDESIGN);
				Element layout = (Element) nll.item(0);
				MessungTyp dt = null;
				if (layout != null) {
					NodeList nl2 = edt.getElementsByTagName(ELEMENT_LAYOUTPANEL);
					Panel panel = createPanelFromNode((Element) nl2.item(0));
					dt = new MessungTyp(name, title, description, panel);
				} else {
					dt = new MessungTyp(name, title, description);
					
				}
				
				// Einzlene Felddeklarationen durchgehen
				NodeList dtf = edt.getChildNodes();
				for (int j = 0; j < dtf.getLength(); j++) {
					Node ndtf = dtf.item(j);
					if (ndtf.getNodeType() != Node.ELEMENT_NODE) {
						continue;
					}
					
					Element edtf = (Element) ndtf;
					String fn = edtf.getAttribute(ATTR_NAME);
					String ft = edtf.getAttribute(ATTR_TITLE);
					if (ft.equals("")) { //$NON-NLS-1$
						ft = fn;
					}
					
					// OldMesswertTyp dft;
					IMesswertTyp typ;
					if (edtf.getNodeName().equals(NAME_NUMFIELD)) {
						MesswertTypNum num =
							new MesswertTypNum(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = num;
						if (edtf.hasAttribute(ATTR_DEFAULT))
							typ.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
						if (edtf.hasAttribute(ATTR_FORMATPATTERN))
							num.setFormatPattern(edtf.getAttribute(ATTR_FORMATPATTERN));
						
						if (edtf.hasAttribute(ATTR_DEFAULT))
							((MesswertTypNum) typ).setRoundingMode(edtf
								.getAttribute(ATTR_ROUNDMODE));
						
					} else if (edtf.getNodeName().equals(NAME_BOOLFIELD)) {
						typ = new MesswertTypBool(fn, ft, edtf.getAttribute(ATTR_UNIT));
						if (edtf.hasAttribute(ATTR_DEFAULT))
							typ.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
					} else if (edtf.getNodeName().equals(NAME_STRINGFIELD)) {
						MesswertTypStr str =
							new MesswertTypStr(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = str;
						
						if (edtf.hasAttribute(ATTR_DEFAULT))
							typ.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
						if (edtf.hasAttribute(ATTR_LINES))
							str.setLines(Integer.parseInt(edtf.getAttribute(ATTR_LINES)));
						
					} else if (edtf.getNodeName().equals(NAME_ENUMFIELD)) {
						MesswertTypEnum en =
							new MesswertTypEnum(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = en;
						
						if (edtf.hasAttribute(ATTR_DEFAULT))
							typ.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
						NodeList children = edtf.getChildNodes();
						for (int k = 0; k < children.getLength(); k++) {
							if (!ELEMENT_ENUM_OPTION.equals(children.item(k).getNodeName())) {
								continue;
							}
							if (children.item(k).getNodeType() != Node.ELEMENT_NODE) {
								continue;
							}
							
							Element choice = (Element) children.item(k);
							en.addChoice(choice.getAttribute(ATTR_TITLE),
								Integer.parseInt(choice.getAttribute(ATTR_VALUE)));
						}
						
						// Wenn kein vernuenftiger Standardwert angegeben wurde
						// nehmen wir die erste Auswahlmoeglichkeit
						if (typ.getDefault(null).equals("")) { //$NON-NLS-1$
							for (int k = 0; k < children.getLength(); k++) {
								if (ELEMENT_ENUM_OPTION.equals(children.item(k).getNodeName())) {
									if (children.item(k).getNodeType() == Node.ELEMENT_NODE) {
										Element choice = (Element) children.item(k);
										typ.setDefault(choice.getAttribute(ATTR_VALUE));
										break;
									}
								}
							}
						}
					} else if (edtf.getNodeName().equals(NAME_CALCFIELD)) {
						MesswertTypCalc calc =
							new MesswertTypCalc(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = calc;
						if (edtf.hasAttribute(ATTR_FORMATPATTERN))
							calc.setFormatPattern(edtf.getAttribute(ATTR_FORMATPATTERN));
						
						if (edtf.hasAttribute(ATTR_DEFAULT))
							typ.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
					} else if (edtf.getNodeName().equals(NAME_DATAFIELD)) {
						MesswertTypData data =
							new MesswertTypData(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = data;
						
						data.setRefType(edtf.getAttribute(ATTR_TYPE));
					} else if (edtf.getNodeName().equals(NAME_SCALEFIELD)) {
						MesswertTypScale scale =
							new MesswertTypScale(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = scale;
						if (edtf.hasAttribute(ATTR_DEFAULT))
							scale.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
						if (edtf.hasAttribute(ATTR_MIN))
							scale.setMin(Integer.parseInt(edtf.getAttribute(ATTR_MIN)));
						
						if (edtf.hasAttribute(ATTR_MAX))
							scale.setMax(Integer.parseInt(edtf.getAttribute(ATTR_MAX)));
						
					} else if (edtf.getNodeName().equals(NAME_DATEFIELD)) {
						MesswertTypDate date =
							new MesswertTypDate(fn, ft, edtf.getAttribute(ATTR_UNIT));
						typ = date;
						if (edtf.hasAttribute(ATTR_DEFAULT))
							typ.setDefault(edtf.getAttribute(ATTR_DEFAULT));
						
					} else if (edtf.getNodeName().equals(NAME_COUNTERFIELD)) {
						MesswertTypCount counter =
							new MesswertTypCount(fn, ft, edtf.getAttribute(ATTR_UNIT));
						counter.setCounterMode(edtf.getAttribute(ATTR_COUNTERMODE));
						typ = counter;
						if (edtf.hasAttribute(ATTR_FORMATPATTERN))
							counter.setFormatPattern(edtf.getAttribute(ATTR_FORMATPATTERN));
						
						if (edtf.hasAttribute(ATTR_STARTVALUE))
							counter.setStartValue(edtf.getAttribute(ATTR_STARTVALUE));
						
					} else if (edtf.getNodeName().equals(ELEMENT_LAYOUTDESIGN)) {
						continue;
					} else {
						log.log(MessageFormat.format(
							Messages.MessungKonfiguration_UnknownFieldType, edtf.getNodeName()),
							Log.ERRORS);
						continue;
					}
					
					String attr;
					attr = ATTR_LOW_ALERT;
					if (edtf.hasAttribute(attr)) {
						typ.setLowAlertValue(edtf.getAttribute(attr));
					}
					attr = ATTR_LOW_WARNING;
					if (edtf.hasAttribute(attr)) {
						typ.setLowWarningValue(edtf.getAttribute(attr));
					}
					attr = ATTR_HIGH_WARNING;
					if (edtf.hasAttribute(attr)) {
						typ.setHighWarningValue(edtf.getAttribute(attr));
					}
					attr = ATTR_HIGH_ALERT;
					if (edtf.hasAttribute(attr)) {
						typ.setHighAlertValue(edtf.getAttribute(attr));
					}
					
					Element formula = (Element) edtf.getElementsByTagName(ELEMENT_FORMULA).item(0);
					if (formula != null)
						typ.setFormula(formula.getTextContent(),
							formula.getAttribute(ATTR_INTERPRETER));
					
					NodeList children = edtf.getElementsByTagName(ELEMENT_VAR);
					if (children != null) {
						for (int k = 0; k < children.getLength(); k++) {
							Node n = children.item(k);
							if (n.getNodeType() != Node.ELEMENT_NODE) {
								continue;
							}
							Element var = (Element) n;
							typ.addVariable(var.getAttribute(ATTR_NAME),
								var.getAttribute(ATTR_SOURCE));
						}
					}
					dt.addField(typ);
				}
				types.add(dt);
			}
			return true;
			
		} catch (Error e) {
			log.log(Messages.MessungKonfiguration_ErrorReadXML + e.getMessage(), Log.ERRORS);
		} catch (SAXParseException e) {
			ExHandler.handle(e);
			SWTHelper.showError(
				Messages.MessungKonfiguration_ErrorInXML,
				MessageFormat.format(Messages.MessungKonfiguration_ErrorInXMLOnLine, path,
					e.getLineNumber(), e.getMessage()));
			log.log(
				Messages.MessungKonfiguration_ErrorReadXML
					+ MessageFormat.format(
						Messages.MessungKonfiguration_ErrorReadXMLFailure + e.getMessage(),
						e.getLineNumber()), Log.ERRORS);
		} catch (Exception e) {
			ExHandler.handle(e);
			log.log(Messages.MessungKonfiguration_ErrorReadXML + e.getMessage(), Log.ERRORS);
			
		}
		
		return false;
	}
	
	public ArrayList<MessungTyp> getTypes(){
		return types;
	}
	
	public MessungTyp getTypeByName(String name){
		for (MessungTyp t : types) {
			if (t.getName().compareTo(name) == 0) {
				return t;
			}
		}
		return null;
	}
}
