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

	private static final String USERNAME_KEY = "key.teamw.username";
	private static final String PASSWORD_KEY = "key.teamw.password";
	private static final String PATH_KEY = "key.teamw.path";
	private static final String TXT_HOUR_TO_UTC_KEY = "key.time.shift";

	private static final String TXT_PRACTICE_IDENTIFICATION = "props.msg.grp.practice.identification";
	private static final String TXT_APPLICATION_IDENTIFICATION = "props.msg.grp.application.identification";
	private static final String TXT_EXPECTED_CHECKSUM = "props.msg.lbl.key.expected.checksum";
	private static final String TXT_CALCULATED_CHECKSUM = "props.msg.lbl.key.calculated.checksum";
	private static final String TXT_PATH_TO_KEY = "props.msg.lbl.key.path.to.key";

	private static final String TXT_USERNAME = "props.msg.lbl.user.username";
	private static final String TXT_PASSWORD = "props.msg.lbl.user.password";

	private static final String TXT_TIME_SHIFT = "props.msg.grp.time.shift";
	private static final String TXT_HOUR_TO_UTC = "props.msg.lbl.time.shift";

	private static final String ERR_TITLE = "props.msg.title.error";
	private static final String ERR_MSG = "props.msg.missing.properties";

	private static final String PEM_FILE = "props.msg.pem.file";
	private static final String ALL_FILES = "props.msg.all.files";

	private static final String PEM_FILE_EXT = "props.msg.pem.file.ext";
	private static final String ALL_FILES_EXT = "props.msg.all.files.ext";

	private static final String EXPECTED_CHECKSUM = "props.teamw.teamw.key.checksum";

	private Properties applicationProperties;
	private Properties messagesProperties;
	private Properties teamwProperties;

	private Text txtUsername;
	private Text txtPassword;

	private Text txtChecksumExpected;
	private Text txtChecksumCalculated;
	private Text txtPath;
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
				getMessagesProperties().getProperty(TXT_PRACTICE_IDENTIFICATION));

		CompositeBuilder.createActivatedLine(practiceIdentificationGroup,
				getMessagesProperties().getProperty(TXT_USERNAME));
		txtUsername = (((Text) practiceIdentificationGroup
				.getChildren()[practiceIdentificationGroup.getChildren().length - 1]));
		txtUsername.setText(configService.get(USERNAME_KEY, txtUsername.getText(), true));

		txtUsername.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				txtUsername.setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		});

		CompositeBuilder.createPasswordLine(practiceIdentificationGroup, getMessagesProperties().getProperty(TXT_PASSWORD));
		txtPassword = (((Text) practiceIdentificationGroup
				.getChildren()[practiceIdentificationGroup.getChildren().length - 1]));
		txtPassword.setText(configService.get(PASSWORD_KEY, txtUsername.getText(), true));
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

		Group applicationIdentificationGroup = CompositeBuilder.createGroup(composite, 2,
				getMessagesProperties().getProperty(TXT_APPLICATION_IDENTIFICATION));

		CompositeBuilder.createDeactivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(TXT_EXPECTED_CHECKSUM));
		txtChecksumExpected = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));
		txtChecksumExpected.setText(getTeamwProperties().getProperty(EXPECTED_CHECKSUM));

		CompositeBuilder.createDeactivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(TXT_CALCULATED_CHECKSUM));
		txtChecksumCalculated = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));

		CompositeBuilder.createActivatedLine(applicationIdentificationGroup,
				getMessagesProperties().getProperty(TXT_PATH_TO_KEY));
		txtPath = (((Text) applicationIdentificationGroup
				.getChildren()[applicationIdentificationGroup.getChildren().length - 1]));
		txtPath.setText(configService.get(PATH_KEY, txtPath.getText(), true));

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

		Group timeShiftGroup = CompositeBuilder.createGroup(composite, 2,
				getMessagesProperties().getProperty(TXT_TIME_SHIFT));

		CompositeBuilder.createActivatedLine(timeShiftGroup, getMessagesProperties().getProperty(TXT_HOUR_TO_UTC));
		txtTimeShift = (((Text) timeShiftGroup.getChildren()[timeShiftGroup.getChildren().length - 1]));
		txtTimeShift.setText(configService.get(TXT_HOUR_TO_UTC_KEY, txtTimeShift.getText(), true));

		dispose();
		return parent;
	}

	@Override
	public boolean performOk() {

		boolean usernameIsNotEmpty = false;
		boolean passwordIsNotEmpty = false;
		boolean returnValue = false;

		if (txtUsername.getText().isEmpty()) {
			txtUsername.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			usernameIsNotEmpty = false;
		} else {
			txtUsername.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(USERNAME_KEY, txtUsername.getText(), false);
			usernameIsNotEmpty = true;
		}

		if (txtPassword.getText().isEmpty()) {
			txtPassword.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			passwordIsNotEmpty = false;
		} else {
			txtPassword.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(PASSWORD_KEY, txtPassword.getText(), false);
			passwordIsNotEmpty = true;
		}

		if (txtPath.getText().isEmpty()) {
			txtPath.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			keyIsValid = false;
		} else {
			txtPath.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
			configService.set(PATH_KEY, txtPath.getText(), false);
		}

		if (txtChecksumCalculated.getText().isEmpty()) {
			txtChecksumCalculated.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_RED));
			keyIsValid = false;
		} else {
			txtChecksumCalculated.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		}

		if (!txtTimeShift.getText().isEmpty()) {
			configService.set(TXT_HOUR_TO_UTC_KEY, txtTimeShift.getText(), false);
		}

		if (!usernameIsNotEmpty || !passwordIsNotEmpty || !keyIsValid) {
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
		dialog.setFilterExtensions(new String[] { getMessagesProperties().getProperty(PEM_FILE_EXT),
				getMessagesProperties().getProperty(ALL_FILES_EXT) });
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
