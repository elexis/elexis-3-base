package at.medevit.elexis.kapsch.referral.ui.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.kapsch.referral.KapschReferralService;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class KapschReferralPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.MANDATOR));
		setDescription("Mandanten spezifische Einstellungen für Kapsch Zuweisungen.");
	}

	@Override
	protected void createFieldEditors() {
		FieldEditor editor;

		editor = new ComboFieldEditor(KapschReferralService.CONFIG_ENDPOINT, "Betriebsart",
				new String[][] { { "Produktiv", KapschReferralService.ENDPOINT_PRODUCTIV },
						{ "Test", KapschReferralService.ENDPOINT_TEST } },
				getFieldEditorParent());
		addField(editor);
	}
}
