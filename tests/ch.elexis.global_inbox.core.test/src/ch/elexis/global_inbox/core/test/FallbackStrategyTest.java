package ch.elexis.global_inbox.core.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ch.elexis.core.services.IVirtualFilesystemService.IVirtualFilesystemHandle;
import ch.elexis.global_inbox.core.strategies.FallbackStrategy;
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
}