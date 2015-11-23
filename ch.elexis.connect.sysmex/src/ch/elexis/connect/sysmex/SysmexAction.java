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

import ch.elexis.connect.sysmex.packages.AbstractData;
import ch.elexis.connect.sysmex.packages.KX21Data;
import ch.elexis.connect.sysmex.packages.KX21NData;
import ch.elexis.connect.sysmex.packages.PackageException;
import ch.elexis.connect.sysmex.packages.PocH100iData;
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
		super(Messages.getString("SysmexAction.ButtonName"), AS_CHECK_BOX); //$NON-NLS-1$
		setToolTipText(Messages.getString("SysmexAction.ToolTip")); //$NON-NLS-1$
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
			new SysmexConnection(Messages.getString("SysmexAction.ConnectionName"), //$NON-NLS-1$
				CoreHub.localCfg.get(Preferences.PORT,
					Messages.getString("SysmexAction.DefaultPort")), CoreHub.localCfg.get( //$NON-NLS-1$
					Preferences.PARAMS, Messages.getString("SysmexAction.DefaultParams")), //$NON-NLS-1$
				this);
	}
	
	private void initPreferences(){
		if (CoreHub.localCfg.get(Preferences.LOG, "n").equalsIgnoreCase("y")) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				_rs232log = new Logger(System.getProperty("user.home") + File.separator + "elexis" //$NON-NLS-1$ //$NON-NLS-2$
					+ File.separator + "sysmex.log"); //$NON-NLS-1$
			} catch (FileNotFoundException e) {
				SWTHelper.showError(Messages.getString("SysmexAction.LogError.Title"), //$NON-NLS-1$
					Messages.getString("SysmexAction.LogError.Text")); //$NON-NLS-1$
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
							Messages.getString("SysmexAction.DefaultTimeout")); //$NON-NLS-1$
					int timeout = 20;
					try {
						timeout = Integer.parseInt(timeoutStr);
					} catch (NumberFormatException e) {
						// Do nothing. Use default value
					}
					_ctrl
						.awaitFrame(
							UiDesk.getTopShell(),
							Messages.getString("SysmexAction.WaitMsg"), 1, 4, 0, timeout, background, true); //$NON-NLS-1$
					return;
				} else {
					_rs232log.log("Error"); //$NON-NLS-1$
					SWTHelper.showError(Messages.getString("SysmexAction.RS232.Error.Title"), //$NON-NLS-1$
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
		SWTHelper.showError(Messages.getString("SysmexAction.RS232.Break.Title"), Messages //$NON-NLS-1$
			.getString("SysmexAction.RS232.Break.Text")); //$NON-NLS-1$
	}
	
	/**
	 * Einzelne Probe wird verarbeitet
	 * 
	 * @param probe
	 */
	private void processProbe(final AbstractData probe){
		UiDesk.getDisplay().syncExec(new Runnable() {
			
			public void run(){
				UiDesk.getDisplay().syncExec(new Runnable() {
					public void run(){
						Patient suggestedPatient = findSuggestedPatient(probe.getPatientId());
						// only open selection dialog if there is a suggestion available
						if (suggestedPatient != null) {
							WhichPatientDialog wpDialog =
								new WhichPatientDialog(UiDesk.getTopShell(), suggestedPatient);
							wpDialog.open();
							selectedPatient = wpDialog.getPatient();
						}
						
						// case no patient selection was orcould be made yet
						if (selectedPatient == null) {
							KontaktSelektor ksl = new KontaktSelektor(Hub.getActiveShell(),
								Patient.class, Messages.getString("SysmexAction.Patient.Title"), //$NON-NLS-1$
								Messages.getString("SysmexAction.Patient.Text"), //$NON-NLS-1$
								Patient.DEFAULT_SORT);
							ksl.create();
							ksl.getShell()
								.setText(Messages.getString("SysmexAction.Patient.Title")); //$NON-NLS-1$
								
							if (ksl.open() == org.eclipse.jface.dialogs.Dialog.OK) {
								selectedPatient = (Patient) ksl.getSelection();
							} else {
								selectedPatient = null;
							}
						}
					}
				});
				if (selectedPatient != null) {
					try {
						probe.write(selectedPatient);
					} catch (PackageException e) {
						showError(
							Messages.getString("SysmexAction.ProbeError.Title"), e.getMessage()); //$NON-NLS-1$
					}
				} else {
					showError(Messages.getString("SysmexAction.Patient.Title"), //$NON-NLS-1$
						Messages.getString("SysmexAction.NoPatientMsg")); //$NON-NLS-1$
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
		
		AbstractData analysisData = null;
		String model = CoreHub.localCfg.get(Preferences.MODEL, Preferences.MODEL_KX21);
		if (Preferences.MODEL_KX21N.equals(model)) {
			analysisData = new KX21NData();
		} else if (Preferences.MODEL_POCH.equals(model)) {
			analysisData = new PocH100iData();
		} else {
			analysisData = new KX21Data();
		}
		
		if (content.length() == analysisData.getSize()) {
			analysisData.parse(content);
			processProbe(analysisData);
		} else {
			showError(Messages.getString("SysmexAction.ErrorTitle"), //$NON-NLS-1$
				Messages.getString("SysmexAction.WrongDataFormat")); //$NON-NLS-1$
			
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
		SWTHelper.showError(Messages.getString("SysmexAction.RS232.Timeout.Title"), //$NON-NLS-1$
			Messages.getString("SysmexAction.RS232.Timeout.Text")); //$NON-NLS-1$
		close();
	}
}
