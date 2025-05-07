/*******************************************************************************
 * Copyright (c) 2010, Oliver Egger, visionary ag
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package ch.docbox.elexis;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import ch.docbox.model.CdaMessage;
import ch.docbox.model.DocboxContact;
import ch.docbox.ws.cdachservices.CDACHServices;
import ch.docbox.ws.cdachservices.CDACHServices_Service;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.docbox.ws.client.WsClientConfig;
import ch.elexis.docbox.ws.client.WsClientUtil;
import ch.swissmedicalsuite.HCardBrowser;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;

/**
 * User specific settings: Case defaults
 */
public class UserDocboxPreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private String oldSha1Password;
	private String oldLoginId;

	private StringFieldEditor loginIdFieldEditor;
	private StringFieldEditor passwordFieldEditor;
	private DirectoryFieldEditor directoryFieldEditor;
	private Button buttonAgendaSettingsPerUser;

	private Combo agendaBereichCombo;
	private Button buttonGetAppointmentsEmergencyService;
	private Button buttonGetAppointmentsPharmaVisits;
	private Button buttonGetAppointmentsTerminvereinbarung;
	private Button buttonClearDocboxInbox;

	private String bereiche[];

	protected static Log log = Log.get("UserDocboxPreferences"); //$NON-NLS-1$

	public static boolean isDocboxTest() {
		return getDocboxLoginID(true) != null && getDocboxLoginID(true).startsWith(WsClientConfig.TESTLOGINIDPREFIX);
	}

	public static String getSSOSignature(String ts) {
		String username = getDocboxLoginID(false);
		String basicUser = WsClientConfig.getDocboxBasicUser();
		String message = username + ":" + ts + ":" + getSha1DocboxPassword();
		Mac mac;
		try {
			mac = Mac.getInstance("HmacSHA1");
			mac.init(new SecretKeySpec(toHex(sha1(basicUser)).getBytes("UTF-8"), "HmacSHA1"));
			Base64 base64 = new Base64();
			return new String(base64.encode(mac.doFinal(message.getBytes("UTF-8"))));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Helper method to obtain SHA1 hash
	static byte[] sha1(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			md.update(text.getBytes("UTF-8"));
			return md.digest();
		} catch (final Exception e) {
			return null;
			// Error
		}
	}

	// Helper method to convert bytes to Hexadecimal form
	private static String toHex(final byte[] v) {
		char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		String out = "";

		for (final byte element : v) {
			out = out + hex[(element >> 4) & 0xF] + hex[element & 0xF];
		}
		return out;
	}

	public static final String ID = "ch.docbox.elexis.UserDocboxPreferences";//$NON-NLS-1$

	public static final String USR_DEFDOCBOXPATHFILES = "docbox/pathfiles"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXPATHHCARDAPI = "docbox/pathhcardapi"; //$NON-NLS-1$
	public static final String USR_AGENDASETTINGSPERUSER = "docbox/agendasettingsperuser"; //$NON-NLS-1$
	public static final String USR_USEHCARD = "docbox/usefmhcard"; //$NON-NLS-1$
	public static final String USR_GETAPPOINTMENTSEMERGENCYSERVICE = "docbox/getappointmentsemergencyservice";//$NON-NLS-1$
	public static final String USR_GETAPPOINTMENTSPHARMAVISITS = "docbox/getappointmentspharmavisits";//$NON-NLS-1$
	public static final String USR_GETAPPOINTMENTSTERMINVEREINBARUNG = "docbox/getappointmentsterminvereinbarung";//$NON-NLS-1$
	public static final String USR_APPOINTMENTSBEREICH = "docbox/getappointmentsbereich";//$NON-NLS-1$
	public static final String USR_ISDOCBOXTEST = "docbox/isdocboxtest";//$NON-NLS-1$
	public static final String USR_UPDATEDOCTORDIRECTORY = "docbox/updatedoctordirectory";//$NON-NLS-1$
	public static final String USR_USEPROXY = "docbox/useproxy"; //$NON-NLS-1$
	public static final String USR_PROXYHOST = "docbox/proxyhost"; //$NON-NLS-1$
	public static final String USR_PROXYPORT = "docbox/proxyport"; //$NON-NLS-1$

	public boolean oldAppointmentsEmergencyService;
	public boolean oldAppointmentsPharmaVisits;
	public boolean oldAppointmentsTerminvereinbarung;
	public String oldAppointmentsBereich;

	public static final String NOPASSWORD = "***NONE***"; //$NON-NLS-1$

	public UserDocboxPreferences() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.MANDATOR));
		setDescription(Messages.UserDocboxPreferences_Description);
		bereiche = Termin.TerminBereiche;
	}

	@Override
	protected void createFieldEditors() {

		oldSha1Password = getSha1DocboxPassword();
		oldLoginId = getDocboxLoginID(true);
		oldAppointmentsEmergencyService = isAppointmentsEmergencyService();
		oldAppointmentsPharmaVisits = isAppointmentsPharmaVisits();
		oldAppointmentsTerminvereinbarung = isAppointmentsTerminvereinbarung();
		oldAppointmentsBereich = getAppointmentsBereich();
		oldSecretKey = getSha1DocboxSecretKey();

		boolean enableForMandant = AccessControlServiceHolder.get().evaluate(EvACE.of(IUser.class, Right.UPDATE));

		loginIdFieldEditor = new StringFieldEditor(WsClientConfig.USR_DEFDOCBXLOGINID,
				Messages.UserDocboxPreferences_LoginId, getFieldEditorParent());
		addField(loginIdFieldEditor);
		loginIdFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());

		passwordFieldEditor = new StringFieldEditor(WsClientConfig.USR_DEFDOCBOXPASSWORD,
				Messages.UserDocboxPreferences_Password, getFieldEditorParent());
		passwordFieldEditor.getTextControl(getFieldEditorParent()).setEchoChar('*'); // $NON-NLS-1$
		passwordFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());

		addField(passwordFieldEditor);

		directoryhCardEditor = new DirectoryFieldEditor(USR_DEFDOCBOXPATHHCARDAPI,
				Messages.UserDocboxPreferences_PathHCardAPI, getFieldEditorParent());
		directoryhCardEditor.setEnabled(enableForMandant, getFieldEditorParent());

		addField(directoryhCardEditor);
		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		Button docboxConnectionTestButton = new Button(getFieldEditorParent(), SWT.PUSH);
		docboxConnectionTestButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {

				String sha1Password = (passwordFieldEditor.getStringValue().equals(oldSha1Password) ? oldSha1Password
						: CDACHServicesClient.getSHA1(passwordFieldEditor.getStringValue()));
				ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBXLOGINID,
						loginIdFieldEditor.getStringValue());
				ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBOXPASSWORD, sha1Password);
				if (showSha1SecretKey && secretkeyFieldEditor != null) {
					ConfigServiceHolder.setMandator(WsClientConfig.USR_SECRETKEY,
							secretkeyFieldEditor.getStringValue());
				}
				jakarta.xml.ws.Holder<java.lang.String> message = new jakarta.xml.ws.Holder<java.lang.String>();
				boolean isOk = performConnectionTest(message);
				MessageBox box = new MessageBox(UiDesk.getDisplay().getActiveShell(),
						(isOk ? SWT.ICON_WORKING : SWT.ICON_ERROR));
				box.setText(Messages.UserDocboxPreferences_ConnectionTestWithDocbox);
				box.setMessage(message.value);
				box.open();
			}
		});

		docboxConnectionTestButton.setText(Messages.UserDocboxPreferences_ConnectionTest);
		docboxConnectionTestButton.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));

		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		directoryFieldEditor = new DirectoryFieldEditor(USR_DEFDOCBOXPATHFILES,
				Messages.UserDocboxPreferences_PathFiles, getFieldEditorParent());
		addField(directoryFieldEditor);
		directoryFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());

		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		if (hasAgendaPlugin() && enableForMandant) {
			buttonAgendaSettingsPerUser = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonAgendaSettingsPerUser.setText(Messages.UserDocboxPreferences_AgendaSettingsPerUser);
			buttonAgendaSettingsPerUser.setSelection(getAgendaSettingsPerUser());
			buttonAgendaSettingsPerUser.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));

		}

		if (hasAgendaPlugin()) {

			boolean enabled = getAgendaSettingsPerUser() || enableForMandant;

			Label textLabel = new Label(getFieldEditorParent(), SWT.NONE);
			textLabel.setText(Messages.UserDocboxPreferences_AgendaBerich);
			textLabel.setLayoutData(SWTHelper.getFillGridData(1, false, 1, false));

			agendaBereichCombo = new Combo(getFieldEditorParent(), SWT.READ_ONLY | SWT.SINGLE);
			agendaBereichCombo.setItems(bereiche);
			agendaBereichCombo.select(getAgendaIndex());
			agendaBereichCombo.setEnabled(enabled);
			textLabel.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));

			buttonGetAppointmentsEmergencyService = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonGetAppointmentsEmergencyService
					.setText(Messages.UserDocboxPreferences_GetAppointmentsEmergencyService);
			buttonGetAppointmentsEmergencyService.setSelection(isAppointmentsEmergencyService());
			buttonGetAppointmentsEmergencyService.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
			buttonGetAppointmentsEmergencyService.setEnabled(enabled);

			buttonGetAppointmentsPharmaVisits = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonGetAppointmentsPharmaVisits.setText(Messages.UserDocboxPreferences_GetAppointmentsPharmaVisits);
			buttonGetAppointmentsPharmaVisits.setSelection(isAppointmentsPharmaVisits());
			buttonGetAppointmentsPharmaVisits.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
			buttonGetAppointmentsPharmaVisits.setEnabled(enabled);

			buttonGetAppointmentsTerminvereinbarung = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonGetAppointmentsTerminvereinbarung
					.setText(Messages.UserDocboxPreferences_GetAppointmentsTerminvereinbarungen);
			buttonGetAppointmentsTerminvereinbarung.setSelection(isAppointmentsTerminvereinbarung());
			buttonGetAppointmentsTerminvereinbarung.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
			buttonGetAppointmentsTerminvereinbarung.setEnabled(enabled);

		}

		if (enableForMandant) {
			new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL)
					.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

			buttonClearDocboxInbox = new Button(getFieldEditorParent(), SWT.PUSH);
			buttonClearDocboxInbox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent) {

					MessageBox box = new MessageBox(UiDesk.getDisplay().getActiveShell(),
							SWT.ICON_WARNING | SWT.YES | SWT.NO);
					box.setText(Messages.UserDocboxPreferences_ClearDocboxInbox);
					box.setMessage(Messages.UserDocboxPreferences_ClearDocboxInboxConfirm);
					if (box.open() == SWT.YES) {
						CdaMessage.deleteCdaMessages(CoreHub.actMandant);
					}
				}

			});

			buttonClearDocboxInbox.setText(Messages.UserDocboxPreferences_ClearDocboxInbox);
			buttonClearDocboxInbox.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		}

		Button btnConvertDocboxIds = new Button(getFieldEditorParent(), SWT.PUSH);
		btnConvertDocboxIds.setText(Messages.UserDocboxPreferences_ConvertDocboxIds);
		btnConvertDocboxIds.setToolTipText(Messages.UserDocboxPreferences_ConvertDocboxIds_Tooltip);
		btnConvertDocboxIds.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		btnConvertDocboxIds.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				IProgressService progService = PlatformUI.getWorkbench().getProgressService();
				try {
					progService.runInUI(progService, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException, InterruptedException {
							DocboxContact.importDocboxIdsFromKontaktExtinfo(monitor);
						}
					}, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public int getAgendaIndex() {
		String agendaBereich = getAppointmentsBereich();
		if (agendaBereich != null && !StringUtils.EMPTY.equals(agendaBereich)) {
			for (int i = 0; bereiche != null && i < bereiche.length; ++i) {
				if (bereiche[i].equals(agendaBereich)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static boolean hasAgendaPlugin() {
		Termin termin = Termin.load("1");
		return termin != null && termin.exists(); // $NON-NLS-1$
	}

	public static String getDocboxLoginID(boolean prefixed) {
		String loginId = ConfigServiceHolder.getMandator(WsClientConfig.USR_DEFDOCBXLOGINID, StringUtils.EMPTY);
		return loginId;
	}

	public static String getSha1DocboxPassword() {
		String sha1Password = ConfigServiceHolder.getMandator(WsClientConfig.USR_DEFDOCBOXPASSWORD, StringUtils.EMPTY);
		return sha1Password;
	}

	/**
	 * if loginID is prefix with TEST_ we use the tesystem
	 *
	 * @param loginID
	 * @return
	 */
	public static String getSha1DocboxSecretKey() {
		String docboxSha1SecretKey = StringUtils.EMPTY;
		if (isDocboxTest()) {
			return CDACHServicesClient.getSHA1("docboxtest");
		}
		URL baseUrl = ch.docbox.ws.cdachservices.CDACHServices_Service.class.getResource(StringUtils.EMPTY);
		try {
			URL url = new URL(baseUrl + "/product.key");
			InputStream in = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			docboxSha1SecretKey = bufferedReader.readLine();
		} catch (Exception e) {
			docboxSha1SecretKey = CDACHServicesClient
					.getSHA1(ConfigServiceHolder.getMandator(WsClientConfig.USR_SECRETKEY, StringUtils.EMPTY));
		}
		return docboxSha1SecretKey;
	}

	public static String getPathFiles() {
		return ConfigServiceHolder.getMandator(USR_DEFDOCBOXPATHFILES, StringUtils.EMPTY);
	}

	public static String getPathHCardAPI() {
		return ConfigServiceHolder.getMandator(USR_DEFDOCBOXPATHHCARDAPI, StringUtils.EMPTY);
	}

	public static boolean getAgendaSettingsPerUser() {
		if (CoreHub.getLoggedInContact() == null || !ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return false;
		}
		boolean value = ConfigServiceHolder.getMandator(USR_AGENDASETTINGSPERUSER, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
		return value;
	}

	private static String getBrowserHost() {
		String host = StringUtils.EMPTY;
		if (isDocboxTest()) {
			host = "www.test.docbox.ch"; //$NON-NLS-1$
		} else {
			host = "www.docbox.ch"; //$NON-NLS-1$
		}
		return host;
	}

	private static String getHost() {
		String host = StringUtils.EMPTY;
		if (useHCard()) {
			if (isDocboxTest()) {
				host = "swissmedicalsuite.test.docbox.ch"; //$NON-NLS-1$
			} else {
				host = "swissmedicalsuite.docbox.ch"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			if (isDocboxTest()) {
				host = "soap.test.docbox.swiss"; //$NON-NLS-1$
			} else {
				host = "soap.docbox.swiss"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return host;
	}

	public static String getDocboxBrowserHome() {
		return "http://www.docbox.ch";
	}

	public static String getDocboxBrowserUrl() {
		String host = getBrowserHost(); // $NON-NLS-1$
		String cgibin = "cgi-bin"; //$NON-NLS-1$
		return "https://" + host + "/" + cgibin + "/WebObjects/docbox.woa/wa/default"; //$NON-NLS-1$//$NON-NLS-2$
	}

	public static String getDocboxServiceUrl() {
		String host = getHost();
		return "https://" + host + "/CDACHServices"; //$NON-NLS-1$//$NON-NLS-2$
	}

	private void setAgendaSettingsPerUser(boolean value) {
		ConfigServiceHolder.setMandator(USR_AGENDASETTINGSPERUSER, (value ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public boolean performOk() {
		super.performOk();

		String sha1Password = (passwordFieldEditor.getStringValue().equals(oldSha1Password) ? oldSha1Password
				: CDACHServicesClient.getSHA1(passwordFieldEditor.getStringValue()));
		ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBXLOGINID, loginIdFieldEditor.getStringValue());
		ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBOXPASSWORD, sha1Password);
		ConfigServiceHolder.setMandator(USR_DEFDOCBOXPATHFILES, directoryFieldEditor.getStringValue());
		ConfigServiceHolder.setMandator(USR_DEFDOCBOXPATHHCARDAPI, directoryhCardEditor.getStringValue());
		if (showSha1SecretKey) {
			ConfigServiceHolder.setMandator(WsClientConfig.USR_SECRETKEY, secretkeyFieldEditor.getStringValue());
		}

		if (buttonAgendaSettingsPerUser != null) {
			setAgendaSettingsPerUser(buttonAgendaSettingsPerUser.getSelection());
		}

		if (hasAgendaPlugin()) {
			if (!oldAppointmentsBereich.equals(getSelectedAgendaBereich())) {
				setAppointmentsBereich(getSelectedAgendaBereich());
			}

			if (buttonGetAppointmentsEmergencyService.getSelection() != this.oldAppointmentsEmergencyService) {
				setAppointmentsEmergencyService(buttonGetAppointmentsEmergencyService.getSelection());
			}
			if (buttonGetAppointmentsPharmaVisits.getSelection() != this.oldAppointmentsPharmaVisits) {
				setAppointmentsPharmaVisits(buttonGetAppointmentsPharmaVisits.getSelection());
			}
			if (buttonGetAppointmentsTerminvereinbarung.getSelection() != this.oldAppointmentsTerminvereinbarung) {
				setAppointmentsTerminvereinbarung(buttonGetAppointmentsTerminvereinbarung.getSelection());
			}
		}

		return true;
	}

	private String getSelectedAgendaBereich() {
		String bereich = StringUtils.EMPTY;
		if (this.agendaBereichCombo.getSelectionIndex() != -1) {
			return bereiche[agendaBereichCombo.getSelectionIndex()];
		}
		return bereich;
	}

	@Override
	public boolean performCancel() {
		super.performCancel();

		ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBXLOGINID, oldLoginId);
		ConfigServiceHolder.setMandator(WsClientConfig.USR_DEFDOCBOXPASSWORD, oldSha1Password);

		return true;
	}

	@Override
	protected void performDefaults() {
		this.initialize();
	}

	public boolean performConnectionTest(jakarta.xml.ws.Holder<java.lang.String> message) {
		jakarta.xml.ws.Holder<java.lang.Boolean> _checkAccess_success = new jakarta.xml.ws.Holder<java.lang.Boolean>();
		try {
			CDACHServices port = getPort();
			port.checkAccess(_checkAccess_success, message);
		} catch (Exception e) {
			message.value = "Verbindungsproblem mit docbox";
			message.value += StringUtils.LF;
			message.value = e.getMessage();
			return false;
		} catch (java.lang.NoClassDefFoundError e) {
			message.value = "Verbindungsproblem mit docbox";
			message.value += StringUtils.LF;
			message.value += e.getMessage();
			return false;
		} catch (java.lang.ExceptionInInitializerError e2) {
			message.value = "Verbindungsproblem mit docbox";
			message.value += StringUtils.LF;
			return false;
		}
		return _checkAccess_success.value;
	}

	public static boolean hasValidDocboxCredentials() {
		return ((!StringUtils.EMPTY.equals(getDocboxLoginID(true))
				&& !StringUtils.EMPTY.equals(getSha1DocboxPassword())));
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	public static IPreferenceStore getSettingsForUser() {
		// if anwender is also mandant the settings preferences get a hickup, therefore
		// we return
		// the mandantcfg
		Optional<IMandator> activeMandator = ContextServiceHolder.get().getActiveMandator();
		Optional<IContact> activeUserContact = ContextServiceHolder.get().getActiveUserContact();
		if (activeUserContact.isPresent() && activeMandator.isPresent()
				&& activeUserContact.get().getId().equals(activeMandator.get().getId())) {
			return new ConfigServicePreferenceStore(Scope.MANDATOR);
		}
		IPreferenceStore settings = getAgendaSettingsPerUser() ? new ConfigServicePreferenceStore(Scope.USER)
				: new ConfigServicePreferenceStore(Scope.MANDATOR);
		return settings;
	}

	public static boolean isAppointmentsEmergencyService() {
		if (CoreHub.getLoggedInContact() == null || !ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return false;
		}
		return getSettingsForUser().getBoolean(USR_GETAPPOINTMENTSEMERGENCYSERVICE);
	}

	public static void setAppointmentsEmergencyService(boolean appointmentsEmergencyService) {
		getSettingsForUser().setValue(USR_GETAPPOINTMENTSEMERGENCYSERVICE, (appointmentsEmergencyService));
	}

	public static boolean isAppointmentsPharmaVisits() {
		if (CoreHub.getLoggedInContact() == null || !ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return false;
		}
		return getSettingsForUser().getBoolean(USR_GETAPPOINTMENTSPHARMAVISITS);
	}

	public static void setAppointmentsPharmaVisits(boolean appointmentsPharmaVisits) {
		getSettingsForUser().setValue(USR_GETAPPOINTMENTSPHARMAVISITS, (appointmentsPharmaVisits)); // $NON-NLS-1$
																									// //$NON-NLS-2$
	}

	public static boolean isAppointmentsTerminvereinbarung() {
		if (CoreHub.getLoggedInContact() == null || !ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return false;
		}
		return getSettingsForUser().getBoolean(USR_GETAPPOINTMENTSTERMINVEREINBARUNG); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setAppointmentsTerminvereinbarung(boolean appointmentsTerminvereinbarung) {
		getSettingsForUser().setValue(USR_GETAPPOINTMENTSTERMINVEREINBARUNG, (appointmentsTerminvereinbarung)); // $NON-NLS-1$
																												// //$NON-NLS-2$
	}

	public static String getAppointmentsBereich() {
		if (CoreHub.getLoggedInContact() == null || !ContextServiceHolder.get().getActiveMandator().isPresent()) {
			return StringUtils.EMPTY;
		}
		return getSettingsForUser().getString(USR_APPOINTMENTSBEREICH); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setAppointmentsBereich(String appointmentsBereich) {
		getSettingsForUser().setValue(USR_APPOINTMENTSBEREICH, appointmentsBereich); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean useHCard() {
		return ConfigServiceHolder.getMandator(USR_USEHCARD, false); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setUseHCard(boolean useHCard) {
		ConfigServiceHolder.setMandator(USR_USEHCARD, useHCard); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static boolean useProxy() {
		return ConfigServiceHolder.getMandator(USR_USEPROXY, false); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static void setUseProxy(boolean useHCard) {
		ConfigServiceHolder.setMandator(USR_USEPROXY, useHCard); // $NON-NLS-1$ //$NON-NLS-2$
	}

	public static synchronized CDACHServices getPort() {
		CDACHServices_Service serviceClient = new CDACHServices_Service();
		// if (UserDocboxPreferences.useHCard()) {
		// new HCardBrowser(UserDocboxPreferences.getDocboxLoginID(false),
		// null).setProxyPort();
		// }
		WsClientUtil.addWsSecurityAndHttpConfigWithClientCert(serviceClient, WsClientConfig.getUsername(),
				WsClientConfig.getPassword());

		CDACHServices port = serviceClient.getCDACHServices();
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
				getDocboxServiceUrl());

		Map<String, List<String>> headers = new HashMap<String, List<String>>();
		headers.put("Authorization", Collections.singletonList("Basic " + WsClientConfig.getDocboxBasicAuth()));
		((BindingProvider) port).getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, headers);

		return port;
	}

	public static boolean downloadAppointments() {
		return getAppointmentsBereich() != null && !StringUtils.EMPTY.equals(getAppointmentsBereich())
				&& (UserDocboxPreferences.isAppointmentsPharmaVisits()
						|| UserDocboxPreferences.isAppointmentsEmergencyService()
						|| UserDocboxPreferences.isAppointmentsTerminvereinbarung());
	}
}
