package ch.elexis.connect.sysmex.ui;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;

public class WhichPatientDialog extends TitleAreaDialog {
	private Patient pat, sysmexPat, selectedPat;
	private Button btnSysmexPatient, btnSelectedPatient, btnOtherPatient;

	public WhichPatientDialog(Shell parentShell, Patient sysmexPat) {
		super(parentShell);
		this.sysmexPat = sysmexPat;
		// only add selected pat if he/she is not null and not equal the sysmex patient
		Patient tmpPat = ElexisEventDispatcher.getSelectedPatient();
		if (tmpPat != null && !tmpPat.equals(sysmexPat)) {
			selectedPat = tmpPat;
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Patienten Vorschlag");
		setMessage("Wem sollen die Laborwerte zugewiesen werden?");

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		btnSysmexPatient = new Button(container, SWT.RADIO);
		btnSysmexPatient.setText(sysmexPat.getLabel());

		if (selectedPat != null && selectedPat.exists()) {
			btnSelectedPatient = new Button(container, SWT.RADIO);
			btnSelectedPatient.setText(selectedPat.getLabel());
		}

		btnOtherPatient = new Button(container, SWT.RADIO);
		btnOtherPatient.setText("anderen Patienten selektieren");

		return area;
	}

	@Override
	protected void okPressed() {
		if (btnSysmexPatient.getSelection()) {
			pat = sysmexPat;
		} else if (btnOtherPatient.getSelection()) {
			pat = null;
		} else {
			pat = selectedPat;
		}
		super.okPressed();
	}

	public Patient getPatient() {
		return pat;
	}
}
