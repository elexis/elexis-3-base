package ch.elexis.global_inbox.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.elexis.global_inbox.core.strategies.FilePrefixStrategy;

public class FilePrefixStrategyTest {

	private StubImportOmnivoreInboxUtil stubUtil;
	private FilePrefixStrategy strategy;

	@Before
	public void setUp() {
		stubUtil = new StubImportOmnivoreInboxUtil();
		strategy = new FilePrefixStrategy(stubUtil, "ScannerA");
	}

	@Test
	public void testMatchSuccess() {
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("12345_Befund.pdf", null);

		boolean result = strategy.importFile(file);

		assertTrue("Should be imported", result);
		assertEquals("12345", stubUtil.capturedPatientId);
		assertEquals("ScannerA_Befund.pdf", stubUtil.capturedDocumentName);
	}

	@Test
	public void testNoMatch() {
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("OhneNummer.pdf", null);

		boolean result = strategy.importFile(file);

		assertFalse("Should NOT be imported", result);
	}
}