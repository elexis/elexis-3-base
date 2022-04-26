package ch.elexis.connect.reflotron;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.connect.reflotron.packages.PackageException;
import ch.elexis.connect.reflotron.packages.Probe;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.serial.Connection;
import ch.elexis.core.serial.Connection.ComPortListener;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.rs232.SerialConnectionUi;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

public class ReflotronSprintAction extends Action implements ComPortListener {

	Connection _ctrl;
	Labor _myLab;
	DeviceLogger _rs232log;
	private Logger logger = LoggerFactory.getLogger("ReflotronSprintAction");
	Thread msgDialogThread;
	Patient selectedPatient;
	boolean background = false;

	public ReflotronSprintAction() {
		super(Messages.ReflotronSprintAction_ButtonName, AS_CHECK_BOX);
		setToolTipText(Messages.ReflotronSprintAction_ToolTip);
		setImageDescriptor(
				AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.connect.reflotron.v2", "icons/reflotron.png")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Init serial connection using {@link Connection}.
	 */
	private void initConnection() {
		if (_ctrl != null && _ctrl.isOpen()) {
			_ctrl.close();
		}
		_ctrl = new Connection(Messages.ReflotronSprintAction_ConnectionName,
				CoreHub.localCfg.get(Preferences.PORT, Messages.ReflotronSprintAction_DefaultPort),
				CoreHub.localCfg.get(Preferences.PARAMS, Messages.ReflotronSprintAction_DefaultParams), this)
						.withStartOfChunk(new byte[] { Connection.STX }).withEndOfChunk(new byte[] { Connection.ETX })
						.excludeDelimiters(true);

		if (CoreHub.localCfg.get(Preferences.LOG, "n").equalsIgnoreCase("y")) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				_rs232log = new DeviceLogger(System.getProperty("user.home") + File.separator + "elexis" //$NON-NLS-1$ //$NON-NLS-2$
						+ File.separator + "reflotron.log"); //$NON-NLS-1$
			} catch (FileNotFoundException e) {
				SWTHelper.showError(Messages.ReflotronSprintAction_LogError_Title,
						Messages.ReflotronSprintAction_LogError_Text);
				_rs232log = new DeviceLogger();
			}
		} else {
			_rs232log = new DeviceLogger(false);
		}

		background = CoreHub.localCfg.get(Preferences.BACKGROUND, "n").equalsIgnoreCase("y");
	}

	@Override
	public void run() {
		if (isChecked()) {
			initConnection();
			_rs232log.logStart();
			if (_ctrl.connect()) {
				String timeoutStr = CoreHub.localCfg.get(Preferences.TIMEOUT,
						Messages.ReflotronSprintAction_DefaultTimeout);
				int timeout = 20;
				try {
					timeout = Integer.parseInt(timeoutStr);
				} catch (NumberFormatException e) {
					// Do nothing. Use default value
				}
				SerialConnectionUi.awaitFrame(_ctrl, UiDesk.getTopShell(), Messages.ReflotronSprintAction_WaitMsg,
						timeout, background, true);
				return;
			} else {
				_rs232log.log("Error"); //$NON-NLS-1$
				SWTHelper.showError(Messages.ReflotronSprintAction_RS232_Error_Title,
						Messages.ReflotronSprintAction_RS232_Error_Text);
			}
		} else {
			if (_ctrl.isOpen()) {
				_ctrl.sendBreak();
				_ctrl.close();
			}
		}
		setChecked(false);
		_rs232log.logEnd();
	}

	/**
	 * Eine Standard-Fehlermeldung asynchron im UI-Thread zeigen
	 */
	private static void showError(final String title, final String message) {
		UiDesk.getDisplay().asyncExec(new Runnable() {

			public void run() {
				Shell shell = UiDesk.getTopShell();
				MessageDialog.openError(shell, title, message);
			}
		});
	}

	/**
	 * Erstes Zeichen wird Uppercase gemacht
	 */
	private String getFirstUpper(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		String retStr = str.toUpperCase();
		if (str.length() > 1) {
			retStr = str.substring(0, 1).toUpperCase() + str.substring(1).trim();
		}
		return retStr;
	}

	/**
	 * Einzelne Probe wird verarbeitet
	 *
	 * @param probe
	 */
	private void processProbe(final Probe probe) {
		UiDesk.getDisplay().syncExec(new Runnable() {

			public void run() {
				selectedPatient = ElexisEventDispatcher.getSelectedPatient();
				Patient probePat = null;
				// TODO: Filter fuer KontaktSelektor
				String vorname = null;
				String name = null;
				String patientElexisStr = Messages.ReflotronSprintAction_UnknownPatientHeaderString;
				String patientDeviceStr = probe.getIdent();
				Long patId = null;
				if (patientDeviceStr != null) {
					String patName = probe.getIdent();
					String text = MessageFormat.format("patName={0}; resultat={1}; hint={2}, zusatztext={3}", patName,
							probe.getResultat(), probe.getHint(), probe.getZusatztext());
					System.out.println(text);
					logger.info(text);

					// Suchkriterium für Patientenzuordnung
					Query<Patient> patQuery = new Query<Patient>(Patient.class);
					if (patName != null && patName.length() > 0) {
						String[] parts = patName.split(","); //$NON-NLS-1$
						if (parts.length > 1) {
							try {
								patId = new Long(parts[0]);
							} catch (NumberFormatException e) {
								// Do nothing
							}

							if (patId != null) { // PatId, Name
								name = getFirstUpper(parts[1]);
							} else { // Name, Vorname
								name = getFirstUpper(parts[0]);
								vorname = getFirstUpper(parts[1]);
								patQuery.add(Patient.FLD_FIRSTNAME, "like", vorname + "%"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						} else if (parts.length == 1) {
							name = getFirstUpper(parts[0]);
						}

						if (patId != null) {
							patQuery.add(Patient.FLD_PATID, "=", patId.toString()); //$NON-NLS-1$
						}

						if (name != null && name.length() > 0) {
							patQuery.add(Patient.FLD_NAME, "like", name + "%"); //$NON-NLS-1$ //$NON-NLS-2$
						}
						if (vorname != null && vorname.length() > 0) {
							patQuery.add(Patient.FLD_FIRSTNAME, "like", vorname + "%"); //$NON-NLS-1$ //$NON-NLS-2$
						}

						List<Patient> patientList = patQuery.execute();

						if (patientList.size() == 1) {
							probePat = patientList.get(0);
							patientDeviceStr = probe.getIdent();
							patientElexisStr = probePat.getName() + " " + probePat.getVorname();
						}
					}
				}

				if ((patientDeviceStr == null) || (patientDeviceStr.equals(""))) {
					patientDeviceStr = Messages.ReflotronSprintAction_NoPatientInfo;
				}
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
				String warning = ""; // derzeit keine
				String text = MessageFormat.format(Messages.ReflotronSprintAction_ValueInfoMsg, patientDeviceStr,
						patientElexisStr, sdf.format(probe.getDate().getTime()), probe.getResultat(), warning);

				boolean ok = MessageDialog.openConfirm(UiDesk.getTopShell(), Messages.ReflotronSprintAction_DeviceName,
						text);
				if (ok) {
					boolean showSelectionDialog = false;
					if (probePat != null) {
						selectedPatient = probePat;
					} else {
						showSelectionDialog = true;
					}

					if (showSelectionDialog) {
						UiDesk.getDisplay().syncExec(new Runnable() {
							public void run() {
								// TODO: Filter vorname/name in KontaktSelektor
								// einbauen
								KontaktSelektor ksl = new KontaktSelektor(Hub.getActiveShell(), Patient.class,
										Messages.ReflotronSprintAction_Patient_Title,
										Messages.ReflotronSprintAction_Patient_Text, Patient.DEFAULT_SORT);
								ksl.create();
								ksl.getShell().setText(Messages.ReflotronSprintAction_Patient_Title);
								if (ksl.open() == org.eclipse.jface.dialogs.Dialog.OK) {
									selectedPatient = (Patient) ksl.getSelection();
								} else {
									selectedPatient = null;
								}

							}
						});
					}
					if (selectedPatient != null) {
						try {
							String warnMsg = probe.write(selectedPatient);
							if (warnMsg != null && warnMsg.length() != 0) {
								_rs232log.log("Warn: " + warnMsg);
							}
						} catch (PackageException e) {
							showError(Messages.ReflotronSprintAction_ProbeError_Title, e.getMessage());
						}
					} else {
						showError(Messages.ReflotronSprintAction_Patient_Title,
								Messages.ReflotronSprintAction_NoPatientMsg);
					}
				}
			}
		});
	}

	@Override
	public void gotData(Connection conn, byte[] data) {
		String encoding = CoreHub.localCfg.get(Preferences.ENCODING, Charset.defaultCharset().displayName());
		String content = null;
		try {
			content = new String(data, encoding);
		} catch (UnsupportedEncodingException e) {
			showError("Reflotron", MessageFormat.format("Encoding ''{0}'' unbekannt", encoding));
		}

		if (content != null) {
			_rs232log.logRX(content);
			String[] strArray = content.split("\r\n"); //$NON-NLS-1$
			if (strArray.length > 3) {
				Probe probe = new Probe(strArray);
				processProbe(probe);
			} else {
				if (content.length() > 0) {
					showError("Reflotron", //$NON-NLS-1$
							Messages.ReflotronSprintAction_IncompleteDataRecordMsg + content
									+ Messages.ReflotronSprintAction_ResendMsg);
				}
			}
			_rs232log.log("Saved"); //$NON-NLS-1$
		}

		ElexisEventDispatcher.reload(LabItem.class);
	}

	/**
	 * Verbindung zu serieller Schnittstelle wurde getrennt
	 */
	public void closed() {
		_rs232log.log("Closed"); //$NON-NLS-1$
		setChecked(false);
		_rs232log.logEnd();
	}
}
