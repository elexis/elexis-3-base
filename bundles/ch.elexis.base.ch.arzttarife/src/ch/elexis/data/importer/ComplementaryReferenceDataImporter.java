package ch.elexis.data.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.data.ComplementaryLeistung;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

public class ComplementaryReferenceDataImporter extends AbstractReferenceDataImporter {
	private static final Logger logger =
		LoggerFactory.getLogger(ComplementaryReferenceDataImporter.class);
	
	private int chapternr_index = 4;
	private int chaptertext_index = 5;
	
	private int code_index = 8;
	private int codetext_index = 9;
	
	private int description_index = 12;
	
	private int validfrom_index = 15;
	private int validto_index = 16;
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, InputStream input,
		@Nullable Integer newVersion){
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		CSVReader reader;
		try {
			reader = new CSVReader(new InputStreamReader(input, "ISO-8859-1"), ';');
			monitor.beginTask("Import Complementary", IProgressMonitor.UNKNOWN);
			
			if (PersistentObject.tableExists(ComplementaryLeistung.TABLENAME)) {
				Stm statement = null;
				try {
					statement =
						PersistentObject.getDefaultConnection().getJdbcLink().getStatement();
					statement.exec("DELETE FROM " + ComplementaryLeistung.TABLENAME);
				} finally {
					if (statement != null) {
						PersistentObject.getDefaultConnection().getJdbcLink()
							.releaseStatement(statement);
					}
				}
			}
			
			try {
				updateIndexForLang();
				
				String[] line = reader.readNext();
				while ((line = reader.readNext()) != null) {
					if (line.length < validto_index + 1) {
						continue;
					}
					if (line[0] != null && !line[0].isEmpty() && "590".equals(line[0])) {
						monitor.subTask(line[codetext_index]);
						
						String validFromString = getValidDateString(line[validfrom_index]);
						String validToString = getValidDateString(line[validto_index]);
						String chapterString =
							line[chapternr_index] + " " + line[chaptertext_index];
						
						String id = line[code_index] + "-" + validFromString;
						new ComplementaryLeistung(id, chapterString, line[code_index],
							line[codetext_index], line[description_index], validFromString,
							validToString);
					}
				}
				monitor.done();
				return Status.OK_STATUS;
			} catch (IOException ioe) {
				logger.error("Could not import complementary tarif", ioe);
				return Status.CANCEL_STATUS;
			}
		} catch (UnsupportedEncodingException uee) {
			logger.error("Could not import complementary tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}
	
	private DateTimeFormatter csvDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private DateTimeFormatter elexisDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	private String getValidDateString(String string){
		LocalDate date = LocalDate.parse(string, csvDateTimeFormatter);
		return elexisDateTimeFormatter.format(date);
	}
	
	private void updateIndexForLang(){
		String lang =
			JdbcLink.wrap(CoreHub.localCfg.get(Preferences.ABL_LANGUAGE, "d").toUpperCase()); //$NON-NLS-1$
		int offset = 0;
		if ("I".equals(lang)) {
			offset = 2;
		} else if ("F".equals(lang)) {
			offset = 1;
		}
		codetext_index += offset;
		chaptertext_index += offset;
		description_index += offset;
	}
	
	@Override
	public int getCurrentVersion(){
		// currently the dataset is not versioned
		return 0;
	}
	
}
