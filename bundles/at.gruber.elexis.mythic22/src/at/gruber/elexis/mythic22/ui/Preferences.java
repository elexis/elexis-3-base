/*******************************************************************************
 * Copyright (c) 2011, Christian Gruber and MEDEVIT OG
 * All rights reserved.
 *******************************************************************************/
package at.gruber.elexis.mythic22.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * This is the code for the preference page of Mythic22 used for the extension
 * "org.eclipse.ui.preferencePages"
 *
 * In the preference page the port on which mythic22 results will come in can be
 * defined Also the path of the mapping file can be entered here
 *
 */
public class Preferences extends PreferencePage implements IWorkbenchPreferencePage {

	public static final String CFG_PORT = "connectors/mythic22/port";
	public static final String CFG_AUTOSTART = "connectors/mythic22/autostart";
	public static final String CFG_NETWORKDEVICE = "connectors/mythic22/networkdevice";
	public static final String CFG_PATHMAPPINGFILE = "connectors/mythic22/pathmappingfile";

	private Text m_port;
	private Text m_directorypath;
	private Button btnAutoStart;

	public Preferences() {
		super("Mythic22 - Einstellungen");
		setTitle("mythic 22 ");
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(final Composite parent) {

		String mythic22Port = CoreHub.localCfg.get(CFG_PORT, "1200");
		String mythic22MappingPath = CoreHub.localCfg.get(CFG_PATHMAPPINGFILE, "c:\\");
		boolean mythic22Autostart = CoreHub.localCfg.get(CFG_AUTOSTART, false);

		Composite returnComposite = new Composite(parent, SWT.NONE);
		returnComposite.setLayout(new GridLayout(3, false));
		returnComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		new Label(returnComposite, SWT.NONE).setText("Port");
		m_port = new Text(returnComposite, SWT.BORDER);
		m_port.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_port.setText(mythic22Port);
		new Label(returnComposite, SWT.NONE).setVisible(false);

		new Label(returnComposite, SWT.NONE).setText("Pfad der Mapping Datei");
		m_directorypath = new Text(returnComposite, SWT.BORDER);
		m_directorypath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		m_directorypath.setText(mythic22MappingPath);

		Button btnBrowse = new Button(returnComposite, SWT.NONE);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				String selected = fd.open();
				m_directorypath.setText(selected);
			}
		});
		btnBrowse.setText("Browse");

		btnAutoStart = new Button(returnComposite, SWT.CHECK);
		btnAutoStart.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1));
		btnAutoStart.setText("Automatisch starten");
		btnAutoStart.setSelection(mythic22Autostart);

		new Label(returnComposite, SWT.NONE);
		new Label(returnComposite, SWT.NONE);
		new Label(returnComposite, SWT.NONE);

		return returnComposite;
	}

	@Override
	public boolean performOk() {
		CoreHub.localCfg.set(CFG_PORT, m_port.getText());
		CoreHub.localCfg.set(CFG_PATHMAPPINGFILE, m_directorypath.getText());
		CoreHub.localCfg.set(CFG_AUTOSTART, btnAutoStart.getSelection());

		CoreHub.localCfg.flush();
		return super.performOk();
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
