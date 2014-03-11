/*******************************************************************************
 * Copyright (c) 2009, A. Kaufmann and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    A. Kaufmann - initial implementation 
 *    
 *******************************************************************************/

package com.hilotec.elexis.pluginstatistiken.config;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ch.elexis.core.ui.util.Log;

import com.hilotec.elexis.pluginstatistiken.Datensatz;
import com.hilotec.elexis.pluginstatistiken.PluginstatistikException;

/**
 * Where-Klausel fuer eine Abfrage. (Eigentlich allgemein Bedingungsklauseln, wird beispielsweise
 * auch fuer die Join-Bedingung benutzt).
 * 
 * @author Antoine Kaufmann
 */
public class KonfigurationWhere {
	private Element element;
	
	public static final String ELEM_OR = "or";
	public static final String ELEM_AND = "and";
	public static final String ELEM_NOT = "not";
	public static final String ELEM_EQUAL = "equal";
	public static final String ELEM_GREATERTHAN = "greaterthan";
	public static final String ELEM_LESSTHAN = "lessthan";
	public static final String ATTR_A = "a";
	public static final String ATTR_B = "b";
	public static final String REGEX_PLUGINREF = "^\\[.*\\]$";
	
	enum ElementTyp {
		E_INVALID,
		
		E_NOT, E_AND, E_OR,
		
		E_EQUAL, E_GREATERTHAN, E_LESSTHAN,
	};
	
	/**
	 * Neue Where-Klausel erstellen
	 * 
	 * @param e
	 *            DOM—Element der Klausel
	 */
	public KonfigurationWhere(Element e){
		element = e;
	}
	
	/**
	 * Interne Repraesentation des Typs anhand des DOM-Elements ausfindig machen.
	 */
	private ElementTyp getTyp(Element e){
		String n = e.getTagName();
		if (n.equals(ELEM_AND)) {
			return ElementTyp.E_AND;
		} else if (n.equals(ELEM_OR)) {
			return ElementTyp.E_OR;
		} else if (n.equals(ELEM_NOT)) {
			return ElementTyp.E_NOT;
		} else if (n.equals(ELEM_EQUAL)) {
			return ElementTyp.E_EQUAL;
		} else if (n.equals(ELEM_GREATERTHAN)) {
			return ElementTyp.E_GREATERTHAN;
		} else if (n.equals(ELEM_LESSTHAN)) {
			return ElementTyp.E_LESSTHAN;
		}
		
		return ElementTyp.E_INVALID;
	}
	
	/**
	 * Hilfsfunktion um eine Liste aller Kindelemente eines DOM-Elements abzurufen (Nimmt nur die
	 * ELEMENT_NODE aus get Childnodes).
	 */
	private List<Element> getChildElements(Element parent){
		ArrayList<Element> l = new ArrayList<Element>();
		
		NodeList nl = parent.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				l.add((Element) n);
			}
		}
		return l;
	}
	
	/**
	 * Wert eines Attributs eines DOM-Elements auslesen. Wenn dabei Referenzen auf Felder in eckigen
	 * Klammern enthalten sind, werden diese automatisch ersetzt.
	 * 
	 * @param e
	 *            Element
	 * @param name
	 *            Name des Attributs
	 * @param ds
	 *            Datensatz
	 * 
	 * @return Wert des Attributs
	 * @throws PluginstatistikException
	 */
	private String attrValue(Element e, String name, Datensatz ds) throws PluginstatistikException{
		String val = e.getAttribute(name);
		// Wenn es sich um Verweise auf Feldnamen handelt, muessen wir
		// die erst aufloesen
		if (val.matches(REGEX_PLUGINREF)) {
			String feld = ds.getFeld(val.substring(1, val.length() - 1));
			if (feld == null) {
				throw new PluginstatistikException("Ungueltige Referenz: '" + "'");
			} else {
				val = feld;
			}
		}
		return val;
	}
	
	/**
	 * Hilfsfunktion, die prueft ob es sich bei einem String um einen rein numerischen Wert handelt.
	 */
	private boolean isNum(String val){
		try {
			Double.parseDouble(val);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	/**
	 * Hilfsfunktion fuers Abarbeiten der XML-Repraesentation einer Where- Klausel fuer einen
	 * bestimmten Datensatz. (Der ganze Kram wird rekursiv geparst)
	 * 
	 * @param e
	 *            Aktuelles DOM-Element das gerade verarbeitet wird.
	 * @throws PluginstatistikException
	 */
	private boolean matchesElement(Element e, Datensatz ds) throws PluginstatistikException{
		ElementTyp typ = getTyp(e);
		List<Element> children;
		String a, b;
		Double da, db;
		
		switch (typ) {
		case E_AND:
			children = getChildElements(e);
			for (int i = 0; i < children.size(); i++) {
				if (!matchesElement(children.get(i), ds)) {
					return false;
				}
			}
			return true;
			
		case E_OR:
			children = getChildElements(e);
			for (int i = 0; i < children.size(); i++) {
				if (matchesElement(children.get(i), ds)) {
					return true;
				}
			}
			return false;
			
		case E_NOT:
			return !matchesElement(getChildElements(e).get(0), ds);
			
		case E_EQUAL:
			a = attrValue(e, ATTR_A, ds);
			b = attrValue(e, ATTR_B, ds);
			
			// Wenn es sich um Zahlen handelt, koennte ein einfacher
			// Stringvergleich nicht das erwuenschte Ergebnis bringen
			if (isNum(a) && isNum(b)) {
				return (Double.parseDouble(a) == Double.parseDouble(b));
			}
			return a.equals(b);
			
		case E_GREATERTHAN:
		case E_LESSTHAN:
			a = attrValue(e, ATTR_A, ds);
			b = attrValue(e, ATTR_B, ds);
			
			da = Double.parseDouble(a);
			db = Double.parseDouble(b);
			if ((typ == ElementTyp.E_GREATERTHAN) && (da > db)) {
				return true;
			} else if ((typ == ElementTyp.E_LESSTHAN) && (da < db)) {
				return true;
			}
			return false;
			
		case E_INVALID:
		default:
			Log.get("Messwertstatistiken").log("Ungueltige Operation: " + e.getTagName(),
				Log.ERRORS);
			return false;
		}
	}
	
	/**
	 * Prueft ob der Datensatz zur Where-Klausel passt
	 * 
	 * @throws PluginstatistikException
	 */
	public boolean matches(Datensatz ds) throws PluginstatistikException{
		return matchesElement(element, ds);
	}
}
