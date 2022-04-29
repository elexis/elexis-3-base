package ch.elexis.connect.afinion;

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

	private String replaceBinaryChar(String s) {
		String debug = s.replace("<", "<LT>").replace(">", "<GT>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		debug = debug.replace("\000", "<NUL>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\001", "<SOH>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\002", "<STX>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\003", "<ETX>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\004", "<EOT>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\005", "<ENQ>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\006", "<ACK>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\007", "<007>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\008", "<008>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\009", "<009>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\010", "<010>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\011", "<011>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\012", "<012>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\013", "<013>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\014", "<014>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\015", "<015>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\016", "<DLE>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\017", "<017>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\018", "<018>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\019", "<019>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\020", "<020>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\021", "<NAK>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\022", "<022>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\023", "<ETB>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\024", "<024>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\025", "<025>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\026", "<026"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\027", "<027>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\028", "<028>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\029", "<029>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\030", "<030>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\031", "<031>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace(StringUtils.SPACE, "<SPACE>"); //$NON-NLS-1$
		debug = debug.replace(StringUtils.LF, "<LF>"); //$NON-NLS-1$
		debug = debug.replace("\t", "<HT>"); //$NON-NLS-1$ //$NON-NLS-2$
		debug = debug.replace("\"", "<QUOTE>"); //$NON-NLS-1$ //$NON-NLS-2$
		return debug;
	}

	public void logRX(String s) {
		_log.println("<-- \"" + s + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	// public void logTX(String s)
	// {
	//
	// _log.println("--> \"" + replaceBinaryChar(s) + "\""); //$NON-NLS-1$
	// //$NON-NLS-2$
	// }

	public void logRaw(String s) {
		_log.println(s);
	}

	public void log(String s) {
		_log.println("-*- \"" + replaceBinaryChar(s) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void logStart() {
		_log.println("-S- \"" + new TimeTool().toDBString(true) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void logEnd() {
		_log.println("-E- \"" + new TimeTool().toDBString(true) + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	class DummyPrintStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			// Do nothing
		}
	}
}
