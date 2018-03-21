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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Composite;

import ch.elexis.core.ui.UiDesk;
import ch.gpb.elexis.cst.data.MinimaxValue;
import ch.gpb.elexis.cst.service.CstService;

/**
 * 
 * @author Daniel Ludin
 *
 *	Custom Canvas for the Display of Min/Max Lab Values
 *
 */
public class MinimaxCanvas extends CstCanvas {

    int iPixX = 774; // Anzeigebreite in pix
    int iPixY = 140; // Anzeigehöhe in pix
    int xoffBase = 4; // x offset
    int yoffBase = 140; // y offset
    int werteBereich = 160; // Wertebereich der in iPixY/X angezeigt werden soll

    MinimaxValue finding = new MinimaxValue();

    public MinimaxCanvas(Composite parent, int style) {
	super(parent, style);

	setBackground(WHITE);

	setSize(iPixX, iPixY);

	addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		MinimaxCanvas.this.paintControl(e);
	    }
	});
    }

    public MinimaxValue getFinding() {
	return finding;
    }

    public void setFinding(MinimaxValue finding) {
	this.finding = finding;
    }

    void paintControl(PaintEvent e) {

	GC gc = e.gc;
	gc.setFont(fontSmall);

	if (finding == null) {
	    gc.drawText("no Finding set", 20, 20);
	    return;
	}
	//		int xOff1 = 20;
	//		int xOff2 = 320;
	//		int xOff3 = 620;
	int xOff1 = 0;
	//int xOff2 = 280;
	//int xOff3 = 520;
	/*
		int xOff2 = 200;
		int xOff3 = 400;
		int xOff4 = 600;
	*/
	int xOff2 = 170;
	int xOff3 = 340;
	int xOff4 = 510;

	gc.setForeground(BLUE);
	//gc.drawRectangle(xoffBase, yoffBase, 400, 1);

	gc.setFont(fontBig);
	gc.setForeground(DARKGRAY);

	/*
	gc.drawLine(260, 0, 260, iPixY);
	gc.drawLine(500, 0, 500, iPixY);
	*/
	gc.drawLine(xOff2, 0, xOff2, iPixY);
	gc.drawLine(xOff3, 0, xOff3, iPixY);
	gc.drawLine(xOff4, 0, xOff4, iPixY);

	gc.drawLine(iPixX, 0, iPixX, iPixY);

	StringBuffer title = new StringBuffer(finding.getName());
	if (finding.getRangeStart() > 0 && finding.getRangeEnd() > 0) {

	    title.append(" (");
	    title.append(finding.getRangeStart());
	    title.append(" - ");
	    title.append(finding.getRangeEnd());
	    title.append(" )");
	} else {
	    title.append(" (keine Ref.Werte)");
	}

	gc.drawText(title.toString(), xOff1, 2, true);

	gc.setFont(fontSmall);
	gc.setForeground(BLUE);

	gc.drawText("Zeitspanne \r\n"
		+ "von\t" + CstService.getGermanFromDate(finding.getDateStartOfSpan3())
		+
		"\r\nbis\t" + CstService.getGermanFromDate(finding.getDateEndOfSpan3()) + ": ",
		xOff1 + 10, 30);

	if (finding.getMaxOfSpan3() == -1) {
	    gc.drawText("Maximum:\t keine Werte",
		    xOff1 + 10, 80, true);

	}
	else {
	    gc.drawText("Maximum:\t " + finding.getMaxOfSpan3(),
		    xOff1 + 10, 80, true);
	}

	if (finding.getMinOfSpan3() == -1) {
	    gc.drawText("Minimum:\t keine Werte",
		    xOff1 + 10, 96, true);

	}
	else {
	    gc.drawText("Minimum:\t " + finding.getMinOfSpan3(),
		    xOff1 + 10, 96, true);
	}

	gc.setForeground(ORANGE);

	gc.drawText("Zeitspanne \r\n"
		+ "von\t" + CstService.getGermanFromDate(finding.getDateStartOfSpan2()) +
		"\r\nbis\t" + CstService.getGermanFromDate(finding.getDateEndOfSpan2()) + ": ",
		xOff2 + 10, 30);

	if (finding.getMaxOfSpan2() == -1) {
	    gc.drawText("Maximum:\t keine Werte",
		    xOff2 + 10, 80, true);
	}
	else {
	    gc.drawText("Maximum:\t " + finding.getMaxOfSpan2(),
		    xOff2 + 10, 80, true);
	}

	if (finding.getMinOfSpan2() == -1) {
	    gc.drawText("Minimum:\t keine Werte",
		    xOff2 + 10, 96, true);
	}
	else {
	    gc.drawText("Minimum:\t " + finding.getMinOfSpan2(),
		    xOff2 + 10, 96, true);
	}

	gc.setForeground(BLACK);

	gc.drawText("Zeitspanne \r\n"
		+ "von\t" + CstService.getGermanFromDate(finding.getDateStartOfSpan1()) +
		"\r\nbis\t" + CstService.getGermanFromDate(finding.getDateEndOfSpan1()) + ": ",
		xOff3 + 10, 30);

	if (finding.getMaxOfSpan1() == -1) {
	    gc.drawText("Maximum:\tkeine Werte",
		    xOff3 + 10, 80, true);

	} else {
	    gc.drawText("Maximum:\t " + finding.getMaxOfSpan1(),
		    xOff3 + 10, 80, true);
	}

	if (finding.getMinOfSpan1() == -1) {
	    gc.drawText("Minimum:\t keine Werte",
		    xOff3 + 10, 96, true);

	} else {
	    gc.drawText("Minimum:\t " + finding.getMinOfSpan1(),
		    xOff3 + 10, 96, true);
	}

	/*
	StyledText styledText = new StyledText(this, SWT.NONE);
	styledText.setText(finding.getAbstract());
	styledText.setWordWrap(true);
	styledText.setSize(200, 300);
	gc.drawText(styledText.getText(),
		xOff3 + 210, 20, true);
	*/
	final TextLayout layout = new TextLayout(getDisplay());
	layout.setText(finding.getAbstract());
	layout.setWidth(250);
	Font fontNormal = UiDesk.getFont("Helvetica", 7, SWT.NORMAL); //$NON-NLS-1$
	layout.setFont(fontNormal);

	layout.draw(gc, xOff4 + 4, 4);

	//TextStyle textStyle = new TextStyle();
	//textStyle.
	/*
	gc.drawText(finding.getAbstract(),
		xOff3 + 210, 20, true);
	 */

	gc.dispose();
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
	return new Point(iPixX, iPixY);

    }

}
