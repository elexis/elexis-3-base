package ch.elexis.base.messages;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.format.UserFormatUtil;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditor;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class MessagePreferences extends PreferencePage implements IWorkbenchPreferencePage {
	public static final String DEF_SOUND_PATH = "/sounds/notify_sound.wav"; //$NON-NLS-1$

	private URIFieldEditor soundPathEditor;
	private Button btnSoundOn, btnAnswerAutoclear;
	private ComboViewer comboDefaultRecipient;
	private Composite editorComposite;
	private boolean soundOn, answerAutoclear;

	public MessagePreferences() {
		super(Messages.Prefs_Messages);
		soundOn = ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_SOUND_ON, true);
		answerAutoclear = ConfigServiceHolder.getUser(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, false);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(1, false));

		Group grpSound = new Group(ret, SWT.NONE);
		grpSound.setLayout(new GridLayout(1, false));
		grpSound.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		grpSound.setText(Messages.Prefs_SoundSettings);

		btnSoundOn = new Button(grpSound, SWT.CHECK);
		btnSoundOn.setText(Messages.Prefs_TurnOnSound);
		btnSoundOn.setSelection(soundOn);
		btnSoundOn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (soundPathEditor != null && editorComposite != null) {
					soundPathEditor.setEnabled(btnSoundOn.getSelection(), editorComposite);
				}
			}
		});

		editorComposite = new Composite(grpSound, SWT.NONE);
		editorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		soundPathEditor = new URIFieldEditor(Preferences.USR_MESSAGES_SOUND_PATH, Messages.Prefs_SoundSettings,
				editorComposite);
		soundPathEditor.setPreferenceStore(new ConfigServicePreferenceStore(Scope.USER));
		soundPathEditor.setMigrateLegacyPaths(true);
		soundPathEditor.setEmptyStringAllowed(true);
		soundPathEditor.setUseFileMode(true);

		soundPathEditor.load();
		soundPathEditor.setEnabled(soundOn, editorComposite);

		Group grpDialogConfig = new Group(ret, SWT.NONE);
		grpDialogConfig.setLayout(new GridLayout(1, false));
		grpDialogConfig.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		grpDialogConfig.setText(Messages.Prefs_DialogSettings);

		btnAnswerAutoclear = new Button(grpDialogConfig, SWT.CHECK);
		btnAnswerAutoclear.setText(Messages.Prefs_btnAnswerAutoclear);
		btnAnswerAutoclear.setSelection(answerAutoclear);

		Group grpDefaultRecipient = new Group(ret, SWT.NONE);
		grpDefaultRecipient.setLayout(new GridLayout(2, false));
		grpDefaultRecipient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		grpDefaultRecipient.setText(Messages.Prefs_DefaultMessageRecipient);

		Label lblActiveUser = new Label(grpDefaultRecipient, SWT.NONE);
		lblActiveUser.setText(Messages.Benutzer + ": " //$NON-NLS-1$
				+ UserFormatUtil.getUserLabel(ContextServiceHolder.get().getActiveUserContact().get())
				+ StringUtils.SPACE);
		comboDefaultRecipient = new ComboViewer(grpDefaultRecipient, SWT.READ_ONLY);
		comboDefaultRecipient.setContentProvider(ArrayContentProvider.getInstance());
		comboDefaultRecipient.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof IContact) {
					return UserFormatUtil.getUserLabel((IContact) element);
				}
				return super.getText(element);
			}
		});
		comboDefaultRecipient.setInput(getUsers());
		loadSavedRecipient();
		return ret;
	}

	private void loadSavedRecipient() {
		String preferenceKey = getPreferenceKeyForUser();
		String savedRecipientId = ConfigServiceHolder.getUser(preferenceKey, null);
		if (savedRecipientId != null && !savedRecipientId.isEmpty()) {
			List<IContact> users = getUsers();
			users.stream().filter(u -> u.getId().equals(savedRecipientId)).findFirst()
					.ifPresent(u -> comboDefaultRecipient.setSelection(new StructuredSelection(u)));
		}
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void performDefaults() {
		btnSoundOn.setSelection(true);
		btnAnswerAutoclear.setSelection(false);
		soundPathEditor.loadDefault();
		soundPathEditor.setEnabled(true, editorComposite);
		super.performDefaults();
	}

	@Override
	public boolean performOk() {
		StructuredSelection selection = (StructuredSelection) comboDefaultRecipient.getSelection();
		if (!selection.isEmpty()) {
			IContact selectedUser = (IContact) selection.getFirstElement();
			ConfigServiceHolder.setUser(getPreferenceKeyForUser(), selectedUser.getId());
		}
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_SOUND_ON, btnSoundOn.getSelection());
		soundPathEditor.store();
		ConfigServiceHolder.setUser(Preferences.USR_MESSAGES_ANSWER_AUTOCLEAR, btnAnswerAutoclear.getSelection());
		return super.performOk();
	}

	private List<IContact> getUsers() {
		IQuery<IUser> userQuery = CoreModelServiceHolder.get().getQuery(IUser.class);
		userQuery.and(ModelPackage.Literals.IUSER__ASSIGNED_CONTACT, COMPARATOR.NOT_EQUALS, null);
		return userQuery.execute().stream().filter(this::isActive).map(IUser::getAssignedContact)
				.collect(Collectors.toList());
	}

	private boolean isActive(IUser user) {
		if (user == null || user.getAssignedContact() == null || !user.isActive())
			return false;
		IContact contact = user.getAssignedContact();
		if (contact.isMandator()) {
			return CoreModelServiceHolder.get().load(contact.getId(), IMandator.class).map(IMandator::isActive)
					.orElse(false);
		}
		return true;
	}

	private String getPreferenceKeyForUser() {
		String userId = ContextServiceHolder.get().getActiveUserContact().get().getId();
		return Preferences.USR_DEFAULT_MESSAGE_RECIPIENT + "_" + userId;
	}
}