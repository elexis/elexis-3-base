package ch.elexis.global_inbox.core.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.strategies.FallbackStrategy;
import ch.elexis.global_inbox.core.strategies.FilePrefixStrategy;
import ch.elexis.global_inbox.core.strategies.HierarchyStrategy;
import ch.elexis.global_inbox.core.strategies.IImportStrategy;

public class FallbackStrategyTest {

	class FakeStrategy implements IImportStrategy {
		boolean returnValue;

		FakeStrategy(boolean ret) {
			this.returnValue = ret;
		}

		@Override
		public boolean importFile(IVirtualFilesystemHandle file) {
			return returnValue;
		}
	}

	@Test
	public void testFirstStrategySuccess() {
		IImportStrategy s1 = new FakeStrategy(true);
		IImportStrategy s2 = new FakeStrategy(false);
		FallbackStrategy fallback = new FallbackStrategy(s1, s2);

		assertTrue(fallback.importFile(null));
	}

	@Test
	public void testSecondStrategySuccess() {
		IImportStrategy s1 = new FakeStrategy(false);
		IImportStrategy s2 = new FakeStrategy(true);
		FallbackStrategy fallback = new FallbackStrategy(s1, s2);
		assertTrue(fallback.importFile(null));
	}

	@Test
	public void testBothFail() {
		IImportStrategy s1 = new FakeStrategy(false);
		IImportStrategy s2 = new FakeStrategy(false);
		FallbackStrategy fallback = new FallbackStrategy(s1, s2);

		assertFalse(fallback.importFile(null));
	}

	@Test
	public void testFileNameBeforeFolder() {
		StubImportOmnivoreInboxUtil stubUtil = new StubImportOmnivoreInboxUtil();
		FallbackStrategy fallback = new FallbackStrategy(new FilePrefixStrategy(stubUtil, "EKG"),
				new HierarchyStrategy(stubUtil, "EKG"));
		StubVirtualFilesystemHandle patFolder = new StubVirtualFilesystemHandle("28_Obelix", null);
		StubVirtualFilesystemHandle file = new StubVirtualFilesystemHandle("751_Holter.pdf", patFolder);

		assertTrue(fallback.importFile(file));
		assertEquals("751", stubUtil.capturedPatientId);
		assertEquals("EKG_Holter.pdf", stubUtil.capturedDocumentName);
	}
}