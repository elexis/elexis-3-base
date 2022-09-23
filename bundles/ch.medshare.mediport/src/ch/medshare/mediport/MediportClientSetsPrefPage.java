/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.medshare.mediport;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Mandant;
import ch.elexis.data.Query;
import ch.elexis.tarmedprefs.TarmedRequirements;
import ch.medshare.awt.Desktop;
import ch.medshare.mediport.config.Client;
import ch.medshare.mediport.config.ClientParam;
import ch.medshare.mediport.config.ConfigKeys;
import ch.medshare.mediport.config.MPCProperties;
import ch.medshare.mediport.util.MediPortHelper;
import ch.medshare.swt.widgets.DirectoryText;
import ch.medshare.swt.widgets.FileText;
import ch.rgw.tools.ExHandler;

public class MediportClientSetsPrefPage extends MediPortAbstractPrefPage {

	String prefsKey;

	Combo cbMandant;

	// Client Felder
	Group clientComp;

	Combo cbMKey;

	Text txtSenderEan;

	Button cxWieMandant;

	DirectoryText txtSendDir;

	DirectoryText txtReceiveDir;

	DirectoryText txtReceiveTestDir;

	DirectoryText txtErrorDir;

	DirectoryText txtDocStatDir;

	FileText txtPartnerFile;

	// Param Felder
	Group paramComp;

	Combo cbNKey;

	Text txtName;

	DirectoryText txtClientDir;

	Combo cbDocAttr;

	Combo cbDocPrinted;

	Combo cbDistType;

	Combo cbPrintLanguage;

	Text txtTrustCenterEAN;

	Integer currentClientNum;

	Integer currentParamNum;

	Map<String, Mandant> mandantMap = new Hashtable<String, Mandant>();

	private Client mapClientValues(Client mappedClient) {
		if (mappedClient == null) {
			return null;
		}
		mappedClient.setEan(txtSenderEan.getText());
		mappedClient.setSend_dir(txtSendDir.getText());
		mappedClient.setReceive_dir(txtReceiveDir.getText());
		mappedClient.setReceivetest_dir(txtReceiveTestDir.getText());
		mappedClient.setError_dir(txtErrorDir.getText());
		mappedClient.setPartner_file(txtPartnerFile.getText());
		mappedClient.setDocstat_dir(txtDocStatDir.getText());

		return mappedClient;
	}

	private ClientParam mapParamValues(ClientParam mappedParam) {
		if (mappedParam == null) {
			return null;
		}
		mappedParam.setName(txtName.getText());
		mappedParam.setDir(txtClientDir.getText());

		String selDocAttr = cbDocAttr.getItem(cbDocAttr.getSelectionIndex());
		mappedParam.setDocattr(selDocAttr);

		String selDocPrinted = cbDocPrinted.getItem(cbDocPrinted.getSelectionIndex());
		if (LBL_DOC_PRINT_COPY.equals(selDocPrinted)) {
			mappedParam.setDocprinted(ConfigKeys.FALSE);
		} else {
			mappedParam.setDocprinted(ConfigKeys.TRUE);
		}

		String selDistType = cbDistType.getItem(cbDistType.getSelectionIndex());
		if (LBL_DIST_TYPE_A.equals(selDistType)) {
			mappedParam.setDisttype("1"); //$NON-NLS-1$
		} else {
			mappedParam.setDisttype("0"); //$NON-NLS-1$
		}

		String selLanguage = cbPrintLanguage.getItem(cbPrintLanguage.getSelectionIndex());
		if (LBL_LANGUAGE_F.equals(selLanguage)) {
			mappedParam.setPrintlanguage(ConfigKeys.FRENCH);
		} else if (LBL_LANGUAGE_I.equals(selLanguage)) {
			mappedParam.setPrintlanguage(ConfigKeys.ITALIAN);
		} else {
			mappedParam.setPrintlanguage(ConfigKeys.GERMAN);
		}

		mappedParam.setTrustcenterean(txtTrustCenterEAN.getText());

		return mappedParam;
	}

	private Mandant getSelectedMandant() {
		return this.mandantMap.get(cbMandant.getText());
	}

	private Integer getSelectedNum(Combo cbKey) {
		Integer key = null;
		String keyStr = cbKey.getText();
		if (keyStr != null && keyStr.length() > 0) {
			if (!LBL_NEW_KEY.equals(keyStr)) {
				try {
					key = Integer.parseInt(cbKey.getText());
				} catch (NumberFormatException e) {
					Log.get(getClass().getName()).log(e.getMessage(), Log.WARNINGS);
				}
			}
		}
		return key;
	}

	private Integer getSelectedClientNum() {
		return getSelectedNum(cbMKey);
	}

	private Integer getSelectedParamNum() {
		return getSelectedNum(cbNKey);
	}

	private void fillMKey(Integer clientNum) {
		String clientNumStr = null;
		if (clientNum != null) {
			Client client = props.getClient(clientNum);
			if (client != null) {
				clientNumStr = clientNum.toString();
			} else {
				clientNum = null;
			}
		}

		cbMKey.removeAll();
		cbMKey.add(LBL_NEW_KEY);
		for (String key : getFreeClientKeys(props, getSelectedMandant(), mandantMap.values())) {
			cbMKey.add(key);
			if (key.equals(clientNumStr)) {
				clientNumStr = null;
			}
		}
		if (clientNum != null) {
			if (clientNumStr != null) {
				cbMKey.add(clientNumStr);
			}
			cbMKey.setText(clientNum.toString());
		} else {
			cbMKey.setText(LBL_NEW_KEY);
		}
		this.currentClientNum = getSelectedClientNum();
	}

	private void fillNKey(Client client, Integer paramNum) {
		String paramNumStr = null;
		if (paramNum != null) {
			paramNumStr = paramNum.toString();
		}

		cbNKey.removeAll();
		cbNKey.add(LBL_NEW_KEY);
		if (client != null) {
			for (Integer key : client.getParamKeys()) {
				cbNKey.add(key.toString());
				if (key.toString().equals(paramNumStr)) {
					paramNumStr = null;
				}
			}
		}
		if (paramNum == null) {
			cbNKey.select(0);
		} else {
			if (paramNumStr != null) {
				cbNKey.add(paramNumStr);
			}
			cbNKey.setText(paramNum.toString());
		}
		this.currentParamNum = getSelectedParamNum();
	}

	private void fillClient(Mandant mandant, Integer clientNum) {
		Client client = props.getClient(clientNum);
		if (client == null) {
			client = new Client(getPrefString(MediPortAbstractPrefPage.MPC_INSTALL_DIR));
		}
		if ((client.getEan() == null || client.getEan().length() == 0) && mandant != null) {
			client.setEan(TarmedRequirements
					.getEAN(CoreModelServiceHolder.get().load(mandant.getId(), IMandator.class).orElse(null)));
		}

		fillMKey(clientNum);

		String mandantEan = null;
		String senderEan = client.getEan();
		if (mandant != null) {
			mandantEan = TarmedRequirements
					.getEAN(CoreModelServiceHolder.get().load(mandant.getId(), IMandator.class).orElse(null));
		}
		if (senderEan == null && mandantEan != null) {
			senderEan = mandantEan;
		}
		txtSenderEan.setText(senderEan);
		cxWieMandant.setSelection(
				(mandantEan == null && senderEan == null) || (senderEan != null) && senderEan.equals(mandantEan));
		txtSenderEan.setEnabled(!cxWieMandant.getSelection());

		txtSendDir.setText(client.getSend_dir());
		txtReceiveDir.setText(client.getReceive_dir());
		txtReceiveTestDir.setText(client.getReceivetest_dir());
		txtErrorDir.setText(client.getError_dir());
		txtDocStatDir.setText(client.getDocstat_dir());
		txtPartnerFile.setText(client.getPartner_file());

		Integer firstParamKey = null;
		if (client.getParamKeys().size() > 0) {
			firstParamKey = client.getParamKeys().get(0);
		}
		fillParam(clientNum, firstParamKey);
	}

	private void fillParam(Integer clientNum, Integer paramNum) {
		Client client = props.getClient(clientNum);
		ClientParam param = new ClientParam(Messages.MediportClientSetsPrefPage_default_paramName);
		if (client != null && paramNum != null) {
			param = client.getParam(paramNum);
		}

		fillNKey(client, paramNum);

		txtName.setText(param.getName());
		txtClientDir.setText(param.getDir());
		cbDocAttr.setText(param.getDocattr());
		if (ConfigKeys.TRUE.equals(param.getDocprinted())) {
			cbDocPrinted.select(0);
		} else {
			cbDocPrinted.select(1);
		}

		if ("0".equals(param.getDisttype())) { // B-Post
			cbDistType.select(0);
		} else {
			cbDistType.select(1);
		}

		if (ConfigKeys.FRENCH.equals(param.getPrintlanguage())) {
			cbPrintLanguage.select(1);
		} else if (ConfigKeys.ITALIAN.equals(param.getPrintlanguage())) {
			cbPrintLanguage.select(2);
		} else {
			cbPrintLanguage.select(0);
		}

		txtTrustCenterEAN.setText(param.getTrustcenterean());
	}

	private void writeClient() {
		Integer clientNum = this.currentClientNum;
		Client client = null;
		if (clientNum != null) {
			client = props.getClient(clientNum);
		}
		if (client == null) {
			client = new Client(getPrefString(MediPortAbstractPrefPage.MPC_INSTALL_DIR));
			clientNum = props.addNewClient(client);
			fillMKey(clientNum);
		}
		mapClientValues(client);
		putPrefString(prefsKey, String.valueOf(clientNum));
		storePrefs();
	}

	private void writeParam() {
		Integer clientNum = this.currentClientNum;
		ClientParam param = new ClientParam(StringUtils.EMPTY);
		if (clientNum != null) {
			Client client = props.getClient(clientNum);
			Integer paramNum = this.currentParamNum;
			if (paramNum != null) {
				param = client.getParam(paramNum);
			} else {
				paramNum = client.addNewParam(param);
				fillNKey(client, paramNum);
			}
		}

		mapParamValues(param);
	}

	private boolean storeParam() {
		writeParam();

		try {
			props.store();
		} catch (IOException ex) {
			ExHandler.handle(ex);
			MessageDialog.openError(getShell(), Messages.MediportClientSetsPrefPage_error_title_saveConfig,
					ex.getMessage());
			return false;
		}

		return true;
	}

	protected boolean storeAll() {
		if (props != null) {
			writeClient();

			return storeParam();
		}
		return true;
	}

	private void clientChanged() {
		// Zuerst aktuelle Werte speichern
		if (this.currentClientNum != null) {
			Client client = props.getClient(this.currentClientNum);
			client = mapClientValues(client);
			if (client != null && client.hasChanged()) {
				storeAll();
			}
		}

		Integer clientNum = getSelectedClientNum();
		Mandant mandant = getSelectedMandant();
		prefsKey = MediPortHelper.getMandantPrefix(mandant.getLabel());
		String numString = getPrefString(prefsKey);
		if (numString != null && numString.length() > 0) {
			clientNum = Integer.parseInt(numString);
		}

		fillClient(mandant, clientNum);
	}

	private void mKeyChanged() {
		// Zuerst aktuelle Werte speichern
		if (this.currentClientNum != null) {
			Client client = props.getClient(this.currentClientNum);
			client = mapClientValues(client);
			if (client != null && client.hasChanged()) {
				storeAll();
			}
		}
		this.currentClientNum = getSelectedClientNum();
		fillClient(getSelectedMandant(), this.currentClientNum);
	}

	private void nKeyChanged() {
		// Zuerst aktuelle Werte speichern
		Integer clientNum = getSelectedClientNum();
		if (clientNum != null && this.currentParamNum != null) {
			Client client = props.getClient(clientNum);
			ClientParam param = client.getParam(this.currentParamNum);
			param = mapParamValues(param);
			if (param != null && param.hasChanged()) {
				storeAll();
			}
		}

		this.currentParamNum = getSelectedParamNum();
		fillParam(clientNum, this.currentParamNum);
	}

	private List<String> getFreeClientKeys(MPCProperties props, Mandant currentMandant, Collection<Mandant> mandanten) {
		// Zugeordnete Mandanten lesen
		List<String> allocatedKeyList = new Vector<String>();
		for (Mandant m : mandanten) {
			if (!m.equals(currentMandant)) {
				String key = getPrefString(MediPortHelper.getMandantPrefix(m.getLabel()));
				if (key != null && key.length() > 0) {
					allocatedKeyList.add(key);
				}
			}
		}
		List<String> retList = new Vector<String>();
		for (Integer key : props.getClientKeys()) {
			String keyStr = key.toString();
			if (!allocatedKeyList.contains(keyStr)) {
				retList.add(keyStr);
			}
		}

		return retList;
	}

	@Override
	protected Control createContents(Composite parent) {
		if (props != null) {
			Composite comp = new Composite(parent, SWT.NONE);
			comp.setLayout(new GridLayout(2, false));
			cbMandant = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.FILL);
			cbMandant.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			// Client
			clientComp = new Group(comp, SWT.NONE);
			clientComp.setText(Messages.MediportClientSetsPrefPage_lbl_title_config);
			clientComp.setLayout(new GridLayout(3, false));
			clientComp.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));

			Label lblMKey = new Label(clientComp, SWT.NONE);
			lblMKey.setText(Messages.MediportClientSetsPrefPage_lbl_mKey);
			cbMKey = new Combo(clientComp, SWT.DROP_DOWN | SWT.READ_ONLY);
			cbMKey.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));

			Label lblEan = new Label(clientComp, SWT.NONE);
			lblEan.setText(Messages.MediportClientSetsPrefPage_lbl_SenderEAN);
			txtSenderEan = new Text(clientComp, SWT.BORDER);
			txtSenderEan.setEnabled(false);
			txtSenderEan.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			cxWieMandant = new Button(clientComp, SWT.CHECK);
			cxWieMandant.setText(Messages.MediportClientSetsPrefPage_lbl_WieMandant);

			Label lblSendDir = new Label(clientComp, SWT.NONE);
			lblSendDir.setText(Messages.MediportClientSetsPrefPage_lbl_Sendeverzeichnis);
			txtSendDir = new DirectoryText(clientComp, SWT.BORDER);
			txtSendDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Label lblReceiveDir = new Label(clientComp, SWT.NONE);
			lblReceiveDir.setText(Messages.MediportClientSetsPrefPage_lbl_Empfangsverzeichnis);
			txtReceiveDir = new DirectoryText(clientComp, SWT.BORDER);
			txtReceiveDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Label lblReceiveTestDir = new Label(clientComp, SWT.NONE);
			lblReceiveTestDir.setText(Messages.MediportClientSetsPrefPage_lbl_EmpfangsverzeichnisTest);
			txtReceiveTestDir = new DirectoryText(clientComp, SWT.BORDER);
			txtReceiveTestDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Label lblErrorDir = new Label(clientComp, SWT.NONE);
			lblErrorDir.setText(Messages.MediportClientSetsPrefPage_lbl_Fehlerverzeichnis);
			txtErrorDir = new DirectoryText(clientComp, SWT.BORDER);
			txtErrorDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Label lblDocStatDir = new Label(clientComp, SWT.NONE);
			lblDocStatDir.setText(Messages.MediportClientSetsPrefPage_lbl_DokumentstatusVerzeichnis);
			txtDocStatDir = new DirectoryText(clientComp, SWT.BORDER);
			txtDocStatDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Label lblPartnerFile = new Label(clientComp, SWT.NONE);
			lblPartnerFile.setText(Messages.MediportClientSetsPrefPage_lbl_Partnerfile);
			txtPartnerFile = new FileText(clientComp, SWT.BORDER);
			txtPartnerFile.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Button btnPartnerFile = new Button(clientComp, SWT.PUSH);
			btnPartnerFile.setText(Messages.MediportClientSetsPrefPage_btn_PartnerinfoAnzeigen);
			btnPartnerFile.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			// Parameter-Set
			paramComp = new Group(clientComp, SWT.NONE);
			paramComp.setText(Messages.MediportClientSetsPrefPage_lbl_title_Parameter);
			paramComp.setLayout(new GridLayout(3, false));
			paramComp.setLayoutData(SWTHelper.getFillGridData(3, true, 1, true));

			Label lblNKey = new Label(paramComp, SWT.NONE);
			lblNKey.setText(Messages.MediportClientSetsPrefPage_lbl_nKey);
			cbNKey = new Combo(paramComp, SWT.DROP_DOWN | SWT.READ_ONLY);
			cbNKey.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));

			Label lblParam = new Label(paramComp, SWT.NONE);
			lblParam.setText(Messages.MediportClientSetsPrefPage_lbl_Bezeichnung);
			txtName = new Text(paramComp, SWT.BORDER);
			txtName.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

			Label lblClientDir = new Label(paramComp, SWT.NONE);
			lblClientDir.setText(Messages.MediportClientSetsPrefPage_lbl_Ausgabeverzeichnis);
			txtClientDir = new DirectoryText(paramComp, SWT.BORDER);
			txtClientDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

			Label lblDocAttr = new Label(paramComp, SWT.NONE);
			lblDocAttr.setText(Messages.MediportClientSetsPrefPage_lbl_Workflow);
			cbDocAttr = new Combo(paramComp, SWT.BORDER | SWT.READ_ONLY);
			cbDocAttr.add(TIER_PAYANT);
			cbDocAttr.add(TIER_GARANT_MANUELL);
			cbDocAttr.add(TIER_GARANT_DIRECT);
			cbDocAttr.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

			Label lblDocPrinted = new Label(paramComp, SWT.NONE);
			lblDocPrinted.setText(Messages.MediportClientSetsPrefPage_lbl_Rechnungskopie);
			cbDocPrinted = new Combo(paramComp, SWT.BORDER | SWT.READ_ONLY);
			cbDocPrinted.add(LBL_DOC_NO_PRINT);
			cbDocPrinted.add(LBL_DOC_PRINT_COPY);
			cbDocPrinted.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

			Label lblDistType = new Label(paramComp, SWT.NONE);
			lblDistType.setText(Messages.MediportClientSetsPrefPage_lbl_Versandart);
			cbDistType = new Combo(paramComp, SWT.BORDER | SWT.READ_ONLY);
			cbDistType.add(LBL_DIST_TYPE_B);
			cbDistType.add(LBL_DIST_TYPE_A);
			cbDistType.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

			Label lblPrintLanguage = new Label(paramComp, SWT.NONE);
			lblPrintLanguage.setText(Messages.MediportClientSetsPrefPage_lbl_Drucksprache);
			cbPrintLanguage = new Combo(paramComp, SWT.BORDER | SWT.READ_ONLY);
			cbPrintLanguage.add(LBL_LANGUAGE_D);
			cbPrintLanguage.add(LBL_LANGUAGE_F);
			cbPrintLanguage.add(LBL_LANGUAGE_I);
			cbPrintLanguage.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

			Label lblTrustEan = new Label(paramComp, SWT.NONE);
			lblTrustEan.setText(Messages.MediportClientSetsPrefPage_lbl_TrustcenterEAN);
			txtTrustCenterEAN = new Text(paramComp, SWT.BORDER);
			txtTrustCenterEAN.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

			// Events
			cbMandant.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					clientChanged();
				}
			});

			cbMKey.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					mKeyChanged();
				}
			});

			cxWieMandant.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					txtSenderEan.setEnabled(!cxWieMandant.getSelection());
					txtSenderEan.setText(TarmedRequirements.getEAN(CoreModelServiceHolder.get()
							.load(getSelectedMandant().getId(), IMandator.class).orElse(null)));
				}
			});

			cbNKey.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					nKeyChanged();
				}
			});

			btnPartnerFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						File partnerFile = new File(txtPartnerFile.getText());
						Desktop.open(partnerFile);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						MessageDialog.openError(getShell(),
								Messages.MediportClientSetsPrefPage_error_msg_PartnerdateiOeffnen, ex.getMessage());
					}
				}
			});

			// Data
			Query<Mandant> qbe = new Query<Mandant>(Mandant.class);
			List<Mandant> list = qbe.execute();
			for (Mandant m : list) {
				cbMandant.add(m.getLabel());
				mandantMap.put(m.getLabel(), m);
			}
			final String actMandantLabel = CoreHub.actMandant.getLabel();
			cbMandant.setText(actMandantLabel);

			cxWieMandant.setSelection(true);
			cbDocAttr.select(0);
			cbDocPrinted.select(0);
			cbDistType.select(0);
			String defaultLang = Locale.getDefault().getLanguage();
			if (Locale.FRENCH.getLanguage().equals(defaultLang)) {
				cbPrintLanguage.select(1);
			} else if (Locale.ITALIAN.getLanguage().equals(defaultLang)) {
				cbPrintLanguage.select(2);
			} else {
				cbPrintLanguage.select(0);
			}

			clientChanged();

			return comp;
		} else {
			Composite form = new Composite(parent, SWT.NONE);
			form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			form.setLayout(new FillLayout());
			new Label(form, SWT.WRAP).setText(Messages.MediportClientSetsPrefPage_error_msg_missingConfiguration);
			return form;
		}
	}

	@Override
	protected void showReloadInfo() {
		if (props != null) {
			MessageDialog.openInformation(getShell(),
					Messages.MediportClientSetsPrefPage_info_title_MediPortCommunicator,
					Messages.MediportClientSetsPrefPage_info_reloadInfo1
							+ Messages.MediportClientSetsPrefPage_info_reloadInfo2);
		}
	}

	public void init(IWorkbench workbench) {
		setMessage(Messages.MediportClientSetsPrefPage_message);
	}

}
