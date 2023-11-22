package ch.elexis.base.messages;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.data.Anwender;

public class MessagePreferences extends PreferencePage implements IWorkbenchPreferencePage {
	public static final String DEF_SOUND_PATH = "/sounds/notify_sound.wav"; //$NON-NLS-1$

	private Text txtSoundFilePath;
	private Button btnBrowse, btnSoundOn, btnAnswerAutoclear;
	private ComboViewer comboDefaultRecipient;
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
				fd.setFilterPath("C:/"); //$NON-NLS-1$
				String[] filterExt = { "*.wav" }; //$NON-NLS-1$
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

		Group grpDefaultRecipient = new Group(ret, SWT.NONE);
		grpDefaultRecipient.setLayout(new GridLayout(2, false));
		grpDefaultRecipient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpDefaultRecipient.setText(Messages.Prefs_DefaultMessageRecipient);

		Label lblActiveUser = new Label(grpDefaultRecipient, SWT.NONE);
		lblActiveUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblActiveUser.setText(Messages.Benutzer + ": " + CoreHub.getLoggedInContact().getLabel() + " ");
		comboDefaultRecipient = new ComboViewer(grpDefaultRecipient, SWT.READ_ONLY);
		comboDefaultRecipient.setContentProvider(ArrayContentProvider.getInstance());
		comboDefaultRecipient.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Anwender) {
					Anwender anwender = (Anwender) element;
					return anwender.getLabel();
				}
				return super.getText(element);
			}
		});
		comboDefaultRecipient.setInput(getUsers());
		String preferenceKey = getPreferenceKeyForUser();
		String savedRecipientId = ConfigServiceHolder.getUser(preferenceKey, null);
		if (savedRecipientId != null && !savedRecipientId.isEmpty()) {
			List<Anwender> users = getUsers();
			for (Anwender user : users) {
				if (user.getId().equals(savedRecipientId)) {
					comboDefaultRecipient.setSelection(new StructuredSelection(user));
					break;
				}
			}
		}
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
		StructuredSelection selection = (StructuredSelection) comboDefaultRecipient.getSelection();
		Anwender selectedUser = (Anwender) selection.getFirstElement();
		String selectedUserId = selectedUser.getId();
		String preferenceKey = getPreferenceKeyForUser();
		ConfigServiceHolder.setUser(preferenceKey, selectedUserId);
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_ON, btnSoundOn.getSelection());
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_PATH, txtSoundFilePath.getText());
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, btnAnswerAutoclear.getSelection());
		return super.performOk();
	}

	private List<Anwender> getUsers() {
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		List<IUser> users = userQuery.execute();
		return users.stream().filter(u -> isActive(u)).map(u -> Anwender.load(u.getAssignedContact().getId()))
				.collect(Collectors.toList());
	}

	private boolean isActive(IUser user) {
		if (user == null || user.getAssignedContact() == null) {
			return false;
		}
		if (!user.isActive()) {
			return false;
		}
		if (user.getAssignedContact() != null && user.getAssignedContact().isMandator()) {
			IMandator mandator = CoreModelServiceHolder.get().load(user.getAssignedContact().getId(), IMandator.class)
					.orElse(null);
			if (mandator != null && !mandator.isActive()) {
				return false;
			}
		}
		return true;
	}

	private String getPreferenceKeyForUser() {
		String userId = CoreHub.getLoggedInContact().getId();
		return Preferences.USR_DEFAULT_MESSAGE_RECIPIENT + "_" + userId;
	}

}
