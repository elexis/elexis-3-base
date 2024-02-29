package waelti.statistics.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Locale;

import ch.rgw.tools.Money;
import waelti.statistics.queries.AbstractQuery;
import waelti.statistics.queries.ResultMatrix;

public class CSVWriter {

	private static String delimiter = ";";

	public static File write(AbstractQuery query, String file) throws IOException {
		return CSVWriter.write(query, new File(file));
	}

	/**
	 * Writes the data to a csv file with ';' as a delimiter and returns the file.
	 * If writing fails, null will be returned.
	 *
	 * @throws IOException
	 */
	public static File write(AbstractQuery query, File file) throws IOException {
		ResultMatrix matrix = query.getMatrix();
		return CSVWriter.writer(matrix, file);
	}

	public static File writer(ResultMatrix matrix, File file) throws IOException {

		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "ISO-8859-1");

		try {
			CSVWriter.writeHeading(osw, matrix.getHeadings());

			for (Object[] objects : matrix) {
				CSVWriter.writeRow(osw, objects);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			osw.close();
		}

		return file;
	}

	private static void writeHeading(OutputStreamWriter writer, List<String> headings) throws IOException {
		Object[] objects = headings.toArray();
		CSVWriter.writeRow(writer, objects);
	}

	private static void writeRow(OutputStreamWriter writer, Object[] objects) throws IOException {
		StringBuilder buf = new StringBuilder();
		for (Object obj : objects) {
			// Überprüfung, ob der Wert eine Zahl ist
			if (obj instanceof Number) {
				if (obj instanceof Money) {
					buf.append(obj.toString()); // Kommas durch Punkte ersetzen

				} else {
					buf.append(String.format(Locale.US, "%.2f", obj)); // Dezimalzahlen mit Punkt statt Komma
				}
			} else {
				buf.append(obj.toString()); // Kommas durch Punkte ersetzen
			}
			buf.append(CSVWriter.delimiter);
		}

		buf.deleteCharAt(buf.length() - 1); // letztes Trennzeichen nicht benötigt
		buf.append(System.lineSeparator());
		writer.write(buf.toString());
	}
}
