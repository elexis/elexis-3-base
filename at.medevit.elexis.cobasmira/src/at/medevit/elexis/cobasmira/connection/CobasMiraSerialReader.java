package at.medevit.elexis.cobasmira.connection;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.cobasmira.model.CobasMiraLog;
import at.medevit.elexis.cobasmira.model.CobasMiraMessage;

public class CobasMiraSerialReader implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CobasMiraSerialReader.class);
	
	private static final int SOH = 0x01; // Start of Heading
	private static final int STX = 0x02; // Start of Text within a Cobas Mira Measurement
	private static final int ETX = 0x03; // End of Text within a Cobas Mira Measurement (see sampledata/cobaslogsingle.txt for info)
	private static final int EOT = 0x04; // End of Transmission
	
	private InputStream in;
	private boolean keepRunning = true;
	
	public boolean isKeepRunning(){
		return keepRunning;
	}
	
	public void setKeepRunning(boolean keepRunning){
		this.keepRunning = keepRunning;
	}
	
	public CobasMiraSerialReader(InputStream in){
		this.in = in;
	}
	
	public void run(){
		logger.debug("Starting reader thread");
		try {
			CobasMiraLog log = CobasMiraLog.getInstance();
			
			int data;
			int state = 0;
			StringBuffer headerBuf = new StringBuffer();
			StringBuffer textBuf = new StringBuffer();
			CobasMiraMessage temp = null;
			data = in.read();
			while (data != -1) {
				if (!keepRunning)
					return;
				if (data == SOH) {
					logger.debug("SOH");
					headerBuf = new StringBuffer();
					state = 1;
					data = in.read();
					continue;
				} else if (data == STX) {
					logger.debug("STX");
					temp = new CobasMiraMessage();
					temp.setHeader(headerBuf.toString());
					textBuf = new StringBuffer();
					state = 2;
					data = in.read();
					continue;
				} else if (data == ETX) {
					logger.debug("ETX");
					temp.setText(textBuf.toString());
					data = in.read();
					continue;
				} else if (data == EOT) {
					logger.debug("EOT");
					log.addMessage(temp);
					data = in.read();
					continue;
				} else {
					if (state == 0) {
						logger.debug("Invalid state! Ignoring " + data);
						data = in.read();
						continue;
					}
					if (state == 1)
						headerBuf.append((char) data);
					if (state == 2)
						textBuf.append((char) data);
					data = in.read();
					continue;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
