package ch.elexis.connect.afinion;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.connect.afinion.packages.PackageException;
import ch.elexis.connect.afinion.packages.Record;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.serial.Connection;
import ch.elexis.core.serial.Connection.ComPortListener;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.rs232.SerialConnectionUi;
import ch.elexis.core.ui.util.Log;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;

public class AfinionAS100Action extends Action implements ComPortListener {
	
	AfinionConnection _ctrl;
	Labor _myLab;
	Thread msgDialogThread;
	Thread infoDialogThread;
	Patient selectedPatient;
	Logger _rs232log;
	Log _elexislog = Log.get("AfinionAS100Action");
	Record lastRecord = null;
	boolean background;
	int debugRecord = 0; // test only!! for production must be 0!
	String simulate = null; // "C:\\Temp\\Afinion\\afinion.log"; // declare filename to the log for
	
	// test only!! for production must be null!
	
	// "c:/temp/afinion/test.log"; oder null
	// Anweisung zum Logfile:
	// Wenn das Häckchen "Logging" in der Konfiguration eingeschaltet ist, werden die empfangenen
	// Resultate vom Afinion Gerät in ein Logfile geschrieben: C:\Users\tony\elexis\afinion.log
	// Dieses Logfile enthält pro Verbindung eine Section:
	// -S- "'08.07.2009, 21:51:38'" == Section Start
	// -E- "'08.07.2009, 21:52:11'" == Section Ende
	// Dazwischen werden die erhaltenen Daten mit <-- "..." geloggt.
	// Für die Simulation muss der Bereich zwischen den Anführungszeichen aus <-- "..." in einem
	// eigenständigen Logfile vorliegen. Die Binären Daten müssen dabei unverändert übernommen
	// werden.
	// Tipp: Logfile kopieren, mit Ultradedit in der HEX-Darstellung editieren und überzählige
	// Zeichen löschen (Ausschneiden). Beim speichern sollte es dann i.O. sein.
	// Kontrolle: Die Dateigrösse sollte 2568 Bytes sein (immer 10 Resultate à 256 Bytes plus 8
	// Bytes footer)
	
	public AfinionAS100Action(){
		super(Messages.AfinionAS100Action_ButtonName, AS_CHECK_BOX); //$NON-NLS-1$
		setToolTipText(Messages.AfinionAS100Action_ToolTip); //$NON-NLS-1$
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.connect.afinion", //$NON-NLS-1$
			"icons/afinion.png")); //$NON-NLS-1$
	}
	
	private void initConnection(){
		if (_ctrl != null && _ctrl.isOpen()) {
			_ctrl.close();
		}
		_ctrl =
			(AfinionConnection) new AfinionConnection(Messages.AfinionAS100Action_ConnectionName, //$NON-NLS-1$
				CoreHub.localCfg.get(Preferences.PORT,
					Messages.AfinionAS100Action_DefaultPort), CoreHub.localCfg.get( //$NON-NLS-1$
					Preferences.PARAMS, Messages.AfinionAS100Action_DefaultParams), //$NON-NLS-1$
				this).withStartOfChunk(
					new byte[] {
						AfinionConnection.DLE, AfinionConnection.STX
					}).withEndOfChunk(new byte[] {
						AfinionConnection.DLE, AfinionConnection.ETX
			}).excludeDelimiters(false);
		
		Calendar cal = new GregorianCalendar();
		if (debugRecord != 0) {
			SWTHelper.showInfo("Debugging!!!", "Record " + debugRecord); //$NON-NLS-2$
			// beim debuggen wollen wir alle Records sehen!
			cal.add(Calendar.DATE, -365);
		}
		cal.add(Calendar.HOUR, -cal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		_ctrl.setCurrentDate(cal);
		
		if (CoreHub.localCfg.get(Preferences.LOG, "n").equalsIgnoreCase("y")) { //$NON-NLS-1$ //$NON-NLS-2$
			try {
				_rs232log = new Logger(System.getProperty("user.home") + File.separator + "elexis" //$NON-NLS-1$ //$NON-NLS-2$
					+ File.separator + "afinion.log"); //$NON-NLS-1$
			} catch (FileNotFoundException e) {
				SWTHelper.showError(Messages.AfinionAS100Action_LogError_Title,
					Messages.AfinionAS100Action_LogError_Text);
				_rs232log = new Logger();
			}
		} else {
			_rs232log = new Logger(false);
		}
		
		background = CoreHub.localCfg.get(Preferences.BACKGROUND, "n").equalsIgnoreCase("y");
	}
	
	@Override
	public void run(){
		if (isChecked()) {
			if (simulate == null) {
				initConnection();
				_rs232log.logStart();
				if (_ctrl.connect()) {
					String timeoutStr =
						CoreHub.localCfg.get(Preferences.TIMEOUT,
							Messages.AfinionAS100Action_DefaultTimeout); //$NON-NLS-1$
					int timeout = 20;
					try {
						timeout = Integer.parseInt(timeoutStr);
					} catch (NumberFormatException e) {
						// Do nothing. Use default value
					}
					SerialConnectionUi.awaitFrame(_ctrl,
							UiDesk.getTopShell(),
						Messages.AfinionAS100Action_WaitMsg, timeout, background, false); //$NON-NLS-1$
					return;
				} else {
					_rs232log.log("Error"); //$NON-NLS-1$
					SWTHelper.showError(
						Messages.AfinionAS100Action_RS232_Error_Title,
						"Konnte seriellen Port nicht öffnen"); //$NON-NLS-1$
				}
			} else {
				SWTHelper.showInfo("Simulating!!!", simulate);
				// test only
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					FileInputStream inputStream = new FileInputStream(simulate);
					int test = inputStream.read();
					int ETX = 0x03;
					int DLE = 0x10;
					while (test != -1 && test != ETX) {
						while (test != -1 && test != DLE) {
							baos.write(test);
							test = inputStream.read();
						}
						test = inputStream.read();
					}
					
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				byte[] data = baos.toByteArray();
				gotData(null, data);
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
	
	public void gotBreak(final Connection connection){
		connection.close();
		setChecked(false);
		_rs232log.log("Break"); //$NON-NLS-1$
		_rs232log.logEnd();
		SWTHelper.showError(Messages.AfinionAS100Action_RS232_Break_Title, Messages.AfinionAS100Action_RS232_Break_Text);
	}
	
	/**
	 * Liest Bytes aus einem Bytearray
	 */
	private byte[] subBytes(final byte[] bytes, final int pos, final int length){
		byte[] retVal = new byte[length];
		for (int i = 0; i < length; i++) {
			retVal[i] = bytes[pos + i];
		}
		return retVal;
	}
	
	/**
	 * Einzelner Messwert wird verarbeitet
	 * 
	 * @param probe
	 */
	private void processRecord(final Record record){
		UiDesk.getDisplay().syncExec(new Runnable() {
			
			public void run(){
				selectedPatient = ElexisEventDispatcher.getSelectedPatient();
				Patient probePat = null;
				String vorname = null;
				String name = null;
				String patientElexisStr =
					Messages.AfinionAS100Action_UnknownPatientHeaderString;
				String patientDeviceStr = record.getId();
				Long patId = null;
				
				if (patientDeviceStr != null) {
					
					// Suchkriterium für Patientenzuordnung
					Query<Patient> patQuery = new Query<Patient>(Patient.class);
					if (patientDeviceStr != null && patientDeviceStr.length() > 0) {
						String[] parts = patientDeviceStr.split(","); //$NON-NLS-1$
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
							try {
								patId = new Long(patientDeviceStr);
							} catch (NumberFormatException e) {
								// Do nothing
							}
							if (patId == null) { // PatId, Name
								name = getFirstUpper(patientDeviceStr);
							}
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
							patientDeviceStr = record.getId();
							patientElexisStr = probePat.getName() + " " + probePat.getVorname();
						}
					}
				}
				
				if ((patientDeviceStr == null) || (patientDeviceStr.equals(""))) {
					patientDeviceStr = Messages.AfinionAS100Action_NoPatientInfo;
				}
				String warning = "";
				// Gemäss Mail von Frau Rytz (Axis-Shield) vom 3.8.09: Anstelle der Warnung soll der
				// Wert als <min
				// oder >max angezeigt werden
				// Wurde in SubRecordPart.getResultStr implementiert
				// if (record.isOutOfRange()) {
				// warning = Messages.AfinionAS100Action_ValueOutOfRangeWarning;
				// }
				String text =
					MessageFormat.format(
						Messages.AfinionAS100Action_ValueInfoMsg, patientDeviceStr, patientElexisStr, record.getRunNr(), record //$NON-NLS-1$
							.getText(), warning);
				
				boolean ok =
					MessageDialog.openConfirm(UiDesk.getTopShell(),
						Messages.AfinionAS100Action_DeviceName, text); //$NON-NLS-1$
				if (ok) {
					boolean showSelectionDialog = false;
					if (probePat != null) {
						selectedPatient = probePat;
					} else {
						showSelectionDialog = true;
					}
					
					if (showSelectionDialog) {
						UiDesk.getDisplay().syncExec(new Runnable() {
							public void run(){
								// TODO: Filter vorname/name in KontaktSelektor einbauen
								KontaktSelektor ksl =
									new KontaktSelektor(
										Hub.getActiveShell(),
										Patient.class,
										Messages.AfinionAS100Action_Patient_Title, //$NON-NLS-1$
										Messages.AfinionAS100Action_Patient_Text, Patient.DEFAULT_SORT); //$NON-NLS-1$
								ksl.create();
								ksl.getShell().setText(
									Messages.AfinionAS100Action_Patient_Title); //$NON-NLS-1$
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
							record.write(selectedPatient);
						} catch (PackageException e) {
							SWTHelper.showError(
								Messages.AfinionAS100Action_ProbeError_Title, e.getMessage());
						}
					} else {
						SWTHelper.showError(Messages.AfinionAS100Action_Patient_Title, //$NON-NLS-1$
							Messages.AfinionAS100Action_NoPatientSelectedMsg); //$NON-NLS-1$
					}
					_rs232log.log("Saved"); //$NON-NLS-1$
					ElexisEventDispatcher.reload(LabItem.class);
				}
			}
		});
	}
	
	/**
	 * Messagedaten von Afinion wurden gelesen
	 */
	public void gotData(final Connection connection, final byte[] data){
		if (_rs232log != null) {
			_rs232log.logRX(new String(data));
		}
		
		// Record lesen
		int pos = 0;
		int i = 0;
		int validRecords = 0;
		while (i < 10) {
			byte[] subbytes = subBytes(data, pos, 256);
			Record tmpRecord = new Record(subbytes);
			String text = tmpRecord.toString();
			System.out.println("DEBUG: " + text);
			_elexislog.log(text, Log.DEBUGMSG);
			if (tmpRecord.isValid()) {
				if (debugRecord != 0) {
					// lets debug that given record
					if (tmpRecord.getRecordNum() <= debugRecord) {
						lastRecord = tmpRecord;
					}
					if (tmpRecord.getRecordNum() == debugRecord) {
						validRecords = 0;
					}
				} else {
					lastRecord = tmpRecord;
				}
				text = lastRecord.toString();
				System.out.println(text);
				_elexislog.log(text, Log.INFOS);
				validRecords++;
			}
			pos += 256;
			i++;
		}
		
		if (validRecords == 10) { // Read next 10 records
			Calendar cal = lastRecord.getCalendar();
			cal.add(Calendar.SECOND, 1);
			if (_ctrl != null) {
				_ctrl.setCurrentDate(cal);
				_ctrl.setState(AfinionConnection.SEND_PAT_REQUEST);
			}
		} else {
			if (_ctrl != null) {
				_ctrl.setState(AfinionConnection.ENDING);
				_ctrl.close();
			}
			setChecked(false);
			
			if (lastRecord != null) {
				processRecord(lastRecord);
			} else {
				SWTHelper
					.showInfo(
						Messages.AfinionAS100Action_DeviceName, Messages.AfinionAS100Action_NoValuesMsg); //$NON-NLS-2$
			}
			
			_rs232log.log("Saved"); //$NON-NLS-1$
			ElexisEventDispatcher.reload(LabItem.class);
		}
	}
	
	public void closed(){
		_rs232log.log("Closed"); //$NON-NLS-1$
		setChecked(false);
		_rs232log.logEnd();
	}
	
	public void cancelled(){
		_ctrl.close();
		_rs232log.log("Cancelled"); //$NON-NLS-1$
		setChecked(false);
		_rs232log.logEnd();
	}
	
	public void timeout(){
		_ctrl.close();
		_rs232log.log("Timeout"); //$NON-NLS-1$
		SWTHelper.showError(Messages.AfinionAS100Action_RS232_Timeout_Title, Messages.AfinionAS100Action_RS232_Timeout_Text);
		setChecked(false);
		_rs232log.logEnd();
	}
	
	/**
	 * Erstes Zeichen wird Uppercase gemacht
	 */
	private String getFirstUpper(String str){
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
}
