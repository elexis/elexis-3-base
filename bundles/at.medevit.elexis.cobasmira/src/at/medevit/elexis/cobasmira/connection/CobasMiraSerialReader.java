package at.medevit.elexis.cobasmira.connection;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.model.CobasMiraLog;
import at.medevit.elexis.cobasmira.model.CobasMiraMessage;
import gnu.io.SerialPort;

public class CobasMiraSerialReader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CobasMiraSerialReader.class);
	
	private static final int SOH = 0x01; // Start of Heading
	private static final int STX = 0x02; // Start of Text within a Cobas Mira Measurement
	private static final int ETX = 0x03; // End of Text within a Cobas Mira Measurement (see sampledata/cobaslogsingle.txt for info)
	private static final int EOT = 0x04; // End of Transmission
	
	private InputStream in;
	private SerialPort serialPort;
	private boolean keepRunning = true;
	
	public boolean isKeepRunning(){
		return keepRunning;
	}
	
	public void setKeepRunning(boolean keepRunning){
		this.keepRunning = keepRunning;
	}
	
	public CobasMiraSerialReader(InputStream in, SerialPort serialPort){
		this.serialPort = serialPort;
		this.in = in;
	}
	
	public void run(){
		logger.debug("Starting reader thread");
		try {
			CobasMiraLog cobasMiraLog = CobasMiraLog.getInstance();
			
			int data;
			int state = 0;
			StringBuffer headerBuf = new StringBuffer();
			StringBuffer textBuf = new StringBuffer();
			CobasMiraMessage message = null;
			
			while(keepRunning) {
				data = in.read();
				if(data==-1) {
					try {
						Thread.sleep(1000);
						logger.trace("Waiting for serial input...");
					} catch (InterruptedException e) {
						logger.warn("Sleep interrupted", e);
					}
				} else if (data == SOH) {
					logger.trace("SOH");
					headerBuf = new StringBuffer();
					state = SOH;
				} else if (data == STX) {
					logger.trace("STX");
					message = new CobasMiraMessage();
					message.setHeader(headerBuf.toString());
					textBuf = new StringBuffer();
					state = STX;
				} else if (data == ETX) {
					logger.trace("ETX");
					if(message!=null) {
						message.setText(textBuf.toString());
					} else {
						logger.warn("message is null, programmatic error");
					}
					state = ETX;
				} else if (data == EOT) {
					logger.trace("EOT");
					cobasMiraLog.addMessage(message);
					state = EOT;
				} else {
					switch (state) {
					case SOH:
						headerBuf.append((char) data);
						break;
					case STX:
						textBuf.append((char) data);
						break;
					default:
						logger.debug("Invalid state! Ignoring " + data);
						break;
					}	
				}
			}

			logger.debug("Exiting reader");
			if(serialPort!=null) serialPort.close();
		} catch (IOException e) {
			logger.error("Error receveiving data", e);
			e.printStackTrace();
		}
	}
}
