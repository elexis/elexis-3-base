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
				new Class[] { String.class /* Ziffer */, String.class /* Bezeichnung */, String.class /* Fixpreis */
		});
		if (exw.load(input, 0)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;

			monitor.beginTask("Arbeitsmedizinische Vorsorgeuntersuchungen Import", count);

			List<Object> imported = new ArrayList<>();
			List<Object> closed = new ArrayList<>();

			for (int i = 0; i < last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || line.get(0).length() < 4 || line.get(0).startsWith("Ziffer")) {
					continue;
				}

				LocalDate validFrom = LocalDate.of(2022, 7, 1);
				List<String> codes = parseCode(line.get(0));
				for (String code : codes) {
//					LocalDate validTo = null;
					List<OccupationalLeistung> existing = getExisting(code, validFrom);
					if (!existing.isEmpty()) {
//						if (validTo != null) {
//							for (PsychoLeistung tarmedPauschalen : existing) {
//								// update validto of existing
//								tarmedPauschalen.setValidUntil(validTo);
//								closed.add(tarmedPauschalen);
//							}
//						}
					} else {
						OccupationalLeistung occupationalLeistung = new OccupationalLeistung();
						occupationalLeistung.setCode(code);
						occupationalLeistung.setCodeText(
								StringUtils.abbreviate(line.get(1).replace(StringUtils.LF, StringUtils.EMPTY)
										.replace(StringUtils.CR, StringUtils.EMPTY), 255));
						occupationalLeistung.setValidFrom(validFrom);

						occupationalLeistung.setTp(parseTp(code, line.get(2)));

						imported.add(occupationalLeistung);
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
		} else {
			ret = Status.CANCEL_STATUS;
		}
		return ret;
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

	private List<OccupationalLeistung> getExisting(String code, LocalDate validFrom) {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("code", code);
		propertyMap.put("validFrom", validFrom);
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
