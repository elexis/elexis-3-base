package ch.elexis.base.ch.arzttarife.physio.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

import ch.elexis.base.ch.arzttarife.model.service.ArzttarifeModelServiceHolder;
import ch.elexis.base.ch.arzttarife.physio.IPhysioLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.model.importer.EntityUtil;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.jpa.entities.PhysioLeistung;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.rgw.tools.TimeTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=physio")
public class PhysioReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {
	
	private LocalDate validFrom;
	private LocalDate endOfEpoch = new TimeTool(TimeTool.END_OF_UNIX_EPOCH).toLocalDate();
	
	@Override
	public IStatus performImport(IProgressMonitor monitor, InputStream input, Integer newVersion){
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		validFrom = getValidFromVersion(newVersion).toLocalDate();
		
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(input, "ISO-8859-1"), ';');
			monitor.beginTask("Importiere Physio", 100);
			String[] line = reader.readNext();
			while ((line = reader.readNext()) != null) {
				if (line.length < 3) {
					continue;
				}
				monitor.subTask(line[1]);
				updateOrCreateFromLine(line);
			}
			closeAllOlder();
			
			monitor.done();
			return Status.OK_STATUS;
		} catch (IOException uee) {
			LoggerFactory.getLogger(getClass()).error("Could not import physio tarif", uee);
			return Status.CANCEL_STATUS;
		}
	}
	
	/**
	 * Convert version Integer in yymmdd format to date.
	 * 
	 * @param newVersion
	 * @return
	 */
	private TimeTool getValidFromVersion(Integer newVersion){
		String intString = Integer.toString(newVersion);
		if (intString.length() != 6) {
			throw new IllegalStateException(
				"Version " + newVersion + " can not be parsed to valid date.");
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
	
	private void closeAllOlder(){
		// get all entries
		LocalDate defaultValidFrom = LocalDate.of(1970, 1, 1);
		List<PhysioLeistung> entries = EntityUtil.loadAll(PhysioLeistung.class);
		
		for (PhysioLeistung physio : entries) {
			LocalDate pValidFrom = physio.getValidFrom();
			LocalDate pValidUntil = physio.getValidUntil();
			if ((pValidFrom == null)) {
				// old entry with no valid from
				physio.setValidFrom(defaultValidFrom);
				physio.setValidUntil(validFrom);
			} else if (!validFrom.equals(pValidFrom)) {
				// old entry not closed yet
				if (pValidUntil == null) {
					physio.setValidUntil(validFrom);
				} else {
					if (pValidUntil.isEqual(endOfEpoch)) {
						physio.setValidUntil(validFrom);
					}
				}
			}
		}
	}
	
	private void updateOrCreateFromLine(String[] line){
		List<PhysioLeistung> entries = EntityUtil
			.loadByNamedQuery(Collections.singletonMap("ziffer", line[0]), PhysioLeistung.class);
		List<PhysioLeistung> openEntries = new ArrayList<PhysioLeistung>();
		// get open entries -> field FLD_GUELTIG_BIS not set
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
			physio.setZiffer(line[0]);
			physio.setTitel(line[1]);
			physio.setTp(line[2]);
			physio.setValidFrom(validFrom);
			physio.setValidUntil(null);
			if (lineHasFixPrice(line)) {
				applyFixPrice(physio, line[3]);
			}
			EntityUtil.save(Collections.singletonList(physio));
		} else {
			// do actual import if entries with updating open entries
			for (PhysioLeistung physio : openEntries) {
				if (physio.getValidFrom().equals(validFrom)) {
					// test if the gVon is the same -> update the values of the entry
					physio.setTitel(line[1]);
					physio.setTp(line[2]);
					if (lineHasFixPrice(line)) {
						applyFixPrice(physio, line[3]);
					}
				} else {
					// close entry and create new entry
					physio.setValidUntil(validFrom);
					
					PhysioLeistung newPhysio = new PhysioLeistung();
					physio.setZiffer(line[0]);
					physio.setTitel(line[1]);
					physio.setTp(line[2]);
					physio.setValidFrom(validFrom);
					physio.setValidUntil(null);
					if (lineHasFixPrice(line)) {
						applyFixPrice(physio, line[3]);
					}
					EntityUtil.save(Collections.singletonList(newPhysio));
				}
			}
		}
	}
	
	private void applyFixPrice(PhysioLeistung physio, String string){
		physio.setTp(string);
		StringBuilder sb = new StringBuilder();
		String existingText = physio.getTitel();
		if (existingText != null) {
			sb.append(existingText);
		}
		sb.append(PhysioLeistung.FIXEDPRICE);
		physio.setTitel(sb.toString());
	}
	
	private boolean lineHasFixPrice(String[] line){
		return line.length > 3 && line[3] != null && !line[3].isEmpty()
			&& Character.isDigit(line[3].charAt(0));
	}
	
	@Override
	public int getCurrentVersion(){
		IQuery<IPhysioLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPhysioLeistung.class);
		query.and("validFrom", COMPARATOR.NOT_EQUALS, null);
		query.and("validUntil", COMPARATOR.EQUALS, null);
		List<IPhysioLeistung> physioLeistungen = query.execute();
		if (!physioLeistungen.isEmpty()) {
			LocalDate validFrom = physioLeistungen.get(0).getValidFrom();
			if (validFrom != null) {
				DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyMMdd");
				int version = Integer.valueOf(ofPattern.format(validFrom));
				return version;
			}
		}
		return -1;
	}
}
