package ch.elexis.importer.aeskulap.core.test;

import java.io.File;
import java.util.Optional;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import ch.elexis.importer.aeskulap.core.internal.AeskulapImporterTest;

@RunWith(Suite.class)
@SuiteClasses({ AeskulapImporterTest.class 
})
public class AllTests {

	public static File getTestFile(String filename) {
		File testDirectory = getTestDirectory();
		if (testDirectory.exists() && testDirectory.isDirectory()) {
			return new File(testDirectory, filename);
		}
		throw new IllegalStateException("Test file [" + filename + "] not found");
	}

	public static File getTestDirectory() {
		Optional<File> bundleLocation = FileLocator
				.getBundleFileLocation(Platform.getBundle("ch.elexis.importer.aeskulap.core.test"));
		if (bundleLocation.isPresent()) {
			File testDirectory = new File(bundleLocation.get(), "testrsc");
			if (testDirectory.exists()) {
				return testDirectory;
			}
		}
		throw new IllegalStateException("Test folder not found");
	}
}
