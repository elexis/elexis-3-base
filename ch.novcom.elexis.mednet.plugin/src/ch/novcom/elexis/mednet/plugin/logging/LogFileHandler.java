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

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * This FileHandler is like an easy FileHandler for logging.
 * It has the advantage not to lock the file.
 * @author David Gutknecht
 *
 */
public class LogFileHandler extends Handler{
	private static final Charset CHARSET = StandardCharsets.ISO_8859_1;
	private static final String FILENAME = "MedNet.log";
	private static final int FILEMAXSIZE = 2; /* Max logfile size - in MByte */
	private final static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	
	/**
	 * The file were the logs will be written
	 */
	private Path logFile=null;
	
	/**
	 * The formatter that define how the records will look like
	 */
	private LogFormatter logFormatter = null;
	
	/**
	 * Create a new LogFileHandler
	 * @param the path
	 * @throws Exception 
	 */
	public LogFileHandler(Path path) {
			if(		Files.isDirectory(path)
				&&	Files.isWritable(path)	){
				this.logFile = path.resolve(LogFileHandler.FILENAME);
				this.logFormatter = new LogFormatter();
			}
	}
	
	
	@Override
	public void close() throws SecurityException {
		//Nothing to do
	}

	@Override
	public void flush() {
		//Nothing to do
	}

	/**
	 * Write a log record to the logFile
	 */
	@Override
	public void publish(LogRecord record) {
		List<String> lines = new ArrayList<String>();
		lines.add(this.logFormatter.format(record));
		try {
			//Write the log
			Files.write(
				this.logFile,
				lines,
				LogFileHandler.CHARSET,
				StandardOpenOption.APPEND,
				StandardOpenOption.CREATE	
				);
			
			//If the file is bigger than 2MB move it to another filename
			if(Files.size(this.logFile) > (LogFileHandler.FILEMAXSIZE * 1024 * 1024) ){
				Files.move(
					this.logFile,
					this.logFile.resolveSibling(
							dayFormat.format(Calendar.getInstance().getTime())
						+	"_" + LogFileHandler.FILENAME
					),
					StandardCopyOption.REPLACE_EXISTING		//In this case we replace existing, since the chance to have the same file in this folder
															//is not so big. And if it the case, it means that the converter produce more than 2MB Logs per second
															//And it should not be normal.
				);
			}
		} catch (IOException e) {
			//Unable to write to the file
		}
		
	}

}
