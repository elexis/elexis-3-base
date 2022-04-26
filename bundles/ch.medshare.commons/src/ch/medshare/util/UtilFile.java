package ch.medshare.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.zip.GZIPInputStream;

public class UtilFile {
	public static String DIRECTORY_SEPARATOR = File.separator;

	public static final String ZIP_EXTENSION = ".gz"; //$NON-NLS-1$

	private static String getCorrectSeparators(final String pathOrFilename) {
		return pathOrFilename.replace("\\", DIRECTORY_SEPARATOR).replace("//", //$NON-NLS-1$ //$NON-NLS-2$
				DIRECTORY_SEPARATOR).replace("/", DIRECTORY_SEPARATOR); //$NON-NLS-1$
	}

	private static String removeMultipleSeparators(String pathOrFilename) {
		String doubleSeparator = DIRECTORY_SEPARATOR + DIRECTORY_SEPARATOR;
		if (pathOrFilename.indexOf(doubleSeparator) >= 0) {
			pathOrFilename = pathOrFilename.replace(doubleSeparator, DIRECTORY_SEPARATOR);
		}
		return pathOrFilename;
	}

	/**
	 * Überprüft ob Verzeichnis korrekt ist. Falls nicht, wird das Verzeichnis
	 * korrigiert.
	 *
	 * @param path oder null
	 */
	public static String getCorrectPath(String path) throws IllegalArgumentException {
		if (path == null) {
			return ""; //$NON-NLS-1$
		}
		path = getCorrectSeparators(path);
		path = removeMultipleSeparators(path);
		if (!path.endsWith(DIRECTORY_SEPARATOR)) {
			path += DIRECTORY_SEPARATOR;
		}
		return path;
	}

	/**
	 * Überprüft, ob eine Datei existiert
	 */
	public static boolean doesFileExist(final String filePathName) {
		File file = new File(filePathName);
		return file.isFile() && file.exists();
	}

	/**
	 * Überprüft, ob es sich um ein absolutes Verzeichnis handelt
	 */
	public static boolean isRootDir(String dir) {
		return (dir.startsWith(DIRECTORY_SEPARATOR) || dir.indexOf(":") > 0);// Linux & Windows Root //$NON-NLS-1$
	}

	/**
	 * Löscht Datei
	 *
	 * @param filePathName Filename with path information
	 * @return true if successful deleted, otherwise false
	 */
	public static boolean deleteFile(final String filePathName) throws IllegalArgumentException {
		if (doesFileExist(filePathName)) {
			File file = new File(filePathName);
			return file.delete();
		}
		return true;
	}

	/**
	 * Liest serialisiertes Java Objekt
	 */
	public static Object readObject(final String fileNamePath) throws IOException, ClassNotFoundException {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(fileNamePath);
			in = new ObjectInputStream(fis);
			return in.readObject();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * Liest binäre gezippte Datei
	 */
	public static byte[] readZippedFile(final String filenamePath) throws IOException {
		byte[] daten = new byte[1024];
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				GZIPInputStream in = new GZIPInputStream(new FileInputStream(filenamePath))) {
			int read = 0;
			while ((read = in.read(daten, 0, 1024)) != -1)
				out.write(daten, 0, read);
			return out.toByteArray();
		}
	}

	/**
	 * Liest binäre Datei. Vorsicht bei grossen Dateien. Diese können zu einem
	 * OutOfMemory Error führen. Grosse Dateien sollten wenn möglich in einzelnen
	 * Blöcken (InputStream) gelesen werden.
	 */
	public static byte[] readFile(final String fileNamePath) throws IOException {
		FileInputStream input = null;
		byte[] daten = null;
		try {
			input = new FileInputStream(fileNamePath);
			daten = new byte[input.available()];
			input.read(daten);
		} finally {
			if (input != null) {
				input.close();
			}
		}
		return daten;
	}

	/**
	 * Liest Text Datei
	 */
	public static String readTextFile(final String fileNamePath) throws IOException {
		return readTextFile(fileNamePath, Charset.defaultCharset().name());
	}

	/**
	 * Liest Text Datei
	 */
	public static String readTextFile(final String fileNamePath, final String charsetName) throws IOException {
		byte[] text = readFile(fileNamePath);
		return new String(text, charsetName);
	}

	/**
	 * Schreibt (serialisiert) Java Objekt
	 */
	public static void writeObject(final String fileNamePath, Object obj) throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(fileNamePath);
			out = new ObjectOutputStream(fos);
			out.writeObject(obj);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * Schreibt binäre Datei
	 */
	public static void writeFile(final String fileNamePath, final byte[] daten) throws IOException {
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(fileNamePath);
			output.write(daten);
		} finally {
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * Schreibt Text Datei
	 */
	public static void writeTextFile(final String fileNamePath, final String text) throws IOException {
		if (text != null) {
			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(fileNamePath));

				bw.write(text);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}
		}
	}

	/**
	 * Copy file 'from' to file 'to' If not successful, exception is thrown
	 */
	public static void copyFile(final String from, final String to) throws IOException {
		byte[] daten = readFile(from);
		writeFile(to, daten);
	}

	/**
	 * Move file 'from' to file 'to'
	 *
	 * @return true if successful deleted, otherwise false or exception
	 */
	public static boolean moveFile(final String from, final String to) throws IOException {
		byte[] daten = readFile(from);
		writeFile(to, daten);
		// Write was ok. Now delete the file
		return deleteFile(from);
	}

	/**
	 * Retourniert Pfad ohne Dateinamen als String
	 */
	public static String getFilepath(final String filenamePath) {
		String correctFilenamePath = getCorrectSeparators(filenamePath);

		if (correctFilenamePath.indexOf(DIRECTORY_SEPARATOR) < 0) {
			return ""; //$NON-NLS-1$
		}
		return correctFilenamePath.substring(0, correctFilenamePath.lastIndexOf(DIRECTORY_SEPARATOR));
	}

	/**
	 * Retourniert Dateinamen ohne Pfad als String
	 */
	public static String getFilename(final String filenamePath) {
		String correctFilenamePath = getCorrectSeparators(filenamePath);

		if (correctFilenamePath.indexOf(DIRECTORY_SEPARATOR) < 0) {
			return filenamePath;
		}
		return correctFilenamePath.substring(correctFilenamePath.lastIndexOf(DIRECTORY_SEPARATOR) + 1,
				correctFilenamePath.length());
	}

	/**
	 * Retourniert Dateinamen ohne Pfad und Endung. Falls keine Endung vorhanden
	 * ist, wird der Dateinamen retourniert.
	 */
	public static String getNakedFilename(final String filenamePath) {
		String filename = getFilename(filenamePath);

		if (filename.lastIndexOf(".") > 0) { //$NON-NLS-1$
			return filename.substring(0, filename.lastIndexOf(".")); //$NON-NLS-1$
		}

		return filename;
	}

	/**
	 * Retourniert Dateiendung (mit Punkt). Falls keine Endung gefunden wird, wird
	 * ein leerer String retourniert.
	 */
	public static String getFileExtension(final String filenamePath) {
		String filename = getFilename(filenamePath);

		if (filename.lastIndexOf(".") > 0) { //$NON-NLS-1$
			return filename.substring(filename.lastIndexOf("."), filename.length()); //$NON-NLS-1$

		}

		return ""; //$NON-NLS-1$
	}

	/**
	 * Fügt die beiden Pfade zusammen. Fügt Separator ein, achtet darauf dass nicht
	 * doppelter Separator
	 *
	 * @param sPart1
	 * @param sPart2
	 * @return
	 */
	public static String concatenatePath(String sPart1, String sPart2) {
		if (sPart1 == null || sPart2 == null) {
			System.err.println("cannot concatenate nulls, part1: " + sPart1 //$NON-NLS-1$
					+ " part2: " + sPart2); //$NON-NLS-1$
			return null;
		}

		if (!sPart1.endsWith(DIRECTORY_SEPARATOR)) {
			sPart1 += DIRECTORY_SEPARATOR;
		}
		if (sPart2.startsWith(DIRECTORY_SEPARATOR)) {
			sPart2 = sPart2.substring(1);
		}
		return sPart1 + sPart2;
	}

	/**
	 * Fügt die drei Pfade zusammen. Fügt Separator ein, achtet darauf dass nicht
	 * doppelter Separator
	 *
	 * @param parts  Die einzelnen Pfadteile als String
	 * @param sPart2
	 * @param sPart3
	 * @return
	 */
	public static String concatenatePaths(String[] parts) {
		StringBuilder sb = new StringBuilder();
		if (parts != null) {
			for (int i = 0; i < parts.length; i++) {
				String sPart = parts[i];
				if ((!sPart.endsWith(DIRECTORY_SEPARATOR)) && (i != parts.length - 1)) {
					sPart += DIRECTORY_SEPARATOR;
				}
				sb.append(sPart);
			}
		}
		return sb.toString();
	}

	/**
	 * Delete a Directory with all of its content
	 *
	 * @param Directory to Delete
	 * @return true if successful, otherwise false
	 */
	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Überprüft, ob Verzeichnis existiert. Falls nicht, wird probiert, das
	 * Verzeichnis zu erstellen.
	 *
	 * @param path , darf nicht null sein.
	 */
	public static void checkCreatePath(final String path) throws IllegalArgumentException {
		File dir = new File(path);
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				throw new IllegalArgumentException(Messages.UtilFile_error_msg_invalidPath, null);
			}
		} else {
			if (!dir.mkdirs()) {
				String msg = MessageFormat.format(Messages.UtilFile_error_msg_creationFailed, new Object[] { path });
				throw new IllegalArgumentException(msg, null);
			}
		}
	}
}
