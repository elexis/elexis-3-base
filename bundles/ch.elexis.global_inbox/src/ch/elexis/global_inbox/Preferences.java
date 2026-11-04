package ch.elexis.global_inbox;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.preferences.PreferencesUtil;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditor;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.core.utils.CoreUtil.OS;
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
	private URIFieldEditor dirFieldEditor;
	private OS selectedOs;
	private boolean pathLoaded = false;

	public Preferences() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
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

		selectedOs = CoreUtil.getOperatingSystemType();

		Composite dirComposite = new Composite(getFieldEditorParent(), SWT.NONE);
		dirComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));

		Label lblOs = new Label(dirComposite, SWT.NONE);
		lblOs.setText("Betriebssystem");

		Combo comboOs = new Combo(dirComposite, SWT.READ_ONLY);
		comboOs.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		ComboViewer osCombo = new ComboViewer(comboOs);
		osCombo.setContentProvider(ArrayContentProvider.getInstance());
		osCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((OS) element).name();
			}
		});
		osCombo.setInput(Arrays.stream(OS.values()).filter(os -> os != OS.UNSPECIFIED).toArray());

		dirFieldEditor = new URIFieldEditor(PREF_DIR, Messages.Preferences_directory, dirComposite);
		dirFieldEditor.setEmptyStringAllowed(true);

		Text textControl = dirFieldEditor.getTextControl(dirComposite);
		textControl.setEchoChar('\0');
		textControl.setEnabled(true);
		textControl.setEditable(false);

		BooleanFieldEditor bAutomaticBilling = new BooleanFieldEditor(PREF_AUTOBILLING,
				"Automatische Verrechnung bei import", getFieldEditorParent());
		addField(bAutomaticBilling);

		BooleanFieldEditor bInfoToInbox = new BooleanFieldEditor(PREF_INFO_IN_INBOX, "Vorselektion Info am Stammarzt",
				getFieldEditorParent());
		addField(bInfoToInbox);

		addField(dirFieldEditor);

		if (selectedOs != OS.UNSPECIFIED) {
			osCombo.setSelection(new StructuredSelection(selectedOs));
		}
		osCombo.addSelectionChangedListener(
				event -> loadForOperatingSystem((OS) event.getStructuredSelection().getFirstElement()));
	}

	@Override
	protected void initialize() {
		super.initialize();
		updateFSSettingsStore();
	}

	private void updateFSSettingsStore() {
		boolean isGlobal = ConfigServiceHolder.getGlobal(STOREFSGLOBAL, false);
		fsSettingsStore = new ConfigServicePreferenceStore(isGlobal ? Scope.GLOBAL : Scope.LOCAL);
		bStoreFSGlobal.setPreferenceStore(fsSettingsStore);
		bStoreFSGlobal.load();
		dirFieldEditor.setPreferenceStore(fsSettingsStore);
		pathLoaded = false;
		loadForOperatingSystem(selectedOs);
	}

	private void loadForOperatingSystem(OS operatingSystem) {
		if (pathLoaded) {
			dirFieldEditor.store();
		}
		selectedOs = operatingSystem;
		dirFieldEditor.setPreferenceName(PreferencesUtil.getOsSpecificPreferenceName(operatingSystem, PREF_DIR));
		dirFieldEditor.load();

		if (operatingSystem == CoreUtil.getOperatingSystemType() && dirFieldEditor.getStringValue().isBlank()) {
			String legacyPath = fsSettingsStore.getString(PREF_DIR);
			if (StringUtils.isNotBlank(legacyPath)) {
				dirFieldEditor.setStringValue(legacyPath);
			}
		}
		pathLoaded = true;
	}

	@Override
	public void init(IWorkbench workbench) {

	}
}
