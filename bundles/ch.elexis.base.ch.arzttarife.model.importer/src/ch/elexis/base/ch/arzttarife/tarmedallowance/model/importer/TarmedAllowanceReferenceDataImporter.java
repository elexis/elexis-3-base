package ch.elexis.base.ch.arzttarife.tarmedallowance.model.importer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import ch.elexis.core.jpa.entities.TarmedPauschalen;
import ch.rgw.tools.TimeTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=tarmedallowance")
public class TarmedAllowanceReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {

	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		IStatus ret = Status.OK_STATUS;

		ExcelWrapper exw = new ExcelWrapper();
		exw.setFieldTypes(new Class[] { String.class /* Tarif-Nr. */, String.class /* Tarif-Name */,
				String.class /* Tarif-Name F */, String.class /* Tarif-Name I */, String.class /* Kapitelziffer */,
				String.class /* Kapitelbezeichung_D */, String.class /* Kapitelbezeichung_F */,
				String.class /* Kapitelbezeichung_I */, String.class /* Positions-Nr. */,
				String.class /* Positions-Text D */, String.class /* Positions-Text F */,
				String.class /* Positions-Text I */, TimeTool.class /* Gültig von */, TimeTool.class /* Gültig bis */
		});
		if (exw.load(input, 0)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;
			if (monitor != null) {
				monitor.beginTask("Tarmedpauschalen Import", count);
			}

			List<Object> imported = new ArrayList<>();
			List<Object> closed = new ArrayList<>();
			LocalDate now = LocalDate.now();

			for (int i = 0; i < last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || !line.get(0).equals("003") || StringUtils.isBlank(line.get(8))) {
					continue;
				}

				List<TarmedPauschalen> existing = getExisting(line.get(8), getValidFrom(line));
				if (!existing.isEmpty()) {
					for (TarmedPauschalen tarmedPauschalen : existing) {
						// update validto of existing
						tarmedPauschalen.setValidTo(getValidTo(line));
						closed.add(tarmedPauschalen);
					}
				} else {
					TarmedPauschalen tarmedPauschalen = new TarmedPauschalen();
					tarmedPauschalen.setCode(line.get(8));
					tarmedPauschalen.setText(getText(line));
					tarmedPauschalen.setChapter(getChapter(line));
					tarmedPauschalen.setValidFrom(getValidFrom(line));
					// set validto for already closed
					if (getValidTo(line).isBefore(now)) {
						tarmedPauschalen.setValidTo(getValidTo(line));
					}
					imported.add(tarmedPauschalen);
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

	private String getText(List<String> line) {
		return StringUtils
				.abbreviate(line.get(9).replace(StringUtils.LF, ";").replace(StringUtils.CR, StringUtils.EMPTY), 255);
	}

	private String getChapter(List<String> line) {
		return StringUtils
				.abbreviate(line.get(5).replace(StringUtils.LF, ";").replace(StringUtils.CR, StringUtils.EMPTY), 255);
	}

	private List<TarmedPauschalen> getExisting(String code, LocalDate validFrom) {
		Map<String, Object> propertyMap = new LinkedHashMap<String, Object>();
		propertyMap.put("code", code);
		propertyMap.put("validFrom", validFrom);
		return EntityUtil.loadByNamedQuery(propertyMap, TarmedPauschalen.class);
	}

	private LocalDate getValidFrom(List<String> line) {
		return getLocalDate(line.get(12).trim());
	}

	private LocalDate getValidTo(List<String> line) {
		if (StringUtils.isNotBlank(line.get(13).trim())) {
			return getLocalDate(line.get(13).trim());
		} else {
			return LocalDate.MAX;
		}
	}

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	private LocalDate getLocalDate(String value) {
		try {
			if (value.isEmpty()) {
				return LocalDate.parse("01.01.2016", dateFormatter);
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
		TarmedPauschalen versionEntry = EntityUtil.load("VERSION", TarmedPauschalen.class);
		if (versionEntry != null) {
			versionEntry.setChapter(Integer.toString(newVersion));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Version entry");
	}

	@Override
	public int getCurrentVersion() {
		TarmedPauschalen versionEntry = EntityUtil.load("VERSION", TarmedPauschalen.class);
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
