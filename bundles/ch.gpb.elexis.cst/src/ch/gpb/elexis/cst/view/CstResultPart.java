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
package ch.gpb.elexis.cst.view;

import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import ch.elexis.befunde.Messwert;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;
import ch.gpb.elexis.cst.Activator;
import ch.gpb.elexis.cst.Messages;
import ch.gpb.elexis.cst.data.CstGastroColo;
import ch.gpb.elexis.cst.data.CstGroup;
import ch.gpb.elexis.cst.data.CstProfile;
import ch.gpb.elexis.cst.data.CstProimmun;
import ch.gpb.elexis.cst.data.LabItemWrapper;
import ch.gpb.elexis.cst.data.ValuePairTimeline;
import ch.gpb.elexis.cst.data.ValueSingleTimeline;
import ch.gpb.elexis.cst.dialog.PdfOptionsDialog;
import ch.gpb.elexis.cst.preferences.CstPreference;
import ch.gpb.elexis.cst.service.CstService;
import ch.gpb.elexis.cst.util.ImageUtils;
import ch.gpb.elexis.cst.widget.GastroColoCanvas;
import ch.gpb.elexis.cst.widget.ValuePairTimelineCanvas;
import ch.gpb.elexis.cst.widget.ValueSingleTimelineCanvas;
import ch.rgw.tools.TimeTool;

/**
 *
 * @author daniel created: 11.01.2015
 *
 *         this is the super class for the custom result views. child classes
 *         must implement the layout method (createPartControl)
 *
 *         595 / 842 794 / 1123
 */
// TODO: the output mechanism with SWT widgets may seem clumsy at first sight,
// but an output via a generated and buffered image has the same limitations, that is,
// it simply stops being displayed at around 30'000 px heigth.
// There seems to be some inner limitation that has to be investigated.

public abstract class CstResultPart extends ViewPart implements IActivationListener {
	/**
	 * We make a String constant for the ID to reference it from the perspective
	 */
	// public static final String ID = "ch.gpb.elexis.cst.cstresultview";
	protected Logger log = LoggerFactory.getLogger(CstResultPart.class.getName());

	Patient patient;
	CstProfile profile;
	Composite baseComposite;
	Font fontSmall;
	Font fontBig;
	Font fontMedium;

	Color RED;
	Color YELLOW;
	Color WHITE;
	Color BROWN;
	Color ORANGE;
	Color GRAY;
	Color LIGHTGRAY;

	String[] flds = null;
	private Action actionScreenshot;
	private Action actionPdf;

	public static int OUTPUTWIDTH = 794;
	public static int OUTPUTHEIGTH = 1123;

	boolean a4Quer = false;

	/**
	 * This is a default implemetation of an ElexisEventListener. It listens for
	 * SelectionEVents and UnselectionEvents on Instances of the Patient class.
	 */

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);

		Font initialFont = site.getShell().getFont();

		FontData[] fontDataSmall = initialFont.getFontData();
		for (int i = 0; i < fontDataSmall.length; i++) {
			fontDataSmall[i].setHeight(7);
		}
		fontSmall = new Font(site.getShell().getDisplay(), fontDataSmall);

		FontData[] fontDataBig = initialFont.getFontData();
		for (int i = 0; i < fontDataBig.length; i++) {
			fontDataBig[i].setHeight(12);
		}
		fontBig = new Font(site.getShell().getDisplay(), fontDataBig);

		FontData[] fontDataMedium = initialFont.getFontData();
		for (int i = 0; i < fontDataMedium.length; i++) {
			fontDataMedium[i].setHeight(10);
		}
		fontMedium = new Font(site.getShell().getDisplay(), fontDataMedium);

		RED = UiDesk.getColorFromRGB("FF2222");
		YELLOW = UiDesk.getColorFromRGB("FFEF46");
		WHITE = UiDesk.getColorFromRGB("FFFFFF");
		BROWN = UiDesk.getColorFromRGB("CC9900");
		ORANGE = UiDesk.getColorFromRGB("FFCC66");
		GRAY = UiDesk.getColorFromRGB("888888");
		LIGHTGRAY = UiDesk.getColorFromRGB("DDDDDD");
		;

	}

	/**
	 * On disposal,the IActivationListener MUST be unregistered. Also, our
	 * RestrictedAction must be unregistered from the AutoAdapt queue to prevent
	 * memory leaks.
	 */
	@Override
	public void dispose() {
		GlobalEventDispatcher.removeActivationListener(this, this);

		fontSmall.dispose();
		fontBig.dispose();
		fontMedium.dispose();
		/*
		 * RED.dispose(); WHITE.dispose(); YELLOW.dispose(); BROWN.dispose();
		 * ORANGE.dispose();
		 */
		super.dispose();
	}

	/**
	 * As the Elexis User changes, he or she has propably different righs to see and
	 * modify our data, than the previous user. So we catch the USER_CHANGED Event
	 * and react accordingly.
	 */

	/**
	 * This is the right place to create all UI elements. The parent composite
	 * already has a GridLayout.
	 */
	@Override
	public void createPartControl(Composite parent) {

		// TODO: Actually, ScrolledComposite requires a FillLayout to work properly
		// but now, this needs some work because the width isn't correct anymore in PDF
		// output.
		// GridLayout parentLayout = new GridLayout(1, false);
		FillLayout parentLayout = new FillLayout(SWT.VERTICAL);

		parent.setLayout(parentLayout);
		parent.setBackground(WHITE);

		GridLayout baseLayout = new GridLayout(1, false);
		baseLayout.numColumns = 1;

		final ScrolledComposite sc1 = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		baseComposite = new Composite(sc1, SWT.NONE);
		baseComposite.setLayout(baseLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessVerticalSpace = true;
		baseComposite.setLayoutData(gd);
		baseComposite.setBackground(RED);

		// OUTPUTWIDTH = OUTPUTHEIGTH;

		sc1.setContent(baseComposite);
		sc1.setMinSize(OUTPUTWIDTH, 800);
		sc1.setSize(OUTPUTWIDTH, 800);

		sc1.setExpandHorizontal(true);

		sc1.addListener(SWT.MouseWheel, new Listener() {
			public void handleEvent(Event event) {
				int wheelCount = event.count;
				wheelCount = (int) Math.ceil(wheelCount / 3.0f);
				while (wheelCount < 0) {
					sc1.getVerticalBar().setIncrement(50);
					wheelCount++;
				}

				while (wheelCount > 0) {
					sc1.getVerticalBar().setIncrement(-50);
					wheelCount--;
				}
			}
		});

		// TODO: no idea what this is doing, but it works - find out!
		// probably the sc is not getting the focus but the parent without this
		sc1.addListener(SWT.Activate, new Listener() {
			public void handleEvent(Event e) {
				sc1.setFocus();
				sc1.getVerticalBar().setIncrement(40);
			}
		});

		Point pBaseComp = baseComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		baseComposite.setSize(pBaseComp);

		makeActions(baseComposite);
		contributeToActionBars();
		GlobalEventDispatcher.addActivationListener(this, this);

	}

	@Override
	public void setFocus() {
		// Don't mind

	}

	/**
	 * From IActivationListener: the view was activated or inactivated
	 *
	 * @param mode
	 */
	public void activation(boolean mode) {
		// don't mind

	}

	/**
	 * inheriting class must implement this method
	 *
	 * @param aProfile
	 */
	public abstract void layoutDisplay(CstProfile aProfile);

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(actionScreenshot);
		manager.add(actionPdf);
		manager.add(new Separator());
	}

	protected String getHeader(Patient patient) {
		String title = Messages.Cst_Text_Interpretation_Mitochondrienlabor + " (" + profile.getName() + ") "
				+ Messages.Cst_Text_fuer + StringUtils.SPACE + patient.getName() + StringUtils.SPACE
				+ patient.getVorname() + StringUtils.SPACE + patient.getGeburtsdatum();
		return title;
	}

	protected String getSubTitle(Patient patient, CstProfile aProfile) {
		String title = Messages.CstProfileEditor_Datum + ": " + CstService.getReadableDateAndTime() + "    ("
				+ Messages.Cst_Text_Auswertungstyp_effektiv + StringUtils.SPACE + Messages.Cst_Text_startdatum
				+ StringUtils.SPACE + CstService.getGermanFromCompact(aProfile.getValidFrom()) + StringUtils.SPACE
				+ Messages.CstProfileEditor_Crawlback + StringUtils.SPACE + aProfile.getCrawlBack() + ")";
		return title;
	}

	protected void addLine(Composite comp, int indent) {
		// baseComposite.pack();
		comp.pack();

		Label labelLine = new Label(comp, SWT.NONE);
		// Image imgLine = UiDesk.getImage(Activator.IMG_LINE_NAME);
		// labelLine.setImage(imgLine);

		GridData gdLine = new GridData(SWT.None);
		gdLine.verticalIndent = indent;
		labelLine.setLayoutData(gdLine);

		comp.pack();

	}

	/**
	 * Check the length of the composite and add a Label with a vertical indent that
	 * matches the remaining heigth.
	 *
	 * @param comp
	 */
	protected void checkPageBreak(Composite comp) {
		// baseComposite.pack();
		comp.pack();
		int currentHeigth = comp.getSize().y;
		int printHeigth = profile.getAusgabeRichtung() ? 794 : 1123;

		int pageCnt = currentHeigth / printHeigth;
		int rmn = ((pageCnt + 1) * printHeigth) - currentHeigth;

		if (rmn < 250) {
			addLine(comp, rmn);

		}

	}

	protected void addNoValuesLabel(Composite composite) {
		StringBuffer lblText = new StringBuffer(Messages.CstResultEffektiv_hinweis_keine_werte);
		Label lblNoValues = new Label(composite, SWT.NONE);
		GridData gdNoValues = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gdNoValues.grabExcessHorizontalSpace = true;
		gdNoValues.horizontalAlignment = SWT.FILL;

		if (profile.getAusgabeRichtung()) {
			gdNoValues.widthHint = 858;
		} else {
			gdNoValues.widthHint = 530;
		}
		lblNoValues.setLayoutData(gdNoValues);
		lblNoValues.setText(lblText.toString());
		lblNoValues.setForeground(RED);
		lblNoValues.setBackground(WHITE);

	}

	/**
	 * The findings display is always the same for all display modes, call this
	 * method to add them to your custom display class
	 *
	 * @param baseComposite
	 */
	protected void addBefunde(Composite baseComposite) {
		baseComposite.pack();
		checkPageBreak(baseComposite);

		//
		// Anzeige-Element Darmfunktion / Pro Immun
		//
		Composite compoDarmfunktion = new Composite(baseComposite, SWT.BORDER | SWT.FILL);
		compoDarmfunktion.setBackground(WHITE);
		compoDarmfunktion.setSize(780, 400);

		GridLayout dlDarmfunktion = new GridLayout(2, false);
		compoDarmfunktion.setLayout(dlDarmfunktion);
		GridData gdDarmfunktion = new GridData();
		gdDarmfunktion.verticalAlignment = GridData.FILL;
		gdDarmfunktion.grabExcessHorizontalSpace = false;
		gdDarmfunktion.widthHint = 780;

		compoDarmfunktion.setLayoutData(gdDarmfunktion);

		CstProimmun cstProimmun = CstProimmun.getByProfileId(profile.getId());
		if (cstProimmun != null) {

			String sLabeltxt = Messages.Cst_Text_Proimmun + "\r\n" + Messages.Cst_Text_IggAntikoerper + "\r\n" + "\r\n"
					+ cstProimmun.getTested() + "\r\n" + "\r\n" + " von " + cstProimmun.getToBeTested()
					+ StringUtils.SPACE + Messages.Cst_Text_getesteten + "\r\n" + Messages.Cst_Text_Lebensmittel
					+ "\r\n\r\n" + CstService.getGermanFromCompact(cstProimmun.getDatum());

			Label lblDarmfunktion = new Label(compoDarmfunktion, SWT.NONE);
			lblDarmfunktion.setLayoutData(new GridData(GridData.FILL_VERTICAL));
			lblDarmfunktion.setText(sLabeltxt);
			lblDarmfunktion.setFont(fontSmall);

			Composite compoRightHalf = new Composite(compoDarmfunktion, SWT.FILL);
			compoRightHalf.setLayout(new GridLayout(2, false));

			Label lblReaktion4 = new Label(compoRightHalf, SWT.NONE);
			lblReaktion4.setForeground(BROWN);
			lblReaktion4.setText(Messages.CstProfileEditor_Reaktionsstaerke4);
			lblReaktion4.setLayoutData(new GridData(SWT.NONE));
			lblReaktion4.setFont(fontSmall);

			Text txtReaktion4 = new Text(compoRightHalf, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData gdReaktion4 = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdReaktion4.widthHint = 540;
			gdReaktion4.heightHint = 55;
			txtReaktion4.setLayoutData(gdReaktion4);
			txtReaktion4.setText(cstProimmun.getText4());
			txtReaktion4.setFont(fontSmall);
			txtReaktion4.setEditable(false);
			txtReaktion4.setBackground(WHITE);

			Label lblReaktion3 = new Label(compoRightHalf, SWT.NONE);
			lblReaktion3.setText(Messages.CstProfileEditor_Reaktionsstaerke3);
			lblReaktion3.setForeground(RED);
			lblReaktion3.setLayoutData(new GridData(SWT.NONE));
			lblReaktion3.setFont(fontSmall);

			Text txtReaktion3 = new Text(compoRightHalf, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData gdReaktion3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdReaktion3.widthHint = 540;
			gdReaktion3.heightHint = 55;
			txtReaktion3.setLayoutData(gdReaktion3);
			txtReaktion3.setText(cstProimmun.getText3());
			txtReaktion3.setFont(fontSmall);
			txtReaktion3.setEditable(false);
			txtReaktion3.setBackground(WHITE);

			Label lblReaktion2 = new Label(compoRightHalf, SWT.NONE);
			lblReaktion2.setText(Messages.CstProfileEditor_Reaktionsstaerke2);
			lblReaktion2.setForeground(ORANGE);
			lblReaktion2.setLayoutData(new GridData(SWT.NONE));
			lblReaktion2.setFont(fontSmall);

			Text txtReaktion2 = new Text(compoRightHalf, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData gdReaktion2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdReaktion2.heightHint = 55;
			gdReaktion2.widthHint = 540;
			txtReaktion2.setLayoutData(gdReaktion2);
			txtReaktion2.setText(cstProimmun.getText2());
			txtReaktion2.setFont(fontSmall);
			txtReaktion2.setEditable(false);
			txtReaktion2.setBackground(WHITE);

			Label lblReaktion1 = new Label(compoRightHalf, SWT.NONE);
			lblReaktion1.setText(Messages.CstProfileEditor_Reaktionsstaerke1);
			lblReaktion1.setForeground(YELLOW);
			lblReaktion1.setLayoutData(new GridData(SWT.NONE));
			lblReaktion1.setFont(fontSmall);
			Text txtReaktion1 = new Text(compoRightHalf, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
			GridData gdReaktion1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gdReaktion1.widthHint = 540;
			gdReaktion1.heightHint = 55;
			txtReaktion1.setLayoutData(gdReaktion1);
			txtReaktion1.setText(cstProimmun.getText1());
			txtReaktion1.setFont(fontSmall);
			txtReaktion1.setEditable(false);
			txtReaktion1.setBackground(WHITE);

			cstProimmun.getDatum();
		}

		baseComposite.pack();
		checkPageBreak(baseComposite);

		//
		// Anzeige-Element Fixmedikation
		//
		if (patient.getFixmedikation().length > 0) {

			Composite compoFixmedi = new Composite(baseComposite, SWT.BORDER | SWT.FILL);
			// compoFixmedi.setSize(980, SWT.FILL);
			compoFixmedi.setBackground(WHITE);
			compoFixmedi.setLayout(new GridLayout(1, false));
			GridData gdFixmedi = new GridData();
			gdFixmedi.grabExcessHorizontalSpace = false;
			gdFixmedi.horizontalAlignment = SWT.BEGINNING;
			gdFixmedi.widthHint = 780;
			compoFixmedi.setLayoutData(gdFixmedi);

			Label lFixmediTitel = new Label(compoFixmedi, SWT.FILL);
			GridData gdFixmediTitel = new GridData();
			lFixmediTitel.setLayoutData(gdFixmediTitel);
			lFixmediTitel.setText(profile.getTherapievorschlag());
			lFixmediTitel.setSize(200, 40);
			lFixmediTitel.setText(Messages.Cst_Text_Fixmedikation);
			lFixmediTitel.setFont(fontBig);
			lFixmediTitel.setBackground(WHITE);

			Label lblFixmedi = new Label(compoFixmedi, SWT.FILL);
			GridData gdFixText = new GridData(GridData.FILL);
			gdFixText.grabExcessHorizontalSpace = true;

			lblFixmedi.setLayoutData(gdFixText);
			// log.info("Medikation: " + patient.getFixmedikation());
			Prescription[] prescriptions = patient.getFixmedikation();
			StringBuffer sb = new StringBuffer();
			for (Prescription prescription : prescriptions) {
				log.info("Prescription: " + prescription.getLabel() + "/" + prescription.getDosis());
				sb.append(prescription.getLabel());
				sb.append("\r\n");
			}
			lblFixmedi.setText(sb.toString());

			lblFixmedi.setFont(fontSmall);
			lblFixmedi.setBackground(WHITE);
		}
		baseComposite.pack();
		checkPageBreak(baseComposite);

		//
		// Anzeige-Element Befunde
		//

		Map<Object, Object> mAuswahl = profile.getMap(CstProfile.KEY_AUSWAHLBEFUNDE);
		List<String> befundFelder = CstService.getBefundArtenFields();
		for (String befundFeld : befundFelder) {

			if (CstService.isBefundSelected(mAuswahl, befundFeld)) {

				Composite compoBefunde = new Composite(baseComposite, SWT.BORDER | SWT.FILL);
				// compoBefunde.setSize(980, SWT.FILL);
				compoBefunde.setBackground(WHITE);
				// compoBefunde.setLayout(new GridLayout(2, false));
				compoBefunde.setLayout(new GridLayout(1, false));
				GridData gdBefunde = new GridData();
				gdBefunde.horizontalAlignment = GridData.BEGINNING;
				gdBefunde.grabExcessHorizontalSpace = false;
				gdBefunde.widthHint = 780;

				/*
				 * if (profile.getAusgabeRichtung()) { gdBefunde.widthHint = 1120; }
				 */

				compoBefunde.setLayoutData(gdBefunde);
				// when the befund is selected for display, search for a separator

				String befundParameter = CstService.getBefundArtOfField(profile, befundFeld);

				String separator = CstService.getBefundArtSeparator(mAuswahl, befundFeld);
				if (separator != null) {
					// Get Values for ValuePair
					List<ValuePairTimeline> values = getValuesForValuePairTimeline(patient, befundParameter, befundFeld,
							separator);

					Composite compoValuePair = new Composite(compoBefunde, SWT.FILL);
					GridLayout gridLayoutBase = new GridLayout();
					gridLayoutBase.numColumns = 1;
					compoValuePair.setLayout(gridLayoutBase);
					compoValuePair.setSize(490, 300);
					GridData gdBlutdruck = new GridData();
					gdBlutdruck.horizontalAlignment = SWT.CENTER;
					compoValuePair.setLayoutData(gdBlutdruck);

					String[] splits = befundFeld.split(Messwert.SETUP_CHECKSEPARATOR);

					ValuePairTimelineCanvas bdCanvas = new ValuePairTimelineCanvas(compoValuePair, SWT.FILL,
							befundParameter + " (" + splits[0] + ")", splits[0]);
					bdCanvas.setLayoutData(new GridData(GridData.BEGINNING));
					bdCanvas.setFindings(values);

				} else {
					// Get Values for SingleValue
					List<ValueSingleTimeline> values = getValuesForSingleValueTimeline(patient, befundParameter,
							befundFeld);

					Composite compoValueSingle = new Composite(compoBefunde, SWT.FILL);
					GridLayout gridLayoutBase = new GridLayout();
					gridLayoutBase.numColumns = 1;
					compoValueSingle.setLayout(gridLayoutBase);
					compoValueSingle.setSize(490, 300);
					GridData gdBlutdruck = new GridData();
					gdBlutdruck.horizontalAlignment = SWT.CENTER;
					compoValueSingle.setLayoutData(gdBlutdruck);

					String[] splits = befundFeld.split(Messwert.SETUP_CHECKSEPARATOR);
					ValueSingleTimelineCanvas bdGewicht = new ValueSingleTimelineCanvas(compoValueSingle, SWT.FILL,
							befundParameter + " (" + splits[0] + ")", splits[0]);
					bdGewicht.setLayoutData(new GridData(GridData.BEGINNING));
					bdGewicht.setFindings(values);

				}
				baseComposite.pack();
				checkPageBreak(baseComposite);

			}

		}

		//
		// Anzeige-Element Darmuntersuchungen / GastroDuodenoskopie - Coloskopie
		//

		CstGastroColo cstGastroColo = CstGastroColo.getByProfileId(profile.getId());

		Composite compoDarm = new Composite(baseComposite, SWT.BORDER | SWT.FILL);
		compoDarm.setBackground(WHITE);
		compoDarm.setLayout(new GridLayout());
		GridData gdDarm = new GridData();
		gdDarm.horizontalAlignment = SWT.BEGINNING;
		gdDarm.grabExcessHorizontalSpace = false;
		gdDarm.widthHint = 780;
		compoDarm.setLayoutData(gdDarm);

		Label lDarm = new Label(compoDarm, SWT.FILL);
		GridData gdDarm2 = new GridData();
		lDarm.setLayoutData(gdDarm2);
		lDarm.setSize(200, 40);
		lDarm.setText(Messages.Cst_Text_Darmuntersuchungen);
		lDarm.setFont(fontBig);
		lDarm.setBackground(WHITE);

		GastroColoCanvas gcCanvas = new GastroColoCanvas(compoDarm, SWT.NONE, cstGastroColo);
		gcCanvas.setLayoutData(new GridData(SWT.FILL));

		checkPageBreak(baseComposite);

		//
		// Anzeige-Element Therapievorschlag
		//

		Composite compoTherapie = new Composite(baseComposite, SWT.BORDER | SWT.FILL);
		// compoTherapie.setSize(980, SWT.FILL);
		compoTherapie.setBackground(WHITE);
		compoTherapie.setLayout(new GridLayout());
		GridData gdTherapie = new GridData();
		gdTherapie.horizontalAlignment = GridData.BEGINNING;
		gdTherapie.grabExcessHorizontalSpace = false;
		gdTherapie.widthHint = 780;

		compoTherapie.setLayoutData(gdTherapie);

		Label lTherapie = new Label(compoTherapie, SWT.FILL);
		GridData gdTherapie1 = new GridData();
		lTherapie.setLayoutData(gdTherapie1);
		lTherapie.setSize(200, 40);
		lTherapie.setText(Messages.Cst_Text_Therapievorschlag);
		lTherapie.setFont(fontBig);
		lTherapie.setBackground(WHITE);

		Text txtTherapie = new Text(compoTherapie, SWT.READ_ONLY | SWT.WRAP);
		txtTherapie.setFont(fontSmall);
		GridData gdTherapie2 = new GridData(GridData.FILL_VERTICAL);
		gdTherapie2.verticalAlignment = SWT.TOP;
		gdTherapie2.grabExcessVerticalSpace = true;
		// gdTherapie2.widthHint = 980;
		txtTherapie.setLayoutData(gdTherapie2);
		txtTherapie.setBackground(WHITE);
		txtTherapie.setText(profile.getTherapievorschlag());

		checkPageBreak(baseComposite);

		//
		// Anzeige-Element Diagnose
		//
		Composite compoDiagnose = new Composite(baseComposite, SWT.BORDER | SWT.FILL);
		// compoTherapie.setSize(980, SWT.FILL);
		compoDiagnose.setBackground(WHITE);
		compoDiagnose.setLayout(new GridLayout());
		GridData gdDiagnose = new GridData();
		gdDiagnose.horizontalAlignment = GridData.BEGINNING;
		gdDiagnose.grabExcessHorizontalSpace = false;
		gdDiagnose.widthHint = 780;

		compoDiagnose.setLayoutData(gdDiagnose);

		Label lDiagnose = new Label(compoDiagnose, SWT.FILL);
		GridData gdDiagnose1 = new GridData();
		lDiagnose.setLayoutData(gdDiagnose1);
		lDiagnose.setText(profile.getTherapievorschlag());
		lDiagnose.setSize(200, 40);
		lDiagnose.setText(Messages.CstProfileEditor_Diagnose);
		lDiagnose.setFont(fontBig);
		lDiagnose.setBackground(WHITE);

		Text txtDiagnose = new Text(compoDiagnose, SWT.READ_ONLY | SWT.WRAP);
		txtDiagnose.setFont(fontSmall);
		GridData gdDiagnose2 = new GridData(GridData.FILL_VERTICAL);
		gdDiagnose2.verticalAlignment = SWT.TOP;
		gdDiagnose2.grabExcessVerticalSpace = true;
		// gdDiagnose2.widthHint = 980;
		txtDiagnose.setLayoutData(gdDiagnose2);
		txtDiagnose.setBackground(WHITE);
		txtDiagnose.setText(profile.getDiagnose());

		// Setting the final size of the base composite
		// baseComposite.setSize(OUTPUTWIDTH, newHeigth + 400);
		checkPageBreak(baseComposite);
	}

	private void makeActions(final Control viewer) {

		actionScreenshot = new Action() {
			public void run() {
				if (profile == null) {
					SWTHelper.alert("No profile", "Ohne Profil kann kein Resultat erzeugt werden");
					return;
				}

				GC gc = null;
				Image image = null;
				try {

					String latestPath = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_LATESTPATH, null);
					if (latestPath == null) {
						latestPath = System.getProperty("user.home");
					}

					FileDialog fd = new FileDialog(baseComposite.getShell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath(latestPath);
					String[] filterExt = { "*.png", "*.*" };
					fd.setFilterExtensions(filterExt);
					fd.setFileName(CstService.generateFilename(patient));
					String selected = fd.open();

					if (selected == null) {
						return;
					}

					File selFile = new File(selected);

					ConfigServiceHolder.setUser(CstPreference.CST_IDENTIFIER_LATESTPATH,
							selFile.getParentFile().getAbsolutePath());

					// if (profile.getAnzeigeTyp().toLowerCase().equals("effektiv")) {
					if (profile.getAnzeigeTyp().toLowerCase().equals(CstProfile.ANZEIGETYP_EFFEKTIV)) {

						if (profile.getAusgabeRichtung()) {
							image = new Image(viewer.getDisplay(), 1123, viewer.getBounds().height);

						} else {
							image = new Image(viewer.getDisplay(), 794, viewer.getBounds().height);

						}
					} else {
						image = new Image(viewer.getDisplay(), 794, viewer.getBounds().height);

					}

					ImageLoader loader = new ImageLoader();

					gc = new GC(image);
					viewer.print(gc);

					gc.dispose();

					loader.data = new ImageData[] { image.getImageData() };
					loader.save(selected, SWT.IMAGE_PNG);

				} catch (Exception e) {
					log.error("Error saving png: " + e.toString());
					showMessage("Error while saving PNG", e.getMessage());
				} finally {
					if (image != null) {
						image.dispose();
					}
					if (gc != null) {
						gc.dispose();
					}
				}

			}
		};
		actionScreenshot.setText(Messages.Cst_Text_Save_as_png);
		actionScreenshot.setToolTipText(Messages.Cst_Text_Save_as_png);
		actionScreenshot.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));
		actionScreenshot.setImageDescriptor(Activator.getImageDescriptor(Activator.IMG_PNG_PATH));

		// TODO: die pdf ausgabe ist eine ziemliche Baustelle - �berarbeiten
		actionPdf = new Action() {
			public void run() {

				//////////////////////////
				if (profile == null) {
					SWTHelper.alert("No profile", "Ohne Profil kann kein Resultat erzeugt werden");
					return;
				}

				GC gc = null;
				Image image = null;
				try {
					String latestPath = ConfigServiceHolder.getUser(CstPreference.CST_IDENTIFIER_LATESTPATH, null);
					if (latestPath == null) {
						latestPath = System.getProperty("user.home");
					}

					FileDialog fd = new FileDialog(baseComposite.getShell(), SWT.SAVE);
					fd.setText("Save");
					fd.setFilterPath(latestPath);
					String[] filterExt = { "*.pdf", "*.*" };
					fd.setFilterExtensions(filterExt);
					fd.setFileName(CstService.generateFilename(patient));
					String selected = fd.open();

					if (selected == null) {
						return;
					}
					File selFile = new File(selected);

					ConfigServiceHolder.setUser(CstPreference.CST_IDENTIFIER_LATESTPATH,
							selFile.getParentFile().getAbsolutePath());

					int printHeigth = 1123;
					int printWidth = 794;
					if (profile.getAusgabeRichtung()) {
						printHeigth = 794;
						printWidth = 1123;

					}

					// get the image from the viewport
					image = new Image(viewer.getDisplay(), printWidth, viewer.getBounds().height);
					ImageLoader loader = new ImageLoader();

					gc = new GC(image);
					viewer.print(gc);
					gc.dispose();

					// prepare title data
					// Date date = new Date();
					// SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

					Patient patient = Patient.load(profile.getKontaktId());
					// String sTitle = "Gemeinschaftspraxis Brunnmatt Dr. Beat K�nzi ";
					String sTitle;
					sTitle = profile.getOutputHeader() == null ? "No header configured!" : profile.getOutputHeader();

					if (sTitle == null || sTitle.length() == 0) {
						sTitle = "No header configured!";
					}
					sTitle = sTitle + " Datum: " + CstService.getReadableDateAndTime();

					// get option (paging to A4/ in one piece)
					int pdfOutputOption = 0;
					boolean onePage = true;

					PdfOptionsDialog dialog = new PdfOptionsDialog(baseComposite.getShell());
					dialog.create();

					if (dialog.open() == Window.OK) {
						pdfOutputOption = dialog.getPdfOutputOption();
						if (pdfOutputOption == PdfOptionsDialog.OPTION_ONE_PAGE) {
							onePage = true;
						} else {

							onePage = false;
						}
					}

					float docHeight = viewer.getBounds().height;
					docHeight = docHeight / 7.5f;

					float fontSize = 12f;

					if (docHeight < 360f) {
						docHeight = 360f;
					}

					BufferedImage bimage = ImageUtils.convertToAWT(image.getImageData());

					// create an Itextt Image from AWT BufferedImage
					com.lowagie.text.Image itextImage = null;
					java.awt.Image awtImage = null;

					try {
						awtImage = Toolkit.getDefaultToolkit().createImage(bimage.getSource());
						itextImage = com.lowagie.text.Image.getInstance(awtImage, null);

					} catch (Exception e) {
						log.error("Error on image loading: " + e.toString());
						e.printStackTrace();
						return;
					}

					// only for debugging

					// loader.data = new ImageData[] { image.getImageData() };
					// loader.save("C:\\Users\\daniel\\tmp\\debug.png", SWT.IMAGE_PNG);

					Rectangle pagesize = new Rectangle(595f, itextImage.getHeight() * 0.75f);

					// is it a4 quer?
					if (profile.getAusgabeRichtung()) {
						pagesize = new Rectangle(842f, itextImage.getHeight() * 0.75f);

					}

					// System.out.println("pagesize: " + pagesize.toString());

					Document document;
					if (onePage) {
						document = new Document(pagesize);

					} else {
						document = new Document(PageSize.A4);
						if (profile.getAusgabeRichtung()) {
							document = new Document(PageSize.A4.rotate());
						}

					}

					document.addCreationDate();

					try {
						// creation of the different writers
						PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(selected));

						// various fonts
						BaseFont bf_helv = BaseFont.createFont(BaseFont.HELVETICA, "Cp1252", true);
						BaseFont bf_times = BaseFont.createFont(BaseFont.TIMES_ROMAN, "Cp1252", true);
						BaseFont bf_courier = BaseFont.createFont(BaseFont.COURIER, "Cp1252", true);

						com.lowagie.text.Font fontHelv12 = new com.lowagie.text.Font(bf_helv, fontSize);

						com.lowagie.text.Font fontTimes = new com.lowagie.text.Font(bf_times, fontSize);

						fontTimes.setSize(fontSize);
						fontTimes.setStyle(com.lowagie.text.Font.ITALIC);

						Chunk chunkHeader = new Chunk(sTitle, FontFactory.getFont(FontFactory.HELVETICA, fontSize,
								com.lowagie.text.Font.NORMAL, new java.awt.Color(255, 0, 0)));

						com.lowagie.text.Phrase phraseHeader = new com.lowagie.text.Phrase(chunkHeader);

						// headers and footers must be added before the document
						// is opened

						Chunk chunkFooter = new Chunk("Seite: ", FontFactory.getFont(FontFactory.HELVETICA, fontSize,
								com.lowagie.text.Font.BOLD, new java.awt.Color(0, 0, 0)));

						Phrase phraseFooter = new Phrase(chunkFooter);
						phraseFooter.setFont(fontHelv12);

						HeaderFooter footer = new HeaderFooter(phraseFooter, true);
						footer.setBorder(Rectangle.NO_BORDER);
						footer.setAlignment(Element.ALIGN_CENTER);

						document.setFooter(footer);

						Phrase headerPhrase = new Phrase(sTitle);
						headerPhrase.setFont(fontTimes);

						HeaderFooter header = new HeaderFooter(phraseHeader, false);
						header.setBorder(Rectangle.BOTTOM);
						header.setBorderWidth(0.5f);
						header.setAlignment(Element.ALIGN_LEFT);
						document.setHeader(header);

						document.open();

						// System.out.println("itext image w: " + itextImage.getWidth() + " h:" +
						// itextImage.getHeight());

						if (onePage) {

							int scale = 66;
							itextImage.scalePercent(scale);

							document.add(itextImage);

						} else {

							BufferedImage[] imageChunks = ImageUtils.splitImageByHeigth(bimage, printHeigth);

							for (int i = 0; i < imageChunks.length; i++) {

								com.lowagie.text.Image itextImage2 = com.lowagie.text.Image.getInstance(
										Toolkit.getDefaultToolkit().createImage(imageChunks[i].getSource()), null);

								// width becomes typically 523 (595 - 72) for a4Hoch or 770 (842 - 72) for
								// A4Quer
								float imgWidth = document.getPageSize().getWidth() - document.leftMargin()
										- document.rightMargin();
								float imgHeigth = itextImage.getHeight() * 0.75f;

								itextImage2.setAbsolutePosition(30, 20);
								int scale = 66;
								itextImage2.scalePercent(scale);

								document.add(itextImage2);
								document.newPage();

							}

						}

						// we're done!
						document.close();

						///////////////////////////////

					} catch (Exception ex) {
						log.error(ex.getMessage());
						showMessage("Error while generating PDF", ex.getMessage());
					}

				} finally {
					if (image != null) {
						image.dispose();
					}
					if (gc != null) {
						gc.dispose();
					}
					/*
					 * image.dispose(); gc.dispose();
					 */
				}

			}
		};

		actionPdf.setText(Messages.Cst_Text_Save_as_pdf);
		actionPdf.setToolTipText(Messages.Cst_Text_Save_as_pdf);
		/*
		 * actionPdf.setImageDescriptor(PlatformUI.getWorkbench() .getSharedImages()
		 * .getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		 */
		actionPdf.setImageDescriptor(Activator.getImageDescriptor(Activator.IMG_PDF_PATH));

	}

	protected double[] extractRefValues(LabItem labItem) {
		double[] result = new double[2];

		String sRangeStart = "0";

		if (patient.getGeschlecht().toLowerCase().equals("m")) {

			if (labItem.getRefM() != null) {
				sRangeStart = labItem.getRefM();
			} else {
				if (labItem.getRefW() != null) {
					sRangeStart = labItem.getRefW();
				}
			}

		} else {
			if (labItem.getRefW() != null) {
				sRangeStart = labItem.getRefW();
			} else {
				if (labItem.getRefM() != null) {
					sRangeStart = labItem.getRefM();
				}
			}
		}

		sRangeStart = sRangeStart.trim();
		double dRangeStart = 0;
		double dRangeEnd = 0;

		try {
			if (sRangeStart.startsWith("-")) {
				sRangeStart = sRangeStart.replace("-", StringUtils.EMPTY);
				dRangeEnd = Double.parseDouble(sRangeStart);
				dRangeStart = 0;
			} else if (sRangeStart.startsWith("<")) {
				sRangeStart = sRangeStart.replace("<", StringUtils.EMPTY);
				dRangeEnd = Double.parseDouble(sRangeStart);
				dRangeStart = 0;

			} else if (sRangeStart.startsWith(">")) {
				sRangeStart = sRangeStart.replace(">", StringUtils.EMPTY);
				dRangeStart = Double.parseDouble(sRangeStart);
				dRangeEnd = 0;

			} // if there is only a single number, it's probably the End of range value.
			else if (sRangeStart.matches("\\d*")) {
				dRangeEnd = Double.parseDouble(sRangeStart);
				dRangeStart = 0;

			} else {
				String[] values = sRangeStart.split("-");
				dRangeStart = Double.parseDouble(values[0]);
				dRangeEnd = Double.parseDouble(values[1]);

			}
		} catch (NumberFormatException e) {
			log.error("NumberFormatException for start range of  Pat ID:"/* + aProfile.getKontaktId() */
					+ ":" + labItem.getName() + ":" + "/" + sRangeStart + e.getMessage(), Log.ERRORS);
		} catch (ArrayIndexOutOfBoundsException e) {
			log.error("ArrayIndexOutOfBoundsException for start range of " + labItem.getName() + ":" + "/" + sRangeStart
					+ e.getMessage(), Log.ERRORS);
		}

		log.debug("Formatting Reference Values of Labitem: " + labItem.getName() + ":\t" + sRangeStart + " => "
				+ dRangeStart + "/" + dRangeEnd);

		result[0] = dRangeStart;
		result[1] = dRangeEnd;

		return result;

	}

	private void showMessage(String title, String msg) {
		MessageDialog.openInformation(UiDesk.getTopShell(), title, msg);
	}

	/**
	 * Holt die Werte der Befunde (so wie in den Anzeigeoptionen ausgew�hlt) f�r
	 * Einzelwerte
	 *
	 * @param pat
	 * @param parm
	 * @param fieldName
	 * @param separator
	 * @return a value object for single values
	 */
	List<ValuePairTimeline> getValuesForValuePairTimeline(final Patient pat, final String parm, final String fieldName,
			final String separator) {
		List<ValuePairTimeline> resultList = new ArrayList<ValuePairTimeline>();
		// row keys...
		final String myparm = parm;

		if (pat != null) {

			// die Messwerte befinden sich in Elexisbefunde
			Query<Messwert> qbe = new Query<Messwert>(Messwert.class);
			qbe.add(Messwert.FLD_PATIENT_ID, Query.EQUALS, pat.getId());
			qbe.add(Messwert.FLD_NAME, Query.EQUALS, myparm);
			List<Messwert> list = qbe.execute();

			Collections.sort(list, new Comparator<Messwert>() {

				public int compare(final Messwert o1, final Messwert o2) {
					TimeTool t1 = new TimeTool(o1.get(Messwert.FLD_DATE));
					TimeTool t2 = new TimeTool(o2.get(Messwert.FLD_DATE));
					return t1.compareTo(t2);
				}
			});

			StringBuffer result = new StringBuffer();

			for (Messwert m : list) {
				result.append(m.get(Messwert.FLD_DATE)); // $NON-NLS-1$
				result.append(m.toString());
				@SuppressWarnings("unchecked")
				Map<String, ?> hash = m.getMap(Messwert.FLD_BEFUNDE); // $NON-NLS-1$

				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				Date date1 = null;
				DateFormat shortDf = DateFormat.getDateInstance(DateFormat.SHORT);

				@SuppressWarnings("unused")
				String da = new String();
				try {
					date1 = formatter.parse(m.get(Messwert.FLD_DATE));
					da = shortDf.format(date1);
				} catch (ParseException e) {
					log.error(e.toString() + StringUtils.SPACE + m.get(Messwert.FLD_DATE));
				}

				Set<String> keys = hash.keySet();
				for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
					Object key = (Object) iterator.next();

					if (fieldName.startsWith(key.toString())) {
						String bd = hash.get(key).toString();

						String[] sysdia = bd.split(separator);

						// sometimes theres no slash in between the 2 values
						if (sysdia.length < 2) {
							// if there is only one value
							sysdia[1] = hash.get(key).toString();
						}
						String diastol = sysdia[1];
						if (diastol.contains(StringUtils.SPACE)) {
							String[] korrD = diastol.split(StringUtils.SPACE);
							sysdia[1] = korrD[0];
						}

						Double dSys = new Double(sysdia[0]);
						Double dDia = new Double(sysdia[1]);
						ValuePairTimeline bdValue = new ValuePairTimeline(CstService.getCompactFromDate(date1),
								dSys.intValue(), dDia.intValue());
						resultList.add(bdValue);

					}
				}

			}

		}
		return resultList;
	}

	/**
	 * Holt die Werte der Befunde (so wie in den Anzeigeoptionen ausgew�hlt) f�r
	 * Wertepaaare
	 *
	 * @param pat
	 * @param parm
	 * @param fieldName
	 * @return a value object for value pairs
	 */
	List<ValueSingleTimeline> getValuesForSingleValueTimeline(final Patient pat, final String parm,
			final String fieldName) {
		List<ValueSingleTimeline> resultList = new ArrayList<ValueSingleTimeline>();
		// row keys...
		final String myparm = parm;

		if (pat != null) {

			Query<Messwert> qbe = new Query<Messwert>(Messwert.class);
			qbe.add(Messwert.FLD_PATIENT_ID, Query.EQUALS, pat.getId());
			qbe.add(Messwert.FLD_NAME, Query.EQUALS, myparm);
			List<Messwert> list = qbe.execute();

			Collections.sort(list, new Comparator<Messwert>() {

				public int compare(final Messwert o1, final Messwert o2) {
					TimeTool t1 = new TimeTool(o1.get(Messwert.FLD_DATE));
					TimeTool t2 = new TimeTool(o2.get(Messwert.FLD_DATE));
					return t1.compareTo(t2);
				}
			});

			StringBuffer result = new StringBuffer();

			for (Messwert messwert : list) {
				result.append(messwert.get(Messwert.FLD_DATE)); // $NON-NLS-1$
				result.append(messwert.toString());
				@SuppressWarnings("unchecked")
				Map<String, ?> hash = messwert.getMap(Messwert.FLD_BEFUNDE); // $NON-NLS-1$

				SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
				Date date1 = null;
				DateFormat shortDf = DateFormat.getDateInstance(DateFormat.SHORT);

				@SuppressWarnings("unused")
				String da = StringUtils.EMPTY;
				try {
					date1 = formatter.parse(messwert.get(Messwert.FLD_DATE));
					da = shortDf.format(date1);
				} catch (ParseException e) {
					log.error(e.toString() + "/" + messwert.get(Messwert.FLD_DATE));
				}

				Set<String> keys = hash.keySet();
				for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
					Object key = (Object) iterator.next();

					if (fieldName.startsWith(key.toString())) {

						String bd = hash.get(key).toString();

						// Strip non-digits, ignoring decimal point
						String maxValue = CstService.getMaximumOfNumbersInString(bd);

						// TODO: again there could be values like "77.4 mit Kleider und Schuhen"

						ValueSingleTimeline wv = new ValueSingleTimeline(CstService.getCompactFromDate(date1),
								new Double(maxValue).doubleValue());

						resultList.add(wv);

					}
				}

			}

		}
		return resultList;
	}

	class LabItemSorter implements Comparator<LabItemWrapper> {

		Map<String, Integer> itemRanking;

		public LabItemSorter(Map<String, Integer> itemRanking) {
			super();
			this.itemRanking = itemRanking;
		}

		@Override
		public int compare(LabItemWrapper o1, LabItemWrapper o2) {
			Integer r1 = (Integer) itemRanking.get(o1.getLabItem().getId());
			Integer r2 = (Integer) itemRanking.get(o2.getLabItem().getId());
			if (r1 == null || r2 == null) {
				return 0;
			}

			return r1.compareTo(r2);
		}

	}

	class GroupSorter implements Comparator<CstGroup> {

		Map<String, Integer> itemRanking;

		public GroupSorter(Map<String, Integer> itemRanking) {
			super();
			this.itemRanking = itemRanking;
		}

		@Override
		public int compare(CstGroup o1, CstGroup o2) {
			Integer r1 = (Integer) itemRanking.get(o1.getId());
			Integer r2 = (Integer) itemRanking.get(o2.getId());
			if (r1 == null || r2 == null) {
				return 0;
			}

			return r1.compareTo(r2);
		}

	}

	/**
	 * From IActivationListener: The View changes visibility Our listeners need only
	 * to be active,if gthe view is visible. So we untregister them if the view
	 * disappears to save resources. If the view becomes visible again, we must send
	 * an event, because the listeners don't know what happend, before the start
	 * listening
	 *
	 * @param mode true: the view becomes visible. false: the view becomes invisible
	 */
	@Override
	public void visible(boolean mode) {


	}

	public void setProfile(CstProfile profile) {
		this.profile = profile;
		patient = Patient.load(profile.getKontaktId());
		log.info("Result for patient: " + patient.getId() + "/" + patient.getName());

		layoutDisplay(profile);
	}

	public boolean isA4Quer() {
		return a4Quer;
	}

	public void setA4Quer(boolean a4Quer) {
		this.a4Quer = a4Quer;
	}

}
