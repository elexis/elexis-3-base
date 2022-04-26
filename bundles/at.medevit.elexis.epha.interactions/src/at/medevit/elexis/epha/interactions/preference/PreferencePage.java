package at.medevit.elexis.epha.interactions.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(EphaConstants.CFG_USE_REST, "Epha Interaktionen REST API verwnden",
				getFieldEditorParent()));
	}

}
