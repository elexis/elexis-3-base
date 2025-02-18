package ch.elexis.base.ch.arzttarife.complementary.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.jpa.entities.ComplementaryLeistung;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=complementary")
public class ComplementaryReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {
	private static final String REFERENCEDATA_COMPLEMENTARY_VERSION = "referencedata/complementary/version";

	private static final Logger logger = LoggerFactory.getLogger(ComplementaryReferenceDataImporter.class);

	private int chapternr_index = 4;
	private int chaptertext_index = 5;

	private int code_index = 8;
	private int codetext_index = 9;

	private int description_index = 12;

	private int validfrom_index = 15;
	private int validto_index = 16;

	private void updateIndexes(List<String> line) {
		if (line != null && !line.isEmpty()) {
			if (line.contains("Kapitelbezeichung_D") && line.contains("ABRECHNUNGSZIFFERTEXT_D")
					&& line.contains("DATUMVON")) {
				chapternr_index = line.indexOf("Kapitelziffer");
				chaptertext_index = line.indexOf("Kapitelbezeichung_D");

				code_index = line.indexOf("Abrechnungsziffer");
				codetext_index = line.indexOf("ABRECHNUNGSZIFFERTEXT_D");

				description_index = line.indexOf("Beschreibung");

				validfrom_index = line.indexOf("DATUMVON");
				validto_index = line.indexOf("DATUMBIS");
			} else {
				throw new IllegalStateException("Unknown CSV file format");
			}
		}
	}

	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, InputStream input, @Nullable Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		CSVReader reader;
		try {
			reader = new CSVReaderBuilder(new InputStreamReader(input, "ISO-8859-1"))
					.withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();

			monitor.beginTask("Import Complementary", IProgressMonitor.UNKNOWN);

			List<Object> imported = new ArrayList<>();
			List<Object> closed = new ArrayList<>();
			String[] line = reader.readNext();
			updateIndexes(Arrays.asList(line));
			updateIndexForLang();
			while ((line = reader.readNext()) != null) {
				if (line.length < validto_index + 1) {
					continue;
				}
				if (StringUtils.isNotBlank(line[0]) && "590".equals(line[0])
						&& StringUtils.isNotBlank(line[code_index])) {
					monitor.subTask(line[codetext_index]);

					LocalDate validFrom = LocalDate.parse(line[validfrom_index], csvDateTimeFormatter);
					LocalDate validTo = LocalDate.parse(line[validto_index], csvDateTimeFormatter);

					ComplementaryLeistung existing = getExisting(line[code_index], validFrom);
					if (existing != null) {
						if ((existing.getValidTo() == null || !existing.getValidTo().equals(validTo))) {
							// update validto of existing
							existing.setValidTo(validTo);
							closed.add(existing);
						}
					} else {
						imported.add(createComplementary(line, validFrom, validTo));
					}
				}
			}
			LoggerFactory.getLogger(getClass())
					.info("Closing " + closed.size() + " and creating " + imported.size() + " tarifs");
			EntityUtil.save(imported);
			EntityUtil.save(closed);
			monitor.done();
			if (newVersion != null) {
				ConfigServiceHolder.get().get().set(REFERENCEDATA_COMPLEMENTARY_VERSION, newVersion);
			}
			return Status.OK_STATUS;
		} catch (IOException | CsvValidationException uee) {
			logger.error("Could not import complementary tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}

	private ComplementaryLeistung createComplementary(String[] line, LocalDate validFrom, LocalDate validTo) {
		String chapterString = line[chapternr_index] + StringUtils.SPACE + line[chaptertext_index];
		String id = getId(line[code_index], validFrom);
		ComplementaryLeistung complementary = new ComplementaryLeistung();
		complementary.setId(id);
		complementary.setChapter(chapterString);
		complementary.setCode(line[code_index]);
		complementary.setCodeText(line[codetext_index]);
		complementary.setDescription(line[description_index]);
		complementary.setValidFrom(validFrom);
		complementary.setValidTo(validTo);
		return complementary;
	}

	private String getId(String code, LocalDate validFrom) {
		return code + "-" + validFrom.format(elexisDateTimeFormatter);
	}

	private DateTimeFormatter csvDateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private DateTimeFormatter elexisDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private void updateIndexForLang() {
		String lang = ConfigServiceHolder.get().get().get(Preferences.ABL_LANGUAGE, "d").toUpperCase();
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

	private ComplementaryLeistung getExisting(String code, LocalDate validFrom) {
		return EntityUtil.load(getId(code, validFrom), ComplementaryLeistung.class);
	}

	@Override
	public int getCurrentVersion() {
		return ConfigServiceHolder.get().get().get(REFERENCEDATA_COMPLEMENTARY_VERSION, 0);
	}

}
