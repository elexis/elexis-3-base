package ch.elexis.base.ch.arzttarife.physio.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.jpa.entities.PhysioLeistung;
import ch.rgw.tools.TimeTool;

/**
 * Importer for the new physio tariff csv format (Tarif 312), e.g.
 * Tarif_312_Physiotherapie_KVG_20260227.csv. The file is ';'-separated, IBM850
 * encoded and contains a header row. Rows of type 'chp' define the chapters,
 * rows of type 'srv' the billable services, only 'srv' rows are imported as
 * {@link PhysioLeistung}. In contrast to {@link PhysioReferenceDataImporter}
 * the valid from date is taken per row from the csv.
 */
public abstract class PhysioReferenceDataImporterV2 extends PhysioReferenceDataImporter {

	// column indexes of the new csv format
	private static final int COL_TYPE = 0;
	private static final int COL_CHAPTER_NO = 3;
	private static final int COL_SERVICE_NO = 4;
	private static final int COL_DESCRIPTION_DE = 5;
	private static final int COL_INTERPRETATION_DE = 6;
	private static final int COL_VALID_FROM = 11;
	private static final int COL_VALID_UNTIL = 12;
	private static final int COL_PRICING_METHOD = 13;
	private static final int COL_PRICE = 14;

	private static final String TYPE_CHAPTER = "chp";
	private static final String TYPE_SERVICE = "srv";

	private static final String PRICING_METHOD_TAX_POINTS = "tax_points";
	private static final String PRICING_METHOD_CHF = "chf";

	// valid until value in the csv that marks an open (not yet closed) entry
	private static final String VALID_UNTIL_OPEN = "2999-12-31";

	private static final DateTimeFormatter CSV_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// replacements for the tags used within the interpretation column
	private static final Map<String, String> INTERPRETATION_REPLACEMENTS = createInterpretationReplacements();

	// earliest valid from of the imported rows, used to close older entries
	private LocalDate validFrom;
	private LocalDate endOfEpoch = new TimeTool(TimeTool.END_OF_UNIX_EPOCH).toLocalDate();

	private static Map<String, String> createInterpretationReplacements() {
		Map<String, String> replacements = new HashMap<>();
		replacements.put("[APPLICATION_RULE]", "Anwendungsregel");
		replacements.put("[POSITION_DESCRIPTION]", "Positionsbeschreibung");
		replacements.put("[CUMULATION_RULE]", "Kumulationsregel");
		replacements.put("[CHAPTER_INTERPRETATION]", "Kapitelinterpretation");
		return Collections.unmodifiableMap(replacements);
	}

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		try {
			CSVReader reader = new CSVReaderBuilder(new InputStreamReader(input, "IBM850"))
					.withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();
			monitor.beginTask("Importiere Physio", 100);
			String[] line = reader.readNext(); // header
			Map<String, String> chapters = new HashMap<>();
			List<String[]> services = new ArrayList<>();
			while ((line = reader.readNext()) != null) {
				if (TYPE_CHAPTER.equals(col(line, COL_TYPE))) {
					chapters.put(col(line, COL_CHAPTER_NO), col(line, COL_DESCRIPTION_DE));
				} else if (TYPE_SERVICE.equals(col(line, COL_TYPE))) {
					services.add(line);
				}
				// 'trf' and unknown rows are not imported as Leistungen
			}
			for (String[] service : services) {
				monitor.subTask(col(service, COL_SERVICE_NO));
				updateOrCreateFromLine(service, chapters.get(col(service, COL_CHAPTER_NO)));
			}
			closeAllOlder();

			monitor.done();
			return Status.OK_STATUS;
		} catch (IOException | CsvValidationException uee) {
			LoggerFactory.getLogger(getClass()).error("Could not import physio tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}

	private void updateOrCreateFromLine(String[] line, String chapter) {
		LocalDate rowValidFrom = parseDate(col(line, COL_VALID_FROM));
		if (rowValidFrom == null) {
			LoggerFactory.getLogger(getClass()).warn("Skipping service {} without valid from date",
					col(line, COL_SERVICE_NO));
			return;
		}
		if (validFrom == null || rowValidFrom.isBefore(validFrom)) {
			validFrom = rowValidFrom;
		}
		List<PhysioLeistung> entries = EntityUtil.loadByNamedQuery(
				Collections.singletonMap("ziffer", col(line, COL_SERVICE_NO)), PhysioLeistung.class);
		List<PhysioLeistung> openEntries = new ArrayList<PhysioLeistung>();
		// get open entries -> field validUntil not set
		for (PhysioLeistung physio : entries) {
			LocalDate pValidUntil = physio.getValidUntil();
			if (pValidUntil == null) {
				openEntries.add(physio);
			} else {
				if (pValidUntil.isEqual(endOfEpoch)) {
					openEntries.add(physio);
				}
			}
		}
		if (openEntries.isEmpty()) {
			PhysioLeistung physio = new PhysioLeistung();
			applyLine(physio, line, chapter, rowValidFrom);
			EntityUtil.save(Collections.singletonList(physio));
		} else {
			// do actual import if entries with updating open entries
			for (PhysioLeistung physio : openEntries) {
				if (rowValidFrom.equals(physio.getValidFrom())) {
					// test if the valid from is the same -> update the values of the entry
					applyLine(physio, line, chapter, rowValidFrom);
					EntityUtil.save(Collections.singletonList(physio));
				} else {
					// close entry and create new entry
					if (physio.getValidFrom() == null) {
						physio.setValidFrom(LocalDate.of(1970, 1, 1));
					}
					physio.setValidUntil(rowValidFrom);
					EntityUtil.save(Collections.singletonList(physio));

					PhysioLeistung newPhysio = new PhysioLeistung();
					applyLine(newPhysio, line, chapter, rowValidFrom);
					EntityUtil.save(Collections.singletonList(newPhysio));
				}
			}
		}
	}

	private void applyLine(PhysioLeistung physio, String[] line, String chapter, LocalDate rowValidFrom) {
		physio.setZiffer(col(line, COL_SERVICE_NO));
		physio.setTitel(col(line, COL_DESCRIPTION_DE));
		physio.setKapitel(chapter);
		physio.setInterpretation(replaceTags(col(line, COL_INTERPRETATION_DE)));
		physio.setValidFrom(rowValidFrom);
		physio.setValidUntil(parseValidUntil(col(line, COL_VALID_UNTIL)));
		physio.setLaw(getLaw());
		applyPrice(physio, line);
	}

	private void applyPrice(PhysioLeistung physio, String[] line) {
		String pricingMethod = col(line, COL_PRICING_METHOD);
		String price = col(line, COL_PRICE);
		if (StringUtils.isBlank(price)) {
			// no price in the csv (e.g. purchase_price or marker positions)
			return;
		}
		if (PRICING_METHOD_CHF.equals(pricingMethod)) {
			// fixed price in CHF, mark the title as fixed price as with the old format
			applyFixPrice(physio, price);
		} else if (PRICING_METHOD_TAX_POINTS.equals(pricingMethod)) {
			physio.setTp(scaleToTaxPoints(price));
		}
	}

	private void applyFixPrice(PhysioLeistung physio, String price) {
		physio.setTp(scaleToTaxPoints(price));
		StringBuilder sb = new StringBuilder();
		String existingText = physio.getTitel();
		if (existingText != null) {
			sb.append(existingText);
		}
		sb.append(PhysioLeistung.FIXEDPRICE);
		physio.setTitel(sb.toString());
	}

	/**
	 * Scale a decimal price (e.g. 7.37 or 12.) to integer tax points (rappen),
	 * 1 tax point = 1/100 CHF.
	 *
	 * @param price
	 * @return
	 */
	private String scaleToTaxPoints(String price) {
		String cleaned = price.trim();
		if (cleaned.endsWith(".")) {
			cleaned = cleaned.substring(0, cleaned.length() - 1);
		}
		return Integer.toString(new BigDecimal(cleaned).movePointRight(2).setScale(0, RoundingMode.HALF_UP).intValue());
	}

	/**
	 * Replace the tags (e.g. [APPLICATION_RULE]) used within the interpretation
	 * column with readable text.
	 *
	 * @param interpretation
	 * @return
	 */
	private String replaceTags(String interpretation) {
		if (interpretation == null) {
			return null;
		}
		String replaced = interpretation;
		for (Map.Entry<String, String> replacement : INTERPRETATION_REPLACEMENTS.entrySet()) {
			replaced = StringUtils.replace(replaced, replacement.getKey(), replacement.getValue());
		}
		return replaced;
	}

	private LocalDate parseValidUntil(String validUntil) {
		if (StringUtils.isBlank(validUntil) || VALID_UNTIL_OPEN.equals(validUntil.trim())) {
			return null;
		}
		return parseDate(validUntil);
	}

	private LocalDate parseDate(String date) {
		if (StringUtils.isBlank(date)) {
			return null;
		}
		try {
			return LocalDate.parse(date.trim(), CSV_DATE_FORMAT);
		} catch (DateTimeParseException e) {
			LoggerFactory.getLogger(getClass()).warn("Could not parse date {}", date);
			return null;
		}
	}

	private String col(String[] line, int index) {
		return index < line.length ? line[index] : StringUtils.EMPTY;
	}

	private void closeAllOlder() {
		if (validFrom == null) {
			// nothing imported
			return;
		}
		// get all entries
		LocalDate defaultValidFrom = LocalDate.of(1970, 1, 1);
		List<PhysioLeistung> entries = EntityUtil.loadAll(PhysioLeistung.class);
		List<Object> modified = new ArrayList<Object>();

		for (PhysioLeistung physio : entries) {
			String entryLaw = physio.getLaw() == null ? StringUtils.EMPTY : physio.getLaw();
			if (!getLaw().equals(entryLaw)) {
				// entries of other laws (e.g. UVG, IVG, MVG) keep their own tarif
				continue;
			}
			LocalDate pValidFrom = physio.getValidFrom();
			LocalDate pValidUntil = physio.getValidUntil();
			if (pValidFrom == null) {
				// old entry with no valid from
				physio.setValidFrom(defaultValidFrom);
				physio.setValidUntil(validFrom);
				modified.add(physio);
			} else if (pValidFrom.isBefore(validFrom)) {
				// older entry not closed yet
				if (pValidUntil == null || pValidUntil.isEqual(endOfEpoch)) {
					physio.setValidUntil(validFrom);
					modified.add(physio);
				}
			}
		}
		if (!modified.isEmpty()) {
			EntityUtil.save(modified);
		}
	}
}
