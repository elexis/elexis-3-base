package ch.elexis.base.ch.arzttarife.complementary.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.jpa.entities.ComplementaryLeistung;
import com.opencsv.CSVReader;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=complementary")
public class ComplementaryReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {
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
			
			ArzttarifeModelServiceHolder.get()
				.executeNativeUpdate(
					"DELETE FROM CH_ELEXIS_ARZTTARIFE_CH_COMPLEMENTARY WHERE ID NOT LIKE '%sub%'");
			
			updateIndexForLang();
			
			List<Object> imported = new ArrayList<>();
			String[] line = reader.readNext();
			while ((line = reader.readNext()) != null) {
				if (line.length < validto_index + 1) {
					continue;
				}
				if (line[0] != null && !line[0].isEmpty() && "590".equals(line[0])) {
					monitor.subTask(line[codetext_index]);
					
					LocalDate validFrom =
						LocalDate.parse(line[validfrom_index], csvDateTimeFormatter);
					LocalDate validTo = LocalDate.parse(line[validto_index], csvDateTimeFormatter);
					String chapterString = line[chapternr_index] + " " + line[chaptertext_index];
					
					String id = line[code_index] + "-" + validFrom.format(elexisDateTimeFormatter);
					ComplementaryLeistung complementary = new ComplementaryLeistung();
					complementary.setId(id);
					complementary.setChapter(chapterString);
					complementary.setCode(line[code_index]);
					complementary.setCodeText(line[codetext_index]);
					complementary.setDescription(line[description_index]);
					complementary.setValidFrom(validFrom);
					complementary.setValidTo(validTo);
					imported.add(complementary);
				}
			}
			EntityUtil.save(imported);
			monitor.done();
			return Status.OK_STATUS;
		} catch (IOException uee) {
			logger.error("Could not import complementary tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}
	
	private DateTimeFormatter csvDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private DateTimeFormatter elexisDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	private void updateIndexForLang(){
		String lang =
			ConfigServiceHolder.get().get().get(Preferences.ABL_LANGUAGE, "d").toUpperCase();
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
