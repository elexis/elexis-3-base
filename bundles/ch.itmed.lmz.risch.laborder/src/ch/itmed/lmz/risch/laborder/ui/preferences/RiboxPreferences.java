/*******************************************************************************
 * Copyright (c) 2018 IT-Med AG <info@it-med-ag.ch>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IT-Med AG <info@it-med-ag.ch> - initial implementation
 ******************************************************************************/

package ch.itmed.lmz.risch.laborder.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.SWTHelper;
import ch.itmed.lmz.risch.laborder.preferences.PreferenceConstants;
import ch.rgw.io.Settings;

public final class RiboxPreferences extends PreferencePage implements IWorkbenchPreferencePage {
	private Settings settingsProvider = CoreHub.userCfg;
	private Combo settingsProviderCombo;
	private Text clientCertPath;
	private Text certPassword;
	private Text riboxIP;

	public RiboxPreferences() {
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));

		settingsProvider = CoreHub.mandantCfg;

		Label description = new Label(composite, SWT.NONE);
		description.setText("Spezifizieren Sie die Einstellungen für das Login am LabOrder Portal.");
		description.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));

		new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR)
				.setLayoutData(SWTHelper.getFillGridData(4, true, 1, false));

		new Label(composite, SWT.NONE).setText("Einstellungen für:");
		String[] comboItems = { "aktueller Benutzer", "aktueller Mandant", "Global" };
		settingsProviderCombo = new Combo(composite, SWT.READ_ONLY);
		settingsProviderCombo.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		settingsProviderCombo.setItems(comboItems);
		settingsProviderCombo.select(setComboSelection(settingsProviderCombo, comboItems,
				CoreHub.localCfg.get(PreferenceConstants.SETTINGS_PROVIDER, "aktueller Benutzer"))); // TODO
		settingsProviderCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSettingsProvider();
				updateFields();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		// Update the settings provider
		setSettingsProvider();

		new Label(composite, SWT.NONE).setText("Client Zertifikat:");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		clientCertPath = new Text(composite, SWT.BORDER);
		clientCertPath.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
		clientCertPath.setText(settingsProvider.get(PreferenceConstants.CLIENT_CERTIFICATE, ""));

		Button clientCertFileDialog = new Button(composite, SWT.PUSH);
		clientCertFileDialog.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		clientCertFileDialog.setText("Client Zertifikat Datei wählen");
		clientCertFileDialog.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setFilterNames(new String[] { "Client Zertifikat", "Alle Dateien (*.*)" });
				dialog.setFilterExtensions(new String[] { "*.p12", "*.*" });
				String certPath = dialog.open();
				if (certPath != null) {
					clientCertPath.setText(certPath);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

		new Label(composite, SWT.NONE).setText("Privater Schlüssel:");
		certPassword = new Text(composite, SWT.PASSWORD | SWT.BORDER);
		certPassword.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		certPassword.setText(settingsProvider.get(PreferenceConstants.CERTIFICATE_PASSWORD, ""));

		new Label(composite, SWT.NONE).setText("Ribox IP/DNS:");
		riboxIP = new Text(composite, SWT.BORDER);
		riboxIP.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		riboxIP.setText(settingsProvider.get(PreferenceConstants.RIBOX_IP, ""));

		return composite;
	}

	@Override
	public boolean performOk() {
		CoreHub.localCfg.set(PreferenceConstants.SETTINGS_PROVIDER, settingsProviderCombo.getText());
		settingsProvider.set(PreferenceConstants.CLIENT_CERTIFICATE, clientCertPath.getText());
		settingsProvider.set(PreferenceConstants.CERTIFICATE_PASSWORD, certPassword.getText());
		settingsProvider.set(PreferenceConstants.RIBOX_IP, riboxIP.getText());

		return super.performOk();
	}

	private void updateFields() {
		clientCertPath.setText(settingsProvider.get(PreferenceConstants.CLIENT_CERTIFICATE, ""));
		certPassword.setText(settingsProvider.get(PreferenceConstants.CERTIFICATE_PASSWORD, ""));
		riboxIP.setText(settingsProvider.get(PreferenceConstants.RIBOX_IP, ""));
	}

	public int setComboSelection(Combo combo, String[] items, String item) {
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(item))
				return i;
		}
		return 0;
	}

	public void setSettingsProvider() {
		if (settingsProviderCombo.getText().equals("aktueller Benutzer")) {
			settingsProvider.flush();
			settingsProvider = CoreHub.userCfg;
		} else if (settingsProviderCombo.getText().equals("aktueller Mandant")) {
			settingsProvider.flush();
			settingsProvider = CoreHub.mandantCfg;
		} else if (settingsProviderCombo.getText().equals("Global")) {
			settingsProvider.flush();
			settingsProvider = CoreHub.globalCfg;
		}
	}

}
