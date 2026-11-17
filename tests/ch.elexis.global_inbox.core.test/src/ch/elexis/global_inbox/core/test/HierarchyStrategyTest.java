package ch.elexis.global_inbox.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.elexis.global_inbox.core.strategies.HierarchyStrategy;

public class HierarchyStrategyTest {

	private StubImportOmnivoreInboxUtil stubUtil;
	private HierarchyStrategy strategy;

	@Before
	public void setUp() {
		stubUtil = new StubImportOmnivoreInboxUtil();
		strategy = new HierarchyStrategy(stubUtil, "DeviceX");
	}

	@Test
	public void testNoPatientInHierarchy() {
		StubVirtualFilesystemHandle archiveFolder = new StubVirtualFilesystemHandle("Archiv", null);
		StubVirtualFilesystemHandle yearFolder = new StubVirtualFilesystemHandle("2024", archiveFolder);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("Bild.jpg", yearFolder);

		boolean result = strategy.importFile(file);

		assertFalse("Sollte nicht importiert werden, da kein Patient gefunden wurde", result);
	}

	@Test
	public void testUnderscoreFilenameCleanup() {
		StubVirtualFilesystemHandle patFolder = new StubVirtualFilesystemHandle("12345_Test", null);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("_Befund.txt", patFolder);

		boolean result = strategy.importFile(file);

		assertTrue(result);
		assertEquals("DeviceX_Befund.txt", stubUtil.capturedDocumentName);
	}

	@Test
	public void testDeepHierarchy() {
		StubVirtualFilesystemHandle patFolder = new StubVirtualFilesystemHandle("777_Patient", null);
		StubVirtualFilesystemHandle catFolder = new StubVirtualFilesystemHandle("Labor", patFolder);
		StubVirtualFilesystemHandle yearFolder = new StubVirtualFilesystemHandle("2024", catFolder);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("Bericht.txt", yearFolder);

		boolean result = strategy.importFile(file);

		assertTrue(result);
		assertEquals("777", stubUtil.capturedPatientId);

		assertEquals("DeviceX_Labor_2024_Bericht.txt", stubUtil.capturedDocumentName);
		assertEquals("DeviceX", stubUtil.capturedDeviceName);
	}

	@Test
	public void testDateFolderNotPatient() {
		StubVirtualFilesystemHandle patFolder = new StubVirtualFilesystemHandle("2222_Armeswesen", null);
		StubVirtualFilesystemHandle bodyFolder = new StubVirtualFilesystemHandle("Knie", patFolder);
		StubVirtualFilesystemHandle dateFolder = new StubVirtualFilesystemHandle("2026-11-17", bodyFolder);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("aufnahme.pdf", dateFolder);

		boolean result = strategy.importFile(file);

		assertTrue(result);
		assertEquals("2222", stubUtil.capturedPatientId);
		assertEquals("DeviceX_Knie_2026-11-17_aufnahme.pdf", stubUtil.capturedDocumentName);
	}
}