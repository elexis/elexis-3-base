package ch.elexis.connect.fuji.drichem3500;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.serial.Connection;
import ch.elexis.core.serial.Connection.ComPortListener;
import ch.elexis.core.ui.util.SWTHelper;
import gnu.io.SerialPort;

public class SerialListener implements ComPortListener {

	private static Logger logger = LoggerFactory.getLogger(SerialListener.class);

	private Connection conn;

	private ConnectAction action;

	private FujiMessageHandler messageHandler;

	/**
	 * Init serial connection using {@link Connection}.
	 *
	 * @param action
	 */
	public SerialListener(ConnectAction action) {
		conn = new Connection("Elexis-Fuji-DriChem", CoreHub.localCfg.get(Preferences.PORT, "COM1"),
				CoreHub.localCfg.get(Preferences.PARAMS,
						"9600,8,n,1," + SerialPort.FLOWCONTROL_RTSCTS_IN + "," + SerialPort.FLOWCONTROL_RTSCTS_OUT),
				this).withEndOfChunk(new byte[] { Connection.ETX });
		messageHandler = new FujiMessageHandler(action);
		this.action = action;
	}

	public boolean connect() {
		try {
			if (conn.connect()) {
				logger.debug("Opening connection on " + CoreHub.localCfg.get(Preferences.PORT, "COM1"));
				logger.debug("Serial Parameters:" + CoreHub.localCfg.get(Preferences.PARAMS, ""));
				return true;
			} else {
				SWTHelper.showError("Fehler mit Port", "Konnte seriellen Port nicht öffnen");
				return false;
			}
		} catch (NoClassDefFoundError e) {
			logger.error("Could not load serial lib", e);
			return false;
		}
	}

	public void disconnect() {
		if (conn.isOpen()) {
			conn.sendBreak();
			conn.close();
			logger.debug("Closed connection on " + CoreHub.localCfg.get(Preferences.PORT, "COM1"));
		}
	}

	public void gotChunk(Connection conn, String chunk) {
		messageHandler.handle(chunk);
	}

	public void gotBreak(Connection conn) {
		// PANIC Don't to this to me
	}

	@Override
	public void closed() {
		action.setChecked(false);
	}
}
