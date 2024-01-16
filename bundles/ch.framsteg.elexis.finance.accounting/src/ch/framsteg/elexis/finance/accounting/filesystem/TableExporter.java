/*******************************************************************************
 * Copyright (c) 2020-2022,  Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olivier Debenath <olivier@debenath.ch> - initial implementation
 *    
 *******************************************************************************/
package ch.framsteg.elexis.finance.accounting.filesystem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class TableExporter {

	private final static String DATE_TIME_FORMAT = "app.date.time.format.long";
	private final static String OUTPUT_DIR = "pref.output.dir";
	private final static String OUTPUT_FILE_NAME = "pref.output.file.name";
	private final static String CHARSET = "app.charset";

	private Properties applicationProperties;
	private Properties messagesProperties;

	private final SettingsPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.globalCfg);

	public TableExporter(Properties applicationProperties, Properties messagesProperties) {
		setApplicationProperties(applicationProperties);
		setMessagesProperties(messagesProperties);
	}
	
	public void export(StringBuilder content) throws IOException {

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter
				.ofPattern(getApplicationProperties().getProperty(DATE_TIME_FORMAT));
		LocalDateTime now = LocalDateTime.now();

		Path p = Paths.get(preferenceStore.getString(getApplicationProperties().getProperty(OUTPUT_DIR)),
				MessageFormat.format(
						preferenceStore.getString(getApplicationProperties().getProperty(OUTPUT_FILE_NAME)),
						dateTimeFormatter.format(now)));
		try (BufferedWriter writer = Files.newBufferedWriter(p,
				Charset.forName(getApplicationProperties().getProperty(CHARSET)))) {
			writer.write(content.toString());
		} catch (IOException ioe) {
			throw ioe;
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
