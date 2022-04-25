/**
 * (c) 2007 by G. Weirich
 * All rights reserved
 *
 * From: Laborimport Viollier
 *
 * Adapted to Bioanalytica by Daniel Lutz <danlutz@watz.ch>
 * Adapted to Risch by Gerry Weirich
 *
 */

package ch.medshare.mediport;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.medshare.mediport.config.ConfigKeys;
import ch.medshare.mediport.config.MPCProperties;
import ch.medshare.mediport.util.MediPortHelper;
import ch.medshare.swt.widgets.DirectoryText;
import ch.medshare.util.UtilFile;
import ch.rgw.tools.ExHandler;

public class MediportMainPrefPage extends MediPortAbstractPrefPage {
	private static Log log = Log.get("MediportMainPrefPage"); //$NON-NLS-1$

	DirectoryText txtInstallDir;

	Combo cbServer;

	Combo cbServerIp;

	Combo cbAusgabe;

	Text txtIntermediaerEAN;

	Text txtSenderEAN;

	Text txtSenderDN;

	Button btnInit;

	private boolean overwriteRunning = false;

	private boolean installChanged = false;

	private void fillData() {
		String installDir = getPrefString(MPC_INSTALL_DIR);
		txtInstallDir.setText(installDir);
		cbAusgabe.setText(getPrefString(MPC_AUSGABE));
		cbServer.setText(getPrefString(MPC_SERVER));
		String interEAN = getPrefString(MPC_INTERMEDIAER_EAN);
		if (interEAN == null || interEAN.length() == 0) {
			interEAN = MEDIDATA_EAN;
		}
		txtIntermediaerEAN.setText(interEAN);

		try {
			props = MPCProperties.reload(installDir);
			if (props != null) {
				txtSenderEAN.setText(props.getProperty(ConfigKeys.SENDER_EAN));
				txtSenderDN.setText(props.getProperty(ConfigKeys.MEDIPORT_DN));
				String serverIp = props.getProperty(ConfigKeys.MEDIPORT_IP);
				if (VALUE_SERVER_URL_PRODUKTIV.equals(serverIp)) {
					cbServerIp.setText(LBL_SERVER_URL_PRODUKTIV);
				} else if (VALUE_SERVER_URL_TEST.equals(serverIp)) {
					cbServerIp.setText(LBL_SERVER_URL_TEST);
				} else {
					cbServerIp.setText(serverIp);
				}
			}
		} catch (IOException e) {
			Log.get(getClass().getName()).log(e.getMessage(), Log.WARNINGS);
		}

		installChanged = false;
	}

	private void installDirChanged() {
		if (installChanged && !overwriteRunning) {
			overwriteRunning = true;
			try {
				String installDir = txtInstallDir.getText();
				File installFile = new File(installDir);
				if (installFile.exists() && installFile.isDirectory()) {
					props = MPCProperties.reload(installDir);
					if (props != null) {
						boolean overwrite = false;
						String ean = props.getProperty(ConfigKeys.SENDER_EAN, ""); //$NON-NLS-1$
						String dn = props.getProperty(ConfigKeys.MEDIPORT_DN, ""); //$NON-NLS-1$
						String ip = props.getProperty(ConfigKeys.MEDIPORT_IP, ""); //$NON-NLS-1$
						if (!"".equals(txtSenderEAN.getText()) //$NON-NLS-1$
								|| !"".equals(txtSenderDN.getText()) //$NON-NLS-1$
								|| !"".equals(cbServerIp.getText())) { //$NON-NLS-1$
							if (!ean.equals(txtSenderEAN.getText()) || !dn.equals(txtSenderDN.getText())
									|| !ip.equals(cbServerIp.getText())) {

								String msg = MessageFormat.format(
										Messages.MediportMainPrefPage_question_msg_differentData, // $NON-NLS-1$
										new Object[] { props.getConfigFilenamePath() });
								if (MessageDialog.openQuestion(getShell(), msg,
										Messages.MediportMainPrefPage_question_msg_DatenUebernehmen)) { // $NON-NLS-1$
									overwrite = true;
								}
							}
						} else {
							overwrite = true;
						}
						if (overwrite) {
							if (txtIntermediaerEAN.getText() == null || txtIntermediaerEAN.getText().length() == 0) {
								txtIntermediaerEAN.setText(ean);
							}
							txtSenderEAN.setText(ean);
							txtSenderDN.setText(dn);
							cbServerIp.setText(ip);
						}
					}
				}
			} catch (IOException e1) {
				// Do nothing. Was just trying
			} finally {
				overwriteRunning = false;
				installChanged = false;
			}
		}
	}

	private void btnInitAbCDPressed() {
		FileDialog fileDialog = new FileDialog(getShell());
		fileDialog.setFilterExtensions(new String[] { "*.re", "*.*" });
		String filenamePath = fileDialog.open();
		if (filenamePath != null) {
			try {
				String content = UtilFile.readTextFile(filenamePath);
				int start = content.indexOf("uid="); //$NON-NLS-1$
				if (start >= 0) {
					int end = content.indexOf(",", start); //$NON-NLS-1$
					if (end < 0) {
						end = content.length() - 1;
					}
					String uid = content.substring(start + 4, end);
					if (uid.length() > 3 && uid.toUpperCase().startsWith("EAN")) { //$NON-NLS-1$
						uid = uid.substring(3);
					}
					log.log("INIT: Content = " + uid, Log.DEBUGMSG);
					log.log("INIT: UID = " + uid, Log.DEBUGMSG);
					txtSenderEAN.setText(uid);
					txtSenderDN.setText(content);

					String keystoreFilename = "EAN" + uid //$NON-NLS-1$
							+ "_mpg.keystore"; //$NON-NLS-1$
					File keystoreFile = new File(
							UtilFile.getFilepath(filenamePath) + File.separator + keystoreFilename);
					File toDir = new File(prefs.getString(MPC_INSTALL_DIR) + File.separator + "config");
					log.log("INIT: Check Keystorefile = " + keystoreFile.getCanonicalPath(), Log.DEBUGMSG);
					log.log("INIT: Check config path = " + toDir.getCanonicalPath(), Log.DEBUGMSG);
					String msg = "";
					if (!keystoreFile.exists()) {
						msg = Messages.MediportMainPrefPage_error_msg_InitAbCD_SrcNotFound
								+ keystoreFile.getCanonicalPath(); // $NON-NLS-1$
					}
					if (!toDir.exists()) {
						msg = Messages.MediportMainPrefPage_error_msg_InitAbCD_DstNotFound + toDir.getCanonicalPath(); // $NON-NLS-1$
					}
					if (keystoreFile.exists() && toDir.exists()) {
						log.log("INIT: Keystorefile gefunden", Log.DEBUGMSG);
						String toPath = toDir.getCanonicalPath() + File.separator // $NON-NLS-1$
								+ keystoreFilename;
						log.log("INIT: Keystorefile wird kopiert. Ziel: " + toPath, Log.DEBUGMSG);
						UtilFile.deleteFile(toPath + ".old");
						UtilFile.moveFile(toPath, toPath + ".old");
						UtilFile.copyFile(keystoreFile.getCanonicalPath(), toPath);
						if (new File(toPath).exists()) {
							log.log("INIT: Keystorefile wurde erfolgreich kopiert", Log.DEBUGMSG);
						} else {
							log.log("INIT: " + toPath + " existiert nicht...", Log.DEBUGMSG);
							MessageDialog.openError(getShell(), Messages.MediportMainPrefPage_error_title_InitAbCD,
									Messages.MediportMainPrefPage_error_msg_InitAbCD + "\r" + msg); //$NON-NLS-1$
						}
					} else {
						log.log("INIT: " + msg, Log.DEBUGMSG);
						MessageDialog.openError(getShell(), Messages.MediportMainPrefPage_error_title_InitAbCD,
								Messages.MediportMainPrefPage_error_msg_InitAbCD + "\r" + msg); //$NON-NLS-1$
					}
				} else {
					throw new IOException(Messages.MediportMainPrefPage_error_msg_Dateiformat); // $NON-NLS-1$
				}
			} catch (IOException ex) {
				ExHandler.handle(ex);
				MessageDialog.openError(getShell(), Messages.MediportMainPrefPage_error_title_InitAbCD,
						ex.getMessage()); // $NON-NLS-1$
			}
		}
	}

	@Override
	protected Control createContents(final Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		comp.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Label lblInstallDir = new Label(comp, SWT.NONE);
		lblInstallDir.setText(Messages.MediportMainPrefPage_lbl_Installationsverzeichnis); // $NON-NLS-1$
		txtInstallDir = new DirectoryText(comp, SWT.BORDER);
		txtInstallDir.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label lblServer = new Label(comp, SWT.NONE);
		lblServer.setText(Messages.MediportMainPrefPage_lbl_Uebermittlung); // $NON-NLS-1$
		cbServer = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
		cbServer.add(LBL_SERVER_PRODUCTION);
		cbServer.add(LBL_SERVER_TEST);
		cbServer.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));

		Label lblServerIp = new Label(comp, SWT.NONE);
		lblServerIp.setText(Messages.MediportMainPrefPage_lbl_MediportServerURL); // $NON-NLS-1$
		cbServerIp = new Combo(comp, SWT.BORDER);
		cbServerIp.add(LBL_SERVER_URL_PRODUKTIV);
		cbServerIp.add(LBL_SERVER_URL_TEST);
		cbServerIp.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));

		Label lblAusgabe = new Label(comp, SWT.NONE);
		lblAusgabe.setText(Messages.MediportMainPrefPage_lbl_Rechnungsausgabe); // $NON-NLS-1$
		cbAusgabe = new Combo(comp, SWT.BORDER | SWT.READ_ONLY);
		cbAusgabe.setLayoutData(SWTHelper.getFillGridData(2, false, 1, false));

		Label lblIntermediaerEAN = new Label(comp, SWT.NONE);
		lblIntermediaerEAN.setText(Messages.MediportMainPrefPage_lbl_IntermediaerEAN); // $NON-NLS-1$
		txtIntermediaerEAN = new Text(comp, SWT.BORDER);
		txtIntermediaerEAN.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));

		Group senderComp = new Group(comp, SWT.NONE);
		senderComp.setText(Messages.MediportMainPrefPage_mediport_Sender); // $NON-NLS-1$
		senderComp.setLayout(new GridLayout(2, false));
		senderComp.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));

		Label lblSenderEAN = new Label(senderComp, SWT.NONE);
		lblSenderEAN.setText(Messages.MediportMainPrefPage_mediport_EAN); // $NON-NLS-1$
		txtSenderEAN = new Text(senderComp, SWT.BORDER);
		txtSenderEAN.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		Label lblSenderDN = new Label(senderComp, SWT.NONE);
		lblSenderDN.setText(Messages.MediportMainPrefPage_mediport_DN); // $NON-NLS-1$
		txtSenderDN = new Text(senderComp, SWT.BORDER);
		txtSenderDN.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));

		btnInit = new Button(senderComp, SWT.PUSH);
		btnInit.setText(Messages.MediportMainPrefPage_btn_InitAbCD); // $NON-NLS-1$
		btnInit.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		// Events
		btnInit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				btnInitAbCDPressed();
			}
		});

		txtInstallDir.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				installDirChanged();
			}

			public void focusGained(FocusEvent e) {
				installDirChanged();
			}
		});

		txtInstallDir.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				installChanged = true;
			}
		});

		// Data
		List<IRnOutputter> lo = MediPortHelper.getRnOutputter();
		for (IRnOutputter ro : lo) {
			cbAusgabe.add(ro.getDescription());
		}
		cbAusgabe.select(0);

		cbServer.select(0);
		cbServerIp.select(0);

		fillData();

		return parent;
	}

	protected boolean storeAll() {
		putPrefString(MPC_INSTALL_DIR, this.txtInstallDir.getText());
		putPrefString(MPC_AUSGABE, this.cbAusgabe.getItem(this.cbAusgabe.getSelectionIndex()));
		putPrefString(MPC_SERVER, this.cbServer.getItem(this.cbServer.getSelectionIndex()));
		putPrefString(MPC_INTERMEDIAER_EAN, this.txtIntermediaerEAN.getText());
		storePrefs();

		String installDir = txtInstallDir.getText();
		if (installDir != null && installDir.length() > 0) {
			String configFilename = ""; //$NON-NLS-1$
			try {
				if (!"".equals(txtSenderEAN.getText()) //$NON-NLS-1$
						|| !"".equals(txtSenderDN.getText()) //$NON-NLS-1$
						|| !"".equals(cbServerIp.getText())) { //$NON-NLS-1$
					props = MPCProperties.reload(installDir);
					configFilename = props.getConfigFilenamePath();
					props.put(ConfigKeys.SENDER_EAN, this.txtSenderEAN.getText());
					props.put(ConfigKeys.MEDIPORT_DN, this.txtSenderDN.getText());
					String lblServerIp = this.cbServerIp.getText();
					if (LBL_SERVER_URL_PRODUKTIV.equals(lblServerIp)) {
						props.put(ConfigKeys.MEDIPORT_IP, VALUE_SERVER_URL_PRODUKTIV);
					} else if (LBL_SERVER_URL_TEST.equals(lblServerIp)) {
						props.put(ConfigKeys.MEDIPORT_IP, VALUE_SERVER_URL_TEST);
					} else {
						props.put(ConfigKeys.MEDIPORT_IP, lblServerIp);
					}
					props.put(ConfigKeys.KEYSTORE_NAME, "config/EAN" //$NON-NLS-1$
							+ txtSenderEAN.getText() + "_mpg.keystore"); //$NON-NLS-1$
					props.store();
				}
				installChanged = false;
			} catch (Exception e) {
				ExHandler.handle(e);
				String msg = MessageFormat.format(Messages.MediportMainPrefPage_error_title_SaveConfig, // $NON-NLS-1$
						new Object[] { configFilename });
				MessageDialog.openError(getShell(), msg, e.getMessage());
				return false;
			}
		}

		return true;
	}

	public void init(final IWorkbench workbench) {
		setMessage(Messages.MediportMainPrefPage_message); // $NON-NLS-1$
		setDescription(Messages.MediportMainPrefPage_description); // $NON-NLS-1$
	}

}
