package at.medevit.ch.artikelstamm.medcalendar.test.internal;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.ch.artikelstamm.medcalendar.MedCalendarSection;

public class SerializedMapGenerator {
	private static final Logger log = LoggerFactory.getLogger(SerializedMapGenerator.class);
	
	public static final String MEDCAL_CSV = "rsc/medcal.csv";
	public static final String MEDCAL_ATC_MATCHING_CSV = "rsc/medcal_atc_match.csv";
	private static final int IDX_CHAPTER = 0;
	private static final int IDX_SECTION = 1;
	private static final int IDX_SUBSECTION = 2;
	
	private HashMap<String, MedCalendarSection> medCalMap = null;
	private HashMap<String, String> atcMedCalMap = null;
	
	public boolean initSerializableMedCalMaps(File mcCSV, File mcAtcMatching, String serMedCalMap,
		String serATCMedCalMap){
		
		initMedCalMap(mcCSV);
		initATCMedCalMap(mcAtcMatching);
		
		try {
			OutputStream serializedMedCalMap = new FileOutputStream(serMedCalMap);
			OutputStream mcBuffer = new BufferedOutputStream(serializedMedCalMap);
			ObjectOutput mcOutput = new ObjectOutputStream(mcBuffer);
			mcOutput.writeObject(medCalMap);
			mcOutput.close();
			
			OutputStream serializedATCMedCalMap = new FileOutputStream(serATCMedCalMap);
			OutputStream atcMcBuffer = new BufferedOutputStream(serializedATCMedCalMap);
			ObjectOutput atcMcOutput = new ObjectOutputStream(atcMcBuffer);
			atcMcOutput.writeObject(atcMedCalMap);
			atcMcOutput.close();
			
		} catch (IOException ioe) {
			log.error("Error writing medCal maps", ioe);
			return false;
		}
		return true;
	}
	
	private void initMedCalMap(File medCalCSV){
		medCalMap = new HashMap<String, MedCalendarSection>();
		
		List<String[]> splitLines = getCSVLinesSplitted(medCalCSV);
		for (String[] line : splitLines) {
			if (line.length >= 2) {
				String code = line[0].trim().replaceAll("[^\\d.]", "");
				int level = determineLevel(code);
				
				MedCalendarSection medCalSection =
					new MedCalendarSection(code, line[1].trim(), level);
					
				// read referring sections
				if (line.length == 3) {
					String refSections = line[2];
					if (refSections != null && !refSections.isEmpty()) {
						String[] refs = refSections.split("/");
						for (String ref : refs) {
							medCalSection.addRefSection(ref.trim());
						}
					}
				}
				medCalMap.put(code, medCalSection);
			}
		}
	}
	
	private void initATCMedCalMap(File atcMedCalCSV){
		atcMedCalMap = new HashMap<String, String>();
		
		List<String[]> splitLines = getCSVLinesSplitted(atcMedCalCSV);
		for (String[] line : splitLines) {
			if (line.length >= 6) {
				// combination of chapter, section and subsection
				String[] mcsHierarchy =
					generateMedCalendarSectionHierarchy(line[0], line[1], line[2]);
				String atcCode = line[4];
				
				populateMedCalendarSectionsWithATCCode(mcsHierarchy, line[3], atcCode);
				
				//add atcCode - medCalCode mapping
				atcMedCalMap.put(atcCode, mcsHierarchy[IDX_SUBSECTION]);
			}
		}
	}
	
	private void populateMedCalendarSectionsWithATCCode(String[] mcsHierarchy, String mcsName,
		String atcCode){
		
		MedCalendarSection subSection =
			getOrCreateMedCalendarSection(mcsHierarchy[IDX_SUBSECTION], mcsName, 3);
		subSection.addATCCode(atcCode);
		medCalMap.put(mcsHierarchy[IDX_SUBSECTION], subSection);
		
		MedCalendarSection section =
			getOrCreateMedCalendarSection(mcsHierarchy[IDX_SECTION], mcsName, 2);
		section.addATCCode(atcCode);
		medCalMap.put(mcsHierarchy[IDX_SECTION], section);
		
		MedCalendarSection chapter =
			getOrCreateMedCalendarSection(mcsHierarchy[IDX_CHAPTER], mcsName, 1);
		chapter.addATCCode(atcCode);
		medCalMap.put(mcsHierarchy[IDX_CHAPTER], chapter);
	}
	
	private MedCalendarSection getOrCreateMedCalendarSection(String mcsCode, String mcsName,
		int level){
		MedCalendarSection section = medCalMap.get(mcsCode);
		
		//create MedCalendarSection if not existing
		if (section == null) {
			if (mcsName.equals("X nicht im Medkalender")) {
				mcsName = "Nicht im Medkalender";
			}
			return new MedCalendarSection(mcsCode, mcsName, level);
		}
		return section;
	}
	
	private String[] generateMedCalendarSectionHierarchy(String chapter, String section,
		String subsection){
		String[] mcsHierarchy = new String[3];
		// add chapter
		mcsHierarchy[IDX_CHAPTER] = chapter;
		// add section
		mcsHierarchy[IDX_SECTION] = chapter + "." + section + ".";
		// add subsection
		mcsHierarchy[IDX_SUBSECTION] = chapter + "." + section + "." + subsection + ".";
		
		return mcsHierarchy;
	}
	
	private int determineLevel(String code){
		int codeLength = code.split("\\.").length;
		
		switch (codeLength) {
		case 3:
			return 3;
		case 2:
			return 2;
		case 1:
			return 1;
		default:
			return 1;
		}
	}
	
	private List<String[]> getCSVLinesSplitted(File csvFile){
		List<String[]> splittedLines = new ArrayList<String[]>();
		
		try {
			List<String> csvLines = Files.readAllLines(csvFile.toPath());
			for (String line : csvLines) {
				splittedLines.add(line.split(";"));
			}
		} catch (IOException ioe) {
			log.error("Error processing medcal CSV", ioe);
		}
		return splittedLines;
	}
	
}
