/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.finance.analytics.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class CsvExporter {

	private Properties applicationProperties;
	private Properties messagesProperties;

	private final static String DELIMITER = "app.delimiter";
	private final static String NEW_LINE = "app.newline";
	private final static String CHARSET = "app.charset";
	private final static String DATE_TIME_FORMAT = "date.format";
	
	private final static String EXTENSION_ALL="file.save.dialog.extension.all";
	private final static String EXTENSION_CSV="file.save.dialog.extension.csv";
	private final static String EXTENSION_CSV_SHORT="file.save.dialog.extension.csv.short";
	
	private final static String FILTER_ALL="file.save.dialog.filter.all";
	private final static String FILTER_CSV="file.save.dialog.filter.csv";
	
	private final static String DASH="-";

	public CsvExporter(Properties applicationProperties, Properties messagesProperties) {
		setApplicationProperties(applicationProperties);
		setMessagesProperties(messagesProperties);
	}

	public void export(Shell shell, ArrayList<String[]> lines, String filenamePart) {

		StringBuilder filteredTableContent = new StringBuilder();

		for (String[] s : lines) {
			for (int a = 0; a < s.length; a++) {
				filteredTableContent.append(s[a]);
				if (a < s.length - 1) {
					filteredTableContent.append(getApplicationProperties().getProperty(DELIMITER));
				}
			}
			filteredTableContent.append(getApplicationProperties().getProperty(NEW_LINE));
		}
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter
				.ofPattern(getApplicationProperties().getProperty(DATE_TIME_FORMAT));
		LocalDateTime now = LocalDateTime.now();
		String datePart = dateTimeFormatter.format(now);

		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setFilterNames(new String[] { getApplicationProperties().getProperty(FILTER_CSV), getApplicationProperties().getProperty(FILTER_ALL) });
		dialog.setFilterExtensions(new String[] { getApplicationProperties().getProperty(EXTENSION_CSV), getApplicationProperties().getProperty(EXTENSION_ALL) });

		dialog.setFilterPath(System.getProperty("user.home"));
		dialog.setFileName(datePart + DASH + filenamePart + getApplicationProperties().getProperty(EXTENSION_CSV_SHORT));
		dialog.open();
		Path p = Paths.get(dialog.getFilterPath() + System.getProperty("file.separator") + dialog.getFileName());
		try {
			BufferedWriter writer = Files.newBufferedWriter(p,
					Charset.forName(getApplicationProperties().getProperty(CHARSET)));
			writer.write(filteredTableContent.toString());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}
}
