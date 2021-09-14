package at.medevit.elexis.bluemedication.ui.preference;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.bluemedication.core.BlueMedicationConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class BlueMedicationPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	private SettingsPreferenceStore localPreferenceStore;
	
	public BlueMedicationPreferencePage(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
		localPreferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
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
			CoreHub.localCfg.get(BlueMedicationConstants.CFG_HIN_PROXY_HOST, null);
		if (initialvalue == null) {
			initialvalue = CoreHub.globalCfg.get(BlueMedicationConstants.CFG_HIN_PROXY_HOST, null);
			if (initialvalue != null) {
				CoreHub.localCfg.set(BlueMedicationConstants.CFG_HIN_PROXY_HOST, initialvalue);
			} else {
				CoreHub.localCfg.set(BlueMedicationConstants.CFG_HIN_PROXY_HOST,
					BlueMedicationConstants.DEFAULT_HIN_PROXY_HOST);
			}
		}
		
		initialvalue = CoreHub.localCfg.get(BlueMedicationConstants.CFG_HIN_PROXY_PORT, null);
		if (initialvalue == null) {
			initialvalue = CoreHub.globalCfg.get(BlueMedicationConstants.CFG_HIN_PROXY_PORT, null);
			if (initialvalue != null) {
				CoreHub.localCfg.set(BlueMedicationConstants.CFG_HIN_PROXY_PORT, initialvalue);
			} else {
				CoreHub.localCfg.set(BlueMedicationConstants.CFG_HIN_PROXY_PORT,
					BlueMedicationConstants.DEFAULT_HIN_PROXY_PORT);
			}
		}
	}
}
