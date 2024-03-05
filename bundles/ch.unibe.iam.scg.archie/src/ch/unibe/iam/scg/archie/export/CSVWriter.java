/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie.export;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ch.unibe.iam.scg.archie.model.AbstractDataProvider;
import ch.unibe.iam.scg.archie.model.DataSet;
import ch.unibe.iam.scg.archie.ui.widgets.DateWidget;
import ch.unibe.iam.scg.archie.utils.ProviderHelper;

/**
 * <p>
 * A simple CSV writer that takes care of exporting data from a given provider
 * into a CSV file.
 * </p>
 *
 * $Id: CSVWriter.java 734 2009-03-23 12:11:13Z peschehimself $
 *
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 734 $
 */
public class CSVWriter {

	/** Default delimiter for the CSV data */
	private static String DELIMITER = ";"; //$NON-NLS-1$

	/**
	 * Writes contents of a data provider into the given file in a CSV-format.
	 *
	 * @param provider A data provider.
	 * @param fileName A filename to write to.
	 * @return File File containing a data provider's content.
	 * @throws IOException
	 */
	public static File writeFile(final AbstractDataProvider provider, final String fileName) throws IOException {
		File file = new File(fileName);
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(fos, "ISO-8859-1");

		// retrieve DataSet
		DataSet data = provider.getDataSet();

		// write header - provider information
		CSVWriter.writeProviderInformation(writer, provider);

		// write headings
		CSVWriter.writeColumnHeadings(writer, data.getHeadings());

		// write rows from data
		for (Object[] objects : data) {
			CSVWriter.writeRow(writer, objects);
		}

		writer.close();
		return file;
	}

	/**
	 * Writes the heading for each column into the given file.
	 *
	 * @param writer   A FileWriter object.
	 * @param headings List of column headings to write.
	 * @throws IOException
	 */
	private static void writeColumnHeadings(OutputStreamWriter writer, List<String> headings) throws IOException {
		// write column headings
		Object[] objects = headings.toArray();
		CSVWriter.writeRow(writer, objects);
	}

	/**
	 * Writes the provider parameter information based on the given provider. This
	 * also adds the current date to the file being written, so that we know when
	 * the statistics were exported.
	 *
	 * @param writer   A FileWriter object.
	 * @param provider A data provider.
	 * @throws IOException
	 */
	private static void writeProviderInformation(OutputStreamWriter writer, final AbstractDataProvider provider)
			throws IOException {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateWidget.VALID_DATE_FORMAT);

		// provider title
		writer.write(provider.getName() + StringUtils.LF);
		writer.write(dateFormat.format(Calendar.getInstance().getTime()) + StringUtils.LF + StringUtils.LF);

		// write parameters
		Map<String, Object> getters = ProviderHelper.getGetterMap(provider, true);
		for (Object name : getters.keySet().toArray()) {
			writer.write(name + " = " + getters.get(name) + StringUtils.LF); //$NON-NLS-1$
		}
		writer.write(StringUtils.LF + StringUtils.LF);
	}

	/**
	 * Writes a row of given data to the output file.
	 *
	 * @param writer  A FileWriter object.
	 * @param objects An array of objects containing the data to write.
	 * @throws IOException
	 */
	private static void writeRow(OutputStreamWriter writer, Object[] objects) throws IOException {
		StringBuffer buf = new StringBuffer();
		for (Object obj : objects) {
			buf.append(obj != null ? obj.toString() : StringUtils.EMPTY);
			buf.append(CSVWriter.DELIMITER);
		}
		buf.deleteCharAt(buf.length() - 1); // last delimiter not needed
		buf.append(StringUtils.LF);
		writer.write(buf.toString());
	}
}
