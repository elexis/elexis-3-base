package ch.elexis.connect.afinion;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import ch.elexis.core.serial.Connection;
import ch.elexis.core.ui.util.Log;

/**
 * Ueberarbeitete Version des Handshakes zwischen PC und Afinion
 * 
 * @author immi
 * 
 */
public class AfinionConnection extends Connection {
	
	Log _elexislog = Log.get("AfinionConnection");
	
	private static final int NUL = 0x00;
	private static final int STX = 0x02;
	private static final int ETX = 0x03;
	private static final int ACK = 0x06;
	private static final int DLE = 0x10;
	private static final int NAK = 0x15;
	private static final int ETB = 0x17;
	private static final int LF = 0x0D;
	
	private static final long STARTUP_DELAY_IN_MS = 2000; // 60 Sekunden
	private static final long RESEND_IN_MS = 30000; // 30 Sekunden
	
	// Initialisierung
	public static final int INIT = 0;
	// 1 Minute warten
	public static final int WAITING = 1;
	// Patientenrequest senden
	public static final int SEND_PAT_REQUEST = 2;
	// Patient Record Request gesendet. Wartet auf Record Ack Meldung
	public static final int PAT_REQUEST_SENDED = 3;
	// Patient Record Request Acknowledge erhalten. Nun können Daten gelesen werden.
	public static final int PAT_REQUEST_ACK = 4;
	// Beenden
	public static final int ENDING = 99;
	
	private String awaitPacketNr;
	
	private static int pc_packet_nr = 21;
	
	private Calendar currentCal = new GregorianCalendar();
	
	private int state;
	
	// Wird für Fehlerhandling verwendet. Alles wird in console geloggt.
	private static final boolean debugToConsole = false;
	
	public AfinionConnection(String portName, String port, String settings, ComPortListener l){
		super(portName, port, settings, l);
		setState(INIT);
	}
	
	public void setCurrentDate(Calendar cal){
		this.currentCal = cal;
	}
	
	/**
	 * Wenn variable debug = true, dann werden alle bytes in die console geloggt. In jedem Fall wird
	 * ins Elexis Log geloggt
	 * 
	 * @param text
	 */
	private void debug(String text){
		_elexislog.log(text, Log.DEBUGMSG);
		if (debugToConsole) {
			System.out.print(text);
		}
	}
	
	/**
	 * Wenn variable debug = true, dann werden alle bytes in die console geloggt.
	 * 
	 * @param text
	 */
	private void debugln(String text){
		_elexislog.log(text, Log.DEBUGMSG);
		if (debugToConsole) {
			System.out.println(text);
		}
	}
	
	/**
	 * Crc calculation
	 */
	private static long getCrc(byte[] array){
		char crc = 0xFFFF;
		for (byte b : array) {
			char value = (char) b;
			value ^= crc & 0xFF;
			value ^= (value << 4) & 0xFF;
			crc = (char) ((crc >>> 8) ^ (value << 8) ^ (value << 3) ^ (value >>> 4));
		}
		return crc;
	}
	
	/**
	 * Retourniert Byte-Array als Textausgabe.
	 */
	private String getByteStr(byte[] bytes){
		StringBuffer strBuf = new StringBuffer();
		int counter = 1;
		for (byte b : bytes) {
			if (strBuf.length() > 0) {
				strBuf.append(", "); //$NON-NLS-1$
				if (counter > 16) {
					strBuf.append("\n"); //$NON-NLS-1$
					counter = 1;
				}
			}
			String byteStr = Long.toHexString((long) b);
			while (byteStr.length() < 2) {
				byteStr = "0" + byteStr; //$NON-NLS-1$
			}
			strBuf.append("0x" + byteStr); //$NON-NLS-1$
			
			counter++;
		}
		return strBuf.toString();
	}
	
	/**
	 * Textausgabe für debugging
	 * 
	 * @param value
	 * @return
	 */
	private String getText(int value){
		if (value == NUL) {
			return "<NUL>"; //$NON-NLS-1$
		}
		if (value == STX) {
			return "<STX>"; //$NON-NLS-1$
		}
		if (value == ETX) {
			return "<ETX>"; //$NON-NLS-1$
		}
		if (value == ACK) {
			return "<ACK>"; //$NON-NLS-1$
		}
		if (value == DLE) {
			return "<DLE>"; //$NON-NLS-1$
		}
		if (value == NAK) {
			return "<NAK>"; //$NON-NLS-1$
		}
		if (value == ETB) {
			return "<ETB>"; //$NON-NLS-1$
		}
		
		return new Character((char) value).toString();
	}
	
	/**
	 * Liest nächste Elexis-interne PacketNr
	 * 
	 * @param strBuf
	 * @param date
	 */
	private String nextPacketNr(){
		String packetNrStr = new Integer(pc_packet_nr).toString();
		pc_packet_nr++;
		while (packetNrStr.length() < 4) {
			packetNrStr = "0" + packetNrStr; //$NON-NLS-1$
		}
		return packetNrStr;
	}
	
	/**
	 * Fuegt Datum als String yyyyMMdd HH:mm:ss dazu (ohne Timezone-Umwandlung)
	 */
	private void addDate(StringBuffer strBuf){
		int day = this.currentCal.get(Calendar.DATE);
		int month = this.currentCal.get(Calendar.MONTH) + 1;
		int year = this.currentCal.get(Calendar.YEAR);
		int hour = this.currentCal.get(Calendar.HOUR_OF_DAY);
		int minutes = this.currentCal.get(Calendar.MINUTE);
		int seconds = this.currentCal.get(Calendar.SECOND);
		
		String dayStr = (day < 10 ? "0" : "") + Integer.valueOf(day).toString(); //$NON-NLS-1$ //$NON-NLS-2$
		String monthStr = (month < 10 ? "0" : "") + Integer.valueOf(month).toString();
		String yearStr = Integer.valueOf(year).toString();
		String hourStr = (hour < 10 ? "0" : "") + Integer.valueOf(hour).toString(); //$NON-NLS-1$ //$NON-NLS-2$
		String minuteStr = (minutes < 10 ? "0" : "") + Integer.valueOf(minutes).toString(); //$NON-NLS-1$ //$NON-NLS-2$
		String secondStr = (seconds < 10 ? "0" : "") + Integer.valueOf(seconds).toString(); //$NON-NLS-1$ //$NON-NLS-2$
		
		String dateStr = yearStr + monthStr + dayStr + " " + hourStr + ":" + minuteStr + ":" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			+ secondStr;
		strBuf.append(dateStr);
		String text = "Request starting at:" + dateStr;
		System.out.println(text);
		_elexislog.log(text, Log.INFOS);
	}
	
	private void addContentStart(ByteArrayOutputStream os){
		debug("<DLE>"); //$NON-NLS-1$
		os.write(DLE);
		debug("<STX>"); //$NON-NLS-1$
		os.write(STX);
	}
	
	private void addContentEnd(ByteArrayOutputStream os){
		debug("<DLE>"); //$NON-NLS-1$
		os.write(DLE);
		debug("<ETB>"); //$NON-NLS-1$
		os.write(ETB);
	}
	
	private void addEnding(ByteArrayOutputStream os) throws IOException{
		long crc = getCrc(os.toByteArray());
		
		String crcStr = Long.toHexString(crc).toUpperCase();
		while (crcStr.length() < 4) {
			crcStr = "0" + crcStr; //$NON-NLS-1$
		}
		debug(crcStr);
		os.write(crcStr.getBytes());
		debug("<DLE>"); //$NON-NLS-1$
		os.write(DLE);
		debug("<ETX>"); //$NON-NLS-1$
		os.write(ETX);
		debugln(""); //$NON-NLS-1$
	}
	
	private void sendPacketACK(String packetNr){
		debug("-->"); //$NON-NLS-1$
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			addContentStart(os);
			debug(packetNr);
			os.write(packetNr.getBytes());
			debug("<ACK>"); //$NON-NLS-1$
			os.write(ACK);
			addContentEnd(os);
			addEnding(os);
			
			debugln("Send: " + getByteStr(os.toByteArray())); //$NON-NLS-1$
			if (send(os.toByteArray())) {
				debugln("OK"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessageACK(){
		debug("-->"); //$NON-NLS-1$
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			addContentStart(os);
			String packetNr = nextPacketNr();
			debug(packetNr);
			os.write(packetNr.getBytes());
			String cmdack = "0025:cmdack@"; //$NON-NLS-1$
			debug(cmdack);
			os.write(cmdack.getBytes());
			addContentEnd(os);
			addEnding(os);
			
			debugln("Send: " + getByteStr(os.toByteArray())); //$NON-NLS-1$
			if (send(os.toByteArray())) {
				debugln("OK"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendPacketNAK(String packetNr){
		debug("-->"); //$NON-NLS-1$
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			addContentStart(os);
			debug(packetNr);
			os.write(packetNr.getBytes());
			debug("<NAK>"); //$NON-NLS-1$
			os.write(NAK);
			addContentEnd(os);
			addEnding(os);
			
			debugln("Send: " + getByteStr(os.toByteArray())); //$NON-NLS-1$
			if (send(os.toByteArray())) {
				debugln("OK"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Patienteanfrage wird gesendet
	 * 
	 * @return
	 */
	private String sendPatRecordRequest(){
		debug("-->"); //$NON-NLS-1$
		
		StringBuffer contentBuf = new StringBuffer();
		String packetNrStr = nextPacketNr();
		contentBuf.append(packetNrStr);
		contentBuf.append("0025:record,patient@"); //$NON-NLS-1$
		addDate(contentBuf);
		
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			addContentStart(os);
			debug(contentBuf.toString());
			os.write(contentBuf.toString().getBytes());
			addContentEnd(os);
			addEnding(os);
			
			debugln("Send: " + getByteStr(os.toByteArray())); //$NON-NLS-1$
			if (send(os.toByteArray())) {
				debugln("OK"); //$NON-NLS-1$
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error sending patient request", e);
		}
		
		return packetNrStr;
	}
	
	/**
	 * Liest Stream bis zum nächsten <DEL><ETX>
	 */
	private void readToEnd(final InputStream inputStream) throws IOException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		int data = inputStream.read();
		while (data != -1 && data != ETX) {
			while (data != -1 && data != DLE) {
				os.write(data);
				data = inputStream.read();
			}
			data = inputStream.read();
		}
		
		debug(os.toString());
		debugln("<DLE><ETX>");
	}
	
	/**
	 * Liest Stream bis zum nächsten <DEL><ETX>
	 */
	private void readToLF(final InputStream inputStream) throws IOException{
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		int data = inputStream.read();
		while (data != -1 && data != LF) {
			os.write(data);
			data = inputStream.read();
		}
		debug("...");
		debugln("<LF>");
	}
	
	/**
	 * Liest Datenstream bis <DLE><ETX> und sendet anschliessend das Ack
	 * 
	 * @param inputStream
	 * @throws IOException
	 */
	private void readToEndAndACK(final String packetNr, final InputStream inputStream)
		throws IOException{
		readToEnd(inputStream);
		debugln(""); //$NON-NLS-1$
		if (packetNr != null) {
			sendPacketACK(packetNr);
		}
	}
	
	/**
	 * Verarbeitet Patientendaten
	 */
	private void handlePatientRecord(final String packetNr, final InputStream inputStream)
		throws IOException{
		// nächste 2560 Bytes lesen
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// <DLE><ETB> suchen
		int data = inputStream.read();
		while (data != -1 && data != ETB) {
			while (data != -1 && data != DLE) {
				baos.write(data);
				data = inputStream.read();
			}
			if (data == DLE) { // <DLE><DLE> wird zweites DLE nicht beachtet
				baos.write(data);
				data = inputStream.read();
				if (data != DLE) {
					baos.write(data);
				}
				data = inputStream.read();
			}
		}
		
		byte[] bytes = baos.toByteArray();
		
		debug(getByteStr(bytes));
		debugln("<DLE><ETB>");
		
		readToEnd(inputStream);
		
		if (bytes.length < 2560) {
			sendPacketNAK(packetNr);
		} else {
			sendPacketACK(packetNr);
			sendMessageACK();
			fireData(bytes);
		}
	}
	
	private void dataAvailable(final InputStream inputStream) throws IOException{
		debug("<--"); //$NON-NLS-1$
		int data = inputStream.read();
		if (data == DLE) {
			debug("<DLE>"); //$NON-NLS-1$
			data = inputStream.read();
			if (data == STX) {
				debug("<STX>"); //$NON-NLS-1$
				
				String packetNr = ""; //$NON-NLS-1$
				for (int i = 0; i < 4; i++) {
					data = inputStream.read();
					packetNr += (char) data;
				}
				debug(packetNr);
				
				// ACK/ NAK
				data = inputStream.read();
				if (data == NAK) {
					debug("<NAK>");
					data = inputStream.read(); // <DLE>
					debug(getText(data));
					data = inputStream.read(); // <ETB>
					debug(getText(data));
					readToEnd(inputStream);
				} else if (data == ACK) {
					debug("<ACK>");
					data = inputStream.read(); // <DLE>
					debug(getText(data));
					data = inputStream.read(); // <ETB>
					debug(getText(data));
					readToEnd(inputStream);
					if (packetNr.equals(awaitPacketNr)) {
						// Request wurde bestaetigt
						setState(PAT_REQUEST_ACK);
					}
				} else {
					// Content lesen
					StringBuffer header = new StringBuffer();
					while ((data != -1) && (data != '@')) {
						header.append((char) data);
						data = inputStream.read();
					}
					String headerStr = header.toString();
					debug(headerStr);
					debug("@"); //$NON-NLS-1$
					if (headerStr.indexOf("0025:record,patient") != -1) { //$NON-NLS-1$
						if (getState() == PAT_REQUEST_ACK) {
							handlePatientRecord(packetNr, inputStream);
						} else {
							readToEndAndACK(packetNr, inputStream);
						}
					} else if (headerStr.indexOf("0024:record.control") != -1) { //$NON-NLS-1$
						readToEndAndACK(packetNr, inputStream);
					} else if (headerStr.indexOf("cmdack") != -1) {//$NON-NLS-1$
						readToEndAndACK(packetNr, inputStream);
					} else if (headerStr.indexOf("cmderr") != -1) {//$NON-NLS-1$
						readToEndAndACK(packetNr, inputStream);
						// setState(SEND_PAT_REQUEST);
					} else if (headerStr.indexOf("cmdcmpl") != -1) {//$NON-NLS-1$
						readToEndAndACK(packetNr, inputStream);
						// setState(SEND_PAT_REQUEST);
					} else if (headerStr.indexOf("debugmsg") != -1) {//$NON-NLS-1$
						readToEndAndACK(packetNr, inputStream);
					} else if (headerStr.indexOf("FFFF:IC") != -1) {//$NON-NLS-1$
						readToEndAndACK(packetNr, inputStream);
					}
				}
			} else {
				// Sollte nicht vorkommen
				readToEnd(inputStream);
			}
		} else {
			// {Text} <LF>
			readToLF(inputStream);
		}
	}
	
	@Override
	protected void setData(byte[] newData){
		try {
			dataAvailable(new ByteArrayInputStream(newData));
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error setting available serial data", e);
		}
	}
	
	@Override
	public boolean connect(){
		setState(INIT);
		boolean ret = super.connect();
		if (ret) {
			ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
			executorService.scheduleAtFixedRate(() -> {
				// Initialisierung
				if (getState() == INIT) {
					setState(WAITING);
				}
				
				// 1 Minute delay
				if (getState() == WAITING) {
					setState(SEND_PAT_REQUEST);
				}
				
				// Überprüft Status. Nach x Sekunden wird Request nochmals gesendet
				if (getState() == PAT_REQUEST_SENDED || getState() == PAT_REQUEST_ACK) {
					setState(SEND_PAT_REQUEST);
				}
				
				if (getState() == SEND_PAT_REQUEST) {
					awaitPacketNr = sendPatRecordRequest();
					LoggerFactory.getLogger(getClass()).info(
						"Sending patient record request -> packet number [" + awaitPacketNr + "]");
					setState(PAT_REQUEST_SENDED);
				}
			}, 3, 30, TimeUnit.SECONDS);
		}
		return ret;
	}
	
	private String getStateText(int state){
		switch (state) {
		case INIT:
			return "INIT";
		case WAITING:
			return "WAITING";
		case SEND_PAT_REQUEST:
			return "SEND_PAT_REQUEST";
		case PAT_REQUEST_SENDED:
			return "SEND_PAT_REQUEST";
		case PAT_REQUEST_ACK:
			return "SEND_PAT_REQUEST";
		default:
			break;
		}
		return "#" + state;
	}
	
	public void setState(int state){
		debugln(getStateText(getState()) + " -> " + getStateText(state));
		this.state = state;
	}
	
	public int getState(){
		return state;
	}
}
