package com.hilotec.elexis.kgview;

import java.util.Date;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.TimeTool.TimeFormatException;

import com.hilotec.elexis.kgview.data.KonsData;
import com.tiff.common.ui.datepicker.DatePickerCombo;

public class NeueKonsDialog extends TitleAreaDialog {
	
	DatePickerCombo datum;
	Text zeit;
	Combo typ;
	
	Konsultation kons;
	KonsData data;
	Fall fall;
	int neuTyp;
	
	String[] typenS = {
		"Regulär", "Telefon", "Hausbesuch",
	};
	int[] typenI = {
		KonsData.KONSTYP_NORMAL, KonsData.KONSTYP_TELEFON, KonsData.KONSTYP_HAUSBESUCH,
	};
	
	/**
	 * Dialog zum anlegen neuer Kons erstellen.
	 * 
	 * @param fall
	 *            Fall zu dem die Konsultation gehoert
	 * @param typ
	 *            Gibt welcher Konsultationstyp als standard benutzt werden soll.
	 */
	public NeueKonsDialog(Shell parentShell, Fall fall, int typ){
		super(parentShell);
		this.fall = fall;
		neuTyp = typ;
	}
	
	public NeueKonsDialog(Shell parentShell, Konsultation kons){
		super(parentShell);
		this.kons = kons;
		data = new KonsData(kons);
		fall = kons.getFall();
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite comp = new Composite(parent, 0);
		comp.setLayout(new GridLayout(2, false));
		
		new Label(comp, SWT.NONE).setText("Patient");
		new Label(comp, SWT.NONE).setText(fall.getPatient().getLabel());
		
		new Label(comp, SWT.NONE).setText("Fall");
		new Label(comp, SWT.NONE).setText(fall.getLabel());
		
		new Label(comp, SWT.NONE).setText("Datum");
		datum = new DatePickerCombo(comp, SWT.NONE);
		
		new Label(comp, SWT.NONE).setText("Zeit");
		zeit = SWTHelper.createText(comp, 1, SWT.BORDER);
		
		new Label(comp, SWT.NONE).setText("Typ");
		typ = new Combo(comp, SWT.DROP_DOWN);
		for (String s : typenS) {
			typ.add(s);
		}
		
		// Datum- und Zeitfelder initialisieren
		if (kons == null) {
			setTitle("Neue Konsultation erstellen");
			datum.setDate(new Date());
			zeit.setText(new TimeTool().toString(TimeTool.TIME_SMALL));
		} else {
			setTitle("Konsultation modifizieren");
			TimeTool tt = new TimeTool(kons.getDatum());
			datum.setDate(tt.getTime());
			zeit.setText(data.getKonsBeginn());
			neuTyp = data.getKonsTyp();
		}
		
		for (int i = 0; i < typenI.length; i++) {
			if (neuTyp == typenI[i]) {
				typ.select(i);
				break;
			}
		}
		
		return comp;
	}
	
	/** Zeit in format hh:mm validieren, wirft eine Exception im Fehlerfall */
	private void validateTime(String zeit) throws TimeFormatException{
		if (!zeit.matches("[0-9]{1,2}:[0-9]{1,2}"))
			throw new TimeFormatException("");
		String[] parts = zeit.split(":");
		if (Integer.parseInt(parts[0]) > 23 || Integer.parseInt(parts[1]) > 60)
			throw new TimeFormatException("");
	}
	
	@Override
	public void okPressed(){
		// Eingaben pruefen
		Date d = datum.getDate();
		if (d == null || d.compareTo(new Date()) > 0) {
			setMessage("Es muss ein Datum ausgewählt werden. Darf nicht in "
				+ "der Zukunft liegen.");
			return;
		}
		TimeTool tt;
		try {
			String sZeit = zeit.getText();
			validateTime(sZeit);
			tt = new TimeTool(sZeit);
		} catch (TimeFormatException tfe) {
			setMessage("Es muss eine gültige Startzeit (hh:mm) eingegeben " + "werden.");
			return;
		}
		
		// Neue kons anlegen falls noetig
		if (kons == null) {
			kons = fall.neueKonsultation();
			data = new KonsData(kons);
			ElexisEventDispatcher.fireSelectionEvent(kons);
		}
		
		// Eingaben speichern
		data.setKonsBeginn(tt.getTimeInMillis());
		kons.setDatum(new TimeTool(d.getTime()).toString(TimeTool.DATE_GER), false);
		data.setKonsTyp(typenI[typ.getSelectionIndex()]);
		close();
	}
}
