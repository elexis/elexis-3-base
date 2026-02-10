package ch.elexis.global_inbox.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.elexis.global_inbox.core.strategies.PatientFolderStrategy;

public class PatientFolderStrategyTest {

	private StubImportOmnivoreInboxUtil stubUtil;
	private PatientFolderStrategy strategy;

	@Before
	public void setUp() {
		stubUtil = new StubImportOmnivoreInboxUtil();
		strategy = new PatientFolderStrategy(stubUtil, "ScannerA");
	}

	@Test
	public void testFolderMatch() {
		StubVirtualFilesystemHandle folder = new StubVirtualFilesystemHandle("12345_Muster", null);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("Scan.pdf", folder);

		boolean result = strategy.importFile(file);

		assertTrue(result);
		assertEquals("12345", stubUtil.capturedPatientId);
		assertEquals("ScannerA_Scan.pdf", stubUtil.capturedDocumentName);
	}

	@Test
	public void testFolderOnlyNumber() {
		StubVirtualFilesystemHandle folder = new StubVirtualFilesystemHandle("12345", null);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("Scan.pdf", folder);
		boolean result = strategy.importFile(file);
		assertTrue(result);
		assertEquals("12345", stubUtil.capturedPatientId);
	}

	@Test
	public void testNoParent() {
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("Scan.pdf", null);
		assertFalse(strategy.importFile(file));
	}
}