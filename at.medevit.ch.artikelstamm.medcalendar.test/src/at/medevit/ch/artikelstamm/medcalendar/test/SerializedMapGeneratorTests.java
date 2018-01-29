package at.medevit.ch.artikelstamm.medcalendar.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import at.medevit.ch.artikelstamm.medcalendar.test.internal.SerializedMapGenerator;

public class SerializedMapGeneratorTests {
	public static final String MEDCAL_CSV = "rsc/medcal.csv";
	public static final String MEDCAL_ATC_MATCHING_CSV = "rsc/medcal_atc_match.csv";
	public static final String MEDCAL_SERIALIZED_FILE = "rsc/gen/MedCalMap.ser";
	public static final String ATC_MEDCAL_SERIALIZED_FILE = "rsc/gen/ATCMedCalMap.ser";
	
	@Test
	public void testInitHashMap(){
		File medCalCSV = new File(MEDCAL_CSV);
		File medCalATCMatchCSV = new File(MEDCAL_ATC_MATCHING_CSV);
		
		SerializedMapGenerator generator = new SerializedMapGenerator();
		boolean success =
			generator.initSerializableMedCalMaps(medCalCSV, medCalATCMatchCSV,
				MEDCAL_SERIALIZED_FILE, ATC_MEDCAL_SERIALIZED_FILE);
		assertTrue(success);
	}
}
