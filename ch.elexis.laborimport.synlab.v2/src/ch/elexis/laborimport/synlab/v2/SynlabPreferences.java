package ch.elexis.laborimport.synlab.v2;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.wb.swt.ResourceManager;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class SynlabPreferences extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {
		
	public static final String CFG_SYNLAB2_DOWNLOAD_DIR = "synlab2/downloaddir"; //$NON-NLS-1$
	public static final String CFG_SYNLAB2_GDT_EXPORT_DIR = "synlab2/gdt/exportdir"; //$NON-NLS-1$
	
	public SynlabPreferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		setImageDescriptor(ResourceManager
			.getPluginImageDescriptor("ch.elexis.laborimport.synlab.v2", "icons/synlab.png"));
	}
	
	@Override
	public void init(IWorkbench workbench){
		if (CoreHub.localCfg.get(CFG_SYNLAB2_DOWNLOAD_DIR, null) == null) {
			File downloadDir = new File(getDefaultDownloadDir());
			if (!downloadDir.exists()) {
				downloadDir.mkdirs();
			}
			CoreHub.localCfg.set(CFG_SYNLAB2_DOWNLOAD_DIR, downloadDir.getAbsolutePath());
		}
		
		if (CoreHub.localCfg.get(CFG_SYNLAB2_GDT_EXPORT_DIR, null) == null) {
			File gdtExportDir = new File(getDefaultGDTExportDir());
			if (!gdtExportDir.exists()) {
				gdtExportDir.mkdirs();
			}
			CoreHub.localCfg.set(CFG_SYNLAB2_GDT_EXPORT_DIR, gdtExportDir.getAbsolutePath());
		}
	}
	
	@Override
	protected void createFieldEditors(){
		addField(new DirectoryFieldEditor(CFG_SYNLAB2_DOWNLOAD_DIR, Messages.Prefs_DownloadDir,
			getFieldEditorParent()));
		addField(new DirectoryFieldEditor(CFG_SYNLAB2_GDT_EXPORT_DIR, Messages.Prefs_GDTExportDir,
			getFieldEditorParent()));
	}
	
	@Override
	public boolean performOk(){
		CoreHub.localCfg.flush();
		return super.performOk();
	}
	
	private String getDefaultDownloadDir(){
		return CoreHub.getWritableUserDir() + File.separator + "synlab2" + File.separator
			+ "download";
	}
	
	private String getDefaultGDTExportDir(){
		return CoreHub.getWritableUserDir() + File.separator + "synlab2" + File.separator + "gdt";
	}
}
