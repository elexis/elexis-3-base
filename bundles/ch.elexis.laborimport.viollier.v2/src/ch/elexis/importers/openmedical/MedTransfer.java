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
package ch.elexis.importers.openmedical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Universell einsetzbare Klasse für alle Laborplugins, welche die HL7 Dateien
 * via MedTransfer von OpenMedical abholen. Die Klasse bietet eine statische
 * Methode für den Download an. Das Ini File wird automatisch ins Temp Directory
 * extrahiert und von dort angewendet. Die Laborimporter Plugins müssen das Ini
 * damit nicht mehr zwingend in den Einstellungen verlangen. Damit wird der
 * Konfigurationsaufwand für Supporter und Techniker wesentlich reduziert.
 *
 */
public class MedTransfer {

	private static String OPENMEDICAL_MAINCLASS = "ch.openmedical.JMedTransfer.JMedTransfer"; //$NON-NLS-1$

	/**
	 * Ruft den JMedTransferO von OpenMedical auf. Damit werden HL7 Dateien
	 * abgeholt.
	 *
	 * @param jarFilePath Voller Pfad und Dateiname auf das JMedTransferO.jar
	 * @param downloadDir Verzeichnis, in welches die Dateien heruntergeladen werden
	 *                    sollen
	 * @param params      Benutzerspezifische Parameter für den JMedTransfer (z.B.
	 *                    -allInOne bei Gruppenpraxen)
	 * @return Anzahl heruntergeladene Dateien
	 */
	public static int doDownload(String jarFilePath, String downloadDir, String params) {
		int result = 0;
		Object openmedicalObject = null;
		Method openmedicalDownloadMethod = null;
		String iniFilename = ""; //$NON-NLS-1$

		// try to dynamically load the openmedical JAR file
		if (jarFilePath != null) {
			File jar = new File(jarFilePath);
			if (jar.canRead()) {

				iniFilename = extractIni(jarFilePath);

				try {
					URLClassLoader urlLoader = getURLClassLoader(new URL("file", null, jar.getAbsolutePath())); //$NON-NLS-1$

					Class<?> openmedicalClass = urlLoader.loadClass(OPENMEDICAL_MAINCLASS);

					// try to get the download method
					Method meth;
					try {
						meth = openmedicalClass.getMethod("download", String[].class); //$NON-NLS-1$
					} catch (Throwable e) {
						throw e;
					}

					// try to get an instance
					Object obj = openmedicalClass.newInstance();

					// success (no exception); set the global variables
					openmedicalObject = obj;
					openmedicalDownloadMethod = meth;
				} catch (Throwable e) {
					// loading the class failed; do nothing
				}
			}
		}

		try {
			String[] customParams = params.split("[ ]"); //$NON-NLS-1$
			String[] systemParams = new String[] { "--download", downloadDir, "--logPath", downloadDir, "--ini", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					iniFilename, "--verbose", "INF", "-#OpenMedicalKey#" //$NON-NLS-2$ //$NON-NLS-3$
			};
			Object[] finalParams = new Object[] { concatArrays(systemParams, customParams) };
			if (openmedicalDownloadMethod != null) {
				Object omResult = openmedicalDownloadMethod.invoke(openmedicalObject, finalParams);
				if (omResult instanceof Integer) {
					result = ((Integer) omResult).intValue();
				}
			}
		} catch (Throwable e) {
			// method call failed; do nothing
		}
		return result;
	}

	/**
	 * Erstellt den URLClassLoader
	 *
	 * @param jarURL
	 * @return URLClassLoader mit jarURL
	 */
	private static URLClassLoader getURLClassLoader(final URL jarURL) {
		return new URLClassLoader(new URL[] { jarURL });
	}

	/**
	 * Hängt zwei Arrays aneinander
	 *
	 * @param first  Erstes Array
	 *
	 * @param second Zweites Array
	 * @return Kombiniertes Array, welches erstes Array und zweites Array enthält
	 */
	private static <T> T[] concatArrays(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	/**
	 * Extrahiert das Ini File aus dem Jar ins temp. Directory des Systems. Getestet
	 * auf Windows. Linux und Mac noch ohne Gewähr.
	 *
	 * @param jarFilePath Voller Pfad und Dateiname des JMedTransferO.jar
	 * @return Voller Pfad und Dateiname des Ini-Files
	 */
	private static String extractIni(String jarFilePath) {
		String retVal = ""; //$NON-NLS-1$
		File iniFile;

		try {
			iniFile = File.createTempFile("MedTransfer", ".ini"); //$NON-NLS-1$ //$NON-NLS-2$
			iniFile.deleteOnExit(); // wir wollen keine Altlasten...

			retVal = iniFile.getCanonicalPath();

			JarFile jar = new JarFile(jarFilePath);
			ZipEntry entry = jar.getEntry("MedTransfer.ini"); //$NON-NLS-1$

			InputStream in = new BufferedInputStream(jar.getInputStream(entry));
			OutputStream out = new BufferedOutputStream(new FileOutputStream(iniFile));
			byte[] buffer = new byte[2048];
			for (;;) {
				int nBytes = in.read(buffer);
				if (nBytes <= 0)
					break;
				out.write(buffer, 0, nBytes);
			}
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
		}

		return retVal;
	}
}
