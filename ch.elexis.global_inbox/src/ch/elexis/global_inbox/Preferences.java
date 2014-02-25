package ch.elexis.global_inbox;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String PREFERENCE_BRANCH = "plugins/global_inbox/"; //$NON-NLS-1$
	public static final String PREF_DIR = PREFERENCE_BRANCH + "dir"; //$NON-NLS-1$
	public static final String PREF_DIR_DEFAULT =
		System.getProperty("user.home") + File.separator + "elexis" + File.separator + "Eingangsfach";//$NON-NLS-1$
	
	public Preferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new DirectoryFieldEditor(PREF_DIR, Messages.Preferences_directory,
			getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench){
		
	}
	
	@Override
	protected void performApply(){
		super.performApply();
		CoreHub.localCfg.flush();
	}
	
}
