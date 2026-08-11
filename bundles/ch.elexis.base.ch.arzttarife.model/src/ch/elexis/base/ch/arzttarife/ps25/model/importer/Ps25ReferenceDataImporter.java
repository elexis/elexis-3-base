package ch.elexis.base.ch.arzttarife.ps25.model.importer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.Ps25Leistung;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.rgw.tools.TimeTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=" + Ps25ReferenceDataImporter.REFERENCEDATA_ID)
public class Ps25ReferenceDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	public static final String REFERENCEDATA_ID = "ps25";

	private static final String REFERENCEDATA_PS25_VERSION = "referencedata/ps25/version";
	private static final int HEADER_ROW = 3;
	private static final DateTimeFormatter DATE_DOTTED = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATE_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static final DateTimeFormatter DATE_ISO = DateTimeFormatter.ISO_LOCAL_DATE;

	private static final Logger log = LoggerFactory.getLogger(Ps25ReferenceDataImporter.class);

	private enum Field {
		POS_ID(0, "Pos_ID"),
		HONORAREMPFAENGER(1, "Honorarempf\u00e4nger"),
		FACHGEBIET_KAPITEL(2, "Fachgebiet/Kapitel"),
		UNTERKAPITEL(3, "Unterkapitel"),
		FACHAERZTLICHE_MEHRLEISTUNG_BEI(4, "Fach\u00e4rztliche Mehrleistung bei"),
		SPEZIFIKATION(5, "Spezifikation"),
		ANWENDUNGSREGELN(6, "Anwendungsregeln"),
		TAXPUNKTE(7, "Taxpunkte"),
		STUFE(8, "Stufe"),
		MOEGLICHE_KOMBINATION(9, "m\u00f6gliche Kombination"),
		MEHRLEISTUNGSTYP(10, "Mehrleistungstyp"),
		MEHRLEISTUNG(11, "Mehrleistung"),
		GUELTIG_VON(12, "G\u00fcltig von"),
		GUELTIG_BIS(13, "G\u00fcltig bis");

		private final int fallbackIndex;
		private final String headerName;

		Field(int fallbackIndex, String headerName) {
			this.fallbackIndex = fallbackIndex;
			this.headerName = headerName;
		}
	}

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		IProgressMonitor effectiveMonitor = monitor == null ? new NullProgressMonitor() : monitor;
		AtomicReference<IStatus> status = new AtomicReference<>(Status.CANCEL_STATUS);
		AccessControlServiceHolder.get()
				.doPrivileged(() -> status.set(performPrivilegedImport(effectiveMonitor, input, newVersion)));
		return status.get();
	}

	private IStatus performPrivilegedImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		try {
			InputStream xlsxInput = new ByteArrayInputStream(extractXlsx(input));
			ExcelWrapper xl = new ExcelWrapper();
			if (!xl.load(xlsxInput, 0)) {
				return Status.CANCEL_STATUS;
			}
			Map<Field, Integer> columns = getColumns(xl.getRow(HEADER_ROW));
			xl.setFieldTypes(new Class[] { String.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class, String.class, String.class, String.class,
					TimeTool.class, TimeTool.class });
			ImportResult result = importRows(xl, columns, monitor);
			normalizeVersions(result.importedCodes);

			if (newVersion != null) {
				ConfigServiceHolder.get().ifPresent(config -> config.set(REFERENCEDATA_PS25_VERSION, newVersion));
			}
			log.info("Finished PS25 import. Imported/updated {} rows for {} codes", result.rows, result.importedCodes.size());
			return Status.OK_STATUS;
		} catch (Exception e) {
			log.error("Could not import PS25 tarif", e);
			return Status.CANCEL_STATUS;
		}
	}

	private ImportResult importRows(ExcelWrapper xl, Map<Field, Integer> columns, IProgressMonitor monitor) {
		int firstDataRow = HEADER_ROW + 1;
		int last = xl.getLastRow();
		monitor.beginTask("PS25 Tarif Import", Math.max(1, last - firstDataRow));

		List<Object> changed = new ArrayList<>();
		Set<String> importedCodes = new HashSet<>();
		int rows = 0;
		for (int i = firstDataRow; i <= last; i++) {
			List<String> row = xl.getRow(i);
			if (row == null) {
				break;
			}
			String code = value(row, columns, Field.POS_ID);
			if (StringUtils.isBlank(code)) {
				continue;
			}
			LocalDate validFrom = parseDate(value(row, columns, Field.GUELTIG_VON), Field.GUELTIG_VON);
			LocalDate validUntil = parseOptionalDate(value(row, columns, Field.GUELTIG_BIS));
			Ps25Leistung ps25 = getExisting(code, validFrom);
			if (ps25 == null) {
				ps25 = new Ps25Leistung();
				ps25.setId(getId(code, validFrom));
			}
			update(ps25, row, columns, code, validFrom, validUntil);
			changed.add(ps25);
			importedCodes.add(code);
			rows++;
			monitor.worked(1);
		}
		if (!changed.isEmpty()) {
			EntityUtil.save(changed);
		}
		monitor.done();
		return new ImportResult(rows, importedCodes);
	}

	private void update(Ps25Leistung ps25, List<String> row, Map<Field, Integer> columns, String code,
			LocalDate validFrom, LocalDate validUntil) {
		ps25.setDeleted(false);
		ps25.setCode(code);
		ps25.setHonorarEmpfaenger(dbValue(row, columns, Field.HONORAREMPFAENGER));
		ps25.setFachgebietKapitel(dbValue(row, columns, Field.FACHGEBIET_KAPITEL));
		ps25.setUnterkapitel(dbValue(row, columns, Field.UNTERKAPITEL));
		ps25.setFachaerztlicheMehrleistungBei(dbValue(row, columns, Field.FACHAERZTLICHE_MEHRLEISTUNG_BEI));
		ps25.setSpezifikation(dbValue(row, columns, Field.SPEZIFIKATION));
		ps25.setAnwendungsregeln(dbValue(row, columns, Field.ANWENDUNGSREGELN));
		ps25.setTaxpunkte(dbValue(row, columns, Field.TAXPUNKTE));
		ps25.setStufe(dbValue(row, columns, Field.STUFE));
		ps25.setMoeglicheKombination(dbValue(row, columns, Field.MOEGLICHE_KOMBINATION));
		ps25.setMehrleistungstyp(dbValue(row, columns, Field.MEHRLEISTUNGSTYP));
		ps25.setMehrleistung(StringUtils.abbreviate(dbValue(row, columns, Field.MEHRLEISTUNG), 512));
		ps25.setValidFrom(validFrom);
		ps25.setValidUntil(validUntil);
	}

	private void normalizeVersions(Set<String> importedCodes) {
		List<Object> changed = new ArrayList<>();
		for (String code : importedCodes) {
			List<Ps25Leistung> versions = getExisting(code);
			versions.sort(Comparator.comparing(Ps25Leistung::getValidFrom, Comparator.nullsFirst(Comparator.naturalOrder())));
			for (int i = 0; i < versions.size() - 1; i++) {
				Ps25Leistung current = versions.get(i);
				Ps25Leistung next = versions.get(i + 1);
				if (current.getValidFrom() == null || next.getValidFrom() == null) {
					continue;
				}
				LocalDate dayBeforeNext = next.getValidFrom().minusDays(1);
				if (current.getValidUntil() == null || current.getValidUntil().isAfter(dayBeforeNext)) {
					current.setValidUntil(dayBeforeNext);
					changed.add(current);
				}
			}
		}
		if (!changed.isEmpty()) {
			EntityUtil.save(changed);
		}
	}

	private Ps25Leistung getExisting(String code, LocalDate validFrom) {
		return EntityUtil.load(getId(code, validFrom), Ps25Leistung.class);
	}

	private List<Ps25Leistung> getExisting(String code) {
		Map<String, Object> propertyMap = new LinkedHashMap<>();
		propertyMap.put("code", code);
		return EntityUtil.loadByNamedQuery(propertyMap, Ps25Leistung.class);
	}

	private String getId(String code, LocalDate validFrom) {
		return code + "-" + validFrom.format(DATE_COMPACT);
	}

	private String value(List<String> row, Map<Field, Integer> columns, Field field) {
		Integer index = columns.get(field);
		if (index == null || index >= row.size()) {
			return StringUtils.EMPTY;
		}
		return StringUtils.trimToEmpty(row.get(index));
	}

	private String dbValue(List<String> row, Map<Field, Integer> columns, Field field) {
		return toDatabaseCharset(value(row, columns, field));
	}

	private String toDatabaseCharset(String value) {
		if (StringUtils.isBlank(value)) {
			return value;
		}
		String mapped = value.replace("\u2265", ">=").replace("\u2264", "<=").replace("\u2013", "-")
				.replace("\u2014", "-").replace("\u2018", "'").replace("\u2019", "'").replace("\u201c", "\"")
				.replace("\u201d", "\"").replace("\u00d7", "x").replace("\u2212", "-");
		StringBuilder ret = new StringBuilder(mapped.length());
		mapped.codePoints().forEach(cp -> ret.append(cp <= 0x00ff ? (char) cp : '?'));
		return ret.toString();
	}

	private Map<Field, Integer> getColumns(List<String> header) {
		Map<Field, Integer> ret = new HashMap<>();
		Map<String, Integer> byHeader = new HashMap<>();
		if (header != null) {
			for (int i = 0; i < header.size(); i++) {
				byHeader.put(normalizeHeader(header.get(i)), i);
			}
		}
		for (Field field : Field.values()) {
			Integer index = byHeader.get(normalizeHeader(field.headerName));
			ret.put(field, index != null ? index : field.fallbackIndex);
		}
		return ret;
	}

	private String normalizeHeader(String header) {
		return StringUtils.trimToEmpty(header).toLowerCase().replace("_", "").replace("/", "").replace(" ", "")
				.replace("\u00e4", "ae").replace("\u00f6", "oe").replace("\u00fc", "ue");
	}

	private LocalDate parseOptionalDate(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return parseDate(value, null);
	}

	private LocalDate parseDate(String value, Field field) {
		String trimmed = StringUtils.trimToEmpty(value);
		if (StringUtils.isBlank(trimmed)) {
			throw new IllegalStateException("Missing date for " + field);
		}
		List<DateTimeFormatter> formatters = List.of(DATE_DOTTED, DATE_ISO, DATE_COMPACT);
		for (DateTimeFormatter formatter : formatters) {
			try {
				return LocalDate.parse(
						trimmed.length() > 8 && formatter == DATE_COMPACT ? trimmed.substring(0, 8) : trimmed,
						formatter);
			} catch (DateTimeParseException e) {
				// try next format
			}
		}
		try {
			return LocalDateTime.parse(trimmed.replace(' ', 'T')).toLocalDate();
		} catch (DateTimeParseException e) {
			// report below
		}
		throw new IllegalStateException("Could not parse date [" + trimmed + "]");
	}

	private byte[] extractXlsx(InputStream input) throws IOException {
		byte[] inputBytes = input.readAllBytes();
		try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(inputBytes))) {
			ZipEntry entry;
			while ((entry = zip.getNextEntry()) != null) {
				if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(".xlsx")) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					zip.transferTo(out);
					return out.toByteArray();
				}
			}
		}
		return inputBytes;
	}

	@Override
	public int getCurrentVersion() {
		return ConfigServiceHolder.get().map(config -> config.get(REFERENCEDATA_PS25_VERSION, 0)).orElse(0);
	}

	private static class ImportResult {
		private final int rows;
		private final Set<String> importedCodes;

		private ImportResult(int rows, Set<String> importedCodes) {
			this.rows = rows;
			this.importedCodes = importedCodes;
		}
	}
}
