package at.medevit.elexis.corona123.preference;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class WorkbenchPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	@Override
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
		setDescription("corona123.ch Einstellungen");
	}
	
	@Override
	protected void createFieldEditors(){
		// organization-id
		addField(new StringFieldEditor(PreferenceConstants.CFG_CORONA123_ORGID,
			"Corona123 Organisations-ID", getFieldEditorParent()));
	}
}
