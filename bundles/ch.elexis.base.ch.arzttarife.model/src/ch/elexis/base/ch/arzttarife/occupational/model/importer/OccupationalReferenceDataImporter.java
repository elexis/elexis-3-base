package ch.elexis.base.ch.arzttarife.occupational.model.importer;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.OccupationalLeistung;
import ch.elexis.core.time.TimeUtil;
import ch.rgw.tools.TimeTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=occupational")
public class OccupationalReferenceDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		IStatus ret = Status.OK_STATUS;

		ExcelWrapper exw = new ExcelWrapper();
		exw.setFieldTypes(
				new Class[] { String.class /* Ziffer */, String.class /* Bezeichnung */, String.class /* Fixpreis */,
						TimeTool.class /* Gültig Bis */
		});
		if (exw.load(input, 0)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;

			monitor.beginTask("Arbeitsmedizinische Vorsorgeuntersuchungen Import", count);

			List<Object> imported = new ArrayList<>();
			List<Object> updated = new ArrayList<>();
			List<Object> closed = new ArrayList<>();

			for (int i = 0; i <= last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || line.get(0).length() < 4 || line.get(0).startsWith("Ziffer")) {
					continue;
				}

				LocalDate validFrom = getValidFromVersion(newVersion).toLocalDate();
				List<String> codes = parseCode(line.get(0));
				for (String code : codes) {

					List<OccupationalLeistung> existing = getExisting(code);
					if (!existing.isEmpty()) {
						for (OccupationalLeistung occupationalLeistung : existing) {
							if (validFrom.equals(occupationalLeistung.getValidFrom())) {
								String codeText = StringUtils
										.abbreviate(line.get(1).replace(StringUtils.LF, StringUtils.EMPTY)
												.replace(StringUtils.CR, StringUtils.EMPTY), 255);
								if (codeText.equals(occupationalLeistung.getCodeText())) {
									occupationalLeistung.setCodeText(codeText);
									updated.add(occupationalLeistung);
								}
							} else {
								if (occupationalLeistung.getValidUntil() == null) {
									// update validto of existing -> closed
									occupationalLeistung.setValidUntil(validFrom.minusDays(1));
									closed.add(occupationalLeistung);
								}
								// create
								imported.add(createOccupationalLeistung(code, line, validFrom));
							}
						}
					} else {
						imported.add(createOccupationalLeistung(code, line, validFrom));
					}
				}
			}
			LoggerFactory.getLogger(getClass()).info("Closing " + closed.size() + " updating " + updated.size()
					+ " and creating " + imported.size() + " tarifs");
			EntityUtil.save(closed);
			EntityUtil.save(updated);
			EntityUtil.save(imported);
			monitor.done();

			if (newVersion != null) {
				setCurrentVersion(newVersion);
			}
		} else {
			ret = Status.CANCEL_STATUS;
		}
		return ret;
	}

	private OccupationalLeistung createOccupationalLeistung(String code, List<String> line, LocalDate validFrom) {
		LocalDate validTo = getValidTo(line.get(3));

		String codeText = StringUtils.abbreviate(
				line.get(1).replace(StringUtils.LF, StringUtils.EMPTY).replace(StringUtils.CR, StringUtils.EMPTY), 255);

		OccupationalLeistung occupationalLeistung = new OccupationalLeistung();
		occupationalLeistung.setCode(code);
		occupationalLeistung.setCodeText(codeText);
		occupationalLeistung.setValidFrom(validFrom);

		occupationalLeistung.setTp(parseTp(code, line.get(2)));

		if (validTo != null && validTo.isBefore(LocalDate.now().plusYears(5))) {
			// update validto of existing
			occupationalLeistung.setValidUntil(validTo);
		}

		return occupationalLeistung;
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

	private LocalDate getValidTo(String string) {
		if (StringUtils.isNotBlank(string)) {
			// 31.12.2026
			if (string.length() == 10) {
				try {
					return LocalDate.parse(string, TimeUtil.DATE_GER);
				} catch (Exception e) {
					LoggerFactory.getLogger(getClass()).warn("Could not parse valid to [" + string + "]");
				}
			} else {
				// fallback to TimeTool
				try {
					return new TimeTool(string).toLocalDate();
				} catch (Exception e) {
					LoggerFactory.getLogger(getClass()).warn("Could not parse valid to [" + string + "]");
				}
			}
			LoggerFactory.getLogger(getClass()).warn("Unknown valid to [" + string + "] format");
		}
		return null;
	}

	private List<String> parseCode(String code) {
		List<String> ret = new ArrayList<>();
		String codes = code.replaceAll(",", ".").replaceAll("(\r\n|\r|\n)", StringUtils.EMPTY);
		String[] parts = codes.split("\\+");
		for (String string : parts) {
			string = string.trim();
			if (string.indexOf('.') == -1) {
				string = string + ".00";
			} else {
				try {
					Float value = Float.parseFloat(string);
					string = String.format("%.02f", value).replaceAll(",", ".");
				} catch (NumberFormatException ne) {

				}
			}
			ret.add(string);
		}
		return ret;
	}

	private String parseTp(String code, String string) {
		try {
			Float value = Float.parseFloat(string.replaceAll(",", "."));
			// we need cents
			value = value * 100;
			return Integer.toString(value.intValue());
		} catch (NumberFormatException ne) {

		}

		throw new IllegalStateException("Could not parse tp [" + string + "] for code [" + code + "]");
	}

	private List<OccupationalLeistung> getExisting(String code) {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("code", code);
		return EntityUtil.loadByNamedQuery(propertyMap, OccupationalLeistung.class);
	}

	@Override
	public int getCurrentVersion() {
		OccupationalLeistung versionEntry = EntityUtil.load("VERSION", OccupationalLeistung.class);
		if (versionEntry != null) {
			String versionString = versionEntry.getCodeText();
			if (StringUtils.isNumeric(versionString)) {
				return Integer.parseInt(versionString);
			}
		}
		return -1;
	}

	public static void setCurrentVersion(int newVersion) {
		OccupationalLeistung versionEntry = EntityUtil.load("VERSION", OccupationalLeistung.class);
		if (versionEntry != null) {
			versionEntry.setCodeText(Integer.toString(newVersion));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry");
	}
}
