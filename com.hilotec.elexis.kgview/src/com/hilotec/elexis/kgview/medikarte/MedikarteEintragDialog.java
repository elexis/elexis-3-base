package com.hilotec.elexis.kgview.medikarte;

import java.util.HashMap;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.kgview.Preferences;
import com.hilotec.elexis.kgview.data.FavMedikament;

/**
 * Dialog um einen Eintrag in der Liste der favorisierten Medikamente anzupassen oder neu zu
 * erstellen.
 * 
 * @author Antoine Kaufmann
 */
public class MedikarteEintragDialog extends TitleAreaDialog {
	private Patient pat;
	private FavMedikament fm;
	private Prescription presc;
	
	private Text tOrd;
	private Text tDoMorgen;
	private Text tDoMittag;
	private Text tDoAbend;
	private Text tDoNacht;
	private Text tVon;
	private Text tBis;
	private Combo cEV;
	private Text tZweck;
	
	public MedikarteEintragDialog(Shell parentShell, Patient patient, FavMedikament med){
		super(parentShell);
		fm = med;
		pat = patient;
		presc = null;
	}
	
	public MedikarteEintragDialog(Shell parentShell, Patient patient, Prescription prescription){
		super(parentShell);
		fm = FavMedikament.load(prescription.getArtikel());
		pat = patient;
		presc = prescription;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite comp = new Composite(parent, 0);
		comp.setLayout(new GridLayout(2, false));
		
		// Patient, Medikament beides rein informativ
		setTitle("Neues Medikament fuer " + pat.getName() + ", " + pat.getGeburtsdatum());
		Label lLMed = new Label(comp, 0);
		lLMed.setText("Medikament");
		Label lMed = new Label(comp, SWT.BORDER);
		lMed.setText(fm.getBezeichnung());
		
		// Originalnamen anzeigen
		Label lLOMed = new Label(comp, 0);
		lLOMed.setText("Original");
		Label lOMed = new Label(comp, SWT.BORDER);
		lOMed.setText(fm.getArtikel().getName());
		
		// Feld fuer Ordnungszahl
		Label lOrd = new Label(comp, 0);
		lOrd.setText("Ordnungszahl");
		tOrd = SWTHelper.createText(comp, 1, 0);
		
		// Felder zum ausfuellen, Datum von bis, Dosis
		Label lVon = new Label(comp, 0);
		lVon.setText("Von");
		tVon = SWTHelper.createText(comp, 1, 0);
		Label lBis = new Label(comp, 0);
		lBis.setText("Bis");
		tBis = SWTHelper.createText(comp, 1, 0);
		
		Label lDosierung = new Label(comp, 0);
		lDosierung.setText("Dosierung");
		
		Composite cDos = new Composite(comp, 0);
		cDos.setLayout(new RowLayout());
		tDoMorgen = new Text(cDos, SWT.BORDER);
		tDoMorgen.setLayoutData(new RowData(30, SWT.DEFAULT));
		tDoMittag = new Text(cDos, SWT.BORDER);
		tDoMittag.setLayoutData(new RowData(30, SWT.DEFAULT));
		tDoAbend = new Text(cDos, SWT.BORDER);
		tDoAbend.setLayoutData(new RowData(30, SWT.DEFAULT));
		tDoNacht = new Text(cDos, SWT.BORDER);
		tDoNacht.setLayoutData(new RowData(30, SWT.DEFAULT));
		
		// Liste mit Einnahmevorschriften initialisieren
		Label lEV = new Label(comp, 0);
		lEV.setText("Einnahmevorschrift");
		cEV = new Combo(comp, SWT.DROP_DOWN | SWT.BORDER);
		HashMap<String, Integer> evMap = new HashMap<String, Integer>();
		int evIndex = 0;
		cEV.add("");
		cEV.select(0);
		evMap.put("", evIndex++);
		for (String ev : Preferences.getEinnahmevorschriften()) {
			if (evMap.containsKey(ev))
				continue;
			cEV.add(ev);
			evMap.put(ev, evIndex++);
		}
		
		// Zweck
		Label lZweck = new Label(comp, 0);
		lZweck.setText("Zweck");
		tZweck = SWTHelper.createText(comp, 2, 0);
		
		// Einheit, rein informativ
		Label lEinheit = new Label(comp, 0);
		lEinheit.setText("Einheit");
		Label lEinheitText = new Label(comp, SWT.BORDER);
		lEinheitText.setText(fm.getEinheit());
		
		tVon.setText(new TimeTool().toString(TimeTool.DATE_GER));
		if (presc != null) {
			int o = MedikarteHelpers.getOrdnungszahl(presc);
			tOrd.setText(Integer.toString(o));
			
			tBis.setText(presc.getEndDate());
			String[] dos = presc.getDosis().split("-");
			tDoMorgen.setText(dos[0]);
			tDoMittag.setText(dos[1]);
			tDoAbend.setText(dos[2]);
			tDoNacht.setText(dos[3]);
			tZweck.setText(MedikarteHelpers.getPZweck(presc));
			
			// Korrekte Einnahmevorschrift auswaehlen
			String ev = presc.getBemerkung();
			if (evMap.containsKey(ev)) {
				cEV.select(evMap.get(ev));
			} else {
				// XXX: Ist das so sinnvoll??
				cEV.add(ev);
				cEV.select(evIndex);
			}
		} else {
			tOrd.setText(Integer.toString(fm.getOrdnungszahl()));
			tDoMorgen.setText("0");
			tDoMittag.setText("0");
			tDoAbend.setText("0");
			tDoNacht.setText("0");
			tZweck.setText(fm.getZweck());
			;
		}
		
		return comp;
	}
	
	private boolean validateDate(String s, boolean allowempty){
		TimeTool tt = new TimeTool();
		return (s.isEmpty() && allowempty) || tt.setDate(s);
	}
	
	/**
	 * Format einer Dosierung ueberpruefen.
	 */
	private boolean validateDosierung(String s){
		s = s.toUpperCase();
		
		// Spezielle Dosierung auf Beiblatt
		if (s.equals("X"))
			return true;
		
		// Ganzzahlige Dosierung
		if (s.matches("[0-9]+")) {
			return true;
		}
		
		// Fuehrende Ganzzahl parsen
		if (s.matches("[0-9]+ .*")) {
			s = s.replaceAll("[ \t]+", " ");
			String[] parts = s.split(" ");
			if (parts.length != 2)
				return false;
			s = parts[1];
		}
		
		// Bruch-Dosierung parsen
		if (s.matches("[0-9]+/[0-9]+")) {
			String[] parts = s.split("/");
			int z, n;
			try {
				z = Integer.parseInt(parts[0]);
				n = Integer.parseInt(parts[1]);
			} catch (NumberFormatException nfe) {
				// Sollte nicht passieren nach Regex-Check oben.
				return false;
			}
			return (z > 0) && (n > 0) && (n > z);
		}
		return false;
	}
	
	private boolean validateInput(){
		setMessage("");
		
		// Datumsfelder pruefen
		if (!validateDate(tVon.getText(), false) || !validateDate(tBis.getText(), true)) {
			setMessage("Fehler: Ungültiges Datum. Erwarte Format "
				+ "dd.mm.jjjj, oder leer (nur Bis).");
			return false;
		}
		
		// Ordnungszahl pruefen
		try {
			Integer.parseInt(tOrd.getText());
		} catch (NumberFormatException nfe) {
			setMessage("Fehler: Ungültige Ordnungszahl. Erwarte Ganzzahl.");
			return false;
		}
		
		// Format der Dosierungen pruefen
		if (!validateDosierung(tDoMorgen.getText()) || !validateDosierung(tDoMittag.getText())
			|| !validateDosierung(tDoAbend.getText()) || !validateDosierung(tDoNacht.getText())) {
			setMessage("Fehler: Ungültige Dosierung. Erwarte nicht-negative "
				+ "Ganzzahl, Bruch mit positivem, ganzzahligem Zähler "
				+ "und Nenner, oder x für Einnahme gemäss separater " + "Verschreibungskarte.");
			return false;
		}
		
		return true;
	}
	
	@Override
	public void okPressed(){
		if (!validateInput())
			return;
		String dosierung =
			tDoMorgen.getText() + "-" + tDoMittag.getText() + "-" + tDoAbend.getText() + "-"
				+ tDoNacht.getText();
		dosierung = dosierung.toUpperCase();
		String bemerkung = cEV.getItem(cEV.getSelectionIndex());
		int ordnungszahl = Integer.parseInt(tOrd.getText());
		
		// Spezialfall, nur Ordnungszahl geaendert, muss nicht aktenkundig sein
		if (presc != null && presc.getDosis().equals(dosierung)
			&& presc.getBeginDate().equals(tVon.getText())
			&& presc.getEndDate().equals(tBis.getText()) && presc.getBemerkung().equals(bemerkung)
			&& MedikarteHelpers.getPZweck(presc).equals(tZweck.getText())) {
			MedikarteHelpers.setOrdnungszahl(presc, ordnungszahl);
			close();
			return;
		}
		
		if (presc != null && !presc.isDeleted() && presc.getEndDate().equals("")) {
			TimeTool ttOld = new TimeTool(presc.getBeginDate());
			TimeTool ttNew = new TimeTool(tVon.getText());
			// Wenn das neue vonDatum >= das alte von Datum ist, setzen wir das
			// bis Datum des bestehenden Medikamentes darauf. Sind sie gleich
			// wird die bisherige verschreibung geloescht.
			int cmp = ttOld.compareTo(ttNew);
			if (cmp == 0) {
				presc.remove();
			} else if (cmp < 0) {
				presc.setEndDate(tVon.getText());
			}
		}
		presc = new Prescription(fm.getArtikel(), pat, dosierung, bemerkung);
		presc.setBeginDate(tVon.getText());
		presc.setEndDate(tBis.getText());
		MedikarteHelpers.setOrdnungszahl(presc, Integer.parseInt(tOrd.getText()));
		MedikarteHelpers.setPZweck(presc, tZweck.getText());
		close();
	}
}
