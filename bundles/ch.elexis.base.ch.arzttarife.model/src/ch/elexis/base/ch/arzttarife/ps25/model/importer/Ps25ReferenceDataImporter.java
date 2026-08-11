package ch.elexis.base.ch.arzttarife.ps25.model.importer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.Ps25Leistung;
import ch.elexis.core.services.holder.ConfigServiceHolder;
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
		POS_ID(0),
		HONORAREMPFAENGER(1), FACHGEBIET_KAPITEL(2), UNTERKAPITEL(3), FACHAERZTLICHE_MEHRLEISTUNG_BEI(4),
		SPEZIFIKATION(5), ANWENDUNGSREGELN(6), TAXPUNKTE(7), STUFE(8), MOEGLICHE_KOMBINATION(9), MEHRLEISTUNGSTYP(10),
		MEHRLEISTUNG(11), GUELTIG_VON(12), GUELTIG_BIS(13);

		private final int index;

		Field(int index) {
			this.index = index;
		}
	}


	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		IStatus ret = Status.OK_STATUS;

		try {
			ExcelWrapper exw = new ExcelWrapper();
			exw.setFieldTypes(new Class[] { String.class, String.class, String.class, String.class, String.class,
					String.class, String.class, String.class, String.class, String.class, String.class, String.class,
					TimeTool.class, TimeTool.class });
			if (exw.load(input, 0)) {

				ImportResult result = importRows(exw, monitor);
				normalizeVersions(result.importedCodes);

				if (newVersion != null) {
					ConfigServiceHolder.get().set(REFERENCEDATA_PS25_VERSION, newVersion);
				}
				log.info("Finished PS25 import. Imported/updated {} rows for {} codes", result.rows,
						result.importedCodes.size());
			} else {
				ret = Status.CANCEL_STATUS;
			}
			return ret;
		} catch (Exception e) {
			log.error("Could not import PS25 tarif", e);
			return Status.CANCEL_STATUS;
		}
	}

	private ImportResult importRows(ExcelWrapper xl, IProgressMonitor monitor) {
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
			String code = value(row, Field.POS_ID);
			if (StringUtils.isBlank(code)) {
				continue;
			}
			LocalDate validFrom = parseDate(value(row, Field.GUELTIG_VON), Field.GUELTIG_VON);
			LocalDate validUntil = parseOptionalDate(value(row, Field.GUELTIG_BIS));
			Ps25Leistung ps25 = getExisting(code, validFrom);
			if (ps25 == null) {
				ps25 = new Ps25Leistung();
				ps25.setId(getId(code, validFrom));
			}
			update(ps25, row, code, validFrom, validUntil);
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

	private void update(Ps25Leistung ps25, List<String> row, String code,
			LocalDate validFrom, LocalDate validUntil) {
		ps25.setDeleted(false);
		ps25.setCode(code);
		ps25.setHonorarEmpfaenger(value(row, Field.HONORAREMPFAENGER));
		ps25.setFachgebietKapitel(value(row, Field.FACHGEBIET_KAPITEL));
		ps25.setUnterkapitel(value(row, Field.UNTERKAPITEL));
		ps25.setMehrleistungBei(value(row, Field.FACHAERZTLICHE_MEHRLEISTUNG_BEI));
		ps25.setSpezifikation(value(row, Field.SPEZIFIKATION));
		ps25.setAnwendungsregeln(value(row, Field.ANWENDUNGSREGELN));
		ps25.setTaxpunkte(value(row, Field.TAXPUNKTE));
		ps25.setStufe(value(row, Field.STUFE));
		ps25.setMoeglicheKombination(value(row, Field.MOEGLICHE_KOMBINATION));
		ps25.setMehrleistungstyp(value(row, Field.MEHRLEISTUNGSTYP));
		ps25.setMehrleistung(StringUtils.abbreviate(value(row, Field.MEHRLEISTUNG), 512));
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

	private String value(List<String> row, Field field) {
		if (field.index >= row.size()) {
			return StringUtils.EMPTY;
		}
		return StringUtils.trimToEmpty(row.get(field.index));
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

	@Override
	public int getCurrentVersion() {
		return ConfigServiceHolder.get().get(REFERENCEDATA_PS25_VERSION, 0);
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