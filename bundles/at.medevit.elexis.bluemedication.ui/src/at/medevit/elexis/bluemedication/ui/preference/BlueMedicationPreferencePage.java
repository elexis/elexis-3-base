package at.medevit.elexis.bluemedication.ui.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class BlueMedicationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public BlueMedicationPreferencePage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		setDescription("BlueMedication Einstellungen");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(BlueMedicationConstants.CFG_USE_IMPORT,
				"Medikationsabgleich bei Blue Medication nutzen. (Automatisch deaktiviert bei Eigenartikel in Medikation des Patienten)",
				getFieldEditorParent()));

		addField(new BooleanFieldEditor(BlueMedicationConstants.CFG_URL_STAGING, "Test Server verwenden",
				getFieldEditorParent()));
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}
}
