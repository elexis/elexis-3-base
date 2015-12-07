/*******************************************************************************
 * Copyright (c) 2007-2013, G. Weirich and Elexis
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.jdt.NonNull;

public class EStyledText extends StyledText implements FocusListener {
	
	protected ElexisEditor editor;
	
	public EStyledText(Composite parent, ElexisEditor editor, int style){
		super(parent, style);
		this.editor = editor;
		addFocusListener(this);
	}
	
	public void cut(){
		editor.handleCutCopy(this);
		super.cut();
	}
	
	public void copy(){
		editor.handleCutCopy(this);
		super.copy();
	}
	
	public void focusGained(FocusEvent e){
		editor.setSelectedText(this);
	}
	
	public void focusLost(FocusEvent e){}
	
	public void readFrom(DataInputStream in) throws IOException{
		setText(in.readUTF());
		int stylesCount = in.readInt();
		for (int i = 0; i < stylesCount; i++) {
			StyleRange style = new StyleRange();
			style.start = in.readInt();
			style.length = in.readInt();
			style.fontStyle = in.readInt();
			style.underline = in.readBoolean();
			boolean isFont = in.readBoolean();
			if (isFont) {
				style.font = new Font(getDisplay(), in.readUTF(), in.readInt(), style.fontStyle);
			}
			
			setStyleRange(style);
		}
	}
	
	public void writeTo(DataOutputStream out) throws IOException{
		out.writeUTF(getText());
		List<StyleRange> styles = getStyles();
		out.writeInt(styles.size());
		for (Iterator<StyleRange> it = styles.iterator(); it.hasNext();) {
			StyleRange style = it.next();
			out.writeInt(style.start);
			out.writeInt(style.length);
			out.writeInt(style.fontStyle);
			out.writeBoolean(style.underline);
			Font font = style.font;
			if (font != null && font.getFontData() != null && font.getFontData().length > 0) {
				out.writeBoolean(true);
				FontData fd = font.getFontData()[0];
				out.writeUTF(fd.getName());
				out.writeInt(fd.getHeight());
			} else {
				out.writeBoolean(false);
			}
		}
		
	}
	
	protected List<StyleRange> getStyles(){
		List<StyleRange> result = new ArrayList<StyleRange>();
		StyleRange[] styles = getStyleRanges();
		
		boolean same = true;
		int index = 0;
		int start = 0;
		int len = 0;
		StyleRange current = styles.length > 0 ? styles[0] : null;
		
		while (index < styles.length) {
			index++;
			if (index < styles.length) {
				StyleRange style = styles[index];
				if (current!=null && sameStyle(current, style)) {
					current.length += style.length;
				} else {
					result.add(current);
					current = style;
				}
			}
		}
		if (current != null) {
			result.add(current);
		}
		return result;
	}
	
	protected boolean sameStyle(@NonNull StyleRange s1, @NonNull StyleRange s2){
		if (s1.fontStyle != s2.fontStyle || s1.underline != s2.underline) {
			return false;
		}
		if (s1.font != null && s2.font != null) {
			if (s1.font.getFontData() != null && s2.font.getFontData() != null) {
				FontData fd1 = s1.font.getFontData().length > 0 ? s1.font.getFontData()[0] : null;
				FontData fd2 = s2.font.getFontData().length > 0 ? s2.font.getFontData()[0] : null;
				if (fd1 != null && fd2 != null) {
					return fd1.height == fd2.height && fd1.getName().equals(fd2.getName());
				} else {
					return fd1 == fd2;
				}
			} else {
				return s1.font.getFontData() == s2.font.getFontData();
			}
		} else {
			return s1.font == s2.font;
		}
	}
}
