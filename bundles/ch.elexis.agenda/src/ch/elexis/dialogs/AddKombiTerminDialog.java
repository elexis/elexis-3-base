package ch.elexis.dialogs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.agenda.preferences.Messages;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class AddKombiTerminDialog extends TitleAreaDialog {

	private Text titleText;
	private Combo agendaBereichCombo;
	private Combo terminTypCombo;
	private Combo dauerCombo;
	private Button vorherRadio;
	private Button nachherRadio;
	private Combo zeitversatzCombo;
	private Button okButton;
	private List<String> areas;
	private List<String> appointmentTypes;
	private String bereichsTyp;
	private String[] appointmentData;
	private boolean isManualDauerChange = false;

	public AddKombiTerminDialog(Shell parentShell, String bereich) {
		super(parentShell);
		this.bereichsTyp = bereich;
		this.appointmentData = null;
	}

	public AddKombiTerminDialog(Shell parentShell, String bereich, String[] appointmentData) {
		super(parentShell);
		this.bereichsTyp = bereich;
		this.appointmentData = appointmentData;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite mainContainer = (Composite) super.createDialogArea(parent);
		mainContainer.setLayout(new GridLayout(1, false));
		setTitle(appointmentData == null ? Messages.AddCombiTerminErstellen : Messages.AddCombiTerminBearbeiten);
		setMessage(
				appointmentData == null ? MessageFormat.format(Messages.AddCombiTerminErstellenDescription, bereichsTyp)
						: MessageFormat.format(Messages.AddCombiTerminBearbeitenDescription, bereichsTyp));
		Composite container = new Composite(mainContainer, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		container.setLayout(new GridLayout(2, false));
		GridData comboData = new GridData();
		comboData.widthHint = 230;
		Label titleLabel = new Label(container, SWT.NONE);
		titleLabel.setText(Messages.AddCombiTerminDialogReason + ":");
		titleText = new Text(container, SWT.BORDER);
		titleText.setLayoutData(comboData);
		Label bereichLabel = new Label(container, SWT.NONE);
		bereichLabel.setText(Messages.AddCombiTerminDialogRange);
		agendaBereichCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		agendaBereichCombo.setLayoutData(comboData);
		Label terminTypLabel = new Label(container, SWT.NONE);
		terminTypLabel.setText(Messages.AddCombiTerminDialogTyp + ":");
		terminTypCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		terminTypCombo.setLayoutData(comboData);
		Label dauerLabel = new Label(container, SWT.NONE);
		dauerLabel.setText(Messages.AddCombiTerminDialogDuration);
		Composite dauerComposite = new Composite(container, SWT.NONE);
		dauerComposite.setLayout(new GridLayout(2, false));
		dauerComposite.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false, 1, 1));
		dauerCombo = new Combo(dauerComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData dauerComboData = new GridData();
		dauerComboData.widthHint = 40;
		dauerComboData.horizontalIndent = -5;
		dauerCombo.setLayoutData(dauerComboData);
		Label minutenLabel = new Label(dauerComposite, SWT.NONE);
		minutenLabel.setText(Messages.AddCombiTerminDialogMinute);
		Label zeitversatzLabel = new Label(container, SWT.NONE);
		zeitversatzLabel.setText(Messages.AddCombiTerminDialogTimeOffset);
		Composite zeitversatzComposite = new Composite(container, SWT.NONE);
		zeitversatzComposite.setLayout(new GridLayout(4, false));
		zeitversatzComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		vorherRadio = new Button(zeitversatzComposite, SWT.RADIO);
		vorherRadio.setText(Messages.AddCombiTerminDialogBefore);
		nachherRadio = new Button(zeitversatzComposite, SWT.RADIO);
		nachherRadio.setText(Messages.AddCombiTerminDialogAfter);
		zeitversatzCombo = new Combo(zeitversatzComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridData zeitversatzComboData = new GridData();
		zeitversatzComboData.widthHint = 40;
		zeitversatzCombo.setLayoutData(zeitversatzComboData);
		Label minutenLabel2 = new Label(zeitversatzComposite, SWT.NONE);
		minutenLabel2.setText(Messages.AddCombiTerminDialogMinute);
		populateAgendaBereichCombo();
		populateZeitversatzCombo();
		SelectionAdapter commonSelectionListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!isManualDauerChange) {
					updateDauerCombo();
				}
				updateOkButtonState();
			}
		};
		agendaBereichCombo.addSelectionListener(commonSelectionListener);
		terminTypCombo.addSelectionListener(commonSelectionListener);
		vorherRadio.addSelectionListener(commonSelectionListener);
		nachherRadio.addSelectionListener(commonSelectionListener);
		zeitversatzCombo.addSelectionListener(commonSelectionListener);
		dauerCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				isManualDauerChange = true;
				updateOkButtonState();
			}
		});
		titleText.addModifyListener(e -> updateOkButtonState());
		if (appointmentData != null) {
			isManualDauerChange = false;
			titleText.setText(appointmentData[0]);
			agendaBereichCombo.setText(appointmentData[1]);
			terminTypCombo.setText(appointmentData[2]);
			if (Messages.AddCombiTerminDialogBefore.equals(appointmentData[3])) {
				vorherRadio.setSelection(true);
			} else {
				nachherRadio.setSelection(true);
			}
			zeitversatzCombo.setText(appointmentData[4]);
			dauerCombo.setText(appointmentData[5]);
		}
		return mainContainer;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		okButton = getButton(OK);
		okButton.setEnabled(false);
		updateOkButtonState();
		return control;
	}

	@Override
	protected void okPressed() {
		String title = titleText.getText();
		String agendaBereich = agendaBereichCombo.getText();
		String terminTyp = terminTypCombo.getText();
		String dauer = dauerCombo.getText();
		String zeitversatz = zeitversatzCombo.getText();
		String zeitversatzType = vorherRadio.getSelection() ? Messages.AddCombiTerminDialogBefore
				: Messages.AddCombiTerminDialogAfter;
		String kombiTermin = "{"
				+ String.join(";", title, agendaBereich, terminTyp, zeitversatzType, zeitversatz, dauer) + "}";
		List<String> existingKombiTermine = new ArrayList<>(
				ConfigServiceHolder.get().getAsList(PreferenceConstants.AG_KOMBITERMINE + "/" + bereichsTyp));
		if (appointmentData != null) {
			String oldKombiTermin = "{" + String.join(";", appointmentData) + "}";
			existingKombiTermine.remove(oldKombiTermin);
		}
		existingKombiTermine.add(kombiTermin);
		ConfigServiceHolder.setGlobalAsList(PreferenceConstants.AG_KOMBITERMINE + "/" + bereichsTyp,
				existingKombiTermine);
		super.okPressed();
	}

	private void updateDauerCombo() {
		String selectedBereich = agendaBereichCombo.getText();
		String selectedTyp = terminTypCombo.getText();
		if (!selectedBereich.isEmpty() && !selectedTyp.isEmpty()) {
			String dauer = getDauerForBereichAndTyp(selectedBereich, selectedTyp);
			if (dauer != null) {
				dauerCombo.setText(dauer);
			}
		}
	}

	private String getDauerForBereichAndTyp(String bereich, String typ) {
		Hashtable<String, String> timePrefs = Plannables.getTimePrefFor(bereich);
		String dauer = timePrefs.get(typ);
		if (dauer == null) {
			dauer = timePrefs.get("std");
		}
		return dauer;
	}

	private void updateOkButtonState() {
		boolean isFormValid = !titleText.getText().trim().isEmpty() && !agendaBereichCombo.getText().isEmpty()
				&& !terminTypCombo.getText().isEmpty() && !dauerCombo.getText().isEmpty()
				&& !dauerCombo.getText().equals("0") && (vorherRadio.getSelection() || nachherRadio.getSelection())
				&& !zeitversatzCombo.getText().isEmpty();
		if (okButton != null) {
			okButton.setEnabled(isFormValid);
		}
	}

	private void populateAgendaBereichCombo() {
		areas = ConfigServiceHolder.get().getAsList(PreferenceConstants.AG_BEREICHE);
		appointmentTypes = ConfigServiceHolder.get().getAsList(PreferenceConstants.AG_TERMINTYPEN);
		agendaBereichCombo.setItems(areas.toArray(new String[0]));
		List<String> filteredAppointmentTypes = appointmentTypes.stream().skip(2).collect(Collectors.toList());
		terminTypCombo.setItems(filteredAppointmentTypes.toArray(new String[0]));
	}

	private void populateZeitversatzCombo() {
		List<String> timeIntervals = new ArrayList<>();
		for (int minutes = 0; minutes <= 240; minutes += 5) {
			timeIntervals.add(String.valueOf(minutes));
		}
		zeitversatzCombo.setItems(timeIntervals.toArray(new String[0]));
		dauerCombo.setItems(timeIntervals.toArray(new String[0]));
	}
}
