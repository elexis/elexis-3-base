package at.medevit.elexis.hin.sign.core.internal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CliProcessTest {

	@Test
	public void isCliAvailable() {
		assertTrue(CliProcess.isCliAvailable());
	}
}
