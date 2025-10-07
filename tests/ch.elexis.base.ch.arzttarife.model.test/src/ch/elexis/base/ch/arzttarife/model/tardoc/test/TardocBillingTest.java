package ch.elexis.base.ch.arzttarife.model.tardoc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;

public class TardocBillingTest extends AbstractTardocTest {

	private TardocLeistung code_000010;
	private TardocLeistung code_000020;
	private TardocLeistung code_000040;

	private TardocLeistung code_RG050010;
	private TardocLeistung code_RG000040;

	@Override
	@Before
	public void before() {
		super.before();
		code_000010 = TardocLeistung.getFromCode("AA.00.0010", LocalDate.of(2026, 1, 1), null);
		code_000020 = TardocLeistung.getFromCode("AA.00.0020", LocalDate.of(2026, 1, 1), null);

		code_000040 = TardocLeistung.getFromCode("AA.00.0040", LocalDate.of(2026, 1, 1), null);
		
		code_RG050010 = TardocLeistung.getFromCode("RG.05.0010", LocalDate.of(2026, 1, 1), null);
		code_RG000040 = TardocLeistung.getFromCode("RG.00.0040", LocalDate.of(2026, 1, 1), null);
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

		String bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("AA.00.0010", bezug);

		status = billingService.bill(code_000040, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
	}

	@Test
	public void incrementTardocPositions() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		Result<IBilled> status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		status = billingService.bill(code_000020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		status = billingService.bill(code_000020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
	}

	@Test
	public void sideTardocPositions() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		Result<IBilled> status = billingService.bill(code_RG050010, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		String side = (String) status.get().getExtInfo(Constants.FLD_EXT_SIDE);
		assertNotNull(side);

		// auto side left
		status = billingService.bill(code_RG000040, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		String bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("RG.05.0010", bezug);

		// auto side right
		status = billingService.bill(code_RG000040, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("RG.05.0010", bezug);

		// fail limit
		status = billingService.bill(code_RG000040, encounter, 1);
		billed = status.get();
		assertFalse(status.getMessages().toString(), status.isOK());

		assertEquals(3, encounter.getBilled().stream().mapToInt(b -> (int) b.getAmount()).sum());
	}
}
