package ch.elexis.mednet.webapi.ui.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;
import javax.inject.Inject;

public class MedNetWebPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Inject
    private IConfigService configService;

	private Button demoRadioButton;
	private Button produktivRadioButton;
	public static final String DEMO = "DEMO"; //$NON-NLS-1$
	public static final String PRODUKTIV = "PRODUKTIV"; //$NON-NLS-1$

    public MedNetWebPreferencePage() {
        super(GRID);
		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.MEDNET_PLUGIN_STRING);
		setPreferenceStore(preferenceStore);

        setDescription(Messages.MedNetWebPreferencePage_configForMedNetWebAPI);
    }

    @Override
    public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
				Messages.MedNetWebPreferencePage_downloadFolder, getFieldEditorParent()));

		addField(new StringFieldEditor(PreferenceConstants.MEDNET_USER_STRING,
				Messages.MedNetWebPreferencePage_loginName, getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND,
				Messages.MedNetWebPreferencePage_confirmBeforeSend, getFieldEditorParent()));

		createRadioButtonGroup(getFieldEditorParent());
	}

	private void createRadioButtonGroup(Composite parent) {

		Group group = new Group(parent, SWT.NONE);
		group.setText(Messages.MedNetWebPreferencePage_operatingMode);
		group.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));

		demoRadioButton = new Button(group, SWT.RADIO);
		demoRadioButton.setText(Messages.MedNetWebPreferencePage_demoMode);
		demoRadioButton.setSelection(DEMO.equals(getPreferenceStore().getString(PreferenceConstants.MEDNET_MODE)));

		produktivRadioButton = new Button(group, SWT.RADIO);
		produktivRadioButton.setText(Messages.MedNetWebPreferencePage_produktivMode);
		produktivRadioButton
				.setSelection(PRODUKTIV.equals(getPreferenceStore().getString(PreferenceConstants.MEDNET_MODE)));
	}

	@Override
	public void init(IWorkbench workbench) {
		if (configService != null) {
			String downloadPath = configService.getActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
					StringUtils.EMPTY);
			getPreferenceStore().setValue(PreferenceConstants.MEDNET_DOWNLOAD_PATH, downloadPath);
			String userName = configService.getActiveUserContact(PreferenceConstants.MEDNET_USER_STRING,
					StringUtils.EMPTY);
			getPreferenceStore().setValue(PreferenceConstants.MEDNET_USER_STRING, userName);
			String mode = configService.getActiveUserContact(PreferenceConstants.MEDNET_MODE, DEMO);
			getPreferenceStore().setValue(PreferenceConstants.MEDNET_MODE, mode);

			String confirm = configService.getActiveUserContact(PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND, "true"); //$NON-NLS-1$
			getPreferenceStore().setValue(PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND,
					Boolean.parseBoolean(confirm));
		}
	}


	@Override
	public boolean performOk() {
		boolean result = super.performOk();
		applyChanges();
		return result;
    }

	@Override
	protected void performApply() {
		super.performApply();
		applyChanges();
	}

	private void applyChanges() {
		if (configService != null) {
			configService.setActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH,
					getPreferenceStore().getString(PreferenceConstants.MEDNET_DOWNLOAD_PATH));

			configService.setActiveUserContact(PreferenceConstants.MEDNET_USER_STRING,
					getPreferenceStore().getString(PreferenceConstants.MEDNET_USER_STRING));

			configService.setActiveUserContact(PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND,
					Boolean.toString(getPreferenceStore().getBoolean(PreferenceConstants.MEDNET_CONFIRM_BEFORE_SEND)));

		}
	}
}
