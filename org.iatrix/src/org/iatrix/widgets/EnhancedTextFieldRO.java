/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    G. Weirich - initial implementation
 *    D. Lutz    - simplified read-only version
 * 
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * Ein StyledText mit erweiterten Eigenschaften. Kann XML-Dokumente von SAmDaS-Typ lesen. Aus
 * Kompatibiltätsgründen können auch reine Texteinträge gelesen werden, werden beim Speichern aber
 * nach XML gewandelt.
 * 
 * @author Gerry
 * 
 */
public class EnhancedTextFieldRO extends Composite {
	StyledText text;
	
	List<Samdas.XRef> links;
	List<Samdas.Markup> markups;
	List<Samdas.Range> ranges;
	Samdas samdas;
	Samdas.Record record;
	
	private static Pattern outline = Pattern.compile("^\\S+:", Pattern.MULTILINE);
	private static Pattern bold = Pattern.compile("\\*\\S+\\*");
	private static Pattern italic = Pattern.compile("\\/\\S+\\/");
	private static Pattern underline = Pattern.compile("_\\S+_");
	
	public EnhancedTextFieldRO(Composite parent){
		super(parent, SWT.NONE);
		setLayout(new TableWrapLayout());
		text = new StyledText(this, SWT.WRAP | SWT.READ_ONLY);
		text.setLayoutData(SWTHelper.getFillTableWrapData(1, true, 1, true));
		text.setWordWrap(true);
	}
	
	/**
	 * Text formatieren (d.h. Style-Ranges erstellen. Es wird unterschieden zwischen dem KG-Eintrag
	 * alten Stils und dem neuen XML-basierten format.
	 */
	void doFormat(String tx){
		text.setStyleRange(null);
		if (tx.startsWith("<")) {
			doFormatXML(tx);
			tx = text.getText();
		} else {
			samdas = new Samdas(tx);
			record = samdas.getRecord();
			text.setText(tx);
		}
		
		// Überschriften formatieren
		
		// obsoleted by markups!
		Matcher matcher = outline.matcher(tx);
		while (matcher.find() == true) {
			StyleRange n = new StyleRange();
			n.start = matcher.start();
			n.length = matcher.end() - n.start;
			n.fontStyle = SWT.BOLD;
			text.setStyleRange(n);
		}
		
		matcher = bold.matcher(tx);
		while (matcher.find() == true) {
			StyleRange n = new StyleRange();
			n.start = matcher.start();
			n.length = matcher.end() - n.start;
			n.fontStyle = SWT.BOLD;
			text.setStyleRange(n);
		}
		matcher = italic.matcher(tx);
		while (matcher.find() == true) {
			StyleRange n = new StyleRange();
			n.start = matcher.start();
			n.length = matcher.end() - n.start;
			n.fontStyle = SWT.ITALIC;
			text.setStyleRange(n);
		}
		
		matcher = underline.matcher(tx);
		while (matcher.find() == true) {
			StyleRange n = new StyleRange();
			n.start = matcher.start();
			n.length = matcher.end() - n.start;
			n.underline = true;
			text.setStyleRange(n);
		}
		// Obsoleted, do not rely
	}
	
	@SuppressWarnings("unchecked")
	void doFormatXML(String tx){
		samdas = new Samdas(tx);
		record = samdas.getRecord();
		List<Samdas.XRef> xrefs = record.getXrefs();
		text.setText(record.getText());
		int textlen = text.getCharCount();
		markups = record.getMarkups();
		links = new ArrayList<Samdas.XRef>(xrefs.size());
		ranges = new ArrayList<Samdas.Range>(xrefs.size() + markups.size());
		for (Samdas.Markup m : markups) {
			String type = m.getType();
			StyleRange n = new StyleRange();
			n.start = m.getPos();
			n.length = m.getLength();
			if (type.equalsIgnoreCase("emphasized")) {
				n.strikeout = true;
			} else if (type.equalsIgnoreCase("bold")) {
				n.fontStyle = SWT.BOLD;
			} else if (type.equalsIgnoreCase("italic")) {
				n.fontStyle = SWT.ITALIC;
			} else if (type.equalsIgnoreCase("underlined")) {
				n.underline = true;
			}
			if ((n.start + n.length) > textlen) {
				n.length = textlen - n.start;
			}
			if ((n.length > 0) && (n.start >= 0)) {
				text.setStyleRange(n);
				ranges.add(m);
			} else {
				// fehlerhaftes Markup entfernen.
				record.remove(m);
			}
			
		}
		
		/*
		 * for(Samdas.XRef xref:xrefs){ IKonsExtension xProvider=hXrefs.get(xref.getProvider());
		 * if(xProvider==null){ continue; } StyleRange n=new StyleRange(); n.start=xref.getPos();
		 * n.length=xref.getLength();
		 * if(xProvider.doLayout(n,xref.getProvider(),xref.getID())==true){ links.add(xref); }
		 * 
		 * if((n.start+n.length)>text.getCharCount()){ n.length=text.getCharCount()-n.start; }
		 * if((n.length>0) && (n.start>=0)){ text.setStyleRange(n); ranges.add(xref); }else{
		 * xref.setPos(0); } }
		 */
	}
	
	/**
	 * Liefert den Inhalt des Textfields als jdom-Document zurück
	 */
	private Document getDocument(){
		record.setText(text.getText());
		// StyleRange[] rgs=text.getStyleRanges();
		return samdas.getDocument();
	}
	
	/**
	 * Liefert den Inhalt des Textfelds als XML-Text zurück
	 */
	private String getContentsAsXML(){
		XMLOutputter xo = new XMLOutputter(Format.getRawFormat());
		return xo.outputString(getDocument());
	}
	
	/**
	 * Markup erstellen
	 * 
	 * @param type
	 *            '*' bold, '/' italic, '_', underline
	 */
	public void createMarkup(char type, int pos, int len){
		String typ = "bold";
		switch (type) {
		case '/':
			typ = "italic";
			break;
		case '_':
			typ = "underline";
			break;
		}
		Samdas.Markup markup = new Samdas.Markup(pos, len, typ);
		record.add(markup);
		doFormat(getContentsAsXML());
	}
	
	/**
	 * Den Text mit len zeichen ab start durch nt ersetzen
	 */
	public void replace(int start, int len, String nt){
		text.replaceTextRange(start, len, nt);
	}
	
	public void setText(String ntext){
		doFormat(ntext);
	}
	
	/**
	 * Liefert das zugrundeliegende Text-Control zurueck
	 * 
	 * @return das zugrundeliegende Text-Control
	 */
	public Control getControl(){
		return text;
	}
}
