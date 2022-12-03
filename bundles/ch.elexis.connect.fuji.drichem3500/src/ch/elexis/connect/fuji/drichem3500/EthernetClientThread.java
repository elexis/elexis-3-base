package ch.elexis.connect.fuji.drichem3500;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EthernetClientThread extends Thread {

	protected static Logger logger = LoggerFactory.getLogger(EthernetClientThread.class);

	final byte STX_BYTE = 0x02; // Start of text within a ABX measurement
	final byte ETX_BYTE = 0x03; // End of Text
	final byte[] ACK = { 0x06 };

	private Socket clientSocket;
	private boolean running;

	private FujiMessageHandler messageHandler;

	public EthernetClientThread(Socket clientSocket, ConnectAction action) {
		this.clientSocket = clientSocket;
		messageHandler = new FujiMessageHandler(action);
	}

	@Override
	public void run() {
		InputStream in = null;
		running = true;
		try {
			in = clientSocket.getInputStream();

			// Run in a loop until socket gets closed
			while (running) {
				// read incoming stream
				String fujiMessage = readIncomingData(in);
				if (messageHandler.handle(fujiMessage)) {
					// close socket after successful import of message
					running = false;
				}
			}
		} catch (Exception e) {
			logger.info("Stop client connection.", e);
		} finally {
			// Clean up
			try {
				if (in != null) {
					in.close();
				}
				if (clientSocket != null) {
					clientSocket.close();
				}
			} catch (IOException e) {
				logger.warn("Stopped client connection.", e);
			}
		}
	}

	/**
	 * reads incoming data from fuji drichem
	 *
	 * @param socket
	 * @return data received from ABX
	 * @throws IOException
	 */
	protected String readIncomingData(InputStream input) throws IOException {
		logger.debug("Received input stream " + input);
		StringBuilder sBuilder = new StringBuilder();

		boolean startOfMessage = false;
		boolean endOfMessage = false;

		int readByte = -1;
		while (((readByte = input.read()) != -1) && !endOfMessage) {
			if (readByte == STX_BYTE) {
				logger.debug("STX (" + (char) readByte + ")");
				startOfMessage = true;
			}
			if (startOfMessage) {
				if (readByte == ETX_BYTE) {
					logger.debug("ETX (" + (char) readByte + ")");
					endOfMessage = true;
				} else {
					sBuilder.append((char) readByte);
				}
			}
		}
		logger.debug("End of Stream (etx " + endOfMessage + "). Data: " + sBuilder.toString());

		return sBuilder.toString();
	}
}
