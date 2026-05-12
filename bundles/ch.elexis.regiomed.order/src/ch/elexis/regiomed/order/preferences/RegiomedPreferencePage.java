package ch.elexis.regiomed.order.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.KontaktFieldEditor;
import ch.elexis.regiomed.order.messages.Messages;
import ch.rgw.tools.StringTool;

public class RegiomedPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private StringFieldEditor baseUrlEditor;
	private StringFieldEditor clientIdEditor;
	private StringFieldEditor emailEditor;
	private StringFieldEditor passwordEditor;
	private KontaktFieldEditor supplierEditor;

	private BooleanFieldEditor errorMailEnabledEditor;
	private StringFieldEditor errorMailAddressEditor;

	private Combo comboScope;
	private Button btnOverrideGlobal;
	private List<IMandator> mandators;
	private ConfigServicePreferenceStore globalStore;

	private int currentScopeIndex = 0;

	public RegiomedPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		globalStore = new ConfigServicePreferenceStore(Scope.GLOBAL);
		setPreferenceStore(globalStore);
		setDescription(Messages.RegiomedPreferencePage_Description);
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		Composite topComp = new Composite(parent, SWT.NONE);
		GridData topGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		topGd.horizontalSpan = 3;
		topComp.setLayoutData(topGd);
		topComp.setLayout(new GridLayout(2, false));

		Label lblScope = new Label(topComp, SWT.NONE);
		lblScope.setText(Messages.RegiomedPreferencePage_EditSettingsFor);

		comboScope = new Combo(topComp, SWT.READ_ONLY);
		comboScope.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		new Label(topComp, SWT.NONE);
		btnOverrideGlobal = new Button(topComp, SWT.CHECK);
		btnOverrideGlobal.setText(Messages.RegiomedPreferencePage_OverrideGlobal);
		btnOverrideGlobal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		comboScope.add(Messages.RegiomedPreferencePage_GlobalSettings);
		List<IUser> allUsers = CoreModelServiceHolder.get().getQuery(IUser.class).execute();
		mandators = new ArrayList<>();

		if (allUsers != null) {
			for (IUser user : allUsers) {
				if (user.isActive() && user.getAssignedContact() != null) {

					Optional<IMandator> optMandator = CoreModelServiceHolder.get()
							.load(user.getAssignedContact().getId(), IMandator.class);

					if (optMandator.isPresent()) {
						IMandator m = optMandator.get();
						if (m.isActive() && !m.isDeleted() && !mandators.contains(m)) {
							mandators.add(m);
						}
					}
				}
			}

			mandators.sort((m1, m2) -> m1.getLabel().compareToIgnoreCase(m2.getLabel()));
			for (IMandator m : mandators) {
				comboScope.add(Messages.RegiomedPreferencePage_MandatorPrefix + m.getLabel());
			}
		}

		comboScope.select(0);
		currentScopeIndex = 0;

		comboScope.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleMandatorSwitch();
			}
		});

		btnOverrideGlobal.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleOverrideToggle();
			}
		});

		Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData sepGd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		sepGd.horizontalSpan = 3;
		sep.setLayoutData(sepGd);

		baseUrlEditor = new StringFieldEditor(RegiomedConstants.PREF_BASE_URL,
				Messages.RegiomedPreferencePage_ServiceUrlLabel, parent);
		addField(baseUrlEditor);

		clientIdEditor = new StringFieldEditor(RegiomedConstants.PREF_CLIENT_ID,
				Messages.RegiomedPreferencePage_ClientIdLabel, parent);
		addField(clientIdEditor);

		emailEditor = new StringFieldEditor(RegiomedConstants.PREF_EMAIL,
				Messages.RegiomedPreferencePage_EmailUserLabel, parent);
		addField(emailEditor);

		passwordEditor = new StringFieldEditor(RegiomedConstants.PREF_PASSWORD,
				Messages.RegiomedPreferencePage_PasswordLabel, parent);
		passwordEditor.getTextControl(parent).setEchoChar('*');
		addField(passwordEditor);

		supplierEditor = new KontaktFieldEditor(globalStore, RegiomedConstants.CFG_REGIOMED_SUPPLIER,
				Messages.RegiomedPreferencePage_SupplierLabel, parent);
		addField(supplierEditor);

		errorMailEnabledEditor = new BooleanFieldEditor(RegiomedConstants.PREF_ERROR_EMAIL_ENABLED,
				Messages.RegiomedPreferencePage_ErrorMailCheckbox, parent);
		addField(errorMailEnabledEditor);

		errorMailAddressEditor = new StringFieldEditor(RegiomedConstants.PREF_ERROR_EMAIL_ADDRESS,
				Messages.RegiomedPreferencePage_ErrorMailAddressLabel, parent);
		addField(errorMailAddressEditor);

		hookErrorEmailBehaviour(parent);

		loadScope(0);
	}

	private void handleMandatorSwitch() {
		int newIndex = comboScope.getSelectionIndex();
		if (newIndex == currentScopeIndex)
			return;

		saveScope(currentScopeIndex);

		currentScopeIndex = newIndex;
		loadScope(newIndex);
	}

	private void saveScope(int index) {
		if (index > 0 && mandators != null) {
			IMandator m = mandators.get(index - 1);
			globalStore.setValue(m.getId() + RegiomedConstants.SUFFIX_OVERRIDE_GLOBAL,
					btnOverrideGlobal.getSelection());
		}

		baseUrlEditor.store();
		clientIdEditor.store();
		emailEditor.store();
		passwordEditor.store();
		supplierEditor.store();
		errorMailEnabledEditor.store();
		errorMailAddressEditor.store();
	}

	private void loadScope(int index) {
		boolean isGlobalMode = (index == 0);
		String prefix = StringUtils.EMPTY;
		boolean override = false;

		if (isGlobalMode) {
			btnOverrideGlobal.setEnabled(false);
			btnOverrideGlobal.setSelection(false);
		} else {
			btnOverrideGlobal.setEnabled(true);
			IMandator selected = mandators.get(index - 1);
			prefix = selected.getId() + "_";
			override = globalStore.getBoolean(selected.getId() + RegiomedConstants.SUFFIX_OVERRIDE_GLOBAL);
			btnOverrideGlobal.setSelection(override);
		}

		String activePrefixForLogins = (!isGlobalMode && override) ? prefix : StringUtils.EMPTY;

		updateEditorKeysAndLoad(activePrefixForLogins);
		setEditorsEnabled(override, isGlobalMode);
		updateErrorMailCheckboxState(getFieldEditorParent());
	}

	private void handleOverrideToggle() {
		if (currentScopeIndex == 0)
			return;

		boolean override = btnOverrideGlobal.getSelection();
		IMandator selected = mandators.get(currentScopeIndex - 1);
		String prefix = selected.getId() + "_";

		if (!override) {
			clientIdEditor.store();
			emailEditor.store();
			passwordEditor.store();
			errorMailEnabledEditor.store();
			errorMailAddressEditor.store();
		}

		globalStore.setValue(selected.getId() + RegiomedConstants.SUFFIX_OVERRIDE_GLOBAL, override);

		String activePrefix = override ? prefix : StringUtils.EMPTY;
		updateEditorKeysAndLoad(activePrefix);

		setEditorsEnabled(override, false);
		updateErrorMailCheckboxState(getFieldEditorParent());
	}

	private void updateEditorKeysAndLoad(String loginPrefix) {

		baseUrlEditor.setPreferenceName(RegiomedConstants.PREF_BASE_URL);
		supplierEditor.setPreferenceName(RegiomedConstants.CFG_REGIOMED_SUPPLIER);

		clientIdEditor.setPreferenceName(loginPrefix + RegiomedConstants.PREF_CLIENT_ID);
		emailEditor.setPreferenceName(loginPrefix + RegiomedConstants.PREF_EMAIL);
		passwordEditor.setPreferenceName(loginPrefix + RegiomedConstants.PREF_PASSWORD);
		errorMailEnabledEditor.setPreferenceName(loginPrefix + RegiomedConstants.PREF_ERROR_EMAIL_ENABLED);
		errorMailAddressEditor.setPreferenceName(loginPrefix + RegiomedConstants.PREF_ERROR_EMAIL_ADDRESS);

		baseUrlEditor.load();
		clientIdEditor.load();
		emailEditor.load();
		passwordEditor.load();
		supplierEditor.load();
		errorMailEnabledEditor.load();
		errorMailAddressEditor.load();
	}

	private void setEditorsEnabled(boolean overrideEnabled, boolean isGlobalMode) {
		Composite parent = getFieldEditorParent();

		baseUrlEditor.setEnabled(isGlobalMode, parent);
		supplierEditor.setEnabled(isGlobalMode, parent);

		boolean allowLoginEdit = isGlobalMode || overrideEnabled;
		clientIdEditor.setEnabled(allowLoginEdit, parent);
		emailEditor.setEnabled(allowLoginEdit, parent);
		passwordEditor.setEnabled(allowLoginEdit, parent);
		errorMailAddressEditor.setEnabled(allowLoginEdit, parent);
	}

	private void hookErrorEmailBehaviour(Composite parent) {
		updateErrorMailCheckboxState(parent);
		errorMailAddressEditor.getTextControl(parent).addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateErrorMailCheckboxState(parent);
			}
		});
	}

	private void updateErrorMailCheckboxState(Composite parent) {
		String addr = StringUtils.trimToEmpty(errorMailAddressEditor.getStringValue());
		boolean valid = StringTool.isMailAddress(addr);

		Control ctrl = errorMailEnabledEditor.getDescriptionControl(parent);
		if (!(ctrl instanceof Button))
			return;
		Button check = (Button) ctrl;

		if (errorMailAddressEditor.getTextControl(parent).isEnabled()) {
			check.setEnabled(valid);
			if (!valid) {
				check.setSelection(false);
			}
		} else {
			check.setEnabled(false);
		}
	}

	@Override
	public boolean performOk() {
		saveScope(currentScopeIndex);

		if (!validateErrorEmail()) {
			return false;
		}
		return super.performOk();
	}

	private boolean validateErrorEmail() {
		boolean enabled = errorMailEnabledEditor.getBooleanValue();
		String addr = StringUtils.trimToEmpty(errorMailAddressEditor.getStringValue());

		if (!enabled || !errorMailAddressEditor.getTextControl(getFieldEditorParent()).isEnabled()) {
			return true;
		}
		if (addr.isEmpty()) {
			MessageDialog.openError(getShell(), Messages.RegiomedPreferencePage_InvalidMailTitle,
					Messages.RegiomedPreferencePage_EnterMailOrDisable);
			return false;
		}
		if (!StringTool.isMailAddress(addr)) {
			MessageDialog.openError(getShell(), Messages.RegiomedPreferencePage_InvalidMailTitle,
					Messages.RegiomedPreferencePage_InvalidMailMessage);
			return false;
		}
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}
}