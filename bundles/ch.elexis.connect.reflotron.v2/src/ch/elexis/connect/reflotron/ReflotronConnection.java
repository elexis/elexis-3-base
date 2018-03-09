package ch.elexis.connect.reflotron;

import gnu.io.SerialPortEvent;

import java.io.IOException;
import java.io.InputStream;

import ch.elexis.core.ui.importer.div.rs232.AbstractConnection;
import ch.elexis.core.ui.util.Log;

public class ReflotronConnection extends AbstractConnection {
	private static final int STX = 0x02;
	private static final int ETX = 0x03;
	private static StringBuffer textBuf = new StringBuffer();
	Log _elexislog = Log.get("ReflotronConnection");
	
	public ReflotronConnection(String portName, String port, String settings, ComPortListener l){
		super(portName, port, settings, l);
	}
	
	/**
	 * Handles serial event.
	 */
	public void serialEvent(final int state, final InputStream inputStream, final SerialPortEvent e)
		throws IOException{
		int data = inputStream.read();
		if (data == STX) {
			_elexislog.log("Start of stream: " + data + " (STX)", Log.DEBUGMSG);
			textBuf = new StringBuffer();
			data = inputStream.read();
		} else {
			_elexislog.log("Continue stream..", Log.DEBUGMSG);
		}
		
		while ((data != -1) && (data != ETX)) {
			textBuf.append((char) data);
			data = inputStream.read();
		}
		// Log output
		String text = "";
		if (data == -1) {
			text = " (EOF)";
		}
		if (data == ETX) {
			text = " (ETX)";
		}
		_elexislog.log("End of stream: " + data + text, Log.DEBUGMSG);
		
		if (data == ETX) {
			_elexislog.log("buffer: " + textBuf.toString(), Log.DEBUGMSG);
			this.listener.gotData(this, textBuf.toString().getBytes());
			textBuf = new StringBuffer();
		}
	}
}
