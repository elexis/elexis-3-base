package ch.elexis.laborimport.eurolyser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil.TransientLabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;

public class EurolyserImporter {
	
	public final static String CONFIG_IMPORT_MANDANTONLY = "eurolyser/import/mandantonly";
	
	private File file;
	private Labor labor;
	
	private HashMap<String, Patient> filePatientMap = new HashMap<String, Patient>();
	
	public EurolyserImporter(Labor labor, File file){
		this.file = file;
		this.labor = labor;
	}
	
	public boolean createResults(){
		List<TransientLabResult> results = new ArrayList<TransientLabResult>();
		List<String> lines = readFile();
		try {
			for (String line : lines) {
				// skip empty lines
				if (line != null && !line.isEmpty()) {
					EurolyserLine eurolyserLine = new EurolyserLine(labor, line);
					if (eurolyserLine.isRelevant()) {
						TransientLabResult result = eurolyserLine.createResult(filePatientMap);
						if (result != null) {
							results.add(result);
						}
					}
				}
			}
			// import grouped by patient, creates nice orders
			HashMap<Patient, List<TransientLabResult>> resultsMap = getGroupedResults(results);
			Set<Patient> keys = resultsMap.keySet();
			for (Patient patient : keys) {
				List<TransientLabResult> patResults = resultsMap.get(patient);
				LabImportUtil.importLabResults(patResults, new DefaultLabImportUiHandler());
			}
			return !resultsMap.isEmpty();
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	private HashMap<Patient, List<TransientLabResult>> getGroupedResults(
		List<TransientLabResult> results){
		HashMap<Patient, List<TransientLabResult>> ret = new HashMap<Patient, List<TransientLabResult>>();
		for (TransientLabResult transientLabResult : results) {
			List<TransientLabResult> patResults = ret.get(transientLabResult.getPatient());
			if (patResults == null) {
				patResults = new ArrayList<TransientLabResult>();
				patResults.add(transientLabResult);
				ret.put(transientLabResult.getPatient(), patResults);
			} else {
				patResults.add(transientLabResult);
			}
		}
		return ret;
	}
	
	private List<String> readFile(){
		ArrayList<String> ret = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				ret.add(line);
			}
		} catch (IOException e) {
			throw new IllegalStateException("File [" + file.getAbsolutePath() + "] is not valid.");
		}
		return ret;
	}
}
