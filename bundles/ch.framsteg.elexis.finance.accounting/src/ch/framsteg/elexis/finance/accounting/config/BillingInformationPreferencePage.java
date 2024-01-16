/*******************************************************************************
 * Copyright (c) 2020-2022,  Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Debenath <olivier@debenath.ch> - initial implementation
 *    
 *******************************************************************************/
package ch.framsteg.elexis.finance.accounting.config;

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
import ch.framsteg.elexis.finance.accounting.views.TableView;

public class BillingInformationPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Properties applicationProperties;
	private Properties messagesProperties;

	private Text txtOutputDir;
	private Text txtOutputFile;

	private final SettingsPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.globalCfg);

	public BillingInformationPreferencePage() {
		loadProperties();
	}

	private void loadProperties() {
		try {
			String separator = FileSystems.getDefault().getSeparator();
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			getApplicationProperties().load(TableView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "application.properties"));
			getMessagesProperties().load(TableView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "messages.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(IWorkbench arg0) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;

		Group group = new Group(composite, SWT.BORDER);
		group.setText(getMessagesProperties().getProperty("msg.prefs.output.dir"));
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label lblPath = new Label(group, SWT.NONE);
		lblPath.setText(getMessagesProperties().getProperty("msg.prefs.output.path"));

		txtOutputDir = new Text(group, SWT.BORDER);
		txtOutputDir.setText(preferenceStore.getString(getApplicationProperties().getProperty("pref.output.dir")));
		txtOutputDir.setEnabled(true);
		txtOutputDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label lblFilename = new Label(group, SWT.NONE);
		lblFilename.setText(getMessagesProperties().getProperty("msg.prefs.output.file.name"));

		txtOutputFile = new Text(group, SWT.BORDER);
		txtOutputFile.setText(
				preferenceStore.getString(getApplicationProperties().getProperty("pref.output.file.name")));
		txtOutputFile.setEnabled(true);
		txtOutputFile.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Button btnCreateDirectory = new Button(group, SWT.PUSH);
		btnCreateDirectory.setText(getMessagesProperties().getProperty("msg.prefs.create")); // $NON-NLS-1$
		btnCreateDirectory.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		btnCreateDirectory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ApplicationDirectoryStructure applicationDirectoryStructure = new ApplicationDirectoryStructure();
				applicationDirectoryStructure.create(txtOutputDir.getText());

			}
		});
		return parent;
	}

	@Override
	public boolean performOk() {
		preferenceStore.putValue(getApplicationProperties().getProperty("pref.output.dir"), txtOutputDir.getText());
		preferenceStore.putValue(getApplicationProperties().getProperty("pref.output.file.name"),
				txtOutputFile.getText());
		return super.performOk();
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

}
