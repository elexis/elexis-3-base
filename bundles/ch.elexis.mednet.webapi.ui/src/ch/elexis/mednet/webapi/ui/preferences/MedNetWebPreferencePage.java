package ch.elexis.mednet.webapi.ui.preferences;


import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
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

@Component
public class MedNetWebPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private IConfigService configService;

    @Reference
    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }

    public MedNetWebPreferencePage() {
        super(GRID);
		ScopedPreferenceStore preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.MEDNET_PLUGIN_STRING);
		setPreferenceStore(preferenceStore);

        setDescription(Messages.MedNetWebPreferencePage_configForMedNetWebAPI);
    }

    @Override
    public void createFieldEditors() {

        addField(new DirectoryFieldEditor(
                PreferenceConstants.MEDNET_DOWNLOAD_PATH,
                Messages.MedNetWebPreferencePage_downloadFolder,
                getFieldEditorParent()
        ));

        addField(new StringFieldEditor(
                PreferenceConstants.MEDNET_USER_STRING,
                Messages.MedNetWebPreferencePage_loginName,
                getFieldEditorParent()
        ));
    }

    @Override
	public void init(IWorkbench workbench) {
		if (configService == null) {

			BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			ServiceReference<IConfigService> serviceReference = context.getServiceReference(IConfigService.class);
			if (serviceReference != null) {
				configService = context.getService(serviceReference);
			} else {
				throw new IllegalStateException("IConfigService konnte nicht gefunden werden.");
			}
		}

		String downloadPath = configService.getActiveUserContact(PreferenceConstants.MEDNET_DOWNLOAD_PATH, "");
        getPreferenceStore().setValue(PreferenceConstants.MEDNET_DOWNLOAD_PATH, downloadPath);

		String userName = configService.getActiveUserContact(PreferenceConstants.MEDNET_USER_STRING, "");
        getPreferenceStore().setValue(PreferenceConstants.MEDNET_USER_STRING, userName);
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
		}
    }
}
