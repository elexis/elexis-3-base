/*******************************************************************************
 * Copyright (c) 2020-2022,  Fabian Schmid and Olivier Debenath
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Fabian <f.schmid@netzkonzept.ch> - initial implementation
 *    Olivier Debenath <olivier@debenath.ch>
 *
 *******************************************************************************/
package ch.netzkonzept.elexis.medidata.output;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Properties;

public class ApplicationDirectoryStructure {

	private static final String SEND_DIR_VALUE = "value.medidata.send.dir";
	private static final String SEND_PROCESSING_DIR_VALUE = "value.medidata.send.processing.dir";
	private static final String SEND_ERROR_DIR_VALUE = "value.medidata.send.error.dir";
	private static final String SEND_DONE_DIR_VALUE = "value.medidata.send.done.dir";
	private static final String RECEIVE_DIR_VALUE = "value.medidata.receive.dir";
	private static final String TEMP_DIR_VALUE = "value.medidata.temp.dir";
	
	private Path baseDir;
	private Path sendDir;
	private Path sendProcessingDir;
	private Path sendErrorDir;
	private Path sendDoneDir;
	private Path receiveDir;
	
	private Properties properties;
	
	public ApplicationDirectoryStructure(String baseDir, Properties properties) {
		setProperties(properties);
		setBaseDir(Paths.get(baseDir));
		setSendDir(Paths.get(MessageFormat.format(properties.getProperty(SEND_DIR_VALUE), baseDir)));
		setSendProcessingDir(Paths.get(MessageFormat.format(properties.getProperty(SEND_PROCESSING_DIR_VALUE), baseDir)));
		setSendErrorDir(Paths.get(MessageFormat.format(properties.getProperty(SEND_ERROR_DIR_VALUE), baseDir)));
		setSendDoneDir(Paths.get(MessageFormat.format(properties.getProperty(SEND_DONE_DIR_VALUE), baseDir)));
		setReceiveDir(Paths.get(MessageFormat.format(properties.getProperty(RECEIVE_DIR_VALUE), baseDir)));
	}
	
	public void create() {		
		try {
			Files.createDirectories(getBaseDir());
			Files.createDirectories(getSendDir());
			Files.createDirectories(getSendProcessingDir());
			Files.createDirectories(getSendErrorDir());
			Files.createDirectories(getSendDoneDir());
			Files.createDirectories(getReceiveDir());
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}

	public Path getBaseDir() {
		return baseDir;
	}

	private void setBaseDir(Path baseDir) {
		this.baseDir = baseDir;
	}

	public Path getSendDir() {
		return sendDir;
	}

	private void setSendDir(Path sendDir) {
		this.sendDir = sendDir;
	}

	public Path getSendProcessingDir() {
		return sendProcessingDir;
	}

	private void setSendProcessingDir(Path sendProcessingDir) {
		this.sendProcessingDir = sendProcessingDir;
	}

	public Path getSendErrorDir() {
		return sendErrorDir;
	}

	private void setSendErrorDir(Path sendErrorDir) {
		this.sendErrorDir = sendErrorDir;
	}

	public Path getSendDoneDir() {
		return sendDoneDir;
	}

	private void setSendDoneDir(Path sendDoneDir) {
		this.sendDoneDir = sendDoneDir;
	}

	public Path getReceiveDir() {
		return receiveDir;
	}

	private void setReceiveDir(Path receiveDir) {
		this.receiveDir = receiveDir;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
