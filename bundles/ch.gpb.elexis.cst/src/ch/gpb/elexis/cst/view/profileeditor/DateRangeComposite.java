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
package ch.gpb.elexis.cst.view.profileeditor;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.service.CstService;

public class DateRangeComposite extends CstComposite {

	Text txtCol1, txtCol2, txtCol3;
	Slider sldCol1, sldCol2, sldCol3;
	Label lblCol1, lblCol2, lblCol3, lblDiff1, lblDiff2, lblDiff3, lblMessage;

	CDateTime cdtValidFrom;
	CDateTime cdtPeriod1Start;
	CDateTime cdtPeriod1End;
	CDateTime cdtPeriod2Start;
	CDateTime cdtPeriod2End;
	CDateTime cdtPeriod3Start;
	CDateTime cdtPeriod3End;

	/**
	 * period1 = the most recent period period2 = the 2nd most recent period (middle
	 * one) period1 = the 3rd most recent period (the oldest)
	 *
	 */

	public DateRangeComposite(Composite parent, int style) {
		super(parent, style);

		InitDatesMouseAdapter initMouseAdapter = new InitDatesMouseAdapter();

		lblCol1 = new Label(this, SWT.NONE);
		lblCol1.addMouseListener(initMouseAdapter);

		lblCol1.setSize(300, 20);
		lblCol1.setText(Messages.Cst_Text_erste_Periode);

		cdtPeriod1Start = new CDateTime(this, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

		lblDiff1 = new Label(this, SWT.NONE);
		lblDiff1.setSize(30, 20);
		lblDiff1.setText(Messages.Cst_Text_difference);

		cdtPeriod1End = new CDateTime(this, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

		lblCol2 = new Label(this, SWT.NONE);
		lblCol2.setSize(300, 20);
		lblCol2.setText(Messages.Cst_Text_zweite_Periode);
		lblCol2.addMouseListener(initMouseAdapter);

		cdtPeriod2Start = new CDateTime(this, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

		lblDiff2 = new Label(this, SWT.NONE);
		lblDiff2.setSize(30, 20);
		lblDiff2.setText(Messages.Cst_Text_difference);

		cdtPeriod2End = new CDateTime(this, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

		lblCol3 = new Label(this, SWT.NONE);
		lblCol3.setSize(300, 20);
		lblCol3.setText(Messages.Cst_Text_dritte_Periode);
		lblCol3.addMouseListener(initMouseAdapter);

		cdtPeriod3Start = new CDateTime(this, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

		lblDiff3 = new Label(this, SWT.NONE);
		lblDiff3.setSize(30, 20);
		lblDiff3.setText(Messages.Cst_Text_difference);

		cdtPeriod3End = new CDateTime(this, CDT.BORDER | CDT.DROP_DOWN | CDT.DATE_MEDIUM | CDT.TEXT_TRAIL);

		lblMessage = new Label(this, SWT.NONE);
		GridData gdMessage = new GridData();
		gdMessage.horizontalSpan = 3;

		initDateWidgets();
		renderDiffLabels();

		cdtPeriod1End.setSelection(new Date());

		cdtPeriod1Start.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (cdtPeriod1Start.getSelection().after(cdtPeriod1End.getSelection())) {

					cdtPeriod1Start.setBackground(COLOR_RED);
					cdtPeriod2End.setBackground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_startdate_enddate);
					lblMessage.setForeground(COLOR_RED);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod1End.getSelection());
					cal.add(Calendar.DATE, -1);
					cdtPeriod1Start.setSelection(cal.getTime());
					cdtPeriod2End.setSelection(cal.getTime());
					lblMessage.setForeground(COLOR_RED);

				} else {
					cdtPeriod2End.setSelection(cdtPeriod1Start.getSelection());
					resetErrorMarkers();
				}

				renderDiffLabels();
			}
		});

		cdtPeriod1End.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (cdtPeriod1End.getSelection().before(cdtPeriod1Start.getSelection())) {
					lblMessage.setForeground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_enddate_startdate);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod1End.getSelection());
					cal.add(Calendar.DATE, 1);
					cdtPeriod2End.setSelection(cal.getTime());
					cdtPeriod1Start.setBackground(COLOR_RED);
				} else {
					resetErrorMarkers();
				}
				renderDiffLabels();
			}
		});

		cdtPeriod2Start.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (cdtPeriod2Start.getSelection().after(cdtPeriod2End.getSelection())) {
					cdtPeriod2Start.setBackground(COLOR_RED);
					cdtPeriod3End.setBackground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_startdate_enddate);
					lblMessage.setForeground(COLOR_RED);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod1End.getSelection());
					cal.add(Calendar.DATE, -1);
					cdtPeriod2Start.setSelection(cal.getTime());
					cdtPeriod3End.setSelection(cal.getTime());
					lblMessage.setForeground(COLOR_RED);

				} else {
					cdtPeriod3End.setSelection(cdtPeriod2Start.getSelection());
					resetErrorMarkers();
				}
				renderDiffLabels();
			}
		});

		cdtPeriod2End.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (cdtPeriod2End.getSelection().before(cdtPeriod2Start.getSelection())) {
					lblMessage.setForeground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_enddate_startdate);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod2End.getSelection());
					cal.add(Calendar.DATE, 1);
					cdtPeriod3End.setSelection(cal.getTime());

					cdtPeriod2Start.setBackground(COLOR_RED);

				} else if (cdtPeriod2End.getSelection().after(cdtPeriod1Start.getSelection())) {
					lblMessage.setForeground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_enddate_startdate);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod1Start.getSelection());
					cal.add(Calendar.DATE, -1);
					cdtPeriod2End.setSelection(cal.getTime());

					cdtPeriod1Start.setBackground(COLOR_RED);
				} else {
					resetErrorMarkers();
				}
				renderDiffLabels();

			}
		});

		cdtPeriod3Start.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (cdtPeriod3Start.getSelection().after(cdtPeriod3End.getSelection())) {

					cdtPeriod3Start.setBackground(COLOR_RED);
					cdtPeriod3End.setBackground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_startdate_enddate);
					lblMessage.setForeground(COLOR_RED);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod1End.getSelection());
					cal.add(Calendar.DATE, -1);
					cdtPeriod3Start.setSelection(cal.getTime());
					lblMessage.setForeground(COLOR_RED);

				} else {
					resetErrorMarkers();
				}
				renderDiffLabels();

			}

		});

		cdtPeriod3End.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				if (cdtPeriod3End.getSelection().before(cdtPeriod3Start.getSelection())) {
					lblMessage.setForeground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_enddate_startdate);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod3End.getSelection());
					cal.add(Calendar.DATE, 1);
					cdtPeriod3End.setSelection(cal.getTime());
					cdtPeriod3Start.setBackground(COLOR_RED);

				} else if (cdtPeriod3End.getSelection().after(cdtPeriod2Start.getSelection())) {
					lblMessage.setForeground(COLOR_RED);
					lblMessage.setText(Messages.Cst_Text_error_enddate_startdate);

					Calendar cal = Calendar.getInstance();
					cal.setTime(cdtPeriod2Start.getSelection());
					cal.add(Calendar.DATE, -1);
					cdtPeriod3End.setSelection(cal.getTime());

					cdtPeriod2Start.setBackground(COLOR_RED);
				} else {
					resetErrorMarkers();
				}
				renderDiffLabels();
			}
		});

		setLayout(new PictureLabelLayout());

	}

	class InitDatesMouseAdapter extends MouseAdapter {
		@Override
		public void mouseDown(MouseEvent e) {
			initDateWidgets();
		}

	}

	private void resetErrorMarkers() {
		cdtPeriod1Start.setBackground(BLACK);
		cdtPeriod1End.setBackground(BLACK);

		cdtPeriod2Start.setBackground(BLACK);
		cdtPeriod2End.setBackground(BLACK);

		cdtPeriod3Start.setBackground(BLACK);
		cdtPeriod3End.setBackground(BLACK);

		lblMessage.setText(StringUtils.EMPTY);
		lblMessage.setForeground(BLACK);

	}

	private void renderDiffLabels() {
		if (cdtPeriod1Start.getSelection().before(cdtPeriod1End.getSelection())) {
			int days = CstService.getDaysBetweenDates(cdtPeriod1Start.getSelection(), cdtPeriod1End.getSelection());
			lblDiff1.setText(String.valueOf(days));
		}

		if (cdtPeriod2Start.getSelection().before(cdtPeriod2End.getSelection())) {
			int days = CstService.getDaysBetweenDates(cdtPeriod2Start.getSelection(), cdtPeriod2End.getSelection());
			lblDiff2.setText(String.valueOf(days));
		}

		if (cdtPeriod3Start.getSelection().before(cdtPeriod3End.getSelection())) {
			int days = CstService.getDaysBetweenDates(cdtPeriod3Start.getSelection(), cdtPeriod3End.getSelection());
			lblDiff3.setText(String.valueOf(days));
		}

	}

	public void initDateWidgets() {
		Calendar cal = Calendar.getInstance();
		cdtPeriod1End.setSelection(cal.getTime());

		cal.add(Calendar.DATE, -365);
		cdtPeriod1Start.setSelection(cal.getTime());
		cdtPeriod2End.setSelection(cal.getTime());

		cal.add(Calendar.DATE, -365);
		cdtPeriod2Start.setSelection(cal.getTime());
		cdtPeriod3End.setSelection(cal.getTime());

		cal.add(Calendar.DATE, -365);
		cdtPeriod3Start.setSelection(cal.getTime());
	}

	public void setDateStartPeriod1(Date date) {
		this.cdtPeriod1Start.setSelection(date);
	}

	public Date getDateStartPeriod1() {
		return this.cdtPeriod1Start.getSelection();
	}

	public void setDateEndPeriod1(Date date) {
		this.cdtPeriod1End.setSelection(date);
	}

	public Date getDateEndPeriod1() {
		return this.cdtPeriod1End.getSelection();
	}

	public void setDateStartPeriod2(Date date) {
		this.cdtPeriod2Start.setSelection(date);
	}

	public Date getDateStartPeriod2() {
		return this.cdtPeriod2Start.getSelection();
	}

	public void setDateEndPeriod2(Date date) {
		this.cdtPeriod2End.setSelection(date);
	}

	public Date getDateEndPeriod2() {
		return this.cdtPeriod2End.getSelection();
	}

	public void setDateStartPeriod3(Date date) {
		this.cdtPeriod3Start.setSelection(date);
	}

	public Date getDateStartPeriod3() {
		return this.cdtPeriod3Start.getSelection();
	}

	public void setDateEndPeriod3(Date date) {
		this.cdtPeriod3End.setSelection(date);
	}

	public Date getDateEndPeriod3() {
		return this.cdtPeriod3End.getSelection();
	}

	public void setLabelColor(Color color) {
		lblCol1.setForeground(color);
		lblCol2.setForeground(color);
		lblCol3.setForeground(color);
	}

}

class PictureLabelLayout extends Layout {
	Point iExtent, tExtent, extentLabel1, extentSlider1, extentText1; // the cached sizes

	protected Point computeSize(Composite composite, int wHint, int hHint, boolean changed) {
		return new Point(500, 150);
	}

	protected void layout(Composite composite, boolean changed) {

		Control[] children = composite.getChildren();

		children[0].setBounds(5, 13, 150, 20);
		children[1].setBounds(155, 10, 150, 20);
		children[2].setBounds(315, 13, 30, 20);
		children[3].setBounds(345, 10, 150, 20);

		children[4].setBounds(5, 43, 150, 20);
		children[5].setBounds(155, 40, 150, 20);
		children[6].setBounds(315, 43, 30, 20);
		children[7].setBounds(345, 40, 150, 20);

		children[8].setBounds(5, 73, 150, 20);
		children[9].setBounds(155, 70, 150, 20);
		children[10].setBounds(315, 73, 30, 20);
		children[11].setBounds(345, 70, 150, 20);

		children[12].setBounds(160, 95, 300, 20);

	}
}
