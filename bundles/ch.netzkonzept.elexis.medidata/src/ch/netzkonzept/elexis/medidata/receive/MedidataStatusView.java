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
package ch.netzkonzept.elexis.medidata.receive;

import java.text.*;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Properties;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.views.IRefreshable;
import ch.netzkonzept.elexis.medidata.config.MedidataPreferencePage;

public class MedidataStatusView extends ViewPart implements IRefreshable {

	private Properties applicationProperties;
	private Properties messagesProperties;

	final SettingsPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.globalCfg);

	private int insertMark = -1;

	private TabFolder tabFolder;

	public MedidataStatusView() {
		super();
	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			String separator = FileSystems.getDefault().getSeparator();
			getApplicationProperties().load(MedidataStatusView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "application.properties"));
			// .getResourceAsStream("/resources/application.properties"));
			getMessagesProperties().load(MedidataStatusView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "messages_de.properties"));
			// .getResourceAsStream("/resources/messages_de.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		loadProperties();

		tabFolder = new TabFolder(parent, SWT.NONE);

		TabItem cti3 = new TabItem(tabFolder, SWT.NONE);
		TabItem cti2 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti1 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);

		cti1.setText("Antwortdokumente");
		cti2.setText("Benachrichtigungen");
		cti3.setText("Versandstatus");

		TabData tabData = new TabData(tabFolder, Paths.get(preferenceStore.getString("mdn_base_dir")));

		try {
			cti1.setControl(tabData.buildResponseDocTable(parent));
			cti2.setControl(tabData.buildMessageTable(parent));
			cti3.setControl(tabData.buildTransmissionTable(parent));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	@Override
	public void setFocus() {
	}

	@Override
	public void refresh() {
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
