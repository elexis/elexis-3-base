/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */

package ch.medelexis.templator.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Composite;
import org.jdom.Element;
import org.jdom.JDOMException;

import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.MimeTypeUtil;
import ch.elexis.core.ui.util.SWTHelper;
import ch.medelexis.templator.model.ProcessingSchema;
import ch.rgw.tools.ExHandler;

public class TextPluginImpl implements ITextPlugin {
	ProcessingSchemaDisplay schemaDisplay;
	ProcessingSchema schema;
	ICallback saveHandler;
	
	@Override
	public boolean clear(){
		return createEmptyDocument();
	}
	
	@Override
	public boolean createEmptyDocument(){
		schema = new ProcessingSchema();
		schemaDisplay.set(schema);
		return true;
	}
	
	@Override
	public void dispose(){
		if (schemaDisplay != null && !schemaDisplay.isDisposed()) {
			schemaDisplay.dispose();
		}
		schema = null;
	}
	
	@Override
	public boolean findOrReplace(String pattern, ReplaceCallback cb){
		boolean bHit = false;
		Pattern pat = Pattern.compile(pattern);
		for (Element field : schema.getFields()) {
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pat.matcher(field.getText());
			while (matcher.find()) {
				String found = matcher.group();
				bHit = true;
				matcher.appendReplacement(sb, (String) cb.replace(found));
			}
			matcher.appendTail(sb);
			field.setText(sb.toString());
		}
		schemaDisplay.set(schema);
		return bHit;
	}
	
	@Override
	public PageFormat getFormat(){
		return PageFormat.A4;
	}
	
	@Override
	public String getMimeType(){
		return MimeTypeUtil.MIME_TYPE_TEMPLATOR;
	}
	
	@Override
	public boolean insertTable(String place, int properties, String[][] contents, int[] columnSizes){
		StringBuffer sbu = new StringBuffer();
		for (int z = 0; z < contents.length; z++) {
			for (int s = 0; s < contents[z].length; s++) {
				sbu.append(contents[z][s]).append("\t");
			}
			sbu.append("\n");
		}
		String repl = sbu.toString();
		place = "\\[" + place.substring(1, place.length() - 1) + "\\]";
		Pattern pat = Pattern.compile(place);
		for (Element field : schema.getFields()) {
			sbu = new StringBuffer();
			Matcher matcher = pat.matcher(field.getText());
			while (matcher.find()) {
				matcher.appendReplacement(sbu, (String) repl);
			}
			matcher.appendTail(sbu);
			field.setText(sbu.toString());
		}
		schemaDisplay.set(schema);
		return true;
	}
	
	/*
	 * @Override public Object insertText(String marke, String text, int adjust) { Position pos =
	 * new Position(); pos.e = schema.getField(marke); pos.pos = 0; return insertText(pos, text,
	 * adjust); }
	 */
	public Object insertText(String place, String text, int adjust){
		place = "\\[" + place.substring(1, place.length() - 1) + "\\]";
		Pattern pat = Pattern.compile(place);
		Element ret = null;
		for (Element field : schema.getFields()) {
			StringBuffer sbu = new StringBuffer();
			Matcher matcher = pat.matcher(field.getText());
			while (matcher.find()) {
				matcher.appendReplacement(sbu, (String) text);
			}
			matcher.appendTail(sbu);
			field.setText(sbu.toString());
			ret = field;
		}
		return ret;
	}
	
	@Override
	public Object insertText(Object pos, String text, int adjust){
		Position p = (Position) pos;
		StringBuilder sb = new StringBuilder(p.e.getText());
		sb.insert(p.pos, text);
		p.pos += text.length();
		schemaDisplay.set(schema);
		return pos;
	}
	
	@Override
	public Object insertTextAt(int x, int y, int w, int h, String text, int adjust){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean loadFromByteArray(byte[] bs, boolean asTemplate){
		ByteArrayInputStream bais = new ByteArrayInputStream(bs);
		return loadFromStream(bais, asTemplate);
	}
	
	@Override
	public boolean loadFromStream(InputStream is, boolean asTemplate){
		try {
			schema = ProcessingSchema.load(is);
			
			schemaDisplay.set(schema);
			/*
			 * if(schema.getDirectOutput()){ schemaDisplay.save();
			 * schema.getProcessor().doOutput(schema); }
			 */
			return true;
		} catch (JDOMException e) {
			ExHandler.handle(e);
			// SWTHelper.alert("Fehler beim Parsen",
			// "Das Schema hat formale XML Fehler ");
		} catch (IOException e) {
			ExHandler.handle(e);
			SWTHelper.alert("Fehler beim Lesen", "Die Datei konnte nicht gelesen werden ");
		}
		return false;
	}
	
	@Override
	public boolean print(String toPrinter, String toTray, boolean waitUntilFinished){
		schemaDisplay.save();
		schema.getProcessor().doOutput(schema);
		return true;
	}
	
	@Override
	public void setFocus(){
		schemaDisplay.setFocus();
		
	}
	
	@Override
	public boolean setFont(String name, int style, float size){
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setFormat(PageFormat f){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setSaveOnFocusLost(boolean bSave){
		schemaDisplay.setSaveOnFocusLost(bSave);
	}
	
	@Override
	public boolean setStyle(int style){
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void showMenu(boolean b){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void showToolbar(boolean b){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public byte[] storeToByteArray(){
		schemaDisplay.collect();
		String s = schema.toXML();
		try {
			return s.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			// Will not happen
			return null;
		}
	}
	
	@Override
	public Composite createContainer(Composite parent, ICallback handler){
		schemaDisplay = new ProcessingSchemaDisplay(parent, handler);
		saveHandler = handler;
		return schemaDisplay;
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException{
		// TODO Auto-generated method stub
		
	}
	
	class Position {
		Element e;
		int pos;
	}
	
	@Override
	public boolean isDirectOutput(){
		if (schema != null) {
			return schema.getDirectOutput();
		}
		return false;
	}
	
	@Override
	public void setParameter(Parameter parameter){
		// TODO Auto-generated method stub
		
	}
}
