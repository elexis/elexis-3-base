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

/**
 *
 * @author Daniel Ludin
 *
 *         Custom Canvas for the Display of Min/Max Lab Values
 *
 */
public class NoValuesCanvas extends CstCanvas {

	int iPixX = 774; // Anzeigebreite in pix
	int iPixY = 140; // Anzeigeh�he in pix
	int xoffBase = 4; // x offset
	int yoffBase = 140; // y offset
	int werteBereich = 160; // Wertebereich der in iPixY/X angezeigt werden soll

	MinimaxValue finding = new MinimaxValue();

	public NoValuesCanvas(Composite parent, int style) {
		super(parent, style);

		setBackground(WHITE);

		setSize(iPixX, iPixY);

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				NoValuesCanvas.this.paintControl(e);
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

		int xOff1 = 0;

		int xOff4 = 510;

		gc.setForeground(BLUE);

		gc.setFont(fontBig);
		gc.setForeground(DARKGRAY);

		gc.drawLine(iPixX, 0, iPixX, iPixY);
		gc.drawLine(xOff4, 0, xOff4, iPixY);

		StringBuffer title = new StringBuffer(finding.getName());
		/*
		 * if (finding.getRangeStart() > 0 && finding.getRangeEnd() > 0) {
		 *
		 * title.append(" ("); title.append(finding.getRangeStart());
		 * title.append(" - "); title.append(finding.getRangeEnd()); title.append(" )");
		 * } else { title.append(" (keine Ref.Werte)"); }
		 *
		 */
		gc.drawText(title.toString(), xOff1, 2, true);

		gc.setFont(fontSmall);
		gc.setForeground(BLUE);
		String sTxt1 = finding.getText();

		gc.drawText(sTxt1, xOff1 + 10, 30);

		/*
		 * if (finding.getMaxOfSpan3() == -1) { gc.drawText("Resultat:\t keine Werte",
		 * xOff1 + 10, 100, true);
		 *
		 * } else { gc.drawText( "Resultat:\t " + finding.getMaxOfSpan3() +
		 * StringUtils.LF + CstService.getGermanFromDate(finding.getDateStartOfSpan3()),
		 * xOff1 + 10, 100, true); }
		 */
		gc.setForeground(BLACK);

		final TextLayout layout = new TextLayout(getDisplay());
		layout.setText(finding.getAbstract() == null ? "null" : finding.getAbstract());
		layout.setWidth(250);

		Font fontNormal = UiDesk.getFont("Helvetica", 7, SWT.NORMAL); //$NON-NLS-1$
		layout.setFont(fontNormal);

		layout.draw(gc, xOff4 + 4, 4);

		gc.dispose();
	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(iPixX, iPixY);

	}

}
