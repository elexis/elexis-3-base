package com.hilotec.elexis.kgview.medikarte;

import java.util.Date;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.data.Prescription;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.kgview.data.FavMedikament;
import com.tiff.common.ui.datepicker.DatePickerCombo;


/**
 * Dialog zum Stoppen eines Medikaments. Das gewuenschte Datum kann ausgewaehlt
 * werden.
 */
public class MedikarteStopDialog extends TitleAreaDialog {
	private Prescription presc;
	private FavMedikament fm;
	private DatePickerCombo dpc;

	/**
	 * Dialog anzeigen
	 *
	 * @param presc Medikament das gestoppt werden soll
	 */
	public MedikarteStopDialog(Shell parentShell, Prescription presc) {
		super(parentShell);
		this.presc = presc;
		this.fm = FavMedikament.load(presc.getArtikel());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Medikament stoppen");

		Composite comp = new Composite(parent, 0);
		comp.setLayout(new GridLayout(2, false));

		Label lMed = new Label(comp, 0);
		lMed.setText("Medikament");
		Label medikament = new Label(comp, SWT.BORDER);
		medikament.setText(fm.getLabel());

		Label lStart = new Label(comp, 0);
		lStart.setText("Startdatum");
		Label start = new Label(comp, 0);
		start.setText(presc.getBeginDate());

		Label lDatum = new Label(comp, 0);
		lDatum.setText("Stoppdatum");

		dpc = new DatePickerCombo(comp, 0);
		dpc.setDate(new Date());

		return comp;
	}

	@Override
	public void okPressed() {
		TimeTool tStart = new TimeTool(presc.getBeginDate());
		TimeTool tStop = new TimeTool(dpc.getDate().getTime());
		if (tStop.compareTo(tStart) < 0) {
			setMessage("Es kann kein Datum vor dem Startdatum angegeben werden!");
			return;
		}

		presc.setEndDate(tStop.toString(TimeTool.DATE_GER));
		close();
	}
}
