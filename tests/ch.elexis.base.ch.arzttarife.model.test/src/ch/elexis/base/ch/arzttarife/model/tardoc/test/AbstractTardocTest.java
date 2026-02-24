package ch.elexis.base.ch.arzttarife.model.tardoc.test;

import java.util.List;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.tarmed.model.TarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.findings.codes.TransientCoding;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPerson;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.services.IBillingService;
import ch.elexis.core.services.IContextService;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.test.initializer.TestDatabaseInitializer;
import ch.elexis.core.types.Gender;
import ch.elexis.core.utils.OsgiServiceUtil;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public abstract class AbstractTardocTest {

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

		ArzttarifeUtil.setMandantTardocSepcialist(mandator,
				List.of(new TransientCoding("tardoc_dignitaet", "0010", null),
						new TransientCoding("tardoc_dignitaet", "0100", null),
						new TransientCoding("tardoc_dignitaet", "1000", null),
						new TransientCoding("tardoc_dignitaet", "1100", null),
						new TransientCoding("tardoc_dignitaet", "0026", null),
						new TransientCoding("tardoc_dignitaet", "9971", null)));
		coreModelService.save(mandator);
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

	protected IBilled getEncounterBilled(String code, IEncounter encounter) {
		for (IBilled billed : encounter.getBilled()) {
			if (code.equals(billed.getBillable().getCode())) {
				return billed;
			}
		}
		return null;
	}

	protected IBilled getEncounterBilled(String code) {
		return getEncounterBilled(code, encounter);
	}
}
