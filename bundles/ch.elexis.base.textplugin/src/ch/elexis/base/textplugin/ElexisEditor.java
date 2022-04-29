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

import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.ITextPlugin.ICallback;

/**
 * @author bogdan314
 */
public class ElexisEditor extends Composite implements ExtendedModifyListener {
	protected Page page;
	private boolean isBold;
	private boolean isItalic;
	private boolean isUnderline;
	private String fontName = "Arial";
	private int fontHeight = 10;
	protected ToolBar toolBar;
	private ToolItem newBoxToolItem;
	private ToolItem highlightToolItem;
	private ToolItem deleteToolItem;
	private ToolItem boldToolItem;
	private ToolItem italicToolItem;
	private ToolItem underlineToolItem;
	private ToolItem cutToolItem;
	private ToolItem copyToolItem;
	private ToolItem pasteToolItem;
	private ToolItem saveToolItem;
	private ToolItem saveAsToolItem;
	private ToolItem printToolItem;
	private Combo fontCombo;
	private Combo fontHeigtCombo;
	private final Listener caretListener;
	private StyledText lastSelectedText;
	private final Vector<StyleRange> cachedStyles = new Vector<StyleRange>();
	private final ImageRegistry imageRegistry = UiDesk.getImageRegistry();

	private final ICallback handler;

	protected ElexisEditor(final Composite parent, final ICallback handler) {
		super(parent, SWT.NONE);

		this.handler = handler;
		GridLayout layout = new GridLayout();
		setLayout(layout);

		caretListener = new Listener() {
			public void handleEvent(final Event event) {
				caretUpdate(event);
			}
		};

		checkImages();

		createToolbar();
		createPage();

		layout();
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	private ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.base.textplugin", path); //$NON-NLS-1$
	}

	private void checkImages() {
		if (imageRegistry.get("new-box") == null) {
			imageRegistry.put("new-box", getImageDescriptor("icons/new-box.gif"));
		}

		if (imageRegistry.get("italic") == null) {
			imageRegistry.put("italic", getImageDescriptor("icons/italic.gif"));
		}

		if (imageRegistry.get("bold") == null) {
			imageRegistry.put("bold", getImageDescriptor("icons/bold.gif"));
		}

		if (imageRegistry.get("underline") == null) {
			imageRegistry.put("underline", getImageDescriptor("icons/underline.gif"));
		}

		if (imageRegistry.get("font") == null) {
			imageRegistry.put("font", getImageDescriptor("icons/font.gif"));
		}

		if (imageRegistry.get("highlight-boxes") == null) {
			imageRegistry.put("highlight-boxes", getImageDescriptor("icons/highlight-boxes.gif"));
		}

		if (imageRegistry.get("delete") == null) {
			imageRegistry.put("delete", getImageDescriptor("icons/delete.gif"));
		}

		if (imageRegistry.get("paste") == null) {
			imageRegistry.put("paste", getImageDescriptor("icons/paste.gif"));
		}

		if (imageRegistry.get("copy") == null) {
			imageRegistry.put("copy", getImageDescriptor("icons/copy.gif"));
		}

		if (imageRegistry.get("cut") == null) {
			imageRegistry.put("cut", getImageDescriptor("icons/cut.gif"));
		}

		if (imageRegistry.get("save") == null) {
			imageRegistry.put("save", getImageDescriptor("icons/save.gif"));
		}

		if (imageRegistry.get("saveas") == null) {
			imageRegistry.put("saveas", getImageDescriptor("icons/saveas.gif"));
		}

		if (imageRegistry.get("print") == null) {
			imageRegistry.put("print", getImageDescriptor("icons/printer.png"));
		}
	}

	private void createPage() {
		ScrolledComposite scrollable = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		page = new Page(scrollable, this);
		page.setHightlight(true);
		page.forceFocus();
		page.addExtendedModifyListener(this);
		addCaretListener(page);
		scrollable.setContent(page);

		// size
		GridData data = new GridData(602, 350);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		scrollable.setLayoutData(data);

		lastSelectedText = page;
		updateControls();
	}

	private void addCaretListener(final StyledText text) {
		text.addListener(SWT.MouseDown, caretListener);
		text.addListener(SWT.KeyDown, caretListener);
	}

	private void createToolbar() {
		toolBar = new ToolBar(this, SWT.NONE);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		toolBar.setLayoutData(data);

		newBoxToolItem = new ToolItem(toolBar, SWT.PUSH);
		newBoxToolItem.setToolTipText("Insert new text-box");
		newBoxToolItem.setImage(imageRegistry.get("new-box"));

		deleteToolItem = new ToolItem(toolBar, SWT.PUSH);
		deleteToolItem.setToolTipText("Delete current text-box");
		deleteToolItem.setImage(imageRegistry.get("delete"));

		highlightToolItem = new ToolItem(toolBar, SWT.CHECK);
		highlightToolItem.setToolTipText("Highlight text-boxes");
		highlightToolItem.setSelection(true);
		highlightToolItem.setImage(imageRegistry.get("highlight-boxes"));

		new ToolItem(toolBar, SWT.SEPARATOR);

		saveToolItem = new ToolItem(toolBar, SWT.PUSH);
		saveToolItem.setToolTipText("Save");
		saveToolItem.setImage(imageRegistry.get("save"));

		saveAsToolItem = new ToolItem(toolBar, SWT.PUSH);
		saveAsToolItem.setToolTipText("Save As");
		saveAsToolItem.setImage(imageRegistry.get("saveas"));

		printToolItem = new ToolItem(toolBar, SWT.PUSH);
		printToolItem.setToolTipText("Print");
		printToolItem.setImage(imageRegistry.get("print"));

		new ToolItem(toolBar, SWT.SEPARATOR);

		boldToolItem = new ToolItem(toolBar, SWT.CHECK);
		boldToolItem.setToolTipText("Bold");
		boldToolItem.setImage(imageRegistry.get("bold"));

		italicToolItem = new ToolItem(toolBar, SWT.CHECK);
		italicToolItem.setToolTipText("Italic");
		italicToolItem.setImage(imageRegistry.get("italic"));

		underlineToolItem = new ToolItem(toolBar, SWT.CHECK);
		underlineToolItem.setToolTipText("Underline");
		underlineToolItem.setImage(imageRegistry.get("underline"));

		ToolItem sep = new ToolItem(toolBar, SWT.SEPARATOR);

		fontCombo = new Combo(toolBar, SWT.READ_ONLY);
		fontCombo.setToolTipText("Font");
		FontData[] fonts = getDisplay().getFontList(null, true);
		Set<String> uniqueFonts = new TreeSet<String>();
		for (int i = 0; i < fonts.length; i++) {
			FontData fd = fonts[i];
			uniqueFonts.add(fd.getName());
		}
		for (Iterator<String> it = uniqueFonts.iterator(); it.hasNext();) {
			String name = it.next();
			fontCombo.add(name);
		}

		fontCombo.pack();
		sep.setWidth(fontCombo.getSize().x);
		sep.setControl(fontCombo);

		new ToolItem(toolBar, SWT.SEPARATOR);

		fontHeigtCombo = new Combo(toolBar, SWT.READ_ONLY);
		int[] height = new int[] { 8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 72 };
		for (int i = 0; i < height.length; i++) {
			fontHeigtCombo.add(String.valueOf(height[i]));
		}

		fontHeigtCombo.pack();
		ToolItem sep2 = new ToolItem(toolBar, SWT.SEPARATOR);
		sep2.setWidth(fontHeigtCombo.getSize().x);
		sep2.setControl(fontHeigtCombo);

		new ToolItem(toolBar, SWT.SEPARATOR);

		cutToolItem = new ToolItem(toolBar, SWT.PUSH);
		cutToolItem.setToolTipText("Cut");
		cutToolItem.setImage(imageRegistry.get("cut"));

		copyToolItem = new ToolItem(toolBar, SWT.PUSH);
		copyToolItem.setToolTipText("Copy");
		copyToolItem.setImage(imageRegistry.get("copy"));

		pasteToolItem = new ToolItem(toolBar, SWT.PUSH);
		pasteToolItem.setToolTipText("Paste");
		pasteToolItem.setImage(imageRegistry.get("paste"));

		Listener listener = new Listener() {
			public void handleEvent(Event e) {
				if (e.widget == highlightToolItem) {
					page.setHightlight(highlightToolItem.getSelection());
				} else if (e.widget == newBoxToolItem) {
					insertBox();
				} else if (e.widget == deleteToolItem) {
					deleteBox();
				} else if (e.widget == boldToolItem) {
					isBold = !isBold;
					updateStylesOnText();
				} else if (e.widget == italicToolItem) {
					isItalic = !isItalic;
					updateStylesOnText();
				} else if (e.widget == underlineToolItem) {
					isUnderline = !isUnderline;
					updateStylesOnText();
				} else if (e.widget == cutToolItem) {
					lastSelectedText.cut();
				} else if (e.widget == copyToolItem) {
					lastSelectedText.copy();
					testInsertTable();
				} else if (e.widget == pasteToolItem) {
					lastSelectedText.paste();
				} else if (e.widget == fontCombo) {
					changeFont();
				} else if (e.widget == fontHeigtCombo) {
					changeFontHeight();
				} else if (e.widget == saveToolItem) {
					if (handler != null) {
						handler.save();
					}
				} else if (e.widget == saveAsToolItem) {
					if (handler != null) {
						handler.saveAs();
					}
				} else if (e.widget == printToolItem) {
					print();
				}
			}
		};

		highlightToolItem.addListener(SWT.Selection, listener);
		newBoxToolItem.addListener(SWT.Selection, listener);
		deleteToolItem.addListener(SWT.Selection, listener);
		boldToolItem.addListener(SWT.Selection, listener);
		italicToolItem.addListener(SWT.Selection, listener);
		underlineToolItem.addListener(SWT.Selection, listener);
		cutToolItem.addListener(SWT.Selection, listener);
		copyToolItem.addListener(SWT.Selection, listener);
		pasteToolItem.addListener(SWT.Selection, listener);
		fontCombo.addListener(SWT.Selection, listener);
		fontHeigtCombo.addListener(SWT.Selection, listener);
		saveToolItem.addListener(SWT.Selection, listener);
		saveAsToolItem.addListener(SWT.Selection, listener);
		printToolItem.addListener(SWT.Selection, listener);
	}

	private void testInsertTable() {
		String[][] contents = new String[6][];
		contents[0] = new String[] { "Whatever", "Chemical", "Programming" };
		contents[1] = new String[] { "1", "Mercur", "Python" };
		contents[2] = new String[] { "2", "Iridium", "Ruby" };
		contents[3] = new String[] { "3", "Iron", "C++" };
		contents[4] = new String[] { "4", "Gold", "Java" };
		contents[5] = new String[] { "5", "Silver", "Pascal" };

		ElexisTextPlugin.tempInstance.setFont("Verdana", SWT.NONE, 13);

		// test insert table
		ElexisTextPlugin.tempInstance.insertTable("ab", ITextPlugin.FIRST_ROW_IS_HEADER | ITextPlugin.GRID_VISIBLE,
				contents, null);
	}

	public void print() {

		/*
		 * PrinterData data = Printer.getDefaultPrinterData(); if (data == null) {
		 * System.out.println("Warning: No default printer."); return; } final Printer
		 * printer = new Printer(data); if (printer.startJob("Elexis Printing")) { Color
		 * black = printer.getSystemColor(SWT.COLOR_BLACK); Color white =
		 * printer.getSystemColor(SWT.COLOR_WHITE); Rectangle trim =
		 * printer.computeTrim(0, 0, 0, 0); GC gc = new GC(printer);
		 *
		 * if (printer.startPage()) { gc.setBackground(white); gc.setForeground(black);
		 *
		 * page.print(printer, gc);
		 *
		 * printer.endPage(); gc.dispose(); printer.endJob(); } }
		 */
	}

	private void changeFont() {
		fontName = fontCombo.getItem(fontCombo.getSelectionIndex());
		updateStylesOnText();
		lastSelectedText.forceFocus();
	}

	private void changeFontHeight() {
		fontHeight = Integer.parseInt(fontHeigtCombo.getItem(fontHeigtCombo.getSelectionIndex()));
		updateStylesOnText();
		lastSelectedText.forceFocus();
	}

	protected TextBox insertBox() {
		TextBox box = page.insertBox();
		box.addExtendedModifyListener(this);
		addCaretListener(box);
		updateControls();
		return box;
	}

	protected void insertTable(final int start, final int end, final String[][] contents, final boolean header,
			final boolean grid, final String fontName, final int fontHeight, final int fontStyle) {

		Table table = new Table(page, SWT.FULL_SELECTION | (grid ? SWT.BORDER : SWT.NONE));

		table.setLinesVisible(grid);
		table.setHeaderVisible(false);

		int rowcount = contents.length;
		int columncount = contents[0].length;

		for (int i = 0; i < columncount; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setText(String.valueOf(i));
		}

		Font tableFont = table.getFont();
		if (fontName != null) {
			tableFont = new Font(getDisplay(), fontName, fontHeight, fontStyle);
		}
		table.setFont(tableFont);

		for (int i = 0; i < rowcount; i++) {
			String[] rowcontent = contents[i];
			TableItem row = new TableItem(table, SWT.NONE);
			for (int j = 0; j < rowcontent.length; j++) {
				row.setText(j, rowcontent[j]);
				if ((i == 0) && header) {
					row.setBackground(j, getDisplay().getSystemColor(SWT.COLOR_GRAY));
				}
			}
		}

		for (int i = 0; i < columncount; i++) {
			table.getColumn(i).pack();
		}

		table.pack();

		Rectangle rect = table.getItem(rowcount - 1).getBounds(columncount - 1);
		table.setSize(rect.x + rect.width + 5, rect.y + rect.height + 5);

		page.replaceTextRange(start, end - start, StringUtils.SPACE);
		page.addTable(table, start);
	}

	protected TextBox insertBox(int x, int y, int w, int h) {
		TextBox box = insertBox();

		w = Math.min(page.getSize().x, w);
		h = Math.min(page.getSize().y, h);
		x = Math.max(0, Math.min(page.getSize().x - w, x));
		y = Math.max(0, Math.min(page.getSize().y - h, y));

		box.forceLocation(x, y);
		box.setSize(w, h);
		return box;
	}

	private void deleteBox() {
		StyledText text = lastSelectedText;
		if (text instanceof TextBox) {
			page.textBoxes.remove(text);
			text.dispose();
			if (!page.textBoxes.isEmpty()) {
				(page.textBoxes.get(page.textBoxes.size() - 1)).forceFocus();
			}
			updateControls();
		}
	}

	public void setSelectedText(final EStyledText text) {
		lastSelectedText = text;
	}

	private void updateStylesOnText() {
		int fontStyle = (isBold ? SWT.BOLD : SWT.NONE) | (isItalic ? SWT.ITALIC : SWT.NONE);
		StyledText text = lastSelectedText;
		Point sel = text.getSelectionRange();
		if ((sel == null) || (sel.y == 0)) {
			return;
		}
		StyleRange style = new StyleRange();
		for (int i = sel.x; i < sel.x + sel.y; i++) {
			StyleRange range = text.getStyleRangeAtOffset(i);
			style.start = i;
			style.length = 1;
			if (range != null) {
				style = (StyleRange) range.clone();
			}
			style.fontStyle = fontStyle;
			style.underline = isUnderline;

			style.font = new Font(getDisplay(), fontName, fontHeight, fontStyle);
			text.setStyleRange(style);
		}
	}

	private void updateControls() {
		StyledText text = lastSelectedText;
		if (text.getCharCount() == 0) {
			return;
		}
		StyleRange style = text.getStyleRangeAtOffset(Math.max(0, text.getCaretOffset() - 1));
		if (style == null) {
			style = new StyleRange();
		}

		isBold = (style.fontStyle & SWT.BOLD) != 0;
		isItalic = (style.fontStyle & SWT.ITALIC) != 0;
		isUnderline = style.underline;

		if ((style.font != null) && (style.font.getFontData() != null) && (style.font.getFontData().length > 0)) {
			FontData data = style.font.getFontData()[0];
			isBold = (data.getStyle() & SWT.BOLD) != 0;
			isItalic = (data.getStyle() & SWT.ITALIC) != 0;
			fontName = data.getName();
			fontHeight = data.getHeight();
		}

		boldToolItem.setSelection(isBold);
		italicToolItem.setSelection(isItalic);
		underlineToolItem.setSelection(isUnderline);
		fontCombo.select(fontCombo.indexOf(fontName != null ? fontName : StringUtils.EMPTY));
		fontHeigtCombo.select(fontHeigtCombo.indexOf(String.valueOf(fontHeight)));
	}

	public void caretUpdate(final Event ev) {
		updateControls();
	}

	/*
	 * Cache the style information for text that has been cutToolItem or copied.
	 */
	void handleCutCopy(final StyledText text) {
		// Save the cutToolItem/copied style info so that during pasteToolItem we will
		// maintain
		// the style information. Cut/copied text is put in the clipboard in
		// RTF format, but is not pasted in RTF format. The other way to
		// handle the pasting of styles would be to access the Clipboard
		// directly and
		// parse the RTF text.
		cachedStyles.clear();
		Point sel = text.getSelectionRange();
		int startX = sel.x;
		for (int i = sel.x; i <= sel.x + sel.y - 1; i++) {
			StyleRange style = text.getStyleRangeAtOffset(i);
			if (style != null) {
				style.start = style.start - startX;
				if (!cachedStyles.isEmpty()) {
					StyleRange lastStyle = cachedStyles.lastElement();
					if (lastStyle.similarTo(style) && (lastStyle.start + lastStyle.length == style.start)) {
						lastStyle.length++;
					} else {
						cachedStyles.addElement(style);
					}
				} else {
					cachedStyles.addElement(style);
				}
			}
		}
	}

	public void modifyText(final ExtendedModifyEvent event) {
		StyledText text = (StyledText) event.widget;
		if (event.length == 0) {
			return;
		}
		StyleRange style;
		if ((event.length == 1) || text.getTextRange(event.start, event.length).equals(text.getLineDelimiter())) {
			// Have the new text take on the style of the text to its right
			// (during
			// typing) if no style information is active.
			int caretOffset = text.getCaretOffset() - 1;
			style = null;
			if (caretOffset < text.getCharCount()) {
				style = text.getStyleRangeAtOffset(caretOffset);
			}
			if (style != null) {
				style = (StyleRange) style.clone();
				style.start = event.start;
				style.length = event.length;
			} else {
				style = new StyleRange(event.start, event.length, null, null, SWT.NORMAL);
			}
			if (isBold) {
				style.fontStyle |= SWT.BOLD;
			}
			if (isItalic) {
				style.fontStyle |= SWT.ITALIC;
			}
			style.underline = isUnderline;

			style.font = new Font(getDisplay(), fontName, fontHeight, style.fontStyle);

			if (!style.isUnstyled()) {
				text.setStyleRange(style);
			}
		} else {
			// pasteToolItem occurring, have text take on the styles it had when it was
			// cutToolItem/copied
			if (!cachedStyles.isEmpty()) {
				for (int i = 0; i < cachedStyles.size(); i++) {
					style = cachedStyles.elementAt(i);
					StyleRange newStyle = (StyleRange) style.clone();
					newStyle.start = style.start + event.start;
					text.setStyleRange(newStyle);
				}
			} else {
				text.setSelection(event.start, event.start + event.length);
				updateStylesOnText();
			}
		}
		updateControls();
	}

	public boolean clear() {
		page.clear();
		return true;
	}

}

// test find/replace
// System.out.println(ElexisTextPlugin.tempInstance.findOrReplace("a*b", new
// ReplaceCallback() {
// public String replace(String in) {
// return "gaga";
// }}));

// test insertText
// ElexisTextPlugin.tempInstance.setFont("Courier", SWT.BOLD, 24);
// ElexisTextPlugin.Pos pos = (Pos)
// ElexisTextPlugin.tempInstance.insertTextAt(100, 1000, 200, 200,
// "\nHello\n box\n", 0);
// ElexisTextPlugin.tempInstance.setFont("Tahoma", SWT.ITALIC, 8);
// ElexisTextPlugin.tempInstance.insertText(pos, "Tahoma text", 0);

// test storeToByteArray()
// byte[] array =
// ElexisTextPlugin.tempInstance.storeToByteArray();
// try {
// FileOutputStream fout = new
// FileOutputStream("./test.dat");
// fout.write(array);
// fout.close();
// } catch (Exception ex) {
// ex.printStackTrace();
// }
// test load from byte array
// try {
// FileInputStream fin = new FileInputStream("./test.dat");
// ByteArrayOutputStream bout = new ByteArrayOutputStream();
// byte[] buff = new byte[4096];
// int len = 0;
// while ((len = fin.read(buff)) > 0) {
// bout.write(buff, 0, len);
// }
// fin.close();
// bout.close();
// ElexisTextPlugin.tempInstance.loadFromByteArray(bout.toByteArray(),
// false);
// } catch (Exception ex) {
// ex.printStackTrace();
// }
