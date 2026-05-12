package ch.elexis.base.ch.arzttarife.model.psycho.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.elexis.base.ch.arzttarife.model.test.AllTestsSuite;
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.builder.IContactBuilder;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.model.builder.IEncounterBuilder;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.IBillingSystemService;
import ch.elexis.core.services.ICodeElementService;
import ch.elexis.core.test.AbstractTest;
import ch.elexis.core.types.Gender;
import ch.rgw.tools.Result;

public class PsychoBillingTest extends AbstractTest {

	@BeforeClass
	public static void beforeClass() {
		IReferenceDataImporter tarifImporter = OsgiServiceUtil
				.getService(IReferenceDataImporter.class, "(" + IReferenceDataImporter.REFERENCEDATAID + "=psycho)")
				.get();
		IStatus result = tarifImporter.performImport(new NullProgressMonitor(),
				AllTestsSuite.class.getResourceAsStream("/rsc/20220609_PsyTarif_DE.xlsx"), 1);
		assertTrue(result.isOK());
		OsgiServiceUtil.ungetService(tarifImporter);

		IReferenceDataImporter limitImporter = OsgiServiceUtil.getService(IReferenceDataImporter.class,
				"(" + IReferenceDataImporter.REFERENCEDATAID + "=psycholimitation)").get();
		result = limitImporter.performImport(new NullProgressMonitor(),
				AllTestsSuite.class.getResourceAsStream("/rsc/psycho_limit.csv"), 1);
		assertTrue(result.isOK());
		OsgiServiceUtil.ungetService(limitImporter);
	}

	private IBillingSystemFactor factor;
	private ICoverage otherCoverage;
	private IEncounter otherEncounter;
	private IPatient otherPatient;

	@After
	@Override
	public void after() {
		if (factor != null) {
			coreModelService.remove(factor);
		}
		super.after();
	}

	@Test
	public void loadFromCodeElementService() {
		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		ICodeElement loadFromString = codeElementService.loadFromString("Psychotherapie", "PA010", null).get();
		assertTrue(loadFromString instanceof IPsychoLeistung);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void billing() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService
				.loadFromString("Psychotherapie", "PA010", null).get();
		Result<IBilled> result = therapy.getOptifier().add(therapy, encounter, 1);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());
		IBilled billed = encounter.getBilled().get(0);
		assertEquals(1, billed.getAmount(), 0.01);
		assertEquals(therapy.getText(), billed.getText());
		assertEquals(100, billed.getPrimaryScale());
		assertEquals(100, billed.getSecondaryScale());
		assertEquals(100, billed.getPoints());
		assertEquals(258, billed.getTotal().getCents());
		assertEquals(therapy.getText(), billed.getText());
		assertEquals(encounter, billed.getEncounter());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitSession() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PA010", null)
				.get();
		Result<IBilled> result = therapy.getOptifier().add(therapy, encounter, 90);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = therapy.getOptifier().add(therapy, encounter, 1);
		assertFalse(result.isOK());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitExclusive() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPsychoLeistung prepare = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PE010", null)
				.get();
		Result<IBilled> result = prepare.getOptifier().add(prepare, encounter, 10);
		assertFalse(result.isOK());
		assertTrue(encounter.getBilled().isEmpty());

		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PA010", null)
				.get();
		result = therapy.getOptifier().add(therapy, encounter, 30);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = prepare.getOptifier().add(prepare, encounter, 10);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitSessionIncluding() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PA010", null)
				.get();
		Result<IBilled> result = therapy.getOptifier().add(therapy, encounter, 80);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		IPsychoLeistung prepare = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PE010", null)
				.get();
		result = prepare.getOptifier().add(prepare, encounter, 10);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = therapy.getOptifier().add(prepare, encounter, 1);
		assertFalse(result.isOK());

		result = therapy.getOptifier().add(therapy, encounter, 1);
		assertFalse(result.isOK());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitDayPatient() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PA010", null)
				.get();
		Result<IBilled> result = therapy.getOptifier().add(therapy, encounter, 80);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		IPsychoLeistung addition = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PN020", null)
				.get();
		result = therapy.getOptifier().add(addition, encounter, 1);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = therapy.getOptifier().add(addition, encounter, 1);
		assertFalse(result.isOK());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitDaysPatient() throws ParseException {
		createEncounter();

		// create encounter 89 days ago
		IEncounter pastInRangeEncounter = new IEncounterBuilder(coreModelService, coverage, mandator)
				.date(LocalDateTime.now().minusDays(89)).buildAndSave();
		// create encounter 200 days ago
		IEncounter pastNotInRangeEncounter = new IEncounterBuilder(coreModelService, coverage, mandator)
				.date(LocalDateTime.now().minusDays(200)).buildAndSave();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();
		IPsychoLeistung diagnostic = (IPsychoLeistung) codeElementService
				.loadFromString("Psychotherapie", "PA220", null)
				.get();
		Result<IBilled> result = diagnostic.getOptifier().add(diagnostic, pastInRangeEncounter, 120);
		assertTrue(result.isOK());
		result = diagnostic.getOptifier().add(diagnostic, pastNotInRangeEncounter, 120);
		assertTrue(result.isOK());
		result = diagnostic.getOptifier().add(diagnostic, encounter, 120);
		assertFalse(result.isOK());
		result = diagnostic.getOptifier().add(diagnostic, encounter, 60);
		assertTrue(result.isOK());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitExclusion() throws ParseException {
		createEncounter();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PA010", null)
				.get();
		Result<IBilled> result = therapy.getOptifier().add(therapy, encounter, 30);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		IPsychoLeistung therapyCouple = (IPsychoLeistung) codeElementService
				.loadFromString("Psychotherapie", "PA020", null).get();
		result = therapy.getOptifier().add(therapyCouple, encounter, 30);
		assertFalse(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void limitPerBiller() throws ParseException {
		createEncounter();
		createOtherEncounter();

		IEncounter yesterDayEncounter = new IEncounterBuilder(coreModelService, coverage, mandator)
				.date(LocalDateTime.now().minusDays(1)).buildAndSave();
		IEncounter sameDayEncounter = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();

		ICodeElementService codeElementService = OsgiServiceUtil.getService(ICodeElementService.class).get();

		IPsychoLeistung therapy = (IPsychoLeistung) codeElementService.loadFromString("Psychotherapie", "PA010", null)
				.get();
		Result<IBilled> result = therapy.getOptifier().add(therapy, encounter, 30);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = therapy.getOptifier().add(therapy, otherEncounter, 30);
		assertTrue(result.isOK());
		assertFalse(otherEncounter.getBilled().isEmpty());

		result = therapy.getOptifier().add(therapy, sameDayEncounter, 30);
		assertTrue(result.isOK());
		assertFalse(sameDayEncounter.getBilled().isEmpty());

		result = therapy.getOptifier().add(therapy, yesterDayEncounter, 30);
		assertTrue(result.isOK());
		assertFalse(yesterDayEncounter.getBilled().isEmpty());

		IPsychoLeistung administrativ = (IPsychoLeistung) codeElementService
				.loadFromString("Psychotherapie", "PN010", null)
				.get();
		result = administrativ.getOptifier().add(administrativ, encounter, 15);
		assertFalse(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		result = administrativ.getOptifier().add(administrativ, encounter, 10);
		assertTrue(result.isOK());
		assertFalse(encounter.getBilled().isEmpty());

		// same day second session too much not valid
		result = administrativ.getOptifier().add(administrativ, otherEncounter, 15);
		assertFalse(result.isOK());
		assertFalse(otherEncounter.getBilled().isEmpty());

		// same day second session is ok
		result = administrativ.getOptifier().add(administrativ, otherEncounter, 10);
		assertTrue(result.isOK());
		assertFalse(otherEncounter.getBilled().isEmpty());

		// same day third session is not valid
		result = administrativ.getOptifier().add(administrativ, sameDayEncounter, 10);
		assertFalse(result.isOK());
		assertFalse(sameDayEncounter.getBilled().isEmpty());

		result = administrativ.getOptifier().add(administrativ, yesterDayEncounter, 15);
		assertFalse(result.isOK());
		assertFalse(yesterDayEncounter.getBilled().isEmpty());

		// other day first session is ok
		result = administrativ.getOptifier().add(administrativ, yesterDayEncounter, 10);
		assertTrue(result.isOK());
		assertFalse(yesterDayEncounter.getBilled().isEmpty());
	}

	public void createOtherPatient() {
		LocalDate dob = LocalDate.of(2017, 9, 1);
		otherPatient = new IContactBuilder.PatientBuilder(coreModelService, "OtherTestPatient", "OtherTestPatient",
				dob,
				Gender.FEMALE)
				.buildAndSave();
		assertTrue(otherPatient.isPerson());
		assertTrue(otherPatient.isPatient());
		assertFalse(otherPatient.isOrganization());
		assertFalse(otherPatient.isLaboratory());
		assertFalse(otherPatient.isMandator());
	}

	public void createOtherCoverage() {
		IBillingSystemService billingSystemService = OsgiServiceUtil.getService(IBillingSystemService.class).get();
		billingSystemService.addOrModifyBillingSystem("Psychotherapie", CONST_TARMED_DRUCKER, KVG_REQUIREMENTS,
				BillingLaw.KVG);

		if (otherPatient == null) {
			createOtherPatient();
		}
		otherCoverage = new ICoverageBuilder(coreModelService, patient, "testPsycho", "testReason", "Psychotherapie")
				.buildAndSave();
	}

	public void createOtherEncounter() {
		if (otherCoverage == null) {
			createOtherCoverage();
		}
		if (mandator == null) {
			createMandator();
		}
		otherEncounter = new IEncounterBuilder(coreModelService, otherCoverage, mandator).buildAndSave();
	}

	@Override
	public void createCoverage() {
		IBillingSystemService billingSystemService = OsgiServiceUtil.getService(IBillingSystemService.class).get();
		billingSystemService.addOrModifyBillingSystem("Psychotherapie", CONST_TARMED_DRUCKER, KVG_REQUIREMENTS,
				BillingLaw.KVG);

		factor = AllTestsSuite.createBillingSystemFactor("Psychotherapie", 2.58,
				LocalDate.of(2022, 1, 1));

		if (patient == null) {
			createPatient();
		}
		coverage = new ICoverageBuilder(coreModelService, patient, "testPsycho", "testReason", "Psychotherapie")
				.buildAndSave();
	}

	@Override
	public void createEncounter() {
		if (coverage == null) {
			createCoverage();
		}
		if (mandator == null) {
			createMandator();
		}
		encounter = new IEncounterBuilder(coreModelService, coverage, mandator).buildAndSave();
	}
}
