package ch.elexis.base.ch.arzttarife.pandemie.model.importer;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.jpa.entities.PandemieLeistung;
import ch.rgw.tools.TimeTool;

@Component(property = IReferenceDataImporter.REFERENCEDATAID + "=pandemie")
public class PandemieReferenceDataImporter extends AbstractReferenceDataImporter
		implements IReferenceDataImporter {
	private static final Logger logger =
		LoggerFactory.getLogger(PandemieReferenceDataImporter.class);
	
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
			
			List<Object> imported = new ArrayList<>();
			
			for (int i = 0; i < last; i++) {
				List<String> line = exw.getRow(i);
				if (line == null) {
					break;
				} else if (line.isEmpty() || !line.get(0).equals("351")) {
					continue;
				}
				
				PandemieLeistung pl = new PandemieLeistung();
				pl.setPandemic(line.get(1));
				pl.setChapter(getChapter(line));
				pl.setCode(line.get(4));
				pl.setTitle(StringUtils.abbreviate(line.get(5), 255));
				pl.setValidFrom(getValidFrom(line));
				pl.setDescription(line.get(6));
				pl.setOrg(StringUtils.abbreviate(line.get(7).replace("\n", ";").replace("\r", ""), 255));
				
				if (isCents(line)) {
					pl.setCents(getAsCents(line.get(13)));
				} else {
					pl.setTaxpoints(getAsTaxpoints(line.get(13)));
				}
				imported.add(pl);
			}
			EntityUtil.save(imported);
			monitor.done();

			if (newVersion != null) {
				setCurrentVersion(newVersion);
			}
		}
		
		return ret;
		
	}
	
	private boolean isCents(List<String> line){
		String centortaxpoint = line.get(12);
		return centortaxpoint.equalsIgnoreCase("chf");
	}
	
	private int getAsCents(String string){
		string = string.replaceAll(",", ".");
		try {
			double doubleValue = Double.parseDouble(string);
			return (int) (doubleValue * 100);
		} catch (NumberFormatException e) {
			// ignore return 0
		}
		return 0;
	}
	
	private int getAsTaxpoints(String string){
		string = string.replaceAll(",", ".");
		try {
			double doubleValue = Double.parseDouble(string);
			return (int) doubleValue;
		} catch (NumberFormatException e) {
			// ignore return 0
		}
		return 0;
	}
	
	private LocalDate getValidFrom(List<String> line){
		return new TimeTool("02.11.2020").toLocalDate();
	}
	
	private String getChapter(List<String> line){
		return StringUtils.abbreviate(line.get(2) + " | " + line.get(3), 255);
	}
	
	public static void setCurrentVersion(int newVersion){
		PandemieLeistung versionEntry = EntityUtil.load("VERSION", PandemieLeistung.class);
		if (versionEntry != null) {
			versionEntry.setChapter(Integer.toString(newVersion));
			EntityUtil.save(Collections.singletonList(versionEntry));
			return;
		}
		throw new IllegalArgumentException("No Verison entry");
	}
	
	@Override
	public int getCurrentVersion(){
		PandemieLeistung versionEntry = EntityUtil.load("VERSION", PandemieLeistung.class);
		if (versionEntry != null) {
			String chapter = versionEntry.getChapter();
			try {
				return Integer.parseInt(((String) chapter).trim());
			} catch (NumberFormatException e) {
				// ignore return 0
			}
		}
		return 0;
	}
}
