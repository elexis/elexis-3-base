package at.medevit.elexis.cobasmira.connection;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.State;
import java.util.LinkedList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.Activator;
import at.medevit.elexis.cobasmira.ui.Preferences;
import ch.elexis.core.data.activator.CoreHub;

public class CobasMiraConnection implements PropertyChangeListener {
	private static Logger logger = LoggerFactory.getLogger(CobasMiraConnection.class);
	private static CobasMiraConnection instance = null;
	
	private Thread cobasMiraReader;
	private CobasMiraSerialReader reader;
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private CobasMiraConnection(){}
	
	public static CobasMiraConnection getInstance(){
		if (instance == null) {
			instance = new CobasMiraConnection();
		}
		return instance;
	}
	
	static void listPorts(){
		java.util.Enumeration<CommPortIdentifier> portEnum =
			CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier portIdentifier = portEnum.nextElement();
			System.out.println(portIdentifier.getName() + " - "
				+ getPortTypeName(portIdentifier.getPortType()));
		}
	}
	
	static String getPortTypeName(int portType){
		switch (portType) {
		case CommPortIdentifier.PORT_I2C:
			return "I2C";
		case CommPortIdentifier.PORT_PARALLEL:
			return "Parallel";
		case CommPortIdentifier.PORT_RAW:
			return "Raw";
		case CommPortIdentifier.PORT_RS485:
			return "RS485";
		case CommPortIdentifier.PORT_SERIAL:
			return "Serial";
		default:
			return "unknown type";
		}
	}
	
	void connect(String portName) throws NoSuchPortException, PortInUseException,
		UnsupportedCommOperationException, IOException{
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned()) {
			logger.warn("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(1200, SerialPort.DATABITS_7, SerialPort.STOPBITS_2,
					SerialPort.PARITY_EVEN);
				serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
					| SerialPort.FLOWCONTROL_RTSCTS_OUT);
				serialPort.setDTR(true);
				
				logger.debug("Opening connection to " + portName);
				logger.debug("BaudRate: " + serialPort.getBaudRate());
				logger.debug("STPB/DTB/PAR: " + serialPort.getStopBits() + " "
					+ serialPort.getDataBits() + " " + serialPort.getParity());
				logger.debug("FCM: " + serialPort.getFlowControlMode() + " should be: "
					+ (SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT));
				logger.debug("CTS/CD/DTR/DSR/RTS: " + serialPort.isCTS() + " " + serialPort.isCD()
					+ " " + serialPort.isDTR() + " " + serialPort.isDSR() + " "
					+ serialPort.isRTS());
				
				InputStream in = serialPort.getInputStream();
				//OutputStream out = serialPort.getOutputStream();
				
				reader = new CobasMiraSerialReader(in);
				cobasMiraReader = new Thread(reader);
				cobasMiraReader.start();
				logger.debug("Reader Thread ID: " + cobasMiraReader.getId() + " Priority: "
					+ cobasMiraReader.getPriority() + " Name: " + cobasMiraReader.getName());
				
				//serialPort.setDTR(true);
				//(new Thread(new SerialWriter(out))).start();
			} else {
				logger.warn("Error: Only serial ports are handled by this example.");
			}
		}
	}
	
	/**
	 * 
	 * @return Wahrheitswert ob der CobasMiraReader derzeit aktiv ist
	 */
	public boolean isActivated(){
		if (instance == null)
			return false;
		if (cobasMiraReader != null) {
			State cobasMiraReaderState = cobasMiraReader.getState();
			if (cobasMiraReaderState == Thread.State.NEW
				|| cobasMiraReaderState == Thread.State.TERMINATED)
				return false;
		} else {
			return false;
		}
		return true;
	}
	
	/**
	 * @return Wahrheitswert ob der CobasMiraReader derzeit deaktiviert ist
	 */
	public boolean isDeactivated(){
		if (instance == null)
			return true;
		if (cobasMiraReader != null) {
			State cobasMiraReaderState = cobasMiraReader.getState();
			if (cobasMiraReaderState == Thread.State.NEW
				|| cobasMiraReaderState == Thread.State.TERMINATED)
				return true;
		} else {
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return wurde erfolgreich gestartet
	 */
	public boolean startReadingSerialInput(){
		if (!isActivated()) {
			String port = CoreHub.localCfg.get(Preferences.PORT, "/dev/cu.PL2303-0000103D");
			try {
				instance = CobasMiraConnection.getInstance();
				instance.connect(port);
				if (this.isActivated()) {
					propertyChangeSupport.firePropertyChange("status", false, true);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 
	 * @return wurde erfolgreich beendet
	 */
	public boolean stopReadingSerialInput(){
		if (isActivated()) {
			//TODO: Does only work if there is traffic on the line ...
			reader.setKeepRunning(false);
			try {
				cobasMiraReader.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return (!this.isActivated());
		} else {
			return false;
		}
	}
	
	public static String[] getComPorts(){
		try {
			java.util.Enumeration<CommPortIdentifier> portEnum =
				CommPortIdentifier.getPortIdentifiers();
			LinkedList<String> portList = new LinkedList<String>();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier portIdentifier = portEnum.nextElement();
				portList.add(portIdentifier.getName());
			}
			return portList.toArray(new String[portList.size()]);
		} catch (UnsatisfiedLinkError e) {
			Status status = new Status(IStatus.WARNING, Activator.PLUGIN_ID, "Unsatisfied Link", e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return new String[] {
				"No ports found"
			};
		} catch (NoClassDefFoundError e) {
			Status status =
				new Status(IStatus.WARNING, Activator.PLUGIN_ID, "No class definition found", e);
			StatusManager.getManager().handle(status, StatusManager.SHOW);
			return new String[] {
				"No ports found"
			};
		}
	}
	
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	public String getStatus(){
		if (isActivated()) {
			return new String("Status: Cobas Mira verbunden auf Port "
				+ CoreHub.localCfg.get(Preferences.PORT, "/dev/cu.PL2303-0000103D")
				+ ", Daten werden gesammelt.");
		} else {
			return new String("Status: Cobas Mira nicht verbunden.");
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt){}
	
}
