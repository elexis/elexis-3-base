package ch.elexis.base.ch.arzttarife.model.tardoc.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.tardoc.model.TardocLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.EncounterServiceHolder;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.rgw.tools.Result;

public class TardocLimitsTest extends AbstractTardocTest {

	private TardocLeistung code_AA150010;
	private TardocLeistung code_AA150060;

	@Override
	@Before
	public void before() {
		super.before();
		// Für alle Tarifpositionen der Leistungsgruppe LG-002 - Leistungen in
		// Abwesenheit (Büroarbeit) gilt: ≤ 30 Mal pro 90 Tage
		code_AA150010 = TardocLeistung.getFromCode("AA.15.0010", LocalDate.of(2026, 1, 1), null);
		// Für alle Tarifpositionen der Leistungsgruppe LG-002 - Leistungen in
		// Abwesenheit (Büroarbeit) gilt: ≤ 30 Mal pro 90 Tage
		code_AA150060 = TardocLeistung.getFromCode("AA.15.0060", LocalDate.of(2026, 1, 1), null);
	}

	@Override
	@After
	public void after() {
		super.after();
	}

	@Test
	public void limitGroup002MultiSessionMultiMandator() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);
		Result<IBilled> status = billingService.bill(code_AA150010, encounter, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter1 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter1.setDate(LocalDate.of(2026, 1, 7));
		CoreModelServiceHolder.get().save(encounter1);
		status = billingService.bill(code_AA150010, encounter1, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter1, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter2 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter2.setDate(LocalDate.of(2026, 1, 14));
		CoreModelServiceHolder.get().save(encounter2);
		status = billingService.bill(code_AA150010, encounter2, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter2, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter3 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter3.setDate(LocalDate.of(2026, 1, 21));
		CoreModelServiceHolder.get().save(encounter3);
		status = billingService.bill(code_AA150010, encounter3, 1);
		// fail combination of Leistungsgruppe LG-002
		assertFalse(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter3, 1);
		// fail combination of Leistungsgruppe LG-002
		assertFalse(status.getMessages().toString(), status.isOK());

		// still fail at plus 90 days
		IEncounter encounter4 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter4.setDate(LocalDate.of(2026, 1, 1).plusDays(9));
		CoreModelServiceHolder.get().save(encounter4);
		status = billingService.bill(code_AA150010, encounter4, 1);
		// fail combination of Leistungsgruppe LG-002 90 days
		assertFalse(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter4, 1);
		// fail combination of Leistungsgruppe LG-002 90 days
		assertFalse(status.getMessages().toString(), status.isOK());

		// ok after 91 days
		IEncounter encounter5 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter5.setDate(LocalDate.of(2026, 1, 1).plusDays(91));
		CoreModelServiceHolder.get().save(encounter5);
		status = billingService.bill(code_AA150010, encounter5, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter5, 1);
		assertTrue(status.getMessages().toString(), status.isOK());
	}

	@Test
	public void limitGroup002MultiSessionMultiMandatorMove() {
		encounter.setDate(LocalDate.of(2026, 1, 1));
		CoreModelServiceHolder.get().save(encounter);
		Result<IBilled> status = billingService.bill(code_AA150010, encounter, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter1 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter1.setDate(LocalDate.of(2026, 1, 7));
		CoreModelServiceHolder.get().save(encounter1);
		status = billingService.bill(code_AA150010, encounter1, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter1, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter2 = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
		encounter2.setDate(LocalDate.of(2026, 1, 14));
		CoreModelServiceHolder.get().save(encounter2);
		status = billingService.bill(code_AA150010, encounter2, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter2, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		IEncounter encounter3 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter3.setDate(LocalDate.of(2026, 1, 21));
		CoreModelServiceHolder.get().save(encounter3);
		status = billingService.bill(code_AA150010, encounter3, 1);
		// fail combination of Leistungsgruppe LG-002
		assertFalse(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter3, 1);
		// fail combination of Leistungsgruppe LG-002
		assertFalse(status.getMessages().toString(), status.isOK());

		IEncounter encounter4 = new IEncounterBuilder(coreModelService, coverage, otherMandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(otherMandator);
		encounter4.setDate(LocalDate.of(2026, 1, 1).plusDays(96));
		CoreModelServiceHolder.get().save(encounter4);
		status = billingService.bill(code_AA150010, encounter4, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter4, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		// move forward excl. 07.01.26
		Result<IEncounter> moveResult = EncounterServiceHolder.get().setEncounterDate(encounter3,
				LocalDate.of(2026, 1, 1).plusDays(101));
		assertTrue(moveResult.getMessages().toString(), moveResult.isOK());
		status = billingService.bill(code_AA150010, encounter3, 5);
		assertTrue(status.getMessages().toString(), status.isOK());
		status = billingService.bill(code_AA150060, encounter3, 5);
		assertTrue(status.getMessages().toString(), status.isOK());

		// move backwards incl. 07.01.26
		moveResult = EncounterServiceHolder.get().setEncounterDate(encounter3, LocalDate.of(2026, 1, 1).plusDays(91));
		assertFalse(moveResult.getMessages().toString(), moveResult.isOK());
		assertTrue(encounter3.getBilled().isEmpty());
	}
}
