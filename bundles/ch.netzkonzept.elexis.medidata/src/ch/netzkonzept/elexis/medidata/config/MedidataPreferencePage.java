/*******************************************************************************
 * Copyright (c) 2020-2022,  Fabian Schmid and Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Fabian <f.schmid@netzkonzept.ch> - initial implementation
 *    Olivier Debenath <olivier@debenath.ch>
 *
 *******************************************************************************/
package ch.netzkonzept.elexis.medidata.config;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Properties;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.SWTHelper;
import ch.netzkonzept.elexis.medidata.output.ApplicationDirectoryStructure;

public class MedidataPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String PLUGIN_TITLE = "Medidata Plugin (Netzkonzept)";
	private static final String PLUGIN_DESCRIPTION = "Medidata Plugin (EPL)";

	private static final String MEDIDATA_EAN_CONFIG_LABEL = "label.medidata.ean.configuration";
	private static final String MEDIDATA_BASE_DIR_CONFIG_LABEL = "label.medidata.base.dir.configuration";
	private static final String EAN_TC_LABEL = "label.medidata.ean.trustcenter";
	private static final String EAN_IM_LABEL = "label.medidata.ean.intermediate";
	private static final String EAN_IG_LABEL = "label.medidata.ean.tiers.garant";

	private static final String BASE_DIR_LABEL = "label.medidata.base.dir";
	private static final String SEND_DIR_LABEL = "label.medidata.send.dir";
	private static final String SEND_PROCESSING_DIR_LABEL = "label.medidata.send.processing.dir";
	private static final String SEND_ERROR_DIR_LABEL = "label.medidata.send.error.dir";
	private static final String SEND_DONE_DIR_LABEL = "label.medidata.send.done.dir";
	private static final String RECEIVE_DIR_LABEL = "label.medidata.receive.dir";
	private static final String DIR_CREATE_LABEL = "label.medidata.dir.create";
	private static final String EAN_TC_KEY = "key.medidata.ean.trustcenter";
	private static final String EAN_IM_KEY = "key.medidata.ean.intermediate";
	private static final String EAN_TG_KEY = "key.medidata.ean.tiers.garant";

	private static final String BASE_DIR_KEY = "key.medidata.base.dir";
	private static final String SEND_DIR_KEY = "key.medidata.send.dir";
	private static final String SEND_PROCESSING_DIR_KEY = "key.medidata.send.processing.dir";
	private static final String SEND_ERROR_DIR_KEY = "key.medidata.send.error.dir";
	private static final String SEND_DONE_DIR_KEY = "key.medidata.send.done.dir";
	private static final String RECEIVE_DIR_KEY = "key.medidata.receive.dir";
	private String createJSONFile;

	private Properties applicationProperties;
	private Properties messagesProperties;

	private Text txtBaseDir;
	private Text txtSendDir;
	private Text txtSendProcessingDir;
	private Text txtSendErrorDir;
	private Text txtSendDoneDir;
	private Text txtReceiveDir;

	private Text txtIntermediateEAN;
	private Text txtTrustEAN;
	private Text txtTGEAN;

	private final SettingsPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.globalCfg);

	public MedidataPreferencePage() {
		super(PLUGIN_TITLE);
		setTitle(PLUGIN_DESCRIPTION);
		loadProperties();
	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			String separator = FileSystems.getDefault().getSeparator();
			getApplicationProperties().load(MedidataPreferencePage.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "application.properties"));
			getMessagesProperties().load(MedidataPreferencePage.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "messages_de.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite eanComposite = new Composite(parent, SWT.NONE);
		eanComposite.setLayout(new GridLayout(2, false));
		eanComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Group eanConfigGroup = new Group(eanComposite, SWT.BORDER);
		eanConfigGroup.setText(getMessagesProperties().getProperty(MEDIDATA_EAN_CONFIG_LABEL));
		eanConfigGroup.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		eanConfigGroup.setLayout(new GridLayout(2, false));

		Composite directoryComposite = new Composite(parent, SWT.NONE);
		directoryComposite.setLayout(new GridLayout(2, false));
		directoryComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Group baseDirectoryConfigGroup = new Group(directoryComposite, SWT.BORDER);
		baseDirectoryConfigGroup.setText(getMessagesProperties().getProperty(MEDIDATA_BASE_DIR_CONFIG_LABEL));
		baseDirectoryConfigGroup.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		baseDirectoryConfigGroup.setLayout(new GridLayout(2, false));

		Label labelBaseDir = new Label(baseDirectoryConfigGroup, SWT.NONE);
		labelBaseDir.setVisible(true);
		labelBaseDir.setText(getMessagesProperties().getProperty(BASE_DIR_LABEL));
		txtBaseDir = new Text(baseDirectoryConfigGroup, SWT.BORDER);
		txtBaseDir.setText(preferenceStore.getString(getApplicationProperties().getProperty(BASE_DIR_KEY)));
		txtBaseDir.setEnabled(true);
		txtBaseDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelSendDir = new Label(baseDirectoryConfigGroup, SWT.NONE);
		labelSendDir.setVisible(true);
		labelSendDir.setText(getMessagesProperties().getProperty(SEND_DIR_LABEL));
		txtSendDir = new Text(baseDirectoryConfigGroup, SWT.BORDER);
		txtSendDir.setText(preferenceStore.getString(getApplicationProperties().getProperty(SEND_DIR_KEY)));
		txtSendDir.setEnabled(false);
		txtSendDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelSendProcessingDir = new Label(baseDirectoryConfigGroup, SWT.NONE);
		labelSendProcessingDir.setVisible(true);
		labelSendProcessingDir.setText(getMessagesProperties().getProperty(SEND_PROCESSING_DIR_LABEL));
		txtSendProcessingDir = new Text(baseDirectoryConfigGroup, SWT.BORDER);

		txtSendProcessingDir
				.setText(preferenceStore.getString(getApplicationProperties().getProperty(SEND_PROCESSING_DIR_KEY)));
		txtSendProcessingDir.setEnabled(false);
		txtSendProcessingDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelSendErrorDir = new Label(baseDirectoryConfigGroup, SWT.NONE);
		labelSendErrorDir.setVisible(true);
		labelSendErrorDir.setText(getMessagesProperties().getProperty(SEND_ERROR_DIR_LABEL));
		txtSendErrorDir = new Text(baseDirectoryConfigGroup, SWT.BORDER);
		txtSendErrorDir.setText(preferenceStore.getString(getApplicationProperties().getProperty(SEND_ERROR_DIR_KEY)));
		txtSendErrorDir.setEnabled(false);
		txtSendErrorDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelSendDoneDir = new Label(baseDirectoryConfigGroup, SWT.NONE);
		labelSendDoneDir.setVisible(true);
		labelSendDoneDir.setText(getMessagesProperties().getProperty(SEND_DONE_DIR_LABEL));
		txtSendDoneDir = new Text(baseDirectoryConfigGroup, SWT.BORDER);
		txtSendDoneDir.setText(preferenceStore.getString(getApplicationProperties().getProperty(SEND_DONE_DIR_KEY)));
		txtSendDoneDir.setEnabled(false);
		txtSendDoneDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelReceiveDir = new Label(baseDirectoryConfigGroup, SWT.NONE);
		labelReceiveDir.setVisible(true);
		labelReceiveDir.setText(getMessagesProperties().getProperty(RECEIVE_DIR_LABEL));
		txtReceiveDir = new Text(baseDirectoryConfigGroup, SWT.BORDER);
		txtReceiveDir.setText(preferenceStore.getString(getApplicationProperties().getProperty(RECEIVE_DIR_KEY)));
		txtReceiveDir.setEnabled(false);
		txtReceiveDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Button btnCreateDirectories = new Button(baseDirectoryConfigGroup, SWT.PUSH);
		btnCreateDirectories.setText(getMessagesProperties().getProperty(DIR_CREATE_LABEL)); // $NON-NLS-1$
		btnCreateDirectories.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		btnCreateDirectories.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ApplicationDirectoryStructure applicationDirectoryStructure = new ApplicationDirectoryStructure(
						txtBaseDir.getText(), getApplicationProperties());
				applicationDirectoryStructure.create();
				txtSendDir.setText(applicationDirectoryStructure.getSendDir().toString());
				txtSendProcessingDir.setText(applicationDirectoryStructure.getSendProcessingDir().toString());
				txtSendErrorDir.setText(applicationDirectoryStructure.getSendErrorDir().toString());
				txtSendDoneDir.setText(applicationDirectoryStructure.getSendDoneDir().toString());
				txtReceiveDir.setText(applicationDirectoryStructure.getReceiveDir().toString());
			}
		});

		Label labelTrustEAN = new Label(eanConfigGroup, SWT.NONE);
		labelTrustEAN.setText(getMessagesProperties().getProperty(EAN_TC_LABEL));
		txtTrustEAN = new Text(eanConfigGroup, SWT.BORDER);
		txtTrustEAN.setText(preferenceStore.getString(getApplicationProperties().getProperty(EAN_TC_KEY)));
		txtTrustEAN.setEnabled(true);
		txtTrustEAN.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelIntermediateEAN = new Label(eanConfigGroup, SWT.NONE);
		labelIntermediateEAN.setText(getMessagesProperties().getProperty(EAN_IM_LABEL));
		txtIntermediateEAN = new Text(eanConfigGroup, SWT.BORDER);
		txtIntermediateEAN.setText(preferenceStore.getString(getApplicationProperties().getProperty(EAN_IM_KEY)));
		txtIntermediateEAN.setEnabled(true);
		txtIntermediateEAN.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label labelTiersGarantEAN = new Label(eanConfigGroup, SWT.NONE);
		labelTiersGarantEAN.setText(getMessagesProperties().getProperty(EAN_IG_LABEL));
		txtTGEAN = new Text(eanConfigGroup, SWT.BORDER);
		txtTGEAN.setText(preferenceStore.getString(getApplicationProperties().getProperty(EAN_TG_KEY)));
		txtTGEAN.setEnabled(true);
		txtTGEAN.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		return parent;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		preferenceStore.putValue(getApplicationProperties().getProperty(BASE_DIR_KEY), txtBaseDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(EAN_TC_KEY), txtTrustEAN.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(EAN_IM_KEY), txtIntermediateEAN.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(EAN_TG_KEY), txtTGEAN.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(SEND_DIR_KEY), txtSendDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(SEND_PROCESSING_DIR_KEY),
				txtSendProcessingDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(SEND_ERROR_DIR_KEY), txtSendErrorDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(SEND_DONE_DIR_KEY), txtSendDoneDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(RECEIVE_DIR_KEY), txtReceiveDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty(BASE_DIR_KEY), txtBaseDir.getText());

		preferenceStore.flush();

		return super.performOk();
	}

	private Properties getApplicationProperties() {
		return applicationProperties;
	}

	private void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	private Properties getMessagesProperties() {
		return messagesProperties;
	}

	private void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

	public String getCreateJSONFile() {
		return createJSONFile;
	}

	public void setCreateJSONFile(String createJSONFile) {
		this.createJSONFile = createJSONFile;
	}
}
