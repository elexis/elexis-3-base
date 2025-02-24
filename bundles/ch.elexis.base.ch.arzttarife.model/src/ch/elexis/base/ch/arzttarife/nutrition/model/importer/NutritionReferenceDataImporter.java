package ch.elexis.base.ch.arzttarife.nutrition.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.opencsv.exceptions.CsvValidationException;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.nutrition.INutritionLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.NutritionLeistung;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.TimeTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=nutrition")
public class NutritionReferenceDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	private LocalDate validFrom;
	private LocalDate endOfEpoch = new TimeTool(TimeTool.END_OF_UNIX_EPOCH).toLocalDate();

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		validFrom = getValidFromVersion(newVersion).toLocalDate();

		try {
			CSVReader reader = new CSVReaderBuilder(new InputStreamReader(input, "UTF-8"))
					.withCSVParser(new CSVParserBuilder().withSeparator(';').build()).build();
			monitor.beginTask("Importiere Ernährungsberatung", 100);
			String[] line = reader.readNext();
			while ((line = reader.readNext()) != null) {
				if (line.length < 4) {
					continue;
				}
				if (StringUtils.isNoneBlank(line[0]) && Character.isDigit(line[0].charAt(0))) {
					monitor.subTask(line[1]);
					updateOrCreateFromLine(line);
				}
			}
			closeAllOlder();

			monitor.done();
			return Status.OK_STATUS;
		} catch (IOException | CsvValidationException uee) {
			LoggerFactory.getLogger(getClass()).error("Could not import nutrition tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}

	/**
	 * Convert version Integer in yymmdd format to date.
	 *
	 * @param newVersion
	 * @return
	 */
	private TimeTool getValidFromVersion(Integer newVersion) {
		String intString = Integer.toString(newVersion);
		if (intString.length() != 6) {
			throw new IllegalStateException("Version " + newVersion + " can not be parsed to valid date.");
		}
		String year = intString.substring(0, 2);
		String month = intString.substring(2, 4);
		String day = intString.substring(4, 6);
		TimeTool ret = new TimeTool();
		ret.set(TimeTool.YEAR, Integer.parseInt(year) + 2000);
		ret.set(TimeTool.MONTH, Integer.parseInt(month) - 1);
		ret.set(TimeTool.DAY_OF_MONTH, Integer.parseInt(day));
		return ret;
	}

	private void closeAllOlder() {
		// get all entries
		LocalDate defaultValidFrom = LocalDate.of(1970, 1, 1);
		List<NutritionLeistung> entries = EntityUtil.loadAll(NutritionLeistung.class);

		for (NutritionLeistung nutrition : entries) {
			LocalDate pValidFrom = nutrition.getValidFrom();
			LocalDate pValidUntil = nutrition.getValidUntil();
			if ((pValidFrom == null)) {
				// old entry with no valid from
				nutrition.setValidFrom(defaultValidFrom);
				nutrition.setValidUntil(validFrom);
			} else if (!validFrom.equals(pValidFrom)) {
				// old entry not closed yet
				if (pValidUntil == null) {
					nutrition.setValidUntil(validFrom);
				} else {
					if (pValidUntil.isEqual(endOfEpoch)) {
						nutrition.setValidUntil(validFrom);
					}
				}
			}
		}
	}

	private void updateOrCreateFromLine(String[] line) {
		List<NutritionLeistung> entries = EntityUtil.loadByNamedQuery(Collections.singletonMap("code", line[0]),
				NutritionLeistung.class);
		List<NutritionLeistung> openEntries = new ArrayList<>();
		// get open entries -> field FLD_GUELTIG_BIS not set
		for (NutritionLeistung nutrition : entries) {
			LocalDate pValidUntil = nutrition.getValidUntil();
			if (pValidUntil == null) {
				openEntries.add(nutrition);
			} else {
				if (pValidUntil.isEqual(endOfEpoch)) {
					openEntries.add(nutrition);
				}
			}
		}
		if (openEntries.isEmpty()) {
			NutritionLeistung nutrition = new NutritionLeistung();
			nutrition.setCode(line[0]);
			nutrition.setCodeText(line[1]);
			nutrition.setDescription(line[2]);
			nutrition.setTp(line[3]);
			nutrition.setValidFrom(validFrom);
			nutrition.setValidUntil(null);
			if (lineHasFixPrice(line)) {
				applyFixPrice(nutrition, line[4]);
			}
			EntityUtil.save(Collections.singletonList(nutrition));
		} else {
			// do actual import if entries with updating open entries
			for (NutritionLeistung nutrition : openEntries) {
				if (nutrition.getValidFrom().equals(validFrom)) {
					// test if the gVon is the same -> update the values of the entry
					nutrition.setCodeText(line[1]);
					nutrition.setDescription(line[2]);
					nutrition.setTp(line[3]);
					if (lineHasFixPrice(line)) {
						applyFixPrice(nutrition, line[4]);
					}
				} else {
					// close entry and create new entry
					nutrition.setValidUntil(validFrom);
					EntityUtil.save(Collections.singletonList(nutrition));

					NutritionLeistung newNutrition = new NutritionLeistung();
					newNutrition.setCode(line[0]);
					newNutrition.setCodeText(line[1]);
					newNutrition.setDescription(line[2]);
					newNutrition.setTp(line[3]);
					newNutrition.setValidFrom(validFrom);
					newNutrition.setValidUntil(null);
					if (lineHasFixPrice(line)) {
						applyFixPrice(newNutrition, line[4]);
					}
					EntityUtil.save(Collections.singletonList(newNutrition));
				}
			}
		}
	}

	private void applyFixPrice(NutritionLeistung nutrition, String string) {
		nutrition.setTp(string);
		StringBuilder sb = new StringBuilder();
		String existingText = nutrition.getText();
		if (existingText != null) {
			sb.append(existingText);
		}
		sb.append(NutritionLeistung.FIXEDPRICE);
		nutrition.setCodeText(sb.toString());
	}

	private boolean lineHasFixPrice(String[] line) {
		return line.length > 4 && line[4] != null && !line[4].isEmpty() && Character.isDigit(line[4].charAt(0));
	}

	@Override
	public int getCurrentVersion() {
		IQuery<INutritionLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(INutritionLeistung.class);
		query.and("validFrom", COMPARATOR.NOT_EQUALS, null);
		query.and("validUntil", COMPARATOR.EQUALS, null);
		List<INutritionLeistung> nutritionLeistungen = query.execute();
		if (!nutritionLeistungen.isEmpty()) {
			LocalDate validFrom = nutritionLeistungen.get(0).getValidFrom();
			if (validFrom != null) {
				DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyMMdd");
				int version = Integer.valueOf(ofPattern.format(validFrom));
				return version;
			}
		}
		return -1;
	}
}
