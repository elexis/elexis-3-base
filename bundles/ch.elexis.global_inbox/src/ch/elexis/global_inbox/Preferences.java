package ch.elexis.global_inbox;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.global_inbox.ui.Messages;

public class Preferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String PREFERENCE_BRANCH = "plugins/global_inbox/"; //$NON-NLS-1$
	public static final String PREF_DIR = PREFERENCE_BRANCH + "dir"; //$NON-NLS-1$
	public static final String PREF_AUTOBILLING = PREFERENCE_BRANCH + "autobilling"; //$NON-NLS-1$
	public static final String PREF_INFO_IN_INBOX = PREFERENCE_BRANCH + "infoToInbox"; //$NON-NLS-1$
	public static final String PREF_DIR_DEFAULT = StringUtils.EMPTY;
	public static final String PREF_TITLE_COMPLETION = PREFERENCE_BRANCH + "titleCompletions"; //$NON-NLS-1$
	public static final String STOREFSGLOBAL = PREFERENCE_BRANCH + "store_in_fs_global"; //$NON-NLS-1$

	private IPreferenceStore fsSettingsStore;

	private BooleanFieldEditor bStoreFSGlobal;
	private DirectoryFieldEditor dirFieldEditor;

	public Preferences() {
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
	}

	@Override
	protected void createFieldEditors() {
		bStoreFSGlobal = new BooleanFieldEditor(STOREFSGLOBAL, "Dateisystem Einstellungen global speichern",
				getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				ConfigServiceHolder.get().set(STOREFSGLOBAL, (Boolean) newValue);
				updateFSSettingsStore();
			}
		};
		addField(bStoreFSGlobal);

		dirFieldEditor = new DirectoryFieldEditor(PREF_DIR, Messages.Preferences_directory, getFieldEditorParent());

		BooleanFieldEditor bAutomaticBilling = new BooleanFieldEditor(PREF_AUTOBILLING,
				"Automatische Verrechnung bei import", getFieldEditorParent());
		addField(bAutomaticBilling);

		BooleanFieldEditor bInfoToInbox = new BooleanFieldEditor(PREF_INFO_IN_INBOX, "Vorselektion Info am Stammarzt",
				getFieldEditorParent());
		addField(bInfoToInbox);

		dirFieldEditor.getTextControl(getFieldEditorParent()).setEditable(false);
		addField(dirFieldEditor);

		updateFSSettingsStore();
	}

	private void updateFSSettingsStore() {
		boolean isGlobal = ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false);
		if (isGlobal) {
			fsSettingsStore = new ConfigServicePreferenceStore(Scope.GLOBAL);
			dirFieldEditor.getTextControl(getFieldEditorParent()).setEditable(true);
		} else {
			fsSettingsStore = getPreferenceStore();
			dirFieldEditor.getTextControl(getFieldEditorParent()).setEditable(false);
		}
		bStoreFSGlobal.setPreferenceStore(fsSettingsStore);
		bStoreFSGlobal.load();
		dirFieldEditor.setPreferenceStore(fsSettingsStore);
		dirFieldEditor.load();
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
