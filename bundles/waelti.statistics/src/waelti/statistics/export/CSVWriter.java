package waelti.statistics.export;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

		FileWriter writer = new FileWriter(file);

		CSVWriter.writeHeading(writer, matrix.getHeadings());

		for (Object[] objects : matrix) {
			CSVWriter.writeRow(writer, objects);
		}

		writer.close();
		return file;
	}

	private static void writeHeading(FileWriter writer, List<String> headings) throws IOException {
		Object[] objects = headings.toArray();
		CSVWriter.writeRow(writer, objects);
	}

	private static void writeRow(FileWriter writer, Object[] objects) throws IOException {
		StringBuffer buf = new StringBuffer();
		for (Object obj : objects) {
			buf.append(obj.toString());
			buf.append(CSVWriter.delimiter);
		}
		buf.deleteCharAt(buf.length() - 1); // last delimiter not needed
		buf.append(StringUtils.LF);
		writer.write(buf.toString());
	}

}
