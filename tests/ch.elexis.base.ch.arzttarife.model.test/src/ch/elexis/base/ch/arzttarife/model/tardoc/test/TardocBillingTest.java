package ch.elexis.base.ch.arzttarife.model.tardoc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;

public class TardocBillingTest extends AbstractTardocTest {

	private TardocLeistung code_000010;
	private TardocLeistung code_000020;
	private TardocLeistung code_000040;

	@Override
	@Before
	public void before() {
		super.before();
		code_000010 = TardocLeistung.getFromCode("AA.00.0010", LocalDate.of(2026, 1, 1), null);
		code_000020 = TardocLeistung.getFromCode("AA.00.0020", LocalDate.of(2026, 1, 1), null);

		code_000040 = TardocLeistung.getFromCode("AA.00.0040", LocalDate.of(2026, 1, 1), null);
	}

	@Override
	@After
	public void after() {
		super.after();
	}

	@Test
	public void basicTardocPositions() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor(coverage.getBillingSystem().getName(),
				0.89, LocalDate.of(2000, 1, 1));

		Result<IBilled> status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("AA.00.0010", billed.getCode());

		status = billingService.bill(code_000020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("AA.00.0020", billed.getCode());

		status = billingService.bill(code_000040, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
	}

	@Test
	public void triggerTardocPositions() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor(coverage.getBillingSystem().getName(),
				0.89, LocalDate.of(2000, 1, 1));

		Result<IBilled> status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("AA.00.0010", billed.getCode());

		status = billingService.bill(code_000020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("AA.00.0020", billed.getCode());

		/** C06.CB.0010 is a trigger code. */


		status = billingService.bill(code_000040, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
	}
}
