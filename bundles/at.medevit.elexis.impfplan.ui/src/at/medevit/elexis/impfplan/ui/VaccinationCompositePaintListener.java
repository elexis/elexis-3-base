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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.services.holder.ConfigServiceHolder;
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
	private static final int SEPARATOR_WIDTH_BASE_EXTENDED = 2;
	private static final int OFFSET = 15;

	private final Color COLOR_WHITE = UiDesk.getColor(UiDesk.COL_WHITE);
	private final Color COLOR_BOTTOM = UiDesk.getColorFromRGB("D0DCF2"); // rgb(208, 220, 242) //$NON-NLS-1$
	private final Color COLOR_DARKGREY = UiDesk.getColor(UiDesk.COL_DARKGREY);
	private final Color COLOR_LIGHTGREY = UiDesk.getColorFromRGB("F0F0F0"); //$NON-NLS-1$
	private final Color COLOR_BLACK = UiDesk.getColor(UiDesk.COL_BLACK);
	private final Color COLOR_GREEN = UiDesk.getColorFromRGB("39961C"); //$NON-NLS-1$
	private final Color COLOR_CREME = UiDesk.getColorFromRGB("FEFFB1"); // rgb(254, 255, 177) //$NON-NLS-1$

	private final Font headerFont, boldFont, defaultFont;
	private final int fontHeightDefaultFont, fontHeightBoldFont;
	private int separatorBoundary, locationOfLotNrBorder, locationOfDocBorder, locationOfDateBorder,
			locationOfAgeBorder, locationOfFirstDisease, locationOfSideBorder;

	private int distanceBetweenDiseases = 19;

	private int lengthOfBasisimpfungen, lengthOfDoctor, lengthOfLotNr, lengthOfDateString, lengthOfSide, leftStart;

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

	private int pageElementCount = 20; // default will be calculated
	private int pageTotalSize = 1; // default will be calculated

	private int entryHeight;
	private int defaultEntryHeight;
	private ScrolledComposite scrolledComposite;

	public int mouseX;
	public int mouseY;

	public VaccinationCompositePaintListener() {
		Display disp = Display.getCurrent();
		defaultFont = UiDesk.getFont(Preferences.USR_DEFAULTFONT);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(defaultFont).setStyle(SWT.BOLD);
		boldFont = boldDescriptor.createFont(disp);
		headerFont = new Font(disp, "Helvetica", 16, SWT.BOLD); //$NON-NLS-1$

		distanceBetweenDiseases = (int) (distanceBetweenDiseases * getScaleFactor());

		fontHeightDefaultFont = (int) ((defaultFont.getFontData()[0].getHeight() + 5) * getScaleFactor());
		fontHeightBoldFont = (int) ((boldFont.getFontData()[0].getHeight() + 5) * getScaleFactor());

		entryHeight = (int) ((fontHeightBoldFont + 4) * getScaleFactor());
		defaultEntryHeight = entryHeight;

		naviVacMap = new TreeMap<Integer, Vaccination>();
		selectedVacc = null;
		showSide = ConfigServiceHolder.getUser(PreferencePage.VAC_SHOW_SIDE, false);
	}

	private boolean shouldScale() {
		String osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
		if (osName.startsWith("win")) { //$NON-NLS-1$
			if (getWindowsVersion() < 10) {
				return true;
			}
		}
		return false;
	}

	private double getWindowsVersion() {
		String osVersion = System.getProperty("os.version").toLowerCase(); //$NON-NLS-1$
		try {
			return Double.valueOf(osVersion);
		} catch (NumberFormatException e) {
			// ignore default is 6.1 (Win 7)
		}
		return 6.1;
	}

	public double getScaleFactor() {
		if (shouldScale()) {
			int dpi = Display.getDefault().getDPI().x;
			if (dpi == 120) {
				return 1.25;
			} else if (dpi == 144) {
				return 1.5;
			}
		}
		return 1;
	}

	@Override
	public void paintControl(PaintEvent e) {
		if (_vphd == null)
			return;

		paintControl(e.gc, e.display, e.width, e.height, false, -1);

		Widget widget = e.widget;
		if (widget instanceof VaccinationComposite) {
			VaccinationComposite vaccinationComposite = (VaccinationComposite) widget;
			Composite parent = vaccinationComposite.getParent();
			if (parent instanceof ScrolledComposite) {
				scrolledComposite = (ScrolledComposite) parent;
				scrolledComposite.setMinSize(vaccinationComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		}
	}

	public void paintControl(GC gc, Display display, int width, int height, boolean b, int pageIdx) {
		if (b) {
			// do print
			showSide = false;
			width = 800;
			maxLengthBasisImpf = 150;
			maxLengthDoctor = 100;
			maxLengthLotNr = 70;
		} else {
			if (width < 800) {
				width = 800;
			}
			if (scrolledComposite != null && scrolledComposite.getVerticalBar().isVisible()) {
				width = width - scrolledComposite.getVerticalBar().getSize().x;
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
			if (wrapTextByWidth(gc, vacc.getShortBusinessName(), maxLengthBasisImpf, false).contains(StringUtils.LF)) {
				wrapText = true;
				break;
			}
			if (wrapTextByWidth(gc, vacc.getLotNo(), maxLengthLotNr, true).contains(StringUtils.LF)) {
				wrapText = true;
				break;
			}
			if (wrapTextByWidth(gc, vacc.getAdministratorLabel(), maxLengthDoctor, false).contains(StringUtils.LF)) {
				wrapText = true;
				break;
			}
		}

		entryHeight = defaultEntryHeight;
		if (wrapText) {
			entryHeight = entryHeight + defaultEntryHeight;
		}

		if (b) {
			// calculate page element count
			pageElementCount = (height - HEADER_HEIGHT) / entryHeight;
			// calculate total page size
			pageTotalSize = (int) Math.ceil((float) _vaccinations.size() / pageElementCount);
		} else {
			pageTotalSize = 1;
		}

		determineMinWidth(gc, wrapText);
		drawHeader(gc, display, width, pageIdx);
		drawEntries(gc, display, width, b, pageIdx);

		if (b) {
			gc.drawLine(0, 0, width, 0);
			gc.drawLine(0, 0, 0, height);
			gc.drawLine(0, height - 1, width, height - 1);
			gc.drawLine(width - 1, 0, width - 1, height - 1);
		}
	}

	public int getPageTotalSize() {
		return pageTotalSize;
	}

	public void setVaccinationPlanHeader(VaccinationPlanHeaderDefinition vphd) {
		_vphd = vphd;
	}

	public void setVaccinationEntries(List<Vaccination> vaccinations) {
		_vaccinations = vaccinations;
	}

	public void setPatientBirthdate(TimeTool birthDate) {
		_patientBirthDate = birthDate;
	}

	private int determineMinWidth(GC gc, boolean wrapText) {
		int minWidth = 0;

		lengthOfBasisimpfungen = _vphd.base.size() * distanceBetweenDiseases;
		lengthOfDoctor = determineMaxAdministratorLabelLength(gc, wrapText) + OFFSET;
		lengthOfLotNr = determineMaxLotNr(gc, wrapText) + OFFSET;
		lengthOfDateString = gc.textExtent("09.07.2014").x + OFFSET; //$NON-NLS-1$
		leftStart = _vphd.extended.size() * distanceBetweenDiseases + SEPARATOR_WIDTH_BASE_EXTENDED;
		int lNames = determineMaxVaccNameLength(gc);

		gc.setFont(headerFont);
		String extendedVaccination = "sowie ergänzende Impfungen";
		Point pt = gc.textExtent(extendedVaccination);
		if (pt.x > lNames) {
			lNames = pt.x;
		}
		gc.setFont(defaultFont);

		if (showSide) {
			lengthOfSide = gc.textExtent("rechts").x + OFFSET; //$NON-NLS-1$
			minWidth = lengthOfSide + lengthOfBasisimpfungen + lengthOfDoctor + lengthOfLotNr + lengthOfDateString
					+ leftStart + lNames;
		} else {
			minWidth = lengthOfBasisimpfungen + lengthOfDoctor + lengthOfLotNr + lengthOfDateString + leftStart
					+ lNames;
		}
		return minWidth;
	}

	private void drawHeader(GC gc, Display display, int width, int pageIdx) {
		boolean alternator = true;
		diseaseBoundaries.clear();

		if (HEADER_HEIGHT == -1)
			HEADER_HEIGHT = determineMaxDiseaseStringExtension(gc).x + 10;

		int ehHeight = calcEhHeight(pageIdx);

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
		locationOfAgeBorder = (int) (width - lengthOfBasisimpfungen - leftStart - ((20 + 15) * getScaleFactor()));
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
		gc.drawText(ADMINISTRATOR, locationOfDocBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);

		// lot number
		locationOfLotNrBorder = locationOfDocBorder - (lengthOfLotNr + 10);
		gc.drawText(LOT_NO, locationOfLotNrBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont - 2);
		gc.setFont(defaultFont);

		// extended diseases
		int spaceCounter = -distanceBetweenDiseases;
		for (String val : _vphd.extended) {
			if (alternator) {
				gc.setBackground(COLOR_BOTTOM);
			} else {
				gc.setBackground(COLOR_WHITE);
			}
			gc.fillRectangle(width + spaceCounter, 0, distanceBetweenDiseases, ehHeight);
			gc.drawLine(width + spaceCounter, 0, width + spaceCounter, ehHeight);

			diseaseBoundaries.add(new DiseaseBoundary(width + spaceCounter, gc.getBackground(), val));

			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(val);
			GraphicsUtil.drawVerticalText(diseaseLabel, display, width + spaceCounter + 3, HEADER_HEIGHT - 3, gc,
					SWT.UP | SWT.BOTTOM);

			spaceCounter -= distanceBetweenDiseases;
			alternator = !alternator;
		}
		gc.setBackground(COLOR_WHITE);

		// separator
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		separatorBoundary = width - leftStart;
		gc.fillRectangle(separatorBoundary, 0, SEPARATOR_WIDTH_BASE_EXTENDED, ehHeight);

		// Basisimpfungen
		spaceCounter = -distanceBetweenDiseases;
		locationOfFirstDisease = width - lengthOfBasisimpfungen - leftStart;
		for (String baseDisease : _vphd.base) {
			if (alternator) {
				gc.setBackground(COLOR_BOTTOM);
			} else {
				gc.setBackground(COLOR_WHITE);
			}
			gc.fillRectangle(width + spaceCounter - leftStart, 0, distanceBetweenDiseases, ehHeight);
			gc.drawLine(width + spaceCounter - leftStart, 0, width + spaceCounter - leftStart, ehHeight);

			diseaseBoundaries
					.add(new DiseaseBoundary(width + spaceCounter - leftStart, gc.getBackground(), baseDisease));

			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(baseDisease);
			GraphicsUtil.drawVerticalText(diseaseLabel, display, width + spaceCounter + 3 - leftStart,
					HEADER_HEIGHT - 3, gc, SWT.UP | SWT.BOTTOM);

			spaceCounter -= distanceBetweenDiseases;
			alternator = !alternator;
		}
		gc.setBackground(COLOR_WHITE);

		// bottom line
		gc.drawLine(0, HEADER_HEIGHT - 1, width, HEADER_HEIGHT - 1);

		// vertical date delimiter line
		if (ehHeight != HEADER_HEIGHT) {
			gc.drawLine(locationOfAgeBorder, HEADER_HEIGHT, locationOfAgeBorder, ehHeight);
			if (showSide) {
				gc.drawLine(locationOfSideBorder, HEADER_HEIGHT, locationOfSideBorder, ehHeight);
			}
			gc.drawLine(locationOfDateBorder, HEADER_HEIGHT, locationOfDateBorder, ehHeight);
			gc.drawLine(locationOfDocBorder, HEADER_HEIGHT, locationOfDocBorder, ehHeight);
			gc.drawLine(locationOfLotNrBorder, HEADER_HEIGHT, locationOfLotNrBorder, ehHeight);

		}
	}

	private int calcEhHeight(int pageIdx) {
		if (pageIdx > 0) {
			// calculate how many elements left and its height
			int diff = _vaccinations.size() - (pageElementCount * pageIdx);
			if (diff <= pageElementCount) {
				// show only the elements which are left
				return HEADER_HEIGHT + diff * entryHeight;
			}
		}
		return eh(pageIdx);
	}

	private Point determineMaxDiseaseStringExtension(GC gc) {
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
	private int determineMaxAdministratorLabelLength(GC gc, boolean wrapText) {
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
	private int determineMaxLotNr(GC gc, boolean wrapText) {
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

	private int determineMaxVaccNameLength(GC gc) {
		Point maxExtended = new Point(0, 0);
		// assure minimal size is header length
		maxExtended = determineMax(VACCINE, maxExtended, gc);

		for (Vaccination vac : _vaccinations) {
			String sbn = vac.getBusinessName();
			maxExtended = determineMax(sbn, maxExtended, gc);
		}
		return maxExtended.x;
	}

	private Point determineMax(String text, Point maxExtended, GC gc) {
		Point val = gc.textExtent(text);
		if (val.x > maxExtended.x)
			maxExtended.x = val.x;
		if (val.y > maxExtended.y)
			maxExtended.y = val.y;

		return maxExtended;
	}

	/**
	 * the extended height, including the header and the number of entries*entries
	 * height
	 *
	 * @return
	 */
	private int eh(int pageIndex) {
		return HEADER_HEIGHT + ((pageIndex == -1 || _vaccinations.size() < pageElementCount ? _vaccinations.size()
				: pageElementCount) * entryHeight);
	}

	private void drawEntries(GC gc, Display display, int width, boolean b, int pageIdx) {
		int lastHeightStart = 0;
		naviVacMap.clear();
		naviVacMap.put(0, null);

		// calculate the offset and the max size for paginagtion
		int i = 0;
		int size = _vaccinations.size();
		if (pageIdx > -1) {
			i = pageElementCount * pageIdx;
			size = pageElementCount * (pageIdx + 1);
		}
		int a = 0; // only for increment
		for (; i < size; i++) {
			if (_vaccinations.size() <= i) {
				break;
			}

			Vaccination vacc = _vaccinations.get(i);

			gc.setBackground(COLOR_WHITE);
			gc.setForeground(COLOR_BLACK);
			int heightStart = HEADER_HEIGHT + (entryHeight * a + 1);
			lastHeightStart = heightStart;

			if (vacc.equals(selectedVacc)) {
				highlightSelectedEntry(gc, heightStart, width, pageIdx);
			}

			gc.setFont(boldFont);
			gc.drawText(wrapTextByWidth(gc, vacc.getShortBusinessName(), maxLengthBasisImpf, false), 7, heightStart);
			gc.setFont(defaultFont);

			// add lot nr
			String lotNr = vacc.getLotNo();
			gc.drawText(wrapTextByWidth(gc, lotNr, maxLengthLotNr, true), locationOfLotNrBorder + 7, heightStart);

			// add doc name
			String docName = vacc.getAdministratorLabel();
			if (vacc.isSupplement()) {
				gc.setForeground(COLOR_GREEN);
			}
			gc.drawText(wrapTextByWidth(gc, docName, maxLengthDoctor, false), locationOfDocBorder + 7, heightStart);
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

			a++;
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
	private String wrapTextByWidth(GC gc, String text, int maxTextWidth, boolean wrapWithoutSeperator) {
		if (text != null) {
			int stringWidth = gc.stringExtent(text).x;
			int textLength = text.length();
			if (stringWidth > maxTextWidth) {
				int maxChars = maxTextWidth * textLength / stringWidth;
				if (maxChars < textLength) {
					int idxLastSpace = text.substring(0, maxChars).lastIndexOf(StringUtils.SPACE) + 1;
					if (idxLastSpace > 1 && idxLastSpace < textLength) {
						return text.replaceFirst("(.{" + idxLastSpace + "})", "$1\n"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					} else {
						return text.replaceFirst("(.{" + maxChars + "})", //$NON-NLS-1$ //$NON-NLS-2$
								"$1" + (wrapWithoutSeperator ? StringUtils.EMPTY : "-") + StringUtils.LF); //$NON-NLS-1$ //$NON-NLS-2$
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
	 * @param heightStart start of the row
	 * @param width
	 */
	private void highlightSelectedEntry(GC gc, int heightStart, int width, int pageIdx) {
		gc.setForeground(COLOR_CREME);
		gc.setBackground(UiDesk.getColorFromRGB("FEFFCB")); //$NON-NLS-1$
		gc.drawRectangle(0, heightStart, width, entryHeight - 1);
		// gc.fillRectangle(0, heightStart, width, ENTRY_HEIGHT - 1);
		gc.fillGradientRectangle(0, heightStart, width, entryHeight - 1, true);
		gc.setForeground(COLOR_BLACK);
		// vertical lines
		int ehHeight = calcEhHeight(pageIdx);

		int yStart = HEADER_HEIGHT;
		if (ehHeight != HEADER_HEIGHT) {
			// age, date and first disease
			gc.drawLine(locationOfLotNrBorder, yStart, locationOfLotNrBorder, ehHeight);
			gc.drawLine(locationOfDocBorder, yStart, locationOfDocBorder, ehHeight);
			gc.drawLine(locationOfAgeBorder, yStart, locationOfAgeBorder, ehHeight);
			if (showSide) {
				gc.drawLine(locationOfSideBorder, yStart, locationOfSideBorder, ehHeight);
			}
			gc.drawLine(locationOfDateBorder, yStart, locationOfDateBorder, ehHeight);
			gc.drawLine(locationOfFirstDisease, yStart, locationOfFirstDisease, ehHeight);

			// draw lines for each disease
			for (DiseaseBoundary db : diseaseBoundaries) {
				gc.drawLine(db.leftTrim, 0, db.leftTrim, ehHeight);
			}

			// draw the base/extended disease separator line
			gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			gc.fillRectangle(separatorBoundary, 0, SEPARATOR_WIDTH_BASE_EXTENDED, ehHeight);
			gc.setBackground(COLOR_CREME);
		}
	}

	private String getAgeLabel(TimeTool adminDate) {
		int daysTo = _patientBirthDate.daysTo(adminDate);
		int months = (daysTo / 30);

		if (months >= 48) {
			return (daysTo / 365) + "J"; //$NON-NLS-1$
		} else {
			return months + "m"; //$NON-NLS-1$
		}
	}

	private void drawDiseaseMarkers(GC gc, int heightStart, Vaccination vacc, Color background) {
		String atcCode = vacc.getAtcCode();
		HashSet<String> immunisation = new HashSet<>(ArticleToImmunisationModel.getImmunisationForAtcCode(atcCode));

		if (immunisation == null || immunisation.isEmpty()) {
			immunisation = new HashSet<>(vacc.getVaccAgainstList());
		}

		for (DiseaseBoundary db : diseaseBoundaries) {
			if (immunisation.contains(db.atcCode)) {
				if (!background.equals(COLOR_CREME) && !background.equals(COLOR_LIGHTGREY)) {
					gc.setBackground(db.backgroundColor);
				}

				gc.drawText("X", db.leftTrim + 5, heightStart); //$NON-NLS-1$
				gc.setBackground(background);
			}
		}
	}

	private class DiseaseBoundary {
		int leftTrim;
		String atcCode;
		Color backgroundColor;

		public DiseaseBoundary(int leftTrim, Color background, String atcCode) {
			this.leftTrim = leftTrim;
			this.atcCode = atcCode;
			this.backgroundColor = background;
		}
	}

	public void setSelection(Vaccination vaccination, Control control) {
		selectedVacc = vaccination;
		if (control != null) {
			control.redraw();
		}
	}

	/**
	 * get the selected vaccination
	 *
	 * @return the vaccination or null if none is selected
	 */
	public Vaccination getSelectedVaccination() {
		return selectedVacc;
	}

	public void restorePrePrintSettting() {
		showSide = ConfigServiceHolder.getUser(PreferencePage.VAC_SHOW_SIDE, false);
	}

	public int getWidth() {
		return 800;
	}

	public int getHeight() {
		if (_vaccinations == null) {
			return 64;
		}
		return eh(-1);
	}

	public boolean isTitleArea() {
		return mouseY > 1 && mouseX > 1 && mouseY < 90 && mouseX < 350;
	}
}
