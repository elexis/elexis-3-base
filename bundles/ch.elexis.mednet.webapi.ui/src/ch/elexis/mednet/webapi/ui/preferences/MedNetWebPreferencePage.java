package ch.elexis.mednet.webapi.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditor;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;

/**
 * Preference page for configuring MedNet Web API settings. Supports both global
 * fallback configurations and mandator-specific overrides.
 */
public class MedNetWebPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private static final Logger log = LoggerFactory.getLogger(MedNetWebPreferencePage.class);

	public static final String DEMO = "DEMO"; //$NON-NLS-1$
	public static final String PRODUKTIV = "PRODUKTIV"; //$NON-NLS-1$

	private static final String OVERRIDE_GLOBAL_SUFFIX = "_override_global"; //$NON-NLS-1$

	@Inject
	private IConfigService configService;

	private URIFieldEditor downloadPathEditor;
	private StringFieldEditor loginNameEditor;
	private BooleanFieldEditor confirmBeforeSendEditor;
	private RadioGroupFieldEditor operatingModeEditor;

	private Combo scopeSelectionCombo;
	private Button overrideGlobalButton;
	private List<IMandator> availableMandators;
	private ConfigServicePreferenceStore globalPreferenceStore;

	private int currentScopeIndex = 0;

	/**
	 * Initializes the preference page with grid layout.
	 */
	public MedNetWebPreferencePage() {
		super(GRID);
	}

	/**
	 * Initializes the preference page, injects required services, and sets up the
	 * global preference store. * @param workbench the active workbench
	 */
	@Override
	public void init(IWorkbench workbench) {
		CoreUiUtil.injectServices(this);
		globalPreferenceStore = new ConfigServicePreferenceStore(Scope.GLOBAL);
		setPreferenceStore(globalPreferenceStore);
		setDescription(Messages.MedNetWebPreferencePage_configForMedNetWebAPI);
	}

	/**
	 * Creates all necessary field editors for the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		createScopeSelectionArea(parent);

		// Führt die Migration alter Dateipfade (C:\...) in das URI-Format (file:/...)
		// durch
		migrateLegacyPaths();

		createSeparator(parent);
		createConfigurationEditors(parent);

		loadScope(0);
	}

	/**
	 * Scans global and mandator-specific preferences for legacy directory paths and
	 * converts them into proper URI strings to ensure compatibility with
	 * URIFieldEditor.
	 */
	private void migrateLegacyPaths() {
		migrateLegacyPath(PreferenceConstants.MEDNET_DOWNLOAD_PATH);

		if (availableMandators != null) {
			for (IMandator mandator : availableMandators) {
				migrateLegacyPath(mandator.getId() + "_" + PreferenceConstants.MEDNET_DOWNLOAD_PATH); //$NON-NLS-1$
			}
		}
	}

	private void migrateLegacyPath(String key) {
		String path = globalPreferenceStore.getString(key);
		if (StringUtils.isNotBlank(path) && !path.startsWith("file:") && !path.startsWith("smb:") //$NON-NLS-1$ //$NON-NLS-2$
				&& !path.startsWith("dav")) { //$NON-NLS-1$
			try {
				String uriString = new File(path).toURI().toString();
				globalPreferenceStore.setValue(key, uriString);
				log.info("Successfully migrated legacy path to URI format for key [{}]: [{}]", key, uriString);
			} catch (Exception exception) {
				log.warn("Could not migrate legacy path to URI format: [{}]", path, exception);
			}
		}
	}

	private void createScopeSelectionArea(Composite parent) {
		Composite topComposite = new Composite(parent, SWT.NONE);
		GridData topGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
		topGridData.horizontalSpan = 3;
		topComposite.setLayoutData(topGridData);
		topComposite.setLayout(new GridLayout(2, false));

		Label scopeLabel = new Label(topComposite, SWT.NONE);
		scopeLabel.setText(Messages.MedNetWebPreferencePage_editSettingsFor);

		scopeSelectionCombo = new Combo(topComposite, SWT.READ_ONLY);
		scopeSelectionCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		new Label(topComposite, SWT.NONE);
		overrideGlobalButton = new Button(topComposite, SWT.CHECK);
		overrideGlobalButton.setText(Messages.MedNetWebPreferencePage_overrideGlobal);
		overrideGlobalButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		populateScopeCombo();

		scopeSelectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleMandatorSwitch();
			}
		});

		overrideGlobalButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				handleOverrideToggle();
			}
		});
	}

	private void populateScopeCombo() {
		scopeSelectionCombo.add(Messages.MedNetWebPreferencePage_globalSettings);
		availableMandators = new ArrayList<>();

		try {
			List<IUser> allUsers = CoreModelServiceHolder.get().getQuery(IUser.class).execute();
			if (allUsers != null) {
				for (IUser user : allUsers) {
					if (user.isActive() && user.getAssignedContact() != null) {
						Optional<IMandator> optionalMandator = CoreModelServiceHolder.get()
								.load(user.getAssignedContact().getId(), IMandator.class);

						if (optionalMandator.isPresent()) {
							IMandator mandator = optionalMandator.get();
							if (mandator.isActive() && !mandator.isDeleted()
									&& !availableMandators.contains(mandator)) {
								availableMandators.add(mandator);
							}
						}
					}
				}

				availableMandators.sort((m1, m2) -> m1.getLabel().compareToIgnoreCase(m2.getLabel()));
				for (IMandator mandator : availableMandators) {
					scopeSelectionCombo.add(Messages.MedNetWebPreferencePage_mandatorPrefix + mandator.getLabel());
				}
			}
		} catch (Exception exception) {
			log.error("Failed to load users and mandators for MedNet scope selection.", exception);
		}

		scopeSelectionCombo.select(0);
		currentScopeIndex = 0;
	}

	private void createSeparator(Composite parent) {
		Label separator = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData separatorGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		separatorGridData.horizontalSpan = 3;
		separator.setLayoutData(separatorGridData);
	}

	private void createConfigurationEditors(Composite parent) {
		downloadPathEditor = new URIFieldEditor(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
				Messages.MedNetWebPreferencePage_downloadFolder, parent);
		downloadPathEditor.setEmptyStringAllowed(true);
		addField(downloadPathEditor);

		loginNameEditor = new StringFieldEditor(PreferenceConstants.MEDNET_USER_STRING,
				Messages.MedNetWebPreferencePage_loginName, parent);
		addField(loginNameEditor);

		confirmBeforeSendEditor = new BooleanFieldEditor(PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND,
				Messages.MedNetWebPreferencePage_confirmBeforeSend, parent);
		addField(confirmBeforeSendEditor);

		String[][] modeOptions = new String[][] { { Messages.MedNetWebPreferencePage_demoMode, DEMO },
				{ Messages.MedNetWebPreferencePage_produktivMode, PRODUKTIV } };

		operatingModeEditor = new RadioGroupFieldEditor(PreferenceConstants.MEDNET_MODE,
				Messages.MedNetWebPreferencePage_operatingMode, 1, modeOptions, parent, true);
		addField(operatingModeEditor);
	}

	private void handleMandatorSwitch() {
		int newSelectionIndex = scopeSelectionCombo.getSelectionIndex();
		if (newSelectionIndex == currentScopeIndex) {
			return;
		}

		saveScope(currentScopeIndex);
		currentScopeIndex = newSelectionIndex;
		loadScope(newSelectionIndex);
	}

	private void handleOverrideToggle() {
		if (currentScopeIndex == 0) {
			return;
		}

		boolean isOverrideEnabled = overrideGlobalButton.getSelection();
		IMandator selectedMandator = availableMandators.get(currentScopeIndex - 1);
		String prefix = selectedMandator.getId() + "_"; //$NON-NLS-1$

		if (!isOverrideEnabled) {
			downloadPathEditor.store();
			loginNameEditor.store();
			confirmBeforeSendEditor.store();
			operatingModeEditor.store();
		}

		globalPreferenceStore.setValue(selectedMandator.getId() + OVERRIDE_GLOBAL_SUFFIX, isOverrideEnabled);

		String activePrefix = isOverrideEnabled ? prefix : StringUtils.EMPTY;
		updateEditorKeysAndLoad(activePrefix);
		setEditorsEnabled(isOverrideEnabled, false);
	}

	private void saveScope(int index) {
		if (index > 0 && availableMandators != null) {
			IMandator mandator = availableMandators.get(index - 1);
			globalPreferenceStore.setValue(mandator.getId() + OVERRIDE_GLOBAL_SUFFIX,
					overrideGlobalButton.getSelection());
		}

		String prefix = getPrefixForIndex(index);
		String modeKey = prefix + PreferenceConstants.MEDNET_MODE;
		String previousMode = globalPreferenceStore.getString(modeKey);

		downloadPathEditor.store();
		loginNameEditor.store();
		confirmBeforeSendEditor.store();
		operatingModeEditor.store();

		String newlySelectedMode = globalPreferenceStore.getString(modeKey);

		if (StringUtils.isNotBlank(previousMode) && !previousMode.equals(newlySelectedMode)) {
			clearMandatorTokens();
			MessageDialog.openWarning(getShell(), Messages.MedNetMainComposite_restartRequiredTitle,
					Messages.MedNetMainComposite_restartRequiredMessage);
		}
	}

	private void loadScope(int index) {
		boolean isGlobalMode = (index == 0);
		String prefix = StringUtils.EMPTY;
		boolean isOverrideActive = false;

		if (isGlobalMode) {
			overrideGlobalButton.setEnabled(false);
			overrideGlobalButton.setSelection(false);
		} else {
			overrideGlobalButton.setEnabled(true);
			IMandator selectedMandator = availableMandators.get(index - 1);
			prefix = selectedMandator.getId() + "_"; //$NON-NLS-1$
			isOverrideActive = globalPreferenceStore.getBoolean(selectedMandator.getId() + OVERRIDE_GLOBAL_SUFFIX);
			overrideGlobalButton.setSelection(isOverrideActive);
		}

		String activePrefixForEditors = (!isGlobalMode && isOverrideActive) ? prefix : StringUtils.EMPTY;

		updateEditorKeysAndLoad(activePrefixForEditors);
		setEditorsEnabled(isOverrideActive, isGlobalMode);
	}

	private String getPrefixForIndex(int index) {
		if (index == 0) {
			return StringUtils.EMPTY;
		}
		boolean isOverrideActive = overrideGlobalButton.getSelection();
		if (!isOverrideActive) {
			return StringUtils.EMPTY;
		}
		IMandator selectedMandator = availableMandators.get(index - 1);
		return selectedMandator.getId() + "_"; //$NON-NLS-1$
	}

	private void updateEditorKeysAndLoad(String editorPrefix) {
		downloadPathEditor.setPreferenceName(editorPrefix + PreferenceConstants.MEDNET_DOWNLOAD_PATH);
		loginNameEditor.setPreferenceName(editorPrefix + PreferenceConstants.MEDNET_USER_STRING);
		confirmBeforeSendEditor.setPreferenceName(editorPrefix + PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND);
		operatingModeEditor.setPreferenceName(editorPrefix + PreferenceConstants.MEDNET_MODE);

		downloadPathEditor.load();
		loginNameEditor.load();
		confirmBeforeSendEditor.load();
		operatingModeEditor.load();
	}

	private void setEditorsEnabled(boolean overrideEnabled, boolean isGlobalMode) {
		Composite parent = getFieldEditorParent();
		boolean allowEditing = isGlobalMode || overrideEnabled;

		downloadPathEditor.setEnabled(allowEditing, parent);
		loginNameEditor.setEnabled(allowEditing, parent);
		confirmBeforeSendEditor.setEnabled(allowEditing, parent);
		operatingModeEditor.setEnabled(allowEditing, parent);
	}

	private void clearMandatorTokens() {
		if (configService != null) {
			configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + "mednet", null); //$NON-NLS-1$
			configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + "mednet", null); //$NON-NLS-1$
			configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + "mednet", null); //$NON-NLS-1$
		}
	}

	/**
	 * Saves the currently active scope and applies the preferences. * @return true
	 * if the changes were successfully saved
	 */
	@Override
	public boolean performOk() {
		saveScope(currentScopeIndex);
		return super.performOk();
	}
}