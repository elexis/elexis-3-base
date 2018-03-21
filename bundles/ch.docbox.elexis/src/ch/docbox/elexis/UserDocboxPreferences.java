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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.BindingProvider;

import org.apache.commons.codec.binary.Base64;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
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
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.docbox.ws.client.WsClientConfig;
import ch.elexis.docbox.ws.client.WsClientUtil;
import ch.rgw.io.Settings;
import ch.swissmedicalsuite.HCardBrowser;

/**
 * User specific settings: Case defaults
 */
public class UserDocboxPreferences extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	private String oldSha1Password;
	private String oldLoginId;
	
	private StringFieldEditor loginIdFieldEditor;
	private StringFieldEditor passwordFieldEditor;
	private StringFieldEditor secretkeyFieldEditor;
	private StringFieldEditor proxyPortFieldEditor;
	private StringFieldEditor proxyHostFieldEditor;
	
	private DirectoryFieldEditor directoryFieldEditor;
	private DirectoryFieldEditor directoryhCardEditor;
	private Button buttonAgendaSettingsPerUser;
	
	private Combo agendaBereichCombo;
	private Button buttonGetAppointmentsEmergencyService;
	private Button buttonGetAppointmentsPharmaVisits;
	private Button buttonGetAppointmentsTerminvereinbarung;
	private Button buttonConfigureCert;
	
	private Button buttonUseHCard;
	
	private Button buttonUseProxy;
	
	private Button buttonClearDocboxInbox;
	
	private String bereiche[];
	
	static private boolean showSha1SecretKey = true;
	
	protected static Log log = Log.get("UserDocboxPreferences"); //$NON-NLS-1$
	
	public static boolean isDocboxTest(){
		return getDocboxLoginID(true) != null
			&& getDocboxLoginID(true).startsWith(WsClientConfig.TESTLOGINIDPREFIX);
	}
	
	public static String getSSOSignature(String ts){
		
		String username = getDocboxLoginID(false);
		
		String sha1Password = getSha1DocboxPassword();
		String sha1SecretKey = getSha1DocboxSecretKey();
		
		String message = username + ":" + ts + ":" + sha1Password; //$NON-NLS-1$ //$NON-NLS-2$
		try {
			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey;
			signingKey = new SecretKeySpec(sha1SecretKey.getBytes("UTF-8"), "HmacSHA1"); //$NON-NLS-1$//$NON-NLS-2$
			
			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance("HmacSHA1");//$NON-NLS-1$
			mac.init(signingKey);
			
			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8")); //$NON-NLS-1$
			
			// base64-encode the hmac
			// If desired, convert the digest into a string
			byte[] base64 = Base64.encodeBase64(rawHmac);
			return new String(base64);
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final String ID = "ch.docbox.elexis.UserDocboxPreferences";//$NON-NLS-1$
	
	public static final String USR_DEFDOCBOXPATHFILES = "docbox/pathfiles"; //$NON-NLS-1$
	public static final String USR_DEFDOCBOXPATHHCARDAPI = "docbox/pathhcardapi"; //$NON-NLS-1$
	public static final String USR_AGENDASETTINGSPERUSER = "docbox/agendasettingsperuser"; //$NON-NLS-1$
	public static final String USR_USEHCARD = "docbox/usefmhcard"; //$NON-NLS-1$
	public static final String USR_GETAPPOINTMENTSEMERGENCYSERVICE =
		"docbox/getappointmentsemergencyservice";//$NON-NLS-1$
	public static final String USR_GETAPPOINTMENTSPHARMAVISITS =
		"docbox/getappointmentspharmavisits";//$NON-NLS-1$
	public static final String USR_GETAPPOINTMENTSTERMINVEREINBARUNG =
		"docbox/getappointmentsterminvereinbarung";//$NON-NLS-1$
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
	public boolean oldIsDocboxTest;
	public boolean oldUseHCard;
	public boolean oldUseProxy;
	public String oldSecretKey;
	
	public String proxyPort;
	public String proxyHost;
	public String oldProxyPort;
	public String oldProxyHost;
	
	public static final String NOPASSWORD = "***NONE***"; //$NON-NLS-1$
	
	public UserDocboxPreferences(){
		super(GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.mandantCfg));
		setDescription(Messages.UserDocboxPreferences_Description);
		bereiche = Termin.TerminBereiche;
	}
	
	@Override
	protected void createFieldEditors(){
		
		oldSha1Password = getSha1DocboxPassword();
		oldLoginId = getDocboxLoginID(true);
		oldAppointmentsEmergencyService = isAppointmentsEmergencyService();
		oldAppointmentsPharmaVisits = isAppointmentsPharmaVisits();
		oldAppointmentsTerminvereinbarung = isAppointmentsTerminvereinbarung();
		oldAppointmentsBereich = getAppointmentsBereich();
		oldUseHCard = useHCard();
		oldUseProxy = useProxy();
		oldProxyHost = getProxyHost();
		oldProxyPort = getProxyPort();
		
		oldSecretKey = getSha1DocboxSecretKey();
		
		boolean enableForMandant = CoreHub.acl.request(AccessControlDefaults.ACL_USERS);
		
		loginIdFieldEditor =
			new StringFieldEditor(WsClientConfig.USR_DEFDOCBXLOGINID,
				Messages.UserDocboxPreferences_LoginId, getFieldEditorParent());
		addField(loginIdFieldEditor);
		loginIdFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());
		
		passwordFieldEditor =
			new StringFieldEditor(WsClientConfig.USR_DEFDOCBOXPASSWORD,
				Messages.UserDocboxPreferences_Password, getFieldEditorParent());
		passwordFieldEditor.getTextControl(getFieldEditorParent()).setEchoChar('*'); //$NON-NLS-1$
		passwordFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());
		
		addField(passwordFieldEditor);
		
		if (showSha1SecretKey) {
			secretkeyFieldEditor =
				new StringFieldEditor(WsClientConfig.USR_SECRETKEY,
					Messages.UserDocboxPreferences_SecretKey, getFieldEditorParent());
			secretkeyFieldEditor.getTextControl(getFieldEditorParent()).setEchoChar('*'); //$NON-NLS-1$
			secretkeyFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());
			
			addField(secretkeyFieldEditor);
		}
		
		buttonConfigureCert = new Button(getFieldEditorParent(), SWT.PUSH);
		buttonConfigureCert.setText("Zertifikat konfigurieren");
		buttonConfigureCert.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		buttonConfigureCert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				CertificateConfigDialog dlg = new CertificateConfigDialog(getShell());
				dlg.open();
			}
		});
		if (WsClientUtil.isMedelexisCertAvailable()) {
			buttonConfigureCert.setEnabled(false);
		}
		
		buttonUseHCard = new Button(getFieldEditorParent(), SWT.CHECK);
		buttonUseHCard.setText(Messages.UserDocboxPreferences_UseHCard);
		buttonUseHCard.setSelection(useHCard());
		buttonUseHCard.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		buttonUseHCard.setEnabled(enableForMandant);
		
		directoryhCardEditor =
			new DirectoryFieldEditor(USR_DEFDOCBOXPATHHCARDAPI,
				Messages.UserDocboxPreferences_PathHCardAPI, getFieldEditorParent());
		directoryhCardEditor.setEnabled(enableForMandant, getFieldEditorParent());
		
		addField(directoryhCardEditor);
		
		buttonUseProxy = new Button(getFieldEditorParent(), SWT.CHECK);
		buttonUseProxy.setText(Messages.UserDocboxPreferences_UseProxy);
		buttonUseProxy.setSelection(useProxy());
		buttonUseProxy.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		buttonUseProxy.setEnabled(enableForMandant);
		
		proxyHostFieldEditor =
			new StringFieldEditor(USR_PROXYHOST, Messages.UserDocboxPreferences_UseProxyHost,
				getFieldEditorParent());
		addField(proxyHostFieldEditor);
		proxyHostFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());
		
		proxyPortFieldEditor =
			new StringFieldEditor(USR_PROXYPORT, Messages.UserDocboxPreferences_UseProxyPort,
				getFieldEditorParent());
		addField(proxyPortFieldEditor);
		proxyPortFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());
		
		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(SWTHelper
			.getFillGridData(3, true, 1, false));
		
		Button docboxConnectionTestButton = new Button(getFieldEditorParent(), SWT.PUSH);
		docboxConnectionTestButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0){
				
				String sha1Password =
					(passwordFieldEditor.getStringValue().equals(oldSha1Password) ? oldSha1Password
							: CDACHServicesClient.getSHA1(passwordFieldEditor.getStringValue()));
				CoreHub.mandantCfg.set(WsClientConfig.USR_DEFDOCBXLOGINID,
					loginIdFieldEditor.getStringValue());
				CoreHub.mandantCfg.set(WsClientConfig.USR_DEFDOCBOXPASSWORD, sha1Password);
				if (showSha1SecretKey && secretkeyFieldEditor != null) {
					CoreHub.mandantCfg.set(WsClientConfig.USR_SECRETKEY,
						secretkeyFieldEditor.getStringValue());
				}
				setUseHCard(buttonUseHCard.getSelection());
				setUseProxy(buttonUseProxy.getSelection());
				setProxyHost(proxyHostFieldEditor.getStringValue());
				setProxyPort(proxyPortFieldEditor.getStringValue());
				
				if (getSha1DocboxSecretKey() == null || "".equals(getSha1DocboxSecretKey())) {
					MessageBox box =
						new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_ERROR);
					box.setText(Messages.UserDocboxPreferences_NoSecretKeyTitle);
					box.setMessage(Messages.UserDocboxPreferences_NoSecretKey);
					box.open();
				} else {
					javax.xml.ws.Holder<java.lang.String> message =
						new javax.xml.ws.Holder<java.lang.String>();
					boolean isOk = performConnectionTest(message);
					MessageBox box =
						new MessageBox(UiDesk.getDisplay().getActiveShell(),
							(isOk ? SWT.ICON_WORKING : SWT.ICON_ERROR));
					box.setText(Messages.UserDocboxPreferences_ConnectionTestWithDocbox);
					box.setMessage(message.value);
					box.open();
				}
			}
		});
		
		docboxConnectionTestButton.setText(Messages.UserDocboxPreferences_ConnectionTest);
		docboxConnectionTestButton.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
		
		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(SWTHelper
			.getFillGridData(3, true, 1, false));
		
		directoryFieldEditor =
			new DirectoryFieldEditor(USR_DEFDOCBOXPATHFILES,
				Messages.UserDocboxPreferences_PathFiles, getFieldEditorParent());
		addField(directoryFieldEditor);
		directoryFieldEditor.setEnabled(enableForMandant, getFieldEditorParent());
		
		new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL).setLayoutData(SWTHelper
			.getFillGridData(3, true, 1, false));
		
		if (hasAgendaPlugin() && enableForMandant) {
			buttonAgendaSettingsPerUser = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonAgendaSettingsPerUser
				.setText(Messages.UserDocboxPreferences_AgendaSettingsPerUser);
			buttonAgendaSettingsPerUser.setSelection(getAgendaSettingsPerUser());
			buttonAgendaSettingsPerUser
				.setLayoutData(SWTHelper.getFillGridData(3, false, 1, false));
			
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
			buttonGetAppointmentsEmergencyService.setLayoutData(SWTHelper.getFillGridData(3, false,
				1, false));
			buttonGetAppointmentsEmergencyService.setEnabled(enabled);
			
			buttonGetAppointmentsPharmaVisits = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonGetAppointmentsPharmaVisits
				.setText(Messages.UserDocboxPreferences_GetAppointmentsPharmaVisits);
			buttonGetAppointmentsPharmaVisits.setSelection(isAppointmentsPharmaVisits());
			buttonGetAppointmentsPharmaVisits.setLayoutData(SWTHelper.getFillGridData(3, false, 1,
				false));
			buttonGetAppointmentsPharmaVisits.setEnabled(enabled);
			
			buttonGetAppointmentsTerminvereinbarung = new Button(getFieldEditorParent(), SWT.CHECK);
			buttonGetAppointmentsTerminvereinbarung
				.setText(Messages.UserDocboxPreferences_GetAppointmentsTerminvereinbarungen);
			buttonGetAppointmentsTerminvereinbarung
				.setSelection(isAppointmentsTerminvereinbarung());
			buttonGetAppointmentsTerminvereinbarung.setLayoutData(SWTHelper.getFillGridData(3,
				false, 1, false));
			buttonGetAppointmentsTerminvereinbarung.setEnabled(enabled);
			
		}
		
		if (enableForMandant) {
			new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL)
				.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			
			buttonClearDocboxInbox = new Button(getFieldEditorParent(), SWT.PUSH);
			buttonClearDocboxInbox.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent selectionEvent){
					
					MessageBox box =
						new MessageBox(UiDesk.getDisplay().getActiveShell(), SWT.ICON_WARNING
							| SWT.YES | SWT.NO);
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
			public void widgetSelected(SelectionEvent arg0){
				IProgressService progService = PlatformUI.getWorkbench().getProgressService();
				try {
					progService.runInUI(progService, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
							InterruptedException{
							DocboxContact.importDocboxIdsFromKontaktExtinfo(monitor);
						}
					}, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public int getAgendaIndex(){
		String agendaBereich = getAppointmentsBereich();
		if (agendaBereich != null && !"".equals(agendaBereich)) { //$NON-NLS-1$
			for (int i = 0; bereiche != null && i < bereiche.length; ++i) {
				if (bereiche[i].equals(agendaBereich)) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public static boolean hasAgendaPlugin(){
		Termin termin = Termin.load("1");
		return termin != null && termin.exists(); //$NON-NLS-1$
	}
	
	public static String getDocboxLoginID(boolean prefixed){
		String loginId = CoreHub.mandantCfg.get(WsClientConfig.USR_DEFDOCBXLOGINID, "");//$NON-NLS-1$
		if (!prefixed && loginId.startsWith(WsClientConfig.TESTLOGINIDPREFIX)) {
			loginId = loginId.substring(WsClientConfig.TESTLOGINIDPREFIX.length());
		}
		return loginId;
	}
	
	public static String getSha1DocboxPassword(){
		String sha1Password = CoreHub.mandantCfg.get(WsClientConfig.USR_DEFDOCBOXPASSWORD, "");//$NON-NLS-1$
		return sha1Password;
	}
	
	/**
	 * if loginID is prefix with TEST_ we use the tesystem
	 * 
	 * @param loginID
	 * @return
	 */
	public static String getSha1DocboxSecretKey(){
		String docboxSha1SecretKey = "";
		showSha1SecretKey = false;
		if (isDocboxTest()) {
			return CDACHServicesClient.getSHA1("docboxtest");
		}
		URL baseUrl = ch.docbox.ws.cdachservices.CDACHServices_Service.class.getResource("");
		try {
			URL url = new URL(baseUrl + "/product.key");
			InputStream in = url.openStream();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
			docboxSha1SecretKey = bufferedReader.readLine();
		} catch (Exception e) {
			docboxSha1SecretKey =
				CDACHServicesClient.getSHA1(CoreHub.mandantCfg
					.get(WsClientConfig.USR_SECRETKEY, ""));
			showSha1SecretKey = true;
		}
		return docboxSha1SecretKey;
	}
	
	public static String getPathFiles(){
		return CoreHub.mandantCfg.get(USR_DEFDOCBOXPATHFILES, "");//$NON-NLS-1$
	}
	
	public static String getPathHCardAPI(){
		return CoreHub.mandantCfg.get(USR_DEFDOCBOXPATHHCARDAPI, "");//$NON-NLS-1$
	}
	
	public static boolean getAgendaSettingsPerUser(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return false;
		}
		boolean value = CoreHub.mandantCfg.get(USR_AGENDASETTINGSPERUSER, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
		return value;
	}
	
	private static String getHost(){
		String host = "";
		if (useHCard()) {
			if (isDocboxTest()) {
				host = "swissmedicalsuite.test.docbox.ch"; //$NON-NLS-1$ 
			} else {
				host = "swissmedicalsuite.docbox.ch"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		} else {
			if (isDocboxTest()) {
				host = "www.test.docbox.ch"; //$NON-NLS-1$ 
			} else {
				host = "www.docbox.ch"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return host;
	}
	
	public static String getDocboxBrowserHome(){
		return "http://www.docbox.ch";
	}
	
	public static String getDocboxBrowserUrl(){
		String test = isDocboxTest() ? "test" : ""; //$NON-NLS-1$ //$NON-NLS-2$
		String host = getHost(); //$NON-NLS-1$ 
		String cgibin = "cgi-bin"; //$NON-NLS-1$ 
		return "https://" + host + "/" + cgibin + "/WebObjects/docbox" + test + ".woa/wa/default"; //$NON-NLS-1$//$NON-NLS-2$
	}
	
	public static String getDocboxServiceUrl(){
		String test = isDocboxTest() ? "test" : ""; //$NON-NLS-1$ //$NON-NLS-2$
		String host = getHost();
		String cgibin = "cgi-bin"; //$NON-NLS-1$ 
		return "https://" + host + "/" + cgibin + "/WebObjects/docboxservice" + test + ".woa/ws/CDACHServices"; //$NON-NLS-1$//$NON-NLS-2$
	}
	
	private void setAgendaSettingsPerUser(boolean value){
		CoreHub.mandantCfg.set(USR_AGENDASETTINGSPERUSER, (value ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Override
	public boolean performOk(){
		super.performOk();
		
		String sha1Password =
			(passwordFieldEditor.getStringValue().equals(oldSha1Password) ? oldSha1Password
					: CDACHServicesClient.getSHA1(passwordFieldEditor.getStringValue()));
		CoreHub.mandantCfg.set(WsClientConfig.USR_DEFDOCBXLOGINID,
			loginIdFieldEditor.getStringValue());
		CoreHub.mandantCfg.set(WsClientConfig.USR_DEFDOCBOXPASSWORD, sha1Password);
		CoreHub.mandantCfg.set(USR_DEFDOCBOXPATHFILES, directoryFieldEditor.getStringValue());
		CoreHub.mandantCfg.set(USR_DEFDOCBOXPATHHCARDAPI, directoryhCardEditor.getStringValue());
		if (showSha1SecretKey) {
			CoreHub.mandantCfg.set(WsClientConfig.USR_SECRETKEY,
				secretkeyFieldEditor.getStringValue());
		}
		
		if (buttonAgendaSettingsPerUser != null) {
			setAgendaSettingsPerUser(buttonAgendaSettingsPerUser.getSelection());
		}
		
		if (buttonUseHCard.getSelection() != oldUseHCard) {
			setUseHCard(buttonUseHCard.getSelection());
		}
		
		if (buttonUseProxy.getSelection() != oldUseProxy) {
			setUseProxy(buttonUseProxy.getSelection());
		}
		
		setProxyHost(proxyHostFieldEditor.getStringValue());
		setProxyPort(proxyPortFieldEditor.getStringValue());
		
		if (hasAgendaPlugin()) {
			if (!oldAppointmentsBereich.equals(getSelectedAgendaBereich())) {
				setAppointmentsBereich(getSelectedAgendaBereich());
			}
			
			if (buttonGetAppointmentsEmergencyService.getSelection() != this.oldAppointmentsEmergencyService) {
				setAppointmentsEmergencyService(buttonGetAppointmentsEmergencyService
					.getSelection());
			}
			if (buttonGetAppointmentsPharmaVisits.getSelection() != this.oldAppointmentsPharmaVisits) {
				setAppointmentsPharmaVisits(buttonGetAppointmentsPharmaVisits.getSelection());
			}
			if (buttonGetAppointmentsTerminvereinbarung.getSelection() != this.oldAppointmentsTerminvereinbarung) {
				setAppointmentsTerminvereinbarung(buttonGetAppointmentsTerminvereinbarung
					.getSelection());
			}
		}
		
		return true;
	}
	
	private String getSelectedAgendaBereich(){
		String bereich = ""; //$NON-NLS-1$
		if (this.agendaBereichCombo.getSelectionIndex() != -1) {
			return bereiche[agendaBereichCombo.getSelectionIndex()];
		}
		return bereich;
	}
	
	@Override
	public boolean performCancel(){
		super.performCancel();
		
		CoreHub.mandantCfg.set(WsClientConfig.USR_DEFDOCBXLOGINID, oldLoginId);
		CoreHub.mandantCfg.set(WsClientConfig.USR_DEFDOCBOXPASSWORD, oldSha1Password);
		CoreHub.mandantCfg.set(WsClientConfig.USR_SECRETKEY, oldSecretKey);
		setUseHCard(oldUseHCard);
		setUseProxy(oldUseProxy);
		setProxyHost(oldProxyHost);
		setProxyPort(oldProxyPort);
		
		return true;
	}
	
	@Override
	protected void performDefaults(){
		this.initialize();
	}
	
	public boolean performConnectionTest(javax.xml.ws.Holder<java.lang.String> message){
		javax.xml.ws.Holder<java.lang.Boolean> _checkAccess_success =
			new javax.xml.ws.Holder<java.lang.Boolean>();
		try {
			CDACHServices port = getPort();
			port.checkAccess(_checkAccess_success, message);
		} catch (Exception e) {
			message.value = "Verbindungsproblem mit docbox";
			message.value += "\n";
			message.value = e.getMessage();
			return false;
		} catch (java.lang.NoClassDefFoundError e) {
			message.value = "Verbindungsproblem mit docbox";
			message.value += "\n";
			message.value += e.getMessage();
			return false;
		} catch (java.lang.ExceptionInInitializerError e2) {
			message.value = "Verbindungsproblem mit docbox";
			message.value += "\n";
			return false;
		}
		return _checkAccess_success.value;
	}
	
	public static boolean hasValidDocboxCredentials(){
		return ((!"".equals(getDocboxLoginID(true)) && !"".equals(getSha1DocboxPassword())) || useHCard()) && !"".equals(getSha1DocboxSecretKey()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	
	public void init(IWorkbench workbench){
		
	}
	
	public static Settings getSettingsForUser(){
		// if anwender is also mandant the settings preferences get a hickup, therefore we return
		// the mandantcfg
		if (CoreHub.actUser.getId().equals(CoreHub.actMandant.getId())) {
			return CoreHub.mandantCfg;
		}
		Settings settings = getAgendaSettingsPerUser() ? CoreHub.userCfg : CoreHub.mandantCfg;
		return settings;
	}
	
	public static boolean isAppointmentsEmergencyService(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return false;
		}
		return getSettingsForUser().get(USR_GETAPPOINTMENTSEMERGENCYSERVICE, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setAppointmentsEmergencyService(boolean appointmentsEmergencyService){
		getSettingsForUser().set(USR_GETAPPOINTMENTSEMERGENCYSERVICE,
			(appointmentsEmergencyService ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean isAppointmentsPharmaVisits(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return false;
		}
		return getSettingsForUser().get(USR_GETAPPOINTMENTSPHARMAVISITS, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setAppointmentsPharmaVisits(boolean appointmentsPharmaVisits){
		getSettingsForUser().set(USR_GETAPPOINTMENTSPHARMAVISITS,
			(appointmentsPharmaVisits ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean isAppointmentsTerminvereinbarung(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return false;
		}
		return getSettingsForUser().get(USR_GETAPPOINTMENTSTERMINVEREINBARUNG, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setAppointmentsTerminvereinbarung(boolean appointmentsTerminvereinbarung){
		getSettingsForUser().set(USR_GETAPPOINTMENTSTERMINVEREINBARUNG,
			(appointmentsTerminvereinbarung ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getAppointmentsBereich(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return ""; //$NON-NLS-1$
		}
		return getSettingsForUser().get(USR_APPOINTMENTSBEREICH, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setAppointmentsBereich(String appointmentsBereich){
		getSettingsForUser().set(USR_APPOINTMENTSBEREICH, appointmentsBereich); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean useHCard(){
		return CoreHub.mandantCfg.get(USR_USEHCARD, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setUseHCard(boolean useHCard){
		CoreHub.mandantCfg.set(USR_USEHCARD, (useHCard ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static boolean useProxy(){
		return CoreHub.mandantCfg.get(USR_USEPROXY, "0").equals("1"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setUseProxy(boolean useHCard){
		CoreHub.mandantCfg.set(USR_USEPROXY, (useHCard ? "1" : "0")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static synchronized CDACHServices getPort(){
		System.setProperty("jna.library.path", UserDocboxPreferences.getPathHCardAPI());
		if (UserDocboxPreferences.useProxy()) {
			System.setProperty("https.proxyHost", UserDocboxPreferences.getProxyHost());
			System.setProperty("https.proxyPort", UserDocboxPreferences.getProxyPort());
		}
		CDACHServices_Service serviceClient = new CDACHServices_Service();
		if (UserDocboxPreferences.useHCard()) {
			new HCardBrowser(UserDocboxPreferences.getDocboxLoginID(false), null).setProxyPort();
		}
		WsClientUtil.addWsSecurityAndHttpConfigWithClientCert(serviceClient,
			WsClientConfig.getSecretkey() + WsClientConfig.getUsername(),
			WsClientConfig.getPassword(), WsClientConfig.getP12Path(), null,
			WsClientConfig.getP12Password(), null);
		
		CDACHServices port = serviceClient.getCDACHServices();
		((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
			getDocboxServiceUrl());
		
		return port;
	}
	
	public static boolean downloadAppointments(){
		return getAppointmentsBereich() != null
			&& !"".equals(getAppointmentsBereich())
			&& (UserDocboxPreferences.isAppointmentsPharmaVisits()
				|| UserDocboxPreferences.isAppointmentsEmergencyService() || UserDocboxPreferences
					.isAppointmentsTerminvereinbarung());
	}
	
	public static String getProxyHost(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return ""; //$NON-NLS-1$
		}
		return CoreHub.mandantCfg.get(USR_PROXYHOST, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setProxyHost(String proxyHost){
		CoreHub.mandantCfg.set(USR_PROXYHOST, proxyHost); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String getProxyPort(){
		if (CoreHub.actUser == null || CoreHub.mandantCfg == null) {
			return ""; //$NON-NLS-1$
		}
		return CoreHub.mandantCfg.get(USR_PROXYPORT, ""); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static void setProxyPort(String proxyPort){
		CoreHub.mandantCfg.set(USR_PROXYPORT, proxyPort); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
}
