package ch.elexis.base.ch.arzttarife.ambulatory.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.AmbulantePauschalen;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=ambulatorytarif")
public class AmbulatoryTarifsReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {

	private CSVReader reader;

	private String[] headers;

	private String lang;

	private String version;

	private long readLines;

	private InputStream input;

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		try {
			monitor.beginTask("Ambulantetarife Import", 100);
			this.input = input;
			lang = getLang();

			List<Object> imported = new ArrayList<>();
			List<Object> closed = new ArrayList<>();

			Optional<Map<String, String>> line = Optional.empty();
			while ((line = getNextLine()).isPresent()) {
				if (version == null) {
					version = line.get().get("version");
					LoggerFactory.getLogger(getClass()).info("Start importing version " + version);
				} else {
					if (!version.equals(line.get().get("version"))) {
						LoggerFactory.getLogger(getClass()).info("Stop importing version " + version
								+ " encountered version " + line.get().get("version"));
						break;
					}
				}
				monitor.subTask(line.get().get("code"));
				String linetype = line.get().get("type");
				if (linetype != null && (linetype.equals("P") || linetype.equals("PZ"))) {
					List<AmbulantePauschalen> existing = getExisting(line.get().get("lkn"), getValidFrom(line.get()));
					if (!existing.isEmpty()) {
						for (AmbulantePauschalen ambulantePauschale : existing) {
							if (ambulantePauschale.getValidTo().equals(getValidTo(line.get()))) {
								// update fields of existing
								ambulantePauschale.setCode(line.get().get("lkn"));
								ambulantePauschale.setText(getText(line.get()));
								ambulantePauschale.setValidFrom(getValidFrom(line.get()));
								ambulantePauschale.setValidTo(getValidTo(line.get()));
								imported.add(ambulantePauschale);
							} else {
								// update validto of existing
								ambulantePauschale.setValidTo(getValidTo(line.get()));
								closed.add(ambulantePauschale);
							}
						}
					} else {
							AmbulantePauschalen ambulantePauschale = new AmbulantePauschalen();
							ambulantePauschale.setTyp(AmbulantePauschalenTyp.TRIGGER.getCode());
							ambulantePauschale.setCode(line.get().get("lkn"));
							ambulantePauschale.setText(getText(line.get()));
							ambulantePauschale.setValidFrom(getValidFrom(line.get()));
							ambulantePauschale.setValidTo(getValidTo(line.get()));
							imported.add(ambulantePauschale);
					}
				}
			}
			LoggerFactory.getLogger(getClass())
					.info("Closing " + closed.size() + " and creating " + imported.size() + " tarifs");
			EntityUtil.save(closed);
			EntityUtil.save(imported);
			monitor.done();

			if (newVersion != null) {
				setCurrentVersion(newVersion);
			}
			return Status.OK_STATUS;
		} catch (IOException uee) {
			LoggerFactory.getLogger(getClass()).error("Could not import ambulatory allowance tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}

	private String getLang() {
		String ret = ConfigServiceHolder.get().get().getLocal(Preferences.ABL_LANGUAGE, "de");//$NON-NLS-1$
		if (ret.toLowerCase().equals("d")) {
			ret = "de";
		} else if (ret.toLowerCase().equals("f")) {
			ret = "fr";
		} else if (ret.toLowerCase().equals("i")) {
			ret = "it";
		}
		return ret;
	}

	private Optional<Map<String, String>> getNextLine() throws IOException {
		if (reader == null) {
			reader = new CSVReaderBuilder(new InputStreamReader(input, "UTF-8"))
					.withCSVParser(new CSVParserBuilder().withSeparator(',').withQuoteChar('"').build())
					.withKeepCarriageReturn(false).build();
			headers = reader.readNext();
			// file is empty
			if (headers == null || headers.length == 0) {
				return Optional.empty();
			}
			readLines = 0;
		}
		readLines++;
		return Optional.ofNullable(toMap(reader.readNext()));
	}

	private Map<String, String> toMap(String[] values) {
		if (headers != null && values != null) {
			Map<String, String> ret = new HashMap<String, String>();
			for (int i = 0; i < headers.length; i++) {
				ret.put(new String(headers[i]), values[i]);
			}
			return ret;
		}
		return null;
	}

	private String getText(Map<String, String> line) {
		return StringUtils
				.abbreviate(line.get("title_" + lang).replace(StringUtils.LF, ";").replace(StringUtils.CR,
						StringUtils.EMPTY), 255);
	}

	private List<AmbulantePauschalen> getExisting(String code, LocalDate validFrom) {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("typ", AmbulantePauschalenTyp.TRIGGER);
		propertyMap.put("code", code);
		propertyMap.put("validFrom", validFrom);
		return EntityUtil.loadByNamedQuery(propertyMap, AmbulantePauschalen.class);
	}

	private LocalDate getValidFrom(Map<String, String> line) {
		return getLocalDate(line.get("valid_start").trim());
	}

	private LocalDate getValidTo(Map<String, String> line) {
		if (StringUtils.isNotBlank(line.get("valid_end").trim())) {
			return getLocalDate(line.get("valid_end").trim());
		} else {
			return LocalDate.MAX;
		}
	}

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private LocalDate getLocalDate(String value) {
		try {
			if (value.isEmpty()) {
				return LocalDate.parse("01.01.2026", dateFormatter);
			} else if (value.length() < 11) {
				return LocalDate.parse(value, dateFormatter);

			} else {
				return LocalDate.parse(value, dateTimeFormatter);
			}
		} catch (DateTimeParseException pe) {
			LoggerFactory.getLogger(getClass()).error("Could not parse as local date [" + value + "]");
			throw pe;
		}
	}

	public static void setCurrentVersion(int newVersion) {
		AmbulantePauschalen versionEntry = EntityUtil.load("VERSION", AmbulantePauschalen.class);
		if (versionEntry != null) {
			versionEntry.setChapter(Integer.toString(newVersion));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry");
	}

	@Override
	public int getCurrentVersion() {
		AmbulantePauschalen versionEntry = EntityUtil.load("VERSION", AmbulantePauschalen.class);
		if (versionEntry != null) {
			String chapter = versionEntry.getChapter();
			if (chapter != null) {
				try {
					return Integer.parseInt(chapter.trim());
				} catch (NumberFormatException e) {
					// ignore return 0
				}
			}
		}
		return 0;
	}

}
