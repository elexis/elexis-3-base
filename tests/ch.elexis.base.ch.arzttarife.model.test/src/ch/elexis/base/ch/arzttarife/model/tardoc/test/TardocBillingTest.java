package ch.elexis.base.ch.arzttarife.model.tardoc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.ambulatory.AmbulantePauschalenTyp;
import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.ambulatory.model.AmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IDiagnosisReference;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;

public class TardocBillingTest extends AbstractTardocTest {

	private TardocLeistung code_000010;
	private TardocLeistung code_000020;
	private TardocLeistung code_000040;

	private TardocLeistung code_RG050010;
	private TardocLeistung code_RG000040;

	private IAmbulatoryAllowance code_C06CB0010;

	@Override
	@Before
	public void before() {
		super.before();
		code_000010 = TardocLeistung.getFromCode("AA.00.0010", LocalDate.of(2026, 1, 1), null);
		code_000020 = TardocLeistung.getFromCode("AA.00.0020", LocalDate.of(2026, 1, 1), null);

		code_000040 = TardocLeistung.getFromCode("AA.00.0040", LocalDate.of(2026, 1, 1), null);
		
		code_RG050010 = TardocLeistung.getFromCode("RG.05.0010", LocalDate.of(2026, 1, 1), null);
		code_RG000040 = TardocLeistung.getFromCode("RG.00.0040", LocalDate.of(2026, 1, 1), null);

		code_C06CB0010 = AmbulatoryAllowance.getFromCode("C06.CB.0010", AmbulantePauschalenTyp.TRIGGER,
				LocalDate.of(2026, 1, 1));
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

		String bezug = (String) billed.getExtInfo("Bezug");
		assertNotNull(bezug);
		assertEquals("AA.00.0010", bezug);

		status = billingService.bill(code_000040, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
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
		assertTrue(getEncounterBilled("AA.00.0010") != null);

		/** C06.CB.0010 is a trigger code. */
		status = billingService.bill(code_C06CB0010, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNull(billed);
		assertTrue(getEncounterBilled("C06.CB.0010") == null);

		IDiagnosisReference diagnosis = CoreModelServiceHolder.get().create(IDiagnosisReference.class);
		diagnosis.setCode("A54.4");
		diagnosis.setReferredClass("ch.elexis.data.ICD10");
		encounter.addDiagnosis(diagnosis);
		CoreModelServiceHolder.get().save(encounter);

		/** C06.CB.0010 is a trigger code. */
		status = billingService.bill(code_C06CB0010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		assertEquals("C90.01B", billed.getCode());
		assertTrue(getEncounterBilled("C90.01B") != null);

		status = billingService.bill(code_000020, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
	}

	private IBilled getEncounterBilled(String code) {
		for (IBilled billed : encounter.getBilled()) {
			if (code.equals(billed.getBillable().getCode())) {
				return billed;
			}
		}
		return null;
	}
}
