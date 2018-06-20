package ch.elexis.omnivore.data;

import static ch.elexis.omnivore.PreferenceConstants.BASEPATH;
import static ch.elexis.omnivore.PreferenceConstants.OmnivoreMax_Filename_Length_Default;
import static ch.elexis.omnivore.PreferenceConstants.PREFBASE;
import static ch.elexis.omnivore.PreferenceConstants.PREF_DEST_DIR;
import static ch.elexis.omnivore.PreferenceConstants.PREF_MAX_FILENAME_LENGTH;
import static ch.elexis.omnivore.PreferenceConstants.PREF_SRC_PATTERN;
import static ch.elexis.omnivore.PreferenceConstants.STOREFS;
import static ch.elexis.omnivore.PreferenceConstants.STOREFSGLOBAL;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.activator.CoreHubHelper;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink;

public class Test_Utils{
	private static Logger log = LoggerFactory.getLogger(Test_Utils.class);
	private static IPreferenceStore preferenceStore = new SettingsPreferenceStore(CoreHub.localCfg);
	static ArrayList<Path> archiveDestPaths = new ArrayList<Path>();
	static ArrayList<String> archiveSrcPattern = new ArrayList<String>();
	public Test_Utils(){}
	
	@BeforeClass
	public static void setUpClass(){
		log.debug("testImportFirst getLink {}", AbstractPersistentObjectTest.getLink());
		setArchivePreferences();
	}

	private void setCotfLength(int len) {
		preferenceStore.setValue( "ch.elexis.omnivore/cotf_constant1_num_digits" , "ANFANG_");
		preferenceStore.setValue( "ch.elexis.omnivore/cotf_constant2_num_digits" , "_ENDE");
		String[] ids = new String[] {"PID", "fn", "gn", "dob", "dt", "dk", "dguid", "random"};
		String leading = "-";
		String trailing = "Y";
		for (String id: ids) {
			String key = String.format("ch.elexis.omnivore/cotf_%s_num_digits", id);
			preferenceStore.setValue(key, len);
			preferenceStore.setValue(key.replace("num_digits", "fill_leading_char"), leading);
			leading = leading.equals("-") ? "_" : "-";			
			preferenceStore.setValue(key.replace("num_digits", "add_trailing_char"), trailing);
			trailing = trailing.equals("Y") ? "X" : "Y";			
		}
	}
	
	private static void setArchivePreferences() {
		String tmpDir = System.getProperty("java.io.tmpdir");
		Path basePath = new File(tmpDir + "/omnivore/test/basepath").toPath();
		preferenceStore.setValue(BASEPATH, tmpDir + "/omnivore/test/basepath");
		try {
			Files.createDirectories(basePath);
			
			preferenceStore.setValue(STOREFS, true);
			preferenceStore.setValue(PREF_MAX_FILENAME_LENGTH, 66);
			Integer nCotfRules = Preferences.PREFERENCE_cotf_elements.length;
			for (int i = 0; i < nCotfRules; i++) {
				String search =
					PREFBASE + Preferences.PREFERENCE_COTF + Preferences.PREFERENCE_cotf_elements[i]
						+ "_" + Preferences.PREFERENCE_cotf_parameters[1];
				log.debug("Setup {} {}", i, search);
				// preferenceStore.setValue(search, i);
			}
			Integer nAutoArchiveRules = Preferences.getOmnivorenRulesForAutoArchiving();
			for (int i = 0; i < nAutoArchiveRules; i++) {
				Path destPath = new File(tmpDir + "/omnivore/test/destPath_" + i).toPath();
				String srcPattern = "src_" + i + "_src";
				log.debug("{}: src {} destDir {}", i, srcPattern, destPath.toString());
				preferenceStore.setValue(PREF_SRC_PATTERN[i], srcPattern);
				preferenceStore.setValue(PREF_DEST_DIR[i], destPath.toString());
				archiveDestPaths.add(destPath);
				archiveSrcPattern.add(srcPattern);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception{
		log.debug("setUp each");
		setCotfLength(4);
	}
	
	@Test
	public void testCreateMeaningfull(){
		Patient female = new Patient("Musterfrau", "Erika", "1.1.2000", "f");
		DocHandle dh = new DocHandle("category", new byte[] {
			1, 2
		}, female, "title", "mime", "keyword");

		String result = Utils.createNiceFileName(dh);
		// We cannot test the random here
		Assert.assertThat(result, endsWith("_ENDE"));
		Assert.assertThat(result, startsWith("ANFANG_"));
		Assert.assertThat(result, containsString("----1YMustXErikY01.0XtitlYkeywX"));
		
	}
	@Test
	public void testCreateTemporaryFile(){
		Patient female = new Patient("Musterfrau", "Marianne", "1.1.2000", "f");
		DocHandle dh = new DocHandle("category", new byte[] {
			1, 2, 3
		}, female, "title", "mime", "keyword");

		File f = dh.createTemporaryFile("dummy");
		Assert.assertTrue(f.exists());
		Assert.assertThat(f.getName(), startsWith("ANFANG_"));
		Assert.assertEquals(3, f.length());
		Assert.assertThat(f.getName(), containsString("YMustXMariY01.0XtitlYkeywX"));
		Assert.assertThat(f.getName(), endsWith("_ENDE."));
	}
	@Test
	public void testRemoveUnwanted(){
		setCotfLength(7);

		String toBeStripped = "A\\B\\C:D/E:*?()+,\';\"\\r\t\n´`F";
		Patient male = new Patient(toBeStripped, "B" + toBeStripped, "31.12.1955", "m");
		DocHandle dh = new DocHandle("category", new byte[] {
			1, 2
		}, male, "Dr. hc.", "mime", "Stichwort");
		
		String result = Utils.createNiceFileName(dh);
		Assert.assertEquals(toBeStripped, male.getName());
		Assert.assertFalse(result.contains(toBeStripped));
		Assert.assertThat(result, endsWith("_ENDE"));
		Assert.assertThat(result, containsString("ANFANG_"));
		Assert.assertThat(result, containsString("ANFANG_-------3YABCDErFXBABCDErY31.12.1XDr. hc.YStichwoX"));
	}
	@Test
	public void testArchiveFile(){
		setArchivePreferences();
		try {
			String srcPattern = archiveSrcPattern.get(0);
			File testTmp = File.createTempFile(srcPattern, ".tmp");
			String testContent = "Some dummy content";
			PrintWriter writer = new PrintWriter(testTmp.getAbsolutePath(), "UTF-8");
			writer.println(testContent);
			writer.close();
			Path archiveDir = archiveDestPaths.get(0);
			archiveDir.toFile().mkdirs();
			File result = Utils.archiveFile(testTmp);
			Assert.assertNotNull(result);
			String readBack= new String(Files.readAllLines(result.toPath()).get(0));
			Assert.assertTrue(result.toString().contains(srcPattern));
			Assert.assertEquals(testContent, readBack);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNothingToArchive(){
		setArchivePreferences();
		try {
			File testTmp;
			testTmp = File.createTempFile("XXXXXXXXXXX", ".tmp");
			File result = Utils.archiveFile(testTmp);
			Assert.assertNull(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testDontArchiveIfDirectory(){
		setArchivePreferences();
		try {
			Path testTmpDir = Files.createTempDirectory("Dummy");
			File result = Utils.archiveFile(testTmpDir.toFile());
			Assert.assertNull(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
