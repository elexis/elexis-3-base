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

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.UiDesk;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.dialog.ProfileOverview;
import ch.gpb.elexis.cst.service.CstService;

public class AnzeigeOptionsComposite extends CstComposite {

	Label lblCrawlback;
	Label lblCrawlbackDate;
	int crawlback = 0;
	Text txtCrawlback;
	Slider sldCrawlback;
	Button btnEffektiv;
	Button btnMinimax;
	Button btnA4Hoch;
	Button btnA4Quer;

	private DateRangeComposite dateRangeComposite;
	private Group grpAusrichtung;
	private Label lblShowProfiles;

	public AnzeigeOptionsComposite(Composite parent) {
		super(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout(5, false);
		setLayout(gridLayout);

		createLayout(this);

		lblShowProfiles = new Label(this, SWT.NONE);
		lblShowProfiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblShowProfiles.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				ProfileOverview dialog = new ProfileOverview(UiDesk.getTopShell());
				dialog.create();

				if (dialog.open() == Window.OK) {

				} else {
					return;
				}
			}
		});

		lblShowProfiles.setText(Messages.AnzeigeOptionsComposite_lblWoSindMeine_text);
		lblShowProfiles.setForeground(COLOR_RED);

		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

	}

	// dynamic Layout elements
	private void createLayout(Composite anzeigeCanvas) {
		GridData gd5 = new GridData();

		gd5.verticalAlignment = SWT.TOP;
		gd5.grabExcessVerticalSpace = true;
		anzeigeCanvas.setLayoutData(gd5);

		// Create the first Group
		Group group1 = new Group(anzeigeCanvas, SWT.NONE);
		group1.setText(Messages.CstProfileEditor_Darstellungsoptionen);
		group1.setLayout(new RowLayout(SWT.VERTICAL));
		btnEffektiv = new Button(group1, SWT.RADIO);
		btnEffektiv.setText(Messages.CstProfileEditor_Effektiv);
		btnEffektiv.setData(CstProfile.ANZEIGETYP_EFFEKTIV);

		btnMinimax = new Button(group1, SWT.RADIO);
		btnMinimax.setText(Messages.CstProfileEditor_MinimalMaximal);
		btnMinimax.setData(CstProfile.ANZEIGETYP_MINIMAX);

		GridData gdGroup1 = new GridData();
		gdGroup1.widthHint = 120;
		gdGroup1.verticalIndent = 20;
		gdGroup1.horizontalSpan = 2;
		group1.setLayoutData(gdGroup1);

		grpAusrichtung = new Group(anzeigeCanvas, SWT.NONE);
		GridData gd_grpAusrichtung = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gd_grpAusrichtung.widthHint = 120;
		gd_grpAusrichtung.verticalIndent = 20;
		grpAusrichtung.setLayoutData(gd_grpAusrichtung);
		RowLayout rl_grpAusrichtung = new RowLayout(SWT.VERTICAL);
		grpAusrichtung.setLayout(rl_grpAusrichtung);
		// grpAusrichtung.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false,
		// 3, 1));
		grpAusrichtung.setText(Messages.Cst_Text_ausrichtung);

		btnA4Hoch = new Button(grpAusrichtung, SWT.RADIO);
		btnA4Hoch.setText(Messages.Cst_Text_a4hoch);

		btnA4Quer = new Button(grpAusrichtung, SWT.RADIO);
		btnA4Quer.setText(Messages.Cst_Text_a4quer);

		lblCrawlback = new Label(anzeigeCanvas, SWT.NONE);
		lblCrawlback.setSize(300, 20);
		lblCrawlback.setText(Messages.CstProfileEditor_Crawlback);
		GridData gdLblCrawlback = new GridData(SWT.BEGINNING);
		gdLblCrawlback.verticalIndent = 20;
		gdLblCrawlback.horizontalIndent = 10;
		lblCrawlback.setLayoutData(gdLblCrawlback);

		sldCrawlback = new Slider(anzeigeCanvas, SWT.HORIZONTAL);
		sldCrawlback.setBounds(115, 50, 25, 15);
		sldCrawlback.setMinimum(0);
		sldCrawlback.setMaximum(5840);
		sldCrawlback.setIncrement(5);
		sldCrawlback.setPageIncrement(10);
		sldCrawlback.setToolTipText(Messages.CstProfileEditor_CrawlbackTooltip);

		GridData gdSliderCrawl = new GridData();
		gdSliderCrawl.verticalIndent = 20;
		gdSliderCrawl.horizontalAlignment = SWT.CENTER;
		gdSliderCrawl.horizontalIndent = 50;
		sldCrawlback.setLayoutData(gdSliderCrawl);

		txtCrawlback = new Text(anzeigeCanvas, SWT.BORDER);
		txtCrawlback.setEditable(false);
		txtCrawlback.setBounds(115, 25, 40, 25);
		txtCrawlback.setText("0");

		GridData gdTextCrawl = new GridData();
		gdTextCrawl.verticalIndent = 20;
		gdTextCrawl.minimumWidth = 60;
		gdTextCrawl.widthHint = 60;
		gdTextCrawl.horizontalAlignment = SWT.RIGHT;
		gdTextCrawl.horizontalIndent = 20;
		txtCrawlback.setLayoutData(gdTextCrawl);

		lblCrawlbackDate = new Label(anzeigeCanvas, SWT.NONE);
		lblCrawlbackDate.setBounds(115, 25, 40, 25);
		GridData gdLblCrawlDate = new GridData();
		gdLblCrawlDate.verticalIndent = 20;
		gdLblCrawlDate.minimumWidth = 60;
		gdLblCrawlDate.widthHint = 60;
		gdLblCrawlDate.horizontalAlignment = SWT.RIGHT;
		gdLblCrawlDate.horizontalIndent = 20;
		lblCrawlbackDate.setLayoutData(gdLblCrawlDate);
		lblCrawlbackDate.setText("Datum");

		sldCrawlback.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				txtCrawlback.setText(new Integer(sldCrawlback.getSelection()).toString());
				lblCrawlbackDate.setText(CstService.getDateFromSubraction(sldCrawlback.getSelection()));
				crawlback = sldCrawlback.getSelection();
			}
		});
		new Label(this, SWT.NONE);

		Label lblPlausiCheck = new Label(anzeigeCanvas, SWT.NONE);
		lblPlausiCheck.setSize(300, 20);
		lblPlausiCheck.setText(Messages.Cst_Text_plausibilty_check);

		GridData gdLblPlausiCheck = new GridData(SWT.BEGINNING);
		gdLblPlausiCheck.horizontalIndent = 10;
		lblPlausiCheck.setLayoutData(gdLblPlausiCheck);
		new Label(this, SWT.NONE);

		Button btnPlausiCheck = new Button(anzeigeCanvas, SWT.CHECK);
		GridData gdBtnPlausiCheck = new GridData(SWT.END);
		gdBtnPlausiCheck.horizontalIndent = 20;
		btnPlausiCheck.setLayoutData(gdBtnPlausiCheck);
		gdBtnPlausiCheck.horizontalSpan = 2;
		btnPlausiCheck.setVisible(false);
		lblPlausiCheck.setVisible(false);
		new Label(this, SWT.NONE);

		dateRangeComposite = new DateRangeComposite(anzeigeCanvas, SWT.NONE);
		GridData gdDateRange = new GridData();

		gdDateRange.horizontalSpan = 5;
		dateRangeComposite.setLayoutData(gdDateRange);

		SelectionListener selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				Button button = ((Button) event.widget);
				System.out.print(button.getText());

				if (button.getData().equals(CstProfile.ANZEIGETYP_EFFEKTIV)) {
					lblCrawlback.setForeground(GREEN);
					dateRangeComposite.setLabelColor(BLACK);
				} else {
					lblCrawlback.setForeground(BLACK);
					dateRangeComposite.setLabelColor(GREEN);
				}

			};
		};

		btnEffektiv.addSelectionListener(selectionListener);
		btnMinimax.addSelectionListener(selectionListener);

		initDates();
	}

	public void initDates() {
		Calendar c = Calendar.getInstance();
		dateRangeComposite.setDateStartPeriod1(CstService.getDateByAddingDays(c.getTime(), -365));
		dateRangeComposite.setDateEndPeriod1(c.getTime());

		dateRangeComposite.setDateEndPeriod2(CstService.getDateByAddingDays(c.getTime(), -365));
		dateRangeComposite.setDateStartPeriod2(CstService.getDateByAddingDays(c.getTime(), -730));

		dateRangeComposite.setDateEndPeriod3(CstService.getDateByAddingDays(c.getTime(), -730));
		dateRangeComposite.setDateStartPeriod3(CstService.getDateByAddingDays(c.getTime(), -1095));

	}

	public int getCrawlback() {
		return crawlback;
	}

	public void setCrawlback(int crawlback) {
		this.crawlback = crawlback;
		this.sldCrawlback.setSelection(crawlback);
		this.txtCrawlback.setText(String.valueOf(crawlback));
		lblCrawlbackDate.setText(CstService.getDateFromSubraction(crawlback));

	}

	public DateRangeComposite getDateRangeComposite() {
		return dateRangeComposite;
	}

	public void setDateRangeComposite(DateRangeComposite dateRangeComposite) {
		this.dateRangeComposite = dateRangeComposite;
	}

	public String getPeriod1StartDate() {
		return CstService.getCompactFromDate(dateRangeComposite.cdtPeriod1Start.getSelection());
	}

	public void setPeriod1StartDate(String sCompactDate) {
		dateRangeComposite.cdtPeriod1Start.setSelection(CstService.getDateFromCompact(sCompactDate));
	}

	public String getPeriod1EndDate() {
		return CstService.getCompactFromDate(dateRangeComposite.cdtPeriod1End.getSelection());
	}

	public void setPeriod1EndDate(String sCompactDate) {
		dateRangeComposite.cdtPeriod1End.setSelection(CstService.getDateFromCompact(sCompactDate));
	}

	public String getPeriod2StartDate() {
		return CstService.getCompactFromDate(dateRangeComposite.cdtPeriod2Start.getSelection());
	}

	public void setPeriod2StartDate(String sCompactDate) {
		dateRangeComposite.cdtPeriod2Start.setSelection(CstService.getDateFromCompact(sCompactDate));
	}

	public String getPeriod2EndDate() {
		return CstService.getCompactFromDate(dateRangeComposite.cdtPeriod2End.getSelection());
	}

	public void setPeriod2EndDate(String sCompactDate) {
		dateRangeComposite.cdtPeriod2End.setSelection(CstService.getDateFromCompact(sCompactDate));
	}

	public String getPeriod3StartDate() {
		return CstService.getCompactFromDate(dateRangeComposite.cdtPeriod3Start.getSelection());
	}

	public void setPeriod3StartDate(String sCompactDate) {
		dateRangeComposite.cdtPeriod3Start.setSelection(CstService.getDateFromCompact(sCompactDate));
	}

	public String getPeriod3EndDate() {
		return CstService.getCompactFromDate(dateRangeComposite.cdtPeriod3End.getSelection());
	}

	public void setPeriod3EndDate(String sCompactDate) {
		dateRangeComposite.cdtPeriod3End.setSelection(CstService.getDateFromCompact(sCompactDate));
	}

	public void setAnzeigeTyp(String sAnzeigeTyp) {
		if (sAnzeigeTyp.startsWith(CstProfile.ANZEIGETYP_EFFEKTIV)) {
			btnEffektiv.setSelection(true);
			btnMinimax.setSelection(false);
		} else {
			btnEffektiv.setSelection(false);
			btnMinimax.setSelection(true);

		}

	}

	public String getAnzeigeTyp() {
		if (btnEffektiv.getSelection()) {
			return CstProfile.ANZEIGETYP_EFFEKTIV;
		} else {
			return CstProfile.ANZEIGETYP_MINIMAX;

		}
	}

	// 0 = hoch, 1 = quer
	public void setAusgabeRichtung(boolean ausgabeRichtung) {
		if (ausgabeRichtung) {
			btnA4Hoch.setSelection(false);
			btnA4Quer.setSelection(true);
		} else {
			btnA4Hoch.setSelection(true);
			btnA4Quer.setSelection(false);

		}

	}

	public boolean getAusgabeRichtung() {
		return btnA4Quer.getSelection();
	}

}
