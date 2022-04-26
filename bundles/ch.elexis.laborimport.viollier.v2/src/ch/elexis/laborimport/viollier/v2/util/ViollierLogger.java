/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package ch.elexis.laborimport.viollier.v2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import ch.rgw.io.FileTool;

/**
 * Logger, der die Ereignisse während eines Imports protokolliert
 *
 */
public class ViollierLogger {

	private final String LOG_FILENAMEPATH = System.getProperty("user.home") //$NON-NLS-1$
			+ File.separator + "elexis" + File.separator + "Viollier.log"; //$NON-NLS-1$ //$NON-NLS-2$

	private final String LOG_BACKUP_FILENAME = "Viollier_%1$tY%1$tm%1$td%1$tH%1$tM%1$tS.log"; //$NON-NLS-1$

	private PrintStream log;
	private static ViollierLogger logger = null;
	private String insetStr = ""; //$NON-NLS-1$

	/**
	 * Liefert die aktuell gültige Logger Instanz
	 */
	public static ViollierLogger getLogger() {
		if (logger == null) {
			logger = new ViollierLogger();
		}
		return logger;
	}

	/**
	 * Standard COnstructor
	 */
	public ViollierLogger() {
		initLogger();
	}

	/**
	 * Initialisiert den Logger (Erstellt Logfile, falls noch nicht vorhanden)
	 */
	private void initLogger() {
		try {
			log = new PrintStream(new FileOutputStream(LOG_FILENAMEPATH, true));
		} catch (FileNotFoundException e) {
			log = System.out;
		}
	}

	/**
	 * Liefert vollen Pfad und Dateinamen auf das Logfile
	 *
	 * @return vollen Pfad und Dateinamen auf das Logfile
	 */
	public String getLocation() {
		return LOG_FILENAMEPATH;
	}

	/**
	 * Erstellt einen Eintrag im Logfile inkl. Zeilenumbruch
	 *
	 * @param s Eintrag, der protokolliert werden soll
	 */
	public void println(String s) {
		log.println(insetStr + s);
	}

	/**
	 * Erstellt einen Eintrag im Logfile ohne Zeilenumbruch
	 *
	 * @param s Eintrag, der protokolliert werden soll
	 */
	public void print(String s) {
		log.print(insetStr + s);
	}

	/**
	 * Erstellt einen Einzug zur optischen Abgrenzung der Protokolleinträge
	 */
	public void addInset() {
		insetStr += "   "; //$NON-NLS-1$
	}

	/**
	 * Entfernt den Einzug von einem Log-Eintrag
	 */
	public void removeInset() {
		if (insetStr.length() > 0) {
			insetStr = insetStr.substring(0, insetStr.length() - 3);
		}
	}

	/**
	 * Lädt das ganze Logfile
	 *
	 * @return
	 */
	public String getContent() {
		String content = "-"; //$NON-NLS-1$
		try {
			content = FileTool.readTextFile(new File(LOG_FILENAMEPATH));
		} catch (IOException e) {
			content = e.getMessage();
		}
		return content;
	}

	/**
	 * Löscht das aktuelle Logfile
	 *
	 * @throws IOException
	 */
	public void deleteLog() throws IOException {
		log.close();
		new File(LOG_FILENAMEPATH).delete();
		new File(LOG_FILENAMEPATH).createNewFile();
		initLogger();
	}

	/**
	 * Kopiert das aktuelle Logfile in das Backup-Verzeichnis
	 *
	 * @param backupDir Backup-Verzeichnis, in welches das Logfile kopiert werden
	 *                  soll
	 * @throws IOException
	 */
	public void backupLog(String backupDir) throws IOException {
		log.close();
		new File(LOG_FILENAMEPATH)
				.renameTo(new File(backupDir + File.separator + String.format(LOG_BACKUP_FILENAME, new Date())));
		new File(LOG_FILENAMEPATH).createNewFile();
		initLogger();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		if (log != null) {
			log.close();
		}
	}
}
