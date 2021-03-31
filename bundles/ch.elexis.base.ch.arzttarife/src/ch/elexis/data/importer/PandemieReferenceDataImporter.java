package ch.elexis.data.importer;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.data.PandemieLeistung;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class PandemieReferenceDataImporter extends AbstractReferenceDataImporter {
	private static final Logger logger =
		LoggerFactory.getLogger(PandemieReferenceDataImporter.class);
	
	
	@Override
	public Class<?> getReferenceDataTypeResponsibleFor(){
		return PandemieLeistung.class;
	}
	
	@Override
	public IStatus performImport(@Nullable IProgressMonitor monitor, InputStream input,
		@Nullable Integer newVersion){
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		IStatus ret = Status.OK_STATUS;
		
		ExcelWrapper exw = new ExcelWrapper();
		exw.setFieldTypes(new Class[] {
			String.class /* tarifcode */, String.class /* pandemie */, String.class /* kapitel */,
			String.class /* unterkapitel */, String.class /* ziffer */, String.class /* leistung */,
			String.class /* interpretation */, String.class /* part-og */,
			Integer.class /* anzahl */, String.class /* kumultation einsch. */,
			String.class /* kumulation */, String.class /* limitation */,
			String.class /* tp oder chf */, Double.class /* wert */, TimeTool.class /* gülti ab */,
			TimeTool.class /* gülti bis */
		});
		if (exw.load(input, 0)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;
			if (monitor != null) {
				monitor.beginTask("Pandemie Tarif Import", count);
			}
			
			int closed = 0;
			int imported = 0;
			TimeTool now = new TimeTool();
			
			for (int i = 0; i < last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || !line.get(0).equals("351")) {
					continue;
				}
				
				List<PandemieLeistung> existing =
					getExisting(line.get(1), line.get(4), getValidFrom(line));
				if (!existing.isEmpty()) {
					for (PandemieLeistung pandemieLeistung : existing) {
						if (StringUtils
							.isBlank(pandemieLeistung.get(PandemieLeistung.FLD_VALIDUNTIL))) {
							pandemieLeistung.set(PandemieLeistung.FLD_VALIDUNTIL,
								getValidTo(line).toString(TimeTool.DATE_COMPACT));
							closed++;
						}
					}
				} else {
					PandemieLeistung pl = new PandemieLeistung(line.get(1), getChapter(line),
						line.get(4), StringUtils.abbreviate(line.get(5), 255), getValidFrom(line));
					pl.set(PandemieLeistung.FLD_DESCRIPTION, line.get(6));
					pl.set(PandemieLeistung.FLD_ORG, StringUtils
						.abbreviate(line.get(7).replace("\n", ";").replace("\r", ""), 255));
					if (isCents(line)) {
						pl.set(PandemieLeistung.FLD_CENTS, getAsCents(line.get(13)));
					} else {
						pl.set(PandemieLeistung.FLD_TAXPOINTS, getAsTaxpoints(line.get(13)));
					}
					// set validto for already closed
					if (getValidTo(line).isBefore(now)) {
						pl.set(PandemieLeistung.FLD_VALIDUNTIL,
							getValidTo(line).toString(TimeTool.DATE_COMPACT));
					}
					imported++;
				}
			}
			LoggerFactory.getLogger(getClass())
				.info("Closing " + closed + " and creating " + imported + " tarifs");
			if (newVersion != null) {
				PandemieLeistung.setCurrentCodeVersion(newVersion);
			}
			ElexisEventDispatcher.getInstance()
				.fire(new ElexisEvent(null, PandemieLeistung.class, ElexisEvent.EVENT_RELOAD));
		}
		
		return ret;
		

	}
	
	private List<PandemieLeistung> getExisting(String pandemic, String code, TimeTool validFrom){
		Query<PandemieLeistung> query = new Query<PandemieLeistung>(PandemieLeistung.class);
		query.add(PandemieLeistung.FLD_PANDEMIC, Query.EQUALS, pandemic);
		query.add(PandemieLeistung.FLD_CODE, Query.EQUALS, code);
		query.add(PandemieLeistung.FLD_VALIDFROM, Query.EQUALS,
			validFrom.toString(TimeTool.DATE_COMPACT));
		return query.execute();
	}
	
	private boolean isCents(List<String> line){
		String centortaxpoint = line.get(12);
		return centortaxpoint.equalsIgnoreCase("chf");
	}
	
	private String getAsCents(String string){
		string = string.replaceAll(",", ".");
		try {
			double doubleValue = Double.parseDouble(string);
			return Integer.toString((int) (doubleValue * 100));
		} catch (NumberFormatException e) {
			// ignore return 0
		}
		return "0";
	}
	
	private String getAsTaxpoints(String string){
		string = string.replaceAll(",", ".");
		try {
			double doubleValue = Double.parseDouble(string);
			return Integer.toString((int) doubleValue);
		} catch (NumberFormatException e) {
			// ignore return 0
		}
		return "0";
	}
	
	private DateTimeFormatter dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	
	private LocalDate getLocalDate(String value){
		try {
			if (value.isEmpty()) {
				return LocalDate.parse("02.11.2020", dateFormatter);
			} else if (value.length() < 11) {
				return LocalDate.parse(value, dateFormatter);
			} else {
				return LocalDate.parse(value, dateTimeFormatter);
			}
		} catch (DateTimeParseException pe) {
			LoggerFactory.getLogger(getClass())
				.error("Could not parse as local date [" + value + "]");
			throw pe;
		}
	}
	
	private TimeTool getValidFrom(List<String> line){
		return new TimeTool(getLocalDate((String) line.get(14).trim()));
	}
	
	private TimeTool getValidTo(List<String> line){
		if (StringUtils.isNotBlank(line.get(15).trim())) {
			return new TimeTool(getLocalDate((String) line.get(15).trim()));
		} else {
			return new TimeTool(TimeTool.END_OF_UNIX_EPOCH);
		}
	}
	
	private String getChapter(List<String> line){
		return StringUtils.abbreviate(line.get(2) + " | " + line.get(3), 255);
	}
	
	@Override
	public int getCurrentVersion(){
		return PandemieLeistung.getCurrentCodeVersion();
	}
	
}
