package at.medevit.elexis.impfplan.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String ID = "at.medevit.elexis.impfplan.ui.preferences";
	
	public static final String PREFBASE = "plugins/impfplan/";
	public static final String VAC_PDF_OUTPUTDIR = PREFBASE + "outputdir";
	public static final String VAC_SORT_ORDER = PREFBASE + "sortorder";
	public static final String VAC_BILLING_POS = PREFBASE + "defleistungen";
	
	public PreferencePage(){}
	
	@Override
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.userCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		DirectoryFieldEditor editor =
			new DirectoryFieldEditor(VAC_PDF_OUTPUTDIR, "Standard Ausgabeverzeichnis",
				getFieldEditorParent());
		addField(editor);
		
	}
}
