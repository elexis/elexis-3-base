package ch.elexis.base.ch.ebanking.esr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.base.ch.ebanking.test.AllTests;
import ch.rgw.tools.Result;

public class ESRFileTest {
	
	private static List<File> esrFiles;
	
	@BeforeClass
	public static void beforeClass(){
		File dir = new File(AllTests.ESR_FILE_DIR);
		if (!dir.exists()) {
			fail("ESR_FILE_DIR not defined");
		}
		esrFiles = Arrays.asList(dir.listFiles());
		if (esrFiles.isEmpty()) {
			fail("No ESR files in [" + dir.getAbsolutePath() + "]");
		}
	}
	
	@Test
	public void readFiles(){
		ESRFile esrFile = new ESRFile();
		for (File file : esrFiles) {
			Result<List<ESRRecord>> result = esrFile.read(file, new NullProgressMonitor());
			assertTrue(result.isOK());
			assertFalse(result.get().isEmpty());
		}
	}
}
