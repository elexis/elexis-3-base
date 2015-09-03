package at.medevit.ch.artikelstamm.medcalendar;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class MedCalendar {
	public static final String MEDCAL_SERIALIZED_FILE = "/rsc/MedCalMap.ser";
	public static final String ATC_MEDCAL_SERIALIZED_FILE = "/rsc/ATCMedCalMap.ser";
	
	private static MedCalendar instance = null;
	private TreeMap<String, MedCalendarSection> medCalMap = null;
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
				medCalMap = (TreeMap<String, MedCalendarSection>) inMedCal.readObject();
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
	
	protected TreeMap<String, MedCalendarSection> getMedCalMap(){
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
		ArrayList<MedCalendarSection> ret = new ArrayList<MedCalendarSection>();
		MedCalendarSection root = getMedCalendarSectionByATC(atcCode);
		
		if (root != null) {
			String[] borderKeys = fetchBorderSections(root.getCode());
			SortedMap<String, MedCalendarSection> subMap =
				medCalMap.subMap(borderKeys[0], borderKeys[1]);
			Set<Entry<String, MedCalendarSection>> entrySet = subMap.entrySet();
			
			for (Entry<String, MedCalendarSection> entry : entrySet) {
				ret.add(entry.getValue());
			}
			ret.remove(0);
		}
		return ret;
	}
	
	private String[] fetchBorderSections(String code){
		String[] borders = new String[] {
			"", ""
		};
		String[] codeParts = code.split("\\.");
		if (codeParts.length < 2) {
			return borders;
		}
		
		borders[0] = codeParts[0] + "." + codeParts[1] + ".";
		// increase section number
		int nextSection = Integer.parseInt(codeParts[1]) + 1;
		borders[1] = codeParts[0] + "." + nextSection + ".";
		return borders;
	}
}
