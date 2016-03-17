/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst.widget;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.UiDesk;

/**
 * This is the base class for all CST Canvasses.
 * 
 * @author daniel
 *
 */
public class CstCanvas extends Canvas {
    Font fontSmall;
    Font fontBig;

    Font fontA;

    static Color BLUE;
    static Color GRAY;
    static Color DARKGRAY;
    static Color WHITE;
    static Color BRIGHTGREEN;
    static Color ORANGE;
    static Color BLACK;
    protected static Logger log = LoggerFactory.getLogger(CstCanvas.class.getName());

    public CstCanvas(Composite parent, int style) {
	super(parent, style);

	fontSmall = createFontofSize(7);
	fontBig = createFontofSize(12);

	Font initialFont = getDisplay().getSystemFont();
	FontData[] fontData = initialFont.getFontData();
	for (int i = 0; i < fontData.length; i++) {
	    fontData[i].setHeight(7);
	}
	fontA = new Font(getDisplay(), fontData);
	BLUE = UiDesk.getColorFromRGB("1E1EFF");
	GRAY = UiDesk.getColorFromRGB("DDDDDD");
	DARKGRAY = UiDesk.getColorFromRGB("777777");
	WHITE = UiDesk.getColorFromRGB("FFFFFF");
	BRIGHTGREEN = UiDesk.getColorFromRGB("68FF00");
	ORANGE = UiDesk.getColorFromRGB("FF6800");
	BLACK = UiDesk.getColorFromRGB("000000");

	setBackground(WHITE);

	addDisposeListener(new DisposeListener() {
	    public void widgetDisposed(DisposeEvent e) {
		// TODO: is it necessary to dispose these colors (don't think so, but make sure)
		fontA.dispose();
	    }

	});

    }

    private Font createFontofSize(int sizeOfFont) {
	Font initialFont = getDisplay().getSystemFont();
	FontData[] fontData = initialFont.getFontData();
	for (int i = 0; i < fontData.length; i++) {
	    fontData[i].setHeight(sizeOfFont);
	}
	Font newFont = new Font(getDisplay(), fontData);
	return newFont;
    }

}
