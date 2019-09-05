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
	
	public BlueMedicationPreferencePage(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
		setDescription("BlueMedication Einstellungen");
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(BlueMedicationConstants.CFG_HIN_PROXY_HOST, "HIN Proxy Host",
			getFieldEditorParent()));
		addField(new StringFieldEditor(BlueMedicationConstants.CFG_HIN_PROXY_PORT, "HIN Proxy Port",
			getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(BlueMedicationConstants.CFG_USE_IMPORT,
			"Medikationsabgleich bei Blue Medication nutzen", getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(BlueMedicationConstants.CFG_URL_STAGING,
			"Test Server verwenden", getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench){
		String initialvalue =
			CoreHub.globalCfg.get(BlueMedicationConstants.CFG_HIN_PROXY_HOST, null);
		if (initialvalue == null) {
			CoreHub.globalCfg.set(BlueMedicationConstants.CFG_HIN_PROXY_HOST,
				BlueMedicationConstants.DEFAULT_HIN_PROXY_HOST);
		}
		
		initialvalue = CoreHub.globalCfg.get(BlueMedicationConstants.CFG_HIN_PROXY_PORT, null);
		if (initialvalue == null) {
			CoreHub.globalCfg.set(BlueMedicationConstants.CFG_HIN_PROXY_PORT,
				BlueMedicationConstants.DEFAULT_HIN_PROXY_PORT);
		}
	}
}
