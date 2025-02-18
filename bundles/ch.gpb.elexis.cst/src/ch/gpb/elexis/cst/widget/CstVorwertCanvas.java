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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.ValueFinding;
import ch.gpb.elexis.cst.service.CstService;

public class CstVorwertCanvas extends CstCanvas {

	// Image pointer;
	ValueFinding finding;
	int iPixX = 480;
	int iPixY = 60;
	int xoffBase = 4;
	int yoffBase = 80;

	List<ValueFinding> findings = new ArrayList<ValueFinding>();

	public CstVorwertCanvas(Composite parent, boolean a4Quer, int style) {
		super(parent, style);
		setBackground(WHITE);

		if (a4Quer) {
			iPixX = 810;
		} else {
			iPixX = 480;
		}

		parent.setSize(iPixX, 600);

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				CstVorwertCanvas.this.paintControl(e);
			}
		});
	}

	public double getHightestValue() {
		double highest = 0;
		for (ValueFinding finding : getFindings()) {
			if (finding.getValue() > highest) {
				highest = finding.getValue();
			}
		}
		return highest;
	}

	void paintControl(PaintEvent e) {

		GC gc = e.gc;
		gc.setFont(fontA);

		if (findings == null || findings.size() == 0) {
			gc.drawText(Messages.Cst_Text_keine_vorwerte, 20, 20);
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		// compute nr of days between lowest and highest date
		Date dStart = findings.get(0).getDateOfFinding();
		Date dEnd = findings.get(findings.size() - 1).getDateOfFinding();

		long totalSpan = getNrOfDaysBetween(dStart, dEnd);

		gc.setBackground(ORANGE);
		// draw the x base line
		gc.setForeground(GRAY);
		gc.drawRectangle(xoffBase, yoffBase, iPixX, 1);

		double dRefFend = findings.get(0).getRefFend();
		double dRefMend = findings.get(0).getRefMend();
		double dRefFstart = findings.get(0).getRefFstart();
		double dRefMstart = findings.get(0).getRefMstart();

		double maxRef = Math.max(dRefFend, dRefMend);
		double yFactor = new Double(iPixY).doubleValue() / Math.max(getHightestValue(), maxRef);

		int iOffYtopM = new Double(dRefMend * yFactor).intValue();
		int iOffYtopF = new Double(dRefFend * yFactor).intValue();

		int iOffYbottomM = new Double(dRefMstart * yFactor).intValue();
		int iOffYbottomF = new Double(dRefFstart * yFactor).intValue();

		// draw top range line and value Male
		gc.setForeground(BRIGHTGREEN);
		if (dRefMend > 0) {
			gc.drawLine(xoffBase, yoffBase - iOffYtopM, iPixX, yoffBase - iOffYtopM);
			gc.drawText(String.valueOf(dRefMend), iPixX + 25, yoffBase - iOffYtopM - 6, true);
		}
		// draw bottom range line and value Male
		if (dRefMstart > 0) {
			gc.drawLine(xoffBase, yoffBase - iOffYbottomM, iPixX, yoffBase - iOffYbottomM);
			gc.drawText(String.valueOf(dRefMstart), iPixX + 25, yoffBase - iOffYbottomM - 6, true);
		}
		// draw top range line and value Female
		gc.setForeground(ORANGE);
		if (dRefFend > 0) {
			gc.drawLine(xoffBase, yoffBase - iOffYtopF, iPixX, yoffBase - iOffYtopF);
			gc.drawText(String.valueOf(dRefFend), iPixX + 25, yoffBase - iOffYtopF - 6, true);
		}
		// draw bottom range line and value Female
		if (dRefFstart > 0) {
			gc.drawLine(xoffBase, yoffBase - iOffYbottomF, iPixX, yoffBase - iOffYbottomF);
			gc.drawText(String.valueOf(dRefFstart), iPixX + 25, yoffBase - iOffYbottomF - 6, true);
		}

		gc.setBackground(GRAY);

		int xoff = 0;
		double xFactor = new Double(iPixX).doubleValue() / new Double(totalSpan).doubleValue();

		gc.setForeground(BLACK);
		for (int x = 0; x < findings.size(); x++) {

			ValueFinding finding = findings.get(x);

			int yoff = 0;
			if (x % 2 == 0) {
				yoff = 8;
			}
			String date = sdf.format(finding.getDateOfFinding());
			gc.drawText(date, xoffBase + new Double(xoff * xFactor).intValue(), yoffBase + yoff + 4, true);

			// je gr�sser corrY desto h�her wandert der Text
			int corrY = 10;

			if (!(finding.getParam().toLowerCase().indexOf("neg") > -1
					|| finding.getParam().toLowerCase().indexOf("norm") > -1
					|| finding.getParam().toLowerCase().indexOf("pos") > -1
					|| finding.getParam().toLowerCase().indexOf("+") > -1
					|| finding.getParam().toLowerCase().indexOf("-") > -1)) {

				gc.drawText(String.valueOf(finding.getValue()), xoffBase + new Double(xoff * xFactor).intValue(),
						yoffBase - new Double(finding.getValue() * yFactor).intValue() - corrY, true);
			} else {
				String sDisplay = finding.getParam();
				if (sDisplay.indexOf("-") > -1) {
					sDisplay = "negativ";
				} else if (sDisplay.indexOf("+") > -1) {
					sDisplay = "positiv";
				}
				gc.drawText(sDisplay, xoffBase + new Double(xoff * xFactor).intValue(), yoffBase - 12 - corrY, true);

			}

			if (x < findings.size() - 1) {
				long lSpan = getNrOfDaysBetween(findings.get(0).getDateOfFinding(),
						findings.get(x + 1).getDateOfFinding());
				xoff = new Long(lSpan).intValue();
			}

		}

		gc.dispose();
	}

	private long getNrOfDaysBetween(Date dStart, Date dEnd) {
		long diff = dEnd.getTime() - dStart.getTime();
		long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return days;

	}

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(iPixX + 50, 30 + yoffBase);

	}

	public ValueFinding getFinding() {
		return finding;
	}

	public void setFinding(ValueFinding finding) {
		this.finding = finding;
	}

	public List<ValueFinding> getFindings() {
		return findings;
	}

	public void setFindings(List<ValueFinding> findings) {
		this.findings = findings;
		Collections.sort(this.findings, new FindingsComparable());
	}

	public class FindingsComparable implements Comparator<ValueFinding> {

		@Override
		public int compare(ValueFinding o1, ValueFinding o2) {
			return o1.getDateOfFinding().compareTo(o2.getDateOfFinding());

		}
	}

	@Override
	public String toString() {
		// return super.toString();
		StringBuffer result = new StringBuffer();
		result.append(StringUtils.EMPTY);
		for (ValueFinding finding : getFindings()) {
			result.append(
					"(" + CstService.getCompactFromDate(finding.getDateOfFinding()) + ":" + finding.getValue() + ")");
		}
		return result.toString();
	}

}
