package ch.elexis.connect.sysmex;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.LoggerFactory;

import ch.elexis.connect.sysmex.packages.IProbe;
import ch.elexis.connect.sysmex.packages.KX21Data;
import ch.elexis.connect.sysmex.packages.KX21NData;
import ch.elexis.connect.sysmex.packages.PackageException;
import ch.elexis.connect.sysmex.packages.PocH100iData;
import ch.elexis.connect.sysmex.packages.UC1000Data;
import ch.elexis.connect.sysmex.ui.Preferences;
import ch.elexis.connect.sysmex.ui.WhichPatientDialog;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.rs232.AbstractConnection;
import ch.elexis.core.ui.importer.div.rs232.AbstractConnection.ComPortListener;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;

public class SysmexAction extends Action implements ComPortListener {
	
	AbstractConnection _ctrl;
	Labor _myLab;
	Logger _rs232log;
	Log _elexislog = Log.get("SysmexAction"); //$NON-NLS-1$
	Thread msgDialogThread;
	Patient selectedPatient;
	boolean background = false;
	
	private ShutdownThread shutdownThread = null;
	
	// Declare filename to the log for test only!! for production must be null!
	String simulate = null; // "C:\\tmp\\sysmex.log";
	
	private class ShutdownThread extends Thread {
		private boolean shouldShutdown = false;
		
		public void startSequence(){
			_elexislog.log("Start sysmex shutdown sequence", Log.DEBUGMSG);
			shouldShutdown = true;
		}
		
		public void stopSequence(){
			_elexislog.log("Stop sysmex shutdown sequence", Log.DEBUGMSG);
			shouldShutdown = false;
		}
		
		public void run(){
			try {
				while (true) {
					_elexislog.log("Waiting for sysmex shutdown..", Log.DEBUGMSG);
					while (!shouldShutdown) {
						// Wait till sequence started
					}
					_elexislog.log("Sysmex shutdown sequence started (5 sec)..", Log.DEBUGMSG);
					Thread.sleep(5000);
					_elexislog.log("Sysmex shutdown sequence over. Should shutdown sysmex="
						+ shouldShutdown, Log.DEBUGMSG);
					if (shouldShutdown) {
						_elexislog.log("Shutdown", Log.INFOS); //$NON-NLS-1$
						close();
					}
				}
			} catch (Exception ex) {
				// Do nothing
			}
		}
	};
	
	public SysmexAction(){
		super(Messages.SysmexAction_ButtonName, AS_CHECK_BOX);
		setToolTipText(Messages.SysmexAction_ToolTip);
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
			"ch.elexis.connect.sysmex", "icons/sysmex.png")); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Serielle Verbindung wird initialisiert
	 */
	private void initConnection(){
		if (_ctrl != null && _ctrl.isOpen()) {
			_ctrl.close();
		}
		_ctrl =
			new SysmexConnection(Messages.SysmexAction_ConnectionName,
				CoreHub.localCfg.get(Preferences.PORT,
					Messages.SysmexAction_DefaultPort), CoreHub.localCfg.get(
					Preferences.PARAMS, Messages.SysmexAction_DefaultParams),
				this);
	}
	
	private void initPreferences(){
		if (CoreHub.localCfg.get(Preferences.LOG, "n").equalsIgnoreCase("y")) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				_rs232log = new Logger(System.getProperty("user.home") + File.separator + "elexis" //$NON-NLS-1$ //$NON-NLS-2$
					+ File.separator + "sysmex.log"); //$NON-NLS-1$
			} catch (FileNotFoundException e) {
				SWTHelper.showError(Messages.SysmexAction_LogError_Title,
					Messages.SysmexAction_LogError_Text);
				_rs232log = new Logger();
			}
		} else {
			_rs232log = new Logger(false);
		}
		background = CoreHub.localCfg.get(Preferences.BACKGROUND, "n") //$NON-NLS-1$
			.equalsIgnoreCase("y"); //$NON-NLS-1$
	}
	
	@Override
	public void run(){
		if (isChecked()) {
			initPreferences();
			if (simulate == null) {
				initConnection();
				String msg = _ctrl.connect();
				if (msg == null) {
					String timeoutStr =
						CoreHub.localCfg.get(Preferences.TIMEOUT,
							Messages.SysmexAction_DefaultTimeout);
					int timeout = 20;
					try {
						timeout = Integer.parseInt(timeoutStr);
					} catch (NumberFormatException e) {
						// Do nothing. Use default value
					}
					_ctrl
						.awaitFrame(
							UiDesk.getTopShell(),
							Messages.SysmexAction_WaitMsg, 1, 4, 0, timeout, background, true);
					return;
				} else {
					_rs232log.log("Error"); //$NON-NLS-1$
					SWTHelper.showError(Messages.SysmexAction_RS232_Error_Title,
						msg);
				}
			} else {
				SWTHelper.showInfo("Simulating!!!", simulate);
				// test only
				FileInputStream inputStream = null;
				try {
					inputStream = new FileInputStream(simulate);
					int test = inputStream.read();
					while (test != -1) {
						if (test == Logger.STX) {
							test = inputStream.read();
						}
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						while (test != -1 && test != Logger.ETX) {
							baos.write(test);
							test = inputStream.read();
						}
						byte[] data = baos.toByteArray();
						gotData(null, data);
						while (test != -1 && test != Logger.STX) {
							test = inputStream.read();
						}
					}
				} catch (FileNotFoundException fne) {
					fne.printStackTrace();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			if (_ctrl.isOpen()) {
				_ctrl.sendBreak();
				_ctrl.close();
			}
		}
		setChecked(false);
	}
	
	/**
	 * Eine Standard-Fehlermeldung asynchron im UI-Thread zeigen
	 */
	private static void showError(final String title, final String message){
		UiDesk.getDisplay().asyncExec(new Runnable() {
			
			public void run(){
				Shell shell = UiDesk.getTopShell();
				MessageDialog.openError(shell, title, message);
			}
		});
	}
	
	/**
	 * Unterbruche wird von serieller Schnittstelle geschickt.
	 */
	public void gotBreak(final AbstractConnection connection){
		connection.close();
		setChecked(false);
		_elexislog.log("Break", Log.INFOS); //$NON-NLS-1$
		SWTHelper.showError(Messages.SysmexAction_RS232_Break_Title, Messages
			.SysmexAction_RS232_Break_Text);
	}
	
	/**
	 * Einzelne Probe wird verarbeitet
	 * 
	 * @param probe
	 */
	private void processProbe(final IProbe probe){
		UiDesk.getDisplay().syncExec(new Runnable() {
			
			public void run(){
				UiDesk.getDisplay().syncExec(new Runnable() {
					public void run(){
						// perform direct import if patient with matching patient id is found
						selectedPatient = Patient.loadByPatientID(probe.getPatientId());
						LoggerFactory.getLogger(getClass()).info("Found patient [" + selectedPatient
							+ "] for id [" + probe.getPatientId() + "]");
						if (selectedPatient == null || !selectedPatient.exists()) {
							Patient suggestedPatient = findSuggestedPatient(probe.getPatientId());
							// only open selection dialog if there is a suggestion available
							if (suggestedPatient != null) {
								WhichPatientDialog wpDialog =
									new WhichPatientDialog(UiDesk.getTopShell(), suggestedPatient);
								wpDialog.open();
								selectedPatient = wpDialog.getPatient();
							}
						}
						// case no patient selection could be made yet
						if (selectedPatient == null || !selectedPatient.exists()) {
							KontaktSelektor ksl = new KontaktSelektor(Hub.getActiveShell(),
								Patient.class, Messages.SysmexAction_Patient_Title,
								Messages.SysmexAction_Patient_Text,
								Patient.DEFAULT_SORT);
							ksl.create();
							ksl.getShell()
								.setText(Messages.SysmexAction_Patient_Title);
								
							if (ksl.open() == org.eclipse.jface.dialogs.Dialog.OK) {
								selectedPatient = (Patient) ksl.getSelection();
							} else {
								selectedPatient = null;
							}
						}
					}
				});
				if (selectedPatient != null && selectedPatient.exists()) {
					try {
						probe.write(selectedPatient);
					} catch (PackageException e) {
						showError(
							Messages.SysmexAction_ProbeError_Title, e.getMessage());
					}
				} else {
					showError(Messages.SysmexAction_Patient_Title,
						Messages.SysmexAction_NoPatientMsg);
				}
			}
		});
	}
	
	/**
	 * Finds potential suggestion of a patient.<br>
	 * Tries to resolve Sysmex sent patient number first and alternatively tries to resolve
	 * currently selected patient.
	 * 
	 * @param sysmexPatId
	 * @return The patient sent via the Sysmex device. If SysmexPatient is can't be resolved the
	 *         currently selected patient is used. NULL if neither SysmexPatient or ActivePatient
	 *         could be resolved.
	 */
	private Patient findSuggestedPatient(String sysmexPatId){
		if (sysmexPatId == null || sysmexPatId.isEmpty()) {
			return ElexisEventDispatcher.getSelectedPatient();
		}
		
		Patient suggestedPatient = Patient.loadByPatientID(sysmexPatId);
		if (suggestedPatient == null) {
			return ElexisEventDispatcher.getSelectedPatient();
		}
		return suggestedPatient;
	}
	
	/**
	 * Daten werden von der Seriellen Schnittstelle geliefert
	 */
	public void gotData(final AbstractConnection connection, final byte[] data){
		stopShutdownSequence();
		
		String content = new String(data);
		if (connection != null) {
			_rs232log.logSTX();
			_rs232log.log(content);
		}
		
		IProbe analysisProbe = null;
		String model = CoreHub.localCfg.get(Preferences.MODEL, Preferences.MODEL_KX21);
		if (Preferences.MODEL_KX21N.equals(model)) {
			analysisProbe = new KX21NData();
		} else if (Preferences.MODEL_POCH.equals(model)) {
			analysisProbe = new PocH100iData();
		} else if (Preferences.MODEL_UC1000.equals(model)) {
			analysisProbe = new UC1000Data();
		} else {
			analysisProbe = new KX21Data();
		}
		
		if (content.length() == analysisProbe.getSize()) {
			analysisProbe.parse(content);
			processProbe(analysisProbe);
		} else {
			showError(Messages.SysmexAction_ErrorTitle,
				Messages.SysmexAction_WrongDataFormat);
			
		}
		
		if (connection != null) {
			_rs232log.logETX();
		}
		ElexisEventDispatcher.reload(LabItem.class);
		
		boolean background =
			CoreHub.localCfg.get(Preferences.BACKGROUND, "n").equalsIgnoreCase("y");
		if (!background) {
			startShutdownSequence();
		}
	}
	
	private void stopShutdownSequence(){
		if (shutdownThread != null) {
			shutdownThread.stopSequence();
		}
	}
	
	private void startShutdownSequence(){
		if (shutdownThread == null) {
			shutdownThread = new ShutdownThread();
			shutdownThread.setPriority(Thread.MIN_PRIORITY);
			shutdownThread.start();
		}
		shutdownThread.startSequence();
	}
	
	private void close(){
		if (shutdownThread != null) {
			shutdownThread.interrupt();
			shutdownThread = null;
		}
		_ctrl.close();
		setChecked(false);
	}
	
	/**
	 * Verbindung zu serieller Schnittstelle wurde getrennt
	 */
	public void closed(){
		_elexislog.log("Closed", Log.INFOS); //$NON-NLS-1$
		close();
	}
	
	/**
	 * Verbindung zu serieller Schnittstelle wurde vom Benutzer abgebrochen
	 */
	public void cancelled(){
		_elexislog.log("Cancelled", Log.INFOS); //$NON-NLS-1$
		close();
	}
	
	/**
	 * Verbindung zu serieller Schnittstelle hat timeout erreicht.
	 */
	public void timeout(){
		_elexislog.log("Timeout", Log.INFOS); //$NON-NLS-1$
		SWTHelper.showError(Messages.SysmexAction_RS232_Timeout_Title,
			Messages.SysmexAction_RS232_Timeout_Text);
		close();
	}
}
