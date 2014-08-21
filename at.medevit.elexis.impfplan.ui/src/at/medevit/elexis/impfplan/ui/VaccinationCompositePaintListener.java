package at.medevit.elexis.impfplan.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.ui.UiDesk;
import ch.rgw.tools.TimeTool;

public class VaccinationCompositePaintListener implements PaintListener {
	
	private static int HEADER_HEIGHT = -1;
	private static int ENTRY_HEIGHT;
	private static final int DISTANCE_BETWEEN_DISEASES = 19;
	private static final int SEPARATOR_WIDTH_BASE_EXTENDED = 2;
	
	private final Color COLOR_WHITE = UiDesk.getColor(UiDesk.COL_WHITE);
	private final Color COLOR_BOTTOM = UiDesk.getColorFromRGB("D0DCF2"); // rgb(208, 220, 242)
	private final Color COLOR_DARKGREY = UiDesk.getColor(UiDesk.COL_DARKGREY);
	private final Color COLOR_BLACK = UiDesk.getColor(UiDesk.COL_BLACK);
	
	private final Font headerFont, boldFont, defaultFont;
	private final int fontHeightDefaultFont, fontHeightBoldFont;
	private int separatorBoundary, locationOfDateBorder, locationOfAgeBorder;
	
	private List<Vaccination> _vaccinations;
	private List<DiseaseBoundary> diseaseBoundaries = new ArrayList<DiseaseBoundary>();
	
	private VaccinationPlanHeaderDefinition _vphd;
	
	private TimeTool _administrationDate;
	private TimeTool _patientBirthDate;
	
	public VaccinationCompositePaintListener(){
		Display disp = Display.getCurrent();
		defaultFont = UiDesk.getFont(Preferences.USR_DEFAULTFONT);
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(defaultFont).setStyle(SWT.BOLD);
		boldFont = boldDescriptor.createFont(disp);
		headerFont = new Font(disp, "Helvetica", 16, SWT.BOLD);
		
		fontHeightDefaultFont = defaultFont.getFontData()[0].getHeight() + 5;
		fontHeightBoldFont = boldFont.getFontData()[0].getHeight() + 5;
		
		ENTRY_HEIGHT = fontHeightBoldFont + fontHeightDefaultFont;
	}
	
	@Override
	public void paintControl(PaintEvent e){
		if (_vphd == null)
			return;

		paintControl(e.gc, e.display, e.width, e.height, false);
	}
	
	public void paintControl(GC gc, Display display, int width, int height, boolean b) {
		drawHeader(gc, display, width);
		drawEntries(gc, display, width);
		
		if(b) {
			gc.drawLine(0, 0, width, 0);
			gc.drawLine(0, 0, 0, height);
			gc.drawLine(0, height-1, width, height-1);
			gc.drawLine(width-1, 0, width-1, height-1);
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
	
	private void drawHeader(GC gc, Display display, int width){
		boolean alternator = true;
		diseaseBoundaries.clear();
		
		int lengthOfBasisimpfungen = _vphd.base.size() * DISTANCE_BETWEEN_DISEASES;
		int lengthOfDateString = gc.textExtent("09.07.2014").x;
		
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
		gc.drawText(_vphd.name, 10, pt.y + 30, true);
		
		// left label
		gc.setFont(boldFont);
		gc.drawText("Impfstoff", 10, HEADER_HEIGHT - (fontHeightDefaultFont * 4));
		
		gc.setFont(defaultFont);
		gc.setForeground(COLOR_DARKGREY);
		gc.drawText("Handelsname", 10, HEADER_HEIGHT - (fontHeightDefaultFont * 3));
		gc.drawText("Hersteller", 10, HEADER_HEIGHT - (fontHeightDefaultFont * 2));
		gc.drawText("Lot-Nr.", 10, HEADER_HEIGHT - fontHeightDefaultFont);
		
		gc.setForeground(COLOR_BLACK);
		
		int leftStart =
			_vphd.extended.size() * DISTANCE_BETWEEN_DISEASES + SEPARATOR_WIDTH_BASE_EXTENDED;
		
		// age
		gc.setFont(boldFont);
		locationOfAgeBorder = width - lengthOfBasisimpfungen - leftStart - (20 + 10);
		gc.drawText("Age", locationOfAgeBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont);
		
		// date label
		locationOfDateBorder = locationOfAgeBorder - (lengthOfDateString + 10);
		gc.drawText("Datum", locationOfDateBorder + 2, HEADER_HEIGHT - fontHeightDefaultFont);
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
			
			diseaseBoundaries.add(new DiseaseBoundary(width + spaceCounter, gc.getBackground(),
				val));
			
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
		for (String baseDisease : _vphd.base) {
			if (alternator) {
				gc.setBackground(COLOR_BOTTOM);
			} else {
				gc.setBackground(COLOR_WHITE);
			}
			gc.fillRectangle(width + spaceCounter - leftStart, 0, DISTANCE_BETWEEN_DISEASES,
				eh());
			gc.drawLine(width + spaceCounter - leftStart, 0,
				width + spaceCounter - leftStart, eh());
			
			diseaseBoundaries.add(new DiseaseBoundary(width + spaceCounter - leftStart, gc
				.getBackground(), baseDisease));
			
			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(baseDisease);
			GraphicsUtil.drawVerticalText(diseaseLabel, display, width + spaceCounter + 3
				- leftStart, HEADER_HEIGHT - 3, gc, SWT.UP | SWT.BOTTOM);
			
			spaceCounter -= DISTANCE_BETWEEN_DISEASES;
			alternator = !alternator;
		}
		gc.setBackground(COLOR_WHITE);
		
		// bottom line
		gc.drawLine(0, HEADER_HEIGHT - 1, width, HEADER_HEIGHT - 1);
		
		// vertical date delimiter line
		if (eh() != HEADER_HEIGHT) {
			gc.drawLine(locationOfAgeBorder, HEADER_HEIGHT, locationOfAgeBorder, eh());
			gc.drawLine(locationOfDateBorder, HEADER_HEIGHT, locationOfDateBorder, eh());
		}
	}
	
	private Point determineMaxDiseaseStringExtension(GC gc){
		Point maxExtent = new Point(0, 0);
		List<String> fullList = new ArrayList<>();
		fullList.addAll(_vphd.base);
		fullList.addAll(_vphd.extended);
		for (String valS : fullList) {
			String diseaseLabel = DiseaseDefinitionModel.getLabelForAtcCode(valS);
			
			Point val = gc.textExtent(diseaseLabel);
			if (val.x > maxExtent.x)
				maxExtent.x = val.x;
			if (val.y > maxExtent.y)
				maxExtent.y = val.y;
		}
		return maxExtent;
	}
	
	/**
	 * the extended height, including the header and the number of entries*entries height
	 * 
	 * @return
	 */
	private int eh(){
		return HEADER_HEIGHT + (_vaccinations.size() * ENTRY_HEIGHT);
	}
	
	private void drawEntries(GC gc, Display display, int width){
		
		for (int i = 0; i < _vaccinations.size(); i++) {
			Vaccination vacc = _vaccinations.get(i);
			
			int heightStart = HEADER_HEIGHT + (ENTRY_HEIGHT * i + 1);
			
			gc.setFont(boldFont);
			gc.drawText(vacc.getBusinessName(), 10, heightStart);
			gc.setFont(defaultFont);
			gc.drawText(vacc.getChargeNo(), 10, heightStart + fontHeightBoldFont);
			
			_administrationDate = vacc.getDateOfAdministration();
			gc.drawText(_administrationDate.toString(TimeTool.DATE_GER),
				locationOfDateBorder + 5, heightStart);
			gc.drawText(getAgeLabel(_administrationDate), locationOfAgeBorder + 2, heightStart);
			
			drawDiseaseMarkers(gc, heightStart, vacc);
			
			gc.drawLine(0, heightStart + ENTRY_HEIGHT - 1, width, heightStart + ENTRY_HEIGHT
				- 1);
		}
	}
	
	private String getAgeLabel(TimeTool adminDate){
		int daysTo = _patientBirthDate.daysTo(adminDate);
		int months = (daysTo / 30);
		
		if (months >= 48) {
			return (daysTo / 365) + "";
		} else {
			return months + "m";
		}
	}
	
	private void drawDiseaseMarkers(GC gc, int heightStart, Vaccination vacc){
		String atcCode = vacc.getAtcCode();
		HashSet<String> immunisation =
			new HashSet<>(ArticleToImmunisationModel.getImmunisationForAtcCode(atcCode));
		for (DiseaseBoundary db : diseaseBoundaries) {
			if (immunisation.contains(db.atcCode)) {
				Color background = gc.getBackground();
				gc.setBackground(db.backgroundColor);
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
}
