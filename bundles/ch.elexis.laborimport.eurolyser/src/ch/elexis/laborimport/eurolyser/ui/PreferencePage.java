package ch.elexis.laborimport.eurolyser.ui;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.laborimport.eurolyser.EurolyserImporter;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public PreferencePage(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.mandantCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new StringFieldEditor(EurolyserImporter.CONFIG_IMPORT_MANDANTONLY,
			"Import nur für Mandanten (Kürzel)", getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
}
