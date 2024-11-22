package ch.elexis.mednet.webapi.ui.preferences;


import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.widgets.Composite;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;


import ch.elexis.core.services.IConfigService;
import ch.elexis.mednet.webapi.core.IMednetAuthService;
import ch.elexis.mednet.webapi.core.constants.PreferenceConstants;
import ch.elexis.mednet.webapi.core.messages.Messages;

@Component
public class MedNetWebPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Reference
	private IConfigService configService;


	public MedNetWebPreferencePage() {
		super(GRID);
		ScopedPreferenceStore scopedPreferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE,
				PreferenceConstants.MEDNET_PLUGIN_STRING);
		setPreferenceStore(scopedPreferenceStore);
		setDescription(Messages.MedNetWebPreferencePage_configForMedNetWebAPI);
	}

	@Override
    public void createFieldEditors() {
		addField(new DirectoryFieldEditor(PreferenceConstants.MEDNET_DOWNLOAD_PATH, Messages.MedNetWebPreferencePage_downloadFolder,
				getFieldEditorParent()));

		addField(new StringFieldEditor(PreferenceConstants.MEDNET_USER_STRING, Messages.MedNetWebPreferencePage_loginName, getFieldEditorParent()));

        Composite parent = getFieldEditorParent();
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		GridLayout layout = new GridLayout(1, false);
		buttonComposite.setLayout(layout);

		Button deleteTokenButton = new Button(buttonComposite, SWT.PUSH);
		deleteTokenButton.setText(Messages.MedNetWebPreferencePage_requestNewToken);
		deleteTokenButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		deleteTokenButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				deleteAndFetchNewToken();
			}
		});

	}

	private void deleteAndFetchNewToken() {
		
		String tokenGroup = "mednet"; //$NON-NLS-1$
		BundleContext context = FrameworkUtil.getBundle(getClass()).getBundleContext();
		ServiceReference<IMednetAuthService> serviceReference = context.getServiceReference(IMednetAuthService.class);

		if (serviceReference != null) {
			IMednetAuthService authService = context.getService(serviceReference);
			try {
			
				if (configService == null) {
					MessageDialog.openError(getFieldEditorParent().getShell(), "Fehler", //$NON-NLS-1$
							"Konfigurationsdienst ist nicht verfügbar."); //$NON-NLS-1$
					LoggerFactory.getLogger(getClass()).error("Konfigurationsdienst ist nicht verfügbar."); //$NON-NLS-1$
					return;
				}

				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN + tokenGroup, null);
				configService.setActiveMandator(PreferenceConstants.PREF_TOKEN_EXPIRES + tokenGroup, null);
				configService.setActiveMandator(PreferenceConstants.PREF_REFRESHTOKEN + tokenGroup, null);

				Optional<String> newToken = authService.getToken(Map.of(PreferenceConstants.TOKEN_GROUP, tokenGroup));
				if (newToken.isPresent()) {
					MessageDialog.openInformation(getFieldEditorParent().getShell(), "Neuer Token", //$NON-NLS-1$
							"Neuer Token erfolgreich abgerufen: " + newToken.get()); //$NON-NLS-1$
				} else {
					MessageDialog.openError(getFieldEditorParent().getShell(), "Fehler", //$NON-NLS-1$
							"Neuer Token konnte nicht abgerufen werden."); //$NON-NLS-1$
				}

			} catch (Exception ex) {
				MessageDialog.openError(getFieldEditorParent().getShell(), "Fehler", //$NON-NLS-1$
						"Ein Fehler ist aufgetreten: " + ex.getMessage()); //$NON-NLS-1$
				LoggerFactory.getLogger(getClass()).error("Fehler beim Abrufen eines neuen Tokens", ex); //$NON-NLS-1$
			} finally {
				context.ungetService(serviceReference);
			}
		} else {
			MessageDialog.openError(getFieldEditorParent().getShell(), "Fehler", "MednetAuthService nicht verfügbar."); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}


	@Override
	public void init(IWorkbench workbench) {
		// Initialisierung, falls benötigt
	}
}
