/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    bogdan314 - initial implementation
 * Sponsor: 
 *    G. Weirich
 ******************************************************************************/
package ch.elexis.base.textplugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.data.interfaces.text.ReplaceCallback;
import ch.elexis.core.ui.text.ITextPlugin;

public class ElexisTextPlugin implements ITextPlugin {
	
	private ElexisEditor editor;
	private boolean showToolbar = true;
	private PageFormat pageFormat;
	private String font;
	private int style;
	private float size;
	public static ElexisTextPlugin tempInstance;
	
	public ElexisTextPlugin(){
		tempInstance = this;
	}
	
	@SuppressWarnings("unused")
	private Parameter textParameter = null;
	
	public void setParameter(Parameter parameter){
		textParameter = parameter;
	}
	
	public boolean clear(){
		if (editor != null) {
			editor.page.clear();
		}
		return true;
	}
	
	public Composite createContainer(final Composite parent, final ICallback handler){
		if (editor == null) {
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout grid = new GridLayout();
			grid.numColumns = 1;
			composite.setLayout(grid);
			
			editor = new ElexisEditor(composite, handler);
			GridData spec = new GridData();
			spec.horizontalAlignment = GridData.FILL;
			spec.grabExcessHorizontalSpace = true;
			spec.verticalAlignment = GridData.FILL;
			spec.grabExcessVerticalSpace = true;
			editor.setLayoutData(spec);
			
			showToolbar(showToolbar);
			
			return composite;
		} else {
			return editor.getParent();
		}
	}
	
	public boolean createEmptyDocument(){
		return clear();
	}
	
	public String getMimeType(){
		return "Mime-Type";
	}
	
	public boolean insertTable(final String place, final int properties, final String[][] contents,
		final int[] columnSizes){
		
		if (editor == null) {
			return false;
		}
		
		Pattern pattern = Pattern.compile(place);
		String text = editor.page.getText();
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find()) {
			return false;
		}
		
		editor.insertTable(matcher.start(), matcher.end(), contents,
			(properties & FIRST_ROW_IS_HEADER) != 0, (properties & GRID_VISIBLE) != 0, font,
			(int) size, style);
		
		return false;
	}
	
	public Object insertText(final String marke, final String text, final int adjust){
		if (editor == null) {
			return false;
		}
		return findOrReplace(marke, new ReplaceCallback() {
			public String replace(final String in){
				return text;
			}
		}, true);
	}
	
	public Object insertText(final Object pos, final String text, final int adjust){
		if ((editor == null) || !(pos instanceof Pos)) {
			return false;
		}
		Pos pospos = (Pos) pos;
		try {
			pospos.text.setCaretOffset(pospos.caret);
			pospos.text.insert(text);
			StyleRange original = pospos.text.getStyleRangeAtOffset(pospos.caret);
			if (original == null) {
				original = new StyleRange();
			}
			StyleRange style = (StyleRange) original.clone();
			style.start = pospos.caret;
			style.length = text.length();
			style.font =
				font != null ? new Font(editor.getDisplay(), font, (int) size, this.style) : null;
			style.fontStyle = this.style != 0 ? this.style : style.fontStyle;
			pospos.text.setStyleRange(style);
			
			Pos p = new Pos();
			p.text = pospos.text;
			p.caret = pospos.caret + text.length();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return null;
	}
	
	public Object insertTextAt(final int x, final int y, final int w, final int h,
		final String text, final int adjust){
		TextBox box = editor.insertBox(x, y, w, h);
		box.setText(text);
		return new Pos(box, text.length());
	}
	
	public boolean loadFromStream(final InputStream is, final boolean asTemplate){
		return false;
	}
	
	public boolean print(final String toPrinter, final String toTray,
		final boolean waitUntilFinished){
		return false;
	}
	
	public boolean setFont(final String name, final int style, final float size){
		this.font = name;
		this.style = style;
		this.size = size;
		return true;
	}
	
	public boolean setStyle(final int style){
		this.style = style;
		return true;
	}
	
	public void showMenu(final boolean b){}
	
	public void showToolbar(boolean b){
		if (editor != null) {
			editor.toolBar.setVisible(b);
			GridData data = (GridData) editor.toolBar.getLayoutData();
			data.exclude = !b;
			editor.layout();
		} else {
			showToolbar = b;
		}
	}
	
	public byte[] storeToByteArray(){
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(bout);
			
			editor.page.writeTo(out);
			
			bout.close();
			out.close();
			return bout.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			return new byte[0];
		}
	}
	
	public boolean loadFromByteArray(final byte[] bs, final boolean asTemplate){
		ByteArrayInputStream bin = new ByteArrayInputStream(bs);
		DataInputStream in = new DataInputStream(bin);
		try {
			editor.page.readFrom(in);
			
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean findOrReplace(final String pattern, final ReplaceCallback cb){
		return findOrReplace(pattern, cb, false) != null;
	}
	
	private Pos findOrReplace(final String pattern, final ReplaceCallback cb,
		final boolean firstTimeOnly){
		// carefull, might throw: PatternSyntaxException
		if (editor != null) {
			Pattern regexp = Pattern.compile(pattern);
			Pos result = null;
			
			result = findOrReplace(regexp, editor.page, cb, firstTimeOnly);
			if ((result != null) && ((cb == null) || firstTimeOnly)) {
				// no reason to keep searching
				return result;
			}
			
			for (Iterator<TextBox> it = editor.page.textBoxes.iterator(); it.hasNext();) {
				TextBox box = it.next();
				result = findOrReplace(regexp, box, cb, firstTimeOnly);
				if ((result != null) && ((cb == null) || firstTimeOnly)) {
					// no reason to keep searching
					return result;
				}
			}
			return result;
		}
		return null;
	}
	
	private Pos findOrReplace(final Pattern pattern, final StyledText styledText,
		final ReplaceCallback callback, final boolean firstTimeOnly){
		String text = styledText.getText();
		Matcher matcher = pattern.matcher(text);
		if (!matcher.find()) {
			return null;
		}
		int diff = 0;
		do {
			int start = matcher.start();
			int end = matcher.end();
			String str = text.substring(start, end);
			if (callback != null) {
				String replace = (String) callback.replace(str);
				StyleRange style = styledText.getStyleRangeAtOffset(start + diff);
				style = (StyleRange) style.clone();
				if (firstTimeOnly) {
					// in this case, an insertion is made, so set the new style to the cached one
					style = (StyleRange) style.clone();
					style.fontStyle = (this.style != 0 ? this.style : style.fontStyle);
					if (font != null) {
						style.font =
							new Font(editor.getDisplay(), this.font, (int) size, this.style);
					}
				}
				styledText.replaceTextRange(start + diff, end - start, replace);
				style.start = start + diff;
				style.length = replace.length();
				styledText.setStyleRange(style);
				diff += replace.length() - end + start;
				if (firstTimeOnly) {
					return new Pos(styledText, end + diff);
				}
			} else {
				// no reason to keep searching
				return new Pos(styledText, end);
			}
			
		} while (matcher.find());
		return new Pos();
	}
	
	public PageFormat getFormat(){
		return pageFormat;
	}
	
	public void setFocus(){
		if (editor != null) {
			editor.page.forceFocus();
		}
	}
	
	public void setFormat(final PageFormat f){
		this.pageFormat = f;
	}
	
	public void dispose(){
		
	}
	
	static class Pos {
		StyledText text;
		int caret;
		
		public Pos(){}
		
		public Pos(final StyledText text, final int caret){
			this.text = text;
			this.caret = caret;
		}
	}
	
	public void setInitializationData(final IConfigurationElement config,
		final String propertyName, final Object data) throws CoreException{
		// TODO Auto-generated method stub
		
	}
	
	public void setSaveOnFocusLost(final boolean bSave){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isDirectOutput(){
		return false;
	}
	
	@Override
	public void initTemplatePrintSettings(String template){
		// TODO Auto-generated method stub
		
	}
}
