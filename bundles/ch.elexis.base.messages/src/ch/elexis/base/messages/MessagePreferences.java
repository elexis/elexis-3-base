package ch.elexis.base.messages;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class MessagePreferences extends PreferencePage implements IWorkbenchPreferencePage {
	public static final String DEF_SOUND_PATH = "/sounds/notify_sound.wav";

	private Text txtSoundFilePath;
	private Button btnBrowse, btnSoundOn, btnAnswerAutoclear;

	private boolean soundOn, answerAutoclear;
	String soundFilePath;

	public MessagePreferences() {
		super(Messages.Prefs_Messages);
		soundOn = ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_SOUND_ON, true);
		soundFilePath = ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_SOUND_PATH, DEF_SOUND_PATH);
		answerAutoclear = ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, false);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(1, false));

		Group grpSound = new Group(ret, SWT.NONE);
		grpSound.setLayout(new GridLayout(2, false));
		GridData gd_grpSound = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		grpSound.setLayoutData(gd_grpSound);
		grpSound.setText(Messages.Prefs_SoundSettings);

		btnSoundOn = new Button(grpSound, SWT.CHECK);
		btnSoundOn.setText(Messages.Prefs_TurnOnSound);
		btnSoundOn.setSelection(soundOn);
		btnSoundOn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				txtSoundFilePath.setEnabled(btnSoundOn.getSelection());
				btnBrowse.setEnabled(btnSoundOn.getSelection());
			};
		});
		new Label(grpSound, SWT.NONE);

		txtSoundFilePath = new Text(grpSound, SWT.BORDER);
		txtSoundFilePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtSoundFilePath.setText(soundFilePath);
		txtSoundFilePath.setEnabled(soundOn);

		btnBrowse = new Button(grpSound, SWT.NONE);
		btnBrowse.setText(Messages.Prefs_BrowseFS);
		btnBrowse.setEnabled(soundOn);
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(btnBrowse.getShell(), SWT.OPEN);
				fd.setText(Messages.Prefs_FS_Open);
				fd.setFilterPath("C:/");
				String[] filterExt = { "*.wav" };
				fd.setFilterExtensions(filterExt);
				String filePath = fd.open();
				if (filePath != null) {
					txtSoundFilePath.setText(filePath);
				}
			}
		});

		Group grpDialogConfig = new Group(ret, SWT.NONE);
		grpDialogConfig.setLayout(new GridLayout(1, false));
		grpDialogConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		grpDialogConfig.setText(Messages.Prefs_DialogSettings);

		btnAnswerAutoclear = new Button(grpDialogConfig, SWT.CHECK);
		btnAnswerAutoclear.setText(Messages.Prefs_btnAnswerAutoclear);
		btnAnswerAutoclear.setSelection(answerAutoclear);

		return ret;
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void performDefaults() {
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_ON, true);
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_PATH, DEF_SOUND_PATH);
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, false);

		btnAnswerAutoclear.setSelection(false);
		btnSoundOn.setSelection(true);
		btnBrowse.setEnabled(true);
		txtSoundFilePath.setEnabled(true);
		txtSoundFilePath.setText(ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_SOUND_PATH, DEF_SOUND_PATH));

		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_ON, btnSoundOn.getSelection());
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_PATH, txtSoundFilePath.getText());
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, btnAnswerAutoclear.getSelection());
		return super.performOk();
	}
}
