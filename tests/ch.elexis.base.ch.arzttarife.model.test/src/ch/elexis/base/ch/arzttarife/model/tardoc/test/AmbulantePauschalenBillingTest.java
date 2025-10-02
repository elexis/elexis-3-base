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
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Result;

public class AmbulantePauschalenBillingTest extends AbstractTardocTest {

	private TardocLeistung code_000010;
	private TardocLeistung code_000020;

	private TardocLeistung code_JE000010;

	private IAmbulatoryAllowance code_C06CB0010;
	private IAmbulatoryAllowance code_C02CN0040;

	@Override
	@Before
	public void before() {
		super.before();
		code_000010 = TardocLeistung.getFromCode("AA.00.0010", LocalDate.of(2026, 1, 1), null);
		code_000020 = TardocLeistung.getFromCode("AA.00.0020", LocalDate.of(2026, 1, 1), null);

		code_JE000010 = TardocLeistung.getFromCode("JE.00.0010", LocalDate.of(2026, 1, 1), null);

		code_JE000010 = TardocLeistung.getFromCode("JE.00.0010", LocalDate.of(2026, 1, 1), null);

		code_C06CB0010 = AmbulatoryAllowance.getFromCode("C06.CB.0010", AmbulantePauschalenTyp.TRIGGER,
				LocalDate.of(2026, 1, 1));
		code_C02CN0040 = AmbulatoryAllowance.getFromCode("C02.CN.0040", AmbulantePauschalenTyp.TRIGGER,
				LocalDate.of(2026, 1, 1));
	}

	@Override
	@After
	public void after() {
		super.after();
	}

	/**
	 * Test case derived from tarmed xml 5.0 example AmbPau 1 KVG
	 */
	@Test
	public void triggerKataraktEingriff() {
		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor(coverage.getBillingSystem().getName(),
				0.89, LocalDate.of(2000, 1, 1));

		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);
		Result<IBilled> status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_000020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_JE000010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter1 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter1.setDate(LocalDate.of(2026, 1, 7));
		CoreModelServiceHolder.get().save(encounter1);
		IDiagnosisReference diagnosis = CoreModelServiceHolder.get().create(IDiagnosisReference.class);
		diagnosis.setCode("C44.1");
		diagnosis.setReferredClass("ch.elexis.data.ICD10");
		encounter1.addDiagnosis(diagnosis);
		CoreModelServiceHolder.get().save(encounter1);
		/** C02.CN.0040 is a trigger code. */
		status = billingService.bill(code_C02CN0040, encounter1, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		assertTrue(getEncounterBilled("C02.15C", encounter1) != null);
		assertTrue(getEncounterBilled("C02.CN.0040", encounter1) != null);
	}

	@Test
	public void triggerOperativeProzeduren() {
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
		assertFalse(getEncounterBilled("AA.00.0010") != null);
		assertTrue(getEncounterBilled("C90.01B") != null);
		assertTrue(getEncounterBilled("C06.CB.0010") != null);

		status = billingService.bill(code_000020, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());
	}
}
