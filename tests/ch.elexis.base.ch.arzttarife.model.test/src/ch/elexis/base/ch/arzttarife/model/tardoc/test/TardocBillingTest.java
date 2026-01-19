package ch.elexis.base.ch.arzttarife.model.tardoc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tardoc.model.TardocConstants;
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

	private TardocLeistung code_MK250020;
	private TardocLeistung code_MK250090;
	private TardocLeistung code_MK200300;

	private TardocLeistung code_AA300040;
	private TardocLeistung code_AA300050;
	private TardocLeistung code_MK250120;

	private TardocLeistung code_TK000010;
	private TardocLeistung code_AR300130;

	@Override
	@Before
	public void before() {
		super.before();
		code_000010 = TardocLeistung.getFromCode("AA.00.0010", LocalDate.of(2026, 1, 1), null);
		code_000020 = TardocLeistung.getFromCode("AA.00.0020", LocalDate.of(2026, 1, 1), null);

		code_000040 = TardocLeistung.getFromCode("AA.00.0040", LocalDate.of(2026, 1, 1), null);
		
		code_RG050010 = TardocLeistung.getFromCode("RG.05.0010", LocalDate.of(2026, 1, 1), null);
		code_RG000040 = TardocLeistung.getFromCode("RG.00.0040", LocalDate.of(2026, 1, 1), null);

		// MK25.0020 - Hauptleistung
		code_MK250020 = TardocLeistung.getFromCode("MK.25.0020", LocalDate.of(2026, 1, 1), null);
		// MK25.0090 - Zuschalgsleistung
		code_MK250090 = TardocLeistung.getFromCode("MK.25.0090", LocalDate.of(2026, 1, 1), null);
		// MK20.0300 - Zuschlag-Zuschlagsleistung
		code_MK200300 = TardocLeistung.getFromCode("MK.20.0300", LocalDate.of(2026, 1, 1), null);

		// Prozent Zuschläge
		code_AA300040 = TardocLeistung.getFromCode("AA.30.0040", LocalDate.of(2026, 1, 1), null);
		code_AA300050 = TardocLeistung.getFromCode("AA.30.0050", LocalDate.of(2026, 1, 1), null);
		code_MK250120 = TardocLeistung.getFromCode("MK.25.0120", LocalDate.of(2026, 1, 1), null);

		// auto Kummulation Referenz
		code_TK000010 = TardocLeistung.getFromCode("TK.00.0010", LocalDate.of(2026, 1, 1), null);
		code_AR300130 = TardocLeistung.getFromCode("AR.00.0130", LocalDate.of(2026, 1, 1), null);

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

	@Test
	public void zuschlagZuschlagTardocPositions() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		Result<IBilled> status = billingService.bill(code_MK250020, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		// zuschlag
		status = billingService.bill(code_MK250090, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		String bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("MK.25.0020", bezug);

		// zuschlag - zuschlag
		status = billingService.bill(code_MK200300, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("MK.25.0090", bezug);

		assertEquals(3, encounter.getBilled().stream().mapToInt(b -> (int) b.getAmount()).sum());
	}

	@Test
	public void zuschlagProzentALTardocPosition() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		Result<IBilled> status = billingService.bill(code_AA300040, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());
		int noZuschalgCents = billed.getTotal().getCents();

		// zuschlag
		status = billingService.bill(code_AA300050, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		assertEquals("0.25", code_AA300050.getExtension().getExtInfo(TardocConstants.TardocLeistung.EXT_FLD_F_AL));

		// zuschlag prozent
		assertEquals(1.0, billed.getAmount(), 0.001);
		assertFalse(billed.getPrice().isZero());
		assertEquals(Math.round(noZuschalgCents * 0.25), billed.getTotal().getCents(), 0.01);
	}

	@Test
	public void zuschlagProzentALTLSameTardocPosition() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		// AA.00.0010 should be ignored for value of MK.25.0120
		Result<IBilled> status = billingService.bill(code_000010, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		status = billingService.bill(code_MK250020, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());
		int noZuschalgCents = billed.getTotal().getCents();

		// zuschlag
		status = billingService.bill(code_MK250120, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		assertEquals("0.3", code_MK250120.getExtension().getExtInfo(TardocConstants.TardocLeistung.EXT_FLD_F_AL));
		assertEquals("0.3", code_MK250120.getExtension().getExtInfo(TardocConstants.TardocLeistung.EXT_FLD_F_TL));

		// zuschlag prozent
		assertEquals(1.0, billed.getAmount(), 0.001);
		assertFalse(billed.getPrice().isZero());
		assertEquals(Math.round(noZuschalgCents * 0.30), billed.getTotal().getCents(), 0.01);
	}

	@Test
	public void kumulationTardocPosition() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		// AA.00.0010 should be ignored setting bezug of code_AR300130
		Result<IBilled> status = billingService.bill(code_000010, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());

		status = billingService.bill(code_TK000010, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());
		assertEquals(2, encounter.getBilled().size());

		// test adding kumulation
		status = billingService.bill(code_AR300130, encounter, 1);
		billed = status.get();
		assertTrue(status.getMessages().toString(), status.isOK());
		String bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("TK.00.0010", bezug);
	}
}
