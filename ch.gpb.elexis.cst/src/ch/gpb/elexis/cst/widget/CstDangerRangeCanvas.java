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

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.ui.UiDesk;
import ch.gpb.elexis.cst.Activator;

/**
 * 
 * @author daniel 
 * created: 11.01.2015
 *
 * Custom Widget for the display of values within a defined range
 * 
 */
public class CstDangerRangeCanvas extends CstCanvas {
    double dRangeStart;
    double dRangeEnd;
    double dValue;
    String alphanumValue;
    int iLenScale;
    Image marker;
    Image verlaufRl;
    Image verlaufLr;
    Image pointer;
    int xoffset;
    int yoffset;
    int iPixLen;

    String title;
    String sDate;

    public CstDangerRangeCanvas(Composite parent, boolean a4Quer, int style, double dRangeStart, double dRangeEnd,
	    double dValue, String sValue, String title, String date) {
	super(parent, style);
	this.dRangeStart = dRangeStart;
	this.dRangeEnd = dRangeEnd;
	this.dValue = dValue;
	this.title = title;
	this.sDate = date;
	this.alphanumValue = sValue;

	// 1123 - 794 = 329
	if (a4Quer) {
	    iPixLen = 809;
	} else {
	    iPixLen = 480;
	}

	xoffset = 4;
	yoffset = 12;

	pointer = UiDesk.getImage(Activator.IMG_POINTER_NAME);

	addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		CstDangerRangeCanvas.this.paintControl(e);
	    }
	});
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
	return new Point(iPixLen + 50, 36);
    }

    void paintControl(PaintEvent e) {

	GC gc = e.gc;
	gc.setFont(fontA);

	// der Anzeigebereich wird mit dem höchsten Wert berechnet, zB wenn
	// RangeEnd grösser als der eigentlich Wert ist, nimmt man diesen
	// ceilValue = 10 hoch Anzahl Stellen vor dem Komma von value
	double dTmp = dValue;
	if (dValue < dRangeEnd) {
	    dTmp = dRangeEnd;
	}
	double rTmp = Math.round(dTmp);
	//System.out.println("========================");
	//System.out.println("rTmp : " + rTmp);

	double dAnzeigeBereich;

	// Wenn bereits das Aufrunden des höchsten Wertes eine 10er Potenz
	// ergibt, soll dieser als Anzeigebereich gelten.
	if (rTmp % 10 == 0) {
	    dAnzeigeBereich = rTmp;
	    //System.out.println("Runden ergibt 10er Potenz");
	} else {
	    dAnzeigeBereich = dTmp * 1.2;
	}

	//System.out.println("dAnzeigeBereich : " + dAnzeigeBereich);

	// compute a factor for the x-offset
	double xFac = iPixLen / dAnzeigeBereich;
	//System.out.println("val: " + dValue);
	//System.out.println("fac: " + fac);

	gc.setBackground(ORANGE);
	gc.fillRectangle(xoffset, yoffset, iPixLen, 10);
	int greenStart = 0;
	int greenEnd = 0;
	if (dRangeStart > 0) {
	    greenStart = new Double(Math.round(xFac * dRangeStart)).intValue();
	}
	greenEnd = new Double(xFac * dRangeEnd).intValue();

	gc.setBackground(BRIGHTGREEN);
	gc.fillRectangle(xoffset + greenStart, yoffset, (greenEnd - greenStart), 10);

	gc.setForeground(ORANGE);

	if (dRangeStart > 0) {
	    //System.out.println("end x/xLenToFill: " + (xoffset + greenEnd - 20) + " / " + xLenToFill);
	    gc.fillGradientRectangle(xoffset + greenStart, yoffset, 40, 10, false);
	    //System.out.println("Verlauf links  xoff/yoff/xlen: "+ (xoffset + greenStart-20) + " / " + yoffset +" / "+ ((greenEnd - greenStart)+20));
	}
	gc.setForeground(BRIGHTGREEN);
	gc.setBackground(ORANGE);

	int xLenToFill = iPixLen - (greenEnd - 20);
	//System.out.println("end x/xLenToFill: " + (xoffset + greenEnd - 20) + " / " + xLenToFill);

	try {
	    gc.fillGradientRectangle((xoffset + greenEnd - 20), yoffset, xLenToFill, 10, false);
	} catch (Exception e1) {
	    e1.printStackTrace();
	    log.error("DangerRangeCanvas error: " + "greenEnd:" + greenEnd + " xLenToFill: " + xLenToFill
		    + "  " + "title: " + title + " " + e1.getMessage());
	}

	gc.setForeground(BLACK);

	//if (!alphanumValue.matches("(?i)(.*)[neg|pos|norm](.*)")) {
	if (!(alphanumValue.toLowerCase().indexOf("neg") > -1 ||
		alphanumValue.toLowerCase().indexOf("norm") > -1 ||
		alphanumValue.toLowerCase().indexOf("pos") > -1 ||
		alphanumValue.toLowerCase().indexOf("-") > -1 || alphanumValue.toLowerCase().indexOf("+") > -1)) {
	    gc.drawString(">" + String.valueOf(dRangeEnd), xoffset + greenEnd, yoffset - 2, true);

	}

	if (this.title != null) {
	    gc.drawString(title + " (" + sDate + ")", xoffset + 2, yoffset - 13, true);

	} else {
	    gc.drawString("[title missing] (" + sDate + ")", xoffset + 2, yoffset - 13, true);
	}

	if (dRangeStart > 0) {
	    gc.drawString("< " + String.valueOf(dRangeStart), xoffset + greenStart, yoffset - 2, true);
	}

	int posM = new Double(Math.round(xFac * dValue)).intValue();
	// draw a Rect as pointer

	// draw the value 
	String sValue = String.valueOf(dValue);
	// TODO: compute the pixel width of the value string
	int iLenValue = 4;

	//if (alphanumValue.matches("(?i)(.*)[neg|norm](.*)")) {
	FontMetrics fm = gc.getFontMetrics();
	Point pt = gc.textExtent(alphanumValue);

	if (alphanumValue.toLowerCase().indexOf("pos") > -1 || alphanumValue.toLowerCase().indexOf("+") > -1) {
	    String sDisplay = alphanumValue;
	    if (sDisplay.indexOf("+") > -1) {
		sDisplay = "positiv";
	    }

	    gc.drawString(sDisplay, iPixLen - pt.x - 20, yoffset - iLenValue + 1, true);
	}
	else if (alphanumValue.toLowerCase().indexOf("neg") > -1
		|| alphanumValue.toLowerCase().indexOf("norm") > -1 || alphanumValue.toLowerCase().indexOf("-") > -1) {

	    /*
	    if (alphanumValue != null
	    	&& (alphanumValue.toLowerCase().indexOf("pos") > -1
	    		|| alphanumValue.toLowerCase().indexOf("neg") > -1
	    		|| alphanumValue.toLowerCase().indexOf("norm") > -1)) {
	        */
	    String sDisplay = alphanumValue;
	    if (sDisplay.indexOf("-") > -1) {
		sDisplay = "negativ";
	    }

	    gc.drawString(sDisplay, xoffset + posM, yoffset - iLenValue + 1, true);

	} else {
	    if (dValue > 0) {
		gc.drawString(sValue, xoffset + posM, yoffset + 14 - iLenValue, true);
		// draw the pointer icon
		gc.drawImage(pointer, xoffset + posM - 6, yoffset + 14 - iLenValue - 14);
	    }
	}

    }

    public double getRangeStart() {
	return dRangeStart;
    }

    public void setRangeStart(double rangeStart) {
	this.dRangeStart = rangeStart;
    }

    public double getRangeEnd() {
	return dRangeEnd;
    }

    public void setRangeEnd(double rangeEnd) {
	this.dRangeEnd = rangeEnd;
    }

    public double getValue() {
	return dValue;
    }

    public void setValue(double value) {
	this.dValue = value;
    }

    public int getlScale() {
	return iLenScale;
    }

    public void setlScale(int lScale) {
	this.iLenScale = lScale;
    }

}
