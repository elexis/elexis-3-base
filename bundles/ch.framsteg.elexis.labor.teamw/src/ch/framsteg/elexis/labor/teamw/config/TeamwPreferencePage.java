/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.labor.teamw.config;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.services.IConfigService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.framsteg.elexis.labor.teamw.composites.CompositeBuilder;
import ch.framsteg.elexis.labor.teamw.utilities.HashGenerator;
import ch.framsteg.elexis.labor.teamw.views.LabordersView;

public class TeamwPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	// Preference keys
	private static final String KEY_USERNAME = "key.teamw.username";
	private static final String KEY_PASSWORD = "key.teamw.password";
	private static final String KEY_IP = "key.teamw.ip";
	private static final String KEY_PATH = "key.teamw.path";
	private static final String KEY_HOUR_TO_UTC = "key.time.shift";

	// Groups
	private static final String GRP_PRACTICE_IDENTIFICATION = "props.msg.grp.practice.identification";
	private static final String GRP_APPLICATION_IDENTIFICATION = "props.msg.grp.application.identification";
	private static final String GRP_APPLICATION_ENDPOINT = "props.msg.grp.application.endpoint";
	private static final String GRP_TIME_SHIFT = "props.msg.grp.time.shift";

	// Labels
	private static final String LBL_APPLICATION_SERIAL_NUM = "props.msg.lbl.application.serial.number";
	private static final String LBL_APPLICATION_CLIENT_TYPE = "props.msg.lbl.application.client.type";
	private static final String LBL_APPLICATION_ENDPOINT = "props.msg.lbl.application.endpoint";
	private static final String LBL_EXPECTED_CHECKSUM = "props.msg.lbl.key.expected.checksum";
	private static final String LBL_CALCULATED_CHECKSUM = "props.msg.lbl.key.calculated.checksum";
	private static final String LBL_PATH_TO_KEY = "props.msg.lbl.key.path.to.key";
	private static final String LBL_USERNAME = "props.msg.lbl.user.username";
	private static final String LBL_PASSWORD = "props.msg.lbl.user.password";
	private static final String LBL_IP = "props.msq.lbl.ip";
	private static final String LBL_HOUR_TO_UTC = "props.msg.lbl.time.shift";

	// Text
	private static final String TXT_APPLICATION_SERIAL_NUM = "props.teamw.message.property.serial.num";
	private static final String TXT_APPLICATION_CLIENT_TYPE = "props.teamw.message.property.client.type";
	private static final String TXT_APPLICATION_ENDPOINT = "props.teamw.message.property.endpoint";
	private static final String TXT_EXPECTED_CHECKSUM = "props.teamw.teamw.key.checksum";

	// File dialog
	private static final String PEM_FILE = "props.msg.pem.file";
	private static final String ALL_FILES = "props.msg.all.files";
	private static final String PEM_FILE_EXTENSION = "props.msg.pem.file.ext";
	private static final String ALL_FILES_EXTENSION = "props.msg.all.files.ext";

	// Error messages
	private static final String ERR_TITLE = "props.msg.title.error";
	private static final String ERR_MSG = "props.msg.missing.properties";


	private Properties applicationProperties;
	private Properties messagesProperties;
	private Properties teamwProperties;

	private Text txtUsername;
	private Text txtPassword;
	private Text txtIP;

	private Text txtSerialNumber;
	private Text txtClientType;
	private Text txtChecksumExpected;
	private Text txtChecksumCalculated;
	private Text txtPath;
	private Text txtEndPoint;
	private Text txtTimeShift;

	private String path = System.getProperty("user.home");

	private boolean keyIsValid;

	@Inject
	private IConfigService configService;

	public TeamwPreferencePage() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public void init(IWorkbench workbench) {
		keyIsValid = false;
		loadProperties();

	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			setTeamwProperties(new Properties());

			getApplicationProperties().load(
					LabordersView.class.getClassLoader().getResourceAsStream("/resources/application.properties"));
			getMessagesProperties()
					.load(LabordersView.class.getClassLoader().getResourceAsStream("/resources/messages.properties"));
			getTeamwProperties()
					.load((LabordersView.class.getClassLoader().getResourceAsStream("/resources/teamw.properties")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Control createContents(Composite parent) {

		Composite composite = CompositeBuilder.createStandardComposite(parent);
		Group practiceIdentificationGroup = CompositeBuilder.createGroup(composite, 2,
				getMessagesProperties().getProperty(GRP_PRACTICE_IDENTIFICATION));

		CompositeBuilder.createActivatedLine(practiceIdentificationGroup,
				getMessagesProperties().getProperty(LBL_USERNAME));
		txtUsername = (((Text) practiceIdentificationGroup
				.getChildren()[practiceIdentificationGroup.getChildren().length - 1]));
		txtUsername.setText(configService.get(KEY_USERNAME, txtUsername.getText(), true));

		txtUsername.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				txtUsername.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		});

		CompositeBuilder.createPasswordLine(practiceIdentificationGroup,
				getMessagesProperties().getProperty(LBL_PASSWORD));
		txtPassword = (((Text) practiceIdentificationGroup
				.getChildren()[practiceIdentificationGroup.getChildren().length - 1]));
		txtPassword.setText(configService.get(KEY_PASSWORD, txtPassword.getText(), true));
		txtPassword.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				txtPassword.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
				if (txtChecksumCalculated.getText().equalsIgnoreCase(txtChecksumExpected.getText())) {
					txtChecksumCalculated
							.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
					txtChecksumExpected
							.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
					keyIsValid = true;
				} else {
					txtChecksumCalculated
							.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
					keyIsValid = false;
				}
			}
		});

		CompositeBuilder.createActivatedLine(practiceIdentificationGroup, getMessagesProperties().getProperty(LBL_IP));
		txtIP = (((Text) practiceIdentificationGroup.getChildren()[practiceIdentificationGroup.getChildren().length
				- 1]));
		txtIP.setText(configService.get(KEY_IP, txtIP.getText(), true));

		Group applicationIdentificationGroup = CompositeBuilder.createGroup(composite, 2,
				getMessagesProperties().getProperty(GRP_APPLICATION_IDENTIFICATION));

		CompositeBuilder.createDeactivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(LBL_APPLICATION_SERIAL_NUM));
		txtSerialNumber = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));
		txtSerialNumber.setText(getTeamwProperties().getProperty(TXT_APPLICATION_SERIAL_NUM));

		CompositeBuilder.createDeactivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(LBL_APPLICATION_CLIENT_TYPE));
		txtClientType = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));
		txtClientType.setText(getTeamwProperties().getProperty(TXT_APPLICATION_CLIENT_TYPE));

		CompositeBuilder.createDeactivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(LBL_EXPECTED_CHECKSUM));
		txtChecksumExpected = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));
		txtChecksumExpected.setText(getTeamwProperties().getProperty(TXT_EXPECTED_CHECKSUM));

		CompositeBuilder.createDeactivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(LBL_CALCULATED_CHECKSUM));
		txtChecksumCalculated = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));

		CompositeBuilder.createActivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(LBL_PATH_TO_KEY));
		txtPath = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));
		txtPath.setText(configService.get(KEY_PATH, txtPath.getText(), true));

		if (!txtPath.getText().isEmpty()) {
			try {
				txtChecksumCalculated.setText(HashGenerator.createFileChecksumSHA256(txtPath.getText()));
				if (txtChecksumCalculated.getText().equalsIgnoreCase(txtChecksumExpected.getText())) {
					keyIsValid = true;
				} else {
					keyIsValid = false;
				}
			} catch (NoSuchAlgorithmException | IOException e1) {
				e1.printStackTrace();
			}
		}

		Button keyButton = new Button(applicationIdentificationGroup, SWT.PUSH);
		keyButton.setText("Durchsuchen...");
		keyButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				txtChecksumCalculated.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
				txtPath.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
				getKey(composite.getShell());
				txtPath.setText(path);
				try {
					txtChecksumCalculated.setText(HashGenerator.createFileChecksumSHA256(path));
					if (txtChecksumCalculated.getText().equalsIgnoreCase(txtChecksumExpected.getText())) {
						txtChecksumCalculated
								.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
						txtChecksumExpected
								.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
						keyIsValid = true;
					} else {
						txtChecksumCalculated
								.setForeground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_RED));
						keyIsValid = false;
					}
				} catch (NoSuchAlgorithmException | IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		Group endPointGroup = CompositeBuilder.createGroup(composite, 2,
				getMessagesProperties().getProperty(GRP_APPLICATION_ENDPOINT));

		CompositeBuilder.createDeactivatedLine(endPointGroup,
				getMessagesProperties().getProperty(LBL_APPLICATION_ENDPOINT));
		txtEndPoint = (((Text) endPointGroup.getChildren()[endPointGroup.getChildren().length - 1]));
		txtEndPoint.setText(getTeamwProperties().getProperty(TXT_APPLICATION_ENDPOINT));

		Group timeShiftGroup = CompositeBuilder.createGroup(composite, 2,
				getMessagesProperties().getProperty(GRP_TIME_SHIFT));

		CompositeBuilder.createActivatedLine(timeShiftGroup, getMessagesProperties().getProperty(LBL_HOUR_TO_UTC));
		txtTimeShift = (((Text) timeShiftGroup.getChildren()[timeShiftGroup.getChildren().length - 1]));
		txtTimeShift.setText(configService.get(KEY_HOUR_TO_UTC, txtTimeShift.getText(), true));

		dispose();
		return parent;
	}

	@Override
	public boolean performOk() {

		boolean usernameIsNotEmpty = false;
		boolean passwordIsNotEmpty = false;
		boolean ipNotEmpty = false;
		boolean returnValue = false;

		if (txtUsername.getText().isEmpty()) {
			txtUsername.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			usernameIsNotEmpty = false;
		} else {
			txtUsername.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(KEY_USERNAME, txtUsername.getText(), false);
			usernameIsNotEmpty = true;
		}

		if (txtPassword.getText().isEmpty()) {
			txtPassword.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			passwordIsNotEmpty = false;
		} else {
			txtPassword.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(KEY_PASSWORD, txtPassword.getText(), false);
			passwordIsNotEmpty = true;
		}

		if (txtIP.getText().isEmpty()) {
			txtIP.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			ipNotEmpty = false;
		} else {
			txtIP.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(KEY_IP, txtIP.getText(), false);
			ipNotEmpty = true;
		}

		if (txtPath.getText().isEmpty()) {
			txtPath.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			keyIsValid = false;
		} else {
			txtPath.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(KEY_PATH, txtPath.getText(), false);
		}

		if (txtChecksumCalculated.getText().isEmpty()) {
			txtChecksumCalculated.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			keyIsValid = false;
		} else {
			txtChecksumCalculated.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}

		if (!txtTimeShift.getText().isEmpty()) {
			configService.set(KEY_HOUR_TO_UTC, txtTimeShift.getText(), false);
		}

		if (!usernameIsNotEmpty || !passwordIsNotEmpty || !ipNotEmpty || !keyIsValid) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					getMessagesProperties().getProperty(ERR_TITLE), getMessagesProperties().getProperty(ERR_MSG));
			returnValue = false;
		} else {
			returnValue = true;
		}

		return returnValue;
	}

	private void getKey(Shell shell) {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);

		dialog.setOverwrite(true);
		dialog.setFilterNames(new String[] { getMessagesProperties().getProperty(PEM_FILE),
				getMessagesProperties().getProperty(ALL_FILES) });
		dialog.setFilterExtensions(new String[] { getMessagesProperties().getProperty(PEM_FILE_EXTENSION),
				getMessagesProperties().getProperty(ALL_FILES_EXTENSION) });
		dialog.getFileName();

		dialog.setFilterPath(System.getProperty("user.home"));
		path = dialog.open();
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

	public Properties getTeamwProperties() {
		return teamwProperties;
	}

	public void setTeamwProperties(Properties teamwProperties) {
		this.teamwProperties = teamwProperties;
	}
}
