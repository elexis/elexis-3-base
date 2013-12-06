package ch.elexis.externe_dokumente;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.data.Patient;
import ch.elexis.extdoc.preferences.PreferenceConstants;
import ch.elexis.extdoc.util.MatchPatientToPath;
import ch.elexis.core.data.activator.CoreHub;

public class Test_externe_dokumente {
	private static Patient helena;
	private static Patient werner;
	private static Patient fritz; // Hat kein Geburtsdatum
	private static Patient anneCecile;
	private static Patient meier; // Hat weder Vornamen noch Geburtsdatum
	
	static class PathToFirstAndFamily {
		public String path;
		public String firstName;
		public String familyName;
		
		PathToFirstAndFamily(String pat, String family, String first){
			path = pat;
			firstName = first;
			familyName = family;
		}
	}
	
	static class PatOldNew {
		public Patient p;
		public String alt;
		public String neu;
		public int nrFiles;
		
		PatOldNew(Patient pat, String old, String neuer, int nrF){
			p = pat;
			alt = old;
			neu = neuer;
			nrFiles = nrF;
		}
	}
	
	PatOldNew[] validExamples;
	static String testRoot;
	static String base_1;
	static String base_2;
	static String base_3;
	static String saved[];
	
	private static void createFiles(String[] names){
		
		for (int j = 0; j < names.length; j++) {
			File file = new File(names[j]);
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			assertTrue(file.exists());
			file.deleteOnExit();
		}
	}
	
	@BeforeClass
	public static void setupOnce(){
		testRoot = org.apache.commons.io.FileUtils.getTempDirectoryPath() + "/data/test";
		anneCecile = new Patient("Beck", "Anne-Cécile", "01.07.2002", "f");
		fritz = new Patient("Meier", "Fritz", "04.01.1981", "m");
		fritz.set("Geburtsdatum", "");
		helena = new Patient("Duck", "Helena", "01.01.2001", "f");
		werner = new Patient("Giezendanner", "Werner", "30.12.1980", "m");
		meier = new Patient("Meyer", "", "30.12.1970", "m");
		meier.set("Geburtsdatum", "");
		try {
			org.apache.commons.io.FileUtils.deleteDirectory(new File(testRoot));
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		saved = new String[4];
		String Invalid = "Invalid";
		saved[0] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD1, Invalid);
		saved[1] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD2, Invalid);
		saved[2] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD3, Invalid);
		saved[3] = CoreHub.localCfg.get(PreferenceConstants.BASIS_PFAD4, Invalid);
	}
	
	@AfterClass
	public static void restoreLast(){
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD1, saved[0]);
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD2, saved[1]);
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD3, saved[2]);
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD4, saved[3]);
		
	}
	
	@Before
	public void setup(){
		String testPfad_1 = testRoot + "/1";
		String testPfad_2 = testRoot + "/2";
		String testPfad_3 = testRoot + "/3";
		File test1 = new File(testPfad_1);
		File test2 = new File(testPfad_2);
		File test3 = new File(testPfad_3);
		test1.mkdirs();
		test2.mkdirs();
		test3.mkdirs();
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD1, testPfad_1);
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD2, testPfad_2);
		CoreHub.localCfg.set(PreferenceConstants.BASIS_PFAD3, testPfad_3);
		CoreHub.userCfg.set(PreferenceConstants.SELECTED_PATHS, "7");
		
		PreferenceConstants.PathElement[] prefElems = PreferenceConstants.getPrefenceElements();
		base_1 = prefElems[0].baseDir;
		base_2 = prefElems[1].baseDir;
		base_3 = prefElems[2].baseDir;
		PatOldNew[] valid =
			{
				new PatOldNew(anneCecile, base_1 + "/Beck  Anne-Cécile Pers.tif", base_1
					+ "/Beck  Annecécile 2002-07-01/Beck  Anne-Cécile Pers.tif", 2),
				new PatOldNew(anneCecile, base_1 + "/Beck  Anne-Cécile", base_1
					+ "/Beck  Annecécile 2002-07-01/Beck  Anne-Cécile", 2),
				new PatOldNew(helena, base_1 + "/Duck  Helena Pers.tif", base_1
					+ "/Duck  Helena 2001-01-01/Duck  Helena Pers.tif", 2),
				new PatOldNew(helena, base_1 + "/Duck  Helena.tif", base_1
					+ "/Duck  Helena 2001-01-01/Duck  Helena.tif", 2),
				new PatOldNew(fritz, base_1 + "/Meier Fritz PilonFxR_StnOSME RoeKSL.pdf", base_1
					+ "/Meier Fritz 1111-11-11/Meier Fritz PilonFxR_StnOSME RoeKSL.pdf", 1),
				new PatOldNew(werner, base_1 + "/GiezenWerner Antikoagulation.xls", base_1
					+ "/GiezenWerner 1980-12-30/GiezenWerner Antikoagulation.xls", 4),
				new PatOldNew(werner, base_2 + "/GiezenWerner Antikoagulation.txt", base_2
					+ "/GiezenWerner 1980-12-30/GiezenWerner Antikoagulation.txt", 4),
				new PatOldNew(werner, base_3 + "/GiezenWerner Antikoagulation.txt", base_3
					+ "/GiezenWerner 1980-12-30/GiezenWerner Antikoagulation.txt", 4),
				new PatOldNew(werner, base_3 + "/GiezenWerner test.txt", base_3
					+ "/GiezenWerner 1980-12-30/GiezenWerner test.txt", 4),
				new PatOldNew(meier, base_1 + "/Meyer ", base_1 + "/Meyer  1111-11-11/Meyer ", 1)
			};
		validExamples = valid;
		try {
			File temp = File.createTempFile("abc", "b");
			String dirName = temp.getAbsolutePath();
			File dir = new File(dirName);
			dir.mkdir();
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
	}
	
	@After
	public void tearDown(){
		for (int j = 0; j < validExamples.length; j++) {
			File file = new File(validExamples[j].neu);
			file.delete();
			file.getParentFile().deleteOnExit();
		}
	}
	
	@Test
	public void testSplitValid(){
		PathToFirstAndFamily[] valid =
			{
				new PathToFirstAndFamily("anything/Aack  Helena Pers.tif", "Aack", "Helena"),
				new PathToFirstAndFamily("anything/Back  Helena", "Back", "Helena"),
				new PathToFirstAndFamily("anything/Ceck  Max", "Ceck", "Max"),
				new PathToFirstAndFamily("anything/deeper/Dack  Helena.tif", "Dack", "Helena"),
			};
		for (int j = 0; j < valid.length; j++) {
			PathToFirstAndFamily t = valid[j];
			String names[] = MatchPatientToPath.getFirstAndFamilyNameFromPathOldConvention(t.path);
			String first = names[0];
			String family = names[1];
			if (!first.equals(t.firstName))
				System.out.format("first %s should match %s", first, t.firstName);
			if (!family.equals(t.familyName))
				System.out.format("family %s should match %s", family, t.familyName);
			
			Assert.assertEquals("path and first  name should match", t.firstName, first);
			Assert.assertEquals("path and family name should match", t.familyName, family);
		}
	}
	
	@Test
	public void testSplitInalid(){
		PathToFirstAndFamily[] invalid =
			{
				new PathToFirstAndFamily("anything/deeper/Duck  Max.tif", "Duck", "Max "),
				new PathToFirstAndFamily("anything/Duck  Helena Pers.tif", "Duck ", "Helena"),
				new PathToFirstAndFamily("anything/Duck  Helena", "Duck", "Helen"),
				new PathToFirstAndFamily("anything/deeper/Duck  Max.tif", "Duck", "May"),
				new PathToFirstAndFamily("anything/deeper/Duck  Max", "Duck", "Max "),
			};
		for (int j = 0; j < invalid.length; j++) {
			PathToFirstAndFamily t = invalid[j];
			String first = MatchPatientToPath.getFirstAndFamilyNameFromPathOldConvention(t.path)[0];
			String family =
				MatchPatientToPath.getFirstAndFamilyNameFromPathOldConvention(t.path)[1];
			assert (first.equals(t.firstName));
			Assert.assertNotSame("path and family name must not match", family, t.familyName);
			assert (first.equals(t.familyName));
		}
	}
	
	@Test
	public void testMoveIntoSubDir(){
		for (int j = 0; j < validExamples.length; j++) {
			// create old file
			File file = new File(validExamples[j].alt);
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			assertTrue(file.exists());
			File neu = new File(validExamples[j].neu);
			if (neu.exists())
				assertTrue(neu.delete());
			
			// test where it should be moved to
			MatchPatientToPath m = new MatchPatientToPath(validExamples[j].p);
			String should =
				m.ShouldBeMovedToThisSubDir(validExamples[j].alt,
					validExamples[j].p.getGeburtsdatum());
			if (!validExamples[j].neu.equals(should))
				System.out.format("\nalt: '%s' => \nneu: '%s' should be equal \nshd: '%s'\n",
					validExamples[j].alt, validExamples[j].neu, should);
			assertEquals(validExamples[j].neu, should);
			if (!file.exists()) {
				boolean success = false;
				try {
					success = file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				assertTrue(success);
			}
		}
		List<File> oldFiles = MatchPatientToPath.getAllOldConventionFiles();
		assert (oldFiles.size() > 0);
		Iterator<File> iterator = oldFiles.iterator();
		while (iterator.hasNext()) {
			MatchPatientToPath.MoveIntoSubDir(iterator.next().getAbsolutePath());
		}
		
		for (int j = 0; j < validExamples.length; j++) {
			File neu = new File(validExamples[j].neu);
			if (!neu.exists())
				System.out.format("alt: %1s should exist %2s\n", neu.getAbsolutePath(),
					neu.exists());
			assertTrue(neu.exists());
			Object allFiles = MatchPatientToPath.getFilesForPatient(validExamples[j].p, null);
			assertEquals("class java.util.ArrayList", allFiles.getClass().toString());
			ArrayList<String> tst = (ArrayList<String>) allFiles;
			if (tst.size() != validExamples[j].nrFiles)
				System.out.format("validExamples %d %s: allFiles %d should match size of %d\n", j,
					validExamples[j].alt, validExamples[j].nrFiles, tst.size());
			assertEquals(tst.size(), validExamples[j].nrFiles);
		}
	}
	
	@Test
	public void testMatchesAllFilesInSubDir(){
		String[] wernerFiles =
			{
				base_2 + "/GiezenWerner 1980-12-30/kurz",
				base_2 + "/GiezenWerner 1980-12-30/Meier Fritz   TestDatei.xx"
			};
		createFiles(wernerFiles);
		
		Object allFiles = MatchPatientToPath.getFilesForPatient(werner, null);
		assertEquals("class java.util.ArrayList", allFiles.getClass().toString());
		ArrayList<String> tst = (ArrayList<String>) allFiles;
		for (int j = 0; j < wernerFiles.length; j++) {
			boolean found = false;
			String name = wernerFiles[j];
			Iterator<String> iterator = tst.iterator();
			while (iterator.hasNext()) {
				Object o = iterator.next();
				File f = new File(o.toString());
				if (f.getAbsolutePath().equals(name)) {
					found = true;
					break;
				}
			}
			if (!found)
				System.out.format("Search for  %s found in tst ? %s\n", name, found);
			assertTrue("Did not find file " + name, found);
		}
	}
	
	@Test
	public void testPatientenMitGleichemNamenUndVornamen(){
		Patient peter1 = new Patient("Mustermann", "Peter", "04.01.1981", "m");
		Patient peter2 = new Patient("Mustermann", "Peter", "04.01.1955", "m");
		String[] peterFiles = {
			base_2 + "/MusterPeter Brief wegen Vater", base_2 + "/MusterPeter Brief wegen Sohn"
		};
		createFiles(peterFiles);
		
		/* Test für Peter1 */
		Object allFiles = MatchPatientToPath.getFilesForPatient(peter1, null);
		assertEquals("class java.util.ArrayList", allFiles.getClass().toString());
		ArrayList<String> tst = (ArrayList<String>) allFiles;
		assertEquals("class java.util.ArrayList", allFiles.getClass().toString());
		assertEquals("Fuer Peter1 müssen wir zwei Dateien finden", 2, tst.size());
		List<File> oldFiles = MatchPatientToPath.getAllOldConventionFiles();
		assertEquals("Fuer Peter1 müssen wir zwei alte Dateien finden", 2, oldFiles.size());
		
		/* Test für Peter2 */
		allFiles = MatchPatientToPath.getFilesForPatient(peter2, null);
		assertEquals("class java.util.ArrayList", allFiles.getClass().toString());
		tst = (ArrayList<String>) allFiles;
		assertEquals("Fuer Peter1 müssen wir zwei Dateien finden", 2, tst.size());
		oldFiles = MatchPatientToPath.getAllOldConventionFiles();
		assertEquals("Fuer Peter1 müssen wir zwei alte Dateien finden", oldFiles.size(), 2);
		
		/* Jetzt versuchen wir sie in ein Unterverzeichnis zu schieben */
		/* Dies muss fehlschlagen, da es mehrere Möglichkeiten gibt */
		MatchPatientToPath m = new MatchPatientToPath(peter1);
		oldFiles = MatchPatientToPath.getAllOldConventionFiles();
		assertEquals(oldFiles.size(), 2);
		Iterator<File> iterator = oldFiles.iterator();
		while (iterator.hasNext()) {
			MatchPatientToPath.MoveIntoSubDir(iterator.next().getAbsolutePath());
		}
		/* Es muessen immer noch zwei alte Dateien vorhanden sein */
		allFiles = MatchPatientToPath.getFilesForPatient(peter2, null);
		assertEquals("class java.util.ArrayList", allFiles.getClass().toString());
		assertEquals("Fuer Peter1 müssen wir immer noch zwei Dateien finden", 2, tst.size());
		oldFiles = MatchPatientToPath.getAllOldConventionFiles();
		assertEquals("Fuer Peter1 müssen wir immer noch zwei alte Dateien finden", 2,
			oldFiles.size());
		
	}
	
	@Test
	public void keineDateienVorhanden(){
		Patient ohneDateien = new Patient("Muster", "Albert", "30.12.1972", "m");
		Object allFiles = MatchPatientToPath.getFilesForPatient(ohneDateien, null);
		assertEquals("class java.lang.String", allFiles.getClass().toString());
	}
	
}
