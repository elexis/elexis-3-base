package ch.elexis.base.ch.arzttarife.elexis.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.service.ConfigServiceHolder;
import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class IEncounterServiceTest {

	private final IEncounterService encounterService = AllTestsSuite.getEncounterService();
	private final IBillingService billingService = AllTestsSuite.getBillingService();
	private final IModelService coreModelService = AllTestsSuite.getCoreModelService();

	private TarmedLeistung code_000010 = TarmedLeistung.getFromCode("00.0010", "KVG");
	private TarmedLeistung code_000015 = TarmedLeistung.getFromCode("00.0015", "KVG");
	private TarmedLeistung code_000020 = TarmedLeistung.getFromCode("00.0020", "KVG");

	private TarmedLeistung code_000516_kvgOnly = TarmedLeistung.getFromCode("00.0516", "KVG");

	private IMandator mandator;
	private IPatient patient;
	private ICoverage coverage;
	private IEncounter encounter;

	private Result<IBilled> status;

	@Before
	public void before() {
		assertNotNull(code_000010);

		TimeTool timeTool = new TimeTool();
		IPerson _mandator = new IContactBuilder.PersonBuilder(coreModelService, "mandator1 " + timeTool.toString(),
				"Anton" + timeTool.toString(), timeTool.toLocalDate(), Gender.MALE).mandator().buildAndSave();
		mandator = coreModelService.load(_mandator.getId(), IMandator.class).get();
		patient = new IContactBuilder.PatientBuilder(coreModelService, "Armer", "Anton" + timeTool.toString(),
				timeTool.toLocalDate().minusYears(8), Gender.MALE).buildAndSave();
		coverage = new ICoverageBuilder(coreModelService, patient, "Fallbezeichnung", "Fallgrund", "KVG")
				.buildAndSave();
		assertEquals(BillingLaw.KVG, coverage.getBillingSystem().getLaw());
		encounter = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();

		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
	}

	@After
	public void after() {
		coreModelService.remove(encounter);
		coreModelService.remove(coverage);
		coreModelService.remove(patient);
		coreModelService.remove(mandator);
		status = null;
	}

	@Test
	public void addDefaultDiagnosis() {
		assertEquals(0, encounter.getDiagnoses().size());
		encounterService.addDefaultDiagnosis(encounter);
		coreModelService.refresh(encounter);
		assertEquals(0, encounter.getDiagnoses().size());

		ConfigServiceHolder.get().get().setActiveUserContact(Preferences.USR_DEFDIAGNOSE, "ch.elexis.data.TICode::U9");
		encounterService.addDefaultDiagnosis(encounter);
		coreModelService.refresh(encounter);
		assertEquals(1, encounter.getDiagnoses().size());
	}

	@Test
	@Ignore
	// FIXME
	public void testTransferKonsFromKVGToUVGFall_rechargedPrice() {

		ICoverage coverageUVG = new ICoverageBuilder(coreModelService, patient, "Fallbezeichnung", "Fallgrund", "UVG")
				.buildAndSave();
		assertEquals(BillingLaw.UVG, coverageUVG.getBillingSystem().getLaw());

		status = billingService.bill(code_000010, encounter, 1);
		assertTrue(status.isOK());
		status = billingService.bill(code_000015, encounter, 1);
		assertTrue(status.toString(), status.isOK());
		assertEquals(1, status.get().getAmount(), 0);
		status = billingService.bill(code_000020, encounter, 2);
		assertTrue(status.toString(), status.isOK());
		assertEquals(2, status.get().getAmount(), 0.01d);
		status = billingService.bill(code_000516_kvgOnly, encounter, 3);
		assertTrue(status.toString(), status.isOK());
		assertEquals(3, status.get().getAmount(), 0.01d);

		IBilled billed_kvg_000010 = null;
		IBilled billed_kvg_000015 = null;
		List<IBilled> billed_beforeTransfer = encounter.getBilled();
		for (IBilled iBilled : billed_beforeTransfer) {
			if ("00.0010".equals(iBilled.getCode())) {
				billed_kvg_000010 = iBilled;
			} else if ("00.0015".equals(iBilled.getCode())) {
				billed_kvg_000015 = iBilled;
			}
		}
		if (billed_kvg_000010 == null || billed_kvg_000015 == null) {
			fail();
			return;
		}

		assertEquals(BillingLaw.KVG, encounter.getCoverage().getBillingSystem().getLaw());
		List<IBilled> billed = encounter.getBilled();
		assertTrue(billed.contains(billed_kvg_000010));
		assertTrue(billed.contains(billed_kvg_000015));

		Result<IEncounter> result = encounterService.transferToCoverage(encounter, coverageUVG, false);
		assertFalse(result.toString(), result.isOK());
		assertEquals(BillingLaw.UVG, result.get().getCoverage().getBillingSystem().getLaw());
		coreModelService.refresh(billed_kvg_000010);
		assertTrue(billed_kvg_000010.isDeleted());
		coreModelService.refresh(billed_kvg_000015);
		assertTrue(billed_kvg_000015.isDeleted());

		List<IBilled> billed_afterTransfer = result.get().getBilled();
		for (IBilled iBilled : billed_afterTransfer) {
			if ("00.0010".equals(iBilled.getCode())) {
				assertTrue(iBilled.getBillable().getId() + " KVG: " + billed_kvg_000010.getPrice() + " / UVG: "
						+ iBilled.getPrice(), billed_kvg_000010.getPrice().isMoreThan(iBilled.getPrice()));
				assertEquals(1, iBilled.getAmount(), 0);
			} else if ("00.0015".equals(iBilled.getCode())) {
				assertTrue(iBilled.getBillable().getId() + " KVG: " + billed_kvg_000015.getPrice() + " / UVG: "
						+ iBilled.getPrice(), billed_kvg_000015.getPrice().isMoreThan(iBilled.getPrice()));
				assertEquals(1, iBilled.getAmount(), 0);
			} else if ("00.0020".equals(iBilled.getCode())) {
				assertEquals(2, iBilled.getAmount(), 0);
			} else if ("00.0516".equals(iBilled.getCode())) {
				fail("Position 00.0516 should have been removed (KVG only)");
			}
		}

	}

}
