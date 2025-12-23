package ch.elexis.regiomed.order.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

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

	private BooleanFieldEditor errorMailEnabledEditor;
	private StringFieldEditor errorMailAddressEditor;

	public RegiomedPreferencePage() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		setDescription(Messages.RegiomedPreferencePage_Description);
	}

	@Override
	protected void createFieldEditors() {
		Composite parent = getFieldEditorParent();

		baseUrlEditor = new StringFieldEditor(
				RegiomedConstants.PREF_BASE_URL, Messages.RegiomedPreferencePage_ServiceUrlLabel,
				parent);
		addField(baseUrlEditor);

		clientIdEditor = new StringFieldEditor(
				RegiomedConstants.PREF_CLIENT_ID, Messages.RegiomedPreferencePage_ClientIdLabel,
				parent);
		addField(clientIdEditor);

		emailEditor = new StringFieldEditor(
				RegiomedConstants.PREF_EMAIL, Messages.RegiomedPreferencePage_EmailUserLabel,
				parent);
		addField(emailEditor);

		passwordEditor = new StringFieldEditor(
				RegiomedConstants.PREF_PASSWORD, Messages.RegiomedPreferencePage_PasswordLabel,
				parent);
		passwordEditor.getTextControl(parent).setEchoChar('*');
		addField(passwordEditor);

		addField(new KontaktFieldEditor(new ConfigServicePreferenceStore(Scope.GLOBAL),
				RegiomedConstants.CFG_REGIOMED_SUPPLIER, Messages.RegiomedPreferencePage_SupplierLabel, parent));

		errorMailEnabledEditor = new BooleanFieldEditor(RegiomedConstants.PREF_ERROR_EMAIL_ENABLED,
				Messages.RegiomedPreferencePage_ErrorMailCheckbox, parent);
		addField(errorMailEnabledEditor);

		errorMailAddressEditor = new StringFieldEditor(RegiomedConstants.PREF_ERROR_EMAIL_ADDRESS,
				Messages.RegiomedPreferencePage_ErrorMailAddressLabel, parent);
		addField(errorMailAddressEditor);

		hookErrorEmailBehaviour(parent);
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
		if (!(ctrl instanceof Button)) {
			return;
		}
		Button check = (Button) ctrl;

		check.setEnabled(valid);

		if (!valid) {
			check.setSelection(false);
		}
	}

	@Override
	public boolean performOk() {
		if (!validateErrorEmail()) {
			return false;
		}
		return super.performOk();
	}

	private boolean validateErrorEmail() {
		boolean enabled = errorMailEnabledEditor.getBooleanValue();
		String addr = StringUtils.trimToEmpty(errorMailAddressEditor.getStringValue());

		if (!enabled) {
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