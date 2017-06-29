/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.rgw.tools.TimeTool;

public class VaccinationCompositePaintListener implements PaintListener {
	private static final String VACCINE = "Impfstoff";
	private static final String TRADENAME = "Handelsname";
	private static final String LOT_NO = "Lot Nr.";
	private static final String ADMINISTRATOR = "Arzt";
	private static final String DATE = "Datum";
	private static final String AGE = "Alter";
	private static final String SIDE = "Seite";
	
	private static int HEADER_HEIGHT = -1;
	private static final int DISTANCE_BETWEEN_DISEASES = 19;
	private static final int SEPARATOR_WIDTH_BASE_EXTENDED = 2;
	private static final int OFFSET = 15;
	
	private final Color COLOR_WHITE = UiDesk.getColor(UiDesk.COL_WHITE);
	private final Color COLOR_BOTTOM = UiDesk.getColorFromRGB("D0DCF2"); // rgb(208, 220, 242)
	private final Color COLOR_DARKGREY = UiDesk.getColor(UiDesk.COL_DARKGREY);
	private final Color COLOR_LIGHTGREY = UiDesk.getColorFromRGB("F0F0F0");
	private final Color COLOR_BLACK = UiDesk.getColor(UiDesk.COL_BLACK);
	private final Color COLOR_GREEN = UiDesk.getColorFromRGB("39961C");
	private final Color COLOR_CREME = UiDesk.getColorFromRGB("FEFFB1"); // rgb(254, 255, 177)
	
	private final Font headerFont, boldFont, defaultFont;
	private final int fontHeightDefaultFont, fontHeightBoldFont;
	private int separatorBoundary, locationOfLotNrBorder, locationOfDocBorder,
			locationOfDateBorder, locationOfAgeBorder, locationOfFirstDisease,
			locationOfSideBorder;
	
	private int lengthOfBasisimpfungen, lengthOfDoctor, lengthOfLotNr, lengthOfDateString,
			lengthOfSide, leftStart;
	
	private List<Vaccination> _vaccinations;
	private List<DiseaseBoundary> diseaseBoundaries = new ArrayList<DiseaseBoundary>();
	
	private VaccinationPlanHeaderDefinition _vphd;
	private Vaccination selectedVacc;
	
	private TimeTool _administrationDate;
	private TimeTool _patientBirthDate;
	
	public NavigableMap<Integer, Vaccination> naviVacMap;
	
	private boolean showSide;
	
	private int maxLengthBasisImpf = 150;
	private int maxLengthDoctor = 100;
	private int maxLengthLotNr = 70;
	
	private int entryHeight;
	private int defaultEntryHeight;
	
	public VaccinationCompositePaintListener(){
		Display disp = Display.getCurrent();
		defaultFont = UiDesk.getFont(Preferences.USR_DEFAULTFONT);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(defaultFont).setStyle(SWT.BOLD);
		boldFont = boldDescriptor.createFont(disp);
		headerFont = new Font(disp, "Helvetica", 16, SWT.BOLD);
		
		fontHeightDefaultFont = defaultFont.getFontData()[0].getHeight() + 5;
		fontHeightBoldFont = boldFont.getFontData()[0].getHeight() + 5;
		
		entryHeight = (fontHeightBoldFont + 4);
		defaultEntryHeight = entryHeight;
		
		naviVacMap = new TreeMap<Integer, Vaccination>();
		selectedVacc = null;
		showSide = CoreHub.userCfg.get(PreferencePage.VAC_SHOW_SIDE, false);
	}
	
	@Override
	public void paintControl(PaintEvent e){
		if (_vphd == null)
			return;
		
		paintControl(e.gc, e.display, e.width, e.height, false);
	}
	
	public void paintControl(GC gc, Display display, int width, int height, boolean b){
		if (b) {
			//do print
			showSide = false;
			width = 800;
			maxLengthBasisImpf = 150;
			maxLengthDoctor = 100;
			maxLengthLotNr = 70;
		}
		else {
			if (width < 800) {
				width = 800;
			}
			if (locationOfLotNrBorder - OFFSET > 0) {
				maxLengthBasisImpf = locationOfLotNrBorder - OFFSET;
			}
			if (locationOfDocBorder - OFFSET > 0) {
				maxLengthLotNr = locationOfDocBorder - OFFSET;
				if (width * 0.10f < maxLengthLotNr) {
					maxLengthLotNr = (int) (width * 0.10f);
				}
			}
			if (locationOfDateBorder - OFFSET > 0) {
				maxLengthDoctor = locationOfDateBorder - OFFSET;
				if (width * 0.20f < maxLengthDoctor) {
					maxLengthDoctor = (int) (width * 0.20f);
				}
			}
		}
		
		// check if 2 row height is needed
		boolean wrapText = false;
		for (Vaccination vacc : _vaccinations) {
			if (wrapTextByWidth(gc, vacc.getShortBusinessName(), maxLengthBasisImpf, false)
				.contains("\n")) {
				wrapText = true;
				break;
			}
			if (wrapTextByWidth(gc, vacc.getLotNo(), maxLengthLotNr, true).contains("\n")) {
				wrapText = true;
				break;
			}
			if (wrapTextByWidth(gc, vacc.getAdministratorLabel(), maxLengthDoctor, false)
				.contains("\n")) {
				wrapText = true;
				break;
			}
		}
		
		entryHeight = defaultEntryHeight;
		if (wrapText) {
			entryHeight = entryHeight + defaultEntryHeight;
		}
		
		determineMinWidth(gc, wrapText);
		drawHeader(gc, display, width);
		drawEntries(gc, display, width, b);
		
		if(b) {
			gc.drawLine(0, 0, width, 0);
			gc.drawLine(0, 0, 0, height);
			gc.drawLine(0, height - 1, width, height - 1);
			gc.drawLine(width - 1, 0, width - 1, height - 1);
		}
	}
	
	public void setVaccinationPlanHeader(VaccinationPlanHeaderDefinition vphd){
		_vphd = vphd;
	}
	
	public void setVaccinationEntries(List<Vaccination> vaccinations){
		_vaccinations = vaccinations;
	}
	
	public void setPatientBirthdate(TimeTool birthDate){
		_patientBirthDate = birthDate;
	}
	
	private int determineMinWidth(GC gc, boolean wrapText){
		int minWidth = 0;
		
		lengthOfBasisimpfungen = _vphd.base.size() * DISTANCE_BETWEEN_DISEASES;
		lengthOfDoctor = determineMaxAdministratorLabelLength(gc, wrapText) + OFFSET;
		lengthOfLotNr = determineMaxLotNr(gc, wrapText) + OFFSET;
		lengthOfDateString = gc.textExtent("09.07.2014").x + OFFSET;
		leftStart =
			_vphd.extended.size() * DISTANCE_BETWEEN_DISEASES + SEPARATOR_WIDTH_BASE_EXTENDED;
		int lNames = determineMaxVaccNameLength(gc);
		
		gc.setFont(headerFont);
		String extendedVaccination = "sowie ergänzende Impfungen";
		Point pt = gc.textExtent(extendedVaccination);
		if (pt.x > lNames) {
			lNames = pt.x;
		}
		gc.setFont(defaultFont);
		
		if (showSide) {
			lengthOfSide = gc.textExtent("rechts").x + OFFSET;
			minWidth =
				lengthOfSide + lengthOfBasisimpfungen + lengthOfDoctor + lengthOfLotNr
					+ lengthOfDateString + leftStart + lNames;
		} else {
			minWidth =
				lengthOfBasisimpfungen + lengthOfDoctor + lengthOfLotNr + lengthOfDateString
					+ leftStart + lNames;
		}
		return minWidth;
	}
	
	private void drawHeader(GC gc, Display display, int width){
		boolean alternator = true;
		diseaseBoundaries.clear();
		
		if (HEADER_HEIGHT == -1)
			HEADER_HEIGHT = determineMaxDiseaseStringExtension(gc).x + 10;
		
		// main label
		gc.setFont(headerFont);
		String baseVaccination = "Basisimpfungen";
		String extendedVaccination = "sowie ergänzende Impfungen";
		Point pt = gc.textExtent(baseVaccination);
		gc.drawText(baseVaccination, 10, 10, true);
		gc.drawText(extendedVaccination, 10, pt.y + 10, true);
		
		gc.setFont(defaultFont);
		gc.drawText(_vphd.name, 10, pt.y + 35, true);
		
		// left label
		gc.setFont(boldFont);
		gc.drawText(VACCINE, 10, HEADER_HEIGHT - (fontHeightDefaultFont * 2) - 2);
		
		gc.setFont(defaultFont);
		gc.setForeground(COLOR_DARKGREY);
		gc.drawText(TRADENAME, 10, HEADER_HEIGHT - (fontHeightDefaultFont * 1) - 2);
		gc.setForeground(COLOR_BLACK);
		
		gc.setFont(boldFont);
		
		// age
		locationOfAgeBorder = width - lengthOfBasisimpfungen - leftStart - (20 + 15);
		gc.drawText(AGE, locationOfAgeBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);
		
		// side if settings say so
		if (showSide) {
			locationOfSideBorder = locationOfAgeBorder - (lengthOfSide + 10);
			gc.drawText(SIDE, locationOfSideBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);
			
			// date label
			locationOfDateBorder = locationOfSideBorder - (lengthOfDateString + 10);
			gc.drawText(DATE, locationOfDateBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);
		} else {
			// date label
			locationOfDateBorder = locationOfAgeBorder - (lengthOfDateString + 10);
			gc.drawText(DATE, locationOfDateBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);
		}
		
		// doctor
		locationOfDocBorder = locationOfDateBorder - (lengthOfDoctor + 10);
		gc.drawText(ADMINISTRATOR, locationOfDocBorder + 2,
			HEADER_HEIGHT - fontHeightDefaultFont - 2);
		
		// lot number
		locationOfLotNrBorder = locationOfDocBorder - (lengthOfLotNr + 10);
		gc.drawText(LOT_NO, locationOfLotNrBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);
		gc.setFont(defaultFont);
		
		// extended diseases
		int spaceCounter = -DISTANCE_BETWEEN_DISEASES;
		for (String val : _vphd.extended) {
			if (alternator) {
				gc.setBackground(COLOR_BOTTOM);
			} else {
				gc.setBackground(COLOR_WHITE);
			}
			gc.fillRectangle(width + spaceCounter, 0, DISTANCE_BETWEEN_DISEASES, eh());
			gc.drawLine(width + spaceCounter, 0, width + spaceCounter, eh());
			
			diseaseBoundaries
				.add(new DiseaseBoundary(width + spaceCounter, gc.getBackground(), val));
			
			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(val);
			GraphicsUtil.drawVerticalText(diseaseLabel, display, width + spaceCounter + 3,
				HEADER_HEIGHT - 3, gc, SWT.UP | SWT.BOTTOM);
			
			spaceCounter -= DISTANCE_BETWEEN_DISEASES;
			alternator = !alternator;
		}
		gc.setBackground(COLOR_WHITE);
		
		// separator
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		separatorBoundary = width - leftStart;
		gc.fillRectangle(separatorBoundary, 0, SEPARATOR_WIDTH_BASE_EXTENDED, eh());
		
		// Basisimpfungen
		spaceCounter = -DISTANCE_BETWEEN_DISEASES;
		locationOfFirstDisease = width - lengthOfBasisimpfungen - leftStart;
		for (String baseDisease : _vphd.base) {
			if (alternator) {
				gc.setBackground(COLOR_BOTTOM);
			} else {
				gc.setBackground(COLOR_WHITE);
			}
			gc.fillRectangle(width + spaceCounter - leftStart, 0, DISTANCE_BETWEEN_DISEASES, eh());
			gc.drawLine(width + spaceCounter - leftStart, 0, width + spaceCounter - leftStart,
				eh());
			
			diseaseBoundaries.add(new DiseaseBoundary(width + spaceCounter - leftStart,
				gc.getBackground(), baseDisease));
			
			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(baseDisease);
			GraphicsUtil.drawVerticalText(diseaseLabel, display,
				width + spaceCounter + 3 - leftStart, HEADER_HEIGHT - 3, gc, SWT.UP | SWT.BOTTOM);
			
			spaceCounter -= DISTANCE_BETWEEN_DISEASES;
			alternator = !alternator;
		}
		gc.setBackground(COLOR_WHITE);
		
		// bottom line
		gc.drawLine(0, HEADER_HEIGHT - 1, width, HEADER_HEIGHT - 1);
		
		// vertical date delimiter line
		if (eh() != HEADER_HEIGHT) {
			gc.drawLine(locationOfAgeBorder, HEADER_HEIGHT, locationOfAgeBorder, eh());
			if (showSide) {
				gc.drawLine(locationOfSideBorder, HEADER_HEIGHT, locationOfSideBorder, eh());
			}
			gc.drawLine(locationOfDateBorder, HEADER_HEIGHT, locationOfDateBorder, eh());
			gc.drawLine(locationOfDocBorder, HEADER_HEIGHT, locationOfDocBorder, eh());
			gc.drawLine(locationOfLotNrBorder, HEADER_HEIGHT, locationOfLotNrBorder, eh());
			
		}
	}
	
	private Point determineMaxDiseaseStringExtension(GC gc){
		Point maxExtent = new Point(0, 0);
		List<String> fullList = new ArrayList<>();
		fullList.addAll(_vphd.base);
		fullList.addAll(_vphd.extended);
		for (String valS : fullList) {
			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(valS);
			maxExtent = determineMax(diseaseLabel, maxExtent, gc);
		}
		return maxExtent;
	}
	
	/**
	 * get needed length of administrator column
	 * 
	 * @param gc
	 * @return at least the length of the header
	 */
	private int determineMaxAdministratorLabelLength(GC gc, boolean wrapText){
		Point maxExtended = new Point(0, 0);
		// assure minimal size is header length
		maxExtended = determineMax(ADMINISTRATOR, maxExtended, gc);
		for (Vaccination vac : _vaccinations) {
			String doc = vac.getAdministratorLabel();
			maxExtended = determineMax(doc, maxExtended, gc);
		}
		if (wrapText && maxExtended.x > maxLengthDoctor) {
			maxExtended.x = maxLengthDoctor;
		}
		return maxExtended.x;
	}
	
	/**
	 * get needed length of lot# column
	 * 
	 * @param gc
	 * @return at least the length of the header
	 */
	private int determineMaxLotNr(GC gc, boolean wrapText){
		Point maxExtended = new Point(0, 0);
		// assure minimal size is header length
		maxExtended = determineMax(LOT_NO, maxExtended, gc);
		
		for (Vaccination vac : _vaccinations) {
			String lotNr = vac.getLotNo();
			maxExtended = determineMax(lotNr, maxExtended, gc);
		}
		if (wrapText && maxExtended.x > maxLengthLotNr) {
			maxExtended.x = maxLengthLotNr;
		}
		return maxExtended.x;
	}
	
	private int determineMaxVaccNameLength(GC gc){
		Point maxExtended = new Point(0, 0);
		// assure minimal size is header length
		maxExtended = determineMax(VACCINE, maxExtended, gc);
		
		for (Vaccination vac : _vaccinations) {
			String sbn = vac.getBusinessName();
			maxExtended = determineMax(sbn, maxExtended, gc);
		}
		return maxExtended.x;
	}
	
	private Point determineMax(String text, Point maxExtended, GC gc){
		Point val = gc.textExtent(text);
		if (val.x > maxExtended.x)
			maxExtended.x = val.x;
		if (val.y > maxExtended.y)
			maxExtended.y = val.y;
		
		return maxExtended;
	}
	
	/**
	 * the extended height, including the header and the number of entries*entries height
	 * 
	 * @return
	 */
	private int eh(){
		return HEADER_HEIGHT + (_vaccinations.size() * entryHeight);
	}
	
	private void drawEntries(GC gc, Display display, int width, boolean b){
		int lastHeightStart = 0;
		naviVacMap.clear();
		naviVacMap.put(0, null);
		
		for (int i = 0; i < _vaccinations.size(); i++) {
			Vaccination vacc = _vaccinations.get(i);
			
			gc.setBackground(COLOR_WHITE);
			gc.setForeground(COLOR_BLACK);
			int heightStart = HEADER_HEIGHT + (entryHeight * i + 1);
			lastHeightStart = heightStart;
			
			if (vacc.equals(selectedVacc)) {
				highlightSelectedEntry(gc, heightStart, width);
			}
			
			gc.setFont(boldFont);
			gc.drawText(wrapTextByWidth(gc, vacc.getShortBusinessName(), maxLengthBasisImpf, false),
				7,
				heightStart);
			gc.setFont(defaultFont);
			
			// add lot nr
			String lotNr = vacc.getLotNo();
			gc.drawText(wrapTextByWidth(gc, lotNr, maxLengthLotNr, true),
				locationOfLotNrBorder + 7,
				heightStart);
			
			// add doc name
			String docName = vacc.getAdministratorLabel();
			if (vacc.isSupplement()) {
				gc.setForeground(COLOR_GREEN);
			}
			gc.drawText(wrapTextByWidth(gc, docName, maxLengthDoctor, false),
				locationOfDocBorder + 7,
				heightStart);
			gc.setForeground(COLOR_BLACK);
			
			// add date
			_administrationDate = vacc.getDateOfAdministration();
			gc.drawText(vacc.getDateOfAdministrationLabel(), locationOfDateBorder + 7, heightStart);
			
			if (showSide) {
				String side = vacc.getSide();
				gc.drawText(side, locationOfSideBorder + 7, heightStart);
			}
			
			// get age
			gc.drawText(getAgeLabel(_administrationDate), locationOfAgeBorder + 7, heightStart);
			drawDiseaseMarkers(gc, heightStart, vacc, gc.getBackground());
			gc.drawLine(0, heightStart + entryHeight - 1, width, heightStart + entryHeight - 1);
			
			gc.setBackground(COLOR_WHITE);
			naviVacMap.put(heightStart, vacc);
		}
		// position from which on to ignore selection
		naviVacMap.put(lastHeightStart + entryHeight - 1, null);
	}
	
	/**
	 * Adds a new line if a text is longer then the displayed text length
	 * 
	 * @param gc
	 * @param text
	 * @param maxTextWidth
	 * @return
	 */
	private String wrapTextByWidth(GC gc, String text, int maxTextWidth,
		boolean wrapWithoutSeperator)
	{
		if (text != null)
		{
			int stringWidth = gc.stringExtent(text).x;
			int textLength = text.length();
			if (stringWidth > maxTextWidth)
			{
				int maxChars = maxTextWidth * textLength / stringWidth;
				if (maxChars < textLength)
				{
					int idxLastSpace = text.substring(0, maxChars).lastIndexOf(" ") + 1;
					if (idxLastSpace > 1 && idxLastSpace < textLength) {
						return text.replaceFirst("(.{" + idxLastSpace + "})", "$1\n");
					} else {
						return text.replaceFirst("(.{" + maxChars + "})",
							"$1" + (wrapWithoutSeperator ? "" : "-") + "\n");
					}
				}
			}
		}
		return text;
	}
	
	/**
	 * sets a different background color on the row starting at heightStart
	 * 
	 * @param gc
	 * @param heightStart
	 *            start of the row
	 * @param width
	 */
	private void highlightSelectedEntry(GC gc, int heightStart, int width){
		gc.setForeground(COLOR_CREME);
		gc.setBackground(UiDesk.getColorFromRGB("FEFFCB"));
		gc.drawRectangle(0, heightStart, width, entryHeight - 1);
		// gc.fillRectangle(0, heightStart, width, ENTRY_HEIGHT - 1);
		gc.fillGradientRectangle(0, heightStart, width, entryHeight - 1, true);
		
		gc.setForeground(COLOR_BLACK);
		// vertical lines
		if (eh() != HEADER_HEIGHT) {
			// age, date and first disease
			gc.drawLine(locationOfLotNrBorder, HEADER_HEIGHT, locationOfLotNrBorder, eh());
			gc.drawLine(locationOfDocBorder, HEADER_HEIGHT, locationOfDocBorder, eh());
			gc.drawLine(locationOfAgeBorder, HEADER_HEIGHT, locationOfAgeBorder, eh());
			if (showSide) {
				gc.drawLine(locationOfSideBorder, HEADER_HEIGHT, locationOfSideBorder, eh());
			}
			gc.drawLine(locationOfDateBorder, HEADER_HEIGHT, locationOfDateBorder, eh());
			gc.drawLine(locationOfFirstDisease, HEADER_HEIGHT, locationOfFirstDisease, eh());
			
			// draw lines for each disease
			for (DiseaseBoundary db : diseaseBoundaries) {
				gc.drawLine(db.leftTrim, 0, db.leftTrim, eh());
			}
			
			// draw the base/extended disease separator line
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(separatorBoundary, 0, SEPARATOR_WIDTH_BASE_EXTENDED, eh());
			gc.setBackground(COLOR_CREME);
		}
	}
	
	private String getAgeLabel(TimeTool adminDate){
		int daysTo = _patientBirthDate.daysTo(adminDate);
		int months = (daysTo / 30);
		
		if (months >= 48) {
			return (daysTo / 365) + "J";
		} else {
			return months + "m";
		}
	}
	
	private void drawDiseaseMarkers(GC gc, int heightStart, Vaccination vacc, Color background){
		String atcCode = vacc.getAtcCode();
		HashSet<String> immunisation =
			new HashSet<>(ArticleToImmunisationModel.getImmunisationForAtcCode(atcCode));
		
		if (immunisation == null || immunisation.isEmpty()) {
			immunisation = new HashSet<>(vacc.getVaccAgainstList());
		}
		
		for (DiseaseBoundary db : diseaseBoundaries) {
			if (immunisation.contains(db.atcCode)) {
				if (!background.equals(COLOR_CREME) && !background.equals(COLOR_LIGHTGREY)) {
					gc.setBackground(db.backgroundColor);
				}
				
				gc.drawText("X", db.leftTrim + 5, heightStart);
				gc.setBackground(background);
			}
		}
	}
	
	private class DiseaseBoundary {
		int leftTrim;
		String atcCode;
		Color backgroundColor;
		
		public DiseaseBoundary(int leftTrim, Color background, String atcCode){
			this.leftTrim = leftTrim;
			this.atcCode = atcCode;
			this.backgroundColor = background;
		}
	}
	
	public void setSelection(Vaccination vaccination, Control control){
		selectedVacc = vaccination;
		control.redraw();
	}
	
	/**
	 * get the selected vaccination
	 * 
	 * @return the vaccination or null if none is selected
	 */
	public Vaccination getSelectedVaccination(){
		return selectedVacc;
	}
	
	public void restorePrePrintSettting(){
		showSide = CoreHub.userCfg.get(PreferencePage.VAC_SHOW_SIDE, false);
	}
}
