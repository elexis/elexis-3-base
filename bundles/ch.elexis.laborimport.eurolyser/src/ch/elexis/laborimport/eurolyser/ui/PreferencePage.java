package ch.elexis.laborimport.eurolyser.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.laborimport.eurolyser.EurolyserImporter;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.MANDATOR));
	}

	@Override
	protected void createFieldEditors() {
		addField(new StringFieldEditor(EurolyserImporter.CONFIG_IMPORT_MANDANTONLY, "Import nur für Mandanten (Kürzel)",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}
}
