package ch.elexis.base.ch.arzttarife.model.tarmed.test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public abstract class AbstractTarmedTest {

	final IBillingService billingService = AllTestsSuite.getBillingService();
	final IModelService coreModelService = AllTestsSuite.getCoreModelService();

	IMandator mandator;
	IPatient patient;
	ICoverage coverage;
	IEncounter encounter;

	IBilled billed;
	Result<IBilled> result;

	public void before() {
		TimeTool timeTool = new TimeTool();
		IPerson _mandator = new IContactBuilder.PersonBuilder(coreModelService, "mandator1 " + timeTool.toString(),
				"Anton" + timeTool.toString(), timeTool.toLocalDate(), Gender.MALE).mandator().buildAndSave();
		mandator = coreModelService.load(_mandator.getId(), IMandator.class).get();
		patient = new IContactBuilder.PatientBuilder(coreModelService, "Armer", "Anton" + timeTool.toString(),
				timeTool.toLocalDate(), Gender.MALE).buildAndSave();
		coverage = new ICoverageBuilder(coreModelService, patient, "Fallbezeichnung", "Fallgrund", "KVG")
				.buildAndSave();
		encounter = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
		OsgiServiceUtil.getService(IContextService.class).get().setActiveUser(TestDatabaseInitializer.getUser());
		OsgiServiceUtil.getService(IContextService.class).get().setActiveMandator(mandator);
	}

	public void after() {
		coreModelService.remove(encounter);
		coreModelService.remove(coverage);
		coreModelService.remove(patient);
		coreModelService.remove(mandator);
		result = null;
		billed = null;
	}

	Result<IBilled> billSingle(IEncounter encounter, TarmedLeistung billable) {
		return billingService.bill(billable, encounter, 1);
	}
}
