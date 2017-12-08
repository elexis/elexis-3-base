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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import ch.gpb.elexis.cst.data.ValueSingleTimeline;
import ch.gpb.elexis.cst.service.CstService;

public class ValueSingleTimelineCanvas extends Canvas {

    Font fontSmall;
    Font fontBig;
    Color ORANGE;
    Color BRIGHTGREEN;
    Color WHITE;
    Color GREY;
    Color BLACK;
    Color BLUE;

    int iPixX = 700; // Anzeigebreite in pix
    int iPixY = 120; // Anzeigehöhe in pix
    int xoffBase = 4; // x offset
    int yoffBase = 140; // y offset
    //int werteBereich = 500; // Wertebereich der in iPixY/X angezeigt werden soll
    int werteBereich = 1200; // Wertebereich der in iPixY/X angezeigt werden soll
    String befundArt = new String();
    String einheit = new String();
    List<ValueSingleTimeline> findings = new ArrayList<ValueSingleTimeline>();
    double valueRangeOfInput;
    double distBetweenLines;
    double nrOfScaleLines;
    int scaleStepWidth = 0;

    public ValueSingleTimelineCanvas(Composite parent, int style, String befundArt, String einheit) {
	super(parent, style);
	this.befundArt = befundArt;
	this.einheit = einheit;
	WHITE = new Color(null, 255, 255, 255);
	ORANGE = new Color(getDisplay(), 255, 104, 0);
	BRIGHTGREEN = new Color(getDisplay(), 104, 255, 0);
	BLACK = new Color(getDisplay(), 0, 0, 0);
	GREY = new Color(getDisplay(), 200, 200, 200);
	BLUE = new Color(getDisplay(), 30, 30, 255);

	fontSmall = createFontofSize(7);
	fontBig = createFontofSize(12);
	setBackground(WHITE);

	setSize(440, 500);

	addDisposeListener(new DisposeListener() {
	    public void widgetDisposed(DisposeEvent e) {
		WHITE.dispose();
		BLACK.dispose();
		ORANGE.dispose();
		BRIGHTGREEN.dispose();
		BLUE.dispose();
		GREY.dispose();
		fontSmall.dispose();
	    }

	});

	addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		ValueSingleTimelineCanvas.this.paintControl(e);
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

    void paintControl(PaintEvent e) {

	GC gc = e.gc;
	gc.setFont(fontSmall);

	gc.setBackground(ORANGE);
	// draw the x base line
	gc.setForeground(GREY);
	gc.drawRectangle(xoffBase, yoffBase, iPixX, 1);

	// draw a title
	gc.setFont(fontBig);
	gc.drawText(String.valueOf(befundArt), 2, 0, true);

	if (findings == null || findings.size() == 0) {
	    gc.drawText("no Findings found", 20, 20);
	    return;
	}

	// compute nr of days between lowest and highest date
	Date dStart = CstService.getDateFromCompact(findings.get(0).getDate());
	Date dEnd = CstService.getDateFromCompact(findings.get(findings.size() - 1).getDate());

	long totalSpan = CstService.getNrOfDaysBetween(dStart, dEnd);

	if (findings == null || findings.size() == 0) {
	    return;
	}

	// y-axis: Display range is 0 - 160
	double yFactor = new Double(iPixY) / new Double(werteBereich).doubleValue();
	//System.out.println("yFactor: " + yFactor);

	// 20er Linie
	gc.setFont(fontSmall);
	gc.setForeground(BLUE);

	int lineValue = 0;
	gc.drawText(String.valueOf(einheit), iPixX + 25, yoffBase - new Double(lineValue * yFactor).intValue() - 6,
		true);

	gc.setForeground(GREY);

	for (int i = 1; i < nrOfScaleLines + 1; i++) {

	    gc.drawLine(xoffBase, yoffBase - new Double((i * scaleStepWidth) * yFactor).intValue(),
		    iPixX, yoffBase - new Double((i * scaleStepWidth) * yFactor).intValue());


	    gc.drawText(String.valueOf(i * scaleStepWidth),
		    iPixX + 25,
		    yoffBase - new Double((i * scaleStepWidth) * yFactor).intValue() - 6,
		    true);

	}

	gc.setBackground(GREY);

	int xoff = 0;
	double xFactor = new Double(iPixX).doubleValue() / new Double(totalSpan).doubleValue();

	gc.setForeground(BLACK);

	if (findings.size() == 1) {
	    String date = CstService.getReadableFromCompact(findings.get(0).getDate());
	    gc.drawText(date, xoffBase + new Double(iPixX / 2).intValue(), yoffBase, true);

	    gc.setForeground(BLUE);
	    gc.drawLine(xoff, yoffBase - new Double(findings.get(0).getWeightKg() * yFactor).intValue(), xoff + iPixX,
		    yoffBase - new Double(findings.get(0).getWeightKg() * yFactor).intValue());
	    gc.drawText(String.valueOf(findings.get(0).getWeightKg()), xoffBase + new Double(iPixX / 2).intValue(),
		    yoffBase - new Double(findings.get(0).getWeightKg() * yFactor).intValue(), true);

	    gc.setForeground(ORANGE);

	} else {
	    for (int x = 0; x < findings.size(); x++) {

		ValueSingleTimeline finding = findings.get(x);

		int yoff = 0;
		if (x % 2 == 0) {
		    yoff = 8;
		}
		String date = CstService.getReadableFromCompact(finding.getDate());
		gc.drawText(date, xoffBase + new Double(xoff * xFactor).intValue(), yoffBase + yoff + 4, true);

		// je grösser corrY desto höher wandert der Text
		int corrY = 10;
		// print syst
		gc.setForeground(BLUE);
		gc.drawText(String.valueOf(finding.getWeightKg()), xoffBase + new Double(xoff * xFactor).intValue(),
			yoffBase - new Double(finding.getWeightKg() * yFactor).intValue() - corrY, true);

		gc.setForeground(BLACK);

		if (x < findings.size() - 1) {
		    long lSpan = CstService.getNrOfDaysBetween(
			    CstService.getDateFromCompact(findings.get(0).getDate()),
			    CstService.getDateFromCompact(findings.get(x + 1).getDate()));
		    xoff = new Long(lSpan).intValue();
		}
	    }
	}

	gc.dispose();
    }

    private double getHighestValueInFindings() {
	double highest = 0;
	try {
	    highest = 0;
	} catch (Exception e) {
	    return highest;
	}

	for (int x = 0; x < findings.size(); x++) {
	    if (findings.get(x).getWeightKg() > highest) {
		highest = findings.get(x).getWeightKg();
	    }
	}
	return highest;

    }

    private double getLowestValueInFindings() {
	double lowest = 0;
	try {
	    lowest = findings.get(0).getWeightKg();
	} catch (Exception e) {
	    return lowest;
	}


	for (int x = 1; x < findings.size(); x++) {
	    if (findings.get(x).getWeightKg() < lowest) {
		lowest = findings.get(x).getWeightKg();
	    }
	}
	return lowest;

    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
	return new Point(iPixX + 70, 30 + yoffBase);

    }

    public List<ValueSingleTimeline> getFindings() {
	return findings;
    }

    public void setFindings(List<ValueSingleTimeline> findings) {
	this.findings = findings;
	Collections.sort(this.findings, new FindingsComparable());

	double lowest = getLowestValueInFindings();
	double highest = getHighestValueInFindings();

	valueRangeOfInput = highest - lowest;
	// 20 px is the distance between scale lines
	distBetweenLines = 20d;
	//nrOfScaleLines = iPixY / distBetweenLines;
	nrOfScaleLines = 10;

	int num = new Double(getHighestValueInFindings()).intValue();

	int rounded = 0;
	if (highest > 0 && highest < 10) {
	    rounded = ((num + 9) / 10) * 10;
	    scaleStepWidth = 1;
	}
	else if (highest > 10 && highest < 100) {
	    rounded = ((num + 99) / 100) * 100;
	    scaleStepWidth = 10;
	}
	else if (highest > 100 && highest < 1000) {
	    rounded = ((num + 990) / 1000) * 1000;
	    scaleStepWidth = 100;
	}

	else if (highest > 1000 && highest < 10000) {
	    rounded = ((num + 9999) / 10000) * 10000;
	    scaleStepWidth = 1000;

	}

	this.werteBereich = rounded;

    }

    public class FindingsComparable implements Comparator<ValueSingleTimeline> {

	@Override
	public int compare(ValueSingleTimeline o1, ValueSingleTimeline o2) {
	    return o1.getDate().compareTo(o2.getDate());

	}
    }
}
