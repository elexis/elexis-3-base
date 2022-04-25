package ch.weirich.templator.pages;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	IPreferenceStore store;
	public static final String PREFERENCE_BRANCH = Preferences.PREFERENCE_BRANCH + "pagesprocessor/";

	public Preferences() {
		super(GRID);
		store = new SettingsPreferenceStore(CoreHub.localCfg);
		setPreferenceStore(store);
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void createFieldEditors() {
		Label info = new Label(getFieldEditorParent(), SWT.WRAP);
		info.setText(
				"Geben Sie bitte den Startbefehl für die Ausgabe des Dokuments ein.\nSetzen Sie % für den Namen des auszugebenden Dokuments");
		info.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		addField(new StringFieldEditor(PREFERENCE_BRANCH + "cmd", "Befehl", getFieldEditorParent()));
		addField(new StringFieldEditor(PREFERENCE_BRANCH + "param", "Parameter", getFieldEditorParent()));

	}

	@Override
	protected void performApply() {
		CoreHub.localCfg.flush();
	}
}
