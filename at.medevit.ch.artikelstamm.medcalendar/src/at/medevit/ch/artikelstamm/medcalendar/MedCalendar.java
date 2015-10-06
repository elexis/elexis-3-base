package at.medevit.ch.artikelstamm.medcalendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedCalendar {
	public static final String MEDCAL_SERIALIZED_FILE = "/lib/MedCalMap.ser";
	public static final String ATC_MEDCAL_SERIALIZED_FILE = "/lib/ATCMedCalMap.ser";
	
	private static MedCalendar instance = null;
	private HashMap<String, MedCalendarSection> medCalMap = null;
	private HashMap<String, String> atcMedCalMap = null;
	
	private MedCalendar(){
		initHashMapFromSerializedObject();
	}
	
	public static MedCalendar getInstance(){
		if (instance == null) {
			instance = new MedCalendar();
		}
		return instance;
	}
	
	private void initHashMapFromSerializedObject(){
		try {
			// use buffering for medcal serialized map
			InputStream isMedCal = MedCalendar.class.getResourceAsStream(MEDCAL_SERIALIZED_FILE);
			ObjectInput inMedCal = new ObjectInputStream(isMedCal);
			// use buffering for serialized ATC-MedCal-Matching map
			InputStream isAtcMedCal =
				MedCalendar.class.getResourceAsStream(ATC_MEDCAL_SERIALIZED_FILE);
			ObjectInput inAtcMedCal = new ObjectInputStream(isAtcMedCal);
			
			try {
				// deserialize the Lists
				medCalMap = (HashMap<String, MedCalendarSection>) inMedCal.readObject();
				atcMedCalMap = (HashMap<String, String>) inAtcMedCal.readObject();
			} finally {
				inMedCal.close();
			}
		} catch (ClassNotFoundException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	protected HashMap<String, String> getAtcMedCalMap(){
		return atcMedCalMap;
	}
	
	protected HashMap<String, MedCalendarSection> getMedCalMap(){
		return medCalMap;
	}
	
	public MedCalendarSection getMedCalendarSectionByATC(String atcCode){
		String mcCode = atcMedCalMap.get(atcCode);
		return medCalMap.get(mcCode);
	}
	
	public MedCalendarSection getMedCalendarSectionByCode(String code){
		return medCalMap.get(code);
	}
	
	public List<MedCalendarSection> getHierarchyForMedCal(String atcCode){
		List<MedCalendarSection> mcsHierarchy = new ArrayList<MedCalendarSection>();
		
		MedCalendarSection root = getMedCalendarSectionByATC(atcCode);
		if (root != null) {
			mcsHierarchy.add(root);
			int currentLevel = root.getLevel() - 1;
			while (currentLevel > 0) {
				MedCalendarSection mcs = fetchLevelForMedCalSection(root, currentLevel);
				if (mcs != null) {
					mcsHierarchy.add(mcs);
					// check refs
					if (!mcs.getRefSections().isEmpty()) {
						for (String subCode : mcs.getRefSections()) {
							MedCalendarSection mcsSub = getMedCalendarSectionByCode(subCode);
							mcsHierarchy.add(mcsSub);
						}
					}
				}
				currentLevel--;
			}
		}
		return mcsHierarchy;
	}
	
	private MedCalendarSection fetchLevelForMedCalSection(MedCalendarSection rootSec,
		int currentLevel){
		String[] codeParts = rootSec.getCode().split("\\.");
		
		switch (currentLevel) {
		case 3:
			return getMedCalendarSectionByCode(
				codeParts[0] + "." + codeParts[1] + "." + codeParts[2] + ".");
		case 2:
			return getMedCalendarSectionByCode(codeParts[0] + "." + codeParts[1] + ".");
		case 1:
			return getMedCalendarSectionByCode(codeParts[0]);
		}
		return null;
	}
}
