package at.medevit.elexis.emediplan.core.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import at.medevit.elexis.emediplan.core.internal.EMediplanServiceImplTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	EMediplanServiceImplTest.class
})
public class AllTests {
	
	public static File getAsFile(String string) throws IOException{
		File ret = Files.createTempFile("test_", ".pdf").toFile();
		try (FileOutputStream output = new FileOutputStream(ret)) {
			IOUtils.copy(AllTests.class.getResourceAsStream(string), output);
		}
		return ret;
	}
	
	public static String getAsString(String string) throws IOException{
		return IOUtils.toString(AllTests.class.getResourceAsStream(string), "UTF-8");
	}
}
