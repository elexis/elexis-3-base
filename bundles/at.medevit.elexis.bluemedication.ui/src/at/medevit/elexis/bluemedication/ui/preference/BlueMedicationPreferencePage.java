package at.medevit.elexis.bluemedication.ui.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class BlueMedicationPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	private ConfigServicePreferenceStore localPreferenceStore;
	
	public BlueMedicationPreferencePage(){
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		localPreferenceStore = new ConfigServicePreferenceStore(Scope.LOCAL);
		setDescription("BlueMedication Einstellungen");
	}
	
	@Override
	protected void createFieldEditors(){
		StringFieldEditor editor = new StringFieldEditor(BlueMedicationConstants.CFG_HIN_PROXY_HOST,
			"HIN Proxy Host", getFieldEditorParent());
		editor.setPreferenceStore(localPreferenceStore);
		addField(editor);
		
		editor = new StringFieldEditor(BlueMedicationConstants.CFG_HIN_PROXY_PORT, "HIN Proxy Port",
			getFieldEditorParent());
		editor.setPreferenceStore(localPreferenceStore);
		addField(editor);
		
		addField(new BooleanFieldEditor(BlueMedicationConstants.CFG_USE_IMPORT,
			"Medikationsabgleich bei Blue Medication nutzen. (Automatisch deaktiviert bei Eigenartikel in Medikation des Patienten)",
			getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(BlueMedicationConstants.CFG_URL_STAGING,
			"Test Server verwenden", getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench){
		String initialvalue =
			ConfigServiceHolder.get().getLocal(BlueMedicationConstants.CFG_HIN_PROXY_HOST, null);
		if (initialvalue == null) {
			initialvalue =
				ConfigServiceHolder.get().get(BlueMedicationConstants.CFG_HIN_PROXY_HOST, null);
			if (initialvalue != null) {
				ConfigServiceHolder.get().setLocal(BlueMedicationConstants.CFG_HIN_PROXY_HOST,
					initialvalue);
			} else {
				ConfigServiceHolder.get().setLocal(BlueMedicationConstants.CFG_HIN_PROXY_HOST,
					BlueMedicationConstants.DEFAULT_HIN_PROXY_HOST);
			}
		}
		
		initialvalue =
			ConfigServiceHolder.get().getLocal(BlueMedicationConstants.CFG_HIN_PROXY_PORT, null);
		if (initialvalue == null) {
			initialvalue =
				ConfigServiceHolder.get().get(BlueMedicationConstants.CFG_HIN_PROXY_PORT, null);
			if (initialvalue != null) {
				ConfigServiceHolder.get().setLocal(BlueMedicationConstants.CFG_HIN_PROXY_PORT,
					initialvalue);
			} else {
				ConfigServiceHolder.get().setLocal(BlueMedicationConstants.CFG_HIN_PROXY_PORT,
					BlueMedicationConstants.DEFAULT_HIN_PROXY_PORT);
			}
		}
	}
}
