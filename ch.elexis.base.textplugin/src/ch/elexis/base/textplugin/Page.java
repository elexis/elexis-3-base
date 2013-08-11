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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class Page extends EStyledText implements MouseListener, MouseMoveListener, KeyListener,
		VerifyListener, PaintObjectListener {
	
	private boolean altDown;	
	private boolean mouseDown;	
	private Point mouseScreenLoc;	
	private Point mouseWidgetLoc;	
	protected List<TextBox> textBoxes;	
	private int state;	
	private boolean highlight;	
	private final List<Integer> offsets;	
	private final List<Control> controls;	
	private final static int STATE_NONE = 0;	
	private final static int STATE_MOVE = 1;	
	private final static int STATE_RESIZE_LEFT = 2;	
	private final static int STATE_RESIZE_RIGHT = 3;	
	private final static int STATE_RESIZE_UP = 4;	
	private final static int STATE_RESIZE_DOWN = 5;	
	private static final int MARGIN = 1;
	
	public Page(final Composite parent, final ElexisEditor editor){
		super(parent, editor, SWT.BORDER | SWT.WRAP);
		setSize(602, 800);
		textBoxes = new ArrayList<TextBox>();
		setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		addKeyListener(this);
		addMouseListener(this);
		addMouseMoveListener(this);
		mouseScreenLoc = mouseWidgetLoc = new Point(0, 0);
		addVerifyListener(this);
		addPaintObjectListener(this);
		
		offsets = new ArrayList<Integer>();
		controls = new ArrayList<Control>();
	}
	
	public TextBox insertBox(){
		// remove boxes temporary
		Composite comp = new Composite(this, SWT.NONE);
		for (Iterator<TextBox> it = textBoxes.iterator(); it.hasNext();) {
			TextBox old = it.next();
			old.setParent(comp);
		}
		TextBox box = new TextBox(this, editor);
		// add them back
		Control[] controlls = comp.getChildren();
		for (int i = controlls.length - 1; i >= 0; i--) {
			controlls[i].setParent(this);
		}
		comp.dispose();
		
		box.setHighlight(highlight);
		box.forceFocus();
		
		textBoxes.add(box);
		box.addKeyListener(this);
		box.addMouseListener(this);
		box.addMouseMoveListener(this);
		return box;
	}
	
	public void setHightlight(final boolean b){
		this.highlight = b;
		for (Iterator<TextBox> it = textBoxes.iterator(); it.hasNext();) {
			TextBox box = it.next();
			box.setHighlight(b);
		}
	}
	
	public void mouseDoubleClick(final MouseEvent e){}
	
	public void mouseDown(final MouseEvent e){
		mouseDown = true;
		mouseWidgetLoc = new Point(e.x, e.y);
		mouseScreenLoc = getDisplay().getCursorLocation();
	}
	
	public void mouseUp(final MouseEvent e){
		mouseDown = false;
	}
	
	public void mouseMove(final MouseEvent e){
		mouseWidgetLoc = new Point(e.x, e.y);
		
		Object o = e.getSource();
		Control control = (Control) o;
		
		if (mouseDown && (control instanceof TextBox)) {
			TextBox box = (TextBox) control;
			switch (state) {
			case STATE_MOVE:
				moveBox(box);
				break;
			case STATE_RESIZE_LEFT:
				resizeLeft(box);
				break;
			case STATE_RESIZE_RIGHT:
				resizeRight(box);
				break;
			case STATE_RESIZE_UP:
				resizeUp(box);
				break;
			case STATE_RESIZE_DOWN:
				resizeDown(box);
				break;
			}
		} else {
			updateState(control);
		}
		
	}
	
	private void resizeDown(final TextBox box){
		Point mouseNewLoc = getDisplay().getCursorLocation();
		
		int diff = mouseNewLoc.y - mouseScreenLoc.y;
		Rectangle bounds = box.getBounds();
		
		bounds.height += diff;
		if ((bounds.height > TextBox.MIN_SIZE) && (bounds.y + bounds.height < getSize().y)) {
			box.setBounds(bounds);
			mouseScreenLoc = mouseNewLoc;
		}
	}
	
	private void resizeUp(final TextBox box){
		Point mouseNewLoc = getDisplay().getCursorLocation();
		
		int diff = mouseNewLoc.y - mouseScreenLoc.y;
		Rectangle bounds = box.getBounds();
		
		bounds.y += diff;
		bounds.height -= diff;
		if ((bounds.y > 0) && (bounds.height > TextBox.MIN_SIZE)) {
			box.setBounds(bounds);
			mouseScreenLoc = mouseNewLoc;
		}
	}
	
	private void resizeRight(final TextBox box){
		Point mouseNewLoc = getDisplay().getCursorLocation();
		
		int diff = mouseNewLoc.x - mouseScreenLoc.x;
		Rectangle bounds = box.getBounds();
		
		bounds.width += diff;
		if ((bounds.width > TextBox.MIN_SIZE) && (bounds.x + bounds.width < getSize().x)) {
			box.setBounds(bounds);
			mouseScreenLoc = mouseNewLoc;
		}
	}
	
	private void resizeLeft(final TextBox box){
		Point mouseNewLoc = getDisplay().getCursorLocation();
		
		int diff = mouseNewLoc.x - mouseScreenLoc.x;
		Rectangle bounds = box.getBounds();
		
		bounds.x += diff;
		bounds.width -= diff;
		if ((bounds.x > 0) && (bounds.width > TextBox.MIN_SIZE)) {
			box.setBounds(bounds);
			mouseScreenLoc = mouseNewLoc;
		}
	}
	
	private void moveBox(final TextBox box){
		Point loc = box.getLocation();
		Point size = getSize();
		Point boxSize = box.getSize();
		Point mouseNewLoc = getDisplay().getCursorLocation();
		int x = Math.max(0, loc.x + mouseNewLoc.x - mouseScreenLoc.x);
		int y = Math.max(0, loc.y + mouseNewLoc.y - mouseScreenLoc.y);
		x = Math.min(size.x - boxSize.x, x);
		y = Math.min(size.y - boxSize.y, y);
		box.forceLocation(x, y);
		mouseScreenLoc = mouseNewLoc;
	}
	
	private void updateState(final Control control){
		
		if (altDown) {
			state = checkResize(control);
			if (state == STATE_NONE) {
				state = STATE_MOVE;
			}
		} else {
			state = STATE_NONE;
		}
		
		int cursor = SWT.CURSOR_ARROW;
		switch (state) {
		case STATE_MOVE:
			cursor = SWT.CURSOR_HAND;
			break;
		case STATE_RESIZE_LEFT:
		case STATE_RESIZE_RIGHT:
			cursor = SWT.CURSOR_SIZEW;
			break;
		case STATE_RESIZE_DOWN:
		case STATE_RESIZE_UP:
			cursor = SWT.CURSOR_SIZEN;
			break;
		}
		Cursor mouseCursor = getDisplay().getSystemCursor(cursor);
		
		for (Iterator<TextBox> it = textBoxes.iterator(); it.hasNext();) {
			TextBox box = it.next();
			box.setCursor(mouseCursor);
		}
	}
	
	private int checkResize(final Control control){
		int diff = control.getBorderWidth() + 4;
		if (mouseWidgetLoc.x < diff) {
			return STATE_RESIZE_LEFT;
		} else if (mouseWidgetLoc.x > control.getSize().x - 2 * diff) {
			return STATE_RESIZE_RIGHT;
		} else if (mouseWidgetLoc.y < diff) {
			return STATE_RESIZE_UP;
		} else if (mouseWidgetLoc.y > control.getSize().y - 2 * diff) {
			return STATE_RESIZE_DOWN;
		}
		return STATE_NONE;
	}
	
	public void keyPressed(final KeyEvent e){
		altDown = e.keyCode == SWT.ALT;
		updateState((Control) e.getSource());
	}
	
	public void keyReleased(final KeyEvent e){
		if (e.keyCode == SWT.ALT) {
			altDown = false;
		}
		updateState((Control) e.getSource());
	}
	
	public void clear(){
		setText("");
		for (Iterator<TextBox> it = textBoxes.iterator(); it.hasNext();) {
			TextBox box = it.next();
			box.dispose();
		}
		textBoxes.clear();
	}
	
	@Override
	public void readFrom(final DataInputStream in) throws IOException{
		clear();
		int boxesCount = in.readInt();
		super.readFrom(in);
		for (int i = 0; i < boxesCount; i++) {
			TextBox box = editor.insertBox();
			box.readFrom(in);
		}
	}
	
	@Override
	public void writeTo(final DataOutputStream out) throws IOException{
		out.writeInt(textBoxes.size());
		super.writeTo(out);
		for (Iterator<TextBox> it = textBoxes.iterator(); it.hasNext();) {
			TextBox box = it.next();
			box.writeTo(out);
		}
	}
	
	public void addTable(final Table table, final int offset){
		offsets.add(offset);
		controls.add(table);
		
		StyleRange style = new StyleRange();
		style.start = offset;
		style.length = 1;
		Rectangle rect = table.getBounds();
		int ascent = 2 * rect.height / 3;
		int descent = rect.height - ascent;
		style.metrics =
			new GlyphMetrics(ascent + MARGIN, descent + MARGIN, rect.width + 2 * MARGIN);
		setStyleRange(style);
		
		table.addListener(SWT.MouseWheel, new Listener() {
			public void handleEvent(final Event event){
				TableItem item = table.getItem(new Point(event.x, event.y));
				int x = event.x;
				int index = 0;
				TableColumn column = table.getColumn(index);
				int w = column.getWidth();
				while (x > w) {
					index++;
					if (index >= table.getColumnCount()) {
						return;
					}
					column = table.getColumn(index);
					w += column.getWidth();
				}
				
				int inc = (event.count > 0 ? 5 : -5);
				if (column.getWidth() + inc < 5) {
					return;
				}
				
				column.setWidth(column.getWidth() + inc);
				int rowcount = table.getItemCount();
				int columncount = table.getColumnCount();
				Rectangle rect = table.getItem(rowcount - 1).getBounds(columncount - 1);
				table.setSize(rect.x + rect.width + 5, rect.y + rect.height + 5);
				rect = table.getBounds();
				StyleRange style = new StyleRange();
				style.start = offset;
				style.length = 1;
				int ascent = 2 * rect.height / 3;
				int descent = rect.height - ascent;
				style.metrics =
					new GlyphMetrics(ascent + MARGIN, descent + MARGIN, rect.width + 2 * MARGIN);
				setStyleRange(style);
			}
		});
	}
	
	public void verifyText(final VerifyEvent e){
		int start = e.start;
		int replaceCharCount = e.end - e.start;
		int newCharCount = e.text.length();
		int index = 0;
		
		for (Iterator<Integer> it = offsets.iterator(); it.hasNext(); index++) {
			int offset = it.next();
			if ((start <= offset) && (offset < start + replaceCharCount)) {
				// this widget is being deleted from the text
				Control control = controls.get(index);
				if ((control != null) && !control.isDisposed()) {
					control.dispose();
					control = null;
				}
				offset = -1;
			}
			if ((offset != -1) && (offset >= start)) {
				offset += newCharCount - replaceCharCount;
			}
			offsets.set(index, offset);
		}
	}
	
	public void paintObject(final PaintObjectEvent event){
		StyleRange style = event.style;
		int start = style.start;
		int index = 0;
		for (Iterator<Integer> it = offsets.iterator(); it.hasNext(); index++) {
			int offset = it.next();
			if (start == offset) {
				Control control = controls.get(index);
				Point pt = control.getSize();
				int x = event.x + MARGIN;
				int y = event.y + event.ascent - 2 * pt.y / 3;
				control.setLocation(x, y);
				break;
			}
		}
	}
	
	private Rectangle centerRect(final Point containerSize, final Point childSize){
		int x = (containerSize.x - childSize.x) / 2;
		int y = (containerSize.y - childSize.y) / 2;
		Rectangle rect = new Rectangle(x, y, childSize.x, childSize.y);
		return rect;
	}
	
	public void print(final Printer printer, final GC gc){
		Rectangle clientArea = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point dpi = printer.getDPI();
		
		int leftMargin = dpi.x + trim.x; // one inch from left side of paper
		int rightMargin = clientArea.width - dpi.x + trim.x + trim.width; // one inch from right
		// side of paper
		int topMargin = dpi.y + trim.y; // one inch from top edge of paper
		int bottomMargin = clientArea.height - dpi.y + trim.y + trim.height; // one inch from
		// bottom edge of
		
		int x = leftMargin;
		int ex = leftMargin;
		int y = topMargin;
		StringBuffer line = new StringBuffer();
		FontMetrics fm = gc.getFontMetrics();
		
		List<StyleRange> styles = getStyles();
		for (Iterator it = styles.iterator(); it.hasNext();) {
			StyleRange style = (StyleRange) it.next();
			gc.setFont(new Font(printer, style.font.getFontData()[0].getName(), style.font
				.getFontData()[0].getHeight(), style.fontStyle));
			fm = gc.getFontMetrics();
			
			String text = getText(style.start, style.start + style.length - 1);
			List<String> words = split(text);
			
			Iterator<String> wit = words.iterator();
			String word = wit.hasNext() ? wit.next() : null;
			while (word != null) {
				if (word.equals("\r")) {
					gc.drawString(line.toString(), leftMargin, y);
					line.setLength(0);
					x = ex = leftMargin;
					y += fm.getHeight();
				}
				word = (ex == leftMargin ? word.trim() : word);
				ex += gc.stringExtent(word).x;
				if (ex < rightMargin) {
					line.append(word);
					word = wit.hasNext() ? wit.next() : null;
				} else {
					gc.drawString(line.toString(), x, y);
					line.setLength(0);
					x = ex = leftMargin;
					y += fm.getHeight();
				}
			}
			if (line.length() > 0) {
				gc.drawString(line.toString(), x, y);
				// x = gc.stringExtent(line.toString()).x;
				// y += fm.getHeight();
				x = ex;
				line.setLength(0);
			}
		}
	}
	
	private List<String> split(final String text){
		List<String> words = new ArrayList<String>();
		String delims = " \n\r";
		boolean previosWordIsLineReturn = false;
		for (StringTokenizer st = new StringTokenizer(text, delims, true); st.hasMoreTokens();) {
			String word = st.nextToken();
			if (word.equals("\n") || word.equals("\r")) {
				if (!previosWordIsLineReturn) {
					words.add(word);
					previosWordIsLineReturn = false;
				} else {
					previosWordIsLineReturn = true;
				}
			} else {
				words.add(word);
				previosWordIsLineReturn = false;
			}
		}
		return words;
	}
}
