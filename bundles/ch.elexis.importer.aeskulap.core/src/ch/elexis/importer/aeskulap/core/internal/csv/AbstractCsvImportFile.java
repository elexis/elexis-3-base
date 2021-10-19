package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import ch.elexis.core.data.interfaces.IPersistentObject;
import ch.elexis.data.Xid;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile.Type;

public abstract class AbstractCsvImportFile<T> {
	
	private File csvFile;
	
	private CSVReader reader;
	
	public AbstractCsvImportFile(File csvFile){
		this.csvFile = csvFile;
	}
	
	public abstract boolean isHeaderLine(String[] line);
	
	public abstract String getXidDomain();
	
	@SuppressWarnings("unchecked")
	public T getExisting(String id){
		String domain = getXidDomain();
		if (domain != null) {
			Xid existingXid = Xid.findXID(domain, id);
			if (existingXid != null) {
				IPersistentObject ret = existingXid.getObject();
				if (getType().checkElexisClass(ret)) {
					return (T) ret;
				} else {
					LoggerFactory.getLogger(getClass()).error("XID [" + domain + "] [" + id
						+ "] referes to object of wrong type [" + ret.getClass() + "]");
				}
			}
		}
		return null;
	}
	
	public abstract T create(String[] line);
	
	public abstract void setProperties(T contact, String[] line);
	
	public abstract Type getType();
	
	public String[] getNextLine() throws IOException{
		if (reader == null) {
			reader = new CSVReader(new FileReader(csvFile), ',', '"');
			String[] firstLine = reader.readNext();
			// file is empty
			if (firstLine == null || firstLine.length == 0) {
				return null;
			}
			// test if the first line is a header line, if so return next line
			if (isHeaderLine(firstLine)) {
				return reader.readNext();
			} else {
				return firstLine;
			}
		}
		return reader.readNext();
	}
	
	public int getLineCount(){
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(csvFile));
			try {
				byte[] c = new byte[1024];
				int count = 0;
				int readChars = 0;
				boolean empty = true;
				while ((readChars = is.read(c)) != -1) {
					empty = false;
					for (int i = 0; i < readChars; ++i) {
						if (c[i] == '\n') {
							++count;
						}
					}
				}
				return (count == 0 && !empty) ? 1 : count;
			} finally {
				is.close();
			}
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error getting line count", e);
		}
		return 1;
	}
	
	public void close(){
		try {
			reader.close();
		} catch (IOException e) {
			// ignore
		}
	}
}
