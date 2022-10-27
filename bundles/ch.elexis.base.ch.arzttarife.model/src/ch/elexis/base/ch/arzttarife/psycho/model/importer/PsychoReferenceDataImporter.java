package ch.elexis.base.ch.arzttarife.psycho.model.importer;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import ch.elexis.core.jpa.entities.PsychoLeistung;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=psycho")
public class PsychoReferenceDataImporter extends AbstractReferenceDataImporter implements IReferenceDataImporter {

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		IStatus ret = Status.OK_STATUS;

		ExcelWrapper exw = new ExcelWrapper();
		exw.setFieldTypes(new Class[] { String.class /* Nr. */, String.class /* Bezeichnung */,
				String.class /* Interpretation */, String.class /* Limitation */,
				String.class /* Ausschlusskriterien */, String.class /* Taxpunkt */
		});
		if (exw.load(input, 0)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;

			monitor.beginTask("Pyschotherapie Tarif Import", count);

			List<Object> imported = new ArrayList<>();
			List<Object> closed = new ArrayList<>();

			for (int i = 0; i < last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || line.get(0).length() < 4 || !line.get(0).startsWith("P")) {
					continue;
				}

				LocalDate validFrom = LocalDate.of(2022, 7, 1);
//				LocalDate validTo = null;
				List<PsychoLeistung> existing = getExisting(line.get(0), validFrom);
				if (!existing.isEmpty()) {
//					if (validTo != null) {
//						for (PsychoLeistung tarmedPauschalen : existing) {
//							// update validto of existing
//							tarmedPauschalen.setValidUntil(validTo);
//							closed.add(tarmedPauschalen);
//						}
//					}
				} else {
					PsychoLeistung psychoLeistung = new PsychoLeistung();
					psychoLeistung.setCode(line.get(0));
					psychoLeistung
							.setCodeText(StringUtils.abbreviate(line.get(1).replace(StringUtils.LF, StringUtils.EMPTY)
									.replace(StringUtils.CR, StringUtils.EMPTY), 255));
					psychoLeistung.setDescription(line.get(2));

					psychoLeistung.setValidFrom(validFrom);

					psychoLeistung.setLimitations(parseLimitations(line.get(3)));
					psychoLeistung.setExclusions(parseExclusions(line.get(4)));

					psychoLeistung.setTp(parseTp(line.get(0), line.get(5)));

					imported.add(psychoLeistung);
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

	private String parseTp(String code, String string) {
		if ("prozent".equalsIgnoreCase(string)) {
			if ("PN020".equals(code)) {
				return "%20";
			}
		} else {
			try (Scanner scanner = new Scanner(string)) {
				if (scanner.hasNextInt()) {
					return Integer.toString(scanner.nextInt());
				}
			}
		}
		throw new IllegalStateException("Could not parse tp [" + string + "] for code [" + code + "]");
	}

	private String parseExclusions(String string) {
		return string;
	}

	private String parseLimitations(String string) {
		return string;
	}

	private List<PsychoLeistung> getExisting(String code, LocalDate validFrom) {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("code", code);
		propertyMap.put("validFrom", validFrom);
		return EntityUtil.loadByNamedQuery(propertyMap, PsychoLeistung.class);
	}

	@Override
	public int getCurrentVersion() {
		PsychoLeistung versionEntry = EntityUtil.load("VERSION", PsychoLeistung.class);
		if (versionEntry != null) {
			String versionString = versionEntry.getCodeText();
			if (StringUtils.isNumeric(versionString)) {
				return Integer.parseInt(versionString);
			}
		}
		return -1;
	}


	public static void setCurrentVersion(int newVersion) {
		PsychoLeistung versionEntry = EntityUtil.load("VERSION", PsychoLeistung.class);
		if (versionEntry != null) {
			versionEntry.setCodeText(Integer.toString(newVersion));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry");
	}
}
