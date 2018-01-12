/*******************************************************************************
 * Copyright (c) 2017 novcom AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Gutknecht
 *******************************************************************************/
package ch.novcom.elexis.mednet.plugin.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * Formatter used to define the way a LogRecord must be written into the logfile
 * @author David Gutknecht
 *
 */
public final class LogFormatter extends Formatter {
	private final DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	//private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Override
	public String format(LogRecord record) {
		StringBuilder sb = new StringBuilder();

		sb.append(formatter.format(new Date(record.getMillis())));
		sb.append(": ");
		sb.append(record.getLevel().getName());
		sb.append(": (");
		sb.append(record.getSourceClassName());
		sb.append(".");
		sb.append(record.getSourceMethodName());
		sb.append("): ");
		sb.append(formatMessage(record));
		//sb.append(LINE_SEPARATOR);

		if (record.getThrown() != null) {
			try {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				record.getThrown().printStackTrace(pw);
				pw.close();
				sb.append(sw.toString());
			} catch (Exception ex) {
				// ignore
			}
		}

		return sb.toString();
	}
}