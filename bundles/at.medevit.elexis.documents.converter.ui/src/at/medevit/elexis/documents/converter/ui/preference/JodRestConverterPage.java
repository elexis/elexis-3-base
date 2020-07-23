package at.medevit.elexis.documents.converter.ui.preference;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class JodRestConverterPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
	
	public JodRestConverterPage(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.globalCfg));
	}
	
	@Override
	public void init(IWorkbench workbench){
		
	}
	
	@Override
	protected void createFieldEditors(){
		StringFieldEditor jodRestBasPath = new StringFieldEditor("jodrestconverter/basepath",
			"JODconverter REST URL", getFieldEditorParent());
		addField(jodRestBasPath);
	}
	
	@Override
	public boolean performOk(){
		CoreHub.globalCfg.flush();
		return super.performOk();
	}
}
