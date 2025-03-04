/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package net.medshare.connector.aerztekasse.view;

import java.net.InetAddress;
import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import net.medshare.connector.aerztekasse.Messages;
import net.medshare.connector.aerztekasse.data.AerztekasseSettings;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private AerztekasseSettings aerztekasseSettings;

	private GridData gridDataForLabels;
	private GridData gridDataForInputs;

	private Group gGlobalSettings;
	private Text tGlobalUsername;
	private Text tGlobalPassword;
	private Text tGlobalUrl;

	private Group gMandantSettings;
	private Button bMandantUseGlobalSettings;
	private Text tMandantUsername;
	private Text tMandantPassword;

	private Group gMachineSettings;
	private Button bMachineUseGlobalSettings;
	private Text tMachineUrl;

	private Boolean isInitializing = true;

	public Preferences() {
		super(GRID);
	}

	@Override
	public boolean performOk() {
		aerztekasseSettings.setGlobalUsername(tGlobalUsername.getText());
		aerztekasseSettings.setGlobalPassword(tGlobalPassword.getText());
		aerztekasseSettings.setGlobalUrl(tGlobalUrl.getText());

		aerztekasseSettings.setMandantUsingGlobalSettings(bMandantUseGlobalSettings.getSelection());
		aerztekasseSettings.setMandantUsername(tMandantUsername.getText());
		aerztekasseSettings.setMandantPassword(tMandantPassword.getText());

		aerztekasseSettings.setMachineUsingGlobalSettings(bMachineUseGlobalSettings.getSelection());
		aerztekasseSettings.setMachineUrl(tMachineUrl.getText());

		aerztekasseSettings.saveSettings();

		return true;
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		if (aerztekasseSettings == null)
			aerztekasseSettings = new AerztekasseSettings((CoreHub.actMandant));
	}

	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Control createContents(Composite parent) {
		String undefined = Messages.Preferences_undefiniert;
		Text dummy = new Text(parent, SWT.NONE);
		dummy.setText(Messages.Preferences_UseGlobalSettings);
		dummy.pack();
		gridDataForLabels = new GridData(dummy.getSize().x, dummy.getLineHeight());
		gridDataForInputs = new GridData(GridData.FILL_HORIZONTAL);
		aerztekasseSettings = new AerztekasseSettings((CoreHub.actMandant));
		dummy.dispose();

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));

		Label lbl;
		Group grp;

		// =====================================================================
		// Global Settings
		// =====================================================================

		gGlobalSettings = new Group(comp, SWT.NONE);
		grp = gGlobalSettings;
		grp.setText(Messages.Preferences_GlobalSettings);
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Benutzername
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_Username);
		lbl.setLayoutData(gridDataForLabels);

		tGlobalUsername = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalUsername.setLayoutData(gridDataForInputs);
		tGlobalUsername.setText(undefined);

		// Passwort
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_Password);
		lbl.setLayoutData(gridDataForLabels);

		tGlobalPassword = new Text(grp, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		tGlobalPassword.setLayoutData(gridDataForInputs);
		tGlobalPassword.setText(undefined);

		// URL
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_URL);
		lbl.setLayoutData(gridDataForLabels);

		tGlobalUrl = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tGlobalUrl.setLayoutData(gridDataForInputs);
		tGlobalUrl.setText(undefined);

		// =====================================================================
		// Mandant Settings
		// =====================================================================
		gMandantSettings = new Group(comp, SWT.NONE);
		grp = gMandantSettings;
		grp.setText(Messages.Preferences_MandantSettingsFor);
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Use global Settings
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UseGlobalSettings);
		lbl.setLayoutData(gridDataForLabels);

		bMandantUseGlobalSettings = new Button(grp, SWT.CHECK);
		bMandantUseGlobalSettings.setSelection(false);
		bMandantUseGlobalSettings.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean mandantUseGlobalSettings = c.getSelection();
				aerztekasseSettings.setMandantUsingGlobalSettings(mandantUseGlobalSettings);
				showMandantSettings();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// Benutzername
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_Username);
		lbl.setLayoutData(gridDataForLabels);

		tMandantUsername = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMandantUsername.setLayoutData(gridDataForInputs);
		tMandantUsername.setText(undefined);

		// Passwort
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_Password);
		lbl.setLayoutData(gridDataForLabels);

		tMandantPassword = new Text(grp, SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		tMandantPassword.setLayoutData(gridDataForInputs);
		tMandantPassword.setText(undefined);

		// =====================================================================
		// Machine Settings
		// =====================================================================
		gMachineSettings = new Group(comp, SWT.NONE);
		grp = gMachineSettings;
		grp.setText(Messages.Preferences_LocalSettingsFor);
		grp.setLayout(new GridLayout(2, false));
		grp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Use global Settings
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_UseGlobalSettings);
		lbl.setLayoutData(gridDataForLabels);

		bMachineUseGlobalSettings = new Button(grp, SWT.CHECK);
		bMachineUseGlobalSettings.setSelection(false);
		bMachineUseGlobalSettings.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (isInitializing)
					return;
				Button c = (Button) e.widget;
				Boolean machineUseGlobalSettings = c.getSelection();
				aerztekasseSettings.setMachineUsingGlobalSettings(machineUseGlobalSettings);
				showMachineSettings();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// URL
		lbl = new Label(grp, SWT.NONE);
		lbl.setText(Messages.Preferences_URL);
		lbl.setLayoutData(gridDataForLabels);

		tMachineUrl = new Text(grp, SWT.BORDER | SWT.SINGLE);
		tMachineUrl.setLayoutData(gridDataForInputs);
		tMachineUrl.setText(undefined);

		// ----------------------------------------------------
		// ----------------------------------------------------
		// Test Button zum Anzeigen welche Einstellung verwendet wird
		// ----------------------------------------------------
		// ----------------------------------------------------
// Button btnShowSettings = new Button(parent, SWT.NONE);
//		btnShowSettings.setText("Test Button: Anzeigen der Ausgewählten Einstellungen"); //$NON-NLS-1$
// btnShowSettings.addSelectionListener(new SelectionListener() {
//
// @Override
// public void widgetSelected(SelectionEvent e){
// SWTHelper.showInfo(
//					"Ausgewählte Einstellungen", //$NON-NLS-1$
//					"Benutzername: " + aerztekasseSettings.getUsername() + "\nPasswort: " //$NON-NLS-1$ //$NON-NLS-2$
//						+ aerztekasseSettings.getPassword() + "\nURL: " //$NON-NLS-1$
// + aerztekasseSettings.getUrl());
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){
// // TODO Auto-generated method stub
//
// }
// });
		// ----------------------------------------------------
		// ----------------------------------------------------
		// Ende Test Button
		// ----------------------------------------------------
		// ----------------------------------------------------

		showSettings();
		isInitializing = false;
		return comp;

	}

	private void showSettings() {
		tGlobalUsername.setText(aerztekasseSettings.getGlobalUsername());
		tGlobalPassword.setText(aerztekasseSettings.getGlobalPassword());
		tGlobalUrl.setText(aerztekasseSettings.getGlobalUrl());

		showMandantSettings();
		showMachineSettings();

	}

	private void showMandantSettings() {
		Boolean mandantUseGlobalSettings = aerztekasseSettings.isMandantUsingGlobalSettings();
		String mandantname = StringUtils.EMPTY;
		try {
			mandantname = aerztekasseSettings.getMandant().getLabel();
		} catch (Exception e) {
		}
		gMandantSettings.setText(MessageFormat.format(Messages.Preferences_MandantSettingsFor, mandantname));
		bMandantUseGlobalSettings.setSelection(mandantUseGlobalSettings);
		tMandantUsername.setEditable(!mandantUseGlobalSettings);
		tMandantPassword.setEditable(!mandantUseGlobalSettings);

		tMandantUsername.setText(aerztekasseSettings.getMandantUsername());
		tMandantPassword.setText(aerztekasseSettings.getMandantPasword());
	}

	private void showMachineSettings() {
		Boolean machineUseGlobalSettings = aerztekasseSettings.isMachineUsingGlobalSettings();
		String hostname = "localhost"; //$NON-NLS-1$
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}
		gMachineSettings.setText(MessageFormat.format(Messages.Preferences_LocalSettingsFor, hostname));
		bMachineUseGlobalSettings.setSelection(machineUseGlobalSettings);
		tMachineUrl.setEditable(!machineUseGlobalSettings);

		tMachineUrl.setText(aerztekasseSettings.getMachineUrl());
	}

}
