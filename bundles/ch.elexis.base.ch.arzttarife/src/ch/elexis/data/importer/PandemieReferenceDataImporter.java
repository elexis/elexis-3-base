package ch.elexis.data.importer;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.jdt.Nullable;
import ch.elexis.data.PandemieLeistung;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
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
			String.class /* tp oder chf */, Double.class /* wert */
		});
		if (exw.load(input, 0)) {
			int first = exw.getFirstRow();
			int last = exw.getLastRow();
			int count = last - first;
			if (monitor != null)
				monitor.beginTask("Pandemie Tarif Import", count);
			
			deleteOldEntries();
			
			for (int i = 0; i < last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || !line.get(0).equals("351")) {
					continue;
				}
				
				PandemieLeistung pl = new PandemieLeistung(line.get(1), getChapter(line),
					line.get(4), StringUtils.abbreviate(line.get(5), 255),
					getValidFrom(line));
				pl.set(PandemieLeistung.FLD_DESCRIPTION, line.get(6));
				pl.set(PandemieLeistung.FLD_ORG,
					StringUtils.abbreviate(line.get(7).replace("\n", ";").replace("\r", ""), 255));
				if (isCents(line)) {
					pl.set(PandemieLeistung.FLD_CENTS, getAsCents(line.get(13)));
				} else {
					pl.set(PandemieLeistung.FLD_TAXPOINTS, getAsTaxpoints(line.get(13)));
				}
			}
			if (newVersion != null) {
				PandemieLeistung.setCurrentCodeVersion(newVersion);
			}
		}
		
		return ret;
		

	}
	
	private void deleteOldEntries(){
		if (PersistentObject.tableExists("CH_ELEXIS_ARZTTARIFE_CH_PANDEMIC")) {
			JdbcLink jdbcLink = PersistentObject.getDefaultConnection().getJdbcLink();
			jdbcLink.exec("DELETE FROM CH_ELEXIS_ARZTTARIFE_CH_PANDEMIC WHERE ID <> 'VERSION'");
		}
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
	
	private TimeTool getValidFrom(List<String> line){
		return new TimeTool("02.11.2020");
	}
	
	private String getChapter(List<String> line){
		return StringUtils.abbreviate(line.get(2) + " | " + line.get(3), 255);
	}
	
	@Override
	public int getCurrentVersion(){
		return PandemieLeistung.getCurrentCodeVersion();
	}
	
}
