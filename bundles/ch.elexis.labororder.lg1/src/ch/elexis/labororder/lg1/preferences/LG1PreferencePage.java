package ch.elexis.labororder.lg1.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.labororder.lg1.messages.Messages;

/**
 * Einstellungen für Medics Plugin
 * 
 * @author immi
 */
public class LG1PreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	public static final String UPLOAD_DIR = "ch.elexis.labororder.lg1/upload"; //$NON-NLS-1$
	
	private static final String DEFAULT_UPLOAD = ""; //$NON-NLS-1$
	
	public LG1PreferencePage(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		getPreferenceStore().setDefault(UPLOAD_DIR, DEFAULT_UPLOAD);
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new DirectoryFieldEditor(UPLOAD_DIR, Messages.Lg1PreferencePage_labelUploadDir,
			getFieldEditorParent()));
	}
	
	@Override
	public void init(IWorkbench workbench){}
	
	public static String getUploadDir(){
		return CoreHub.localCfg.get(UPLOAD_DIR, DEFAULT_UPLOAD);
	}
}
