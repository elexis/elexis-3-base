package ch.elexis.barcode.scanner.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.serial.Connection;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.ComboFieldEditor;
import ch.elexis.core.ui.util.SWTHelper;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String BarcodeScanner_COMPORT = "barcode/Symbol/port"; //$NON-NLS-1$
	public static final String BarcodeScanner_AUTOSTART = "barcode/Symbol/autostart"; //$NON-NLS-1$
	public static final String BarcodeScanner_SETTINGS = "barcode/Symbol/settings"; //$NON-NLS-1$
	public static final String BarcodeScanner_WAITFORNEWLINE = "barcode/Symbol/waitfornewline"; //$NON-NLS-1$

	public static final int NUMBER_OF_SCANNERS = 2;

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	public void init(IWorkbench workbench) {
		setDetaults();
	}

	private void setDetaults() {
		for (int i = 0; i < NUMBER_OF_SCANNERS; i++) {
			String postfix = i > 0 ? String.valueOf(i) : StringUtils.EMPTY;
			getPreferenceStore().setDefault(PreferencePage.BarcodeScanner_SETTINGS + postfix, "9600,8,n,1"); //$NON-NLS-1$
		}
	}

	@Override
	protected void createFieldEditors() {
		Group group = null;
		List<String> ports = new ArrayList<>();
		ports.add(StringUtils.EMPTY);
		ports.addAll(Arrays.asList(Connection.getComPorts()));
		if (ports.size() > 1) {
			String[] comPorts = ports.toArray(new String[0]);

			for (int i = 0; i < NUMBER_OF_SCANNERS; i++) {
				String postfix = i > 0 ? String.valueOf(i) : StringUtils.EMPTY;
				group = new Group(getFieldEditorParent(), SWT.None);
				group.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
				group.setLayout(new GridLayout(1, true));
				group.setText("Barcode Scanner " + (i + 1));

				Composite c = new Composite(group, SWT.NONE);
				c.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
				GridLayout layout = new GridLayout(1, true);
				c.setLayout(layout);

				addField(new ComboFieldEditor(BarcodeScanner_COMPORT + postfix, "Com-Schnittstelle", comPorts, c));

				StringFieldEditor settingsEditor = new StringFieldEditor(BarcodeScanner_SETTINGS + postfix,
						"Einstellungen", c);
				addField(settingsEditor);
				settingsEditor.getTextControl(c).setToolTipText("baudrate,databits,parity,stopbits[,flowcontrol]");

				addField(new BooleanFieldEditor(BarcodeScanner_WAITFORNEWLINE + postfix, "Daten in Zeilen empfangen",
						SWT.None, c));
			}

			addField(new BooleanFieldEditor(BarcodeScanner_AUTOSTART, "Bei Start automatisch verbinden", SWT.None,
					getFieldEditorParent()));

		} else {
			SWTHelper.showError("Barcode Scanner", "Es wurde keine COM-Schnittstelle gefunden!");
		}
	}
}
