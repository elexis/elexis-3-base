package ch.medshare.connect.abacusjunior;

import org.apache.commons.lang3.StringUtils;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import ch.rgw.tools.TimeTool;

public class Logger {
	PrintStream _log;

	public Logger() {
		_log = System.out;
	}

	public Logger(String filename) throws FileNotFoundException {
		_log = new PrintStream(new FileOutputStream(filename, true));
	}

	public Logger(boolean enable) {
		if (enable) {
			_log = System.out;
		} else {
			_log = new PrintStream(new DummyPrintStream());
		}
	}

	public void logRX(String s) {
		String debug = s.replace("<", "<LT>").replace(">", "<GT>");
		debug = debug.replace("\001", "<SOH>");
		debug = debug.replace("\002", "<STX>");
		debug = debug.replace("\003", "<ETX>");
		debug = debug.replace("\004", "<EOT>");
		debug = debug.replace("\005", "<ENQ>");
		debug = debug.replace("\006", "<ACK>");
		debug = debug.replace("\021", "<NAK>");
		debug = debug.replace(StringUtils.SPACE, "<SPACE>");
		debug = debug.replace(StringUtils.LF, "<LF>");
		debug = debug.replace("\t", "<HT>");
		debug = debug.replace("\"", "<QUOTE>");

		_log.println("<-- \"" + debug + "\"");
	}

	public void logTX(String s) {
		String debug = s.replace("<", "<LT>").replace(">", "<GT>");
		debug = debug.replace("\001", "<SOH>");
		debug = debug.replace("\002", "<STX>");
		debug = debug.replace("\003", "<ETX>");
		debug = debug.replace("\004", "<EOT>");
		debug = debug.replace("\005", "<ENQ>");
		debug = debug.replace("\006", "<ACK>");
		debug = debug.replace("\021", "<NAK>");
		debug = debug.replace(StringUtils.SPACE, "<SPACE>");
		debug = debug.replace(StringUtils.LF, "<LF>");
		debug = debug.replace("\t", "<HT>");
		debug = debug.replace("\"", "<QUOTE>");

		_log.println("--> \"" + debug + "\"");
	}

	public void log(String s) {
		String debug = s.replace("<", "<LT>").replace(">", "<GT>");
		debug = debug.replace("\"", "<QUOTE>");
		_log.println("-*- \"" + debug + "\"");
	}

	public void logStart() {
		_log.println("-S- \"" + new TimeTool().toDBString(true) + "\"");
	}

	public void logEnd() {
		_log.println("-E- \"" + new TimeTool().toDBString(true) + "\"");
	}

	class DummyPrintStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			// Do nothing
		}
	}
}
