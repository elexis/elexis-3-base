package ch.elexis.global_inbox;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String PREFERENCE_BRANCH = "plugins/global_inbox/"; //$NON-NLS-1$
	public static final String PREF_DIR = PREFERENCE_BRANCH + "dir"; //$NON-NLS-1$
	public static final String PREF_AUTOBILLING = PREFERENCE_BRANCH + "autobilling"; //$NON-NLS-1$
	public static final String PREF_DIR_DEFAULT = "";
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		DirectoryFieldEditor dirFieldEditor =
			new DirectoryFieldEditor(PREF_DIR, Messages.Preferences_directory,
				getFieldEditorParent());
		
		BooleanFieldEditor bAutomaticBilling = new BooleanFieldEditor(PREF_AUTOBILLING,
			"Automatische Verrechnung bei import", getFieldEditorParent());
		addField(bAutomaticBilling);
		
		dirFieldEditor.getTextControl(getFieldEditorParent()).setEditable(false);
		addField(dirFieldEditor);
	}
	
	@Override
	public void init(IWorkbench workbench){
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
}
