package ch.elexis.base.ch.arzttarife.model.tardoc.test;

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
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Result;

public class TardocGroupLimitsTest extends AbstractTardocTest {

	private TardocLeistung code_AA050010;
	private TardocLeistung code_AA050020;
	private TardocLeistung code_AA050030;

	private TardocLeistung code_AA050040;
	private TardocLeistung code_AA050050;

	@Override
	@Before
	public void before() {
		super.before();
		code_AA050010 = TardocLeistung.getFromCode("AA.05.0010", LocalDate.of(2026, 1, 1), null);
		code_AA050020 = TardocLeistung.getFromCode("AA.05.0020", LocalDate.of(2026, 1, 1), null);
		code_AA050030 = TardocLeistung.getFromCode("AA.05.0030", LocalDate.of(2026, 1, 1), null);
		code_AA050040 = TardocLeistung.getFromCode("AA.05.0040", LocalDate.of(2026, 1, 1), null);
		code_AA050050 = TardocLeistung.getFromCode("AA.05.0050", LocalDate.of(2026, 1, 1), null);
	}

	@Override
	@After
	public void after() {
		super.after();
	}

	@Test
	public void limitGroup001SingleSession() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);

		IBillingSystemFactor factor = AllTestsSuite.createBillingSystemFactor(coverage.getBillingSystem().getName(),
				0.89, LocalDate.of(2000, 1, 1));

		Result<IBilled> status = billingService.bill(code_AA050010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		status = billingService.bill(code_AA050020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		status = billingService.bill(code_AA050030, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		status = billingService.bill(code_AA050040, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		billed = status.get();
		assertNotNull(billed);
		status = billingService.bill(code_AA050050, encounter, 1);
		assertFalse(status.getMessages().toString(), status.isOK());

	}

	@Test
	public void limitGroup001MultiSession() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);
		Result<IBilled> status = billingService.bill(code_AA050010, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050020, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050030, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050040, encounter, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter1 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter1.setDate(LocalDate.of(2026, 1, 7));
		CoreModelServiceHolder.get().save(encounter1);
		status = billingService.bill(code_AA050010, encounter1, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050020, encounter1, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050030, encounter1, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050040, encounter1, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter2 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter2.setDate(LocalDate.of(2026, 1, 14));
		CoreModelServiceHolder.get().save(encounter2);
		status = billingService.bill(code_AA050010, encounter2, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050020, encounter2, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050030, encounter2, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050040, encounter2, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter3 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter3.setDate(LocalDate.of(2026, 1, 21));
		CoreModelServiceHolder.get().save(encounter3);
		status = billingService.bill(code_AA050010, encounter3, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050020, encounter3, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050030, encounter3, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050040, encounter3, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter4 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter4.setDate(LocalDate.of(2026, 2, 1));
		CoreModelServiceHolder.get().save(encounter4);
		status = billingService.bill(code_AA050010, encounter4, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050020, encounter4, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050030, encounter4, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA050040, encounter4, 1);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter5 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter5.setDate(LocalDate.of(2026, 2, 7));
		CoreModelServiceHolder.get().save(encounter5);
		status = billingService.bill(code_AA050010, encounter5, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		// 21 in 90 days is not valid
		status = billingService.bill(code_AA050020, encounter5, 1);
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
