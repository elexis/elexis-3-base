/********************************************************
 * (c) 2009 by Medelexis AG, Baden, Switzerland
 * Alle Rechte vorbehalten / All rights reserved
 * http://www.medelexis.ch
 *
 * $Id$
 ********************************************************/
package ch.elexis.labor.medics.v2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang3.StringUtils;

import ch.rgw.io.FileTool;

public class MedicsLogger {

	private final String LOG_FILENAMEPATH = System.getProperty("user.home") //$NON-NLS-1$
			+ File.separator + "elexis" + File.separator + "medics.log"; //$NON-NLS-1$ //$NON-NLS-2$

	private PrintStream log;
	private static MedicsLogger logger = null;
	private String insetStr = StringUtils.EMPTY;

	public static MedicsLogger getLogger() {
		if (logger == null) {
			logger = new MedicsLogger();
		}
		return logger;
	}

	public MedicsLogger() {
		initLogger();
	}

	private void initLogger() {
		try {
			log = new PrintStream(new FileOutputStream(LOG_FILENAMEPATH, true));
		} catch (FileNotFoundException e) {
			log = System.out;
		}
	}

	public void println(String s) {
		log.println(insetStr + s);
	}

	public void print(String s) {
		log.print(insetStr + s);
	}

	public void addInset() {
		insetStr += "   "; //$NON-NLS-1$
	}

	public void removeInset() {
		if (insetStr.length() > 0) {
			insetStr = insetStr.substring(0, insetStr.length() - 3);
		}
	}

	public String getContent() {
		String content = "-"; //$NON-NLS-1$
		try {
			content = FileTool.readTextFile(new File(LOG_FILENAMEPATH));
		} catch (IOException e) {
			content = e.getMessage();
		}
		return content;
	}

	public void deleteLog() throws IOException {
		log.close();
		new File(LOG_FILENAMEPATH).delete();
		new File(LOG_FILENAMEPATH).createNewFile();
		initLogger();
	}

	@Override
	protected void finalize() throws Throwable {
		if (log != null) {
			log.close();
		}
	}
}
